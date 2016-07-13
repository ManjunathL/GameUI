
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
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.dd.VerticalDropLocation;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vaadin.cssinject.CSSInject;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class ModuleDetailsWindow extends Window {

    private static final Logger LOG = LogManager.getLogger(ModuleDetailsWindow.class);
    private final List<MGModule> mgModules;
    private final Product product;
    private final String DEF_CODE_PREFIX = "def_";
    private static final String LABEL_WARNING = "warning";
    private final int moduleIndex;

    private TextField importedModule;
    private TextField description;
    private TextField dimensions;
    private ComboBox mgModuleCombo;
    private ComboBox carcassMaterialSelection;
    private ComboBox colorCombo;
    private ComboBox exposedSidesCombo;
    private ComboBox exposedBottomCombo;

    private ComboBox accessoryPack1;
    private ComboBox accessoryPack2;
    private ComboBox accessoryPack3;

    private ComboBox addons1;
    private ComboBox addons2;
    private ComboBox addons3;


    private ComboBox shutterFinishSelection;
    private ComboBox finishTypeSelection;

    private Button closeBtn;
    private Button applyButton;

    private Module module;

    private ProposalDataProvider proposalDataProvider = ServerManager.getInstance().getProposalDataProvider();
    private final BeanFieldGroup<Module> binder = new BeanFieldGroup<>(Module.class);

    private List<Finish> shutterFinishMasterList;
    private Image moduleImage;
    private ThemeResource emptyModuleImage;
    private String basePath = ConfigHolder.getInstance().getImageBasePath();
    private TextField totalAmount;
    private boolean readOnly;
    private final Label defaultsOverridden;
    private Button applyNextButton;
    private VerticalLayout accessoryImageLayout;

    private ModuleDetailsWindow(Module module, Product product, boolean readOnly, int moduleIndex) {

        this.product = product;
        this.module = module;
        this.readOnly = readOnly;
        this.moduleIndex = moduleIndex;
        initModule();
        this.mgModules = proposalDataProvider.getMGModules(module.getExtCode(), module.getExtDefCode());
        this.binder.setItemDataSource(this.module);

        setModal(true);
        removeCloseShortcut(ShortcutAction.KeyCode.ESCAPE);
        setWidth("75%");
        setHeight("85%");
        setClosable(false);
        setCaption("Edit Module Configuration for " + product.getTitle());

        VerticalLayout verticalLayout=new VerticalLayout();
        verticalLayout.setSizeFull();

        VerticalLayout verticalLayout1=new VerticalLayout();
        verticalLayout.setSizeFull();

        VerticalLayout verticalLayout2=new VerticalLayout();
        verticalLayout.setSizeFull();

        HorizontalLayout horizontalLayoutupper=new HorizontalLayout();
        verticalLayout1.addComponent(horizontalLayoutupper);
        verticalLayout1.setSpacing(false);
        horizontalLayoutupper.setMargin(new MarginInfo(false, false, false, false));
        Responsive.makeResponsive(this);
        horizontalLayoutupper.setSizeFull();

        HorizontalLayout horizontalLayout1=new HorizontalLayout();
        horizontalLayout1.setSizeFull();

        Component componentUpper1=buildModuleComponent();
        horizontalLayout1.addComponent(componentUpper1);
        horizontalLayout1.setSpacing(true);

        Component componentUpper2=buildModuleOptionsComponent();
        horizontalLayout1.addComponent(componentUpper2);
        horizontalLayout1.setSpacing(true);

        horizontalLayoutupper.addComponent(horizontalLayout1);
        horizontalLayoutupper.setExpandRatio(horizontalLayout1,0.8f);

        Component componentUpper3=buildModuleImageComponent();
        horizontalLayoutupper.addComponent(componentUpper3);
        horizontalLayoutupper.setExpandRatio(componentUpper3,0.2f);

        defaultsOverridden = new Label("Note that the defaults have been overridden.");
        defaultsOverridden.setStyleName(LABEL_WARNING);
        defaultsOverridden.setVisible(false);

        verticalLayout.addComponent(defaultsOverridden);
        verticalLayout.setComponentAlignment(defaultsOverridden, Alignment.MIDDLE_CENTER);

        HorizontalLayout horizontalLayoutlower=new HorizontalLayout();
        verticalLayout1.addComponent(horizontalLayoutlower);
        horizontalLayoutlower.setSizeFull();
        horizontalLayoutlower.setMargin(new MarginInfo(false, false, true, false));
        Responsive.makeResponsive(this);


        HorizontalLayout horizontalLayout2=new HorizontalLayout();
        horizontalLayout2.setSizeUndefined();
        horizontalLayout2.setHeight("400px");

        Component componentLower1=buildAccPack1Component();
        horizontalLayout2.addComponent(componentLower1);
        horizontalLayout2.setSpacing(true);

        Component componentLower2=buildAccPack2Component();
        horizontalLayout2.addComponent(componentLower2);
        horizontalLayout2.setSpacing(true);

        Component componentLower3=buildAccPack3Component();
        horizontalLayout2.addComponent(componentLower3);

        horizontalLayoutlower.addComponent(horizontalLayout2);
        horizontalLayoutlower.setExpandRatio(horizontalLayout2,0.75f);

        Component componentLower4=buildAccessoryImagesComponent();
        horizontalLayoutlower.addComponent(componentLower4);
        horizontalLayoutlower.setExpandRatio(componentLower4,0.25f);

        verticalLayout.addComponent(verticalLayout1);
        verticalLayout.setExpandRatio(verticalLayout1,0.9f);

        Component footerLayOut = buildFooter();
        verticalLayout2.addComponent(footerLayOut);

        verticalLayout.addComponent(verticalLayout2);
        verticalLayout.setExpandRatio(verticalLayout2,0.1f);

        setContent(verticalLayout);

        updateValues();
        handleState();
    }


    private void handleState() {
        if (readOnly) {
            mgModuleCombo.setReadOnly(true);
            carcassMaterialSelection.setReadOnly(true);
            colorCombo.setReadOnly(true);
            finishTypeSelection.setReadOnly(true);
            shutterFinishSelection.setReadOnly(true);
            disableApply();
        }
    }



    private void updateValues() {
        if (!StringUtils.isEmpty(module.getMgCode())) {
            mgModuleCombo.setValue(module.getMgCode());
            mgModuleChanged(null);
        }

        if (StringUtils.isNotEmpty(module.getFixedCarcassCode())) {
            carcassMaterialSelection.setValue(module.getFixedCarcassCode());
            carcassMaterialSelection.setReadOnly(true);
        } else {
            if (module.getCarcass().contains(Module.DEFAULT)) {
                carcassMaterialSelection.setValue(DEF_CODE_PREFIX + module.getCarcassCode());
            } else {
                carcassMaterialSelection.setValue(module.getCarcassCode());
                defaultsOverridden.setVisible(true);
            }
        }
        if (module.getFinishType().contains(Module.DEFAULT)) {
            finishTypeSelection.setValue(DEF_CODE_PREFIX + module.getFinishTypeCode());
        } else {
            finishTypeSelection.setValue(module.getFinishTypeCode());
            defaultsOverridden.setVisible(true);
        }

        if (module.getFinish().contains(Module.DEFAULT)) {
            shutterFinishSelection.setValue(DEF_CODE_PREFIX + module.getFinishCode());
        } else {
            shutterFinishSelection.setValue(module.getFinishCode());
            defaultsOverridden.setVisible(true);
        }

        if (StringUtils.isEmpty((String) mgModuleCombo.getValue()) && mgModules.size() == 1) {
            mgModuleCombo.setValue(mgModuleCombo.getItemIds().iterator().next());
        }
        checkDefaultsOverridden();
    }

    private void initModule() {
        if (module.getCarcass().contains(Module.DEFAULT)) {
            module.setCarcassCodeBasedOnUnitType(product);
        }
        if (module.getFinishType().contains(Module.DEFAULT)) {
            module.setFinishTypeCode(product.getFinishTypeCode());
        }
        if (module.getFinish().contains(Module.DEFAULT)) {
            module.setFinishCode(product.getFinishCode());
        }
    }

    private Component buildModuleImageComponent() {
        Panel panel = new Panel();
        panel.setWidth("200px");
        panel.setHeight("200px");
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        verticalLayout.setMargin(new MarginInfo(false,true,true,true));
        Label accessoriesLabel = new Label("Module Image");
        verticalLayout.addComponent(accessoriesLabel);
        verticalLayout.setComponentAlignment(accessoriesLabel, Alignment.TOP_CENTER);
        verticalLayout.setSpacing(false);
        emptyModuleImage = new ThemeResource("img/empty-poster.png");
        moduleImage = new Image("", emptyModuleImage);
        moduleImage.setCaption(null);
        moduleImage.setSizeFull();
        verticalLayout.addComponent(moduleImage);
        panel.setContent(verticalLayout);
        return panel;
    }

    private Component buildModuleOptionsComponent() {

        FormLayout formLayout = new FormLayout();
        formLayout.setSizeFull();
        formLayout.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        this.carcassMaterialSelection = getSimpleItemFilledCombo("Carcass Material", ProposalDataProvider.CARCASS_LOOKUP, null, getCarcassCodeBasedOnType());
        binder.bind(carcassMaterialSelection, Module.CARCASS_MATERIAL_CODE);
        carcassMaterialSelection.addValueChangeListener(this::refreshPrice);
        formLayout.addComponent(this.carcassMaterialSelection);

        this.finishTypeSelection = getSimpleItemFilledCombo("Finish Material", ProposalDataProvider.FINISH_TYPE_LOOKUP, null, product.getFinishTypeCode());
        binder.bind(finishTypeSelection, Module.FINISH_TYPE_CODE);
        formLayout.addComponent(this.finishTypeSelection);
        this.finishTypeSelection.addValueChangeListener(this::finishTypeChanged);

        shutterFinishMasterList = proposalDataProvider.getFinishes(); //LookupItems(ProposalDataProvider.FINISH_LOOKUP);
        List<Finish> filteredShutterFinish = filterShutterFinishByType();
        this.shutterFinishSelection = getFinishItemFilledCombo("Finish", filteredShutterFinish, null);
        binder.bind(shutterFinishSelection, Module.SHUTTER_FINISH_CODE);
        shutterFinishSelection.addValueChangeListener(this::finishChanged);//refreshPrice);
        formLayout.addComponent(this.shutterFinishSelection);

        List<Color> colors = filterColorsByType();
        this.colorCombo = getColorsCombo("Colors", colors);
        binder.bind(colorCombo, Module.COLOR_CODE);
        formLayout.addComponent(this.colorCombo);

        totalAmount = new TextField("Total Amount");
        binder.bind(totalAmount, Module.AMOUNT);
        totalAmount.setReadOnly(true);
        formLayout.addComponent(totalAmount);

        return formLayout;
    }

    private Component buildModuleComponent() {
        FormLayout formLayout = new FormLayout();
        formLayout.setSizeFull();
        formLayout.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        this.importedModule = (TextField) binder.buildAndBind("Module", Module.IMPORTED_MODULE_TEXT);
        this.importedModule.setReadOnly(true);
        formLayout.addComponent(importedModule);

        this.mgModuleCombo = getMGModuleCombo("MG Module");
        binder.bind(this.mgModuleCombo, Module.MG_MODULE_CODE);
        this.mgModuleCombo.addValueChangeListener(this::mgModuleChanged);
        formLayout.addComponent(this.mgModuleCombo);

        this.description = new TextField("Description");
        this.description.setReadOnly(true);
        formLayout.addComponent(this.description);

        this.dimensions = new TextField("Dimensions");
        formLayout.addComponent(this.dimensions);

        this.exposedSidesCombo = getMGModuleCombo("Exposed Sides");
//        binder.bind(this.mgModuleCombo, Module.MG_MODULE_CODE);
//        this.mgModuleCombo.addValueChangeListener(this::mgModuleChanged);
        formLayout.addComponent(this.exposedSidesCombo);

        this.exposedBottomCombo = getMGModuleCombo("Exposed Bottom");
//        binder.bind(this.mgModuleCombo, Module.MG_MODULE_CODE);
//        this.mgModuleCombo.addValueChangeListener(this::mgModuleChanged);
        formLayout.addComponent(this.exposedBottomCombo);

        return formLayout;
    }

    private Component buildAccPack1Component() {
        FormLayout formLayout=new FormLayout();
        formLayout.setSizeFull();
        formLayout.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        this.accessoryPack1 = getMGModuleCombo("Accessory Pack 1");
        accessoryPack1.setWidth("150px");
        formLayout.addComponent(this.accessoryPack1);

        this.addons1 = getMGModuleCombo("Addons 1");
        addons1.setWidth("150px");
        formLayout.addComponent(this.addons1);
        this.addons2 = getMGModuleCombo("Addons 2");
        addons2.setWidth("150px");
        formLayout.addComponent(this.addons2);
        this.addons3 = getMGModuleCombo("Addons 3");
        addons3.setWidth("150px");
        formLayout.addComponent(this.addons3);

        return formLayout;
    }

    private Component buildAccPack2Component() {
        FormLayout formLayout=new FormLayout();
        formLayout.setSizeFull();

        formLayout.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        this.accessoryPack2 = getMGModuleCombo("Accessory Pack 2");
        accessoryPack2.setWidth("150px");
        formLayout.addComponent(this.accessoryPack2);

        this.addons1 = getMGModuleCombo("Addons 1");
        addons1.setWidth("150px");
        formLayout.addComponent(this.addons1);
        this.addons2 = getMGModuleCombo("Addons 2");
        addons2.setWidth("150px");
        formLayout.addComponent(this.addons2);
        this.addons3 = getMGModuleCombo("Addons 3");
        addons3.setWidth("150px");
        formLayout.addComponent(this.addons3);

        return formLayout;
    }

    private Component buildAccPack3Component() {
        FormLayout formLayout=new FormLayout();
        formLayout.setSizeFull();
        formLayout.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        this.accessoryPack3 = getMGModuleCombo("Accessory Pack 3");
        accessoryPack3.setWidth("150px");
        formLayout.addComponent(this.accessoryPack3);

        this.addons1 = getMGModuleCombo("Addons 1");
        addons1.setWidth("150px");
        formLayout.addComponent(this.addons1);
        this.addons2 = getMGModuleCombo("Addons 2");
        addons2.setWidth("150px");
        formLayout.addComponent(this.addons2);
        this.addons3 = getMGModuleCombo("Addons 3");
        addons3.setWidth("150px");
        formLayout.addComponent(this.addons3);

        return formLayout;
    }

    private Component buildAccessoryImagesComponent() {
        Panel panel = new Panel();
        panel.setHeight("300px");
        panel.setWidth("290px");
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        Label accessoriesLabel = new Label("Accessories");
        accessoriesLabel.setStyleName(ValoTheme.LABEL_SMALL);
        accessoriesLabel.setSizeUndefined();
        accessoriesLabel.setHeight("22px");
        verticalLayout.addComponent(accessoriesLabel);
        verticalLayout.setComponentAlignment(accessoriesLabel, Alignment.MIDDLE_CENTER);
        accessoryImageLayout = new VerticalLayout();
        accessoryImageLayout.setSizeFull();
        verticalLayout.addComponent(accessoryImageLayout);
        verticalLayout.setExpandRatio(accessoryImageLayout, 1.0f);
        panel.setContent(verticalLayout);
        return panel;
    }



    private void mgModuleChanged(Property.ValueChangeEvent valueChangeEvent) {
        MGModule mgModule = ((BeanItem<MGModule>) this.mgModuleCombo.getItem(this.mgModuleCombo.getValue())).getBean();
        if (mgModule == null) {
            this.description.setReadOnly(false);
            this.description.setValue("");
            this.description.setReadOnly(true);
            this.dimensions.setReadOnly(false);
            this.dimensions.setValue("");
            this.dimensions.setReadOnly(true);
            this.moduleImage.setSource(emptyModuleImage);
            emptyAccessoryImages();
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

            refreshAccessories(mgModule);
            refreshPrice();
            enableApply();
        }
    }

    private void refreshAccessories(MGModule mgModule) {
        mgModule.setAccessories(proposalDataProvider.getModuleAccessories(mgModule.getCode()));

        emptyAccessoryImages();

        for (ModuleAccessory moduleAccessory : mgModule.getAccessories()) {
            File sourceFile = new File(basePath + moduleAccessory.getImagePath());
            if (sourceFile.exists())
            {
                Image img = new Image("", new FileResource(sourceFile));
                img.setWidth("200px");
                accessoryImageLayout.addComponent(img);
                accessoryImageLayout.setComponentAlignment(img, Alignment.MIDDLE_CENTER);
                Label c = new Label(moduleAccessory.getTitle() + " (" + moduleAccessory.getMake() + ")");
                accessoryImageLayout.addComponent(c);
                accessoryImageLayout.setComponentAlignment(c, Alignment.MIDDLE_CENTER);
            }
        }
    }

    private void emptyAccessoryImages() {
        this.accessoryImageLayout.removeAllComponents();
    }

    private void refreshPrice() {

        if (StringUtils.isEmpty((String) mgModuleCombo.getValue())) {
            return;
        }

        Module moduleForPrice = new Module();
        moduleForPrice.setMgCode((String) mgModuleCombo.getValue());
        moduleForPrice.setCarcassCode(removeDefaultPrefix((String) carcassMaterialSelection.getValue()));
        moduleForPrice.setFinishCode(removeDefaultPrefix((String) shutterFinishSelection.getValue()));

        LOG.info("Asking for module price - " + moduleForPrice.toString());
        ModulePrice modulePrice = proposalDataProvider.getModulePrice(moduleForPrice);
        LOG.info("Got price - " + modulePrice.getTotalCost());

        totalAmount.setReadOnly(false);
        totalAmount.setValue(modulePrice.getTotalCost() + "");
        totalAmount.setReadOnly(true);

        checkDefaultsOverridden();

    }


    private void finishChanged(Property.ValueChangeEvent valueChangeEvent) {
        List<Color> filteredColors = filterColorsByType();
        String previousColorCode = (String) this.colorCombo.getValue();
        colorCombo.getContainerDataSource().removeAllItems();
        ((BeanContainer<String, Color>) colorCombo.getContainerDataSource()).addAll(filteredColors);

        if (filteredColors.stream().anyMatch(color -> color.getCode().equals(previousColorCode))) {
            colorCombo.setValue(previousColorCode);
        }

        refreshPrice();
    }

    private void refreshPrice(Property.ValueChangeEvent valueChangeEvent) {
        refreshPrice();
    }

    private void checkDefaultsOverridden() {
        if (!((String) carcassMaterialSelection.getValue()).startsWith(DEF_CODE_PREFIX)
                || !((String) finishTypeSelection.getValue()).startsWith(DEF_CODE_PREFIX)
                || !((String) shutterFinishSelection.getValue()).startsWith(DEF_CODE_PREFIX)) {
            defaultsOverridden.setVisible(true);
        } else {
            defaultsOverridden.setVisible(false);
        }
    }

    private List<Finish> filterShutterFinishByType() {
        List<Finish> filteredShutterFinish = new ArrayList<>();

        String finishTypeCode = (String) finishTypeSelection.getValue();

        if (finishTypeCode.startsWith(DEF_CODE_PREFIX))
            finishTypeCode = finishTypeCode.substring(DEF_CODE_PREFIX.length());

        for (Finish shutterFinishComboItem : shutterFinishMasterList) {
            if (finishTypeCode.equals(shutterFinishComboItem.getFinishMaterial())) {
                filteredShutterFinish.add(shutterFinishComboItem);
            }
        }

        if (finishTypeCode.equals(product.getFinishTypeCode())) {
            Finish defaultItem = filteredShutterFinish.stream().filter(finish -> finish.getFinishCode().equals(product.getFinishCode())).findFirst().get();
            Finish newFinish = new Finish();
            newFinish.setTitle(Module.DEFAULT + " (" + defaultItem.getTitle() + ")");
            newFinish.setFinishCode(DEF_CODE_PREFIX + defaultItem.getFinishCode());
            newFinish.setFinishMaterial(defaultItem.getFinishMaterial());
            newFinish.setColorGroupCode(defaultItem.getColorGroupCode());

            filteredShutterFinish.add(newFinish);
        }

        return filteredShutterFinish;
    }

    private List<Color> filterColorsByType() {
        Finish finish = ((BeanContainer<String, Finish>) shutterFinishSelection.getContainerDataSource()).getItem(shutterFinishSelection.getValue()).getBean();
        return proposalDataProvider.getColorsByGroup(finish.getColorGroupCode());
    }

    private void finishTypeChanged(Property.ValueChangeEvent valueChangeEvent) {
        List<Finish> filteredShutterFinish = filterShutterFinishByType();
        String prevValue = this.shutterFinishSelection.getValue().toString();
        this.shutterFinishSelection.getContainerDataSource().removeAllItems();
        ((BeanContainer<String, Finish>) this.shutterFinishSelection.getContainerDataSource()).addAll(filteredShutterFinish);
        if (filteredShutterFinish.size() > 0) {
            if (shutterFinishSelection.getItemIds().contains(prevValue)) {
                shutterFinishSelection.setValue(prevValue);
            } else {
                shutterFinishSelection.setValue(shutterFinishSelection.getItemIds().iterator().next());
            }
        }
        checkDefaultsOverridden();
    }

    private void enableApply() {
        applyButton.setEnabled(true);
        applyNextButton.setEnabled(true);
    }

    private void disableApply() {
        applyButton.setEnabled(false);
        applyNextButton.setEnabled(false);
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

        closeBtn = new Button("Close");
        closeBtn.setStyleName("module-close-btn");
        closeBtn.addClickListener((ClickListener) clickEvent -> {
            binder.discard();
            close();
        });
        closeBtn.focus();
        footer.addComponent(closeBtn);
        footer.setSpacing(true);

        applyButton = new Button("Apply");
        applyButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        applyButton.addClickListener(getApplyListener(false));
        applyButton.focus();
        applyButton.setVisible(true);

        applyNextButton = new Button("Apply & Load Next");
        applyNextButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        applyNextButton.addClickListener(getApplyListener(true));
        applyNextButton.focus();
        applyNextButton.setVisible(!isLastModule());

        if (mgModuleCombo.getValue() == null) {
            disableApply();
        }

        footer.addComponent(applyButton);
        footer.setSpacing(false);
        footer.addComponent(applyNextButton);
        footer.setComponentAlignment(closeBtn, Alignment.TOP_RIGHT);

        return footer;
    }

    private ClickListener getApplyListener(boolean loadNext) {
        return event -> {

            try {
                binder.commit();
            } catch (FieldGroup.CommitException e) {
                e.printStackTrace();
                NotificationUtil.showNotification("Problem while applying changes. Please contact GAME Admin", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            }

            module.setCarcassCode(removeDefaultPrefix(module.getCarcassCode()));
            module.setFinishTypeCode(removeDefaultPrefix(module.getFinishTypeCode()));
            module.setFinishCode(removeDefaultPrefix(module.getFinishCode()));
            if (carcassMaterialSelection.isReadOnly()) {
                module.setFixedCarcassCode((String) carcassMaterialSelection.getValue());
            }

            String carcassCodeBasedOnType = getCarcassCodeBasedOnType();
            String carcassTitle = (String) carcassMaterialSelection.getItem(module.getCarcassCode()).getItemProperty("title").getValue();
            if (!carcassCodeBasedOnType.equals(module.getCarcassCode()) || !((String) carcassMaterialSelection.getValue()).contains(DEF_CODE_PREFIX)) {
                module.setCarcass(carcassTitle);
            } else {
                module.setCarcass(getDefaultText(carcassTitle));
            }

            String finishTypeTitle = (String) finishTypeSelection.getItem(module.getFinishTypeCode()).getItemProperty("title").getValue();
            if (!product.getFinishTypeCode().equals(module.getFinishTypeCode()) || !((String) finishTypeSelection.getValue()).contains(DEF_CODE_PREFIX)) {
                module.setFinishType(finishTypeTitle);
            } else {
                module.setFinishType(getDefaultText(finishTypeTitle));
            }

            String finishTitle = (String) shutterFinishSelection.getItem(module.getFinishCode()).getItemProperty("title").getValue();
            if (!product.getFinishCode().equals(module.getFinishCode()) || !((String) shutterFinishSelection.getValue()).contains(DEF_CODE_PREFIX)) {
                module.setFinish(finishTitle);
            } else {
                module.setFinish(getDefaultText(finishTitle));
            }

            mgModuleCombo.removeValueChangeListener(this::mgModuleChanged);
            finishTypeSelection.removeValueChangeListener(this::finishTypeChanged);
            close();
            ProposalEvent.ModuleUpdated event1 = new ProposalEvent.ModuleUpdated(module, loadNext, moduleIndex);
            DashboardEventBus.post(event1);
        };
    }

    private boolean isLastModule() {
        return moduleIndex == product.getModules().size();
    }

    private String getDefaultText(String itemTitle) {
        return Module.DEFAULT + " (" + itemTitle + ")";
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

    public static void open(Module module, Product product, boolean readOnly, int moduleIndex) {
        Window w = new ModuleDetailsWindow(module, product, readOnly, moduleIndex);
        UI.getCurrent().addWindow(w);
        w.focus();
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
        select.setFilteringMode(FilteringMode.CONTAINS);
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

            list.add(new LookupItem(DEF_CODE_PREFIX + defaultCode, Module.DEFAULT + " (" + title + ")", additionalType));
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