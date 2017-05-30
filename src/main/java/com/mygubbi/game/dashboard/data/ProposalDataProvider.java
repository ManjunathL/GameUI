package com.mygubbi.game.dashboard.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mygubbi.game.dashboard.ServerManager;
import com.mygubbi.game.dashboard.config.ConfigHolder;
import com.mygubbi.game.dashboard.data.dummy.FileDataProviderMode;
import com.mygubbi.game.dashboard.domain.*;
import com.mygubbi.game.dashboard.domain.JsonPojo.*;
import com.mygubbi.game.dashboard.view.NotificationUtil;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import us.monoid.json.JSONArray;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;
import us.monoid.web.JSONResource;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.sql.Date;
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
    private static final String ROLE_DESIGN_PARTNER = "designPartner";
    private static final String ROLE_SALES = "sales";
    public static final String MODULE_CATEGORY_LOOKUP = "modulecategory";
    private static final String MODULE_SELECTION_LOOKUP = "module";
    public static final String ACCESSORY_LOOKUP = "accessories";
    public static final String COLLECTION_LOOKUP = "collection";


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

    public List<ProposalHeader> getProposalId() {
        JSONArray proposalHeaders = dataProviderMode.getResourceArray("proposal/id", new HashMap<>());
        return getProposalHeaders(proposalHeaders);
    }

    public List<ProposalHeader> fetchCrmId() {
        JSONArray proposalHeaders = dataProviderMode.getResourceArray("proposal/crmId", new HashMap<>());
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

    public List<ProposalHeader> getProposalHeadersBasedOnDesignPartner(String designPartnerEmail) {
        JSONArray proposalHeaders = dataProviderMode.getResourceArray("proposal/listbydesignpartner", new HashMap<String, String>() {
            {
                put("designPartnerEmail", designPartnerEmail);
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

    public List<Product> getVersionProducts(int proposalId, String Vid) {
        JSONArray jsonArray = dataProviderMode.getResourceArray("product/versionlist", new HashMap<String, String>() {
            {
                put("proposalId", proposalId + "");
                put("fromVersion", Vid + "");
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

    public List<AddonProduct> getVersionAddons(int proposalId, String Vid) {
        JSONArray jsonArray = dataProviderMode.getResourceArray("proposal/version/addonlist", new HashMap<String, String>() {
            {
                put("proposalId", proposalId + "");
                put("fromVersion", Vid + "");
            }
        });
        try {
            AddonProduct[] items = this.mapper.readValue(jsonArray.toString(), AddonProduct[].class);
            return new ArrayList<>(Arrays.asList(items));
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<ProposalVersion> getProposalVersionPreSales(int proposalId) {
        JSONArray jsonArray = dataProviderMode.getResourceArray("proposal/version/presales", new HashMap<String, String>() {
            {
                put("proposalId", proposalId + "");
            }
        });
        try {
            ProposalVersion[] items = this.mapper.readValue(jsonArray.toString(), ProposalVersion[].class);
            return new ArrayList<>(Arrays.asList(items));
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<ProposalVersion> getProposalVersionPostSales(int proposalId) {
        JSONArray jsonArray = dataProviderMode.getResourceArray("proposal/version/postsales", new HashMap<String, String>() {
            {
                put("proposalId", proposalId + "");
            }
        });
        try {
            ProposalVersion[] items = this.mapper.readValue(jsonArray.toString(), ProposalVersion[].class);
            return new ArrayList<>(Arrays.asList(items));
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<ProposalVersion> getProposalVersionProduction(int proposalId) {
        JSONArray jsonArray = dataProviderMode.getResourceArray("proposal/version/production", new HashMap<String, String>() {
            {
                put("proposalId", proposalId + "");
            }
        });
        try {
            ProposalVersion[] items = this.mapper.readValue(jsonArray.toString(), ProposalVersion[].class);
            return new ArrayList<>(Arrays.asList(items));
        } catch (IOException e) {
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
        try {
            ProposalVersion[] items = this.mapper.readValue(jsonArray.toString(), ProposalVersion[].class);
            return new ArrayList<>(Arrays.asList(items));
        } catch (IOException e) {
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
    public Product getProposalProductDetails(int productId, String proposalVersion) {

        JSONObject jsonObject = dataProviderMode.getResource("product/detail", new HashMap<String, String>() {
            {
                put("id", productId + "");
                put("fromVersion", proposalVersion + "");
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

    public List<Product> getProposalProductManualSeq(int proposalId, String proposalVersion) {

        JSONArray jsonArray = dataProviderMode.getResourceArray("product/manualSeq", new HashMap<String, String>() {
            {
                put("proposalId", proposalId + "");
                put("fromVersion", proposalVersion + "");
            }
        });
        try {
            Product[] items = this.mapper.readValue(jsonArray.toString(), Product[].class);
            return new ArrayList<>(Arrays.asList(items));
        } catch (Exception e) {
            e.printStackTrace();
            NotificationUtil.showNotification("Lookup failed from Server, contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            return new ArrayList<>();
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

            JSONObject jsonObject = dataProviderMode.postResource("proposal/create", "{\"createdBy\": \"" + getUserId() + "\"}");
            LOG.debug("Create Proposal OP :" + jsonObject.toString());
            return this.mapper.readValue(jsonObject.toString(), ProposalHeader.class);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't create proposal", e);
        }
    }

    public ProposalVersion createDraft(int pid, String title) {
        try {

            JSONObject jsonObject = dataProviderMode.postResource("proposal/version/createdraft", "{\"proposalId\": " + pid + "," + "\"fromVersion\" : \"0.0\"" + "," + "\"title\" : " + "\"" + title + "\"" + "}");
            return this.mapper.readValue(jsonObject.toString(), ProposalVersion.class);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't create proposal", e);
        }
    }

    public ProposalVersion createPostSalesInitial(ProposalVersion proposalVersion) {
        try {

            JSONObject jsonObject = dataProviderMode.postResource("proposal/version/createPostSalesInitial", "{\"proposalId\": " + proposalVersion.getProposalId() + "," + "\"fromVersion\" : \"1.0\"" + "," + "\"title\" : " + "\"" + proposalVersion.getTitle() + "\"" + "}");
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
            LOG.debug("Proposal header json :" + proposalJson);
            JSONObject jsonObject = dataProviderMode.postResource("proposal/update", proposalJson);
            return !jsonObject.has("error");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Couldn't save proposal", e);
        }
    }

    public boolean saveProposalOnConfirm(ProposalHeader proposalHeader) {

        try {

            proposalHeader.setUpdatedBy(getUserId());
            String proposalJson = this.mapper.writeValueAsString(proposalHeader);
            LOG.debug("Proposal header json :" + proposalJson);
            JSONObject jsonObject = dataProviderMode.postResource("proposal/updateonconfirm", proposalJson);
            return !jsonObject.has("error");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Couldn't save proposal", e);
        }
    }

    public boolean publishVersion(String version, int proposalId) {
        JSONObject jsonObject = dataProviderMode.postResource("proposal/version/publish", "{\"version\": " + version + "," + "\"proposalId\": " + proposalId + "}");
        return !jsonObject.has("error");
    }

    public boolean confirmVersion(String version, int proposalId, String fromVersion, String toVersion) {
        JSONObject jsonObject = dataProviderMode.postResource("proposal/version/confirm", "{\"version\": " + version + "," + "\"proposalId\": " + proposalId + "," + "\"fromVersion\": " + fromVersion + "," + "\"toVersion\": " + toVersion + "}");
        return !jsonObject.has("error");
    }

    public boolean versionDesignSignOff(String version, int proposalId, String fromVersion, String toVersion) {
        JSONObject jsonObject = dataProviderMode.postResource("proposal/version/designsignoff", "{\"version\": " + version + "," + "\"proposalId\": " + proposalId + "," + "\"fromVersion\": " + fromVersion + "," + "\"toVersion\": " + toVersion + "}");
        return !jsonObject.has("error");
    }

    public boolean versionProductionSignOff(String version, int proposalId, String fromVersion, String toVersion) {
        JSONObject jsonObject = dataProviderMode.postResource("proposal/version/productionsignoff", "{\"version\": " + version + "," + "\"proposalId\": " + proposalId + "," + "\"fromVersion\": " + fromVersion + "," + "\"toVersion\": " + toVersion + "}");
        LOG.debug("JSON OBJECT :" + jsonObject.toString());
        return !jsonObject.has("error");
    }

    //submit to Draft
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

    public boolean deleteProposalVersion(int proposalId) {

        try {
            JSONObject jsonObject = dataProviderMode.postResource("proposal/version/delete", "{\"id\": " + proposalId + "}");
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

    public List<User> getDesignPartnerUsers() {
        return getUsersByRole(ROLE_DESIGN_PARTNER);
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


    public List<AccessoryPack> getAccessoryPacks(String mgCode) {
        JSONArray array = dataProviderMode.getResourceArray("module/accpacks", new HashMap<String, String>() {
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

    public List<AccessoryAddon> getAccessoryAddons(String apCode) {
        JSONArray array = dataProviderMode.getResourceArray("module/addonsforaccpack", new HashMap<String, String>() {
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

    public ProposalVersion createNewProduct(ProposalVersion proposalVersion) {
        try {
            String productJson = this.mapper.writeValueAsString(proposalVersion);
            JSONObject jsonObject = dataProviderMode.postResource(
                    "product/createnew", productJson);

            return this.mapper.readValue(jsonObject.toString(), ProposalVersion.class);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't map modules", e);
        }

    }

    public ProposalVersion createNewAddons(ProposalVersion proposalVersion) {
        try {
            String productJson = this.mapper.writeValueAsString(proposalVersion);
            JSONObject jsonObject = dataProviderMode.postResource(
                    "addon/createnew", productJson);

            return this.mapper.readValue(jsonObject.toString(), ProposalVersion.class);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't map modules", e);
        }

    }

    public ProposalVersion createNewProductFromOldProposal(ProposalVersion proposalVersion) {
        try {
            String productJson = this.mapper.writeValueAsString(proposalVersion);
            JSONObject jsonObject = dataProviderMode.postResource(
                    "product/createnewfromoldproposal", productJson);

            return this.mapper.readValue(jsonObject.toString(), ProposalVersion.class);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't map modules", e);
        }
    }

    public ProposalVersion createNewAddonFromOldProposal(ProposalVersion proposalVersion) {
        try {
            String productJson = this.mapper.writeValueAsString(proposalVersion);
            JSONObject jsonObject = dataProviderMode.postResource(
                    "addon/createnewfromoldproposal", productJson);

            return this.mapper.readValue(jsonObject.toString(), ProposalVersion.class);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't map modules", e);
        }

    }

    public ProposalVersion copyProposalVersion(ProposalVersion proposalVersion) {
        try {
            String productJson = this.mapper.writeValueAsString(proposalVersion);
            JSONObject jsonObject = dataProviderMode.postResource(
                    "proposal/version/copyversion", productJson);

            return this.mapper.readValue(jsonObject.toString(), ProposalVersion.class);
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

   /* public String getProdSpecFile(ProductAndAddonSelection productAndAddonSelection) {
        try {
            String productSelectionsJson = this.mapper.writeValueAsString(productAndAddonSelection);
            JSONObject obj = dataProviderMode.postResource("proposal/downloadprodspecfile", productSelectionsJson);
            return obj.getString("prodSpecFile");
        } catch (JSONException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }*/

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

    public String getMarginSheet(ProductAndAddonSelection productAndAddonSelection) {
        try {
            String productSelectionsJson = this.mapper.writeValueAsString(productAndAddonSelection);
            JSONObject obj = dataProviderMode.postResource("proposal/downloadmarginsheet", productSelectionsJson);
            return obj.getString("marginSheetFile");
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

    public Product updateProductWithRefreshedPrice(Product product) {
        try {
            product.setUpdatedBy(getUserId());
            String productJson = this.mapper.writeValueAsString(product);
            LOG.debug("Product json:" + productJson);
            JSONObject jsonObject = dataProviderMode.postResource(
                    "product/updaterefreshedprice", productJson);
            if (!jsonObject.has("error")) {
                product.setId(jsonObject.getInt("id"));
                return this.mapper.readValue(jsonObject.toString(), Product.class);
            } else {
                return null;
            }
        } catch (IOException | JSONException e) {
            throw new RuntimeException("Couldn't update product", e);
        }
    }

    public Product updateProductSequence(int seq, int id) {
        try {
            JSONObject jsonObject = dataProviderMode.postResource("product/updatesequence", "{\"seq\": " + "\"" + seq + "\"" + "," + "\"id\" : " + id + "}");
            return this.mapper.readValue(jsonObject.toString(), Product.class);


        } catch (IOException e) {
            throw new RuntimeException("Couldn't update product", e);
        }
    }

    public List<MGModule> getModules(String productCategory, String moduleCategory) {
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

    /*public static void main(String[] args) {
        ProposalDataProvider proposalDataProvider = new ProposalDataProvider(new FileDataProviderMode());
        ProposalHeader proposal = proposalDataProvider.createProposal();

        try {
            proposalDataProvider.mapper.writeValue(System.out, proposal);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }*/


    public ModulePrice getModulePrice(ModuleForPrice moduleForPrice) {
        try {
            String moduleForPriceJson = this.mapper.writeValueAsString(moduleForPrice);
            LOG.debug("Module For price Json : " + moduleForPriceJson);
            JSONObject jsonObject = dataProviderMode.postResource(
                    "module/pricev2", moduleForPriceJson);
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
    public List<AddonProductType> getAddonProductTypes(String addonCategoryCode) {
        JSONArray array = dataProviderMode.getResourceArray("addon/productTypes", new HashMap<String, String>() {
            {
                put("categoryCode", urlEncode(addonCategoryCode));
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

    public List<AddonProductSubtype> getAddonProductSubTypes(String addonCategoryCode, String addonProductTypeCode) {
        JSONArray array = dataProviderMode.getResourceArray("addon/productSubTypes", new HashMap<String, String>() {
            {
                put("categoryCode", urlEncode(addonCategoryCode));
                put("productTypeCode", urlEncode(addonProductTypeCode));
            }
        });
        try {
            AddonProductSubtype[] items = this.mapper.readValue(array.toString(), AddonProductSubtype[].class);
            return new ArrayList<>(Arrays.asList(items));
        } catch (Exception e) {
            e.printStackTrace();
            NotificationUtil.showNotification("Lookup failed from Server, contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            return new ArrayList<>();
        }
    }

    //addon/brands
    public List<AddonBrand> getAddonBrands(String category, String addonProductSubType, String addonProductTypeCode) {
        JSONArray array = dataProviderMode.getResourceArray("addon/brands", new HashMap<String, String>() {
            {
                put("categoryCode", urlEncode(category));
                put("productTypeCode", urlEncode(addonProductTypeCode));
                put("productSubtypeCode", urlEncode(addonProductSubType));
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
    public List<AddonProductItem> getAddonProductItems(String categoryCode, String addonProductTypeCode, String addonProductSubTypeCode, String brandCode) {
        JSONArray array = dataProviderMode.getResourceArray("addon/products", new HashMap<String, String>() {
            {
                put("categoryCode", urlEncode(categoryCode));
                put("productTypeCode", urlEncode(addonProductTypeCode));
                put("productSubtypeCode", urlEncode(addonProductSubTypeCode));
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

    public ProposalVersion createProposalVersion(ProposalVersion proposalversion) {
        try {

            String versionJson = this.mapper.writeValueAsString(proposalversion);
            LOG.debug("Version json :" + versionJson);
            JSONObject jsonObject = dataProviderMode.postResource(
                    "proposal/version/createversion", versionJson);

            return this.mapper.readValue(jsonObject.toString(), ProposalVersion.class);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't map products", e);
        }

    }

    public ProposalVersion updateVersion(ProposalVersion proposalVersion) {

        try {
            String versionJson = this.mapper.writeValueAsString(proposalVersion);
            JSONObject jsonObject = dataProviderMode.postResource("proposal/updateversion", versionJson);

            return this.mapper.readValue(jsonObject.toString(), ProposalVersion.class);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't update proposal", e);
        }
    }

    public ProposalVersion lockAllPreSalesVersions(String status, int proposalId) {

        try {
            JSONObject jsonObject = dataProviderMode.postResource("proposal/version/locakallpresalesversions", "{\"internalStatus\": " + "\"" + status + "\"" + "," + "\"proposalId\" : " + proposalId + "}");
            return this.mapper.readValue(jsonObject.toString(), ProposalVersion.class);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't update proposal", e);
        }
    }

    public ProposalVersion lockAllPostSalesVersions(String status, int proposalId) {

        try {
            JSONObject jsonObject = dataProviderMode.postResource("proposal/version/locakallpostsalesversions", "{\"internalStatus\": " + "\"" + status + "\"" + "," + "\"proposalId\" : " + proposalId + "}");
            return this.mapper.readValue(jsonObject.toString(), ProposalVersion.class);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't update proposal", e);
        }
    }

    public ProposalVersion lockAllVersionsExceptPSO(String status, int proposalId) {

        try {
            JSONObject jsonObject = dataProviderMode.postResource("proposal/version/lockallversions", "{\"internalStatus\": " + "\"" + status + "\"" + "," + "\"proposalId\" : " + proposalId + "}");
            return this.mapper.readValue(jsonObject.toString(), ProposalVersion.class);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't update proposal", e);
        }
    }


    public ProposalVersion updateProposalProductOnConfirm(String version, int proposalId, String fromVersion) {

        try {
            JSONObject jsonObject = dataProviderMode.postResource("proposal/version/product/updateVersionOnConfirm", "{\"version\": " + "\"" + version + "\"" + "," + "\"proposalId\" : " + proposalId + "," + "\"fromVersion\" : " + "\"" + fromVersion + "\"" + "}");
            return this.mapper.readValue(jsonObject.toString(), ProposalVersion.class);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't update proposal", e);
        }
    }

    public ProposalVersion updateProposalAddonOnConfirm(String version, int proposalId, String fromVersion) {

        try {
            JSONObject jsonObject = dataProviderMode.postResource("proposal/version/addon/updateVersionOnConfirm", "{\"version\": " + "\"" + version + "\"" + "," + "\"proposalId\" : " + proposalId + "," + "\"fromVersion\" : " + "\"" + fromVersion + "\"" + "}");
            return this.mapper.readValue(jsonObject.toString(), ProposalVersion.class);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't update proposal", e);
        }
    }

    public ProposalVersion getLatestVersion(int proposalId) {
        JSONArray jsonArray = dataProviderMode.getResourceArray("proposal/version/getlatestversion", new HashMap<String, String>() {
            {
                put("proposalId", proposalId + "");
                put("proposalId", proposalId + "");
            }
        });
        try {
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            LOG.info(jsonObject);
            return this.mapper.readValue(jsonObject.toString(), ProposalVersion.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<ProposalVersion> getLatestVersionTemproary(int proposalId, String version) {
        JSONArray jsonArray = dataProviderMode.getResourceArray("proposal/version/getlatestTemproary", new HashMap<String, String>() {
            {
                put("proposalId", proposalId + "");
                put("version", version + "");
            }
        });
        try {
            ProposalVersion[] items = this.mapper.readValue(jsonArray.toString(), ProposalVersion[].class);
            return new ArrayList<>(Arrays.asList(items));
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<ProposalCity> getMonthCount(int curmonth, String city) {
        JSONArray jsonArray = dataProviderMode.getResourceArray("city/selectMonthCount", new HashMap<String, String>() {
            {
                put("curmonth", curmonth + "");
                put("city", city + "");
            }
        });

        try {
            ProposalCity[] items = this.mapper.readValue(jsonArray.toString(), ProposalCity[].class);
            return new ArrayList<>(Arrays.asList(items));
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

    }

    public List<ProposalCity> getCityDataTest(int proposalId) {
        JSONArray jsonArray = dataProviderMode.getResourceArray("city/selectCity", new HashMap<String, String>() {
            {
                put("proposalId", proposalId + "");
            }
        });

        try {
            ProposalCity[] items = this.mapper.readValue(jsonArray.toString(), ProposalCity[].class);
            return new ArrayList<>(Arrays.asList(items));
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

    }

    public List<ProposalCity> checkCity(int proposalId) {
        JSONArray jsonArray = dataProviderMode.getResourceArray("city/checkCity", new HashMap<String, String>() {
            {
                put("proposalId", proposalId + "");
            }
        });

        try {
            ProposalCity[] items = this.mapper.readValue(jsonArray.toString(), ProposalCity[].class);
            return new ArrayList<>(Arrays.asList(items));
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

    }

    public boolean getCityData(int proposalId) {
        JSONObject jsonObject = dataProviderMode.postResource("city/selectCity", "{\"proposalId\": " + proposalId + "}");
        return !jsonObject.has("error");
    }


    public ProposalCity createCity(String city, int curmonth, int proposalId, String quoteNo) {
        try {
            JSONObject jsonObject = dataProviderMode.postResource("city/newCityQuote", "{\"city\": " + "\"" + city + "\"" + "," + "\"curmonth\" : " + "\"" + curmonth + "\"" + "," + "\"proposalId\" : " + "\"" + proposalId + "\"" + " , " + "\"quoteNo\" : " + "\"" + quoteNo + "\"" + "}");
            return this.mapper.readValue(jsonObject.toString(), ProposalCity.class);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't create proposal", e);
        }
    }

    public List<AddonMaster> getAddondDetails(String code) {
        JSONArray jsonArray = dataProviderMode.getResourceArray("addon/addondetails", new HashMap<String, String>() {
            {
                put("code", code + "");
            }
        });
        try
        {
            AddonMaster[] items = this.mapper.readValue(jsonArray.toString(), AddonMaster[].class);
            return new ArrayList<>(Arrays.asList(items));
        }
        catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }



    public List<AccessoryDetails> getAccessoryDetails(String apcode)
    {
        JSONArray array = dataProviderMode.getResourceArray("proposal/accdetails", new HashMap<String, String>(){
            {
                put("apcode", apcode);
            }
        });
        try {
            AccessoryDetails[] items = this.mapper.readValue(array.toString(), AccessoryDetails[].class);
            return new ArrayList<>(Arrays.asList(items));
        } catch (Exception e) {
            NotificationUtil.showNotification("Lookup failed from Server, contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            throw new RuntimeException(e);
        }
    }
    public List<AccessoryDetails> getAccessoryhwDetails(String apcode)
    {
        JSONArray array = dataProviderMode.getResourceArray("proposal/acchwdetails", new HashMap<String, String>(){
            {
                put("apcode", apcode);
            }
        });
        try {
            AccessoryDetails[] items = this.mapper.readValue(array.toString(), AccessoryDetails[].class);
            return new ArrayList<>(Arrays.asList(items));
        } catch (Exception e) {
            NotificationUtil.showNotification("Lookup failed from Server, contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            throw new RuntimeException(e);
        }
    }

    public PriceMaster getAddonRate(String code, Date priceDate, String city) {
        JSONObject jsonObject = dataProviderMode.getResource("addon/getprice", new HashMap<String, String>() {
            {
                put("code", code + "" );
                put("priceDate", priceDate +  "");
                put("city", city + "" );
            }
        });

        try {
            return this.mapper.readValue(jsonObject.toString(), PriceMaster.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    public PriceMaster getAccessoryRateDetails(String rateId, Date priceDate, String city)
    {
        JSONObject jsonObject = dataProviderMode.getResource("proposal/accratedetails", new HashMap<String, String>(){
            {
                put("rateId", rateId + "");
                put("priceDate", priceDate + "");
                put("city",city + "");
            }
        });
        try
        {
            return this.mapper.readValue(jsonObject.toString(), PriceMaster.class);

        } catch (Exception e) {
            NotificationUtil.showNotification("Lookup failed from Server, contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            throw new RuntimeException(e);
        }
    }

    public PriceMaster getHardwareRateDetails(String rateId, Date priceDate, String city)
    {
        JSONObject jsonObject = dataProviderMode.getResource("proposal/hardwareratedetails", new HashMap<String, String>(){
            {
                put("rateId", rateId + "");
                put("priceDate", priceDate + "");
                put("city",city + "");
            }
        });
        try
        {
            return this.mapper.readValue(jsonObject.toString(), PriceMaster.class);

        } catch (Exception e) {
            NotificationUtil.showNotification("Lookup failed from Server, contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            throw new RuntimeException(e);
        }
    }

    public boolean updatePriceForDraftProposals(int id) {
        JSONObject jsonObject = dataProviderMode.postResource("proposal/updatepricefordraftproposals",  "{\"priceDate\": " + id + "}");
        return !jsonObject.has("error");

    }

    public ProposalHeader updatePriceForNewProposal(ProposalHeader proposalHeader) {
            try {
                String productJson = this.mapper.writeValueAsString(proposalHeader);
                JSONObject jsonObject = dataProviderMode.postResource(
                        "priceupdate/updatepricefornewproposal", productJson);

                return this.mapper.readValue(jsonObject.toString(), ProposalHeader.class);
            } catch (IOException e) {
                throw new RuntimeException("Couldn't update prices", e);
            }
        }


    public PriceMaster getFactorRatePriceDetails(String rateId, Date priceDate, String city)
    {
        JSONObject jsonObject = dataProviderMode.getResource("proposal/ratefactordetailsfromhandler", new HashMap<String, String>(){
            {
                put("rateId", rateId + "");
                put("priceDate", priceDate + "");
                put("city",city + "");
            }
        });
        try
        {
            return this.mapper.readValue(jsonObject.toString(), PriceMaster.class);

        } catch (Exception e) {
            NotificationUtil.showNotification("Lookup failed from Server, contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            throw new RuntimeException(e);
        }
    }

    public List<RateCard> getFactorRateCodeDetails(String rateCardId)
    {
        JSONArray array = dataProviderMode.getResourceArray("proposal/rcodedetails", new HashMap<String, String>(){
            {
                put("rateCardId", rateCardId);
            }
        });
        try {
            RateCard[] items = this.mapper.readValue(array.toString(), RateCard[].class);
            return new ArrayList<>(Arrays.asList(items));
        } catch (Exception e) {
            NotificationUtil.showNotification("Lookup failed from Server, contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            throw new RuntimeException(e);
        }
    }

    public List<ModuleComponent> getModuleAccessoryhwDetails(String modulecode)
    {
        JSONArray array = dataProviderMode.getResourceArray("proposal/moduleacchwdetails", new HashMap<String, String>(){
            {
                put("modulecode", modulecode);
            }
        });
        try {
            ModuleComponent[] items = this.mapper.readValue(array.toString(), ModuleComponent[].class);
            return new ArrayList<>(Arrays.asList(items));
        } catch (Exception e) {
            NotificationUtil.showNotification("Lookup failed from Server, contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            throw new RuntimeException(e);
        }
    }

    public List<Product> getAllProducts(String productCategoryCode) {
        JSONArray jsonArray = dataProviderMode.getResourceArray("proposal/selectallproducts", new HashMap<String, String>() {
            {
                put("productCategoryCode", productCategoryCode);

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

    /*public ProposalVersion createNewProductLibrary(ProposalVersion proposalVersion) {
        try {
            String productJson = this.mapper.writeValueAsString(proposalVersion);
            JSONObject jsonObject = dataProviderMode.postResource(
                    "product/createnew", productJson);

            return this.mapper.readValue(jsonObject.toString(), ProposalVersion.class);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't map modules", e);
        }

    }*/

    public boolean InsertProductLibrary(ProductLibrary product) {
        try {
            product.setUpdatedBy(getUserId());
            String productJson = this.mapper.writeValueAsString(product);
            LOG.debug("Product json:" + productJson);
            JSONObject jsonObject = dataProviderMode.postResource(
                    "product/insertproductlibray", productJson);
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

    public boolean UpdateProductLibrary(ProductLibrary product) {
        try {
            product.setUpdatedBy(getUserId());
            String productJson = this.mapper.writeValueAsString(product);
            LOG.debug("Product json:" + productJson);
            JSONObject jsonObject = dataProviderMode.postResource(
                    "product/updateproductlibray", productJson);
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

    public List<ProductLibrary> getProductsLibrary(String productTitle,String collection) {
        try{
        JSONArray jsonArray = dataProviderMode.getResourceArray("proposal/searchproductlibrary", new HashMap<String, String>() {
            {
                put("productTitle", URLEncoder.encode(productTitle,"UTF-8"));
                put("collection", URLEncoder.encode(collection,"UTF-8"));
            }
        });
            ProductLibrary[] items = this.mapper.readValue(jsonArray.toString(), ProductLibrary[].class);
            return new ArrayList<>(Arrays.asList(items));
        }
        catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<ProductLibrary> getAllProductsLibrary()
    {
        JSONArray jsonArray = dataProviderMode.getResourceArray("proposal/selectproductlibrary", new HashMap<String, String>() {
            {

            }
        });
        try
        {
            ProductLibrary[] items = this.mapper.readValue(jsonArray.toString(), ProductLibrary[].class);
            return new ArrayList<>(Arrays.asList(items));
        }
        catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public ProposalVersion AddToProposalProduct(ProductLibrary proposalVersion) {
        try {
            String productJson = this.mapper.writeValueAsString(proposalVersion);
            JSONObject jsonObject = dataProviderMode.postResource(
                    "proposal/addtoproductlibrary", productJson);

            return this.mapper.readValue(jsonObject.toString(), ProposalVersion.class);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't map modules", e);
        }
    }

    public List<ProductLibrary> getProductsLibraryBasedonId(String id)
    {
        JSONArray jsonArray = dataProviderMode.getResourceArray("proposal/selectlibrarybasedonid", new HashMap<String, String>() {
            {
                put("id",id);
            }
        });
        try
        {
            ProductLibrary[] items = this.mapper.readValue(jsonArray.toString(), ProductLibrary[].class);
            return new ArrayList<>(Arrays.asList(items));
        }
        catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<ProductLibraryMaster> getProductsubcategory(String category)
    {
        try
        {
            JSONArray jsonArray = dataProviderMode.getResourceArray("proposal/selectcategorybasedonproduct", new HashMap<String, String>() {
                {

                    put("category",URLEncoder.encode(category,"UTF-8"));
                }
            });
            ProductLibraryMaster[] items = this.mapper.readValue(jsonArray.toString(), ProductLibraryMaster[].class);
            return new ArrayList<>(Arrays.asList(items));
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<ProductLibrary> getproductlibraryimage(String image)
    {
        LOG.info("image parameter" +image);
        JSONArray jsonArray = dataProviderMode.getResourceArray("cloudinaryfileupload/image", new HashMap<String, String>() {
            {
                put("imagepath",image);
            }
        });
        try
        {
            ProductLibrary[] items = this.mapper.readValue(jsonArray.toString(), ProductLibrary[].class);
            return new ArrayList<>(Arrays.asList(items));
        }
        catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public CloudinaryImageUrl addToCloudinary(CloudinaryImageUrl cloudinaryImageUrl)
    {
        try {

            String faJson = this.mapper.writeValueAsString(cloudinaryImageUrl);
            JSONObject jsonObject = dataProviderMode.postResource(
                    "cloudinaryfileupload/", faJson);
            if (!jsonObject.has("error")) {
                return this.mapper.readValue(jsonObject.toString(), CloudinaryImageUrl.class);

            } else {
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException("Couldn't add proposal doc", e);
        }
    }

    public boolean updateCrmPrice(SendToCRM sendToCRM)
    {
        try {
            String baseCrmUrl = "http://52.66.107.178/mygubbi_crm/rest_update_opp.php";
            String final_amount = String.valueOf(sendToCRM.getFinal_proposal_amount_c());
            String estimated_project_cost = String.valueOf(sendToCRM.getEstimated_project_cost_c());
        /*
                    JSONObject jsonObject = dataProviderMode.postResourceWithUrl("http://52.66.107.178/mygubbi_crm/rest_update_opp.php", "{\"opportunity_name\": " + "\"" + crmId + "\"" + "," + "\"final_proposal_amount_c\" : " + finalProposalAmount + "," + "\"estimated_project_cost_c\" : " + estimatedProjectCost  + "," + "\"estimated_project_cost_c\" : " + "\"" + quoteNo + "\""   + "}");
        */
            JSONResource jsonObject = dataProviderMode.postResourceWithUrlForCrm(baseCrmUrl, sendToCRM.getOpportunity_name(),final_amount,estimated_project_cost,sendToCRM.getQuotation_number_c());
            LOG.debug("Json Object : "+ jsonObject.toString());
//            return this.mapper.readValue(jsonObject.toString(), JSONObject.class);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCrmPriceOnPublish(SendToCRMOnPublish sendToCRM)
    {
        try {
            String baseCrmUrl = "http://52.66.107.178/mygubbi_crm/rest_update_opp.php";
            String estimated_project_cost = String.valueOf(sendToCRM.getEstimated_project_cost_c());
        /*
                    JSONObject jsonObject = dataProviderMode.postResourceWithUrl("http://52.66.107.178/mygubbi_crm/rest_update_opp.php", "{\"opportunity_name\": " + "\"" + crmId + "\"" + "," + "\"final_proposal_amount_c\" : " + finalProposalAmount + "," + "\"estimated_project_cost_c\" : " + estimatedProjectCost  + "," + "\"estimated_project_cost_c\" : " + "\"" + quoteNo + "\""   + "}");
        */
            JSONResource jsonObject = dataProviderMode.postResourceWithUrlForCrmOnPublish(baseCrmUrl, sendToCRM.getOpportunity_name(),estimated_project_cost,sendToCRM.getQuotation_number_c());
            LOG.debug("Json Object : "+ jsonObject.toString());
//            return this.mapper.readValue(jsonObject.toString(), JSONObject.class);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args)
    {
        ProposalDataProvider proposalDataProvider = new ProposalDataProvider(new RestDataProviderMode());
        SendToCRM sendToCRM = new SendToCRM();
        sendToCRM.setEstimated_project_cost_c(8000);
        sendToCRM.setFinal_proposal_amount_c(8000);
        sendToCRM.setOpportunity_name("SAL-1701-000951");
        sendToCRM.setQuotation_number_c("BLR-111-111");
        boolean jsonObject = proposalDataProvider.updateCrmPrice(sendToCRM);
        System.out.println(jsonObject);
    }

    public List<UserProfile> getUserProfileDetails(String crmID) {
        JSONArray jsonArray = dataProviderMode.getResourceArray("proposal/selectuserprofiledata", new HashMap<String, String>() {
            {
                /*put("crmId", crmID);*/
            }
        });
        try {
            return this.mapper.readValue(jsonArray.toString(), new TypeReference<List<UserProfile>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /*public UserProfile getUserProfileDetails(String crmID) {
        JSONObject jsonArray = dataProviderMode.getResource("proposal/selectuserprofiledata", new HashMap<String, String>() {
            {
                put("crmId", crmID);

            }
        });
        try {
            return this.mapper.readValue(jsonArray.toString(), UserProfile.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }*/

    public List<UserProfile> getUserProfileDetailsonCRMId(String crmID) {
        JSONArray jsonArray = dataProviderMode.getResourceArray("proposal/userprofiledatabasedoncrmid", new HashMap<String, String>() {
            {
                put("crmId", crmID);
            }
        });
        try {
            return this.mapper.readValue(jsonArray.toString(), new TypeReference<List<UserProfile>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

    }

    public List<HandleMaster> getHandleTitle(String type)
    {
        try {
            JSONArray jsonArray = dataProviderMode.getResourceArray("module/selecthandletitle", new HashMap<String, String>() {
                {
                        //put("mgCode",URLEncoder.encode(mgcode, "UTF-8"));
                        put("type",URLEncoder.encode(type, "UTF-8"));

                }
            });
            HandleMaster[] items = this.mapper.readValue(jsonArray.toString(), HandleMaster[].class);
            return new ArrayList<>(Arrays.asList(items));
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    public List<MGModule> retrieveModuleDetails(String mgcode)
    {
        try {
            JSONArray jsonArray = dataProviderMode.getResourceArray("module/checkhandlepresent", new HashMap<String, String>() {
                {
                    put("code",URLEncoder.encode(mgcode, "UTF-8"));
                }
            });
            MGModule[] items = this.mapper.readValue(jsonArray.toString(), MGModule[].class);
            return new ArrayList<>(Arrays.asList(items));
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<HandleMaster> getHandleFinish(String Title,String type) {
        try {
            JSONArray jsonArray = dataProviderMode.getResourceArray("module/selecthandlefinish", new HashMap<String, String>() {
                {
                    put("title", URLEncoder.encode(Title, "UTF-8"));
                    put("type", type);
                }
            });
            HandleMaster[] items = this.mapper.readValue(jsonArray.toString(), HandleMaster[].class);
            return new ArrayList<>(Arrays.asList(items));
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<HandleMaster> getHandleThickness(String Title,String finish,String type) {
        try {
            JSONArray jsonArray = dataProviderMode.getResourceArray("module/selecthandleThickness", new HashMap<String, String>() {
                {
                    put("finish", URLEncoder.encode(finish, "UTF-8"));
                    put("title", URLEncoder.encode(Title, "UTF-8"));
                    put("type", URLEncoder.encode(type, "UTF-8"));
                }
            });
            HandleMaster[] items = this.mapper.readValue(jsonArray.toString(), HandleMaster[].class);
            return new ArrayList<>(Arrays.asList(items));
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    public List<LookupItem> getHinges(String LookupType) {
        try {
            JSONArray jsonArray = dataProviderMode.getResourceArray("module/selectfromcodemaster", new HashMap<String, String>() {
                {
                    put("lookupType", URLEncoder.encode(LookupType, "UTF-8"));
                }
            });
            LookupItem[] items = this.mapper.readValue(jsonArray.toString(), LookupItem[].class);
            return new ArrayList<>(Arrays.asList(items));
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    public List<ProductLibraryMaster> getProductcategory() {
        try {
            JSONArray jsonArray = dataProviderMode.getResourceArray("proposal/selectcategory", new HashMap<String, String>() {
                {

                }
            });
            ProductLibraryMaster[] items = this.mapper.readValue(jsonArray.toString(), ProductLibraryMaster[].class);
            return new ArrayList<>(Arrays.asList(items));
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    public List<ProductLibrary> getProducttitle(String productCategoryCode,String subCategory)
    {
        try {
            JSONArray jsonArray = dataProviderMode.getResourceArray("proposal/selectproductname", new HashMap<String, String>() {
                {
                    put("productCategoryCode",URLEncoder.encode(productCategoryCode, "UTF-8"));
                    put("subCategory",URLEncoder.encode(subCategory, "UTF-8"));
                }
            });
            ProductLibrary[] items = this.mapper.readValue(jsonArray.toString(), ProductLibrary[].class);
            return new ArrayList<>(Arrays.asList(items));
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<ProductLibrary> getProductLibrary(String productCategoryCode,String subCategory,String title)
    {
        try {
            JSONArray jsonArray = dataProviderMode.getResourceArray("proposal/selectproductfromlibrary", new HashMap<String, String>() {
                {
                    put("productCategoryCode",URLEncoder.encode(productCategoryCode, "UTF-8"));
                    put("subCategory",URLEncoder.encode(subCategory, "UTF-8"));
                    put("productTitle",URLEncoder.encode(title, "UTF-8"));
                }
            });
            ProductLibrary[] items = this.mapper.readValue(jsonArray.toString(), ProductLibrary[].class);
            return new ArrayList<>(Arrays.asList(items));
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<MGModule> checksqftCalculation(String mgcode)
    {
        try {
            JSONArray jsonArray = dataProviderMode.getResourceArray("module/sqftcalculation", new HashMap<String, String>() {
                {
                    put("code",URLEncoder.encode(mgcode, "UTF-8"));
                }
            });
            MGModule[] items = this.mapper.readValue(jsonArray.toString(), MGModule[].class);
            return new ArrayList<>(Arrays.asList(items));
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<HandleMaster> getHandles(String type,String Title,String finish,String thickness)
    {
        try {
            JSONArray jsonArray = dataProviderMode.getResourceArray("module/selecthandleCode", new HashMap<String, String>() {
                {
                    put("type", URLEncoder.encode(type, "UTF-8"));
                    put("title", URLEncoder.encode(Title, "UTF-8"));
                    put("finish", URLEncoder.encode(finish, "UTF-8"));
                    put("thickness", URLEncoder.encode(thickness, "UTF-8"));
                }
            });
            HandleMaster[] items = this.mapper.readValue(jsonArray.toString(), HandleMaster[].class);
            return new ArrayList<>(Arrays.asList(items));
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    public List<HandleMaster> getHandleArray(String code)
    {
        try {
            JSONArray jsonArray = dataProviderMode.getResourceArray("module/handledata", new HashMap<String, String>() {
                {
                    put("code", URLEncoder.encode(code, "UTF-8"));
                }
            });
            HandleMaster[] items = this.mapper.readValue(jsonArray.toString(), HandleMaster[].class);
            return new ArrayList<>(Arrays.asList(items));
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<AccessoryDetails> getAccessoryhandleDetails(String apcode,String type)
    {
        JSONArray array = dataProviderMode.getResourceArray("module/accessoryhandledata", new HashMap<String, String>() {
            {
                put("apcode", apcode);
                put("type", type);
            }
        });
        try {
            AccessoryDetails[] items = this.mapper.readValue(array.toString(), AccessoryDetails[].class);
            return new ArrayList<>(Arrays.asList(items));
        } catch (Exception e) {
            NotificationUtil.showNotification("Lookup failed from Server, contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            throw new RuntimeException(e);
        }
    }
    public List<HandleMaster> getHandleImages(String type,String Title,String finish)
    {
        try {
            JSONArray jsonArray = dataProviderMode.getResourceArray("module/selecthandleimage", new HashMap<String, String>() {
                {
                    put("type", URLEncoder.encode(type, "UTF-8"));
                    put("title", URLEncoder.encode(Title, "UTF-8"));
                    put("finish", URLEncoder.encode(finish, "UTF-8"));
                }
            });
            HandleMaster[] items = this.mapper.readValue(jsonArray.toString(), HandleMaster[].class);
            return new ArrayList<>(Arrays.asList(items));
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Finish> getShutterCodes(String title) {
        JSONArray array = dataProviderMode.getResourceArray("module/shuttercodeforcombo", new HashMap<String, String>() {
            {
                put("finishCode",title);
            }
        });
        try {
            Finish[] items = this.mapper.readValue(array.toString(), Finish[].class);
            return new ArrayList<>(Arrays.asList(items));
        } catch (Exception e) {
            e.printStackTrace();
            NotificationUtil.showNotification("Lookup failed from Server, contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            return new ArrayList<>();
        }
    }

    public PriceMaster getHandleAndKnobRateDetails(String rateId, Date priceDate, String city)
    {
        JSONObject jsonObject = dataProviderMode.getResource("proposal/handleknobdetails", new HashMap<String, String>(){
            {
                put("rateId", rateId + "");
                put("priceDate", priceDate + "");
                put("city",city + "");
            }
        });
        try
        {
            return this.mapper.readValue(jsonObject.toString(), PriceMaster.class);

        } catch (Exception e) {
            NotificationUtil.showNotification("Lookup failed from Server, contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            throw new RuntimeException(e);
        }
    }

    public List<User> getUsersByEmail(String Email) {
        JSONArray array = dataProviderMode.getResourceArray("user/listbyemail", new HashMap<String, String>() {
            {
                put("email", Email);
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
    public List<ModuleHingeMap> getHinges(String moduleCode,String type)
    {
        LOG.debug("hinges inside proposal data provider : " + moduleCode  +" :" + type);
        try {
            JSONArray jsonArray = dataProviderMode.getResourceArray("module/selecthingedata", new HashMap<String, String>() {
                {
                    put("moduleCode", URLEncoder.encode(moduleCode, "UTF-8"));
                    put("type", URLEncoder.encode(type, "UTF-8"));
                }
            });
            ModuleHingeMap[] items = this.mapper.readValue(jsonArray.toString(), ModuleHingeMap[].class);
            return new ArrayList<>(Arrays.asList(items));
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
