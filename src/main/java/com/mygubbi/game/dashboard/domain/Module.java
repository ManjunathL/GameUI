package com.mygubbi.game.dashboard.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by nitinpuri on 01-05-2016.
 */
public class Module {

    public static final String DEFAULT = "default";

    public void setGenerated() {
        if (importStatus.equals(ImportStatusType.d.name())) {
            extText = extCode + " / " + extDefCode;
        } else {
            extText = extCode;
        }
    }

    public void setCarcassCodeBasedOnUnitType(Product product) {
        this.setCarcassCode(
                this.getUnitType().equals(Module.UnitTypes.Wall.name())
                        ? product.getWallCarcassCode()
                        : product.getBaseCarcassCode());
    }

    public enum ImportStatusType {m, d, n}
    public enum UnitTypes {Base, Wall}

    public static final String SEQ = "seq";
    public static final String UNIT_TYPE = "unitType";
    public static final String IMPORTED_MODULE_CODE = "extCode";
    public static final String IMPORTED_MODULE_DEFAULT_CODE = "extDefCode";
    public static final String IMPORTED_MODULE_TEXT = "extText";
    public static final String MG_MODULE_CODE = "mgCode";
    public static final String MAKE_TYPE = "makeType";
    public static final String MAKE_TYPE_CODE = "makeTypeCode";
    public static final String MAKE_TYPE_TEXT = "makeTypeText";
    public static final String CARCASS_MATERIAL = "carcass";
    public static final String CARCASS_MATERIAL_CODE = "carcassCode";
    public static final String CARCASS_MATERIAL_TEXT = "carcassText";
    public static final String FINISH_TYPE = "finishType";
    public static final String FINISH_TYPE_CODE = "finishTypeCode";
    public static final String FINISH_TYPE_TEXT = "finishTypeText";
    public static final String SHUTTER_FINISH = "finish";
    public static final String SHUTTER_FINISH_CODE = "finishCode";
    public static final String SHUTTER_FINISH_TEXT = "finishText";
    public static final String COLOR_CODE = "colorCode";
    public static final String COLOR_NAME = "colorName";
    public static final String COLOR_IMAGE_PATH = "colorImagePath";
    public static final String AMOUNT = "amount";
    public static final String IMPORT_STATUS = "importStatus";
    public static final String MG_MODULES = "mgModules";
    public static final String MODULE_PRICE = "modulePrice";
    public static final String REMARKS = "remarks";

    private int seq;
    private String unitType;
    private String extCode;
    private String extDefCode;
    private String extText;
    private String mgCode;

    private String carcass;
    private String carcassCode;
    private String carcassText;
    private String finishType;
    private String finishTypeCode;
    private String finishTypeText;
    private String finish;
    private String finishCode;
    private String finishText;
    private String colorCode;
    private String colorName;
    private String colorImagePath;
    private String makeType;
    private String makeTypeCode;
    private String makeTypeText;
    private double amount;
    private String remarks;
    private String importStatus;

    private List<MGModule> mgModules = new ArrayList<>();  //todo: make transient
    private Map<String, String> mgModuleImageMap;  //todo: make transient
    private ModulePrice modulePrice;  //todo: make transient

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

    public Map<String, String> getMgModuleImageMap() {
        return mgModuleImageMap;
    }

    public void setMgModuleImageMap(Map<String, String> mgModuleImageMap) {
        this.mgModuleImageMap = mgModuleImageMap;
    }

    public String getExtDefCode() {
        return extDefCode;
    }

    public void setExtDefCode(String extDefCode) {
        this.extDefCode = extDefCode;
    }

    public String getMakeType() {
        return makeType;
    }

    public void setMakeType(String makeType) {
        this.makeType = makeType;
    }

    public String getMakeTypeCode() {
        return makeTypeCode;
    }

    public void setMakeTypeCode(String makeTypeCode) {
        this.makeTypeCode = makeTypeCode;
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

    public String getMakeTypeText() {
        return makeTypeText;
    }

    public void setMakeTypeText(String makeTypeText) {
        this.makeTypeText = makeTypeText;
    }

    public String getCarcassText() {
        return carcassText;
    }

    public void setCarcassText(String carcassText) {
        this.carcassText = carcassText;
    }

    public String getFinishTypeText() {
        return finishTypeText;
    }

    public void setFinishTypeText(String finishTypeText) {
        this.finishTypeText = finishTypeText;
    }

    public String getFinishText() {
        return finishText;
    }

    public void setFinishText(String finishText) {
        this.finishText = finishText;
    }

    public List<MGModule> getMgModules() {
        return mgModules;
    }

    public void setMgModules(List<MGModule> mgModules) {
        this.mgModules = mgModules;
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

    public ModulePrice getModulePrice() {
        return modulePrice;
    }

    public void setModulePrice(ModulePrice modulePrice) {
        this.modulePrice = modulePrice;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Module module = (Module) o;

        return seq == module.seq;

    }

    @Override
    public int hashCode() {
        return seq;
    }
}
