package com.mygubbi.game.dashboard.domain;

import java.sql.Date;

/**
 * Created by Shruthi on 8/15/2017.
 */
public class OfferMaster {
    public static final String ID = "id";
    public static final String OFFER_NAME = "offerName";
    public static final String FROM_DATE = "fromDate";
    public static final String TO_DATE = "toDate";
    public static final String OFFER_CODE = "offerCode";


    private int id;
    private String offerName;
    private Date fromDate;
    private Date toDate;
    private String offerCode;

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

    public String getOfferCode() {
        return offerCode;
    }

    public void setOfferCode(String offerCode) {
        this.offerCode = offerCode;
    }

    @Override
    public String toString() {
        return "OfferMaster{" +
                "id=" + id +
                ", offerName='" + offerName + '\'' +
                ", fromDate=" + fromDate +
                ", toDate=" + toDate +
                ", offerCode=" + offerCode +
                '}';
    }
}
