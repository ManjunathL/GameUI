package com.mygubbi.game.dashboard.domain;

import com.mygubbi.game.dashboard.config.ConfigHolder;
import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;

import java.io.File;

/**
 * Created by test on 15-07-2016.
 */
public class AccessoryAddon {

    public static final String ACCESSORY_ADDON_CODE= "code";
    public static final String ACCESSORY_ADDON_TITLE= "title";
    public static final String IMAGE_RESOURCE = "imageResource";

    private String code;
    private String title;
    private String imagePath;
    private Resource imageResource;


    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

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

    public void initImageResource() {
        this.imageResource = new FileResource(new File(ConfigHolder.getInstance().getImageBasePath() + imagePath));
    }

    public Resource getImageResource() {
        return imageResource;
    }
}
