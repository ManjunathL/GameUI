package com.mygubbi.game.dashboard.view.proposals;

import com.mygubbi.game.dashboard.ServerManager;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.domain.*;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.mygubbi.game.dashboard.event.ProposalEvent;
import com.mygubbi.game.dashboard.view.NotificationUtil;
import com.vaadin.data.Property;
import com.vaadin.data.util.converter.StringToDoubleConverter;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.FieldEvents;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vaadin.dialogs.ConfirmDialog;
import us.monoid.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Created by Shruthi on 12/12/2017.
 */
public class Miscellaneous extends Window
{
    private ProposalDataProvider proposalDataProvider = ServerManager.getInstance().getProposalDataProvider();
    private static final Logger LOG = LogManager.getLogger(Miscellaneous.class);
    ProposalVersion proposalVersion;
    ProposalHeader proposalHeader;
    private Date priceDate;
    private String city;
    Label PHCRate,DCCRate,FPCRate;
    TextField DCCQTY,FPCQTY;
    Label PHCQTY;
    CheckBox PHCcheck,DCCcheck,FPCcheck;
    Label DCCAmount,FPCAmount;
    Label PHCAmount;
    Label servicesTotal;
    double projectHandlingChargesRate,deepCleaningChargesRate,floorProtectionChargesRate;
    Label projectHandlingLabel,deepClearingLabel,floorProtectionLabel;
    String userEmail = ((User) VaadinSession.getCurrent().getAttribute(User.class.getName())).getEmail();
    private TextField discountPercentage;
    private TextField productdiscountPercentage;
    private TextField discountAmount;
    private TextField productdiscountAmount;
    private Label grandTotal;
    private Label productgrandTotal;
    private String status=null;
    private String saveVersionFlag = "no";
    Label totalWithoutDiscount;
    Label producttotalWithoutDiscount;
    private Label discountTotal;
    private Label totalAfterDiscount;
    private Label producttotalAfterDiscount;
    VersionPriceHolder versionPriceHolder;
    Double discountPercentageValue;
    Double discountAmountValue;
    private TextArea remarksTextArea;

    public static void open(ProposalVersion proposalVersion, ProposalHeader proposalHeader)
    {
        Miscellaneous miscellaneous=new Miscellaneous(proposalVersion,proposalHeader);
        UI.getCurrent().addWindow(miscellaneous);
        miscellaneous.focus();
    }
    Miscellaneous(ProposalVersion proposalVersion,ProposalHeader proposalHeader)
    {
        DashboardEventBus.register(this);
        this.proposalHeader=proposalHeader;
        this.proposalVersion=proposalVersion;
        LOG.info("proposal version in miscellaneous class " +proposalVersion);
        this.priceDate=proposalHeader.getPriceDate();
        this.city = proposalHeader.getPcity();
        if (this.priceDate == null)
        {
            this.priceDate = new Date(System.currentTimeMillis());
        }
        setModal(true);
        setSizeFull();
        versionPriceHolder=proposalDataProvider.getVersionPrice(proposalVersion);
        LOG.info("version price holder in  constructor " +versionPriceHolder);
        VerticalLayout verticalLayout = new VerticalLayout();
        Responsive.makeResponsive(this);

        updateTotal();

        HorizontalLayout titleLayout=new HorizontalLayout();
        titleLayout.setMargin(new MarginInfo(false, true, true, true));
        titleLayout.setSizeFull();
        Label customerDetailsLabel = new Label("Miscellaneous Charges");
        customerDetailsLabel.addStyleName(ValoTheme.LABEL_HUGE);
        customerDetailsLabel.addStyleName(ValoTheme.LABEL_COLORED);
        customerDetailsLabel.addStyleName("products-and-addons-heading-text");
        titleLayout.addComponent(customerDetailsLabel);
        verticalLayout.addComponent(titleLayout);
        verticalLayout.setComponentAlignment(titleLayout,Alignment.TOP_CENTER);

       /* Component servicesLayout1=getTestLayout();
        verticalLayout.addComponent(servicesLayout1);*/

        Component amountsLayout = getProductsAmountLayout();
        this.discountPercentage.addFocusListener(this::onFocusToDiscountPercentage);
        this.discountAmount.addFocusListener(this::onFocusToDiscountAmount);
        this.grandTotal.addValueChangeListener(this::onGrandTotalValueChange);
        this.discountPercentage.addValueChangeListener(this::onDiscountPercentageValueChange);
        this.discountAmount.addValueChangeListener(this::onDiscountAmountValueChange);
        verticalLayout.addComponent(amountsLayout);

        Component addonsLayout=getAddonsLayout();
        verticalLayout.addComponent(addonsLayout);

        Component servicesLayout=getServicesLabelLayout();
        verticalLayout.addComponent(servicesLayout);

        HorizontalLayout horizontalLayout2 = new HorizontalLayout();
        horizontalLayout2.setMargin(new MarginInfo(false, false, false, false));
        horizontalLayout2.setSizeFull();
        horizontalLayout2.addComponent(buildServiceHeading());
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

        /*HorizontalLayout horizontalLayout6 = new HorizontalLayout();
        horizontalLayout2.setMargin(new MarginInfo(false, false, false, false));
        horizontalLayout2.setSizeFull();
        horizontalLayout2.addComponent(buildLayout5());
        verticalLayout.addComponent(horizontalLayout6);
        verticalLayout.setComponentAlignment(horizontalLayout6,Alignment.TOP_CENTER);
        horizontalLayout2.setHeightUndefined();*/

        HorizontalLayout horizontalLayout4 = new HorizontalLayout();
        horizontalLayout2.setMargin(new MarginInfo(false, false, false, false));
        horizontalLayout2.setSizeFull();
        horizontalLayout2.addComponent(buildLayout3());
        verticalLayout.addComponent(horizontalLayout4);
        verticalLayout.setComponentAlignment(horizontalLayout4,Alignment.TOP_CENTER);
        horizontalLayout2.setHeightUndefined();
        PHCQTY.addValueChangeListener(this::projectHandlingchargesQuantityChanged);
        DCCQTY.addValueChangeListener(this::deepCleaningchargesQuantityChanged);
        FPCQTY.addValueChangeListener(this::floorProtectionQuantityChanged);

        HorizontalLayout horizontalLayout5 = new HorizontalLayout();
        horizontalLayout2.setMargin(new MarginInfo(false, false, false, false));
        horizontalLayout2.setSizeFull();
        horizontalLayout2.addComponent(buildLayout4());
        verticalLayout.addComponent(horizontalLayout5);
        verticalLayout.setComponentAlignment(horizontalLayout5,Alignment.TOP_CENTER);
        horizontalLayout2.setHeightUndefined();

        Component serviceTotalLayout=getServicesTotal();
        verticalLayout.addComponent(serviceTotalLayout);

        Component totalLayout=getTotalLayout();
        verticalLayout.addComponent(totalLayout);

        /*Component OptionDescriptionLayout = buildOptionLayout();
        verticalLayout.addComponent(OptionDescriptionLayout);*/

        Component componentactionbutton = buildActionButtons();
        verticalLayout.addComponent(componentactionbutton);

        setContent(verticalLayout);

    }

    private void onDiscountAmountValueChange(Property.ValueChangeEvent valueChangeEvent) {
        if ("DA".equals(status)) {
            calculateDiscount(proposalVersion);
        }
    }

    private void onDiscountPercentageValueChange(Property.ValueChangeEvent valueChangeEvent) {
        if ("DP".equals(status)) {
            calculateDiscount(proposalVersion);
        }
    }

    private void onGrandTotalValueChange(Property.ValueChangeEvent valueChangeEvent) {
        status = "DP";
        saveVersionFlag = "Yes";
        calculateDiscount(proposalVersion);
    }

    private void onFocusToDiscountPercentage(FieldEvents.FocusEvent event) {
        LOG.info("DP focused in misceleous");
        status = "DP";
    }

    private void onFocusToDiscountAmount(FieldEvents.FocusEvent event) {
        LOG.info("DA focused in misceleous");
        status = "DA";
    }

    private Component getAddonsLayout()
    {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(new MarginInfo(false, true, false, true));

        Label addonTitle = new Label("Addons ");
        addonTitle.setStyleName("products-and-addons-label-text");
        verticalLayout.addComponent(addonTitle);
        verticalLayout.setComponentAlignment(addonTitle, Alignment.TOP_LEFT);

        HorizontalLayout vlayout = new HorizontalLayout();
        FormLayout left = new FormLayout();
        totalWithoutDiscount = new Label("Addon Price:");
        left.addComponent(totalWithoutDiscount);
        totalWithoutDiscount.addStyleName("amount-text-label1");
        totalWithoutDiscount.addStyleName("margin-top-18");
        totalWithoutDiscount.addStyleName("v-label-amount-text-label1");
        vlayout.addComponent(left);

        FormLayout left1 = new FormLayout();
        Label addontTotal = new Label();
        addontTotal.setConverter(getAmountConverter());
        addontTotal.setReadOnly(true);
        left1.addComponent(addontTotal);
        addontTotal.setConverter(getAmountConverter());
        addontTotal.setValue(String.valueOf(versionPriceHolder.getAddonPrice()));
        addontTotal.setReadOnly(true);
        addontTotal.addStyleName("amount-text-label1");
        addontTotal.addStyleName("v-label-amount-text-label1");
        addontTotal.addStyleName("margin-top-18");
        vlayout.addComponent(left1);

        verticalLayout.addComponent(vlayout);
        return verticalLayout;
    }
    private  Component getServicesLabelLayout()
    {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(new MarginInfo(false, true, true, true));

        Label addonTitle = new Label("Services ");
        addonTitle.setStyleName("products-and-addons-label-text");
        verticalLayout.addComponent(addonTitle);
        verticalLayout.setComponentAlignment(addonTitle, Alignment.TOP_LEFT);

        return verticalLayout;

    }
    private Component getServicesTotal()
    {
        HorizontalLayout verticalLayout = new HorizontalLayout();
        verticalLayout.setMargin(new MarginInfo(false, true, true, true));
        verticalLayout.setSpacing(true);

        Label seriveLabel=new Label("Service Total Price : ");
        seriveLabel.setStyleName("products-and-addons-label-text");
        verticalLayout.addComponent(seriveLabel);
        verticalLayout.setComponentAlignment(seriveLabel,Alignment.TOP_RIGHT);

        Label servicesAmount=new Label();
        servicesAmount.setValue(String.valueOf(versionPriceHolder.getFloorProtectionAmount()+versionPriceHolder.getProjectHandlingAmount()+versionPriceHolder.getDeepClearingAmount()));
        verticalLayout.addComponent(servicesAmount);

        verticalLayout.setComponentAlignment(servicesAmount,Alignment.TOP_RIGHT);

        return verticalLayout;
    }
    private Component getTotalLayout()
    {
        HorizontalLayout horizontalLayout=new HorizontalLayout();
        horizontalLayout.setMargin(new MarginInfo(false,true,true,true));
        horizontalLayout.setSpacing(true);

        Label productLabel=new Label("Total Price (A+B+C): ");
        horizontalLayout.addComponent(productLabel);
        productLabel.addStyleName("amount-text-label1");
        productLabel.addStyleName("v-label-amount-text-label1");
        horizontalLayout.addComponent(productLabel);

        Label productamount= new Label(String.valueOf(versionPriceHolder.getVrPrice()));
        horizontalLayout.addComponent(productamount);
        productamount.addStyleName("amount-text-label1");
        productamount.addStyleName("v-label-amount-text-label1");
        horizontalLayout.addComponent(productamount);

        return horizontalLayout;
    }
    private Component getProductsAmountLayout() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(new MarginInfo(false, true, false, true));

        Label addonTitle = new Label("Modular Products ");
        addonTitle.setStyleName("products-and-addons-label-text");
        verticalLayout.addComponent(addonTitle);
        verticalLayout.setComponentAlignment(addonTitle, Alignment.TOP_LEFT);

        HorizontalLayout vlayout = new HorizontalLayout();
        FormLayout left = new FormLayout();
        producttotalWithoutDiscount = new Label("Product Price:");
        left.addComponent(producttotalWithoutDiscount);
        producttotalWithoutDiscount.addStyleName("amount-text-label1");
        producttotalWithoutDiscount.addStyleName("v-label-amount-text-label1");
        producttotalWithoutDiscount.addStyleName("margin-top-18");
        vlayout.addComponent(left);

        FormLayout left1 = new FormLayout();
        this.productgrandTotal = new Label();
        this.productgrandTotal.setConverter(getAmountConverter());
        this.productgrandTotal.setReadOnly(true);
        left1.addComponent(productgrandTotal);
        this.productgrandTotal.setValue(String.valueOf(versionPriceHolder.getPrPrice()));
        productgrandTotal.addStyleName("amount-text-label1");
        productgrandTotal.addStyleName("v-label-amount-text-label1");
        productgrandTotal.addStyleName("margin-top-18");
        vlayout.addComponent(left1);

        FormLayout left2 = new FormLayout();
        Label Discount = new Label("Discount % :");
        Discount.addStyleName("amount-text-label1");
        Discount.addStyleName("v-label-amount-text-label1");
        Discount.addStyleName("margin-top-18");
        left2.addComponent(Discount);
        vlayout.addComponent(left2);

        FormLayout left3 = new FormLayout();
        this.productdiscountPercentage = new TextField();
        productdiscountPercentage.addStyleName("amount-text-label1");
        productdiscountPercentage.addStyleName("v-label-amount-text-label1");
        productdiscountPercentage.addStyleName("margin-top-18");
        this.productdiscountPercentage.setConverter(new StringToDoubleConverter());
        this.productdiscountPercentage.setValue(String.valueOf(proposalVersion.getDiscountPercentage()));
        this.productdiscountPercentage.setNullRepresentation("0");

        left3.addComponent(productdiscountPercentage);
        vlayout.addComponent(left3);

        FormLayout left4 = new FormLayout();
        Label DiscountAmount = new Label("Discount Amount :");
        left4.addComponent(DiscountAmount);
        DiscountAmount.addStyleName("amount-text-label1");
        DiscountAmount.addStyleName("v-label-amount-text-label1");
        DiscountAmount.addStyleName("margin-top-18");
        vlayout.addComponent(left4);

        FormLayout left5 = new FormLayout();
        this.productdiscountAmount = new TextField();
        this.productdiscountAmount.setConverter(new StringToDoubleConverter());
        this.productdiscountAmount.setValue(String.valueOf(proposalVersion.getDiscountAmount()));
        //this.discountAmount.setNullRepresentation("0");
        left5.addComponent(productdiscountAmount);
        productdiscountAmount.addStyleName("amount-text-label1");
        productdiscountAmount.addStyleName("v-label-amount-text-label1");
        productdiscountAmount.addStyleName("margin-top-18");
        vlayout.addComponent(left5);

        FormLayout left6 = new FormLayout();
        Label totalAfterDiscount = new Label("Total After Discount :");
        left6.addComponent(totalAfterDiscount);
        totalAfterDiscount.addStyleName("amount-text-label1");
        totalAfterDiscount.addStyleName("v-label-amount-text-label1");
        totalAfterDiscount.addStyleName("margin-top-18");
        vlayout.addComponent(left6);

        FormLayout left7 = new FormLayout();
        this.producttotalAfterDiscount = new Label();
        this.producttotalAfterDiscount.setConverter(getAmountConverter());
        LOG.info("versin price aftere discount " +String.valueOf(versionPriceHolder.getPrPriceAfterDiscount()));
        this.producttotalAfterDiscount.setValue(String.valueOf(versionPriceHolder.getPrPriceAfterDiscount()));
        //this.discountTotal.setValue(proposalVersion.getDiscountAmount());
        this.producttotalAfterDiscount.setReadOnly(true);
        left7.addComponent(this.producttotalAfterDiscount);
        producttotalAfterDiscount.addStyleName("amount-text-label1");
        producttotalAfterDiscount.addStyleName("v-label-amount-text-label1");
        producttotalAfterDiscount.addStyleName("margin-top-18");
        vlayout.addComponent(left7);

        verticalLayout.addComponent(vlayout);
        return verticalLayout;
    }
    private void projectHandlingAppliedChanged(Property.ValueChangeEvent valueChangeEvent)
    {
        LOG.info("project Handling value change " +valueChangeEvent.getProperty().getValue());
        if(valueChangeEvent.getProperty().getValue().equals(true))
        {
            LOG.info("project handling amount chnaged " +versionPriceHolder.getProjectHandlingAmount());
            PHCAmount.setValue(String.valueOf(versionPriceHolder.getProjectHandlingAmount()));
            status="DP";
            calculateDiscount(proposalVersion);
        }else {
            PHCAmount.setValue("0");
        }
    }
    private void deepClearingAppliedChanged(Property.ValueChangeEvent valueChangeEvent)
    {
        if(valueChangeEvent.getProperty().getValue().equals(true))
        {
            DCCQTY.setReadOnly(false);
            DCCAmount.setValue("0");
        }else
        {
            DCCQTY.setReadOnly(true);
        }
    }
    private void floorProtectionAppliedChanged(Property.ValueChangeEvent valueChangeEvent)
    {
        if(valueChangeEvent.getProperty().getValue().equals(true))
        {
            FPCQTY.setReadOnly(false);
            FPCAmount.setValue("0");
        }else
        {
            FPCQTY.setReadOnly(true);
        }
    }
    private void projectHandlingchargesQuantityChanged(Property.ValueChangeEvent valueChangeEvent)
    {
        PHCAmount.setValue(String.valueOf(Double.valueOf(PHCQTY.getValue()) * projectHandlingChargesRate));
        calculateDiscount(proposalVersion);
    }
    private void deepCleaningchargesQuantityChanged(Property.ValueChangeEvent valueChangeEvent)
    {
        DCCAmount.setValue(String.valueOf(Double.valueOf(DCCQTY.getValue()) * deepCleaningChargesRate));
        proposalVersion.setDeepClearingQty(Double.valueOf(DCCQTY.getValue()));
        status="DP";
        calculateDiscount(proposalVersion);
    }
    private void floorProtectionQuantityChanged(Property.ValueChangeEvent valueChangeEvent)
    {
        FPCAmount.setValue(String.valueOf(Double.valueOf(FPCQTY.getValue()) * floorProtectionChargesRate));
        proposalVersion.setFloorProtectionSqft(Double.valueOf(FPCQTY.getValue()));
        status="DP";
        calculateDiscount(proposalVersion);
    }

    public Component buildServiceHeading()
    {

        VerticalLayout verticalLayout=new VerticalLayout();
        verticalLayout.setSpacing(true);
        verticalLayout.setSpacing(true);
        verticalLayout.setSpacing(true);
        verticalLayout.setMargin(new MarginInfo(false,true,false,true));

        Label heading=new Label("Service Title");
        heading.addStyleName("margin-label-style1");
        verticalLayout.addComponent(heading);

        projectHandlingLabel=new Label("Project Handling Charges");
        projectHandlingLabel.addStyleName("margin-label-style2");
        verticalLayout.addComponent(projectHandlingLabel);
        verticalLayout.setComponentAlignment(projectHandlingLabel,Alignment.MIDDLE_CENTER);

        deepClearingLabel=new Label("Deep Clearing Charges");
        deepClearingLabel.addStyleName("margin-label-style2");
        verticalLayout.addComponent(deepClearingLabel);
        verticalLayout.setComponentAlignment(deepClearingLabel,Alignment.MIDDLE_CENTER);

        floorProtectionLabel=new Label("Floor Protection Charges(per Sqft)");
        floorProtectionLabel.addStyleName("margin-label-style2");
        verticalLayout.addComponent(floorProtectionLabel);
        verticalLayout.setComponentAlignment(floorProtectionLabel,Alignment.MIDDLE_CENTER);

        return verticalLayout;
    }

    public Component buildLayout2()
    {
        VerticalLayout verticalLayout=new VerticalLayout();
        verticalLayout.setSpacing(true);
        verticalLayout.setMargin(new MarginInfo(false,true,false,true));

        Label heading=new Label();
        heading.addStyleName("margin-label-style");
        heading.setValue("Rate");

        verticalLayout.addComponent(heading);
        PHCRate =new Label();
        PHCRate.addStyleName("margin-label-style2");
        PHCRate.setValue(String.valueOf(projectHandlingChargesRate));
        verticalLayout.addComponent(PHCRate);

        DCCRate =new Label();
        DCCRate.addStyleName("margin-label-style2");
        DCCRate.setValue(String.valueOf(deepCleaningChargesRate));
        verticalLayout.addComponent(DCCRate);

        FPCRate = new Label();
        FPCRate.addStyleName("margin-label-style2");
        FPCRate.setReadOnly(true);
        FPCRate.setValue(String.valueOf(floorProtectionChargesRate));
        verticalLayout.addComponent(FPCRate);
        verticalLayout.setComponentAlignment(FPCRate,Alignment.MIDDLE_CENTER);

        return verticalLayout;
    }
    public Component buildLayout5()
    {
        VerticalLayout verticalLayout=new VerticalLayout();
        verticalLayout.setSpacing(true);
        verticalLayout.setMargin(new MarginInfo(false,true,false,true));

        Label heading=new Label("Applicable");
        heading.addStyleName("margin-label-style2");
        heading.setValue("Applicable");
        verticalLayout.addComponent(heading);

        PHCcheck=new CheckBox("");
        PHCcheck.setValue(Boolean.valueOf(proposalVersion.getProjectHandlingChargesApplied()));
        verticalLayout.addComponent(PHCcheck);

        DCCcheck=new CheckBox("");
        DCCcheck.setValue(Boolean.valueOf(proposalVersion.getDeepClearingChargesApplied()));
        verticalLayout.addComponent(DCCcheck);

        FPCcheck=new CheckBox("");
        FPCcheck.setValue(Boolean.valueOf(proposalVersion.getFloorProtectionChargesApplied()));
        verticalLayout.addComponent(FPCcheck);

        PHCcheck.addValueChangeListener(this::projectHandlingAppliedChanged);
        DCCcheck.addValueChangeListener(this::deepClearingAppliedChanged);
        FPCcheck.addValueChangeListener(this::floorProtectionAppliedChanged);
        return verticalLayout;
    }

    public Component buildLayout3()
    {
        VerticalLayout verticalLayout=new VerticalLayout();
        verticalLayout.setSpacing(true);
        verticalLayout.setMargin(new MarginInfo(false,true,false,true));


        Label heading=new Label("Quantity");
        heading.addStyleName("margin-label-style");
        verticalLayout.addComponent(heading);

        PHCQTY =new Label();
        PHCQTY.addStyleName("heighttext");
        PHCQTY.addStyleName("margin-label-style2");
        PHCQTY.setValue(String.valueOf(versionPriceHolder.getVrPriceAfterDiscount()));
        verticalLayout.addComponent(PHCQTY);

        DCCQTY =new TextField();
        DCCQTY.addStyleName("margin-label-style2");
        DCCQTY.addStyleName("heighttext");
        DCCQTY.setValue(String.valueOf(proposalVersion.getDeepClearingQty()));
        DCCQTY.setReadOnly(true);

        verticalLayout.addComponent(DCCQTY);

        FPCQTY = new TextField();
        FPCQTY.addStyleName("margin-label-style2");
        FPCQTY.addStyleName("heighttext");
        FPCQTY.setValue(String.valueOf(proposalVersion.getFloorProtectionSqft()));
        FPCQTY.setReadOnly(true);

        verticalLayout.addComponent(FPCQTY);

        return verticalLayout;
    }

    public Component buildLayout4()
    {
        VerticalLayout verticalLayout=new VerticalLayout();
        verticalLayout.setSpacing(true);
        verticalLayout.setMargin(new MarginInfo(false,true,false,true));

        Label heading=new Label();
        heading.addStyleName("margin-label-style");
        heading.setValue("Amount");
        verticalLayout.addComponent(heading);

        PHCAmount =new Label();
        PHCAmount.addStyleName("heighttext");
        PHCAmount.addStyleName("margin-label-style2");
        //PHCAmount.setValue(String.valueOf(Double.valueOf(PHCQTY.getValue()) * projectHandlingChargesRate));
        PHCAmount.setValue(String.valueOf(proposalVersion.getProjectHandlingAmount()));
        verticalLayout.addComponent(PHCAmount);

        DCCAmount =new Label();
        DCCAmount.addStyleName("margin-label-style2");
        DCCAmount.addStyleName("heighttext");
        //DCCAmount.setValue(String.valueOf(Double.valueOf(DCCQTY.getValue()) * deepCleaningChargesRate));
        DCCAmount.setValue(String.valueOf(proposalVersion.getDeepClearingAmount()));
        verticalLayout.addComponent(DCCAmount);

        FPCAmount = new Label();
        FPCAmount.addStyleName("margin-label-style2");
        FPCAmount.addStyleName("heighttext");
        //FPCAmount.setValue(String.valueOf(Double.valueOf(FPCQTY.getValue()) * floorProtectionChargesRate));
        FPCAmount.setValue(String.valueOf(proposalVersion.getFloorProtectionAmount()));
        verticalLayout.addComponent(FPCAmount);

        return verticalLayout;
    }


    private Component buildActionButtons() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setMargin(new MarginInfo(true, true, true, true));
        horizontalLayout.setSizeFull();

        HorizontalLayout right = new HorizontalLayout();

        Button saveButton = new Button("Save");
        saveButton.addStyleName(ValoTheme.BUTTON_SMALL);
        saveButton.addStyleName("margin-right-10-for-headerlevelbutton");
        saveButton.addClickListener(this::save);
        right.addComponent(saveButton);
        right.setComponentAlignment(saveButton, Alignment.MIDDLE_RIGHT);

        Button closeButton = new Button("Close");
        closeButton.addStyleName(ValoTheme.BUTTON_SMALL);
        closeButton.addStyleName("margin-right-10-for-headerlevelbutton");
        closeButton.addClickListener(this::close);
        right.addComponent(closeButton);
        right.setComponentAlignment(closeButton, Alignment.MIDDLE_RIGHT);

        horizontalLayout.addComponent(right);
        horizontalLayout.setComponentAlignment(right, Alignment.MIDDLE_CENTER);

        return horizontalLayout;
    }

    private void save(Button.ClickEvent clickEvent)
    {
       /* try
        {
            String string1="{\"services\": [{\"proposalId\": " + proposalHeader.getId() + "," + "\"fromVersion\": " + proposalVersion.getVersion() + " , " + "\"serviceTitle\":\"" +projectHandlingLabel.getValue() +"\"" + "," + "\"quantity\":" + PHCQTY.getValue() + "," + "\"amount\":" +PHCAmount.getValue()+ "," + "\"updatedBy\":\"" +userEmail+ "\""+ "},";
            String string2="{\"proposalId\": " + proposalHeader.getId() + "," + "\"fromVersion\": " + proposalVersion.getVersion() + " , " + "\"serviceTitle\":\"" +deepClearingLabel.getValue() + "\"" +"," + "\"quantity\":" +DCCQTY.getValue() + "," + "\"amount\":" +DCCAmount.getValue()+ "," + "\"updatedBy\":\"" +userEmail+"\""+"},";
            String string3="{\"proposalId\": " + proposalHeader.getId() + "," + "\"fromVersion\": " + proposalVersion.getVersion() + " , " + "\"serviceTitle\":\"" +floorProtectionLabel.getValue() +"\"" +"," + "\"quantity\":" + FPCQTY.getValue() + "," + "\"amount\":" +FPCAmount.getValue()+ "," + "\"updatedBy\":\"" +userEmail+ "\"" +"}]}";
            StringBuilder servicesJson=new StringBuilder(string1);
            servicesJson.append(string2);
            servicesJson.append(string3);
            //        LOG.info("final JSON " +servicesJson.toString());
            JSONObject response=null;
            response=proposalDataProvider.miscellenous(servicesJson.toString());
            LOG.info("response " +response.getString("status"));
            if(response.getString("status").equals("success"))
            {
                NotificationUtil.showNotification("Saved successfully!", NotificationUtil.STYLE_BAR_SUCCESS_SMALL);
                close();
            }else
            {
                NotificationUtil.showNotification("Error while Saving", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            }
        }
        catch (Exception e)
        {

        }*/
       saveProposalVersion();

    }
    private Component buildOptionLayout() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(new MarginInfo(false, true, false, true));

        HorizontalLayout vlayout = new HorizontalLayout();

        FormLayout left1 = new FormLayout();
        left1.addStyleName("text-area-main-size");
        this.remarksTextArea = new TextArea();
        this.remarksTextArea.setCaption("<h3> Remarks </h3>");
        this.remarksTextArea.setCaptionAsHtml(true);
        remarksTextArea.setValue(proposalVersion.getRemarks());
        remarksTextArea.addValidator(new StringLengthValidator("Remarks must not exceed 255 characters long ", 0, 255, true));
       /* remarksTextArea.setImmediate(true);*/
        remarksTextArea.addStyleName("text-area-size");
        left1.addComponent(remarksTextArea);
        vlayout.addComponent(left1);

        verticalLayout.addComponent(vlayout);
        return verticalLayout;
    }
    private void saveProposalVersion() {
        try {
            LOG.info("Vlaue on save " +PHCcheck.getValue().toString());
            String disAmount = discountAmount.getValue();
            proposalVersion.setAmount(Double.parseDouble(grandTotal.getValue()));
            proposalVersion.setFinalAmount(Double.parseDouble(discountTotal.getValue()));
            proposalVersion.setDiscountAmount(Double.parseDouble(disAmount.replace(",", "")));
            proposalVersion.setDiscountPercentage(Double.parseDouble(discountPercentage.getValue()));
            proposalVersion.setRemarks(remarksTextArea.getValue());
            proposalVersion.setTitle(proposalVersion.getTitle());
            proposalVersion.setProjectHandlingAmount(Double.valueOf(PHCAmount.getValue()));
            proposalVersion.setDeepClearingQty(Double.valueOf(DCCQTY.getValue()));
            proposalVersion.setDeepClearingAmount(Double.valueOf(DCCAmount.getValue()));
            proposalVersion.setFloorProtectionSqft(Double.valueOf(FPCQTY.getValue()));
            proposalVersion.setFloorProtectionAmount(Double.valueOf(FPCAmount.getValue()));
            proposalVersion.setProjectHandlingChargesApplied(PHCcheck.getValue().toString());
            proposalVersion.setDeepClearingChargesApplied(DCCcheck.getValue().toString());
            proposalVersion.setFloorProtectionChargesApplied(FPCcheck.getValue().toString());
            proposalHeader.setStatus(proposalVersion.getStatus());
            proposalHeader.setVersion(proposalVersion.getVersion());

            boolean success = proposalDataProvider.saveProposal(proposalHeader);
            if (success) {
                NotificationUtil.showNotification("Saved successfully!", NotificationUtil.STYLE_BAR_SUCCESS_SMALL);

            } else {
                NotificationUtil.showNotification("Cannot Save Proposal!!", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                return;
            }

            proposalVersion = proposalDataProvider.updateVersion(proposalVersion);
            NotificationUtil.showNotification("Saved successfully!", NotificationUtil.STYLE_BAR_SUCCESS_SMALL);
            DashboardEventBus.post(new ProposalEvent.ServicesCreatedEvent(proposalVersion));
            close();
        } catch (Exception e) {
            NotificationUtil.showNotification("Couldn't Save Proposal! Please contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            LOG.info("Exception :" + e.toString());
        }
    }

    private void close(Button.ClickEvent clickEvent)
    {
        ConfirmDialog.show(UI.getCurrent(), "", "Do you want to close this screen? All unsaved data will be lost",
                "Yes", "No", dialog -> {
                    if (!dialog.isCanceled()) {
                        DashboardEventBus.unregister(this);
                        close();

                    }
                });
    }

    private StringToDoubleConverter getAmountConverter() {
        return new StringToDoubleConverter() {
            @Override
            protected Number convertToNumber(String value, Class<? extends Number> targetType, Locale locale) throws ConversionException {
                Number number = super.convertToNumber(value, targetType, locale);
                if (number == null) {
                    return 0;
                } else {
                    return number.longValue();
                }
            }
        };
    }

    private void updateTotal() {

        PriceMaster projectHandlingCharges = proposalDataProvider.getFactorRatePriceDetails("PHC", this.priceDate, this.city);
        projectHandlingChargesRate = projectHandlingCharges.getPrice();
        PriceMaster deepCleaningCharges = proposalDataProvider.getFactorRatePriceDetails("DCC", this.priceDate, this.city);
        deepCleaningChargesRate = deepCleaningCharges.getPrice();
        PriceMaster floorProtectionCharges = proposalDataProvider.getFactorRatePriceDetails("FPC", this.priceDate, this.city);
        floorProtectionChargesRate = floorProtectionCharges.getPrice();
    }

    private void calculateDiscount(ProposalVersion proposalVersion)
    {
        java.util.Date date = proposalHeader.getCreatedOn();
        java.util.Date currentDate = new java.util.Date(117, 3, 20, 0, 0, 00);
        // proposalVersion.setDiscountPercentage(Double.parseDouble(manualInputDiscountPercentage.getValue()));
        LOG.info("version price holder product price " +versionPriceHolder.getPrPrice());
        VersionPriceHolder versionPriceHolderForDiscountOveride;
        if ("DP".equals(status))
        {
            discountPercentageValue = Double.valueOf(discountPercentage.getValue());

            if (date.after(currentDate)) {
                    discountAmountValue = versionPriceHolder.getPrPrice() * discountPercentageValue / 100.0;
                } else {
                    discountAmountValue = (versionPriceHolder.getCostWoAccessories()) * discountPercentageValue / 100.0;

                }
                discountAmount.setValue(String.valueOf(round(discountAmountValue, 2)));

            } else if ("DA".equals(status)) {
                discountAmountValue = Double.valueOf(discountAmount.getValue().replace(",",""));
                if (date.after(currentDate)) {
                    discountPercentageValue = (discountAmountValue / versionPriceHolder.getPrPrice()) * 100;
                } else {
                    discountPercentageValue = (discountAmountValue / (versionPriceHolder.getCostWoAccessories())) * 100;
                }

            discountPercentage.setValue(String.valueOf(round(discountPercentageValue, 2)));
            }
            proposalVersion.setDiscountPercentage(discountPercentageValue);
            proposalVersion.setDiscountAmount(discountAmountValue);
            proposalVersion.setFloorProtectionSqft(Double.parseDouble(FPCQTY.getValue()));
            proposalVersion.setDeepClearingQty(Double.parseDouble(DCCQTY.getValue()));
            versionPriceHolderForDiscountOveride=new VersionPriceHolder();
            versionPriceHolderForDiscountOveride = proposalDataProvider.getVersionPrice(proposalVersion);
            LOG.info("copy version " +versionPriceHolderForDiscountOveride);
            discountTotal.setValue(String.valueOf(versionPriceHolderForDiscountOveride.getVrPriceAfterDiscount()));
            PHCQTY.setValue(String.valueOf(versionPriceHolderForDiscountOveride.getVrPriceAfterDiscount()));
            DCCAmount.setValue(String.valueOf(versionPriceHolderForDiscountOveride.getDeepClearingAmount()));
            FPCAmount.setValue(String.valueOf(versionPriceHolderForDiscountOveride.getFloorProtectionAmount()));
            PHCAmount.setValue(String.valueOf(versionPriceHolderForDiscountOveride.getProjectHandlingAmount()));
            totalAfterDiscount.setValue(String.valueOf(versionPriceHolderForDiscountOveride.getPrPriceAfterDiscount()));
            grandTotal.setValue(String.valueOf(versionPriceHolderForDiscountOveride.getVrPrice()));
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

    private Component getAmountLayout()
    {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(new MarginInfo(false, true, false, true));

        HorizontalLayout vlayout = new HorizontalLayout();

        FormLayout left = new FormLayout();
        Label maintotalWithoutDiscountLabel = new Label("Total Without Discount:");
        left.addComponent(maintotalWithoutDiscountLabel);
        maintotalWithoutDiscountLabel.addStyleName("amount-text-label1");
        maintotalWithoutDiscountLabel.addStyleName("v-label-amount-text-label1");
        maintotalWithoutDiscountLabel.addStyleName("margin-top-18");
        vlayout.addComponent(left);

        FormLayout left1 = new FormLayout();
        this.grandTotal = new Label();
        this.grandTotal.setConverter(getAmountConverter());
        this.grandTotal.setReadOnly(true);
        left1.addComponent(grandTotal);
        this.grandTotal.setValue(String.valueOf(proposalVersion.getAmount()));
        grandTotal.addStyleName("amount-text-label1");
        grandTotal.addStyleName("v-label-amount-text-label1");
        grandTotal.addStyleName("margin-top-18");
        vlayout.addComponent(left1);

        FormLayout left2 = new FormLayout();
        Label Discount = new Label("Discount % :");
        Discount.addStyleName("amount-text-label1");
        Discount.addStyleName("v-label-amount-text-label1");
        Discount.addStyleName("margin-top-18");
        left2.addComponent(Discount);
        vlayout.addComponent(left2);

        FormLayout left3 = new FormLayout();
      //  this.discountPercentage = new Label();
        discountPercentage.addStyleName("amount-text-label1");
        discountPercentage.addStyleName("v-label-amount-text-label1");
        discountPercentage.addStyleName("margin-top-18");
        this.discountPercentage.setConverter(new StringToDoubleConverter());
        this.discountPercentage.setValue(String.valueOf(proposalVersion.getDiscountPercentage()));


        left3.addComponent(discountPercentage);
        vlayout.addComponent(left3);

        FormLayout left4 = new FormLayout();
        Label DiscountAmount = new Label("Discount Amount :");
        left4.addComponent(DiscountAmount);
        DiscountAmount.addStyleName("amount-text-label1");
        DiscountAmount.addStyleName("v-label-amount-text-label1");
        DiscountAmount.addStyleName("margin-top-18");
        vlayout.addComponent(left4);

        FormLayout left5 = new FormLayout();
        //this.discountAmount = new Label();
        this.discountAmount.setConverter(new StringToDoubleConverter());
        this.discountAmount.setValue(String.valueOf(proposalVersion.getDiscountAmount()));
        //this.discountAmount.setNullRepresentation("0");
        left5.addComponent(discountAmount);
        discountAmount.addStyleName("amount-text-label1");
        discountAmount.addStyleName("v-label-amount-text-label1");
        discountAmount.addStyleName("margin-top-18");
        vlayout.addComponent(left5);

        FormLayout left6 = new FormLayout();
        Label totalAfterDiscount = new Label("Total After Discount :");
        left6.addComponent(totalAfterDiscount);
        totalAfterDiscount.addStyleName("amount-text-label1");
        totalAfterDiscount.addStyleName("v-label-amount-text-label1");
        totalAfterDiscount.addStyleName("margin-top-18");
        vlayout.addComponent(left6);

        FormLayout left7 = new FormLayout();
        this.discountTotal = new Label();
        this.discountTotal.setConverter(getAmountConverter());
        this.discountTotal.setValue(String.valueOf(proposalVersion.getFinalAmount()));
        //this.discountTotal.setValue(proposalVersion.getDiscountAmount());
        this.discountTotal.setReadOnly(true);
        left7.addComponent(discountTotal);
        discountTotal.addStyleName("amount-text-label1");
        discountTotal.addStyleName("v-label-amount-text-label1");
        discountTotal.addStyleName("margin-top-18");
        vlayout.addComponent(left7);

        verticalLayout.addComponent(vlayout);
        return verticalLayout;
    }
}