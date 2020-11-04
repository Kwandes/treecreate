package dev.hotdeals.treecreate.controller;

import dev.hotdeals.treecreate.config.CustomProperties;
import dev.hotdeals.treecreate.model.User;
import dev.hotdeals.treecreate.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.hibernate.bytecode.BytecodeLogger.LOGGER;

@Controller
public class PaymentController
{
    @Autowired
    TreeController treeController;

    @Autowired
    UserRepo userRepo;

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

    @Autowired
    CustomProperties customProperties;

    @ResponseBody
    @PostMapping("/getPaymentHash")
    public ResponseEntity<String> getPaymentHash(@RequestBody String data)
    {
        LOGGER.info("Data received: " + data);
        String secret = customProperties.getNordeaSecret();
        if (!secret.contains("$2a$12"))
        {
            LOGGER.warn("Nordea secret is not present, unable to create a payment hash");
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
        LOGGER.info("Nordea secret: " + secret);
        data += secret;
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(data.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return new ResponseEntity<>(hashtext, HttpStatus.OK);

        } catch (NoSuchAlgorithmException e)
        {
            LOGGER.error(e);
        }
        return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
    }

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
