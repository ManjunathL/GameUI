package com.mygubbi.game.dashboard.view.proposals;

import com.mygubbi.game.dashboard.ServerManager;
import com.mygubbi.game.dashboard.config.ConfigHolder;
import com.mygubbi.game.dashboard.data.AddonBrand;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.domain.*;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.mygubbi.game.dashboard.event.ProposalEvent;
import com.mygubbi.game.dashboard.view.NotificationUtil;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FileResource;
import com.vaadin.server.Responsive;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.sql.Date;
import java.util.List;

/**
 * Created by nitinpuri on 02-06-2016.
 */
public class AddonDetailsWindow extends Window {

    private static final String BLANK_ROOM_CODE = "";
    private final boolean isProposalAddon;
    private ProposalDataProvider proposalDataProvider = ServerManager.getInstance().getProposalDataProvider();
    private final AddonProduct addonProduct;
    private final BeanFieldGroup<AddonProduct> binder = new BeanFieldGroup<>(AddonProduct.class);
    private ComboBox category;
    private ComboBox productType;
    private ComboBox productSubtype;
    private ComboBox brand;
    private ComboBox product;
    private TextArea title;
    private TextField uom;
    private TextField rate;
    private TextField quantity;
    private TextField amount;
    private TextField description;
    private Button applyButton;
    private Image addonImage;
    private String imageBasePath = ConfigHolder.getInstance().getImageBasePath();

    private String originalImagePath;
    private BeanContainer<String, AddonProductType> productTypeBeanContainer;
    private BeanContainer<String, AddonProductSubtype> productSubtypeBeanContainer;
    private BeanContainer<String, AddonProductItem> productCodeBeanContainer;
    private BeanContainer<String, AddonBrand> brandBeanContainer;
    private BeanContainer<String, AddonCategory> categoryBeanContainer;
    private boolean readOnly;
    private ProposalVersion proposalVersion;
    private Date priceDate;
    private String city;
    Double rateToBeUsed;

    private static final Logger LOG = LogManager.getLogger(AddonProduct.class);


    public AddonDetailsWindow(AddonProduct addonProduct, String title, boolean isProposalAddon, ProposalVersion proposalVersion, ProposalHeader proposalHeader) {
        this.addonProduct = addonProduct;
        this.isProposalAddon = isProposalAddon;
        this.originalImagePath = this.addonProduct.getImagePath();
        this.city = proposalHeader.getPcity();
        this.proposalVersion = proposalVersion;
        this.binder.setItemDataSource(this.addonProduct);
        setModal(true);
        removeCloseShortcut(ShortcutAction.KeyCode.ESCAPE);
        setWidth("90%");
        setClosable(false);
        setCaption("Addon Configuration for " + title);

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(new MarginInfo(true, true, true, true));
        Responsive.makeResponsive(this);

        HorizontalLayout horizontalLayout2 = new HorizontalLayout();
        horizontalLayout2.setMargin(new MarginInfo(false, false, false, false));
        horizontalLayout2.setSizeFull();
        horizontalLayout2.addComponent(buildAddonSelectionsComponent());
        verticalLayout.addComponent(horizontalLayout2);
        horizontalLayout2.setHeightUndefined();

        Component footerLayOut = buildFooter();
        verticalLayout.addComponent(footerLayOut);

        setContent(verticalLayout);
        this.productCodeChanged(null);

        handleState();
    }

    private void handleState() {
        ProposalVersion.ProposalStage proposalStage = ProposalVersion.ProposalStage.valueOf(proposalVersion.getInternalStatus());
        switch (proposalStage) {
            case Draft:
                title.setReadOnly(true);
                break;
            case Published:
                setComponentsReadOnly();
                break;
            case Confirmed:
                setComponentsReadOnly();
                break;
            case Locked:
                setComponentsReadOnly();
                break;
            case DSO:
                setComponentsReadOnly();
                break;
            case PSO:
                setComponentsReadOnly();
                break;
            default:
                throw new RuntimeException("Unknown State");
        }
    }

    private void setComponentsReadOnly() {
        category.setReadOnly(true);
        productType.setReadOnly(true);
        productSubtype.setReadOnly(true);
        brand.setReadOnly(true);
        product.setReadOnly(true);
        title.setReadOnly(true);
        uom.setReadOnly(true);
        quantity.setReadOnly(true);
        rate.setReadOnly(true);
        amount.setReadOnly(true);
    }

    private Component buildAddonSelectionsComponent() {

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSizeFull();
        verticalLayout.addComponent(horizontalLayout);

        FormLayout formLayoutLeft = new FormLayout();
        formLayoutLeft.setSizeFull();
        formLayoutLeft.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        horizontalLayout.addComponent(formLayoutLeft);
        horizontalLayout.setExpandRatio(formLayoutLeft,0.4f);


        this.category = getCategoryCombo();
        this.category.setRequired(true);
        binder.bind(this.category, AddonProduct.ADDON_CATEGORY_CODE);
        this.category.addValueChangeListener(this::categoryChanged);
        formLayoutLeft.addComponent(this.category);

        this.productType = getProductTypeCombo();
        this.productType.setRequired(true);
        binder.bind(this.productType, AddonProduct.PRODUCT_TYPE_CODE);
        this.productType.addValueChangeListener(this::productTypeChanged);
        formLayoutLeft.addComponent(this.productType);

        this.productSubtype = getProductSubtypeCombo();
        this.productSubtype.setRequired(true);
        binder.bind(this.productSubtype, AddonProduct.PRODUCT_SUBTYPE_CODE);
        this.productSubtype.addValueChangeListener(this::productSubtypeChanged);
        formLayoutLeft.addComponent(this.productSubtype);


        this.brand = getBrandCombo();
        this.brand.setRequired(true);
        binder.bind(this.brand, AddonProduct.BRAND_CODE);
        this.brand.addValueChangeListener(this::brandChanged);
        formLayoutLeft.addComponent(this.brand);

        this.product = getProductCodeCombo();
        this.product.setRequired(true);
        binder.bind(this.product, AddonProduct.PRODUCT);
        this.product.addValueChangeListener(this::productCodeChanged);
        formLayoutLeft.addComponent(this.product);

        FormLayout formLayoutRight = new FormLayout();
        formLayoutRight.setSizeFull();
        formLayoutRight.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        horizontalLayout.addComponent(formLayoutRight);
        horizontalLayout.setExpandRatio(formLayoutRight,0.4f);


        this.title = new TextArea("Specification");
        this.title.setHeight("45px");

        binder.bind(this.title, AddonProductItem.TITLE);
        formLayoutRight.addComponent(this.title);

        this.uom = new TextField("UOM");
        this.uom.setNullRepresentation("");
        binder.bind(this.uom, AddonProduct.UOM);
        this.uom.setReadOnly(true);
        formLayoutRight.addComponent(this.uom);

        this.rate = new TextField("Rate");
        this.rate.setRequired(true);
        this.rate.addValueChangeListener(this::rateChanged);
        formLayoutRight.addComponent(this.rate);

        this.quantity = new TextField("Qty");
        this.quantity.setRequired(true);
        binder.bind(this.quantity, AddonProduct.QUANTITY);
        if (addonProduct.getQuantity() == 0) {
            this.quantity.setValue("1");
        }
        this.quantity.addValueChangeListener(this::quantityChanged);
        formLayoutRight.addComponent(this.quantity);

        this.amount = new TextField("Amount");
        binder.bind(this.amount, AddonProduct.AMOUNT);
        this.amount.setReadOnly(true);
        formLayoutRight.addComponent(this.amount);


        if (StringUtils.isEmpty(addonProduct.getImagePath())) {
            addonImage = new Image("", new ThemeResource("img/empty-poster.png"));
        } else {
            addonImage = new Image("", new FileResource(new File(imageBasePath + addonProduct.getImagePath())));
        }
        addonImage.setCaption(null);
        addonImage.setHeight("180px");
        addonImage.setWidth("180px");
        horizontalLayout.addComponent(addonImage);
        horizontalLayout.setComponentAlignment(addonImage, Alignment.MIDDLE_CENTER);
        horizontalLayout.setExpandRatio(addonImage,0.2f);


        return verticalLayout;
    }

    private void rateChanged(Property.ValueChangeEvent valueChangeEvent) {

        String rateValue = this.rate.getValue().replaceAll(",", "");
        String quantityValue = this.quantity.getValue().replaceAll(",", "");
        this.amount.setReadOnly(false);
        this.amount.setValue(String.valueOf(Double.parseDouble(rateValue) * Double.parseDouble(quantityValue)));
        this.amount.setReadOnly(true);
        checkApply();
    }

    private void quantityChanged(Property.ValueChangeEvent valueChangeEvent) {
        String rateValue = this.rate.getValue().replaceAll(",", "");
        String quantityValue = this.quantity.getValue().replaceAll(",", "");
        if (Double.parseDouble(quantityValue) <= 0) {
            quantityValue = "1";
            this.quantity.setValue("1");
        }
        this.amount.setReadOnly(false);
        this.amount.setValue(String.valueOf(Double.parseDouble(rateValue) * Double.parseDouble(quantityValue)));
        this.amount.setReadOnly(true);
        checkApply();
    }

    private void categoryChanged(Property.ValueChangeEvent valueChangeEvent) {

        String prevCode = addonProduct.getProductTypeCode();

        List<AddonProductType> list = proposalDataProvider.getAddonProductTypes( (String) this.category.getValue());
        this.productTypeBeanContainer.removeAllItems();
        this.productTypeBeanContainer.addAll(list);
        for (AddonProductType addonProductType : list)
        {
            LOG.debug("Addon ProductType :" + addonProductType.toString());
        }
        Object next = this.productType.getItemIds().iterator().next();
        productType.setValue(next);


        if (next.equals(prevCode)) {
            this.productTypeChanged(null);
        }

/*

        if ((Boolean) category.getItem(category.getValue()).getItemProperty(AddonCategory.RATE_READ_ONLY).getValue()) {

            if (product.getValue() != null) {

                if (this.priceDate == null)
                {
                    this.priceDate = new Date(System.currentTimeMillis());
                }
                List<PriceMaster> rate = proposalDataProvider.getAddonRate(this.addonProduct.getCode(),this.priceDate,this.city);
                for (PriceMaster priceMaster : rate)
                {
                   rateToBeUsed = String.valueOf(priceMaster.getPrice());
                }

                this.rate.setReadOnly(false);
                this.rate.setValue(this.rateToBeUsed + "");
                this.rate.setReadOnly(true);
            }
        } else {
            this.rate.setReadOnly(false);
        }
*/

        checkApply();
    }

    private void productTypeChanged(Property.ValueChangeEvent valueChangeEvent) {


        String prevCode = (String) productSubtype.getValue(); //addonProduct.getBrandCode();

        List<AddonProductSubtype> list = proposalDataProvider.getAddonProductSubTypes( (String) this.category.getValue(),(String) this.productType.getValue());
        for (AddonProductSubtype addonProductSubtype : list)
        {
            LOG.debug("product subtype :" + addonProductSubtype.toString());
        }

        this.productSubtypeBeanContainer.removeAllItems();
        this.productSubtypeBeanContainer.addAll(list);
        Object next = this.productSubtype.getItemIds().iterator().next();
        this.productSubtype.setValue(next);
        LOG.debug("Product type value set");

        if (next.equals(prevCode)) {
            LOG.debug("hey");
            this.productSubtypeChanged(null);
        }

        checkApply();
    }


    private void productSubtypeChanged(Property.ValueChangeEvent valueChangeEvent) {


        String prevCode = (String) brand.getValue(); //addonProduct.getBrandCode();

        List<AddonBrand> list = proposalDataProvider.getAddonBrands((String) this.category.getValue(), (String) this.productSubtype.getValue(), (String) this.productType.getValue());
        for (AddonBrand addonBrand : list)
        {
            LOG.debug("Addon brands :" + addonBrand.getBrandCode());
        }
        this.brandBeanContainer.removeAllItems();
        this.brandBeanContainer.addAll(list);
        Object next = this.brand.getItemIds().iterator().next();
        this.brand.setValue(next);

        if (next.equals(prevCode)) {
            this.brandChanged(null);
        }

        checkApply();
    }

    private void brandChanged(Property.ValueChangeEvent valueChangeEvent) {


        String prevCode = (String) this.product.getValue();

        List<AddonProductItem> list = proposalDataProvider.getAddonProductItems((String) this.category.getValue(), (String) this.productType.getValue(),(String) this.productSubtype.getValue(), (String) this.brand.getValue());
        LOG.debug("List :" + list.size() + "|" + list.toString());
        this.productCodeBeanContainer.removeAllItems();
        this.productCodeBeanContainer.addAll(list);
        Object next = this.product.getItemIds().iterator().next();
        this.product.setValue(next);

        if (next.equals(prevCode)) {
            this.productCodeChanged(null);
        }

        checkApply();
    }


    private void productCodeChanged(Property.ValueChangeEvent valueChangeEvent) {

        AddonProductItem addonProductItem = ((BeanItem<AddonProductItem>) this.product.getItem(product.getValue())).getBean();

        LOG.debug("Addon product item : " + addonProductItem.toString());

        this.uom.setReadOnly(false);
        this.uom.setValue(addonProductItem.getUom());
        this.uom.setReadOnly(true);

        this.title.setReadOnly(false);
        this.title.setValue(addonProductItem.getTitle() + "");
        this.title.setReadOnly(true);

        this.addonProduct.setImagePath(addonProductItem.getImagePath());
        this.addonImage.setSource(new FileResource(new File(imageBasePath + addonProductItem.getImagePath())));

        if (this.priceDate == null)
        {
            this.priceDate = new Date(System.currentTimeMillis());
        }
        PriceMaster rate = proposalDataProvider.getAddonRate(addonProductItem.getCode(),this.priceDate,this.city);
       if (rate != null)
        {
            this.rateToBeUsed = rate.getPrice();
        }
        else {
           NotificationUtil.showNotification("Error in Addon pricing",NotificationUtil.STYLE_BAR_ERROR_SMALL);
           return;
       }

        boolean wasReadOnly = this.rate.isReadOnly();
        this.rate.setReadOnly(false);
        this.rate.setValue(this.rateToBeUsed + "");
        this.rate.setReadOnly(wasReadOnly);

        if (StringUtils.isEmpty(this.quantity.getValue()) || Double.parseDouble(this.quantity.getValue().replaceAll(",", "")) <= 0) {
            this.quantity.setValue("1");
        }
        checkApply();
    }


    private ComboBox getProductTypeCombo() {
        List<AddonProductType> list = proposalDataProvider.getAddonProductTypes((String) this.category.getValue());

        productTypeBeanContainer = new BeanContainer<>(AddonProductType.class);
        productTypeBeanContainer.setBeanIdProperty(AddonProductType.PRODUCT_TYPE_CODE);
        productTypeBeanContainer.addAll(list);

        ComboBox select = new ComboBox("Product Type");
        select.setNullSelectionAllowed(false);
        select.setContainerDataSource(productTypeBeanContainer);
        select.setItemCaptionPropertyId(AddonProductType.PRODUCT_TYPE_CODE);
        if (StringUtils.isNotEmpty(addonProduct.getProductTypeCode())) {
            select.setValue(addonProduct.getProductTypeCode());
        } else {
            select.setValue(select.getItemIds().iterator().next());
            addonProduct.setProductTypeCode(select.getValue().toString());
        }
        return select;
    }

   private ComboBox getProductSubtypeCombo() {

        List<AddonProductSubtype> list = proposalDataProvider.getAddonProductSubTypes((String) this.category.getValue(),(String) this.productType.getValue());
        productSubtypeBeanContainer = new BeanContainer<>(AddonProductSubtype.class);
        productSubtypeBeanContainer.setBeanIdProperty(AddonProductSubtype.PRODUCT_SUBTYPE_CODE);
        productSubtypeBeanContainer.addAll(list);

       ComboBox select = new ComboBox("Product Subtype");
        select.setNullSelectionAllowed(false);
        select.setContainerDataSource(productSubtypeBeanContainer);
        select.setItemCaptionPropertyId(AddonProductSubtype.PRODUCT_SUBTYPE_CODE);
        if (StringUtils.isNotEmpty(addonProduct.getProductSubtypeCode())) {
            select.setValue(addonProduct.getProductSubtypeCode());
        } else {
            select.setValue(select.getItemIds().iterator().next());
            addonProduct.setProductSubtypeCode(select.getValue().toString());
        }
        return select;
    }

    private ComboBox getProductCodeCombo() {

        List<AddonProductItem> list = proposalDataProvider.getAddonProductItems((String) this.category.getValue(), (String) this.productType.getValue(),(String) this.productSubtype.getValue(), (String) this.brand.getValue());
        LOG.info("list size" +list.size());
        productCodeBeanContainer = new BeanContainer<>(AddonProductItem.class);
        productCodeBeanContainer.setBeanIdProperty(AddonProductItem.PRODUCT);
        productCodeBeanContainer.addAll(list);



        ComboBox select = new ComboBox("Product");
        select.setNullSelectionAllowed(false);
        select.setContainerDataSource(productCodeBeanContainer);
        select.setItemCaptionPropertyId(AddonProductItem.PRODUCT);
        if (StringUtils.isNotEmpty(addonProduct.getProduct())) {
            select.setValue(addonProduct.getProduct());

        } else {
            select.setValue(select.getItemIds().iterator().next());
            addonProduct.setProduct(select.getValue().toString());
        }

        return select;
    }

    private ComboBox getBrandCombo() {
        List<AddonBrand> list = proposalDataProvider.getAddonBrands((String) this.category.getValue(), (String) this.productSubtype.getValue(), (String) this.productType.getValue());

        brandBeanContainer = new BeanContainer<>(AddonBrand.class);
        brandBeanContainer.setBeanIdProperty(AddonBrand.BRAND_CODE);
        brandBeanContainer.addAll(list);

        ComboBox select = new ComboBox("Brand");
        select.setNullSelectionAllowed(false);
        select.setContainerDataSource(brandBeanContainer);
        select.setItemCaptionPropertyId(AddonBrand.BRAND_CODE);
        if (StringUtils.isNotEmpty(addonProduct.getBrandCode())) {
            select.setValue(addonProduct.getBrandCode());
        } else {
            select.setValue(select.getItemIds().iterator().next());
            addonProduct.setBrandCode(select.getValue().toString());
        }
        return select;
    }

    private ComboBox getCategoryCombo() {
        List<AddonCategory> list = proposalDataProvider.getAddonCategories(BLANK_ROOM_CODE);

        categoryBeanContainer = new BeanContainer<>(AddonCategory.class);
        categoryBeanContainer.setBeanIdProperty(AddonCategory.CATGEORY_CODE);
        categoryBeanContainer.addAll(list);

        ComboBox select = new ComboBox("Category");
        select.setNullSelectionAllowed(false);
        select.setContainerDataSource(categoryBeanContainer);
        select.setItemCaptionPropertyId(AddonCategory.CATGEORY_CODE);
        if (StringUtils.isNotEmpty(addonProduct.getCategoryCode())) {
            select.setValue(addonProduct.getCategoryCode());
        } else {
            Object next = select.getItemIds().iterator().next();
            select.setValue(next);
            addonProduct.setCategoryCode(next.toString());
        }
        return select;
    }

    private Component buildFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.setSizeFull();
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Unit.PERCENTAGE);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.addClickListener((Button.ClickListener) clickEvent -> {
            binder.discard();
            this.addonProduct.setImagePath(this.originalImagePath);
            close();
        });
        cancelBtn.focus();
        footer.addComponent(cancelBtn);
        footer.setSpacing(true);

        applyButton = new Button("Apply");
        applyButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        applyButton.addClickListener(event -> {

            if (!binder.isValid()) {
                NotificationUtil.showNotification("Please ensure all mandatory fields are filled.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            } else {

                try {
                    int seq = 0;
                    binder.commit();
                    List<AddonProduct> addons = proposalDataProvider.getVersionAddons(proposalVersion.getProposalId(),proposalVersion.getVersion());
                    if (addons.size() == 0)
                    {
                        addonProduct.setSeq(1);
                    }
                    else
                    {
                        seq = addons.size()+1;
                        addonProduct.setSeq(seq);
                    }
                    addonProduct.setFromVersion(proposalVersion.getVersion());
                    addonProduct.setCategory(this.categoryBeanContainer.getItem(this.category.getValue()).getBean().getCategoryCode());
                    addonProduct.setProductTypeCode(this.productTypeBeanContainer.getItem(this.productType.getValue()).getBean().getProductTypeCode());
                    addonProduct.setProductSubtypeCode(this.productSubtypeBeanContainer.getItem(this.productSubtype.getValue()).getBean().getProductSubtypeCode());
                    addonProduct.setProduct(this.productCodeBeanContainer.getItem(this.product.getValue()).getBean().getProduct());
                    addonProduct.setBrand(this.brandBeanContainer.getItem(this.brand.getValue()).getBean().getBrandCode());
                    addonProduct.setCode(this.productCodeBeanContainer.getItem(this.product.getValue()).getBean().getCode());
                    LOG.debug("Addon product Class :" + addonProduct.toString());


                        if (isProposalAddon) {
                            DashboardEventBus.post(new ProposalEvent.ProposalAddonUpdated(addonProduct));
                        } else {
                            DashboardEventBus.post(new ProposalEvent.AddonUpdated(addonProduct));
                        }
                    close();
                } catch (FieldGroup.CommitException e) {
                    e.printStackTrace();
                }
            }

        });
        applyButton.focus();
        applyButton.setVisible(true);
        applyButton.setEnabled(this.binder.isValid());

        footer.addComponent(applyButton);
        footer.setComponentAlignment(cancelBtn, Alignment.TOP_RIGHT);

        return footer;
    }

    private void checkApply() {
        if (this.binder.isValid()) {
            this.applyButton.setEnabled(true);
        }
    }

    public static void open(AddonProduct addon, String title, boolean isProposalAddon, ProposalVersion proposalVersion, ProposalHeader proposalHeader) {
        Window w = new AddonDetailsWindow(addon, title, isProposalAddon,proposalVersion, proposalHeader);
        UI.getCurrent().addWindow(w);
        w.focus();

    }
}
