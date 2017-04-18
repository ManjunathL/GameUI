package com.mygubbi.game.dashboard.view.proposals;

import com.mygubbi.game.dashboard.ServerManager;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.domain.*;
import com.mygubbi.game.dashboard.domain.JsonPojo.AccessoryDetails;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.Responsive;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.List;

/**
 * Created by shruthi on 14-Feb-17.
 */
public class MarginDetailsWindow extends Window
{
    private ProposalDataProvider proposalDataProvider = ServerManager.getInstance().getProposalDataProvider();
    private static final Logger LOG = LogManager.getLogger(MarginDetailsWindow.class);
    private String status=null;
    private String checkstatus;
    ModuleForPrice moduleForPrice;

    Double discountPercentage;
    Double discountAmount;

    Button closeButton;
    Label actualSalesPrice;
    Label amt;
    Label actualSalesPriceWOtax;
    Label actualSalesMargin;
    Label actualProfit;
    Label profitPercentage;
    Label profitPercentageAmount;

    Label productsCost,addonsCost,totalPrice,hikeLabel;

    Label discountedSalesPrice;
    Label discountedSalesPriceWOtax;
    Label discountedSalesMargin;
    Label discountedProfit;

    Label manualInputSalesPrice;
    TextField manualInputDiscountAmount;
    TextField manualInputDiscountPercentage;
    Label manualInputSalesPriceWOtax;
    Label manualInputMargin;
    Label manualInputProfitPercentage;

    private final BeanFieldGroup<Product> binder = new BeanFieldGroup<>(Product.class);
    ProposalVersion proposalVersion;
    ProposalHeader proposalHeader;

    Double TotalCost = 0.0;
    Double NSWoodWorkCost=0.0;
    Double SWoodWorkCost=0.0;
    Double ShutterCost=0.0;
    Double CarcassCost=0.0;
    Double AccessoryCost=0.0;
    Double HardwareCost=0.0;
    Double LabourCost=0.0;
    Double hikeCost=0.0;

    Double productsTotal=0.0;
    Double totalWoAccessories = 0.0;
    Double addonsTotal=0.0;
    Double addonsTotalWOtax=0.0;
    Double addonsProfit=0.0;
    Double addonsMargin=0.0;
    Double totalAmount=0.0;
    Double costOfAccessories=0.0;
    Double totalSalesPrice =0.0;
    Double totalSalesPriceWOAcc =0.0;

    Double totalSalesPriceWOtax =0.0;

    Double ProductTotal=0.0;

    Double stdModuleManufacturingCost =0.0;
    Double nonStdModuleManufacturingCost =0.0;
    Double manufacturingLabourCost =0.0;
    Double manufacturingHardwareCost =0.0;
    Double manufacturingAccessoryCost =0.0;
    Double FinalmanufacturingAccoryCost=0.0;
    Double manufacturingAccessoryCostForZgeneric=0.0;
    Double manufacturingTotalSalesPrice =0.0;
    Double manufacturingProfit =0.0;
    Double marginCompute=0.0;
    int nonStdModuleCount =0;
    int standardModuleCount =0;
    Double addonDealerPrice=0.0;
    margin obj1=new margin();
    margin obj2=new margin();
    margin obj3=new margin();

    Label per;
    Label wodiscount,whatIf,withDiscount;
    OptionGroup checkProduct;
    private String city;
    private Date priceDate;

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

    //private ProposalDataProvider proposalDataProvider = ServerManager.getInstance().getProposalDataProvider();

    //private ProposalDataProvider proposalDataProvider = ServerManager.getInstance().getProposalDataProvider();

    public static void open(ProposalVersion proposalVersion, ProposalHeader proposalHeader)
    {
        MarginDetailsWindow w=new MarginDetailsWindow(proposalVersion,proposalHeader);
        UI.getCurrent().addWindow(w);
        w.focus();
    }
    public MarginDetailsWindow(ProposalVersion proposalVersion, ProposalHeader proposalHeader)
    {
        DashboardEventBus.register(this);
        this.proposalVersion=proposalVersion;
        this.proposalHeader = proposalHeader;
        this.priceDate = proposalHeader.getPriceDate();
        this.city = proposalHeader.getPcity();
        if (this.priceDate == null)
        {
            this.priceDate = new Date(System.currentTimeMillis());
        }
        setModal(true);
        removeCloseShortcut(ShortcutAction.KeyCode.ESCAPE);
        setWidth("75%");
        setClosable(false);
        setCaption("Margin Computation");

        updateTotal();

        VerticalLayout verticalLayout = new VerticalLayout();
        //verticalLayout.setMargin(new MarginInfo(true, true, true, true));
        Responsive.makeResponsive(this);

        HorizontalLayout mainhorizontalLayout= new HorizontalLayout();
        mainhorizontalLayout.setMargin(new MarginInfo(false, false, false, false));
        mainhorizontalLayout.setSizeFull();
        mainhorizontalLayout.addComponent(buildTsp());
        verticalLayout.addComponent(mainhorizontalLayout);
        verticalLayout.setComponentAlignment(mainhorizontalLayout,Alignment.TOP_CENTER);
        //mainhorizontalLayout.setHeightUndefined();

        HorizontalLayout checkboxlayout= new HorizontalLayout();
        checkboxlayout.setMargin(new MarginInfo(false, false, false, false));
        checkboxlayout.setSizeFull();
        checkboxlayout.addComponent(buildCheckBox());
        verticalLayout.addComponent(checkboxlayout);
        verticalLayout.setComponentAlignment(checkboxlayout,Alignment.TOP_CENTER);

        HorizontalLayout horizontalLayout2 = new HorizontalLayout();
        horizontalLayout2.setMargin(new MarginInfo(false, false, false, false));
        horizontalLayout2.setSizeFull();
        horizontalLayout2.addComponent(buildHeading());
        verticalLayout.addComponent(horizontalLayout2);
        verticalLayout.setComponentAlignment(horizontalLayout2,Alignment.TOP_CENTER);
        horizontalLayout2.setHeightUndefined();

        HorizontalLayout horizontalLayout1 = new HorizontalLayout();
        horizontalLayout2.setMargin(new MarginInfo(false, false, false, false));
        horizontalLayout2.setSizeFull();
        horizontalLayout2.addComponent(buildLayout1());
        verticalLayout.addComponent(horizontalLayout1);
        verticalLayout.setComponentAlignment(horizontalLayout1,Alignment.TOP_CENTER);
        horizontalLayout1.setHeightUndefined();

        HorizontalLayout horizontalLayout3 = new HorizontalLayout();
        horizontalLayout2.setMargin(new MarginInfo(false, false, false, false));
        horizontalLayout2.setSizeFull();
        horizontalLayout2.addComponent(buildLayout2());
        verticalLayout.addComponent(horizontalLayout3);
        verticalLayout.setComponentAlignment(horizontalLayout3,Alignment.TOP_CENTER);
        horizontalLayout2.setHeightUndefined();

        HorizontalLayout horizontalLayout4 = new HorizontalLayout();
        horizontalLayout2.setMargin(new MarginInfo(false, false, false, false));
        horizontalLayout2.setSizeFull();
        horizontalLayout2.addComponent(buildLayout3());
        manualInputDiscountPercentage.addFocusListener(this::onFocusToDiscountPercentage);
        manualInputDiscountPercentage.addValueChangeListener(this::onDiscountPercentageValueChange);

        manualInputDiscountAmount.addFocusListener(this::onFocusToDiscountAmount);
        manualInputDiscountAmount.addValueChangeListener(this::onDiscountAmountValueChange);
        verticalLayout.addComponent(horizontalLayout4);
        verticalLayout.setComponentAlignment(horizontalLayout4,Alignment.TOP_CENTER);
        horizontalLayout2.setHeightUndefined();

        Component footerLayOut = buildCloseButton();
        verticalLayout.addComponent(footerLayOut);
        setContent(verticalLayout);

    }

    private Component buildCheckBox()
    {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setMargin(new MarginInfo(true,true,true,true));
        horizontalLayout.setSizeFull();

        checkProduct=new OptionGroup();
        checkProduct.addItems("Product","Addon","Product & Addon");
        checkProduct.setValue("Product & Addon");
        checkProduct.addStyleName("horizontal");
        checkProduct.addValueChangeListener(this::checkSelectedValue);
        horizontalLayout.addComponent(checkProduct);

        /*checkproduct=new CheckBox("Products Only");
        checkproduct.setValue(true);
        horizontalLayout.addComponent(checkproduct);

        checkaddon=new CheckBox("Adons Only");
        checkaddon.setValue(true);
        horizontalLayout.addComponent(checkaddon);*/

        /*checkAddon=new OptionGroup("Addon");
        horizontalLayout.addComponent(checkAddon);

        checkboth=new OptionGroup("Product&Addon");
        horizontalLayout.addComponent(checkboth);*/

        return horizontalLayout;
    }

    private Component buildCloseButton()
    {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setMargin(new MarginInfo(false,true,true,true));
        horizontalLayout.setSizeFull();

        closeButton = new Button("Close");
        closeButton.addStyleName(ValoTheme.BUTTON_SMALL);
        closeButton.addStyleName("margin-right-10-for-headerlevelbutton");
        closeButton.addClickListener(this::close);
        horizontalLayout.addComponent(closeButton);
        horizontalLayout.setComponentAlignment(closeButton, Alignment.MIDDLE_CENTER);

        return horizontalLayout;
    }

    private void close(Button.ClickEvent clickEvent)
    {
        close();
    }

    private void updateTotal()
    {
        Double Amount=0.0;
        List<Product> products;

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

        products = proposalDataProvider.getVersionProducts(proposalVersion.getProposalId(), proposalVersion.getVersion());
        for(Product product:products)
        {
            LOG.info("products" +product);
        }

        for(Product product:products)
        {
            Amount+=product.getAmount();
            List<Module> modules=product.getModules();
            for(Module module:modules)
            {
                LOG.info("module component" +module);
                ModuleForPrice moduleForPrice = new ModuleForPrice();
                moduleForPrice.setCity(proposalHeader.getPcity());
                moduleForPrice.setModule(module);
                if (proposalHeader.getPriceDate() == null)
                {
                    Date dateToBeUsed = new Date(System.currentTimeMillis());
                    moduleForPrice.setPriceDate(dateToBeUsed);
                }
                else {
                    moduleForPrice.setPriceDate(proposalHeader.getPriceDate());
                }
                ModulePrice modulePrice = proposalDataProvider.getModulePrice(moduleForPrice);
             /*   if(module.getMgCode().equals("MG-NS-H-001") || ("MG-NS-H-002").equals(module.getMgCode()))
                {
                    hikeCost+=modulePrice.getWoodworkCost();
                }*/
                if(module.getMgCode().startsWith("MG-NS-H"))
                {
                    hikeCost+=modulePrice.getWoodworkCost();
                }
            }

            List<Module> modulesToUse=product.getModules();
            for(Module module:modulesToUse)
            {
                ModuleForPrice moduleForPrice = new ModuleForPrice();
                moduleForPrice.setCity(proposalHeader.getPcity());
                moduleForPrice.setModule(module);
                if (proposalHeader.getPriceDate() == null)
                {
                    Date dateToBeUsed = new Date(System.currentTimeMillis());
                    moduleForPrice.setPriceDate(dateToBeUsed);
                }
                else
                {
                    moduleForPrice.setPriceDate(proposalHeader.getPriceDate());

                }
                ModulePrice modulePrice = proposalDataProvider.getModulePrice(moduleForPrice);
                TotalCost+=modulePrice.getTotalCost();
                if (module.getMgCode().startsWith("MG-NS"))
                {
                    nonStdModuleCount++;
                    /*if((!module.getMgCode().equals("MG-NS-H-001")) || (!module.getMgCode().equals("MG-NS-H-002")))*/
                    if(!(module.getMgCode().startsWith("MG-NS-H")))
                    {
                        NSWoodWorkCost+=modulePrice.getCarcassCost()+modulePrice.getShutterCost();
                    }
                }
                else
                {
                    standardModuleCount++;
                    SWoodWorkCost+=modulePrice.getCarcassCost()+modulePrice.getShutterCost();
                }
                ShutterCost+=modulePrice.getShutterCost();
                CarcassCost+=modulePrice.getCarcassCost();
                AccessoryCost += modulePrice.getAccessoryCost();//msp
                List<ModuleAccessoryPack> moduleaccpack=module.getAccessoryPacks();
                for(ModuleAccessoryPack moduleAccessoryPack:moduleaccpack)
                {
                    List<String> acccode=moduleAccessoryPack.getAccessories();
                    for(String ZgenericAccessory: acccode)
                    {
                        LOG.info("acc code" +ZgenericAccessory);
                        PriceMaster accessoryRateMaster=proposalDataProvider.getAccessoryRateDetails(ZgenericAccessory,this.priceDate,this.city);
                        {
                            LOG.info("source price" +accessoryRateMaster.getSourcePrice());
                            manufacturingAccessoryCost+=accessoryRateMaster.getSourcePrice();
                        }
                    }

                    List <AccessoryDetails>  accesoryHardwareMasters =proposalDataProvider.getAccessoryDetails(moduleAccessoryPack.getCode());
                    LOG.info("Accessory code z generic" +moduleAccessoryPack.getCode());
                    for(AccessoryDetails acc: accesoryHardwareMasters)
                    {
                        PriceMaster accessoryRateMaster=proposalDataProvider.getAccessoryRateDetails(acc.getCode(),this.priceDate,this.city);
                        LOG.info("price data" +accessoryRateMaster);
                        {
                            manufacturingAccessoryCostForZgeneric+=accessoryRateMaster.getSourcePrice();
                        }
                        PriceMaster hardwareRateMaster=proposalDataProvider.getHardwareRateDetails(acc.getCode(),this.priceDate,this.city);
                        LOG.info("hardware code" +acc.getCode());
                        {
                            {
                                LOG.info("hardware price" +hardwareRateMaster);
                                manufacturingHardwareCost += hardwareRateMaster.getSourcePrice();
                            }
                        }
                    }

                    /*List<AccessoryDetails> accessoryDetailshardware=proposalDataProvider.getAccessoryhwDetails(moduleAccessoryPack.getCode());
                    for(AccessoryDetails acchwdetails: accessoryDetailshardware)
                    {
                        LOG.info("hardware code in string" +acchwdetails.getCode());
                        PriceMaster hardwareRateMaster=proposalDataProvider.getHardwareRateDetails(acchwdetails.getCode(),this.priceDate,this.city);
                        {
                            LOG.info("hardware price in string" +hardwareRateMaster);
                            {
                                manufacturingHardwareCost += hardwareRateMaster.getSourcePrice();
                            }
                        }
                    }*/
                }
                HardwareCost+=modulePrice.getHardwareCost();
/*
                if((!module.getMgCode().equals("MG-NS-H-001")) || (!module.getMgCode().equals("MG-NS-H-002")) )
*/
                if(!(module.getMgCode().startsWith("MG-NS-H")))

                {
                    LabourCost+=modulePrice.getLabourCost();
                }
                List<ModuleComponent> modulehardwaredetails=proposalDataProvider.getModuleAccessoryhwDetails(module.getMgCode());
                for(ModuleComponent acchwdetails: modulehardwaredetails)
                {
                    LOG.info("module hardware code in string1" +acchwdetails);
                    PriceMaster hardwareRateMaster=proposalDataProvider.getHardwareRateDetails(acchwdetails.getCompcode(),this.priceDate,this.city);
                    {
                        LOG.info("module hardware price in string1" +hardwareRateMaster);
                        {
                            manufacturingHardwareCost += hardwareRateMaster.getSourcePrice() * acchwdetails.getQuantity();
                        }
                    }
                }
            }
        }
        LOG.info("Non std WoodCost " +NSWoodWorkCost);
        LOG.info("Std WoodCost" +SWoodWorkCost);
        LOG.info("Accessory Cost" +AccessoryCost);
        LOG.info("Hardware Cost" +HardwareCost);
        LOG.info("Labour Cost" +LabourCost);
        LOG.info("hike cost" +hikeCost);
        totalSalesPrice =NSWoodWorkCost+SWoodWorkCost+HardwareCost+LabourCost+AccessoryCost+hikeCost;
        totalSalesPriceWOAcc =NSWoodWorkCost+SWoodWorkCost+HardwareCost+LabourCost+hikeCost;
        LOG.info("totalsalesprice" +totalSalesPrice);

        //totalSalesPriceWOtax = (totalSalesPrice-hikeCost) *0.8558;
        totalSalesPriceWOtax = (totalSalesPrice) *rateForProductWOTax;

        LOG.info("TSP WO Tax" +totalSalesPriceWOtax);

        //stdModuleManufacturingCost =SWoodWorkCost/2.46;
        stdModuleManufacturingCost =SWoodWorkCost/rateForStdManfCost;
        //nonStdModuleManufacturingCost =NSWoodWorkCost/1.288;
        nonStdModuleManufacturingCost =NSWoodWorkCost/rateForNStdManfCost;
        //manufacturingLabourCost =(LabourCost)/1.288;
        manufacturingLabourCost =(LabourCost)/rateForManfLabourCost;
        //manufacturingHardwareCost =HardwareCost/1.546;
        //manufacturingAccessoryCost =AccessoryCost/1.546;
        LOG.info("acc cost" +manufacturingAccessoryCost +"generic" +manufacturingAccessoryCostForZgeneric);
        FinalmanufacturingAccoryCost = manufacturingAccessoryCost + manufacturingAccessoryCostForZgeneric;
        LOG.info(FinalmanufacturingAccoryCost);

        LOG.info("Manf std" +stdModuleManufacturingCost);
        LOG.info("manf NONStd " +nonStdModuleManufacturingCost);
        LOG.info("mnf Accessory price" +FinalmanufacturingAccoryCost);
        LOG.info("mnf Hardware price" +manufacturingHardwareCost);
        LOG.info("mnf Labour price" +manufacturingLabourCost);

        manufacturingTotalSalesPrice = stdModuleManufacturingCost + nonStdModuleManufacturingCost + manufacturingLabourCost + manufacturingHardwareCost + FinalmanufacturingAccoryCost;
        LOG.info("Mnf Total price" +manufacturingTotalSalesPrice);

        List<AddonProduct> addonProducts=proposalDataProvider.getVersionAddons(proposalVersion.getProposalId(), proposalVersion.getVersion());
        for (AddonProduct addonProduct:addonProducts)
        {
            ModuleForPrice moduleForPrice = new ModuleForPrice();
            moduleForPrice.setCity(proposalHeader.getPcity());
            if (proposalHeader.getPriceDate() == null)
            {
                Date dateToBeUsed = new Date(System.currentTimeMillis());
                moduleForPrice.setPriceDate(dateToBeUsed);
            }
            else
            {
                moduleForPrice.setPriceDate(proposalHeader.getPriceDate());
            }

            addonsTotal+=addonProduct.getAmount();
            //addonsTotalWOtax+=addonProduct.getAmount()*0.8558;
            addonsTotalWOtax+=addonProduct.getAmount()*rateForAddonWOTax;
            LOG.info("addon code" +addonProduct.getCode());
            if(addonProduct.getCode().equals("NA"))
            {
                double addonDiscountedPrice=0;
                //addonDiscountedPrice=0.8*addonProduct.getAmount();
                addonDiscountedPrice=rateForAddonSourcePrice*addonProduct.getAmount();
                addonDealerPrice+=addonDiscountedPrice;
                LOG.info("custom addon " +addonDealerPrice);
            }

            PriceMaster addonMasterRate=proposalDataProvider.getAddonRate(addonProduct.getCode(),this.priceDate,this.city);
            //PriceMaster addonMasters=proposalDataProvider.getAddonRate(addonProduct.getCode(),moduleForPrice.getPriceDate(),moduleForPrice.getCity());
            {
                addonDealerPrice+=Double.valueOf(addonMasterRate.getSourcePrice());
                //addonsTotalWOtax+=Double.valueOf(addonMaster.getDealerPrice());
                //addonsProfit+=addonsTotalWOtax-Double.valueOf(addonMaster.getDealerPrice());*/
            }
        }
        addonsProfit=addonsTotalWOtax-addonDealerPrice;
        LOG.info("Addon dealer price" +addonDealerPrice);
        ProductTotal=(totalSalesPrice)+addonsTotal;

        LOG.info("Addon Total" +addonsTotal);
        LOG.info("Addon total wo tax" +addonsTotalWOtax);
        LOG.info("Addonprofit" +addonsProfit);

        java.util.Date date =proposalHeader.getCreatedOn();
        java.util.Date currentDate = new java.util.Date(117,3,17,0,0,00);
        double TspToBeConsidered = ProductTotal - addonsTotal;
        if (date.after(currentDate))
        {
            LOG.info("if loop in main method");
            LOG.info("MSC : " + TspToBeConsidered);
            obj1=calculateSalesPriceWithDiscount(obj1, TspToBeConsidered);
            obj2=calculateSalesPriceWithDiscount(obj2,totalSalesPrice-proposalVersion.getDiscountAmount());
            obj3=obj2;
            //refreshDiscountForNewProposals(totalAmount,addonsTotal,productsTotal);
        }
        else {
            LOG.info("else loop in main method");
            //refreshDiscountForOldProposals(totalWoAccessories, totalAmount, costOfAccessories, addonsTotal);
            obj1=calculateSalesPriceWithDiscount(obj1, TspToBeConsidered);
            LOG.info("discount amoount" +proposalVersion.getDiscountAmount());
            //obj2=calculateSalesPriceWithDiscount(obj2,(TspToBeConsidered-AccessoryCost)-proposalVersion.getDiscountAmount());
            obj2=calculateSalesPriceWithDiscount(obj2,totalSalesPrice-proposalVersion.getDiscountAmount());
            obj3=obj2;

        }


        manufacturingProfit = totalSalesPriceWOtax - manufacturingTotalSalesPrice;
        marginCompute=(manufacturingProfit / totalSalesPriceWOtax)*100;

        LOG.info("addon amount" +addonsTotal);
        LOG.info("Addon amount WO Tax" +addonsTotalWOtax);
        LOG.info("Manufacturing profit" +manufacturingProfit);

        totalAmount = addonsTotal + productsTotal;
        costOfAccessories = productsTotal - totalWoAccessories;
    }

    private margin calculateSalesPriceWithDiscount(margin margin,Double Tsp)
    {

        Double Tspwt=(Tsp)*rateForProductWOTax;

        //Tspwt=Tspwt+hikeCost;

        Double MProfit=Tspwt- manufacturingTotalSalesPrice;
        Double marginCompute=(MProfit/Tspwt)*100;
        if(Double.isNaN(marginCompute))
        {
            marginCompute=0.0;
        }
        LOG.info("margin compute" +marginCompute);

        Double AddonTotal=addonsTotal;
        Double AddonTotalWOtax=addonsTotalWOtax;
        Double AddonsProfit=addonsProfit;
        Double addonsMargin=(addonsProfit / AddonTotalWOtax)*100;
        if(Double.isNaN(addonsMargin))
        {
            addonsMargin=0.0;
        }

        Double total=Tsp+AddonTotal;
        Double totalwt=Tspwt+AddonTotalWOtax;
        Double profit=MProfit+AddonsProfit;
        Double finalmargin=(profit/totalwt)*100;
        if(Double.isNaN(finalmargin))
        {
            finalmargin=0.0;
        }

        margin.setTsp(Tsp);
        margin.setTspWt(Tspwt);
        margin.setMprofit(MProfit);
        margin.setMArginCompute(marginCompute);

        margin.setAddonPrice(AddonTotal);
        margin.setAddonPriceWTtax(AddonTotalWOtax);
        margin.setAddonProfit(addonsProfit);
        margin.setAddonMargin(addonsMargin);

        margin.setProductaddonTsp(total);
        margin.setProductaddontspwt(totalwt);
        margin.setProductaddonProfit(profit);
        margin.setProductaddonMargin(finalmargin);

        return margin;
    }

    public Component buildTsp()
    {
        HorizontalLayout horizontalLayout=new HorizontalLayout();
        horizontalLayout.setMargin(new MarginInfo(false,true,false,true));
        horizontalLayout.setSpacing(true);
        productsCost=new Label("Products Price:   " + round(totalSalesPrice,2));
        productsCost.addStyleName("margin-label-style1");
        horizontalLayout.addComponent(productsCost);

        addonsCost=new Label("Addons Price:   " +round(addonsTotal,2));
        addonsCost.addStyleName("margin-label-style1");
        horizontalLayout.addComponent(addonsCost);

        totalPrice=new Label("Total Sales Price:   "+round(totalSalesPrice+addonsTotal,2));
        totalPrice.addStyleName("margin-label-style1");
        horizontalLayout.addComponent(totalPrice);

        hikeLabel=new Label("Hike Price:   "+round(hikeCost,2));
        hikeLabel.addStyleName("margin-label-style1");
        horizontalLayout.addComponent(hikeLabel);

        return horizontalLayout;
    }

    public Component buildHeading()
    {
        VerticalLayout verticalLayout=new VerticalLayout();
        verticalLayout.setSpacing(true);
        verticalLayout.setSpacing(true);
        verticalLayout.setSpacing(true);
        verticalLayout.setMargin(new MarginInfo(false,true,false,true));

        Label label=new Label();
        verticalLayout.addComponent(label);
        verticalLayout.setComponentAlignment(label,Alignment.MIDDLE_CENTER);

        Label label5=new Label("Discount %");
        label5.addStyleName("margin-label-style");
        verticalLayout.addComponent(label5);
        verticalLayout.setComponentAlignment(label5,Alignment.MIDDLE_CENTER);

        Label label6=new Label("Discount Amount");
        label6.addStyleName("margin-label-style");
        verticalLayout.addComponent(label6);
        verticalLayout.setComponentAlignment(label6,Alignment.MIDDLE_CENTER);

        Label label1=new Label("Actual Sales Price(ASP)");
        label1.addStyleName("margin-label-style");
        verticalLayout.addComponent(label1);
        verticalLayout.setComponentAlignment(label1,Alignment.MIDDLE_CENTER);

        Label label2=new Label("ASP W/O Tax");
        label2.addStyleName("margin-label-style");
        verticalLayout.addComponent(label2);
        verticalLayout.setComponentAlignment(label2,Alignment.MIDDLE_CENTER);

        Label label3=new Label("Profit");
        label3.addStyleName("margin-label-style");
        verticalLayout.addComponent(label3);
        verticalLayout.setComponentAlignment(label3,Alignment.MIDDLE_CENTER);

        Label label4=new Label("Margin");
        label4.addStyleName("margin-label-style");
        verticalLayout.addComponent(label4);
        verticalLayout.setComponentAlignment(label4,Alignment.MIDDLE_CENTER);

        return verticalLayout;
    }

    public Component buildLayout1()
    {
        VerticalLayout verticalLayout=new VerticalLayout();
        verticalLayout.setSpacing(true);
        verticalLayout.setMargin(new MarginInfo(false,true,false,true));

        wodiscount=new Label("W/O Discount");
        wodiscount.addStyleName("margin-label-style");
        verticalLayout.addComponent(wodiscount);

        per=new Label();
        per.addStyleName("margin-label-style2");
        per.setValue("0.0");
        verticalLayout.addComponent(per);

        amt=new Label();
        amt.addStyleName("margin-label-style2");
        amt.setValue("0.0");
        verticalLayout.addComponent(amt);

        actualSalesPrice = new Label();
        actualSalesPrice.addStyleName("margin-label-style2");
        actualSalesPrice.setValue(String.valueOf(round(ProductTotal,2)).toString());
        actualSalesPrice.setReadOnly(true);
        verticalLayout.addComponent(actualSalesPrice);
        verticalLayout.setComponentAlignment(actualSalesPrice,Alignment.MIDDLE_CENTER);

        actualSalesPriceWOtax = new Label();
        actualSalesPriceWOtax.addStyleName("margin-label-style2");
        actualSalesPriceWOtax.setValue(String.valueOf(round(obj1.getProductaddontspwt(),2)).toString());
        actualSalesPriceWOtax.setReadOnly(true);
        verticalLayout.addComponent(actualSalesPriceWOtax);
        verticalLayout.setComponentAlignment(actualSalesPriceWOtax,Alignment.MIDDLE_CENTER);

        actualProfit = new Label();
        actualProfit.addStyleName("margin-label-style2");
        actualProfit.setValue(String.valueOf(round(obj1.getProductaddonProfit(),2)).toString());
        actualProfit.setReadOnly(true);
        verticalLayout.addComponent(actualProfit);
        verticalLayout.setComponentAlignment(actualProfit,Alignment.MIDDLE_CENTER);

        actualSalesMargin = new Label();
        actualSalesMargin.addStyleName("margin-label-style2");
        actualSalesMargin.setValue(String.valueOf(round(obj1.getProductaddonMargin(),2)).toString()+ "%");
        actualSalesMargin.setReadOnly(true);
        verticalLayout.addComponent(actualSalesMargin);
        verticalLayout.setComponentAlignment(actualSalesMargin,Alignment.MIDDLE_CENTER);

        /*actualAddonPrice=new Label();
        actualAddonPrice.addStyleName("margin-label-style2");
        actualAddonPrice.setValue(String.valueOf(round(obj1.getAddonPrice(),2)).toString());
        actualAddonPrice.setReadOnly(true);
        verticalLayout.addComponent(actualAddonPrice);
        verticalLayout.setComponentAlignment(actualAddonPrice,Alignment.MIDDLE_CENTER);

        actuaAddonPriceWTtax=new Label();
        actuaAddonPriceWTtax.addStyleName("margin-label-style2");
        actuaAddonPriceWTtax.setValue(String.valueOf(round(obj1.getAddonPriceWTtax(),2)).toString());
        actuaAddonPriceWTtax.setReadOnly(true);
        verticalLayout.addComponent(actuaAddonPriceWTtax);
        verticalLayout.setComponentAlignment(actuaAddonPriceWTtax,Alignment.MIDDLE_CENTER);

        actualAddonProfit=new Label();
        actualAddonProfit.addStyleName("margin-label-style2");
        actualAddonProfit.setValue(String.valueOf(round(obj1.getAddonProfit(),2)).toString());
        actualAddonProfit.setReadOnly(true);
        verticalLayout.addComponent(actualAddonProfit);
        verticalLayout.setComponentAlignment(actualAddonProfit,Alignment.MIDDLE_CENTER);

        actualAddonMargin=new Label();
        actualAddonMargin.addStyleName("margin-label-style2");
        actualAddonMargin.setValue(String.valueOf(round(obj1.getAddonMargin(),2)).toString());
        actualAddonMargin.setReadOnly(true);
        verticalLayout.addComponent(actualAddonMargin);
        verticalLayout.setComponentAlignment(actualAddonMargin,Alignment.MIDDLE_CENTER);*/

        return verticalLayout;
    }

    public Component buildLayout2()
    {
        VerticalLayout verticalLayout=new VerticalLayout();
        verticalLayout.setSpacing(true);
        verticalLayout.setMargin(new MarginInfo(false,true,false,true));

        withDiscount=new Label("With Discount");
        withDiscount.addStyleName("margin-label-style");
        verticalLayout.addComponent(withDiscount);

        profitPercentage =new Label();
        profitPercentage.addStyleName("margin-label-style2");
        profitPercentage.setValue(String.valueOf(proposalVersion.getDiscountPercentage()));
        verticalLayout.addComponent(profitPercentage);

        profitPercentageAmount =new Label();
        profitPercentageAmount.addStyleName("margin-label-style2");
        profitPercentageAmount.setValue(String.valueOf(proposalVersion.getDiscountAmount()));
        verticalLayout.addComponent(profitPercentageAmount);

        discountedSalesPrice = new Label();
        discountedSalesPrice.addStyleName("margin-label-style2");
        discountedSalesPrice.setReadOnly(true);
        //LOG.info("final value" +String.valueOf(round(obj2.getTsp(),2)).toString());
        //LOG.info(obj2.getTsp());
        //String.valueOf(round(ProductTotal,2)).toString();

            discountedSalesPrice.setValue(String.valueOf(round(obj2.getProductaddonTsp(), 2)).toString());

        verticalLayout.addComponent(discountedSalesPrice);
        verticalLayout.setComponentAlignment(discountedSalesPrice,Alignment.MIDDLE_CENTER);

        discountedSalesPriceWOtax = new Label();
        discountedSalesPriceWOtax.addStyleName("margin-label-style2");
        discountedSalesPriceWOtax.setValue(String.valueOf(round(obj2.getProductaddontspwt(),2)).toString());
        discountedSalesPriceWOtax.setReadOnly(true);
        verticalLayout.addComponent(discountedSalesPriceWOtax);
        verticalLayout.setComponentAlignment(discountedSalesPriceWOtax,Alignment.MIDDLE_CENTER);

        discountedProfit = new Label();
        discountedProfit.addStyleName("margin-label-style2");
        discountedProfit.setValue(String.valueOf(round(obj2.getProductaddonProfit(),2)).toString());
        discountedProfit.setReadOnly(true);
        verticalLayout.addComponent(discountedProfit);
        verticalLayout.setComponentAlignment(discountedProfit,Alignment.MIDDLE_CENTER);

        discountedSalesMargin = new Label();
        discountedSalesMargin.addStyleName("margin-label-style2");
        discountedSalesMargin.setValue(String.valueOf(round(obj2.getProductaddonMargin(),2)).toString()+ "%");
        discountedSalesMargin.setReadOnly(true);
        verticalLayout.addComponent(discountedSalesMargin);
        verticalLayout.setComponentAlignment(discountedSalesMargin,Alignment.MIDDLE_CENTER);

        /*discountedaddonPrice=new Label();
        discountedaddonPrice.addStyleName("margin-label-style2");
        discountedaddonPrice.setValue(String.valueOf(round(obj2.getAddonPrice(),2)).toString() );
        verticalLayout.addComponent(discountedaddonPrice);
        verticalLayout.setComponentAlignment(discountedaddonPrice,Alignment.MIDDLE_CENTER);

        discountedaddonpriceWOtax=new Label();
        discountedaddonpriceWOtax.addStyleName("margin-label-style2");
        discountedaddonpriceWOtax.setValue(String.valueOf(round(obj2.getAddonPriceWTtax(),2)).toString() );
        verticalLayout.addComponent(discountedaddonpriceWOtax);
        verticalLayout.setComponentAlignment(discountedaddonpriceWOtax,Alignment.MIDDLE_CENTER);

        discountedaddonProfit=new Label();
        discountedaddonProfit.addStyleName("margin-label-style2");
        discountedaddonProfit.setValue(String.valueOf(round(obj2.getAddonProfit(),2)).toString() );
        verticalLayout.addComponent(discountedaddonProfit);
        verticalLayout.setComponentAlignment(discountedaddonProfit,Alignment.MIDDLE_CENTER);

        discountedaddonMargin=new Label();
        discountedaddonMargin.addStyleName("margin-label-style2");
        discountedaddonMargin.setValue(String.valueOf(round(obj2.getAddonMargin(),2)).toString() );
        verticalLayout.addComponent(discountedaddonMargin);
        verticalLayout.setComponentAlignment(discountedaddonMargin,Alignment.MIDDLE_CENTER);*/

        return verticalLayout;
    }

    public Component buildLayout3()
    {
        VerticalLayout verticalLayout=new VerticalLayout();
        verticalLayout.setSpacing(true);
        verticalLayout.setMargin(new MarginInfo(false,true,false,true));

        whatIf=new Label("What If");
        whatIf.addStyleName("margin-label-style");
        verticalLayout.addComponent(whatIf);

        manualInputDiscountPercentage =new TextField();
        manualInputDiscountPercentage.addStyleName("heighttext");
        manualInputDiscountPercentage.addStyleName("margin-label-style2");
        manualInputDiscountPercentage.setValue(String.valueOf(proposalVersion.getDiscountPercentage()));
        verticalLayout.addComponent(manualInputDiscountPercentage);

        manualInputDiscountAmount =new TextField();
        manualInputDiscountAmount.addStyleName("margin-label-style2");
        manualInputDiscountAmount.addStyleName("heighttext");
        manualInputDiscountAmount.setValue(String.valueOf(proposalVersion.getDiscountAmount()));
        verticalLayout.addComponent(manualInputDiscountAmount);

        manualInputSalesPrice = new Label();
        manualInputSalesPrice.addStyleName("margin-label-style2");
        manualInputSalesPrice.setValue(String.valueOf(round(obj3.getProductaddonTsp(),2)).toString());
        //manualInputSalesPrice.addValueChangeListener(this::calculateSalesPrice);
        verticalLayout.addComponent(manualInputSalesPrice);
        verticalLayout.setComponentAlignment(manualInputSalesPrice,Alignment.MIDDLE_CENTER);

        manualInputSalesPriceWOtax = new Label();
        manualInputSalesPriceWOtax.addStyleName("margin-label-style2");
        manualInputSalesPriceWOtax.setValue(String.valueOf(round(obj3.getProductaddontspwt(),2)).toString());
        verticalLayout.addComponent(manualInputSalesPriceWOtax);
        verticalLayout.setComponentAlignment(manualInputSalesPriceWOtax,Alignment.MIDDLE_CENTER);

        manualInputProfitPercentage = new Label();
        manualInputProfitPercentage.addStyleName("margin-label-style2");
        manualInputProfitPercentage.setValue(String.valueOf(round(obj3.getProductaddonProfit(),2)).toString());
        verticalLayout.addComponent(manualInputProfitPercentage);
        verticalLayout.setComponentAlignment(manualInputProfitPercentage,Alignment.MIDDLE_CENTER);

        manualInputMargin = new Label();
        manualInputMargin.addStyleName("margin-label-style2");
        manualInputMargin.setValue(String.valueOf(round(obj3.getProductaddonMargin(),2)).toString() + "%");
        verticalLayout.addComponent(manualInputMargin);
        verticalLayout.setComponentAlignment(manualInputMargin,Alignment.MIDDLE_CENTER);

        return verticalLayout;
    }
    private void checkSelectedValue(Property.ValueChangeEvent valueChangeEvent)
    {
        LOG.info("selected Value" +checkProduct.getValue());
        java.util.Date date =proposalHeader.getCreatedOn();
        java.util.Date currentDate = new java.util.Date(200,1,28,0,0,00);
        if(checkProduct.getValue()=="Product")
        {
            obj1=new margin();
            obj2=new margin();
            obj3=new margin();

            if (date.after(currentDate))
            {
                obj1=calculateSalesPriceWithDiscount(obj1,totalSalesPrice);
                obj2=calculateSalesPriceWithDiscount(obj2,totalSalesPrice-proposalVersion.getDiscountAmount());
                obj3=obj2;

                /*obj1=calculateSalesPriceWithDiscount(obj1,ProductTotal-addonsTotal);
                obj2=calculateSalesPriceWithDiscount(obj2,totalSalesPrice-proposalVersion.getDiscountAmount());
                obj3=obj2;*/
                //refreshDiscountForNewProposals(totalAmount,addonsTotal,productsTotal);
            }
            else {
                //refreshDiscountForOldProposals(totalWoAccessories, totalAmount, costOfAccessories, addonsTotal);
                /*obj1=calculateSalesPriceWithDiscount(obj1,ProductTotal-addonsTotal);
                obj2=calculateSalesPriceWithDiscount(obj2,totalSalesPriceWOAcc-proposalVersion.getDiscountAmount());
                obj3=obj2;*/
                obj1=calculateSalesPriceWithDiscount(obj1,totalSalesPrice);
                obj2=calculateSalesPriceWithDiscount(obj2,totalSalesPrice-proposalVersion.getDiscountAmount());
                obj3=obj2;

            }

            /*obj1=calculateSalesPriceWithDiscount(obj1,totalSalesPrice);
            obj2=calculateSalesPriceWithDiscount(obj2,totalSalesPrice-proposalVersion.getDiscountAmount());
            obj3=obj2;*/

            checkstatus="P";
            amt.setValue("0.0");
            per.setValue("0.0");
            actualSalesPrice.setValue(String.valueOf(round(obj1.getTsp(),2)).toString());
            actualSalesPriceWOtax.setValue(String.valueOf(round(obj1.getTspWt(),2)).toString());
            actualProfit.setValue(String.valueOf(round(obj1.getMprofit(),2)).toString());
            actualSalesMargin.setValue(String.valueOf(round(obj1.getMArginCompute(),2)).toString()+ "%");
            /*actualAddonPrice.setValue(String.valueOf(round(obj1.getAddonPrice(),2)).toString());
            actuaAddonPriceWTtax.setValue(String.valueOf(round(obj1.getAddonPriceWTtax(),2)).toString());
            actualAddonProfit.setValue(String.valueOf(round(obj1.getAddonProfit(),2)).toString());
            actualAddonMargin.setValue(String.valueOf(round(obj1.getAddonMargin(),2)).toString());*/

            profitPercentage.setValue(String.valueOf(proposalVersion.getDiscountPercentage()));
            profitPercentageAmount.setValue(String.valueOf(proposalVersion.getDiscountAmount()));
            discountedSalesPrice.setValue(String.valueOf(round(obj2.getTsp(),2)).toString());
            discountedSalesPriceWOtax.setValue(String.valueOf(round(obj2.getTspWt(),2)).toString());
            discountedProfit.setValue(String.valueOf(round(obj2.getMprofit(),2)).toString());
            discountedSalesMargin.setValue(String.valueOf(round(obj2.getMArginCompute(),2)).toString()+ "%");

            manualInputDiscountPercentage.setValue(String.valueOf(proposalVersion.getDiscountPercentage()));
            manualInputDiscountAmount.setValue(String.valueOf(proposalVersion.getDiscountAmount()));
            manualInputSalesPrice.setValue(String.valueOf(round(obj3.getTsp(),2)).toString());
            manualInputSalesPriceWOtax.setValue(String.valueOf(round(obj3.getTspWt(),2)).toString());
            manualInputProfitPercentage.setValue(String.valueOf(round(obj3.getMprofit(),2)).toString());
            manualInputMargin.setValue(String.valueOf(round(obj3.getMArginCompute(),2)).toString() + "%");
        }
        else if(checkProduct.getValue()=="Addon")
        {
            obj1=new margin();
            obj2=new margin();
            obj3=new margin();

            obj1=calculateSalesPriceWithDiscount(obj1,addonsTotal);
            obj2=calculateSalesPriceWithDiscount(obj2,addonsTotal);
            obj3=obj2;

            checkstatus="A";
            amt.setValue("0.0");
            per.setValue("0.0");
            actualSalesPrice.setValue(String.valueOf(round(obj1.getAddonPrice(),2)).toString());
            actualSalesPriceWOtax.setValue(String.valueOf(round(obj1.getAddonPriceWTtax(),2)).toString());
            actualProfit.setValue(String.valueOf(round(obj1.getAddonProfit(),2)).toString());
            actualSalesMargin.setValue(String.valueOf(round(obj1.getAddonMargin(),2)).toString()+ "%");
            /*actualAddonPrice.setValue(String.valueOf(round(obj1.getAddonPrice(),2)).toString());
            actuaAddonPriceWTtax.setValue(String.valueOf(round(obj1.getAddonPriceWTtax(),2)).toString());
            actualAddonProfit.setValue(String.valueOf(round(obj1.getAddonProfit(),2)).toString());
            actualAddonMargin.setValue(String.valueOf(round(obj1.getAddonMargin(),2)).toString());*/

            profitPercentage.setValue(String.valueOf(proposalVersion.getDiscountPercentage()));
            profitPercentageAmount.setValue(String.valueOf(proposalVersion.getDiscountAmount()));
            discountedSalesPrice.setValue(String.valueOf(round(obj2.getAddonPrice(),2)).toString());
            discountedSalesPriceWOtax.setValue(String.valueOf(round(obj2.getAddonPriceWTtax(),2)).toString());
            discountedProfit.setValue(String.valueOf(round(obj2.getAddonProfit(),2)).toString());
            discountedSalesMargin.setValue(String.valueOf(round(obj2.getAddonMargin(),2)).toString()+ "%");

            manualInputDiscountPercentage.setValue(String.valueOf(proposalVersion.getDiscountPercentage()));
            manualInputDiscountAmount.setValue(String.valueOf(proposalVersion.getDiscountAmount()));
            manualInputSalesPrice.setValue(String.valueOf(round(obj3.getAddonPrice(),2)).toString());
            manualInputSalesPriceWOtax.setValue(String.valueOf(round(obj3.getAddonPriceWTtax(),2)).toString());
            manualInputProfitPercentage.setValue(String.valueOf(round(obj3.getAddonProfit(),2)).toString());
            manualInputMargin.setValue(String.valueOf(round(obj3.getAddonMargin(),2)).toString() + "%");
        }
        else if(checkProduct.getValue()=="Product & Addon")
        {
            obj1=new margin();
            obj2=new margin();
            obj3=new margin();

            if (date.after(currentDate))
            {
                obj1=calculateSalesPriceWithDiscount(obj1,ProductTotal-addonsTotal);
                obj2=calculateSalesPriceWithDiscount(obj2,totalSalesPrice-proposalVersion.getDiscountAmount());
                LOG.info("product and addon value in radio button listeiner" +(totalSalesPrice-proposalVersion.getDiscountAmount()));
                obj3=obj2;
            }
            else {
                obj1=calculateSalesPriceWithDiscount(obj1,ProductTotal-addonsTotal);
                LOG.info("obj1" +(ProductTotal-addonsTotal));
                obj2=calculateSalesPriceWithDiscount(obj2,totalSalesPrice-proposalVersion.getDiscountAmount());
                LOG.info("obj2" +(totalSalesPriceWOAcc-proposalVersion.getDiscountAmount()));
                LOG.info("product and addon value in radio button listeiner else part" +(totalSalesPriceWOAcc-proposalVersion.getDiscountAmount()));
                obj3=obj2;

            }

            /*obj1=calculateSalesPriceWithDiscount(obj1,ProductTotal-addonsTotal);
            obj2=calculateSalesPriceWithDiscount(obj2,totalSalesPrice-proposalVersion.getDiscountAmount());
            LOG.info("product and addon value in radio button listeiner" +(totalSalesPrice-proposalVersion.getDiscountAmount()));
            obj3=obj2;*/

            checkstatus="PA";
            amt.setValue("0.0");
            per.setValue("0.0");

            actualSalesPrice.setValue(String.valueOf(round(ProductTotal,2)).toString());
            actualSalesPriceWOtax.setValue(String.valueOf(round(obj1.getProductaddontspwt(),2)).toString());
            actualProfit.setValue(String.valueOf(round(obj1.getProductaddonProfit(),2)).toString());
            actualSalesMargin.setValue(String.valueOf(round(obj1.getProductaddonMargin(),2)).toString()+ "%");
            /*actualAddonPrice.setValue(String.valueOf(round(obj1.getAddonPrice(),2)).toString());
            actuaAddonPriceWTtax.setValue(String.valueOf(round(obj1.getAddonPriceWTtax(),2)).toString());
            actualAddonProfit.setValue(String.valueOf(round(obj1.getAddonProfit(),2)).toString());
            actualAddonMargin.setValue(String.valueOf(round(obj1.getAddonMargin(),2)).toString());*/

            profitPercentage.setValue(String.valueOf(proposalVersion.getDiscountPercentage()));
            profitPercentageAmount.setValue(String.valueOf(proposalVersion.getDiscountAmount()));
            discountedSalesPrice.setValue(String.valueOf(round(obj2.getProductaddonTsp(),2)).toString());
            discountedSalesPriceWOtax.setValue(String.valueOf(round(obj2.getProductaddontspwt(),2)).toString());
            discountedProfit.setValue(String.valueOf(round(obj2.getProductaddonProfit(),2)).toString());
            discountedSalesMargin.setValue(String.valueOf(round(obj2.getProductaddonMargin(),2)).toString()+ "%");

            manualInputDiscountPercentage.setValue(String.valueOf(proposalVersion.getDiscountPercentage()));
            manualInputDiscountAmount.setValue(String.valueOf(proposalVersion.getDiscountAmount()));
            manualInputSalesPrice.setValue(String.valueOf(round(obj3.getProductaddonTsp(),2)).toString());
            manualInputSalesPriceWOtax.setValue(String.valueOf(round(obj3.getProductaddontspwt(),2)).toString());
            manualInputProfitPercentage.setValue(String.valueOf(round(obj3.getProductaddonProfit(),2)).toString());
            manualInputMargin.setValue(String.valueOf(round(obj3.getProductaddonMargin(),2)).toString() + "%");
        }
    }
    public class margin
    {
        Double Tsp;
        Double TspWt;
        Double Mprofit;
        Double MArginCompute;

        Double AddonPrice;
        Double AddonPriceWTtax;
        Double AddonProfit;
        Double AddonMargin;

        Double productaddonTsp;
        Double productaddontspwt;
        Double productaddonProfit;
        Double productaddonMargin;

        public Double getProductaddonTsp() {
            return productaddonTsp;
        }

        public void setProductaddonTsp(Double productaddonTsp) {
            this.productaddonTsp = productaddonTsp;
        }

        public Double getProductaddontspwt() {
            return productaddontspwt;
        }

        public void setProductaddontspwt(Double productaddontspwt) {
            this.productaddontspwt = productaddontspwt;
        }

        public Double getProductaddonProfit() {
            return productaddonProfit;
        }

        public void setProductaddonProfit(Double productaddonProfit) {
            this.productaddonProfit = productaddonProfit;
        }

        public Double getProductaddonMargin() {
            return productaddonMargin;
        }

        public void setProductaddonMargin(Double productaddonMargin) {
            this.productaddonMargin = productaddonMargin;
        }

        public Double getTsp() {
            return Tsp;
        }

        public Double getAddonPrice() {
            return AddonPrice;
        }

        public void setAddonPrice(Double addonPrice) {
            AddonPrice = addonPrice;
        }

        public Double getAddonPriceWTtax() {
            return AddonPriceWTtax;
        }

        public void setAddonPriceWTtax(Double addonPriceWTtax) {
            AddonPriceWTtax = addonPriceWTtax;
        }

        public Double getAddonProfit() {
            return AddonProfit;
        }

        public void setAddonProfit(Double addonProfit) {
            AddonProfit = addonProfit;
        }

        public Double getAddonMargin() {
            return AddonMargin;
        }

        public void setAddonMargin(Double addonMargin) {
            AddonMargin = addonMargin;
        }

        public void setTsp(Double tsp) {
            Tsp = tsp;
        }

        public Double getTspWt() {
            return TspWt;
        }

        public void setTspWt(Double tspWt) {
            TspWt = tspWt;
        }

        public Double getMprofit() {
            return Mprofit;
        }

        public void setMprofit(Double mprofit) {
            Mprofit = mprofit;
        }

        public Double getMArginCompute() {
            return MArginCompute;
        }

        public void setMArginCompute(Double MArginCompute) {
            this.MArginCompute = MArginCompute;
        }
    }

    private double round(double value, int places)
    {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private void onFocusToDiscountPercentage(FieldEvents.FocusEvent event)
    {
        LOG.info("DP focused");
        status="DP";
    }
    private void onFocusToDiscountAmount(FieldEvents.FocusEvent event)
    {
        LOG.info("DA focused");
        status="DA";
    }
    private void onDiscountAmountValueChange(Property.ValueChangeEvent valueChangeEvent) {
        if("DA".equals(status))
        {
            calculateDiscount();
        }
    }

    private void onDiscountPercentageValueChange(Property.ValueChangeEvent valueChangeEvent) {
        if("DP".equals(status))
        {
            calculateDiscount();
        }
    }
    private void calculateDiscount()
    {
        java.util.Date date =proposalHeader.getCreatedOn();
        java.util.Date currentDate = new java.util.Date(200,1,28,0,0,00);
        if(checkProduct.getValue()=="Product") {

            if("DP".equals(status))
            {
                LOG.info("Enter discount Percentage" + manualInputDiscountPercentage.getValue());
                discountPercentage =Double.valueOf(manualInputDiscountPercentage.getValue());
                if (date.after(currentDate)) {
                    discountAmount = totalSalesPrice * discountPercentage / 100.0;
                }
                else
                {
                    discountAmount = totalSalesPriceWOAcc * discountPercentage / 100.0;

                }
                manualInputDiscountAmount.setValue(String.valueOf(round(discountAmount,2)));
                LOG.info("Final discount Amount" + discountAmount);
            }
            else if("DA".equals(status))
            {
                discountAmount =Double.valueOf(manualInputDiscountAmount.getValue());
                if (date.after(currentDate)) {
                    discountPercentage =(discountAmount /totalSalesPrice)*100;
                }
                else
                {
                    discountPercentage =(discountAmount /totalSalesPriceWOAcc)*100;
                }

                manualInputDiscountPercentage.setValue(String.valueOf(round(discountPercentage,2)));
                LOG.info("Final discount Percentage" + discountPercentage);
            }
            if (date.after(currentDate))
            {
                obj3=new margin();
                //obj3=calculateSalesPriceWithDiscount(obj3,totalSalesPrice-discountAmount);
                obj3=calculateSalesPriceWithDiscount(obj3,totalSalesPrice-discountAmount);
            }
            else
            {
                LOG.info("else part " +totalSalesPriceWOAcc);
                obj3=new margin();
                //obj3=calculateSalesPriceWithDiscount(obj3,totalSalesPrice-discountAmount);
                obj3=calculateSalesPriceWithDiscount(obj3,totalSalesPrice-discountAmount);

            }
            /*obj3=new margin();
            //obj3=calculateSalesPriceWithDiscount(obj3,totalSalesPrice-discountAmount);
            obj3=calculateSalesPriceWithDiscount(obj3,totalSalesPriceWOAcc-discountAmount);*/


           // obj3=calculateSalesPriceWithDiscount(obj3,productTotal-discountAmount,addonsTotal,addonsTotalWOtax,addonsProfit);
            manualInputSalesPrice.setValue(String.valueOf(round(obj3.getTsp(), 2)));
            manualInputSalesPriceWOtax.setValue(String.valueOf(round(obj3.getTspWt(), 2)));
            manualInputProfitPercentage.setValue(String.valueOf(round(obj3.getMprofit(), 2)));
            manualInputMargin.setValue(String.valueOf(round(obj3.getMArginCompute(), 2)) + "%");
        }
        else if(checkProduct.getValue()=="Addon")
        {
            if("DP".equals(status))
            {
                /*LOG.info("Enter discount Percentage" + manualInputDiscountPercentage.getValue());
                discountPercentage =Double.valueOf(manualInputDiscountPercentage.getValue());
                discountAmount =AddonTotal* discountPercentage /100.0;
                manualInputDiscountAmount.setValue(String.valueOf(round(discountAmount,2)));
                LOG.info("Final discount Amount" + discountAmount);*/
                manualInputDiscountAmount.setValue("0.0");
            }
            else if("DA".equals(status))
            {
                /*discountAmount =Double.valueOf(manualInputDiscountAmount.getValue());
                discountPercentage =(discountAmount /AddonTotal)*100;
                manualInputDiscountPercentage.setValue(String.valueOf(round(discountPercentage,2)));
                LOG.info("Final discount Percentage" + discountPercentage);*/
                manualInputDiscountPercentage.setValue("0.0");
            }


            obj3=new margin();
            //obj3=calculateSalesPriceWithDiscount(obj3,addonsTotal);
            obj3=calculateSalesPriceWithDiscount(obj3,addonsTotal);
            //obj3=calculateSalesPriceWithDiscount(obj3,productTotal-discountAmount,addonsTotal,addonsTotalWOtax,addonsProfit);
            manualInputSalesPrice.setValue(String.valueOf(round(obj3.getAddonPrice(), 2)));
            manualInputSalesPriceWOtax.setValue(String.valueOf(round(obj3.getAddonPriceWTtax(), 2)));
            manualInputProfitPercentage.setValue(String.valueOf(round(obj3.getAddonProfit(), 2)));
            manualInputMargin.setValue(String.valueOf(round(obj3.getAddonMargin(), 2)) + "%");
        }
        else if(checkProduct.getValue()=="Product & Addon")
        {
            if("DP".equals(status))
            {
                LOG.info("Enter discount Percentage" + manualInputDiscountPercentage.getValue());
                discountPercentage = Double.valueOf(manualInputDiscountPercentage.getValue());
                if (date.after(currentDate)) {
                    discountAmount =totalSalesPrice* discountPercentage /100.0;
                }
                else
                {
                    discountAmount =totalSalesPriceWOAcc* discountPercentage /100.0;
                }

                manualInputDiscountAmount.setValue(String.valueOf(round(discountAmount,2)));
                LOG.info("Final discount Amount" + discountAmount);
            }
            else if("DA".equals(status))
            {
                discountAmount =Double.valueOf(manualInputDiscountAmount.getValue());
                if (date.after(currentDate)) {
                    discountPercentage = (discountAmount / totalSalesPrice) * 100;
                }
                else
                {
                    discountPercentage = (discountAmount / totalSalesPriceWOAcc) * 100;
                }
                manualInputDiscountPercentage.setValue(String.valueOf(round(discountPercentage,2)));
                LOG.info("Final discount Percentage" + discountPercentage);
            }
            if (date.after(currentDate))
            {
                obj3=new margin();
                //obj3=calculateSalesPriceWithDiscount(obj3,totalSalesPrice-discountAmount);
                obj3=calculateSalesPriceWithDiscount(obj3,totalSalesPrice-discountAmount);
            }
            else {
                obj3=new margin();
                //obj3=calculateSalesPriceWithDiscount(obj3,totalSalesPrice-discountAmount);
                obj3=calculateSalesPriceWithDiscount(obj3,totalSalesPrice-discountAmount);

            }

            //obj2=calculateSalesPriceWithDiscount(obj2,totalSalesPrice-proposalVersion.getDiscountAmount());
            /*obj3=new margin();
            //obj3=calculateSalesPriceWithDiscount(obj3,totalSalesPrice-discountAmount);
            obj3=calculateSalesPriceWithDiscount(obj3,totalSalesPriceWOAcc-discountAmount);*/
            LOG.info("addon and product" +(totalSalesPrice-discountAmount));
            //obj3=calculateSalesPriceWithDiscount(obj3,productTotal-discountAmount,addonsTotal,addonsTotalWOtax,addonsProfit);

            LOG.info("");

            manualInputSalesPrice.setValue(String.valueOf(round(obj3.getProductaddonTsp(),2)).toString());
            manualInputSalesPriceWOtax.setValue(String.valueOf(round(obj3.getProductaddontspwt(),2)).toString());
            manualInputProfitPercentage.setValue(String.valueOf(round(obj3.getProductaddonProfit(),2)).toString());
            manualInputMargin.setValue(String.valueOf(round(obj3.getProductaddonMargin(),2)).toString() + "%");

        }
    }
}
