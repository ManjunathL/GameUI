package com.mygubbi.game.dashboard.domain;

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

    private String finishMaterial;
    private String finishCode;
    private String colorGroupCode;
    private String title;
    private String shutterCode;
    private String imagePath;

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

    @Override
    public String toString() {
        return "Finish{" +
                "colorGroupCode='" + colorGroupCode + '\'' +
                ", finishMaterial='" + finishMaterial + '\'' +
                ", finishCode='" + finishCode + '\'' +
                ", title='" + title + '\'' +
                ", shutterCode='" + shutterCode + '\'' +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
}
