package com.mygubbi.game.dashboard.domain;

/**
 * Created by nitinpuri on 08-06-2016.
 */
public class CatalogueProductSubCategory {

    public static final String NAME = "name";
    public static final String ID = "id";
    public static final String CODE = "code";

    private String name;
    private String id;
    private String code;

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
}
