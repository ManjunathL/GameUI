package com.mygubbi.game.dashboard.domain;

/**
 * Created by user on 19-Apr-17.
 */
public class ProductLibraryMaster
{
    public static final String ID = "id";
    public static final String CATEGORY = "category";
    public static final String SUB_CATEGORY = "subcategory";

    private int id;
    private String category;
    private String subcategory;

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

    @Override
    public String toString() {
        return "ProductLibraryMaster{" +
                "category='" + category + '\'' +
                ", id=" + id +
                ", subcategory='" + subcategory + '\'' +
                '}';
    }
}
