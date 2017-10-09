package com.mygubbi.game.dashboard.domain;

/**
 * Created by Shruthi on 10/6/2017.
 */
public class VersionPriceHolder
{
    private String proposalId;
    private String version;
    private double vrPrice;
    private double vrPriceAfterDiscount;
    private double vrPriceWoTax;
    private double vrCost;
    private double vrProfit;
    private double vrMargin;
    private double hikePrice;
    private double prPrice;
    private double prPriceAfterDiscount;
    private double prPriceWoTax;
    private double prCost;
    private double prProfit;
    private double prMargin;
    private double addonPrice;
    private double addonPriceAfterDiscount;
    private double addonPriceWoTax;
    private double addonCost;
    private double addonProfit;
    private double addonMargin;
    private double costWoAccessories;

    public double getVrPrice() {
        return vrPrice;
    }

    public void setVrPrice(double vrPrice) {
        this.vrPrice = vrPrice;
    }

    public double getVrPriceAfterDiscount() {
        return vrPriceAfterDiscount;
    }

    public void setVrPriceAfterDiscount(double vrPriceAfterDiscount) {
        this.vrPriceAfterDiscount = vrPriceAfterDiscount;
    }

    public double getVrPriceWoTax() {
        return vrPriceWoTax;
    }

    public void setVrPriceWoTax(double vrPriceWoTax) {
        this.vrPriceWoTax = vrPriceWoTax;
    }

    public double getVrCost() {
        return vrCost;
    }

    public void setVrCost(double vrCost) {
        this.vrCost = vrCost;
    }

    public double getVrProfit() {
        return vrProfit;
    }

    public void setVrProfit(double vrProfit) {
        this.vrProfit = vrProfit;
    }

    public double getVrMargin() {
        return vrMargin;
    }

    public void setVrMargin(double vrMargin) {
        this.vrMargin = vrMargin;
    }

    public double getHikePrice() {
        return hikePrice;
    }

    public void setHikePrice(double hikePrice) {
        this.hikePrice = hikePrice;
    }

    public double getPrPrice() {
        return prPrice;
    }

    public void setPrPrice(double prPrice) {
        this.prPrice = prPrice;
    }

    public double getPrPriceAfterDiscount() {
        return prPriceAfterDiscount;
    }

    public void setPrPriceAfterDiscount(double prPriceAfterDiscount) {
        this.prPriceAfterDiscount = prPriceAfterDiscount;
    }

    public double getPrPriceWoTax() {
        return prPriceWoTax;
    }

    public void setPrPriceWoTax(double prPriceWoTax) {
        this.prPriceWoTax = prPriceWoTax;
    }

    public double getPrCost() {
        return prCost;
    }

    public void setPrCost(double prCost) {
        this.prCost = prCost;
    }

    public double getPrProfit() {
        return prProfit;
    }

    public void setPrProfit(double prProfit) {
        this.prProfit = prProfit;
    }

    public double getPrMargin() {
        return prMargin;
    }

    public void setPrMargin(double prMargin) {
        this.prMargin = prMargin;
    }

    public double getAddonPrice() {
        return addonPrice;
    }

    public void setAddonPrice(double addonPrice) {
        this.addonPrice = addonPrice;
    }

    public double getAddonPriceAfterDiscount() {
        return addonPriceAfterDiscount;
    }

    public void setAddonPriceAfterDiscount(double addonPriceAfterDiscount) {
        this.addonPriceAfterDiscount = addonPriceAfterDiscount;
    }

    public double getAddonPriceWoTax() {
        return addonPriceWoTax;
    }

    public void setAddonPriceWoTax(double addonPriceWoTax) {
        this.addonPriceWoTax = addonPriceWoTax;
    }

    public double getAddonCost() {
        return addonCost;
    }

    public void setAddonCost(double addonCost) {
        this.addonCost = addonCost;
    }

    public double getAddonProfit() {
        return addonProfit;
    }

    public void setAddonProfit(double addonProfit) {
        this.addonProfit = addonProfit;
    }

    public double getAddonMargin() {
        return addonMargin;
    }

    public void setAddonMargin(double addonMargin) {
        this.addonMargin = addonMargin;
    }

    public double getCostWoAccessories() {
        return costWoAccessories;
    }

    public void setCostWoAccessories(double costWoAccessories) {
        this.costWoAccessories = costWoAccessories;
    }

    @Override
    public String toString() {
        return "VersionPriceHolder{" +
                "proposalId='" + proposalId + '\'' +
                ", version='" + version + '\'' +
                ", vrPrice=" + vrPrice +
                ", vrPriceAfterDiscount=" + vrPriceAfterDiscount +
                ", vrPriceWoTax=" + vrPriceWoTax +
                ", vrCost=" + vrCost +
                ", vrProfit=" + vrProfit +
                ", vrMargin=" + vrMargin +
                ", hikePrice=" + hikePrice +
                ", prPrice=" + prPrice +
                ", prPriceAfterDiscount=" + prPriceAfterDiscount +
                ", prPriceWoTax=" + prPriceWoTax +
                ", prCost=" + prCost +
                ", prProfit=" + prProfit +
                ", prMargin=" + prMargin +
                ", addonPrice=" + addonPrice +
                ", addonPriceAfterDiscount=" + addonPriceAfterDiscount +
                ", addonPriceWoTax=" + addonPriceWoTax +
                ", addonCost=" + addonCost +
                ", addonProfit=" + addonProfit +
                ", addonMargin=" + addonMargin +
                ", costWoAccessories=" + costWoAccessories +
                '}';
    }
}
