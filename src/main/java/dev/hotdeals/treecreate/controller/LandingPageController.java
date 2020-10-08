package dev.hotdeals.treecreate.controller;

import dev.hotdeals.treecreate.repository.LandingPageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;

@Controller
public class LandingPageController
{
    private static final Logger LOGGER = LogManager.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    LandingPageRepo landingPageRepo;

    @RequestMapping(value = "/landingPage", method = {RequestMethod.GET, RequestMethod.POST})
    public String landingPage()
    {
        return "home/landingPage";
    }
}
