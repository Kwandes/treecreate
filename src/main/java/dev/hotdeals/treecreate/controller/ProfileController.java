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
    String accountInfo(Model model, HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        if (session.getAttribute("userId") == null)
        {
            LOGGER.info("Session " + session.getId() + " does not have a logged in user");
            return "redirect:/aboutUs";
        }

        int id = 0;
        try
        {
            id = Integer.parseInt(session.getAttribute("userId").toString());
        } catch (NumberFormatException | NullPointerException e)
        {
            LOGGER.error("Provided user ID is invalid, session " + request.getSession().getId(), e);
        }

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
    String updateAccountInfo(WebRequest request, HttpServletRequest httpServletRequest)
    {
        HttpSession session = httpServletRequest.getSession();
        if (session.getAttribute("userId") == null)
        {
            LOGGER.info("Session " + session.getId() + " does not have a logged in user");
            return "redirect:/aboutUs";
        }

        int id = 0;
        try
        {
            id = Integer.parseInt(session.getAttribute("userId").toString());

        } catch (NumberFormatException | NullPointerException e)
        {
            LOGGER.error("Provided user ID was invalid", e);
            return "redirect:/aboutUs";
        }

        User user = userRepo.findById(id).orElse(null);

        if (user == null)
        {
            LOGGER.warn("User doesn't exist, going back");
            return "redirect:/aboutUs";

        } else
        {
            user.setName(request.getParameter("inputName"));
            user.setEmail(request.getParameter("inputEmail"));
            user.setPhoneNumber(request.getParameter("inputPhoneNumber"));
            user.setStreetAddress(request.getParameter("inputStreetAddress"));
            user.setCity(request.getParameter("inputCity"));
            user.setPostcode(request.getParameter("inputPostcode"));

            LOGGER.info("User " + id + " information has been updated");
            userRepo.save(user);
            return "redirect:account/info";
        }
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

        String email = body.getEmail();
        String password = body.getPassword();
        if (email == null || password == null)
        {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        User user = userRepo.findOneByEmail(email);
        if (user != null && PasswordService.matches(password, user.getPassword()))
        {
            session.setAttribute("userId", user.getId());
            LOGGER.info("New login: " + session.getId() + " as user: " + user.getId());
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = {"/logout"}, method = {RequestMethod.GET, RequestMethod.POST})
    String logout(HttpServletRequest request)
    {
        endSession(request);
        return "redirect:/aboutUs";
    }

    @ResponseBody
    @RequestMapping(value = {"/endSession"}, method = {RequestMethod.GET, RequestMethod.POST})
    ResponseEntity<Boolean> endSession(HttpServletRequest httpServletRequest)
    {
        LOGGER.info("Ending session " + httpServletRequest.getSession().getId());
        httpServletRequest.getSession().invalidate();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "isLoggedIn", method = {RequestMethod.GET, RequestMethod.POST})
    ResponseEntity<Boolean> isLoggedIn(HttpServletRequest request)
    {
        return new ResponseEntity<>(request.getSession().getAttribute("userId") != null, HttpStatus.OK);
    }

}
