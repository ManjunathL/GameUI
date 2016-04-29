package com.mygubbi.game.dashboard.domain;

import java.util.Date;

/**
 * Created by test on 28-04-2016.
 */
public class ProposalListItem {


    private String proposalId;
    private String crmId;
    private String title;
    private Date creationDate;
    private String status;
    private Integer amount;
    private String lastUpdatedBy;
    private String completionDate;
    private String sales;
    private String designer;
    private String city;

    public ProposalListItem() {
    }

    public ProposalListItem(String proposalId, String crmId, String title, Date creationDate, String status, String lastUpdatedBy, Integer amount, String completionDate, String sales, String designer, String city) {
        this.proposalId = proposalId;
        this.crmId = crmId;
        this.title = title;
        this.creationDate = creationDate;
        this.status = status;
        this.lastUpdatedBy = lastUpdatedBy;
        this.amount = amount;
        this.completionDate = completionDate;
        this.sales = sales;
        this.designer = designer;
        this.city = city;

    }

    public String getProposalId() {
        return proposalId;
    }

    public void setProposalId(String proposalId) {
        this.proposalId = proposalId;
    }

    public String getCrmId() {
        return crmId;
    }

    public void setCrmId(String crmId) {
        this.crmId = crmId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public String getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(String completionDate) {
        this.completionDate = completionDate;
    }

    public String getDesigner() {
        return designer;
    }

    public void setDesigner(String design) {
        this.designer = design;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSales() {
        return sales;
    }

    public void setSales(String sales) {
        this.sales = sales;
    }
}
