package com.mygubbi.game.dashboard.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nitinpuri on 02-06-2016.
 */
public class ProductAndAddonSelection {
    private int proposalId;
    private List<Integer> productIds = new ArrayList<>();
    private List<Integer> addonIds = new ArrayList<>();
    private double discountAmount;
    private double discountPercentage;
    private String fromVersion;
    private String userId;
    private String bookingFormFlag;
    private String worksContractFlag;
    private String city;

    public String getBookingFormFlag() {
        return bookingFormFlag;
    }

    public void setBookingFormFlag(String bookingFormFlag) {
        this.bookingFormFlag = bookingFormFlag;
    }

    public String getUserId() {return userId;}

    public void setUserId(String userId) {this.userId = userId;}

    public String getFromVersion() {
        return fromVersion;
    }

    public void setFromVersion(String fromVersion) {
        this.fromVersion = fromVersion;
    }

    public int getProposalId() {
        return proposalId;
    }

    public void setProposalId(int proposalId) {
        this.proposalId = proposalId;
    }

    public List<Integer> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<Integer> productIds) {
        this.productIds = productIds;
    }

    public List<Integer> getAddonIds() {
        return addonIds;
    }

    public void setAddonIds(List<Integer> addonIds) {
        this.addonIds = addonIds;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getWorksContractFlag() {
        return worksContractFlag;
    }

    public void setWorksContractFlag(String worksContractFlag) {
        this.worksContractFlag = worksContractFlag;
    }

    @Override
    public String toString() {
        return "ProductAndAddonSelection{" +
                "proposalId=" + proposalId +
                ", productIds=" + productIds +
                ", addonIds=" + addonIds +
                ", discountAmount=" + discountAmount +
                ", discountPercentage=" + discountPercentage +
                ", fromVersion='" + fromVersion + '\'' +
                ", userId='" + userId + '\'' +
                ", bookingFormFlag='" + bookingFormFlag + '\'' +
                ", worksContractFlag='" + worksContractFlag + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}
