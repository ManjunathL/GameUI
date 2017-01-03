package com.mygubbi.game.dashboard.domain;

import java.util.Date;

/**
 * Created by Chirag on 06-12-2016.
 */
public class ProposalVersion implements Cloneable {

    public enum ProposalStage {Draft, Published, Confirmed, Locked, DSO, PSO}

    public static final String VERSION = "version";
    public static final String FROM_VERSION = "fromVersion";
    public static final String PROPOSAL_ID = "proposalId";
    public static final String TITLE = "title";
    public static final String FINAL_AMOUNT = "finalAmount";
    public static final String STATUS = "status";
    public static final String DATE = "date";
    public static final String REMARKS = "remarks";
    public static final String PRODUCTS = "products";
    public static final String DISCOUNT_AMOUNT="discountAmount";
    public static final String DISCOUNT_PERCENTAGE="discountPercentage";
    public static final String TOTAL="total";
    public static final String TOTAL_AFTER_DISCOUNT="amount";

    private String version;
    private int proposalId;
    private String title;
    private String finalAmount;
    private String status;
    private Date date;
    private String remarks;
    private String fromVersion;
    private String toVersion;
    private String discountAmount;
    private String discountPercentage;
    private String amount;

    @Override
    public Object clone()  {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
            //Will never happen since we are implementing Cloneable
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProposalVersion that = (ProposalVersion) o;

        if (proposalId != that.proposalId) return false;
        return version.equals(that.version);

    }

    @Override
    public int hashCode() {
        int result = version.hashCode();
        result = 31 * result + proposalId;
        return result;
    }

    @Override
    public String toString() {
        return "ProposalVersion{" +
                "version='" + version + '\'' +
                ", proposalId=" + proposalId +
                ", title='" + title + '\'' +
                ", finalAmount='" + finalAmount + '\'' +
                ", status='" + status + '\'' +
                ", date=" + date +
                ", remarks='" + remarks + '\'' +
                ", fromVersion='" + fromVersion + '\'' +
                ", toVersion='" + toVersion + '\'' +
                ", discountAmount='" + discountAmount + '\'' +
                ", discountPercentage='" + discountPercentage + '\'' +
                ", amount='" + amount + '\'' +
                '}';
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getProposalId() {
        return proposalId;
    }

    public void setProposalId(int proposalId) {
        this.proposalId = proposalId;
    }

    public String getTitle() {
        return title;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDiscountAmount() {

        return discountAmount;
    }

    public void setDiscountAmount(String discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(String discountPercentage) {
        this.discountPercentage = discountPercentage;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getFromVersion() {
        return fromVersion;
    }

    public void setFromVersion(String fromVersion) {
        this.fromVersion = fromVersion;
    }

    public String getToVersion() {
        return toVersion;
    }

    public void setToVersion(String toVersion) {
        this.toVersion = toVersion;
    }
}


