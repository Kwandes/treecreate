package dev.hotdeals.treecreate.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "user")
public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Basic
    @Column(name = "name", length = 80)
    private String name;

    @Basic
    @Column(name = "email", length = 254)
    private String email;

    @Basic
    @Column(name = "password", length = 60)
    private String password;

    @Basic
    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Basic
    @Column(name = "street_address", length = 99)
    private String streetAddress;

    @Basic
    @Column(name = "city", length = 50)
    private String city;

    @Basic
    @Column(name = "postcode", length = 15)
    private String postcode;

    @Basic
    @Column(name = "access_level")
    private int accessLevel;

    @Basic
    @Column(name = "verification", length = 8)
    private String verification;

    @Basic
    @Column(name = "accepted_cookies", columnDefinition = "boolean default false")
    private boolean acceptedCookies;

    public User()
    {
    }

    public User(@NotNull int id, @NotNull String name, @NotNull String email, @NotNull String password,
                @NotNull String phoneNumber, @NotNull String streetAddress, @NotNull String city,
                @NotNull String postcode, @NotNull int accessLevel, @NotNull boolean acceptedCookies)
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
        this.acceptedCookies = acceptedCookies;
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
                ", accessLevel=" + accessLevel + '\'' +
                ", acceptedCookies=" + acceptedCookies +
                '}';
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
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

    public String getVerification()
    {
        return verification;
    }

    public void setVerification(String verification)
    {
        this.verification = verification;
    }

    public boolean getAcceptedCookies() {
        return acceptedCookies;
    }

    public void setAcceptedCookies(boolean acceptedCookies) {
        this.acceptedCookies = acceptedCookies;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id &&
                accessLevel == user.accessLevel &&
                Objects.equals(name, user.name) &&
                Objects.equals(email, user.email) &&
                Objects.equals(password, user.password) &&
                Objects.equals(phoneNumber, user.phoneNumber) &&
                Objects.equals(streetAddress, user.streetAddress) &&
                Objects.equals(city, user.city) &&
                Objects.equals(postcode, user.postcode) &&
                Objects.equals(verification, user.verification) &&
                Objects.equals(acceptedCookies, user.acceptedCookies);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, name, email, password, phoneNumber, streetAddress, city, postcode, accessLevel, acceptedCookies);
    }
}
