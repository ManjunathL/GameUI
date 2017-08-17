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

    private int id;
    private String offerName;
    private Date fromDate;
    private Date toDate;

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

    @Override
    public String toString() {
        return "Offer{" +
                "id=" + id +
                ", offerName='" + offerName + '\'' +
                ", fromDate=" + fromDate +
                ", toDate=" + toDate +
                '}';
    }
}
