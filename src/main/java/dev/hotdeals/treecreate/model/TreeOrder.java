package dev.hotdeals.treecreate.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "tree_order")
public class TreeOrder
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private int orderId;

    @Basic
    @Column(name = "amount")
    private int amount;

    @Basic
    @Column(name = "size")
    private String size;

    @Basic
    @Column(name = "status")
    private String status;


    @ManyToOne
    @JoinColumn(name = "tree_design", referencedColumnName = "id", nullable = false)
    private TreeDesign treeDesign;

    @ManyToOne
    @JoinColumn(name = "user", referencedColumnName = "id", nullable = false)
    private User user;

    public TreeOrder()
    {
    }

    public int getOrderId()
    {
        return orderId;
    }

    public void setOrderId(int orderId)
    {
        this.orderId = orderId;
    }

    public int getAmount()
    {
        return amount;
    }

    public void setAmount(int amount)
    {
        this.amount = amount;
    }

    public String getSize()
    {
        return size;
    }

    public void setSize(String size)
    {
        this.size = size;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public TreeDesign getTreeDesignById()
    {
        return treeDesign;
    }

    public void setTreeDesignById(TreeDesign treeDesign)
    {
        this.treeDesign = treeDesign;
    }

    public User getUserByUserId()
    {
        return user;
    }

    public void setUserByUserId(User user)
    {
        this.user = user;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TreeOrder order = (TreeOrder) o;
        return orderId == order.orderId &&
                amount == order.amount &&
                Objects.equals(size, order.size) &&
                Objects.equals(status, order.status) &&
                Objects.equals(treeDesign, order.treeDesign) &&
                Objects.equals(user, order.user);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(orderId, amount, size, status, treeDesign, user);
    }

    public String stringify()
    {
        return "{"+
                "\"amount\":" + amount +
                ",\"size\":\"" + size + "\"" +
                ",\"status\":\"" + status + "\"" +
                '}';
    }

    @Override
    public String toString()
    {
        return "TreeOrder{" +
                "orderId=" + orderId +
                ", amount=" + amount +
                ", size='" + size + '\'' +
                ", status='" + status + '\'' +
                ", user=" + user.getId() +
                ", design=" + treeDesign.getId() +
                '}';
    }
}
