package com.mygubbi.game.dashboard.domain;

/**
 * Created by nitinpuri on 02-06-2016.
 */
public class AddonProduct implements Cloneable {

    /**
     * NOTE THAT THIS CLASS IMPLEMENTS CLONEABLE AND GIVES A CLONE METHOD.
     * DO NOT INTRODUCE NEW FIELDS, SPECIALLY FIELDS OF TYPE OBJECT OR COLLECTIONS WITHOUT KNOWING HOW TO CLONE THEM
     */

    public static final String SEQ = "seq";
    public static final String ID = "id";
    public static final String FROM_VERSION = "fromVersion";
    public static final String CODE = "code";
    public static final String TITLE = "title";
    public static final String AMOUNT = "amount";
    public static final String IMAGE_PATH = "imagePath";
    public static final String QUANTITY = "quantity";
    public static final String RATE = "rate";
    public static final String UOM = "uom";
    public static final String ADDON_SPACE_TYPE = "spaceType";
    public static final String ADDON_ROOM = "roomcode";
    public static final String ADDON_CATEGORY_CODE = "categoryCode";
    public static final String PRODUCT_TYPE_CODE = "productTypeCode";
    public static final String PRODUCT_SUBTYPE_CODE = "productSubtypeCode";
    public static final String PRODUCT = "product";
    public static final String BRAND_CODE = "brandCode";
    public static final String ADDON_CATEGORY = "category";
    public static final String PRODUCT_TYPE = "productType";
    public static final String BRAND = "brand";
    public static final String CATALOGUE_CODE = "catalogueCode";
    public static final String DESCRIPTION = "description";
    public static final String REMARKS="remarks";
    public static final String SPACE_TYPE="spaceType";
    public static final String ROOM_CODE="roomcode";
    public static final String CUSTOM_ADDON_CATEGORY="customAddonCategory";
    public static final String SCOPEDISPLAYFLAG = "scopeDisplayFlag";
    public static final String ADDON_TYPE = "addonType";
    public static final String INSTALLATION_PRICE = "installationPrice";

    private int seq;
    private int id;
    private int proposalId;
    private String fromVersion;
    private String code;
    private String catalogueCode;
    private String title;
    private String categoryCode;
    private String customAddonCategory;
    private String category;
    private String product;
    private String productTypeCode;
    private String productSubtypeCode;
    private String productType;
    private String brandCode;
    private String brand;
    private String uom;
    private double amount;
    private double installationPrice;
    private String imagePath;
    private int quantity;
    private double rate;
    private String updatedBy;
    private String description;
    private String remarks;
    private String spaceType;
    private String roomcode;
    private String scopeDisplayFlag;
    private String addonType;

    private boolean add = false;

    @Override
    public Object clone()  {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
            //Will never happen since we are implementing Cloneable
        }
    }

    public String getScopeDisplayFlag() {
        return scopeDisplayFlag;
    }

    public void setScopeDisplayFlag(String scopeDisplayFlag) {
        this.scopeDisplayFlag = scopeDisplayFlag;
    }

    public String getCustomAddonCategory() {
        return customAddonCategory;
    }

    public void setCustomAddonCategory(String customAddonCategory) {
        this.customAddonCategory = customAddonCategory;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getProductTypeCode() {
        return productTypeCode;
    }

    public void setProductTypeCode(String productTypeCode) {
        this.productTypeCode = productTypeCode;
    }

    public String getBrandCode() {
        return brandCode;
    }

    public void setBrandCode(String brandCode) {
        this.brandCode = brandCode;
    }

    public String getCatalogueCode() {
        return catalogueCode;
    }

    public void setCatalogueCode(String catalogueCode) {
        this.catalogueCode = catalogueCode;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public boolean isAdd() {
        return add;
    }

    public void setAdd(boolean add) {
        this.add = add;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public int getProposalId() {
        return proposalId;
    }

    public void setProposalId(int proposalId) {
        this.proposalId = proposalId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProductSubtypeCode() {
        return productSubtypeCode;
    }

    public void setProductSubtypeCode(String productSubtypeCode) {
        this.productSubtypeCode = productSubtypeCode;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getSpaceType() {
        return spaceType;
    }

    public void setSpaceType(String spaceType) {
        this.spaceType = spaceType;
    }

    public String getRoomcode() {
        return roomcode;
    }

    public void setRoomcode(String roomcode) {
        this.roomcode = roomcode;
    }

    public String getAddonType() {
        return addonType;
    }

    public void setAddonType(String addonType) {
        this.addonType = addonType;
    }

    public double getInstallationPrice() {
        return installationPrice;
    }

    public void setInstallationPrice(double installationPrice) {
        this.installationPrice = installationPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AddonProduct that = (AddonProduct) o;

        if (id != that.id) return false;
        if (!code.equals(that.code)) return false;
        if (!categoryCode.equals(that.categoryCode)) return false;
        if (!productTypeCode.equals(that.productTypeCode)) return false;
        return brandCode.equals(that.brandCode);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + code.hashCode();
        result = 31 * result + categoryCode.hashCode();
        result = 31 * result + productTypeCode.hashCode();
        result = 31 * result + brandCode.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "AddonProduct{" +
                "seq=" + seq +
                ", id=" + id +
                ", proposalId=" + proposalId +
                ", fromVersion='" + fromVersion + '\'' +
                ", code='" + code + '\'' +
                ", catalogueCode='" + catalogueCode + '\'' +
                ", title='" + title + '\'' +
                ", categoryCode='" + categoryCode + '\'' +
                ", customAddonCategory='" + customAddonCategory + '\'' +
                ", category='" + category + '\'' +
                ", product='" + product + '\'' +
                ", productTypeCode='" + productTypeCode + '\'' +
                ", productSubtypeCode='" + productSubtypeCode + '\'' +
                ", productType='" + productType + '\'' +
                ", brandCode='" + brandCode + '\'' +
                ", brand='" + brand + '\'' +
                ", uom='" + uom + '\'' +
                ", amount=" + amount +
                ", installationPrice=" + installationPrice +
                ", imagePath='" + imagePath + '\'' +
                ", quantity=" + quantity +
                ", rate=" + rate +
                ", updatedBy='" + updatedBy + '\'' +
                ", description='" + description + '\'' +
                ", remarks='" + remarks + '\'' +
                ", spaceType='" + spaceType + '\'' +
                ", roomcode='" + roomcode + '\'' +
                ", scopeDisplayFlag='" + scopeDisplayFlag + '\'' +
                ", addonType='" + addonType + '\'' +
                ", add=" + add +
                '}';
    }
}
