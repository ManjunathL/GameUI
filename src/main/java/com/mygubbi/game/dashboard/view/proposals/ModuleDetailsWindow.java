
package com.mygubbi.game.dashboard.view.proposals;


import com.mygubbi.game.dashboard.ServerManager;
import com.mygubbi.game.dashboard.config.ConfigHolder;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.domain.*;
import com.mygubbi.game.dashboard.domain.JsonPojo.LookupItem;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.mygubbi.game.dashboard.event.ProposalEvent;
import com.mygubbi.game.dashboard.view.NotificationUtil;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FileResource;
import com.vaadin.server.Responsive;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class ModuleDetailsWindow extends Window {

    private static final Logger LOG = LogManager.getLogger(ModuleDetailsWindow.class);
    private final Product product;
    private final String DEF_CODE_PREFIX = "def_";
    private static final String LABEL_WARNING = "warning";
    private final int moduleIndex;

    private Label accessoryHeading;

    private TextField importedModule;
    private TextField description;
    private TextField dimensions;
    private ComboBox carcassMaterialSelection;
    private ComboBox colorCombo;
    private ComboBox exposedSidesCombo;
    private ComboBox exposedBottomCombo;

    private ComboBox accessoryPack1;
    private ComboBox accessoryPack2;
    private ComboBox accessoryPack3;

    private ComboBox addons11;
    private ComboBox addons12;
    private ComboBox addons13;

    private ComboBox addons21;
    private ComboBox addons22;
    private ComboBox addons23;

    private ComboBox addons31;
    private ComboBox addons32;
    private ComboBox addons33;


    private ComboBox shutterFinishSelection;
    private ComboBox finishTypeSelection;

    private Button closeBtn;
    private Button applyButton;

    private Module module;
    private AccessoryPack accessoryPack;

    private ProposalDataProvider proposalDataProvider = ServerManager.getInstance().getProposalDataProvider();
    private final BeanFieldGroup<Module> binder = new BeanFieldGroup<>(Module.class);


    private List<AccessoryAddon> accessoryAddonsList;
    private List<AccessoryPack> accessoryPackList;
    private List<Finish> shutterFinishMasterList;
    private Image moduleImage;
    private ThemeResource emptyModuleImage;
    private String basePath = ConfigHolder.getInstance().getImageBasePath();
    private TextField totalAmount;
    private boolean readOnly;
    private final Label defaultsOverridden;
    private Button applyNextButton;
    private HorizontalLayout accessoryImageLayout;

    private ModuleDetailsWindow(Module module, Product product, boolean readOnly, int moduleIndex) {

        this.product = product;
        this.module = module;
        this.readOnly = readOnly;
        this.moduleIndex = moduleIndex;
        initModule();
        this.binder.setItemDataSource(this.module);

        setModal(true);
        removeCloseShortcut(ShortcutAction.KeyCode.ESCAPE);
        setWidth("75%");
        setHeight("87%");
        setClosable(false);
        setCaption("Edit Module Configuration for " + product.getTitle());

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();

        VerticalLayout verticalLayout1 = new VerticalLayout();
        verticalLayout.setSizeFull();

        VerticalLayout verticalLayout2 = new VerticalLayout();
        verticalLayout.setSizeFull();

        HorizontalLayout horizontalLayoutupper = new HorizontalLayout();
        verticalLayout1.addComponent(horizontalLayoutupper);
        verticalLayout1.setSpacing(false);
        horizontalLayoutupper.setMargin(new MarginInfo(false, false, false, false));
        Responsive.makeResponsive(this);
        horizontalLayoutupper.setWidth("100%");
        horizontalLayoutupper.setHeight("210px");

        HorizontalLayout horizontalLayout1 = new HorizontalLayout();
        horizontalLayout1.setSizeFull();

        Component componentUpper1 = buildModuleComponent();
        horizontalLayout1.addComponent(componentUpper1);
        horizontalLayout1.setSpacing(true);

        Component componentUpper2 = buildModuleOptionsComponent();
        horizontalLayout1.addComponent(componentUpper2);
        horizontalLayout1.setSpacing(true);

        horizontalLayoutupper.addComponent(horizontalLayout1);
        horizontalLayoutupper.setExpandRatio(horizontalLayout1, 0.8f);

        Component componentUpper3 = buildModuleImageComponent();
        horizontalLayoutupper.addComponent(componentUpper3);
        horizontalLayoutupper.setExpandRatio(componentUpper3, 0.2f);

        defaultsOverridden = new Label("Note that the defaults have been overridden.");
        defaultsOverridden.setStyleName(LABEL_WARNING);
        defaultsOverridden.setVisible(false);

        accessoryHeading = new Label("Accessory Configuration");
        accessoryHeading.setStyleName("margin-left-10");
        verticalLayout1.addComponent(accessoryHeading);

        HorizontalLayout horizontalLayoutlower = new HorizontalLayout();
        verticalLayout1.addComponent(horizontalLayoutlower);
        horizontalLayoutlower.setSizeFull();
        horizontalLayoutlower.setMargin(new MarginInfo(false, true, false, true));
        Responsive.makeResponsive(this);


        HorizontalLayout horizontalLayout2 = new HorizontalLayout();
        horizontalLayout2.setSizeUndefined();
        horizontalLayout2.setHeight("150px");

        Component componentLower1 = buildAccPack1Component();
        horizontalLayout2.addComponent(componentLower1);
        horizontalLayout2.setSpacing(true);

        Component componentLower2 = buildAccPack2Component();
        horizontalLayout2.addComponent(componentLower2);
        horizontalLayout2.setSpacing(true);

        Component componentLower3 = buildAccPack3Component();
        horizontalLayout2.addComponent(componentLower3);

        horizontalLayoutlower.addComponent(horizontalLayout2);



        Component componentLower4 = buildAccessoryImagesComponent();
        verticalLayout1.addComponent(componentLower4);

        verticalLayout.addComponent(verticalLayout1);

        verticalLayout.setExpandRatio(verticalLayout1, 0.9f);

        Component footerLayOut = buildFooter();
        verticalLayout2.addComponent(footerLayOut);

        verticalLayout.addComponent(verticalLayout2);
        verticalLayout.setExpandRatio(verticalLayout2, 0.1f);

        setContent(verticalLayout);

        updateValues();
        handleState();
    }


    private void handleState() {
        if (readOnly) {
            carcassMaterialSelection.setReadOnly(true);
            colorCombo.setReadOnly(true);
            finishTypeSelection.setReadOnly(true);
            shutterFinishSelection.setReadOnly(true);
            disableApply();
        }
    }


    private void updateValues() {

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

        updateAccessoryPack(0, accessoryPack1, addons11, addons12, addons13);
        updateAccessoryPack(1, accessoryPack2, addons21, addons22, addons23);
        updateAccessoryPack(2, accessoryPack3, addons31, addons32, addons33);

        if (StringUtils.isNotEmpty(module.getExpSides())) exposedSidesCombo.setValue(module.getExpSides());
        if (StringUtils.isNotEmpty(module.getExpBottom())) exposedBottomCombo.setValue(module.getExpBottom());

        checkDefaultsOverridden();
    }

    private void updateAccessoryPack(int i, ComboBox accessoryPack, ComboBox addons1, ComboBox addons2, ComboBox addons3) {
        if (module.getAccessoryPacks().size() > i) {
            ModuleAccessoryPack moduleAccessoryPack = module.getAccessoryPacks().get(i);
            accessoryPack.setValue(moduleAccessoryPack.getCode());
            List<String> addons = moduleAccessoryPack.getAccessories();
            if (addons.size() > 0) addons1.setValue(addons.get(0));
            if (addons.size() > 1) addons2.setValue(addons.get(1));
            if (addons.size() > 2) addons3.setValue(addons.get(2));
        }
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
        verticalLayout.setMargin(new MarginInfo(false, true, true, true));
        verticalLayout.setSpacing(false);
        //emptyModuleImage = new ThemeResource("img/empty-poster.png");
        moduleImage = new Image("", new FileResource(new File(basePath + module.getImagePath())));
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
        this.colorCombo = getColorsCombo("Color", colors);
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
        this.importedModule = (TextField) binder.buildAndBind("Module", Module.MG_MODULE_CODE);
        this.importedModule.setReadOnly(true);
        formLayout.addComponent(importedModule);

        this.description = (TextField) binder.buildAndBind("Description", Module.DESCRIPTION);
        this.description.setReadOnly(true);
        formLayout.addComponent(this.description);

        this.dimensions = (TextField) binder.buildAndBind("Dimensions", Module.DIMENSION);
        this.dimensions.setReadOnly(true);
        formLayout.addComponent(this.dimensions);

        this.exposedSidesCombo = new ComboBox("Exposed Sides");
        exposedSidesCombo.addItems("None", "Left", "Right", "Both");
        binder.bind(exposedSidesCombo, Module.EXPOSED_SIDES);
        formLayout.addComponent(this.exposedSidesCombo);
        exposedSidesCombo.setNullSelectionAllowed(false);
        exposedSidesCombo.setValue(exposedSidesCombo.getItemIds().iterator().next());
        exposedSidesCombo.addValueChangeListener(this::refreshPrice);

        this.exposedBottomCombo = new ComboBox("Exposed Bottom");
        exposedBottomCombo.addItems("No", "Yes");
        binder.bind(exposedBottomCombo, Module.EXPOSED_BOTTOM);
        formLayout.addComponent(this.exposedBottomCombo);
        exposedBottomCombo.setNullSelectionAllowed(false);
        exposedBottomCombo.setValue(exposedBottomCombo.getItemIds().iterator().next());
        exposedBottomCombo.addValueChangeListener(this::refreshPrice);

        return formLayout;
    }

    private Component buildAccPack1Component() {
        FormLayout formLayout = new FormLayout();
        formLayout.setSizeFull();
        formLayout.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        accessoryPackList = proposalDataProvider.getAccessoryPacks(module.getMgCode());
        this.accessoryPack1 = getAccessoryPackCombo("Acc Pack 1", accessoryPackList, null);
        formLayout.addComponent(this.accessoryPack1);
        accessoryPack1.addValueChangeListener(this::accessoryPack1Changed);

        this.addons11 = getAccessoryAddonsCombo("Addons 1", new ArrayList<>(), null);
        formLayout.addComponent(this.addons11);
        addons11.addValueChangeListener(this::refreshPrice);

        this.addons12 = getAccessoryAddonsCombo("Addons 2", new ArrayList<>(), null);
        formLayout.addComponent(this.addons12);
        addons12.addValueChangeListener(this::refreshPrice);

        this.addons13 = getAccessoryAddonsCombo("Addons 3", new ArrayList<>(), null);
        formLayout.addComponent(this.addons13);
        addons13.addValueChangeListener(this::refreshPrice);

        return formLayout;
    }

    private void accessoryPack1Changed(Property.ValueChangeEvent valueChangeEvent) {
        accessoryPackChanged(accessoryPack1, addons11, addons12, addons13);
        refreshPrice();
    }

    private void accessoryPackChanged(ComboBox accessoryPack, ComboBox addons1, ComboBox addons2, ComboBox addons3) {
        String accessoryPackCode = (String) accessoryPack.getValue();
        List<AccessoryAddon> accessoryAddons = proposalDataProvider.getAccessoryAddons(accessoryPackCode);
        addons1.getContainerDataSource().removeAllItems();
        ((BeanContainer<String, AccessoryAddon>) addons1.getContainerDataSource()).addAll(accessoryAddons);
        addons2.getContainerDataSource().removeAllItems();
        ((BeanContainer<String, AccessoryAddon>) addons2.getContainerDataSource()).addAll(accessoryAddons);
        addons3.getContainerDataSource().removeAllItems();
        ((BeanContainer<String, AccessoryAddon>) addons3.getContainerDataSource()).addAll(accessoryAddons);

        refreshAccessoryImages();
    }

    private Component buildAccPack2Component() {
        FormLayout formLayout = new FormLayout();
        formLayout.setSizeFull();

        formLayout.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        accessoryPackList = proposalDataProvider.getAccessoryPacks(module.getMgCode());
        this.accessoryPack2 = getAccessoryPackCombo("Acc Pack 2", accessoryPackList, null);
        formLayout.addComponent(this.accessoryPack2);
        accessoryPack2.addValueChangeListener(this::accessoryPack2Changed);

        this.addons21 = getAccessoryAddonsCombo("Addons 1", new ArrayList<>(), null);
        formLayout.addComponent(this.addons21);
        addons21.addValueChangeListener(this::refreshPrice);

        this.addons22 = getAccessoryAddonsCombo("Addons 2", new ArrayList<>(), null);
        formLayout.addComponent(this.addons22);
        addons22.addValueChangeListener(this::refreshPrice);

        this.addons23 = getAccessoryAddonsCombo("Addons 3", new ArrayList<>(), null);
        formLayout.addComponent(this.addons23);
        addons23.addValueChangeListener(this::refreshPrice);

        return formLayout;
    }

    private void accessoryPack2Changed(Property.ValueChangeEvent valueChangeEvent) {
        accessoryPackChanged(accessoryPack2, addons21, addons22, addons23);
        refreshPrice();
    }

    private Component buildAccPack3Component() {
        FormLayout formLayout = new FormLayout();
        formLayout.setSizeFull();
        formLayout.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        accessoryPackList = proposalDataProvider.getAccessoryPacks(module.getMgCode());
        this.accessoryPack3 = getAccessoryPackCombo("Acc Pack 3", accessoryPackList, null);
        formLayout.addComponent(this.accessoryPack3);
        accessoryPack3.addValueChangeListener(this::accessoryPack3Changed);

        this.addons31 = getAccessoryAddonsCombo("Addons 1", new ArrayList<>(), null);
        formLayout.addComponent(this.addons31);
        addons31.addValueChangeListener(this::refreshPrice);

        this.addons32 = getAccessoryAddonsCombo("Addons 2", new ArrayList<>(), null);
        formLayout.addComponent(this.addons32);
        addons32.addValueChangeListener(this::refreshPrice);

        this.addons33 = getAccessoryAddonsCombo("Addons 3", new ArrayList<>(), null);
        formLayout.addComponent(this.addons33);
        addons33.addValueChangeListener(this::refreshPrice);

        return formLayout;
    }

    private void accessoryPack3Changed(Property.ValueChangeEvent valueChangeEvent) {
        accessoryPackChanged(accessoryPack3, addons31, addons32, addons33);
        refreshPrice();
    }

    private Component buildAccessoryImagesComponent() {

        Panel accImagePanel=new Panel();
        accImagePanel.setSizeFull();
        accImagePanel.setCaption("Accessory Images");
        accessoryImageLayout = new HorizontalLayout();
        accessoryImageLayout.setCaption("Accessory Images");
        accessoryImageLayout.setMargin(new MarginInfo(false,true,false,true));
        accessoryImageLayout.setStyleName("vLayoutScroll");
        /*accessoryImageLayout.setHeight("150px");
        accessoryImageLayout.setWidth("1000px");*/
        accessoryImageLayout.setSizeUndefined();
        accImagePanel.setContent(accessoryImageLayout);
        return accImagePanel;
    }


    private void refreshAccessoryImages() {
        emptyAccessoryImages();

        String apCode1 = (String) accessoryPack1.getValue();
        if (apCode1 != null) {
            List<Accessory> accessories1 = getAccessories(apCode1, accessoryPack1);
            populateAccessoryImages(accessories1);
        }
        String apCode2 = (String) accessoryPack2.getValue();
        if (apCode2 != null) {
            List<Accessory> accessories2 = getAccessories(apCode2, accessoryPack2);
            populateAccessoryImages(accessories2);
        }
        String apCode3 = (String) accessoryPack3.getValue();
        if (apCode3 != null) {
            List<Accessory> accessories3 = getAccessories(apCode3, accessoryPack3);
            populateAccessoryImages(accessories3);
        }
    }

    private void populateAccessoryImages(List<Accessory> accessories) {
        for (Accessory accessory : accessories) {
            File sourceFile = new File(basePath + accessory.getImagePath());
            LOG.info("image path : " + sourceFile);
            if (sourceFile.exists()) {
                VerticalLayout verticalLayout=new VerticalLayout();
                Image img = new Image("", new FileResource(sourceFile));
                img.setWidth("100px");
                img.setHeight("100px");
                verticalLayout.addComponent(img);
                verticalLayout.setComponentAlignment(img, Alignment.MIDDLE_LEFT);
                Label c = new Label(accessory.getTitle());
                c.setStyleName("labelFont");
                c.setWidth("150px");
                c.setHeight("40px");
                verticalLayout.addComponent(c);
                verticalLayout.setComponentAlignment(c, Alignment.MIDDLE_CENTER);
                accessoryImageLayout.addComponent(verticalLayout);
            }
            accessoryImageLayout.setSpacing(true);
        }
    }

    private List<Accessory> getAccessories(String apCode1, ComboBox accessoryPack) {
        return ((BeanContainer<String, AccessoryPack>) accessoryPack.getContainerDataSource()).getItem(apCode1).getBean().getAccessories();
    }

    private void emptyAccessoryImages() {
        this.accessoryImageLayout.removeAllComponents();
    }

    private void refreshPrice() {

        Module moduleForPrice = new Module();
        moduleForPrice.setCarcassCode(removeDefaultPrefix((String) carcassMaterialSelection.getValue()));
        moduleForPrice.setFinishCode(removeDefaultPrefix((String) shutterFinishSelection.getValue()));


        moduleForPrice.setMgCode(module.getMgCode());
        LOG.info("Asking for module price - " + moduleForPrice.toString());
        moduleForPrice.setSeq(module.getSeq());
        moduleForPrice.setExtCode(module.getExtCode());

        List<ModuleAccessoryPack> accPacks = getModuleAccessoryPacks();

        moduleForPrice.setAccessoryPacks(accPacks);

        moduleForPrice.setExpSides((String) exposedSidesCombo.getValue());
        moduleForPrice.setExpBottom((String) exposedBottomCombo.getValue());

        ModulePrice modulePrice = proposalDataProvider.getModulePrice(moduleForPrice);
        LOG.info("Got price - " + modulePrice.getTotalCost());

        totalAmount.setReadOnly(false);
        totalAmount.setValue(modulePrice.getTotalCost() + "");
        totalAmount.setReadOnly(true);

        checkDefaultsOverridden();

    }

    private List<ModuleAccessoryPack> getModuleAccessoryPacks() {
        List<ModuleAccessoryPack> accPacks = new ArrayList<>();
        ModuleAccessoryPack moduleAccessoryPack = this.getModuleAccessoryPack(this.accessoryPack1, this.addons11, this.addons12, this.addons13);
        if (moduleAccessoryPack != null) accPacks.add(moduleAccessoryPack);

        moduleAccessoryPack = this.getModuleAccessoryPack(this.accessoryPack2, this.addons21, this.addons22, this.addons23);
        if (moduleAccessoryPack != null) accPacks.add(moduleAccessoryPack);

        moduleAccessoryPack = this.getModuleAccessoryPack(this.accessoryPack3, this.addons31, this.addons32, this.addons33);
        if (moduleAccessoryPack != null) accPacks.add(moduleAccessoryPack);
        return accPacks;
    }

    private ModuleAccessoryPack getModuleAccessoryPack(ComboBox accPackCombo, ComboBox addon1Combo, ComboBox addons2Combo, ComboBox addons3Combo) {
        String value = (String) accPackCombo.getValue();
        LOG.debug(value);
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        ModuleAccessoryPack accPack = new ModuleAccessoryPack();
        accPack.setCode(value);

        List<String> addons = new ArrayList<>();

        value = (String) addon1Combo.getValue();
        if (StringUtils.isNotEmpty(value)) addons.add(value);

        value = (String) addons2Combo.getValue();
        if (StringUtils.isNotEmpty(value)) addons.add(value);

        value = (String) addons3Combo.getValue();
        if (StringUtils.isNotEmpty(value)) addons.add(value);

        accPack.setAccessories(addons);
        return accPack;

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

    private void disableApply() {
        applyButton.setEnabled(false);
        applyNextButton.setEnabled(false);
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

        applyButton = new Button("Apply");
        applyButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        applyButton.addClickListener(getApplyListener(false));
        applyButton.setWidth("10%");
        applyButton.focus();
        applyButton.setVisible(true);

        applyNextButton = new Button("Apply & Load Next");
        applyNextButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        applyNextButton.addClickListener(getApplyListener(true));
        applyNextButton.focus();
        applyNextButton.setVisible(!isLastModule());


        footer.addComponent(applyButton);
        footer.addComponent(applyNextButton);
        footer.setComponentAlignment(closeBtn, Alignment.MIDDLE_RIGHT);
        footer.setComponentAlignment(applyButton, Alignment.MIDDLE_CENTER);
        footer.setComponentAlignment(applyNextButton, Alignment.MIDDLE_LEFT);

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
            module.setAccessoryPacks(getModuleAccessoryPacks());
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

    private ComboBox getAccessoryPackCombo(String caption, List<AccessoryPack> list, Property.ValueChangeListener listener) {

        final BeanContainer<String, AccessoryPack> container =
                new BeanContainer<>(AccessoryPack.class);
        container.setBeanIdProperty(AccessoryPack.ACCESSORY_PACK_CODE);
        container.addAll(list);

        ComboBox select = new ComboBox(caption);
        select.setNullSelectionAllowed(true);
        select.setWidth("250px");
        select.setContainerDataSource(container);
        select.setItemCaptionPropertyId(AccessoryPack.ACCESSORY_PACK_TITLE);
        if (listener != null) select.addValueChangeListener(listener);
        return select;
    }

    private ComboBox getAccessoryAddonsCombo(String caption, List<AccessoryAddon> list, Property.ValueChangeListener listener) {
        final BeanContainer<String, AccessoryAddon> container =
                new BeanContainer<>(AccessoryAddon.class);
        container.setBeanIdProperty(AccessoryAddon.ACCESSORY_ADDON_CODE);
        container.addAll(list);

        ComboBox select = new ComboBox(caption);
        select.setNullSelectionAllowed(true);
        select.setWidth("250px");
        select.setStyleName("designs-combo");
        select.setContainerDataSource(container);
        select.setItemIconPropertyId(AccessoryAddon.IMAGE_RESOURCE);
        select.setItemCaptionPropertyId(AccessoryAddon.ACCESSORY_ADDON_TITLE);
        if (listener != null) select.addValueChangeListener(listener);
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


}

