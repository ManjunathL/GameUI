package com.mygubbi.game.dashboard.domain;

/**
 * Created by Chirag on 19-12-2016.
 */
public class NewProductFromVersion {

    public static final String PROPOSAL_ID = "proposalId";
    public static final String FROM_VERSION = "fromVersion";
    public static final String TO_VERSION = "toVersion";

    private int proposalId;
    private float fromVersion;
    private float toVersion;

    public int getProposalId() {
        return proposalId;
    }

    public void setProposalId(int proposalId) {
        this.proposalId = proposalId;
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
