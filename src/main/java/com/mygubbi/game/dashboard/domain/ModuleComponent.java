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
    public static final String QUANTITY_FLAG="quantityFlag";
    public static final String QUANTITY_FORMULA="quantityFormula";

    private String modulecode;
    private String comptype;
    private String compcode;
    private double quantity;
    private String quantityFlag;
    private String quantityFormula;


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

    public String getQuantityFlag() {
        return quantityFlag;
    }

    public void setQuantityFlag(String quantityFlag) {
        this.quantityFlag = quantityFlag;
    }

    public String getQuantityFormula() {
        return quantityFormula;
    }

    public void setQuantityFormula(String quantityFormula) {
        this.quantityFormula = quantityFormula;
    }

    @Override
    public String toString() {
        return "ModuleComponent{" +
                "modulecode='" + modulecode + '\'' +
                ", comptype='" + comptype + '\'' +
                ", compcode='" + compcode + '\'' +
                ", quantity=" + quantity +
                ", quantityFlag='" + quantityFlag + '\'' +
                ", quantityFormula='" + quantityFormula + '\'' +
                '}';
    }
}
