package com.mygubbi.game.dashboard.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nitinpuri on 01-05-2016.
 */
public class Module implements Cloneable {

    /**
     * NOTE THAT THIS CLASS IMPLEMENTS CLONEABLE AND GIVES A CLONE METHOD.
     * DO NOT INTRODUCE NEW FIELDS, SPECIALLY FIELDS OF TYPE OBJECT OR COLLECTIONS WITHOUT KNOWING HOW TO CLONE THEM
     */

    public static final String DEFAULT = "default";

    public enum ImportStatusType {m, d, n}

    public enum UnitTypes {base, wall}

    public static final String SEQ = "seq";
    public static final String UNIT_TYPE = "unitType";
    public static final String IMPORTED_MODULE_CODE = "extCode";
    public static final String IMPORTED_MODULE_DEFAULT_CODE = "extDefCode";
    public static final String IMPORTED_MODULE_TEXT = "extText";
    public static final String MG_MODULE_CODE = "mgCode";
    public static final String CARCASS_MATERIAL = "carcass";
    public static final String CARCASS_MATERIAL_CODE = "carcassCode";
    public static final String FINISH_TYPE = "finishType";
    public static final String FINISH_TYPE_CODE = "finishTypeCode";
    public static final String SHUTTER_FINISH = "finish";
    public static final String SHUTTER_FINISH_CODE = "finishCode";
    public static final String COLOR_CODE = "colorCode";
    public static final String COLOR_NAME = "colorName";
    public static final String COLOR_IMAGE_PATH = "colorImagePath";
    public static final String AMOUNT = "amount";
    public static final String IMPORT_STATUS = "importStatus";
    public static final String REMARKS = "remarks";

    private int seq;
    private String unitType;
    private String extCode;
    private String extDefCode;
    private String extText;
    private String mgCode;
    private String carcass;
    private String carcassCode;
    private String fixedCarcassCode;
    private String finishType;
    private String finishTypeCode;
    private String finish;
    private String finishCode;
    private String colorCode;
    private String colorName;
    private String colorImagePath;
    private double amount;
    private String remarks;
    private String importStatus;
    private double area;
    private double amountWOAccessories;

    private List<AccessoryPack> accessoryPacks=new ArrayList<>();

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
            //Will never happen since we are implementing Cloneable
        }
    }

    public void setCarcassCodeBasedOnUnitType(Product product) {
        this.setCarcassCode(
                this.getUnitType().toLowerCase().contains(Module.UnitTypes.wall.name())
                        ? product.getWallCarcassCode()
                        : product.getBaseCarcassCode());
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getExtCode() {
        return extCode;
    }

    public void setExtCode(String extCode) {
        this.extCode = extCode;
    }

    public String getMgCode() {
        return mgCode;
    }

    public void setMgCode(String mgCode) {
        this.mgCode = mgCode;
    }

    public String getCarcass() {
        return carcass;
    }

    public void setCarcass(String carcass) {
        this.carcass = carcass;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getImportStatus() {
        return importStatus;
    }

    public void setImportStatus(String importStatus) {
        this.importStatus = importStatus;
    }

    public String getExtDefCode() {
        return extDefCode;
    }

    public void setExtDefCode(String extDefCode) {
        this.extDefCode = extDefCode;
    }

    public String getCarcassCode() {
        return carcassCode;
    }

    public void setCarcassCode(String carcassCode) {
        this.carcassCode = carcassCode;
    }

    public String getFinishType() {
        return finishType;
    }

    public void setFinishType(String finishType) {
        this.finishType = finishType;
    }

    public String getFinishTypeCode() {
        return finishTypeCode;
    }

    public void setFinishTypeCode(String finishTypeCode) {
        this.finishTypeCode = finishTypeCode;
    }

    public String getFinish() {
        return finish;
    }

    public void setFinish(String finish) {
        this.finish = finish;
    }

    public String getFinishCode() {
        return finishCode;
    }

    public void setFinishCode(String finishCode) {
        this.finishCode = finishCode;
    }

    public String getExtText() {
        return extText;
    }

    public void setExtText(String extText) {
        this.extText = extText;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public String getColorImagePath() {
        return colorImagePath;
    }

    public void setColorImagePath(String colorImagePath) {
        this.colorImagePath = colorImagePath;
    }

    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }


    public List<AccessoryPack> getAccessoryPacks() {
        return accessoryPacks;
    }

    public void setAccessoryPacks(List<AccessoryPack> accessoryPacks) {
        this.accessoryPacks = accessoryPacks;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        Module module = (Module) o;

        if (getSeq() != module.getSeq())
        {
            return false;
        }
        return !(getUnitType() != null ? !getUnitType().equals(module.getUnitType()) : module.getUnitType() != null);

    }

    @Override
    public int hashCode()
    {
        int result = getSeq();
        result = 31 * result + (getUnitType() != null ? getUnitType().hashCode() : 0);
        return result;
    }

    public String getFixedCarcassCode() {
        return fixedCarcassCode;
    }

    public void setFixedCarcassCode(String fixedCarcassCode) {
        this.fixedCarcassCode = fixedCarcassCode;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public double getAmountWOAccessories() {
        return amountWOAccessories;
    }

    public void setAmountWOAccessories(double amountWOAccessories) {
        this.amountWOAccessories = amountWOAccessories;
    }

    @Override
    public String toString() {
        return "mgCode - " + this.getMgCode() + ", carcassCode - " + this.getCarcassCode() + ", finishCode - " + this.getFinishCode();
    }
}
