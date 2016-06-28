package com.mygubbi.game.dashboard.domain.JsonPojo;

import com.mygubbi.game.dashboard.config.ConfigHolder;
import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;

import java.io.File;

public class ShutterDesign {

    public static final String CODE = "code";
    public static final String TITLE = "title";
    public static final String IMAGE_RESOURCE = "imageResource";
    private String code;
    private String title;
    private String additionalType;
    private Resource imageResource;

    public ShutterDesign(LookupItem lookupItem) {
        this.code = lookupItem.getCode();
        this.title = lookupItem.getTitle();
        this.additionalType = lookupItem.getAdditionalType();
        this.imageResource = new FileResource(new File(ConfigHolder.getInstance().getImageBasePath() + "designs/" + this.title + ".jpg"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ShutterDesign that = (ShutterDesign) o;

        return code.equals(that.code);

    }

    @Override
    public int hashCode() {
        return code.hashCode();
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

    public String getAdditionalType() {
        return additionalType;
    }

    public void setAdditionalType(String additionalType) {
        this.additionalType = additionalType;
    }

    public Resource getImageResource() {
        return imageResource;
    }

    public void setImageResource(Resource imageResource) {
        this.imageResource = imageResource;
    }
}
