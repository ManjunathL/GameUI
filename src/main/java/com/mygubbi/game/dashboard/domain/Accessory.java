package com.mygubbi.game.dashboard.domain;

import com.vaadin.server.FileResource;

import java.io.File;

/**
 * Created by test on 15-07-2016.
 */
public class Accessory {

    private String code;
    private String title;
    private String imagePath;

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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

}
