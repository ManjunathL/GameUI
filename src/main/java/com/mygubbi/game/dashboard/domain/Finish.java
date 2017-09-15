package com.mygubbi.game.dashboard.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nitinpuri on 15-06-2016.
 */
public class Finish {

    public static final String FINISH_MATERIAL = "finishMaterial";
    public static final String FINISH_CODE = "finishCode";
    public static final String SHUTTER_CODE = "shutterCode";
    public static final String COLOR_GROUP_CODE = "colorGroupCode";
    public static final String TITLE = "title";
    public static final String IMAGE_PATH = "imagePath";
    public static final String FROM_DATE="fromDate";
    public static final String TO_DATE="toDate";
    public static final String SET_CODE="setCode";

    private String finishMaterial;
    private String finishCode;
    private String colorGroupCode;
    private String title;
    private String shutterCode;
    private String imagePath;
    private String toDate;
    private String fromDate;
    private String setCode;

    public String getFinishMaterial() {
        return finishMaterial;
    }

    public void setFinishMaterial(String finishMaterial) {
        this.finishMaterial = finishMaterial;
    }

    public String getFinishCode() {
        return finishCode;
    }

    public void setFinishCode(String finishCode) {
        this.finishCode = finishCode;
    }

    public String getColorGroupCode() {
        return colorGroupCode;
    }

    public void setColorGroupCode(String colorGroupCode) {
        this.colorGroupCode = colorGroupCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShutterCode() {
        return shutterCode;
    }

    public void setShutterCode(String shutterCode) {
        this.shutterCode = shutterCode;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }


    public String getSetCode() {
        return setCode;
    }

    public void setSetCode(String setCode) {
        this.setCode = setCode;
    }

    @Override
    public String toString() {
        return "Finish{" +
                "finishMaterial='" + finishMaterial + '\'' +
                ", finishCode='" + finishCode + '\'' +
                ", colorGroupCode='" + colorGroupCode + '\'' +
                ", title='" + title + '\'' +
                ", shutterCode='" + shutterCode + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", toDate='" + toDate + '\'' +
                ", fromDate='" + fromDate + '\'' +
                ", setCode='" + setCode + '\'' +
                '}';
    }
}
