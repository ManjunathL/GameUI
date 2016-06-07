package com.mygubbi.game.dashboard.domain;

/**
 * Created by nitinpuri on 02-06-2016.
 */
public class  AddonProductItem {
    public static final String CATALOGUE_CODE = "catalogueCode";
    public static final String TITLE = "title";
    public static final String RATE = "rate";
    private String catalogueCode;
    private String title;
    private String code;
    private double rate;
    private String imagePath;
    private String uom;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCatalogueCode() {
        return catalogueCode;
    }

    public void setCatalogueCode(String catalogueCode) {
        this.catalogueCode = catalogueCode;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }
}
