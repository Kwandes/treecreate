package dev.hotdeals.treecreate.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "discount_code")
public class DiscountCode
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Basic
    @Column(name = "discount_code", length = 25)
    private String discountCode;

    @Basic
    @Column(name = "date_created", length = 25)
    private String dateCreated;

    @Basic
    @Column(name = "active", columnDefinition = "boolean default false")
    private Boolean active;

    @Basic
    @Column(name = "discount_type", length = 25)
    private String discountType;

    @Basic
    @Column(name = "discount_amount", length = 25)
    private String discountAmount;

    @Basic
    @Column(name = "times_used", columnDefinition = "integer default 0")
    private int timesUsed;

    @Basic
    @Column(name = "max_usages", columnDefinition = "integer default 1")
    private int maxUsages;

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiscountCode that = (DiscountCode) o;
        return id == that.id &&
                timesUsed == that.timesUsed &&
                maxUsages == that.maxUsages &&
                Objects.equals(discountCode, that.discountCode) &&
                Objects.equals(dateCreated, that.dateCreated) &&
                Objects.equals(active, that.active) &&
                Objects.equals(discountType, that.discountType) &&
                Objects.equals(discountAmount, that.discountAmount);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, discountCode, dateCreated, active, discountType, discountAmount, timesUsed, maxUsages);
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getDiscountCode()
    {
        return discountCode;
    }

    public void setDiscountCode(String discountCode)
    {
        this.discountCode = discountCode;
    }

    public String getDateCreated()
    {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated)
    {
        this.dateCreated = dateCreated;
    }

    public Boolean getActive()
    {
        return active;
    }

    public void setActive(Boolean active)
    {
        this.active = active;
    }

    public String getDiscountType()
    {
        return discountType;
    }

    public void setDiscountType(String discountType)
    {
        this.discountType = discountType;
    }

    public String getDiscountAmount()
    {
        return discountAmount;
    }

    public void setDiscountAmount(String discountAmount)
    {
        this.discountAmount = discountAmount;
    }

    public int getTimesUsed()
    {
        return timesUsed;
    }

    public void setTimesUsed(int timesUsed)
    {
        this.timesUsed = timesUsed;
    }

    public int getMaxUsages()
    {
        return maxUsages;
    }

    public void setMaxUsages(int maxUsages)
    {
        this.maxUsages = maxUsages;
    }
}
