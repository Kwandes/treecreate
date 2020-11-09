package dev.hotdeals.treecreate.controller;

import dev.hotdeals.treecreate.service.MailService;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.mail.MessagingException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.hibernate.bytecode.BytecodeLogger.LOGGER;

@Controller
public class IndexController
{
    // First mapping to be called when accessing the base url. It determines where to go next
    @RequestMapping(value = {"", "/", "/index", "/index/"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String index()
    {
        return "redirect:/landingPage";
    }

    @RequestMapping(value = "/productExample", method = {RequestMethod.GET, RequestMethod.POST})
    public String productExample()
    {
        return "home/productExample";
    }

    @RequestMapping(value = "/orderTermsAndConditionsDanish", method = {RequestMethod.GET, RequestMethod.POST})
    public String termsAndConditions()
    {
        return "home/orderTermsAndConditionsDanish";
    }

    @RequestMapping(value = "/cookieTermsAndConditionsDanish", method = {RequestMethod.GET, RequestMethod.POST})
    public String cookieTermsAndConditions()
    {
        return "home/cookieTermsAndConditionsDanish";
    }

    @ResponseBody
    @RequestMapping(value = "/version", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<String> getVersion()
    {
        try
        {
            Model model;
            InputStream stream;
            if ((new File("pom.xml")).exists())
                stream = new FileInputStream("pom.xml");
            else
                stream = IndexController.class.getResourceAsStream("/META-INF/maven/dev.hotdeals/treecreate/pom.xml");
            model = new MavenXpp3Reader().read(stream);
            stream.close();
            return new ResponseEntity<>(model.getVersion(), HttpStatus.OK);
        } catch (XmlPullParserException | IOException e)
        {
            LOGGER.error("Failed to retrieve the pom.xml", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Autowired
    MailService mailService;

    @GetMapping("/sendMail")
    ResponseEntity<String> sendMail()
    {
        LOGGER.info("Sending an email");
        try
        {
            mailService.sendInfoMail("Example Info Message", "Example Info Subject", "orders@treecreate.dk");
        } catch (MailException e)
        {
            LOGGER.error("Unable to send an email", e);
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
        LOGGER.info("Email sending finished question mark?");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/sendOrderMail")
    ResponseEntity<String> sendOrderMail()
    {
        LOGGER.info("Sending an email");
        try
        {
            mailService.sendOrderMail("Example Order Message", "Example Order Subject", "info@treecreate.dk");
        } catch (MailException | MessagingException e)
        {
            LOGGER.error("Unable to send an email", e);
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
        LOGGER.info("Email sending finished");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = {"mobile", "/mobile", "/mobile.html"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String mobile()
    {
        return "products/mobile";
    }
}
