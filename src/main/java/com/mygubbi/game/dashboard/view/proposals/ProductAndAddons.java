package com.mygubbi.game.dashboard.view.proposals;

import com.google.common.eventbus.Subscribe;
import com.mygubbi.game.dashboard.ServerManager;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.domain.*;
import com.mygubbi.game.dashboard.domain.JsonPojo.LookupItem;
import com.mygubbi.game.dashboard.event.DashboardEvent;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.mygubbi.game.dashboard.event.ProposalEvent;
import com.mygubbi.game.dashboard.view.FileAttachmentComponent;
import com.mygubbi.game.dashboard.view.NotificationUtil;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.data.util.converter.StringToDoubleConverter;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.SelectionEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.ClickableRenderer;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.gridutil.renderer.EditDeleteButtonValueRenderer;
import org.vaadin.gridutil.renderer.ViewEditDeleteButtonValueRenderer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static com.mygubbi.game.dashboard.domain.Product.TYPE;

/**
 * Created by shruthi on 12-Dec-16.
 */
public class ProductAndAddons extends Window
{
    private static final Logger LOG = LogManager.getLogger(ProductAndAddons.class);

    private ProposalDataProvider proposalDataProvider = ServerManager.getInstance().getProposalDataProvider();
    int disAmount;
    private TextField versionNum;
    private ProductAndAddonSelection productAndAddonSelection;
    private Button addKitchenOrWardrobeButton;
    private Button addFromCatalogueButton;
    private Button addFromProductLibrary;
    private FileAttachmentComponent fileAttachmentComponent;
    private TextField discountPercentage;
    private TextField discountAmount;
    private Label discountTotal;
    private Button addonAddButton;
    private Button customAddonAddButton;
    private TextField ttitle;
    private BeanItemContainer<AddonProduct> addonsContainer;
    private Grid addonsGrid;

    private Grid productsGrid;
    private final BeanFieldGroup<ProposalHeader> binder = new BeanFieldGroup<>(ProposalHeader.class);
    private Button submitButton;
    private ProposalHeader proposalHeader;
    private ProposalVersion proposalVersion;
    private Proposal proposal;
    private Button saveButton;
    private Button closeButton;
    private BeanItemContainer<Product> productContainer;
    private Label grandTotal;
    private String city;


    private TextArea remarksTextArea;

    private String status=null;
    String vid;
    private Button confirmButton;
    private Button designSignOffButton;
    private Button productionSignOffButton;
    Label totalWithoutDiscount;
    String codeForDiscount;
    Double rateForDiscount;
    java.sql.Date priceDate;

    List<Product> products = new ArrayList<>();
    List<AddonProduct> addons = new ArrayList<>();


    public static void open(ProposalHeader proposalHeader, Proposal proposal, String vid, ProposalVersion proposalVersion )
    {
        DashboardEventBus.post(new DashboardEvent.CloseOpenWindowsEvent());
        ProductAndAddons w = new ProductAndAddons(proposalHeader,proposal,vid,proposalVersion);
        UI.getCurrent().addWindow(w);
        w.focus();
        LOG.info("header  " +proposalHeader);

    }

    public ProductAndAddons(ProposalHeader proposalHeader, Proposal proposal, String vid, ProposalVersion proposalVersion)
    {
        this.proposalHeader=proposalHeader;
        this.proposal=proposal;
        this.proposalVersion=proposalVersion;
        rateForDiscount=proposalHeader.getMaxDiscountPercentage();
        this.vid=vid;
        this.priceDate = proposalHeader.getPriceDate();
        this.city = proposalHeader.getPcity();
        this.products = proposalDataProvider.getVersionProducts(proposalHeader.getId(),vid);
        this.addons = proposalDataProvider.getVersionAddons(proposalHeader.getId(),vid);

        if (this.priceDate == null)
        {
            this.priceDate = new java.sql.Date(System.currentTimeMillis());
        }

        this.proposal.setProducts(this.products);
        this.proposal.setAddons(this.addons);
        this.productAndAddonSelection = new ProductAndAddonSelection();
        this.productAndAddonSelection.setProposalId(this.proposalHeader.getId());
        this.productAndAddonSelection.setFromVersion(this.proposalVersion.getVersion());
        LOG.info("On open DA in constructor :" + this.proposalVersion.getDiscountAmount());
        LOG.info("On open DP in constructor :" + this.proposalVersion.getDiscountPercentage());
        DashboardEventBus.register(this);
        setModal(true);
        setSizeFull();
        setResizable(false);
        setClosable(false);


        VerticalLayout verticalLayout = new VerticalLayout();

        setSizeFull();

        verticalLayout.setSpacing(true);
        setContent(verticalLayout);

        Component componentHeading=buildHeading();
        verticalLayout.addComponent(componentHeading);

        Component componentversionDetails=getVersionHeadingLayout();
        verticalLayout.addComponent(componentversionDetails);

        Component componentRemarksDetails=buildRemarks();
        verticalLayout.addComponent(componentRemarksDetails);

        Component componentProductDetails=buildProductDetails();
        verticalLayout.addComponent(componentProductDetails);

        Component componentAddonDetails = buildAddons();
        verticalLayout.addComponent(componentAddonDetails);

        Component amountsLayout = getAmountLayout();
        this.discountPercentage.addFocusListener(this::onFocusToDiscountPercentage);
        this.discountAmount.addFocusListener(this::onFocusToDiscountAmount);
        this.grandTotal.addValueChangeListener(this::onGrandTotalValueChange);
        this.discountPercentage.addValueChangeListener(this::onDiscountPercentageValueChange);
        this.discountAmount.addValueChangeListener(this::onDiscountAmountValueChange);
        verticalLayout.addComponent(amountsLayout);

        Component OptionDescriptionLayout=buildOptionLayout();
        verticalLayout.addComponent(OptionDescriptionLayout);

        Component componentactionbutton=buildActionButtons();
        verticalLayout.addComponent(componentactionbutton);

        LOG.info("Proposal Header :" + proposalHeader.toString());

        //updateTotal();
        calculateTotal();
        handleState();
        handlepackage();
    }


    private Component getVersionHeadingLayout()
    {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setMargin(new MarginInfo(false,true,false,true));
        horizontalLayout.setSizeFull();
        horizontalLayout.setStyleName("v-has-width-forLabel");

        Label title = new Label("Version Details");
        title.setStyleName("products-and-addons-label-text");
        horizontalLayout.addComponent(title);
        horizontalLayout.setComponentAlignment(title,Alignment.TOP_LEFT);

        return horizontalLayout;
    }

    private Component buildHeading()
    {
        HorizontalLayout formLayoutLeft = new HorizontalLayout();
        formLayoutLeft.setSizeFull();
        formLayoutLeft.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        Label customerDetailsLabel = new Label("Product And Addons");
        customerDetailsLabel.addStyleName(ValoTheme.LABEL_HUGE);
        customerDetailsLabel.addStyleName(ValoTheme.LABEL_COLORED);
        customerDetailsLabel.addStyleName("products-and-addons-heading-text");
        formLayoutLeft.addComponent(customerDetailsLabel);

        Component headingbutton=buildHeadingButtons();
        formLayoutLeft.addComponent(headingbutton);

        formLayoutLeft.setComponentAlignment(headingbutton,Alignment.TOP_LEFT);
        return formLayoutLeft;

    }
    private Component buildHeadingButtons()
    {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setMargin(new MarginInfo(false,true,false,true));
        horizontalLayout.setSizeFull();

        HorizontalLayout right = new HorizontalLayout();

        Button quotePdf = new Button("Quote Pdf&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
        quotePdf.setCaptionAsHtml(true);
        quotePdf.setIcon(FontAwesome.DOWNLOAD);
        quotePdf.setStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
        quotePdf.addStyleName(ValoTheme.BUTTON_PRIMARY);
        quotePdf.addStyleName(ValoTheme.BUTTON_SMALL);
        quotePdf.addStyleName("margin-top-for-headerlevelbutton");
        quotePdf.addStyleName("margin-right-10-for-headerlevelbutton");
        quotePdf.setWidth("120px");
        quotePdf.addClickListener(this::checkProductsAndAddonsAvailable);

        StreamResource quotePdfresource = createQuoteResourcePdf();
        FileDownloader fileDownloaderPdf = new FileDownloader(quotePdfresource);
        fileDownloaderPdf.extend(quotePdf);
        right.addComponent(quotePdf);
        right.setComponentAlignment(quotePdf, Alignment.MIDDLE_RIGHT);


        String role = ((User) VaadinSession.getCurrent().getAttribute(User.class.getName())).getRole();

        if (("admin").equals(role))
        {
            Button downloadButton = new Button("Quote&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            downloadButton.setCaptionAsHtml(true);
            downloadButton.setIcon(FontAwesome.DOWNLOAD);
            downloadButton.setStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
            downloadButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
            downloadButton.addStyleName(ValoTheme.BUTTON_SMALL);
            downloadButton.addStyleName("margin-top-for-headerlevelbutton");
            downloadButton.addStyleName("margin-right-10-for-headerlevelbutton");
            downloadButton.setWidth("85px");
            downloadButton.addClickListener(this::checkProductsAndAddonsAvailable);

            StreamResource myResource = createQuoteResource();
            FileDownloader fileDownloader = new FileDownloader(myResource);
            fileDownloader.extend(downloadButton);
            right.addComponent(downloadButton);
            right.setComponentAlignment(downloadButton, Alignment.MIDDLE_RIGHT);
        }

        if (("admin").equals(role) || ("finance").equals(role) || ("departmenthead").equals(role)) {
            Button margin = new Button("Margin&nbsp;&nbsp;");
            margin.setCaptionAsHtml(true);
            margin.setStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
            margin.addStyleName(ValoTheme.BUTTON_PRIMARY);
            margin.addStyleName(ValoTheme.BUTTON_SMALL);
            margin.addStyleName("margin-top-for-headerlevelbutton");
            margin.addStyleName("margin-right-10-for-headerlevelbutton");
            margin.setWidth("120px");
            right.addComponent(margin);
            right.setComponentAlignment(margin, Alignment.MIDDLE_RIGHT);
            margin.addClickListener(
                    clickEvent -> {
                        //MarginComputationWindow.open(proposalVersion);
                        saveVersionAmounts();
                        MarginDetailsWindow.open(proposalVersion,this.proposalHeader, this.products);

                    }
            );
        }
        //margin.addClickListener();

        horizontalLayout.addComponent(right);
        horizontalLayout.setComponentAlignment(right, Alignment.MIDDLE_RIGHT);

        return horizontalLayout;
    }
    private Component buildRemarks()
    {
        FormLayout verticalLayout=new FormLayout();
        verticalLayout.setMargin(new MarginInfo(false,true,false,true));
        HorizontalLayout horizontalLayout1 = new HorizontalLayout();
        horizontalLayout1.setSizeFull();

        horizontalLayout1.addComponent(buildMainFormLayoutLeft());
        horizontalLayout1.addComponent(buildMainFormLayoutCenter());
        horizontalLayout1.addComponent(buildMainFormLayoutRight());
        verticalLayout.addComponent(horizontalLayout1);
        return verticalLayout;
    }

    private Component buildMainFormLayoutLeft()
    {

        FormLayout formLayoutLeft = new FormLayout();
        formLayoutLeft.setSizeFull();
        formLayoutLeft.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        ttitle=new TextField("Title: ");
        ttitle.addStyleName("textfield-background-color");
        ttitle.addStyleName(ValoTheme.LABEL_COLORED);
        ttitle.addStyleName(ValoTheme.TEXTFIELD_HUGE);
        formLayoutLeft.addComponent(ttitle);
        ttitle.setValue(proposalVersion.getTitle());

        return formLayoutLeft;
    }

    private FormLayout buildMainFormLayoutCenter() {

        FormLayout formLayoutLeft = new FormLayout();
        formLayoutLeft.setSizeFull();
        formLayoutLeft.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        versionNum =new TextField("Version # :");
        versionNum.addStyleName(ValoTheme.TEXTFIELD_HUGE);
        versionNum.setValue(String.valueOf(proposalVersion.getVersion()));
        formLayoutLeft.addComponent(versionNum);
        versionNum.setReadOnly(true);

        return formLayoutLeft;
    }

    private FormLayout buildMainFormLayoutRight() {

        FormLayout formLayoutLeft = new FormLayout();
        formLayoutLeft.setSizeFull();
        formLayoutLeft.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        TextField tstatus=new TextField("Status :");
        tstatus.addStyleName(ValoTheme.TEXTFIELD_HUGE);
        tstatus.setValue(proposalVersion.getStatus());
        formLayoutLeft.addComponent(tstatus);
        tstatus.setReadOnly(true);

        return formLayoutLeft;
    }



    private Component buildActionButtons()
    {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setMargin(new MarginInfo(true,true,true,true));
        horizontalLayout.setSizeFull();

        HorizontalLayout right = new HorizontalLayout();

        submitButton = new Button("Publish");
        submitButton.addStyleName(ValoTheme.BUTTON_SMALL);
        submitButton.addStyleName("margin-right-10-for-headerlevelbutton");
        submitButton.addClickListener(this::submit);
        right.addComponent(submitButton);
        right.setComponentAlignment(submitButton, Alignment.MIDDLE_RIGHT);

        confirmButton = new Button("Confirm");
        confirmButton.addStyleName(ValoTheme.BUTTON_SMALL);
        confirmButton.addStyleName("margin-right-10-for-headerlevelbutton");
        confirmButton.addClickListener(this::confirm);
        right.addComponent(confirmButton);
        right.setComponentAlignment(confirmButton, Alignment.MIDDLE_RIGHT);

        designSignOffButton = new Button("Design Sign off");
        designSignOffButton.addStyleName(ValoTheme.BUTTON_SMALL);
        designSignOffButton.addStyleName("margin-right-10-for-headerlevelbutton");
        designSignOffButton.addClickListener(this::confirm);
        right.addComponent(designSignOffButton);
        right.setComponentAlignment(designSignOffButton, Alignment.MIDDLE_RIGHT);

        productionSignOffButton = new Button("Production Sign off");
        productionSignOffButton.addStyleName(ValoTheme.BUTTON_SMALL);
        productionSignOffButton.addStyleName("margin-right-10-for-headerlevelbutton");
        productionSignOffButton.addClickListener(this::confirm);
        right.addComponent(productionSignOffButton);
        right.setComponentAlignment(productionSignOffButton, Alignment.MIDDLE_RIGHT);

        saveButton = new Button("Save");
        saveButton.addStyleName(ValoTheme.BUTTON_SMALL);
        saveButton.addStyleName("margin-right-10-for-headerlevelbutton");
        saveButton.addClickListener(this::save);
        right.addComponent(saveButton);
        right.setComponentAlignment(saveButton, Alignment.MIDDLE_RIGHT);

        closeButton = new Button("Close");
        closeButton.addStyleName(ValoTheme.BUTTON_SMALL);
        closeButton.addStyleName("margin-right-10-for-headerlevelbutton");
        closeButton.addClickListener(this::close);
        right.addComponent(closeButton);
        right.setComponentAlignment(closeButton, Alignment.MIDDLE_RIGHT);

        horizontalLayout.addComponent(right);
        horizontalLayout.setComponentAlignment(right, Alignment.MIDDLE_CENTER);

        return horizontalLayout;
    }


    private Component buildOptionLayout()
    {
        VerticalLayout verticalLayout =new VerticalLayout();
        verticalLayout.setMargin(new MarginInfo(false,true,false,true));

        HorizontalLayout vlayout  = new HorizontalLayout();

        FormLayout left1 = new FormLayout();
        left1.addStyleName("text-area-main-size");
        this.remarksTextArea=new TextArea();
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


    private Component getAmountLayout()
    {
        VerticalLayout verticalLayout =new VerticalLayout();
        verticalLayout.setMargin(new MarginInfo(false,true,false,true));

        HorizontalLayout vlayout  = new HorizontalLayout();

        FormLayout left = new FormLayout();
        totalWithoutDiscount = new Label("Total Without Discount:");
        left.addComponent(totalWithoutDiscount);
        totalWithoutDiscount.addStyleName("amount-text-label1");
        totalWithoutDiscount.addStyleName("v-label-amount-text-label1");
        totalWithoutDiscount.addStyleName("margin-top-18");
        vlayout.addComponent(left);

        FormLayout left1 = new FormLayout();
        this.grandTotal=new Label();
        this.grandTotal.setConverter(getAmountConverter());
        this.grandTotal.setReadOnly(true);
        left1.addComponent(grandTotal);
        this.grandTotal.setConverter(getAmountConverter());
        this.grandTotal.setValue(String.valueOf(proposalVersion.getAmount()));
        this.grandTotal.setReadOnly(true);
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
        this.discountPercentage = new TextField();
        discountPercentage.addStyleName("amount-text-label1");
        discountPercentage.addStyleName("v-label-amount-text-label1");
        discountPercentage.addStyleName("margin-top-18");
        this.discountPercentage.setConverter(new StringToDoubleConverter());
        this.discountPercentage.setValue(String.valueOf(proposalVersion.getDiscountPercentage()));
        this.discountPercentage.setNullRepresentation("0");

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
        this.discountAmount=new TextField();
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
        this.discountTotal=new Label();
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
    private void updateTotal()
    {


        productsGrid.setSelectionMode(Grid.SelectionMode.NONE);
        addonsGrid.setSelectionMode(Grid.SelectionMode.NONE);

        Collection<?> productObjects = productsGrid.getSelectedRows();
        Collection<?> addonObjects = addonsGrid.getSelectedRows();
        boolean anythingSelected = true;
        this.productAndAddonSelection.getProductIds().clear();
        this.productAndAddonSelection.getAddonIds().clear();

        double productsTotal = 0;
        double ProductsTotalWoTax=0;
        double ProdutsMargin=0;
        double ProductsProfit=0;
        double ProductsManufactureAmount=0;

        double addonsTotalWOTax=0;
        double addonsMargin=0;
        double addonsProfit=0;
        double addonsManufactureAmount=0;

        if (productObjects.size() == 0) {
            anythingSelected = false;
            productObjects = this.productsGrid.getContainerDataSource().getItemIds();
        }



        for (Object object : productObjects) {
            Double amount = (Double) this.productsGrid.getContainerDataSource().getItem(object).getItemProperty(Product.AMOUNT).getValue();
            productsTotal += amount;

            Double amountWotax=(Double) this.productsGrid.getContainerDataSource().getItem(object).getItemProperty(Product.AMOUNTWOTAX).getValue();
            ProductsTotalWoTax +=amountWotax;

            Double manufactureAmount= (Double) this.productsGrid.getContainerDataSource().getItem(object).getItemProperty(Product.MANUFACTUREAMOUNT).getValue();
            ProductsManufactureAmount +=manufactureAmount;

            Double profit= (Double) this.productsGrid.getContainerDataSource().getItem(object).getItemProperty(Product.PROFIT).getValue();
            ProductsProfit +=profit;

            Integer id = (Integer) this.productsGrid.getContainerDataSource().getItem(object).getItemProperty(Product.ID).getValue();
            if (anythingSelected) {
                this.productAndAddonSelection.getProductIds().add(id);
            }
        }

        ProdutsMargin=(ProductsManufactureAmount / ProductsTotalWoTax)*100;
        if(Double.isNaN(ProdutsMargin))
        {
            ProdutsMargin=0.0;
        }

        LOG.info(" productsTotal" +productsTotal+ "ProductsTotalWoTax"  +ProductsTotalWoTax+ "Margin" +ProdutsMargin+ "profit" +ProductsProfit + "ProductsManufactureAmount" +ProductsManufactureAmount);
        proposalVersion.setProfit(ProductsProfit);
        proposalVersion.setMargin(ProdutsMargin);
        proposalVersion.setAmountWotax(ProductsTotalWoTax);
        proposalVersion.setManufactureAmount(ProductsManufactureAmount);

        if (addonObjects.size() == 0) {
            anythingSelected = false;
            addonObjects = this.addonsGrid.getContainerDataSource().getItemIds();
        }

        double addonsTotal = 0;

        for (Object object : addonObjects) {
            Double amount = (Double) this.addonsGrid.getContainerDataSource().getItem(object).getItemProperty(AddonProduct.AMOUNT).getValue();
            addonsTotal += amount;

            Double amountwotax=(Double) this.addonsGrid.getContainerDataSource().getItem(object).getItemProperty(AddonProduct.AMOUNT_WO_TAX).getValue();
            addonsTotalWOTax +=amountwotax;

            Double manufactureamount=(Double) this.addonsGrid.getContainerDataSource().getItem(object).getItemProperty(AddonProduct.SOURCE_PRICE).getValue();
            addonsManufactureAmount+=manufactureamount;

            Double profit=(Double)this.addonsGrid.getContainerDataSource().getItem(object).getItemProperty(AddonProduct.PROFIT).getValue();
            addonsProfit+=profit;

            Integer id = (Integer) this.addonsGrid.getContainerDataSource().getItem(object).getItemProperty(AddonProduct.ID).getValue();

            if (anythingSelected) {
                this.productAndAddonSelection.getAddonIds().add(id);
            }
        }

        addonsMargin=(addonsProfit/addonsTotalWOTax)*100;
        if(Double.isNaN(addonsMargin))
        {
            addonsMargin=0.0;
        }
        LOG.info("Addons Total" +addonsTotal+ "AddonsTotalWoTax"  +addonsTotalWOTax+ "profit" +addonsProfit + "AddonsManufactureAmount" +addonsManufactureAmount + "Addons Margin" +addonsMargin);

        //Double total=productsTotal+addonsTotal;
        Double totalWT=ProductsTotalWoTax+addonsTotalWOTax;
        Double profit=ProductsProfit+addonsProfit;
        Double totalmanufactureamount=ProductsManufactureAmount+addonsManufactureAmount;
        Double finalmargin=(profit/totalWT)*100;
        if(Double.isNaN(finalmargin))
        {
            finalmargin=0.0;
        }
        /*if(Double.isNaN(fin))
        Double finalmargin=(profit/totalWT)*100;*/

        LOG.info("TotalWoTax"  +totalWT+ "profit" +profit + "ManufactureAmount" +totalmanufactureamount +"Margin" +finalmargin);

        proposalVersion.setProfit(round(profit,2));
        proposalVersion.setMargin(round(finalmargin,2));
        proposalVersion.setAmountWotax(round(totalWT,2));
        proposalVersion.setManufactureAmount(round(totalmanufactureamount,2));

        Double totalWoAccessories = 0.0;
        List<Product> products = proposalDataProvider.getVersionProducts(proposalVersion.getProposalId(),proposalVersion.getVersion());
        for (Product product : products) {
            totalWoAccessories += product.getCostWoAccessories();
        }

        Double totalAmount = addonsTotal + productsTotal;
        Double costOfAccessories = productsTotal - totalWoAccessories;

        ProposalHeader proposalHeaderCreateDate = proposalDataProvider.getProposalHeader(this.proposalHeader.getId());

        java.util.Date date = proposalHeaderCreateDate.getCreatedOn();
        java.util.Date currentDate = new Date(117,3,20,0,0,00);
        if (date.after(currentDate))
        {

            refreshDiscountForNewProposals(totalAmount,addonsTotal,productsTotal);
        }
        else {
            refreshDiscountForOldProposals(totalWoAccessories, totalAmount, costOfAccessories, addonsTotal);
        }

    }

    private void refreshDiscountForNewProposals(Double totalAmount, Double addonsTotal, Double productsTotal)
    {
        Double discountPercent=0.0,discountAmount=0.0;
        rateForDiscount=proposalHeader.getMaxDiscountPercentage();
        if("DP".equals(status))
        {
            discountPercent = (Double) this.discountPercentage.getConvertedValue();
            if(discountPercent<=rateForDiscount)
            {
                if (discountPercent == null) {
                    discountPercent = 0.0;
                }
                discountAmount = productsTotal * (discountPercent / 100.0);
                if(Objects.equals(proposalHeader.getPackageFlag(), "Yes"))
                {
                    this.discountAmount.setReadOnly(false);
                    this.discountAmount.setValue(String.valueOf(discountAmount.intValue())+ " ");
                    this.discountAmount.setReadOnly(true);
                }
                else
                {
                    this.discountAmount.setValue(String.valueOf(discountAmount.intValue())+ " ");
                }

                disAmount=discountAmount.intValue();
            }
            else
            {
                NotificationUtil.showNotification("Discount should not exceed " +rateForDiscount.intValue(), NotificationUtil.STYLE_BAR_ERROR_SMALL);
                return;
            }
        }
        else if("DA".equals(status))
        {
            discountAmount = (Double) this.discountAmount.getConvertedValue();
            discountPercent=(discountAmount/productsTotal)*100;
            if(discountPercent<=rateForDiscount)
            {
                this.discountPercentage.setValue(String.valueOf(round(discountPercent, 2)));
            }
            else
            {
                NotificationUtil.showNotification("Discount should not exceed " +rateForDiscount.intValue(), NotificationUtil.STYLE_BAR_ERROR_SMALL);
                return;
            }
        }
        Double totalAfterDiscount = this.round((productsTotal - discountAmount), 0);

        Double grandTotal = totalAfterDiscount + addonsTotal;
        double res=grandTotal-grandTotal%10;

        this.discountTotal.setReadOnly(false);
        this.discountTotal.setValue(String.valueOf(res));

        this.grandTotal.setReadOnly(false);
        this.grandTotal.setValue(totalAmount.intValue() + "");
        this.grandTotal.setReadOnly(true);

        productAndAddonSelection.setDiscountPercentage(proposalVersion.getDiscountPercentage());
        productAndAddonSelection.setDiscountAmount(proposalVersion.getDiscountAmount());

    }



    private void refreshDiscountForOldProposals(Double totalWoAccessories, Double totalAmount, Double costOfAccessories, Double addonsTotal)
    {
        LOG.debug("for old proposal");
        Double discountPercent=0.0,discountAmount=0.0;
        //rateForDiscount=rateForDiscount*100;
        rateForDiscount=proposalHeader.getMaxDiscountPercentage();
        if("DP".equals(status))
        {
            discountPercent = (Double) this.discountPercentage.getConvertedValue();
            if(discountPercent<=rateForDiscount) {
                if (discountPercent == null) {
                    discountPercent = 0.0;
                }

                discountAmount = totalWoAccessories * (discountPercent / 100) ;
                if(Objects.equals(proposalHeader.getPackageFlag(), "Yes"))
                {
                    this.discountAmount.setReadOnly(false);
                    this.discountAmount.setValue(String.valueOf(discountAmount.intValue())+ " ");
                    this.discountAmount.setReadOnly(true);
                }
                else
                {
                    this.discountAmount.setValue(String.valueOf(discountAmount.intValue())+ " ");
                }
                disAmount=discountAmount.intValue();
            }
            else
            {
                NotificationUtil.showNotification("Discount should not exceed " +rateForDiscount.intValue(), NotificationUtil.STYLE_BAR_ERROR_SMALL);
                return;
            }
        }
        else if("DA".equals(status))
        {
            discountAmount = (Double) this.discountAmount.getConvertedValue();
            discountPercent=(discountAmount/totalWoAccessories)*100;
            if(discountPercent<=rateForDiscount) {
                this.discountPercentage.setValue(String.valueOf(round(discountPercent, 2)));
            }
            else
            {
                NotificationUtil.showNotification("Discount should not exceed " +rateForDiscount.intValue(), NotificationUtil.STYLE_BAR_ERROR_SMALL);
                return;
            }
        }
        Double totalAfterDiscount = this.round((totalWoAccessories - discountAmount), 0);

        Double grandTotal = totalAfterDiscount + costOfAccessories + addonsTotal ;
        double res=grandTotal-grandTotal%10;

        this.discountTotal.setReadOnly(false);
        this.discountTotal.setValue(String.valueOf(res));

        this.grandTotal.setReadOnly(false);
        this.grandTotal.setValue(totalAmount.intValue() + "");
        this.grandTotal.setReadOnly(true);

        productAndAddonSelection.setDiscountPercentage(proposalVersion.getDiscountPercentage());
        productAndAddonSelection.setDiscountAmount(proposalVersion.getDiscountAmount());

    }
    private void onDiscountAmountValueChange(Property.ValueChangeEvent valueChangeEvent) {
        if("DA".equals(status))
        {
            updateTotal();
        }
    }

    private void onDiscountPercentageValueChange(Property.ValueChangeEvent valueChangeEvent) {
        if("DP".equals(status))
        {
            updateTotal();
        }
    }

    private void onGrandTotalValueChange(Property.ValueChangeEvent valueChangeEvent)
    {
        status="DP";
        updateTotal();
    }
    private void calculateTotal()
    {
        double productsTotal = 0;
        double addonsTotal = 0;
        double TotalAmount=0;

        this.discountAmount.setValue(String.valueOf(proposalVersion.getDiscountAmount()).replace(",",""));
        this.discountPercentage.setValue(String.valueOf(proposalVersion.getDiscountPercentage()));

        boolean anythingSelected = true;

        for (Product product : this.products) {
            Double amount = product.getAmount();
            productsTotal += amount;
        }

        for (AddonProduct addonProduct : this.addons) {
            Double amount = addonProduct.getAmount();
            addonsTotal += amount;

        }
        TotalAmount=productsTotal+addonsTotal;
        this.grandTotal.setValue(String.valueOf(TotalAmount));

    }
    private Component buildProductDetails() {

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();

        verticalLayout.setMargin(new MarginInfo(true, true, true, true));
        verticalLayout.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSizeFull();
        horizontalLayout.setStyleName("v-has-width-forLabel");

        Label title = new Label("Products Details");
        title.setStyleName("products-and-addons-label-text");
        horizontalLayout.addComponent(title);
        horizontalLayout.setComponentAlignment(title,Alignment.TOP_LEFT);
        verticalLayout.setSpacing(true);

        HorizontalLayout hLayoutInner = new HorizontalLayout();

        addKitchenOrWardrobeButton = new Button("Kitchen/Wardrobe");
        addKitchenOrWardrobeButton.setIcon(FontAwesome.PLUS_CIRCLE);
        addKitchenOrWardrobeButton.addStyleName(ValoTheme.BUTTON_SMALL);
        hLayoutInner.addComponent(addKitchenOrWardrobeButton);
        hLayoutInner.setComponentAlignment(addKitchenOrWardrobeButton,Alignment.TOP_RIGHT);
        hLayoutInner.setSpacing(true);

        addFromCatalogueButton = new Button("From Catalogue");
        addFromCatalogueButton.setIcon(FontAwesome.PLUS_CIRCLE);
        addFromCatalogueButton.addStyleName(ValoTheme.BUTTON_SMALL);
        hLayoutInner.addComponent(addFromCatalogueButton);
        hLayoutInner.setComponentAlignment(addFromCatalogueButton,Alignment.TOP_RIGHT);

        if(Objects.equals(proposalHeader.getBeforeProductionSpecification(), "yes"))
        {
            addFromProductLibrary = new Button("From Product Library");
            addFromProductLibrary.setIcon(FontAwesome.PLUS_CIRCLE);
            addFromProductLibrary.addStyleName(ValoTheme.BUTTON_SMALL);
            hLayoutInner.addComponent(addFromProductLibrary);
            hLayoutInner.setComponentAlignment(addFromProductLibrary,Alignment.TOP_RIGHT);
        }

        horizontalLayout.addComponent(hLayoutInner);
        horizontalLayout.setComponentAlignment(hLayoutInner, Alignment.TOP_RIGHT);

        addKitchenOrWardrobeButton.addClickListener(
                clickEvent -> {
                    Product newProduct = new Product();
                    newProduct.setType(Product.TYPES.CUSTOMIZED.name());
                    newProduct.setProposalId(this.proposalHeader.getId());
                    newProduct.setFromVersion(this.vid);
                    CustomizedProductDetailsWindow.open(ProductAndAddons.this.proposal, newProduct, proposalVersion, proposalHeader);
                }
        );

        addFromCatalogueButton.addClickListener(
                clickEvent -> {
                    CatalogueProduct newProduct = new CatalogueProduct();
                    newProduct.setType(CatalogueProduct.TYPES.CATALOGUE.name());
                    newProduct.setProposalId(this.proposalHeader.getId());
                    newProduct.setFromVersion(this.vid);
                    CatalogItemDetailsWindow.open(ProductAndAddons.this.proposal, newProduct, proposalVersion, proposalHeader);
                }
        );

        if(Objects.equals(proposalHeader.getBeforeProductionSpecification(), "yes"))
        {
            addFromProductLibrary.addClickListener(
                    clickEvent -> {
                        //AllProductsDetailsWindow.open();
                        //Dummy.open(proposal,proposalVersion,proposalHeader);
                        AllProposalLibrary.open(proposal,proposalVersion,proposalHeader);
                    }
            );
        }


        productContainer = new BeanItemContainer<>(Product.class);

        GeneratedPropertyContainer genContainer = createGeneratedProductPropertyContainer();

        productsGrid = new Grid(genContainer);
        productsGrid.setSelectionMode(Grid.SelectionMode.NONE);
        productsGrid.addSelectionListener(this::updateTotal);
        productsGrid.setSizeFull();
        productsGrid.setColumnReorderingAllowed(true);
        productsGrid.setColumns(Product.SEQ,Product.ROOM_CODE, Product.TITLE, "productCategoryText", Product.AMOUNT, TYPE, "actions");

        List<Grid.Column> columns = productsGrid.getColumns();
        int idx = 0;

        columns.get(idx++).setHeaderCaption("Seq");
        columns.get(idx++).setHeaderCaption("Room");
        columns.get(idx++).setHeaderCaption("Title");
        columns.get(idx++).setHeaderCaption("Category");
        columns.get(idx++).setHeaderCaption("Amount");
        columns.get(idx++).setHeaderCaption("Type");
        columns.get(idx++).setHeaderCaption("Actions").setRenderer(new ViewEditDeleteButtonValueRenderer(new ViewEditDeleteButtonValueRenderer.ViewEditDeleteButtonClickListener() {
            @Override
            public void onView(ClickableRenderer.RendererClickEvent rendererClickEvent) {

                if (("Published").equals(proposalVersion.getInternalStatus()) || ("Confirmed").equals(proposalVersion.getInternalStatus()) || ("Locked").equals(proposalVersion.getInternalStatus()) || ("DSO").equals(proposalVersion.getInternalStatus()) || ("PSO").equals(proposalVersion.getInternalStatus()))
                {
                    Notification.show("Cannot copy on Published, Confirmed and Locked versions");
                    return;
                }

                if(("Yes").equals(proposalHeader.getPackageFlag()))
                {
                    Notification.show("Cannot copy Products");
                    return;
                }
                Product p = (Product) rendererClickEvent.getItemId();

                productContainer.removeAllItems();

                List<Product> copy = proposalDataProvider.getVersionProducts(proposalHeader.getId(),proposalVersion.getVersion());
                int length = (copy.size()) + 1;

                Product proposalProductDetails = proposalDataProvider.getProposalProductDetails(p.getId(),p.getFromVersion());
                List<Module> modulesFromOldProduct = proposalProductDetails.getModules();
                Product copyProduct = new Product();
                copyProduct.setType(Product.TYPES.CUSTOMIZED.name());
                copyProduct.setSeq(length);
                copyProduct.setProposalId(proposalHeader.getId());
                copyProduct.setFromVersion(p.getFromVersion());
                copyProduct.setTitle(p.getTitle());
                copyProduct.setProductCategory(p.getProductCategory());
                copyProduct.setProductCategoryCode(p.getProductCategoryCode());
                copyProduct.setRoom(p.getRoom());
                copyProduct.setRoomCode(p.getRoomCode());
                copyProduct.setShutterDesign(p.getShutterDesign());
                copyProduct.setShutterDesignCode(p.getShutterDesignCode());
                copyProduct.setCatalogueName(p.getCatalogueName());
                copyProduct.setCatalogueId(p.getCatalogueId());
                copyProduct.setBaseCarcass(p.getBaseCarcass());
                copyProduct.setBaseCarcassCode(p.getBaseCarcassCode());
                copyProduct.setWallCarcass(p.getWallCarcass());
                copyProduct.setWallCarcassCode(p.getWallCarcassCode());
                copyProduct.setFinishType(p.getFinishType());
                copyProduct.setFinishTypeCode(p.getFinishTypeCode());
                copyProduct.setFinish(p.getFinish());
                copyProduct.setFinishCode(p.getFinishCode());
                copyProduct.setDimension(p.getDimension());
                copyProduct.setAmount(p.getAmount());
                copyProduct.setQuantity(p.getQuantity());
                copyProduct.setType(p.getType());
                copyProduct.setQuoteFilePath(p.getQuoteFilePath());
                copyProduct.setCreatedBy(p.getCreatedBy());
                copyProduct.setCostWoAccessories(p.getCostWoAccessories());
                copyProduct.setProfit(p.getProfit());
                copyProduct.setMargin(p.getMargin());
                copyProduct.setManufactureAmount(p.getManufactureAmount());
                copyProduct.setAmountWoTax(p.getAmountWoTax());
                copyProduct.setModules(modulesFromOldProduct);
                copyProduct.setSource(p.getSource());
                copyProduct.setHinge(p.getHinge());
                copyProduct.setGlass(p.getGlass());
                copyProduct.setKnobType(p.getKnobType());
                copyProduct.setKnobFinish(p.getKnobFinish());
                copyProduct.setKnobImage(p.getKnobImage());
                copyProduct.setHandleType(p.getHandleType());
                copyProduct.setHandleFinish(p.getHandleFinish());
                copyProduct.setHandleImage(p.getHandleImage());
                copyProduct.setHandleTypeSelection(p.getHandleTypeSelection());
                copyProduct.setShutterDesignImage(p.getShutterDesignImage());
                copyProduct.setHandleCode(p.getHandleCode());
                copyProduct.setKnobCode(p.getKnobCode());
                copyProduct.setlConnectorPrice(p.getlConnectorPrice());
                copyProduct.setHandleThickness(p.getHandleThickness());
                copyProduct.setNoOfLengths(p.getNoOfLengths());
                LOG.info("COPIED@"+ copyProduct);
                copyProduct.setAddons(p.getAddons());

                proposalDataProvider.updateProduct(copyProduct);
//                copy.add(copyProduct);

                List<Product> proposalProductUpdated = proposalDataProvider.getVersionProducts(proposalHeader.getId(),p.getFromVersion());

                for (Product updatedProduct : proposalProductUpdated)
                {
                    productContainer.addItem(updatedProduct);
                }
                productsGrid.setContainerDataSource(createGeneratedProductPropertyContainer());
                updateTotal();
                status = "DP";
                saveVersionAmounts();

            }
            @Override
            public void onEdit(ClickableRenderer.RendererClickEvent rendererClickEvent) {

                Product product = (Product) rendererClickEvent.getItemId();

                if (product.getType().equals(Product.TYPES.CUSTOMIZED.name()) || product.getType().equals(Product.TYPES.PRODUCT_LIBRARY.name())) {
                    if (product.getModules().isEmpty()) {
                        Product productDetails = proposalDataProvider.getProposalProductDetails(product.getId(),product.getFromVersion());

                        product.setModules(productDetails.getModules());
                        product.setAddons(productDetails.getAddons());
                    }

                    if (product.getFileAttachmentList().isEmpty()) {
                        List<FileAttachment> productAttachments = proposalDataProvider.getProposalProductDocuments(product.getId());
                        product.setFileAttachmentList(productAttachments);
                    }
                    LOG.info("product details " +product);
                    CustomizedProductDetailsWindow.open(proposal, product, proposalVersion,proposalHeader);
                } else {
                    CatalogueProduct catalogueProduct = new CatalogueProduct();
                    catalogueProduct.populateFromProduct(product);
                    CatalogItemDetailsWindow.open(proposal, catalogueProduct, proposalVersion, proposalHeader);
                }
            }

            @Override
            public void onDelete(ClickableRenderer.RendererClickEvent rendererClickEvent) {

                if (("Published").equals(proposalVersion.getInternalStatus()) || ("Confirmed").equals(proposalVersion.getInternalStatus()) || ("Locked").equals(proposalVersion.getInternalStatus()) || ("DSO").equals(proposalVersion.getInternalStatus()) || ("PSO").equals(proposalVersion.getInternalStatus()))
                {
                    Notification.show("Cannot delete products on Published, Confirmed and Locked versions");
                    return;
                }

                    ConfirmDialog.show(UI.getCurrent(), "", "Are you sure you want to Delete this Product?",
                            "Yes", "No", dialog -> {
                                if (dialog.isConfirmed()) {
                                    Product product = (Product) rendererClickEvent.getItemId();

                                    proposal.getProducts().remove(product);

                                    int seq = product.getSeq();
                                    productContainer.removeAllItems();

                                    proposalDataProvider.deleteProduct(product.getId());

                                    for (Product product1 : proposalDataProvider.getVersionProducts(proposalHeader.getId(),proposalVersion.getVersion())) {
                                        if (product1.getSeq() > seq) {
                                            product1.setSeq(product1.getSeq() - 1);
                                            proposalDataProvider.updateProductSequence(product1.getSeq(),product1.getId());
                                        }
                                    }
                                    List<Product> productsUpdated = proposalDataProvider.getVersionProducts(proposalHeader.getId(),proposalVersion.getVersion());
                                    for (Product updatedProduct : productsUpdated)
                                    {
                                        productContainer.addItem(updatedProduct);
                                    }
                                    productsGrid.setContainerDataSource(createGeneratedProductPropertyContainer());
                                    productsGrid.getSelectionModel().reset();
                                    updateTotal();
                                    NotificationUtil.showNotification("Product deleted successfully.", NotificationUtil.STYLE_BAR_SUCCESS_SMALL);
                                }
                            });
            }
        }));

        if (!proposal.getProducts().isEmpty()) {
            productContainer.addAll(proposal.getProducts());
            productsGrid.sort(Product.SEQ, SortDirection.ASCENDING);
        }
        Label label = new Label("Select Products and click Download Quote/Job Card button to generate output for only the selected Products.");
        label.setStyleName("font-italics");

        verticalLayout.addComponent(horizontalLayout);
        verticalLayout.setSpacing(true);

        verticalLayout.addComponent(productsGrid);

        return verticalLayout;
    }

    private Component buildAddons() {

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        verticalLayout.setMargin(new MarginInfo(true,true,true,true));

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSizeFull();
        horizontalLayout.setStyleName("v-has-width-forLabel");

        Label addonTitle = new Label("Addon Details");
        addonTitle.setStyleName("products-and-addons-label-text");
        horizontalLayout.addComponent(addonTitle);
        horizontalLayout.setComponentAlignment(addonTitle,Alignment.TOP_LEFT);
        verticalLayout.setSpacing(true);

        HorizontalLayout hLayoutInner = new HorizontalLayout();

        addonAddButton = new Button("Add");
        addonAddButton.setIcon(FontAwesome.PLUS_CIRCLE);
        addonAddButton.addStyleName(ValoTheme.BUTTON_SMALL);
        addonAddButton.addClickListener(clickEvent -> {
            AddonProduct addonProduct = new AddonProduct();
            addonProduct.setAdd(true);
            AddonDetailsWindow.open(addonProduct, "Add Addon", true, proposalVersion,proposalHeader);
        });
        hLayoutInner.addComponent(addonAddButton);
        hLayoutInner.setComponentAlignment(addonAddButton,Alignment.TOP_RIGHT);
        hLayoutInner.setSpacing(true);


        customAddonAddButton = new Button("Custom Addon");
        customAddonAddButton.setIcon(FontAwesome.PLUS_CIRCLE);
        customAddonAddButton.addStyleName(ValoTheme.BUTTON_SMALL);
        customAddonAddButton.addClickListener(clickEvent -> {
            AddonProduct addonProduct = new AddonProduct();
            addonProduct.setAdd(true);
            CustomAddonDetailsWindow.open(addonProduct, "Add Addon", true, proposalVersion);
        });
        hLayoutInner.addComponent(customAddonAddButton);
        hLayoutInner.setComponentAlignment(customAddonAddButton,Alignment.TOP_RIGHT);

        horizontalLayout.addComponent(hLayoutInner);
        horizontalLayout.setComponentAlignment(hLayoutInner, Alignment.TOP_RIGHT);

        addonsContainer = new BeanItemContainer<>(AddonProduct.class);

        GeneratedPropertyContainer genContainer = createGeneratedAddonsPropertyContainer();

        addonsGrid = new Grid(genContainer);
        addonsGrid.setSelectionMode(Grid.SelectionMode.NONE);
        addonsGrid.setSizeFull();
        /*addonsGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        addonsGrid.addSelectionListener(this::updateTotal);*/
        addonsGrid.setColumnReorderingAllowed(true);
        addonsGrid.setColumns(AddonProduct.SEQ, AddonProduct.ADDON_CATEGORY_CODE, AddonProduct.PRODUCT_TYPE_CODE, AddonProduct.PRODUCT_SUBTYPE_CODE, AddonProduct.BRAND_CODE,
                AddonProduct.PRODUCT, AddonProduct.UOM, AddonProduct.RATE, AddonProduct.QUANTITY, AddonProduct.AMOUNT, "actions");

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

                if (("Custom Addon").equals(addon.getCategoryCode()))
                {
                    CustomAddonDetailsWindow.open(addon,"Edit Addon",true,proposalVersion);
                }else {
                    AddonDetailsWindow.open(addon, "Edit Addon", true, proposalVersion, proposalHeader);
                }
            }

            @Override
            public void onDelete(ClickableRenderer.RendererClickEvent rendererClickEvent) {

                if (("Published").equals(proposalVersion.getInternalStatus()) || ("Confirmed").equals(proposalVersion.getInternalStatus()) || ("Locked").equals(proposalVersion.getInternalStatus()) || ("DSO").equals(proposalVersion.getInternalStatus()) || ("PSO").equals(proposalVersion.getInternalStatus()))
                {
                    Notification.show("Cannot delete addons on Published, Confirmed and Locked versions");
                    return;
                }

                    ConfirmDialog.show(UI.getCurrent(), "", "Are you sure you want to Delete this Addon?",
                            "Yes", "No", dialog -> {
                                if (dialog.isConfirmed()) {
                                    AddonProduct addon = (AddonProduct) rendererClickEvent.getItemId();
                                    proposalDataProvider.removeProposalAddon(addon.getId());
                                    List<AddonProduct> addons = proposalDataProvider.getVersionAddons(proposalHeader.getId(),proposalVersion.getVersion());

                                    int seq = addon.getSeq();
                                    addonsContainer.removeAllItems();

                                    for (AddonProduct addonProduct : addons) {
                                        if (addonProduct.getSeq() > seq) {
                                            addonProduct.setSeq(addonProduct.getSeq() - 1);
                                            proposalDataProvider.updateProposalAddon(proposalHeader.getId(),addonProduct);
                                        }
                                    }
                                    addonsContainer.addAll(addons);
                                    addonsGrid.setContainerDataSource(createGeneratedAddonsPropertyContainer());
                                    updateTotal();
                                }
                            });

            }
        }));

        verticalLayout.addComponent(horizontalLayout);
        verticalLayout.setSpacing(true);

        verticalLayout.addComponent(addonsGrid);

        List<AddonProduct> existingAddons = proposal.getAddons();
        int seq = 0;
        for (AddonProduct existingAddon : existingAddons) {
            existingAddon.setSeq(++seq);
        }
        addonsContainer.addAll(existingAddons);
        addonsGrid.sort(AddonProduct.SEQ, SortDirection.ASCENDING);

        return verticalLayout;

    }

   /* private void doSalesOrderDownloadValidation(Button.ClickEvent clickEvent) {
        if (this.productAndAddonSelection.getProductIds().size() != 1) {
            NotificationUtil.showNotification("Please select a single product to download SO extract.", NotificationUtil.STYLE_BAR_WARNING_SMALL);
        }
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
    }*/

   /* private String getSOFilename()
    {
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
    }*/
    private boolean checkModuleSeq()
    {
        List<Product> product=proposal.getProducts();
        for(Product product1:product)
        {
            if(product1.getManualSeq()==0)
            {
                NotificationUtil.showNotification("Manual sequence cannot be zero", NotificationUtil.STYLE_BAR_WARNING_SMALL);
                return false;
            }
        }
        return true;
    }
    private void checkProductsAndAddonsAvailable(Button.ClickEvent clickEvent) {

            if (proposal.getProducts().isEmpty() && proposal.getAddons().isEmpty())
            {
                NotificationUtil.showNotification("No products found. Please add product(s) first to generate the Quote.", NotificationUtil.STYLE_BAR_WARNING_SMALL);
            }
           /* if (proposal.getAddons().isEmpty()) {
                NotificationUtil.showNotification("No Addons found.", NotificationUtil.STYLE_BAR_WARNING_SMALL);
            }*/
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

    private StreamResource createQuoteResourcePdf() {

        StreamResource.StreamSource source = () -> {
            if (!proposal.getProducts().isEmpty()) {
                LOG.info("header value" + proposalVersion.getDiscountAmount() + "discount amount" + discountAmount.getValue());
                String replace = discountAmount.getValue().replace(",", "");
                double discountamount = Double.valueOf(replace);
                return getInputStreamPdf();

            } else {
                return null;
            }
        };
        return new StreamResource(source, "Quotation.pdf");
    }

    private InputStream getInputStreamPdf() {
        String replace = discountAmount.getValue().replace(",", "");
        double discountamount= Double.valueOf(replace);

        productAndAddonSelection.setDiscountPercentage(Double.valueOf(this.discountPercentage.getValue()));
        productAndAddonSelection.setDiscountAmount(discountamount);

        String quoteFile = proposalDataProvider.getProposalQuoteFilePdf(this.productAndAddonSelection);

        InputStream input = null;
        try {
            input = new ByteArrayInputStream(FileUtils.readFileToByteArray(new File(quoteFile)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return input;
    }

    private StreamResource createQuoteResource() {

        StreamResource.StreamSource source = () ->
        {
            if (!proposal.getProducts().isEmpty())
            {
                String replace = discountAmount.getValue().replace(",", "");
                double discountamount= Double.valueOf(replace);
                productAndAddonSelection.setDiscountPercentage(Double.valueOf(this.discountPercentage.getValue()));
                productAndAddonSelection.setDiscountAmount(discountamount);

                String quoteFile = proposalDataProvider.getProposalQuoteFile(this.productAndAddonSelection);
                InputStream input = null;
                try {
                    input = new ByteArrayInputStream(FileUtils.readFileToByteArray(new File(quoteFile)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return input;
            }
            else
            {
                return null;
            }
        };
        return new StreamResource(source, "Quotation.xlsx");
    }

    private double round(double value, int places)
    {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


    private void submit(Button.ClickEvent clickEvent) {
        if(proposalHeader.getMaxDiscountPercentage()>=Double.valueOf(discountPercentage.getValue())) {
            try {
                if(proposalVersion.getAmount()== 0)
                {
                    NotificationUtil.showNotification("Total amount is zero,Cannot publish", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                    return;
                }
                binder.commit();

                LOG.info("value in submit" + totalWithoutDiscount.getValue());
                if (remarksTextArea == null || remarksTextArea.isEmpty()) {
                    NotificationUtil.showNotification("Remarks cannot be empty", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                    return;
                } else if (grandTotal.getValue().equals("0")) {
                    NotificationUtil.showNotification("Please add products", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                    return;
                }

                proposalVersion.setStatus(ProposalVersion.ProposalStage.Published.name());
                proposalVersion.setInternalStatus(ProposalVersion.ProposalStage.Published.name());
                proposalHeader.setStatus(proposalVersion.getStatus());
                proposalHeader.setVersion(versionNum.getValue());
                boolean success = proposalDataProvider.saveProposal(proposalHeader);
                if (success) {
                    boolean mapped = true;
                    for (Product product : proposal.getProducts()) {
                        Product populatedProduct = proposalDataProvider.getProposalProductDetails(product.getId(), product.getFromVersion());
                        mapped = populatedProduct.getType().equals(Product.TYPES.CATALOGUE.name()) || (!populatedProduct.getModules().isEmpty());
                        if (!mapped) {
                            break;
                        }
                    }

                    if (!mapped) {
                        NotificationUtil.showNotification("Couldn't Submit. Please ensure all Products have mapped Modules.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                    } else {
                        saveProposalVersion();
                        success = proposalDataProvider.publishVersion(proposalVersion.getVersion(), proposalHeader.getId());
                        if (success) {
                            saveButton.setVisible(false);
                            addKitchenOrWardrobeButton.setVisible(false);
                            addFromCatalogueButton.setVisible(false);
                            addonAddButton.setVisible(false);
                            if(Objects.equals(proposalHeader.getBeforeProductionSpecification(), "yes"))
                            {
                                addFromProductLibrary.setVisible(false);
                            }

                            customAddonAddButton.setVisible(false);
                        /*versionStatus.setValue("Published");*/
                            NotificationUtil.showNotification("Published successfully!", NotificationUtil.STYLE_BAR_SUCCESS_SMALL);
                            handleState();
                        } else {
                            NotificationUtil.showNotification("Couldn't Publish Proposal! Please contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                        }
                    }
                } else {
                    NotificationUtil.showNotification("Couldn't Save Proposal! Please contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                }
            } catch (FieldGroup.CommitException e) {
                NotificationUtil.showNotification("Validation Error, please fill all mandatory fields!", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            }
            proposalDataProvider.updateVersion(proposalVersion);

            if (!(proposalVersion.getVersion().startsWith("2.")))
            {
                SendToCRMOnPublish sendToCRM = updatePriceInCRMOnPublish();
                proposalDataProvider.updateCrmPriceOnPublish(sendToCRM);
            }
            DashboardEventBus.post(new ProposalEvent.VersionCreated(proposalVersion));
            DashboardEventBus.unregister(this);
            close();
        }
        else {
            NotificationUtil.showNotification("Discount should not exceed " +rateForDiscount.intValue(), NotificationUtil.STYLE_BAR_ERROR_SMALL);
            discountAmount.setValue(String.valueOf(proposalVersion.getDiscountAmount()).replace(",",""));
            discountPercentage.setValue(String.valueOf(proposalVersion.getDiscountPercentage()));
        }
    }

    private SendToCRMOnPublish updatePriceInCRMOnPublish() {
        SendToCRMOnPublish sendToCRM = new SendToCRMOnPublish();
        sendToCRM.setOpportunity_name(proposalHeader.getCrmId());
        sendToCRM.setEstimated_project_cost_c(proposalVersion.getFinalAmount());
        sendToCRM.setQuotation_number_c(proposalHeader.getQuoteNoNew());
        LOG.debug("Send to CRM : " + sendToCRM.toString());
        return sendToCRM;
    }

    private SendToCRM updatePriceInCRMOnConfirm() {
        SendToCRM sendToCRM = new SendToCRM();
        sendToCRM.setOpportunity_name(proposalHeader.getCrmId());
        sendToCRM.setFinal_proposal_amount_c(proposalVersion.getFinalAmount());
        sendToCRM.setEstimated_project_cost_c(proposalVersion.getFinalAmount());
        sendToCRM.setQuotation_number_c(proposalHeader.getQuoteNoNew());
        LOG.debug("Send to CRM : " + sendToCRM.toString());

        return sendToCRM;
    }

    private void confirm(Button.ClickEvent clickEvent) {
        if(proposalHeader.getMaxDiscountPercentage()>=Double.valueOf(discountPercentage.getValue()))
        {
            try {
            binder.commit();
            if (remarksTextArea == null || remarksTextArea.isEmpty()) {
                NotificationUtil.showNotification("Remarks cannot be empty", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                return;
            } else if (grandTotal.getValue().equals("0")) {
                NotificationUtil.showNotification("Please add products", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                return;
            }
                String disAmount=discountAmount.getValue();
            proposalVersion.setAmount(Double.parseDouble(grandTotal.getValue()));
            proposalVersion.setDiscountAmount(Double.parseDouble(disAmount.replace(",","")));
            proposalVersion.setDiscountPercentage(Double.parseDouble(discountPercentage.getValue()));
            proposalVersion.setFinalAmount(Double.parseDouble(discountTotal.getValue()));
            proposalVersion.setStatus(ProposalVersion.ProposalStage.Confirmed.name());
            proposalVersion.setInternalStatus(ProposalVersion.ProposalStage.Confirmed.name());
            proposalHeader.setStatus(proposalVersion.getStatus());

            boolean success = proposalDataProvider.saveProposal(proposalHeader);
            if (success) {
                boolean mapped = true;
                for (Product product : proposal.getProducts()) {
                    Product populatedProduct = proposalDataProvider.getProposalProductDetails(product.getId(), product.getFromVersion());
                    mapped = populatedProduct.getType().equals(Product.TYPES.CATALOGUE.name()) || (!populatedProduct.getModules().isEmpty());
                    if (!mapped) {
                        break;
                    }
                }

                if (!mapped) {
                    NotificationUtil.showNotification("Couldn't Submit. Please ensure all Products have mapped Modules.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                } else {
                    String versionNew = String.valueOf(proposalVersion.getVersion());
                    if (versionNew.startsWith("0.")) {
                        proposalVersion.setFromVersion(proposalVersion.getVersion());
                        proposalVersion.setToVersion(proposalVersion.getVersion());
                        proposalVersion.setVersion("1.0");
                        proposalHeader.setStatus(proposalVersion.getStatus());
                        proposalDataProvider.saveProposalOnConfirm(proposalHeader);
                        proposalDataProvider.lockAllPreSalesVersions(ProposalVersion.ProposalStage.Locked.name(), proposalHeader.getId());
                        success = proposalDataProvider.confirmVersion(proposalVersion.getVersion(), proposalHeader.getId(), proposalVersion.getFromVersion(), proposalVersion.getToVersion());
                        proposalDataProvider.updateProposalProductOnConfirm(proposalVersion.getVersion(), proposalVersion.getProposalId(), proposalVersion.getFromVersion());
                        proposalDataProvider.updateProposalAddonOnConfirm(proposalVersion.getVersion(), proposalVersion.getProposalId(), proposalVersion.getFromVersion());
                        proposalDataProvider.updateVersion(proposalVersion);
                        proposalHeader.setVersion(proposalVersion.getVersion());
                        SendToCRM sendToCRM = updatePriceInCRMOnConfirm();
                        proposalDataProvider.updateCrmPrice(sendToCRM);
                        proposalDataProvider.saveProposal(proposalHeader);
                    } else if (versionNew.startsWith("1.")) {
                        proposalVersion.setFromVersion(proposalVersion.getVersion());
                        proposalVersion.setToVersion(proposalVersion.getVersion());
                        proposalVersion.setVersion("2.0");
                        proposalVersion.setStatus(ProposalVersion.ProposalStage.DSO.name());
                        proposalVersion.setInternalStatus(ProposalVersion.ProposalStage.DSO.name());
                        proposalHeader.setStatus(proposalVersion.getStatus());
                        boolean success1 = proposalDataProvider.saveProposal(proposalHeader);
                        proposalDataProvider.lockAllPostSalesVersions(ProposalVersion.ProposalStage.Locked.name(), proposalHeader.getId());
                        success = proposalDataProvider.versionDesignSignOff(proposalVersion.getVersion(), proposalHeader.getId(), proposalVersion.getFromVersion(), proposalVersion.getToVersion());
                        proposalDataProvider.updateProposalProductOnConfirm(proposalVersion.getVersion(), proposalVersion.getProposalId(), proposalVersion.getFromVersion());
                        proposalDataProvider.updateProposalAddonOnConfirm(proposalVersion.getVersion(), proposalVersion.getProposalId(), proposalVersion.getFromVersion());
                        proposalDataProvider.updateVersion(proposalVersion);
                        proposalHeader.setVersion(proposalVersion.getVersion());
                        SendToCRM sendToCRM = updatePriceInCRMOnConfirm();
                        proposalDataProvider.updateCrmPrice(sendToCRM);
                        proposalDataProvider.saveProposal(proposalHeader);


                    } else if (versionNew.startsWith("2.")) {
                        proposalVersion.setToVersion(proposalVersion.getVersion());
                        proposalVersion.setStatus(ProposalVersion.ProposalStage.PSO.name());
                        proposalVersion.setInternalStatus(ProposalVersion.ProposalStage.Locked.name());
                        proposalHeader.setStatus(proposalVersion.getStatus());
                        boolean success1 = proposalDataProvider.saveProposal(proposalHeader);
                        proposalDataProvider.lockAllVersionsExceptPSO(ProposalVersion.ProposalStage.Locked.name(), proposalHeader.getId());
                        success = proposalDataProvider.versionProductionSignOff(proposalVersion.getVersion(), proposalHeader.getId(), proposalVersion.getFromVersion(), proposalVersion.getToVersion());
                        proposalDataProvider.updateVersion(proposalVersion);
                        proposalHeader.setVersion(proposalVersion.getVersion());
                        proposalDataProvider.saveProposal(proposalHeader);
                    }


                    if (success) {
                        saveButton.setVisible(false);
                        NotificationUtil.showNotification("Confirmed successfully!", NotificationUtil.STYLE_BAR_SUCCESS_SMALL);
                        handleState();
                    } else {
                        NotificationUtil.showNotification("Couldn't Publish Proposal! Please contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                    }
                }
            } else {
                NotificationUtil.showNotification("Couldn't Save Proposal! Please contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            }
        } catch (FieldGroup.CommitException e) {
            NotificationUtil.showNotification("Validation Error, please fill all mandatory fields!", NotificationUtil.STYLE_BAR_ERROR_SMALL);
        }

        ProposalEvent.VersionCreated event1 = new ProposalEvent.VersionCreated(proposalVersion);
        DashboardEventBus.post(event1);
        close();
        }
        else
        {
            NotificationUtil.showNotification("Discount should not exceed " +rateForDiscount.intValue(), NotificationUtil.STYLE_BAR_ERROR_SMALL);
            discountAmount.setValue(String.valueOf(proposalVersion.getDiscountAmount()).replace(",",""));
            discountPercentage.setValue(String.valueOf(proposalVersion.getDiscountPercentage()));
        }

    }


    private void updateTotal(SelectionEvent selectionEvent) {
        updateTotal();
    }

    private GeneratedPropertyContainer createGeneratedAddonsPropertyContainer() {
        GeneratedPropertyContainer genContainer = new GeneratedPropertyContainer(addonsContainer);
        genContainer.addGeneratedProperty("actions", getEmptyActionTextGenerator());
        return genContainer;
    }

    private GeneratedPropertyContainer createGeneratedProductPropertyContainer() {
        GeneratedPropertyContainer genContainer = new GeneratedPropertyContainer(productContainer);
        genContainer.addGeneratedProperty("actions", getActionTextGenerator());
        genContainer.addGeneratedProperty("productCategoryText", getProductCategoryTextGenerator());
        return genContainer;
    }


    private void save(Button.ClickEvent clickEvent)
    {
        if(proposalHeader.getMaxDiscountPercentage()>=Double.valueOf(discountPercentage.getValue()))
        {
            remarksTextArea.setValidationVisible(false);
            try {
                remarksTextArea.validate();
            }
            catch (Exception e) {
                NotificationUtil.showNotification("Validation Error, please fill remarks fields and remarks should not exceed 255 characters", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                remarksTextArea.setValidationVisible(false);
                return;
            }
            saveProposalVersion();
        }else {
            NotificationUtil.showNotification("Discount should not exceed " +rateForDiscount.intValue(), NotificationUtil.STYLE_BAR_ERROR_SMALL);
            discountAmount.setValue(String.valueOf(proposalVersion.getDiscountAmount()).replace(",",""));
            discountPercentage.setValue(String.valueOf(proposalVersion.getDiscountPercentage()));
        }
    }

    private void saveProposalVersion() {
        try
        {
                String disAmount=discountAmount.getValue();
                proposalVersion.setAmount(Double.parseDouble(grandTotal.getValue()));
                proposalVersion.setFinalAmount(Double.parseDouble(discountTotal.getValue()));
                proposalVersion.setDiscountAmount(Double.parseDouble(disAmount.replace(",","")));
                proposalVersion.setDiscountPercentage(Double.parseDouble(discountPercentage.getValue()));
                proposalVersion.setRemarks(remarksTextArea.getValue());
                proposalVersion.setTitle(this.ttitle.getValue());

               proposalHeader.setStatus(proposalVersion.getStatus());
               proposalHeader.setVersion(String.valueOf(versionNum));

                boolean success = proposalDataProvider.saveProposal(proposalHeader);
            if (success)
            {
                NotificationUtil.showNotification("Saved successfully!", NotificationUtil.STYLE_BAR_SUCCESS_SMALL);

            }
            else
            {
                NotificationUtil.showNotification("Cannot Save Proposal!!", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                return;

            }

            proposalVersion = proposalDataProvider.updateVersion(proposalVersion);
            NotificationUtil.showNotification("Saved successfully!", NotificationUtil.STYLE_BAR_SUCCESS_SMALL);
            DashboardEventBus.post(new ProposalEvent.VersionCreated(proposalVersion));
            close();
        }
        catch (Exception e)
        {
            NotificationUtil.showNotification("Couldn't Save Proposal! Please contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            LOG.info("Exception :" + e.toString());
        }
    }

    private void saveVersionAmounts() {
        try
        {
                String disAmount=discountAmount.getValue();
                proposalVersion.setAmount(Double.parseDouble(grandTotal.getValue()));
                proposalVersion.setFinalAmount(Double.parseDouble(discountTotal.getValue()));
                proposalVersion.setDiscountAmount(Double.parseDouble(disAmount.replace(",","")));
                proposalVersion.setDiscountPercentage(Double.parseDouble(discountPercentage.getValue()));
                proposalVersion.setRemarks(remarksTextArea.getValue());
                proposalVersion.setTitle(this.ttitle.getValue());

            proposalVersion = proposalDataProvider.updateVersion(proposalVersion);
            NotificationUtil.showNotification("Saved successfully!", NotificationUtil.STYLE_BAR_SUCCESS_SMALL);
            DashboardEventBus.post(new ProposalEvent.VersionCreated(proposalVersion));
        }
        catch (Exception e)
        {
            NotificationUtil.showNotification("Couldn't Save Proposal! Please contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            LOG.info("Exception :" + e.toString());
        }
    }

    private void saveWithoutClose(Button.ClickEvent clickEvent)
    {
        if(proposalHeader.getMaxDiscountPercentage()>=Double.valueOf(discountPercentage.getValue())) {
            try {

                close();
            } catch (Exception e) {
                NotificationUtil.showNotification("Couldn't Save Proposal! Please contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                LOG.info("Exception :" + e.toString());
            }
        }
        else {
            NotificationUtil.showNotification("Discount should not exceed " +rateForDiscount.intValue(), NotificationUtil.STYLE_BAR_ERROR_SMALL);
            discountAmount.setValue("0.0");
            discountPercentage.setValue("0.0");
        }
    }


    private void close(Button.ClickEvent clickEvent) {

        ConfirmDialog.show(UI.getCurrent(), "", "Do you want to close this screen? All unsaved data will be lost",
                "Yes", "No", dialog -> {
                    if (!dialog.isCanceled()) {
                        saveWithoutClose(clickEvent);
                        DashboardEventBus.unregister(this);
                        close();

                    }
                });
    }

    private void handleState() {
            ProposalVersion.ProposalStage proposalStage = ProposalVersion.ProposalStage.valueOf(proposalVersion.getInternalStatus());
            switch (proposalStage) {
                case Draft:
                    submitButton.setVisible(true);
                    addKitchenOrWardrobeButton.setEnabled(true);
                    if(Objects.equals(proposalHeader.getBeforeProductionSpecification(), "yes"))
                    {
                        addFromProductLibrary.setEnabled(true);
                    }

                    addFromCatalogueButton.setEnabled(true);
                    designSignOffButton.setVisible(false);
                    productionSignOffButton.setVisible(false);
                    addonAddButton.setEnabled(true);
                    customAddonAddButton.setEnabled(true);
                    confirmButton.setVisible(false);
                    break;
                case Published:
                    submitButton.setVisible(false);
                    confirmButton.setVisible(true);
                    String versionNew = String.valueOf(proposalVersion.getVersion());
                    if (versionNew.startsWith("1."))
                    {
                        productionSignOffButton.setVisible(false);
                        designSignOffButton.setEnabled(true);
                        confirmButton.setVisible(false);
                    }
                    else if (versionNew.startsWith("2."))
                    {
                        designSignOffButton.setVisible(false);
                        productionSignOffButton.setEnabled(true);
                        confirmButton.setVisible(false);
                    }
                    else
                    {
                        designSignOffButton.setVisible(false);
                        productionSignOffButton.setVisible(false);
                    }
                    setComponentsReadonly();
                    discountAmount.setReadOnly(true);
                    discountPercentage.setReadOnly(true);

                    break;
                case Confirmed:
                    setComponentsReadonly();
                    submitButton.setVisible(false);
                    confirmButton.setVisible(false);
                    discountAmount.setReadOnly(true);
                    designSignOffButton.setVisible(false);
                    productionSignOffButton.setVisible(false);
                    discountPercentage.setReadOnly(true);

                    break;
                case Locked:
                    submitButton.setVisible(false);
                    confirmButton.setVisible(false);
                    addKitchenOrWardrobeButton.setVisible(false);
                    if(Objects.equals(proposalHeader.getBeforeProductionSpecification(), "yes"))
                    {
                        addFromProductLibrary.setVisible(false);
                    }

                    addFromCatalogueButton.setVisible(false);
                    addonAddButton.setVisible(false);
                    customAddonAddButton.setVisible(false);
                    designSignOffButton.setVisible(false);
                    productionSignOffButton.setVisible(false);
                    discountAmount.setReadOnly(true);
                    discountPercentage.setReadOnly(true);
                    break;
                case DSO:
                    setComponentsReadonly();
                    submitButton.setVisible(false);
                    confirmButton.setVisible(false);
                    discountAmount.setReadOnly(true);
                    productionSignOffButton.setVisible(false);
                    designSignOffButton.setVisible(false);
                    discountPercentage.setReadOnly(true);
                    break;
                case PSO:
                    setComponentsReadonly();
                    submitButton.setVisible(false);
                    confirmButton.setVisible(false);
                    discountAmount.setReadOnly(true);
                    designSignOffButton.setVisible(false);
                    productionSignOffButton.setVisible(false);
                    discountPercentage.setReadOnly(true);
                    break;
                default:
                    throw new RuntimeException("Unknown State");
            }
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

    private PropertyValueGenerator<String> getActionTextGenerator() {
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

    private PropertyValueGenerator<String> getProductCategoryTextGenerator() {
        return new PropertyValueGenerator<String>() {

            @Override
            public String getValue(Item item, Object o, Object o1) {
                Product product = (Product) ((BeanItem) item).getBean();

                if (product.getType().equals(Product.TYPES.CUSTOMIZED.name()) || product.getType().equals(Product.TYPES.PRODUCT_LIBRARY.name() )) {
                    if (StringUtils.isNotEmpty(product.getProductCategory())) {
                        return product.getProductCategory();
                    } else {
                        List<LookupItem> lookupItems = proposalDataProvider.getLookupItems(ProposalDataProvider.CATEGORY_LOOKUP);
                        return lookupItems.stream().filter(lookupItem -> lookupItem.getCode().equals(product.getProductCategoryCode())).findFirst().get().getTitle();
                    }
                } else {
                    List<LookupItem> subCategories = proposalDataProvider.getLookupItems(ProposalDataProvider.SUB_CATEGORY_LOOKUP);
                    return subCategories.stream().filter(
                            lookupItem -> lookupItem.getCode().equals(product.getProductCategoryCode()))
                            .collect(Collectors.toList()).get(0).getTitle();
                }
            }
            @Override
            public Class<String> getType() {
                return String.class;
            }
        };
    }

    private void setComponentsReadonly() {
        addKitchenOrWardrobeButton.setEnabled(false);
        addFromCatalogueButton.setEnabled(false);
        addonAddButton.setEnabled(false);
        customAddonAddButton.setEnabled(false);
        if(Objects.equals(proposalHeader.getBeforeProductionSpecification(), "yes")) {
            addFromProductLibrary.setEnabled(false);
        }
    }

    @Subscribe
    public void productDelete(final ProposalEvent.ProductDeletedEvent event) {
        List<Product> products = proposal.getProducts();
        boolean removed = products.remove(event.getProduct());
        if (removed) {
            productContainer.removeAllItems();
            productContainer.addAll(products);
            productsGrid.setContainerDataSource(createGeneratedProductPropertyContainer());
            productsGrid.getSelectionModel().reset();
            updateTotal();
            saveVersionAmounts();
            productsGrid.sort(Product.SEQ, SortDirection.ASCENDING);
        }
    }

    @Subscribe
    public void productCreatedOrUpdated(final ProposalEvent.ProductCreatedOrUpdatedEvent event) {
        List<Product> products = proposalDataProvider.getVersionProducts(proposalHeader.getId(),proposalVersion.getVersion());
        productContainer.removeAllItems();
        productContainer.addAll(products);
        productsGrid.setContainerDataSource(createGeneratedProductPropertyContainer());
        productsGrid.getSelectionModel().reset();
        updateTotal();
        saveVersionAmounts();
        productsGrid.sort(Product.SEQ, SortDirection.ASCENDING);
    }

    @Subscribe
    public void addonUpdated(final ProposalEvent.ProposalAddonUpdated event) {
        AddonProduct eventAddonProduct = event.getAddonProduct();
        LOG.info("Product :"  + eventAddonProduct.toString());
        persistAddon(eventAddonProduct);
        List<AddonProduct> addons = proposalDataProvider.getVersionAddons(proposalHeader.getId(),proposalVersion.getVersion());
        addonsContainer.removeAllItems();
        addonsContainer.addAll(addons);
        addonsGrid.setContainerDataSource(createGeneratedAddonsPropertyContainer());
        addonsGrid.sort(AddonProduct.SEQ, SortDirection.ASCENDING);
        updateTotal();
        saveVersionAmounts();

    }

    private void persistAddon(AddonProduct eventAddonProduct) {
        if (eventAddonProduct.getId() == 0) {
            proposalDataProvider.addProposalAddon(proposal.getProposalHeader().getId(), eventAddonProduct);
        } else {
            proposalDataProvider.updateProposalAddon(proposal.getProposalHeader().getId(), eventAddonProduct);
        }
    }

    private void handlepackage()
    {
        if(Objects.equals(proposalHeader.getPackageFlag(), "Yes"))
        {
            addKitchenOrWardrobeButton.setEnabled(false);
            addFromProductLibrary.setEnabled(false);
            addFromCatalogueButton.setEnabled(false);
            discountAmount.setReadOnly(true);
            discountPercentage.setReadOnly(true);
        }
    }
}

