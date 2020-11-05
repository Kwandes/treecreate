package dev.hotdeals.treecreate.controller;

import dev.hotdeals.treecreate.model.TreeOrder;
import dev.hotdeals.treecreate.model.User;
import dev.hotdeals.treecreate.repository.TreeOrderRepo;
import dev.hotdeals.treecreate.repository.UserRepo;
import dev.hotdeals.treecreate.service.MailService;
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

import java.util.List;

import static org.hibernate.bytecode.BytecodeLogger.LOGGER;

@Controller
public class ProfileController
{
    @Autowired
    UserRepo userRepo;

    @Autowired
    TreeController treeController;

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
            return "redirect:/productExample";
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
            LOGGER.warn("User doesn't exist, going to productExample");
            return "redirect:/productExample";
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
            return "redirect:/productExample";
        }

        int id = 0;
        try
        {
            id = Integer.parseInt(session.getAttribute("userId").toString());

        } catch (NumberFormatException | NullPointerException e)
        {
            LOGGER.error("Provided user ID was invalid", e);
            return "redirect:/productExample";
        }

        User user = userRepo.findById(id).orElse(null);

        if (user == null)
        {
            LOGGER.warn("User doesn't exist, going back");
            return "redirect:/productExample";

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

    @GetMapping("/account/collections")
    String treeCollections()
    {
        return "profile/treeCollections";
    }

    @Autowired
    TreeOrderRepo treeOrderRepo;

    @Autowired
    MailService mailService;

    @PostMapping("/addUser")
    ResponseEntity<Boolean> addUser(HttpServletRequest request, @RequestBody User user)
    {
        LOGGER.info("Adding new user, email: " + user.getEmail());
        if (userRepo.findOneByEmail(user.getEmail()) != null)
        {
            LOGGER.info("Submitted new user already exists");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        String oldPassword = user.getPassword();
        user.setPassword(PasswordService.encodePassword(user.getPassword()));
        user.setVerification(PasswordService.generateVerificationToken(8));
        userRepo.save(user);
        System.out.println(user);
        User dummyUser = new User(); // Used to bypass JPA's cache allowing modification of the password
        dummyUser.setEmail(user.getEmail());
        dummyUser.setPassword(oldPassword);
        var result = validateCredentials(request, dummyUser);
        if (result.getStatusCode() != HttpStatus.OK)
        {
            return result;
        }
        LOGGER.info("Sending a verification email to the user");
        // Verification link won't work on localhost since it is hardcoded to treecreate.dk.
        mailService.sendInfoMail("Thank you for signing up at Treecreate\n" +
                        "\nPlease click on the link in order to verify: https://treecreate.dk/verify?id=" + user.getId() + "&token=" + user.getVerification() +
                        "\n\nThis is an automated email. Please do not reply to this email.",
                "Confirm your e-mail", user.getEmail());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ResponseBody
    @PostMapping("/submitLogin")
    ResponseEntity<Boolean> validateCredentials(HttpServletRequest httpServletRequest, @RequestBody User body)
    {
        LOGGER.info("Validating a login submission");
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
            LOGGER.info("Submitted login contained null email/password");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        User user = userRepo.findOneByEmail(email);
        LOGGER.info("Login submission: Found user: " + user);
        if (user != null && PasswordService.matches(password, user.getPassword()))
        {
            LOGGER.info("User is logging in, searching for ID: " + session.getId());
            User sessionUser = userRepo.findOneByEmail(session.getId());
            if (sessionUser != null)
            {
                LOGGER.info("Found user: " + sessionUser);
                List<TreeOrder> orderList = treeOrderRepo.findAllByUserId(sessionUser.getId());
                LOGGER.info("Found all orders for the user. Orders: " + orderList.size());

                for (TreeOrder order : orderList)
                {
                    LOGGER.info("Order " + order.getOrderId() + " is being assigned to " + user);
                    order.setUserByUserId(user);
                    treeOrderRepo.save(order);
                }
                LOGGER.info("All orders have been remapped, deleting the temp user");
                userRepo.delete(sessionUser);
            } else
            {
                LOGGER.info("No session user found, moving on");
            }

            session.setAttribute("userId", user.getId());
            LOGGER.info("New login: " + session.getId() + " as user: " + user.getId());
            return new ResponseEntity<>(HttpStatus.OK);
        }
        LOGGER.info("Login failed, probably due to an invalid password");
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = {"/logout"}, method = {RequestMethod.GET, RequestMethod.POST})
    String logout(HttpServletRequest request)
    {
        endSession(request);
        return "redirect:/productExample";
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

    @GetMapping("/verify")
    String verifyUser(@RequestParam(name = "id") int id, @RequestParam(name = "token") String token)
    {
        LOGGER.info("Verification request received for user id: " + id);
        User user = userRepo.findById(id).orElse(null);
        if (user != null && user.getVerification().equals(token))
        {
            LOGGER.info("Verification successful, changing the status to verified");
            user.setVerification("verified");
            userRepo.save(user);
            return "home/verificationSuccess";
        }
        return "home/verificationFail";
    }

    @GetMapping("/cookiesValidation")
    ResponseEntity<Boolean> hasAcceptedCookies(HttpServletRequest request)
    {
        String userID = treeController.getCurrentUser(request).getBody();
        User currentUser = userRepo.findById(Integer.parseInt(userID)).orElse(null);
        if (currentUser != null) {
            return new ResponseEntity<>(currentUser.getAcceptedCookies(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/acceptCookies")
    ResponseEntity<String> acceptCookies(HttpServletRequest request)
    {
        String userID = treeController.getCurrentUser(request).getBody();
        User currentUser = userRepo.findById(Integer.parseInt(userID)).orElse(null);
        if (currentUser != null) {
            currentUser.setAcceptedCookies(true);
            userRepo.save(currentUser);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
