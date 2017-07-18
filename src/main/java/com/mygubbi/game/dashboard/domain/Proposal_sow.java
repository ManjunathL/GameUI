package com.mygubbi.game.dashboard.domain;

import us.monoid.json.JSONObject;

/**
 * Created by User on 10-07-2017.
 */
public class Proposal_sow {

    public int proposalId;
    public String id;
    public String version;



    public int getProposalId() {
        return proposalId;
    }

    public void setProposalId(int proposalId) {
        this.proposalId = proposalId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
