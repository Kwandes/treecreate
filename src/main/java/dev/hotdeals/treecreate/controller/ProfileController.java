package dev.hotdeals.treecreate.controller;

import dev.hotdeals.treecreate.model.User;
import dev.hotdeals.treecreate.repository.UserRepo;
import dev.hotdeals.treecreate.service.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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

    @ResponseBody
    @PostMapping("/submitLogin")
    ResponseEntity<Boolean> validateCredentials(HttpServletRequest httpServletRequest, @RequestBody User body)
    {
        HttpSession session = httpServletRequest.getSession();
        if (session.getAttribute("userId") != null)
        {
            LOGGER.info("Session " + session.getId() + " already is logged in as user " + session.getAttribute("userId"));
            return new ResponseEntity<>(HttpStatus.OK);
        }

        //String email = webRequest.getParameter("email");
        //String password = webRequest.getParameter("password");
        String email = body.getEmail();
        String password = body.getPassword();
        System.out.println("e:" + email + " P: " + password);
        if (email == null || password == null)
        {
            LOGGER.info("Login denied - email is yikes");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        User user = userRepo.findOneByEmail(email);
        System.out.println(user);
        if (user != null && PasswordService.matches(password, user.getPassword()))
        {
            session.setAttribute("userId", user.getId());
            LOGGER.info("New login: " + session.getId() + " as user: " + user.getId());
            return new ResponseEntity<>(HttpStatus.OK);
        }

        LOGGER.info("Login denied");
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @ResponseBody
    @RequestMapping(value = {"/logout", "/endSession"}, method = {RequestMethod.GET, RequestMethod.POST})
    ResponseEntity<Boolean> endSession(HttpServletRequest httpServletRequest)
    {
        LOGGER.info("Ending session " + httpServletRequest.getSession().getId());
        httpServletRequest.getSession().invalidate();
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
