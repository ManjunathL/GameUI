package com.mygubbi.game.dashboard.domain;

/**
 * Created by Shruthi on 12/13/2017.
 */
public class ProposalServices
{
    public static final String PROPOSAL_ID="proposalId";
    public static final String FROM_VERSION="fromVersion";
    public static final String SERVICE_TITLE="serviceTitle";
    public static final String QUANTITY="quantity";
    public static final String AMOUNT="amount";

    private int proposalId;
    private String fromVersion;
    private String serviceTitle;
    private int quantity;
    private double amount;

    public int getProposalId() {
        return proposalId;
    }

    public void setProposalId(int proposalId) {
        this.proposalId = proposalId;
    }

    public String getFromVersion() {
        return fromVersion;
    }

    public void setFromVersion(String fromVersion) {
        this.fromVersion = fromVersion;
    }

    public String getServiceTitle() {
        return serviceTitle;
    }

    public void setServiceTitle(String serviceTitle) {
        this.serviceTitle = serviceTitle;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
