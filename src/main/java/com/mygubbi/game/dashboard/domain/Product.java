package com.mygubbi.game.dashboard.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mygubbi.game.dashboard.view.FileAttachmentsHolder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by nitinpuri on 01-05-2016.
 */
public class Product implements FileAttachmentsHolder {

    public enum TYPES {CUSTOMIZED, CATALOGUE,PRODUCT_LIBRARY;};

    public static final String PROPOSAL_ID = "proposalId";
    public static final String FROM_VERSION = "fromVersion";
    public static final String ID = "id";
    public static final String SEQ = "seq";
    public static final String MANUAL_SEQ = "manualSeq";
    public static final String TITLE = "title";
    public static final String PRODUCT_CATEGORY = "productCategory";
    public static final String SPACE_TYPE = "spaceType";
    public static final String PRODUCT_CATEGORY_CODE = "productCategoryCode";
    public static final String CATALOGUE_ID = "catalogueId";
    public static final String ROOM = "room";
    public static final String ROOM_CODE = "roomCode";
    public static final String SHUTTER_DESIGN = "shutterDesign";
    public static final String SHUTTER_DESIGN_CODE = "shutterDesignCode";
    public static final String BASE_CARCASS = "baseCarcass";
    public static final String BASE_CARCASS_CODE = "baseCarcassCode";
    public static final String WALL_CARCASS = "wallCarcass";
    public static final String WALL_CARCASS_CODE = "wallCarcassCode";
    public static final String FINISH_TYPE = "finishType";
    public static final String FINISH_TYPE_CODE = "finishTypeCode";
    public static final String SHUTTER_FINISH = "finish";
    public static final String SHUTTER_FINISH_CODE = "finishCode";
    public static final String AMOUNT = "amount";
    public static final String TYPE = "type";
    public static final String QTY = "quantity";
    public static final String QUOTE_FILE_PATH = "quoteFilePath";
    public static final String MODULES = "modules";
    public static final String ADDONS = "addons";
    public static final String FILE_ATTACHMENT_LIST = "fileAttachmentList";
    public static final String COST_WO_ACCESSORIES = "costWoAccessories";
    public static final String SOURCE = "source";
    public static final String SUB_CATEGORY="subCategory";
    public static final String PRODUCT_DESCRIPTION="ProductDescription";
    public static final String HINGES_TYPE="hinge";
    public static final String GLASS_TYPE="glass";
    public static final String HANDLE_TYPE="handleType";
    public static final String HANDLE_THICKNESS="handleThickness";
    public static final String KNOB_TYPE="knobType";
    public static final String KNOB_THICKNESS="knobThickness";
    public static final String HANDLE_FINISH="handleFinish";
    public static final String KNOB_Finish="knobFinish";
    public static final String HANDLE_IMAGE="handleImage";
    public static final String KNOB_IMAGE="knobImage";
    public static final String PRODUCT_LOCATION="productLocation";
    public static final String SHUTTER_IMAGE="shutterImage";
    public static final String CLOSE_BUTTON_FLAG="closeButtonFlag";
    public static final String NO_OF_LENGTHS="noOfLengths";
    public static final String HANDLETYPE_SELECTION="handleTypeSelection";
    public static final String HANDLE_CODE="handleCode";
    public static final String KNOB_CODE="knobCode";
    public static final String LCONNECTOR_PRICE="lConnectorPrice";
    public static final String PRODUCT_CATEGORYLOCKED="productCategoryLocked";
    public static final String FROM_PRODUCT="fromProduct";
    public static final String COLOR_CODE = "colorGroupCode";
    public static final String FINISH_SETID="finishSetId";
    public static final String CUSTOM_COLORCODE="customColorCode";

    private int id;
    private int proposalId;
    private String fromVersion;
    private String title;
    private int seq;
    private int manualSeq;
    private String productCategory;
    private String productCategoryCode;
    private String room;
    private String roomCode;
    private String spaceType;
    private String shutterDesign;
    private String shutterDesignCode;
    private String catalogueName;
    private String catalogueId;
    private String source;
    private String ProductDescription;
    private String subCategory;
    private String hinge;
    private String glass;
    private String handleType;
    private String handleFinish;
    private String handleThickness;
    private String knobType;
    private String knobFinish;
    private int knobThickness;
    private String handleTypeSelection;
    private String colorGroupCode;

    private String baseCarcass;
    private String baseCarcassCode;
    private String wallCarcass;
    private String wallCarcassCode;
    private String finishType;
    private String finishTypeCode;
    private String finish;
    private String finishCode;
    private String dimension;
    private double amount;
    private int quantity = 1;
    private String type;
    private String quoteFilePath;
    private Date createdOn;
    private String createdBy;
    private Date updatedOn;
    private double costWoAccessories;
    private String updatedBy;
    private String handleImage;
    private String knobImage;
    private String productLocation;
    private String shutterImage;
    private String closeButtonFlag;
    private double noOfLengths;
    private String handleCode;
    private String knobCode;
    private double lConnectorPrice;
    private String productCategoryLocked;
    private String finishSetId;
    private int fromProduct;
    private String customColorCode;

    private List<Module> modules = new ArrayList<>();

    public double getCostWoAccessories() {
        return costWoAccessories;
    }

    public void setCostWoAccessories(double costWoAccessories) {
        this.costWoAccessories = costWoAccessories;
    }

    private List<AddonProduct> addons = new ArrayList<>();
    private List<FileAttachment> fileAttachmentList = new ArrayList<>();

    public Product() {
    }

    public Product(int proposalId, int seq) {
        this.proposalId = proposalId;
        this.seq = seq;
    }

    public boolean hasImportErrorStatus() {
        return this.getModules().stream().anyMatch(module -> module.getImportStatus().equals(Module.ImportStatusType.n.name()));
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFromVersion() {
        return fromVersion;
    }

    public void setFromVersion(String fromVersion) {
        this.fromVersion = fromVersion;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getBaseCarcass() {
        return baseCarcass;
    }

    public void setBaseCarcass(String baseCarcass) {
        this.baseCarcass = baseCarcass;
    }

    public String getFinish() {
        return finish;
    }

    public void setFinish(String finish) {
        this.finish = finish;
    }

    public String getFinishType() {
        return finishType;
    }

    public void setFinishType(String finishType) {
        this.finishType = finishType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public String getQuoteFilePath() {
        return quoteFilePath;
    }

    public void setQuoteFilePath(String uploadFileName) {
        this.quoteFilePath = uploadFileName;
    }

    public String getProductCategoryCode() {
        return productCategoryCode;
    }

    public void setProductCategoryCode(String productCategoryCode) {
        this.productCategoryCode = productCategoryCode;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getBaseCarcassCode() {
        return baseCarcassCode;
    }

    public void setBaseCarcassCode(String baseCarcassCode) {
        this.baseCarcassCode = baseCarcassCode;
    }

    public String getFinishTypeCode() {
        return finishTypeCode;
    }

    public void setFinishTypeCode(String finishTypeCode) {
        this.finishTypeCode = finishTypeCode;
    }

    public String getFinishCode() {
        return finishCode;
    }

    public void setFinishCode(String finishCode) {
        this.finishCode = finishCode;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Module> getModules() {
        return modules;
    }

    public void setModules(List<Module> modules) {
        this.modules = modules;
    }

    public List<AddonProduct> getAddons() {
        return addons;
    }

    public void setAddons(List<AddonProduct> addons) {
        this.addons = addons;
    }

    @Override
    public List<FileAttachment> getFileAttachmentList() {
        return fileAttachmentList;
    }

    public void setFileAttachmentList(List<FileAttachment> fileAttachmentList) {
        this.fileAttachmentList = fileAttachmentList;
    }

    public int getProposalId() {
        return proposalId;
    }

    public void setProposalId(int proposalId) {
        this.proposalId = proposalId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCatalogueName() {
        return catalogueName;
    }

    public void setCatalogueName(String catalogueName) {
        this.catalogueName = catalogueName;
    }

    public String getCatalogueId() {
        return catalogueId;
    }

    public void setCatalogueId(String catalogueId) {
        this.catalogueId = catalogueId;
    }

    public String getWallCarcass() {
        return wallCarcass;
    }

    public void setWallCarcass(String wallCarcass) {
        this.wallCarcass = wallCarcass;
    }

    public String getWallCarcassCode() {
        return wallCarcassCode;
    }

    public void setWallCarcassCode(String wallCarcassCode) {
        this.wallCarcassCode = wallCarcassCode;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getShutterDesign() {
        return shutterDesign;
    }

    public void setShutterDesign(String shutterDesign) {
        this.shutterDesign = shutterDesign;
    }

    public int getManualSeq() {
        return manualSeq;
    }

    public void setManualSeq(int manualSeq) {
        this.manualSeq = manualSeq;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getProductDescription() {
        return ProductDescription;
    }

    public void setProductDescription(String productDescription) {
        ProductDescription = productDescription;
    }

    public String getGlass() {
        return glass;
    }

    public void setGlass(String glass) {
        this.glass = glass;
    }

    public String getHinge() {
        return hinge;
    }

    public void setHinge(String hinge) {
        this.hinge = hinge;
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

    public int getKnobThickness() {
        return knobThickness;
    }

    public void setKnobThickness(int knobThickness) {
        this.knobThickness = knobThickness;
    }

    public String getKnobType() {
        return knobType;
    }

    public void setKnobType(String knobType) {
        this.knobType = knobType;
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

    public String getHandleImage() {
        return handleImage;
    }

    public void setHandleImage(String handleImage) {
        this.handleImage = handleImage;
    }

    public String getKnobImage() {
        return knobImage;
    }

    public void setKnobImage(String knobImage) {
        this.knobImage = knobImage;
    }

    public String getProductLocation() {    return productLocation;  }

    public void setProductLocation(String productLocation) {  this.productLocation = productLocation;  }

    public String getHandleTypeSelection() {
        return handleTypeSelection;
    }

    public void setHandleTypeSelection(String handleTypeSelection) {
        this.handleTypeSelection = handleTypeSelection;
    }

    public String getShutterDesignImage() {
        return shutterImage;
    }

    public void setShutterDesignImage(String shutterDesignImage) {
        this.shutterImage = shutterDesignImage;
    }

    public String getShutterDesignCode() {
        return shutterDesignCode;
    }

    public void setShutterDesignCode(String shutterDesignCode) {
        this.shutterDesignCode = shutterDesignCode;
    }

    public String getShutterImage() {
        return shutterImage;
    }

    public void setShutterImage(String shutterImage) {
        this.shutterImage = shutterImage;
    }

    public String getCloseButtonFlag() {
        return closeButtonFlag;
    }

    public void setCloseButtonFlag(String closeButtonFlag) {
        this.closeButtonFlag = closeButtonFlag;
    }

    public String getKnobCode() {
        return knobCode;
    }

    public void setKnobCode(String knobCode) {
        this.knobCode = knobCode;
    }

    public String getHandleCode() {
        return handleCode;
    }

    public void setHandleCode(String handleCode) {
        this.handleCode = handleCode;
    }

    public double getlConnectorPrice() {
        return lConnectorPrice;
    }

    public void setlConnectorPrice(double lConnectorPrice) {
        this.lConnectorPrice = lConnectorPrice;
    }

    public String getSpaceType() {
        return spaceType;
    }

    public void setSpaceType(String spaceType) {
        this.spaceType = spaceType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        if (id != product.id) return false;
        if (proposalId != product.proposalId) return false;
        return fromVersion.equals(product.fromVersion);

    }

    public double getNoOfLengths() {
        return noOfLengths;
    }

    public void setNoOfLengths(double noOfLengths) {
        this.noOfLengths = noOfLengths;
    }

    public String getProductCategoryLocked() {
        return productCategoryLocked;
    }

    public void setProductCategoryLocked(String productCategoryLocked) {
        this.productCategoryLocked = productCategoryLocked;
    }

    public int getFromProduct() {
        return fromProduct;
    }

    public void setFromProduct(int fromProduct) {
        this.fromProduct = fromProduct;
    }

    public String getColorGroupCode() {
        return colorGroupCode;
    }

    public void setColorGroupCode(String colorGroupCode) {
        this.colorGroupCode = colorGroupCode;
    }

    public String getFinishSetId() {
        return finishSetId;
    }

    public void setFinishSetId(String finishSetId) {
        this.finishSetId = finishSetId;
    }

    public String getCustomColorCode() {
        return customColorCode;
    }

    public void setCustomColorCode(String customColorCode) {
        this.customColorCode = customColorCode;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + proposalId;
        result = 31 * result + fromVersion.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", proposalId=" + proposalId +
                ", fromVersion='" + fromVersion + '\'' +
                ", title='" + title + '\'' +
                ", seq=" + seq +
                ", manualSeq=" + manualSeq +
                ", productCategory='" + productCategory + '\'' +
                ", productCategoryCode='" + productCategoryCode + '\'' +
                ", room='" + room + '\'' +
                ", roomCode='" + roomCode + '\'' +
                ", spaceType='" + spaceType + '\'' +
                ", shutterDesign='" + shutterDesign + '\'' +
                ", shutterDesignCode='" + shutterDesignCode + '\'' +
                ", catalogueName='" + catalogueName + '\'' +
                ", catalogueId='" + catalogueId + '\'' +
                ", source='" + source + '\'' +
                ", ProductDescription='" + ProductDescription + '\'' +
                ", subCategory='" + subCategory + '\'' +
                ", hinge='" + hinge + '\'' +
                ", glass='" + glass + '\'' +
                ", handleType='" + handleType + '\'' +
                ", handleFinish='" + handleFinish + '\'' +
                ", handleThickness='" + handleThickness + '\'' +
                ", knobType='" + knobType + '\'' +
                ", knobFinish='" + knobFinish + '\'' +
                ", knobThickness=" + knobThickness +
                ", handleTypeSelection='" + handleTypeSelection + '\'' +
                ", colorGroupCode='" + colorGroupCode + '\'' +
                ", baseCarcass='" + baseCarcass + '\'' +
                ", baseCarcassCode='" + baseCarcassCode + '\'' +
                ", wallCarcass='" + wallCarcass + '\'' +
                ", wallCarcassCode='" + wallCarcassCode + '\'' +
                ", finishType='" + finishType + '\'' +
                ", finishTypeCode='" + finishTypeCode + '\'' +
                ", finish='" + finish + '\'' +
                ", finishCode='" + finishCode + '\'' +
                ", dimension='" + dimension + '\'' +
                ", amount=" + amount +
                ", quantity=" + quantity +
                ", type='" + type + '\'' +
                ", quoteFilePath='" + quoteFilePath + '\'' +
                ", createdOn=" + createdOn +
                ", createdBy='" + createdBy + '\'' +
                ", updatedOn=" + updatedOn +
                ", costWoAccessories=" + costWoAccessories +
                ", updatedBy='" + updatedBy + '\'' +
                ", handleImage='" + handleImage + '\'' +
                ", knobImage='" + knobImage + '\'' +
                ", productLocation='" + productLocation + '\'' +
                ", shutterImage='" + shutterImage + '\'' +
                ", closeButtonFlag='" + closeButtonFlag + '\'' +
                ", noOfLengths=" + noOfLengths +
                ", handleCode='" + handleCode + '\'' +
                ", knobCode='" + knobCode + '\'' +
                ", lConnectorPrice=" + lConnectorPrice +
                ", productCategoryLocked='" + productCategoryLocked + '\'' +
                ", finishSetId='" + finishSetId + '\'' +
                ", fromProduct=" + fromProduct +
                ", customColorCode='" + customColorCode + '\'' +
                ", modules=" + modules +
                ", addons=" + addons +
                ", fileAttachmentList=" + fileAttachmentList +
                '}';
    }
}
