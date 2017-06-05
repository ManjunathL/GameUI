package com.mygubbi.game.dashboard.domain;

import com.mygubbi.game.dashboard.domain.JsonPojo.AccessoryDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 04-May-17.
 */
public class HandleMaster
{
    public static final String CODE = "code";
    public static final String TYPE = "type";
    public static final String TITLE = "title";
    public static final String FINISH = "finish";
    public static final String MG_CODE = "mgCode";
    public static final String THICKNESS = "thickness";
    public static final String SOURCE_PRICE = "sourcePrice";
    public static final String MSP = "msp";
    public static final String IMAGE_PATH = "imagePath";
    public static final String PRODUCT_CATEGORY="productCategory";
    private List<AccessoryDetails> quantity=new ArrayList<>();

    private String code;
    private String type;
    private String title;
    private String finish;
    private String mgCode;
    private String thickness;
    private int sourcePrice;
    private int msp;
    private String imagePath;
    private String productCategory;

    public String getFinish() {
        return finish;
    }

    public void setFinish(String finish) {
        this.finish = finish;
    }

    public String getMgCode() {
        return mgCode;
    }

    public void setMgCode(String mgCode) {
        this.mgCode = mgCode;
    }

    public int getMsp() {
        return msp;
    }

    public void setMsp(int msp) {
        this.msp = msp;
    }

    public int getSourcePrice() {
        return sourcePrice;
    }

    public void setSourcePrice(int sourcePrice) {
        this.sourcePrice = sourcePrice;
    }

    public String getThickness() {
        return thickness;
    }

    public void setThickness(String thickness) {
        this.thickness = thickness;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<AccessoryDetails> getQuantity() {
        return quantity;
    }

    public void setQuantity(List<AccessoryDetails> quantity) {
        this.quantity = quantity;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    @Override
    public String toString() {
        return "HandleMaster{" +
                "code='" + code + '\'' +
                ", quantity=" + quantity +
                ", type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", finish='" + finish + '\'' +
                ", mgCode='" + mgCode + '\'' +
                ", thickness='" + thickness + '\'' +
                ", sourcePrice=" + sourcePrice +
                ", msp=" + msp +
                ", imagePath='" + imagePath + '\'' +
                ", productCategory='" + productCategory + '\'' +
                '}';
    }
}
