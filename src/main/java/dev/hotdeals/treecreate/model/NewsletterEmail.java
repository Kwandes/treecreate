package dev.hotdeals.treecreate.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "newsletter_email")
public class NewsletterEmail
{
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    int id;

    @Column(name = "time_plus_date", length = 50)
    private String timePlusDate;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "newsletter_token", length = 10)
    private String newsletterToken;

    public NewsletterEmail(@NotNull int id, String timePlusDate, String email, String newsletterToken)
    {
        this.id = id;
        this.timePlusDate = timePlusDate;
        this.email = email;
        this.newsletterToken = newsletterToken;
    }

    public NewsletterEmail()
    {
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getTimePlusDate()
    {
        return timePlusDate;
    }

    public void setTimePlusDate(String timePlusDate)
    {
        this.timePlusDate = timePlusDate;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getNewsletterToken()
    {
        return newsletterToken;
    }

    public void setNewsletterToken(String newsletterToken)
    {
        this.newsletterToken = newsletterToken;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewsletterEmail that = (NewsletterEmail) o;
        return id == that.id &&
                Objects.equals(timePlusDate, that.timePlusDate) &&
                Objects.equals(email, that.email) &&
                Objects.equals(newsletterToken, that.newsletterToken);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, timePlusDate, email, newsletterToken);
    }
}
