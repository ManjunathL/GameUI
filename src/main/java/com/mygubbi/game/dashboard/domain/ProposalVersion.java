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
    public static final String INTERNAL_STATUS = "internalStatus";
    public static final String DATE = "date";
    public static final String CREATEDBY = "createdBy";
    public static final String REMARKS = "remarks";
    public static final String PRODUCTS = "products";
    public static final String DISCOUNT_AMOUNT="discountAmount";
    public static final String DISCOUNT_PERCENTAGE="discountPercentage";
    public static final String TOTAL="total";
    public static final String TOTAL_AFTER_DISCOUNT="amount";
    public static final String PROFIT="profit";
    public static final String MARGIN="margin";
    public static final String AMOUNTWOTAX="amountWotax";
    public static final String MANUFACTUREAMOUNT="manufactureAmount";
    public static final String REMARKS_IGNORE="remarksIgnore";
    public static final String IGNORE_AND_PUBLISH_FLAG="ignoreAndPublishFlag";
    public static final String UPDATED_ON="updatedOn";
    public static final String UPDATEDBY = "updatedBy";
    public static final String BUSINESS_DATE = "businessDate";
    public static final String PROJECT_HANDLING_AMOUNT = "projectHandlingAmount";
    public static final String PROJECT_HANDLING_QTY = "projectHandlingQty";
    public static final String DEEP_CLEARING_QTY = "deepClearingQty";
    public static final String DEEP_CLEARING_AMOUNT= "deepClearingAmount";
    public static final String FLOOR_PROTECTION_SQFT = "floorProtectionSqft";
    public static final String FLOOR_PROTECTION_AMOUNT= "floorProtectionAmount";
    public static final String PROJECT_HANDLING_APPLIED="projectHandlingChargesApplied";
    public static final String DEEP_CLEARING_APPLIED="deepClearingChargesApplied";
    public static final String FLOOR_PROTECTION_APPLIED="floorProtectionChargesApplied";

    private String version;
    private int proposalId;
    private int oldProposalId;
    private String title;
    private double finalAmount;
    private String status;
    private String internalStatus;
    private java.util.Date date;
    private String createdBy;
    private java.util.Date updatedOn;
    private String updatedBy;
    private String remarks;
    private String fromVersion;
    private String toVersion;
    private double discountAmount;
    private double discountPercentage;
    private double amount;
    private double profit;
    private double margin;
    private double amountWotax;
    private double manufactureAmount;
    private String ignoreAndPublishFlag;
    private String remarksIgnore;
    private String businessDate;
    private double projectHandlingAmount;
    private double deepClearingQty;
    private double deepClearingAmount;
    private double floorProtectionSqft;
    private double floorProtectionAmount;
    private String projectHandlingChargesApplied;
    private String deepClearingChargesApplied;
    private String floorProtectionChargesApplied;
    private double projectHandlingQty;
    private boolean confirmedStatus;
    private String responseMessage;

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

    public void setTitle(String title) {
        this.title = title;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(double finalAmount) {
        this.finalAmount = finalAmount;
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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInternalStatus() {
        return internalStatus;
    }

    public void setInternalStatus(String internalStatus) {
        this.internalStatus = internalStatus;
    }

    public java.util.Date getDate() {
        return date;
    }

    public void setDate(java.util.Date date) {
        this.date = date;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
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

    public int getOldProposalId() {
        return oldProposalId;
    }

    public void setOldProposalId(int oldProposalId) {
        this.oldProposalId = oldProposalId;
    }

    public double getAmountWotax() {
        return amountWotax;
    }

    public void setAmountWotax(double amountWotax) {
        this.amountWotax = amountWotax;
    }

    public double getManufactureAmount() {
        return manufactureAmount;
    }

    public void setManufactureAmount(double manufactureAmount) {
        this.manufactureAmount = manufactureAmount;
    }

    public double getMargin() {
        return margin;
    }

    public void setMargin(double margin) {
        this.margin = margin;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    public String getIgnoreAndPublishFlag() {
        return ignoreAndPublishFlag;
    }

    public void setIgnoreAndPublishFlag(String ignoreAndPublishFlag) {
        this.ignoreAndPublishFlag = ignoreAndPublishFlag;
    }

    public String getRemarksIgnore() {
        return remarksIgnore;
    }

    public void setRemarksIgnore(String remarksIgnore) {
        this.remarksIgnore = remarksIgnore;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getBusinessDate() {
        return businessDate;
    }

    public void setBusinessDate(String businessDate) {
        this.businessDate = businessDate;
    }

    public double getProjectHandlingAmount() {
        return projectHandlingAmount;
    }

    public void setProjectHandlingAmount(double projectHandlingAmount) {
        this.projectHandlingAmount = projectHandlingAmount;
    }

    public double getDeepClearingQty() {
        return deepClearingQty;
    }

    public void setDeepClearingQty(double deepClearingQty) {
        this.deepClearingQty = deepClearingQty;
    }

    public double getFloorProtectionSqft() {
        return floorProtectionSqft;
    }

    public void setFloorProtectionSqft(double floorProtectionSqft) {
        this.floorProtectionSqft = floorProtectionSqft;
    }

    public double getFloorProtectionAmount() {
        return floorProtectionAmount;
    }

    public void setFloorProtectionAmount(double floorProtectionAmount) {
        this.floorProtectionAmount = floorProtectionAmount;
    }

    public double getDeepClearingAmount() {
        return deepClearingAmount;
    }

    public void setDeepClearingAmount(double deepClearingAmount) {
        this.deepClearingAmount = deepClearingAmount;
    }

    public String getProjectHandlingChargesApplied() {
        return projectHandlingChargesApplied;
    }

    public void setProjectHandlingChargesApplied(String projectHandlingChargesApplied) {
        this.projectHandlingChargesApplied = projectHandlingChargesApplied;
    }

    public String getDeepClearingChargesApplied() {
        return deepClearingChargesApplied;
    }

    public void setDeepClearingChargesApplied(String deepClearingChargesApplied) {
        this.deepClearingChargesApplied = deepClearingChargesApplied;
    }

    public String getFloorProtectionChargesApplied() {
        return floorProtectionChargesApplied;
    }

    public void setFloorProtectionChargesApplied(String floorProtectionChargesApplied) {
        this.floorProtectionChargesApplied = floorProtectionChargesApplied;
    }

    public double getProjectHandlingQty() {
        return projectHandlingQty;
    }

    public void setProjectHandlingQty(double projectHandlingQty) {
        this.projectHandlingQty = projectHandlingQty;
    }

    public boolean isConfirmedStatus() {
        return confirmedStatus;
    }

    public void setConfirmedStatus(boolean confirmedStatus) {
        this.confirmedStatus = confirmedStatus;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String sowResponseMessage) {
        this.responseMessage = sowResponseMessage;
    }

    @Override
    public String toString() {
        return "ProposalVersion{" +
                "version='" + version + '\'' +
                ", proposalId=" + proposalId +
                ", oldProposalId=" + oldProposalId +
                ", title='" + title + '\'' +
                ", finalAmount=" + finalAmount +
                ", status='" + status + '\'' +
                ", internalStatus='" + internalStatus + '\'' +
                ", date=" + date +
                ", createdBy='" + createdBy + '\'' +
                ", updatedOn=" + updatedOn +
                ", updatedBy='" + updatedBy + '\'' +
                ", remarks='" + remarks + '\'' +
                ", fromVersion='" + fromVersion + '\'' +
                ", toVersion='" + toVersion + '\'' +
                ", discountAmount=" + discountAmount +
                ", discountPercentage=" + discountPercentage +
                ", amount=" + amount +
                ", profit=" + profit +
                ", margin=" + margin +
                ", amountWotax=" + amountWotax +
                ", manufactureAmount=" + manufactureAmount +
                ", ignoreAndPublishFlag='" + ignoreAndPublishFlag + '\'' +
                ", remarksIgnore='" + remarksIgnore + '\'' +
                ", businessDate='" + businessDate + '\'' +
                ", projectHandlingAmount=" + projectHandlingAmount +
                ", deepClearingQty=" + deepClearingQty +
                ", deepClearingAmount=" + deepClearingAmount +
                ", floorProtectionSqft=" + floorProtectionSqft +
                ", floorProtectionAmount=" + floorProtectionAmount +
                ", projectHandlingChargesApplied='" + projectHandlingChargesApplied + '\'' +
                ", deepClearingChargesApplied='" + deepClearingChargesApplied + '\'' +
                ", floorProtectionChargesApplied='" + floorProtectionChargesApplied + '\'' +
                ", projectHandlingQty=" + projectHandlingQty +
                '}';
    }
}


