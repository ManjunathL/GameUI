package com.mygubbi.game.dashboard.domain;

import java.util.Date;

/**
 * Created by nitinpuri on 01-05-2016.
 */
public class ProposalHeader {

    private int proposalId;
    private String proposalTitle;
    private String proposalVersion;
    private String crmId;
    private String quotationNo;
    private double amount;
    private Date createDate;
    private String createdBy;
    private String status;

    private String customerId;
    private String customerName;
    private String customerAddressLine1;
    private String customerAddressLine2;
    private String customerAddressLine3;
    private String customerCity;
    private String customerEmail;
    private String customerPhone1;
    private String customerPhone2;

    private String projectName;
    private String projectAddressLine1;
    private String projectAddressLine2;
    private String projectCity;

    private String salesContactName;
    private String salesContactEmail;
    private String salesContactPhone;

    private String designContactName;
    private String designContactEmail;
    private String designContactPhone;

    public int getProposalId() {
        return proposalId;
    }

    public void setProposalId(int proposalId) {
        this.proposalId = proposalId;
    }

    public String getProposalTitle() {
        return proposalTitle;
    }

    public void setProposalTitle(String proposalTitle) {
        this.proposalTitle = proposalTitle;
    }

    public String getProposalVersion() {
        return proposalVersion;
    }

    public void setProposalVersion(String proposalVersion) {
        this.proposalVersion = proposalVersion;
    }

    public String getCrmId() {
        return crmId;
    }

    public void setCrmId(String crmId) {
        this.crmId = crmId;
    }

    public String getQuotationNo() {
        return quotationNo;
    }

    public void setQuotationNo(String quotationNo) {
        this.quotationNo = quotationNo;
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

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerAddressLine1() {
        return customerAddressLine1;
    }

    public void setCustomerAddressLine1(String customerAddressLine1) {
        this.customerAddressLine1 = customerAddressLine1;
    }

    public String getCustomerAddressLine2() {
        return customerAddressLine2;
    }

    public void setCustomerAddressLine2(String customerAddressLine2) {
        this.customerAddressLine2 = customerAddressLine2;
    }

    public String getCustomerAddressLine3() {
        return customerAddressLine3;
    }

    public void setCustomerAddressLine3(String customerAddressLine3) {
        this.customerAddressLine3 = customerAddressLine3;
    }

    public String getCustomerCity() {
        return customerCity;
    }

    public void setCustomerCity(String customerCity) {
        this.customerCity = customerCity;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone1() {
        return customerPhone1;
    }

    public void setCustomerPhone1(String customerPhone1) {
        this.customerPhone1 = customerPhone1;
    }

    public String getCustomerPhone2() {
        return customerPhone2;
    }

    public void setCustomerPhone2(String customerPhone2) {
        this.customerPhone2 = customerPhone2;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectAddressLine1() {
        return projectAddressLine1;
    }

    public void setProjectAddressLine1(String projectAddressLine1) {
        this.projectAddressLine1 = projectAddressLine1;
    }

    public String getProjectAddressLine2() {
        return projectAddressLine2;
    }

    public void setProjectAddressLine2(String projectAddressLine2) {
        this.projectAddressLine2 = projectAddressLine2;
    }

    public String getProjectCity() {
        return projectCity;
    }

    public void setProjectCity(String projectCity) {
        this.projectCity = projectCity;
    }

    public String getSalesContactName() {
        return salesContactName;
    }

    public void setSalesContactName(String salesContactName) {
        this.salesContactName = salesContactName;
    }

    public String getSalesContactEmail() {
        return salesContactEmail;
    }

    public void setSalesContactEmail(String salesContactEmail) {
        this.salesContactEmail = salesContactEmail;
    }

    public String getSalesContactPhone() {
        return salesContactPhone;
    }

    public void setSalesContactPhone(String salesContactPhone) {
        this.salesContactPhone = salesContactPhone;
    }

    public String getDesignContactName() {
        return designContactName;
    }

    public void setDesignContactName(String designContactName) {
        this.designContactName = designContactName;
    }

    public String getDesignContactEmail() {
        return designContactEmail;
    }

    public void setDesignContactEmail(String designContactEmail) {
        this.designContactEmail = designContactEmail;
    }

    public String getDesignContactPhone() {
        return designContactPhone;
    }

    public void setDesignContactPhone(String designContactPhone) {
        this.designContactPhone = designContactPhone;
    }
}
