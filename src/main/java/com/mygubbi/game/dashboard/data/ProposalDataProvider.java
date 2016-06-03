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
import com.mygubbi.game.dashboard.ServerManager;
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
    public static final String SHUTTER_DESIGN_LOOKUP = "shutterdesign";
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
        JSONArray proposalHeaders = dataProviderMode.getResourceArray("proposal/list", new HashMap<String, String>() {
            {
                put("userid", getUserId());
            }
        });
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

    public ProposalHeader getProposalHeader(int proposalId) {
        JSONObject jsonObject = dataProviderMode.getResource("proposal/header", new HashMap<String, String>() {
            {
                put("id", proposalId + "");
            }
        });
        try {
            return this.mapper.readValue(jsonObject.toString(), ProposalHeader.class);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't create proposal", e);
        }
    }

    public List<Product> getProposalProducts(int proposalId) {
        JSONArray jsonArray = dataProviderMode.getResourceArray("product/list", new HashMap<String, String>() {
            {
                put("proposalId", proposalId + "");
            }
        });
        try {
            Product[] items = this.mapper.readValue(jsonArray.toString(), Product[].class);
            return new ArrayList<>(Arrays.asList(items));
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    //give modules and addons
    public Product getProposalProductDetails(int productId) {

        JSONObject jsonObject = dataProviderMode.getResource("product/detail", new HashMap<String, String>() {
            {
                put("id", productId + "");
            }
        });
        try {
            Product item = this.mapper.readValue(jsonObject.toString(), Product.class);
            return item;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Not able to fetch Product Details.", e);
        }
    }

    public List<FileAttachment> getProposalProductDocuments(int productId) {
        JSONArray jsonArray = dataProviderMode.getResourceArray("product/documents", new HashMap<String, String>() {
            {
                put("productId", productId + "");
            }
        });
        try {
            FileAttachment[] items = this.mapper.readValue(jsonArray.toString(), FileAttachment[].class);
            return new ArrayList<>(Arrays.asList(items));
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<FileAttachment> getProposalDocuments(int proposalId) {
        JSONArray jsonArray = dataProviderMode.getResourceArray("proposal/documents", new HashMap<String, String>() {
            {
                put("proposalId", proposalId + "");
            }
        });
        try {
            FileAttachment[] items = this.mapper.readValue(jsonArray.toString(), FileAttachment[].class);
            return new ArrayList<>(Arrays.asList(items));
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
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

    //submit to draft
    public boolean reviseProposal(int proposalId) {
        JSONObject jsonObject = dataProviderMode.postResource("proposal/revise", "{\"id\": " + proposalId + "}");
        return !jsonObject.has("error");
    }

    public boolean publishProposal(int proposalId) {
        JSONObject jsonObject = dataProviderMode.postResource("proposal/publish", "{\"id\": " + proposalId + "}");
        return !jsonObject.has("error");
    }

    public boolean deleteProposal(int proposalId) {

        try {
            JSONObject jsonObject = dataProviderMode.postResource("proposal/delete", "{\"id\": " + proposalId + "}");
            return jsonObject.has("status") && jsonObject.getString("status").equals("success");
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Couldn't delete proposal", e);
        }
    }

    public boolean cancelProposal(int proposalId) {
        try {
            JSONObject jsonObject = dataProviderMode.postResource("proposal/cancel", "{\"id\": " + proposalId + "}");
            return jsonObject.has("status") && jsonObject.getString("status").equals("success");
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Couldn't cancel proposal", e);
        }
    }

    public List<FinishTypeColor> getFinishTypeColors() {
        JSONArray array = dataProviderMode.getResourceArray("colorlookup", new HashMap<>());
        try {
            FinishTypeColor[] items = this.mapper.readValue(array.toString(), FinishTypeColor[].class);
            return new ArrayList<>(Arrays.asList(items));
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean addProductDoc(int productId, int proposalId, FileAttachment fileAttachment) {
        try {
            fileAttachment.setUploadedBy(getUserId());
            fileAttachment.setProductId(productId);
            fileAttachment.setProposalId(proposalId);
            String faJson = this.mapper.writeValueAsString(fileAttachment);
            JSONObject jsonObject = dataProviderMode.postResource(
                    "product/add.doc", faJson);
            return !jsonObject.has("error");
        } catch (IOException e) {
            throw new RuntimeException("Couldn't add product doc", e);
        }
    }

    public boolean removeProductDoc(int fileAttachmentId) {
        String faJson = "{\"id\": " + fileAttachmentId + "}";
        JSONObject jsonObject = dataProviderMode.postResource(
                "product/remove.doc", faJson);
        return !jsonObject.has("error");
    }

    //proposal/remove.doc - id (file attachment id)
    public boolean removeProposalDoc(int fileAttachmentId) {
        String faJson = "{\"id\": " + fileAttachmentId + "}";
        JSONObject jsonObject = dataProviderMode.postResource(
                "proposal/remove.doc", faJson);
        return !jsonObject.has("error");
    }

    //proposal/add.doc - proposalId, , FileAttachment
    public boolean addProposalDoc(int proposalId, FileAttachment fileAttachment) {
        try {
            fileAttachment.setUploadedBy(getUserId());
            fileAttachment.setProposalId(proposalId);
            String faJson = this.mapper.writeValueAsString(fileAttachment);
            JSONObject jsonObject = dataProviderMode.postResource(
                    "proposal/add.doc", faJson);
            return !jsonObject.has("error");
        } catch (IOException e) {
            throw new RuntimeException("Couldn't add proposal doc", e);
        }
    }

    //user/listbyrole - role [DESIGNER, SALESPERSON]

    public List<LookupItem> getLookupItems(String type) {

        if (type.equals(FINISH_LOOKUP)) {
            return getFinishes();
        }

        List<LookupItem> cachedItems = ServerManager.getInstance().getLookupItems(type);

        if (cachedItems != null) {
            return cachedItems;
        }

        DataProviderMode overridenMode = type.equals(SHUTTER_DESIGN_LOOKUP) ? new FileDataProviderMode() : dataProviderMode;
        JSONArray array = overridenMode.getResourceArray("codelookup", new HashMap<String, String>() {
            {
                put("lookupType", type);
            }
        });
        try {
            LookupItem[] items = this.mapper.readValue(array.toString(), LookupItem[].class);
            List<LookupItem> lookupItems = new ArrayList<>(Arrays.asList(items));

            ServerManager.getInstance().addLookupItems(type, lookupItems);

            return lookupItems;
        } catch (Exception e) {
            e.printStackTrace();
            NotificationUtil.showNotification("Lookup failed from Server, contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            return new ArrayList<>();
        }
    }

    public List<LookupItem> getFinishes() {

        List<LookupItem> cachedItems = ServerManager.getInstance().getLookupItems(FINISH_LOOKUP);
        if (cachedItems != null) {
            return cachedItems;
        }
        JSONArray array = dataProviderMode.getResourceArray("finishlookup", new HashMap<>());
        try {
            LookupItem[] items = this.mapper.readValue(array.toString(), LookupItem[].class);
            List<LookupItem> lookupItems = new ArrayList<>(Arrays.asList(items));

            ServerManager.getInstance().addLookupItems(FINISH_LOOKUP, lookupItems);

            return lookupItems;
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

    public String getProposalQuoteFile(ProductSelections productSelections) {
        try {
            String productSelectionsJson = this.mapper.writeValueAsString(productSelections);
            JSONObject obj = dataProviderMode.postResource("proposal/downloadquote", productSelectionsJson);
            return obj.getString("quoteFile");
        } catch (JSONException | JsonProcessingException e) {
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
            return new ArrayList<>(Arrays.asList(items));
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<ProductSuggest>();
        }
    }

    public List<ProductSuggest> getProductSearchResults(String inputTerm) {
        JSONArray array = dataProviderMode.getResourceArray("product_search_data", new HashMap<String, String>());
        try {
            ProductSuggest[] items = this.mapper.readValue(array.toString(), ProductSuggest[].class);
            return new ArrayList<>(Arrays.asList(items));
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

    public boolean deleteProduct(int productId) {

        JSONObject jsonObject = dataProviderMode.postResource("product/delete", "{\"id\": "
                + productId + "}");
        boolean isError = jsonObject.has("error");

        if (isError) {
            String error = null;
            try {
                error = jsonObject.getString("error");
            } catch (JSONException e) {
                //
            }
            NotificationUtil.showNotification("Error in deleting product - " + error, NotificationUtil.STYLE_BAR_ERROR_SMALL);
        }
        return !isError;
    }

    //addon/categories
    public List<AddonCategory> getAddonCategories(String roomCode) {
        //todo: change mode
        JSONArray array = new FileDataProviderMode().getResourceArray("addon/categories", new HashMap<String, String>() {
            {
                put("roomCode", roomCode);
            }
        });
        try {
            AddonCategory[] items = this.mapper.readValue(array.toString(), AddonCategory[].class);
            return new ArrayList<>(Arrays.asList(items));
        } catch (Exception e) {
            e.printStackTrace();
            NotificationUtil.showNotification("Lookup failed from Server, contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            return new ArrayList<>();
        }
    }

    //addon/productTypes
    public List<AddonProductType> getAddonProductTypes(String roomCode, String addonCategoryCode) {
        //todo: change mode
        JSONArray array = new FileDataProviderMode().getResourceArray("addon/productTypes", new HashMap<String, String>() {
            {
                put("roomCode", roomCode);
                put("addonCategoryCode", addonCategoryCode);
            }
        });
        try {
            AddonProductType[] items = this.mapper.readValue(array.toString(), AddonProductType[].class);
            return new ArrayList<>(Arrays.asList(items));
        } catch (Exception e) {
            e.printStackTrace();
            NotificationUtil.showNotification("Lookup failed from Server, contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            return new ArrayList<>();
        }
    }

    //addon/brands
    public List<AddonBrand> getAddonBrands(String addonProductTypeCode) {
        //todo: change mode
        JSONArray array = new FileDataProviderMode().getResourceArray("addon/brands", new HashMap<String, String>() {
            {
                put("addonProductTypeCode", addonProductTypeCode);
            }
        });
        try {
            AddonBrand[] items = this.mapper.readValue(array.toString(), AddonBrand[].class);
            return new ArrayList<>(Arrays.asList(items));
        } catch (Exception e) {
            e.printStackTrace();
            NotificationUtil.showNotification("Lookup failed from Server, contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            return new ArrayList<>();
        }
    }

    //addon/products
    public List<AddonProductItem> getAddonProductItems(String addonProductTypeCode, String brandCode) {
        //todo: change mode
        JSONArray array = new FileDataProviderMode().getResourceArray("addon/products", new HashMap<String, String>() {
            {
                put("addonProductTypeCode", addonProductTypeCode);
                put("brandCode", brandCode);
            }
        });
        try {
            AddonProductItem[] items = this.mapper.readValue(array.toString(), AddonProductItem[].class);
            return new ArrayList<>(Arrays.asList(items));
        } catch (Exception e) {
            e.printStackTrace();
            NotificationUtil.showNotification("Lookup failed from Server, contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            return new ArrayList<>();
        }
    }

}
