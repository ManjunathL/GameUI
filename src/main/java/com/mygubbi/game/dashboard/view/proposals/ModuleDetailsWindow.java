package com.mygubbi.game.dashboard.view.proposals;


import com.mygubbi.game.dashboard.config.ConfigHolder;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.data.dummy.FileDataProviderUtil;
import com.mygubbi.game.dashboard.domain.*;
import com.mygubbi.game.dashboard.domain.JsonPojo.SimpleComboItem;
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
    private final List<Module> mgModules;
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

    private ProposalDataProvider proposalDataProvider = ConfigHolder.getInstance().getProposalDataProvider();
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
        this.mgModules = proposalDataProvider.getMGModules(module.getExtCode(), module.getExtDefCode());
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

        Component footerLayOut = buildFooter();
        vLayout.addComponent(footerLayOut);

        updateValues();
    }

    private void updateValues() {
        if (!StringUtils.isEmpty(module.getMgCode())) {
            mgModuleCombo.setValue(module.getMgCode());
            mgModuleChanged(null);
        }

        makeType.setValue(module.getMakeTypeCode());
        carcassMaterialSelection.setValue(module.getCarcassCode());
        finishTypeSelection.setValue(module.getFinishTypeCode());
        shutterFinishSelection.setValue(module.getFinishCode());
    }

    private void initModule() {
        if (Module.DEFAULT.equals(module.getMakeTypeText())) {
            module.setMakeTypeCode(product.getMakeTypeCode());
        }
        if (Module.DEFAULT.equals(module.getCarcassText())) {
            module.setCarcassCode(product.getBaseCarcassCode());
        }
        if (Module.DEFAULT.equals(module.getFinishTypeText())) {
            module.setFinishTypeCode(product.getFinishTypeCode());
        }
        if (Module.DEFAULT.equals(module.getFinishText())) {
            module.setFinishCode(product.getFinishCode());
        }
    }

    private FormLayout buildImportedModuleForm() {
        FormLayout formLayoutLeft = new FormLayout();
        formLayoutLeft.setSizeFull();
        formLayoutLeft.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        this.importedModule = (TextField) binder.buildAndBind("Imported Module", Module.IMPORTED_MODULE_TEXT);
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
        binder.bind(this.mgModuleCombo, Module.MG_MODULE_CODE);
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
        Module mgModule = ((BeanItem<Module>)this.mgModuleCombo.getItem(this.mgModuleCombo.getValue())).getBean();
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
            this.description.setValue(mgModule.getMgDescription());
            this.description.setReadOnly(true);
            this.dimensions.setReadOnly(false);
            this.dimensions.setValue(mgModule.getMgDimension());
            this.dimensions.setReadOnly(true);
            this.moduleImage.setSource(new FileResource(new File(basePath + mgModule.getMgImage())));
            mgModuleCombo.setNullSelectionAllowed(false);

            HorizontalLayout parent = (HorizontalLayout) this.accessoryImageStrip.getParent();
            AbstractSingleComponentContainer.removeFromParent(this.accessoryImageStrip);
            createAccessoryImageStrip();
            parent.addComponent(accessoryImageStrip);

            mgModule.setMgAccessories(proposalDataProvider.getModuleAccessories(mgModule.getMgCode()));

            for (ModuleAccessory moduleAccessory : mgModule.getMgAccessories()) {
                accessoryImageStrip.addImage(new FileResource(new File(basePath + moduleAccessory.getImagePath())));
            }

            ModulePrice modulePrice = proposalDataProvider.getModulePrice(module);
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

        this.makeType = getSimpleItemFilledCombo("Make Type", "make_type_data", null, product.getMakeTypeCode());
        binder.bind(makeType, Module.MAKE_TYPE_CODE);
        formLayoutLeft.addComponent(this.makeType);

        this.carcassMaterialSelection = getSimpleItemFilledCombo("Carcass Material", "carcass_material_data", null, product.getBaseCarcassCode());
        binder.bind(carcassMaterialSelection, Module.CARCASS_MATERIAL_CODE);
        formLayoutLeft.addComponent(this.carcassMaterialSelection);

        FormLayout formLayoutRight = new FormLayout();
        formLayoutRight.setSizeFull();
        formLayoutRight.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        horizontalLayout.addComponent(formLayoutRight);

        this.finishTypeSelection = getSimpleItemFilledCombo("Finish Type", "finish_type_data", null, product.getFinishTypeCode());
        binder.bind(finishTypeSelection, Module.FINISH_TYPE_CODE);
        formLayoutRight.addComponent(this.finishTypeSelection);
        this.finishTypeSelection.addValueChangeListener(this::finishTypeChanged);

        shutterFinishMasterList = proposalDataProvider.getComboItems("shutter_material_data");
        List<SimpleComboItem> filteredShutterFinish = filterShutterFinishByType();
        SimpleComboItem defaultItem = filteredShutterFinish.stream().filter(simpleComboItem -> simpleComboItem.getCode().equals(product.getFinishCode())).findFirst().get();
        defaultItem.setTitle(defaultItem.getTitle() + " (default)");
        this.shutterFinishSelection = getSimpleItemFilledCombo("Finish", filteredShutterFinish, null);
        binder.bind(shutterFinishSelection, Module.SHUTTER_FINISH_CODE);
        formLayoutRight.addComponent(this.shutterFinishSelection);

        totalAmount = new TextField("Total Amount");
        binder.bind(totalAmount, Module.AMOUNT);
        totalAmount.setReadOnly(true);
        formLayoutRight.addComponent(totalAmount);

        HorizontalLayout colorLayout = new HorizontalLayout();
        colorLayout.setSizeFull();
        verticalLayout.addComponent(colorLayout);

        this.colorCombo = getColorsCombo("Colors", ConfigHolder.getInstance().getFinishTypeColors().get(module.getFinishTypeCode()).getColors());
        binder.bind(colorCombo, Module.COLOR_CODE);
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
        cancelBtn.addClickListener((ClickListener) clickEvent -> {
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
            } catch (FieldGroup.CommitException e) {
                e.printStackTrace();
                NotificationUtil.showNotification("Problem while applying changes. Please contact GAME Admin", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            }

            if (!product.getMakeTypeCode().equals(module.getMakeTypeCode()) || !module.getMakeTypeText().equals(Module.DEFAULT)) {
                String title = (String) makeType.getItem(module.getMakeTypeCode()).getItemProperty("title").getValue();
                title = removeDefault(title);
                module.setMakeTypeText(title);
            }

            if (!product.getBaseCarcassCode().equals(module.getCarcassCode()) || !module.getCarcassText().equals(Module.DEFAULT)) {
                String title = (String) carcassMaterialSelection.getItem(module.getCarcassCode()).getItemProperty("title").getValue();
                title = removeDefault(title);
                module.setCarcassText(title);
            }

            if (!product.getFinishTypeCode().equals(module.getFinishTypeCode()) || !module.getFinishTypeText().equals(Module.DEFAULT)) {
                String title = (String) finishTypeSelection.getItem(module.getFinishTypeCode()).getItemProperty("title").getValue();
                title = removeDefault(title);
                module.setFinishTypeText(title);
            }

            if (!product.getFinishCode().equals(module.getFinishCode()) || !module.getFinishText().equals(Module.DEFAULT)) {
                String title = (String) shutterFinishSelection.getItem(module.getFinishCode()).getItemProperty("title").getValue();
                title = removeDefault(title);
                module.setFinishText(title);
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

    private String removeDefault(String title) {
        int defaultIndex = title.indexOf(" (default)");
        return defaultIndex == -1 ? title : title.substring(0, defaultIndex);
    }

    public static void open(Module module, Product product) {
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

    private ComboBox getSimpleItemFilledCombo(String caption, String dataType, Property.ValueChangeListener listener, String defaultCode) {
        List<SimpleComboItem> list = proposalDataProvider.getComboItems(dataType);

        if (StringUtils.isNotEmpty(defaultCode)) {
            for (SimpleComboItem item : list) {
                if (item.getCode().equals(defaultCode)) {
                    item.setTitle(item.getTitle() + " (default)");
                }
            }
        }
        return getSimpleItemFilledCombo(caption, list, listener);
    }

    private ComboBox getMGModuleCombo(String caption) {
        final BeanContainer<String, Module> container =
                new BeanContainer<>(Module.class);
        container.setBeanIdProperty(Module.MG_MODULE_CODE);
        container.addAll(mgModules);
        ComboBox select = new ComboBox(caption);
        select.setNullSelectionAllowed(true);
        select.setWidth("250px");
        select.setContainerDataSource(container);
        select.setItemCaptionPropertyId(Module.MG_MODULE_CODE);
        return select;
    }
}