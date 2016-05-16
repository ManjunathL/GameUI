package com.mygubbi.game.dashboard.data;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mygubbi.game.dashboard.data.dummy.FileDataProviderUtil;
import com.mygubbi.game.dashboard.domain.ProductItem;
import com.mygubbi.game.dashboard.domain.ProductSuggest;
import com.mygubbi.game.dashboard.domain.Proposal;
import com.mygubbi.game.dashboard.domain.JsonPojo.SimpleComboItem;

import com.mygubbi.game.dashboard.domain.ProposalHeader;
import us.monoid.json.JSONArray;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;

/**
 * Created by nitinpuri on 05-04-2016.
 */
public class ProposalDataProvider {

    private DataProviderUtil dataProviderUtil;
    private ObjectMapper mapper;

    public ProposalDataProvider(DataProviderUtil dataProviderUtil) {
        this.dataProviderUtil = dataProviderUtil;
        this.mapper = new ObjectMapper();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.mapper.setDateFormat(df);
    }

    public List<ProposalHeader> getProposalHeaders(String proposalClass) {
        JSONArray proposalHeaders = dataProviderUtil.getResourceArray("proposal_headers", new HashMap<String, String>(){
            {
                put("class", proposalClass);
            }
        });

        List<ProposalHeader> proposalHeaderList = new ArrayList<>();
        for (int i = 0; i < proposalHeaders.length(); i++) {
            try {
                proposalHeaderList.add(this.mapper.readValue(proposalHeaders.getJSONObject(i).toString(), ProposalHeader.class));
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                throw new RuntimeException("Couldn't fetch Proposals", e);
            }
        }

        return proposalHeaderList;
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

    public Proposal createProposal() {

        try {
            JSONObject jsonObject = dataProviderUtil.postResource("create_proposal", "");
            return this.mapper.readValue(jsonObject.toString(), Proposal.class);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't create proposal", e);
        }
    }

    public boolean saveProposal(Proposal proposal) {

        try {
            String proposalJson = this.mapper.writeValueAsString(proposal);
            JSONObject jsonObject = dataProviderUtil.postResource("save_proposal", proposalJson);
            return jsonObject.has("status") && jsonObject.getString("status").equals("success");
        } catch (JSONException | JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Couldn't save proposal", e);
        }
    }
    
    public boolean submitProposal(int proposalId) {

        try {
            JSONObject jsonObject = dataProviderUtil.postResource("save_proposal", "\"proposalId\": " + proposalId);
            return jsonObject.has("status") && jsonObject.getString("status").equals("success");
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Couldn't save proposal", e);
        }
    }

    public boolean deleteProposal(int proposalId) {

        try {
            JSONObject jsonObject = dataProviderUtil.postResource("delete_proposal", "\"proposalId\": " + proposalId);
            return jsonObject.has("status") && jsonObject.getString("status").equals("success");
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Couldn't delete proposal", e);
        }
    }

    /**
     * Product Section
     */
    
    public List<SimpleComboItem> getComboItems(String type)
    {
    	JSONArray array = dataProviderUtil.getResourceArray(type, new HashMap<String, String>());
    	try
    	{
    		SimpleComboItem [] items = this.mapper.readValue(array.toString(), SimpleComboItem[].class);
    		return Arrays.asList(items);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		return new ArrayList<SimpleComboItem>();
    	}
    }
    
    public List<ProductSuggest> getProductSuggestions(String inputTerm)
    {
    	JSONArray array = dataProviderUtil.getResourceArray("product_auto_complete", new HashMap<String, String>());
    	try
    	{
    		ProductSuggest [] items = this.mapper.readValue(array.toString(), ProductSuggest[].class);
    		return Arrays.asList(items);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		return new ArrayList<ProductSuggest>();
    	}
    }
    
    public List<ProductSuggest> getProductSearchResults(String inputTerm)
    {
    	JSONArray array = dataProviderUtil.getResourceArray("product_search_data", new HashMap<String, String>());
    	try
    	{
    		ProductSuggest [] items = this.mapper.readValue(array.toString(), ProductSuggest[].class);
    		return Arrays.asList(items);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		return new ArrayList<ProductSuggest>();
    	}
    }
    
    public ProductItem getProduct(int productId)
    {
    	JSONArray array = dataProviderUtil.getResourceArray("product_search_data", new HashMap<String, String>());
    	try
    	{
    		ProductItem [] items = this.mapper.readValue(array.toString(), ProductItem[].class);
    		if ( items.length == 0) return null;
    		return Arrays.asList(items).get(0);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		return null;
    	}
    }
    
    public List<String> getAddonTypes()
    {
    	JSONObject obj = dataProviderUtil.getResource("addon_type", new HashMap<String, String>());
    	try
    	{
    		String typeStr = obj.getJSONArray("addon_type").toString();
    		String [] typeL = this.mapper.readValue(typeStr, String[].class);
    		return Arrays.asList(typeL);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		return new ArrayList<String>();
    	}
    }

    public static void main(String[] args) {
        ProposalDataProvider proposalDataProvider = new ProposalDataProvider(new FileDataProviderUtil());
        Proposal proposal = proposalDataProvider.createProposal();

        try {
            proposalDataProvider.mapper.writeValue(System.out, proposal);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
