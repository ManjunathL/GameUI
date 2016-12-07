package com.mygubbi.game.dashboard.domain;

/**
 * Created by Chirag on 06-12-2016.
 */
public class Versioning implements Cloneable {

    public static final String VERSION = "version";
    public static final String TITLE = "title";
    public static final String FINAL_AMOUNT = "finalAmount";
    public static final String STATUS = "status";
    public static final String DATE = "date";
    public static final String REMARKS = "remarks";

    private double version;
    private String title;
    private String finalAmount;
    private String status;
    private String date;
    private String remarks;

    public double getVersion() {
        return version;
    }

    public void setVersion(double version) {
        this.version = version;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(String finalAmount) {
        this.finalAmount = finalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
