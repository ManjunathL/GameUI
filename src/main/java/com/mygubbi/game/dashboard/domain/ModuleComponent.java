package com.mygubbi.game.dashboard.domain;

/**
 * Created by shruthi on 14-Mar-17.
 */
public class ModuleComponent
{
    public static final String MODULECODE="modulecode";
    public static final String COMPTYPE="comptype";
    public static final String COMOCODE="compcode";
    public static final String QUANTITY="quantity";

    private String modulecode;
    private String comptype;
    private String compcode;
    private double quantity;

    public String getCompcode() {
        return compcode;
    }

    public void setCompcode(String compcode) {
        this.compcode = compcode;
    }

    public String getComptype() {
        return comptype;
    }

    public void setComptype(String comptype) {
        this.comptype = comptype;
    }

    public String getModulecode() {
        return modulecode;
    }

    public void setModulecode(String modulecode) {
        this.modulecode = modulecode;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "ModuleComponent{" +
                "compcode='" + compcode + '\'' +
                ", modulecode='" + modulecode + '\'' +
                ", comptype='" + comptype + '\'' +
                ", quantity='" + quantity + '\'' +
                '}';
    }
}
