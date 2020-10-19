package dev.hotdeals.treecreate.model;

public class FamilyTreeBox
{
    int boxId;

    String text;

    String positionY;

    String positionX;

    String boxDesign;

    public FamilyTreeBox()
    {
    }

    public int getBoxId()
    {
        return boxId;
    }

    public void setBoxId(int boxId)
    {
        this.boxId = boxId;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public String getPositionY()
    {
        return positionY;
    }

    public void setPositionY(String positionY)
    {
        this.positionY = positionY;
    }

    public String getPositionX()
    {
        return positionX;
    }

    public void setPositionX(String positionX)
    {
        this.positionX = positionX;
    }

    public String getBoxDesign()
    {
        return boxDesign;
    }

    public void setBoxDesign(String boxDesign)
    {
        this.boxDesign = boxDesign;
    }

    public String toString()
    {
        return "FamilyTreeBox{" +
                "boxId=" + boxId +
                ", text='" + text + '\'' +
                ", positionY='" + positionY + '\'' +
                ", positionX='" + positionX + '\'' +
                ", boxDesign='" + boxDesign + '\'' +
                '}';
    }
}
