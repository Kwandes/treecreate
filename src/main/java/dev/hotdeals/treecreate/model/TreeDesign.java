package dev.hotdeals.treecreate.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "tree_design")
public class TreeDesign
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Basic
    @Column(name = "design_json", columnDefinition = "TEXT")
    private String designJson;

    @Basic
    @Column(name = "date_created")
    private String dateCreated;

    public TreeDesign()
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

    public String getDesignJson()
    {
        return designJson;
    }

    public void setDesignJson(String designJson)
    {
        this.designJson = designJson;
    }

    public String getDateCreated()
    {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated)
    {
        this.dateCreated = dateCreated;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TreeDesign design = (TreeDesign) o;
        return id == design.id &&
                Objects.equals(designJson, design.designJson) &&
                Objects.equals(dateCreated, design.dateCreated);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, designJson, dateCreated);
    }
}
