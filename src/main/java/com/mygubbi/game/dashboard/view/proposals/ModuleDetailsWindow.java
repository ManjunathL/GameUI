package com.mygubbi.game.dashboard.view.proposals;


import com.mygubbi.game.dashboard.ServerManager;
import com.mygubbi.game.dashboard.config.ConfigHolder;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.domain.*;
import com.mygubbi.game.dashboard.domain.JsonPojo.LookupItem;
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
    private final String DEF_CODE_PREFIX = "def_";

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

    private ProposalDataProvider proposalDataProvider = ServerManager.getInstance().getProposalDataProvider();
    private final BeanFieldGroup<Module> binder = new BeanFieldGroup<>(Module.class);

    private List<LookupItem> shutterFinishMasterList;
    private Image moduleImage;
    private ThemeResource emptyModuleImage;
    private ImageStrip accessoryImageStrip;
    private String basePath = ConfigHolder.getInstance().getImageBasePath();
    private TextField totalAmount;
    private boolean readOnly;

    private ModuleDetailsWindow(Module module, Product product, boolean readOnly) {

        this.product = product;
        this.module = module;
        this.readOnly = readOnly;
        initModule();
        this.mgModules = proposalDataProvider.getMGModules(module.getExtCode(), module.getExtDefCode());
        this.binder.setItemDataSource(this.module);

        setModal(true);
        removeCloseShortcut(ShortcutAction.KeyCode.ESCAPE);
        setWidth("60%");
        setClosable(false);
        setCaption("Edit Module Configuration");

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        VerticalLayout verticalLayoutLeft = new VerticalLayout();
        verticalLayoutLeft.setWidth("70%");
        horizontalLayout.addComponent(verticalLayoutLeft);
        verticalLayoutLeft.setMargin(new MarginInfo(true, true, true, true));
        Responsive.makeResponsive(this);

        HorizontalLayout horizontalLayout0 = new HorizontalLayout();
        horizontalLayout0.setSizeFull();
        horizontalLayout0.addComponent(buildImportedModuleForm());
        verticalLayoutLeft.addComponent(horizontalLayout0);
        horizontalLayout0.setHeightUndefined();

        HorizontalLayout horizontalLayout1 = new HorizontalLayout();
        horizontalLayout1.setSizeFull();
        horizontalLayout1.addComponent(buildMGModuleComponent());
        verticalLayoutLeft.addComponent(horizontalLayout1);
        horizontalLayout1.setHeightUndefined();

        HorizontalLayout horizontalLayout2 = new HorizontalLayout();
        horizontalLayout2.setMargin(new MarginInfo(false, false, false, false));
        horizontalLayout2.setSizeFull();
        horizontalLayout2.addComponent(buildModuleSelectionsComponent());
        verticalLayoutLeft.addComponent(horizontalLayout2);
        horizontalLayout2.setHeightUndefined();

        Component footerLayOut = buildFooter();
        verticalLayoutLeft.addComponent(footerLayOut);

        VerticalLayout verticalLayoutRight = buildImagesComponent();
        verticalLayoutRight.setWidth("30%");
        horizontalLayout.addComponent(verticalLayoutRight);

        setContent(horizontalLayout);

        updateValues();
        handleState();
    }

    private void handleState() {
        if (readOnly) {
            mgModuleCombo.setReadOnly(true);
            makeType.setReadOnly(true);
            carcassMaterialSelection.setReadOnly(true);
            colorCombo.setReadOnly(true);
            finishTypeSelection.setReadOnly(true);
            shutterFinishSelection.setReadOnly(true);
            applyButton.setEnabled(false);
        }
    }

    private VerticalLayout buildImagesComponent() {

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();

/*
        Label accessoriesLabel = new Label("Accessories");
        accessoriesLabel.setStyleName(ValoTheme.LABEL_SMALL);
        accessoriesLabel.setHeight("22px");
        verticalLayout.addComponent(accessoriesLabel);
*/

        createAccessoryImageStrip();
        verticalLayout.addComponent(accessoryImageStrip);

        return verticalLayout;
    }

    private void createAccessoryImageStrip() {
        accessoryImageStrip = new ImageStrip(ImageStrip.Alignment.VERTICAL);
        accessoryImageStrip.setAnimated(true);
        accessoryImageStrip.setImageBoxWidth(120);
        accessoryImageStrip.setImageBoxHeight(120);
        accessoryImageStrip.setImageMaxWidth(105);
        accessoryImageStrip.setImageMaxHeight(105);
        accessoryImageStrip.setMaxAllowed(3);
        accessoryImageStrip.setHeight("520px");
    }

    private void updateValues() {
        if (!StringUtils.isEmpty(module.getMgCode())) {
            mgModuleCombo.setValue(module.getMgCode());
            mgModuleChanged(null);
        }

        if (module.getMakeType().equals(Module.DEFAULT)) {
            makeType.setValue(DEF_CODE_PREFIX + module.getMakeTypeCode());
        } else {
            makeType.setValue(module.getMakeTypeCode());
        }

        if (StringUtils.isNotEmpty(module.getFixedCarcassCode())) {
            carcassMaterialSelection.setValue(module.getFixedCarcassCode());
            carcassMaterialSelection.setReadOnly(true);
        } else {
            if (module.getCarcass().equals(Module.DEFAULT)) {
                carcassMaterialSelection.setValue(DEF_CODE_PREFIX + module.getCarcassCode());
            } else {
                carcassMaterialSelection.setValue(module.getCarcassCode());
            }
        }
        if (module.getFinishType().equals(Module.DEFAULT)) {
            finishTypeSelection.setValue(DEF_CODE_PREFIX + module.getFinishTypeCode());
        } else {
            finishTypeSelection.setValue(module.getFinishTypeCode());
        }

        if (module.getFinish().equals(Module.DEFAULT)) {
            shutterFinishSelection.setValue(DEF_CODE_PREFIX + module.getFinishCode());
        } else {
            shutterFinishSelection.setValue(module.getFinishCode());
        }
    }

    private void initModule() {
        if (Module.DEFAULT.equals(module.getMakeType())) {
            module.setMakeTypeCode(product.getMakeTypeCode());
        }
        if (Module.DEFAULT.equals(module.getCarcass())) {
            module.setCarcassCodeBasedOnUnitType(product);
        }
        if (Module.DEFAULT.equals(module.getFinishType())) {
            module.setFinishTypeCode(product.getFinishTypeCode());
        }
        if (Module.DEFAULT.equals(module.getFinish())) {
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
        moduleImage.setCaption(null);
        moduleImage.setHeight("180px");
        moduleImage.setWidth("180px");
        horizontalLayout.addComponent(moduleImage);

        return verticalLayout;
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
            if (StringUtils.isNotEmpty(mgModule.getCarcassCode())) {
                carcassMaterialSelection.setValue(mgModule.getCarcassCode());
                carcassMaterialSelection.setReadOnly(true);
            } else {
                carcassMaterialSelection.setReadOnly(false);
            }

            VerticalLayout parent = (VerticalLayout) this.accessoryImageStrip.getParent();
            AbstractSingleComponentContainer.removeFromParent(this.accessoryImageStrip);
            createAccessoryImageStrip();
            parent.addComponent(accessoryImageStrip);

            mgModule.setAccessories(proposalDataProvider.getModuleAccessories(mgModule.getCode(), module.getMakeTypeCode()));

            for (ModuleAccessory moduleAccessory : mgModule.getAccessories()) {
                accessoryImageStrip.addImage(new FileResource(new File(basePath + moduleAccessory.getImagePath())));
            }

            refreshPrice();
            enableApply();
        }
    }

    private void refreshPrice() {

        if (StringUtils.isEmpty((String) mgModuleCombo.getValue())) {
            return;
        }

        Module moduleForPrice = new Module();
        moduleForPrice.setMgCode((String) mgModuleCombo.getValue());
        moduleForPrice.setCarcassCode(removeDefaultPrefix((String) carcassMaterialSelection.getValue()));
        moduleForPrice.setFinishCode(removeDefaultPrefix((String) shutterFinishSelection.getValue()));
        moduleForPrice.setMakeTypeCode(removeDefaultPrefix((String) makeType.getValue()));

        LOG.info("Asking for module price - " + moduleForPrice.toString());
        ModulePrice modulePrice = proposalDataProvider.getModulePrice(moduleForPrice);
        LOG.info("Got price - " + modulePrice.getTotalCost());

        totalAmount.setReadOnly(false);
        totalAmount.setValue(modulePrice.getTotalCost() + "");
        totalAmount.setReadOnly(true);
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

        this.makeType = getSimpleItemFilledCombo("Make Type", ProposalDataProvider.MAKE_LOOKUP, null, product.getMakeTypeCode());
        binder.bind(makeType, Module.MAKE_TYPE_CODE);
        makeType.addValueChangeListener(this::refreshPrice);
        formLayoutLeft.addComponent(this.makeType);

        this.carcassMaterialSelection = getSimpleItemFilledCombo("Carcass Material", ProposalDataProvider.CARCASS_LOOKUP, null, getCarcassCodeBasedOnType());
        binder.bind(carcassMaterialSelection, Module.CARCASS_MATERIAL_CODE);
        carcassMaterialSelection.addValueChangeListener(this::refreshPrice);
        formLayoutLeft.addComponent(this.carcassMaterialSelection);

        FormLayout formLayoutRight = new FormLayout();
        formLayoutRight.setSizeFull();
        formLayoutRight.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        horizontalLayout.addComponent(formLayoutRight);

        this.finishTypeSelection = getSimpleItemFilledCombo("Finish Material", ProposalDataProvider.FINISH_TYPE_LOOKUP, null, product.getFinishTypeCode());
        binder.bind(finishTypeSelection, Module.FINISH_TYPE_CODE);
        formLayoutRight.addComponent(this.finishTypeSelection);
        this.finishTypeSelection.addValueChangeListener(this::finishTypeChanged);

        shutterFinishMasterList = proposalDataProvider.getLookupItems(ProposalDataProvider.FINISH_LOOKUP);
        List<LookupItem> filteredShutterFinish = filterShutterFinishByType();
        this.shutterFinishSelection = getSimpleItemFilledCombo("Finish", filteredShutterFinish, null);
        binder.bind(shutterFinishSelection, Module.SHUTTER_FINISH_CODE);
        shutterFinishSelection.addValueChangeListener(this::refreshPrice);
        formLayoutRight.addComponent(this.shutterFinishSelection);

        totalAmount = new TextField("Total Amount");
        binder.bind(totalAmount, Module.AMOUNT);
        totalAmount.setReadOnly(true);
        formLayoutRight.addComponent(totalAmount);

        HorizontalLayout colorLayout = new HorizontalLayout();
        colorLayout.setSizeFull();
        verticalLayout.addComponent(colorLayout);

        this.colorCombo = getColorsCombo("Colors", ServerManager.getInstance().getFinishTypeColors().get(module.getFinishTypeCode()).getColors());
        binder.bind(colorCombo, Module.COLOR_CODE);
        formLayoutLeft.addComponent(this.colorCombo);

        return verticalLayout;
    }

    private void refreshPrice(Property.ValueChangeEvent valueChangeEvent) {
        refreshPrice();
    }

    private List<LookupItem> filterShutterFinishByType() {
        List<LookupItem> filteredShutterFinish = new ArrayList<>();

        String finishTypeCode = (String) finishTypeSelection.getValue();

        if (finishTypeCode.startsWith(DEF_CODE_PREFIX)) finishTypeCode = finishTypeCode.substring(DEF_CODE_PREFIX.length());

        for (LookupItem shutterFinishComboItem : shutterFinishMasterList) {
            if (finishTypeCode.equals(shutterFinishComboItem.getAdditionalType())) {
                filteredShutterFinish.add(shutterFinishComboItem);
            }
        }

        if (finishTypeCode.equals(product.getFinishTypeCode())) {
            LookupItem defaultItem = filteredShutterFinish.stream().filter(simpleComboItem -> simpleComboItem.getCode().equals(product.getFinishCode())).findFirst().get();
            filteredShutterFinish.add(new LookupItem(DEF_CODE_PREFIX + defaultItem.getCode(), "Default (" + defaultItem.getTitle() + ")", defaultItem.getAdditionalType()));
        }

        return filteredShutterFinish;
    }

    private List<Color> filterColorsByType() {
        String finishTypeCode = (String) finishTypeSelection.getValue();
        if (finishTypeCode.startsWith(DEF_CODE_PREFIX)) finishTypeCode = finishTypeCode.substring(DEF_CODE_PREFIX.length());
        return ServerManager.getInstance().getFinishTypeColors().get(finishTypeCode).getColors();
    }

    private void finishTypeChanged(Property.ValueChangeEvent valueChangeEvent) {
        List<LookupItem> filteredShutterFinish = filterShutterFinishByType();
        this.shutterFinishSelection.getContainerDataSource().removeAllItems();
        ((BeanContainer<String, LookupItem>) this.shutterFinishSelection.getContainerDataSource()).addAll(filteredShutterFinish);
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

            module.setMakeTypeCode(removeDefaultPrefix(module.getMakeTypeCode()));
            module.setCarcassCode(removeDefaultPrefix(module.getCarcassCode()));
            module.setFinishTypeCode(removeDefaultPrefix(module.getFinishTypeCode()));
            module.setFinishCode(removeDefaultPrefix(module.getFinishCode()));
            if (carcassMaterialSelection.isReadOnly())
            {
                module.setFixedCarcassCode((String) carcassMaterialSelection.getValue());
            }

            if (!product.getMakeTypeCode().equals(module.getMakeTypeCode()) || !((String) makeType.getValue()).contains(DEF_CODE_PREFIX)) {
                String title = (String) makeType.getItem(module.getMakeTypeCode()).getItemProperty("title").getValue();
                module.setMakeType(title);
            } else {
                module.setMakeType(Module.DEFAULT);
            }
            String carcassCodeBasedOnType = getCarcassCodeBasedOnType();
            if (!carcassCodeBasedOnType.equals(module.getCarcassCode()) || !((String) carcassMaterialSelection.getValue()).contains(DEF_CODE_PREFIX)) {
                String title = (String) carcassMaterialSelection.getItem(module.getCarcassCode()).getItemProperty("title").getValue();
                module.setCarcass(title);
            } else {
                module.setCarcass(Module.DEFAULT);
            }

            if (!product.getFinishTypeCode().equals(module.getFinishTypeCode()) || !((String) finishTypeSelection.getValue()).contains(DEF_CODE_PREFIX)) {
                String title = (String) finishTypeSelection.getItem(module.getFinishTypeCode()).getItemProperty("title").getValue();
                module.setFinishType(title);
            } else {
                module.setFinishType(Module.DEFAULT);
            }

            if (!product.getFinishCode().equals(module.getFinishCode()) || !((String) shutterFinishSelection.getValue()).contains(DEF_CODE_PREFIX)) {
                String title = (String) shutterFinishSelection.getItem(module.getFinishCode()).getItemProperty("title").getValue();
                module.setFinish(title);
            } else {
                module.setFinish(Module.DEFAULT);
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

    private String removeDefaultPrefix(String code) {
        if (code.indexOf(DEF_CODE_PREFIX) != -1) {
            return code.substring(DEF_CODE_PREFIX.length());
        } else {
            return code;
        }
    }

    private String getCarcassCodeBasedOnType() {
        return module.getUnitType().toLowerCase().contains(Module.UnitTypes.wall.name()) ? product.getWallCarcassCode() : product.getBaseCarcassCode();
    }

    private String removeDefault(String title) {
        int defaultIndex = title.indexOf(" (default)");
        return defaultIndex == -1 ? title : title.substring(0, defaultIndex);
    }

    public static void open(Module module, Product product, boolean readOnly) {
        Window w = new ModuleDetailsWindow(module, product, readOnly);
        UI.getCurrent().addWindow(w);
        w.focus();
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
        List<LookupItem> list = new ArrayList<>(proposalDataProvider.getLookupItems(dataType));

        if (StringUtils.isNotEmpty(defaultCode)) {

            String title = null;
            String additionalType = null;
            for (LookupItem item : list) {
                if (item.getCode().equals(defaultCode)) {
                    title = item.getTitle();
                    additionalType = item.getAdditionalType();
                }
            }

            list.add(new LookupItem(DEF_CODE_PREFIX + defaultCode, "Default (" + title + ")", additionalType));
        }
        return getSimpleItemFilledCombo(caption, list, listener);
    }

    private ComboBox getMGModuleCombo(String caption) {
        final BeanContainer<String, MGModule> container =
                new BeanContainer<>(MGModule.class);
        container.setBeanIdProperty(MGModule.CODE);
        container.addAll(mgModules);
        ComboBox select = new ComboBox(caption);
        select.setNullSelectionAllowed(true);
        select.setWidth("250px");
        select.setContainerDataSource(container);
        select.setItemCaptionPropertyId(MGModule.CODE);
        return select;
    }
}