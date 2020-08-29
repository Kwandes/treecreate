package dev.hotdeals.treecreate.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity // marks the class as a representative of the database table
public class User
{
    @Id
    private int id;
    private String username;
    private String password;
    private String email;
    private int accessLevel;

    // empty constructor as per JPA Entity requirements
    public User()
    {
    }

    public String toString()
    {
        return "Id: " + id + " | User:" + username + " | Email: " + email + " | AccessLevel: " + accessLevel;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public int getAccessLevel()
    {
        return accessLevel;
    }

    public void setAccessLevel(int accessLevel)
    {
        this.accessLevel = accessLevel;
    }
}