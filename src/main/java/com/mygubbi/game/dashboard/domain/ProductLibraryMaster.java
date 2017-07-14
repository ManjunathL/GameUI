package com.mygubbi.game.dashboard.domain;

/**
 * Created by user on 19-Apr-17.
 */
public class ProductLibraryMaster
{
    public static final String ID = "id";
    public static final String CATEGORY = "category";
    public static final String SUB_CATEGORY = "subcategory";
    public static final String COLLECTION = "collection";

    private int id;
    private String category;
    private String subcategory;
    private String collection;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public String getCollection() {   return collection;   }

    public void setCollection(String collection) {   this.collection = collection;   }

    @Override
    public String toString() {
        return "ProductLibraryMaster{" +
                "id=" + id +
                ", category='" + category + '\'' +
                ", subcategory='" + subcategory + '\'' +
                ", collection='" + collection + '\'' +
                '}';
    }
}
