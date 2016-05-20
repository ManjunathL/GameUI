package com.mygubbi.game.dashboard.domain;

import com.vaadin.server.Resource;

/**
 * Created by nitinpuri on 19-05-2016.
 */
public class Color {
    private String name;
    private String code;
    private String imagePath;
    private Resource colorImageResource;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Resource getColorImageResource() {
        return colorImageResource;
    }

    public void setColorImageResource(Resource colorImageResource) {
        this.colorImageResource = colorImageResource;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Color color = (Color) o;

        return code != null ? code.equals(color.code) : color.code == null;

    }

    @Override
    public int hashCode() {
        return code != null ? code.hashCode() : 0;
    }
}
