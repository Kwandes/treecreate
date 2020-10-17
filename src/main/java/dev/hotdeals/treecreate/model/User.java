package dev.hotdeals.treecreate.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "user")
public class User
{
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private int id;

    @NotNull
    @Column(name = "name", length = 80)
    private String name;

    @NotNull
    @Column(name = "email", length = 254)
    private String email;

    @NotNull
    @Column(name = "password", length = 60)
    private String password;

    @NotNull
    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @NotNull
    @Column(name = "street_address", length = 99)
    private String streetAddress;

    @NotNull
    @Column(name = "city", length = 50)
    private String city;

    @NotNull
    @Column(name = "postcode", length = 15)
    private String postcode;

    @NotNull
    @Column(name = "access_level")
    private int accessLevel;

    public void setId(Integer id)
    {
        this.id = id;
    }

    public User()
    {
    }

    public User(@NotNull int id, @NotNull String name, @NotNull String email, @NotNull String password,
                @NotNull String phoneNumber, @NotNull String streetAddress, @NotNull String city,
                @NotNull String postcode, @NotNull int accessLevel)
    {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.streetAddress = streetAddress;
        this.city = city;
        this.postcode = postcode;
        this.accessLevel = accessLevel;
    }

    public String toString()
    {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", streetAddress='" + streetAddress + '\'' +
                ", city='" + city + '\'' +
                ", postcode='" + postcode + '\'' +
                ", accessLevel=" + accessLevel +
                '}';
    }

    @Id
    public Integer getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
    }

    public String getStreetAddress()
    {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress)
    {
        this.streetAddress = streetAddress;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getPostcode()
    {
        return postcode;
    }

    public void setPostcode(String postcode)
    {
        this.postcode = postcode;
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
