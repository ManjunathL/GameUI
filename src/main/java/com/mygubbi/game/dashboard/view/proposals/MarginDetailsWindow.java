package com.mygubbi.game.dashboard.view.proposals;

import com.mygubbi.game.dashboard.ServerManager;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.domain.*;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
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
    Button closeButton;
    TextField Asalesprice;
    TextField AsalespriceWT;
    TextField Smargin;
    TextField Aprofit;

    TextField L2Asalesprice;
    TextField L2AsalespriceWT;
    TextField L2Smargin;
    TextField L2Aprofit;

    TextField L3Asalesprice;
    TextField L3AsalespriceWT;
    TextField L3Smargin;
    TextField L3Aprofit;

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
        setWidth("50%");
        setClosable(false);
        setCaption("Margin Computation");

        updateTotal();

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(new MarginInfo(true, true, true, true));
        Responsive.makeResponsive(this);

        HorizontalLayout horizontalLayout2 = new HorizontalLayout();
        horizontalLayout2.setMargin(new MarginInfo(false, false, false, false));
        horizontalLayout2.setSizeFull();
        horizontalLayout2.addComponent(buildLayout1());
        verticalLayout.addComponent(horizontalLayout2);
        verticalLayout.setComponentAlignment(horizontalLayout2,Alignment.TOP_CENTER);
        horizontalLayout2.setHeightUndefined();

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
                    //non standard
                    NSWoodWorkCost+=modulePrice.getCarcassCost()+modulePrice.getShutterCost();
                }
                else
                {
                    Scount++;
                    SWoodWorkCost+=modulePrice.getCarcassCost()+modulePrice.getShutterCost();
                }
                ShutterCost+=modulePrice.getShutterCost();
                CarcassCost+=modulePrice.getCarcassCost();
                AccessoryCost+=modulePrice.getAccessoryCost();
                HardwareCost+=modulePrice.getHardwareCost();
                LabourCost+=modulePrice.getLabourCost();
            }
        }
        Tsp=NSWoodWorkCost+SWoodWorkCost+HardwareCost+LabourCost+AccessoryCost;

        Tspwt=Tsp*0.8558;
        Mwcstd=SWoodWorkCost/2.46;
        Mwcnstd=NSWoodWorkCost/1.288;
        Mlabour=LabourCost/1.288;
        MHarwareCost=HardwareCost/1.546;
        MAccesory=AccessoryCost/1.546;

        MSum=Mwcstd+Mwcnstd+Mlabour+MHarwareCost+MAccesory;

        obj1=calculateSalesPriceWithDiscount(obj1,Tsp);

        obj2=calculateSalesPriceWithDiscount(obj2,Tsp-proposalVersion.getDiscountAmount());
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

    public Component buildLayout1()
    {
        VerticalLayout verticalLayout=new VerticalLayout();
        verticalLayout.setSpacing(true);
        verticalLayout.setMargin(new MarginInfo(true,true,true,true));

        Label heading=new Label("W/O Discount");
        verticalLayout.addComponent(heading);

        Asalesprice = new TextField("Actual Sales Price");
        Asalesprice.setValue(String.valueOf(round(obj1.getTsp(),2)).toString());
        Asalesprice.setReadOnly(true);
        verticalLayout.addComponent(Asalesprice);
        verticalLayout.setComponentAlignment(Asalesprice,Alignment.MIDDLE_CENTER);

        AsalespriceWT = new TextField("Actual Sales Price W/O Tax");
        AsalespriceWT.setValue(String.valueOf(round(obj1.getTspWt(),2)).toString());
        AsalespriceWT.setReadOnly(true);
        verticalLayout.addComponent(AsalespriceWT);
        verticalLayout.setComponentAlignment(AsalespriceWT,Alignment.MIDDLE_CENTER);

        Aprofit = new TextField("Margin");
        Aprofit.setValue(String.valueOf(round(obj1.getMprofit(),2)).toString());
        Aprofit.setReadOnly(true);
        verticalLayout.addComponent(Aprofit);
        verticalLayout.setComponentAlignment(Aprofit,Alignment.MIDDLE_CENTER);

        Smargin = new TextField("Margin %");
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
        verticalLayout.addComponent(heading);

        L2Asalesprice = new TextField("Actual Sales Price");
        L2Asalesprice.setValue(String.valueOf(round(obj2.getTsp(),2)).toString());

        verticalLayout.addComponent(L2Asalesprice);
        verticalLayout.setComponentAlignment(L2Asalesprice,Alignment.MIDDLE_CENTER);

        L2AsalespriceWT = new TextField("Actual Sales Price W/O Tax");
        L2AsalespriceWT.setValue(String.valueOf(round(obj2.getTspWt(),2)).toString());
        L2AsalespriceWT.setReadOnly(true);
        verticalLayout.addComponent(L2AsalespriceWT);
        verticalLayout.setComponentAlignment(L2AsalespriceWT,Alignment.MIDDLE_CENTER);

        L2Aprofit = new TextField("Margin");
        L2Aprofit.setValue(String.valueOf(round(obj2.getMprofit(),2)).toString());
        L2Aprofit.setReadOnly(true);
        verticalLayout.addComponent(L2Aprofit);
        verticalLayout.setComponentAlignment(L2Aprofit,Alignment.MIDDLE_CENTER);

        L2Smargin = new TextField("Margin %");
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

        Label heading=new Label("Manual");
        verticalLayout.addComponent(heading);

        L3Asalesprice = new TextField("Actual Sales Price");
        L3Asalesprice.addValueChangeListener(this::calculateSalesPrice);
        verticalLayout.addComponent(L3Asalesprice);
        verticalLayout.setComponentAlignment(L3Asalesprice,Alignment.MIDDLE_CENTER);

        L3AsalespriceWT = new TextField("Actual Sales Price W/O Tax");
        verticalLayout.addComponent(L3AsalespriceWT);
        verticalLayout.setComponentAlignment(L3AsalespriceWT,Alignment.MIDDLE_CENTER);

        L3Aprofit = new TextField("Margin");
        verticalLayout.addComponent(L3Aprofit);
        verticalLayout.setComponentAlignment(L3Aprofit,Alignment.MIDDLE_CENTER);

        L3Smargin = new TextField("Margin %");
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
}
