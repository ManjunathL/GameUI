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
    Label Asalesprice;
    Label AsalespriceWT;
    Label Smargin;
    Label Aprofit;
    Label ppercentage;
    Label percentAmount;

    Label L2Asalesprice;
    Label L2AsalespriceWT;
    Label L2Smargin;
    Label L2Aprofit;

    Label L3Asalesprice;
    TextField L3DiscountAmount;
    TextField L3DiscountPercentage;
    Label L3AsalespriceWT;
    Label L3Smargin;
    Label L3Aprofit;

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
    Double Tsp=0.0;
    Double Tspwt=0.0;

    Double Mwcstd=0.0;
    Double Mwcnstd=0.0;
    Double Mlabour=0.0;
    Double MHarwareCost=0.0;
    Double MAccesory=0.0;
    Double MSum=0.0;
    Double MProfit=0.0;
    Double marginCompute=0.0;
    int NScount=0;
    int Scount=0;
    margin obj1=new margin();
    margin obj2=new margin();
    margin obj3=new margin();

    Double discountperc;
    Double disamount;
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
        L3DiscountPercentage.addFocusListener(this::onFocusToDiscountPercentage);
        L3DiscountPercentage.addValueChangeListener(this::onDiscountPercentageValueChange);

        L3DiscountAmount.addFocusListener(this::onFocusToDiscountAmount);
        L3DiscountAmount.addValueChangeListener(this::onDiscountAmountValueChange);
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
                    NScount++;
                    NSWoodWorkCost+=modulePrice.getCarcassCost()+modulePrice.getShutterCost();
                    LOG.info("NSWoodWorkCost " +NSWoodWorkCost);
                }
                else
                {
                    Scount++;
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
        Tsp=NSWoodWorkCost+SWoodWorkCost+HardwareCost+LabourCost+AccessoryCost;

        Tspwt=Tsp*0.8558;
        Mwcstd=SWoodWorkCost/2.46;
        Mwcnstd=NSWoodWorkCost/1.288;
        Mlabour=LabourCost/1.288;
        MHarwareCost=HardwareCost/1.546;
        MAccesory=AccessoryCost/1.546;

        MSum=Mwcstd+Mwcnstd+Mlabour+MHarwareCost+MAccesory;

        LOG.info("TSP " +Tsp+ "Tspwt" +Tspwt + "m std " + Mwcstd + "non std " +Mwcnstd + "labour " + Mlabour + "Hardware "+ MHarwareCost + "Macc" +MAccesory);


        obj1=calculateSalesPriceWithDiscount(obj1,Tsp);

        LOG.info("Tsp " +Tsp+ "Discount Amount" +proposalVersion.getDiscountAmount());
        obj2=calculateSalesPriceWithDiscount(obj2,Tsp-proposalVersion.getDiscountAmount());

        obj3=obj2;
        MProfit=Tspwt-MSum;
        marginCompute=(MProfit/Tspwt)*100;

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
        Double MProfit=Tspwt-MSum;
        Double marginCompute=(MProfit/Tspwt)*100;

        margin.setTsp(Tsp);
        margin.setTspWt(Tspwt);
        margin.setMprofit(MProfit);
        margin.setMArginCompute(marginCompute);

        return margin;
    }
    private void calculateSalesPrice(Property.ValueChangeEvent valueChangeEvent)
    {
        L3Aprofit.setReadOnly(false);
        L3AsalespriceWT.setReadOnly(false);
        L3Smargin.setReadOnly(false);

        Double Tsp=Double.parseDouble(L3Asalesprice.getValue());
        obj3.setTsp(Tsp);
        obj3=calculateSalesPriceWithDiscount(obj3,Tsp);

        L3Aprofit.setValue(String.valueOf(round(obj3.getMprofit(),2)).toString());
        L3AsalespriceWT.setValue(String.valueOf(round(obj3.getTspWt(),2)).toString());
        L3Smargin.setValue(String.valueOf(round(obj3.getMArginCompute(),2)).toString() + "%");

        L3Aprofit.setReadOnly(true);
        L3AsalespriceWT.setReadOnly(true);
        L3Smargin.setReadOnly(true);
    }
    public Component buildTsp()
    {
        HorizontalLayout horizontalLayout=new HorizontalLayout();
        horizontalLayout.setMargin(new MarginInfo(true,true,false,true));
        Label LabelTsp=new Label("Total Sales Price:   " + round(Tsp,2));
        LabelTsp.addStyleName("margin-label-style1");
        //LabelTsp.setValue(String.valueOf(Tsp));
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

        Asalesprice = new Label();
        Asalesprice.addStyleName("margin-label-style2");
        Asalesprice.setValue(String.valueOf(round(obj1.getTsp(),2)).toString());
        Asalesprice.setReadOnly(true);
        verticalLayout.addComponent(Asalesprice);
        verticalLayout.setComponentAlignment(Asalesprice,Alignment.MIDDLE_CENTER);

        AsalespriceWT = new Label();
        AsalespriceWT.addStyleName("margin-label-style2");
        AsalespriceWT.setValue(String.valueOf(round(obj1.getTspWt(),2)).toString());
        AsalespriceWT.setReadOnly(true);
        verticalLayout.addComponent(AsalespriceWT);
        verticalLayout.setComponentAlignment(AsalespriceWT,Alignment.MIDDLE_CENTER);

        Aprofit = new Label();
        Aprofit.addStyleName("margin-label-style2");
        Aprofit.setValue(String.valueOf(round(obj1.getMprofit(),2)).toString());
        Aprofit.setReadOnly(true);
        verticalLayout.addComponent(Aprofit);
        verticalLayout.setComponentAlignment(Aprofit,Alignment.MIDDLE_CENTER);

        Smargin = new Label();
        Smargin.addStyleName("margin-label-style2");
        Smargin.setValue(String.valueOf(round(obj1.getMArginCompute(),2)).toString()+ "%");
        Smargin.setReadOnly(true);
        verticalLayout.addComponent(Smargin);
        verticalLayout.setComponentAlignment(Smargin,Alignment.MIDDLE_CENTER);

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

        ppercentage=new Label();
        ppercentage.addStyleName("margin-label-style2");
        ppercentage.setValue(String.valueOf(proposalVersion.getDiscountPercentage()));
        verticalLayout.addComponent(ppercentage);

        percentAmount=new Label();
        percentAmount.addStyleName("margin-label-style2");
        percentAmount.setValue(String.valueOf(proposalVersion.getDiscountAmount()));
        verticalLayout.addComponent(percentAmount);

        L2Asalesprice = new Label();
        L2Asalesprice.addStyleName("margin-label-style2");
        L2Asalesprice.setReadOnly(true);
        LOG.info("final value" +String.valueOf(round(obj2.getTsp(),2)).toString());
        LOG.info(obj2.getTsp());
        L2Asalesprice.setValue(String.valueOf(round(obj2.getTsp(),2)).toString());

        verticalLayout.addComponent(L2Asalesprice);
        verticalLayout.setComponentAlignment(L2Asalesprice,Alignment.MIDDLE_CENTER);

        L2AsalespriceWT = new Label();
        L2AsalespriceWT.addStyleName("margin-label-style2");
        L2AsalespriceWT.setValue(String.valueOf(round(obj2.getTspWt(),2)).toString());
        L2AsalespriceWT.setReadOnly(true);
        verticalLayout.addComponent(L2AsalespriceWT);
        verticalLayout.setComponentAlignment(L2AsalespriceWT,Alignment.MIDDLE_CENTER);

        L2Aprofit = new Label();
        L2Aprofit.addStyleName("margin-label-style2");
        L2Aprofit.setValue(String.valueOf(round(obj2.getMprofit(),2)).toString());
        L2Aprofit.setReadOnly(true);
        verticalLayout.addComponent(L2Aprofit);
        verticalLayout.setComponentAlignment(L2Aprofit,Alignment.MIDDLE_CENTER);

        L2Smargin = new Label();
        L2Smargin.addStyleName("margin-label-style2");
        L2Smargin.setValue(String.valueOf(round(obj2.getMArginCompute(),2)).toString()+ "%");
        L2Smargin.setReadOnly(true);
        verticalLayout.addComponent(L2Smargin);
        verticalLayout.setComponentAlignment(L2Smargin,Alignment.MIDDLE_CENTER);

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

        L3DiscountPercentage=new TextField();
        L3DiscountPercentage.addStyleName("heighttext");
        L3DiscountPercentage.addStyleName("margin-label-style2");
        L3DiscountPercentage.setValue(String.valueOf(proposalVersion.getDiscountPercentage()));
        verticalLayout.addComponent(L3DiscountPercentage);

        L3DiscountAmount=new TextField();
        L3DiscountAmount.addStyleName("margin-label-style2");
        L3DiscountAmount.addStyleName("heighttext");
        L3DiscountAmount.setValue(String.valueOf(proposalVersion.getDiscountAmount()));
        verticalLayout.addComponent(L3DiscountAmount);

        L3Asalesprice = new Label();
        L3Asalesprice.addStyleName("margin-label-style2");
        L3Asalesprice.setValue(String.valueOf(round(obj3.getTsp(),2)).toString());
        L3Asalesprice.addValueChangeListener(this::calculateSalesPrice);
        verticalLayout.addComponent(L3Asalesprice);
        verticalLayout.setComponentAlignment(L3Asalesprice,Alignment.MIDDLE_CENTER);

        L3AsalespriceWT = new Label();
        L3AsalespriceWT.addStyleName("margin-label-style2");
        L3AsalespriceWT.setValue(String.valueOf(round(obj3.getTspWt(),2)).toString());
        verticalLayout.addComponent(L3AsalespriceWT);
        verticalLayout.setComponentAlignment(L3AsalespriceWT,Alignment.MIDDLE_CENTER);

        L3Aprofit = new Label();
        L3Aprofit.addStyleName("margin-label-style2");
        L3Aprofit.setValue(String.valueOf(round(obj3.getMprofit(),2)).toString());
        verticalLayout.addComponent(L3Aprofit);
        verticalLayout.setComponentAlignment(L3Aprofit,Alignment.MIDDLE_CENTER);

        L3Smargin = new Label();
        L3Smargin.addStyleName("margin-label-style2");
        L3Smargin.setValue(String.valueOf(round(obj3.getMArginCompute(),2)).toString() + "%");
        verticalLayout.addComponent(L3Smargin);
        verticalLayout.setComponentAlignment(L3Smargin,Alignment.MIDDLE_CENTER);

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
        Double TotalwoAcc=Tsp-AccessoryCost;

        if("DP".equals(status))
        {
            LOG.info("Enter discount Percentage" +L3DiscountPercentage.getValue());
            discountperc=Double.valueOf(L3DiscountPercentage.getValue());
            disamount=TotalwoAcc*discountperc/100.0;
            L3DiscountAmount.setValue(String.valueOf(round(disamount,2)));
            LOG.info("Final discount Amount" +disamount);
        }
        else if("DA".equals(status))
        {
            disamount=Double.valueOf(L3DiscountAmount.getValue());
            discountperc=(disamount/TotalwoAcc)*100;
            L3DiscountPercentage.setValue(String.valueOf(round(discountperc,2)));
            LOG.info("Final discount Percentage" +discountperc);
        }
        Double NewTsp=Tsp-disamount;
        obj3=calculateSalesPriceWithDiscount(obj3,NewTsp);
        L3Asalesprice.setValue(String.valueOf(round(NewTsp,2)));
        L3AsalespriceWT.setValue(String.valueOf(round(obj3.getTspWt(),2)));
        L3Aprofit.setValue(String.valueOf(round(obj3.getMprofit(),2)));
        L3Smargin.setValue(String.valueOf(round(obj3.getMArginCompute(),2))+ "%");
    }
}
