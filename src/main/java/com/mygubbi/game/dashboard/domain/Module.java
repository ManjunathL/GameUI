package com.mygubbi.game.dashboard.domain;

import java.util.ArrayList;
import java.util.List;

import static java.lang.StrictMath.round;

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

    public enum UnitTypes {base, wall, accessory}

    public enum ModuleSource {file, button}


    public static final String SEQ = "seq";
    public static final String MODULE_SEQUENCE = "moduleSequence";
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
    public static final String DESCRIPTION = "description";
    public static final String DIMENSION = "dimension";
    public static  final String EXPOSED_LEFT = "exposedLeft";
    public static  final String EXPOSED_RIGHT = "exposedRight";
    public static  final String EXPOSED_TOP = "exposedTop";
    public static  final String EXPOSED_BACK = "exposedBack";
    public static  final String EXPOSED_BOTTOM = "exposedBottom";
    public static  final String EXP_BOTTOM = "expBottom";
    public static  final String EXPOSED_SIDES = "expSides";
    public static  final String EXPOSED_OPEN = "exposedOpen";
    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";
    public static final String DEPTH = "depth";
    public static final String MODULE_TYPE = "moduleType";
    public static final String MODULE_CATEGORY = "moduleCategory";
    public static final String PRODUCT_CATEGORY = "productCategory";


    private int seq;
    private int moduleSequence;
    private String unitType;
    private String extCode;
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
    private String description;
    private String dimension;
    private String imagePath;
    private boolean exposedRight;
    private boolean exposedLeft;
    private boolean exposedTop;
    private boolean exposedBottom;
    private boolean exposedBack;
    private boolean exposedOpen;
    private double area;
    private double amountWOAccessories;
    private int width;
    private int depth;
    private int height;
    private String moduleCategory;
    private String moduleType;
    private String productCategory;
    private String moduleSource;
    private String expSides;
    private String expBottom;
    private List<ModuleAccessoryPack> accessoryPacks=new ArrayList<>();

    public String getExpSides() {
        return expSides;
    }

    public void setExpSides(String expSides) {
        this.expSides = expSides;
    }

    public String getExpBottom() {
        return expBottom;
    }

    public void setExpBottom(String expBottom) {
        this.expBottom = expBottom;
    }




    public Boolean getExposedRight() {
        return exposedRight;
    }

    public void setExposedRight(Boolean exposedRight) {
        this.exposedRight = exposedRight;
    }

    public Boolean getExposedLeft() {
        return exposedLeft;
    }

    public void setExposedLeft(Boolean exposedLeft) {
        this.exposedLeft = exposedLeft;
    }

    public Boolean getExposedTop() {
        return exposedTop;
    }

    public void setExposedTop(Boolean exposedTop) {
        this.exposedTop = exposedTop;
    }

    public Boolean getExposedBottom() {
        return exposedBottom;
    }

    public void setExposedBottom(Boolean exposedBottom) {
        this.exposedBottom = exposedBottom;
    }

    public Boolean getExposedBack() {
        return exposedBack;
    }

    public void setExposedBack(Boolean exposedBack) {
        this.exposedBack = exposedBack;
    }

    public Boolean getExposedOpen() {
        return exposedOpen;
    }

    public void setExposedOpen(Boolean exposedOpen) {
        this.exposedOpen = exposedOpen;
    }

    public String getModuleSource() {
        return moduleSource;
    }

    public void setModuleSource(String moduleSource) {
        this.moduleSource = moduleSource;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getModuleCategory() {
        return moduleCategory;
    }

    public void setModuleCategory(String moduleCategory) {
        this.moduleCategory = moduleCategory;
    }

    public String getModuleType() {
        return moduleType;
    }

    public void setModuleType(String moduleType) {
        this.moduleType = moduleType;
    }

    @Override
    public Module clone() {
        try {
            Module clone = (Module) super.clone();
            List<ModuleAccessoryPack> clonedPacks = new ArrayList<>();
            clonedPacks.addAll(this.getAccessoryPacks());
            clone.setAccessoryPacks(clonedPacks);
            return clone;
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


    public int getModuleSequence() {
        return moduleSequence;
    }

    public void setModuleSequence(int moduleSequence) {
        this.moduleSequence = moduleSequence;
    }

    public int getWidth() {
        return width;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
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
        return round(amount);
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


    public List<ModuleAccessoryPack> getAccessoryPacks() {
        return accessoryPacks;
    }

    public void setAccessoryPacks(List<ModuleAccessoryPack> accessoryPacks) {
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

       return this.moduleSequence == module.getModuleSequence();
    }

    @Override
    public int hashCode()
    {
        return moduleSequence;
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
        return "Module{" +
                "seq=" + seq +
                ", moduleSequence=" + moduleSequence +
                ", unitType='" + unitType + '\'' +
                ", extCode='" + extCode + '\'' +
                ", extText='" + extText + '\'' +
                ", mgCode='" + mgCode + '\'' +
                ", carcass='" + carcass + '\'' +
                ", carcassCode='" + carcassCode + '\'' +
                ", fixedCarcassCode='" + fixedCarcassCode + '\'' +
                ", finishType='" + finishType + '\'' +
                ", finishTypeCode='" + finishTypeCode + '\'' +
                ", finish='" + finish + '\'' +
                ", finishCode='" + finishCode + '\'' +
                ", colorCode='" + colorCode + '\'' +
                ", colorName='" + colorName + '\'' +
                ", colorImagePath='" + colorImagePath + '\'' +
                ", amount=" + amount +
                ", remarks='" + remarks + '\'' +
                ", importStatus='" + importStatus + '\'' +
                ", description='" + description + '\'' +
                ", dimension='" + dimension + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", exposedRight=" + exposedRight +
                ", exposedLeft=" + exposedLeft +
                ", exposedTop=" + exposedTop +
                ", exposedBottom=" + exposedBottom +
                ", exposedBack=" + exposedBack +
                ", exposedOpen=" + exposedOpen +
                ", area=" + area +
                ", amountWOAccessories=" + amountWOAccessories +
                ", width=" + width +
                ", depth=" + depth +
                ", height=" + height +
                ", moduleCategory='" + moduleCategory + '\'' +
                ", moduleType='" + moduleType + '\'' +
                ", productCategory='" + productCategory + '\'' +
                ", moduleSource='" + moduleSource + '\'' +
                ", expSides='" + expSides + '\'' +
                ", expBottom='" + expBottom + '\'' +
                ", accessoryPacks=" + accessoryPacks +
                '}';
    }

    public static void main(String[] args)
    {
        //Test cloning

        Module origin = new Module();
        origin.setMgCode("MG1");

        Module clone = (Module) origin.clone();
        System.out.println("Original:" + origin.getMgCode() + " | Cloned:" + clone.getMgCode());
    }
}
