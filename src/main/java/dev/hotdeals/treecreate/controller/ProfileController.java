package dev.hotdeals.treecreate.controller;

import dev.hotdeals.treecreate.model.User;
import dev.hotdeals.treecreate.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.WebRequest;

import java.util.Arrays;
import java.util.Objects;

import static org.hibernate.bytecode.BytecodeLogger.LOGGER;

@Controller
public class ProfileController
{
    @Autowired
    UserRepo userRepo;

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
    String accountInfo(Model model)
    {
        int id = 2;
        User defaultUser = userRepo.findById(id).orElse(null);
        if (defaultUser == null)
        {
            LOGGER.warn("User doesn't exist, going to aboutUs");
            return "redirect:/aboutUs";
        } else
        {
            model.addAttribute(defaultUser);
            return "profile/accountInfo";
        }
    }

    @PostMapping("/account/updateInfo")
    String updateAccountInfo(WebRequest request)
    {
        LOGGER.info("Updated: ");
        request.getParameterMap().forEach((key, value) -> System.out.println((key + ":" + Arrays.toString(value))));


        int id = 0;
        try
        {
            id = Integer.parseInt(Objects.requireNonNull(request.getParameter("id")));

        } catch (NumberFormatException | NullPointerException e)
        {
            LOGGER.error("Shit", e);
        }

        User user = userRepo.findById(id).orElse(null);

        if (user == null)
        {
            LOGGER.warn("User doesn't exist, going back");
        } else
        {
            user.setName(request.getParameter("inputName"));
            user.setEmail(request.getParameter("inputEmail"));
            user.setPhoneNumber(request.getParameter("inputPhoneNumber"));
            user.setStreetAddress(request.getParameter("inputStreetAddress"));
            user.setCity(request.getParameter("inputCity"));
            user.setPostcode(request.getParameter("inputPostcode"));

            userRepo.save(user);
        }

        return "redirect:/account/info";
    }
}
