package com.mygubbi.game.dashboard.view.proposals;


import com.mygubbi.game.dashboard.ServerManager;
import com.mygubbi.game.dashboard.config.ConfigHolder;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.domain.*;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.mygubbi.game.dashboard.event.ProposalEvent;
import com.mygubbi.game.dashboard.view.NotificationUtil;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Responsive;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vaadin.dialogs.ConfirmDialog;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("serial")
public class CatalogItemDetailsWindow extends Window {
    private static final String COMBO_WIDTH = "300px";
    private ComboBox categoryCombo;
    private ComboBox subCategoryCombo;
    private ComboBox productCombo;
    private TextField productTitleField;
    private ComboBox materialCombo;
    private ComboBox finishCombo;
    private TextField amountField;

    private TextArea descriptionField;
    private Embedded productImage;
    private ThemeResource emptyImage;

    private Button closeBtn;
    private Button saveBtn;

    private final CatalogueProduct product;
    private ProposalVersion proposalVersion;
    private ProposalHeader proposalHeader;
    private final Proposal proposal;
    private final BeanFieldGroup<CatalogueProduct> binder = new BeanFieldGroup<>(CatalogueProduct.class);

    private ProposalDataProvider proposalDataProvider = ServerManager.getInstance().getProposalDataProvider();
    private final List<CatalogueProductCategory> categories;

    private static final Logger LOG = LogManager.getLogger(CatalogItemDetailsWindow.class);


    private CatalogItemDetailsWindow(Proposal proposal, CatalogueProduct product, ProposalVersion proposalVersion, ProposalHeader proposalHeader) {

        this.proposal = proposal;
        this.product = product;
        this.proposalVersion = proposalVersion;
        this.proposalHeader = proposalHeader;
        this.binder.setItemDataSource(this.product);
        categories = proposalDataProvider.getCatalogueProductCategories();

        fillCatalogueProduct();

        setModal(true);
        setResizable(false);
        setClosable(false);
        setSizeFull();
        setCaption("Add Catalogue Product");

        VerticalLayout vLayout = new VerticalLayout();
        vLayout.setMargin(new MarginInfo(true, false, false, false));

        HorizontalLayout horizontalLayout0 = new HorizontalLayout();
        horizontalLayout0.setSizeFull();
        horizontalLayout0.addComponent(buildLeftForm());
        horizontalLayout0.addComponent(buildRightForm());
        vLayout.addComponent(horizontalLayout0);

        vLayout.addComponent(new Label("</br></br>", ContentMode.HTML));
        vLayout.addComponent(buildFooter());

        setContent(vLayout);
        Responsive.makeResponsive(horizontalLayout0);
    }

    private void fillCatalogueProduct() {
        if (StringUtils.isNotEmpty(product.getCatalogueId())) {
            CatalogueProduct catalogueProduct = this.proposalDataProvider.getCatalogueProduct(product.getCatalogueId());
            product.setMf(catalogueProduct.getMf());
            product.setImages(catalogueProduct.getImages());
            product.setProductId(product.getCatalogueId());
            product.setName(product.getTitle());
            product.setDesc(catalogueProduct.getDesc());
        }
    }

    private FormLayout buildLeftForm() {
        FormLayout formLayoutLeft = new FormLayout();
        formLayoutLeft.setSizeFull();
        formLayoutLeft.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        this.categoryCombo = getCategoryCombo();
        this.categoryCombo.setRequired(true);
        this.categoryCombo.addValueChangeListener(this::categoryChanged);
        formLayoutLeft.addComponent(this.categoryCombo);

        this.subCategoryCombo = getSubCategoryCombo();
        this.subCategoryCombo.setRequired(true);
        this.binder.bind(this.subCategoryCombo, CatalogueProduct.PRODUCT_CATEGORY_CODE);
        this.subCategoryCombo.addValueChangeListener(this::subCategoryChanged);
        formLayoutLeft.addComponent(this.subCategoryCombo);

        this.productCombo = getProductCombo();
        this.productCombo.setRequired(true);
        this.binder.bind(this.productCombo, CatalogueProduct.CATALOGUE_ID);
        this.productCombo.addValueChangeListener(this::productChanged);
        formLayoutLeft.addComponent(this.productCombo);

        this.productTitleField = new TextField("Title");
        this.productTitleField.setRequired(true);
        this.productTitleField.setNullRepresentation("");
        this.binder.bind(this.productTitleField, CatalogueProduct.TITLE);
        formLayoutLeft.addComponent(productTitleField);

        this.descriptionField = new TextArea("Description");
        this.descriptionField.setNullRepresentation("");
        this.binder.bind(this.descriptionField, CatalogueProduct.DESC);
        this.descriptionField.setReadOnly(true);
        formLayoutLeft.addComponent(this.descriptionField);

        return formLayoutLeft;
    }

    private FormLayout buildRightForm() {
        FormLayout formLayoutRight = new FormLayout();
        formLayoutRight.setSizeFull();
        formLayoutRight.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        this.materialCombo = getMaterialCombo();
        this.materialCombo.setRequired(true);
        this.binder.bind(this.materialCombo, CatalogueProduct.SELECTED_MATERIAL);
        this.materialCombo.addValueChangeListener(this::materialChanged);
        formLayoutRight.addComponent(this.materialCombo);

        this.finishCombo = getFinishCombo();
        this.finishCombo.setRequired(true);
        this.binder.bind(this.finishCombo, CatalogueProduct.SELECTED_FINISH);
        this.finishCombo.addValueChangeListener(this::finishChanged);
        formLayoutRight.addComponent(this.finishCombo);

        this.amountField = new TextField("Amount");
        this.amountField.setRequired(true);
        this.binder.bind(this.amountField, CatalogueProduct.AMOUNT);
        formLayoutRight.addComponent(this.amountField);

        formLayoutRight.addComponent(new Label("</br>", ContentMode.HTML));

        if (product.getImages().isEmpty()) {
            emptyImage = new ThemeResource("img/empty-poster.png");
            productImage = new Embedded("", emptyImage);
        } else {
            String imageBasePath = ConfigHolder.getInstance().getCatalogueImageBasePath();
            this.productImage = new Embedded("", new ExternalResource(imageBasePath + product.getImages().get(0)));
        }
        productImage.setCaption(null);
        productImage.setWidth("220px");
        formLayoutRight.addComponent(this.productImage);

        return formLayoutRight;
    }

    private Component buildFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Unit.PERCENTAGE);

        closeBtn = new Button("Close");
        closeBtn.addClickListener((ClickListener) clickEvent -> {
            ConfirmDialog.show(UI.getCurrent(), "",
                    "Changes will be discarded. Are you sure you want to proceed?",
                    "Yes", "No", dialog -> {
                        if (!dialog.isCanceled()) {
                            binder.discard();
                            close();
                        }
                    });
        });
        footer.addComponent(closeBtn);
        footer.setSpacing(true);

        saveBtn = new Button("Save");
        saveBtn.addStyleName(ValoTheme.BUTTON_PRIMARY);
        saveBtn.addClickListener((ClickListener) event -> {
            if (!binder.isValid()) {
                NotificationUtil.showNotification("Please ensure all mandatory fields are filled.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            } else {

                try {
                    binder.commit();
                    product.setRoom(((BeanContainer<String, CatalogueProductCategory>) this.categoryCombo.getContainerDataSource())
                            .getItem(this.categoryCombo.getValue()).getBean().getName());
                    product.setRoomCode(product.getRoom());
                    product.setProductCategory(((BeanContainer<String, CatalogueProductSubCategory>) this.subCategoryCombo.getContainerDataSource())
                            .getItem(this.subCategoryCombo.getValue()).getBean().getName());
                    product.setFromVersion(proposalVersion.getVersion());
                    List<Product> getAllVersionProducts = proposalDataProvider.getVersionProducts(proposalVersion.getProposalId(),proposalVersion.getVersion());
                    proposal.setProducts(getAllVersionProducts);

                    if (product.getSeq() == 0)
                    {
                        int size = getAllVersionProducts.size();
                        size++;
                        product.setSeq(size);
                    }
                    boolean success = proposalDataProvider.updateProduct(product);

                    if (success) {
                        NotificationUtil.showNotification("Product details saved successfully", NotificationUtil.STYLE_BAR_SUCCESS_SMALL);
                        DashboardEventBus.post(new ProposalEvent.ProductCreatedOrUpdatedEvent(product));
                        close();
                    } else {
                        NotificationUtil.showNotification("Product save failed, please contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                    }
                } catch (FieldGroup.CommitException e) {
                    e.printStackTrace();
                }
            }
        });
        saveBtn.focus();
        saveBtn.setVisible(true);
        footer.addComponent(saveBtn);
        footer.setComponentAlignment(closeBtn, Alignment.TOP_RIGHT);
        return footer;
    }

    private void finishChanged(Property.ValueChangeEvent valueChangeEvent) {
        CatalogueProduct catalogueProduct = ((BeanContainer<String, CatalogueProduct>) this.productCombo.getContainerDataSource())
                .getItem(this.productCombo.getValue()).getBean();
        double price = getPrice(materialCombo.getValue().toString(), finishCombo.getValue().toString(), catalogueProduct.getMf());
        amountField.setValue(price + "");
    }

    private void materialChanged(Property.ValueChangeEvent valueChangeEvent) {
        CatalogueProduct catalogueProduct = ((BeanContainer<String, CatalogueProduct>) this.productCombo.getContainerDataSource())
                .getItem(this.productCombo.getValue()).getBean();

        List<String> finishes = collectFinish(catalogueProduct.getMf(), materialCombo.getValue().toString());
        Object prevFinish = this.finishCombo.getValue();
        this.finishCombo.getContainerDataSource().removeAllItems();
        this.finishCombo.addItems(finishes);
        this.finishCombo.setValue(finishes.get(0));

        if (prevFinish != null && prevFinish.equals(finishes.get(0))) {
            this.finishChanged(null);
        }

    }

    private void productChanged(Property.ValueChangeEvent valueChangeEvent) {
        CatalogueProduct catalogueProduct = ((BeanContainer<String, CatalogueProduct>) this.productCombo.getContainerDataSource())
                .getItem(this.productCombo.getValue()).getBean();

        this.productTitleField.setValue(catalogueProduct.getTitle());
        this.descriptionField.setReadOnly(false);
        this.descriptionField.setValue(catalogueProduct.getDesc());
        this.descriptionField.setReadOnly(true);
        String imageBasePath = ConfigHolder.getInstance().getCatalogueImageBasePath();
        //LOG.debug("image base path :" + imageBasePath);
        this.productImage.setSource(new ExternalResource(imageBasePath + catalogueProduct.getImages().get(0)));
        Object prevMaterial = this.materialCombo.getValue();
        this.materialCombo.getContainerDataSource().removeAllItems();
        List<String> itemIds = collectMaterial(catalogueProduct.getMf());
        this.materialCombo.addItems(itemIds);
        this.materialCombo.setValue(itemIds.get(0));
        if (prevMaterial != null && prevMaterial.equals(itemIds.get(0))) {
            this.materialChanged(null);
        }
    }

    private void subCategoryChanged(Property.ValueChangeEvent valueChangeEvent) {
        String categoryCode = categoryCombo.getValue().toString();
        String subCategoryCode = subCategoryCombo.getValue().toString();

        List<CatalogueProduct> products = this.proposalDataProvider.getCatalogueProducts(categoryCode, subCategoryCode);
        BeanContainer<String, CatalogueProduct> containerDataSource = (BeanContainer<String, CatalogueProduct>) productCombo.getContainerDataSource();
        containerDataSource.removeAllItems();
        containerDataSource.addAll(products);
        if (!products.isEmpty()) {
            this.productCombo.setValue(this.productCombo.getItemIds().iterator().next());
        } else {
            NotificationUtil.showNotification("No Products found in Catalogue!", NotificationUtil.STYLE_BAR_ERROR_SMALL);
        }
    }

    private void categoryChanged(Property.ValueChangeEvent valueChangeEvent) {
        CatalogueProductCategory category = ((BeanContainer<String, CatalogueProductCategory>) this.categoryCombo.getContainerDataSource())
                .getItem(this.categoryCombo.getValue()).getBean();

        BeanContainer<String, CatalogueProductSubCategory> containerDataSource = (BeanContainer<String, CatalogueProductSubCategory>) subCategoryCombo.getContainerDataSource();
        containerDataSource.removeAllItems();
        containerDataSource.addAll(category.getSubCategories());
        subCategoryCombo.setValue(subCategoryCombo.getItemIds().iterator().next());
    }

    private ComboBox getCategoryCombo() {
        final BeanContainer<String, CatalogueProductCategory> container =
                new BeanContainer<>(CatalogueProductCategory.class);
        container.setBeanIdProperty(CatalogueProductCategory.CODE);
        container.addAll(this.categories);

        ComboBox select = new ComboBox("Category");
        select.setWidth(COMBO_WIDTH);
        select.setNullSelectionAllowed(false);
        select.setContainerDataSource(container);
        select.setItemCaptionPropertyId(CatalogueProductCategory.NAME);
        if (StringUtils.isNotEmpty(product.getProductCategoryCode())) {
            select.setValue(getCategoryFromSubCategory(product.getProductCategoryCode()).getCode());
        }
        //if (container.size() > 0) select.setValue(select.getItemIds().iterator().next());
        return select;
    }

    private ComboBox getSubCategoryCombo() {
        final BeanContainer<String, CatalogueProductSubCategory> container =
                new BeanContainer<>(CatalogueProductSubCategory.class);
        container.setBeanIdProperty(CatalogueProductSubCategory.CODE);
        ComboBox select = new ComboBox("Sub Category");
        select.setWidth(COMBO_WIDTH);
        select.setNullSelectionAllowed(false);
        select.setContainerDataSource(container);
        select.setItemCaptionPropertyId(CatalogueProductSubCategory.NAME);
        if (StringUtils.isNotEmpty(product.getProductCategoryCode())) {
            container.removeAllItems();
            container.addAll(getCategoryFromSubCategory(product.getProductCategoryCode()).getSubCategories());
            select.setValue(product.getProductCategoryCode());
        }
        //if (container.size() > 0) select.setValue(select.getItemIds().iterator().next());
        return select;
    }

    private ComboBox getProductCombo() {
        final BeanContainer<String, CatalogueProduct> container =
                new BeanContainer<>(CatalogueProduct.class);
        container.setBeanIdProperty(CatalogueProduct.PRODUCT_ID);

        ComboBox select = new ComboBox("Product");
        select.setWidth(COMBO_WIDTH);
        select.setNullSelectionAllowed(false);
        select.setContainerDataSource(container);
        select.setItemCaptionPropertyId(CatalogueProduct.NAME);
        if (StringUtils.isNotEmpty(product.getProductId())) {
            String categoryCode = categoryCombo.getValue().toString();
            String subCategoryCode = subCategoryCombo.getValue().toString();
            List<CatalogueProduct> products = this.proposalDataProvider.getCatalogueProducts(categoryCode, subCategoryCode);
            container.addAll(products);
            select.setValue(product.getProductId());
        }
        return select;
    }

    private ComboBox getMaterialCombo() {
        ComboBox select = new ComboBox("Material");
        select.setWidth(COMBO_WIDTH);
        BeanItemContainer<String> newDataSource = new BeanItemContainer<>(String.class);
        select.setContainerDataSource(newDataSource);
        select.setNullSelectionAllowed(false);
        if (StringUtils.isNotEmpty(product.getBaseCarcassCode())) {
            CatalogueProduct catalogueProduct = ((BeanContainer<String, CatalogueProduct>) this.productCombo.getContainerDataSource())
                    .getItem(this.productCombo.getValue()).getBean();
            newDataSource.addAll(collectMaterial(catalogueProduct.getMf()));
            select.setValue(product.getBaseCarcassCode());
        }
        return select;
    }

    private ComboBox getFinishCombo() {

        ComboBox select = new ComboBox("Finish");
        select.setWidth(COMBO_WIDTH);
        BeanItemContainer<String> newDataSource = new BeanItemContainer<>(String.class);
        select.setContainerDataSource(newDataSource);
        select.setNullSelectionAllowed(false);
        if (StringUtils.isNotEmpty(product.getFinishCode())) {
            CatalogueProduct catalogueProduct = ((BeanContainer<String, CatalogueProduct>) this.productCombo.getContainerDataSource())
                    .getItem(this.productCombo.getValue()).getBean();
            newDataSource.addAll(collectFinish(catalogueProduct.getMf(), materialCombo.getValue().toString()));
            select.setValue(product.getFinishCode());
        }
        return select;
    }

    private List<CatalogueMaterialFinish> filterMFByMaterial(String material, List<CatalogueMaterialFinish> mfList) {
        return mfList.stream().filter(catalogueMaterialFinish -> catalogueMaterialFinish.getMaterial().equals(material)).collect(Collectors.toList());
    }

    private List<String> collectMaterial(List<CatalogueMaterialFinish> mfList) {
        return mfList.stream().map(CatalogueMaterialFinish::getMaterial).collect(Collectors.toList());
    }

    private List<String> collectFinish(List<CatalogueMaterialFinish> mfList, String material) {
        return filterMFByMaterial(material, mfList).stream().map(CatalogueMaterialFinish::getFinish).collect(Collectors.toList());
    }

    private double getPrice(String material, String finish, List<CatalogueMaterialFinish> mfList) {

        return mfList.stream().filter(
                catalogueMaterialFinish ->
                        catalogueMaterialFinish.getMaterial().equals(material)
                                && catalogueMaterialFinish.getFinish().equals(finish))
                .collect(Collectors.toList())
                .get(0).getBasePrice();
    }

    private CatalogueProductCategory getCategoryFromSubCategory(String subCategoryCode) {
        return categories.stream().filter(
                catalogueProductCategory -> catalogueProductCategory.getSubCategories().stream().anyMatch(
                        catalogueProductSubCategory -> catalogueProductSubCategory.getCode().equals(subCategoryCode)))
                .collect(Collectors.toList())
                .get(0);
    }

    public static void open(Proposal proposal, CatalogueProduct product, ProposalVersion proposalVersion, ProposalHeader proposalHeader) {
        /*DashboardEventBus.post(new DashboardEvent.CloseOpenWindowsEvent());*/
        Window w = new CatalogItemDetailsWindow(proposal, product, proposalVersion, proposalHeader);
        UI.getCurrent().addWindow(w);
        w.focus();
    }

}