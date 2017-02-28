package com.mygubbi.game.dashboard.domain.JsonPojo;

/**
 * Created by user on 27-Feb-17.
 */
public class AccessoryDetails
{
    public static final String APCODE="apcode";
    public static final String TYPE="type";
    public static final String CODE="code";
    public static final String TITLE="title";

    private String apcode;
    private String type;
    private String code;
    private String title;

    public String getApcode() {
        return apcode;
    }

    public void setApcode(String apcode) {
        this.apcode = apcode;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "AccessoryDetails{" +
                "apcode='" + apcode + '\'' +
                ", type='" + type + '\'' +
                ", code='" + code + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
