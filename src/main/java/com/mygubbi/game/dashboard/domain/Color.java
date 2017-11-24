package com.mygubbi.game.dashboard.domain;

import com.mygubbi.game.dashboard.view.proposals.PublishOnCRM;
import com.vaadin.server.Resource;

/**
 * Created by nitinpuri on 19-05-2016.
 */
public class Color {

    public static final String NAME ="name";
    public static final String CODE ="code";
    public static final String IMAGE_PATH ="imagePath";
    public static final String COLORIMAGE_RESOURCE ="colorImageResource";

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
    public String toString() {
        return "Color{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", colorImageResource=" + colorImageResource +
                '}';
    }
}
