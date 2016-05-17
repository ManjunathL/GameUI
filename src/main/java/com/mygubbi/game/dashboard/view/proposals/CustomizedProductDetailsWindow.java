package com.mygubbi.game.dashboard.view.proposals;


import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.data.dummy.FileDataProviderUtil;
import com.mygubbi.game.dashboard.domain.*;
import com.mygubbi.game.dashboard.domain.JsonPojo.SimpleComboItem;
import com.mygubbi.game.dashboard.domain.Module.ImportStatus;
import com.mygubbi.game.dashboard.event.DashboardEvent;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.mygubbi.game.dashboard.view.NotificationUtil;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.event.MouseEvents;
import com.vaadin.server.*;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Grid.HeaderRow;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.renderers.ImageRenderer;
import com.vaadin.ui.themes.ValoTheme;
import de.datenhahn.vaadin.componentrenderer.grid.ComponentGrid;
import de.datenhahn.vaadin.componentrenderer.grid.ComponentGridDecorator;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.*;

@SuppressWarnings("serial")
public class CustomizedProductDetailsWindow extends Window {

    private static final Logger LOG = LogManager.getLogger(CustomizedProductDetailsWindow.class);

    private TextField itemTitleField;
    private ComboBox roomSelection;
    private ComboBox makeType;
    private ComboBox productSelection;
    private ComboBox carcassMaterialSelection;

    private TextField typeField;
    private ComboBox shutterFinishSelection;
    private ComboBox finishTypeSelection;

    private Upload uploadCtrl;
    private File uploadFile;

    private TabSheet tabSheet;
    private Button closeBtn;
    private Button saveBtn;

    private Proposal proposal;
    private Product product = new Product();

    private ProposalDataProvider proposalDataProvider = new ProposalDataProvider(new FileDataProviderUtil());
    private List<SimpleComboItem> shutterFinishMasterList;
    private BeanItemContainer<Module> moduleContainer;

    private CustomizedProductDetailsWindow(Proposal proposal) {

        this.proposal = proposal;

        setModal(true);
        //setCloseShortcut(KeyCode.ESCAPE, null);
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

        this.itemTitleField = new TextField("Product Title");
        formLayoutLeft.addComponent(itemTitleField);

        this.productSelection = getSimpleItemFilledCombo("Product Category", "product_data");
        formLayoutLeft.addComponent(this.productSelection);

        this.roomSelection = getSimpleItemFilledCombo("Room", "room_data");
        formLayoutLeft.addComponent(this.roomSelection);

        this.makeType = getSimpleItemFilledCombo("Make Type", "make_type_data");
        formLayoutLeft.addComponent(this.makeType);

        return formLayoutLeft;
    }

    private FormLayout buildAddItemBasicFormRight() {
        FormLayout formLayoutRight = new FormLayout();
        formLayoutRight.setSizeFull();
        formLayoutRight.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        this.carcassMaterialSelection = getSimpleItemFilledCombo("Carcass Material", "carcass_material_data");
        formLayoutRight.addComponent(this.carcassMaterialSelection);

        this.finishTypeSelection = getSimpleItemFilledCombo("Finish Type", "finish_type_data");
        formLayoutRight.addComponent(this.finishTypeSelection);
        this.finishTypeSelection.addValueChangeListener(this::finishTypeChanged);

        shutterFinishMasterList = proposalDataProvider.getComboItems("shutter_material_data");
        List<SimpleComboItem> filteredShutterFinish = filterShutterFinishByType();
        this.shutterFinishSelection = getSimpleItemFilledCombo("Finish", filteredShutterFinish);
        formLayoutRight.addComponent(this.shutterFinishSelection);

        formLayoutRight.addComponent(getUploadControl());
        return formLayoutRight;
    }

    private List<SimpleComboItem> filterShutterFinishByType() {
        List<SimpleComboItem> filteredShutterFinish = new ArrayList<>();

        SimpleComboItem selectedItem = (SimpleComboItem) finishTypeSelection.getValue();

        for (SimpleComboItem shutterFinishComboItem : shutterFinishMasterList) {
            if (selectedItem.getCode().equals(shutterFinishComboItem.getType())) {
                filteredShutterFinish.add(shutterFinishComboItem);
            }
        }
        return filteredShutterFinish;
    }

    private void finishTypeChanged(Property.ValueChangeEvent valueChangeEvent) {
        List<SimpleComboItem> filteredShutterFinish = filterShutterFinishByType();
        this.shutterFinishSelection.removeAllItems();
        this.shutterFinishSelection.addItems(filteredShutterFinish);
        SimpleComboItem selectedProductType = (filteredShutterFinish.size() > 0) ? filteredShutterFinish.get(0) : null;
        shutterFinishSelection.setValue(selectedProductType);

    }

    private Component getUploadControl() {
        this.uploadCtrl = new Upload("Import Quotation Sheet", (filename, mimeType) -> {
            LOG.debug("Received upload - " + filename);
            FileOutputStream fos = null;
            uploadFile = new File(getUploadPath() + "/" + filename);
            uploadFile.getParentFile().mkdirs();
            try {
                fos = new FileOutputStream(uploadFile);
            } catch (final FileNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            return fos;
        });

        this.uploadCtrl.setStyleName("upload-btn");

        this.uploadCtrl.addProgressListener((Upload.ProgressListener) (readBytes, contentLength) -> LOG.debug("Progress " + (readBytes * 100 / contentLength)));

        this.uploadCtrl.addSucceededListener((Upload.SucceededListener) event -> {
            String filename = event.getFilename();
            String quoteFilePath = getUploadPath() + "/" + filename;
            product = proposalDataProvider.mapAndUpdateProduct(proposal.getProposalHeader().getProposalId(), quoteFilePath);
            //product.setQuoteFilePath(quoteFilePath);
            product.setSeq(proposal.getProducts().size() + 1);
            product.setType(Product.TYPE.CUSTOM.name());
            proposal.getProducts().add(product);
            moduleContainer.addAll(product.getModules());
            enableSave();
            LOG.debug("Successfully uploaded - " + filename);
            NotificationUtil.showNotification("File uploaded successfully", NotificationUtil.STYLE_BAR_SUCCESS_SMALL);
        });

        this.uploadCtrl.addFailedListener((Upload.FailedListener) event -> {
            LOG.error("Error upload - " + event.getFilename() + " :" + event.getReason().getMessage());
            NotificationUtil.showNotification("File upload failed - " + event.getReason().getMessage(), NotificationUtil.STYLE_BAR_ERROR_SMALL);
        });
        return uploadCtrl;
    }

    private void enableSave() {
        saveBtn.setEnabled(true);
    }

    private void disableSave() {
        saveBtn.setEnabled(false);
    }

    private String getUploadPath() {
        return proposal.getProposalHeader().getFolderPath() + "/product_imports";
    }

    private Component buildModulesForm() {
        HorizontalLayout hLayout = new HorizontalLayout();
        hLayout.setSizeFull();

        moduleContainer = new BeanItemContainer<>(Module.class);
        GeneratedPropertyContainer genContainer = new GeneratedPropertyContainer(moduleContainer);

        genContainer.addGeneratedProperty("importedModuleText", new PropertyValueGenerator<String>() {
            @Override
            public String getValue(Item item, Object o, Object o1) {

                String importedModuleCode = item.getItemProperty("importedModuleCode").getValue().toString();
                String importedModuleDefaultCode = item.getItemProperty("importedModuleDefaultCode").getValue().toString();
                String importStatus = item.getItemProperty("importStatus").getValue().toString();

                if (importStatus.equals(ImportStatus.default_matched.name())) {
                    return importedModuleCode + " / " + importedModuleDefaultCode;
                }

                return importedModuleCode;
            }

            @Override
            public Class<String> getType() {
                return String.class;
            }
        });
        //container.addAll(proposalHeaders);

        Grid grid = new Grid(genContainer);
        grid.setSizeFull();
        grid.setColumnReorderingAllowed(true);
        grid.setColumns("importStatus", "seq", "importedModuleText", "mgModuleCode", "makeType", "carcassMaterial", "finishType", "shutterFinish", "color", "amount");

        List<Grid.Column> columns = grid.getColumns();
        int idx = 0;
        Grid.Column statusColumn = columns.get(idx++);
        statusColumn.setHeaderCaption("");
        statusColumn.setRenderer(new ImageRenderer(), new Converter<Resource, String>() {
            @Override
            public String convertToModel(Resource resource, Class<? extends String> aClass, Locale locale) throws ConversionException {
                return "not needed";
            }

            @Override
            public Resource convertToPresentation(String s, Class<? extends Resource> aClass, Locale locale) throws ConversionException {
                return getImportStatusResource(s);
            }

            @Override
            public Class<String> getModelType() {
                return String.class;
            }

            @Override
            public Class<Resource> getPresentationType() {
                return Resource.class;
            }
        });

        columns.get(idx++).setHeaderCaption("#");
        columns.get(idx++).setHeaderCaption("Imported Module");
        columns.get(idx++).setHeaderCaption("MG Module");
        columns.get(idx++).setHeaderCaption("Make Type");
        columns.get(idx++).setHeaderCaption("Carcass Material");
        columns.get(idx++).setHeaderCaption("Finish Type");
        columns.get(idx++).setHeaderCaption("Shutter Finish");
        columns.get(idx++).setHeaderCaption("Color");
        columns.get(idx++).setHeaderCaption("Amount");

        hLayout.addComponent(grid);
        hLayout.setExpandRatio(grid, 1);

        return grid;
    }

/*
    private Component buildModulesForm() {
        HorizontalLayout hLayout = new HorizontalLayout();
        hLayout.setSizeFull();

*/
/*
        List<SimpleComboItem> carcassMaterials = proposalDataProvider.getComboItems("carcass_material_data");
        carcassMaterials.forEach(item -> carcassMaterialL.add(item.title));

        List<SimpleComboItem> shutterMaterials = proposalDataProvider.getComboItems("shutter_material_data");
        shutterMaterials.forEach(item -> shutterFinishL.add(item.title));

        List<SimpleComboItem> colors = proposalDataProvider.getComboItems("color_data");
        colors.forEach(item -> colorL.add(item.title));
*//*


        ComponentGrid<Module> componentGrid = new ComponentGrid<>(Module.class);
        componentGrid.setSizeFull();

        //componentGrid.setRows(getModules(""));
        componentGrid.setDetailsGenerator(new ModulesDetailGenerator());

        componentGrid.addComponentColumn("importStatus", module -> getImportStatusResource(componentGrid.getComponentGridDecorator(), module));

        componentGrid.addComponentColumn("mgModuleCode", module ->
                createMgModuleSelector(componentGrid.getComponentGridDecorator(), module));

        componentGrid.addComponentColumn("image", module -> createModuleImage(componentGrid.getComponentGridDecorator(), module));

        componentGrid.addComponentColumn("carcassMaterial", module -> createModuleCM(componentGrid.getComponentGridDecorator(), module));
        componentGrid.addComponentColumn("shutterMaterial", module -> createModuleSM(componentGrid.getComponentGridDecorator(), module));
        componentGrid.addComponentColumn("color", module -> createModuleColor(componentGrid.getComponentGridDecorator(), module));

        componentGrid.setColumnOrder("importStatus", "seqNo", "importedModuleCode", "image", "mgModuleCode", "carcassMaterial", "shutterMaterial", "color", "qty", "amount");
        componentGrid.setColumns("importStatus", "seqNo", "importedModuleCode", "image", "mgModuleCode", "carcassMaterial", "shutterMaterial", "color", "qty", "amount");

        HeaderRow mainHeader = componentGrid.getDefaultHeaderRow();
        mainHeader.getCell("importStatus").setText("");
        mainHeader.getCell("seqNo").setText("#");
        mainHeader.getCell("importedModuleCode").setText("Imported Module");
        mainHeader.getCell("mgModuleCode").setText("Mg Module");
        mainHeader.getCell("carcassMaterial").setText("Carcass Material");
        mainHeader.getCell("shutterMaterial").setText("Shutter Finish");
        mainHeader.getCell("color").setText("Color");
        mainHeader.getCell("qty").setText("Qty");
        mainHeader.getCell("amount").setText("Amount");

        hLayout.addComponent(componentGrid);

        hLayout.setExpandRatio(componentGrid, 1);

        return componentGrid;
    }

*/
    public enum Status {DEFAULT_MATCHED, MATCHED, NOT_MATCHED}

    private Resource getImportStatusResource(String importStatus) {
        switch (ImportStatus.valueOf(importStatus)) {
            case default_matched:
                return new ThemeResource("img/default-matched.png");
            case success:
                return new ThemeResource("img/active.png");
            case error:
                return new ThemeResource("img/not_matched.png");
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
            System.out.println("" + e.getProperty().getValue().toString());
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
            System.out.println("" + e.getProperty().getValue().toString());
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
        closeBtn.addStyleName(ValoTheme.BUTTON_PRIMARY);
        closeBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                closeWindow();
            }
        });
        closeBtn.focus();
        footer.addComponent(closeBtn);
        footer.setSpacing(true);

        saveBtn = new Button("Save");
        saveBtn.addStyleName(ValoTheme.BUTTON_PRIMARY);
        saveBtn.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    // Updated user should also be persisted to database. But
                    // not in this demo.

                    Notification success = new Notification(
                            "Item details saved successfully");
                    success.setDelayMsec(2000);
                    success.setStyleName("bar success small");
                    success.setPosition(Position.BOTTOM_CENTER);
                    success.show(Page.getCurrent());

                    DashboardEventBus.post(new DashboardEvent.ItemDetailsEvent());
                    close();
                } catch (Exception e) {
                    Notification.show("Error while saving Item details",
                            Type.ERROR_MESSAGE);
                }

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

    public static void open(ProposalHeader proposalHeader) {
        DashboardEventBus.post(new DashboardEvent.CloseOpenWindowsEvent());
        Proposal proposal = new Proposal();
        proposal.setProposalHeader(proposalHeader);
        Window w = new CustomizedProductDetailsWindow(proposal);
        UI.getCurrent().addWindow(w);
        w.focus();
    }

    public static void closeWindow() {
        DashboardEventBus.post(new DashboardEvent.CloseOpenWindowsEvent());
    }

    private ComboBox getSimpleItemFilledCombo(String caption, List<SimpleComboItem> list) {
        final BeanItemContainer<SimpleComboItem> container =
                new BeanItemContainer<SimpleComboItem>(SimpleComboItem.class);
        container.addAll(list);

        ComboBox select = new ComboBox(caption);
        select.setNullSelectionAllowed(false);
        select.setWidth("250px");
        select.setContainerDataSource(container);
        select.setItemCaptionPropertyId("title");
        SimpleComboItem selectedProductType = (container.size() > 0) ? container.getItemIds().get(0) : null;
        select.setValue(selectedProductType);
        return select;
    }

    private ComboBox getSimpleItemFilledCombo(String caption, String dataType) {
        List<SimpleComboItem> list = proposalDataProvider.getComboItems(dataType);
        return getSimpleItemFilledCombo(caption, list);
    }
}