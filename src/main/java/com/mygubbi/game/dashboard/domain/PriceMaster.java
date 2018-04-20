package com.mygubbi.game.dashboard.domain;

import java.sql.Date;

/**
 * Created by Chirag on 23-02-2017.
 */
public class PriceMaster {

    public static final String ALL_CITIES = "all";

    public static final String RATE_TYPE = "rateType";
    public static final String RATE_ID = "rateId";
    public static final String PRICE = "price";
    public static final String CITY = "city";
    public static final String FROM_DATE = "fromDate";
    public static final String TO_DATE = "toDate";
    public static final String SOURCE_PRICE = "sourcePrice";
    public static final String INSTALLATION_PRICE = "installationPrice";
    public static final String INSTALLATIONSOURCE_PRICE = "installationSourcePrice";

    private String rateType;
    private String rateId;
    private String city;
    private double price;
    private double sourcePrice;
    private double installationPrice;
    private double installationSourcePrice;
    private Date fromDate;
    private Date toDate;

    public String getRateType() {
        return rateType;
    }

    public void setRateType(String rateType) {
        this.rateType = rateType;
    }

    public String getRateId() {
        return rateId;
    }

    public void setRateId(String rateId) {
        this.rateId = rateId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
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

    public double getSourcePrice() {
        return sourcePrice;
    }

    public void setSourcePrice(double sourcePrice) {
        this.sourcePrice = sourcePrice;
    }

    public double getInstallationPrice() {
        return installationPrice;
    }

    public void setInstallationPrice(double installationPrice) {
        this.installationPrice = installationPrice;
    }

    public double getInstallationSourcePrice()
    {
        return  installationSourcePrice;
    }

    public void setInstallationsourcePrice(double installationSourcePrice)
    {
        this.installationSourcePrice=installationSourcePrice;
    }

    @Override
    public String toString() {
        return "PriceMaster{" +
                "rateType='" + rateType + '\'' +
                ", rateId='" + rateId + '\'' +
                ", city='" + city + '\'' +
                ", price=" + price +
                ", sourcePrice=" + sourcePrice +
                ", installationPrice=" + installationPrice +
                ", installationSourcePrice=" + installationSourcePrice +
                ", fromDate=" + fromDate +
                ", toDate=" + toDate +
                '}';
    }
}
