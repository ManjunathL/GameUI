package com.mygubbi.game.dashboard.view.proposals;


import com.mygubbi.game.dashboard.config.ConfigHolder;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.data.dummy.FileDataProviderUtil;
import com.mygubbi.game.dashboard.domain.*;
import com.mygubbi.game.dashboard.domain.JsonPojo.SimpleComboItem;
import com.mygubbi.game.dashboard.event.DashboardEvent;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.mygubbi.game.dashboard.event.ProposalEvent;
import com.mygubbi.game.dashboard.view.NotificationUtil;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FileResource;
import com.vaadin.server.Responsive;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vaadin.peter.imagestrip.ImageStrip;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class ModuleDetailsWindow extends Window {

    private static final Logger LOG = LogManager.getLogger(ModuleDetailsWindow.class);
    private final List<MGModule> mgModules;
    private final Product product;

    private TextField importedModule;
    private TextField description;
    private TextField dimensions;
    private ComboBox mgModuleCombo;
    private ComboBox carcassMaterialSelection;
    private ComboBox colorCombo;
    private ComboBox makeType;

    private ComboBox shutterFinishSelection;
    private ComboBox finishTypeSelection;

    private Button cancelBtn;
    private Button applyButton;

    private Module module;

    private ProposalDataProvider proposalDataProvider = new ProposalDataProvider(new FileDataProviderUtil());
    private final BeanFieldGroup<Module> binder = new BeanFieldGroup<>(Module.class);

    private List<SimpleComboItem> shutterFinishMasterList;
    private Image moduleImage;
    private ThemeResource emptyModuleImage;
    private ImageStrip accessoryImageStrip;
    private String basePath = ConfigHolder.getInstance().getStringValue("imageBasePath", "");
    private TextField totalAmount;


    private ModuleDetailsWindow(Module module, Product product) {

        this.product = product;
        this.module = module;
        initModule();
        this.mgModules = proposalDataProvider.getMGModules(module.getImportedModuleCode(), module.getImportedModuleDefaultCode());
        this.binder.setItemDataSource(this.module);

        setModal(true);
        removeCloseShortcut(ShortcutAction.KeyCode.ESCAPE);
        setWidth("60%");
        setClosable(false);
        setCaption("Edit Module Configuration");

        VerticalLayout vLayout = new VerticalLayout();
        vLayout.setMargin(new MarginInfo(true, true, true, true));
        setContent(vLayout);
        Responsive.makeResponsive(this);

        HorizontalLayout horizontalLayout0 = new HorizontalLayout();
        horizontalLayout0.setSizeFull();
        horizontalLayout0.addComponent(buildImportedModuleForm());
        vLayout.addComponent(horizontalLayout0);
        horizontalLayout0.setHeightUndefined();

        HorizontalLayout horizontalLayout1 = new HorizontalLayout();
        horizontalLayout1.setSizeFull();
        horizontalLayout1.addComponent(buildMGModuleComponent());
        vLayout.addComponent(horizontalLayout1);
        horizontalLayout1.setHeightUndefined();

        HorizontalLayout horizontalLayout2 = new HorizontalLayout();
        horizontalLayout2.setMargin(new MarginInfo(false, false, false, false));
        horizontalLayout2.setSizeFull();
        horizontalLayout2.addComponent(buildModuleSelectionsComponent());
        vLayout.addComponent(horizontalLayout2);
        horizontalLayout2.setHeightUndefined();
        //vLayout.setExpandRatio(horizontalLayout0, 0.9f);

        Component footerLayOut = buildFooter();
        vLayout.addComponent(footerLayOut);
        //vLayout.setExpandRatio(footerLayOut, 0.1f);

        updateValues();
    }

    private void updateValues() {
        if (!StringUtils.isEmpty(module.getMgModuleCode())) {
            mgModuleCombo.setValue(module.getMgModuleCode());
            mgModuleChanged(null);
        }

        makeType.setValue(module.getMakeTypeCode());
        carcassMaterialSelection.setValue(module.getCarcassMaterialCode());
        finishTypeSelection.setValue(module.getFinishTypeCode());
        shutterFinishSelection.setValue(module.getShutterFinishCode());
    }

    private void initModule() {
        if (Module.DEFAULT.equals(module.getMakeTypeText())) {
            module.setMakeTypeCode(product.getMakeTypeCode());
            //makeType
        }
        if (Module.DEFAULT.equals(module.getCarcassMaterialText())) {
            module.setCarcassMaterialCode(product.getCarcassMaterialCode());
        }
        if (Module.DEFAULT.equals(module.getFinishTypeText())) {
            module.setFinishTypeCode(product.getFinishTypeCode());
        }
        if (Module.DEFAULT.equals(module.getShutterFinishText())) {
            module.setShutterFinishCode(product.getShutterFinishCode());
        }
    }

    private FormLayout buildImportedModuleForm() {
        FormLayout formLayoutLeft = new FormLayout();
        formLayoutLeft.setSizeFull();
        formLayoutLeft.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        this.importedModule = (TextField) binder.buildAndBind("Imported Module", "importedModuleText");
        this.importedModule.setReadOnly(true);
        formLayoutLeft.addComponent(importedModule);
        return formLayoutLeft;
    }

    private Component buildMGModuleComponent() {

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSizeFull();
        verticalLayout.addComponent(horizontalLayout);

        FormLayout formLayoutLeft = new FormLayout();
        formLayoutLeft.setSizeFull();
        formLayoutLeft.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        horizontalLayout.addComponent(formLayoutLeft);

        this.mgModuleCombo = getMGModuleCombo("MG Module");
        binder.bind(this.mgModuleCombo, "mgModuleCode");
        this.mgModuleCombo.addValueChangeListener(this::mgModuleChanged);
        formLayoutLeft.addComponent(this.mgModuleCombo);

        this.description = new TextField("Description");
        this.description.setReadOnly(true);
        formLayoutLeft.addComponent(this.description);

        this.dimensions = new TextField("Dimensions");
        formLayoutLeft.addComponent(this.dimensions);

        emptyModuleImage = new ThemeResource("img/empty-poster.png");
        moduleImage = new Image("", emptyModuleImage);
        moduleImage.setHeight("180px");
        moduleImage.setWidth("180px");

        horizontalLayout.addComponent(moduleImage);

        HorizontalLayout horizontalLayout1 = new HorizontalLayout();
        horizontalLayout1.setMargin(new MarginInfo(false, true, false, true));
        Label accessories = new Label("Accessories");
        accessories.setStyleName(ValoTheme.LABEL_SMALL);
        horizontalLayout1.setSizeFull();
        horizontalLayout1.addComponent(accessories);
        verticalLayout.addComponent(horizontalLayout1);

        createAccessoryImageStrip();
        HorizontalLayout horizontalLayout2 = new HorizontalLayout();
        horizontalLayout2.setSizeFull();
        horizontalLayout2.addComponent(accessoryImageStrip);
        verticalLayout.addComponent(horizontalLayout2);

        return verticalLayout;
    }

    private void createAccessoryImageStrip() {
        accessoryImageStrip = new ImageStrip();
        accessoryImageStrip.setAnimated(true);
        accessoryImageStrip.setImageBoxWidth(140);
        accessoryImageStrip.setImageBoxHeight(140);
        accessoryImageStrip.setImageMaxWidth(125);
        accessoryImageStrip.setImageMaxHeight(125);
        accessoryImageStrip.setMaxAllowed(6);
        accessoryImageStrip.setWidth("936px");
    }

    private void mgModuleChanged(Property.ValueChangeEvent valueChangeEvent) {
        MGModule mgModule = ((BeanItem<MGModule>)this.mgModuleCombo.getItem(this.mgModuleCombo.getValue())).getBean();
        if (mgModule == null) {
            this.description.setReadOnly(false);
            this.description.setValue("");
            this.description.setReadOnly(true);
            this.dimensions.setReadOnly(false);
            this.dimensions.setValue("");
            this.dimensions.setReadOnly(true);
            this.moduleImage.setSource(emptyModuleImage);
            VerticalLayout parent = (VerticalLayout) this.accessoryImageStrip.getParent();
            AbstractSingleComponentContainer.removeFromParent(this.accessoryImageStrip);
            createAccessoryImageStrip();
            parent.addComponent(accessoryImageStrip);
            disableApply();
        } else {
            this.description.setReadOnly(false);
            this.description.setValue(mgModule.getDescription());
            this.description.setReadOnly(true);
            this.dimensions.setReadOnly(false);
            this.dimensions.setValue(mgModule.getDimensions());
            this.dimensions.setReadOnly(true);
            this.moduleImage.setSource(new FileResource(new File(basePath + mgModule.getImagePath())));
            mgModuleCombo.setNullSelectionAllowed(false);

            HorizontalLayout parent = (HorizontalLayout) this.accessoryImageStrip.getParent();
            AbstractSingleComponentContainer.removeFromParent(this.accessoryImageStrip);
            createAccessoryImageStrip();
            parent.addComponent(accessoryImageStrip);

            for (ModuleAccessory moduleAccessory : mgModule.getAccessories()) {
                accessoryImageStrip.addImage(new FileResource(new File(basePath + moduleAccessory.getImagePath())));
            }

            ModulePrice modulePrice = proposalDataProvider.getModulePrice(module);
            module.setAmount(modulePrice.getTotalCost());
            totalAmount.setReadOnly(false);
            totalAmount.setValue(modulePrice.getTotalCost() + "");
            totalAmount.setReadOnly(true);
            enableApply();
        }
    }

    private Component buildModuleSelectionsComponent() {

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSizeFull();
        verticalLayout.addComponent(horizontalLayout);

        FormLayout formLayoutLeft = new FormLayout();
        formLayoutLeft.setSizeFull();
        formLayoutLeft.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        horizontalLayout.addComponent(formLayoutLeft);

        this.makeType = getSimpleItemFilledCombo("Make Type", "make_type_data", null);
        binder.bind(makeType, "makeTypeCode");
        formLayoutLeft.addComponent(this.makeType);

        this.carcassMaterialSelection = getSimpleItemFilledCombo("Carcass Material", "carcass_material_data", null);
        binder.bind(carcassMaterialSelection, "carcassMaterialCode");
        formLayoutLeft.addComponent(this.carcassMaterialSelection);

        FormLayout formLayoutRight = new FormLayout();
        formLayoutRight.setSizeFull();
        formLayoutRight.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        horizontalLayout.addComponent(formLayoutRight);

        this.finishTypeSelection = getSimpleItemFilledCombo("Finish Type", "finish_type_data", null);
        binder.bind(finishTypeSelection, "finishTypeCode");
        formLayoutRight.addComponent(this.finishTypeSelection);
        this.finishTypeSelection.addValueChangeListener(this::finishTypeChanged);

        shutterFinishMasterList = proposalDataProvider.getComboItems("shutter_material_data");
        List<SimpleComboItem> filteredShutterFinish = filterShutterFinishByType();
        this.shutterFinishSelection = getSimpleItemFilledCombo("Finish", filteredShutterFinish, null);
        binder.bind(shutterFinishSelection, "shutterFinishCode");
        formLayoutRight.addComponent(this.shutterFinishSelection);

        totalAmount = new TextField("Total Amount");
        totalAmount.setReadOnly(true);
        formLayoutRight.addComponent(totalAmount);

        HorizontalLayout colorLayout = new HorizontalLayout();
        colorLayout.setSizeFull();
        verticalLayout.addComponent(colorLayout);

        this.colorCombo = getColorsCombo("Colors", ConfigHolder.getInstance().getFinishTypeColors().get(module.getFinishTypeCode()).getColors());
        binder.bind(colorCombo, "colorCode");
        formLayoutLeft.addComponent(this.colorCombo);

        return verticalLayout;
    }

    private List<SimpleComboItem> filterShutterFinishByType() {
        List<SimpleComboItem> filteredShutterFinish = new ArrayList<>();

        String finishTypeCode = (String) finishTypeSelection.getValue();

        for (SimpleComboItem shutterFinishComboItem : shutterFinishMasterList) {
            if (finishTypeCode.equals(shutterFinishComboItem.getType())) {
                filteredShutterFinish.add(shutterFinishComboItem);
            }
        }
        return filteredShutterFinish;
    }

    private List<Color> filterColorsByType() {
        String finishTypeCode = (String) finishTypeSelection.getValue();
        return ConfigHolder.getInstance().getFinishTypeColors().get(finishTypeCode).getColors();
    }

    private void finishTypeChanged(Property.ValueChangeEvent valueChangeEvent) {
        List<SimpleComboItem> filteredShutterFinish = filterShutterFinishByType();
        this.shutterFinishSelection.getContainerDataSource().removeAllItems();
        ((BeanContainer<String, SimpleComboItem>) this.shutterFinishSelection.getContainerDataSource()).addAll(filteredShutterFinish);
        if (filteredShutterFinish.size() > 0) shutterFinishSelection.setValue(shutterFinishSelection.getItemIds().iterator().next());

        List<Color> filteredColors = filterColorsByType();
        String previousColorCode = (String) this.colorCombo.getValue();
        colorCombo.getContainerDataSource().removeAllItems();
        ((BeanContainer<String, Color>)colorCombo.getContainerDataSource()).addAll(filteredColors);

        if (filteredColors.stream().anyMatch(color -> color.getCode().equals(previousColorCode))) {
            colorCombo.setValue(previousColorCode);
        } else {
            colorCombo.setValue(colorCombo.getItemIds().iterator().next());
        }

    }

    private void enableApply() {
        applyButton.setEnabled(true);
    }

    private void disableApply() {
        applyButton.setEnabled(false);
    }

    private PropertyValueGenerator<String> getActionTextGenerator() {
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

    private Component buildFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.setSizeFull();
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Unit.PERCENTAGE);

        cancelBtn = new Button("Cancel");
        cancelBtn.addClickListener((ClickListener) clickEvent -> close());
        cancelBtn.focus();
        footer.addComponent(cancelBtn);
        footer.setSpacing(true);

        applyButton = new Button("Apply");
        applyButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        applyButton.addClickListener(event -> {

            try {
                binder.commit();
            } catch (FieldGroup.CommitException e) {
                e.printStackTrace();
                NotificationUtil.showNotification("Problem while applying changes. Please contact GAME Admin", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            }

            if (!product.getMakeTypeCode().equals(module.getMakeTypeCode())) {
                module.setMakeTypeText((String) makeType.getItem(module.getMakeTypeCode()).getItemProperty("title").getValue());
            }

            if (!product.getCarcassMaterialCode().equals(module.getCarcassMaterialCode())) {
                module.setCarcassMaterialText((String) carcassMaterialSelection.getItem(module.getCarcassMaterialCode()).getItemProperty("title").getValue());
            }

            if (!product.getFinishTypeCode().equals(module.getFinishTypeCode())) {
                module.setFinishTypeText((String) finishTypeSelection.getItem(module.getFinishTypeCode()).getItemProperty("title").getValue());
            }

            if (!product.getShutterFinishCode().equals(module.getShutterFinishCode())) {
                module.setShutterFinishText((String) shutterFinishSelection.getItem(module.getShutterFinishCode()).getItemProperty("title").getValue());
            }

            mgModuleCombo.removeValueChangeListener(this::mgModuleChanged);
            finishTypeSelection.removeValueChangeListener(this::finishTypeChanged);

            DashboardEventBus.post(new ProposalEvent.ModuleUpdated(module));
            close();
        });
        applyButton.focus();
        applyButton.setVisible(true);

        if (mgModuleCombo.getValue() == null) {
            disableApply();
        }

        footer.addComponent(applyButton);
        footer.setComponentAlignment(cancelBtn, Alignment.TOP_RIGHT);

        return footer;
    }

    public static void open(Module module, Product product) {
        //DashboardEventBus.post(new DashboardEvent.CloseOpenWindowsEvent());
        Window w = new ModuleDetailsWindow(module, product);
        UI.getCurrent().addWindow(w);
        w.focus();
    }

    private ComboBox getSimpleItemFilledCombo(String caption, List<SimpleComboItem> list, Property.ValueChangeListener listener) {

        final BeanContainer<String, SimpleComboItem> container =
                new BeanContainer<>(SimpleComboItem.class);
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

    private ComboBox getColorsCombo(String caption, List<Color> list) {

        final BeanContainer<String, Color> container =
                new BeanContainer<>(Color.class);
        container.setBeanIdProperty("code");
        container.addAll(list);

        ComboBox select = new ComboBox(caption);
        select.setNullSelectionAllowed(false);
        select.setWidth("250px");
        select.setContainerDataSource(container);
        select.setItemIconPropertyId("colorImageResource");
        select.setStyleName("colors-combo");
        select.setItemCaptionPropertyId("name");
        if (container.size() > 0) select.setValue(select.getItemIds().iterator().next());
        return select;
    }

    private ComboBox getSimpleItemFilledCombo(String caption, String dataType, Property.ValueChangeListener listener) {
        List<SimpleComboItem> list = proposalDataProvider.getComboItems(dataType);
        return getSimpleItemFilledCombo(caption, list, listener);
    }

    private ComboBox getMGModuleCombo(String caption) {
        final BeanContainer<String, MGModule> container =
                new BeanContainer<>(MGModule.class);
        container.setBeanIdProperty("code");
        container.addAll(mgModules);
        ComboBox select = new ComboBox(caption);
        select.setNullSelectionAllowed(true);
        select.setWidth("250px");
        select.setContainerDataSource(container);
        select.setItemCaptionPropertyId("code");
        return select;
    }
}