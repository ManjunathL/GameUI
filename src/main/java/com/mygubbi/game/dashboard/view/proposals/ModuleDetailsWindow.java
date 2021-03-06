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
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FileResource;
import com.vaadin.server.Responsive;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.Runo;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.xpath.operations.Mod;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.mygubbi.game.dashboard.domain.ProposalHeader.P_CITY;

@SuppressWarnings("serial")
public class ModuleDetailsWindow extends Window {

    private static final Logger LOG = LogManager.getLogger(ModuleDetailsWindow.class);
    private final Product product;
    private final String DEF_CODE_PREFIX = "def_";
    private static final String LABEL_WARNING = "warning";
    private final int moduleIndex;

    private Label accessoryHeading;

    private TextField importedModule;
    private TextField moduleCategoryText;
    private TextField description;
    private TextField width;
    private TextField depth;
    private TextField height;
    private TextField remarks;

    private ComboBox moduleCategory;
    private ComboBox moduleSelection;
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

    private CheckBox exposedLeft;
    private CheckBox exposedRight;
    private CheckBox exposedBottom;
    private CheckBox exposedTop;
    private CheckBox exposedBack;
    private CheckBox exposedOpen;
    private Image moduleImage;
    private ThemeResource emptyModuleImage;
    private Button applyNextButton;
    private Button loadPreviousButton;
    private HorizontalLayout accessoryImageLayout;
    private Button closeBtn;
    private Button applyButton;
    private final Label defaultsOverridden;
    private TextField totalAmount;

    private ComboBox handleType;
    private ComboBox handleTypefield;
    private ComboBox handle;
    private ComboBox handlefield;
    private ComboBox thickness;
    private ComboBox thicknessfield;
    private Image handleImage;
    private ComboBox knobType;
    private ComboBox knobTypefield;
    private ComboBox knob;
    private ComboBox knobfield;
    private ComboBox knobthickness;
    private ComboBox knobthicknessfield;
    private Image knobImage;
    private OptionGroup glassoptiongroup;
    private OptionGroup hingesoptiongroup;
    private OptionGroup lockoptiongroup;

    private Module module;
    private ProposalVersion proposalVersion;
    private ProposalHeader proposalHeader;

    private ProposalDataProvider proposalDataProvider = ServerManager.getInstance().getProposalDataProvider();
    private final BeanFieldGroup<Module> binder = new BeanFieldGroup<>(Module.class);

    private List<AccessoryPack> accessoryPackList;
    private List<Finish> shutterFinishMasterList;

    private List<MGModule> mgModules;
    private String basePath = ConfigHolder.getInstance().getImageBasePath();
    private boolean readOnly;

    private double calculatedArea = -1;
    private double calculatedAmountWOAccessories = -1;
    private boolean dontCalculatePriceNow = true;
    private double woodworkCost=-1;
    private double hardwareCost=-1;
    private double shutterCost=-1;
    private double carcassCost=-1;
    private double accessoryCost=-1;
    private double labourCost=-1;

    private List<HandleMaster> handlefinish;
    private List<HandleMaster> knobfinsh;
    private List<HandleMaster> handlethickness;
    private List<HandleMaster> Knobthickness;

    Boolean lockhandles=false;
    private ModuleDetailsWindow(Module module, Product product, int moduleIndex, ProposalVersion proposalVersion, ProposalHeader proposalHeader) {
        this.dontCalculatePriceNow = true;
        this.product = product;
        this.module = module;
        this.proposalVersion = proposalVersion;
        this.moduleIndex = moduleIndex;
        this.proposalHeader = proposalHeader;
        initModule();
        this.binder.setItemDataSource(this.module);

        LOG.info("Module in constructor " +module);
        setModal(true);
        setSizeFull();
        setResizable(false);
        setClosable(false);
        //removeCloseShortcut(ShortcutAction.KeyCode.ESCAPE);
        //addStyleName("module-window");
        /*setWidth("80%");
        setHeight("90%");*/
        setClosable(false);
        setCaption("Edit Module Configuration for " + product.getTitle());

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();

        VerticalLayout verticalLayout1 = new VerticalLayout();
        verticalLayout1.addStyleName("margin-10-for-vLayout");
        verticalLayout.setSizeFull();

        VerticalLayout verticalLayout2 = new VerticalLayout();
        verticalLayout.setSizeFull();

        HorizontalLayout horizontalLayoutupper = new HorizontalLayout();
        horizontalLayoutupper.setWidth("100%");
        verticalLayout1.addComponent(horizontalLayoutupper);
        verticalLayout1.setSpacing(false);
        horizontalLayoutupper.setMargin(new MarginInfo(false, false, false, false));
        Responsive.makeResponsive(this);
        horizontalLayoutupper.setHeight("90%");

        HorizontalLayout horizontalLayout1 = new HorizontalLayout();
        horizontalLayout1.setWidth("100%");
        horizontalLayout1.setHeight("70%");

        Component componentUpper1 = buildModuleLeft();
        componentUpper1.setWidth("100%");
        horizontalLayout1.addComponent(componentUpper1);
        horizontalLayout1.setSpacing(true);

       //Component componentUpper2 = buildModuleOptionsComponent();
        Component componentUpper2 = buildModuleMiddle();
        componentUpper2.setWidth("100%");
        horizontalLayout1.addComponent(componentUpper2);
        horizontalLayout1.setSpacing(true);

        horizontalLayoutupper.addComponent(horizontalLayout1);
        horizontalLayoutupper.setExpandRatio(horizontalLayout1, 0.85f);

        horizontalLayoutupper.setWidth("90%");
        Component componentUpper3 = buildModuleRight();
        componentUpper3.setWidth("90%");
        horizontalLayoutupper.addComponent(componentUpper3);
        horizontalLayoutupper.setExpandRatio(componentUpper3, 0.15f);

        defaultsOverridden = new Label("Note that the defaults have been overridden.");
        defaultsOverridden.setStyleName(LABEL_WARNING);
        defaultsOverridden.setVisible(false);

       /* HorizontalLayout horizontalLayout3 = new HorizontalLayout();
        horizontalLayout3.setSizeFull();
        horizontalLayout3.addComponent(buildproduct1());
        horizontalLayout3.addComponent(buildproduct2());
        horizontalLayout3.addComponent(buildproduct3());
        horizontalLayout3.addComponent(buildproduct4());
        verticalLayout1.addComponent(horizontalLayout3);*/

        accessoryHeading = new Label("Accessory Configuration");
        accessoryHeading.setStyleName("margin-exposedLeft-10");
        verticalLayout1.addComponent(accessoryHeading);

        HorizontalLayout horizontalLayoutlower = new HorizontalLayout();
        verticalLayout1.addComponent(horizontalLayoutlower);
        horizontalLayoutlower.setWidth("100%");
        horizontalLayoutlower.setMargin(new MarginInfo(false, true, false, true));
        Responsive.makeResponsive(this);

        HorizontalLayout horizontalLayout2 = new HorizontalLayout();
        horizontalLayout2.setSizeFull();
        horizontalLayout2.setHeight("100%");

        Component componentLower1 = buildAccPack1Component();
        componentLower1.setWidth("100%");
        horizontalLayout2.addComponent(componentLower1);
        horizontalLayout2.setSpacing(true);

        Component componentLower2 = buildAccPack2Component();
        componentLower2.setWidth("100%");
        horizontalLayout2.addComponent(componentLower2);
        horizontalLayout2.setSpacing(true);

        Component componentLower3 = buildAccPack3Component();
        componentLower3.setWidth("100%");
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
        this.addListenerstoDimensionCheckBoxes();
        this.allowDimensionChangesForModule();
        updateValues();
        handleState();

        this.dontCalculatePriceNow = false;

        this.refreshPrice();
    }


    private void handleState() {
        if (readOnly) {
            carcassMaterialSelection.setReadOnly(true);
            colorCombo.setReadOnly(true);
            finishTypeSelection.setReadOnly(true);
            shutterFinishSelection.setReadOnly(true);
            disableApply();
        }

            {
                ProposalVersion.ProposalStage proposalStage = ProposalVersion.ProposalStage.valueOf(proposalVersion.getInternalStatus());
                switch (proposalStage) {
                    case Draft:
                        break;
                    case Published:
                        setComponentsReadOnly();
                        break;
                    case Confirmed:
                        setComponentsReadOnly();
                        break;
                    case Locked:
                        setComponentsReadOnly();
                        break;
                    case DSO:
                        setComponentsReadOnly();
                        break;
                    default:
                        throw new RuntimeException("Unknown State");
                }
            }
        }

    private void setComponentsReadOnly() {
        moduleCategory.setReadOnly(true);
        moduleSelection.setReadOnly(true);
        height.setReadOnly(true);
        width.setReadOnly(true);
        depth.setReadOnly(true);
        carcassMaterialSelection.setReadOnly(true);
        remarks.setReadOnly(true);
        finishTypeSelection.setReadOnly(true);
        shutterFinishSelection.setReadOnly(true);
        colorCombo.setReadOnly(true);
        description.setReadOnly(true);
        exposedBack.setReadOnly(true);
        exposedBottom.setReadOnly(true);
        exposedOpen.setReadOnly(true);
        exposedLeft.setReadOnly(true);
        exposedRight.setReadOnly(true);
        exposedTop.setReadOnly(true);
        accessoryPack1.setReadOnly(true);
        accessoryPack2.setReadOnly(true);
        accessoryPack3.setReadOnly(true);
        addons11.setReadOnly(true);
        addons12.setReadOnly(true);
        addons13.setReadOnly(true);
        addons21.setReadOnly(true);
        addons22.setReadOnly(true);
        addons23.setReadOnly(true);
        addons31.setReadOnly(true);
        addons32.setReadOnly(true);
        addons33.setReadOnly(true);
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
            defaultsOverridden.setVisible(true);
        }

        if (module.getFinish().contains(Module.DEFAULT)) {
            shutterFinishSelection.setValue(DEF_CODE_PREFIX + module.getFinishCode());
        }
        else {
            shutterFinishSelection.setValue(module.getFinishCode());
            defaultsOverridden.setVisible(true);
        }

        updateAccessoryPack(0, accessoryPack1, addons11, addons12, addons13);
        updateAccessoryPack(1, accessoryPack2, addons21, addons22, addons23);
        updateAccessoryPack(2, accessoryPack3, addons31, addons32, addons33);

        if (!module.getExposedLeft().equals(false)) exposedLeft.setValue(module.getExposedLeft());
        if (!module.getExposedRight().equals(false)) exposedRight.setValue(module.getExposedRight());
        if (!module.getExposedTop().equals(false)) exposedTop.setValue(module.getExposedTop());
        if (!module.getExposedBottom().equals(false)) exposedBottom.setValue(module.getExposedBottom());
        if (!module.getExposedBack().equals(false)) exposedBack.setValue(module.getExposedBack());
        if (!module.getExposedOpen().equals(false)) exposedOpen.setValue(module.getExposedOpen());

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

    private void initModule()
    {

        /*if (module.getExpBottom()!= null && module.getExpBottom().equals("Yes"))
            module.setExposedBottom(true);
        else module.setExposedBottom(false);*/


        if (module.getExpSides()!= null && module.getExpSides().equals("both"))
        {
            module.setExposedLeft(true);
            module.setExposedRight(true);
        }
        else if (module.getExpSides()!= null && module.getExpSides().equals("left"))
        {
            module.setExposedLeft(true);
            module.setExposedRight(false);
        }
        else if (module.getExpSides()!= null && module.getExpSides().equals("right"))
        {
            module.setExposedLeft(false);
            module.setExposedRight(true);
        }

        if (module.getModuleType() == null)
            module.setModuleType("S");


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

    private void addListenerstoDimensionCheckBoxes() {
        this.height.addValueChangeListener(valueChangeEvent -> {
            String code = (String) valueChangeEvent.getProperty().getValue();
            this.height.setValue(code);
            refreshPrice();
        });

        this.width.addValueChangeListener(valueChangeEvent -> {
            String code = (String) valueChangeEvent.getProperty().getValue();
            width.setValue(code);
            refreshPrice();
        });

        this.depth.addValueChangeListener(valueChangeEvent -> {
            String code = (String) valueChangeEvent.getProperty().getValue();
            depth.setValue(code);
            refreshPrice();
        });

        if (module.getModuleType().equals("N")) {
            this.description.addValueChangeListener(valueChangeEvent -> {
                String code = (String) valueChangeEvent.getProperty().getValue();
                if (code == null) code = "";
                description.setValue(code);
            });
        }
    }

    private Component buildModuleImageComponent() {
        Panel panel = new Panel();
        panel.setWidth("200px");
        panel.setHeight("150px");
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        verticalLayout.setMargin(new MarginInfo(false, true, true, true));
        verticalLayout.setSpacing(false);
        //emptyModuleImage = new ThemeResource("img/empty-poster.png");
        moduleImage = new Image("", new FileResource(new File(basePath + module.getImagePath())));
        moduleImage.setCaption(null);
        moduleImage.setSizeFull();
        moduleImage.setImmediate(true);
        verticalLayout.addComponent(moduleImage);

        panel.setContent(verticalLayout);
        return panel;
    }
    private Component buildModuleRight()
    {
        VerticalLayout verticalLayoutModule = new VerticalLayout();
        verticalLayoutModule.setSizeFull();
        FormLayout formLayout = new FormLayout();
        formLayout.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        formLayout.addComponent(buildModuleImageComponent());
        formLayout.addComponent(buildAccessoryImagesComponent());

        return formLayout;
    }

    private Component buildModuleMiddle() {
        FormLayout formLayout = new FormLayout();
        formLayout.addStyleName("no-exposedBottom-margin");
        formLayout.setSizeFull();
        formLayout.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        accessoryPackList = proposalDataProvider.getAccessoryPacks(module.getMgCode());
        this.accessoryPack1 = getAccessoryPackCombo("Acc Pack 1", accessoryPackList, null);
        formLayout.addComponent(this.accessoryPack1);
        accessoryPack1.setFilteringMode(FilteringMode.CONTAINS);
        accessoryPack1.addValueChangeListener(this::accessoryPack1Changed);

        this.addons11 = getAccessoryAddonsCombo("Addons 1", new ArrayList<>(), null);
        formLayout.addComponent(this.addons11);
        addons11.setFilteringMode(FilteringMode.CONTAINS);
        addons11.addValueChangeListener(this::addImageAndrefreshPrice);

        this.addons12 = getAccessoryAddonsCombo("Addons 2", new ArrayList<>(), null);
        formLayout.addComponent(this.addons12);
        addons12.setFilteringMode(FilteringMode.CONTAINS);
        addons12.addValueChangeListener(this::addImageAndrefreshPrice);

        this.addons13 = getAccessoryAddonsCombo("Addons 3", new ArrayList<>(), null);
        formLayout.addComponent(this.addons13);
        addons13.setFilteringMode(FilteringMode.CONTAINS);
        addons13.addValueChangeListener(this::addImageAndrefreshPrice);

        accessoryPackList = proposalDataProvider.getAccessoryPacks(module.getMgCode());
        this.accessoryPack2 = getAccessoryPackCombo("Acc Pack 2", accessoryPackList, null);
        formLayout.addComponent(this.accessoryPack2);
        accessoryPack2.setFilteringMode(FilteringMode.CONTAINS);
        accessoryPack2.addValueChangeListener(this::accessoryPack2Changed);

        this.addons21 = getAccessoryAddonsCombo("Addons 1", new ArrayList<>(), null);
        formLayout.addComponent(this.addons21);
        addons21.setFilteringMode(FilteringMode.CONTAINS);
        addons21.addValueChangeListener(this::addImageAndrefreshPrice);

        this.addons22 = getAccessoryAddonsCombo("Addons 2", new ArrayList<>(), null);
        formLayout.addComponent(this.addons22);
        addons22.setFilteringMode(FilteringMode.CONTAINS);
        addons22.addValueChangeListener(this::addImageAndrefreshPrice);

        this.addons23 = getAccessoryAddonsCombo("Addons 3", new ArrayList<>(), null);
        formLayout.addComponent(this.addons23);
        addons23.setFilteringMode(FilteringMode.CONTAINS);
        addons23.addValueChangeListener(this::addImageAndrefreshPrice);

        accessoryPackList = proposalDataProvider.getAccessoryPacks(module.getMgCode());
        this.accessoryPack3 = getAccessoryPackCombo("Acc Pack 3", accessoryPackList, null);
        formLayout.addComponent(this.accessoryPack3);
        accessoryPack3.setFilteringMode(FilteringMode.CONTAINS);
        accessoryPack3.addValueChangeListener(this::accessoryPack3Changed);

        this.addons31 = getAccessoryAddonsCombo("Addons 1", new ArrayList<>(), null);
        formLayout.addComponent(this.addons31);
        addons31.setFilteringMode(FilteringMode.CONTAINS);
        addons31.addValueChangeListener(this::addImageAndrefreshPrice);

        this.addons32 = getAccessoryAddonsCombo("Addons 2", new ArrayList<>(), null);
        formLayout.addComponent(this.addons32);
        addons32.setFilteringMode(FilteringMode.CONTAINS);
        addons32.addValueChangeListener(this::addImageAndrefreshPrice);

        this.addons33 = getAccessoryAddonsCombo("Addons 3", new ArrayList<>(), null);
        formLayout.addComponent(this.addons33);
        addons33.setFilteringMode(FilteringMode.CONTAINS);
        addons33.addValueChangeListener(this::addImageAndrefreshPrice);

        return formLayout;
    }

    private Component buildModuleOptionsComponent() {

        FormLayout formLayout = new FormLayout();
        formLayout.setWidth("70%");
        formLayout.setHeight("30%");
        formLayout.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        this.description = (TextField) binder.buildAndBind("Description", Module.DESCRIPTION);
        formLayout.addComponent(this.description);

        this.carcassMaterialSelection = getSimpleItemFilledCombo("Carcass Material", ProposalDataProvider.CARCASS_LOOKUP, null, getCarcassCodeBasedOnType());
        binder.bind(carcassMaterialSelection, Module.CARCASS_MATERIAL_CODE);
        carcassMaterialSelection.setRequired(true);
        carcassMaterialSelection.addValueChangeListener(this::refreshPrice);
        formLayout.addComponent(this.carcassMaterialSelection);

        this.finishTypeSelection = getSimpleItemFilledCombo("Finish Material", ProposalDataProvider.FINISH_TYPE_LOOKUP, null, product.getFinishTypeCode());
        binder.bind(finishTypeSelection, Module.FINISH_TYPE_CODE);
        formLayout.addComponent(this.finishTypeSelection);
        this.finishTypeSelection.addValueChangeListener(this::finishTypeChanged);

        shutterFinishMasterList = proposalDataProvider.getFinishes(proposalHeader.getPriceDate().toString()); //LookupItems(ProposalDataProvider.FINISH_LOOKUP);
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
    private Component buildModuleLeft()
    {
        VerticalLayout verticalLayoutModule = new VerticalLayout();
        verticalLayoutModule.setSizeFull();
        FormLayout formLayout = new FormLayout();
        formLayout.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        if (Objects.equals("button", module.getModuleSource())) {
            this.moduleCategory = getSimpleItemFilledCombo("Module Category", ProposalDataProvider.MODULE_CATEGORY_LOOKUP, null);
            binder.bind(moduleCategory, Module.MODULE_CATEGORY);
            moduleCategory.setFilteringMode(FilteringMode.CONTAINS);
            moduleCategory.setNullSelectionAllowed(false);
            formLayout.addComponent(this.moduleCategory);
            moduleCategory.addValueChangeListener(valueChangeEvent -> {
                String code = (String) valueChangeEvent.getProperty().getValue();
                LOG.info("code value" +code);
                String title = (String) ((ComboBox) ((Field.ValueChangeEvent) valueChangeEvent).getSource()).getContainerDataSource().getItem(code).getItemProperty("title").getValue();
                module.setModuleCategory(title);
                LOG.debug("After combo box updated:" + module.getProductCategory() + " | module category :" + module.getModuleCategory());

                mgModules = proposalDataProvider.getModules(module.getProductCategory(), module.getModuleCategory());
                if (this.moduleSelection != null) {
                    this.moduleSelection.getContainerDataSource().removeAllItems();
                    ((BeanContainer<String, MGModule>) this.moduleSelection.getContainerDataSource()).addAll(mgModules);
                    moduleSelection.setValue(moduleSelection.getItemIds().iterator().next());
                }
            });

            if (moduleCategory.size() > 0) {
                String code = StringUtils.isNotEmpty(module.getModuleCategory()) ? module.getModuleCategory() : (String) moduleCategory.getItemIds().iterator().next();
                moduleCategory.setValue(code);
            }

            LOG.debug("product category :" + module.getProductCategory() + " | module category :" + module.getModuleCategory());

            mgModules = proposalDataProvider.getModules(module.getProductCategory(), module.getModuleCategory());
            moduleSelection = getModulesCombo("Module", mgModules, null);
            binder.bind(moduleSelection, Module.MG_MODULE_CODE);
            moduleSelection.setFilteringMode(FilteringMode.CONTAINS);
            moduleSelection.setNullSelectionAllowed(false);
            formLayout.addComponent(this.moduleSelection);
            this.moduleSelection.addValueChangeListener(valueChangeEvent -> {
                moduleSelectionChangedEvent(valueChangeEvent);
            });
        }
        else {
            this.importedModule = (TextField) binder.buildAndBind("Module", Module.MG_MODULE_CODE);
            this.importedModule.setReadOnly(true);
            formLayout.addComponent(importedModule);
        }

        this.carcassMaterialSelection = getSimpleItemFilledCombo("Carcass Material", ProposalDataProvider.CARCASS_LOOKUP, null, getCarcassCodeBasedOnType());
        binder.bind(carcassMaterialSelection, Module.CARCASS_MATERIAL_CODE);
        carcassMaterialSelection.setRequired(true);
        carcassMaterialSelection.addValueChangeListener(this::refreshPrice);
        formLayout.addComponent(this.carcassMaterialSelection);

        this.finishTypeSelection = getSimpleItemFilledCombo("Finish Material", ProposalDataProvider.FINISH_TYPE_LOOKUP, null, product.getFinishTypeCode());
        binder.bind(finishTypeSelection, Module.FINISH_TYPE_CODE);
        formLayout.addComponent(this.finishTypeSelection);
        this.finishTypeSelection.addValueChangeListener(this::finishTypeChanged);

        shutterFinishMasterList = proposalDataProvider.getFinishes(proposalHeader.getPriceDate().toString()); //LookupItems(ProposalDataProvider.FINISH_LOOKUP);
        List<Finish> filteredShutterFinish = filterShutterFinishByType();
        this.shutterFinishSelection = getFinishItemFilledCombo("Finish", filteredShutterFinish, null);
        binder.bind(shutterFinishSelection, Module.SHUTTER_FINISH_CODE);
        shutterFinishSelection.addValueChangeListener(this::finishChanged);//refreshPrice);
        formLayout.addComponent(this.shutterFinishSelection);

        List<Color> colors = filterColorsByType();
        this.colorCombo = getColorsCombo("Color", colors);
        binder.bind(colorCombo, Module.COLOR_CODE);
        formLayout.addComponent(this.colorCombo);

        this.remarks = new TextField();
        this.remarks.setCaption("Remarks");
        formLayout.addComponent(remarks);

        this.description = (TextField) binder.buildAndBind("Description", Module.DESCRIPTION);
        formLayout.addComponent(this.description);

       /* formLayout.addComponent(getDimensionsPanel());
        formLayout.addComponent(getExposedPanelsLayout());*/
        totalAmount = new TextField("Total Amount");
        binder.bind(totalAmount, Module.AMOUNT);
        totalAmount.setReadOnly(true);
        formLayout.addComponent(totalAmount);

        verticalLayoutModule.addComponent(formLayout);
        verticalLayoutModule.setExpandRatio(formLayout,0.35f);

        HorizontalLayout horizontalLayoutDimensions = getDimensionsPanel();
        verticalLayoutModule.addComponent(horizontalLayoutDimensions);
        verticalLayoutModule.setExpandRatio(horizontalLayoutDimensions,0.325f);
        verticalLayoutModule.setSpacing(true);

        HorizontalLayout hLayoutExposedPanels = getExposedPanelsLayout();
        verticalLayoutModule.addComponent(hLayoutExposedPanels);
        verticalLayoutModule.setExpandRatio(hLayoutExposedPanels,0.325f);

        return verticalLayoutModule;
    }

    private Component buildModuleComponent() {
        VerticalLayout verticalLayoutModule = new VerticalLayout();
        verticalLayoutModule.setSizeFull();
        FormLayout formLayout = new FormLayout();
        formLayout.setWidth("70%");
        formLayout.setHeight("30%");
        formLayout.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        formLayout.addStyleName("no-bottom-margin-normal");



       if (Objects.equals("button", module.getModuleSource())) {
            this.moduleCategory = getSimpleItemFilledCombo("Module Category", ProposalDataProvider.MODULE_CATEGORY_LOOKUP, null);
            binder.bind(moduleCategory, Module.MODULE_CATEGORY);
            moduleCategory.setFilteringMode(FilteringMode.CONTAINS);
            moduleCategory.setNullSelectionAllowed(false);
            formLayout.addComponent(this.moduleCategory);
            moduleCategory.addValueChangeListener(valueChangeEvent -> {
                String code = (String) valueChangeEvent.getProperty().getValue();
                String title = (String) ((ComboBox) ((Field.ValueChangeEvent) valueChangeEvent).getSource()).getContainerDataSource().getItem(code).getItemProperty("title").getValue();
                module.setModuleCategory(title);
                LOG.debug("After combo box updated:" + module.getProductCategory() + " | module category :" + module.getModuleCategory());

                mgModules = proposalDataProvider.getModules(module.getProductCategory(), module.getModuleCategory());
                if (this.moduleSelection != null) {
                    this.moduleSelection.getContainerDataSource().removeAllItems();
                    ((BeanContainer<String, MGModule>) this.moduleSelection.getContainerDataSource()).addAll(mgModules);
                    moduleSelection.setValue(moduleSelection.getItemIds().iterator().next());
                }
            });


            if (moduleCategory.size() > 0) {
                String code = StringUtils.isNotEmpty(module.getModuleCategory()) ? module.getModuleCategory() : (String) moduleCategory.getItemIds().iterator().next();
                moduleCategory.setValue(code);
            }

            LOG.debug("product category :" + module.getProductCategory() + " | module category :" + module.getModuleCategory());

            mgModules = proposalDataProvider.getModules(module.getProductCategory(), module.getModuleCategory());
            moduleSelection = getModulesCombo("Module", mgModules, null);
            binder.bind(moduleSelection, Module.MG_MODULE_CODE);
            moduleSelection.setFilteringMode(FilteringMode.CONTAINS);
            moduleSelection.setNullSelectionAllowed(false);
            formLayout.addComponent(this.moduleSelection);
            this.moduleSelection.addValueChangeListener(valueChangeEvent -> {
                moduleSelectionChangedEvent(valueChangeEvent);
            });
        }
        else {

           this.importedModule = (TextField) binder.buildAndBind("Module", Module.MG_MODULE_CODE);
           this.importedModule.setReadOnly(true);
           formLayout.addComponent(importedModule);
       }

        formLayout.addComponent(buildModuleOptionsComponent());
        verticalLayoutModule.addComponent(formLayout);
        verticalLayoutModule.setExpandRatio(formLayout,0.35f);

        HorizontalLayout horizontalLayoutDimensions = getDimensionsPanel();
        verticalLayoutModule.addComponent(horizontalLayoutDimensions);
        verticalLayoutModule.setExpandRatio(horizontalLayoutDimensions,0.325f);

        verticalLayoutModule.setSpacing(true);

        HorizontalLayout horizontalLayoutRemarks = getRemarksPanel();
        verticalLayoutModule.addComponent(horizontalLayoutRemarks);
        verticalLayoutModule.setExpandRatio(horizontalLayoutRemarks,0.325f);

        HorizontalLayout hLayoutExposedPanels = getExposedPanelsLayout();
        verticalLayoutModule.addComponent(hLayoutExposedPanels);
        verticalLayoutModule.setExpandRatio(hLayoutExposedPanels,0.325f);

        this.exposedSidesCombo = new ComboBox("Exposed Sides");
        exposedSidesCombo.addItems("None", "Left", "Right", "Both");
        //formLayout.addComponent(this.exposedSidesCombo);
        exposedSidesCombo.setNullSelectionAllowed(false);
        exposedSidesCombo.setValue(exposedSidesCombo.getItemIds().iterator().next());
        exposedSidesCombo.addValueChangeListener(this::refreshPrice);

        this.exposedBottomCombo = new ComboBox("Exposed Bottom");
        exposedBottomCombo.addItems("No", "Yes");
      // formLayout.addComponent(this.exposedBottomCombo);
        exposedBottomCombo.setNullSelectionAllowed(false);
        exposedBottomCombo.setValue(exposedBottomCombo.getItemIds().iterator().next());
        exposedBottomCombo.addValueChangeListener(this::refreshPrice);

        return verticalLayoutModule;
    }

    private void moduleSelectionChangedEvent(Property.ValueChangeEvent valueChangeEvent) {

        this.dontCalculatePriceNow = true;

        String code = (String) valueChangeEvent.getProperty().getValue();
        MGModule mgModule = ((BeanContainer<String, MGModule>) this.moduleSelection.getContainerDataSource()).getItem(code).getBean();
        //((ComboBox) ((Field.ValueChangeEvent) valueChangeEvent).getSource()).getContainerDataSource().getItem(code);

        LOG.debug("MG module - " + mgModule.toString());

        module.setMgCode(mgModule.getCode());
        module.setHeight(mgModule.getHeight());
        module.setDepth(mgModule.getDepth());
        module.setWidth(mgModule.getWidth());
        module.clearAcessorryPacks();
        this.removeAddons();
        this.emptyAccessoryImages();
        module.setModuleType(mgModule.getModuleType());
        module.setModuleCategory(mgModule.getModuleCategory());
        module.setAccessoryPackDefault(mgModule.getAccessoryPackDefault());
        if (module.getModuleType().equals("S")) {
            module.setImagePath(mgModule.getImagePath());
            module.setDescription(mgModule.getDescription());
            module.setRemarks(mgModule.getDescription());
        } else if (module.getModuleType().equals("N")) {
            module.setImagePath("image.jpg");
        }
        moduleImage.setSource(new FileResource(new File(basePath + module.getImagePath())));

        this.height.setReadOnly(false);
        this.width.setReadOnly(false);
        this.depth.setReadOnly(false);

        this.height.setValue(String.valueOf(module.getHeight()));
        this.width.setValue(String.valueOf(module.getWidth()));
        this.depth.setValue(String.valueOf(module.getDepth()));

        this.description.setReadOnly(false);
        String description = module.getDescription();
        if (description == null) {
            this.description.setValue("");
        }
        else
        {
            this.description.setValue(description);
        }

        this.remarks.setReadOnly(false);
        this.remarks.setValue(description);

        module.setImportStatus(Module.ImportStatusType.m.name());
        module.setUnitType(mgModule.getUnitType());
        List<MGModule> value=proposalDataProvider.checksqftCalculation(mgModule.getCode());
        LOG.info("Mg code to check handle present or not " +mgModule.getCode());
        for(MGModule m:value)
        {
            LOG.info("handle present" +m.getHandleMandatory() +lockhandles);
            if(m.getHandleMandatory().equals("Yes"))
            {
                LOG.info("inside if to set value");
                lockhandles = true;
            }
            LOG.info("lock handle value after check" +lockhandles);
        }

        this.allowDimensionChangesForModule();
        LOG.debug("Module b4 price calc :" + module.toString());
        this.dontCalculatePriceNow = false;
        refreshPrice();
        refreshAccPacks();
        refreshHandle();
    }

    private void removeAddons() {
        addons11.getContainerDataSource().removeAllItems();
        addons12.getContainerDataSource().removeAllItems();
        addons13.getContainerDataSource().removeAllItems();
        addons21.getContainerDataSource().removeAllItems();
        addons22.getContainerDataSource().removeAllItems();
        addons23.getContainerDataSource().removeAllItems();
        addons31.getContainerDataSource().removeAllItems();
        addons32.getContainerDataSource().removeAllItems();
        addons33.getContainerDataSource().removeAllItems();
    }

    private void allowDimensionChangesForModule() {
        if (module.getModuleType().equals("S"))
        {
            this.height.setReadOnly(true);
            this.width.setReadOnly(true);
            this.depth.setReadOnly(true);
            this.description.setReadOnly(true);
        }
        else
        {
            this.height.setReadOnly(false);
            this.width.setReadOnly(false);
            this.depth.setReadOnly(false);
            this.description.setReadOnly(false);
            isDimensionsEmpty();
        }
    }

    private void refreshAccPacks(){
        accessoryPackList = proposalDataProvider.getAccessoryPacks(module.getMgCode());
        this.accessoryPack1.getContainerDataSource().removeAllItems();
        ((BeanContainer<String, AccessoryPack>) this.accessoryPack1.getContainerDataSource()).addAll(accessoryPackList);

        this.accessoryPack2.getContainerDataSource().removeAllItems();
        ((BeanContainer<String, AccessoryPack>) this.accessoryPack2.getContainerDataSource()).addAll(accessoryPackList);

        this.accessoryPack3.getContainerDataSource().removeAllItems();
        ((BeanContainer<String, AccessoryPack>) this.accessoryPack3.getContainerDataSource()).addAll(accessoryPackList);

    }

    private void refreshHandle()
    {
        this.handleType.getContainerDataSource().removeAllItems();
        if(lockhandles==true)
        {
            LOG.info("value true");
            List<HandleMaster> handle=proposalDataProvider.getHandleTitle("Handle");
            for(HandleMaster handles:handle)
            {
                LOG.info("handle title" +handles);
            }
            final BeanContainer<String, HandleMaster> container = new BeanContainer<>(HandleMaster.class);
            container.setBeanIdProperty(HandleMaster.TITLE);
            container.addAll(handle);
            handleType.setContainerDataSource(container);
        }

    }

    private void isDimensionsEmpty() {
        /*
        int height = this.getIntegerValue(this.height.getValue());
        int width = this.getIntegerValue(this.width.getValue());
        int depth = this.getIntegerValue(this.depth.getValue());
        if (height == 0  || width == 0 || depth == 0) {
            NotificationUtil.showNotification("Please specify all dimensions", NotificationUtil.STYLE_BAR_ERROR_SMALL);
        }*/
    }

    private HorizontalLayout getExposedPanelsLayout() {
        HorizontalLayout hLayoutExposedPanels = new HorizontalLayout();
        hLayoutExposedPanels.setSizeFull();
        hLayoutExposedPanels.setMargin(new MarginInfo(false,true,false,true));
        hLayoutExposedPanels.setCaption("Exposed Panels");

        this.exposedLeft = new CheckBox("Left");
        hLayoutExposedPanels.addComponent(exposedLeft);
        binder.bind(exposedLeft,Module.EXPOSED_LEFT);
        this.exposedLeft.addValueChangeListener(this::refreshPrice);

        this.exposedRight = new CheckBox("Right");
        hLayoutExposedPanels.addComponent(exposedRight);
        binder.bind(exposedRight,Module.EXPOSED_RIGHT);
        this.exposedRight.addValueChangeListener(this::refreshPrice);

        this.exposedBottom = new CheckBox("Bottom");
        hLayoutExposedPanels.addComponent(exposedBottom);
        binder.bind(exposedBottom,Module.EXPOSED_BOTTOM);
        this.exposedBottom.addValueChangeListener(this::refreshPrice);

        hLayoutExposedPanels.setSpacing(true);

        this.exposedTop = new CheckBox("Top");
        hLayoutExposedPanels.addComponent(exposedTop);
        binder.bind(exposedTop,Module.EXPOSED_TOP);
        this.exposedTop.addValueChangeListener(this::refreshPrice);

        this.exposedBack = new CheckBox("Back");
        hLayoutExposedPanels.addComponent(exposedBack);
        binder.bind(exposedBack,Module.EXPOSED_BACK);
        this.exposedBack.addValueChangeListener(this::refreshPrice);

        this.exposedOpen = new CheckBox("Open");
        hLayoutExposedPanels.addComponent(exposedOpen);
        binder.bind(exposedOpen,Module.EXPOSED_OPEN);
        this.exposedOpen.addValueChangeListener(this::refreshPrice);

        return hLayoutExposedPanels;
    }

    private HorizontalLayout getDimensionsPanel() {
        HorizontalLayout horizontalLayoutDimensions = new HorizontalLayout();
        horizontalLayoutDimensions.setSizeFull();
        horizontalLayoutDimensions.setMargin(new MarginInfo(false,true,false,true));
        horizontalLayoutDimensions.setCaption("Dimensions");
        horizontalLayoutDimensions.setSpacing(false);

        FormLayout formLayoutWidth = new FormLayout();
        formLayoutWidth.setSizeFull();
        formLayoutWidth.setMargin(new MarginInfo(false,false,false,false));
        this.width = new TextField();
        this.width.setCaption("Width");
        this.width.setWidth("60px");
        this.width.addStyleName(Runo.TEXTFIELD_SMALL);
        binder.bind(this.width,Module.WIDTH);
        formLayoutWidth.addComponent(width);
        horizontalLayoutDimensions.addComponent(formLayoutWidth);

        FormLayout formLayoutDepth = new FormLayout();
        formLayoutDepth.setSizeFull();
        formLayoutDepth.setMargin(new MarginInfo(false,false,false,false));
        this.depth = new TextField();
        this.depth.setCaption("Depth");
        this.depth.setWidth("60px");
        this.depth.addStyleName(Runo.TEXTFIELD_SMALL);
        binder.bind(this.depth,Module.DEPTH);
        formLayoutDepth.addComponent(depth);
        horizontalLayoutDimensions.addComponent(formLayoutDepth);

        FormLayout formLayoutHeight = new FormLayout();
        formLayoutHeight.setSizeFull();
        formLayoutHeight.setMargin(new MarginInfo(false,false,false,false));
        this.height = new TextField();
        this.height.setCaption("Height");
        this.height.setWidth("60px");
        this.height.addStyleName(Runo.TEXTFIELD_SMALL);
        binder.bind(this.height, Module.HEIGHT);
        formLayoutHeight.addComponent(height);
        horizontalLayoutDimensions.addComponent(formLayoutHeight);

        return horizontalLayoutDimensions;
    }
    private HorizontalLayout getRemarksPanel(){

        HorizontalLayout horizontalLayoutRemarks = new HorizontalLayout();
        horizontalLayoutRemarks.setSizeFull();
        horizontalLayoutRemarks.setMargin(new MarginInfo(false,true,false,false));
        horizontalLayoutRemarks.setSpacing(false);

        FormLayout formLayoutRemarks = new FormLayout();
        formLayoutRemarks.setSizeFull();
        formLayoutRemarks.setMargin(new MarginInfo(false,false,false,false));
        this.remarks = new TextField();
        this.remarks.setCaption("Remarks");
        formLayoutRemarks.setWidth("100%");
        formLayoutRemarks.setHeight("50%");
        formLayoutRemarks.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        binder.bind(this.remarks, Module.REMARKS);
        formLayoutRemarks.addComponent(remarks);
        horizontalLayoutRemarks.addComponent(formLayoutRemarks);
        return horizontalLayoutRemarks;
    }
    private Component buildAccPack1Component() {
        FormLayout formLayout = new FormLayout();
        formLayout.addStyleName("no-exposedBottom-margin");
        formLayout.setSizeFull();
        formLayout.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        accessoryPackList = proposalDataProvider.getAccessoryPacks(module.getMgCode());
        this.accessoryPack1 = getAccessoryPackCombo("Acc Pack 1", accessoryPackList, null);
        formLayout.addComponent(this.accessoryPack1);
        accessoryPack1.setFilteringMode(FilteringMode.CONTAINS);
        accessoryPack1.addValueChangeListener(this::accessoryPack1Changed);

        this.addons11 = getAccessoryAddonsCombo("Addons 1", new ArrayList<>(), null);
        formLayout.addComponent(this.addons11);
        addons11.setFilteringMode(FilteringMode.CONTAINS);
        addons11.addValueChangeListener(this::addImageAndrefreshPrice);

        this.addons12 = getAccessoryAddonsCombo("Addons 2", new ArrayList<>(), null);
        formLayout.addComponent(this.addons12);
        addons12.setFilteringMode(FilteringMode.CONTAINS);
        addons12.addValueChangeListener(this::addImageAndrefreshPrice);

        this.addons13 = getAccessoryAddonsCombo("Addons 3", new ArrayList<>(), null);
        formLayout.addComponent(this.addons13);
        addons13.setFilteringMode(FilteringMode.CONTAINS);
        addons13.addValueChangeListener(this::addImageAndrefreshPrice);

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
        refreshPrice();
        ((BeanContainer<String, AccessoryAddon>) addons1.getContainerDataSource()).addAll(accessoryAddons);
        addons2.getContainerDataSource().removeAllItems();
        refreshPrice();
        ((BeanContainer<String, AccessoryAddon>) addons2.getContainerDataSource()).addAll(accessoryAddons);
        addons3.getContainerDataSource().removeAllItems();
        refreshPrice();
        ((BeanContainer<String, AccessoryAddon>) addons3.getContainerDataSource()).addAll(accessoryAddons);

        refreshAccessoryImages();
    }

    private Component buildAccPack2Component() {
        FormLayout formLayout = new FormLayout();
        formLayout.setSizeFull();

        formLayout.addStyleName("no-exposedBottom-margin");
        formLayout.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        accessoryPackList = proposalDataProvider.getAccessoryPacks(module.getMgCode());
        this.accessoryPack2 = getAccessoryPackCombo("Acc Pack 2", accessoryPackList, null);
        formLayout.addComponent(this.accessoryPack2);
        accessoryPack2.setFilteringMode(FilteringMode.CONTAINS);
        accessoryPack2.addValueChangeListener(this::accessoryPack2Changed);

        this.addons21 = getAccessoryAddonsCombo("Addons 1", new ArrayList<>(), null);
        formLayout.addComponent(this.addons21);
        addons21.setFilteringMode(FilteringMode.CONTAINS);
        addons21.addValueChangeListener(this::addImageAndrefreshPrice);

        this.addons22 = getAccessoryAddonsCombo("Addons 2", new ArrayList<>(), null);
        formLayout.addComponent(this.addons22);
        addons22.setFilteringMode(FilteringMode.CONTAINS);
        addons22.addValueChangeListener(this::addImageAndrefreshPrice);

        this.addons23 = getAccessoryAddonsCombo("Addons 3", new ArrayList<>(), null);
        formLayout.addComponent(this.addons23);
        addons23.setFilteringMode(FilteringMode.CONTAINS);
        addons23.addValueChangeListener(this::addImageAndrefreshPrice);

        return formLayout;
    }

    private void accessoryPack2Changed(Property.ValueChangeEvent valueChangeEvent) {
        accessoryPackChanged(accessoryPack2, addons21, addons22, addons23);
        refreshPrice();
    }

    private Component buildAccPack3Component() {
        FormLayout formLayout = new FormLayout();
        formLayout.setSizeFull();
        formLayout.addStyleName("no-exposedBottom-margin");
        formLayout.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        accessoryPackList = proposalDataProvider.getAccessoryPacks(module.getMgCode());
        this.accessoryPack3 = getAccessoryPackCombo("Acc Pack 3", accessoryPackList, null);
        formLayout.addComponent(this.accessoryPack3);
        accessoryPack3.setFilteringMode(FilteringMode.CONTAINS);
        accessoryPack3.addValueChangeListener(this::accessoryPack3Changed);

        this.addons31 = getAccessoryAddonsCombo("Addons 1", new ArrayList<>(), null);
        formLayout.addComponent(this.addons31);
        addons31.setFilteringMode(FilteringMode.CONTAINS);
        addons31.addValueChangeListener(this::addImageAndrefreshPrice);

        this.addons32 = getAccessoryAddonsCombo("Addons 2", new ArrayList<>(), null);
        formLayout.addComponent(this.addons32);
        addons32.setFilteringMode(FilteringMode.CONTAINS);
        addons32.addValueChangeListener(this::addImageAndrefreshPrice);

        this.addons33 = getAccessoryAddonsCombo("Addons 3", new ArrayList<>(), null);
        formLayout.addComponent(this.addons33);
        addons33.setFilteringMode(FilteringMode.CONTAINS);
        addons33.addValueChangeListener(this::addImageAndrefreshPrice);

        return formLayout;
    }

    private void accessoryPack3Changed(Property.ValueChangeEvent valueChangeEvent) {
        accessoryPackChanged(accessoryPack3, addons31, addons32, addons33);
        refreshPrice();
    }

    private Component buildAccessoryImagesComponent() {

        Panel accImagePanel=new Panel();
        accImagePanel.setHeight("50%");
        accImagePanel.setCaption("Accessory Images");
        accessoryImageLayout = new HorizontalLayout();
        accessoryImageLayout.setCaption("Accessory Images");
        accessoryImageLayout.setMargin(new MarginInfo(false,true,false,true));
        accessoryImageLayout.addStyleName("vLayoutScroll");
        accessoryImageLayout.setSizeUndefined();
        accImagePanel.setContent(accessoryImageLayout);
        return accImagePanel;
    }

    String role = ((User) VaadinSession.getCurrent().getAttribute(User.class.getName())).getRole();


    private void refreshAccessoryImages() {
        emptyAccessoryImages();

        this.validateAndAddAccessoryPackImages((String) accessoryPack1.getValue(), accessoryPack1);
        this.validateAndAddAccessoryPackImages((String) accessoryPack2.getValue(), accessoryPack2);
        this.validateAndAddAccessoryPackImages((String) accessoryPack3.getValue(), accessoryPack3);

        this.addAddonImageToPanel(this.addons11);
        this.addAddonImageToPanel(this.addons12);
        this.addAddonImageToPanel(this.addons13);
        this.addAddonImageToPanel(this.addons21);
        this.addAddonImageToPanel(this.addons22);
        this.addAddonImageToPanel(this.addons23);
        this.addAddonImageToPanel(this.addons31);
        this.addAddonImageToPanel(this.addons32);
        this.addAddonImageToPanel(this.addons33);

    }

    private void addAddonImageToPanel(ComboBox addonCombo) {
        Object selectedValue = addonCombo.getValue();
        if (selectedValue != null)
        {
            AccessoryAddon addon = (AccessoryAddon) ((BeanItem) addonCombo.getItem(selectedValue)).getBean();
            if (addon != null) this.addImageToAccessoryPanel(addon.getImagePath(), addon.getTitle());
        }
    }

    private void validateAndAddAccessoryPackImages(String apCode, ComboBox accessoryPackCombo) {
        if (apCode != null)
        {
            for (Accessory accessory : this.getAccessories(apCode, accessoryPackCombo)) {
                this.addImageToAccessoryPanel(accessory.getImagePath(), accessory.getTitle());
            }
        }
    }

    private void addImageToAccessoryPanel(String imagePath, String title) {
        File sourceFile = new File(basePath + imagePath);
        LOG.info("image path : " + sourceFile);
        if (sourceFile.exists()) {
            VerticalLayout verticalLayout=new VerticalLayout();
            Image img = new Image("", new FileResource(sourceFile));
            img.setStyleName("img-resp");
            //img.setWidth("100px");
            img.setHeight("90px");
            img.setCaption(title.substring(0, 16) + "..");
            verticalLayout.addComponent(img);
            verticalLayout.setComponentAlignment(img, Alignment.MIDDLE_LEFT);
//            Label c = new Label(title);
//            c.setStyleName("labelFont");
//            c.setWidth("150px");
//            c.setHeight("40px");
//            verticalLayout.addComponent(c);
            accessoryImageLayout.addComponent(verticalLayout);
        }
        accessoryImageLayout.setSpacing(true);
    }

    private List<Accessory> getAccessories(String apCode1, ComboBox accessoryPack) {
        return ((BeanContainer<String, AccessoryPack>) accessoryPack.getContainerDataSource()).getItem(apCode1).getBean().getAccessories();
    }

    private void emptyAccessoryImages() {
        this.accessoryImageLayout.removeAllComponents();
    }

    private void refreshPrice() {
        if (this.dontCalculatePriceNow) return;
        if (StringUtils.isEmpty(this.module.getMgCode())) return;
        //if (StringUtils.isEmpty(this.module.getModuleCategory())) return;
        isDimensionsEmpty();

        ModulePrice modulePrice = this.recalculatePriceForModule();

        LOG.info("module price in Module Details Window " +modulePrice);
        if (modulePrice != null)
        {
            totalAmount.setReadOnly(false);
            totalAmount.setValue(modulePrice.getTotalCost() + "");
            totalAmount.setReadOnly(true);
            this.calculatedArea = modulePrice.getModuleArea();
            this.calculatedAmountWOAccessories = modulePrice.getWoodworkCost();
            this.woodworkCost=modulePrice.getWoodworkCost();
            this.hardwareCost=modulePrice.getHardwareCost();
            this.shutterCost=modulePrice.getShutterCost();
            this.carcassCost=modulePrice.getCarcassCost();
            this.accessoryCost=modulePrice.getAccessoryCost();
            this.labourCost=modulePrice.getLabourCost();
            this.noPricingErrors();
        }
        else
        {
            totalAmount.setReadOnly(false);
            totalAmount.setValue("0");
            totalAmount.setReadOnly(true);
            this.calculatedArea = 0;
            this.calculatedAmountWOAccessories = 0;
            this.woodworkCost=0;
            this.hardwareCost=0;
            this.shutterCost=0;
            this.carcassCost=0;
            this.accessoryCost=0;
            this.labourCost=0;
            this.showPricingErrors();
        }
        checkDefaultsOverridden();
    }

    private void showPricingErrors()
    {
        disableApply();
        //Not
        // ificationUtil.showNotification("Module pricing has errors!", NotificationUtil.STYLE_BAR_ERROR_SMALL);
    }

    private void noPricingErrors()
    {
        enableApply();
    }

    private ModulePrice recalculatePriceForModule() {

        ModuleForPrice moduleForPrice = new ModuleForPrice();

        this.module.setCarcassCode(removeDefaultPrefix((String) carcassMaterialSelection.getValue()));
        this.module.setFinishCode(removeDefaultPrefix((String) shutterFinishSelection.getValue()));

        int integerValueHeight = getIntegerValue(this.height.getValue());
        this.module.setHeight(integerValueHeight);

        int integerValueWidth = getIntegerValue(this.width.getValue());
        this.module.setWidth(integerValueWidth);

        int integerValueDepth = getIntegerValue(this.depth.getValue());
        this.module.setDepth(integerValueDepth);

        List<ModuleAccessoryPack> accPacks = getModuleAccessoryPacks();
        this.module.setAccessoryPacks(accPacks);
        this.module.setExposedLeft(exposedLeft.getValue());
        this.module.setExposedRight(exposedRight.getValue());
        this.module.setExposedTop(exposedTop.getValue());
        this.module.setExposedBottom(exposedBottom.getValue());
        this.module.setExposedBack(exposedBack.getValue());
        this.module.setExposedOpen(exposedOpen.getValue());
        moduleForPrice.setModule(module);


        Date priceDate = proposalHeader.getPriceDate();
        if (!("Draft").equals(proposalVersion.getStatus()))
        moduleForPrice.setPriceToBeChanged(true);

        if (priceDate == null) {
         Date date = new Date(System.currentTimeMillis());
            moduleForPrice.setPriceDate( date);
        }
        else
        {
            moduleForPrice.setPriceDate(priceDate);
        }
        moduleForPrice.setCity(proposalHeader.getPcity());
        moduleForPrice.setModule(this.module);

        if (this.module.getHeight() == 0 || this.module.getDepth() == 0 || this.module.getWidth() == 0)
        {
            return null;
        }

        LOG.info("Asking for module price - " + this.module.toString());
        try
        {
            return proposalDataProvider.getModulePrice(moduleForPrice);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private int getIntegerValue(String value) {
        try {
            return java.text.NumberFormat.getInstance().parse(value).intValue();
            //return Integer.parseInt(value);
        } catch (Exception e) {
            return 0;
        }
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
        LOG.info("Pack Value" +value);

        if (StringUtils.isEmpty(value)) {
            return null;
        }
        ModuleAccessoryPack accPack = new ModuleAccessoryPack();
        accPack.setCode(value);
        LOG.info("caption" +accPackCombo.getItemCaptionPropertyId().toString());
       //accPack.setPacktitle();

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

    private void moduleChanged(Property.ValueChangeEvent valueChangeEvent) {
        String mgCode = (String) moduleSelection.getValue();

        List<Module> list = proposalDataProvider.getModuleDetails((String) this.moduleSelection.getValue());
        refreshPrice();
    }

    private void addImageAndrefreshPrice(Property.ValueChangeEvent valueChangeEvent) {
        refreshPrice();
        refreshAccessoryImages();
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
        //return proposalDataProvider.getColorsByGroup(finish.getColorGroupCode(),proposalHeader.getPriceDate().toString());
        return null;
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
        //applyButton.setEnabled(false);
        //applyNextButton.setEnabled(false);
    }

    private void enableApply() {
        applyButton.setEnabled(true);
        applyNextButton.setEnabled(true);
        loadPreviousButton.setEnabled(true);
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
        applyButton.addClickListener(getApplyListener(false,false));
        applyButton.setWidth("10%");
        applyButton.focus();
        applyButton.setVisible(true);
        applyNextButton = new Button("Apply & Load Next");
        applyNextButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        applyNextButton.addClickListener(getApplyListener(true,true));
        applyNextButton.focus();
        applyNextButton.setVisible(!isLastModule());
        loadPreviousButton = new Button("Previous");
        loadPreviousButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        loadPreviousButton.addClickListener(getApplyListener(false,true));
        loadPreviousButton.focus();
        loadPreviousButton.setVisible(!isFirstModule());

        footer.addComponent(loadPreviousButton);
        footer.addComponent(applyButton);
        footer.addComponent(applyNextButton);
        footer.setComponentAlignment(closeBtn, Alignment.MIDDLE_CENTER);
        footer.setComponentAlignment(applyButton, Alignment.MIDDLE_CENTER);
        footer.setComponentAlignment(applyNextButton, Alignment.MIDDLE_LEFT);
        footer.setComponentAlignment(loadPreviousButton, Alignment.MIDDLE_RIGHT);

        return footer;
    }

    private ClickListener   getApplyListener(boolean loadNext,boolean loadPrevious) {
        return event -> {

            if (module.getMgCode()== null)
            {
                NotificationUtil.showNotification("Please select module before saving", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                return;
            }

            if (("Yes").equals(module.getAccessoryPackDefault())) {
                if (module.getAccessoryPacks().size() == 0) {
                    NotificationUtil.showNotification("Please select accessories", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                    return;
                }
            }

            if(module.getDepth()== 0 || module.getHeight()==0 || module.getWidth() == 0)
            {
                NotificationUtil.showNotification("Please enter valid dimensions", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                return;
            }
                String accPack1 = ((String) accessoryPack1.getValue());
            String addon11 = ((String) addons11.getValue());

            if (("AP-GENERIC").equals(accPack1)) {

                if (StringUtils.isEmpty(addon11))
                {    NotificationUtil.showNotification("Please select at least one addons", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                    return;
                }
            }
            String accPack2 = ((String) accessoryPack2.getValue());
            String addon21 = ((String) addons21.getValue());

            if (("AP-GENERIC").equals(accPack2)) {

                if (StringUtils.isEmpty(addon21))
                {    NotificationUtil.showNotification("Please select at least one addons", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                    return;
                }
            }
            String accPack3 = ((String) accessoryPack3.getValue());
            String addon31 = ((String) addons31.getValue());

            if (("AP-GENERIC").equals(accPack3)) {

                if (StringUtils.isEmpty(addon31))
                {    NotificationUtil.showNotification("Please select at least one addons", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                    return;
                }
            }

            try {
                binder.commit();
            } catch (FieldGroup.CommitException e) {
                e.printStackTrace();
                NotificationUtil.showNotification("Problem while applying changes. Please contact GAME Admin", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            }catch(Exception e)
            {
                e.printStackTrace();
            }

            module.setCarcassCode(removeDefaultPrefix(module.getCarcassCode()));
            module.setFinishTypeCode(removeDefaultPrefix(module.getFinishTypeCode()));
            module.setFinishCode(removeDefaultPrefix(module.getFinishCode()));
            module.setAccessoryPacks(getModuleAccessoryPacks());
           /* module.setHandle(handle.getValue().toString());
            module.setHandleType(handleType.getValue().toString());
            module.setHandleThickness(thickness.getValue().toString());
            module.setKnob(knob.getValue().toString());
            module.setKnobType(knobType.getValue().toString());
            module.setKnobThickness(knobthickness.getValue().toString());*/
            List<ModuleAccessoryPack> accessoryPacks=getModuleAccessoryPacks();
            LOG.info("Accessory pack" +accessoryPacks);
            LOG.info("acc pack size" +accessoryPacks.size());
            if(accessoryPacks.size()!=0)
            {
                module.setAccessoryflag("Y");
            }else
            {
                module.setAccessoryflag("N");
            }

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

            if (this.calculatedArea != -1) module.setArea(this.calculatedArea);
            if (this.calculatedAmountWOAccessories != -1)
            {
                module.setAmountWOAccessories(this.calculatedAmountWOAccessories);
                module.setWoodworkCost(this.woodworkCost);
                module.setHardwareCost(this.hardwareCost);
                module.setShutterCost(this.shutterCost);
                module.setCarcassCost(this.carcassCost);
                module.setAccessoryCost(this.accessoryCost);
                module.setLabourCost(this.labourCost);
            }

            LOG.debug("this.calculatedArea:" + this.calculatedArea + " | this.calculatedAmountWOAccessories:" + this.calculatedAmountWOAccessories + " | WoodworkCost: " +this.woodworkCost+ " | HardwareCost: " +this.hardwareCost+ " | carcasscost " +this.carcassCost + " | Accessory Cost: " +this.accessoryCost+ " | Labour Cost: " +this.accessoryCost);

            if (module.getAmount() == 0)
            {
                NotificationUtil.showNotification("Module Price cannot be zero", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                return;
            }

            LOG.debug("Module being updated:" + this.module.toString());
            finishTypeSelection.removeValueChangeListener(this::finishTypeChanged);
            close();
            LOG.info("module save " +module);
            ProposalEvent.ModuleUpdated event1 = new ProposalEvent.ModuleUpdated(module, loadNext, loadPrevious, moduleIndex, this);
            DashboardEventBus.post(event1);
/*
            ProposalEvent.ModuleCreated event2 = new ProposalEvent.ModuleCreated(module, loadNext, loadPrevious, moduleIndex, this);
            DashboardEventBus.post(event2);
*/
        };
    }

    private boolean isLastModule() {
        return moduleIndex == product.getModules().size();
    }

    private boolean isFirstModule() {
        return moduleIndex == 1;
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
        if (Objects.equals(module.getUnitType(), "NA"))
        {
            return null;
        }
        return module.getUnitType().toLowerCase().contains(Module.UnitTypes.wall.name()) ? product.getWallCarcassCode() : product.getBaseCarcassCode();
    }


    private String removeDefault(String title) {
        int defaultIndex = title.indexOf(" (default)");
        return defaultIndex == -1 ? title : title.substring(0, defaultIndex);
    }

    public static void open(Module module, Product product, int moduleIndex, ProposalVersion proposalVersion, ProposalHeader proposalHeader) {
        Module clonedModule = module.clone();
        Window w = new ModuleDetailsWindow(clonedModule, product, moduleIndex, proposalVersion, proposalHeader);
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
        select.setContainerDataSource(container);
        select.setItemCaptionPropertyId(Finish.TITLE);
        if (listener != null) select.addValueChangeListener(listener);
        if (container.size() > 0) select.setValue(select.getItemIds().iterator().next());
        return select;
    }

    private ComboBox getSimpleItemFilledCombo(String caption, List<LookupItem> list, Property.ValueChangeListener listener) {

        final BeanContainer<String, LookupItem> container =
                new BeanContainer<>(LookupItem.class);
        container.setBeanIdProperty(LookupItem.CODE);
        container.addAll(list);

        ComboBox select = new ComboBox(caption);
        select.setNullSelectionAllowed(false);
        select.setContainerDataSource(container);
        select.setItemCaptionPropertyId(LookupItem.TITLE);
        if (listener != null) select.addValueChangeListener(listener);
        return select;
    }

    private ComboBox getAccessoryPackCombo(String caption, List<AccessoryPack> list, Property.ValueChangeListener listener) {

        final BeanContainer<String, AccessoryPack> container =
                new BeanContainer<>(AccessoryPack.class);
        container.setBeanIdProperty(AccessoryPack.ACCESSORY_PACK_CODE);
        container.addAll(list);

        ComboBox select = new ComboBox(caption);
        select.setNullSelectionAllowed(true);
        select.setContainerDataSource(container);
        select.setItemCaptionPropertyId(AccessoryPack.ACCESSORY_PACK_TITLE);
        if (listener != null) select.addValueChangeListener(listener);
        return select;
    }

    private ComboBox getModulesCombo(String caption, List<MGModule> list, Property.ValueChangeListener listener) {

        final BeanContainer<String, MGModule> container =
                new BeanContainer<>(MGModule.class);
        container.setBeanIdProperty(MGModule.CODE);
        container.addAll(list);

        ComboBox select = new ComboBox(caption);
        select.setNullSelectionAllowed(true);
        select.setContainerDataSource(container);
        select.setItemCaptionPropertyId(MGModule.CONCAT);

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
//        select.setStyleName("designs-combo-addons");
        select.setContainerDataSource(container);
//        select.setItemIconPropertyId(AccessoryAddon.IMAGE_RESOURCE);
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

    private ComboBox getSimpleItemFilledCombo(String caption, String dataType, Property.ValueChangeListener listener) {
        List<LookupItem> list = proposalDataProvider.getLookupItems(dataType);
        return getSimpleItemFilledCombo(caption, list, listener);
    }

    private Component buildproduct1()
    {
        FormLayout formLayout = new FormLayout();
        formLayout.setSizeFull();
        formLayout.addStyleName("no-exposedBottom-margin");
        formLayout.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        handleTypefield =getHandleType();
        binder.bind(handleTypefield, Module.HANDLE_TYPE);
        formLayout.addComponent(handleTypefield);

        knobTypefield=getKnobType();
        binder.bind(knobTypefield, Module.KNOB_TYPE);
        formLayout.addComponent(knobTypefield);

        return formLayout;
    }
    private Component buildproduct2()
    {
        FormLayout formLayout = new FormLayout();
        formLayout.setSizeFull();
        formLayout.addStyleName("no-exposedBottom-margin");
        formLayout.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        handlefield = gethandlefinish();
        binder.bind(handlefield, Module.HANDLE_FINISH);
        formLayout.addComponent(handlefield);

        knobfield= getknobfinish();
        binder.bind(knobfield, Module.KNOB_FINISH);
        formLayout.addComponent(knobfield);

        return formLayout;
    }
    private Component buildproduct3()
    {
        FormLayout formLayout = new FormLayout();
        formLayout.setSizeFull();
        formLayout.addStyleName("no-exposedBottom-margin");
        formLayout.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        thicknessfield=gethandlethickness();
        binder.bind(thicknessfield, Module.HANDLE_THICKNESS);
        formLayout.addComponent(thicknessfield);

        knobthicknessfield=getknobthickness();
        binder.bind(knobthicknessfield, Module.KNOB_THICKNESS);
        formLayout.addComponent(knobthicknessfield);

        return formLayout;
    }

    private  Component buildproduct4()
    {
        FormLayout formLayout = new FormLayout();
        formLayout.setSizeFull();
        formLayout.addStyleName("no-exposedBottom-margin");
        formLayout.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);


        handleImage = new Image("", new FileResource(new File(basePath + module.getImagePath())));
        handleImage.setCaption(null);
        handleImage.setHeight("50%");
        handleImage.setSizeFull();
        handleImage.setImmediate(true);
        formLayout.addComponent(handleImage);

        knobImage = new Image("", new FileResource(new File(basePath + module.getImagePath())));
        knobImage.setCaption(null);
        knobImage.setHeight("50%");
        knobImage.setSizeFull();
        knobImage.setImmediate(true);
        formLayout.addComponent(knobImage);

        return formLayout;
    }

    private ComboBox getHandleType()
    {
        handleType=new ComboBox("Handle Type");
        handleType.setNullSelectionAllowed(false);
        handleType.setItemCaptionPropertyId(HandleMaster.TITLE);
        handleType.addValueChangeListener(this::handletypechanged);
        return handleType;
    }
    private void handletypechanged(Property.ValueChangeEvent valueChangeEvent)
    {
        String title=valueChangeEvent.getProperty().getValue().toString();
        LOG.info("value change in handle type " +valueChangeEvent.getProperty().getValue());
        handlefinish=proposalDataProvider.getHandleFinish(title,"Handle");
        final BeanContainer<String, HandleMaster> container = new BeanContainer<>(HandleMaster.class);
        container.setBeanIdProperty(HandleMaster.FINISH);
        container.addAll(handlefinish);
        handle.setContainerDataSource(container);
    }
    private void knobtypechanged(Property.ValueChangeEvent valueChangeEvent)
    {
        String title=valueChangeEvent.getProperty().getValue().toString();
        LOG.info("knob type changed" +valueChangeEvent.getProperty().getValue());
        knobfinsh=proposalDataProvider.getHandleFinish(title,"knob");
        final BeanContainer<String, HandleMaster> container = new BeanContainer<>(HandleMaster.class);
        container.setBeanIdProperty(HandleMaster.FINISH);
        container.addAll(knobfinsh);
        knob.setContainerDataSource(container);
    }
    private void  handlefinishchanged(Property.ValueChangeEvent valueChangeEvent)
    {
        String title=valueChangeEvent.getProperty().getValue().toString();
        LOG.info("handle finish change" +valueChangeEvent.getProperty().getValue() + "type " +handleType.getValue().toString());
        handlethickness=proposalDataProvider.getHandleThickness(handleType.getValue().toString(),title,"Handle");
        final BeanContainer<String, HandleMaster> container = new BeanContainer<>(HandleMaster.class);
        container.setBeanIdProperty(HandleMaster.THICKNESS);
        container.addAll(handlethickness);
        thickness.setContainerDataSource(container);
    }
    private void knobfinishchanged(Property.ValueChangeEvent valueChangeEvent)
    {
        String title=valueChangeEvent.getProperty().getValue().toString();
        LOG.info("handle finish change" +valueChangeEvent.getProperty().getValue());
        Knobthickness=proposalDataProvider.getHandleThickness(knobType.getValue().toString(),title,"Knob");
        final BeanContainer<String, HandleMaster> container = new BeanContainer<>(HandleMaster.class);
        container.setBeanIdProperty(HandleMaster.THICKNESS);
        container.addAll(Knobthickness);
        knobthickness.setContainerDataSource(container);
    }

    private ComboBox gethandlefinish()
    {
        handle=new ComboBox("Handle Finish");
        handle.setNullSelectionAllowed(false);
        handle.setItemCaptionPropertyId(HandleMaster.FINISH);
        handle.addValueChangeListener(this::handlefinishchanged);
        return handle;
    }
    private ComboBox getknobfinish() {
        knob = new ComboBox("Knob Finish");
        knob.setNullSelectionAllowed(false);
        knob.setItemCaptionPropertyId(HandleMaster.FINISH);
        knob.addValueChangeListener(this::knobfinishchanged);
        return knob;
    }
    private ComboBox gethandlethickness()
    {
        thickness=new ComboBox("Handle Size");
        thickness.setNullSelectionAllowed(false);
        thickness.setItemCaptionPropertyId(HandleMaster.THICKNESS);
        return thickness;
    }
    private ComboBox getknobthickness()
    {
        knobthickness=new ComboBox("Knob Thickness");
        knobthickness.setNullSelectionAllowed(false);
        knobthickness.setItemCaptionPropertyId(HandleMaster.THICKNESS);
        return knobthickness;
    }
    private ComboBox getKnobType()
    {
        List<HandleMaster> handle=proposalDataProvider.getHandleTitle("knob");
        for(HandleMaster handles:handle)
        {
            LOG.info("handle title" +handles);
        }

        final BeanContainer<String, HandleMaster> container = new BeanContainer<>(HandleMaster.class);
        container.setBeanIdProperty(HandleMaster.TITLE);
        container.addAll(handle);

        knobType=new ComboBox("Knob Type");
        knobType.setNullSelectionAllowed(false);
        knobType.setContainerDataSource(container);
        knobType.setItemCaptionPropertyId(HandleMaster.TITLE);
        knobType.addValueChangeListener(this::knobtypechanged);
        return knobType;
    }


}

