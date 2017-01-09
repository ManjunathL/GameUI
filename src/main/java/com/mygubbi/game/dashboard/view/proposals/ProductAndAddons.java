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
import com.vaadin.server.*;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
    private ProductAndAddonSelection productAndAddonSelection;
    private Button addKitchenOrWardrobeButton;
    private Button addFromCatalogueButton;
    private FileAttachmentComponent fileAttachmentComponent;
    private TextField discountPercentage;
    private TextField discountAmount;
    private Label discountTotal;
    private Button addonAddButton;
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
    private TextField remarksText;
    private Label versionNum;
    private Label versionStatus;

    private String status=null;
    String vid;
    private Button confirmButton;
    private Button designSignOffButton;
    private Button productionSignOffButton;

    public static void open(ProposalHeader proposalHeader, Proposal proposal, String vid, ProposalVersion proposalVersion )
    {
        DashboardEventBus.post(new DashboardEvent.CloseOpenWindowsEvent());
        ProductAndAddons w = new ProductAndAddons(proposalHeader,proposal,vid,proposalVersion);
        UI.getCurrent().addWindow(w);
        w.focus();

    }

    public ProductAndAddons(ProposalHeader proposalHeader, Proposal proposal, String vid, ProposalVersion proposalVersion)
    {
        this.proposalHeader=proposalHeader;
        this.proposal=proposal;
        this.proposalVersion=proposalVersion;
        this.vid=vid;

        this.proposal.setProducts(proposalDataProvider.getVersionProducts(proposalHeader.getId(),vid));
        this.productAndAddonSelection = new ProductAndAddonSelection();
        this.productAndAddonSelection.setProposalId(this.proposalHeader.getId());
        this.productAndAddonSelection.setFromVersion(this.proposalVersion.getVersion());
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

        Component componentactionbutton=buildActionButtons();
        verticalLayout.addComponent(componentactionbutton);

        updateTotal();
        handleState();
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
        horizontalLayout1.addComponent(buildMainFormLayoutRight());
        verticalLayout.addComponent(horizontalLayout1);
        return verticalLayout;
    }

    private FormLayout buildMainFormLayoutLeft() {

        FormLayout formLayoutLeft = new FormLayout();
        formLayoutLeft.setSizeFull();
        formLayoutLeft.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        TextField tvnum=new TextField("Version #");
        tvnum.setValue(String.valueOf(proposalVersion.getVersion()));
        formLayoutLeft.addComponent(tvnum);
        tvnum.setReadOnly(true);

        TextField tstatus=new TextField("Status");
        tstatus.setValue(proposalVersion.getStatus());
        formLayoutLeft.addComponent(tstatus);
        tstatus.setReadOnly(true);

        return formLayoutLeft;
    }

    private FormLayout buildMainFormLayoutRight() {

        FormLayout formLayoutRight = new FormLayout();
        formLayoutRight.setSizeFull();
        formLayoutRight.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        ttitle=new TextField("Title");

        ttitle.addStyleName("textfield-background-color");
        ttitle.addStyleName(ValoTheme.LABEL_COLORED);

        formLayoutRight.addComponent(ttitle);
        ttitle.setValue(proposalVersion.getTitle());



        this.remarksText = new TextField("Remarks");
        this.remarksText.addStyleName("textfield-background-color");
        this.remarksText.setWidth("100%");
        this.remarksText.addValidator(new StringLengthValidator("Must be 200 characters long",0,200,false));
        this.remarksText.setImmediate(true);
        this.remarksText.setValue(proposalVersion.getRemarks());
        formLayoutRight.addComponent(remarksText);
        return formLayoutRight;
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


    private Component getVersionLayout()
    {
        HorizontalLayout hlayout=new HorizontalLayout();
        hlayout.setSizeFull();
        hlayout.setMargin(new MarginInfo(true,true,true,true));

        Panel amountsPanel=new Panel("Version Details");
        amountsPanel.setStyleName(ValoTheme.PANEL_BORDERLESS);
        amountsPanel.setSizeFull();

        HorizontalLayout amountsLayout = new HorizontalLayout();
        setSizeFull();

        amountsLayout.setMargin(new MarginInfo(true,true,true,true));

        Label vnum = new Label("<b>Version #: </b>", ContentMode.HTML);
        amountsLayout.addComponent(vnum);
        amountsLayout.setSpacing(true);
        vnum.addStyleName("amount-text-label");
        vnum.addStyleName("v-label-amount-text-label");
        vnum.addStyleName("margin-top-18");

        this.versionNum=new Label("</b>",ContentMode.HTML);
        this.versionNum.addStyleName("amount-text");
        this.versionNum.addStyleName("margin-top-18");
        this.versionNum.addStyleName("v-label-amount-text-label");
        this.versionNum.setReadOnly(true);
        this.versionNum.setValue(vid.toString());
        amountsLayout.addComponent(versionNum);
        amountsLayout.setSpacing(true);

        Label vstatus = new Label("<b>Status: </b>",ContentMode.HTML);
        amountsLayout.addComponent(vstatus);
        //amountsLayout.setSpacing(true);
        vstatus.addStyleName("amount-text-label");
        vstatus.addStyleName("v-label-amount-text-label");
        vstatus.addStyleName("margin-top-18");

        this.versionStatus= new Label("</b>",ContentMode.HTML);
        versionStatus.addStyleName("amount-text-label");
        versionStatus.addStyleName("v-label-amount-text-label");
        versionStatus.addStyleName("margin-top-18");
        versionStatus.setReadOnly(true);
        versionStatus.setValue(proposalVersion.getStatus());
        amountsLayout.addComponent(versionStatus);
        amountsLayout.setSpacing(true);




        amountsPanel.setContent(amountsLayout);
        hlayout.addComponent(amountsPanel);

        return hlayout;
    }



    private Component getAmountLayout()
    {
        VerticalLayout verticalLayout =new VerticalLayout();
        verticalLayout.setMargin(new MarginInfo(false,true,false,true));

        HorizontalLayout vlayout  = new HorizontalLayout();
        FormLayout left = new FormLayout();

        Label totalWithoutDiscount = new Label("Total Without Discount:");
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
        Collection<?> productObjects = productsGrid.getSelectedRows();
        Collection<?> addonObjects = addonsGrid.getSelectedRows();
        boolean anythingSelected = true;
        this.productAndAddonSelection.getProductIds().clear();
        this.productAndAddonSelection.getAddonIds().clear();

        if (productObjects.size() == 0) {
            anythingSelected = false;
            productObjects = this.productsGrid.getContainerDataSource().getItemIds();
        }

        double productsTotal = 0;

        for (Object object : productObjects) {
            Double amount = (Double) this.productsGrid.getContainerDataSource().getItem(object).getItemProperty(Product.AMOUNT).getValue();
            productsTotal += amount;
            Integer id = (Integer) this.productsGrid.getContainerDataSource().getItem(object).getItemProperty(Product.ID).getValue();
            if (anythingSelected) {
                this.productAndAddonSelection.getProductIds().add(id);
            }
        }

        if (addonObjects.size() == 0) {
            anythingSelected = false;
            addonObjects = this.addonsGrid.getContainerDataSource().getItemIds();
        }

        double addonsTotal = 0;

        for (Object object : addonObjects) {
            Double amount = (Double) this.addonsGrid.getContainerDataSource().getItem(object).getItemProperty(AddonProduct.AMOUNT).getValue();
            addonsTotal += amount;
            Integer id = (Integer) this.addonsGrid.getContainerDataSource().getItem(object).getItemProperty(AddonProduct.ID).getValue();

            if (anythingSelected) {
                this.productAndAddonSelection.getAddonIds().add(id);
            }
        }

        Double totalWoAccessories = 0.0;
        List<Product> products = proposalDataProvider.getVersionProducts(proposalVersion.getProposalId(),proposalVersion.getVersion());
        for (Product product : products) {
            totalWoAccessories += product.getCostWoAccessories();
        }

        Double totalAmount = addonsTotal + productsTotal;
        Double costOfAccessories = productsTotal - totalWoAccessories;


        refreshDiscount(totalWoAccessories,totalAmount,costOfAccessories,addonsTotal);
    }
    private void refreshDiscount(Double totalWoAccessories, Double totalAmount, Double costOfAccessories, Double addonsTotal)
    {
        LOG.debug("TW"+ totalWoAccessories+ "TA" +totalAmount + "CA" + costOfAccessories +"Addon" + addonsTotal);

        Double discountPercent=0.0,discountAmount=0.0;
        if("DP".equals(status))
        {
            discountPercent = (Double) this.discountPercentage.getConvertedValue();
            if(discountPercent<=30) {
                if (discountPercent == null) {
                    discountPercent = 0.0;
                }

                discountAmount = totalWoAccessories * discountPercent / 100.0;
                //double res = discountAmount - discountAmount % 100;
                this.discountAmount.setValue(String.valueOf(discountAmount.intValue())+ " ");
                disAmount=discountAmount.intValue();
                //this.discountAmount.setValue(String.valueOf(round(discountAmount, 2)));
            }
            else
            {
                NotificationUtil.showNotification("Discount should not exceed 30%", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                return;
            }
        }
        else if("DA".equals(status))
        {
            discountAmount = (Double) this.discountAmount.getConvertedValue();
            discountPercent=(discountAmount/totalWoAccessories)*100;
            if(discountPercent<=30) {
                this.discountPercentage.setValue(String.valueOf(round(discountPercent, 2)));
            }
            else
            {
                NotificationUtil.showNotification("Discount should not exceed 30%", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                return;
            }
        }
        Double totalAfterDiscount = this.round((totalWoAccessories - discountAmount), 0);

        Double grandTotal = totalAfterDiscount + costOfAccessories + addonsTotal;
        double res=grandTotal-grandTotal%10;
        /*Double rem=grandTotal%10;

        if(rem<5)
        {
            grandTotal=grandTotal-rem;
        }
        else
        {
            grandTotal=grandTotal+(10-rem);
        }*/

        this.discountTotal.setReadOnly(false);
        this.discountTotal.setValue(String.valueOf(res));

        this.grandTotal.setReadOnly(false);
        this.grandTotal.setValue(totalAmount.intValue() + "");
        this.grandTotal.setReadOnly(true);

        //this.grandTotal.addValueChangeListener(this::onGrandTotalValueChange);
       /* productAndAddonSelection.setDiscountPercentage(discountPercent);
        productAndAddonSelection.setDiscountAmount(discountAmount);*/

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

    private Component buildProductDetails() {

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        verticalLayout.setSpacing(true);
        verticalLayout.setMargin(new MarginInfo(true, true, true, true));
        verticalLayout.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSizeFull();
        horizontalLayout.setStyleName("v-has-width-forLabel");

        Label title = new Label("Products Details");
        title.setStyleName("products-and-addons-label-text");
        horizontalLayout.addComponent(title);
        horizontalLayout.setComponentAlignment(title,Alignment.TOP_LEFT);

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
                    CatalogItemDetailsWindow.open(ProductAndAddons.this.proposal, newProduct);
                }
        );

        productContainer = new BeanItemContainer<>(Product.class);

        GeneratedPropertyContainer genContainer = createGeneratedProductPropertyContainer();

        productsGrid = new Grid(genContainer);
        productsGrid.addSelectionListener(this::updateTotal);
        productsGrid.setSizeFull();
        productsGrid.setColumnReorderingAllowed(true);
        productsGrid.setColumns(Product.SEQ, Product.ROOM_CODE, Product.TITLE, "productCategoryText", Product.AMOUNT, TYPE, "actions");

        List<Grid.Column> columns = productsGrid.getColumns();
        int idx = 0;

        columns.get(idx++).setHeaderCaption("#");
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
                Product p = (Product) rendererClickEvent.getItemId();
                LOG.debug("Product container before :" + productContainer.size());

                productContainer.removeAllItems();

                List<Product> copy = proposalDataProvider.getVersionProducts(proposalHeader.getId(),proposalVersion.getVersion());
                int length = (copy.size()) + 1;

                Product proposalProductDetails = proposalDataProvider.getProposalProductDetails(p.getId(),p.getFromVersion());
                List<Module> modulesFromOldProduct = proposalProductDetails.getModules();
                LOG.debug("modules:"+modulesFromOldProduct);
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

                copyProduct.setModules(modulesFromOldProduct);
                LOG.debug("COPIED@"+ copyProduct);

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

            }
            @Override
            public void onEdit(ClickableRenderer.RendererClickEvent rendererClickEvent) {

                Product product = (Product) rendererClickEvent.getItemId();

                if (product.getType().equals(Product.TYPES.CUSTOMIZED.name())) {
                    if (product.getModules().isEmpty()) {
                        Product productDetails = proposalDataProvider.getProposalProductDetails(product.getId(),product.getFromVersion());
                        product.setModules(productDetails.getModules());
                        product.setAddons(productDetails.getAddons());
                    }

                    if (product.getFileAttachmentList().isEmpty()) {
                        List<FileAttachment> productAttachments = proposalDataProvider.getProposalProductDocuments(product.getId());
                        product.setFileAttachmentList(productAttachments);
                    }
                    CustomizedProductDetailsWindow.open(proposal, product, proposalVersion,proposalHeader);
                } else {
                    CatalogueProduct catalogueProduct = new CatalogueProduct();
                    catalogueProduct.populateFromProduct(product);
                    CatalogItemDetailsWindow.open(proposal, catalogueProduct);
                }
            }

            @Override
            public void onDelete(ClickableRenderer.RendererClickEvent rendererClickEvent) {

                if (("Published").equals(proposalVersion.getInternalStatus()) || ("Confirmed").equals(proposalVersion.getInternalStatus()) || ("Locked").equals(proposalVersion.getInternalStatus()) || ("DSO").equals(proposalVersion.getInternalStatus()) || ("PSO").equals(proposalVersion.getInternalStatus()))
                {
                    Notification.show("Cannot delete products on Published, Confirmed and Locked versions");
                    return;
                }

                if (isProposalReadonly()) {
                    NotificationUtil.showNotification("This operation is allowed only in 'Draft' state.", NotificationUtil.STYLE_BAR_WARNING_SMALL);
                } else {

                    ConfirmDialog.show(UI.getCurrent(), "", "Are you sure you want to Delete this Product?",
                            "Yes", "No", dialog -> {
                                if (dialog.isConfirmed()) {
                                    Product product = (Product) rendererClickEvent.getItemId();

                                    proposal.getProducts().remove(product);

                                    int seq = product.getSeq();
                                    productContainer.removeAllItems();

                                    proposalDataProvider.deleteProduct(product.getId());

                                    for (Product product1 : proposal.getProducts()) {
                                        if (product1.getSeq() > seq) {
                                            product1.setSeq(product1.getSeq() - 1);
                                            proposalDataProvider.updateProductSequence(product1.getSeq(),product1.getId());
                                        }
                                    }
                                    List<Product> productsUpdated = proposalDataProvider.getVersionProducts(proposalHeader.getId(),proposalVersion.getVersion());
                                    for (Product updatedProduct : productsUpdated)
                                    {
                                        LOG.debug("products updated to string :" + productsUpdated.toString());
                                        productContainer.addItem(updatedProduct);
                                        LOG.debug(productContainer.size());
                                    }
                                    productsGrid.setContainerDataSource(createGeneratedProductPropertyContainer());
                                    productsGrid.getSelectionModel().reset();
                                    updateTotal();
                                    NotificationUtil.showNotification("Product deleted successfully.", NotificationUtil.STYLE_BAR_SUCCESS_SMALL);
                                }
                            });
                }

            }
        }));

        if (!proposal.getProducts().isEmpty()) {
            productContainer.addAll(proposal.getProducts());
            productsGrid.sort(Product.SEQ, SortDirection.ASCENDING);
        }
        Label label = new Label("Select Products and click Download Quote/Job Card button to generate output for only the selected Products.");
        label.setStyleName("font-italics");

        verticalLayout.addComponent(horizontalLayout);

        verticalLayout.addComponent(productsGrid);

        return verticalLayout;
    }

    private Component buildAddons() {

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        verticalLayout.setMargin(new MarginInfo(true,true,true,true));

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSizeFull();

        Label addonTitle = new Label("Addon Details");
        addonTitle.setStyleName("amount-text");
        addonTitle.addStyleName("margin-top-18");
        horizontalLayout.addComponent(addonTitle);

        addonAddButton = new Button("Add");
        addonAddButton.setIcon(FontAwesome.PLUS_CIRCLE);
        addonAddButton.setStyleName("add-addon-btn");
        addonAddButton.addClickListener(clickEvent -> {
            AddonProduct addonProduct = new AddonProduct();
            addonProduct.setSeq(proposal.getAddons().size() + 1);
            addonProduct.setAdd(true);
            AddonDetailsWindow.open(addonProduct, false, "Add Addon", true);
        });

        horizontalLayout.addComponent(addonAddButton);
        horizontalLayout.setComponentAlignment(addonAddButton, Alignment.MIDDLE_RIGHT);

        verticalLayout.addComponent(horizontalLayout);

        addonsContainer = new BeanItemContainer<>(AddonProduct.class);

        GeneratedPropertyContainer genContainer = createGeneratedAddonsPropertyContainer();

        addonsGrid = new Grid(genContainer);
        addonsGrid.setSizeFull();
        /*addonsGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        addonsGrid.addSelectionListener(this::updateTotal);*/
        addonsGrid.setColumnReorderingAllowed(true);
        addonsGrid.setColumns(AddonProduct.SEQ, AddonProduct.ADDON_CATEGORY_CODE, AddonProduct.PRODUCT_TYPE_CODE, AddonProduct.BRAND_CODE,
                AddonProduct.TITLE, AddonProduct.CATALOGUE_CODE, AddonProduct.UOM, AddonProduct.RATE, AddonProduct.QUANTITY, AddonProduct.AMOUNT, "actions");

        List<Grid.Column> columns = addonsGrid.getColumns();
        int idx = 0;
        columns.get(idx++).setHeaderCaption("#");
        columns.get(idx++).setHeaderCaption("Category");
        columns.get(idx++).setHeaderCaption("Product Type");
        columns.get(idx++).setHeaderCaption("Brand");
        columns.get(idx++).setHeaderCaption("Product Name");
        columns.get(idx++).setHeaderCaption("Product Code");
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
                boolean readOnly = isProposalReadonly();
                AddonDetailsWindow.open(addon, readOnly, "Edit Addon", true);
            }

            @Override
            public void onDelete(ClickableRenderer.RendererClickEvent rendererClickEvent) {
                if (isProposalReadonly()) {
                    NotificationUtil.showNotification("This operation is allowed only in 'Draft' state.", NotificationUtil.STYLE_BAR_WARNING_SMALL);
                } else {
                    ConfirmDialog.show(UI.getCurrent(), "", "Are you sure you want to Delete this Addon?",
                            "Yes", "No", dialog -> {
                                if (dialog.isConfirmed()) {
                                    AddonProduct addon = (AddonProduct) rendererClickEvent.getItemId();
                                    proposalDataProvider.removeProposalAddon(addon.getId());
                                    List<AddonProduct> addons = proposal.getAddons();
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
                                }
                            });
                }
            }
        }));

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

    private void doSalesOrderDownloadValidation(Button.ClickEvent clickEvent) {
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
    }

    private String getSOFilename()
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
    }

    private void checkProductsAndAddonsAvailable(Button.ClickEvent clickEvent) {
        if (proposal.getProducts().isEmpty() ) {
            NotificationUtil.showNotification("No products found. Please add product(s) first to generate the Quote.", NotificationUtil.STYLE_BAR_WARNING_SMALL);
        }
        if (proposal.getAddons().isEmpty()) {
            NotificationUtil.showNotification("No Addons found.", NotificationUtil.STYLE_BAR_WARNING_SMALL);
        }
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
                String quoteFile = proposalDataProvider.getProposalQuoteFilePdf(this.productAndAddonSelection);
                InputStream input = null;
                try {
                    input = new ByteArrayInputStream(FileUtils.readFileToByteArray(new File(quoteFile)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return input;
            } else {
                return null;
            }
        };
        return new StreamResource(source, "Quotation.pdf");
    }

    private StreamResource createQuoteResource() {
        StreamResource.StreamSource source = () -> {
            if (!proposal.getProducts().isEmpty()) {
                String quoteFile = proposalDataProvider.getProposalQuoteFile(this.productAndAddonSelection);
                InputStream input = null;
                try {
                    input = new ByteArrayInputStream(FileUtils.readFileToByteArray(new File(quoteFile)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return input;
            } else {
                return null;
            }
        };
        return new StreamResource(source, "Quotation.xlsx");
    }

    private void checkSingleProductSelection(Button.ClickEvent clickEvent) {
        if (this.productAndAddonSelection.getProductIds().size() != 1) {
            NotificationUtil.showNotification("Please select a single product to download its Job Card.", NotificationUtil.STYLE_BAR_WARNING_SMALL);
        }
    }

    private double round(double value, int places)
    {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private StreamResource createJobcardResource() {
        StreamResource.StreamSource source = () -> {

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


    private void submit(Button.ClickEvent clickEvent) {
        try {
            binder.commit();

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            proposalVersion.setStatus(ProposalVersion.ProposalStage.Published.name());
            proposalVersion.setInternalStatus(ProposalVersion.ProposalStage.Published.name());
            proposalVersion.setDate(dateFormat.format(date));
            save(clickEvent);
            boolean success = proposalDataProvider.saveProposal(proposalHeader);
            if (success) {
                boolean mapped = true;
                for (Product product : proposal.getProducts()) {
                    Product populatedProduct = proposalDataProvider.getProposalProductDetails(product.getId(),product.getFromVersion());
                    mapped = populatedProduct.getType().equals(Product.TYPES.CATALOGUE.name()) || (!populatedProduct.getModules().isEmpty());
                    if (!mapped) {
                        break;
                    }
                }

                if (!mapped) {
                    NotificationUtil.showNotification("Couldn't Submit. Please ensure all Products have mapped Modules.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                } else {

                    success = proposalDataProvider.publishVersion(proposalVersion.getVersion(),proposalHeader.getId());
                    if (success) {
                        saveButton.setVisible(false);
                        addKitchenOrWardrobeButton.setVisible(false);
                        addFromCatalogueButton.setVisible(false);
                        addonAddButton.setVisible(false);
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
        DashboardEventBus.post(new ProposalEvent.VersionCreated(proposalVersion));
        DashboardEventBus.unregister(this);
        close();
    }

    private void confirm(Button.ClickEvent clickEvent) {
        try {
            binder.commit();

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            proposalVersion.setStatus(ProposalVersion.ProposalStage.Confirmed.name());
            proposalVersion.setInternalStatus(ProposalVersion.ProposalStage.Confirmed.name());
            proposalVersion.setDate(dateFormat.format(date));
            save(clickEvent);


            boolean success = proposalDataProvider.saveProposal(proposalHeader);
            if (success) {
                boolean mapped = true;
                for (Product product : proposal.getProducts()) {
                    Product populatedProduct = proposalDataProvider.getProposalProductDetails(product.getId(),product.getFromVersion());
                    mapped = populatedProduct.getType().equals(Product.TYPES.CATALOGUE.name()) || (!populatedProduct.getModules().isEmpty());
                    if (!mapped) {
                        break;
                    }
                }

                if (!mapped) {
                    NotificationUtil.showNotification("Couldn't Submit. Please ensure all Products have mapped Modules.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                } else {
                    String versionNew = String.valueOf(proposalVersion.getVersion());
                    if (versionNew.startsWith("0."))
                    {
                        proposalVersion.setFromVersion(proposalVersion.getVersion());
                        proposalVersion.setToVersion(proposalVersion.getVersion());
                        proposalVersion.setVersion("1.0");
                        proposalDataProvider.lockAllPreSalesVersions(ProposalVersion.ProposalStage.Locked.name(),proposalHeader.getId());
                        success = proposalDataProvider.confirmVersion(proposalVersion.getVersion(),proposalHeader.getId(),proposalVersion.getFromVersion(),proposalVersion.getToVersion(),proposalVersion.getDate());
                    }
                    else if (versionNew.startsWith("1."))
                    {
                        proposalVersion.setFromVersion(proposalVersion.getVersion());
                        proposalVersion.setToVersion(proposalVersion.getVersion());
                        proposalVersion.setVersion("2.0");
                        proposalVersion.setStatus(ProposalVersion.ProposalStage.DSO.name());
                        proposalVersion.setInternalStatus(ProposalVersion.ProposalStage.DSO.name());
                        proposalDataProvider.lockAllPostSalesVersions(ProposalVersion.ProposalStage.Locked.name(),proposalHeader.getId());
                        success = proposalDataProvider.versionDesignSignOff(proposalVersion.getVersion(),proposalHeader.getId(),proposalVersion.getFromVersion(),proposalVersion.getToVersion(),proposalVersion.getDate());
                    }
                    else if (versionNew.startsWith("2."))
                    {
                        proposalVersion.setToVersion(proposalVersion.getVersion());
                        proposalVersion.setStatus(ProposalVersion.ProposalStage.PSO.name());
                        proposalVersion.setInternalStatus(ProposalVersion.ProposalStage.PSO.name());
                        proposalDataProvider.lockAllPostSalesVersions(ProposalVersion.ProposalStage.Locked.name(),proposalHeader.getId());
                        proposalDataProvider.lockAllVersionsExceptPSO(ProposalVersion.ProposalStage.Locked.name(),proposalHeader.getId());
                        success = proposalDataProvider.versionProductionSignOff(proposalVersion.getVersion(),proposalHeader.getId(),proposalVersion.getFromVersion(),proposalVersion.getToVersion(),proposalVersion.getDate());
                    }

                    proposalDataProvider.updateVersionOnConfirm(proposalVersion.getVersion(),proposalVersion.getProposalId(),proposalVersion.getFromVersion());
                    if (success) {
                        saveButton.setVisible(false);
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

        ProposalEvent.VersionCreated event1 = new ProposalEvent.VersionCreated(proposalVersion);
        DashboardEventBus.post(event1);
        close();
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

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        try {
                String disAmount=discountAmount.getValue();
                proposalVersion.setAmount(Double.parseDouble(grandTotal.getValue()));
                proposalVersion.setFinalAmount(Double.parseDouble(discountTotal.getValue()));
                proposalVersion.setDate(dateFormat.format(date));
                proposalVersion.setDiscountAmount(Double.parseDouble(disAmount.replace(",","")));
                proposalVersion.setDiscountPercentage(Double.parseDouble(discountPercentage.getValue()));

                proposalVersion.setRemarks(remarksText.getValue());
                proposalVersion.setTitle(this.ttitle.getValue());

                /*LOG.debug("Proposal Version" + proposalVersion.toString());*/

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

    private void saveWithoutClose(Button.ClickEvent clickEvent)
    {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        try {
                String disAmount=discountAmount.getValue();
                proposalVersion.setDate(dateFormat.format(date));

                /*LOG.debug("Proposal Version" + proposalVersion.toString());*/

                proposalVersion = proposalDataProvider.updateVersion(proposalVersion);
            DashboardEventBus.post(new ProposalEvent.VersionCreated(proposalVersion));
            close();
        }
        catch (Exception e)
        {
            NotificationUtil.showNotification("Couldn't Save Proposal! Please contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            LOG.info("Exception :" + e.toString());
        }
    }

    private boolean isProposalReadonly() {
        return !proposal.getProposalHeader().getStatus().equals(ProposalHeader.ProposalState.Draft.name());
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
        if (proposalHeader.isReadonly()) {
            setComponentsReadonly();
        } else {
            ProposalVersion.ProposalStage proposalStage = ProposalVersion.ProposalStage.valueOf(proposalVersion.getInternalStatus());
            switch (proposalStage) {
                case Draft:
                    submitButton.setVisible(true);
                    addKitchenOrWardrobeButton.setEnabled(true);
                    addFromCatalogueButton.setEnabled(true);
                    designSignOffButton.setVisible(false);
                    productionSignOffButton.setVisible(false);
                    addonAddButton.setEnabled(true);
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
                    addFromCatalogueButton.setVisible(false);
                    addonAddButton.setVisible(false);
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

                if (product.getType().equals(Product.TYPES.CUSTOMIZED.name())) {
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
            productsGrid.sort(Product.SEQ, SortDirection.ASCENDING);
        }
    }

    @Subscribe
    public void productCreatedOrUpdated(final ProposalEvent.ProductCreatedOrUpdatedEvent event) {
        List<Product> products = proposal.getProducts();
        boolean removed = products.remove(event.getProduct());
        products.add(event.getProduct());
        productContainer.removeAllItems();
        productContainer.addAll(products);
        productsGrid.setContainerDataSource(createGeneratedProductPropertyContainer());
        productsGrid.getSelectionModel().reset();
        updateTotal();
        productsGrid.sort(Product.SEQ, SortDirection.ASCENDING);
    }

    @Subscribe
    public void addonUpdated(final ProposalEvent.ProposalAddonUpdated event) {
        AddonProduct eventAddonProduct = event.getAddonProduct();
        persistAddon(eventAddonProduct);
        List<AddonProduct> addons = proposal.getAddons();
        addons.remove(eventAddonProduct);
        addons.add(eventAddonProduct);
        addonsContainer.removeAllItems();
        addonsContainer.addAll(addons);
        addonsGrid.setContainerDataSource(createGeneratedAddonsPropertyContainer());
        addonsGrid.sort(AddonProduct.SEQ, SortDirection.ASCENDING);
    }

    private void persistAddon(AddonProduct eventAddonProduct) {
        if (eventAddonProduct.getId() == 0) {
            proposalDataProvider.addProposalAddon(proposal.getProposalHeader().getId(), eventAddonProduct);
        } else {
            proposalDataProvider.updateProposalAddon(proposal.getProposalHeader().getId(), eventAddonProduct);
        }
    }
    private void setHeaderFieldsReadOnly(boolean readOnly) {
        /*proposalTitleField.setReadOnly(readOnly);
        crmId.setReadOnly(readOnly);
        quotationField.setReadOnly(readOnly);
        customerIdField.setReadOnly(readOnly);
        customerNameField.setReadOnly(readOnly);
        customerAddressLine1.setReadOnly(readOnly);
        customerAddressLine2.setReadOnly(readOnly);
        customerAddressLine3.setReadOnly(readOnly);
        customerCityField.setReadOnly(readOnly);
        customerEmailField.setReadOnly(readOnly);
        customerNumberField1.setReadOnly(readOnly);
        customerNumberField2.setReadOnly(readOnly);
        projectName.setReadOnly(readOnly);
        projectAddressLine1.setReadOnly(readOnly);
        projectAddressLine2.setReadOnly(readOnly);
        projectCityField.setReadOnly(readOnly);
        salesPerson.setReadOnly(readOnly);
        designPerson.setReadOnly(readOnly);*/
    }
}
