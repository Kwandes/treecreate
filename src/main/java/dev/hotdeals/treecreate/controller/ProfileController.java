package dev.hotdeals.treecreate.controller;

import dev.hotdeals.treecreate.model.User;
import dev.hotdeals.treecreate.repository.NewsletterEmailRepo;
import dev.hotdeals.treecreate.repository.TreeOrderRepo;
import dev.hotdeals.treecreate.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static org.hibernate.bytecode.BytecodeLogger.LOGGER;

@Controller
public class ProfileController
{
    @Autowired
    UserRepo userRepo;

    @Autowired
    TreeController treeController;

    @Autowired
    NewsletterEmailRepo newsletterEmailRepo;

    @Autowired
    TreeOrderRepo treeOrderRepo;

    @ResponseBody
    @RequestMapping(value = {"/endSession"}, method = {RequestMethod.GET, RequestMethod.POST})
    ResponseEntity<Boolean> endSession(HttpServletRequest httpServletRequest)
    {
        LOGGER.info("Ending session " + httpServletRequest.getSession().getId());
        httpServletRequest.getSession().invalidate();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/cookiesValidation")
    ResponseEntity<Boolean> hasAcceptedCookies(HttpServletRequest request)
    {
        String userID = treeController.getCurrentUser(request).getBody();
        User currentUser = userRepo.findById(Integer.parseInt(userID)).orElse(null);
        if (currentUser != null)
        {
            return new ResponseEntity<>(currentUser.getAcceptedCookies(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/acceptCookies")
    ResponseEntity<String> acceptCookies(HttpServletRequest request)
    {
        LOGGER.info("Cookies accept request has been received for session " + request.getSession().getId());
        String userID = treeController.getCurrentUser(request).getBody();
        User currentUser = userRepo.findById(Integer.parseInt(userID)).orElse(null);
        if (currentUser != null)
        {
            currentUser.setAcceptedCookies(true);
            userRepo.save(currentUser);
            LOGGER.info("Cookies have been accepted by user " + currentUser.getId());
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
