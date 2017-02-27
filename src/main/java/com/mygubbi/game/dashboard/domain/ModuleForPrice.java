package com.mygubbi.game.dashboard.domain;


import java.sql.Date;

/**
 * Created by nitinpuri on 01-05-2016.
 */
public class ModuleForPrice implements Cloneable {

    /**
     * NOTE THAT THIS CLASS IMPLEMENTS CLONEABLE AND GIVES A CLONE METHOD.
     * DO NOT INTRODUCE NEW FIELDS, SPECIALLY FIELDS OF TYPE OBJECT OR COLLECTIONS WITHOUT KNOWING HOW TO CLONE THEM
     */

    public static final String DEFAULT = "default";

    public static final String DATE = "priceDate";
    public static final String CITY = "city";


    private Date priceDate;
    private String city;
    private Module module = new Module();

    public Date getPriceDate() {
        return priceDate;
    }

    public void setPriceDate(Date priceDate) {
        this.priceDate = priceDate;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }
}