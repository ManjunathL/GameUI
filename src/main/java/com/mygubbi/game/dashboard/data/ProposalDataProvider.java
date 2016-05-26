package com.mygubbi.game.dashboard.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mygubbi.game.dashboard.data.dummy.FileDataProviderMode;
import com.mygubbi.game.dashboard.domain.*;
import com.mygubbi.game.dashboard.domain.JsonPojo.LookupItem;

import com.mygubbi.game.dashboard.view.NotificationUtil;
import com.vaadin.server.VaadinSession;
import us.monoid.json.JSONArray;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;

/**
 * Created by nitinpuri on 05-04-2016.
 */
public class ProposalDataProvider {

    private final DataProviderMode dataProviderMode;
    private final ObjectMapper mapper;

    public static final String CATEGORY_LOOKUP = "category";
    public static final String ROOM_LOOKUP = "room";
    public static final String MAKE_LOOKUP = "maketype";
    public static final String CARCASS_LOOKUP = "carcassmaterial";
    public static final String FINISH_TYPE_LOOKUP = "finishtype";
    public static final String FINISH_LOOKUP = "finish";

    public ProposalDataProvider(DataProviderMode dataProviderMode) {
        this.dataProviderMode = dataProviderMode;
        this.mapper = new ObjectMapper();
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private List<ProposalHeader> getProposalHeaders(JSONArray proposalHeaders) {
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

    public List<ProposalHeader> getProposalHeaders() {
        JSONArray proposalHeaders = dataProviderMode.getResourceArray("proposal/list", new HashMap<>());
        return getProposalHeaders(proposalHeaders);
    }

    public List<ProposalHeader> getProposalHeadersByStatus(String proposalStatus) {
        JSONArray proposalHeaders = dataProviderMode.getResourceArray("proposal/listbystatus", new HashMap<String, String>() {
            {
                put("status", proposalStatus);
            }
        });
        return getProposalHeaders(proposalHeaders);
    }

    public JSONObject getProposalHeader(String proposalId) {
        return dataProviderMode.getResource("proposal_header", new HashMap<String, String>() {
            {
                put("proposalId", proposalId);
            }
        });
    }

    public JSONArray getProposalProducts(String proposalId) {
        return dataProviderMode.getResourceArray("proposal_products", new HashMap<String, String>() {
            {
                put("proposalId", proposalId);
            }
        });
    }

    public JSONArray getProposalProductDetails(String proposalId, String productId) {
        return dataProviderMode.getResourceArray("proposal_product_details", new HashMap<String, String>() {
            {
                put("proposalId", proposalId);
                put("productId", proposalId);
            }
        });
    }

    public JSONArray getProposalProductDocuments(String proposalId, String productId) {
        return dataProviderMode.getResourceArray("proposal_product_documents", new HashMap<String, String>() {
            {
                put("proposalId", proposalId);
                put("productId", proposalId);
            }
        });
    }

    public JSONArray getProposalDocuments(String proposalId) {
        return dataProviderMode.getResourceArray("proposal_documents", new HashMap<String, String>() {
            {
                put("proposalId", proposalId);
            }
        });
    }

    public ProposalHeader createProposal() {

        try {
            JSONObject jsonObject = dataProviderMode.postResource("proposal/create", "{\"title\": \"New Proposal\", \"createdBy\": \"" + getUserId() + "\"}");
            return this.mapper.readValue(jsonObject.toString(), ProposalHeader.class);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't create proposal", e);
        }
    }

    private String getUserId() {
        return ((User) VaadinSession.getCurrent().getAttribute(User.class.getName())).getEmail();
    }
//todo: add calls for adding documents, one each - proposal/add.file

    public boolean saveProposal(ProposalHeader proposalHeader) {

        try {
            proposalHeader.setUpdatedBy(getUserId());
            String proposalJson = this.mapper.writeValueAsString(proposalHeader);
            JSONObject jsonObject = dataProviderMode.postResource("proposal/update", proposalJson);
            return !jsonObject.has("error");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Couldn't save proposal", e);
        }
    }

    public boolean submitProposal(int proposalId) {
        JSONObject jsonObject = dataProviderMode.postResource("proposal/submit", "{\"id\": " + proposalId + "}");
        return !jsonObject.has("error");
    }

    public boolean deleteProposal(int proposalId) {

        try {
            JSONObject jsonObject = dataProviderMode.postResource("delete_proposal", "\"proposalId\": " + proposalId);
            return jsonObject.has("status") && jsonObject.getString("status").equals("success");
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Couldn't delete proposal", e);
        }
    }

    public boolean cancelProposal(int proposalId) {

        try {
            JSONObject jsonObject = dataProviderMode.postResource("cancel_proposal", "\"proposalId\": " + proposalId);
            return jsonObject.has("status") && jsonObject.getString("status").equals("success");
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Couldn't cancel proposal", e);
        }
    }

    public List<FinishTypeColor> getFinishTypeColors() {
        JSONArray array = dataProviderMode.getResourceArray("colorlookup", new HashMap<String, String>());
        try {
            FinishTypeColor[] items = this.mapper.readValue(array.toString(), FinishTypeColor[].class);
            return Arrays.asList(items);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<LookupItem> getLookupItems(String type) {
        JSONArray array = dataProviderMode.getResourceArray("codelookup", new HashMap<String, String>() {
            {
                put("lookupType", type);
            }
        });
        try {
            LookupItem[] items = this.mapper.readValue(array.toString(), LookupItem[].class);
            return Arrays.asList(items);
        } catch (Exception e) {
            e.printStackTrace();
            NotificationUtil.showNotification("Lookup failed from Server, contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            return new ArrayList<>();
        }
    }

    public Product mapAndUpdateProduct(Product product) {
        try {
            product.setCreatedBy(getUserId());
            product.setUpdatedBy(getUserId());
            String productJson = this.mapper.writeValueAsString(product);
            JSONObject jsonObject = dataProviderMode.postResource(
                    "product/mapandupdate", productJson);

            Product product1 = this.mapper.readValue(jsonObject.toString(), Product.class);
            product1.setGenerated();
            return product1;
        } catch (IOException e) {
            throw new RuntimeException("Couldn't map modules", e);
        }

    }

    public String getProposalQuoteFile(int proposalId) {

        try {
            JSONObject obj = dataProviderMode.getResource("proposal/downloadquote", new HashMap<String, String>() {
                {
                    put("proposalId", proposalId + "");
                }
            });
            return obj.getString("quoteFile");

        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    public boolean updateProduct(Product product) {
        try {
            product.setUpdatedBy(getUserId());
            String productJson = this.mapper.writeValueAsString(product);
            JSONObject jsonObject = dataProviderMode.postResource(
                    "product/update", productJson);
            return !jsonObject.has("error");
        } catch (IOException e) {
            throw new RuntimeException("Couldn't update product", e);
        }
    }

    public List<MGModule> getMGModules(String extCode, String extDefCode) {
        try {
            JSONArray jsonArray = dataProviderMode.getResourceArray(
                    "module/mgmodules", new HashMap<String, String>() {
                        {
                            put("extCode", extCode);
                            if (extDefCode != null) put("extDefCode", extDefCode);
                        }
                    });

            return this.mapper.readValue(jsonArray.toString(), new TypeReference<List<MGModule>>() {
            });

        } catch (IOException e) {
            throw new RuntimeException("Couldn't map modules", e);
        }
    }

    public List<ProductSuggest> getProductSuggestions(String inputTerm) {
        JSONArray array = dataProviderMode.getResourceArray("product_auto_complete", new HashMap<String, String>());
        try {
            ProductSuggest[] items = this.mapper.readValue(array.toString(), ProductSuggest[].class);
            return Arrays.asList(items);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<ProductSuggest>();
        }
    }

    public List<ProductSuggest> getProductSearchResults(String inputTerm) {
        JSONArray array = dataProviderMode.getResourceArray("product_search_data", new HashMap<String, String>());
        try {
            ProductSuggest[] items = this.mapper.readValue(array.toString(), ProductSuggest[].class);
            return Arrays.asList(items);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<ProductSuggest>();
        }
    }

    public ProductItem getProduct(int productId) {
        JSONArray array = dataProviderMode.getResourceArray("product_search_data", new HashMap<String, String>());
        try {
            ProductItem[] items = this.mapper.readValue(array.toString(), ProductItem[].class);
            if (items.length == 0) return null;
            return Arrays.asList(items).get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<String> getAddonTypes() {
        JSONObject obj = new FileDataProviderMode().getResource("addon_type", new HashMap<String, String>());
        try {
            String typeStr = obj.getJSONArray("addon_type").toString();
            String[] typeL = this.mapper.readValue(typeStr, String[].class);
            return Arrays.asList(typeL);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<String>();
        }
    }

    public static void main(String[] args) {
        ProposalDataProvider proposalDataProvider = new ProposalDataProvider(new FileDataProviderMode());
        ProposalHeader proposal = proposalDataProvider.createProposal();

        try {
            proposalDataProvider.mapper.writeValue(System.out, proposal);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public ModulePrice getModulePrice(Module module) {
        try {
            String moduleJson = this.mapper.writeValueAsString(module);
            JSONObject jsonObject = dataProviderMode.postResource(
                    "module/price", moduleJson);
            if (jsonObject.has("errors")) {
                throw new RuntimeException("Pricing has errors for this module, please contact GAME Admin.");
            }
            return this.mapper.readValue(jsonObject.toString(), ModulePrice.class);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't get module price", e);
        }

    }

    public List<ModuleAccessory> getModuleAccessories(String moduleCode, String makeTypeCode) {
        try {
            JSONArray jsonArray = dataProviderMode.getResourceArray(
                    "module/accessories", new HashMap<String, String>() {
                        {
                            put("mgCode", moduleCode);
                            put("makeType", makeTypeCode);
                        }
                    });

            return this.mapper.readValue(jsonArray.toString(), new TypeReference<List<ModuleAccessory>>() {
            });

        } catch (IOException e) {
            throw new RuntimeException("Couldn't get module accessories", e);
        }

    }
}
