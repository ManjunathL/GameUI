package com.mygubbi.game.dashboard.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nitinpuri on 19-05-2016.
 */
public class MGModule {
    private String code;
    private String description;
    private double w;
    private double d;
    private double h;
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

    public double getW() {
        return w;
    }

    public String getDimensions() {
        return getW() + " x " + getD() + " x " + getH();
    }

    public void setW(double w) {
        this.w = w;
    }

    public double getD() {
        return d;
    }

    public void setD(double d) {
        this.d = d;
    }

    public double getH() {
        return h;
    }

    public void setH(double h) {
        this.h = h;
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
