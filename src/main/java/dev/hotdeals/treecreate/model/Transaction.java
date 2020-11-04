package dev.hotdeals.treecreate.model;

import javax.persistence.*;
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

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public int getPrice()
    {
        return price;
    }

    public void setPrice(int price)
    {
        this.price = price;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public String getOrders()
    {
        return orders;
    }

    public void setOrders(String orders)
    {
        this.orders = orders;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return id == that.id &&
                price == that.price &&
                Objects.equals(user, that.user) &&
                Objects.equals(orders, that.orders) &&
                Objects.equals(currency, that.currency);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, user, orders, price, currency);
    }
}
