package com.mygubbi.game.dashboard.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mygubbi.game.dashboard.data.dummy.FileDataProviderUtil;
import com.mygubbi.game.dashboard.domain.ProductItem;
import com.mygubbi.game.dashboard.domain.ProductSuggest;
import com.mygubbi.game.dashboard.domain.Proposal;
import com.mygubbi.game.dashboard.domain.JsonPojo.SimpleComboItem;

import us.monoid.json.JSONArray;
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

    public Proposal createProposal() {

        //todo: replace the below with a REST call and create proposal object from the json output. Use jackson lib to do that
        Proposal proposal = new Proposal();
        proposal.setProposalId(1234);
        proposal.setUploadFolderPath("c://tmp//proposals_data//1234");
        return proposal;
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
    
}
