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
import com.mygubbi.game.dashboard.view.DashboardViewType;
import com.mygubbi.game.dashboard.view.NotificationUtil;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.*;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.event.MouseEvents;
import com.vaadin.server.*;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.Grid.HeaderRow;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;
import de.datenhahn.vaadin.componentrenderer.grid.ComponentGrid;
import de.datenhahn.vaadin.componentrenderer.grid.ComponentGridDecorator;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.gridutil.renderer.EditButtonValueRenderer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.*;

import static com.mygubbi.game.dashboard.domain.Product.*;

@SuppressWarnings("serial")
public class CustomizedProductDetailsWindow extends Window {

    private static final Logger LOG = LogManager.getLogger(CustomizedProductDetailsWindow.class);

    private TextField itemTitleField;
    private ComboBox roomSelection;
    private ComboBox makeType;
    private ComboBox productSelection;
    private ComboBox baseCarcassSelection;
    private ComboBox wallCarcassSelection;
    private ComboBox shutterFinishSelection;
    private ComboBox shutterDesign;
    private ComboBox finishTypeSelection;

    private Upload uploadCtrl;
    private File uploadFile;
    private TabSheet tabSheet;

    private Button closeBtn;
    private Button saveBtn;
    private Proposal proposal;

    private Product product;
    private final BeanFieldGroup<Product> binder = new BeanFieldGroup<>(Product.class);
    private ProposalDataProvider proposalDataProvider = ServerManager.getInstance().getProposalDataProvider();
    private List<LookupItem> shutterFinishMasterList;
    private BeanItemContainer<Module> moduleContainer;
    private Grid modulesGrid;
    private TextField totalAmount;

    public CustomizedProductDetailsWindow(Proposal proposal) {
        this(proposal, new Product(proposal.getProposalHeader().getId(), proposal.getProducts().size() + 1));
    }

    public CustomizedProductDetailsWindow(Proposal proposal, Product product) {
        this.proposal = proposal;
        this.product = product;

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
        tabSheet.addTab(buildAttachmentsForm(), "Attachments");
        tabSheet.setEnabled(true);
        horizontalLayout1.addComponent(tabSheet);
        horizontalLayout1.setHeightUndefined();
        vLayout.addComponent(horizontalLayout1);
        vLayout.setExpandRatio(horizontalLayout1, 0.6f);

        Component footerLayOut = buildFooter();
        vLayout.addComponent(footerLayOut);
        vLayout.setExpandRatio(footerLayOut, 0.1f);

    }

    /*
     * Add Item basic section
     */
    private FormLayout buildAddItemBasicFormLeft() {
        FormLayout formLayoutLeft = new FormLayout();
        formLayoutLeft.setSizeFull();
        formLayoutLeft.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        itemTitleField = (TextField) binder.buildAndBind("Product Title", TITLE);
        itemTitleField.setRequired(true);
        itemTitleField.setNullRepresentation("");
        formLayoutLeft.addComponent(itemTitleField);

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

        this.roomSelection = getSimpleItemFilledCombo("Room", ProposalDataProvider.ROOM_LOOKUP, null);
        roomSelection.setRequired(true);
        binder.bind(roomSelection, ROOM_CODE);
        roomSelection.addValueChangeListener(valueChangeEvent -> {
            String code = (String) valueChangeEvent.getProperty().getValue();
            String title = (String) ((ComboBox) ((Field.ValueChangeEvent) valueChangeEvent).getSource()).getContainerDataSource().getItem(code).getItemProperty("title").getValue();
            product.setRoom(title);
        });
        if (roomSelection.size() > 0) {
            String code = StringUtils.isNotEmpty(product.getRoomCode()) ? product.getRoomCode() : (String) roomSelection.getItemIds().iterator().next();
            roomSelection.setValue(code);
        }
        formLayoutLeft.addComponent(this.roomSelection);

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

    //todo: include addons price
    private void refreshPrice(Property.ValueChangeEvent valueChangeEvent) {
        List<Module> modules = product.getModules();
        double total = 0;

        for (Module module : modules) {
            if (StringUtils.isNotEmpty(module.getMgCode())) {
                ModulePrice modulePrice = proposalDataProvider.getModulePrice(module);
                total += modulePrice.getTotalCost();
            }
        }
        totalAmount.setReadOnly(false);
        totalAmount.setValue(total + "");
        totalAmount.setReadOnly(true);
    }

    private FormLayout buildAddItemBasicFormRight() {
        FormLayout formLayoutRight = new FormLayout();
        formLayoutRight.setSizeFull();
        formLayoutRight.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        this.baseCarcassSelection = getSimpleItemFilledCombo("Base Carcass", ProposalDataProvider.CARCASS_LOOKUP, null);
        baseCarcassSelection.setRequired(true);
        binder.bind(baseCarcassSelection, BASE_CARCASS_CODE);
        baseCarcassSelection.addValueChangeListener(this::refreshPrice);
        if (baseCarcassSelection.size() > 0){
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

        this.finishTypeSelection = getSimpleItemFilledCombo("Finish Type", ProposalDataProvider.FINISH_TYPE_LOOKUP, null);
        finishTypeSelection.setRequired(true);
        binder.bind(finishTypeSelection, FINISH_TYPE_CODE);
        if (finishTypeSelection.size() > 0) {
            String code = StringUtils.isNotEmpty(product.getFinishTypeCode()) ? product.getFinishTypeCode() : (String) finishTypeSelection.getItemIds().iterator().next();
            finishTypeSelection.setValue(code);
        }
        formLayoutRight.addComponent(this.finishTypeSelection);
        this.finishTypeSelection.addValueChangeListener(this::finishTypeChanged);

        shutterFinishMasterList = proposalDataProvider.getLookupItems(ProposalDataProvider.FINISH_LOOKUP);
        List<LookupItem> filteredShutterFinish = filterShutterFinishByType();
        this.shutterFinishSelection = getSimpleItemFilledCombo("Finish", filteredShutterFinish, null);
        shutterFinishSelection.setRequired(true);
        binder.bind(shutterFinishSelection, SHUTTER_FINISH_CODE);
        shutterFinishSelection.addValueChangeListener(this::refreshPrice);
        this.shutterFinishSelection.getContainerDataSource().removeAllItems();
        ((BeanContainer<String, LookupItem>) this.shutterFinishSelection.getContainerDataSource()).addAll(filteredShutterFinish);
        if (shutterFinishSelection.size() > 0) {
            String code = StringUtils.isNotEmpty(product.getFinishCode()) ? product.getFinishCode() : (String) shutterFinishSelection.getItemIds().iterator().next();
            shutterFinishSelection.setValue(code);
        }
        formLayoutRight.addComponent(this.shutterFinishSelection);

        formLayoutRight.addComponent(getUploadControl());

        formLayoutRight.addComponent(totalAmount);


        return formLayoutRight;
    }

    private List<LookupItem> filterShutterFinishByType() {
        List<LookupItem> filteredShutterFinish = new ArrayList<>();

        String selectedFinishTypeCode = (String) finishTypeSelection.getValue();

        for (LookupItem shutterFinishComboItem : shutterFinishMasterList) {
            if (selectedFinishTypeCode.equals(shutterFinishComboItem.getAdditionalType())) {
                filteredShutterFinish.add(shutterFinishComboItem);
            }
        }
        return filteredShutterFinish;
    }

    private void finishTypeChanged(Property.ValueChangeEvent valueChangeEvent) {
        List<LookupItem> filteredShutterFinish = filterShutterFinishByType();
        this.shutterFinishSelection.getContainerDataSource().removeAllItems();
        ((BeanContainer<String, LookupItem>) this.shutterFinishSelection.getContainerDataSource()).addAll(filteredShutterFinish);
        if (filteredShutterFinish.size() > 0)
            shutterFinishSelection.setValue(shutterFinishSelection.getItemIds().iterator().next());

    }

    private Component getUploadControl() {
        this.uploadCtrl = new Upload("Import Quotation Sheet", (filename, mimeType) -> {
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
            uploadFile = new File(getUploadPath() + "/" + filename);
            uploadFile.getParentFile().mkdirs();
            try {
                fos = new FileOutputStream(uploadFile);
            } catch (final FileNotFoundException e) {
                NotificationUtil.showNotification("Please specify the file path correctly.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            }
            return fos;
        });

/*
        uploadCtrl.setButtonCaption(null);

        uploadCtrl.addChangeListener(changeEvent -> {
            if (changeEvent.getFilename() != null) uploadCtrl.setButtonCaption("Upload");
        });
*/

        this.uploadCtrl.setStyleName("upload-btn");

        this.uploadCtrl.addProgressListener((Upload.ProgressListener) (readBytes, contentLength) -> LOG.debug("Progress " + (readBytes * 100 / contentLength)));

        this.uploadCtrl.addSucceededListener((Upload.SucceededListener) event -> {

            String filename = event.getFilename();
            String quoteFilePath = getUploadPath() + "/" + filename;
            product.setQuoteFilePath(quoteFilePath);
            Product productResult = proposalDataProvider.mapAndUpdateProduct(product);
            binder.getItemDataSource().getItemProperty(Product.ID).setValue(productResult.getId());
            binder.getItemDataSource().getItemProperty(Product.MODULES).setValue(productResult.getModules());
            binder.getItemDataSource().getItemProperty(Product.TYPE).setValue(TYPES.CUSTOMIZED.name());
            binder.getItemDataSource().getItemProperty(Product.QUOTE_FILE_PATH).setValue(quoteFilePath);
            product.setType(TYPES.CUSTOMIZED.name());
            proposal.getProducts().add(product);
            initModules(product);
            moduleContainer.removeAllItems();
            moduleContainer.addAll(product.getModules());
            modulesGrid.sort(SEQ, SortDirection.ASCENDING);

            if (product.hasImportErrorStatus()) {
                NotificationUtil.showNotification("Error in mapping module(s), please upload new sheet!", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            } else if (product.allModulesMapped()) {
                enableSave();
                NotificationUtil.showNotification("File uploaded successfully", NotificationUtil.STYLE_BAR_SUCCESS_SMALL);
            }
            LOG.debug("Successfully uploaded - " + filename);
        });
        return uploadCtrl;
    }

    private void initModules(Product product) {
        for (Module module : product.getModules()) {

            module.setMakeTypeText(Module.DEFAULT);
            module.setCarcassText(Module.DEFAULT);
            module.setFinishTypeText(Module.DEFAULT);
            module.setFinishText(Module.DEFAULT);

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

    private String getUploadPath() {
        return proposal.getProposalHeader().getFolderPath();
    }

    private Component buildModulesForm() {
        HorizontalLayout hLayout = new HorizontalLayout();
        hLayout.setSizeFull();

        moduleContainer = new BeanItemContainer<>(Module.class);
        GeneratedPropertyContainer genContainer = new GeneratedPropertyContainer(moduleContainer);

        genContainer.addGeneratedProperty("action", getActionTextGenerator());
        genContainer.addGeneratedProperty("colorName", getColorNameGenerator());

        modulesGrid = new Grid(genContainer);
        modulesGrid.setStyleName("modules-grid");
        modulesGrid.setSizeFull();
        modulesGrid.setColumnReorderingAllowed(true);
        modulesGrid.setColumns(Module.IMPORT_STATUS, Module.SEQ, Module.UNIT_TYPE, Module.IMPORTED_MODULE_TEXT, Module.MG_MODULE_CODE,
                Module.MAKE_TYPE_TEXT, Module.CARCASS_MATERIAL_TEXT, Module.FINISH_TYPE_TEXT, Module.SHUTTER_FINISH_TEXT, "colorName", Module.AMOUNT, "action");

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
        columns.get(idx++).setHeaderCaption("Finish Type");
        columns.get(idx++).setHeaderCaption("Shutter Finish");
        columns.get(idx++).setHeaderCaption("Color");
        columns.get(idx++).setHeaderCaption("Amount");
        Grid.Column actionColumn = columns.get(idx++);
        actionColumn.setHeaderCaption("Action");
        actionColumn.setRenderer(new EditButtonValueRenderer(rendererClickEvent -> {
            try {
                binder.commit();
            } catch (FieldGroup.CommitException e) {
                NotificationUtil.showNotification("Please fill all mandatory fields before proceeding!", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                return;
            }
            Module module = (Module) rendererClickEvent.getItemId();
            if (!module.getImportStatus().equals(ImportStatusType.n.name())) {
                ModuleDetailsWindow.open(module, product);
            } else {
                NotificationUtil.showNotification("Cannot edit unmapped module.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            }
        }));

        hLayout.addComponent(modulesGrid);
        hLayout.setExpandRatio(modulesGrid, 1);

        if (!product.getModules().isEmpty()) {
            moduleContainer.addAll(product.getModules());
            modulesGrid.sort(SEQ, SortDirection.ASCENDING);
        }

        return modulesGrid;
    }

    private PropertyValueGenerator<String> getActionTextGenerator() {
        return new PropertyValueGenerator<String>() {

            @Override
            public String getValue(Item item, Object o, Object o1) {
                return "Edit";
            }

            @Override
            public Class<String> getType() {
                return String.class;
            }
        };
    }

    private PropertyValueGenerator<String> getColorNameGenerator() {
        return new PropertyValueGenerator<String>() {

            @Override
            public String getValue(Item item, Object o, Object o1) {
                String colorCode = (String) item.getItemProperty(Module.COLOR_CODE).getValue();
                Color color = ConfigHolder.getInstance().getColors().get(colorCode);
                return color != null ? color.getName() : "UNKNOWN";
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

    /*
     * Addons section
     */

    private Component buildAddonsForm() {
        HorizontalLayout horizontalLayout0 = new HorizontalLayout();
        horizontalLayout0.setSizeFull();

        List<String> addonTypes = proposalDataProvider.getAddonTypes();
        List<String> addonNameL = new ArrayList<>();

        Map<String, String> addonTypeAndNameMap = new HashMap<>();

        addonNameL.add("4T HANDLES");
        addonNameL.add("GRANITE 4MX");
        addonNameL.add("DADO");

        ComponentGrid<Addon> componentGrid = new ComponentGrid<>(Addon.class);
        componentGrid.setSizeFull();

        componentGrid.setRows(getAddon(""));
        componentGrid.setDetailsGenerator(new AddonsDetailGenerator());

        componentGrid.addComponentColumn("type", addon ->
                createAddonTypeSelector(componentGrid.getComponentGridDecorator(), addon, addonTypes));

        componentGrid.addComponentColumn("name", addon -> createAddonNameSelector(componentGrid.getComponentGridDecorator(), addon, addonNameL));
        componentGrid.addComponentColumn("imagePath", addon -> createAddonImage(componentGrid.getComponentGridDecorator(), addon));
        componentGrid.addComponentColumn("qtyArea", addon -> createAddonQty(componentGrid.getComponentGridDecorator(), addon));
        componentGrid.addComponentColumn("rate", addon -> createAddonRate(componentGrid.getComponentGridDecorator(), addon));
        componentGrid.addComponentColumn("delete", addon -> createAddonDelete(componentGrid.getComponentGridDecorator(), addon));
        componentGrid.setColumnOrder("seqNo", "type", "name", "imagePath", "qtyArea", "rate", "amount", "delete");
        componentGrid.setColumns("seqNo", "type", "name", "imagePath", "qtyArea", "rate", "amount", "delete");

        HeaderRow mainHeader = componentGrid.getDefaultHeaderRow();
        mainHeader.getCell("seqNo").setText("#");
        mainHeader.getCell("type").setText("Type");
        mainHeader.getCell("name").setText("Name(Select)");
        mainHeader.getCell("imagePath").setText("Image");
        mainHeader.getCell("qtyArea").setText("Qty / Area");
        mainHeader.getCell("rate").setText("Rate");
        mainHeader.getCell("amount").setText("Amount");

        Image image = new Image();
        image.setSource(new ThemeResource("img/add_black.png"));
        image.setHeight("25px");
        image.setWidth("25px");
        mainHeader.getCell("delete").setComponent(image);

        image.addClickListener(new MouseEvents.ClickListener() {
            @Override
            public void click(com.vaadin.event.MouseEvents.ClickEvent event) {
                int newId = componentGrid.getContainerDataSource().size() + 1;
                Addon addon = new Addon();
                addon.setId(newId);
                addon.setImagePath("");
                addon.setAmount(0.00);
                addon.setRate(0.00);
                addon.setQtyArea(0);
                componentGrid.getContainerDataSource().addItem(addon);
                componentGrid.refresh();
            }
        });

        horizontalLayout0.addComponent(componentGrid);

        return horizontalLayout0;
    }

    public Component createAddonTypeSelector(ComponentGridDecorator<Addon> componentGridDecorator, Addon
            addon, List<String> types) {
        ComboBox select = new ComboBox();
        select.setWidth("150px");
        select.setHeight("30px");

        select.addItems(types);
        select.setPropertyDataSource(new BeanItem<>(addon).getItemProperty("type"));
        select.addValueChangeListener(e -> {
            componentGridDecorator.refresh();
            //System.out.println("" + e.getProperty().getValue().toString());
        });
        return select;
    }


    public Component createAddonNameSelector(ComponentGridDecorator<Addon> componentGridDecorator, Addon
            addon, List<String> names) {
        ComboBox select = new ComboBox();
        select.setWidth("150px");
        select.setHeight("30px");

        select.addItems(names);
        select.setPropertyDataSource(new BeanItem<>(addon).getItemProperty("name"));
        select.addValueChangeListener(e -> {
            componentGridDecorator.refresh();
            //System.out.println("" + e.getProperty().getValue().toString());
        });
        return select;
    }

    public Component createAddonImage(ComponentGridDecorator<Addon> componentGridDecorator, Addon
            addon) {
        ExternalResource resource =
                new ExternalResource(addon.getImagePath());
        Embedded image = new Embedded("", resource);
        image.setHeight(32, Sizeable.Unit.PIXELS);
        image.setWidth(32, Sizeable.Unit.PIXELS);
        return image;
    }

    public Component createAddonQty(ComponentGridDecorator<Addon> componentGridDecorator, Addon
            addon) {
        TextField qtyTxtField = new TextField("", addon.getQtyArea() + "");
        qtyTxtField.setWidth("40px");
        qtyTxtField.setHeight("30px");
        return qtyTxtField;
    }

    public Component createAddonRate(ComponentGridDecorator<Addon> componentGridDecorator, Addon
            addon) {
        TextField qtyTxtField = new TextField("", addon.getRate() + "");
        qtyTxtField.setWidth("60px");
        qtyTxtField.setHeight("30px");
        return qtyTxtField;
    }

    public Component createAddonDelete(ComponentGridDecorator<Addon> componentGridDecorator, Addon
            addon) {
        HorizontalLayout actionLayout = new HorizontalLayout();
        actionLayout.setSizeFull();
        actionLayout.setSpacing(true);

        Image editImage = new Image();
        editImage.setSource(new ThemeResource("img/edit.png"));
        editImage.setWidth("25px");
        editImage.setHeight("25px");

        editImage.addClickListener(new MouseEvents.ClickListener() {
            @Override
            public void click(com.vaadin.event.MouseEvents.ClickEvent event) {
                componentGridDecorator.refresh();
            }
        });

        Image deleteImage = new Image();
        deleteImage.setSource(new ThemeResource("img/delete.png"));
        deleteImage.setWidth("25px");
        deleteImage.setHeight("25px");

        deleteImage.addClickListener(new MouseEvents.ClickListener() {
            @Override
            public void click(com.vaadin.event.MouseEvents.ClickEvent event) {
                componentGridDecorator.getGrid().getContainerDataSource().removeItem(addon);
                componentGridDecorator.refresh();
            }
        });

        actionLayout.addComponent(editImage);
        actionLayout.addComponent(deleteImage);
        return actionLayout;
    }

    private List<Addon> getAddon(String string) {
        List<Addon> addons = new ArrayList<>();

        Addon addon = new Addon();
        addon.setSeqNo(1);
        addon.setId(1);
        addon.setType("ACCESSORY");
        addon.setName("GRANITE 4MX");
        addon.setDescription("");
        addon.setImagePath("https://www.funkit.net/2/2014/07/kitchen-granite-countertops-granite-at-beatiful-kitchen-island-minimalist-decor-ideas-brilliant-countertops-kitchen-options-eco-friendly-prefabricated-fake-zodiac-outlet-venetian-countertops-counter-tops-wils-options-for-1110x833.jpg");
        addon.setQtyArea(20);
        addon.setUom("");
        addon.setRate(1000.00);
        addon.setAmount(20000.00);
        addons.add(addon);

        Addon addon2 = new Addon();
        addon2.setSeqNo(2);
        addon2.setId(2);
        addon2.setType("COUNTER TOP");
        addon2.setName("4T HANDLES");
        addon2.setDescription("");
        addon2.setImagePath("http://www.crystalhandle.com/images/cabinet_handles/cabinet_handle_od.jpg");
        addon2.setQtyArea(8);
        addon2.setUom("");
        addon2.setRate(100.00);
        addon2.setAmount(800.00);
        addons.add(addon2);

        Addon addon3 = new Addon();
        addon3.setSeqNo(3);
        addon3.setId(3);
        addon3.setType("SERVICES");
        addon3.setName("DADO");
        addon3.setDescription("");
        addon3.setImagePath("");
        addon3.setQtyArea(4);
        addon3.setUom("");
        addon3.setRate(1500.00);
        addon3.setAmount(6000.00);
        addons.add(addon3);

        return addons;
    }

	/*
     * Summary Section
	 */

    private Component buildSummaryForm() {
        VerticalLayout verticalLayout = new VerticalLayout();

        HorizontalLayout horizontalLayout0 = new HorizontalLayout();
        horizontalLayout0.setSizeFull();
        verticalLayout.addComponent(horizontalLayout0);

        return verticalLayout;
    }

    /*
     * Attachment section
     */
    private Component buildAttachmentsForm() {
        VerticalLayout verticalLayout = new VerticalLayout();

        HorizontalLayout horizontalLayout0 = new HorizontalLayout();
        horizontalLayout0.setSizeFull();
        verticalLayout.addComponent(horizontalLayout0);

        return verticalLayout;
    }


    private Component buildFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.setSizeFull();
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Unit.PERCENTAGE);

        closeBtn = new Button("Close");
        closeBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                ConfirmDialog.show(UI.getCurrent(), "", "Are you sure? Unsaved data will be lost.",
                        "Close", "Cancel", dialog -> {
                            if (!dialog.isCanceled()) {
                                closeWindow();
                            } else {
                                //close();
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
                    DashboardEventBus.post(new ProposalEvent.ProductCreatedEvent(product));
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

        if (StringUtils.isEmpty(product.getQuoteFilePath())) {
            disableSave();
        }

        footer.addComponent(saveBtn);
        footer.setComponentAlignment(closeBtn, Alignment.TOP_RIGHT);

        return footer;
    }

    public static void open(Proposal proposal) {
        DashboardEventBus.post(new DashboardEvent.CloseOpenWindowsEvent());
        Window w = new CustomizedProductDetailsWindow(proposal);
        UI.getCurrent().addWindow(w);
        w.focus();
    }

    public static void closeWindow() {
        DashboardEventBus.post(new DashboardEvent.CloseOpenWindowsEvent());
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
        List<Module> modules = (List<Module>) binder.getItemDataSource().getItemProperty("modules").getValue();

        double amount = 0;
        for (Module module : modules) {
            amount += module.getAmount();
        }

        totalAmount.setReadOnly(false);
        totalAmount.setValue(amount + "");
        totalAmount.setReadOnly(true);

        product.setAmount(amount);

    }

    @Subscribe
    public void moduleUpdated(final ProposalEvent.ModuleUpdated event) {
        List<Module> modules = (List<Module>) binder.getItemDataSource().getItemProperty("modules").getValue();
        modules.remove(event.getModule());
        modules.add(event.getModule());
        moduleContainer.removeAllItems();
        moduleContainer.addAll(modules);
        modulesGrid.sort(Module.SEQ, SortDirection.ASCENDING);
        updateTotalAmount();

        if (product.allModulesMapped()) {
            enableSave();
        }
    }

    public static void open(Proposal proposal, Product product) {
        DashboardEventBus.post(new DashboardEvent.CloseOpenWindowsEvent());
        Window w = new CustomizedProductDetailsWindow(proposal, product);
        UI.getCurrent().addWindow(w);
        w.focus();

    }
}