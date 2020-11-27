package dev.hotdeals.treecreate.controller;

import dev.hotdeals.treecreate.config.CustomProperties;
import dev.hotdeals.treecreate.model.DiscountCode;
import dev.hotdeals.treecreate.model.User;
import dev.hotdeals.treecreate.repository.DiscountCodeRepo;
import dev.hotdeals.treecreate.repository.UserRepo;
import dev.hotdeals.treecreate.model.Transaction;
import dev.hotdeals.treecreate.model.TreeOrder;
import dev.hotdeals.treecreate.repository.TransactionRepo;
import dev.hotdeals.treecreate.repository.TreeOrderRepo;
import dev.hotdeals.treecreate.service.MailService;
import org.hibernate.LazyInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.*;

import static org.hibernate.bytecode.BytecodeLogger.LOGGER;

@Controller
public class PaymentController
{
    @Autowired
    UserRepo userRepo;

    @Autowired
    TreeController treeController;

    @Autowired
    TreeOrderRepo treeOrderRepo;

    @Autowired
    TransactionRepo transactionRepo;
    @Autowired
    CustomProperties customProperties;

    @PostMapping("/startPayment")
    ResponseEntity<String> startPayment(HttpServletRequest request, @RequestBody Transaction transaction)
    {
        LOGGER.info(request.getSession().getId() + " - Transaction - Starting a new payment");
        int id;
        try
        {
            id = Integer.parseInt(Objects.requireNonNull(treeController.getCurrentUser(request).getBody()));
        } catch (NullPointerException e)
        {
            LOGGER.error(request.getSession().getId() + " - Transaction - Cannot start a payment - user id obtained from the session is null");
            return new ResponseEntity<>("Failed to obtain the user from the session. Please do report this to the developers", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        LOGGER.info(request.getSession().getId() + " - Transaction - User Id: " + id);
        User user = userRepo.findById(id).orElse(null);
        if (user == null)
        {
            LOGGER.info(request.getSession().getId() + " - Transaction - The user is yikes");
            return new ResponseEntity<>("Failed to find a user with an ID of " + id, HttpStatus.NOT_FOUND);
        }

        LOGGER.info(request.getSession().getId() + " - Transaction - Verifying verification");
        if (!user.getVerification().equals("verified"))
        {
            LOGGER.info(request.getSession().getId() + " - Transaction - User " + user.getId() + " is not verified");
            return new ResponseEntity<>("User is not verified", HttpStatus.FORBIDDEN);
        }
        LOGGER.info(request.getSession().getId() + " - Transaction - Everything looks okay, creating a new transaction for user " + user.getId());

        LOGGER.info(request.getSession().getId() + " - Transaction - Getting the environment type");
        String envType = customProperties.getEnvironmentType();
        String callbackUrl = "";
        if (!envType.equals("production"))
        {
            // otherwise it is empty and doesn't get assigned to the paymentLink
            callbackUrl = "&callback_url=https://testing.treecreate.dk/paymentCallback";
        }

        LOGGER.info(request.getSession().getId() + " - Transaction - Getting all orders for user " + user.getId());
        var orderList = treeOrderRepo.findAllByUserId(user.getId());
        if (orderList.size() == 0)
        {
            LOGGER.warn(request.getSession().getId() + " - Transaction - The order list for user " + user.getId() + " is empty! Cancelling the transaction");
            return new ResponseEntity<>(request.getSession().getId() + " - Transaction - Internal server error - failed to find orders. Contact Support for more information", HttpStatus.NOT_FOUND);
        }
        LOGGER.info(request.getSession().getId() + " - Transaction - Order list size: " + orderList.size());
        int totalPrice = 0;
        Map<String, Integer> sizeToPriceMap = new HashMap<>();
        sizeToPriceMap.put("20x20 cm", 495);
        sizeToPriceMap.put("25x25 cm", 695);
        sizeToPriceMap.put("30x30 cm", 995);
        orderList.removeIf(order -> !order.getStatus().equals("active"));
        int totalAmount = 0;
        for (TreeOrder order : orderList)
        {
            int price = sizeToPriceMap.get(order.getSize());
            totalPrice += price * order.getAmount();
            totalAmount += order.getAmount();
        }
        if (totalAmount > 3)
        {
            LOGGER.info(request.getSession().getId() + " - Transaction - Total amount of ordered designs was more than 3, applying a 25% discount to the total");
            totalPrice = (int) (totalPrice * 0.75);
        }
        totalPrice *= 100; // Quickpay takes 2 digits as decimal places, so 1000 becomes 10,00

        if (transaction.getDiscount() != null)
        {
            LOGGER.info(request.getSession().getId() + " - Transaction - Applying the discount for the transaction in sessions ID: " + request.getSession().getId());
            LOGGER.info(request.getSession().getId() + " - Transaction - Total price pre-discount: " + totalPrice);
            var discountCodeResponse = getDiscountCode(transaction.getDiscount());
            if (discountCodeResponse.getStatusCode() != HttpStatus.OK)
            {
                LOGGER.info(request.getSession().getId() + " - Transaction - Failed to find the discount code");
            } else
            {
                var discountCode = discountCodeResponse.getBody();
                if (!discountCode.getActive())
                {
                    LOGGER.info(request.getSession().getId() + " - Transaction - Provided discount is no longer active");
                } else
                {

                    if (discountCode.getTimesUsed() >= discountCode.getMaxUsages())
                    {
                        LOGGER.info(request.getSession().getId() + " - Transaction - Max usages for discount code " + discountCode.getId() + " have been reached already. Not applying the discount");
                        discountCode.setActive(false);
                    } else
                    {
                        int amount = Integer.parseInt(discountCode.getDiscountAmount());
                        String type = discountCode.getDiscountType();
                        if (type.equals("minus"))
                        {
                            LOGGER.info(request.getSession().getId() + " - Transaction - Applying a discount of -" + amount + "kr");
                            totalPrice = totalPrice - (amount * 100);
                            if (totalPrice < 0) totalPrice = 0;
                        }
                        if (type.equals("percent"))
                        {
                            LOGGER.info(request.getSession().getId() + " - Transaction - Applying a discount of " + amount + "%");
                            double percent = (100.0 - amount) / 100;
                            LOGGER.info(request.getSession().getId() + " - Transaction - Percent of total left: " + percent);
                            totalPrice = (int) Math.floor(((totalPrice / 100.0) * percent)) * 100;

                        }
                        discountCode.setTimesUsed(discountCode.getTimesUsed() + 1);
                        if (discountCode.getTimesUsed() >= discountCode.getMaxUsages())
                        {
                            LOGGER.info(request.getSession().getId() + " - Transaction - Max usages for discount code " + discountCode.getId() + " have been reached. Deactivating the discount (this is after applying it)");
                            discountCode.setActive(false);
                        }
                    }
                    discountCodeRepo.save(discountCode);
                }
            }
        }

        LOGGER.info(request.getSession().getId() + " - Transaction - Price: " + totalPrice);
        LOGGER.info(request.getSession().getId() + " - Transaction - Creating a new transaction");
        LOGGER.info(request.getSession().getId() + " - Transaction - Transaction shipment info: " + transaction);
        //Transaction transaction = new Transaction();
        transaction.setCurrency("dkk");
        transaction.setPrice(totalPrice);
        transaction.setUser(user);
        transaction.setOrderList(orderList);
        transaction.setStatus("creating"); // fallback for if the creation of the payment fails etc. It is set to "initial" once a link is created
        transaction.setCreatedOn(LocalDateTime.now().toString()); // fallback for if the creation of the payment fails etc. It is set to "initial" once a link is created
        var expectedDeliveryDate = LocalDateTime.now().plusWeeks(2);
        if (expectedDeliveryDate.getDayOfWeek() == DayOfWeek.SATURDAY)
        {
            expectedDeliveryDate = expectedDeliveryDate.plusDays(2);
        }
        if (expectedDeliveryDate.getDayOfWeek() == DayOfWeek.SUNDAY)
        {
            expectedDeliveryDate = expectedDeliveryDate.plusDays(1);
        }
        transaction.setExpectedDeliveryDate(expectedDeliveryDate.toLocalDate().toString()); // fallback for if the creation of the payment fails etc. It is set to "initial" once a link is created
        transaction.setStatus("creating"); // fallback for if the creation of the payment fails etc. It is set to "initial" once a link is created
        transactionRepo.save(transaction);
        LOGGER.info(request.getSession().getId() + " - Transaction - Transaction ID: " + transaction.getId());
        LOGGER.info(request.getSession().getId() + " - Transaction - Transaction " + transaction.getId() + " - Creating a payment");

        LOGGER.info(request.getSession().getId() + " - Transaction - Transaction " + transaction.getId() +
                " - Creating a payment with an order id: " + createPaymentOrderId(transaction.getId()));

        String apiKey = customProperties.getQuickpaySecret();
        var client = HttpClient.newBuilder().build();

        var httpRequest = HttpRequest.newBuilder(
                URI.create("https://api.quickpay.net/payments/"))
                .header("accept", "application/json")
                .header("Content-Type", "multipart/form-data")
                .header("Accept-Version", "v10")
                .header("Authorization", basicAuth("", apiKey))
                .POST(HttpRequest.BodyPublishers.ofString("currency=" + transaction.getCurrency() +
                        "&order_id=" + createPaymentOrderId(transaction.getId())))
                .build();

        HttpResponse<String> response;
        try
        {
            client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e)
        {
            LOGGER.error("Transaction " + transaction.getId() + " - Payment creation made a fucky wucky", e);
            return new ResponseEntity<>("A request for creating a payment has failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        LOGGER.info(request.getSession().getId() + " - Transaction - Getting the payment via the transaction id: " +
                createPaymentOrderId(transaction.getId()));

        httpRequest = HttpRequest.newBuilder(
                URI.create("https://api.quickpay.net/payments/?order_id=" + createPaymentOrderId(transaction.getId())))
                .header("accept", "application/json")
                .header("Content-Type", "multipart/form-data")
                .header("Accept-Version", "v10")
                .header("Authorization", basicAuth("", apiKey))
                .build();

        response = null;
        try
        {
            response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            //System.out.println(response.body());
        } catch (IOException | InterruptedException e)
        {
            LOGGER.error("Transaction " + transaction.getId() + " - Payment request (getting the payment id) made a fucky wucky", e);
            return new ResponseEntity<>("A request for obtaining a payment id has failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        String orderIdPattern = "\\[\\{\"id\":(\\d+)";
        Pattern pattern = Pattern.compile(orderIdPattern);
        Matcher matcher = pattern.matcher(response.body());
        if (!matcher.find())
        {
            LOGGER.info(request.getSession().getId() + " - Transaction - Transaction " + transaction.getId() + " - Failed to find the payment ID in the response body");
            return new ResponseEntity<>("Failed to extract the payment id from the payment order", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String paymentId = matcher.group(1);
        LOGGER.info(request.getSession().getId() + " - Transaction - Getting a payment link for transaction ID: " + paymentId);
        if (!envType.equals("production"))
        {
            LOGGER.info(request.getSession().getId() + " - Transaction - The env type is " + envType +
                    " so the callback url is being set to " + "https://testing.treecreate.dk/paymentCallback");
        }

        httpRequest = HttpRequest.newBuilder(
                URI.create("https://api.quickpay.net/payments/" + paymentId + "/link"))
                .header("accept", "application/json")
                .header("Content-Type", "multipart/form-data")
                .header("Accept-Version", "v10")
                .header("Authorization", basicAuth("", apiKey))
                .PUT(HttpRequest.BodyPublishers.ofString("amount=" + totalPrice + callbackUrl))
                .build();

        response = null;
        try
        {
            response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e)
        {
            LOGGER.error(request.getSession().getId() + " - Transaction - Transaction " + transaction.getId() + " - Payment Link request made a fucky wucky", e);
            return new ResponseEntity<>("A request for obtaining a payment link has failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        LOGGER.info(request.getSession().getId() + " - Transaction - Setting transaction " + transaction.getId() + " status to 'initial'");
        transaction.setStatus("initial");
        LOGGER.info(request.getSession().getId() + " - Transaction - Setting transaction " + transaction.getId() + " payment link to " + response.body());
        transaction.setPaymentLink(response.body().substring(8, response.body().length() - 2));
        transactionRepo.save(transaction);

        LOGGER.info(request.getSession().getId() + " - Transaction - Transaction " + transaction.getId() + " - Changing the status of orders to pending");
        for (TreeOrder order : orderList)
        {
            LOGGER.info(request.getSession().getId() + " - Transaction - Transaction - changing status of order" + order.getOrderId() + " to pending");
            order.setStatus("pending");
            treeOrderRepo.save(order);
        }

        LOGGER.info(request.getSession().getId() + " - Transaction - Transaction " + transaction.getId() + " - Returning a link");
        return new ResponseEntity<>(response.body(), HttpStatus.OK);
    }

    private static String basicAuth(String username, String password)
    {
        return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }

    public String createPaymentOrderId(int id)
    {
        String fluffedId = id + "";
        String orderIdPrefix = "test-";
        if (customProperties.getEnvironmentType().equals("production"))
        {
            orderIdPrefix = "order-";
        }
        while (fluffedId.length() < 4)
        {
            fluffedId = "0" + fluffedId;
        }
        return orderIdPrefix + fluffedId;
    }

    @GetMapping("/payment")
    String payment()
    {
        return "payment/test";
    }

    @GetMapping("/payment_temp")
    String payment_temp()
    {
        return "payment/netstatExample";
    }

    @GetMapping("/payment_success")
    public String paymentSuccess(@RequestParam(value = "data") String value1, @RequestParam(value = "data2") String value2)
    {
        LOGGER.info("Success");
        LOGGER.info("Data 1: " + value1);
        LOGGER.info("Data 2: " + value2);
        return "payment/success";
    }

    @GetMapping("/payment_fail")
    public String paymentFail(@RequestParam(value = "data1") String value1, @RequestParam(value = "data2") String value2)
    {
        LOGGER.info("Fail");
        LOGGER.info("val1: " + value1);
        LOGGER.info("val2: " + value2);
        return "payment/fail";
    }

    @GetMapping(value = {"/basket", "shoppingBasket"})
    public String basket(Model model, HttpServletRequest request)
    {
        String userID = treeController.getCurrentUser(request).getBody();
        User currentUser = userRepo.findById(Integer.parseInt(userID)).orElse(null);
        if (currentUser != null)
        {
            model.addAttribute("user", currentUser);
        }

        return "payment/basket";
    }

    @GetMapping("/updateOrderStatuses")
    ResponseEntity<String> updateOrderStatuses()
    {
        LOGGER.info("Updating order statuses");
        var client = HttpClient.newBuilder().build();

        var transactionList = transactionRepo.findAllByStatus("initial");
        for (Transaction transaction : transactionList)
        {
            HttpResponse<String> response = null;
            try
            {
                response = getPaymentByTransactionId(transaction.getId(), client);

                if (response.statusCode() != 200)
                {
                    LOGGER.warn("Updating order statuses - Failed to find a payment with transaction id: " + transaction.getId());
                    continue;
                }
            } catch (IOException | InterruptedException e)
            {
                LOGGER.error("An error occurred while trying to update orders for transaction " + transaction.getId(), e);
                continue;
            }

            var paymentStatus = getPaymentStatus(response.body());
            if (paymentStatus.equals(""))
            {
                LOGGER.warn("Failed to find the status for transaction " + transaction.getId());
                continue;
            }
            if (paymentStatus.equals("initial") || paymentStatus.equals("creating"))
            {
                continue;
            }
            LOGGER.info("Updating order statuses - Found a transaction with a status other than initial: ID: " + transaction.getId() + " status: " + paymentStatus);

            try
            {
                var orderList = transaction.getOrderList();
                for (TreeOrder order : orderList)
                {
                    order.setStatus(paymentStatus);
                    treeOrderRepo.save(order);
                }
            } catch (LazyInitializationException e)
            {
                LOGGER.error("Updating order statuses - Failed to process an order list. Possible cause - no orders assigned to a transaction", e);
            }
            transaction.setStatus(paymentStatus);
            transactionRepo.save(transaction);
            LOGGER.info("Updating order statuses - Finished setting status for transaction " + transaction.getId() + " and its orders. New status: " + paymentStatus);
        }
        LOGGER.info("Finished updating order statuses");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public HttpResponse<String> getPaymentByTransactionId(int id, HttpClient client) throws IOException, InterruptedException
    {
        String apiKey = customProperties.getQuickpaySecret();

        var httpRequest = HttpRequest.newBuilder(
                URI.create("https://api.quickpay.net/payments/?order_id=" + createPaymentOrderId(id)))
                .header("accept", "application/json")
                .header("Content-Type", "multipart/form-data")
                .header("Accept-Version", "v10")
                .header("Authorization", basicAuth("", apiKey))
                .build();

        return client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    }

    public String getPaymentStatus(String payment)
    {
        String statePattern = "state\":\"(\\w+)\"";
        Pattern pattern = Pattern.compile(statePattern);
        Matcher matcher = pattern.matcher(payment);
        if (matcher.find())
        {
            return matcher.group(1);
        } else
        {
            return "";
        }
    }

    @GetMapping("/getTransaction")
    ResponseEntity<List<Transaction>> getPayment(HttpServletRequest request)
    {
        LOGGER.info(request.getSession().getId() + " - Fetching transactions");

        LOGGER.info(request.getSession().getId() + " - Fetching transaction - Actual fetching transactions");
        int id;
        try
        {
            id = Integer.parseInt(Objects.requireNonNull(treeController.getCurrentUser(request).getBody()));
        } catch (NullPointerException e)
        {
            LOGGER.error(request.getSession().getId() + " - Fetching transaction - Cannot fetch a transaction - user id obtained from the session is null");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        LOGGER.info(request.getSession().getId() + " - Fetching transaction - Get Transaction - User Id: " + id);
        User user = userRepo.findById(id).orElse(null);
        if (user == null)
        {
            LOGGER.info(request.getSession().getId() + " - Fetching transaction - The user is yikes");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        var transactionList = transactionRepo.findAllByUserId(user.getId());
        if (transactionList.size() == 0)
        {
            LOGGER.info(request.getSession().getId() + " - Fetching transaction - Found 0 transactions for user " + user.getId());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        LOGGER.info(request.getSession().getId() + " - Fetching transaction - Found " + transactionList.size() + " transactions for user " + user.getId());
        LOGGER.info(request.getSession().getId() + " - Fetching transaction - Updating the existing orders while returning the current list");
        // This is a compact version of a lambda expression
        new Thread(this::updateOrderStatuses).start();
        return new ResponseEntity<>(transactionList, HttpStatus.OK);
    }

    @Autowired
    MailService mailService;

    // also sends the order confirmation email because I'm stupid
    @GetMapping("/getTransaction/{id}")
    ResponseEntity<Transaction> getPayment(@PathVariable(name = "id") String id)
    {
        LOGGER.info("Fetching specific transaction - Fetching transaction with an id: " + id);

         /*
        LOGGER.info("Transaction " + id + " - Get current user Id");
        int userId;
        try
        {
            userId = Integer.parseInt(Objects.requireNonNull(treeController.getCurrentUser(request).getBody()));
        } catch (NullPointerException e)
        {
            LOGGER.error("Cannot fetch a transaction - user id obtained from the session is null");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        LOGGER.info("Get Transaction - User Id: " + userId);
        User user = userRepo.findById(userId).orElse(null);
        if (user == null)
        {
            LOGGER.info("The user is yikes");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        */
        Transaction transaction;

        // The id comes as test-XXXX or order-XXXX. The extra characters have to be removed
        int extractedId;
        try
        {
            String extractionPattern = "0+(\\d+)";
            Pattern pattern = Pattern.compile(extractionPattern);
            Matcher matcher = pattern.matcher(id);
            if (matcher.find())
            {
                extractedId = Integer.parseInt(matcher.group(1));
            } else
            {
                LOGGER.info("Fetching specific transaction - Provided transaction id does not seem valid");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

        } catch (NumberFormatException e)
        {
            LOGGER.info("Fetching specific transaction - Provided transaction id was not a number");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        transaction = transactionRepo.findById(extractedId).orElse(null);

        if (transaction == null)
        {
            LOGGER.info("Fetching specific transaction - Found no matching transaction for transaction id: " + extractedId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        LOGGER.info("Fetching specific transaction - Found a transaction, orders: " + transaction.getOrderList().size());

        LOGGER.info("Fetching specific transaction - Sending a order confirmation email for transaction " + transaction.getId() + " to email: " + transaction.getUser().getEmail());

        Map<String, Integer> sizeToPriceMap = new HashMap<>();
        sizeToPriceMap.put("20x20 cm", 495);
        sizeToPriceMap.put("25x25 cm", 695);
        sizeToPriceMap.put("30x30 cm", 995);

        int subtotalPrice = 0;
        int totalOrderAmount = 0;

        StringBuilder emailOrderRows = new StringBuilder();
        for (TreeOrder order : transaction.getOrderList())
        {
            String designJson = order.getTreeDesignById().getDesignJson();
            String designName = "Untitled";
            String namePattern = "name\":\"([\\w\\s\"]+)\",";
            Pattern pattern = Pattern.compile(namePattern);
            Matcher matcher = pattern.matcher(designJson);
            if (matcher.find())
            {
                designName = matcher.group(1);
            } else
            {
                LOGGER.warn("Fetching specific transaction - Failed to obtain a match for the design name in order id: " + order.getOrderId());
            }
            int orderAmount = order.getAmount();
            totalOrderAmount += orderAmount;
            int orderPrice = sizeToPriceMap.get(order.getSize()) * orderAmount;
            String row =
                    "        <tr style=\"width: 60vw; margin: 0 18vw;\">\n" +
                            "            <td style=\"width: 18vw;border: 1px lightgrey solid; padding: 1vh 1vw; text-align: left\">" + designName + "</td>\n" +
                            "            <td style=\"width: 18vw;border: 1px lightgrey solid; padding: 1vh 1vw; text-align: center\">" + orderAmount + "</td>\n" +
                            "            <td style=\"width: 18vw;border: 1px lightgrey solid; padding: 1vh 1vw; text-align: center\">" + orderPrice + "kr" + "</td>\n" +
                            "        </tr>\n";
            emailOrderRows.append(row);
            subtotalPrice += orderPrice;
        }

        int discountPrice = 0;
        LOGGER.info("Fetching specific transaction - Order confirmation price calculation for transactionID: " + transaction.getId() +
                " - Calculated Subtotal: " + subtotalPrice);
        int totalPrice = subtotalPrice;


        if (totalOrderAmount > 3)
        {
            discountPrice = (int) Math.ceil(totalPrice * 0.25);
            totalPrice *= 0.75;
        }

        if (transaction.getDiscount() != null)
        {
            var discountCodeResponse = getDiscountCode(transaction.getDiscount());
            if (discountCodeResponse.getStatusCode() != HttpStatus.OK)
            {
                LOGGER.warn("Fetching specific transaction - Failed to find the discount code for the transaction id: " + transaction.getId());
            } else
            {
                var discountCode = discountCodeResponse.getBody();

                int amount = Integer.parseInt(discountCode.getDiscountAmount());
                String type = discountCode.getDiscountType();
                if (type.equals("minus"))
                {
                    LOGGER.info("Fetching specific transaction - Order confirmation price calculation for transactionID: " + transaction.getId() +
                            " - applying discount of -" + amount + " to total price of: " + totalPrice);
                    discountPrice += amount;
                    totalPrice = totalPrice - amount;
                    if (totalPrice < 0) totalPrice = 0;
                }
                if (type.equals("percent"))
                {
                    LOGGER.info("Fetching specific transaction - Order confirmation price calculation for transactionID: " + transaction.getId() +
                            " - applying discount of -" + amount + "% to total price of: " + totalPrice);
                    double percent = (100.0 - amount) / 100;
                    int newPrice = (int) Math.floor(((totalPrice) * percent)) * 100;
                    int priceDifference = totalPrice - (newPrice / 100);
                    totalPrice = newPrice / 100;
                    discountPrice += priceDifference; // this is the discount obtained via the % discount
                }
            }
        }
        LOGGER.info("Fetching specific transaction - Re-calculated prices for order confirmation email for transaction: " + transaction.getId() +
                " | Subtotal price (no discounts applied): " + subtotalPrice + " | discount: " + discountPrice +
                " | total price (after discounts): " + totalPrice);

        if (totalPrice * 100 != transaction.getPrice())
        {
            LOGGER.warn("Fetching specific transaction - During order confirmation price calculations, the total price (" + totalPrice * 100 + ") and" +
                    " transaction's registered price (" + transaction.getPrice() + ") don't match!");
        }

        String emailSubject = " <p>\n" +
                "        Hi " + transaction.getName() + ",\n" +
                "        <br><br>\n" +
                "        Just to let you know - we've received your order #" + transaction.getId() + ", and it is now being processed:\n" +
                "    </p>\n" +
                "    <h1>[Order " + transaction.getId() + "] (" + LocalDate.now().toString() + ")</h1>\n" +
                "    <table style=\"border-spacing: 0;\">\n" +
                "        <tr style=\"width: 60vw; margin: 0 18vw\">\n" +
                "            <th style=\"width: 18vw;border: 1px lightgrey solid; padding: 1vh 1vw;\">Product</th>\n" +
                "            <th style=\"width: 18vw;border: 1px lightgrey solid; padding: 1vh 1vw;\">Quantity</th>\n" +
                "            <th style=\"width: 18vw;border: 1px lightgrey solid; padding: 1vh 1vw;\">price</th>\n" +
                "        </tr>\n" +
                emailOrderRows +
                "    </table>\n" +
                "    <table style=\"border-spacing: 0\">\n" +
                "        <tr style=\"width: 60vw; margin: 0 18vw;\">\n" +
                "            <td style=\"width: 28vw;border: 1px lightgrey solid; padding: 1vh 1vw\">Subtotal</td>\n" +
                "            <td style=\"width: 28vw;border: 1px lightgrey solid; padding: 1vh 1vw\">" + subtotalPrice + "kr</td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td style=\"width: 28vw;border: 1px lightgrey solid; padding: 1vh 1vw\">Discount:</td>\n" +
                "            <td style=\"width: 28vw;border: 1px lightgrey solid; padding: 1vh 1vw\">-" + discountPrice + "kr</td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td style=\"width: 28vw;border: 1px lightgrey solid; padding: 1vh 1vw\">Shipping:</td>\n" +
                "            <td style=\"width: 28vw;border: 1px lightgrey solid; padding: 1vh 1vw\">Free delivery | Estimated Delivery time: 2-3 weeks</td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td style=\"width: 28vw;border: 1px lightgrey solid; padding: 1vh 1vw\">Total:</td>\n" +
                "            <td style=\"width: 28vw;border: 1px lightgrey solid; padding: 1vh 1vw\">" + transaction.getPrice() / 100 + "kr</td>\n" +
                "        </tr>\n" +
                "    </table>\n" +
                "\n" +
                "    <br><br>\n" +
                "\n" +
                "    <table style=\"border-spacing: 0\">\n" +
                "        <tr style=\"width: 60vw; margin: 0 18vw;\">\n" +
                "            <th style=\"width: 28vw;border: 1px lightgrey solid; padding: 1vh 1vw\">Billing Address</th>\n" +
                "            <th style=\"width: 28vw;border: 1px lightgrey solid; padding: 1vh 1vw\">Shipping Address</th>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td style=\"width: 28vw;border: 1px lightgrey solid; padding: 1vh 1vw; line-height: 18px\">\n" +
                "                " + transaction.getName() + "<br>\n" +
                "                " + transaction.getStreetAddress() + " <br>\n" +
                // If the street address line two is not empty, add it to the email
                (transaction.getStreetAddress2() == null ? "" : (transaction.getStreetAddress2() + " <br>\n")) +
                "                " + transaction.getPostcode() + " " + transaction.getCity() + " <br>\n" +
                "                " + transaction.getCountry() + " <br>\n" +
                "                " + transaction.getPhoneNumber() + " <br>\n" +
                "                " + transaction.getEmail() + "\n" +
                "            </td>\n" +
                "            <td style=\"width: 28vw;border: 1px lightgrey solid; padding: 1vh 1vw; line-height: 18px\">\n" +
                "                " + transaction.getName() + "<br>\n" +
                "                " + transaction.getStreetAddress() + " <br>\n" +
                // If the street address line two is not empty, add it to the email
                (transaction.getStreetAddress2() == null ? "" : (transaction.getStreetAddress2() + " <br>\n")) +
                "                " + transaction.getPostcode() + " " + transaction.getCity() + " <br>\n" +
                "                " + transaction.getCountry() + " <br>\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "    </table>";


        LOGGER.info("Fetching specific transaction - Sending out the email to " + transaction.getEmail());
        try
        {
            mailService.sendOrderMail(emailSubject,
                    "Order Confirmation", transaction.getUser().getEmail());
            LOGGER.info("Fetching specific transaction - Forwarding the order confirmation from " + transaction.getEmail() + " to orders@treecreate.dk");
            mailService.sendOrderMail(emailSubject,
                    "New Order Confirmation", "orders@treecreate.dk");
        } catch (MessagingException e)
        {
            LOGGER.error("Fetching specific transaction - Failed to send an email for transaction order info", e);
        }

        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }

    @PostMapping("/paymentCallback")
    ResponseEntity<String> paymentCallback(@RequestBody String body)
    {
        LOGGER.info("Received a callback from quickpay:\n" + body);
        if (!body.contains("accepted\":true"))
        {
            LOGGER.info("The callback ´accepted´ field is marked as false or missing, Ignoring the callback");
            return new ResponseEntity<>(HttpStatus.OK);
        }

        LOGGER.info("The callback ´accepted´ field is marked as true. Continuing on to send a order confirmation email");
        String orderIdPattern = "order_id\":\"([\\w-]+)";
        Pattern pattern = Pattern.compile(orderIdPattern);
        Matcher matcher = pattern.matcher(body);
        if (matcher.find())
        {
            String orderId = matcher.group(1);
            getPayment(orderId);
        } else
        {
            LOGGER.warn("Failed to obtain the orderId from the callback. NOT sending a order confirmation email");
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Autowired
    DiscountCodeRepo discountCodeRepo;

    @GetMapping("/getDiscountCode")
    ResponseEntity<DiscountCode> getDiscountCode(@RequestParam(name = "code") String code)
    {
        LOGGER.info("Getting a discount for code: " + code);
        if (code.equals(""))
        {
            LOGGER.warn("Provided discount code was empty");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        var discountCode = discountCodeRepo.findObeByDiscountCode(code);
        if (discountCode == null)
        {
            LOGGER.warn("Provided discount code does not match existing codes");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(discountCode, HttpStatus.OK);
    }

    @GetMapping("/fail")
    String fail()
    {
        return "payment/fail";
    }
}
