package com.mygubbi.game.dashboard.domain;

/**
 * Created by Chirag on 05-09-2016.
 */
public class ModuleCategory {

    public static final String NAME = "name";
    public static final String ID = "id";
    public static final String CODE = "code";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";

    private String name;
    private String id;
    private String code;
    private String description;
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
