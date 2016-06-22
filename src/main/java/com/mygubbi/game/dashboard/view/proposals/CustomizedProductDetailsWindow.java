package com.mygubbi.game.dashboard.view.proposals;


import com.google.common.eventbus.Subscribe;
import com.mygubbi.game.dashboard.ServerManager;
import com.mygubbi.game.dashboard.config.ConfigHolder;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.domain.*;
import com.mygubbi.game.dashboard.domain.JsonPojo.LookupItem;
import com.mygubbi.game.dashboard.domain.Module.ImportStatusType;
import com.mygubbi.game.dashboard.event.DashboardEvent;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.mygubbi.game.dashboard.event.ProposalEvent;
import com.mygubbi.game.dashboard.view.FileAttachmentComponent;
import com.mygubbi.game.dashboard.view.NotificationUtil;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.sort.Sort;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.renderers.ClickableRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.gridutil.renderer.EditButtonValueRenderer;
import org.vaadin.gridutil.renderer.EditDeleteButtonValueRenderer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.mygubbi.game.dashboard.domain.Product.*;

@SuppressWarnings("serial")
public class CustomizedProductDetailsWindow extends Window {

    private static final Logger LOG = LogManager.getLogger(CustomizedProductDetailsWindow.class);
    private static final String CLOSE = "Close";
    private static final String DELETE = "Delete";

    private TextField itemTitleField;
    private TextField roomText;
    private ComboBox makeType;
    private ComboBox productSelection;
    private ComboBox baseCarcassSelection;
    private ComboBox wallCarcassSelection;
    private ComboBox shutterFinishSelection;
    private ComboBox shutterDesign;
    private ComboBox finishTypeSelection;

    private Upload quoteUploadCtrl;
    private File uploadedQuoteFile;
    private TabSheet tabSheet;

    private Button closeBtn;
    private Button saveBtn;
    private Proposal proposal;

    private Product product;
    private final BeanFieldGroup<Product> binder = new BeanFieldGroup<>(Product.class);
    private ProposalDataProvider proposalDataProvider = ServerManager.getInstance().getProposalDataProvider();
    private List<Finish> shutterFinishMasterList;
    private BeanItemContainer<Module> moduleContainer;
    private Grid modulesGrid;
    private TextField totalAmount;
    private FileAttachmentComponent fileAttachmentComponent;
    private BeanItemContainer<AddonProduct> addonsContainer;
    private Grid addonsGrid;
    private ArrayList<Module> modulesCopy;
    private ArrayList<AddonProduct> addonsCopy;
    private boolean deleteNotRequired;
    private Button addonAddButton;

    public CustomizedProductDetailsWindow(Proposal proposal, Product product) {
        this.proposal = proposal;
        this.product = product;

        this.cloneModules();
        this.cloneAddons();

        DashboardEventBus.register(this);
        this.binder.setItemDataSource(this.product);
        setModal(true);
        setSizeFull();
        setResizable(false);
        setClosable(false);
        setCaption("Add Customized Product");

        VerticalLayout vLayout = new VerticalLayout();
        vLayout.setSizeFull();
        vLayout.setMargin(new MarginInfo(true, true, true, true));
        setContent(vLayout);
        Responsive.makeResponsive(this);

        HorizontalLayout horizontalLayout0 = new HorizontalLayout();
        horizontalLayout0.setSizeFull();
        horizontalLayout0.addComponent(buildAddItemBasicFormLeft());
        horizontalLayout0.addComponent(buildAddItemBasicFormRight());
        vLayout.addComponent(horizontalLayout0);
        horizontalLayout0.setHeightUndefined();
        vLayout.setExpandRatio(horizontalLayout0, 0.3f);

        HorizontalLayout horizontalLayout1 = new HorizontalLayout();
        horizontalLayout1.setSizeFull();

        tabSheet = new TabSheet();
        tabSheet.setSizeFull();
        tabSheet.addTab(buildModulesForm(), "Modules");
        tabSheet.addTab(buildAddonsForm(), "Addons");

        fileAttachmentComponent = new FileAttachmentComponent(product, proposal.getProposalHeader().getFolderPath(),
                attachmentData -> proposalDataProvider.addProductDoc(product.getId(), product.getProposalId(), attachmentData.getFileAttachment()),
                attachmentData -> proposalDataProvider.removeProductDoc(attachmentData.getFileAttachment().getId()),
                !proposal.getProposalHeader().getStatus().equals(ProposalHeader.ProposalState.draft.name()));

        if (product.getModules().isEmpty()) {
            fileAttachmentComponent.getFileUploadCtrl().setEnabled(false);
        }

        tabSheet.addTab(fileAttachmentComponent, "Attachments");
        tabSheet.setEnabled(true);
        horizontalLayout1.addComponent(tabSheet);
        horizontalLayout1.setHeightUndefined();
        vLayout.addComponent(horizontalLayout1);
        vLayout.setExpandRatio(horizontalLayout1, 0.6f);

        Component footerLayOut = buildFooter();
        vLayout.addComponent(footerLayOut);
        vLayout.setExpandRatio(footerLayOut, 0.1f);

        handleState();

    }

    private void cloneModules() {
        this.modulesCopy = new ArrayList<>();
        for (Module module : product.getModules()) {
            this.modulesCopy.add((Module) module.clone());
        }
    }

    private void cloneAddons() {
        this.addonsCopy = new ArrayList<>();
        for (AddonProduct addon : product.getAddons()) {
            this.addonsCopy.add((AddonProduct) addon.clone());
        }
    }

    /*
     * Add Item basic section
     */
    private FormLayout buildAddItemBasicFormLeft() {
        FormLayout formLayoutLeft = new FormLayout();
        formLayoutLeft.setSizeFull();
        formLayoutLeft.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        roomText = (TextField) binder.buildAndBind("Room", ROOM_CODE);
        roomText.setRequired(true);
        roomText.setNullRepresentation("");
        formLayoutLeft.addComponent(roomText);

        this.productSelection = getSimpleItemFilledCombo("Product Category", ProposalDataProvider.CATEGORY_LOOKUP, null);
        productSelection.setRequired(true);
        binder.bind(productSelection, PRODUCT_CATEGORY_CODE);
        productSelection.addValueChangeListener(valueChangeEvent -> {
            String code = (String) valueChangeEvent.getProperty().getValue();
            String title = (String) ((ComboBox) ((Field.ValueChangeEvent) valueChangeEvent).getSource()).getContainerDataSource().getItem(code).getItemProperty("title").getValue();
            product.setProductCategory(title);
        });
        if (productSelection.size() > 0) {
            String code = StringUtils.isNotEmpty(product.getProductCategoryCode()) ? product.getProductCategoryCode() : (String) productSelection.getItemIds().iterator().next();
            productSelection.setValue(code);
        }
        formLayoutLeft.addComponent(this.productSelection);

        itemTitleField = (TextField) binder.buildAndBind("Title", TITLE);
        itemTitleField.setRequired(true);
        itemTitleField.setNullRepresentation("");
        formLayoutLeft.addComponent(itemTitleField);

        this.makeType = getSimpleItemFilledCombo("Make Type", ProposalDataProvider.MAKE_LOOKUP, null);
        makeType.setRequired(true);
        binder.bind(makeType, MAKE_TYPE_CODE);
        if (makeType.size() > 0) {
            String code = StringUtils.isNotEmpty(product.getMakeTypeCode()) ? product.getMakeTypeCode() : (String) makeType.getItemIds().iterator().next();

            makeType.setValue(code);
        }
        formLayoutLeft.addComponent(this.makeType);

        this.shutterDesign = getSimpleItemFilledCombo("Shutter Design", ProposalDataProvider.SHUTTER_DESIGN_LOOKUP, null);
        shutterDesign.setRequired(true);
        binder.bind(shutterDesign, SHUTTER_DESIGN_CODE);
        if (shutterDesign.size() > 0) {
            String code = StringUtils.isNotEmpty(product.getShutterDesignCode()) ? product.getShutterDesignCode() : (String) shutterDesign.getItemIds().iterator().next();
            shutterDesign.setValue(code);
        }
        formLayoutLeft.addComponent(this.shutterDesign);

        totalAmount = new TextField("<h2>Total Amount:</h2>");
        totalAmount.setValue("0");
        totalAmount.setImmediate(true);
        totalAmount.setStyleName("amount-text");
        binder.bind(totalAmount, AMOUNT);
        totalAmount.setReadOnly(true);
        totalAmount.setCaptionAsHtml(true);

        makeType.addValueChangeListener(this::refreshPrice);
        return formLayoutLeft;
    }

    private void refreshPrice(Property.ValueChangeEvent valueChangeEvent) {
        List<Module> modules = product.getModules();
        //double total = 0;

        List<Module> boundModules = (List<Module>) binder.getItemDataSource().getItemProperty("modules").getValue();

        Component component = ((Field.ValueChangeEvent) valueChangeEvent).getComponent();

        for (Module module : modules) {

            if (component == makeType) {
                String text = (String) moduleContainer.getItem(module).getItemProperty(Module.MAKE_TYPE).getValue();
                if (text.equals(Module.DEFAULT)) {
                    moduleContainer.getItem(module).getItemProperty(Module.MAKE_TYPE_CODE).setValue(makeType.getValue());
                }
            } else if (component == baseCarcassSelection && module.getUnitType().toLowerCase().contains(Module.UnitTypes.base.name())) {
                String text = (String) moduleContainer.getItem(module).getItemProperty(Module.CARCASS_MATERIAL).getValue();
                if (text.equals(Module.DEFAULT)) {
                    moduleContainer.getItem(module).getItemProperty(Module.CARCASS_MATERIAL_CODE).setValue(baseCarcassSelection.getValue());
                }
            } else if (component == wallCarcassSelection && module.getUnitType().toLowerCase().contains(Module.UnitTypes.wall.name())) {
                String text = (String) moduleContainer.getItem(module).getItemProperty(Module.CARCASS_MATERIAL).getValue();
                if (text.equals(Module.DEFAULT)) {
                    moduleContainer.getItem(module).getItemProperty(Module.CARCASS_MATERIAL_CODE).setValue(wallCarcassSelection.getValue());
                }
            } else if (component == finishTypeSelection) {
                String text = (String) moduleContainer.getItem(module).getItemProperty(Module.FINISH_TYPE).getValue();
                if (text.equals(Module.DEFAULT)) {
                    moduleContainer.getItem(module).getItemProperty(Module.FINISH_TYPE_CODE).setValue(finishTypeSelection.getValue());
                }
            } else if (component == shutterFinishSelection) {
                String text = (String) moduleContainer.getItem(module).getItemProperty(Module.SHUTTER_FINISH).getValue();
                if (text.equals(Module.DEFAULT)) {
                    moduleContainer.getItem(module).getItemProperty(Module.SHUTTER_FINISH_CODE).setValue(shutterFinishSelection.getValue());
                }
            }

            if (StringUtils.isNotEmpty(module.getMgCode())) {
                String unitType = module.getUnitType();

                if ((unitType.toLowerCase().contains(Module.UnitTypes.base.name()) && component != wallCarcassSelection)
                        || (unitType.toLowerCase().contains(Module.UnitTypes.wall.name()) && component != baseCarcassSelection)) {

                    LOG.info("asking price for module - " + module.toString());
                    LOG.info("existing amount - " + module.getAmount());
                    ModulePrice modulePrice = proposalDataProvider.getModulePrice(module);
                    double amount = modulePrice.getTotalCost();
                    LOG.info("got new amount - " + amount);
                    //total += amount;
                    boundModules.get(boundModules.indexOf(module)).setAmount(amount);
                    moduleContainer.getItem(module).getItemProperty(Module.AMOUNT).setValue(amount);
                } else {
                    //total += module.getAmount();
                }
            }
        }

        updateTotalAmount();
/*
        totalAmount.setReadOnly(false);
        totalAmount.setValue(total + "");
        totalAmount.setReadOnly(true);
*/
    }

    private FormLayout buildAddItemBasicFormRight() {
        FormLayout formLayoutRight = new FormLayout();
        formLayoutRight.setSizeFull();
        formLayoutRight.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        this.baseCarcassSelection = getSimpleItemFilledCombo("Base Carcass", ProposalDataProvider.CARCASS_LOOKUP, null);
        baseCarcassSelection.setRequired(true);
        binder.bind(baseCarcassSelection, BASE_CARCASS_CODE);
        baseCarcassSelection.addValueChangeListener(this::refreshPrice);
        if (baseCarcassSelection.size() > 0) {
            String code = StringUtils.isNotEmpty(product.getBaseCarcassCode()) ? product.getBaseCarcassCode() : (String) baseCarcassSelection.getItemIds().iterator().next();
            baseCarcassSelection.setValue(code);
        }
        formLayoutRight.addComponent(this.baseCarcassSelection);

        this.wallCarcassSelection = getSimpleItemFilledCombo("Wall Carcass", ProposalDataProvider.CARCASS_LOOKUP, null);
        wallCarcassSelection.setRequired(true);
        binder.bind(wallCarcassSelection, WALL_CARCASS_CODE);
        wallCarcassSelection.addValueChangeListener(this::refreshPrice);
        if (wallCarcassSelection.size() > 0) {
            String code = StringUtils.isNotEmpty(product.getWallCarcassCode()) ? product.getWallCarcassCode() : (String) wallCarcassSelection.getItemIds().iterator().next();
            wallCarcassSelection.setValue(code);
        }
        formLayoutRight.addComponent(this.wallCarcassSelection);

        this.finishTypeSelection = getSimpleItemFilledCombo("Finish Material", ProposalDataProvider.FINISH_TYPE_LOOKUP, null);
        finishTypeSelection.setRequired(true);
        binder.bind(finishTypeSelection, FINISH_TYPE_CODE);
        if (finishTypeSelection.size() > 0) {
            String code = StringUtils.isNotEmpty(product.getFinishTypeCode()) ? product.getFinishTypeCode() : (String) finishTypeSelection.getItemIds().iterator().next();
            finishTypeSelection.setValue(code);
        }
        formLayoutRight.addComponent(this.finishTypeSelection);
        this.finishTypeSelection.addValueChangeListener(this::finishTypeChanged);

        shutterFinishMasterList = proposalDataProvider.getFinishes();//todLookupItems(ProposalDataProvider.FINISH_LOOKUP);
        List<Finish> filteredShutterFinish = filterShutterFinishByType();
        this.shutterFinishSelection = getFinishItemFilledCombo("Finish", filteredShutterFinish, null);
        shutterFinishSelection.setRequired(true);
        binder.bind(shutterFinishSelection, SHUTTER_FINISH_CODE);
        shutterFinishSelection.addValueChangeListener(this::refreshPrice);
        this.shutterFinishSelection.getContainerDataSource().removeAllItems();
        ((BeanContainer<String, Finish>) this.shutterFinishSelection.getContainerDataSource()).addAll(filteredShutterFinish);
        if (shutterFinishSelection.size() > 0) {
            String code = StringUtils.isNotEmpty(product.getFinishCode()) ? product.getFinishCode() : (String) shutterFinishSelection.getItemIds().iterator().next();
            shutterFinishSelection.setValue(code);
        }
        formLayoutRight.addComponent(this.shutterFinishSelection);

        formLayoutRight.addComponent(getQuoteUploadControl());

        formLayoutRight.addComponent(totalAmount);


        return formLayoutRight;
    }

    private List<Finish> filterShutterFinishByType() {
        List<Finish> filteredShutterFinish = new ArrayList<>();

        String selectedFinishTypeCode = (String) finishTypeSelection.getValue();

        for (Finish shutterFinishComboItem : shutterFinishMasterList) {
            if (selectedFinishTypeCode.equals(shutterFinishComboItem.getFinishMaterial())) {
                filteredShutterFinish.add(shutterFinishComboItem);
            }
        }
        return filteredShutterFinish;
    }

    private void finishTypeChanged(Property.ValueChangeEvent valueChangeEvent) {
        List<Finish> filteredShutterFinish = filterShutterFinishByType();
        this.shutterFinishSelection.getContainerDataSource().removeAllItems();
        ((BeanContainer<String, Finish>) this.shutterFinishSelection.getContainerDataSource()).addAll(filteredShutterFinish);
        if (filteredShutterFinish.size() > 0)
            shutterFinishSelection.setValue(shutterFinishSelection.getItemIds().iterator().next());

    }

    private Component getQuoteUploadControl() {
        this.quoteUploadCtrl = new Upload("Import Quotation Sheet", (filename, mimeType) -> {
            LOG.debug("Received upload - " + filename);

            try {
                binder.commit();
            } catch (FieldGroup.CommitException e) {
                NotificationUtil.showNotification("Please fill all mandatory fields before upload!", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                return null;
            }

            if (StringUtils.isEmpty(filename)) {
                NotificationUtil.showNotification("Please specify the file.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                return null;
            }

            FileOutputStream fos = null;
            uploadedQuoteFile = new File(getUploadBasePath() + "/" + filename);
            uploadedQuoteFile.getParentFile().mkdirs();
            try {
                fos = new FileOutputStream(uploadedQuoteFile);
            } catch (final FileNotFoundException e) {
                NotificationUtil.showNotification("Please specify the file path correctly.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            }
            return fos;
        });

        this.quoteUploadCtrl.setStyleName("upload-btn");

        this.quoteUploadCtrl.addProgressListener((Upload.ProgressListener) (readBytes, contentLength) -> LOG.debug("Progress " + (readBytes * 100 / contentLength)));

        this.quoteUploadCtrl.addSucceededListener((Upload.SucceededListener) event -> {

            this.closeBtn.setCaption("Delete");
            this.deleteNotRequired = false;

            String filename = event.getFilename();
            String quoteFilePath = getUploadBasePath() + "/" + filename;
            product.setQuoteFilePath(quoteFilePath);
            Product productResult = proposalDataProvider.mapAndUpdateProduct(product);
            product.setId(productResult.getId());
            product.setModules(productResult.getModules());
            product.setType(TYPES.CUSTOMIZED.name());
            product.setQuoteFilePath(quoteFilePath);
            initModules(product);
            moduleContainer.removeAllItems();
            moduleContainer.addAll(product.getModules());
            modulesGrid.clearSortOrder();
            modulesGrid.sort(Sort.by(Module.UNIT_TYPE, SortDirection.ASCENDING).then(Module.SEQ, SortDirection.ASCENDING));
            modulesGrid.setContainerDataSource(createGeneratedModulePropertyContainer());
            fileAttachmentComponent.getFileUploadCtrl().setEnabled(true);
            updateTotalAmount();

            if (product.hasImportErrorStatus()) {
                NotificationUtil.showNotification("Error in mapping module(s), please upload new sheet!", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                disableSave();
            } else if (product.allModulesMapped()) {
                enableSave();
                NotificationUtil.showNotification("File uploaded successfully", NotificationUtil.STYLE_BAR_SUCCESS_SMALL);
            } else {
                disableSave();
            }
            LOG.debug("Successfully uploaded - " + filename);
        });
        return quoteUploadCtrl;
    }

    private void initModules(Product product) {
        for (Module module : product.getModules()) {

            module.setMakeType(Module.DEFAULT);
            module.setCarcass(Module.DEFAULT);
            module.setFinishType(Module.DEFAULT);
            module.setFinish(Module.DEFAULT);

            module.setMakeTypeCode(product.getMakeTypeCode());
            module.setCarcassCodeBasedOnUnitType(product);
            module.setFinishTypeCode(product.getFinishTypeCode());
            module.setFinishCode(product.getFinishCode());
        }
    }

    private void enableSave() {
        saveBtn.setEnabled(true);
    }

    private void disableSave() {
        saveBtn.setEnabled(false);
    }

    private String getUploadBasePath() {
        return proposal.getProposalHeader().getFolderPath();
    }

    private Component buildModulesForm() {
        HorizontalLayout hLayout = new HorizontalLayout();
        hLayout.setSizeFull();

        moduleContainer = new BeanItemContainer<>(Module.class);
        GeneratedPropertyContainer genContainer = createGeneratedModulePropertyContainer();

        modulesGrid = new Grid(genContainer);
        modulesGrid.setStyleName("modules-grid");
        modulesGrid.setSizeFull();
        modulesGrid.setColumnReorderingAllowed(true);
        modulesGrid.setColumns(Module.IMPORT_STATUS, Module.SEQ, Module.UNIT_TYPE, Module.IMPORTED_MODULE_TEXT, Module.MG_MODULE_CODE,
                Module.MAKE_TYPE, Module.CARCASS_MATERIAL, Module.FINISH_TYPE, Module.SHUTTER_FINISH, Module.COLOR_CODE, Module.AMOUNT, "action");

        List<Grid.Column> columns = modulesGrid.getColumns();
        int idx = 0;
        Grid.Column statusColumn = columns.get(idx++);
        statusColumn.setHeaderCaption("");
        statusColumn.setRenderer(new HtmlRenderer(), getResourceConverter());

        columns.get(idx++).setHeaderCaption("#");
        columns.get(idx++).setHeaderCaption("Unit Type");
        columns.get(idx++).setHeaderCaption("Imported Module");
        columns.get(idx++).setHeaderCaption("MG Module *");
        columns.get(idx++).setHeaderCaption("Make Type");
        columns.get(idx++).setHeaderCaption("Carcass Material");
        columns.get(idx++).setHeaderCaption("Finish Material");
        columns.get(idx++).setHeaderCaption("Shutter Finish");
        columns.get(idx++).setHeaderCaption("Color");
        columns.get(idx++).setHeaderCaption("Amount");
        Grid.Column actionColumn = columns.get(idx++);
        actionColumn.setHeaderCaption("Actions");
        actionColumn.setRenderer(new EditButtonValueRenderer(rendererClickEvent -> {
            if (!binder.isValid()) {
                NotificationUtil.showNotification("Please fill all mandatory fields before proceeding!", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                return;
            }
            Module module = (Module) rendererClickEvent.getItemId();
            if (!module.getImportStatus().equals(ImportStatusType.n.name())) {
                boolean readOnly = !proposal.getProposalHeader().getStatus().equals(ProposalHeader.ProposalState.draft.name());
                ModuleDetailsWindow.open(module, createProductFromUI(), readOnly);
            } else {
                NotificationUtil.showNotification("Cannot edit unmapped module.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            }
        }));

        hLayout.addComponent(modulesGrid);
        hLayout.setExpandRatio(modulesGrid, 1);

        if (!product.getModules().isEmpty()) {
            moduleContainer.addAll(product.getModules());
            modulesGrid.sort(Sort.by(Module.UNIT_TYPE, SortDirection.ASCENDING).then(Module.SEQ, SortDirection.ASCENDING));
        }

        return modulesGrid;
    }

    private GeneratedPropertyContainer createGeneratedModulePropertyContainer() {
        GeneratedPropertyContainer genContainer = new GeneratedPropertyContainer(moduleContainer);
        genContainer.addGeneratedProperty("action", getEmptyActionTextGenerator());
        //genContainer.addGeneratedProperty("colorName", getColorNameGenerator());
        return genContainer;
    }

    private Product createProductFromUI() {
        Product product = new Product();
        product.setFinishTypeCode((String) this.finishTypeSelection.getValue());
        product.setFinishCode((String) this.shutterFinishSelection.getValue());
        product.setMakeTypeCode((String) this.makeType.getValue());
        product.setWallCarcassCode((String) this.wallCarcassSelection.getValue());
        product.setBaseCarcassCode((String) this.baseCarcassSelection.getValue());
        return product;
    }

    private PropertyValueGenerator<String> getEmptyActionTextGenerator() {
        return new PropertyValueGenerator<String>() {

            @Override
            public String getValue(Item item, Object o, Object o1) {
                return "";
            }

            @Override
            public Class<String> getType() {
                return String.class;
            }
        };
    }

    private Converter<String, String> getResourceConverter() {
        return new Converter<String, String>() {
            @Override
            public String convertToModel(String resource, Class<? extends String> aClass, Locale locale) throws ConversionException {
                return "not needed";
            }

            @Override
            public String convertToPresentation(String s, Class<? extends String> aClass, Locale locale) throws ConversionException {
                return getImportStatusResource(s);
            }

            @Override
            public Class<String> getModelType() {
                return String.class;
            }

            @Override
            public Class<String> getPresentationType() {
                return String.class;
            }
        };
    }

    private String getImportStatusResource(String importStatus) {
        switch (ImportStatusType.valueOf(importStatus)) {
            case d:
                return "<font color=\"orange\">" + FontAwesome.EXCLAMATION_CIRCLE.getHtml() + "</font>";
            case m:
                return "<font color=\"green\">" + FontAwesome.CHECK.getHtml() + "</font>";
            case n:
                return "<font color=\"red\">" + FontAwesome.TIMES_CIRCLE.getHtml() + "</font>";
        }
        return null;
    }

    private Component buildAddonsForm() {

        VerticalLayout verticalLayout = new VerticalLayout();
        addonAddButton = new Button("Add");
        addonAddButton.setStyleName("add-addon-btn");
        addonAddButton.addClickListener(clickEvent -> {
            List<AddonProduct> addons = (List<AddonProduct>) binder.getItemDataSource().getItemProperty("addons").getValue();
            AddonProduct addonProduct = new AddonProduct();
            addonProduct.setSeq(addons.size() + 1);
            addonProduct.setAdd(true);
            AddonDetailsWindow.open(addonProduct, product, false);
        });

        verticalLayout.addComponent(addonAddButton);
        verticalLayout.setComponentAlignment(addonAddButton, Alignment.MIDDLE_RIGHT);

        addonsContainer = new BeanItemContainer<>(AddonProduct.class);

        GeneratedPropertyContainer genContainer = createGeneratedAddonsPropertyContainer();

        addonsGrid = new Grid(genContainer);
        addonsGrid.setSizeFull();
        addonsGrid.setHeight("325px");
        addonsGrid.setColumnReorderingAllowed(true);
        addonsGrid.setColumns(AddonProduct.SEQ, AddonProduct.ADDON_CATEGORY, AddonProduct.PRODUCT_TYPE, AddonProduct.BRAND,
                AddonProduct.TITLE, AddonProduct.CATALOGUE_CODE, AddonProduct.UOM, AddonProduct.RATE, AddonProduct.QUANTITY, AddonProduct.AMOUNT, "actions");

        List<Grid.Column> columns = addonsGrid.getColumns();
        int idx = 0;
        columns.get(idx++).setHeaderCaption("#");
        columns.get(idx++).setHeaderCaption("Category");
        columns.get(idx++).setHeaderCaption("Product Type");
        columns.get(idx++).setHeaderCaption("Brand");
        columns.get(idx++).setHeaderCaption("Product Name");
        columns.get(idx++).setHeaderCaption("Product Code");
        columns.get(idx++).setHeaderCaption("UOM");
        columns.get(idx++).setHeaderCaption("Rate");
        columns.get(idx++).setHeaderCaption("Qty");
        columns.get(idx++).setHeaderCaption("Amount");
        Grid.Column actionColumn = columns.get(idx++);
        actionColumn.setHeaderCaption("Actions");
        actionColumn.setRenderer(new EditDeleteButtonValueRenderer(new EditDeleteButtonValueRenderer.EditDeleteButtonClickListener() {
            @Override
            public void onEdit(ClickableRenderer.RendererClickEvent rendererClickEvent) {
                AddonProduct addon = (AddonProduct) rendererClickEvent.getItemId();
                addon.setAdd(false);
                boolean readOnly = !proposal.getProposalHeader().getStatus().equals(ProposalHeader.ProposalState.draft.name());
                AddonDetailsWindow.open(addon, product, readOnly);
            }

            @Override
            public void onDelete(ClickableRenderer.RendererClickEvent rendererClickEvent) {
                if (!proposal.getProposalHeader().getStatus().equals(ProposalHeader.ProposalState.draft.name())) {
                    NotificationUtil.showNotification("This operation is allowed only in 'draft' state.", NotificationUtil.STYLE_BAR_WARNING_SMALL);
                } else {
                    ConfirmDialog.show(UI.getCurrent(), "", "Are you sure you want to Delete this Addon?",
                            "Yes", "No", dialog -> {
                                if (dialog.isConfirmed()) {
                                    AddonProduct addon = (AddonProduct) rendererClickEvent.getItemId();

                                    List<AddonProduct> addons = (List<AddonProduct>) binder.getItemDataSource().getItemProperty("addons").getValue();
                                    addons.remove(addon);

                                    int seq = addon.getSeq();
                                    addonsContainer.removeAllItems();

                                    for (AddonProduct addonProduct : addons) {
                                        if (addonProduct.getSeq() > seq) {
                                            addonProduct.setSeq(addonProduct.getSeq() - 1);
                                        }
                                    }
                                    addonsContainer.addAll(addons);
                                    addonsGrid.setContainerDataSource(createGeneratedAddonsPropertyContainer());
                                    updateTotalAmount();
                                }
                            });
                }
            }
        }));

        verticalLayout.addComponent(addonsGrid);
        verticalLayout.setExpandRatio(addonsGrid, 1);

        if (!product.getAddons().isEmpty()) {
            addonsContainer.addAll(product.getAddons());
            addonsGrid.sort(AddonProduct.SEQ, SortDirection.ASCENDING);
        }

        return verticalLayout;

    }

    private GeneratedPropertyContainer createGeneratedAddonsPropertyContainer() {
        GeneratedPropertyContainer genContainer = new GeneratedPropertyContainer(addonsContainer);
        genContainer.addGeneratedProperty("actions", getEmptyActionTextGenerator());
        return genContainer;
    }

    private Component buildFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.setSizeFull();
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Unit.PERCENTAGE);

        String caption = null;
        boolean modulesInitiallyMapped = this.modulesCopy.stream().allMatch(module -> StringUtils.isNotEmpty(module.getMgCode()));

        deleteNotRequired = modulesInitiallyMapped && !this.modulesCopy.isEmpty();

        if (deleteNotRequired) {
            caption = CLOSE;
        } else {
            caption = DELETE;
        }

        closeBtn = new Button(caption);
        closeBtn.addClickListener((Button.ClickListener) clickEvent -> {

            if (!proposal.getProposalHeader().getStatus().equals(ProposalHeader.ProposalState.draft.name())) {
                closeWindow();
            } else {
                ConfirmDialog.show(UI.getCurrent(), "",
                        deleteNotRequired ?
                                "Are you sure? Unsaved data will be lost."
                                : "The product will be Deleted as the   modules mapping is not yet saved. Are sure you want to proceed?",
                        "Yes", "No", dialog -> {

                            if (!dialog.isCanceled()) {
                                if (!deleteNotRequired) {
                                    proposalDataProvider.deleteProduct(product.getId());
                                    DashboardEventBus.post(new ProposalEvent.ProductDeletedEvent(product));
                                } else {
                                    binder.discard();
                                    this.product.setModules(this.modulesCopy);
                                    this.product.setAddons(this.addonsCopy);
                                }
                                closeWindow();
                            }
                        });
            }
        });
        closeBtn.focus();
        footer.addComponent(closeBtn);
        footer.setSpacing(true);

        saveBtn = new Button("Save");
        saveBtn.addStyleName(ValoTheme.BUTTON_PRIMARY);
        saveBtn.addClickListener(event -> {
            try {

                try {
                    binder.commit();
                } catch (FieldGroup.CommitException e) {
                    NotificationUtil.showNotification("Please fill all mandatory fields.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                    return;
                }
                boolean success = proposalDataProvider.updateProduct(product);

                if (success) {
                    NotificationUtil.showNotification("Product details saved successfully", NotificationUtil.STYLE_BAR_SUCCESS_SMALL);
                    DashboardEventBus.post(new ProposalEvent.ProductCreatedOrUpdatedEvent(product));
                    close();
                } else {
                    NotificationUtil.showNotification("Product save failed, please contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                }
            } catch (Exception e) {
                Notification.show("Error while saving Item details",
                        Type.ERROR_MESSAGE);
            }
        });
        saveBtn.focus();
        saveBtn.setVisible(true);

        if (!product.allModulesMapped() || product.getModules().isEmpty()) {
            disableSave();
        }

        footer.addComponent(saveBtn);
        footer.setComponentAlignment(closeBtn, Alignment.TOP_RIGHT);

        return footer;
    }

    public static void closeWindow() {
        DashboardEventBus.post(new DashboardEvent.CloseOpenWindowsEvent());
    }

    private ComboBox getFinishItemFilledCombo(String caption, List<Finish> list, Property.ValueChangeListener listener) {

        final BeanContainer<String, Finish> container =
                new BeanContainer<>(Finish.class);
        container.setBeanIdProperty(Finish.FINISH_CODE);
        container.addAll(list);

        ComboBox select = new ComboBox(caption);
        select.setNullSelectionAllowed(false);
        select.setWidth("250px");
        select.setContainerDataSource(container);
        select.setItemCaptionPropertyId(Finish.TITLE);
        if (listener != null) select.addValueChangeListener(listener);
        if (container.size() > 0) select.setValue(select.getItemIds().iterator().next());
        return select;
    }

    private ComboBox getSimpleItemFilledCombo(String caption, List<LookupItem> list, Property.ValueChangeListener listener) {

        final BeanContainer<String, LookupItem> container =
                new BeanContainer<>(LookupItem.class);
        container.setBeanIdProperty("code");
        container.addAll(list);

        ComboBox select = new ComboBox(caption);
        select.setNullSelectionAllowed(false);
        select.setWidth("250px");
        select.setContainerDataSource(container);
        select.setItemCaptionPropertyId("title");
        if (listener != null) select.addValueChangeListener(listener);
        if (container.size() > 0) select.setValue(select.getItemIds().iterator().next());
        return select;
    }

    private ComboBox getSimpleItemFilledCombo(String caption, String dataType, Property.ValueChangeListener listener) {
        List<LookupItem> list = proposalDataProvider.getLookupItems(dataType);
        return getSimpleItemFilledCombo(caption, list, listener);
    }

    private void updateTotalAmount() {
        double amount = 0;

        List<Module> modules = (List<Module>) binder.getItemDataSource().getItemProperty("modules").getValue();
        for (Module module : modules) {
            amount += module.getAmount();
        }

        List<AddonProduct> addons = (List<AddonProduct>) binder.getItemDataSource().getItemProperty("addons").getValue();
        for (AddonProduct addon : addons) {
            amount += addon.getAmount();
        }

        totalAmount.setReadOnly(false);
        totalAmount.setValue(amount + "");
        totalAmount.setReadOnly(true);

    }

    @Subscribe
    public void moduleUpdated(final ProposalEvent.ModuleUpdated event) {
        List<Module> modules = (List<Module>) binder.getItemDataSource().getItemProperty("modules").getValue();
        modules.remove(event.getModule());
        modules.add(event.getModule());
        moduleContainer.removeAllItems();
        moduleContainer.addAll(modules);
        modulesGrid.setContainerDataSource(createGeneratedModulePropertyContainer());
        modulesGrid.sort(Sort.by(Module.UNIT_TYPE, SortDirection.ASCENDING).then(Module.SEQ, SortDirection.ASCENDING));
        updateTotalAmount();

        if (product.allModulesMapped()) {
            enableSave();
        }
    }

    @Subscribe
    public void addonUpdated(final ProposalEvent.AddonUpdated event) {
        List<AddonProduct> addons = (List<AddonProduct>) binder.getItemDataSource().getItemProperty("addons").getValue();
        addons.remove(event.getAddonProduct());
        addons.add(event.getAddonProduct());
        addonsContainer.removeAllItems();
        addonsContainer.addAll(addons);
        addonsGrid.setContainerDataSource(createGeneratedAddonsPropertyContainer());
        addonsGrid.sort(AddonProduct.SEQ, SortDirection.ASCENDING);
        updateTotalAmount();
    }

    public static void open(Proposal proposal, Product product) {
        DashboardEventBus.post(new DashboardEvent.CloseOpenWindowsEvent());
        Window w = new CustomizedProductDetailsWindow(proposal, product);
        UI.getCurrent().addWindow(w);
        w.focus();

    }

    private void handleState() {

        ProposalHeader.ProposalState proposalState = ProposalHeader.ProposalState.valueOf(proposal.getProposalHeader().getStatus());
        switch (proposalState) {
            case draft:
                break;
            case active:
            case cancelled:
            case published:
                itemTitleField.setReadOnly(true);
                productSelection.setReadOnly(true);
                roomText.setReadOnly(true);
                makeType.setReadOnly(true);
                shutterDesign.setReadOnly(true);
                baseCarcassSelection.setReadOnly(true);
                wallCarcassSelection.setReadOnly(true);
                finishTypeSelection.setReadOnly(true);
                shutterFinishSelection.setReadOnly(true);
                quoteUploadCtrl.setEnabled(false);
                addonAddButton.setEnabled(false);
                closeBtn.setCaption(CLOSE);
                fileAttachmentComponent.getFileUploadCtrl().setEnabled(false);
                saveBtn.setEnabled(false);
                fileAttachmentComponent.setReadOnly(true);
                break;
            default:
                throw new RuntimeException("Unknown State");
        }
    }

}