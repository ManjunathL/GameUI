package com.mygubbi.game.dashboard.domain;

import java.sql.Date;

/**
 * Created by Shruthi on 8/15/2017.
 */
public class Offer {
    public static final String ID = "id";
    public static final String OFFER_NAME = "offerName";
    public static final String FROM_DATE = "fromDate";
    public static final String TO_DATE = "toDate";
    public static final String CATEGORY = "category";
    public static final String TITLE = "title";
    public static final String MINIMUM_ORDER_VALUE = "minimumOrderValue";

    private int id;
    private String offerName;
    private Date fromDate;
    private Date toDate;
    private String category;
    private String title;
    private double minimumOrderValue;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOfferName() {
        return offerName;
    }

    public void setOfferName(String offerName) {
        this.offerName = offerName;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getMinimumOrderValue() {
        return minimumOrderValue;
    }

    public void setMinimumOrderValue(double minimumOrderValue) {
        this.minimumOrderValue = minimumOrderValue;
    }

    @Override
    public String toString() {
        return "Offer{" +
                "id=" + id +
                ", offerName='" + offerName + '\'' +
                ", fromDate=" + fromDate +
                ", toDate=" + toDate +
                ", category='" + category + '\'' +
                ", title='" + title + '\'' +
                ", minimumOrderValue=" + minimumOrderValue +
                '}';
    }
}
