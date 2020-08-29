package dev.hotdeals.treecreate.controller;

import dev.hotdeals.treecreate.model.User;
import dev.hotdeals.treecreate.service.PasswordService;
import dev.hotdeals.treecreate.service.SessionService;
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
    public String submitLogin(WebRequest wr, HttpServletRequest request)
    {
        User user = userService.searchByUsername(wr.getParameter("username"));
        if (user == null || !user.getUsername().equals(wr.getParameter("username")))
        {
            LOGGER.info("[" + request.getSession().getId() + "] Failed login - Failed to find Username '" + wr.getParameter("username") + "'");
            request.getSession().setAttribute("loginError", "invalid credentials");
            return "redirect:/login";
        } else if (!PasswordService.matches(wr.getParameter("password"), user.getPassword()))
        {
            LOGGER.info("[" + request.getSession().getId() + "] Failed login - Wrong password");
            request.getSession().setAttribute("loginError", "invalid credentials");
            return "redirect:/login";
        }

        LOGGER.info("New session login [" + SessionService.getSessionName(request) + "]");
        request.getSession().setAttribute("userId", user.getId());
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
            LOGGER.info("Attempted creation of an account failed, username [" + user.getUsername() + "] already exists");
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
            LOGGER.info("Access Denied to user [" + SessionService.getSessionName(request) + "] due to null access Level");
            request.getSession().setAttribute("loginError", "invalid access level");
            return "redirect:/login";
        }

        LOGGER.info("New login as [" + SessionService.getSessionName(request) + "]");
        model.addAttribute("username", request.getSession().getAttribute("username"));
        model.addAttribute("email", request.getSession().getAttribute("email"));
        model.addAttribute("accessLevel", request.getSession().getAttribute("accessLevel"));

        return "login/successPage";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request)
    {
        LOGGER.info("[" + SessionService.getSessionName(request) + "] has logged out");
        request.getSession().invalidate();
        return "redirect:/login";
    }
}