package com.mygubbi.game.dashboard.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nitinpuri on 08-06-2016.
 */
public class CatalogueProduct extends Product {

    public static final String PRODUCT_ID = "productId";
    public static final String NAME = "name";
    public static final String SELECTED_MATERIAL = BASE_CARCASS_CODE;
    public static final String SELECTED_FINISH = SHUTTER_FINISH_CODE;
    public static final String DESC = "desc";

    private String id;
    private String productId;
    private String name;
    private String desc;

    private List<CatalogueMaterialFinish> mf = new ArrayList<>();
    private List<String> images = new ArrayList<>();

    public void setId(String id) {
        this.id = id;
    }

    public void popuplateProduct() {
        setCatalogueId(productId);
        setCatalogueName(name);
        setTitle(name);
        setType(TYPES.CATALOGUE.name());
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<CatalogueMaterialFinish> getMf() {
        return mf;
    }

    public void setMf(List<CatalogueMaterialFinish> mf) {
        this.mf = mf;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public void populateFromProduct(Product product) {
        this.setProductId(product.getCatalogueId());
        this.setProposalId(product.getProposalId());
        this.setName(product.getTitle());
        this.setId(product.getId());
        this.setTitle(product.getTitle());
        this.setSeq(product.getSeq());
        this.setProductCategory(product.getProductCategory());
        this.setProductCategoryCode(product.getProductCategoryCode());
        this.setRoom(product.getRoom());
        this.setRoomCode(product.getRoomCode());
        this.setCatalogueName(product.getCatalogueName());
        this.setCatalogueId(product.getCatalogueId());
        this.setBaseCarcassCode(product.getBaseCarcassCode());
        this.setFinish(product.getFinish());
        this.setFinishCode(product.getFinishCode());
        this.setAmount(product.getAmount());
        this.setType(product.getType());
    }

    @Override
    public String toString() {
        return "CatalogueProduct{" +
                "id='" + id + '\'' +
                ", productId='" + productId + '\'' +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", mf=" + mf +
                ", images=" + images +
                '}';
    }
}
