package com.mygubbi.game.dashboard.domain;

/**
 * Created by nitinpuri on 15-06-2016.
 */
public class Finish {

    public static final String FINISH_MATERIAL = "finishMaterial";
    public static final String FINISH_CODE = "finishCode";
    public static final String COLOR_GROUP_CODE = "colorGroupCode";
    public static final String TITLE = "title";

    private String finishMaterial;
    private String finishCode;
    private String colorGroupCode;
    private String title;

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
}
