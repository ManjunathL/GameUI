package com.mygubbi.game.dashboard.domain;

/**
 * Created by user on 19-01-2017.
 */
public class AddonProductSubtype {

    public static final String PRODUCT_SUBTYPE_CODE = "productSubtypeCode";
    private String productSubtypeCode;

    public String getProductSubtypeCode() {
        return productSubtypeCode;
    }

    public void setProductSubtypeCode(String productSubtypeCode) {
        this.productSubtypeCode = productSubtypeCode;
    }

    @Override
    public String toString() {
        return "AddonProductSubtype{" +
                "productSubtypeCode='" + productSubtypeCode + '\'' +
                '}';
    }
}
