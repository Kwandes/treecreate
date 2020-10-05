package dev.hotdeals.treecreate.controller;

import dev.hotdeals.treecreate.repository.LandingPageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

import static org.hibernate.bytecode.BytecodeLogger.LOGGER;

@RestController
public class RestLandingPageController
{
    @Autowired
    LandingPageRepo landingPageRepo;

    @PostMapping(value = "/submitNewsletterEmail")
    public void submitNewsletterEmail(@RequestBody String email)
    {
        System.out.println("Email: " + email);

        if (!landingPageRepo.addNewsletterEmail(LocalDateTime.now().toString(), email))
        {
            LOGGER.warn("Failed to add a newsletter email: " + email);
        }
    }
}
