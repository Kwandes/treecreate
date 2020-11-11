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
        LOGGER.info("Starting a new payment");
        int id;
        try
        {
            id = Integer.parseInt(Objects.requireNonNull(treeController.getCurrentUser(request).getBody()));
        } catch (NullPointerException e)
        {
            LOGGER.error("Cannot start a payment - user id obtained from the session is null");
            return new ResponseEntity<>("Failed to obtain the user from the session. Please do report this to the developers", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        LOGGER.info("Payment - User Id: " + id);
        User user = userRepo.findById(id).orElse(null);
        if (user == null)
        {
            LOGGER.info("The user is yikes");
            return new ResponseEntity<>("Failed to find a user with an ID of " + id, HttpStatus.NOT_FOUND);
        }

        LOGGER.info("Verifying verification");
        if (!user.getVerification().equals("verified"))
        {
            LOGGER.info("User " + user.getId() + " is not verified");
            return new ResponseEntity<>("User is not verified", HttpStatus.FORBIDDEN);
        }
        LOGGER.info("Everything looks okay, creating a new transaction for user " + user.getId());
        LOGGER.info("Getting all orders for user " + user.getId());
        var orderList = treeOrderRepo.findAllByUserId(user.getId());
        if (orderList.size() == 0)
        {
            LOGGER.warn("The order list for user " + user.getId() + " is empty! Cancelling the transaction");
            return new ResponseEntity<>("Internal server error - failed to find orders. Contact Support for more information", HttpStatus.NOT_FOUND);
        }
        LOGGER.info("Order list size: " + orderList.size());
        int totalPrice = 0;
        Map<String, Integer> sizeToPriceMap = new HashMap<>();
        sizeToPriceMap.put("20x20 cm", 495);
        sizeToPriceMap.put("25x25 cm", 695);
        sizeToPriceMap.put("30x30 cm", 995);
        orderList.removeIf(order -> !order.getStatus().equals("active"));
        for (TreeOrder order : orderList)
        {
            int price = sizeToPriceMap.get(order.getSize());
            totalPrice += price * order.getAmount();
        }
        if (orderList.size() > 3)
            totalPrice = (int) (totalPrice * 0.75);
        totalPrice *= 100; // Quickpay takes 2 digits as decimal places, so 1000 becomes 10,00

        if (transaction.getDiscount() != null)
        {
            LOGGER.info("Applying the discount for the transaction");
            LOGGER.info("Total price pre-discount: " + totalPrice);
            var discountCodeResponse = getDiscountCode(transaction.getDiscount());
            if (discountCodeResponse.getStatusCode() != HttpStatus.OK)
            {
                LOGGER.info("Failed to find the discount code");
            } else
            {
                var discountCode = discountCodeResponse.getBody();
                if (!discountCode.getActive())
                {
                    LOGGER.info("Provided discount is no longer active");
                } else
                {
                    int amount = Integer.parseInt(discountCode.getDiscountAmount());
                    String type = discountCode.getDiscountType();
                    if (type.equals("minus"))
                    {
                        LOGGER.info("Applying a discount of -" + amount + "kr");
                        totalPrice = totalPrice - (amount * 100);
                        if (totalPrice < 0) totalPrice = 0;
                    }
                    if (type.equals("percent"))
                    {
                        LOGGER.info("Applying a discount of " + amount + "%");
                        double percent = (100.0 - amount) / 100;
                        LOGGER.info("Percent: " + percent);
                        totalPrice = (int) Math.floor(((totalPrice / 100.0) * percent)) * 100;

                    }
                }
            }
        }

        LOGGER.info("Price: " + totalPrice);
        LOGGER.info("Creating a new transaction");
        LOGGER.info("Transaction shipment info: " + transaction);
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
        LOGGER.info("Transaction ID: " + transaction.getId());
        LOGGER.info("Transaction " + transaction.getId() + " - Creating a payment");

        LOGGER.info("Transaction " + transaction.getId() + " - Creating a payment with an order id: " + createPaymentOrderId(transaction.getId()));

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

        LOGGER.info("Getting the payment via the transaction id: " + createPaymentOrderId(transaction.getId()));

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
            LOGGER.info("Transaction " + transaction.getId() + " - Failed to find the payment ID in the response body");
            return new ResponseEntity<>("Failed to extract the payment id from the payment order", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String paymentId = matcher.group(1);
        LOGGER.info("Getting a payment link for transaction ID: " + paymentId);

        httpRequest = HttpRequest.newBuilder(
                URI.create("https://api.quickpay.net/payments/" + paymentId + "/link"))
                .header("accept", "application/json")
                .header("Content-Type", "multipart/form-data")
                .header("Accept-Version", "v10")
                .header("Authorization", basicAuth("", apiKey))
                .PUT(HttpRequest.BodyPublishers.ofString("amount=" + totalPrice))
                .build();

        response = null;
        try
        {
            response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e)
        {
            LOGGER.error("Transaction " + transaction.getId() + " - Payment Link request made a fucky wucky", e);
            return new ResponseEntity<>("A request for obtaining a payment link has failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        LOGGER.info("Setting transaction " + transaction.getId() + " status to 'initial'");
        transaction.setStatus("initial");
        transactionRepo.save(transaction);

        LOGGER.info("Transaction " + transaction.getId() + " - Changing the status of orders to pending");
        for (TreeOrder order : orderList)
        {
            LOGGER.info("Transaction - changing status of order" + order.getOrderId() + " to pending");
            order.setStatus("pending");
            treeOrderRepo.save(order);
        }

        LOGGER.info("Transaction " + transaction.getId() + " - Returning a link");
        return new ResponseEntity<>(response.body(), HttpStatus.OK);
    }

    private static String basicAuth(String username, String password)
    {
        return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }

    public String createPaymentOrderId(int id)
    {
        String fluffedId = id + "";
        while (fluffedId.length() < 4)
        {
            fluffedId = "0" + fluffedId;
        }
        return fluffedId;
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
                    LOGGER.warn("Failed to find a payment with transaction id: " + transaction.getId());
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
            LOGGER.info("Found a transaction with a status other than initial: " + paymentStatus);

            var orderIdList = transaction.getOrders().split(",");
            for (String orderId : orderIdList)
            {
                TreeOrder order = treeOrderRepo.findById(Integer.parseInt(orderId)).orElse(null);
                if (order == null)
                {
                    LOGGER.warn("Updating order statuses - Transaction " + transaction.getId() +
                            " - failed to find an order with an id: " + orderId);
                    continue;
                }
                order.setStatus(paymentStatus);
                treeOrderRepo.save(order);
            }
            transaction.setStatus(paymentStatus);
            transactionRepo.save(transaction);
            LOGGER.info("Finished setting status for transaction " + transaction.getId() + " and its orders. New status: " + paymentStatus);
        }
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
        LOGGER.info("Fetching transactions");
        LOGGER.info("First, update the existing orders etc");
        updateOrderStatuses();

        LOGGER.info("Actual fetching transactions");
        int id;
        try
        {
            id = Integer.parseInt(Objects.requireNonNull(treeController.getCurrentUser(request).getBody()));
        } catch (NullPointerException e)
        {
            LOGGER.error("Cannot fetch a transaction - user id obtained from the session is null");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        LOGGER.info("Get Transaction - User Id: " + id);
        User user = userRepo.findById(id).orElse(null);
        if (user == null)
        {
            LOGGER.info("The user is yikes");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        var transactionList = transactionRepo.findAllByUserId(user.getId());
        if (transactionList.size() == 0)
        {
            LOGGER.info("Found 0 transactions for user " + user.getId());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        LOGGER.info("Found " + transactionList.size() + " transactions for user " + user.getId());
        return new ResponseEntity<>(transactionList, HttpStatus.OK);
    }

    @Autowired
    MailService mailService;

    @GetMapping("/getTransaction/{id}")
    ResponseEntity<Transaction> getPayment(HttpServletRequest request, @PathVariable(name = "id") String id)
    {
        LOGGER.info("Fetching transaction with an id: " + id);

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
        try
        {
            transaction = transactionRepo.findById(Integer.parseInt(id)).orElse(null);
        } catch (NumberFormatException e)
        {
            LOGGER.info("Provided transaction id was not a number");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (transaction == null)
        {
            LOGGER.info("Found no matching transaction");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        LOGGER.info("Found a transaction, orders: " + transaction.getOrders());

        LOGGER.info("Sending an email");

        try
        {
            mailService.sendOrderMail(
                    " <p>\n" +
                            "        Hi " + transaction.getUser().getName() + ",\n" +
                            "        <br><br>\n" +
                            "        Just to let you know - we've received your order " + transaction.getId() + ", and it is not being processed:\n" +
                            "    </p>\n" +
                            "    <h1>[Order " + transaction.getId() + "] (" + LocalDate.now().toString() + ")</h1>\n" +
                            "    <table style=\"border-spacing: 0;\">\n" +
                            "        <tr style=\"width: 60vw; margin: 0 18vw\">\n" +
                            "            <th style=\"width: 18vw;border: 1px lightgrey solid; padding: 1vh 1vw;\">Product</th>\n" +
                            "            <th style=\"width: 18vw;border: 1px lightgrey solid; padding: 1vh 1vw;\">Quantity</th>\n" +
                            "            <th style=\"width: 18vw;border: 1px lightgrey solid; padding: 1vh 1vw;\">price</th>\n" +
                            "        </tr>\n" +
                            "        <tr style=\"width: 60vw; margin: 0 18vw;\">\n" +
                            "            <td style=\"width: 18vw;border: 1px lightgrey solid; padding: 1vh 1vw; text-align: left\">" + "Fml" + "</td>\n" +
                            "            <td style=\"width: 18vw;border: 1px lightgrey solid; padding: 1vh 1vw; text-align: center\">69</td>\n" +
                            "            <td style=\"width: 18vw;border: 1px lightgrey solid; padding: 1vh 1vw; text-align: center\">1234kr</td>\n" +
                            "        </tr>\n" +
                            "    </table>\n" +
                            "    <table style=\"border-spacing: 0\">\n" +
                            "        <tr style=\"width: 60vw; margin: 0 18vw;\">\n" +
                            "            <td style=\"width: 28vw;border: 1px lightgrey solid; padding: 1vh 1vw\">Subtotal</td>\n" +
                            "            <td style=\"width: 28vw;border: 1px lightgrey solid; padding: 1vh 1vw\">" + transaction.getPrice() + "kr</td>\n" +
                            "        </tr>\n" +
                            "        <tr>\n" +
                            "            <td style=\"width: 28vw;border: 1px lightgrey solid; padding: 1vh 1vw\">Shipping:</td>\n" +
                            "            <td style=\"width: 28vw;border: 1px lightgrey solid; padding: 1vh 1vw\">Free delivery | Estimated Delivery time: 2-3 weeks</td>\n" +
                            "        </tr>\n" +
                            "        <tr>\n" +
                            "            <td style=\"width: 28vw;border: 1px lightgrey solid; padding: 1vh 1vw\">Total:</td>\n" +
                            "            <td style=\"width: 28vw;border: 1px lightgrey solid; padding: 1vh 1vw\">" + transaction.getPrice() + "kr</td>\n" +
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
                            "                " + transaction.getPostcode() + " " + transaction.getCity() + " <br>\n" +
                            "                " + transaction.getCountry() + " <br>\n" +
                            "                " + transaction.getPhoneNumber() + " <br>\n" +
                            "                " + transaction.getEmail() + "\n" +
                            "            </td>\n" +
                            "            <td style=\"width: 28vw;border: 1px lightgrey solid; padding: 1vh 1vw; line-height: 18px\">\n" +
                            "                " + transaction.getName() + "<br>\n" +
                            "                " + transaction.getStreetAddress() + " <br>\n" +
                            "                " + transaction.getPostcode() + " " + transaction.getCity() + " <br>\n" +
                            "                " + transaction.getCountry() + " <br>\n" +
                            "            </td>\n" +
                            "        </tr>\n" +
                            "    </table>"
                    ,
                    "Transaction test", "info@treecreate.dk");
        } catch (MessagingException e)
        {
            LOGGER.error("Failed to send an email for transaction order info", e);
        }

        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }

    @PostMapping("/paymentCallback")
    ResponseEntity<String> paymentCallback(@RequestBody String body)
    {
        LOGGER.info("Recieved a callback from quickpay:\n" + body);
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
