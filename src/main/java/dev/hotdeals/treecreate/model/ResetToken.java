package dev.hotdeals.treecreate.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "reset_token")
public class ResetToken
{
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    int id;

    @Column(name = "date_created", length = 50)
    private String dateCreated;

    @Column(name = "token", length = 8)
    private String token;

    @Column(name = "email")
    private String email;

    @Column(name = "is_active")
    private Boolean isActive;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getDateCreated()
    {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated)
    {
        this.dateCreated = dateCreated;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public Boolean getIsActive()
    {
        return isActive;
    }

    public void setIsActive(Boolean status)
    {
        this.isActive = status;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResetToken that = (ResetToken) o;
        return id == that.id &&
                Objects.equals(dateCreated, that.dateCreated) &&
                Objects.equals(token, that.token) &&
                Objects.equals(email, that.email) &&
                Objects.equals(isActive, that.isActive);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, dateCreated, token, email, isActive);
    }
}
