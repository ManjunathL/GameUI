package com.mygubbi.game.dashboard.data;

import com.mygubbi.game.dashboard.data.dummy.FileDataProviderUtil;
import us.monoid.json.JSONArray;
import us.monoid.json.JSONObject;

import java.util.HashMap;

/**
 * Created by nitinpuri on 05-04-2016.
 */
public class ProposalDataProvider {

    private DataProviderUtil dataProviderUtil;

    public ProposalDataProvider(DataProviderUtil dataProviderUtil) {
        this.dataProviderUtil = dataProviderUtil;
    }

    public JSONArray getProposals(String proposalClass) {
        return dataProviderUtil.getResourceArray("proposals", new HashMap<String, String>(){
            {
                put("class", proposalClass);
            }
        });
    }

    public JSONObject getProposal(String proposalId) {
        return dataProviderUtil.getResource("proposal", new HashMap<String, String>(){
            {
                put("proposalId", proposalId);
            }
        });
    }

    public JSONArray getProposalComments(String proposalId) {
        return dataProviderUtil.getResourceArray("proposal_comments", new HashMap<String, String>(){
            {
                put("proposalId", proposalId);
            }
        });
    }

    public JSONArray getProposalHistory(String proposalId) {
        return dataProviderUtil.getResourceArray("proposal_history", new HashMap<String, String>(){
            {
                put("proposalId", proposalId);
            }
        });
    }

    public JSONArray getProposalLineItems(String proposalId) {
        return dataProviderUtil.getResourceArray("proposal_line_items", new HashMap<String, String>(){
            {
                put("proposalId", proposalId);
            }
        });
    }

    public JSONArray getProposalClasses() {
        return dataProviderUtil.getResourceArray("proposal_classes", new HashMap<String, String>());
    }

    public static void main(String[] args) {
        ProposalDataProvider proposalDataProvider = new ProposalDataProvider(new FileDataProviderUtil());
        JSONArray jsonArray = proposalDataProvider.getProposals("active");
        System.out.println(jsonArray.toString());
    }
}
