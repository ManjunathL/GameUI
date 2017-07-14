package com.mygubbi.game.dashboard.view.proposals;


import com.mygubbi.game.dashboard.ServerManager;
import com.mygubbi.game.dashboard.config.ConfigHolder;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.domain.*;
import com.mygubbi.game.dashboard.domain.JsonPojo.AccessoryDetails;
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
import com.vaadin.server.*;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.Runo;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("serial")
public class MDW extends Window {

    private static final Logger LOG = LogManager.getLogger(ModuleDetailsWindow.class);
    private final Product product;
    private final String DEF_CODE_PREFIX = "def_";
    private static final String LABEL_WARNING = "warning";
    private final int moduleIndex;
    String defaultValueForWardrobe="224";

    private Label accessoryHeading;

    private TextField importedModule;
    private TextField description;
    private TextField width;
    private TextField depth;
    private TextField height;
    private TextField remarks;

    private ComboBox moduleCategory;
    private ComboBox moduleSelection;
    private ComboBox carcassMaterialSelection;
    private ComboBox colorCombo;

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

    private ComboBox thicknessfield;
    private TextField handlequantity;
    private TextField knobqquantity;
    private ComboBox thickness;
    private ComboBox knobthickness;
    private TextField customText;
    OptionGroup single;
    private String hPresent;
    private String knobPresent;

    private MDW(Module module, Product product, int moduleIndex, ProposalVersion proposalVersion, ProposalHeader proposalHeader) {
        this.dontCalculatePriceNow = true;
        this.product = product;
        this.module = module;
        this.proposalVersion = proposalVersion;
        this.moduleIndex = moduleIndex;
        this.proposalHeader = proposalHeader;
        initModule();
        this.binder.setItemDataSource(this.module);

        if(this.module.getProductCategory().equals("K"))
        {
            this.module.setProductCategory("Kitchen");
        }
        if(this.module.getProductCategory().equals("W"))
        {
            this.module.setProductCategory("Wardrobe");
        }

        setModal(true);
        setSizeFull();
        removeCloseShortcut(ShortcutAction.KeyCode.ESCAPE);
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
        //horizontalLayoutupper.setMargin(new MarginInfo(false, false, false, false));
        Responsive.makeResponsive(this);
        horizontalLayoutupper.setHeight("90%");

        HorizontalLayout horizontalLayout1 = new HorizontalLayout();
        horizontalLayout1.setWidth("100%");
        horizontalLayout1.setHeight("70%");

        Component componentUpper1 = buildModuleComponent();
        componentUpper1.setWidth("100%");
        horizontalLayout1.addComponent(componentUpper1);
        horizontalLayout1.setSpacing(true);

        Component componentUpper2 = buildModuleOptionsComponent();
        componentUpper2.setWidth("100%");
        horizontalLayout1.addComponent(componentUpper2);
        horizontalLayout1.setSpacing(true);

        horizontalLayoutupper.addComponent(horizontalLayout1);
        horizontalLayoutupper.setExpandRatio(horizontalLayout1, 0.85f);

        horizontalLayoutupper.setWidth("90%");
        Component componentUpper3 = buildModuleImageComponent();
        componentUpper3.setWidth("90%");
        horizontalLayoutupper.addComponent(componentUpper3);
        horizontalLayoutupper.setExpandRatio(componentUpper3, 0.10f);

        HorizontalLayout hhorizontalLayout = new HorizontalLayout();
        hhorizontalLayout.addStyleName("hlayoutsize");
        hhorizontalLayout.setSizeFull();
        hhorizontalLayout.addComponent(custombuild());
        verticalLayout1.addComponent(hhorizontalLayout);

        HorizontalLayout horizontalLayout9 = new HorizontalLayout();
        horizontalLayout9.setSizeFull();
        horizontalLayout9.addComponent(getDimensionsPanel());
        horizontalLayout9.addComponent(getExposedPanelsLayout());
        verticalLayout1.addComponent(horizontalLayout9);

        defaultsOverridden = new Label("Note that the defaults have been overridden.");
        defaultsOverridden.setStyleName(LABEL_WARNING);
        defaultsOverridden.setVisible(false);

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
        handlepackage();
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
        //remarks.setReadOnly(true);
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
        applyButton.setVisible(false);
        applyNextButton.setVisible(false);
        if(Objects.equals(proposalHeader.getBeforeProductionSpecification(), "yes"))
        {
            handlequantity.setReadOnly(true);
            knobqquantity.setReadOnly(true);
            customText.setReadOnly(true);
            thicknessfield.setReadOnly(true);
            single.setReadOnly(true);
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
        panel.setHeight("200px");
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        verticalLayout.setMargin(new MarginInfo(false, true, true, true));
        verticalLayout.setSpacing(false);
        moduleImage = new Image("", new FileResource(new File(basePath + module.getImagePath())));
        moduleImage.setCaption(null);
        moduleImage.setSizeFull();
        moduleImage.setImmediate(true);
        verticalLayout.addComponent(moduleImage);
        panel.setContent(verticalLayout);
        return panel;
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

    private Component buildModuleComponent()
    {
        VerticalLayout verticalLayoutModule = new VerticalLayout();
        verticalLayoutModule.setSizeFull();
        FormLayout formLayout = new FormLayout();
        formLayout.setWidth("70%");
        formLayout.setHeight("30%");
        formLayout.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        formLayout.addStyleName("no-bottom-margin-normal");
        formLayout.addStyleName("v-formlayout");

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
               // LOG.debug("After combo box updated:" + module.getProductCategory() + " | module category :" + module.getModuleCategory());

                mgModules = proposalDataProvider.getModules(module.getProductCategory(), module.getModuleCategory());
                if (this.moduleSelection != null) {
                    this.moduleSelection.getContainerDataSource().removeAllItems();
                    ((BeanContainer<String, MGModule>) this.moduleSelection.getContainerDataSource()).addAll(mgModules);
                    moduleSelection.setValue(moduleSelection.getItemIds().iterator().next());
                }
                if(module.getModuleCategory().equals("S - Hinged Wardrobe 2100") ||module.getModuleCategory().equals("S - Hinged Wardrobe 2400") || module.getModuleCategory().equals("S - Sliding Wardrobe 2100") ||module.getModuleCategory().equals("S - Sliding Wardrobe 2400") )
                {
                    //LOG.info("handle thickness changed ");
                    module.setHandleThickness(defaultValueForWardrobe);
                    thicknessfield.setValue(defaultValueForWardrobe);
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
       else
       {
           this.importedModule = (TextField) binder.buildAndBind("Module", Module.MG_MODULE_CODE);
           this.importedModule.setReadOnly(true);
           formLayout.addComponent(importedModule);

       }

        if(Objects.equals(proposalHeader.getBeforeProductionSpecification(), "yes"))
        {
            thicknessfield=gethandlethickness();
            binder.bind(thicknessfield, Module.HANDLE_THICKNESS);
            thicknessfield.setRequired(false);
            /*if(thicknessfield.size()>0)
            {
                String code = StringUtils.isNotEmpty(module.getHandleThickness()) ? module.getHandleThickness() : (String) thicknessfield.getItemIds().iterator().next();
                thicknessfield.setValue(code);
            }*/
            thicknessfield.addValueChangeListener(this::thicknessfieldchanged);
            formLayout.addComponent(thicknessfield);

            handlequantity=new TextField("Handle Quantity");
            binder.bind(handlequantity,Module.HANDLE_QUANTITY);
            handlequantity.setRequired(false);
            handlequantity.addValueChangeListener(this::handlequantitychanged);
            formLayout.addComponent(handlequantity);

            knobqquantity=new TextField("Knob Quantity");
            binder.bind(knobqquantity,Module.KNOB_QUANTITY);
            knobqquantity.setRequired(false);
            knobqquantity.addValueChangeListener(this::knobquantitychanged);
            formLayout.addComponent(knobqquantity);
        }

        /*this.remarks = new TextField();
        this.remarks.setCaption("Remarks");
        formLayout.setWidth("100%");
        formLayout.setHeight("50%");
        formLayout.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        binder.bind(this.remarks, Module.REMARKS);
        formLayout.addComponent(remarks);*/

        verticalLayoutModule.addComponent(formLayout);
        return verticalLayoutModule;
    }
    private Component custombuild()
    {
        HorizontalLayout vlayout=new HorizontalLayout();

        single = new OptionGroup("");
        single.addItems("General Remarks", "Custom Remarks");
        binder.bind(single,Module.CUSTOM_CHECK);
        if (module.getCustomCheck()== null)
        {
            single.select("General Remarks");
            single.setImmediate(true);
        }
        single.addStyleName("checkboxstyle");
        single.addValueChangeListener(this::customcheckchanged);
        vlayout.addComponent(single);

        this.customText=new TextField();
        customText.addStyleName("text-area-size1");
        //binder.bind(this.customText,Module.CUSTOM_TEXT);
        binder.bind(this.customText, Module.REMARKS);
        customText.setNullRepresentation(" ");
//        this.customText.setEnabled(false);

        vlayout.addComponent(customText);
        return vlayout;
    }
    private void customcheckchanged(Property.ValueChangeEvent valueChangeEvent)
    {
        module.setCustomCheck(valueChangeEvent.getProperty().getValue().toString());
        if(valueChangeEvent.getProperty().getValue()=="Custom Remarks")
        {
            customText.setValue(" ");
        }
    }
    private void moduleSelectionChangedEvent(Property.ValueChangeEvent valueChangeEvent) {

        this.dontCalculatePriceNow = true;

        String code = (String) valueChangeEvent.getProperty().getValue();
        MGModule mgModule = ((BeanContainer<String, MGModule>) this.moduleSelection.getContainerDataSource()).getItem(code).getBean();
        //((ComboBox) ((Field.ValueChangeEvent) valueChangeEvent).getSource()).getContainerDataSource().getItem(code);

       // LOG.debug("MG module - " + mgModule.toString());

        LOG.debug("Module Type :" + mgModule.getModuleType());

        /*switch (module.getModuleType()) {
            case "S":
                LOG.debug("Inside S" + mgModule.toString());
                module.setImagePath(mgModule.getImagePath());
                module.setDescription(mgModule.getDescription());
                module.setRemarks(mgModule.getDescription());
                break;
            case "N":
                LOG.debug("Inside N" + mgModule.toString());
                module.setImagePath("image.jpg");
                module.setDescription("");
                module.setRemarks("");
                // module.setHeight(0);
                // module.setDepth(0);
                // module.setWidth(0);
                break;
            case "hike":
                LOG.debug("Inside Hike" + mgModule.toString());
                module.setDescription("");
                module.setRemarks("");
                break;
        }*/

        if (module.getModuleCategory().startsWith("S"))
        {
            LOG.debug("Inside S :" + mgModule.toString());

            module.setImagePath(mgModule.getImagePath());
            module.setDescription(mgModule.getDescription());
            module.setRemarks(mgModule.getDescription());
        }
        else if (module.getModuleCategory().startsWith("N"))
        {
            LOG.debug("Inside N :" + mgModule.toString());

            module.setImagePath("image.jpg");
            module.setDescription("");
            module.setRemarks("");
        }
        else
        {
            module.setDescription("");
            module.setRemarks("");
        }

        moduleImage.setSource(new FileResource(new File(basePath + module.getImagePath())));

        module.setMgCode(mgModule.getCode());
        module.setHeight(mgModule.getHeight());
        module.setDepth(mgModule.getDepth());
        module.setWidth(mgModule.getWidth());
        module.clearAcessorryPacks();
        this.removeAddons();
        //this.removeHandleAndKnobQuantity();
        this.emptyAccessoryImages();
        module.setModuleType(mgModule.getModuleType());
        module.setModuleCategory(mgModule.getModuleCategory());
        module.setAccessoryPackDefault(mgModule.getAccessoryPackDefault());

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

        /*this.remarks.setReadOnly(false);
        this.remarks.setValue(description);*/

        /*this.customText.setReadOnly(false);
        this.customText.setValue(description);*/

        module.setImportStatus(Module.ImportStatusType.m.name());
        module.setUnitType(mgModule.getUnitType());

        this.allowDimensionChangesForModule();
       // LOG.debug("Module b4 price calc :" + module.toString());
        this.dontCalculatePriceNow = false;

        if(Objects.equals(proposalHeader.getBeforeProductionSpecification(), "yes")) {
            //LOG.info("product handletype selection " +product.getHandleTypeSelection());
            //LOG.info("product.getHandleTypeSelection().equals(\"Normal\")" +!product.getHandleTypeSelection().equals("Normal"));
            if(!(product.getHandleTypeSelection().equals("Normal")))
            {
                handlequantity.setValue("0");
                knobqquantity.setValue("0");
            }
            else {
                this.customText.setReadOnly(false);
                this.customText.setValue(description);

                List<AccessoryDetails> accDetailsforHandle = proposalDataProvider.getAccessoryhandleDetails(module.getMgCode(), "HL");
                if (accDetailsforHandle.size() == 0) {
                    handlequantity.setValue("0");
                    handlequantity.setRequired(false);
                    thicknessfield.setRequired(false);
                } else {
                    for (AccessoryDetails a : accDetailsforHandle) {
                        //LOG.info("handle quantity " + a);
                        module.setHandleQuantity(Integer.valueOf(a.getQty()));
                        handlequantity.setValue(a.getQty());
                    }
                }

                List<AccessoryDetails> accDetailsforKnob = proposalDataProvider.getAccessoryhandleDetails(module.getMgCode(), "K");
                if (accDetailsforKnob.size() == 0) {
                    knobqquantity.setValue("0");
                    knobqquantity.setRequired(false);
                } else {
                    for (AccessoryDetails a : accDetailsforKnob) {
                        module.setKnobQuantity(Integer.valueOf(a.getQty()));
                        knobqquantity.setValue(a.getQty());
                    }
                }
            }




                //LOG.info("mg code"  +module.getMgCode());
                List<MGModule> handlePresent = proposalDataProvider.retrieveModuleDetails(module.getMgCode());
                for (MGModule m : handlePresent) {
                    //LOG.info("module mand " + m.toString());
                    if (m.getHandleMandatory().equals("Yes")) {
                        module.setHandlePresent(m.getHandleMandatory());
                        hPresent = m.getHandleMandatory();
                        handlequantity.setRequired(true);
                        thicknessfield.setRequired(true);
                    } else {
                        module.setHandlePresent(m.getHandleMandatory());
                        handlequantity.setRequired(false);
                        thicknessfield.setRequired(false);
                    }
                    if (m.getKnobMandatory().equals("Yes")) {
                        module.setKnobPresent(m.getKnobMandatory());
                        knobPresent = m.getKnobMandatory();
                        knobqquantity.setRequired(true);
                    } else {
                        module.setKnobPresent(m.getKnobMandatory());
                    }
                }
                //hinges
//              List<MGModule> hingesPresent=proposalDataProvider.retrieveModuleDetails(module.getMgCode());
                List<ModuleHingeMap> hingeMaps1 = proposalDataProvider.getHinges(module.getMgCode(), product.getHinge());
                //LOG.info("size of hinge" + hingeMaps1.size());
                module.setHingePack(hingeMaps1);
                for (MGModule m : handlePresent) {
                    if (m.getHingeMandatory().equals("Yes")) {
                        module.setHingePresent(m.getHingeMandatory());
                        List<ModuleHingeMap> hingeMaps = proposalDataProvider.getHinges(module.getMgCode(), product.getHinge());
                        module.setHingePack(hingeMaps);
                    }
                }


            List<HandleMaster> handleMasters=proposalDataProvider.getHandles("Handle",module.getHandleType(),module.getHandleFinish(),thicknessfield.getValue().toString());
            for(HandleMaster h:handleMasters)
            {
                module.setHandleCode(h.getCode());
            }

            List<HandleMaster> knobmaster=proposalDataProvider.getHandles("knob",module.getKnobType(),module.getKnobFinish(),"0");
            for(HandleMaster h:knobmaster)
            {
                module.setKnobCode(h.getCode());
            }
        }
        refreshPrice();
        refreshAccPacks();
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

    private void refreshAccPacks()
    {
        accessoryPackList = proposalDataProvider.getAccessoryPacks(module.getMgCode());
        this.accessoryPack1.getContainerDataSource().removeAllItems();
        ((BeanContainer<String, AccessoryPack>) this.accessoryPack1.getContainerDataSource()).addAll(accessoryPackList);

        this.accessoryPack2.getContainerDataSource().removeAllItems();
        ((BeanContainer<String, AccessoryPack>) this.accessoryPack2.getContainerDataSource()).addAll(accessoryPackList);

        this.accessoryPack3.getContainerDataSource().removeAllItems();
        ((BeanContainer<String, AccessoryPack>) this.accessoryPack3.getContainerDataSource()).addAll(accessoryPackList);

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
       // LOG.info("image path : " + sourceFile);
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

       // LOG.info("module price in Module Details Window " +modulePrice);
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
        moduleForPrice.setProduct(product);

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

       // LOG.info("Asking for module price - " + this.module.toString());
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
        } catch (Exception e) {
            return 0;
        }
    }

    private List<ModuleAccessoryPack> getModuleAccessoryPacks() {
        List<ModuleAccessoryPack> accPacks = new ArrayList<>();
        ModuleAccessoryPack moduleAccessoryPack = this.getModuleAccessoryPack(this.accessoryPack1, this.addons11, this.addons12, this.addons13);
        if (moduleAccessoryPack != null)
        {
            accPacks.add(moduleAccessoryPack);
        }
        else
        {
            accPacks.clear();
        }

        moduleAccessoryPack = this.getModuleAccessoryPack(this.accessoryPack2, this.addons21, this.addons22, this.addons23);
        if (moduleAccessoryPack != null) accPacks.add(moduleAccessoryPack);

        moduleAccessoryPack = this.getModuleAccessoryPack(this.accessoryPack3, this.addons31, this.addons32, this.addons33);
        if (moduleAccessoryPack != null) accPacks.add(moduleAccessoryPack);
        return accPacks;
    }

    private ModuleAccessoryPack getModuleAccessoryPack(ComboBox accPackCombo, ComboBox addon1Combo, ComboBox addons2Combo, ComboBox addons3Combo) {
        String value = (String) accPackCombo.getValue();

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

    private ClickListener  getApplyListener(boolean loadNext,boolean loadPrevious) {
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
            //LOG.info("thickness field value " +thicknessfield.getValue().toString());
            if(Objects.equals(proposalHeader.getBeforeProductionSpecification(), "yes"))
            {
                if(("Normal").equals(product.getHandleTypeSelection())) {
                    if(Objects.equals(module.getHandleThickness(),null) && Objects.equals(module.getHandlePresent(), "Yes"))
                    {

                        NotificationUtil.showNotification("Please select handle size before saving", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                        return;
                    }
                    if(Integer.parseInt(handlequantity.getValue())== 0 && Objects.equals(module.getHandlePresent(), "Yes"))
                    {
                        NotificationUtil.showNotification("Please enter valid quantity", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                        return;
                    }
                }
                if(Objects.equals(module.getCustomCheck(),"Custom Remarks") && Objects.equals(customText.getValue(), "") )
                {
                    NotificationUtil.showNotification("Custom Remarks cannot be empty", NotificationUtil.STYLE_BAR_ERROR_SMALL);
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

            List<ModuleAccessoryPack> accessoryPacks=getModuleAccessoryPacks();
           // LOG.info("Accessory pack" +accessoryPacks);
            //LOG.info("acc pack size" +accessoryPacks.size());
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
           // LOG.debug("this.calculatedArea:" + this.calculatedArea + " | this.calculatedAmountWOAccessories:" + this.calculatedAmountWOAccessories + " | WoodworkCost: " +this.woodworkCost+ " | HardwareCost: " +this.hardwareCost+ " | carcasscost " +this.carcassCost + " | Accessory Cost: " +this.accessoryCost+ " | Labour Cost: " +this.accessoryCost);
            if (module.getAmount() == 0)
            {
                NotificationUtil.showNotification("Module Price cannot be zero", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                return;
            }

            //LOG.debug("Module being updated:" + this.module.toString());
            finishTypeSelection.removeValueChangeListener(this::finishTypeChanged);
            close();
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
        Window w = new MDW(clonedModule, product, moduleIndex, proposalVersion, proposalHeader);
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

    private ComboBox gethandlethickness()
    {
       // LOG.debug("Handle Thickness test : " + module.getHandleType() + ":" + module.getHandleFinish());
        List<HandleMaster> handlethickness=proposalDataProvider.getHandleThickness(module.getHandleType(),module.getHandleFinish(),"Handle");
        final BeanContainer<String, HandleMaster> container = new BeanContainer<>(HandleMaster.class);
        container.setBeanIdProperty(HandleMaster.THICKNESS);
        container.addAll(handlethickness);

        thickness=new ComboBox("Handle Size");
        thickness.setNullSelectionAllowed(false);
        thickness.setContainerDataSource(container);
        thickness.setItemCaptionPropertyId(HandleMaster.THICKNESS);
        return thickness;
    }
    private void checkcustomcheck(Property.ValueChangeEvent valueChangeEvent)
    {
        if(valueChangeEvent.getProperty().getValue().equals(true))
        {
            customText.setEnabled(true);
        }else {
            customText.setEnabled(false);
        }
    }
    private void thicknessfieldchanged(Property.ValueChangeEvent valueChangeEvent)
    {
        module.setHandleThickness(valueChangeEvent.getProperty().getValue().toString());
        module.setHandleOverrideFlag("Yes");
        List<HandleMaster> handleMasters=proposalDataProvider.getHandles("Handle",module.getHandleType(),module.getHandleFinish(),thicknessfield.getValue().toString());
        for(HandleMaster h:handleMasters)
        {
            module.setHandleCode(h.getCode());
        }

       // LOG.info("handle code " +module.getHandleCode() + "knob code " +module.getKnobCode());
        List<HandleMaster> handleMasters1=proposalDataProvider.getHandleArray(module.getHandleCode());
        module.setHandlePack(handleMasters1);

        if(module.getKnobCode()!=null)
        {
            List<HandleMaster> knobmaster1=proposalDataProvider.getHandleArray(module.getKnobCode());
            module.setKnobPack(knobmaster1);
        }
        refreshPrice();
    }

    private void handlequantitychanged(Property.ValueChangeEvent valueChangeEvent)
    {
        //LOG.info("handle quantity changed");
        String s = valueChangeEvent.getProperty().getValue().toString();
        Integer integer = Integer.parseInt(s);
        if(!(product.getHandleTypeSelection().equals("Normal")))
        {
            this.handlequantity.setValue("0");
            module.setHandleQuantity(0);

        }else {
            this.handlequantity.setValue(s);
            module.setHandleQuantity(integer);
        }

        refreshPrice();
    }
    private void knobquantitychanged(Property.ValueChangeEvent valueChangeEvent)
    {
        List<HandleMaster> knobmaster=proposalDataProvider.getHandles("knob",module.getKnobType(),module.getKnobFinish(),"0");
        for(HandleMaster h:knobmaster)
        {
            module.setKnobCode(h.getCode());
        }

        String s = valueChangeEvent.getProperty().getValue().toString();
        Integer integer = Integer.parseInt(s);
        if(!(product.getHandleTypeSelection().equals("Normal")))
        {
            this.knobqquantity.setValue("0");
            module.setKnobQuantity(0);
        }
        else {

            this.knobqquantity.setValue(s);
            module.setKnobQuantity(integer);
        }

       // LOG.info("knob quantity  " +valueChangeEvent);
        refreshPrice();
    }
    private void removeHandleAndKnobQuantity()
    {
        handlequantity.setValue("");
        knobqquantity.setValue("");
    }
    private void handlepackage()
    {
        if(Objects.equals(proposalHeader.getPackageFlag(), "Yes"))
        {
            moduleCategory.setReadOnly(true);
            moduleSelection.setReadOnly(true);
            carcassMaterialSelection.setReadOnly(true);
            finishTypeSelection.setReadOnly(true);
            shutterFinishSelection.setReadOnly(true);

            //remarks.setReadOnly(true);
            customText.setReadOnly(true);
            height.setReadOnly(true);
            width.setReadOnly(true);
            depth.setReadOnly(true);
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
            if(Objects.equals(proposalHeader.getBeforeProductionSpecification(), "yes"))
            {
                single.setReadOnly(true);
                handlequantity.setReadOnly(true);
                knobqquantity.setReadOnly(true);
                thicknessfield.setReadOnly(true);
                thickness.setReadOnly(true);
            }

        }

    }
}

