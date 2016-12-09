package com.mygubbi.game.dashboard.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mygubbi.game.dashboard.ServerManager;
import com.mygubbi.game.dashboard.config.ConfigHolder;
import com.mygubbi.game.dashboard.data.dummy.FileDataProviderMode;
import com.mygubbi.game.dashboard.domain.*;
import com.mygubbi.game.dashboard.domain.JsonPojo.LookupItem;
import com.mygubbi.game.dashboard.view.NotificationUtil;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import us.monoid.json.JSONArray;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by nitinpuri on 05-04-2016.
 */
public class ProposalDataProvider {

    private final DataProviderMode dataProviderMode;
    private final ObjectMapper mapper;

    private static final Logger LOG = LogManager.getLogger(ProposalDataProvider.class);


    public static final String CATEGORY_LOOKUP = "category";
    public static final String ATTACHMENT_TYPE_LOOKUP = "attachmenttype";
    public static final String SHUTTER_DESIGN_LOOKUP = "shutterdesign";
    public static final String CARCASS_LOOKUP = "carcassmaterial";
    public static final String FINISH_TYPE_LOOKUP = "finishtype";
    public static final String SUB_CATEGORY_LOOKUP = "psubcategory";
    public static final String CITY_LOOKUP = "city";
    private static final String ROLE_DESIGNER = "designer";
    private static final String ROLE_SALES = "sales";
    public static final String MODULE_CATEGORY_LOOKUP = "modulecategory";
    private static final String MODULE_SELECTION_LOOKUP = "module";
    public static final String ACCESSORY_LOOKUP="accessories";


    public ProposalDataProvider(DataProviderMode dataProviderMode) {
        this.dataProviderMode = dataProviderMode;
        this.mapper = new ObjectMapper();
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public List<ProposalHeader> getProposalHeaders(JSONArray proposalHeaders) {
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
    public List<ProposalHeader> getProposalId()
    {
        JSONArray proposalHeaders = dataProviderMode.getResourceArray("proposal/id", new HashMap<>());
        return getProposalHeaders(proposalHeaders);
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
        try
        {
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
        try
        {
            Product[] items = this.mapper.readValue(jsonArray.toString(), Product[].class);
            return new ArrayList<>(Arrays.asList(items));
        }
        catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<ProposalVersion> getProposalVersions(int proposalId) {
        JSONArray jsonArray = dataProviderMode.getResourceArray("proposal/versions", new HashMap<String, String>() {
            {
                put("proposalId", proposalId + "");
            }
        });
        try
        {
            ProposalVersion[] items = this.mapper.readValue(jsonArray.toString(), ProposalVersion[].class);
            return new ArrayList<>(Arrays.asList(items));
        }
        catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<AddonProduct> getProposalAddons(int id) {
        JSONArray array = dataProviderMode.getResourceArray("proposal/addon.list", new HashMap<String, String>() {
            {
                put("proposalId", id + "");
            }
        });
        try {
            AddonProduct[] items = this.mapper.readValue(array.toString(), AddonProduct[].class);
            return new ArrayList<>(Arrays.asList(items));
        } catch (Exception e) {
            e.printStackTrace();
            NotificationUtil.showNotification("Lookup failed from Server, contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
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

    public ProposalVersion createDraft(int pid,String title) {
        try {

            JSONObject jsonObject = dataProviderMode.postResource("proposal/version/createdraft",  "{\"proposalId\": " + pid + "," + "\"title\" : " + "\"" + title + "\"" + "}");
            return this.mapper.readValue(jsonObject.toString(), ProposalVersion.class);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't create proposal", e);
        }
    }

    public String getUserId() {
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
        try
        {
            FinishTypeColor[] items = this.mapper.readValue(array.toString(), FinishTypeColor[].class);
            return new ArrayList<>(Arrays.asList(items));
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Color> getColorsByGroup(String colorGroupCode) {
        JSONArray array = dataProviderMode.getResourceArray("colorcodes", new HashMap<String, String>() {
            {
                put("colorGroupCode", colorGroupCode);
            }
        });
        try {
            ArrayList<Color> colors = new ArrayList<>(Arrays.asList(this.mapper.readValue(array.toString(), Color[].class)));

            colors.stream().forEach(color -> {
                color.setColorImageResource(new FileResource(new File(ConfigHolder.getInstance().getImageBasePath() + color.getImagePath())));
            });

            return colors;
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
            if (!jsonObject.has("error")) {
                fileAttachment.setId(this.mapper.readValue(jsonObject.toString(), FileAttachment.class).getId());
                return true;
            } else {
                return false;
            }
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
            if (!jsonObject.has("error")) {
                fileAttachment.setId(this.mapper.readValue(jsonObject.toString(), FileAttachment.class).getId());
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            throw new RuntimeException("Couldn't add proposal doc", e);
        }
    }

    private List<User> getUsersByRole(String role) {
        JSONArray array = dataProviderMode.getResourceArray("user/listbyrole", new HashMap<String, String>() {
            {
                put("role", role);
            }
        });
        try {
            User[] items = this.mapper.readValue(array.toString(), User[].class);
            return new ArrayList<>(Arrays.asList(items));
        } catch (Exception e) {
            NotificationUtil.showNotification("Lookup failed from Server, contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            throw new RuntimeException(e);
        }

    }

    public List<CatalogueProductCategory> getCatalogueProductCategories() {
        JSONArray array = dataProviderMode.getResourceArray("categories", new HashMap<>());
        try {
            CatalogueProductCategory[] items = this.mapper.readValue(array.toString(), CatalogueProductCategory[].class);
            return new ArrayList<>(Arrays.asList(items));
        } catch (Exception e) {
            NotificationUtil.showNotification("Lookup failed from Server, contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            throw new RuntimeException(e);
        }
    }

    public List<CatalogueProduct> getCatalogueProducts(String categoryCode, String subCategoryCode) {
        JSONArray array = dataProviderMode.getResourceArray("catalog-products", new HashMap<String, String>() {
            {
                put("subcategory", subCategoryCode);
            }
        });
        try {
            CatalogueProduct[] items = this.mapper.readValue(array.toString(), CatalogueProduct[].class);
            ArrayList<CatalogueProduct> catalogueProducts = new ArrayList<>(Arrays.asList(items));
            catalogueProducts.stream().forEach(CatalogueProduct::popuplateProduct);
            return catalogueProducts;
        } catch (Exception e) {
            NotificationUtil.showNotification("Lookup failed from Server, contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            throw new RuntimeException(e);
        }
    }

    public CatalogueProduct getCatalogueProduct(String productId) {

        JSONObject jsonObject = dataProviderMode.getResource("catalogproduct", new HashMap<String, String>() {
            {
                put("productId", productId);
            }
        });
        try {
            CatalogueProduct item = this.mapper.readValue(jsonObject.toString(), CatalogueProduct.class);
            item.popuplateProduct();
            return item;
        } catch (Exception e) {
            NotificationUtil.showNotification("Lookup failed from Server, contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            throw new RuntimeException(e);
        }
    }

    public List<User> getSalesUsers() {
        return getUsersByRole(ROLE_SALES);
    }

    public List<User> getDesignerUsers() {
        return getUsersByRole(ROLE_DESIGNER);
    }

    public List<LookupItem> getLookupItems(String type) {

        List<LookupItem> cachedItems = ServerManager.getInstance().getLookupItems(type);

        if (cachedItems != null) {
            return cachedItems;
        }

        //DataProviderMode overridenMode = type.equals(SHUTTER_DESIGN_LOOKUP) ? new FileDataProviderMode() : dataProviderMode;
        JSONArray array = dataProviderMode.getResourceArray("codelookup", new HashMap<String, String>() {
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


    public List<AccessoryPack> getAccessoryPacks(String mgCode)
    {
        JSONArray array = dataProviderMode.getResourceArray("module/accpacks", new HashMap<String, String>(){
            {
                put("mgCode", mgCode);
            }
        });
        try {
            AccessoryPack[] items = this.mapper.readValue(array.toString(), AccessoryPack[].class);
            return new ArrayList<>(Arrays.asList(items));
        } catch (Exception e) {
            NotificationUtil.showNotification("Lookup failed from Server, contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            throw new RuntimeException(e);
        }
    }

    public List<AccessoryAddon> getAccessoryAddons(String apCode)
    {
        JSONArray array = dataProviderMode.getResourceArray("module/addonsforaccpack", new HashMap<String, String>(){
            {
                put("apCode", apCode);
            }
        });
        try {
            AccessoryAddon[] items = this.mapper.readValue(array.toString(), AccessoryAddon[].class);

            ArrayList<AccessoryAddon> accessoryAddons = new ArrayList<>(Arrays.asList(items));
            accessoryAddons.stream().forEach(AccessoryAddon::initImageResource);

            return accessoryAddons;
        } catch (Exception e) {
            NotificationUtil.showNotification("Lookup failed from Server, contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            throw new RuntimeException(e);
        }
    }


    public List<Finish> getFinishes() {
        JSONArray array = dataProviderMode.getResourceArray("finishcodes", new HashMap<>());
        try {
            Finish[] items = this.mapper.readValue(array.toString(), Finish[].class);
            return new ArrayList<>(Arrays.asList(items));
        } catch (Exception e) {
            e.printStackTrace();
            NotificationUtil.showNotification("Lookup failed from Server, contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            return new ArrayList<>();
        }
    }

    public Product loadAndUpdateProduct(Product product) {
        try {
            product.setCreatedBy(getUserId());
            product.setUpdatedBy(getUserId());
            String productJson = this.mapper.writeValueAsString(product);
            JSONObject jsonObject = dataProviderMode.postResource(
                    "product/loadandupdate", productJson);

            return this.mapper.readValue(jsonObject.toString(), Product.class);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't map modules", e);
        }

    }

    public String getProposalQuoteFile(ProductAndAddonSelection productAndAddonSelection) {
        try {
            String productSelectionsJson = this.mapper.writeValueAsString(productAndAddonSelection);
            JSONObject obj = dataProviderMode.postResource("proposal/downloadquote", productSelectionsJson);
            return obj.getString("quoteFile");
        } catch (JSONException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getProposalQuoteFilePdf(ProductAndAddonSelection productAndAddonSelection) {
        try {
            String productSelectionsJson = this.mapper.writeValueAsString(productAndAddonSelection);
            JSONObject obj = dataProviderMode.postResource("proposal/downloadquotePdf", productSelectionsJson);
            return obj.getString("quoteFile");
        } catch (JSONException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getJobCardFile(ProductAndAddonSelection productAndAddonSelection) {
        try {
            String productSelectionsJson = this.mapper.writeValueAsString(productAndAddonSelection);
            JSONObject obj = dataProviderMode.postResource("proposal/downloadjobcard", productSelectionsJson);
            return obj.getString("jobcardFile");
        } catch (JSONException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    public String getSalesOrderExtract(ProductAndAddonSelection productAndAddonSelection) {
        try {
            String productSelectionsJson = this.mapper.writeValueAsString(productAndAddonSelection);
            JSONObject obj = dataProviderMode.postResource("proposal/downloadsalesorder", productSelectionsJson);
            return obj.getString("salesorderFile");
        } catch (JSONException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }




    public boolean updateProduct(Product product) {
        try {
            product.setUpdatedBy(getUserId());
            String productJson = this.mapper.writeValueAsString(product);
            LOG.debug("Product json:" + productJson);
            JSONObject jsonObject = dataProviderMode.postResource(
                    "product/update", productJson);
            if (!jsonObject.has("error")) {
                product.setId(jsonObject.getInt("id"));
                return true;
            } else {
                return false;
            }
        } catch (IOException | JSONException e) {
            throw new RuntimeException("Couldn't update product", e);
        }
    }

    public List<MGModule> getModules(String productCategory,String moduleCategory) {
        try {
            JSONArray jsonArray = dataProviderMode.getResourceArray(
                    "module/modulesByCategory", new HashMap<String, String>() {
                        {
                            put("productCategory", URLEncoder.encode(productCategory, "UTF-8"));
                            put("moduleCategory", URLEncoder.encode(moduleCategory, "UTF-8"));
                        }
                    });

            return this.mapper.readValue(jsonArray.toString(), new TypeReference<List<MGModule>>() {
            });

        } catch (IOException e) {
            throw new RuntimeException("Couldn't map modules", e);
        }
    }

    public List<Module> getModuleDetails(String moduleCode) {
        JSONArray array = dataProviderMode.getResourceArray("module/moduledetails", new HashMap<String, String>() {
            {
                put("code", urlEncode(moduleCode));
            }
        });
        try {
            Module[] items = this.mapper.readValue(array.toString(), Module[].class);
            return new ArrayList<>(Arrays.asList(items));
        } catch (Exception e) {
            e.printStackTrace();
            NotificationUtil.showNotification("Lookup failed from Server, contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            return new ArrayList<>();
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
            LOG.debug("Module Json : " + moduleJson);
            JSONObject jsonObject = dataProviderMode.postResource(
                    "module/pricev2", moduleJson);
            if (jsonObject.has("errors")) {
                throw new RuntimeException("Pricing has errors for this module, please contact GAME Admin.");
            }
            return this.mapper.readValue(jsonObject.toString(), ModulePrice.class);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't get module price", e);
        }

    }

    public List<ModuleAccessory> getModuleAccessories(String moduleCode) {
        try {
            JSONArray jsonArray = dataProviderMode.getResourceArray(
                    "module/accessories", new HashMap<String, String>());

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
        JSONArray array = dataProviderMode.getResourceArray("addon/categories", new HashMap<String, String>() {
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
        JSONArray array = dataProviderMode.getResourceArray("addon/productTypes", new HashMap<String, String>() {
            {
                put("roomCode", roomCode);
                put("categoryCode", addonCategoryCode);
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
        JSONArray array = dataProviderMode.getResourceArray("addon/brands", new HashMap<String, String>() {
            {
                put("productTypeCode", urlEncode(addonProductTypeCode));
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

    private String urlEncode(String addonProductTypeCode) {
        try {
            return URLEncoder.encode(addonProductTypeCode, Charset.defaultCharset().name());
        } catch (UnsupportedEncodingException e) {
            return addonProductTypeCode;
        }
    }

    //addon/products
    public List<AddonProductItem> getAddonProductItems(String addonProductTypeCode, String brandCode) {
        JSONArray array = dataProviderMode.getResourceArray("addon/products", new HashMap<String, String>() {
            {
                put("productTypeCode", urlEncode(addonProductTypeCode));
                put("brandCode", urlEncode(brandCode));
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



    public boolean removeProposalAddon(int addonId) {
        String faJson = "{\"id\": " + addonId + "}";
        JSONObject jsonObject = dataProviderMode.postResource(
                "proposal/addon.remove", faJson);
        return !jsonObject.has("error");
    }

    public boolean addProposalAddon(int proposalId, AddonProduct addonProduct) {
        try {
            addonProduct.setUpdatedBy(getUserId());
            addonProduct.setProposalId(proposalId);
            String faJson = this.mapper.writeValueAsString(addonProduct);
            JSONObject jsonObject = dataProviderMode.postResource(
                    "proposal/addon.add", faJson);
            if (!jsonObject.has("error")) {
                addonProduct.setId(this.mapper.readValue(jsonObject.toString(), AddonProduct.class).getId());
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            throw new RuntimeException("Couldn't add proposal addon", e);
        }
    }

    public boolean updateProposalAddon(int proposalId, AddonProduct addonProduct) {
        try {
            addonProduct.setUpdatedBy(getUserId());
            addonProduct.setProposalId(proposalId);
            String faJson = this.mapper.writeValueAsString(addonProduct);
            JSONObject jsonObject = dataProviderMode.postResource(
                    "proposal/addon.update", faJson);
            return !jsonObject.has("error");
        } catch (IOException e) {
            throw new RuntimeException("Couldn't add proposal addon", e);
        }
    }

}
