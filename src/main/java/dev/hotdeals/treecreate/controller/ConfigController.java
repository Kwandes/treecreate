package dev.hotdeals.treecreate.controller;

import dev.hotdeals.treecreate.config.CustomProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigController
{
    @Autowired
    CustomProperties customProperties;

    @GetMapping("/environmentType")
    ResponseEntity<String> getEnvironmentType()
    {
        return new ResponseEntity<>(customProperties.getEnvironmentType(), HttpStatus.OK);
    }
}
