package com.mygubbi.game.dashboard.domain.JsonPojo;

import com.vaadin.server.ExternalResource;

/**
 * Created by user on 10-Feb-17.
 */
public class AddonMaster
{
    public static final String ID="id";
    public static final String CODE="code";
    public static final String CATEGORY_CODE="categoryCode";
    public static final String ROOM_CODE="roomCode";
    public static final String PRODUCT_TYPE_CODE="productTypeCode";
    public static final String PRODUCT_SUBTYPE_CODE="productSubtypeCode";
    public static final String BRANCH_CODE="brandCode";
    public static final String PRODUCT="product";
    public static final String CATALOUGE_CODE="catalogueCode";
    public static final String RATE_READ_ONLY="rateReadOnly";
    public static final String TITLE="title";
    public static final String RATE="rate";
    public static final String MRP="mrp";
    public static final String DEALER_PRICE="dealerPrice";
    public static final String UOM="uom";
    public static final String IMAGE_PATH="imagePath";

    private String id;
    private String code;
    private String categoryCode;
    private String roomCode;
    private String productTypeCode;
    private String productSubtypeCode;
    private String brandCode;
    private String product;
    private String catalogueCode;
    private String rateReadOnly;
    private String title;
    private String rate;
    private String mrp;
    private String dealerPrice;
    private String uom;
    //private ExternalResource imagePath;
    private String imagePath;

    /*public AddonMaster(ExternalResource imagePath,String catalogueCode,String productTypeCode)
    {
        this.imagePath=imagePath;
        this.catalogueCode=catalogueCode;
        this.productTypeCode=productTypeCode;
    }

    public AddonMaster(String brandCode, String catalogueCode, String categoryCode, String code, String dealerPrice, String id, ExternalResource imagePath, String mrp, String product, String productSubtypeCode, String productTypeCode, String rate, String rateReadOnly, String roomCode, String title, String uom) {
        this.brandCode = brandCode;
        this.catalogueCode = catalogueCode;
        this.categoryCode = categoryCode;
        this.code = code;
        this.dealerPrice = dealerPrice;
        this.id = id;
        this.imagePath = imagePath;
        this.mrp = mrp;
        this.product = product;
        this.productSubtypeCode = productSubtypeCode;
        this.productTypeCode = productTypeCode;
        this.rate = rate;
        this.rateReadOnly = rateReadOnly;
        this.roomCode = roomCode;
        this.title = title;
        this.uom = uom;
    }*/

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getProductTypeCode() {
        return productTypeCode;
    }

    public void setProductTypeCode(String productTypeCode) {
        this.productTypeCode = productTypeCode;
    }

    public String getProductSubtypeCode() {
        return productSubtypeCode;
    }

    public void setProductSubtypeCode(String productSubtypeCode) {
        this.productSubtypeCode = productSubtypeCode;
    }

    public String getBrandCode() {
        return brandCode;
    }

    public void setBrandCode(String brandCode) {
        this.brandCode = brandCode;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getCatalogueCode() {
        return catalogueCode;
    }

    public void setCatalogueCode(String catalogueCode) {
        this.catalogueCode = catalogueCode;
    }

    public String getRateReadOnly() {
        return rateReadOnly;
    }

    public void setRateReadOnly(String rateReadOnly) {
        this.rateReadOnly = rateReadOnly;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getDealerPrice() {
        return dealerPrice;
    }

    public void setDealerPrice(String dealerPrice) {
        this.dealerPrice = dealerPrice;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    /*public Image getImagePath() {
        return imagePath;
    }

    public void setImagePath(Image imagePath) {
        this.imagePath = imagePath;
    }*/

   /* public ExternalResource getImagePath() {
        return imagePath;
    }

    public void setImagePath(ExternalResource imagePath) {
        this.imagePath = imagePath;
    }*/

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return "AddonMaster{" +
                "brandCode='" + brandCode + '\'' +
                ", id='" + id + '\'' +
                ", code='" + code + '\'' +
                ", categoryCode='" + categoryCode + '\'' +
                ", roomCode='" + roomCode + '\'' +
                ", productTypeCode='" + productTypeCode + '\'' +
                ", productSubtypeCode='" + productSubtypeCode + '\'' +
                ", product='" + product + '\'' +
                ", catalogueCode='" + catalogueCode + '\'' +
                ", rateReadOnly='" + rateReadOnly + '\'' +
                ", title='" + title + '\'' +
                ", rate='" + rate + '\'' +
                ", mrp='" + mrp + '\'' +
                ", dealerPrice='" + dealerPrice + '\'' +
                ", uom='" + uom + '\'' +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
}
