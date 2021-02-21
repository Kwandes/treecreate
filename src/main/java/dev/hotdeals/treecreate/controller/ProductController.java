package dev.hotdeals.treecreate.controller;

import dev.hotdeals.treecreate.model.SpecialEmail;
import dev.hotdeals.treecreate.repository.SpecialEmailRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import static org.hibernate.bytecode.BytecodeLogger.LOGGER;

@Controller
public class ProductController
{
    @Autowired
    SpecialEmailRepo specialEmailRepo;


    @PostMapping("/specialRequest")
    ResponseEntity<String> updatePassword(@RequestBody String email)
    {
        LOGGER.info("Special request - Received data: " + email);
        SpecialEmail specialEmail = new SpecialEmail();
        specialEmail.setEmail(email);
        specialEmail.setTimePlusDate(LocalDateTime.now().toString());
        SpecialEmail savedSpecialEmail = specialEmailRepo.save(specialEmail);
        LOGGER.info("Special request - Saved email : " + savedSpecialEmail.toString());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
