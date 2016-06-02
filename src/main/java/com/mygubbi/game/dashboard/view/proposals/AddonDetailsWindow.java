package com.mygubbi.game.dashboard.view.proposals;

import com.mygubbi.game.dashboard.ServerManager;
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
import com.vaadin.server.Responsive;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * Created by nitinpuri on 02-06-2016.
 */
public class AddonDetailsWindow extends Window {

    private final Product product;
    private ProposalDataProvider proposalDataProvider = ServerManager.getInstance().getProposalDataProvider();
    private final AddonProduct addonProduct;
    private final BeanFieldGroup<AddonProduct> binder = new BeanFieldGroup<>(AddonProduct.class);
    private ComboBox category;
    private ComboBox productType;
    private ComboBox brand;
    private ComboBox catalogueCode;
    private TextField uom;
    private TextField rate;
    private TextField quantity;
    private TextField amount;
    private Button applyButton;
    private BeanContainer<String, AddonProductType> productTypeBeanContainer;
    private BeanContainer<String, AddonProductItem> catalogueCodeBeanContainer;
    private BeanContainer<String, AddonBrand> brandBeanContainer;
    private BeanContainer<String, AddonCategory> categoryBeanContainer;

    public AddonDetailsWindow(AddonProduct addonProduct, Product product) {
        this.addonProduct = addonProduct;
        this.product = product;
        this.binder.setItemDataSource(this.addonProduct);
        setModal(true);
        removeCloseShortcut(ShortcutAction.KeyCode.ESCAPE);
        setWidth("60%");
        setClosable(false);
        setCaption("Addon Configuration");

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

        List<AddonProductType> list = proposalDataProvider.getAddonProductTypes(this.product.getRoomCode(), (String) this.category.getValue());
        this.productTypeBeanContainer.removeAllItems();
        this.productTypeBeanContainer.addAll(list);

        if (StringUtils.isNotEmpty(prevCode) && list.stream().anyMatch(addonProductType -> addonProductType.getCode().equals(prevCode))) {
            this.productType.setValue(prevCode);
        } else {
            this.productType.setValue(this.productType.getItemIds().iterator().next());
        }
        checkApply();
    }

    private void catalogueCodeChanged(Property.ValueChangeEvent valueChangeEvent) {

        AddonProductItem addonProductItem = ((BeanItem<AddonProductItem>) this.catalogueCode.getItem(valueChangeEvent.getProperty().getValue())).getBean();

        this.uom.setReadOnly(false);
        this.uom.setValue(addonProductItem.getUom());
        this.uom.setReadOnly(true);

        this.rate.setValue(addonProductItem.getRate() + "");
        if (StringUtils.isEmpty(this.quantity.getValue()) || Double.parseDouble(this.quantity.getValue().replaceAll(",", "")) <= 0) {
            this.quantity.setValue("1");
        }
        //todo: image
        checkApply();
    }

    private void brandChanged(Property.ValueChangeEvent valueChangeEvent) {
        String prevCode = addonProduct.getCatalogueCode();

        List<AddonProductItem> list = proposalDataProvider.getAddonProductItems((String) this.productType.getValue(), (String) this.brand.getValue());
        this.catalogueCodeBeanContainer.removeAllItems();
        this.catalogueCodeBeanContainer.addAll(list);

        if (StringUtils.isNotEmpty(prevCode) && list.stream().anyMatch(addonProductItem -> addonProductItem.getCatalogueCode().equals(prevCode))) {
            this.catalogueCode.setValue(prevCode);
        } else {
            this.catalogueCode.setValue(this.catalogueCode.getItemIds().iterator().next());
        }
        checkApply();
    }

    private void productTypeChanged(Property.ValueChangeEvent valueChangeEvent) {
        String prevCode = addonProduct.getBrandCode();

        List<AddonBrand> list = proposalDataProvider.getAddonBrands((String) this.productType.getValue());
        this.brandBeanContainer.removeAllItems();
        this.brandBeanContainer.addAll(list);

        if (StringUtils.isNotEmpty(prevCode) && list.stream().anyMatch(addonBrand -> addonBrand.getCode().equals(prevCode))) {
            this.brand.setValue(prevCode);
        } else {
            this.brand.setValue(this.brand.getItemIds().iterator().next());
        }
        checkApply();
    }

    private ComboBox getProductTypeCombo() {
        List<AddonProductType> list = proposalDataProvider.getAddonProductTypes(this.product.getRoomCode(), (String) this.category.getValue());

        productTypeBeanContainer = new BeanContainer<>(AddonProductType.class);
        productTypeBeanContainer.setBeanIdProperty("code");
        productTypeBeanContainer.addAll(list);

        ComboBox select = new ComboBox("Product Type");
        select.setNullSelectionAllowed(false);
        select.setWidth("250px");
        select.setContainerDataSource(productTypeBeanContainer);
        select.setItemCaptionPropertyId("title");
        if (StringUtils.isNotEmpty(addonProduct.getProductTypeCode())) {
            select.setValue(addonProduct.getProductTypeCode());
        } else {
            select.setValue(select.getItemIds().iterator().next());
        }
        return select;
    }

    private ComboBox getCatalogueCodeCombo() {
        List<AddonProductItem> list = proposalDataProvider.getAddonProductItems((String) this.productType.getValue(), (String) this.brand.getValue());

        catalogueCodeBeanContainer = new BeanContainer<>(AddonProductItem.class);
        catalogueCodeBeanContainer.setBeanIdProperty("catalogueCode");
        catalogueCodeBeanContainer.addAll(list);

        ComboBox select = new ComboBox("Catalogue Code");
        select.setNullSelectionAllowed(false);
        select.setWidth("250px");
        select.setContainerDataSource(catalogueCodeBeanContainer);
        select.setItemCaptionPropertyId("title");
        if (StringUtils.isNotEmpty(addonProduct.getCatalogueCode())) {
            select.setValue(addonProduct.getCatalogueCode());
        } else {
            select.setValue(select.getItemIds().iterator().next());
        }
        return select;
    }

    private ComboBox getBrandCombo() {
        List<AddonBrand> list = proposalDataProvider.getAddonBrands((String) this.productType.getValue());

        brandBeanContainer = new BeanContainer<>(AddonBrand.class);
        brandBeanContainer.setBeanIdProperty("code");
        brandBeanContainer.addAll(list);

        ComboBox select = new ComboBox("Brand");
        select.setNullSelectionAllowed(false);
        select.setWidth("250px");
        select.setContainerDataSource(brandBeanContainer);
        select.setItemCaptionPropertyId("title");
        if (StringUtils.isNotEmpty(addonProduct.getBrandCode())) {
            select.setValue(addonProduct.getBrandCode());
        } else {
            select.setValue(select.getItemIds().iterator().next());
        }
        return select;
    }

    private ComboBox getCategoryCombo() {
        List<AddonCategory> list = proposalDataProvider.getAddonCategories(this.product.getRoomCode());

        categoryBeanContainer = new BeanContainer<>(AddonCategory.class);
        categoryBeanContainer.setBeanIdProperty("code");
        categoryBeanContainer.addAll(list);

        ComboBox select = new ComboBox("Category");
        select.setNullSelectionAllowed(false);
        select.setWidth("250px");
        select.setContainerDataSource(categoryBeanContainer);
        select.setItemCaptionPropertyId("title");
        if (StringUtils.isNotEmpty(addonProduct.getAddonCategoryCode())) {
            select.setValue(addonProduct.getAddonCategoryCode());
        } else {
            select.setValue(select.getItemIds().iterator().next());
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
            close();
        });
        cancelBtn.focus();
        footer.addComponent(cancelBtn);
        footer.setSpacing(true);

        applyButton = new Button("Apply");
        applyButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        applyButton.addClickListener(event -> {

            try {

                binder.commit();

                addonProduct.setAddonCategory(this.categoryBeanContainer.getItem(this.category.getValue()).getBean().getTitle());
                addonProduct.setProductType(this.productTypeBeanContainer.getItem(this.productType.getValue()).getBean().getTitle());
                addonProduct.setBrand(this.brandBeanContainer.getItem(this.brand.getValue()).getBean().getTitle());
                addonProduct.setCode(this.catalogueCodeBeanContainer.getItem(this.catalogueCode.getValue()).getBean().getCode());
                addonProduct.setAddonCategory(this.categoryBeanContainer.getItem(this.category.getValue()).getBean().getTitle());

                DashboardEventBus.post(new ProposalEvent.AddonUpdated(addonProduct));

            } catch (FieldGroup.CommitException e) {
                e.printStackTrace();
                NotificationUtil.showNotification("Problem while applying changes. Please contact GAME Admin", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            }
            close();
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

    public static void open(AddonProduct addon, Product product) {
        Window w = new AddonDetailsWindow(addon, product);
        UI.getCurrent().addWindow(w);
        w.focus();

    }
}
