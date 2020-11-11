package dev.hotdeals.treecreate.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "newsletterEmail")
public class NewsletterEmail
{
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    int id;

    @Column(name = "timePlusDate", length = 50)
    private String timePlusDate;

    @Column(name = "email", length = 255)
    private String email;

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

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewsletterEmail that = (NewsletterEmail) o;
        return id == that.id &&
                Objects.equals(timePlusDate, that.timePlusDate) &&
                Objects.equals(email, that.email);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, timePlusDate, email);
    }
}
