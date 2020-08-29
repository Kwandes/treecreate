package dev.hotdeals.treecreate.controller;

import dev.hotdeals.treecreate.model.User;
import dev.hotdeals.treecreate.service.PasswordService;
import dev.hotdeals.treecreate.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import java.lang.invoke.MethodHandles;

@Controller
public class LoginController
{
    private static final Logger LOGGER = LogManager.getLogger(MethodHandles.lookup().lookupClass());

    @GetMapping({"", "/", "/index", "index/"})
    public String index()
    {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(HttpServletRequest request, Model model)
    {
        model.addAttribute("loginError", request.getSession().getAttribute("loginError"));
        request.getSession().invalidate();
        return "login/login";
    }

    @Autowired
    UserService userService;

    @PostMapping("/submitLogin")
    public String submitLogin(WebRequest wr, Model model, HttpServletRequest request)
    {
        User user = userService.searchByUsername(wr.getParameter("username"));
        String password = wr.getParameter("password");
        if (user == null || !user.getUsername().equals(wr.getParameter("username")))
        {
            System.out.println("Failed login - Username '" + wr.getParameter("username") + "' not found");
            request.getSession().setAttribute("loginError", "invalid credentials");
            return "redirect:/login";
        } else if (!PasswordService.matches(password, user.getPassword()))
        {
            System.out.println("Failed login - User '" + wr.getParameter("username") + "' has inputted wrong password");
            request.getSession().setAttribute("loginError", "invalid credentials");
            return "redirect:/login";
        }

        request.getSession().setAttribute("username", user.getUsername());
        request.getSession().setAttribute("email", user.getEmail());
        request.getSession().setAttribute("accessLevel", user.getAccessLevel());
        return "redirect:/successPage";
    }

    @GetMapping("/createNewAccount")
    public String createNewAccount()
    {
        return "login/createAccount";
    }

    @PostMapping("/submitNewAccount")
    public String submitNewAccount(WebRequest wr, HttpServletRequest request)
    {
        User user = userService.searchByUsername(wr.getParameter("username"));
        if (user != null)
        {
            System.out.println("User " + user.getUsername() + " already exists");
            request.getSession().setAttribute("loginError", "username is taken");
            return "redirect:/login";
        }
        user = new User();
        user.setUsername(wr.getParameter("username"));
        user.setPassword(PasswordService.encodePassword(wr.getParameter("password")));
        user.setEmail(wr.getParameter("email"));
        LOGGER.info("Adding new user (" + user + ")");
        userService.addUser(user);

        return "redirect:/login";
    }

    @GetMapping("/successPage")
    public String successPage(HttpServletRequest request, Model model)
    {
        // check if given user has access to this page
        if (request.getSession().getAttribute("accessLevel") == null)
        {
            LOGGER.debug("Access Denied to user [" + request.getSession().getAttribute("username") + "] due to null access Level");
            request.getSession().setAttribute("loginError", "invalid access level");
            return "redirect:/login";
        }

        // log successful login
        LOGGER.debug("New login as " + request.getSession().getAttribute("username"));
        model.addAttribute("username", request.getSession().getAttribute("username"));
        model.addAttribute("email", request.getSession().getAttribute("email"));
        model.addAttribute("accessLevel", request.getSession().getAttribute("accessLevel"));

        return "login/successPage";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request)
    {
        LOGGER.debug(request.getSession().getAttribute("username") + " has logged out");
        request.getSession().invalidate();
        return "redirect:/login";
    }
}