package com.mygubbi.game.dashboard.domain;

import java.util.Date;
import java.util.stream.Stream;

/**
 * Created by test on 28-04-2016.
 */
public class ProposalListItem {


    private String proposal_id;
    private String crm_id;
    private String title;
    private Date creation_date;
    private String status;
    private Integer amount;
    private String last_updated_by;
    private String completion_date;
    private String sales;
    private String design;
    private String city;

    public ProposalListItem(String proposal_id, String crm_id, String title, Date creation_date, String status, String last_updated_by, Integer amount, String completion_date, String sales, String design, String city) {
        this.proposal_id = proposal_id;
        this.crm_id = crm_id;
        this.title = title;
        this.creation_date = creation_date;
        this.status = status;
        this.last_updated_by = last_updated_by;
        this.amount = amount;
        this.completion_date = completion_date;
        this.sales = sales;
        this.design = design;
        this.city = city;

    }

    public String getProposal_id() {
        return proposal_id;
    }

    public void setProposal_id(String proposal_id) {
        this.proposal_id = proposal_id;
    }

    public String getCrm_id() {
        return crm_id;
    }

    public void setCrm_id(String crm_id) {
        this.crm_id = crm_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(Date creation_date) {
        this.creation_date = creation_date;
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

    public String getLast_updated_by() {
        return last_updated_by;
    }

    public void setLast_updated_by(String last_updated_by) {
        this.last_updated_by = last_updated_by;
    }

    public String getCompletion_date() {
        return completion_date;
    }

    public void setCompletion_date(String completion_date) {
        this.completion_date = completion_date;
    }

    public String getDesign() {
        return design;
    }

    public void setDesign(String design) {
        this.design = design;
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
