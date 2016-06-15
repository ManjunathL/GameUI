package com.mygubbi.game.dashboard.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nitinpuri on 08-06-2016.
 */
public class CatalogueProductCategory {

    public static final String NAME = "name";
    public static final String ID = "id";
    public static final String CODE = "code";

    private String name;
    private String id;
    private String code;
    private List<CatalogueProductSubCategory> subCategories = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public List<CatalogueProductSubCategory> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<CatalogueProductSubCategory> subCategories) {
        this.subCategories = subCategories;
    }
}
