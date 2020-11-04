package dev.hotdeals.treecreate.controller;

import dev.hotdeals.treecreate.config.CustomProperties;
import dev.hotdeals.treecreate.model.User;
import dev.hotdeals.treecreate.repository.UserRepo;
import dev.hotdeals.treecreate.model.Transaction;
import dev.hotdeals.treecreate.model.TreeOrder;
import dev.hotdeals.treecreate.repository.TransactionRepo;
import dev.hotdeals.treecreate.repository.TreeOrderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
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

    @GetMapping("/startPayment")
    ResponseEntity<String> startPayment(HttpServletRequest request)
    {
        LOGGER.info("Starting a new payment");
        int id = Integer.parseInt(treeController.getCurrentUser(request).getBody());
        LOGGER.info("Payment - User Id: " + id);
        User user = userRepo.findById(id).orElse(null);
        if (user == null)
        {
            LOGGER.info("The user is yikes");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        LOGGER.info("Verifying verification");
        if (!user.getVerification().equals("verified"))
        {
            LOGGER.info("User " + user.getId() + " is not verified");
            return new ResponseEntity<>("Not verified", HttpStatus.FORBIDDEN);
        }
        LOGGER.info("Everything looks okay, creating a new transaction for user " + user.getId());
        LOGGER.info("Getting all orders for user " + user.getId());
        var orderList = treeOrderRepo.findAllByUserId(user.getId());
        int totalPrice = 0;
        int totalAmountOfOrders = 0;
        String orderIdList = "";
        Map<String, Integer> sizeToPriceMap = new HashMap<>();
        sizeToPriceMap.put("20x20 cm", 495);
        sizeToPriceMap.put("25x25 cm", 695);
        sizeToPriceMap.put("30x30 cm", 995);
        for (TreeOrder order : orderList)
        {
            totalAmountOfOrders += order.getAmount();
            orderIdList += order.getOrderId() + ",";
            int price = sizeToPriceMap.get(order.getSize());
            totalPrice += price * order.getAmount();
        }
        orderIdList = orderIdList.substring(0, orderIdList.length() - 1);
        LOGGER.info("Order Id list: " + orderIdList);
        if (totalAmountOfOrders > 3)
            totalPrice = (int) (totalPrice * 0.75);
        LOGGER.info("Price: " + totalPrice);
        LOGGER.info("Creating a new transaction");
        Transaction transaction = new Transaction();
        transaction.setCurrency("dkk");
        transaction.setPrice(totalPrice);
        transaction.setUser(user);
        transaction.setOrders(orderIdList);
        transactionRepo.save(transaction);
        LOGGER.info("Transaction ID: " + transaction.getId());
        LOGGER.info("Creating a payment");

        LOGGER.info("Creating a payment with an id: " + createPaymentOrderId(transaction.getId()));

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
            LOGGER.error("Payment creation made a fucky wucky", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
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
            LOGGER.error("Payment request (getting the payment id) made a fucky wucky", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        String orderIdPattern = "\\[\\{\"id\":(\\d+)";
        Pattern pattern = Pattern.compile(orderIdPattern);
        Matcher matcher = pattern.matcher(response.body());
        if (!matcher.find())
        {
            LOGGER.info("Failed to find the payment ID in the response body");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
            LOGGER.error("Payment Link request made a fucky wucky", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        LOGGER.info("Returning a link");
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

    /*
    private static HttpRequest.BodyPublisher buildFormDataFromMap(Map<Object, Object> data) {
        var builder = new StringBuilder();
        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
        }
        System.out.println(builder.toString());
        return HttpRequest.BodyPublishers.ofString(builder.toString());
    }
    */

    @GetMapping(value = {"/basket", "shoppingBasket"})
    public String basket(Model model, HttpServletRequest request)
    {
        String userID = treeController.getCurrentUser(request).getBody();
        User currentUser = userRepo.findById(Integer.parseInt(userID)).orElse(null);
        if (currentUser != null) {
            model.addAttribute("user", currentUser);
        }

        return "payment/basket";
    }
}
