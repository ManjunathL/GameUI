package com.mygubbi.game.dashboard.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by test on 15-07-2016.
 */
public class Accessory {

    private int code;
    private String title;
    private String imagePath;
    private List<AccessoryAddon> accessoryAddons=new ArrayList<>();

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
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

    public List<AccessoryAddon> getAccessoryAddons() {
        return accessoryAddons;
    }

    public void setAccessoryAddons(List<AccessoryAddon> accessoryAddons) {
        this.accessoryAddons = accessoryAddons;
    }
}
