package com.mygubbi.game.dashboard.domain.JsonPojo;

/**
 * Created by shruthi on 27-Feb-17.
 */
public class AccesoryHardwareMaster
{
    public static final String ID="id";
    public static final String TYPE="type";
    public static final String CODE="code";
    public static final String TITLE="title";
    public static final String MAKE="make";
    public static final String CATEGORY="category";
    public static final String IMAGEPATH="imagepath";
    public static final String UOM="uom";
    public static final String CP="cp";
    public static final String MRP="mrp";
    public static final String MSP="msp";

    private String id;
    private String type;
    private String code;
    private String title;
    private String make;
    private String category;
    private String imagePath;
    private String uom;
    private Double cp;
    private Double mrp;
    private Double msp;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Double getCp() {
        return cp;
    }

    public void setCp(Double cp) {
        this.cp = cp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public Double getMrp() {
        return mrp;
    }

    public void setMrp(Double mrp) {
        this.mrp = mrp;
    }

    public Double getMsp() {
        return msp;
    }

    public void setMsp(Double msp) {
        this.msp = msp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    @Override
    public String toString() {
        return "AccesoryHardwareMaster{" +
                "category='" + category + '\'' +
                ", id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", code='" + code + '\'' +
                ", title='" + title + '\'' +
                ", make='" + make + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", uom='" + uom + '\'' +
                ", cp=" + cp +
                ", mrp=" + mrp +
                ", msp=" + msp +
                '}';
    }
}
