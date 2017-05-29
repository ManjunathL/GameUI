package com.mygubbi.game.dashboard.domain;

/**
 * Created by user on 29-May-17.
 */
public class ModuleHingeMap
{
    public static final String MODULE_CODE = "modulecode";
    public static final String HINGE_CODE = "hingecode";
    public static final String QTY = "qty";
    public static final String TYPE = "type";
    public static final String QYT_FORMULA = "qtyFormula";
    public static final String QTY_FLAG = "qtyFlag";

    private String modulecode;
    private String hingecode;
    private int qty;
    private String type;
    private String qtyFormula;
    private String qtyFlag;

    public String getHingecode() {
        return hingecode;
    }

    public void setHingecode(String hingecode) {
        this.hingecode = hingecode;
    }

    public String getModulecode() {
        return modulecode;
    }

    public void setModulecode(String modulecode) {
        this.modulecode = modulecode;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getQtyFlag() {
        return qtyFlag;
    }

    public void setQtyFlag(String qtyFlag) {
        this.qtyFlag = qtyFlag;
    }

    public String getQtyFormula() {
        return qtyFormula;
    }

    public void setQtyFormula(String qtyFormula) {
        this.qtyFormula = qtyFormula;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ModuleHingeMap{" +
                "hingecode='" + hingecode + '\'' +
                ", modulecode='" + modulecode + '\'' +
                ", qty=" + qty +
                ", type='" + type + '\'' +
                ", qtyFormula='" + qtyFormula + '\'' +
                ", qtyFlag='" + qtyFlag + '\'' +
                '}';
    }
}
