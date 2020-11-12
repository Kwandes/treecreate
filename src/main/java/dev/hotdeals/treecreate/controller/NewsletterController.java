package dev.hotdeals.treecreate.controller;

import dev.hotdeals.treecreate.model.NewsletterEmail;
import dev.hotdeals.treecreate.repository.NewsletterEmailRepo;
import dev.hotdeals.treecreate.service.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;

import static org.hibernate.bytecode.BytecodeLogger.LOGGER;

@Controller
public class NewsletterController
{
    @Autowired
    NewsletterEmailRepo newsletterEmailRepo;

    @GetMapping("/unsubscribe/{token}")
    String unsubscribeFromNewsletter(@PathVariable(name = "token") String token)
    {
        LOGGER.info("Received request to unsubscribe an email with a token " + token);
        var email = newsletterEmailRepo.findByToken(token).orElse(null);
        if (email == null)
        {
            LOGGER.info("The token has not returned a valid newsletter email");
            return "home/unsubscribeFail";
        }

        LOGGER.info("Removing a newsletter with an id: " + email.getId());
        newsletterEmailRepo.delete(email);
        return "home/unsubscribeSuccess";
    }

    @PostMapping("/createNewsletter")
    ResponseEntity<String> registerNewsletter(@RequestBody NewsletterEmail email)
    {
        if (email.getEmail() == null)
        {
            return new ResponseEntity<>("The request is missing an email", HttpStatus.BAD_REQUEST);
        }
        LOGGER.info("Received a request for creating a new newsletter email: " + email.getEmail());
        email.setDateCreated(LocalDate.now().toString());
        email.setToken(PasswordService.generateVerificationToken(10));
        newsletterEmailRepo.save(email);
        LOGGER.info("Created a new newsletter, id: " + email.getId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Creates a link used for unsubscribing from the newsletter
     * @param email email to delete
     * @return a complete link that will trigger the deletion
     */
    public String getUnsubscribeLink(String email)
    {
        var newsletterEmailList = newsletterEmailRepo.findAllByEmail(email);
        if (newsletterEmailList.size() == 0)
        {
            return null;
        }

        if (newsletterEmailList.size() > 1)
        {
            LOGGER.info("There are newsletter email duplicates. Deleting all except for one");
            for (int i = 1; i < newsletterEmailList.size(); i++)
            {
                LOGGER.info("Deleting newsletter email ID: " + newsletterEmailList.get(i).getId());
                newsletterEmailRepo.delete(newsletterEmailList.get(i));
            }
        }
        return "http://testing.treecreate.dk/unsubscribe/" + newsletterEmailList.get(0).getToken();
    }
}
