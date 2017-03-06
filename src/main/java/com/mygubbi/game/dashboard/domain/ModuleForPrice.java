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



    private Date priceDate;
    private String city;
    private boolean priceToBeChanged;
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

    public boolean isPriceToBeChanged() {
        return priceToBeChanged;
    }

    public void setPriceToBeChanged(boolean priceToBeChanged) {
        this.priceToBeChanged = priceToBeChanged;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }
}