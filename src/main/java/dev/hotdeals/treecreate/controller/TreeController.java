package dev.hotdeals.treecreate.controller;

import dev.hotdeals.treecreate.model.FamilyTreeDesignJSON;
import dev.hotdeals.treecreate.model.TreeDesign;
import dev.hotdeals.treecreate.model.TreeOrder;
import dev.hotdeals.treecreate.model.User;
import dev.hotdeals.treecreate.repository.TreeDesignRepo;
import dev.hotdeals.treecreate.repository.TreeOrderRepo;
import dev.hotdeals.treecreate.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hibernate.bytecode.BytecodeLogger.LOGGER;

@RestController
public class TreeController
{
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    @Autowired
    UserRepo userRepo;

    @Autowired
    TreeOrderRepo treeOrderRepo;

    @Autowired
    TreeDesignRepo treeDesignRepo;

    @GetMapping("/getUsers")
    ResponseEntity<List<User>> getUsers()
    {
        LOGGER.info("Getting all users");
        return new ResponseEntity<>(userRepo.findAll(), HttpStatus.OK);
    }

    @GetMapping("/getCurrentUser")
    ResponseEntity<String> getCurrentUser(HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        if (session.getAttribute("userId") != null)
        {
            String userId = session.getAttribute("userId").toString();
            //LOGGER.info("Current User ID: " + userId);
            return new ResponseEntity<>(userId, HttpStatus.OK);
        }

        User user = userRepo.findOneByEmail(request.getSession().getId());
        if (user == null)
        {
            LOGGER.info(request.getSession().getId() + " - Get current User - User not found, creating a new one");
            user = new User();
            user.setName("Temp session user");
            user.setEmail(request.getSession().getId());
            user.setVerification("temp");
            LOGGER.info(request.getSession().getId() + " - Get current User - New user: " + user.toString());
            user = userRepo.save(user);
            LOGGER.info(request.getSession().getId() + " - Get current User - Saved user: " + user.toString());
        } else
        {
            //LOGGER.info("User found: " + user.toString());
        }
        return new ResponseEntity<>(String.valueOf(user.getId()), HttpStatus.OK);
    }
}
