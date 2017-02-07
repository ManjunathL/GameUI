package com.mygubbi.game.dashboard.view.proposals;

import com.mygubbi.game.dashboard.ServerManager;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.domain.*;
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
import java.util.List;

/**
 * Created by shruthi on 25-Jan-17.
 */

public class MarginDetailsWindow extends Window
{
    private static final Logger LOG = LogManager.getLogger(MarginDetailsWindow.class);
    private String status=null;
    Button closeButton;
    Label actualSalesPrice;
    Label actualSalesPriceWOtax;
    Label actualSalesMargin;
    Label actualProfit;
    Label profitPercentage;
    Label profitPercentageAmount;

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

    Double TotalCost = 0.0;
    Double NSWoodWorkCost=0.0;
    Double SWoodWorkCost=0.0;
    Double ModuleArea=0.0;
    Double ShutterCost=0.0;
    Double CarcassCost=0.0;
    Double AccessoryCost=0.0;
    Double HardwareCost=0.0;
    Double LabourCost=0.0;

    Double productsTotal=0.0;
    Double Addonamount=0.0;
    Double totalWoAccessories = 0.0;
    Double addonsTotal=0.0;
    Double totalAmount=0.0;
    Double costOfAccessories=0.0;
    Double totalSalesPrice =0.0;
    Double totalSalesPriceWOtax =0.0;

    Double stdModuleManufacturingCost =0.0;
    Double nonStdModuleManufacturingCost =0.0;
    Double manufacturingLabourCost =0.0;
    Double manufacturingHardwareCost =0.0;
    Double manufacturingAccessoryCost =0.0;
    Double manufacturingTotalSalesPrice =0.0;
    Double manufacturingProfit =0.0;
    Double marginCompute=0.0;
    int nonStdModuleCount =0;
    int standardModuleCount =0;
    margin obj1=new margin();
    margin obj2=new margin();
    margin obj3=new margin();

    Double discountPercentage;
    Double discountAmount;
    private ProposalDataProvider proposalDataProvider = ServerManager.getInstance().getProposalDataProvider();

    public static void open(ProposalVersion proposalVersion)
    {
        MarginDetailsWindow w=new MarginDetailsWindow(proposalVersion);
        UI.getCurrent().addWindow(w);
        w.focus();

    }
    public MarginDetailsWindow(ProposalVersion proposalVersion)
    {
        DashboardEventBus.register(this);
        this.proposalVersion=proposalVersion;

        setModal(true);
        removeCloseShortcut(ShortcutAction.KeyCode.ESCAPE);
        setWidth("70%");
        setClosable(false);
        //setCaption("Margin Computation");
        /*Label l=new Label("Margin Computation");
        l.addStyleName("margin-label-style1");*/
        setCaption("Margin Computation");

        updateTotal();

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(new MarginInfo(true, true, true, true));
        Responsive.makeResponsive(this);

        HorizontalLayout mainhorizontalLayout= new HorizontalLayout();
        mainhorizontalLayout.setMargin(new MarginInfo(false, false, false, false));
        mainhorizontalLayout.setSizeFull();
        mainhorizontalLayout.addComponent(buildTsp());
        verticalLayout.addComponent(mainhorizontalLayout);
        verticalLayout.setComponentAlignment(mainhorizontalLayout,Alignment.TOP_CENTER);
        //mainhorizontalLayout.setHeightUndefined();

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
    private void updateTotal()
    {
        List<Product> products;
        products = proposalDataProvider.getVersionProducts(proposalVersion.getProposalId(), proposalVersion.getVersion());
        for(Product product:products)
        {
            List<Module> modules=product.getModules();
            for(Module module:modules)
            {

                ModulePrice modulePrice = proposalDataProvider.getModulePrice(module);

                TotalCost+=modulePrice.getTotalCost();

                if (module.getMgCode().startsWith("MG-NS"))
                {
                    nonStdModuleCount++;
                    NSWoodWorkCost+=modulePrice.getCarcassCost()+modulePrice.getShutterCost();
                    LOG.info("NSWoodWorkCost " +NSWoodWorkCost);
                }
                else
                {
                    standardModuleCount++;
                    SWoodWorkCost+=modulePrice.getCarcassCost()+modulePrice.getShutterCost();
                    LOG.info("SWoodWorkCost" +SWoodWorkCost);
                }
                ShutterCost+=modulePrice.getShutterCost();
                CarcassCost+=modulePrice.getCarcassCost();
                AccessoryCost+=modulePrice.getAccessoryCost();
                HardwareCost+=modulePrice.getHardwareCost();
                LabourCost+=modulePrice.getLabourCost();
                LOG.info("module name" +module.getMgCode() + "module shutter cost" +modulePrice.getShutterCost() +"carcass cost" + modulePrice.getCarcassCost() + "Accessory Cost" +modulePrice.getAccessoryCost() + "Harware cost" +modulePrice.getHardwareCost()+ "Labour cost" +modulePrice.getLabourCost());
            }
        }
        LOG.info("Non std" +NSWoodWorkCost);
        LOG.info("Std " +SWoodWorkCost);
        LOG.info("Shutter Cost" +ShutterCost);
        LOG.info("Carcass Cost" +CarcassCost);
        LOG.info("Accessory Cost" +AccessoryCost);
        LOG.info("Hardware Cost" +HardwareCost);
        LOG.info("Labour Cost" +LabourCost);
        totalSalesPrice =NSWoodWorkCost+SWoodWorkCost+HardwareCost+LabourCost+AccessoryCost;

        totalSalesPriceWOtax = totalSalesPrice *0.8558;
        stdModuleManufacturingCost =SWoodWorkCost/2.46;
        nonStdModuleManufacturingCost =NSWoodWorkCost/1.288;
        manufacturingLabourCost =LabourCost/1.288;
        manufacturingHardwareCost =HardwareCost/1.546;
        manufacturingAccessoryCost =AccessoryCost/1.546;

        manufacturingTotalSalesPrice = stdModuleManufacturingCost + nonStdModuleManufacturingCost + manufacturingLabourCost + manufacturingHardwareCost + manufacturingAccessoryCost;

        LOG.info("TSP " + totalSalesPrice + "totalSalesPriceWOtax" + totalSalesPriceWOtax + "m std " + stdModuleManufacturingCost + "non std " + nonStdModuleManufacturingCost + "labour " + manufacturingLabourCost + "Hardware "+ manufacturingHardwareCost + "Macc" + manufacturingAccessoryCost);


        obj1=calculateSalesPriceWithDiscount(obj1, totalSalesPrice);

        LOG.info("totalSalesPrice " + totalSalesPrice + "Discount Amount" +proposalVersion.getDiscountAmount());
        obj2=calculateSalesPriceWithDiscount(obj2, totalSalesPrice -proposalVersion.getDiscountAmount());

        obj3=obj2;
        manufacturingProfit = totalSalesPriceWOtax - manufacturingTotalSalesPrice;
        marginCompute=(manufacturingProfit / totalSalesPriceWOtax)*100;

        List<AddonProduct> addonProducts=proposalDataProvider.getVersionAddons(proposalVersion.getProposalId(), proposalVersion.getVersion());
        for (AddonProduct addonProduct:addonProducts)
        {
            addonsTotal+=addonProduct.getAmount();
        }
        totalAmount = addonsTotal + productsTotal;
        costOfAccessories = productsTotal - totalWoAccessories;



    }
    private Component buildCloseButton()
    {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setMargin(new MarginInfo(true,true,true,true));
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

    private double round(double value, int places)
    {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    private margin calculateSalesPriceWithDiscount(margin margin,Double Tsp)
    {
        Double Tspwt=Tsp*0.8558;
        Double MProfit=Tspwt- manufacturingTotalSalesPrice;
        Double marginCompute=(MProfit/Tspwt)*100;

        margin.setTsp(Tsp);
        margin.setTspWt(Tspwt);
        margin.setMprofit(MProfit);
        margin.setMArginCompute(marginCompute);

        return margin;
    }
    private void calculateSalesPrice(Property.ValueChangeEvent valueChangeEvent)
    {
        manualInputProfitPercentage.setReadOnly(false);
        manualInputSalesPriceWOtax.setReadOnly(false);
        manualInputMargin.setReadOnly(false);

        Double Tsp=Double.parseDouble(manualInputSalesPrice.getValue());
        obj3.setTsp(Tsp);
        obj3=calculateSalesPriceWithDiscount(obj3,Tsp);

        manualInputProfitPercentage.setValue(String.valueOf(round(obj3.getMprofit(),2)).toString());
        manualInputSalesPriceWOtax.setValue(String.valueOf(round(obj3.getTspWt(),2)).toString());
        manualInputMargin.setValue(String.valueOf(round(obj3.getMArginCompute(),2)).toString() + "%");

        manualInputProfitPercentage.setReadOnly(true);
        manualInputSalesPriceWOtax.setReadOnly(true);
        manualInputMargin.setReadOnly(true);
    }
    public Component buildTsp()
    {
        HorizontalLayout horizontalLayout=new HorizontalLayout();
        horizontalLayout.setMargin(new MarginInfo(true,true,false,true));
        Label LabelTsp=new Label("Total Sales Price:   " + round(totalSalesPrice,2));
        LabelTsp.addStyleName("margin-label-style1");
        //LabelTsp.setValue(String.valueOf(totalSalesPrice));
        horizontalLayout.addComponent(LabelTsp);
        return horizontalLayout;
    }

    public Component buildHeading()
    {
        VerticalLayout verticalLayout=new VerticalLayout();
        verticalLayout.setSpacing(true);
        verticalLayout.setSpacing(true);
        verticalLayout.setSpacing(true);
        verticalLayout.setMargin(new MarginInfo(true,true,true,true));

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
        verticalLayout.setMargin(new MarginInfo(true,true,true,true));

        Label heading=new Label("W/O Discount");
        heading.addStyleName("margin-label-style");
        verticalLayout.addComponent(heading);

        Label per=new Label();
        per.addStyleName("margin-label-style2");
        per.setValue("0.0");
        verticalLayout.addComponent(per);

        Label amt=new Label();
        amt.addStyleName("margin-label-style2");
        amt.setValue("0.0");
        verticalLayout.addComponent(amt);

        actualSalesPrice = new Label();
        actualSalesPrice.addStyleName("margin-label-style2");
        actualSalesPrice.setValue(String.valueOf(round(obj1.getTsp(),2)).toString());
        actualSalesPrice.setReadOnly(true);
        verticalLayout.addComponent(actualSalesPrice);
        verticalLayout.setComponentAlignment(actualSalesPrice,Alignment.MIDDLE_CENTER);

        actualSalesPriceWOtax = new Label();
        actualSalesPriceWOtax.addStyleName("margin-label-style2");
        actualSalesPriceWOtax.setValue(String.valueOf(round(obj1.getTspWt(),2)).toString());
        actualSalesPriceWOtax.setReadOnly(true);
        verticalLayout.addComponent(actualSalesPriceWOtax);
        verticalLayout.setComponentAlignment(actualSalesPriceWOtax,Alignment.MIDDLE_CENTER);

        actualProfit = new Label();
        actualProfit.addStyleName("margin-label-style2");
        actualProfit.setValue(String.valueOf(round(obj1.getMprofit(),2)).toString());
        actualProfit.setReadOnly(true);
        verticalLayout.addComponent(actualProfit);
        verticalLayout.setComponentAlignment(actualProfit,Alignment.MIDDLE_CENTER);

        actualSalesMargin = new Label();
        actualSalesMargin.addStyleName("margin-label-style2");
        actualSalesMargin.setValue(String.valueOf(round(obj1.getMArginCompute(),2)).toString()+ "%");
        actualSalesMargin.setReadOnly(true);
        verticalLayout.addComponent(actualSalesMargin);
        verticalLayout.setComponentAlignment(actualSalesMargin,Alignment.MIDDLE_CENTER);

        return verticalLayout;
    }

    public Component buildLayout2()
    {
        VerticalLayout verticalLayout=new VerticalLayout();
        verticalLayout.setSpacing(true);
        verticalLayout.setMargin(new MarginInfo(true,true,true,true));

        Label heading=new Label("With Discount");
        heading.addStyleName("margin-label-style");
        verticalLayout.addComponent(heading);

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
        LOG.info("final value" +String.valueOf(round(obj2.getTsp(),2)).toString());
        LOG.info(obj2.getTsp());
        discountedSalesPrice.setValue(String.valueOf(round(obj2.getTsp(),2)).toString());

        verticalLayout.addComponent(discountedSalesPrice);
        verticalLayout.setComponentAlignment(discountedSalesPrice,Alignment.MIDDLE_CENTER);

        discountedSalesPriceWOtax = new Label();
        discountedSalesPriceWOtax.addStyleName("margin-label-style2");
        discountedSalesPriceWOtax.setValue(String.valueOf(round(obj2.getTspWt(),2)).toString());
        discountedSalesPriceWOtax.setReadOnly(true);
        verticalLayout.addComponent(discountedSalesPriceWOtax);
        verticalLayout.setComponentAlignment(discountedSalesPriceWOtax,Alignment.MIDDLE_CENTER);

        discountedProfit = new Label();
        discountedProfit.addStyleName("margin-label-style2");
        discountedProfit.setValue(String.valueOf(round(obj2.getMprofit(),2)).toString());
        discountedProfit.setReadOnly(true);
        verticalLayout.addComponent(discountedProfit);
        verticalLayout.setComponentAlignment(discountedProfit,Alignment.MIDDLE_CENTER);

        discountedSalesMargin = new Label();
        discountedSalesMargin.addStyleName("margin-label-style2");
        discountedSalesMargin.setValue(String.valueOf(round(obj2.getMArginCompute(),2)).toString()+ "%");
        discountedSalesMargin.setReadOnly(true);
        verticalLayout.addComponent(discountedSalesMargin);
        verticalLayout.setComponentAlignment(discountedSalesMargin,Alignment.MIDDLE_CENTER);

        return verticalLayout;
    }

    public Component buildLayout3()
    {
        VerticalLayout verticalLayout=new VerticalLayout();
        verticalLayout.setSpacing(true);
        verticalLayout.setMargin(new MarginInfo(true,true,true,true));

        Label heading=new Label("What If");
        heading.addStyleName("margin-label-style");
        verticalLayout.addComponent(heading);

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
        manualInputSalesPrice.setValue(String.valueOf(round(obj3.getTsp(),2)).toString());
        manualInputSalesPrice.addValueChangeListener(this::calculateSalesPrice);
        verticalLayout.addComponent(manualInputSalesPrice);
        verticalLayout.setComponentAlignment(manualInputSalesPrice,Alignment.MIDDLE_CENTER);

        manualInputSalesPriceWOtax = new Label();
        manualInputSalesPriceWOtax.addStyleName("margin-label-style2");
        manualInputSalesPriceWOtax.setValue(String.valueOf(round(obj3.getTspWt(),2)).toString());
        verticalLayout.addComponent(manualInputSalesPriceWOtax);
        verticalLayout.setComponentAlignment(manualInputSalesPriceWOtax,Alignment.MIDDLE_CENTER);

        manualInputProfitPercentage = new Label();
        manualInputProfitPercentage.addStyleName("margin-label-style2");
        manualInputProfitPercentage.setValue(String.valueOf(round(obj3.getMprofit(),2)).toString());
        verticalLayout.addComponent(manualInputProfitPercentage);
        verticalLayout.setComponentAlignment(manualInputProfitPercentage,Alignment.MIDDLE_CENTER);

        manualInputMargin = new Label();
        manualInputMargin.addStyleName("margin-label-style2");
        manualInputMargin.setValue(String.valueOf(round(obj3.getMArginCompute(),2)).toString() + "%");
        verticalLayout.addComponent(manualInputMargin);
        verticalLayout.setComponentAlignment(manualInputMargin,Alignment.MIDDLE_CENTER);

        return verticalLayout;
    }

    public class margin
    {
        Double Tsp;
        Double TspWt;
        Double Mprofit;
        Double MArginCompute;

        public Double getTsp() {
            return Tsp;
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
        Double TotalwoAcc= totalSalesPrice -AccessoryCost;

        if("DP".equals(status))
        {
            LOG.info("Enter discount Percentage" + manualInputDiscountPercentage.getValue());
            discountPercentage =Double.valueOf(manualInputDiscountPercentage.getValue());
            discountAmount =TotalwoAcc* discountPercentage /100.0;
            manualInputDiscountAmount.setValue(String.valueOf(round(discountAmount,2)));
            LOG.info("Final discount Amount" + discountAmount);
        }
        else if("DA".equals(status))
        {
            discountAmount =Double.valueOf(manualInputDiscountAmount.getValue());
            discountPercentage =(discountAmount /TotalwoAcc)*100;
            manualInputDiscountPercentage.setValue(String.valueOf(round(discountPercentage,2)));
            LOG.info("Final discount Percentage" + discountPercentage);
        }
        Double NewTsp= totalSalesPrice - discountAmount;
        obj3=calculateSalesPriceWithDiscount(obj3,NewTsp);
        manualInputSalesPrice.setValue(String.valueOf(round(NewTsp,2)));
        manualInputSalesPriceWOtax.setValue(String.valueOf(round(obj3.getTspWt(),2)));
        manualInputProfitPercentage.setValue(String.valueOf(round(obj3.getMprofit(),2)));
        manualInputMargin.setValue(String.valueOf(round(obj3.getMArginCompute(),2))+ "%");
    }
}
