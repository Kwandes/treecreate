package dev.hotdeals.treecreate.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "transaction")
public class Transaction
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "user", referencedColumnName = "id", nullable = false)
    private User user;

    //ToDo - Make it into an actual one-to-many relationship
    @Basic
    @Column(name = "orders")
    private String orders;

    @Basic
    @Column(name = "price", length = 16)
    private int price;

    @Basic
    @Column(name = "currency", length = 3)
    private String currency;

    @Basic
    @Column(name = "status", length = 25)
    private String status;

    @Basic
    @Column(name = "created_on", length = 50)
    private String createdOn;

    @Basic
    @Column(name = "expected_delivery_date", length = 25)
    private String expectedDeliveryDate;

    @Basic
    @Column(name = "name", length = 80)
    private String name;

    @Basic
    @Column(name = "phoneNumber", length = 15)
    private String phoneNumber;

    @Basic
    @Column(name = "email", length = 255)
    private String email;

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
    @Column(name = "country", length = 30)
    private String country;

    @Basic
    @Column(name = "discount", length = 25)
    private String discount;

    @OneToMany
    @JoinColumn(name = "transaction_id")
    private List<TreeOrder> orderList = new ArrayList<>();

    public Transaction() {}

    public Transaction(User user, String orders, int price, String currency, String status, String createdOn,
                       String expectedDeliveryDate, String name, String phoneNumber, String email, String streetAddress,
                       String city, String postcode, String country, String discount, List<TreeOrder> orderList)
    {
        this.user = user;
        this.orders = orders;
        this.price = price;
        this.currency = currency;
        this.status = status;
        this.createdOn = createdOn;
        this.expectedDeliveryDate = expectedDeliveryDate;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.streetAddress = streetAddress;
        this.city = city;
        this.postcode = postcode;
        this.country = country;
        this.discount = discount;
        this.orderList = orderList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getOrders() {
        return orders;
    }

    public void setOrders(String orders) {
        this.orders = orders;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }

    public void setExpectedDeliveryDate(String expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public List<TreeOrder> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<TreeOrder> orderList) {
        this.orderList = orderList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return id == that.id &&
                price == that.price &&
                Objects.equals(user, that.user) &&
                Objects.equals(orders, that.orders) &&
                Objects.equals(currency, that.currency) &&
                Objects.equals(status, that.status) &&
                Objects.equals(createdOn, that.createdOn) &&
                Objects.equals(expectedDeliveryDate, that.expectedDeliveryDate) &&
                Objects.equals(name, that.name) &&
                Objects.equals(phoneNumber, that.phoneNumber) &&
                Objects.equals(email, that.email) &&
                Objects.equals(streetAddress, that.streetAddress) &&
                Objects.equals(city, that.city) &&
                Objects.equals(postcode, that.postcode) &&
                Objects.equals(country, that.country) &&
                Objects.equals(discount, that.discount) &&
                Objects.equals(orderList, that.orderList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, orders, price, currency, status, createdOn, expectedDeliveryDate, name, phoneNumber, email, streetAddress, city, postcode, country, discount, orderList);
    }

    public String toString()
    {
        return "Transaction{" +
                "id=" + id +
                ", user=" + user +
                ", orders='" + orders + '\'' +
                ", price=" + price +
                ", currency='" + currency + '\'' +
                ", status='" + status + '\'' +
                ", createdOn='" + createdOn + '\'' +
                ", expectedDeliveryDate='" + expectedDeliveryDate + '\'' +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", streetAddress='" + streetAddress + '\'' +
                ", city='" + city + '\'' +
                ", postcode='" + postcode + '\'' +
                ", country='" + country + '\'' +
                ", discount='" + discount + '\'' +
                '}';
    }
}
