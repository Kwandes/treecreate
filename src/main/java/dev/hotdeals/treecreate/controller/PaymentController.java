package dev.hotdeals.treecreate.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


import static org.hibernate.bytecode.BytecodeLogger.LOGGER;

@Controller
public class PaymentController
{
    @GetMapping("/payment")
    String payment()
    {
        return "payment/test";
    }

    @GetMapping("/payment_success")
    public String paymentSuccess(@RequestParam(value = "data") String value1, @RequestParam(value = "data2") String value2)
    {
        LOGGER.info("Data 1: " + value1);
        LOGGER.info("Data 2: " + value2);
        return "payment/success";
    }

    @GetMapping("/payment_fail")
    public String paymentFail(@RequestParam(value = "data1") String value1, @RequestParam(value = "data2") String value2)
    {
        LOGGER.info("val1: " + value1);
        LOGGER.info("val2: " + value2);
        return "payment/fail";
    }
}
