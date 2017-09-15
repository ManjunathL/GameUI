package com.mygubbi.game.dashboard.domain;

/**
 * Created by Shruthi on 9/14/2017.
 */
public class ColourGroupCodeMap
{
    public static final String CODE = "code";
    public static final String TITLE = "title";

    private String code;
    private String title;

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

    @Override
    public String toString() {
        return "ColourGroupCodeMap{" +
                "code='" + code + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
