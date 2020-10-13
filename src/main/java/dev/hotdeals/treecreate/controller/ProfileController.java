package dev.hotdeals.treecreate.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.WebRequest;

import java.util.Arrays;

import static org.hibernate.bytecode.BytecodeLogger.LOGGER;

@Controller
public class ProfileController
{
    @GetMapping(value = {"/profile", "/login"})
    String profileIndex()
    {
        return "redirect:/account";
    }

    @GetMapping("/account")
    String accountIndex()
    {
        return "redirect:/account/info";
    }

    @GetMapping("/account/info")
    String accountInfo()
    {
        return "profile/accountInfo";
    }

    @PostMapping("/account/updateInfo")
    String updateAccountInfo(WebRequest request)
    {
        LOGGER.info("Updated: ");
        request.getParameterMap().forEach((key, value) -> System.out.println((key + ":" + Arrays.toString(value))));
        return "redirect:/account/info";
    }
}
