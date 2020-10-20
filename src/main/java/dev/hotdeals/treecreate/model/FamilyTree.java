package dev.hotdeals.treecreate.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "family_tree")
public class FamilyTree
{
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    int id;

    @Column(name = "time_plus_date")
    private String timePLusDate;

    @Column(name = "owner_id")
    private String ownerId;

    @Column(name = "design")
    private String design;

    public FamilyTree()
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

    public String getTimePLusDate()
    {
        return timePLusDate;
    }

    public void setTimePLusDate(String timePLusDate)
    {
        this.timePLusDate = timePLusDate;
    }

    public String getOwnerId()
    {
        return ownerId;
    }

    public void setOwnerId(String ownerId)
    {
        this.ownerId = ownerId;
    }

    public String getDesign()
    {
        return design;
    }

    public void setDesign(String design)
    {
        this.design = design;
    }

    public void setDesign(FamilyTreeDesignJSON design)
    {
        this.design = design.toString();
    }

    public String toString()
    {
        return "FamilyTreeDesign{" +
                "id=" + id +
                ", timePLusDate='" + timePLusDate + '\'' +
                ", ownerId=" + ownerId +
                '}';
    }
}
