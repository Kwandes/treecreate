package dev.hotdeals.treecreate.controller;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @RequestMapping(value = "/aboutUs", method = {RequestMethod.GET, RequestMethod.POST})
    public String aboutUs()
    {
        return "home/aboutUs";
    }

    @RequestMapping(value = "/termsAndConditions", method = {RequestMethod.GET, RequestMethod.POST})
    public String termsAndConditions()
    {
        return "home/termsAndConditions";
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
}
