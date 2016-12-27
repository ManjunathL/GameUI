package com.mygubbi.game.dashboard.domain;

import java.util.Date;

/**
 * Created by Chirag on 06-12-2016.
 */
public class ProposalVersion implements Cloneable {

    public enum ProposalStage {draft, published, confirmed, locked, DSO, PSO}

    public static final String VERSION = "version";
    public static final String FROM_VERSION = "fromVersion";
    public static final String PROPOSAL_ID = "proposalId";
    public static final String TITLE = "title";
    public static final String FINAL_AMOUNT = "finalAmount";
    public static final String STATUS = "status";
    public static final String DATE = "date";
    public static final String REMARKS = "remarks";
    public static final String PRODUCTS = "products";

    private float version;
    private int proposalId;
    private String title;
    private String finalAmount;
    private String status;
    private Date date;
    private String remarks;
    private float fromVersion;
    private float toVersion;

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

        if (Float.compare(that.version, version) != 0) return false;
        return proposalId == that.proposalId;

    }

    @Override
    public int hashCode() {
        int result = (version != +0.0f ? Float.floatToIntBits(version) : 0);
        result = 31 * result + proposalId;
        return result;
    }

    @Override
    public String toString() {
        return "ProposalVersion{" +
                "version=" + version +
                ", proposalId=" + proposalId +
                ", title='" + title + '\'' +
                ", finalAmount='" + finalAmount + '\'' +
                ", status='" + status + '\'' +
                ", date=" + date +
                ", remarks='" + remarks + '\'' +
                ", fromVersion=" + fromVersion +
                ", toVersion=" + toVersion +
                '}';
    }

    public float getVersion() {
        return version;
    }

    public void setVersion(float version) {
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

    public float getFromVersion() {
        return fromVersion;
    }

    public void setFromVersion(float fromVersion) {
        this.fromVersion = fromVersion;
    }

    public float getToVersion() {
        return toVersion;
    }

    public void setToVersion(float toVersion) {
        this.toVersion = toVersion;
    }
}


