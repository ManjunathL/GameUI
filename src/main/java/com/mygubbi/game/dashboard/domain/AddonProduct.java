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
    public static final String ADDON_CATEGORY_CODE = "categoryCode";
    public static final String PRODUCT_TYPE_CODE = "productTypeCode";
    public static final String BRAND_CODE = "brandCode";
    public static final String ADDON_CATEGORY = "category";
    public static final String PRODUCT_TYPE = "productType";
    public static final String BRAND = "brand";
    public static final String CATALOGUE_CODE = "catalogueCode";

    private int seq;
    private int id;
    private int proposalId;
    private double fromVersion;
    private String code;
    private String catalogueCode;
    private String title;
    private String categoryCode;
    private String category;
    private String productTypeCode;
    private String productType;
    private String brandCode;
    private String brand;
    private String uom;
    private double amount;
    private String imagePath;
    private int quantity;
    private double rate;
    private String updatedBy;

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

    public double getFromVersion() {
        return fromVersion;
    }

    public void setFromVersion(double fromVersion) {
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
}
