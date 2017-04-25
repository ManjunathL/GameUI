package com.mygubbi.game.dashboard.view.proposals;


import com.google.common.eventbus.Subscribe;
import com.mygubbi.game.dashboard.ServerManager;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.domain.*;
import com.mygubbi.game.dashboard.domain.JsonPojo.AccessoryDetails;
import com.mygubbi.game.dashboard.domain.JsonPojo.LookupItem;
import com.mygubbi.game.dashboard.domain.JsonPojo.ShutterDesign;
import com.mygubbi.game.dashboard.domain.Module.ImportStatusType;
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
import com.vaadin.server.*;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.renderers.ClickableRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.gridutil.renderer.EditDeleteButtonValueRenderer;
import org.vaadin.gridutil.renderer.ViewEditDeleteButtonValueRenderer;

import java.io.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

import static com.mygubbi.game.dashboard.domain.Product.*;
import static java.lang.StrictMath.round;

@SuppressWarnings("serial")
public class CustomizedProductDetailsWindow extends Window {

    private static final Logger LOG = LogManager.getLogger(CustomizedProductDetailsWindow.class);
    private static final String CLOSE = "Close";
    private static final String CANCEL = "Cancel";

    private ProductAndAddonSelection productAndAddonSelection;
    private ProposalHeader proposalHeader;

    private TextField itemTitleField;
    private TextField roomText;
    private ComboBox productSelection;
    private ComboBox baseCarcassSelection;
    private ComboBox wallCarcassSelection;
    private ComboBox shutterFinishSelection;
    private ComboBox shutterDesign;
    private ComboBox finishTypeSelection;
    private TextField manualSeq;

    private Upload quoteUploadCtrl;
    private File uploadedQuoteFile;
    private TabSheet tabSheet;

    private Button addModules;
    private Button closeBtn;
    private Button addToproductLibraryBtn;
    private Button saveBtn;
    private Proposal proposal;

    private Product product;
    private Module module;
    private final BeanFieldGroup<Product> binder = new BeanFieldGroup<>(Product.class);
    private ProposalDataProvider proposalDataProvider = ServerManager.getInstance().getProposalDataProvider();
    private List<Finish> shutterFinishMasterList;
    private BeanItemContainer<Module> moduleContainer;
    private Grid modulesGrid;
    private TextField totalAmount;
    private TextField areaInSft;
    private TextField costWithoutAccessories;
    private FileAttachmentComponent fileAttachmentComponent;
    private BeanItemContainer<AddonProduct> addonsContainer;
    private Grid addonsGrid;
    private ArrayList<Module> modulesCopy;
    private ArrayList<AddonProduct> addonsCopy;
    private boolean deleteNotRequired;
    private Button addonAddButton;
    private static Set<CustomizedProductDetailsWindow> previousInstances = new HashSet<>();
    private ProposalVersion proposalVersion;

    String codeForProductWOTax;
    String codeForStdManfCost;
    String codeForNStdManfCost;
    String codeForManfLabourCost;
    String codeForAddonWOTax;
    String codeForAddonSourcePrice;

    double rateForProductWOTax;
    double rateForStdManfCost;
    double rateForNStdManfCost;
    double rateForManfLabourCost;
    double rateForAddonWOTax;
    double rateForAddonSourcePrice;
    private java.sql.Date priceDate;
    private String city;

    public CustomizedProductDetailsWindow(Proposal proposal, Product product, ProposalVersion proposalVersion, ProposalHeader proposalHeader) {
        this.proposal = proposal;
        this.product = product;
        this.proposalVersion = proposalVersion;
        this.proposalHeader = proposalHeader;
        this.priceDate = proposalHeader.getPriceDate();
        this.city = proposalHeader.getPcity();
        if (this.priceDate == null)
        {
            this.priceDate = new Date(System.currentTimeMillis());
        }

        this.cloneModules();
        this.cloneAddons();

        DashboardEventBus.register(this);
        this.binder.setItemDataSource(this.product);
        setModal(true);
        setSizeFull();
        setResizable(false);
        setClosable(false);
        setCaption("Add Kitchen/Wardrobe");

        VerticalLayout vLayout = new VerticalLayout();
        vLayout.setSizeFull();
        vLayout.addStyleName("v-vertical-customized-product-details");
        vLayout.setMargin(new MarginInfo(true, true, true, true));
        setContent(vLayout);
        Responsive.makeResponsive(this);

        HorizontalLayout horizontalLayout0 = new HorizontalLayout();
        horizontalLayout0.setSizeFull();
        horizontalLayout0.addComponent(buildAddItemBasicFormLeft());
        horizontalLayout0.setSpacing(true);
        horizontalLayout0.addComponent(buildAddItemBasicFormLayoutMiddle());
        horizontalLayout0.setSpacing(true);
        horizontalLayout0.addComponent(buildAddItemBasicFormLayoutRight());
        vLayout.addComponent(horizontalLayout0);
        horizontalLayout0.setHeightUndefined();
        vLayout.setExpandRatio(horizontalLayout0, 0.35f);

        HorizontalLayout horizontalLayout1 = new HorizontalLayout();
        horizontalLayout1.setSizeFull();
        horizontalLayout1.setStyleName("product-details-grid-tabs");

        tabSheet = new TabSheet();
        tabSheet.setSizeFull();
        tabSheet.addTab(buildModulesGrid(), "Modules");
        /*tabSheet.addTab(buildAddonsForm(), "Addons");*/

        fileAttachmentComponent = new FileAttachmentComponent(product, proposal.getProposalHeader().getFolderPath(),
                attachmentData -> proposalDataProvider.addProductDoc(product.getId(), product.getProposalId(), attachmentData.getFileAttachment()),
                attachmentData -> proposalDataProvider.removeProductDoc(attachmentData.getFileAttachment().getId()),
                !proposal.getProposalHeader().getStatus().equals(ProposalVersion.ProposalStage.Draft.name()));

        if (product.getModules().isEmpty()) {
            fileAttachmentComponent.getFileUploadCtrl().setEnabled(false);
        }

/*
        tabSheet.addTab(fileAttachmentComponent, "Attachments");
*/
        tabSheet.setEnabled(true);
        horizontalLayout1.addComponent(tabSheet);
        horizontalLayout1.setHeightUndefined();
        vLayout.addComponent(horizontalLayout1);
        vLayout.setExpandRatio(horizontalLayout1, 0.57f);

        Component footerLayOut = buildFooter();
        vLayout.addComponent(footerLayOut);
        vLayout.setExpandRatio(footerLayOut, 0.08f);

        handleState();
        updatePsftCosts();
        updateTotalAmount();

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
        if (productSelection.size() > 0)
        {
            String code = StringUtils.isNotEmpty(product.getProductCategoryCode()) ? product.getProductCategoryCode() : (String) productSelection.getItemIds().iterator().next();
            productSelection.setValue(code);
        }
        formLayoutLeft.addComponent(this.productSelection);

        itemTitleField = (TextField) binder.buildAndBind("Title", TITLE);
        itemTitleField.setRequired(true);
        itemTitleField.setNullRepresentation("");
        formLayoutLeft.addComponent(itemTitleField);


        this.shutterDesign = getShutterDesignCombo();
        shutterDesign.setRequired(true);
        binder.bind(shutterDesign, SHUTTER_DESIGN_CODE);
        if (shutterDesign.size() > 0) {
            String code = StringUtils.isNotEmpty(product.getShutterDesignCode()) ? product.getShutterDesignCode() : (String) shutterDesign.getItemIds().iterator().next();
            shutterDesign.setValue(code);
        }
        formLayoutLeft.addComponent(this.shutterDesign);

        areaInSft = new TextField("Area in sft : ");
        areaInSft.setValue("0");
        areaInSft.setImmediate(true);
        areaInSft.setReadOnly(true);
        formLayoutLeft.setSpacing(true);
        formLayoutLeft.addComponent(areaInSft);

        costWithoutAccessories = new TextField("Cost w/o Accessories : ");
        costWithoutAccessories.setValue("0");
        costWithoutAccessories.setImmediate(true);
        costWithoutAccessories.setReadOnly(true);
        formLayoutLeft.setSpacing(true);
        formLayoutLeft.addComponent(costWithoutAccessories);

       /* manualSeq = (TextField) binder.buildAndBind("Manual Sequence", MANUAL_SEQ);
        manualSeq.setRequired(true);
        manualSeq.setNullSettingAllowed(false);
        manualSeq.addValueChangeListener(this :: checkForDuplicateSeq);
        formLayoutLeft.addComponent(manualSeq);*/

        return formLayoutLeft;
    }

    private void checkForDuplicateSeq(Property.ValueChangeEvent valueChangeEvent) {
        Boolean value=checkForDuplicatefunction();
        if(value==false)
        {
            manualSeq.setValue("0");
        }
    }
    private boolean checkForDuplicatefunction()
    {
        LOG.info("@@@");
        String manualSeqStr = manualSeq.getValue();
        int manualSeq = Integer.parseInt(manualSeqStr);
        List <Product> getManualSeq =  proposalDataProvider.getProposalProductManualSeq(proposalHeader.getId(),proposalVersion.getVersion());
        for(Product product : getManualSeq) {
            if (manualSeq == product.getManualSeq())
            {
                NotificationUtil.showNotification("Product Sequence already exists!", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                return false;
            }

        }
        return true;
    }
    public void updatePsftCosts() {

        List<Module> modules = (List<Module>) binder.getItemDataSource().getItemProperty("modules").getValue();


        Double totalCostWOAccessories = 0.0;
        Double totalSalesPrice =0.0;
        Double NonStandardWoodworkCost=0.0;
        Double hikeCost=0.0;
        Double StandardWoodworkCost=0.0;
        Double hardwareCost=0.0;
        Double carcassCost=0.0;
        Double accessoryCost=0.0;
        Double labourCost=0.0;
        Double totalSalesPriceWOtax =0.0;
        Double stdModuleManufacturingCost =0.0;
        Double nonStdModuleManufacturingCost =0.0;
        Double manufacturingLabourCost =0.0;
        Double manufacturingHardwareCost =0.0;
        Double manufacturingAccessoryCost =0.0;
        Double manufacturingAccessoryCostForZgeneric=0.0;
        Double manufacturingTotalSalesPrice =0.0;
        Double manufacturingProfit =0.0;
        Double marginCompute=0.0;
        Double FinalmanufacturingAccoryCost=0.0;
        double totalModuleArea = 0;

        List<RateCard> rateCard=proposalDataProvider.getFactorRateCodeDetails("F:PRODWOTAX");
        for (RateCard productwotaxcode : rateCard) {
            codeForProductWOTax=productwotaxcode.getCode();
        }

        List<RateCard> manfstdcostlist=proposalDataProvider.getFactorRateCodeDetails("F:STDMC");
        for (RateCard manfstdcode :manfstdcostlist ) {
            codeForStdManfCost=manfstdcode.getCode();
        }

        List<RateCard> manfnstdcostlist=proposalDataProvider.getFactorRateCodeDetails("F:NSTDMC");
        for (RateCard manfnstdcode :manfnstdcostlist ) {
            codeForNStdManfCost=manfnstdcode.getCode();
        }

        List<RateCard> labourcostlist=proposalDataProvider.getFactorRateCodeDetails("F:LC");
        for (RateCard labourcostcode : labourcostlist ) {
            codeForManfLabourCost=labourcostcode.getCode();
        }

        List<RateCard> Addonwotaxlist=proposalDataProvider.getFactorRateCodeDetails("F:ADWOTAX");
        for (RateCard addonWOcode : Addonwotaxlist ) {
            codeForAddonWOTax=addonWOcode.getCode();
        }

        List<RateCard> Addonsourcepricelist=proposalDataProvider.getFactorRateCodeDetails("F:CASP");
        for (RateCard addonsourceprice : Addonsourcepricelist ) {
            codeForAddonSourcePrice=addonsourceprice.getCode();
        }

        PriceMaster productWOtaxpriceMaster=proposalDataProvider.getFactorRatePriceDetails(codeForProductWOTax,this.priceDate,this.city);
        rateForProductWOTax=productWOtaxpriceMaster.getSourcePrice();
        PriceMaster stdmanfcostpriceMaster=proposalDataProvider.getFactorRatePriceDetails(codeForStdManfCost,this.priceDate,this.city);
        rateForStdManfCost=stdmanfcostpriceMaster.getSourcePrice();
        PriceMaster nstdmanfcostpriceMaster=proposalDataProvider.getFactorRatePriceDetails(codeForNStdManfCost,this.priceDate,this.city);
        rateForNStdManfCost=nstdmanfcostpriceMaster.getSourcePrice();
        PriceMaster labourcostpriceMaster=proposalDataProvider.getFactorRatePriceDetails(codeForManfLabourCost,this.priceDate,this.city);
        rateForManfLabourCost=labourcostpriceMaster.getSourcePrice();
        PriceMaster addonwotaxpriceMaster=proposalDataProvider.getFactorRatePriceDetails(codeForAddonWOTax,this.priceDate,this.city);
        rateForAddonWOTax=addonwotaxpriceMaster.getSourcePrice();
        PriceMaster addonsourcepricepriceMaster=proposalDataProvider.getFactorRatePriceDetails(codeForAddonSourcePrice,this.priceDate,this.city);
        rateForAddonSourcePrice=addonsourcepricepriceMaster.getSourcePrice();

        for (Module module : modules)
        {
            LOG.info("module " +module);
            totalCostWOAccessories += module.getAmountWOAccessories();
            totalModuleArea += module.getArea();
            if (module.getMgCode().startsWith("MG-NS"))
            {
                if(!module.getMgCode().equals("MG-NS-H-001"))
                {
                    NonStandardWoodworkCost+=module.getCarcassCost()+module.getShutterCost();
                }
                else
                {
                    hikeCost+=module.getWoodworkCost();
                }
            }
            else
            {
                StandardWoodworkCost+=module.getCarcassCost()+module.getShutterCost();
            }
            hardwareCost+=module.getHardwareCost();
            carcassCost+=module.getCarcassCost();
            accessoryCost+=module.getAccessoryCost();
            if(!module.getMgCode().equals("MG-NS-H-001"))
            {
                labourCost+=module.getLabourCost();
            }

            List<ModuleAccessoryPack> moduleaccpack=module.getAccessoryPacks();
            for(ModuleAccessoryPack moduleAccessoryPack:moduleaccpack)
            {
                List<String> acccode=moduleAccessoryPack.getAccessories();
                for(String ZgenericAccessory: acccode)
                {
                    PriceMaster accessoryRateMaster=proposalDataProvider.getAccessoryRateDetails(ZgenericAccessory,this.priceDate,this.city);
                    {
                        manufacturingAccessoryCost+=accessoryRateMaster.getSourcePrice();
                    }
                }

                List <AccessoryDetails>  accesoryHardwareMasters =proposalDataProvider.getAccessoryDetails(moduleAccessoryPack.getCode());
                for(AccessoryDetails acc: accesoryHardwareMasters)
                {
                    PriceMaster accessoryRateMaster=proposalDataProvider.getAccessoryRateDetails(acc.getCode(),this.priceDate,this.city);
                    {
                        manufacturingAccessoryCostForZgeneric+=accessoryRateMaster.getSourcePrice();
                    }
                    PriceMaster hardwareRateMaster=proposalDataProvider.getHardwareRateDetails(acc.getCode(),this.priceDate,this.city);
                    {
                        {
                            manufacturingHardwareCost += hardwareRateMaster.getSourcePrice();
                        }
                    }
                }

            }

            List<ModuleComponent> modulehardwaredetails=proposalDataProvider.getModuleAccessoryhwDetails(module.getMgCode());
            for(ModuleComponent acchwdetails: modulehardwaredetails)
            {
                PriceMaster hardwareRateMaster=proposalDataProvider.getHardwareRateDetails(acchwdetails.getCompcode(),this.priceDate,this.city);
                {
                    {
                        manufacturingHardwareCost += hardwareRateMaster.getSourcePrice() * acchwdetails.getQuantity();
                    }
                }
            }

            totalSalesPrice =NonStandardWoodworkCost+StandardWoodworkCost+hardwareCost+labourCost+accessoryCost;
            totalSalesPriceWOtax = totalSalesPrice *rateForProductWOTax;
            stdModuleManufacturingCost =StandardWoodworkCost/rateForStdManfCost;
            nonStdModuleManufacturingCost =NonStandardWoodworkCost/rateForNStdManfCost;
            manufacturingLabourCost =labourCost/rateForManfLabourCost;
            FinalmanufacturingAccoryCost = manufacturingAccessoryCost + manufacturingAccessoryCostForZgeneric;
            manufacturingTotalSalesPrice = stdModuleManufacturingCost + nonStdModuleManufacturingCost + manufacturingLabourCost + manufacturingHardwareCost + FinalmanufacturingAccoryCost;

            manufacturingProfit = totalSalesPriceWOtax - manufacturingTotalSalesPrice;
            marginCompute=(manufacturingProfit / totalSalesPriceWOtax)*100;
        }
        product.setCostWoAccessories(round(totalCostWOAccessories));
        product.setProfit(round(manufacturingProfit));
        product.setMargin(round(marginCompute));
        product.setAmountWoTax(round(totalSalesPriceWOtax));
        product.setManufactureAmount(round(manufacturingTotalSalesPrice));

        LOG.info("Non std WoodCost " +NonStandardWoodworkCost);
        LOG.info("Std WoodCost" +StandardWoodworkCost);
        LOG.info("Accessory Cost" +accessoryCost);
        LOG.info("Hardware Cost" +hardwareCost);
        LOG.info("Labour Cost" +labourCost);
        LOG.info("hike cost" +hikeCost);
        LOG.info("Total Sales Price" +totalSalesPrice);
        LOG.info("TSP WO Tax" +totalSalesPriceWOtax);

        LOG.info("Manf std" +stdModuleManufacturingCost);
        LOG.info("manf NONStd " +nonStdModuleManufacturingCost);
        LOG.info("mnf Accessory price" +FinalmanufacturingAccoryCost);
        LOG.info("mnf Hardware price" +manufacturingHardwareCost);
        LOG.info("mnf Labour price" +manufacturingLabourCost);
        LOG.info("Mnf Total price" +manufacturingTotalSalesPrice);

        Double cwa=product.getCostWoAccessories();
        if (totalModuleArea != 0) {
            areaInSft.setReadOnly(false);
            areaInSft.setValue(round(totalModuleArea) + " sft");
            areaInSft.setReadOnly(true);
            costWithoutAccessories.setReadOnly(false);
            costWithoutAccessories.setValue(round(totalCostWOAccessories) + "");
            costWithoutAccessories.setReadOnly(true);
        }
    }

    private void refreshPrice(Property.ValueChangeEvent valueChangeEvent) {
        List<Module> modules = product.getModules();



        List<Module> boundModules = (List<Module>) binder.getItemDataSource().getItemProperty("modules").getValue();

        Component component = valueChangeEvent == null ? null : ((Field.ValueChangeEvent) valueChangeEvent).getComponent();
        this.noPricingErrors();

        for (Module module : modules) {

            LOG.debug("Inside For loop :");
            if (component == baseCarcassSelection &&
                     (module.getUnitType().toLowerCase().contains(Module.UnitTypes.base.name()))) {
                String text = (String) moduleContainer.getItem(module).getItemProperty(Module.CARCASS_MATERIAL).getValue();
                if (text.contains(Module.DEFAULT)) {
                    moduleContainer.getItem(module).getItemProperty(Module.CARCASS_MATERIAL_CODE).setValue(baseCarcassSelection.getValue());
                    moduleContainer.getItem(module).getItemProperty(Module.CARCASS_MATERIAL).setValue(getDefaultText(getSelectedItemText(baseCarcassSelection)));
                }
            } else if (component == wallCarcassSelection && module.getUnitType().toLowerCase().contains(Module.UnitTypes.wall.name())) {
                String text = (String) moduleContainer.getItem(module).getItemProperty(Module.CARCASS_MATERIAL).getValue();
                if (text.contains(Module.DEFAULT)) {
                    moduleContainer.getItem(module).getItemProperty(Module.CARCASS_MATERIAL_CODE).setValue(wallCarcassSelection.getValue());
                    moduleContainer.getItem(module).getItemProperty(Module.CARCASS_MATERIAL).setValue(getDefaultText(getSelectedItemText(wallCarcassSelection)));
                }
            } else if (component == finishTypeSelection) {
                String text = (String) moduleContainer.getItem(module).getItemProperty(Module.FINISH_TYPE).getValue();
                if (text.contains(Module.DEFAULT)) {
                    moduleContainer.getItem(module).getItemProperty(Module.FINISH_TYPE_CODE).setValue(finishTypeSelection.getValue());
                    moduleContainer.getItem(module).getItemProperty(Module.FINISH_TYPE).setValue(getDefaultText(getSelectedItemText(finishTypeSelection)));
                }
            } else if (component == shutterFinishSelection) {
                String text = (String) moduleContainer.getItem(module).getItemProperty(Module.SHUTTER_FINISH).getValue();
                if (text.contains(Module.DEFAULT)) {
                    moduleContainer.getItem(module).getItemProperty(Module.SHUTTER_FINISH_CODE).setValue(shutterFinishSelection.getValue());
                    moduleContainer.getItem(module).getItemProperty(Module.SHUTTER_FINISH).setValue(getDefaultText(getSelectedFinishText(shutterFinishSelection)));
                }
            }

            if (StringUtils.isNotEmpty(module.getMgCode()))
            {
                String unitType = module.getUnitType();

                if ((unitType.toLowerCase().contains(Module.UnitTypes.base.name()) && component != wallCarcassSelection)
                        || (unitType.toLowerCase().contains(Module.UnitTypes.wall.name()) && component != baseCarcassSelection)){
                    LOG.debug("Inside the inner loop :");
                    double amount = 0;
                    double areainsft = 0;
                    double costwoaccessories = 0;

                    LOG.debug("before Module for price :" + module.toString());
                    try {
                        ModuleForPrice moduleForPrice = new ModuleForPrice();

                        if (proposalHeader.getPriceDate() == null) {
                            java.sql.Date date = new java.sql.Date(System.currentTimeMillis());
                            moduleForPrice.setPriceDate( date);
                        }
                        else
                        {
                            moduleForPrice.setPriceDate(proposalHeader.getPriceDate());
                        }

                        moduleForPrice.setCity(proposalHeader.getPcity());

                        moduleForPrice.setModule(module);
                        ModulePrice modulePrice = proposalDataProvider.getModulePrice(moduleForPrice);

                        amount = round(modulePrice.getTotalCost());
                        areainsft = modulePrice.getModuleArea();
                        costwoaccessories = round(modulePrice.getWoodworkCost());


                        boundModules.get(boundModules.indexOf(module)).setAmount(amount);
                        boundModules.get(boundModules.indexOf(module)).setAmountWOAccessories(costwoaccessories);
                        boundModules.get(boundModules.indexOf(module)).setArea(areainsft);
                        moduleContainer.getItem(module).getItemProperty(Module.AMOUNT).setValue(amount);
                    } catch (Property.ReadOnlyException e) {
                        LOG.debug(e.getMessage());
                        return;
                    }
                }
            }
        }
        updateTotalAmount();
        updatePsftCosts();
    }

    private void showPricingErrors()
    {
        disableSave();
        NotificationUtil.showNotification("Some modules could not be priced!", NotificationUtil.STYLE_BAR_ERROR_SMALL);
    }

    private void noPricingErrors()
    {
        enableSave();
    }

    private FormLayout buildAddItemBasicFormLayoutMiddle() {
        FormLayout formLayoutRight = new FormLayout();
        formLayoutRight.setSizeFull();
        formLayoutRight.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        this.baseCarcassSelection = getSimpleItemFilledCombo("Base Carcass", ProposalDataProvider.CARCASS_LOOKUP, null);
        baseCarcassSelection.setRequired(true);
        binder.bind(baseCarcassSelection, BASE_CARCASS_CODE);
        if (baseCarcassSelection.size() > 0) {
            String code = StringUtils.isNotEmpty(product.getBaseCarcassCode()) ? product.getBaseCarcassCode() : (String) baseCarcassSelection.getItemIds().iterator().next();
            baseCarcassSelection.setValue(code);
        }
        baseCarcassSelection.addValueChangeListener(this::refreshPrice);
        formLayoutRight.addComponent(this.baseCarcassSelection);

        this.wallCarcassSelection = getSimpleItemFilledCombo("Wall Carcass", ProposalDataProvider.CARCASS_LOOKUP, null);
        wallCarcassSelection.setRequired(true);
        binder.bind(wallCarcassSelection, WALL_CARCASS_CODE);
        if (wallCarcassSelection.size() > 0) {
            String code = StringUtils.isNotEmpty(product.getWallCarcassCode()) ? product.getWallCarcassCode() : (String) wallCarcassSelection.getItemIds().iterator().next();
            wallCarcassSelection.setValue(code);
        }



        wallCarcassSelection.addValueChangeListener(this::refreshPrice);
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
        this.shutterFinishSelection.getContainerDataSource().removeAllItems();
        ((BeanContainer<String, Finish>) this.shutterFinishSelection.getContainerDataSource()).addAll(filteredShutterFinish);
        if (shutterFinishSelection.size() > 0) {
            String code = StringUtils.isNotEmpty(product.getFinishCode()) ? product.getFinishCode() : (String) shutterFinishSelection.getItemIds().iterator().next();
            shutterFinishSelection.setValue(code);
        }
        shutterFinishSelection.addValueChangeListener(this::refreshPrice);
        formLayoutRight.addComponent(this.shutterFinishSelection);

        totalAmount = new TextField("<h2>Total Amount:</h2>");
        totalAmount.setValue("0");
        totalAmount.setImmediate(true);
        totalAmount.addStyleName("amount-text-customized-product-details");
        binder.bind(totalAmount, AMOUNT);
        totalAmount.setReadOnly(true);
        totalAmount.setCaptionAsHtml(true);

        formLayoutRight.setSpacing(true);
        formLayoutRight.addComponent(totalAmount);

        return formLayoutRight;
    }

    private VerticalLayout buildAddItemBasicFormLayoutRight() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeUndefined();
        verticalLayout.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        String role = ((User) VaadinSession.getCurrent().getAttribute(User.class.getName())).getRole();

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSizeFull();

        if ((("admin").equals(role) || ("planning").equals(role)) && proposalVersion.getVersion().startsWith("2."))
        {
            Button soExtractButton = new Button("SO Extract&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            soExtractButton.setCaptionAsHtml(true);
            soExtractButton.setIcon(FontAwesome.DOWNLOAD);
            soExtractButton.setStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
            soExtractButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
            soExtractButton.addStyleName(ValoTheme.BUTTON_SMALL);
            soExtractButton.setWidth("120px");

            FileDownloader soExtractDownloader = this.getSOExtractFileDownloader();
            soExtractDownloader.extend(soExtractButton);
            horizontalLayout.addComponent(soExtractButton);
//            footer.setComponentAlignment(soExtractButton, Alignment.TOP_RIGHT);

            Button jobcardButton = new Button("Job Card&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            jobcardButton.setCaptionAsHtml(true);
            jobcardButton.setIcon(FontAwesome.DOWNLOAD);
            jobcardButton.addStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
            jobcardButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
            jobcardButton.addStyleName(ValoTheme.BUTTON_SMALL);
            jobcardButton.setWidth("100px");

            StreamResource jobcardResource = createJobcardResource();
            FileDownloader jobcardDownloader = new FileDownloader(jobcardResource);
            jobcardDownloader.extend(jobcardButton);
            horizontalLayout.addComponent(jobcardButton);
//            footer.setComponentAlignment(jobcardButton, Alignment.TOP_RIGHT);

           /* Button marginButton = new Button("Margin&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            marginButton.setCaptionAsHtml(true);
            marginButton.setIcon(FontAwesome.DOWNLOAD);
            marginButton.addStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
            marginButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
            marginButton.addStyleName(ValoTheme.BUTTON_SMALL);
            soExtractButton.setWidth("100px");


            StreamResource marginSheetResource = createMarginSheetResource();
            FileDownloader marginSheetDownloader = new FileDownloader(marginSheetResource);
            marginSheetDownloader.extend(marginButton);
            footer.addComponent(marginButton);
            footer.setComponentAlignment(marginButton, Alignment.TOP_RIGHT);*/
        }



//        HorizontalLayout horizontalLayout = new HorizontalLayout();
//        horizontalLayout.setWidth("100%");
//        horizontalLayout.setHeight("100%");

        verticalLayout.addComponent(horizontalLayout);
        verticalLayout.addComponent(getQuoteUploadControl());

        verticalLayout.setSpacing(true);

        this.addModules = new Button("Add Modules");
        addModules.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                    if (!commitValues()) return;
                    Module module = new Module();
                    product.setType(TYPES.CUSTOMIZED.name());
                    if (product.getSource() == null) {
                        product.setSource("GAME");
                    }
                    module.setModuleType("N");
                    module.setProductCategory(product.getProductCategoryCode());
                    module.setModuleSource("button");
                    module.setExposedLeft(false);
                    module.setExposedRight(false);
                    module.setExposedBack(false);
                    module.setExposedBottom(false);
                    module.setExposedTop(false);
                    module.setExposedOpen(false);
                    module.setUnitType("Base Unit");
                    module.setCarcass(getDefaultText(
                            (module.getUnitType().toLowerCase().contains(Module.UnitTypes.wall.name())
                                    ? getSelectedItemText(wallCarcassSelection)
                                    : getSelectedItemText(baseCarcassSelection))));
                    module.setFinishType(getDefaultText(getSelectedItemText(finishTypeSelection)));
                    module.setFinish(getDefaultText(getSelectedFinishText(shutterFinishSelection)));
                    module.setCarcassCodeBasedOnUnitType(product);
                    module.setFinishTypeCode(product.getFinishTypeCode());
                    module.setFinishCode(product.getFinishCode());
                    ModuleDetailsWindow.open(module, product, 0, proposalVersion, proposalHeader);
                }


        });

        verticalLayout.addComponent(addModules);

//        formLayoutRight.addComponent(horizontalLayout);

        return verticalLayout;
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

        List<Module> modules = product.getModules();

        for (Module module : modules) {
            String text = (String) moduleContainer.getItem(module).getItemProperty(Module.FINISH_TYPE).getValue();
            if (text.contains(Module.DEFAULT)) {
                moduleContainer.getItem(module).getItemProperty(Module.FINISH_TYPE_CODE).setValue(finishTypeSelection.getValue());
                moduleContainer.getItem(module).getItemProperty(Module.FINISH_TYPE).setValue(getDefaultText(getSelectedItemText(finishTypeSelection)));
            }
        }
    }

    private Component getQuoteUploadControl() {
        this.quoteUploadCtrl = new Upload("Import Quotation Sheet", (filename, mimeType) -> {
            LOG.debug("Received upload - " + filename);

            if (!commitValues()) return null;

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
            product.setSource("KDMax");
            Product productResult = proposalDataProvider.loadAndUpdateProduct(product);
            product.setId(productResult.getId());
            product.setModules(productResult.getModules());
            product.setType(TYPES.CUSTOMIZED.name());
            product.setQuoteFilePath(quoteFilePath);
            initModules(product);
            moduleContainer.removeAllItems();
            moduleContainer.addAll(product.getModules());
            modulesGrid.clearSortOrder();
            modulesGrid.setContainerDataSource(createGeneratedModulePropertyContainer());
            fileAttachmentComponent.getFileUploadCtrl().setEnabled(true);
            refreshPrice(null);

            if (product.hasImportErrorStatus()) {
                NotificationUtil.showNotification("Error in mapping module(s), please upload new sheet!", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                disableSave();
            } else {
                enableSave();
            }
            LOG.debug("Successfully uploaded - " + filename);
        });
        return quoteUploadCtrl;
    }

    private boolean commitValues() {
        try {
            binder.commit();
        } catch (FieldGroup.CommitException e) {
            NotificationUtil.showNotification("Please fill all mandatory fields before upload!", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            return false;
        }
        return true;
    }

    private void initModules(Product product) {
        for (Module module : product.getModules()) {

            module.setCarcass(getDefaultText(
                    (module.getUnitType().toLowerCase().contains(Module.UnitTypes.wall.name())
                            ? getSelectedItemText(wallCarcassSelection)
                            : getSelectedItemText(baseCarcassSelection))));
            module.setFinishType(getDefaultText(getSelectedItemText(finishTypeSelection)));
            module.setFinish(getDefaultText(getSelectedFinishText(shutterFinishSelection)));
            module.setCarcassCodeBasedOnUnitType(product);
            module.setFinishTypeCode(product.getFinishTypeCode());
            module.setFinishCode(product.getFinishCode());

        }
    }

    private String getDefaultText(String selectedItemText) {
        return Module.DEFAULT + " (" + selectedItemText + ")";
    }

    private String getSelectedFinishText(ComboBox shutterFinishSelection) {
        return ((BeanContainer<String, Finish>) shutterFinishSelection.getContainerDataSource()).getItem(shutterFinishSelection.getValue()).getBean().getTitle();
    }

    private String getSelectedItemText(ComboBox combo) {
        return ((BeanContainer<String, LookupItem>) combo.getContainerDataSource()).getItem(combo.getValue()).getBean().getTitle();
    }

    private void enableSave() {
        saveBtn.setEnabled(true);
    }

    private void disableSave() {
        //saveBtn.setEnabled(false);
    }

    public void checkAndEnableSave()
    {
        List<Module> modules = (List<Module>) binder.getItemDataSource().getItemProperty("modules").getValue();
        for (Module module : modules)
        {
            if (module.getAmount() == 0)
            {
                this.disableSave();
                return;
            }
        }
        this.enableSave();
    }

    private String getUploadBasePath() {
        return proposal.getProposalHeader().getFolderPath();
    }

    private Component buildModulesGrid() {
        HorizontalLayout hLayout = new HorizontalLayout();
        hLayout.setSizeFull();

        moduleContainer = new BeanItemContainer<>(Module.class);
        GeneratedPropertyContainer genContainer = createGeneratedModulePropertyContainer();

        modulesGrid = new Grid(genContainer);
        modulesGrid.setSelectionMode(Grid.SelectionMode.NONE);
        modulesGrid.setStyleName("modules-grid");
        modulesGrid.setSizeFull();
        modulesGrid.setResponsive(true);
        modulesGrid.setColumnReorderingAllowed(true);
        modulesGrid.setColumns(Module.IMPORT_STATUS,Module.MODULE_SEQUENCE, Module.UNIT_TYPE, Module.MG_MODULE_CODE,Module.REMARKS ,Module.CARCASS_MATERIAL, Module.FINISH_TYPE, Module.SHUTTER_FINISH, Module.COLOR_CODE, Module.AMOUNT,Module.ACCESSORY_FLAG, "action");

        modulesGrid.setCellStyleGenerator(cell -> {
            if (cell.getPropertyId().equals(Module.CARCASS_MATERIAL)
                    || cell.getPropertyId().equals(Module.FINISH_TYPE)
                    || cell.getPropertyId().equals(Module.SHUTTER_FINISH)) {
                if (cell.getValue() != null && !((String) cell.getValue()).contains(Module.DEFAULT)) {
                    return "module-cell-highlight";
                }
            }
            return "";
        });
        List<Grid.Column> columns = modulesGrid.getColumns();
        int idx = 0;
        Grid.Column statusColumn = columns.get(idx++);
        statusColumn.setHeaderCaption("");
        statusColumn.setRenderer(new HtmlRenderer(), getResourceConverter());
        columns.get(idx++).setHeaderCaption("Seq #");
        columns.get(idx++).setHeaderCaption("Unit Type");
        columns.get(idx++).setHeaderCaption("Module");
        Grid.Column remarksColumn = columns.get(idx++);
        remarksColumn.setHeaderCaption("Remarks");
        columns.get(idx++).setHeaderCaption("Carcass Material");
        columns.get(idx++).setHeaderCaption("Finish Material");
        columns.get(idx++).setHeaderCaption("Shutter Finish");
        columns.get(idx++).setHeaderCaption("Color");
        columns.get(idx++).setHeaderCaption("Amount");
        columns.get(idx++).setHeaderCaption("Accessory Flag");
        Grid.Column actionColumn = columns.get(idx++);
        actionColumn.setHeaderCaption("Actions");


        actionColumn.setRenderer(new ViewEditDeleteButtonValueRenderer(new ViewEditDeleteButtonValueRenderer.ViewEditDeleteButtonClickListener() {

            @Override
            public void onView(ClickableRenderer.RendererClickEvent rendererClickEvent) {
               //addStyleName("copy-button");
                if (!binder.isValid()) {
                    NotificationUtil.showNotification("Please fill all mandatory fields before proceeding!", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                    return;
                }

                if (("Published").equals(proposalVersion.getInternalStatus()) || ("Confirmed").equals(proposalVersion.getInternalStatus()) || ("Locked").equals(proposalVersion.getInternalStatus()) || ("DSO").equals(proposalVersion.getInternalStatus()) || ("PSO").equals(proposalVersion.getInternalStatus()))
                {
                    Notification.show("Cannot copy on Published, Confirmed amd Locked versions");
                    return;
                }

                Module m = (Module) rendererClickEvent.getItemId();
                LOG.info("modules:"+ m);
                List<Module> copy = product.getModules();
                int length = (copy.size()) + 1;
                Module copyModule = new Module();
                copyModule.setUnitType(m.getUnitType());
                copyModule.setExtCode(m.getExtCode());
                copyModule.setExtText(m.getExtText());
                copyModule.setMgCode(m.getMgCode()) ;
                copyModule.setCarcass(m.getCarcass());
                copyModule.setCarcassCode(m.getCarcassCode());
                copyModule.setFixedCarcassCode(m.getFixedCarcassCode());
                copyModule.setFinishType(m.getFinishType());
                copyModule.setFinishTypeCode(m.getFinishTypeCode());
                copyModule.setFinish(m.getFinish());
                copyModule.setFinishCode(m.getFinishCode());
                copyModule.setColorCode(m.getColorCode());
                copyModule.setColorName(m.getColorName());
                copyModule.setColorImagePath(m.getColorImagePath());
                copyModule.setAmount(m.getAmount());
                copyModule.setRemarks(m.getRemarks());
                copyModule.setImportStatus(m.getImportStatus());
                copyModule.setDescription(m.getDescription());
                copyModule.setDimension(m.getDimension());
                copyModule.setImagePath(m.getImagePath());
                copyModule.setExposedRight(m.getExposedRight());
                copyModule.setExposedLeft(m.getExposedLeft());
                copyModule.setExposedTop(m.getExposedTop());
                copyModule.setExposedBottom(m.getExposedBottom());
                copyModule.setExposedBack(m.getExposedBack());
                copyModule.setExposedOpen(m.getExposedOpen());
                copyModule.setArea(m.getArea());
                copyModule.setAmountWOAccessories(m.getAmountWOAccessories());
                copyModule.setWidth(m.getWidth());
                copyModule.setDepth(m.getDepth());
                copyModule.setHeight(m.getHeight());
                copyModule.setModuleCategory(m.getModuleCategory());
                copyModule.setModuleType(m.getModuleType());
                copyModule.setProductCategory(m.getProductCategory());
                copyModule.setModuleSource(m.getModuleSource());
                copyModule.setExpSides(m.getExpSides());
                copyModule.setExpBottom(m.getExpBottom());
                copyModule.setAccessoryPackDefault(m.getAccessoryPackDefault());
                copyModule.setAccessoryPacks(m.getAccessoryPacks());
                copyModule.setHardwareCost(m.getHardwareCost());
                copyModule.setWoodworkCost(m.getWoodworkCost());
                copyModule.setShutterCost(m.getShutterCost());
                copyModule.setCarcassCost(m.getCarcassCost());
                copyModule.setAccessoryCost(m.getAccessoryCost());
                copyModule.setLabourCost(m.getLabourCost());
                copyModule.setAccessoryflag(m.getAccessoryflag());

                DashboardEventBus.post(new ProposalEvent.ModuleUpdated(copyModule,false,false,product.getModules().size(),CustomizedProductDetailsWindow.this));
                DashboardEventBus.unregister(this);

                /*copy.add(copyModule);

                moduleContainer.addAll(copy);
                modulesGrid.setContainerDataSource(createGeneratedModulePropertyContainer());*/

                modulesGrid.sort(Module.MODULE_SEQUENCE,SortDirection.ASCENDING);
           /*     updateTotalAmount();
                updatePsftCosts();
                checkAndEnableSave();*/
            }


            @Override
            public void onEdit(ClickableRenderer.RendererClickEvent rendererClickEvent) {

                if (!binder.isValid()) {
                    NotificationUtil.showNotification("Please fill all mandatory fields before proceeding!", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                    return;
                }
                Module module = (Module) rendererClickEvent.getItemId();
                boolean unmappableModulePresent = isAnyModuleUnmappable();
                if (!module.getImportStatus().equals(ImportStatusType.n.name())) {
                    //if (!unmappableModulePresent) {
                    int index = modulesGrid.getContainerDataSource().indexOfId(module);
                    ModuleDetailsWindow.open(module, createProductFromUI(), index + 1,proposalVersion,proposalHeader);
                } else {
                    NotificationUtil.showNotification("Cannot proceed as this module is not setup.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                }
            }

            @Override
            public void onDelete(ClickableRenderer.RendererClickEvent rendererClickEvent) {

                if (("Published").equals(proposalVersion.getInternalStatus()) || ("Confirmed").equals(proposalVersion.getInternalStatus()) || ("Locked").equals(proposalVersion.getInternalStatus()) || ("DSO").equals(proposalVersion.getInternalStatus()) || ("PSO").equals(proposalVersion.getInternalStatus()))
                {
                    Notification.show("Cannot delete modules on Published, Confirmed amd Locked versions");
                    return;
                }

                    ConfirmDialog.show(UI.getCurrent(), "", "Are you sure you want to Delete this Module?",
                            "Yes", "No", dialog -> {
                                if (dialog.isConfirmed()) {
                                    Module module = (Module) rendererClickEvent.getItemId();
                                    List<Module> modules = product.getModules();
                                    modules.remove(module);
                                    moduleContainer.removeAllItems();

                                    int seq = module.getSeq();

                                    for (Module mgModule : modules) {
                                        if (mgModule.getSeq() > seq) {
                                            mgModule.setSeq(mgModule.getSeq() - 1);
                                        }
                                    }

                                    moduleContainer.addAll(modules);
                                    modulesGrid.setContainerDataSource(createGeneratedModulePropertyContainer());
                                    updateTotalAmount();
                                    updatePsftCosts();
                                    checkAndEnableSave();
                                }
                            });
                }

        }));

        hLayout.addComponent(modulesGrid);
        hLayout.setExpandRatio(modulesGrid, 1);

        if (!product.getModules().isEmpty()) {
            moduleContainer.addAll(product.getModules());
            modulesGrid.sort(Sort.by(Module.MODULE_SEQUENCE, SortDirection.ASCENDING));
        }

        return modulesGrid;
    }

    private boolean isAnyModuleUnmappable() {
        return product.getModules().stream().filter(module1 -> module1.getImportStatus().equals(ImportStatusType.n.name())).findAny().isPresent();
    }


    private GeneratedPropertyContainer createGeneratedModulePropertyContainer() {
        GeneratedPropertyContainer genContainer = new GeneratedPropertyContainer(moduleContainer);
        genContainer.addGeneratedProperty("action", getEmptyActionTextGenerator());

                return genContainer;
    }
    private Product createProductFromUI() {
        Product product = new Product();
        product.setFinishTypeCode((String) this.finishTypeSelection.getValue());
        product.setFinishCode((String) this.shutterFinishSelection.getValue());
        product.setWallCarcassCode((String) this.wallCarcassSelection.getValue());
        product.setBaseCarcassCode((String) this.baseCarcassSelection.getValue());
        product.setTitle(itemTitleField.getValue());
        product.setModules(this.product.getModules());
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
            AddonDetailsWindow.open(addonProduct, itemTitleField.getValue(), false,proposalVersion,this.proposalHeader);
        });

        verticalLayout.addComponent(addonAddButton);
        verticalLayout.setComponentAlignment(addonAddButton, Alignment.MIDDLE_RIGHT);

        addonsContainer = new BeanItemContainer<>(AddonProduct.class);

        GeneratedPropertyContainer genContainer = createGeneratedAddonsPropertyContainer();

        addonsGrid = new Grid(genContainer);
        addonsGrid.setResponsive(true);
        addonsGrid.setSizeFull();
        addonsGrid.setHeight("325px");
        addonsGrid.setColumnReorderingAllowed(true);
        addonsGrid.setColumns(AddonProduct.SEQ, AddonProduct.ADDON_CATEGORY, AddonProduct.PRODUCT_TYPE,AddonProduct.PRODUCT_SUBTYPE_CODE, AddonProduct.BRAND,
                AddonProduct.CATALOGUE_CODE, AddonProduct.UOM, AddonProduct.RATE, AddonProduct.QUANTITY, AddonProduct.AMOUNT, "actions");

        List<Grid.Column> columns = addonsGrid.getColumns();
        int idx = 0;
        columns.get(idx++).setHeaderCaption("#");
        columns.get(idx++).setHeaderCaption("Category");
        columns.get(idx++).setHeaderCaption("Product Type");
        columns.get(idx++).setHeaderCaption("Product Sub-Type");
        columns.get(idx++).setHeaderCaption("Brand");
        columns.get(idx++).setHeaderCaption("Product Name");
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
                AddonDetailsWindow.open(addon, itemTitleField.getValue(), false,proposalVersion,proposalHeader);
            }

            @Override
            public void onDelete(ClickableRenderer.RendererClickEvent rendererClickEvent) {

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
                                    updatePsftCosts();
                                }
                            });
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
        //footer.setWidth(100.0f, Unit.PERCENTAGE);

        HorizontalLayout right = new HorizontalLayout();
        right.setSpacing(true);

        String caption = null;
        boolean modulesInitiallyMapped = this.modulesCopy.stream().allMatch(module -> StringUtils.isNotEmpty(module.getMgCode()));

        deleteNotRequired = modulesInitiallyMapped && !this.modulesCopy.isEmpty();

        if (deleteNotRequired) {

            caption = CLOSE;
        } else {
            caption = CANCEL;
        }

        saveBtn = new Button("Save");
        saveBtn.addStyleName(ValoTheme.BUTTON_PRIMARY);
        saveBtn.addClickListener(event -> {
            try {
               /* if ("0".equals(manualSeq.getValue())) {
                    NotificationUtil.showNotification("Product Sequence cannot be 0", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                    return;
                } else {*/

                try {
                    binder.commit();
                } catch (FieldGroup.CommitException e) {
                    NotificationUtil.showNotification("Please fill all mandatory fields.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                    return;
                }
                LOG.debug("cwa :" + product.getCostWoAccessories());

                List<Module> modules = this.product.getModules();
                for (Module module : modules)
                {
                    if (("Yes").equals(module.getAccessoryPackDefault()))
                    {
                        if (module.getAccessoryPacks().size() == 0)
                        {
                            NotificationUtil.showNotification("Ensure accessory packs are chosen for appropriate modules",NotificationUtil.STYLE_BAR_ERROR_SMALL);
                            return;
                        }
                    }
                }

                List<Product> getAllVersionProducts = proposalDataProvider.getVersionProducts(proposalVersion.getProposalId(), proposalVersion.getVersion());
                proposal.setProducts(getAllVersionProducts);

                if (product.getSeq() == 0) {
                    int size = getAllVersionProducts.size();
                    size++;
                    product.setSeq(size);
                }

                boolean success = proposalDataProvider.updateProduct(product);

                if (success) {
                    double amountWoDiscount = 0;
                    double amountWoAccessories = 0;
                    double discountPercentage = this.proposalVersion.getDiscountPercentage();
                    List<Product> versionProducts = proposalDataProvider.getVersionProducts(proposalHeader.getId(), this.proposalVersion.getVersion());
                    for (Product product : versionProducts) {
                        LOG.debug("Product module :" + product.getAmount());
                        amountWoDiscount += product.getAmount();
                        amountWoAccessories += product.getCostWoAccessories();
                    }
                    this.proposalVersion.setAmount(amountWoDiscount);
                    double discountAmount = amountWoAccessories * (discountPercentage / 100);
                    this.proposalVersion.setDiscountAmount(discountAmount);
                    this.proposalVersion.setFinalAmount(amountWoDiscount - discountAmount);

                    LOG.debug("Proposal Version inside module :" + this.proposalVersion.toString());
                    proposalDataProvider.updateVersion(this.proposalVersion);
                    NotificationUtil.showNotification("Product details saved successfully", NotificationUtil.STYLE_BAR_SUCCESS_SMALL);
                    DashboardEventBus.post(new ProposalEvent.ProductCreatedOrUpdatedEvent(product));
                    DashboardEventBus.unregister(this);
                    LOG.info("windows in product " +UI.getCurrent().getWindows());
                    close();
                } else {
                    NotificationUtil.showNotification("Product save failed, please contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                }
            } catch(Exception e){
                Notification.show("Error while saving Item details",
                        Type.ERROR_MESSAGE);
            }

        });
        saveBtn.focus();
        saveBtn.setVisible(true);
        updatePsftCosts();
        right.addComponent(saveBtn);
        right.setComponentAlignment(saveBtn, Alignment.MIDDLE_RIGHT);

        closeBtn = new Button(caption);
        closeBtn.addStyleName(ValoTheme.BUTTON_PRIMARY);
        closeBtn.addClickListener((Button.ClickListener) clickEvent -> {

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
                                DashboardEventBus.unregister(this);
                                close();
                            }
                        });

        });
        closeBtn.focus();
        right.addComponent(closeBtn);
        right.setComponentAlignment(closeBtn, Alignment.MIDDLE_RIGHT);

        String role = ((User) VaadinSession.getCurrent().getAttribute(User.class.getName())).getRole();

        if (("admin").equals(role))
        {
            addToproductLibraryBtn=new Button("Add To Product Library");
            addToproductLibraryBtn.addStyleName(ValoTheme.BUTTON_PRIMARY);
            right.addComponent(addToproductLibraryBtn);
            right.setComponentAlignment(addToproductLibraryBtn, Alignment.MIDDLE_RIGHT);
            addToproductLibraryBtn.addClickListener(clickEvent -> {
                close();
                ProductLibraryInfo.open(product,proposal);
            });
        }

        footer.addComponent(right);
        footer.setComponentAlignment(right, Alignment.MIDDLE_CENTER);
        return footer;
    }

    private FileDownloader getSOExtractFileDownloader() {
        StreamResource soExtractResource =  createSalesOrderResource();

        FileDownloader downloader = new FileDownloader(soExtractResource) {
            @Override
            public boolean handleConnectorRequest(VaadinRequest request,
                                                  VaadinResponse response, String path) throws IOException {
                soExtractResource.setFilename(getSOFilename());
                return super.handleConnectorRequest(request, response, path);
            }
        };
        return downloader;
    }

    private String getSOFilename()
    {
        prepareProductAndAddonObject();

        if (this.productAndAddonSelection.getProductIds().size() == 1)
        {
            int productId = this.productAndAddonSelection.getProductIds().get(0);
            String productTitle = "NA";
            for (Product product : this.proposal.getProducts())
            {
                if (product.getId() == productId)
                {
                    productTitle = product.getTitle();
                    break;
                }
            }
            String cname = this.proposalHeader.getCname();
            String crmid = this.proposalHeader.getCrmId();
            LOG.debug("name : " + cname +" | " + "crm id : " + " | " + crmid + "product title" + productTitle );

            return "SO-" + this.proposalHeader.getCname() + "-" + this.proposalHeader.getCrmId() + "-" + productTitle + ".xlsx";
        }
        return "SO.xlsx";
    }

    private StreamResource createSalesOrderResource() {

        StreamResource.StreamSource source = () -> {

            prepareProductAndAddonObject();

            if (this.productAndAddonSelection.getProductIds().size() == 1) {
                String salesOrderFile = proposalDataProvider.getSalesOrderExtract(this.productAndAddonSelection);
                InputStream input = null;
                try {
                    input = new ByteArrayInputStream(FileUtils.readFileToByteArray(new File(salesOrderFile)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return input;
            } else {
                return null;
            }
        };
        return new StreamResource(source, "so-initial.xlsx");
    }


    private StreamResource createJobcardResource() {
        StreamResource.StreamSource source = () -> {

            prepareProductAndAddonObject();


            if (this.productAndAddonSelection.getProductIds().size() == 1) {
                String jobcardFile = proposalDataProvider.getJobCardFile(this.productAndAddonSelection);
                InputStream input = null;
                try {
                    input = new ByteArrayInputStream(FileUtils.readFileToByteArray(new File(jobcardFile)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return input;
            } else {
                return null;
            }
        };
        return new StreamResource(source, "JobCard.xlsx");
    }

    private StreamResource createMarginSheetResource() {
        StreamResource.StreamSource source = () -> {

            prepareProductAndAddonObject();

            if (this.productAndAddonSelection.getProductIds().size() == 1) {
                String jobcardFile = proposalDataProvider.getMarginSheet(this.productAndAddonSelection);
                InputStream input = null;
                try {
                    input = new ByteArrayInputStream(FileUtils.readFileToByteArray(new File(jobcardFile)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return input;
            } else {
                return null;
            }
        };
        return new StreamResource(source, "MarginSheet.xlsx");
    }

    private void prepareProductAndAddonObject() {
        this.productAndAddonSelection = new ProductAndAddonSelection();
        this.productAndAddonSelection.setProposalId(proposalVersion.getProposalId());
        this.productAndAddonSelection.getProductIds().add(product.getId());
        this.productAndAddonSelection.setFromVersion(proposalVersion.getVersion());
    }

    public static void closeWindow() {
/*
        DashboardEventBus.post(new DashboardEvent.CloseOpenWindowsEvent());
*/
        closeWindow();
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

    private ComboBox getShutterDesignCombo() {
        List<ShutterDesign> list = proposalDataProvider.getLookupItems(ProposalDataProvider.SHUTTER_DESIGN_LOOKUP)
                .stream().map(ShutterDesign::new).collect(Collectors.toList());
        final BeanContainer<String, ShutterDesign> container = new BeanContainer<>(ShutterDesign.class);
        container.setBeanIdProperty(ShutterDesign.CODE);
        container.addAll(list);

        ComboBox select = new ComboBox("Shutter Design");
        select.setNullSelectionAllowed(false);
        select.setWidth("250px");
        select.setStyleName("designs-combo");
        select.setContainerDataSource(container);
        select.setItemIconPropertyId(ShutterDesign.IMAGE_RESOURCE);
        select.setItemCaptionPropertyId(ShutterDesign.TITLE);
        if (container.size() > 0) select.setValue(select.getItemIds().iterator().next());
        return select;
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

        try {
            List<Module> modules = this.product.getModules();
            Module module = event.getModule();

            if (module.getModuleSequence() == 0)
            {
                module.setModuleSequence(this.getNextModuleSequence(modules));
            }
            else
            {
                modules.remove(module);
            }
            LOG.info("modseq"+module.getModuleSequence());

            modules.add(module);
            moduleContainer.removeAllItems();
            moduleContainer.addAll(modules);
            modulesGrid.setContainerDataSource(createGeneratedModulePropertyContainer());
            modulesGrid.sort(Module.MODULE_SEQUENCE,SortDirection.ASCENDING);
            updateTotalAmount();
            updatePsftCosts();

            if (event.isLoadNext()) {
                loadNextModule(event.getModuleIndex());
            }
            else if (event.isLoadPrevious())
            {
                loadPreviousModule(event.getModuleIndex());
            }

            this.checkAndEnableSave();
        } catch (Exception e) {
            LOG.debug(e.getMessage());
        }
    }

    private int getNextModuleSequence(List<Module> modules) {
        int seq = 0;
        for (Module module : modules)
        {
            if (seq < module.getModuleSequence())
            {
                seq = module.getModuleSequence();
            }
        }
        return seq + 1;
    }

    private int getNextUnitSequence(List<Module> modules, String unitType) {
        int seq = 0;
        for (Module module : modules)
        {
            if (module.getUnitType().equals(unitType) && seq < module.getSeq())
            {
                seq = module.getSeq();
            }
        }
        return seq + 1;
    }

    private void loadNextModule(int currentModuleIndex) {
        Module module = (Module) modulesGrid.getContainerDataSource().getIdByIndex(currentModuleIndex);
        ModuleDetailsWindow.open(module, createProductFromUI(),  currentModuleIndex + 1,proposalVersion,proposalHeader);
    }

    private void loadPreviousModule(int currentModuleIndex) {
        Module module = (Module) modulesGrid.getContainerDataSource().getIdByIndex(currentModuleIndex-2);
        int newModuleIndex = currentModuleIndex - 1;
        ModuleDetailsWindow.open(module, createProductFromUI(), newModuleIndex,proposalVersion,proposalHeader);
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

    public static void open(Proposal proposal, Product product, ProposalVersion proposalVersion, ProposalHeader proposalHeader) {
        CustomizedProductDetailsWindow w = new CustomizedProductDetailsWindow(proposal, product, proposalVersion, proposalHeader);

        UI.getCurrent().addWindow(w);
        w.focus();

    }

    private void handleState() {

            ProposalVersion.ProposalStage proposalStage = ProposalVersion.ProposalStage.valueOf(proposalVersion.getInternalStatus());
            LOG.debug("Proposal Stage :" + proposalStage);
            switch (proposalStage) {
                case Draft:
                    break;
                case Published:
                    setComponentsReadonly();
                    break;
                case Confirmed:
                    setComponentsReadonly();
                    break;
                case Locked:
                    setComponentsReadonly();
                    break;
                case DSO:
                    setComponentsReadonly();
                    break;
                case PSO:
                    setComponentsReadonly();
                    break;
                default:
                    throw new RuntimeException("Unknown State");
            }

    }

    private void setComponentsReadonly() {
        itemTitleField.setReadOnly(true);
        productSelection.setReadOnly(true);
        roomText.setReadOnly(true);
       // manualSeq.setReadOnly(true);
        shutterDesign.setReadOnly(true);
        baseCarcassSelection.setReadOnly(true);
        wallCarcassSelection.setReadOnly(true);
        finishTypeSelection.setReadOnly(true);
        shutterFinishSelection.setReadOnly(true);
        quoteUploadCtrl.setEnabled(false);
        closeBtn.setCaption(CLOSE);
        fileAttachmentComponent.getFileUploadCtrl().setEnabled(false);
        saveBtn.setEnabled(false);
        fileAttachmentComponent.setReadOnly(true);
        addModules.setEnabled(false);
    }



}