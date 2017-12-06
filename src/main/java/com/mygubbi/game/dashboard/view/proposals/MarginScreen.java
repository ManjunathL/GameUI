package com.mygubbi.game.dashboard.view.proposals;

import com.mygubbi.game.dashboard.ServerManager;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.domain.Product;
import com.mygubbi.game.dashboard.domain.ProposalHeader;
import com.mygubbi.game.dashboard.domain.ProposalVersion;
import com.mygubbi.game.dashboard.domain.VersionPriceHolder;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.event.FieldEvents;
import com.vaadin.server.Responsive;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Created by Shruthi on 10/5/2017.
 */
public class MarginScreen extends Window
{
    private ProposalDataProvider proposalDataProvider = ServerManager.getInstance().getProposalDataProvider();
    private static final Logger LOG = LogManager.getLogger(MarginScreen.class);

    private final BeanFieldGroup<Product> binder = new BeanFieldGroup<>(Product.class);
    ProposalVersion proposalVersion;
    ProposalHeader proposalHeader;
    private Date priceDate;
    private String city;
    private String status=null;

    Button closeButton;
    Label actualSalesPrice;
    Label amt;
    Label actualSalesPriceWOtax;
    Label actualCost;
    Label actualSalesMargin;
    Label actualProfit;
    Label profitPercentage;
    Label profitPercentageAmount;

    Label productsCost,addonsCost,totalPrice,hikeLabel;

    Label discountedSalesPrice;
    Label discountedSalesPriceWOtax;
    Label discountedManufacturingCost;
    Label discountedSalesMargin;
    Label discountedProfit;

    Label manualInputSalesPrice;
    TextField manualInputDiscountAmount;
    TextField manualInputDiscountPercentage;
    Label manualInputSalesPriceWOtax;
    Label  manualInputCost;
    Label manualInputMargin;
    Label manualInputProfitPercentage;
    Label per;
    Label wodiscount,whatIf,withDiscount;
    OptionGroup checkProduct;
    VersionPriceHolder versionPriceHolder;
    Double discountPercentage;
    Double discountAmount;

    public static void open(ProposalVersion proposalVersion, ProposalHeader proposalHeader,List<Product> products)
    {
        MarginScreen w=new MarginScreen(proposalVersion,proposalHeader,products);
        UI.getCurrent().addWindow(w);
        w.focus();
    }
    MarginScreen(ProposalVersion proposalVersion,ProposalHeader proposalHeader,List<Product> products)
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
        setSizeFull();

        updateTotal();
        versionPriceHolder=proposalDataProvider.getVersionPrice(proposalVersion);


        VerticalLayout verticalLayout = new VerticalLayout();
        Responsive.makeResponsive(this);

        HorizontalLayout titleLayout=new HorizontalLayout();
        titleLayout.setMargin(new MarginInfo(false, true, false, true));
        titleLayout.setSizeFull();
        Label customerDetailsLabel = new Label("Margin Computation");
        customerDetailsLabel.addStyleName(ValoTheme.LABEL_HUGE);
        customerDetailsLabel.addStyleName(ValoTheme.LABEL_COLORED);
        customerDetailsLabel.addStyleName("products-and-addons-heading-text");
        titleLayout.addComponent(customerDetailsLabel);
        verticalLayout.addComponent(titleLayout);
        verticalLayout.setComponentAlignment(titleLayout,Alignment.TOP_CENTER);

        HorizontalLayout proposaldetailsLayout=new HorizontalLayout();
        proposaldetailsLayout.setMargin(new MarginInfo(false, false, false, false));
        proposaldetailsLayout.setSizeFull();
        verticalLayout.addComponent(buildProposalDetails());
        verticalLayout.addComponent(proposaldetailsLayout);
        verticalLayout.setComponentAlignment(proposaldetailsLayout,Alignment.TOP_CENTER);

        HorizontalLayout mainhorizontalLayout= new HorizontalLayout();
        mainhorizontalLayout.setMargin(new MarginInfo(false, false, false, false));
        mainhorizontalLayout.setSizeFull();
        mainhorizontalLayout.addComponent(buildTsp());
        verticalLayout.addComponent(mainhorizontalLayout);
        verticalLayout.setComponentAlignment(mainhorizontalLayout,Alignment.TOP_CENTER);

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

        /*HorizontalLayout horizontalLayout1 = new HorizontalLayout();
        horizontalLayout2.setMargin(new MarginInfo(false, false, false, false));
        horizontalLayout2.setSizeFull();
        horizontalLayout2.addComponent(buildLayout1());
        verticalLayout.addComponent(horizontalLayout1);
        verticalLayout.setComponentAlignment(horizontalLayout1,Alignment.TOP_CENTER);
        horizontalLayout1.setHeightUndefined();*/

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
        VersionPriceHolder versionPriceHolder=new VersionPriceHolder();
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

    public Component buildProposalDetails()
    {
        HorizontalLayout horizontalLayout=new HorizontalLayout();
        horizontalLayout.setMargin(new MarginInfo(false,true,false,true));
        horizontalLayout.setSpacing(true);
        Label clientName=new Label("Client Name:   " + proposalHeader.getCname());
        clientName.addStyleName("margin-label-style1");
        horizontalLayout.addComponent(clientName);

        Label crmid=new Label("CRM ID:   " +proposalHeader.getCrmId());
        crmid.addStyleName("margin-label-style1");
        horizontalLayout.addComponent(crmid);

        Label quoteNum=new Label("Quotation #:   "+proposalHeader.getQuoteNoNew());
        quoteNum.addStyleName("margin-label-style1");
        horizontalLayout.addComponent(quoteNum);

        Label Versionnum=new Label("Version #:   "+proposalVersion.getVersion());
        Versionnum.addStyleName("margin-label-style1");
        horizontalLayout.addComponent(Versionnum);

        return horizontalLayout;
    }

    public Component buildTsp()
    {
        HorizontalLayout horizontalLayout=new HorizontalLayout();
        horizontalLayout.setMargin(new MarginInfo(false,true,false,true));
        horizontalLayout.setSpacing(true);
        //productsCost=new Label("Products Price:   " + round(totalSalesPrice,2));
        productsCost=new Label("Products Price:   " +versionPriceHolder.getPrPrice());
        productsCost.addStyleName("margin-label-style1");
        horizontalLayout.addComponent(productsCost);

        //addonsCost=new Label("Addons Price:   " +round(addonsTotal,2));
        addonsCost=new Label("Addons Price:   "+versionPriceHolder.getAddonPrice());
        addonsCost.addStyleName("margin-label-style1");
        horizontalLayout.addComponent(addonsCost);

        //totalPrice=new Label("Total Sales Price:   "+round(totalSalesPrice+addonsTotal,2));
        totalPrice=new Label("Total Sales Price:   "+versionPriceHolder.getVrPrice());
        totalPrice.addStyleName("margin-label-style1");
        horizontalLayout.addComponent(totalPrice);

        //hikeLabel=new Label("Hike Price:   "+round(hikeCost,2));
        hikeLabel=new Label("Hike Price:   "+versionPriceHolder.getHikePrice());
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

        Label label21=new Label("Manufacturing Cost");
        label21.addStyleName("margin-label-style");
        verticalLayout.addComponent(label21);
        verticalLayout.setComponentAlignment(label21,Alignment.MIDDLE_CENTER);

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
        //discountedSalesPrice.setValue(String.valueOf(round(obj2.getProductaddonTsp(), 2)).toString());
        discountedSalesPrice.setValue(String.valueOf(versionPriceHolder.getVrPriceAfterDiscount()));

        verticalLayout.addComponent(discountedSalesPrice);
        verticalLayout.setComponentAlignment(discountedSalesPrice,Alignment.MIDDLE_CENTER);

        discountedSalesPriceWOtax = new Label();
        discountedSalesPriceWOtax.addStyleName("margin-label-style2");
        //discountedSalesPriceWOtax.setValue(String.valueOf(round(obj2.getProductaddontspwt(),2)).toString());
        discountedSalesPriceWOtax.setValue(String.valueOf(versionPriceHolder.getVrPriceWoTax()));
        discountedSalesPriceWOtax.setReadOnly(true);
        verticalLayout.addComponent(discountedSalesPriceWOtax);
        verticalLayout.setComponentAlignment(discountedSalesPriceWOtax,Alignment.MIDDLE_CENTER);

        discountedManufacturingCost = new Label();
        discountedManufacturingCost.addStyleName("margin-label-style2");
        //discountedManufacturingCost.setValue(String.valueOf(round(obj2.getProductaddonManufactingCost(),2)).toString());
        discountedManufacturingCost.setValue(String.valueOf(versionPriceHolder.getVrCost()));
        discountedManufacturingCost.setReadOnly(true);
        verticalLayout.addComponent(discountedManufacturingCost);
        verticalLayout.setComponentAlignment(discountedManufacturingCost,Alignment.MIDDLE_CENTER);

        discountedProfit = new Label();
        discountedProfit.addStyleName("margin-label-style2");
        //discountedProfit.setValue(String.valueOf(round(obj2.getProductaddonProfit(),2)).toString());
        discountedProfit.setValue(String.valueOf(versionPriceHolder.getVrProfit()));
        discountedProfit.setReadOnly(true);
        verticalLayout.addComponent(discountedProfit);
        verticalLayout.setComponentAlignment(discountedProfit,Alignment.MIDDLE_CENTER);

        discountedSalesMargin = new Label();
        discountedSalesMargin.addStyleName("margin-label-style2");
        //discountedSalesMargin.setValue(String.valueOf(round(obj2.getProductaddonMargin(),2)).toString()+ "%");
        discountedSalesMargin.setValue(String.valueOf(versionPriceHolder.getVrMargin()));
        discountedSalesMargin.setReadOnly(true);
        verticalLayout.addComponent(discountedSalesMargin);
        verticalLayout.setComponentAlignment(discountedSalesMargin,Alignment.MIDDLE_CENTER);

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
        //manualInputSalesPrice.setValue(String.valueOf(round(obj3.getProductaddonTsp(),2)).toString());
        manualInputSalesPrice.setValue(String.valueOf(versionPriceHolder.getVrPriceAfterDiscount()));
        verticalLayout.addComponent(manualInputSalesPrice);
        verticalLayout.setComponentAlignment(manualInputSalesPrice,Alignment.MIDDLE_CENTER);

        manualInputSalesPriceWOtax = new Label();
        manualInputSalesPriceWOtax.addStyleName("margin-label-style2");
        //manualInputSalesPriceWOtax.setValue(String.valueOf(round(obj3.getProductaddontspwt(),2)).toString());
        manualInputSalesPriceWOtax.setValue(String.valueOf(versionPriceHolder.getVrPriceWoTax()));
        verticalLayout.addComponent(manualInputSalesPriceWOtax);
        verticalLayout.setComponentAlignment(manualInputSalesPriceWOtax,Alignment.MIDDLE_CENTER);

        manualInputCost = new Label();
        manualInputCost.addStyleName("margin-label-style2");
        //manualInputCost.setValue(String.valueOf(round(obj3.getProductaddonManufactingCost(),2)).toString());
        manualInputCost.setValue(String.valueOf(versionPriceHolder.getVrCost()));
        verticalLayout.addComponent(manualInputCost);
        verticalLayout.setComponentAlignment( manualInputCost,Alignment.MIDDLE_CENTER);

        manualInputProfitPercentage = new Label();
        manualInputProfitPercentage.addStyleName("margin-label-style2");
        //manualInputProfitPercentage.setValue(String.valueOf(round(obj3.getProductaddonProfit(),2)).toString());
        manualInputProfitPercentage.setValue(String.valueOf(versionPriceHolder.getVrProfit()));
        verticalLayout.addComponent(manualInputProfitPercentage);
        verticalLayout.setComponentAlignment(manualInputProfitPercentage,Alignment.MIDDLE_CENTER);

        manualInputMargin = new Label();
        manualInputMargin.addStyleName("margin-label-style2");
        //manualInputMargin.setValue(String.valueOf(round(obj3.getProductaddonMargin(),2)).toString() + "%");
        manualInputMargin.setValue(String.valueOf(versionPriceHolder.getVrMargin()));
        verticalLayout.addComponent(manualInputMargin);
        verticalLayout.setComponentAlignment(manualInputMargin,Alignment.MIDDLE_CENTER);

        return verticalLayout;
    }
    private void checkSelectedValue(Property.ValueChangeEvent valueChangeEvent)
    {

        if(checkProduct.getValue()=="Product")
        {

            profitPercentage.setValue(String.valueOf(proposalVersion.getDiscountPercentage()));
            profitPercentageAmount.setValue(String.valueOf(proposalVersion.getDiscountAmount()));
            discountedSalesPrice.setValue(String.valueOf(versionPriceHolder.getPrPriceAfterDiscount()));
            discountedSalesPriceWOtax.setValue(String.valueOf(versionPriceHolder.getPrPriceWoTax()));
            discountedProfit.setValue(String.valueOf(versionPriceHolder.getPrProfit()));
            discountedSalesMargin.setValue(String.valueOf(versionPriceHolder.getPrMargin()));
            discountedManufacturingCost.setValue(String.valueOf(versionPriceHolder.getPrCost()));

            manualInputDiscountPercentage.setValue(String.valueOf(proposalVersion.getDiscountPercentage()));
            manualInputDiscountAmount.setValue(String.valueOf(proposalVersion.getDiscountAmount()));
            manualInputSalesPrice.setValue(String.valueOf(versionPriceHolder.getPrPriceAfterDiscount()));
            manualInputSalesPriceWOtax.setValue(String.valueOf(versionPriceHolder.getPrPriceWoTax()));
            manualInputProfitPercentage.setValue(String.valueOf(versionPriceHolder.getPrProfit()));
            manualInputMargin.setValue(String.valueOf(versionPriceHolder.getPrMargin()));
            manualInputCost.setValue(String.valueOf(versionPriceHolder.getPrCost()));
        }
        else if(checkProduct.getValue()=="Addon")
        {
            profitPercentage.setValue(String.valueOf(proposalVersion.getDiscountPercentage()));
            profitPercentageAmount.setValue(String.valueOf(proposalVersion.getDiscountAmount()));
            discountedSalesPrice.setValue(String.valueOf(versionPriceHolder.getAddonPriceAfterDiscount()));
            discountedSalesPriceWOtax.setValue(String.valueOf(versionPriceHolder.getAddonPriceWoTax()));
            discountedProfit.setValue(String.valueOf(versionPriceHolder.getAddonProfit()));
            discountedSalesMargin.setValue(String.valueOf(versionPriceHolder.getAddonMargin()));
            discountedManufacturingCost.setValue(String.valueOf(versionPriceHolder.getAddonCost()));

            manualInputDiscountPercentage.setValue(String.valueOf(proposalVersion.getDiscountPercentage()));
            manualInputDiscountAmount.setValue(String.valueOf(proposalVersion.getDiscountAmount()));
            manualInputSalesPrice.setValue(String.valueOf(versionPriceHolder.getAddonPriceAfterDiscount()));
            manualInputSalesPriceWOtax.setValue(String.valueOf(versionPriceHolder.getAddonPriceWoTax()));
            manualInputProfitPercentage.setValue(String.valueOf(versionPriceHolder.getAddonProfit()));
            manualInputMargin.setValue(String.valueOf(versionPriceHolder.getAddonMargin()));
            manualInputCost.setValue(String.valueOf(versionPriceHolder.getAddonCost()));
        }
        else if(checkProduct.getValue()=="Product & Addon")
        {
            profitPercentage.setValue(String.valueOf(proposalVersion.getDiscountPercentage()));
            profitPercentageAmount.setValue(String.valueOf(proposalVersion.getDiscountAmount()));
            discountedSalesPrice.setValue(String.valueOf(versionPriceHolder.getVrPriceAfterDiscount()));
            discountedSalesPriceWOtax.setValue(String.valueOf(versionPriceHolder.getVrPriceWoTax()));
            discountedProfit.setValue(String.valueOf(versionPriceHolder.getVrProfit()));
            discountedSalesMargin.setValue(String.valueOf(versionPriceHolder.getVrMargin()));
            discountedManufacturingCost.setValue(String.valueOf(versionPriceHolder.getVrCost()));

            manualInputDiscountPercentage.setValue(String.valueOf(proposalVersion.getDiscountPercentage()));
            manualInputDiscountAmount.setValue(String.valueOf(proposalVersion.getDiscountAmount()));
            manualInputSalesPrice.setValue(String.valueOf(versionPriceHolder.getVrPriceAfterDiscount()));
            manualInputSalesPriceWOtax.setValue(String.valueOf(versionPriceHolder.getVrPriceWoTax()));
            manualInputProfitPercentage.setValue(String.valueOf(versionPriceHolder.getVrProfit()));
            manualInputMargin.setValue(String.valueOf(versionPriceHolder.getVrMargin()));
            manualInputCost.setValue(String.valueOf(versionPriceHolder.getVrCost()));
        }
    }

    private void onFocusToDiscountPercentage(FieldEvents.FocusEvent event)
    {
        status="DP";
    }
    private void onFocusToDiscountAmount(FieldEvents.FocusEvent event)
    {
        status="DA";
    }
    private void onDiscountAmountValueChange(Property.ValueChangeEvent valueChangeEvent) {
        if("DA".equals(status))
        {
            calculateDiscount(proposalVersion);
        }
    }

    private void onDiscountPercentageValueChange(Property.ValueChangeEvent valueChangeEvent) {
        if("DP".equals(status))
        {
            calculateDiscount(proposalVersion);
        }
    }

    private void calculateDiscount(ProposalVersion proposalVersion)
    {
        ProposalVersion copyVersion = new ProposalVersion();
        copyVersion.setVersion(proposalVersion.getVersion());
        copyVersion.setFromVersion(proposalVersion.getFromVersion());
        copyVersion.setProposalId(proposalVersion.getProposalId());
        copyVersion.setTitle(proposalVersion.getTitle());
        copyVersion.setRemarks(proposalVersion.getRemarks());
        copyVersion.setFinalAmount(proposalVersion.getFinalAmount());
        copyVersion.setStatus(proposalVersion.getStatus());
        copyVersion.setInternalStatus(proposalVersion.getInternalStatus());
        copyVersion.setToVersion(proposalVersion.getToVersion());
        copyVersion.setDiscountAmount(proposalVersion.getDiscountAmount());
        copyVersion.setDiscountPercentage(proposalVersion.getDiscountPercentage());
        copyVersion.setAmount(proposalVersion.getAmount());
        copyVersion.setProposalId(proposalVersion.getProposalId());
        copyVersion.setBusinessDate(proposalVersion.getBusinessDate());

        java.util.Date date =proposalHeader.getCreatedOn();
        java.util.Date currentDate = new java.util.Date(117 ,3,20,0,0,00);
        // proposalVersion.setDiscountPercentage(Double.parseDouble(manualInputDiscountPercentage.getValue()));

        VersionPriceHolder versionPriceHolderForDiscountOveride;
        if(checkProduct.getValue()=="Product") {

            /*if("DP".equals(status))
            {
                manualInputDiscountAmount.setValue(String.valueOf("0"));
            }
            else if("DA".equals(status))
            {
                manualInputDiscountPercentage.setValue(String.valueOf("0"));
            }*/
            if("DP".equals(status))
            {

                discountPercentage =Double.valueOf(manualInputDiscountPercentage.getValue());
                if (date.after(currentDate)) {
                    discountAmount = versionPriceHolder.getPrPrice() * discountPercentage / 100.0;
                }
                else
                {
                    discountAmount = (versionPriceHolder.getCostWoAccessories()) * discountPercentage / 100.0;

                }
                manualInputDiscountAmount.setValue(String.valueOf(round(discountAmount,2)));
            }
            else if("DA".equals(status))
            {
                discountAmount =Double.valueOf(manualInputDiscountAmount.getValue());
                if (date.after(currentDate)) {
                    discountPercentage =(discountAmount /versionPriceHolder.getPrPrice())*100;
                }
                else
                {
                    discountPercentage =(discountAmount /(versionPriceHolder.getCostWoAccessories()))*100;
                }

                manualInputDiscountPercentage.setValue(String.valueOf(round(discountPercentage,2)));
            }
            copyVersion.setDiscountPercentage(discountPercentage);
            copyVersion.setDiscountAmount(discountAmount);
            //versionPriceHolderForDiscountOveride=new VersionPriceHolder();
            versionPriceHolderForDiscountOveride=proposalDataProvider.getVersionPrice(copyVersion);
            manualInputSalesPrice.setValue(String.valueOf(versionPriceHolderForDiscountOveride.getPrPriceAfterDiscount()));
            manualInputSalesPriceWOtax.setValue(String.valueOf(versionPriceHolderForDiscountOveride.getPrPriceWoTax()));
            manualInputProfitPercentage.setValue(String.valueOf(versionPriceHolderForDiscountOveride.getPrProfit()));
            manualInputMargin.setValue(String.valueOf(versionPriceHolderForDiscountOveride.getPrMargin()));
            manualInputCost.setValue(String.valueOf(versionPriceHolderForDiscountOveride.getPrCost()));
        }
        else if(checkProduct.getValue()=="Addon")
        {
            if("DP".equals(status))
            {
                manualInputDiscountAmount.setValue("0.0");
            }
            else if("DA".equals(status))
            {
                manualInputDiscountPercentage.setValue("0.0");
            }
            copyVersion.setDiscountPercentage(Double.parseDouble("0.0"));
            copyVersion.setDiscountAmount(Double.parseDouble("0.0"));
            versionPriceHolderForDiscountOveride=new VersionPriceHolder();

            versionPriceHolderForDiscountOveride=proposalDataProvider.getVersionPrice(copyVersion);

            manualInputSalesPrice.setValue(String.valueOf(versionPriceHolderForDiscountOveride.getAddonPriceAfterDiscount()));
            manualInputSalesPriceWOtax.setValue(String.valueOf(versionPriceHolderForDiscountOveride.getAddonPriceWoTax()));
            manualInputProfitPercentage.setValue(String.valueOf(versionPriceHolderForDiscountOveride.getAddonProfit()));
            manualInputMargin.setValue(String.valueOf(versionPriceHolderForDiscountOveride.getAddonMargin()));
            manualInputCost.setValue(String.valueOf(versionPriceHolderForDiscountOveride.getAddonCost()));
        }
        else if(checkProduct.getValue()=="Product & Addon")
        {
            /*if("DP".equals(status))
            {
                manualInputDiscountAmount.setValue(String.valueOf("0"));
            }
            else if("DA".equals(status))
            {
                manualInputDiscountPercentage.setValue(String.valueOf("0"));
            }*/

            if("DP".equals(status))
            {
                discountPercentage = Double.valueOf(manualInputDiscountPercentage.getValue());
                if (date.after(currentDate)) {
                    discountAmount =versionPriceHolder.getPrPrice()* discountPercentage /100.0;
                }
                else
                {
                    discountAmount =(versionPriceHolder.getCostWoAccessories())* discountPercentage /100.0;
                }
                manualInputDiscountAmount.setValue(String.valueOf(round(discountAmount,2)));
            }
            else if("DA".equals(status))
            {
                discountAmount =Double.valueOf(manualInputDiscountAmount.getValue());
                if (date.after(currentDate)) {
                    discountPercentage = (discountAmount / versionPriceHolder.getPrPrice()) * 100;
                }
                else
                {
                    discountPercentage = (discountAmount / (versionPriceHolder.getCostWoAccessories())) * 100;
                }
                manualInputDiscountPercentage.setValue(String.valueOf(round(discountPercentage,2)));
            }

            copyVersion.setDiscountPercentage(discountPercentage);
            copyVersion.setDiscountAmount(discountAmount);
            versionPriceHolderForDiscountOveride=new VersionPriceHolder();
            versionPriceHolderForDiscountOveride=proposalDataProvider.getVersionPrice(copyVersion);

            manualInputSalesPrice.setValue(String.valueOf(versionPriceHolderForDiscountOveride.getVrPriceAfterDiscount()));
            manualInputSalesPriceWOtax.setValue(String.valueOf(versionPriceHolderForDiscountOveride.getVrPriceWoTax()));
            manualInputProfitPercentage.setValue(String.valueOf(versionPriceHolderForDiscountOveride.getVrProfit()));
            manualInputMargin.setValue(String.valueOf(versionPriceHolderForDiscountOveride.getVrMargin()));
            manualInputCost.setValue(String.valueOf(versionPriceHolderForDiscountOveride.getVrCost()));
        }
    }

    private double round(double value, int places)
    {
        if (places < 0)
        {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
