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

    public enum UnitTypes {base, wall, loft,tall}

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
    public static final String ACCESSORYPACKDEFAULT = "accessoryPackDefault";
    public static final String ACCESSORY_FLAG="accessoryflag";
    public static final String SHUTTER_DESIGN="shutterDesign";
    public static final String HANDLE_TYPE="handleType";
    public static final String HANDLE_FINISH="handleFinish";
    public static final String HANDLE_THICKNESS="handleThickness";
    public static final String KNOB_TYPE="knobType";
    public static final String KNOB_FINISH="knobFinish";
    public static final String KNOB_THICKNESS="knobThickness";
    public static final String HANDLE_PRESENT="handlePresent";
    public static final String KNOB_PRESENT="knobPresent";
    public static final String CUSTOM_TEXT="customText";
    public static final String HANDLE_CODE="handleCode";
    public static final String KNOB_CODE="knobCode";
    public static final String CUSTOM_CHECK="customCheck";
    public static final String HANDLE_QUANTITY="handleQuantity";
    public static final String KNOB_QUANTITY="knobQuantity";
    public static final String GLASS_TYPE="glassType";
    public static final String HINGE_TYPE="hingeType";
    public static final String HINGE_PRESENT="hingePresent";
    public static final String HINGE_CODE="hingeCode";
    public static final String HINGE_QUANTITY="hingeQuantity";
    public static final String HNADLE_SELECTION_TYPE="handleTypeSelection";
    public static final String HINGE_PACK="hingePack";
    public static final String HINGE_CHANGED_FLAG="handleChangedFlag";
    public static final String NO_OF_HANDLE="noOFHandle";
    public static final String GOLA_PROFILE_FLAG = "golaProfileFlag";

    private int seq;
    private int moduleSequence;
    private String unitType;
    private String extCode;
    private String extText;
    private String mgCode;
    private String carcass;
    private String wallcasscode;
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
    private String description="";
    private String dimension;
    private String imagePath = "image.jpg";
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
    private String accessoryPackDefault;
    private double woodworkCost;
    private double hardwareCost;
    private double shutterCost;
    private double carcassCost;
    private double accessoryCost;
    private double labourCost;
    private String accessoryflag;
    private String shutterDesign;
    private String handleType;
    private String handleFinish;
    private String handleThickness;
    private String knobType;
    private String knobFinish;
    private String knobThickness;
    private String handlePresent;
    private String knobPresent;
    private String handleCode;
    private String knobCode;
    private String customText;
    private String customCheck;
    private int handleQuantity;
    private int knobQuantity;
    private String newModuleFlag;
    private String glassType;
    private String hingeType;
    private String hingePresent;
    private String hingeCode;
    private int hingeQuantity;
    private String handleTypeSelection;
    private boolean handleChangedFlag;
    private boolean knobChangedFlag;
    private int noOFHandle;
    private String golaProfileFlag;

    private List<ModuleAccessoryPack> accessoryPacks=new ArrayList<>();
    private List<HandleMaster> handlePack=new ArrayList<>();
    private List<HandleMaster> knobPack=new ArrayList<>();
    private List<ModuleHingeMap> hingePack=new ArrayList<>();

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

    public String getWallcasscode() {
        return wallcasscode;
    }

    public void setWallcasscode(String wallcasscode) {
        this.wallcasscode = wallcasscode;
    }

    public String getModuleType() {
        return moduleType;
    }

    public void setModuleType(String moduleType) {
        this.moduleType = moduleType;
    }

    public String getAccessoryPackDefault() {
        return accessoryPackDefault;
    }
    public void setAccessoryPackDefault(String accessoryPackDefault)
    {
        this.accessoryPackDefault=accessoryPackDefault;
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

    public double getAccessoryCost() {
        return accessoryCost;
    }

    public void setAccessoryCost(double accessoryCost) {
        this.accessoryCost = accessoryCost;
    }

    public double getCarcassCost() {
        return carcassCost;
    }

    public void setCarcassCost(double carcassCost) {
        this.carcassCost = carcassCost;
    }

    public double getHardwareCost() {
        return hardwareCost;
    }

    public void setHardwareCost(double hardwareCost) {
        this.hardwareCost = hardwareCost;
    }

    public double getLabourCost() {
        return labourCost;
    }

    public void setLabourCost(double labourCost) {
        this.labourCost = labourCost;
    }

    public double getShutterCost() {
        return shutterCost;
    }

    public void setShutterCost(double shutterCost) {
        this.shutterCost = shutterCost;
    }

    public double getWoodworkCost() {
        return woodworkCost;
    }

    public void setWoodworkCost(double woodworkCost) {
        this.woodworkCost = woodworkCost;
    }

    public String getAccessoryflag() {
        return accessoryflag;
    }

    public void setAccessoryflag(String accessoryflag) {
        this.accessoryflag = accessoryflag;
    }

    public String getHandleThickness() {
        return handleThickness;
    }

    public void setHandleThickness(String handleThickness) {
        this.handleThickness = handleThickness;
    }

    public String getHandleType() {
        return handleType;
    }

    public void setHandleType(String handleType) {
        this.handleType = handleType;
    }

    public String getKnobThickness() {
        return knobThickness;
    }

    public void setKnobThickness(String knobThickness) {
        this.knobThickness = knobThickness;
    }

    public String getKnobType() {
        return knobType;
    }

    public void setKnobType(String knobType) {
        this.knobType = knobType;
    }

    public String getShutterDesign() {
        return shutterDesign;
    }

    public void setShutterDesign(String shutterDesign) {
        this.shutterDesign = shutterDesign;
    }

    public String getHandlePresent() {
        return handlePresent;
    }

    public void setHandlePresent(String handlePresent) {
        this.handlePresent = handlePresent;
    }

    public void clearAcessorryPacks() {
        this.accessoryPacks.clear();

    }

    public String getHandleFinish() {
        return handleFinish;
    }

    public void setHandleFinish(String handleFinish) {
        this.handleFinish = handleFinish;
    }

    public String getKnobFinish() {
        return knobFinish;
    }

    public void setKnobFinish(String knobFinish) {
        this.knobFinish = knobFinish;
    }

    public String getHandleCode() {
        return handleCode;
    }

    public void setHandleCode(String handleCode) {
        this.handleCode = handleCode;
    }

    public String getKnobCode() {
        return knobCode;
    }

    public void setKnobCode(String knobCode) {
        this.knobCode = knobCode;
    }

    public String getCustomText() {
        return customText;
    }

    public void setCustomText(String customText) {
        this.customText = customText;
    }

    public String getCustomCheck() {
        return customCheck;
    }

    public void setCustomCheck(String customCheck) {
        this.customCheck = customCheck;
    }

    public String getGolaProfileFlag() {
        return golaProfileFlag;
    }

    public void setGolaProfileFlag(String golaProfileFlag) {
        this.golaProfileFlag = golaProfileFlag;
    }

    public static void main(String[] args)
    {
        //Test cloning

        Module origin = new Module();
        origin.setMgCode("MG1");

        Module clone = (Module) origin.clone();
        System.out.println("Original:" + origin.getMgCode() + " | Cloned:" + clone.getMgCode());
    }


    public List<HandleMaster> getKnobPack() {
        return knobPack;
    }

    public void setKnobPack(List<HandleMaster> knobPack) {
        this.knobPack = knobPack;
    }

    public List<HandleMaster> getHandlePack() {
        return handlePack;
    }

    public void setHandlePack(List<HandleMaster> handlePack) {
        this.handlePack = handlePack;
    }

    public int getHandleQuantity() {
        return handleQuantity;
    }

    public void setHandleQuantity(int handleQuantity) {
        this.handleQuantity = handleQuantity;
    }

    public int getKnobQuantity() {
        return knobQuantity;
    }

    public void setKnobQuantity(int knobQuantity) {
        this.knobQuantity = knobQuantity;
    }

    public String getKnobPresent() {
        return knobPresent;
    }

    public void setKnobPresent(String knobPresent) {
        this.knobPresent = knobPresent;
    }

    public String getGlassType() {
        return glassType;
    }

    public void setGlassType(String glassType) {
        this.glassType = glassType;
    }

    public String getHingeType() {
        return hingeType;
    }

    public void setHingeType(String hingeType) {
        this.hingeType = hingeType;
    }

    public String getHingePresent() {
        return hingePresent;
    }

    public void setHingePresent(String hingePresent) {
        this.hingePresent = hingePresent;
    }

    public String getHingeCode() {
        return hingeCode;
    }

    public void setHingeCode(String hingeCode) {
        this.hingeCode = hingeCode;
    }

    public int getHingeQuantity() {
        return hingeQuantity;
    }

    public void setHingeQuantity(int hingeQuantity) {
        this.hingeQuantity = hingeQuantity;
    }

    public List<ModuleHingeMap> getHingePack() {
        return hingePack;
    }

    public void setHingePack(List<ModuleHingeMap> hingePack) {
        this.hingePack = hingePack;
    }

    public String getHandleTypeSelection() {
        return handleTypeSelection;
    }

    public void setHandleTypeSelection(String handleTypeSelection) {
        this.handleTypeSelection = handleTypeSelection;
    }

    public boolean getHandleChangedFlag() {
        return handleChangedFlag;
    }

    public void setHandleChangedFlag(boolean handleChangedFlag) {
        this.handleChangedFlag = handleChangedFlag;
    }

    public boolean getKnobChangedFlag() {
        return knobChangedFlag;
    }

    public void setKnobChangedFlag(boolean knobChangedFlag) {
        this.knobChangedFlag = knobChangedFlag;
    }

    public String getNewModuleFlag() {
        return newModuleFlag;
    }

    public void setNewModuleFlag(String newModuleFlag) {
        this.newModuleFlag = newModuleFlag;
    }

    public int isNoOFHandle() {
        return noOFHandle;
    }

    public void setNoOFHandle(int noOFHandle) {
        this.noOFHandle = noOFHandle;
    }

    @Override
    public String toString() {
        return "Module{" +
                "accessoryCost=" + accessoryCost +
                ", seq=" + seq +
                ", moduleSequence=" + moduleSequence +
                ", unitType='" + unitType + '\'' +
                ", extCode='" + extCode + '\'' +
                ", extText='" + extText + '\'' +
                ", mgCode='" + mgCode + '\'' +
                ", carcass='" + carcass + '\'' +
                ", wallcasscode='" + wallcasscode + '\'' +
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
                ", accessoryPackDefault='" + accessoryPackDefault + '\'' +
                ", woodworkCost=" + woodworkCost +
                ", hardwareCost=" + hardwareCost +
                ", shutterCost=" + shutterCost +
                ", carcassCost=" + carcassCost +
                ", labourCost=" + labourCost +
                ", accessoryflag='" + accessoryflag + '\'' +
                ", shutterDesign='" + shutterDesign + '\'' +
                ", handleType='" + handleType + '\'' +
                ", handleFinish='" + handleFinish + '\'' +
                ", handleThickness='" + handleThickness + '\'' +
                ", knobType='" + knobType + '\'' +
                ", knobFinish='" + knobFinish + '\'' +
                ", knobThickness='" + knobThickness + '\'' +
                ", handlePresent='" + handlePresent + '\'' +
                ", knobPresent='" + knobPresent + '\'' +
                ", handleCode='" + handleCode + '\'' +
                ", knobCode='" + knobCode + '\'' +
                ", customText='" + customText + '\'' +
                ", customCheck='" + customCheck + '\'' +
                ", handleQuantity=" + handleQuantity +
                ", knobQuantity=" + knobQuantity +
                ", newModuleFlag='" + newModuleFlag + '\'' +
                ", glassType='" + glassType + '\'' +
                ", hingeType='" + hingeType + '\'' +
                ", hingePresent='" + hingePresent + '\'' +
                ", hingeCode='" + hingeCode + '\'' +
                ", hingeQuantity=" + hingeQuantity +
                ", accessoryPacks=" + accessoryPacks +
                ", handlePack=" + handlePack +
                ", knobPack=" + knobPack +
                ", hingePack=" + hingePack +
                '}';
    }
}


