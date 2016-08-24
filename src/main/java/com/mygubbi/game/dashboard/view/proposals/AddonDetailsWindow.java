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

import java.io.File;
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
    private ComboBox brand;
    private ComboBox catalogueCode;
    private TextField title;
    private TextField uom;
    private TextField rate;
    private TextField quantity;
    private TextField amount;
    private Button applyButton;
    private Image addonImage;
    private String imageBasePath = ConfigHolder.getInstance().getImageBasePath();

    private String originalImagePath;
    private BeanContainer<String, AddonProductType> productTypeBeanContainer;
    private BeanContainer<String, AddonProductItem> catalogueCodeBeanContainer;
    private BeanContainer<String, AddonBrand> brandBeanContainer;
    private BeanContainer<String, AddonCategory> categoryBeanContainer;
    private boolean readOnly;

    public AddonDetailsWindow(AddonProduct addonProduct, boolean readOnly, String title, boolean isProposalAddon) {
        this.addonProduct = addonProduct;
        this.readOnly = readOnly;
        this.isProposalAddon = isProposalAddon;
        this.originalImagePath = this.addonProduct.getImagePath();
        this.binder.setItemDataSource(this.addonProduct);
        setModal(true);
        removeCloseShortcut(ShortcutAction.KeyCode.ESCAPE);
        setWidth("60%");
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
        this.catalogueCodeChanged(null);

        handleState();
    }

    private void handleState() {
        if (readOnly) {
            category.setReadOnly(true);
            productType.setReadOnly(true);
            brand.setReadOnly(true);
            catalogueCode.setReadOnly(true);
            title.setReadOnly(true);
            rate.setReadOnly(true);
            quantity.setReadOnly(true);
            applyButton.setEnabled(false);
        }
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

        this.brand = getBrandCombo();
        this.brand.setRequired(true);
        binder.bind(this.brand, AddonProduct.BRAND_CODE);
        this.brand.addValueChangeListener(this::brandChanged);
        formLayoutLeft.addComponent(this.brand);

        this.catalogueCode = getCatalogueCodeCombo();
        this.catalogueCode.setRequired(true);
        binder.bind(this.catalogueCode, AddonProduct.CATALOGUE_CODE);
        this.catalogueCode.addValueChangeListener(this::catalogueCodeChanged);
        formLayoutLeft.addComponent(this.catalogueCode);

        this.title = new TextField("Title");
        this.title.setRequired(true);
        this.title.setNullRepresentation("");
        binder.bind(this.title, AddonProductItem.TITLE);
        formLayoutLeft.addComponent(this.title);

        FormLayout formLayoutRight = new FormLayout();
        formLayoutRight.setSizeFull();
        formLayoutRight.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        horizontalLayout.addComponent(formLayoutRight);

        this.uom = new TextField("UOM");
        this.uom.setNullRepresentation("");
        binder.bind(this.uom, AddonProduct.UOM);
        this.uom.setReadOnly(true);
        formLayoutRight.addComponent(this.uom);

        this.rate = new TextField("Rate");
        this.rate.setRequired(true);
        binder.bind(this.rate, AddonProduct.RATE);
        if (category.getValue() == null || (Boolean) category.getItem(category.getValue()).getItemProperty(AddonCategory.RATE_READ_ONLY).getValue()) {
            this.rate.setReadOnly(true);
        }
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

        String prevCode = (String) productType.getValue();

        List<AddonProductType> list = proposalDataProvider.getAddonProductTypes(BLANK_ROOM_CODE, (String) this.category.getValue());
        this.productTypeBeanContainer.removeAllItems();
        this.productTypeBeanContainer.addAll(list);

        if (StringUtils.isNotEmpty(prevCode) && list.stream().anyMatch(addonProductType -> addonProductType.getProductTypeCode().equals(prevCode))) {
            this.productType.setValue(prevCode);
        } else {
            this.productType.setValue(this.productType.getItemIds().iterator().next());
        }

        if ((Boolean) category.getItem(category.getValue()).getItemProperty(AddonCategory.RATE_READ_ONLY).getValue()) {

            if (catalogueCode.getValue() != null) {
                Double rate = (Double) catalogueCode.getItem(catalogueCode.getValue()).getItemProperty(AddonProductItem.RATE).getValue();
                this.rate.setReadOnly(false);
                this.rate.setValue(rate + "");
                this.rate.setReadOnly(true);
            }
        } else {
            this.rate.setReadOnly(false);
        }

        checkApply();
    }

    private void catalogueCodeChanged(Property.ValueChangeEvent valueChangeEvent) {

        AddonProductItem addonProductItem = ((BeanItem<AddonProductItem>) this.catalogueCode.getItem(catalogueCode.getValue())).getBean();

        this.uom.setReadOnly(false);
        this.uom.setValue(addonProductItem.getUom());
        this.uom.setReadOnly(true);

        this.title.setValue(addonProductItem.getTitle());

        this.addonProduct.setImagePath(addonProductItem.getImagePath());
        this.addonImage.setSource(new FileResource(new File(imageBasePath + addonProductItem.getImagePath())));

        boolean wasReadOnly = this.rate.isReadOnly();
        this.rate.setReadOnly(false);
        this.rate.setValue(addonProductItem.getRate() + "");
        this.rate.setReadOnly(wasReadOnly);

        if (StringUtils.isEmpty(this.quantity.getValue()) || Double.parseDouble(this.quantity.getValue().replaceAll(",", "")) <= 0) {
            this.quantity.setValue("1");
        }
        checkApply();
    }

    private void brandChanged(Property.ValueChangeEvent valueChangeEvent) {
        String prevCode = (String) this.catalogueCode.getValue();

        List<AddonProductItem> list = proposalDataProvider.getAddonProductItems((String) this.productType.getValue(), (String) this.brand.getValue());
        this.catalogueCodeBeanContainer.removeAllItems();
        this.catalogueCodeBeanContainer.addAll(list);
        Object next = this.catalogueCode.getItemIds().iterator().next();
        this.catalogueCode.setValue(next);
        if (next.equals(prevCode)) {
            this.catalogueCodeChanged(null);
        }
        checkApply();
    }

    private void productTypeChanged(Property.ValueChangeEvent valueChangeEvent) {
        String prevCode = (String) brand.getValue(); //addonProduct.getBrandCode();

        List<AddonBrand> list = proposalDataProvider.getAddonBrands((String) this.productType.getValue());
        this.brandBeanContainer.removeAllItems();
        this.brandBeanContainer.addAll(list);
        Object next = this.brand.getItemIds().iterator().next();
        this.brand.setValue(next);

        if (next.equals(prevCode)) {
            this.brandChanged(null);
        }

        checkApply();
    }

    private ComboBox getProductTypeCombo() {
        List<AddonProductType> list = proposalDataProvider.getAddonProductTypes(BLANK_ROOM_CODE, (String) this.category.getValue());

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

    private ComboBox getCatalogueCodeCombo() {
        List<AddonProductItem> list = proposalDataProvider.getAddonProductItems((String) this.productType.getValue(), (String) this.brand.getValue());

        catalogueCodeBeanContainer = new BeanContainer<>(AddonProductItem.class);
        catalogueCodeBeanContainer.setBeanIdProperty(AddonProductItem.CATALOGUE_CODE);
        catalogueCodeBeanContainer.addAll(list);

        ComboBox select = new ComboBox("Product");
        select.setNullSelectionAllowed(false);
        select.setContainerDataSource(catalogueCodeBeanContainer);
        select.setItemCaptionPropertyId(AddonProductItem.TITLE);
        if (StringUtils.isNotEmpty(addonProduct.getCatalogueCode())) {
            select.setValue(addonProduct.getCatalogueCode());
        } else {
            select.setValue(select.getItemIds().iterator().next());
            addonProduct.setCatalogueCode(select.getValue().toString());
        }
        return select;
    }

    private ComboBox getBrandCombo() {
        List<AddonBrand> list = proposalDataProvider.getAddonBrands((String) this.productType.getValue());

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
                    binder.commit();

                    addonProduct.setCategory(this.categoryBeanContainer.getItem(this.category.getValue()).getBean().getCategoryCode());
                    addonProduct.setProductType(this.productTypeBeanContainer.getItem(this.productType.getValue()).getBean().getProductTypeCode());
                    addonProduct.setBrand(this.brandBeanContainer.getItem(this.brand.getValue()).getBean().getBrandCode());
                    addonProduct.setCode(this.catalogueCodeBeanContainer.getItem(this.catalogueCode.getValue()).getBean().getCode());

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

    public static void open(AddonProduct addon, boolean readOnly, String title, boolean isProposalAddon) {
        Window w = new AddonDetailsWindow(addon, readOnly, title, isProposalAddon);
        UI.getCurrent().addWindow(w);
        w.focus();

    }
}
