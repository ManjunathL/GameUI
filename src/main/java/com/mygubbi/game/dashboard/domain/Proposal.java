package com.mygubbi.game.dashboard.domain;

import java.util.Date;

/**
 * Created by test on 30-03-2016.
 */
public final class Proposal {

    private String name;
    private String proposal;
    private Date create_date;
    private String status;
    private String last_assigned_to;
    private String customer_name;
    private Number amount;

    public Proposal() {
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProposal() {
        return proposal;
    }

    public void setProposal(String proposal) {
        this.proposal = proposal;
    }

    public Date getCreate_date() {
        return create_date;
    }

    public void setCreate_date(Date create_date) {
        this.create_date = create_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLast_assigned_to() {
        return last_assigned_to;
    }

    public void setLast_assigned_to(String last_assigned_to) {
        this.last_assigned_to = last_assigned_to;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public Number getAmount() {
        return amount;
    }

    public void setAmount(Number amount) {
        this.amount = amount;
    }
}
