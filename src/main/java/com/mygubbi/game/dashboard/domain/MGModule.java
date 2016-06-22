package com.mygubbi.game.dashboard.domain;

import java.util.ArrayList;
import java.util.List;

import static java.lang.StrictMath.round;

/**
 * Created by nitinpuri on 19-05-2016.
 */
public class MGModule {
    public static final String CODE = "code";
    private String code;
    private String carcassCode;
    private String description;
    private int width;
    private int depth;
    private int height;
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

    public int getWidth() {
        return width;
    }

    public String getDimensions() {
        return getWidth() + " x " + getDepth() + " x " + getHeight();
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
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

    public String getCarcassCode() {
        return carcassCode;
    }

    public void setCarcassCode(String carcassCode) {
        this.carcassCode = carcassCode;
    }
}
