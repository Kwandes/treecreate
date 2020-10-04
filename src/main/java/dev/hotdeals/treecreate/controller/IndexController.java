package dev.hotdeals.treecreate.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class IndexController
{
    // First mapping to be called when accessing the base url. It determines where to go next
    @RequestMapping(value = {"", "/", "/index", "/index/"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String index()
    {
        return "redirect:/landingPage";
    }

    @RequestMapping(value = "/landingPage", method = {RequestMethod.GET, RequestMethod.POST})
    public String landingPage()
    {
        return "home/landingPage";
    }

    @RequestMapping(value = "/aboutUs", method = {RequestMethod.GET, RequestMethod.POST})
    public String aboutUs()
    {
        return "home/aboutUs";
    }
}
