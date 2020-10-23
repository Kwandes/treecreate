package dev.hotdeals.treecreate.model;

import java.util.Arrays;

public class FamilyTreeDesignJSON
{
    int id;

    String bannerDesign;

    String bannerText;

    String fontStyle;

    String isBigFont;

    String boxSize;

    FamilyTreeBox[] boxes;

    public FamilyTreeDesignJSON()
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

    public String getBannerDesign()
    {
        return bannerDesign;
    }

    public void setBannerDesign(String bannerDesign)
    {
        this.bannerDesign = bannerDesign;
    }

    public String getBannerText()
    {
        return bannerText;
    }

    public void setBannerText(String bannerText)
    {
        this.bannerText = bannerText;
    }

    public String getFontStyle()
    {
        return fontStyle;
    }

    public void setFontStyle(String fontStyle)
    {
        this.fontStyle = fontStyle;
    }

    public String getIsBigFont()
    {
        return isBigFont;
    }

    public void setIsBigFont(String isBigFont)
    {
        this.isBigFont = isBigFont;
    }

    public String getBoxSize()
    {
        return boxSize;
    }

    public void setBoxSize(String boxSize)
    {
        this.boxSize = boxSize;
    }

    public FamilyTreeBox[] getBoxes()
    {
        return boxes;
    }

    public void setBoxes(FamilyTreeBox[] boxes)
    {
        this.boxes = boxes;
    }

    public String toString()
    {
        return "FamilyTreeDesign{" +
                "id=" + id +
                ", bannerDesign='" + bannerDesign + '\'' +
                ", bannerText='" + bannerText + '\'' +
                ", fontStyle='" + fontStyle + '\'' +
                ", isBigFont='" + isBigFont + '\'' +
                ", boxSize='" + boxSize + '\'' +
                ", boxes=" + Arrays.toString(boxes) +
                '}';
    }

    public String stringify()
    {
        String asf = "";
        for (var box : boxes)
        {
            asf += box.stringify();
            asf += ",";
        }
        asf = asf.substring(0, asf.length()-1); // strip the extra ,
        return "{"+
                "\"id\":" + id +
                ",\"bannerDesign\":\"" + bannerDesign + "\"" +
                ",\"bannerText\":\"" + bannerText + "\"" +
                ",\"fontStyle\":" + fontStyle +
                ",\"isBigFont\":" + isBigFont +
                ",\"boxSize\":\"" + boxSize + "\"" +
                ",\"boxes\":" + "[" + asf + "]" +
                '}';
    }
}
