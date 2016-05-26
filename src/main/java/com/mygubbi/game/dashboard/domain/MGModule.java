package com.mygubbi.game.dashboard.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nitinpuri on 19-05-2016.
 */
public class MGModule {
    public static final String CODE = "code";
    private String code;
    private String description;
    private double width;
    private double depth;
    private double height;
    private String imagePath;
    private List<ModuleAccessory> accessories = new ArrayList<>();

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

    public double getWidth() {
        return width;
    }

    public String getDimensions() {
        return getWidth() + " x " + getDepth() + " x " + getHeight();
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getDepth() {
        return depth;
    }

    public void setDepth(double depth) {
        this.depth = depth;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public List<ModuleAccessory> getAccessories() {
        return accessories;
    }

    public void setAccessories(List<ModuleAccessory> accessories) {
        this.accessories = accessories;
    }

}
