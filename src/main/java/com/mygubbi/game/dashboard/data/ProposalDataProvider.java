package com.mygubbi.game.dashboard.data;

import com.mygubbi.game.dashboard.data.dummy.FileDataProviderUtil;
import us.monoid.json.JSONArray;
import us.monoid.json.JSONObject;
import us.monoid.web.JSONResource;

import java.util.HashMap;

/**
 * Created by nitinpuri on 05-04-2016.
 */
public class ProposalDataProvider {

    private DataProviderUtil dataProviderUtil = new FileDataProviderUtil();

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

    public static void main(String[] args) {
        JSONArray jsonArray = new ProposalDataProvider().getProposals("active");
        System.out.println(jsonArray.toString());
    }
}
