package dev.hotdeals.treecreate.repository;

import dev.hotdeals.treecreate.model.NewsletterEmail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface NewsletterEmailRepo extends JpaRepository<NewsletterEmail, Integer>
{
    List<NewsletterEmail> findAllByEmail(String email);
    Optional<NewsletterEmail> findByToken(String token);
}
