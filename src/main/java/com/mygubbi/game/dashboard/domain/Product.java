package com.mygubbi.game.dashboard.domain;

import com.mygubbi.game.dashboard.view.FileAttachmentsHolder;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by nitinpuri on 01-05-2016.
 */
public class Product implements FileAttachmentsHolder {

    public enum TYPES {CUSTOMIZED, CATALOGUE;};

    public static final String PROPOSAL_ID = "proposalId";
    public static final String ID = "id";
    public static final String SEQ = "seq";
    public static final String TITLE = "title";
    public static final String PRODUCT_CATEGORY = "productCategory";
    public static final String PRODUCT_CATEGORY_CODE = "productCategoryCode";
    public static final String CATALOGUE_ID = "catalogueId";
    public static final String ROOM = "room";
    public static final String ROOM_CODE = "roomCode";
    public static final String SHUTTER_DESIGN = "shutterDesign";
    public static final String SHUTTER_DESIGN_CODE = "shutterDesignCode";
    public static final String MAKE_TYPE = "makeType";
    public static final String MAKE_TYPE_CODE = "makeTypeCode";
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

    private int id;
    private int proposalId;
    private String title;
    private int seq;
    private String productCategory;
    private String productCategoryCode;
    private String room;
    private String roomCode;
    private String shutterDesign;
    private String shutterDesignCode;
    private String catalogueName;
    private String catalogueId;
    private String makeType;
    private String makeTypeCode;
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
    private String updatedBy;
    private List<Module> modules = new ArrayList<>();

    private List<AddonProduct> addons = new ArrayList<>();
    private List<FileAttachment> fileAttachmentList = new ArrayList<>();

    public Product() {
    }

    public Product(int proposalId, int seq) {
        this.proposalId = proposalId;
        this.seq = seq;
    }

    public void setGenerated() {
        for (Module module : modules) {
            module.setGenerated();
        }
    }

    public boolean hasImportErrorStatus() {
        return this.getModules().stream().anyMatch(module -> module.getImportStatus().equals(Module.ImportStatusType.n.name()));
    }

    public boolean allModulesMapped() {
        return this.getModules().stream().allMatch(module -> StringUtils.isNotEmpty(module.getMgCode()));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getShutterDesignCode() {
        return shutterDesignCode;
    }

    public void setShutterDesignCode(String shutterDesignCode) {
        this.shutterDesignCode = shutterDesignCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        if (id != product.id) return false;
        return proposalId == product.proposalId;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + proposalId;
        return result;
    }
}
