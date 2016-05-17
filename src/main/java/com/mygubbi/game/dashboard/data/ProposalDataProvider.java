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
import com.mygubbi.game.dashboard.domain.*;
import com.mygubbi.game.dashboard.domain.JsonPojo.SimpleComboItem;

import com.vaadin.shared.ui.colorpicker.Color;
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

    public JSONObject getProposalHeader(String proposalId) {
        return dataProviderUtil.getResource("proposal_header", new HashMap<String, String>(){
            {
                put("proposalId", proposalId);
            }
        });
    }

    public JSONArray getProposalProducts(String proposalId) {
        return dataProviderUtil.getResourceArray("proposal_products", new HashMap<String, String>(){
            {
                put("proposalId", proposalId);
            }
        });
    }

    public JSONArray getProposalProductDetails(String proposalId, String productId) {
        return dataProviderUtil.getResourceArray("proposal_product_details", new HashMap<String, String>(){
            {
                put("proposalId", proposalId);
                put("productId", proposalId);
            }
        });
    }

    public JSONArray getProposalProductDocuments(String proposalId, String productId) {
        return dataProviderUtil.getResourceArray("proposal_product_documents", new HashMap<String, String>(){
            {
                put("proposalId", proposalId);
                put("productId", proposalId);
            }
        });
    }

    public JSONArray getProposalDocuments(String proposalId) {
        return dataProviderUtil.getResourceArray("proposal_documents", new HashMap<String, String>(){
            {
                put("proposalId", proposalId);
            }
        });
    }
//todo: add userid to all post calls
    public ProposalHeader createProposal() {

        try {
            JSONObject jsonObject = dataProviderUtil.postResource("create_proposal", "{\"title\": \"New Proposal\"}");
            return this.mapper.readValue(jsonObject.toString(), ProposalHeader.class);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't create proposal", e);
        }
    }
//todo: add calls for adding documents, one each - proposal/add.file

    public boolean saveProposal(ProposalHeader proposalHeader) {

        try {
            String proposalJson = this.mapper.writeValueAsString(proposalHeader);
            JSONObject jsonObject = dataProviderUtil.postResource("save_proposal", proposalJson);
            return jsonObject.has("status") && jsonObject.getString("status").equals("success");
        } catch (JSONException | JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Couldn't save proposal", e);
        }
    }
    //todo: handle status=error and error=<message description>
    public boolean submitProposal(int proposalId) {

        try {
            JSONObject jsonObject = dataProviderUtil.postResource("submit_proposal", "\"proposalId\": " + proposalId);
            return jsonObject.has("status") && jsonObject.getString("status").equals("success");
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Couldn't activate proposal", e);
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

    public boolean cancelProposal(int proposalId) {

        try {
            JSONObject jsonObject = dataProviderUtil.postResource("cancel_proposal", "\"proposalId\": " + proposalId);
            return jsonObject.has("status") && jsonObject.getString("status").equals("success");
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Couldn't cancel proposal", e);
        }
    }

    /**
     * Product Section
     */

    public List<Color> getColorSwatch(String shutterFinishCode) {
        return null;
    }

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

    public Product mapAndUpdateProduct(int proposalId, String quoteFilePath) {
        //returns product json containing header, modules, addons
        try {
            JSONObject jsonObject = dataProviderUtil.postResource(
                    "map_product",
                    "{\"proposalId\": "+ proposalId +", \"filePath\": \"" + quoteFilePath + "\"}");

            return this.mapper.readValue(jsonObject.toString(), Product.class);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't map modules", e);
        }

    }

    public JSONArray updateProduct(String productJson) { //includes modules json, header and addons json
        return null;
    }

    public JSONArray getMGModules(String importedModuleCode) { //includes both module code nad default code
        return null;//includes module description, image, code, dimensions
    }

    public JSONArray getModulePrice(String moduleCode, String carcassMaterial, String shutterFinish, String makeType) { //includes both module code nad default code
        return null;//gives back price
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
        ProposalHeader proposal = proposalDataProvider.createProposal();

        try {
            proposalDataProvider.mapper.writeValue(System.out, proposal);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
