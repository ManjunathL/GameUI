package com.mygubbi.game.dashboard.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nitinpuri on 19-05-2016.
 */
public class MGModule {
    public static final String CODE = "code";
    public static final String TITLE = "title";
    public static final String CONCAT = "concat";
    public static final String MODULE_TYPE = "moduleType";
    public static final String MODULE_CATEGORY = "moduleCategory";
    public static final String UNIT_TYPE = "unitType";
    public static final String ACCESSORYPACKDEFAULT = "accessoryPackDefault";
    public static final String HANDLE_MANDATORY = "handleMandatory";
    public static final String KNOB_MANDATORY = "knobMandatory";
    public static final String SQFT_CALCULATION = "sqftCalculation";
    public static final String HINGE_MANDATORY = "hingeMandatory";


    private String code;
    private String title;
    private String carcassCode;
    private String description;
    private int width;
    private int depth;
    private int height;
    private String imagePath;
    private String unitType;
    private String moduleCategory;
    private String productCategory;
    private String moduleType;
    private String accessoryPackDefault;
    private String handleMandatory;
    private String KnobMandatory;
    private String sqftCalculation;
    private String hingeMandatory;

    private String concat;
    private List<ModuleAccessory> accessories = new ArrayList<>();

    public String getConcat() {
        return description+"-"+code;
    }

    public void setConcat(String concat) {
        this.concat = concat;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getWidth() {
        return width;
    }

    public String getDimensions() {
        return getWidth() + " x " + getDepth() + " x " + getHeight();
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public List<ModuleAccessory> getAccessories() {
        return accessories;
    }

    public void setAccessories(List<ModuleAccessory> accessories) {
        this.accessories = accessories;
    }

    public String getCarcassCode() {
        return carcassCode;
    }

    public void setCarcassCode(String carcassCode) {
        this.carcassCode = carcassCode;
    }

    public String getModuleCategory() {
        return moduleCategory;
    }

    public void setModuleCategory(String moduleCategory) {
        this.moduleCategory = moduleCategory;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getModuleType() {
        return moduleType;
    }

    public void setModuleType(String moduleType) {
        this.moduleType = moduleType;
    }

    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public String getAccessoryPackDefault() {
        return accessoryPackDefault;
    }
    public void setAccessoryPackDefault(String accessoryPackDefault)
    {
        this.accessoryPackDefault=accessoryPackDefault;
    }

    public String getHandleMandatory() {
        return handleMandatory;
    }

    public void setHandleMandatory(String handleMandatory) {
        this.handleMandatory = handleMandatory;
    }

    public String getKnobMandatory() {
        return KnobMandatory;
    }

    public void setKnobMandatory(String knobMandatory) {
        KnobMandatory = knobMandatory;
    }

    public String getSqftCalculation() {
        return sqftCalculation;
    }

    public void setSqftCalculation(String sqftCalculation) {
        this.sqftCalculation = sqftCalculation;
    }

    public String getHingeMandatory() {
        return hingeMandatory;
    }

    public void setHingeMandatory(String hingeMandatory) {
        this.hingeMandatory = hingeMandatory;
    }

    @Override
    public String toString() {
        return "MGModule{" +
                "accessories=" + accessories +
                ", code='" + code + '\'' +
                ", title='" + title + '\'' +
                ", carcassCode='" + carcassCode + '\'' +
                ", description='" + description + '\'' +
                ", width=" + width +
                ", depth=" + depth +
                ", height=" + height +
                ", imagePath='" + imagePath + '\'' +
                ", unitType='" + unitType + '\'' +
                ", moduleCategory='" + moduleCategory + '\'' +
                ", productCategory='" + productCategory + '\'' +
                ", moduleType='" + moduleType + '\'' +
                ", accessoryPackDefault='" + accessoryPackDefault + '\'' +
                ", handleMandatory='" + handleMandatory + '\'' +
                ", KnobMandatory='" + KnobMandatory + '\'' +
                ", sqftCalculation='" + sqftCalculation + '\'' +
                ", hingeMandatory='" + hingeMandatory + '\'' +
                ", concat='" + concat + '\'' +
                '}';
    }
}
