package com.mygubbi.game.dashboard.domain.JsonPojo;

public class LookupItem {

    public static final String CODE = "code";
    public static final String TITLE = "title";
    private String code;
    private String title;
    private String additionalType;


    public LookupItem() {
    }

    public LookupItem(String code, String title, String additionalType) {
        this.code = code;
        this.title = title;
        this.additionalType = additionalType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LookupItem that = (LookupItem) o;

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
}
