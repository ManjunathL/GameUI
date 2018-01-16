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
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;
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
    private BeanItemContainer<ProposalServices> servicesContainer;
    private Grid addonsGrid;
    private Grid servicesGrid;

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
    Button quotePdf;
    FileDownloader fileDownloaderPdf;

    private TextArea remarksTextArea;

    private String status=null;
    private String saveVersionFlag = "no";
    String vid;
    private Button confirmButton;
    private Button designSignOffButton;
    private Button productionSignOffButton;
    Label totalWithoutDiscount;
    String codeForDiscount;
    Double rateForDiscount;
    java.sql.Date priceDate;
    String viewOnlyValue;
    List<Product> products = new ArrayList<>();
    List<AddonProduct> addons = new ArrayList<>();
    private String value;
    double projectHandlingChargesRate,deepCleaningChargesRate,floorProtectionChargesRate;

    double productsTotal = 0;
    double productsTotalAfterDiscount = 0;


    private Label productPrice;
    private Label productPriceAfterDiscount;
    private Label addonPrice;
    private Label miscellaneousPrice;
    private Label totalPrice;

    Label PHCRate,DCCRate,FPCRate;
    TextField DCCQTY;
    TextField FPCQTY;
    TextField PHCQTY;
    CheckBox PHCcheck,DCCcheck,FPCcheck;
    Label DCCAmount,FPCAmount;
    Label PHCAmount;
    String customAddonCheck="No";
    String quickProductCheck="No";
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
        rateForDiscount=proposalHeader.getMaxDiscountPercentage();
        this.vid=vid;
        this.priceDate = proposalHeader.getPriceDate();
        this.city = proposalHeader.getPcity();
        this.products = proposalDataProvider.getVersionProducts(proposalHeader.getId(),vid);
        this.addons = proposalDataProvider.getVersionAddons(proposalHeader.getId(),vid);

        if (this.priceDate == null) {
            this.priceDate = new java.sql.Date(System.currentTimeMillis());
        }

        PriceMaster projectHandlingCharges = proposalDataProvider.getFactorRatePriceDetails("PHC", this.priceDate, this.city);
        projectHandlingChargesRate = projectHandlingCharges.getPrice();
        PriceMaster deepCleaningCharges = proposalDataProvider.getFactorRatePriceDetails("DCC", this.priceDate, this.city);
        deepCleaningChargesRate = deepCleaningCharges.getPrice();
        PriceMaster floorProtectionCharges = proposalDataProvider.getFactorRatePriceDetails("FPC", this.priceDate, this.city);
        floorProtectionChargesRate = floorProtectionCharges.getPrice();

        String email = ((User) VaadinSession.getCurrent().getAttribute(User.class.getName())).getEmail();
        List<User> userList=proposalDataProvider.getUsersViewOnlyAcess(email);
        for(User user:userList)
        {
            viewOnlyValue=user.getIsViewOnly();
        }
        this.proposal.setProducts(this.products);
        this.proposal.setAddons(this.addons);
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

        Component componentHeading = buildHeading();
        verticalLayout.addComponent(componentHeading);

        Component componentversionDetails = getVersionHeadingLayout();
        verticalLayout.addComponent(componentversionDetails);

        Component componentRemarksDetails = buildRemarks();
        verticalLayout.addComponent(componentRemarksDetails);

        Component componentProductDetails = buildProductDetails();
        verticalLayout.addComponent(componentProductDetails);

        Component componentAddonDetails = buildAddons();
        verticalLayout.addComponent(componentAddonDetails);

        Component componentServicesDetails = buildServices();
        verticalLayout.addComponent(componentServicesDetails);

        Component miscellaneousComponent = buildMiscellaneousComponent();
        verticalLayout.addComponent(miscellaneousComponent);

        Component servicesummaryComponent = buildServicesSummaryComponents();
        verticalLayout.addComponent(servicesummaryComponent);
        verticalLayout.setComponentAlignment(servicesummaryComponent,Alignment.MIDDLE_RIGHT);

        Component amountsLayout = getAmountLayout();
        this.discountPercentage.addFocusListener(this::onFocusToDiscountPercentage);
        this.discountAmount.addFocusListener(this::onFocusToDiscountAmount);
//        this.grandTotal.addValueChangeListener(this::onGrandTotalValueChange);
        /*this.productPrice.addValueChangeListener(this::onProductTotalValueChange);
        this.productPriceAfterDiscount.addValueChangeListener(this::onProductTotalValueChange);*/

        this.discountPercentage.addValueChangeListener(this::onDiscountPercentageValueChange);
        this.discountAmount.addValueChangeListener(this::onDiscountAmountValueChange);
        verticalLayout.addComponent(amountsLayout);


        Component OptionDescriptionLayout = buildOptionLayout();
        verticalLayout.addComponent(OptionDescriptionLayout);

        Component componentactionbutton = buildActionButtons();
        verticalLayout.addComponent(componentactionbutton);

        updatePrice();
        addonCheck();
        handleState();
        handlepackage();
        handleQuickProducts();
        if(viewOnlyValue.equalsIgnoreCase("Yes")) {
            setReadOnlyForUser();
        }
    }

    private Component buildMiscellaneousComponent() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(new MarginInfo(false,true,false,true));
        HorizontalLayout horizontalLayout2 = new HorizontalLayout();
        horizontalLayout2.setMargin(new MarginInfo(false, true, false, true));
        horizontalLayout2.addStyleName("layout-with-border");
        horizontalLayout2.setWidth("98%");

        Component titleLayout = buildServiceHeading();
        horizontalLayout2.addComponent(titleLayout);

        verticalLayout.addComponent(horizontalLayout2);
        //verticalLayout.setComponentAlignment(horizontalLayout2,Alignment.TOP_CENTER);
//        horizontalLayout2.setHeightUndefined();

        HorizontalLayout horizontalLayout3 = new HorizontalLayout();
        horizontalLayout2.setMargin(new MarginInfo(false, false, false, false));
//        horizontalLayout3.setSizeFull();
        Component rateLayout = buildLayout2();
        horizontalLayout2.addComponent(rateLayout);

        verticalLayout.addComponent(horizontalLayout3);
        //verticalLayout.setComponentAlignment(horizontalLayout3,Alignment.TOP_CENTER);
//        horizontalLayout2.setHeightUndefined();

        HorizontalLayout horizontalLayoutForUnit = new HorizontalLayout();
        horizontalLayout2.setMargin(new MarginInfo(false, false, false, false));
        Component unitLayout = buildLayoutForUnit();
        horizontalLayout2.addComponent(unitLayout);
        verticalLayout.addComponent(horizontalLayoutForUnit);

        HorizontalLayout horizontalLayout4 = new HorizontalLayout();
        horizontalLayout2.setMargin(new MarginInfo(false, false, false, false));
//        horizontalLayout4.setSizeFull();
        Component quantityLayout = buildLayout3();
        horizontalLayout2.addComponent(quantityLayout);
        verticalLayout.addComponent(horizontalLayout4);
        //verticalLayout.setComponentAlignment(horizontalLayout4,Alignment.TOP_CENTER);
//        horizontalLayout2.setHeightUndefined();

        DCCQTY.addValueChangeListener(this::deepCleaningchargesQuantityChanged);
        FPCQTY.addValueChangeListener(this::floorProtectionQuantityChanged);
        PHCQTY.addValueChangeListener(this::ProjectHandlingChargesQuantityChanged);

        HorizontalLayout horizontalLayout5 = new HorizontalLayout();
        horizontalLayout2.setMargin(new MarginInfo(false, false, false, false));
//        horizontalLayout5.setSizeFull();
        Component amountLayout = buildLayout4();
        horizontalLayout2.addComponent(amountLayout);

        horizontalLayout2.setExpandRatio(titleLayout,0.35f);
        horizontalLayout2.setExpandRatio(rateLayout,0.15f);
        horizontalLayout2.setExpandRatio(unitLayout,0.20f);
        horizontalLayout2.setExpandRatio(quantityLayout,0.15f);
        horizontalLayout2.setExpandRatio(amountLayout,0.15f);


        verticalLayout.addComponent(horizontalLayout5);

        //verticalLayout.setComponentAlignment(horizontalLayout5,Alignment.TOP_CENTER);
//        horizontalLayout2.setHeightUndefined();

        return verticalLayout;

    }


    private Component getVersionHeadingLayout() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setMargin(new MarginInfo(false, true, false, true));
        horizontalLayout.setSizeFull();
        horizontalLayout.setStyleName("v-has-width-forLabel");

        Label title = new Label("Version Details");
        title.setStyleName("products-and-addons-label-text");
        horizontalLayout.addComponent(title);
        horizontalLayout.setComponentAlignment(title, Alignment.TOP_LEFT);

        return horizontalLayout;
    }

    private Component buildHeading() {
        HorizontalLayout formLayoutLeft = new HorizontalLayout();
        formLayoutLeft.setSizeFull();
        formLayoutLeft.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        Label customerDetailsLabel = new Label("Product And Addons");
        customerDetailsLabel.addStyleName(ValoTheme.LABEL_HUGE);
        customerDetailsLabel.addStyleName(ValoTheme.LABEL_COLORED);
        customerDetailsLabel.addStyleName("products-and-addons-heading-text");
        formLayoutLeft.addComponent(customerDetailsLabel);

        Component headingbutton = buildHeadingButtons();
        formLayoutLeft.addComponent(headingbutton);

        formLayoutLeft.setComponentAlignment(headingbutton, Alignment.TOP_LEFT);
        return formLayoutLeft;

    }

    private Component buildHeadingButtons() {

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setMargin(new MarginInfo(false, true, false, true));
        horizontalLayout.setSizeFull();

        HorizontalLayout right = new HorizontalLayout();

        Button pdfQuotation = new Button("Quote PDf&nbsp;&nbsp;");
        pdfQuotation.setCaptionAsHtml(true);
        pdfQuotation.setStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
        pdfQuotation.addStyleName(ValoTheme.BUTTON_PRIMARY);
        pdfQuotation.addStyleName(ValoTheme.BUTTON_SMALL);
        pdfQuotation.addStyleName("margin-top-for-headerlevelbutton");
        pdfQuotation.addStyleName("margin-right-10-for-headerlevelbutton");
        pdfQuotation.setWidth("120px");
        right.addComponent(pdfQuotation);
        right.setComponentAlignment(pdfQuotation, Alignment.MIDDLE_RIGHT);
        pdfQuotation.addClickListener(
                clickEvent -> {
                    double projectHandlingQty=Double.valueOf(PHCQTY.getValue());
                    double deepCleaningQty=Double.valueOf(DCCQTY.getValue());
                    double floorProtectionQty=Double.valueOf(FPCQTY.getValue());
                    double versionNo=Double.valueOf(proposalVersion.getVersion());

                    if(quickProductCheck.equalsIgnoreCase("Yes") && versionNo>1.0)
                    {
                        NotificationUtil.showNotification("quick product has been added , Please delete the quick product", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                        return;
                    }

                    if(proposalHeader.getDeepClearingChargesApplied().equals("true") && deepCleaningQty < 1)
                    {
                        NotificationUtil.showNotification("House Keeping Quantity should be greater 0", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                        return;
                    }else if(proposalHeader.getDeepClearingChargesApplied().equals("false") && deepCleaningQty < 0)
                    {
                        NotificationUtil.showNotification("House Keeping Quantity should be positive number", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                        return;
                    }
                    else if(proposalHeader.getFloorProtectionChargesApplied().equals("true") && floorProtectionQty < 1)
                    {
                        NotificationUtil.showNotification("Floor Protection Quantity should be greater 0", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                        return;
                    } else if(proposalHeader.getFloorProtectionChargesApplied().equals("false") && floorProtectionQty < 0)
                    {
                        NotificationUtil.showNotification("Floor Protection Quantity should be positive number", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                        return;
                    }
                    else if(proposalHeader.getProjectHandlingChargesApplied().equals("false") && projectHandlingQty < 0)
                    {
                        NotificationUtil.showNotification("Project Handling Quantity should be positive number", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                        return;
                    }
                    Download_quotation.open(proposalHeader,proposal,proposalVersion);
                }
        );

        String role = ((User) VaadinSession.getCurrent().getAttribute(User.class.getName())).getRole();

        if (("admin").equals(role) || ("planning").equals(role)) {
            Button downloadButton = new Button("SOW &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            downloadButton.setCaptionAsHtml(true);
            downloadButton.setIcon(FontAwesome.DOWNLOAD);
            downloadButton.setStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
            downloadButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
            downloadButton.addStyleName(ValoTheme.BUTTON_SMALL);
            downloadButton.addStyleName("margin-top-for-headerlevelbutton");
            downloadButton.addStyleName("margin-right-10-for-headerlevelbutton");
            downloadButton.setWidth("85px");
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
                        //saveVersionAmounts();
                        MarginScreen.open(proposalVersion,this.proposalHeader,this.products);

                    }
            );
        }
        horizontalLayout.addComponent(right);
        horizontalLayout.setComponentAlignment(right, Alignment.MIDDLE_RIGHT);

        return horizontalLayout;
    }

    private Component buildRemarks() {
        FormLayout verticalLayout = new FormLayout();
        verticalLayout.setMargin(new MarginInfo(false, true, false, true));
        HorizontalLayout horizontalLayout1 = new HorizontalLayout();
        horizontalLayout1.setSizeFull();

        horizontalLayout1.addComponent(buildMainFormLayoutLeft());
        horizontalLayout1.addComponent(buildMainFormLayoutCenter());
        horizontalLayout1.addComponent(buildMainFormLayoutRight());
        verticalLayout.addComponent(horizontalLayout1);
        return verticalLayout;
    }

    private Component buildMainFormLayoutLeft() {

        FormLayout formLayoutLeft = new FormLayout();
        formLayoutLeft.setSizeFull();
        formLayoutLeft.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        ttitle = new TextField("Title: ");
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

        versionNum = new TextField("Version # :");
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

        TextField tstatus = new TextField("Status :");
        tstatus.addStyleName(ValoTheme.TEXTFIELD_HUGE);
        tstatus.setValue(proposalVersion.getStatus());
        formLayoutLeft.addComponent(tstatus);
        tstatus.setReadOnly(true);

        return formLayoutLeft;
    }


    private Component buildActionButtons() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setMargin(new MarginInfo(true, true, true, true));
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

        /*Button servicesButton  = new Button("Apply Discount");
        servicesButton.addStyleName(ValoTheme.BUTTON_SMALL);
        servicesButton.addStyleName("margin-right-10-for-headerlevelbutton");
        servicesButton.addClickListener(clickEvent -> {
            Miscellaneous.open(proposalVersion,proposalHeader);
        });
        right.addComponent(servicesButton);
        right.setComponentAlignment(servicesButton, Alignment.MIDDLE_RIGHT);
*/
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
        remarksTextArea.addStyleName("text-area-size");
        left1.addComponent(remarksTextArea);
        vlayout.addComponent(left1);

        verticalLayout.addComponent(vlayout);
        return verticalLayout;
    }

    private Component getAmountLayout()
    {
        VerticalLayout verticalLayout = new VerticalLayout();
        HorizontalLayout hLayout = new HorizontalLayout();
        hLayout.setMargin(new MarginInfo(false,true,false,true));

        FormLayout left = new FormLayout();
        Label maintotalWithoutDiscountLabel = new Label("Total Without Discount:");
        left.addComponent(maintotalWithoutDiscountLabel);
        maintotalWithoutDiscountLabel.addStyleName("products-and-addons-summary-text");
        hLayout.addComponent(left);
        hLayout.setComponentAlignment(left,Alignment.MIDDLE_RIGHT);

        FormLayout left1 = new FormLayout();
        this.grandTotal = new Label();
        this.grandTotal.setConverter(getAmountConverter());
        this.grandTotal.setReadOnly(true);
        left1.addComponent(grandTotal);
        this.grandTotal.setValue(String.valueOf(proposalVersion.getAmount()));
        grandTotal.addStyleName("products-and-addons-summary-text");
        hLayout.addComponent(left1);
        hLayout.setComponentAlignment(left1,Alignment.MIDDLE_RIGHT);

        /*FormLayout left2 = new FormLayout();
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
        this.discountAmount = new TextField();
        this.discountAmount.setConverter(new StringToDoubleConverter());
        this.discountAmount.setValue(String.valueOf(proposalVersion.getDiscountAmount()));
        //this.discountAmount.setNullRepresentation("0");
        left5.addComponent(discountAmount);
        discountAmount.addStyleName("amount-text-label1");
        discountAmount.addStyleName("v-label-amount-text-label1");
        discountAmount.addStyleName("margin-top-18");
        vlayout.addComponent(left5);*/

        FormLayout left6 = new FormLayout();
        Label totalAfterDiscount = new Label("Total After Discount :");
        left6.addComponent(totalAfterDiscount);
        totalAfterDiscount.addStyleName("products-and-addons-summary-text");
        hLayout.addComponent(left6);
        hLayout.setComponentAlignment(left6,Alignment.MIDDLE_RIGHT);

        FormLayout left7 = new FormLayout();
        this.discountTotal = new Label();
        this.discountTotal.setConverter(getAmountConverter());
        LOG.info("BEFORE ROUNDING :: "+proposalVersion.getFinalAmount());
        LOG.info("AFTER ROUNDING :: "+this.round(proposalVersion.getFinalAmount(),0));

        this.discountTotal.setValue(String.valueOf(this.round(proposalVersion.getFinalAmount(),0)));
        //this.discountTotal.setValue(proposalVersion.getDiscountAmount());
        this.discountTotal.setReadOnly(true);
        left7.addComponent(discountTotal);
        discountTotal.addStyleName("products-and-addons-summary-text");
        hLayout.addComponent(left7);
        hLayout.setComponentAlignment(left7,Alignment.MIDDLE_RIGHT);

        verticalLayout.addComponent(hLayout);
        return verticalLayout;
    }


    private void onFocusToDiscountPercentage(FieldEvents.FocusEvent event) {
        LOG.info("DP focused");
        status = "DP";
    }

    private void onFocusToDiscountAmount(FieldEvents.FocusEvent event) {
        LOG.info("DA focused");
        status = "DA";
    }

    private void addonCheck()
    {
        List<AddonProduct> addons=proposalDataProvider.getVersionAddons(proposalVersion.getProposalId(),proposalVersion.getVersion());
        for(AddonProduct addonProduct : addons)
        {
            if(addonProduct.getCategoryCode().equals("Custom Addon") && (addonProduct.getCustomAddonCategory().equals("Accessories") || addonProduct.getCustomAddonCategory().equals("Appliances")))
            {
                customAddonCheck="Yes";
            }
        }
    }

    private void handleQuickProducts()
    {
        List<Product> products = proposalDataProvider.getVersionProducts(proposalVersion.getProposalId(), proposalVersion.getVersion());
        for (Product product : products) {
            LOG.info("product get category code " +product.getProductCategoryCode());
            if(product.getProductCategoryCode().startsWith("a"))
            {
                LOG.info("Quick code ");
                quickProductCheck="Yes";
            }
        }
    }

    private void updatePrice()
    {
        customAddonCheck="No";
        quickProductCheck="No";
        Double totalBeforeDiscount=0.0,totalAfterDiscount=0.0,productsTotal=0.0,addonsTotal=0.0,servicesTotal=0.0,disPercentage,disAmount=0.0,productTotalWOAccessories=0.0,deepClearingQty=0.0,deepClearingamount=0.0,floorProtectionQty=0.0,floorProtectionamount=0.0;
        Double projectHandlingCharges=0.0;
        disPercentage=proposalVersion.getDiscountPercentage();
        List<Product> products = proposalDataProvider.getVersionProducts(proposalVersion.getProposalId(), proposalVersion.getVersion());
        for (Product product : products) {
            productsTotal+=product.getAmount();
            productTotalWOAccessories += product.getCostWoAccessories();
        }

        List<AddonProduct> addons=proposalDataProvider.getVersionAddons(proposalVersion.getProposalId(),proposalVersion.getVersion());
        for(AddonProduct addonProduct : addons)
        {
            addonsTotal += addonProduct.getAmount();
        }

        //check date to apply discount
        ProposalHeader proposalHeaderCreateDate = proposalDataProvider.getProposalHeader(this.proposalHeader.getId());
        java.util.Date date = proposalHeaderCreateDate.getCreatedOn();
        java.util.Date currentDate = new Date(117, 3, 20, 0, 0, 00);

        //calculate discount/
        if (date.after(currentDate)) {
            //new proposal
            disAmount = productsTotal * (disPercentage / 100.0);
        } else {
            //old proposal
            disAmount = productTotalWOAccessories * (disPercentage/ 100);
        }


        LOG.info("Shilpa check this::"+this.proposalHeader.getProjectHandlingChargesApplied());
        productsTotalAfterDiscount = this.round((productsTotal - disAmount), 0);
        if(proposalHeader.getProjectHandlingChargesApplied().equalsIgnoreCase("true")) {
            PHCQTY.setReadOnly(false);
            PHCQTY.setValue(String.valueOf(round(productsTotalAfterDiscount,2)));
            PHCQTY.setReadOnly(true);
        }else{
            PHCQTY.setReadOnly(false);
        }

        projectHandlingCharges=Double.parseDouble(this.PHCQTY.getValue() )* (projectHandlingChargesRate / 100);
        PHCAmount.setValue(String.valueOf(round(projectHandlingCharges,2)));

        deepClearingQty=Double.valueOf(DCCQTY.getValue());
        if(productsTotalAfterDiscount > 0 )
            deepClearingamount=deepClearingQty*deepCleaningChargesRate;
        floorProtectionQty=Double.valueOf(FPCQTY.getValue());
        floorProtectionamount=floorProtectionQty*floorProtectionChargesRate;

        servicesTotal=projectHandlingCharges+floorProtectionamount+deepClearingamount;
        totalBeforeDiscount=productsTotal+addonsTotal+servicesTotal;
        totalAfterDiscount=totalBeforeDiscount-disAmount;
        totalAfterDiscount = totalAfterDiscount - totalAfterDiscount % 10;

        productPrice.setValue(String.valueOf(productsTotal));
        productPriceAfterDiscount.setValue(String.valueOf(round(productsTotalAfterDiscount,2)));
        addonPrice.setValue(String.valueOf(addonsTotal));
        grandTotal.setValue(String.valueOf(totalBeforeDiscount.intValue()));

        discountTotal.setValue(String.valueOf(totalAfterDiscount));

        if(productsTotalAfterDiscount > 0) {
            DCCAmount.setValue(String.valueOf(deepClearingamount));
            miscellaneousPrice.setValue(String.valueOf(this.round(servicesTotal, 2)));
        }else{
            miscellaneousPrice.setValue("0.0");
        }


        if (Objects.equals(proposalHeader.getPackageFlag(), "Yes")) {
            this.discountAmount.setReadOnly(false);
            this.discountAmount.setValue(String.valueOf(disAmount.intValue()) + " ");
            this.discountAmount.setReadOnly(true);
        } else {
            this.discountAmount.setValue(String.valueOf(disAmount.intValue()) + " ");
        }

        double res = totalAfterDiscount - totalAfterDiscount % 10;
        proposalVersion.setFinalAmount(res);
        proposalVersion.setProjectHandlingQty(Double.valueOf(PHCQTY.getValue()));
        proposalVersion.setDiscountPercentage(disPercentage);
        proposalVersion.setDiscountAmount(disAmount);
        proposalVersion.setAmount(totalBeforeDiscount.intValue());
        proposalVersion.setProjectHandlingAmount(projectHandlingCharges);
        proposalVersion.setDeepClearingAmount(deepClearingamount);
        proposalVersion.setFloorProtectionAmount(floorProtectionamount);
        proposalDataProvider.updateVersion(proposalVersion);

        ProposalVersion proposalVersionLatest = proposalDataProvider.getLatestVersion(this.proposalHeader.getId());
        proposalHeader.setAmount(proposalVersionLatest.getFinalAmount());
        proposalHeader.setVersion(proposalVersionLatest.getVersion());
        proposalHeader.setStatus(proposalVersionLatest.getStatus());
        proposalDataProvider.saveProposal(proposalHeader);

        addonCheck();
        handleQuickProducts();
    }

    private void calculateDiscount()
    {
        LOG.info("calculate discount ");
        Double totalVersionPrice=0.0,productsTotal=0.0,addonsTotal=0.0,servicesTotal=0.0,disPercentage =0.0,disAmount=0.0,productTotalWOAccessories=0.0;
        Double projectHandlingCharges=0.0;
        disPercentage=proposalVersion.getDiscountPercentage();
        List<Product> products = proposalDataProvider.getVersionProducts(proposalVersion.getProposalId(), proposalVersion.getVersion());
        for (Product product : products) {
            productsTotal+=product.getAmount();
            productTotalWOAccessories += product.getCostWoAccessories();
        }

        List<AddonProduct> addons=proposalDataProvider.getVersionAddons(proposalVersion.getProposalId(),proposalVersion.getVersion());
        for(AddonProduct addonProduct : addons)
        {
            addonsTotal += addonProduct.getAmount();
        }

        servicesTotal=proposalVersion.getProjectHandlingAmount()+proposalVersion.getFloorProtectionAmount()+proposalVersion.getDeepClearingAmount();
        totalVersionPrice=productsTotal+addonsTotal+servicesTotal;

        //check date to apply discount
        ProposalHeader proposalHeaderCreateDate = proposalDataProvider.getProposalHeader(this.proposalHeader.getId());
        java.util.Date date = proposalHeaderCreateDate.getCreatedOn();
        java.util.Date currentDate = new Date(117, 3, 20, 0, 0, 00);

        //calculate discount
        if (date.after(currentDate)) {
            //new proposal
            refreshDiscountForNewProposals(totalVersionPrice,addonsTotal,productsTotal,servicesTotal);
        } else {
            //old proposal
            refreshDiscountForOldProposals(productTotalWOAccessories,totalVersionPrice,(productsTotal-productTotalWOAccessories),addonsTotal,servicesTotal);
        }
    }
    private void updateTotal() {
        Double discountPercent = 0.0, discountAmount = 0.0;
        Double totalAfterDiscount=0.0;

        productsGrid.setSelectionMode(Grid.SelectionMode.NONE);
        addonsGrid.setSelectionMode(Grid.SelectionMode.NONE);

        Collection<?> productObjects = productsGrid.getSelectedRows();
        Collection<?> addonObjects = addonsGrid.getSelectedRows();
        boolean anythingSelected = true;
        this.productAndAddonSelection.getProductIds().clear();
        this.productAndAddonSelection.getAddonIds().clear();

        this.productsTotal = 0;
        this.productsTotalAfterDiscount = 0;
        double ProductsTotalWoTax = 0;
        double ProdutsMargin = 0;
        double ProductsProfit = 0;
        double ProductsManufactureAmount = 0;

        double addonsTotalWOTax = 0;
        double addonsMargin = 0;
        double addonsProfit = 0;
        double addonsManufactureAmount = 0;

        if (productObjects.size() == 0) {
            anythingSelected = false;
            productObjects = this.productsGrid.getContainerDataSource().getItemIds();
        }


        for (Object object : productObjects) {
            Double amount = (Double) this.productsGrid.getContainerDataSource().getItem(object).getItemProperty(Product.AMOUNT).getValue();
            productsTotal += amount;


            Integer id = (Integer) this.productsGrid.getContainerDataSource().getItem(object).getItemProperty(Product.ID).getValue();
            if (anythingSelected) {
                this.productAndAddonSelection.getProductIds().add(id);
            }
        }

        ProdutsMargin = (ProductsManufactureAmount / ProductsTotalWoTax) * 100;
        if (Double.isNaN(ProdutsMargin)) {
            ProdutsMargin = 0.0;
        }

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

            Integer id = (Integer) this.addonsGrid.getContainerDataSource().getItem(object).getItemProperty(AddonProduct.ID).getValue();

            if (anythingSelected) {
                this.productAndAddonSelection.getAddonIds().add(id);
            }
        }

        addonsMargin = (addonsProfit / addonsTotalWOTax) * 100;
        if (Double.isNaN(addonsMargin)) {
            addonsMargin = 0.0;
        }
        Double totalWT = ProductsTotalWoTax + addonsTotalWOTax;
        Double profit = ProductsProfit + addonsProfit;
        Double totalmanufactureamount = ProductsManufactureAmount + addonsManufactureAmount;
        Double finalmargin = (profit / totalWT) * 100;
        if (Double.isNaN(finalmargin)) {
            finalmargin = 0.0;
        }
        proposalVersion.setProfit(round(profit, 2));
        proposalVersion.setMargin(round(finalmargin, 2));
        proposalVersion.setAmountWotax(round(totalWT, 2));
        proposalVersion.setManufactureAmount(round(totalmanufactureamount, 2));

        Double totalWoAccessories = 0.0;
        List<Product> products = proposalDataProvider.getVersionProducts(proposalVersion.getProposalId(), proposalVersion.getVersion());
        for (Product product : products) {
            totalWoAccessories += product.getCostWoAccessories();
        }

        //check date to apply discount
        ProposalHeader proposalHeaderCreateDate = proposalDataProvider.getProposalHeader(this.proposalHeader.getId());
        java.util.Date date = proposalHeaderCreateDate.getCreatedOn();
        java.util.Date currentDate = new Date(117, 3, 20, 0, 0, 00);

        double servicesTotal = proposalVersion.getProjectHandlingAmount() + proposalVersion.getDeepClearingAmount() + proposalVersion.getFloorProtectionAmount();
        Double totalAmount = productsTotal+addonsTotal + servicesTotal;
        double costOfAccessories = productsTotal - totalWoAccessories;

        discountPercent = proposalVersion.getDiscountPercentage();
        if (discountPercent == null) {
            discountPercent = 0.0;
            productsTotalAfterDiscount =0.0;
        }

        /*if (date.after(currentDate)) {
            //refreshDiscountForNewProposals(totalAmount, addonsTotal, productsTotal,servicesTotal);
                discountAmount = productsTotal * (discountPercent / 100.0);
                totalAfterDiscount = this.round((productsTotal - discountAmount), 0);
                productsTotalAfterDiscount = totalAfterDiscount;
        } else {
            //refreshDiscountForOldProposals(totalWoAccessories, totalAmount, costOfAccessories, addonsTotal,servicesTotal);
            discountAmount = totalWoAccessories * (discountPercent / 100);
            totalAfterDiscount = this.round((totalWoAccessories - discountAmount), 0);
            productsTotalAfterDiscount = totalAfterDiscount;
        }
//        if (Objects.equals(proposalHeader.getPackageFlag(), "Yes")) {
//            this.discountAmount.setReadOnly(false);
//            this.discountAmount.setValue(String.valueOf(discountAmount.intValue()) + " ");
//            this.discountAmount.setReadOnly(true);
//        } else {
//            this.discountAmount.setValue(String.valueOf(discountAmount.intValue()) + " ");
//        }
        disAmount = discountAmount.intValue();
        if(proposalVersion.getProjectHandlingChargesApplied().equals("true"))
        {
            proposalVersion.setProjectHandlingAmount(totalAfterDiscount * (projectHandlingChargesRate / 100));
        }
        Double grandTotal = totalAfterDiscount + costOfAccessories + addonsTotal +servicesTotal;
        double res = grandTotal - grandTotal % 10;
//
//        this.discountTotal.setReadOnly(false);
//        this.discountTotal.setValue(String.valueOf(res));
//
//        this.grandTotal.setReadOnly(false);
//        this.grandTotal.setValue(totalAmount.intValue() + "");
//        this.grandTotal.setReadOnly(true);

        this.productPrice.setReadOnly(false);
        this.productPrice.setValue(this.productsTotal + "");
        this.productPrice.setReadOnly(true);

        this.productPriceAfterDiscount.setReadOnly(false);
        double roundedProductDiscount = productsTotalAfterDiscount - productsTotalAfterDiscount % 10;
        this.productPriceAfterDiscount.setValue(roundedProductDiscount+"");
        this.productPriceAfterDiscount.setReadOnly(true);

        this.addonPrice.setReadOnly(false);
        this.addonPrice.setValue(addonsTotal + "");
        this.addonPrice.setReadOnly(true);

        this.miscellaneousPrice.setReadOnly(false);
        this.miscellaneousPrice.setValue(servicesTotal + "");
        this.miscellaneousPrice.setReadOnly(true);

        this.totalPrice.setReadOnly(false);
        this.totalPrice.setValue((productsTotalAfterDiscount+addonsTotal+servicesTotal) + "");
        this.totalPrice.setReadOnly(true);

        proposalVersion.setFinalAmount(totalAmount);
        proposalVersion.setDiscountAmount(discountAmount);
        proposalVersion.setDiscountPercentage(discountPercent);
        proposalVersion.setAmount(totalAfterDiscount);

        productAndAddonSelection.setDiscountPercentage(proposalVersion.getDiscountPercentage());
        productAndAddonSelection.setDiscountAmount(proposalVersion.getDiscountAmount());
        proposalDataProvider.updateVersion(proposalVersion);
*/
    }

   private void refreshDiscountForNewProposals(Double totalAmount, Double addonsTotal, Double productsTotal,double serviceTotal) {
        Double disPercent = 0.0, disAmount = 0.0;
        rateForDiscount = proposalHeader.getMaxDiscountPercentage();
        if ("DP".equals(status)) {
            LOG.info("inside DP ");
            if(discountPercentage.getValue() != null){
                disPercent = Double.valueOf(discountPercentage.getValue());
            } else{
                disAmount = 0.0;
                disPercent = 0.0;
                this.discountAmount.setValue("0.0");
                this.discountPercentage.setValue("0.0");

            }
            if (disPercent <= rateForDiscount) {
                if (disPercent == null) {
                    disPercent = 0.0;
                }
                disAmount = productsTotal * (disPercent / 100.0);
                if (Objects.equals(proposalHeader.getPackageFlag(), "Yes")) {
                    this.discountAmount.setReadOnly(false);
                    this.discountAmount.setValue(String.valueOf(disAmount.intValue()) + " ");
                    this.discountAmount.setReadOnly(true);
                } else {
                    LOG.info("discount amt " +String.valueOf(disAmount.intValue()) );
                    this.discountAmount.setValue(String.valueOf(disAmount.intValue()) + " ");
                    LOG.info("discount amount textbox value " +discountAmount.getValue());
                }
                this.disAmount = disAmount.intValue();
            } else {
                NotificationUtil.showNotification("Discount should not exceed " + rateForDiscount.intValue(), NotificationUtil.STYLE_BAR_ERROR_SMALL);
                return;
            }
        } else if ("DA".equals(status)) {
            LOG.info("inside DA");
            disAmount = (Double) this.discountAmount.getConvertedValue();
            if(disAmount != null)
             disPercent = (disAmount / productsTotal) * 100;
            else{
                disAmount = 0.0;
                disPercent = 0.0;
                this.discountAmount.setValue("0.0");
                this.discountPercentage.setValue("0.0");

            }
            if (disPercent <= rateForDiscount) {
                this.discountPercentage.setValue(String.valueOf(round(disPercent, 2)));
            } else {
                NotificationUtil.showNotification("Discount should not exceed " + rateForDiscount.intValue(), NotificationUtil.STYLE_BAR_ERROR_SMALL);
                return;
            }
        }
        LOG.info("discount amount " +disAmount+ "discount Percentage " +disPercent);
        Double totalAfterDiscount = this.round((productsTotal - disAmount), 0);
        Double grandTotal = totalAfterDiscount + addonsTotal + serviceTotal;
        double res = grandTotal - grandTotal % 10;

        this.productPriceAfterDiscount.setValue(String.valueOf(round(productsTotal-disAmount,2)));

        this.discountTotal.setReadOnly(false);
        this.discountTotal.setValue(String.valueOf(res));

        this.grandTotal.setReadOnly(false);
        this.grandTotal.setValue(totalAmount.intValue() + "");
        this.grandTotal.setReadOnly(true);

        productAndAddonSelection.setDiscountPercentage(proposalVersion.getDiscountPercentage());
        productAndAddonSelection.setDiscountAmount(proposalVersion.getDiscountAmount());

        proposalVersion.setAmount(totalAmount.intValue());
        proposalVersion.setDiscountAmount(disAmount);
        proposalVersion.setDiscountPercentage(disPercent);
        proposalVersion.setFinalAmount(res);
       if(proposalHeader.getProjectHandlingChargesApplied().equalsIgnoreCase("true")) {
           PHCQTY.setReadOnly(false);
           PHCQTY.setValue(String.valueOf(round(productsTotal-disAmount,2)));
           PHCQTY.setReadOnly(true);
       }else{
           PHCQTY.setReadOnly(false);
       }

       ProposalVersion proposalVersionLatest = proposalDataProvider.getLatestVersion(this.proposalHeader.getId());
       proposalHeader.setAmount(proposalVersionLatest.getFinalAmount());
       proposalHeader.setVersion(proposalVersionLatest.getVersion());
       proposalHeader.setStatus(proposalVersionLatest.getStatus());
       proposalDataProvider.saveProposal(proposalHeader);
    }


    private void refreshDiscountForOldProposals(Double totalWoAccessories, Double totalAmount, Double costOfAccessories, Double addonsTotal,double servicesTotal) {
        LOG.debug("for old proposal");
        Double discountPercent = 0.0, discountAmount = 0.0;
        //rateForDiscount=rateForDiscount*100;
        rateForDiscount = proposalHeader.getMaxDiscountPercentage();
        if ("DP".equals(status)) {
            discountPercent = (Double) this.discountPercentage.getConvertedValue();
            if (discountPercent <= rateForDiscount) {
                if (discountPercent == null) {
                    discountPercent = 0.0;
                }

                discountAmount = totalWoAccessories * (discountPercent / 100);
                if (Objects.equals(proposalHeader.getPackageFlag(), "Yes")) {
                    this.discountAmount.setReadOnly(false);
                    this.discountAmount.setValue(String.valueOf(discountAmount.intValue()) + " ");
                    this.discountAmount.setReadOnly(true);
                } else {
                    this.discountAmount.setValue(String.valueOf(discountAmount.intValue()) + " ");
                }
                disAmount = discountAmount.intValue();
            } else {
                NotificationUtil.showNotification("Discount should not exceed " + rateForDiscount.intValue(), NotificationUtil.STYLE_BAR_ERROR_SMALL);
                return;
            }
        } else if ("DA".equals(status)) {
            discountAmount = (Double) this.discountAmount.getConvertedValue();
            discountPercent = (discountAmount / totalWoAccessories) * 100;
            if (discountPercent <= rateForDiscount) {
                this.discountPercentage.setValue(String.valueOf(round(discountPercent, 2)));
            } else {
                NotificationUtil.showNotification("Discount should not exceed " + rateForDiscount.intValue(), NotificationUtil.STYLE_BAR_ERROR_SMALL);
                return;
            }
        }
        Double totalAfterDiscount = this.round((totalWoAccessories - discountAmount), 0);
        proposalVersion.setProjectHandlingAmount(totalAfterDiscount * (projectHandlingChargesRate / 100));
        Double grandTotal = totalAfterDiscount + costOfAccessories + addonsTotal +servicesTotal;
        double res = grandTotal - grandTotal % 10;

        this.productPriceAfterDiscount.setValue(String.valueOf(round((totalWoAccessories+costOfAccessories)-discountAmount,2)));
        this.discountTotal.setReadOnly(false);
        this.discountTotal.setValue(String.valueOf(res));

        this.grandTotal.setReadOnly(false);
        this.grandTotal.setValue(totalAmount.intValue() + "");
        this.grandTotal.setReadOnly(true);

        productAndAddonSelection.setDiscountPercentage(proposalVersion.getDiscountPercentage());
        productAndAddonSelection.setDiscountAmount(proposalVersion.getDiscountAmount());
        proposalDataProvider.updateVersion(proposalVersion);

        ProposalVersion proposalVersionLatest = proposalDataProvider.getLatestVersion(this.proposalHeader.getId());
        proposalHeader.setAmount(proposalVersionLatest.getFinalAmount());
        proposalHeader.setVersion(proposalVersionLatest.getVersion());
        proposalHeader.setStatus(proposalVersionLatest.getStatus());
        proposalDataProvider.saveProposal(proposalHeader);
    }

    private void onDiscountAmountValueChange(Property.ValueChangeEvent valueChangeEvent) {
        if ("DA".equals(status)) {
            //updateTotal();
            calculateDiscount();
        }
    }

    private void onDiscountPercentageValueChange(Property.ValueChangeEvent valueChangeEvent) {
        if ("DP".equals(status)) {
            //updateTotal();
            calculateDiscount();
        }
    }

    private void onGrandTotalValueChange(Property.ValueChangeEvent valueChangeEvent) {
        status = "DP";
        saveVersionFlag = "Yes";
        calculateDiscount();
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
        horizontalLayout.setComponentAlignment(title, Alignment.TOP_LEFT);
        verticalLayout.setSpacing(true);

        HorizontalLayout hLayoutInner = new HorizontalLayout();

        addKitchenOrWardrobeButton = new Button("Kitchen/Wardrobe");
        addKitchenOrWardrobeButton.setIcon(FontAwesome.PLUS_CIRCLE);
        addKitchenOrWardrobeButton.addStyleName(ValoTheme.BUTTON_SMALL);
        hLayoutInner.addComponent(addKitchenOrWardrobeButton);
        hLayoutInner.setComponentAlignment(addKitchenOrWardrobeButton, Alignment.TOP_RIGHT);
        hLayoutInner.setSpacing(true);

        addFromCatalogueButton = new Button("From Catalogue");
        addFromCatalogueButton.setIcon(FontAwesome.PLUS_CIRCLE);
        addFromCatalogueButton.addStyleName(ValoTheme.BUTTON_SMALL);
        hLayoutInner.addComponent(addFromCatalogueButton);
        hLayoutInner.setComponentAlignment(addFromCatalogueButton, Alignment.TOP_RIGHT);

        if (Objects.equals(proposalHeader.getBeforeProductionSpecification(), "yes")) {
            addFromProductLibrary = new Button("From Product Library");
            addFromProductLibrary.setIcon(FontAwesome.PLUS_CIRCLE);
            addFromProductLibrary.addStyleName(ValoTheme.BUTTON_SMALL);
            hLayoutInner.addComponent(addFromProductLibrary);
            hLayoutInner.setComponentAlignment(addFromProductLibrary, Alignment.TOP_RIGHT);
        }

        horizontalLayout.addComponent(hLayoutInner);
        horizontalLayout.setComponentAlignment(hLayoutInner, Alignment.TOP_RIGHT);

        addKitchenOrWardrobeButton.addClickListener(
                clickEvent -> {
                    Product newProduct = new Product();
                    newProduct.setType(Product.TYPES.CUSTOMIZED.name());
                    newProduct.setProposalId(this.proposalHeader.getId());
                    newProduct.setFromVersion(this.vid);
                    newProduct.setProductCategoryLocked("No");
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

        if (Objects.equals(proposalHeader.getBeforeProductionSpecification(), "yes")) {
            addFromProductLibrary.addClickListener(
                    clickEvent -> {
                        //AllProductsDetailsWindow.open();
                        //Dummy.open(proposal,proposalVersion,proposalHeader);
                        AllProposalLibrary.open(proposal, proposalVersion, proposalHeader);
                    }
            );
        }


        productContainer = new BeanItemContainer<>(Product.class);

        GeneratedPropertyContainer genContainer = createGeneratedProductPropertyContainer();

        productsGrid = new Grid(genContainer);
        productsGrid.setSelectionMode(Grid.SelectionMode.NONE);
        /*productsGrid.addSelectionListener(this::updateTotal);*/
        productsGrid.setSizeFull();
        productsGrid.setColumnReorderingAllowed(true);
        productsGrid.setColumns(Product.SEQ, Product.SPACE_TYPE, Product.ROOM_CODE, Product.TITLE, "productCategoryText", Product.AMOUNT, TYPE, "actions");

        List<Grid.Column> columns = productsGrid.getColumns();
        int idx = 0;

        columns.get(idx++).setHeaderCaption("Seq");
        columns.get(idx++).setHeaderCaption("Space Type");
        columns.get(idx++).setHeaderCaption("Room");
        columns.get(idx++).setHeaderCaption("Title");
        columns.get(idx++).setHeaderCaption("Category");
        columns.get(idx++).setHeaderCaption("Amount");
        columns.get(idx++).setHeaderCaption("Type");
        columns.get(idx++).setHeaderCaption("Actions").setRenderer(new ViewEditDeleteButtonValueRenderer(new ViewEditDeleteButtonValueRenderer.ViewEditDeleteButtonClickListener() {
            @Override
            public void onView(ClickableRenderer.RendererClickEvent rendererClickEvent) {

                if (("Published").equals(proposalVersion.getInternalStatus()) || ("Confirmed").equals(proposalVersion.getInternalStatus()) || ("Locked").equals(proposalVersion.getInternalStatus()) || ("DSO").equals(proposalVersion.getInternalStatus()) || ("PSO").equals(proposalVersion.getInternalStatus())) {
                    Notification.show("Cannot copy on Published, Confirmed and Locked versions");
                    return;
                }

                if (("Yes").equals(proposalHeader.getPackageFlag())) {
                    Notification.show("Cannot copy Products");
                    return;
                }
                String role = ((User) VaadinSession.getCurrent().getAttribute(User.class.getName())).getRole();
                if (Objects.equals(proposalHeader.getAdminPackageFlag(), "Yes") && !("admin").equals(role)) {
                    Notification.show("Cannot copy Products");
                    return;
                }
                if(viewOnlyValue.equals("Yes"))
                {
                    Notification.show("Cannot copy Product");
                    return;
                }

                Product p = (Product) rendererClickEvent.getItemId();

                productContainer.removeAllItems();

                List<Product> copy = proposalDataProvider.getVersionProducts(proposalHeader.getId(), proposalVersion.getVersion());
                int length = (copy.size()) + 1;

                Product proposalProductDetails = proposalDataProvider.getProposalProductDetails(p.getId(), p.getFromVersion());
                List<Module> modulesFromOldProduct = proposalProductDetails.getModules();
                Product copyProduct = new Product();
                copyProduct.setSpaceType(p.getSpaceType());
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
                copyProduct.setProductCategoryLocked(p.getProductCategoryLocked());
                copyProduct.setColorGroupCode(p.getColorGroupCode());
                copyProduct.setCustomColorCode(p.getCustomColorCode());
                copyProduct.setFinishSetId(p.getFinishSetId());
                copyProduct.setAddons(p.getAddons());
                proposalDataProvider.updateProduct(copyProduct);
                List<Product> proposalProductUpdated = proposalDataProvider.getVersionProducts(proposalHeader.getId(), p.getFromVersion());

                for (Product updatedProduct : proposalProductUpdated) {
                    productContainer.addItem(updatedProduct);
                }
                productsGrid.setContainerDataSource(createGeneratedProductPropertyContainer());
                /*updateTotal();
                status = "DP";
                saveVersionAmounts();*/
                updatePrice();

            }

            @Override
            public void onEdit(ClickableRenderer.RendererClickEvent rendererClickEvent) {

                Product product = (Product) rendererClickEvent.getItemId();

                if (product.getType().equals(Product.TYPES.CUSTOMIZED.name()) || product.getType().equals(Product.TYPES.PRODUCT_LIBRARY.name())) {
                    if (product.getModules().isEmpty()) {
                        Product productDetails = proposalDataProvider.getProposalProductDetails(product.getId(), product.getFromVersion());

                        product.setModules(productDetails.getModules());
                        product.setAddons(productDetails.getAddons());
                    }

                    if (product.getFileAttachmentList().isEmpty()) {
                        List<FileAttachment> productAttachments = proposalDataProvider.getProposalProductDocuments(product.getId());
                        product.setFileAttachmentList(productAttachments);
                    }
                    //   LOG.info("product details " +product);
                    CustomizedProductDetailsWindow.open(proposal, product, proposalVersion, proposalHeader);
                } else {
                    CatalogueProduct catalogueProduct = new CatalogueProduct();
                    catalogueProduct.populateFromProduct(product);
                    CatalogItemDetailsWindow.open(proposal, catalogueProduct, proposalVersion, proposalHeader);
                }
            }

            @Override
            public void onDelete(ClickableRenderer.RendererClickEvent rendererClickEvent) {

                if (("Published").equals(proposalVersion.getInternalStatus()) || ("Confirmed").equals(proposalVersion.getInternalStatus()) || ("Locked").equals(proposalVersion.getInternalStatus()) || ("DSO").equals(proposalVersion.getInternalStatus()) || ("PSO").equals(proposalVersion.getInternalStatus())) {
                    Notification.show("Cannot delete products on Published, Confirmed and Locked versions");
                    return;
                }
                String role = ((User) VaadinSession.getCurrent().getAttribute(User.class.getName())).getRole();
                if (Objects.equals(proposalHeader.getAdminPackageFlag(), "Yes") && !("admin").equals(role)) {
                    Notification.show("Cannot delete Products");
                    return;
                }
                if(viewOnlyValue.equals("Yes"))
                {
                    Notification.show("Cannot delete Product");
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

                                for (Product product1 : proposalDataProvider.getVersionProducts(proposalHeader.getId(), proposalVersion.getVersion())) {
                                    if (product1.getSeq() > seq) {
                                        product1.setSeq(product1.getSeq() - 1);
                                        proposalDataProvider.updateProductSequence(product1.getSeq(), product1.getId());
                                    }
                                }
                                List<Product> productsUpdated = proposalDataProvider.getVersionProducts(proposalHeader.getId(), proposalVersion.getVersion());
                                for (Product updatedProduct : productsUpdated) {
                                    productContainer.addItem(updatedProduct);
                                }
                                productsGrid.setContainerDataSource(createGeneratedProductPropertyContainer());
                                productsGrid.getSelectionModel().reset();
                                /*updateTotal();*/
                                updatePrice();
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


        Component productSummaryLayout = buildProductSummaryComponents();
        verticalLayout.addComponent(productSummaryLayout);
        verticalLayout.setComponentAlignment(productSummaryLayout,Alignment.MIDDLE_RIGHT);

        return verticalLayout;
    }

    private Component buildProductSummaryComponents() {
        HorizontalLayout productSummaryLayout  = new HorizontalLayout();
        productSummaryLayout.setSpacing(true);
        productSummaryLayout.setSizeUndefined();

        Label productPricetitle = new Label("Total Product Price : ");
        productPricetitle.setStyleName("products-and-addons-summary-text");
        productSummaryLayout.addComponent(productPricetitle);

        this.productPrice = new Label();
        this.productPrice.setConverter(getAmountConverter());
        productPrice.setStyleName("products-and-addons-summary-text");
        productSummaryLayout.addComponent(productPrice);

        Label Discount = new Label("Discount % :");
        Discount.addStyleName("products-and-addons-summary-text");
        productSummaryLayout.addComponent(Discount);

        this.discountPercentage = new TextField();
        discountPercentage.addStyleName("products-and-addons-summary-text");
        this.discountPercentage.setConverter(new StringToDoubleConverter());
        this.discountPercentage.setValue(String.valueOf(proposalVersion.getDiscountPercentage()));
        productSummaryLayout.addComponent(discountPercentage);

        Label DiscountAmount = new Label("Discount Amount :");
        DiscountAmount.addStyleName("products-and-addons-summary-text");
        productSummaryLayout.addComponent(DiscountAmount);

        this.discountAmount = new TextField();
        this.discountAmount.setConverter(new StringToDoubleConverter());
        this.discountAmount.setValue(String.valueOf(proposalVersion.getDiscountAmount()));
        discountAmount.addStyleName("products-and-addons-summary-text");
        productSummaryLayout.addComponent(discountAmount);

        Label productPriceAfterDiscounttitle = new Label("Total Product Price after Discount : ");
        productPriceAfterDiscounttitle.setStyleName("products-and-addons-summary-text");
        productSummaryLayout.addComponent(productPriceAfterDiscounttitle);

        this.productPriceAfterDiscount = new Label();
        this.productPriceAfterDiscount.setStyleName("products-and-addons-summary-text");
        this.productPriceAfterDiscount.setConverter(getAmountConverter());
        productSummaryLayout.addComponent(productPriceAfterDiscount);

        return productSummaryLayout;
    }
    /*private void onProductTotalValueChange(Property.ValueChangeEvent valueChangeEvent) {

        status = "DP";
        saveVersionFlag = "Yes";
        updateTotal();
    }*/

    private Component buildServices()
    {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(new MarginInfo(false, true, false, true));

        Label addonTitle = new Label("Miscellaneous Charges");
        addonTitle.setStyleName("products-and-addons-label-text");
        verticalLayout.addComponent(addonTitle);
        verticalLayout.setComponentAlignment(addonTitle, Alignment.TOP_LEFT);
//        verticalLayout.setSpacing(true);

        /*servicesContainer =new BeanItemContainer<>(ProposalServices.class);
        servicesGrid=new Grid();
        servicesGrid.setSizeFull();
        servicesGrid.setColumnReorderingAllowed(true);
        servicesGrid.addColumn("title");
        servicesGrid.addColumn("Amount");
        servicesGrid.setHeight("200px");

        List<ProposalServices> miscellaneousList=proposalDataProvider.getVersionServices(proposalHeader.getId(),proposalVersion.getVersion());
        LOG.info("misc list "+miscellaneousList.size());
        try {

            for(ProposalServices miscellaneous:miscellaneousList)
            {
                LOG.info("misc json" +miscellaneous);
                servicesContainer.addItem(miscellaneous);
               // servicesGrid.addRow(miscellaneous.get("title").toString(),miscellaneous.get("Amount").toString());
            }
        }
        catch (Exception e)
        {
            LOG.info("exception in service container");
            e.printStackTrace();
        }


        LOG.info("container size " +servicesContainer.size());
        servicesGrid = new Grid(servicesContainer);
        servicesGrid.setSelectionMode(Grid.SelectionMode.NONE);
        servicesGrid.setSizeFull();
        servicesGrid.setColumnReorderingAllowed(true);
        servicesGrid.setColumns(ProposalServices.SERVICE_TITLE,ProposalServices.AMOUNT);
        List<Grid.Column> columns = servicesGrid.getColumns();
        int idx = 0;
        columns.get(idx++).setHeaderCaption("Service Title");
        columns.get(idx++).setHeaderCaption("Amount");*/

//        verticalLayout.addComponent(horizontalLayout);
//        verticalLayout.setSpacing(true);
//        verticalLayout.addComponent(servicesGrid);
//        verticalLayout.addComponent(servicesGrid);

        return verticalLayout;
    }

    private Component buildServicesSummaryComponents() {
        HorizontalLayout serviceSummaryLayout  = new HorizontalLayout();
        serviceSummaryLayout.setMargin(new MarginInfo(false,true,false,false));
        Label servicePricetitle = new Label("Total Miscellaneous Charges : ");
        servicePricetitle.setStyleName("products-and-addons-summary-text");
        serviceSummaryLayout.addComponent(servicePricetitle);
        serviceSummaryLayout.setComponentAlignment(servicePricetitle, Alignment.MIDDLE_RIGHT);

        this.miscellaneousPrice = new Label();
        this.miscellaneousPrice.setConverter(getAmountConverter());
        miscellaneousPrice.setStyleName("products-and-addons-summary-text");
        serviceSummaryLayout.addComponent(miscellaneousPrice);
        serviceSummaryLayout.setComponentAlignment(miscellaneousPrice, Alignment.MIDDLE_RIGHT);

        return serviceSummaryLayout;
    }
    private Component buildAddons() {

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        verticalLayout.setMargin(new MarginInfo(false, true, false, true));

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSizeFull();
        horizontalLayout.setStyleName("v-has-width-forLabel");

        Label addonTitle = new Label("Addon Details");
        addonTitle.setStyleName("products-and-addons-label-text");
        horizontalLayout.addComponent(addonTitle);
        horizontalLayout.setComponentAlignment(addonTitle, Alignment.TOP_LEFT);
        verticalLayout.setSpacing(true);

        HorizontalLayout hLayoutInner = new HorizontalLayout();

        addonAddButton = new Button("Add");
        addonAddButton.setIcon(FontAwesome.PLUS_CIRCLE);
        addonAddButton.addStyleName(ValoTheme.BUTTON_SMALL);
        addonAddButton.addClickListener(clickEvent -> {
            AddonProduct addonProduct = new AddonProduct();
            addonProduct.setAdd(true);
            AddonDetailsWindow.open(addonProduct, "Add Addon", true, proposalVersion, proposalHeader);
        });
        hLayoutInner.addComponent(addonAddButton);
        hLayoutInner.setComponentAlignment(addonAddButton, Alignment.TOP_RIGHT);
        hLayoutInner.setSpacing(true);


        customAddonAddButton = new Button("Custom Addon");
        customAddonAddButton.setIcon(FontAwesome.PLUS_CIRCLE);
        customAddonAddButton.addStyleName(ValoTheme.BUTTON_SMALL);
        customAddonAddButton.addClickListener(clickEvent -> {
            AddonProduct addonProduct = new AddonProduct();
            addonProduct.setAdd(true);
            CustomAddonDetailsWindow.open(addonProduct, "Add Addon", true, proposalVersion, proposalHeader);
        });
        hLayoutInner.addComponent(customAddonAddButton);
        hLayoutInner.setComponentAlignment(customAddonAddButton, Alignment.TOP_RIGHT);

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
        addonsGrid.setColumns(AddonProduct.SEQ, AddonProduct.ADDON_SPACE_TYPE, AddonProduct.ROOM_CODE, AddonProduct.ADDON_CATEGORY_CODE, AddonProduct.PRODUCT_TYPE_CODE, AddonProduct.PRODUCT_SUBTYPE_CODE, AddonProduct.BRAND_CODE,
                AddonProduct.PRODUCT, AddonProduct.UOM, AddonProduct.RATE, AddonProduct.QUANTITY, AddonProduct.AMOUNT, "actions");

        List<Grid.Column> columns = addonsGrid.getColumns();
        int idx = 0;
        columns.get(idx++).setHeaderCaption("#");
        columns.get(idx++).setHeaderCaption("Space Type");
        columns.get(idx++).setHeaderCaption("Room");
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

                if (("Custom Addon").equals(addon.getCategoryCode())) {
                    CustomAddonDetailsWindow.open(addon, "Edit Addon", true, proposalVersion, proposalHeader);
                } else {
                    AddonDetailsWindow.open(addon, "Edit Addon", true, proposalVersion, proposalHeader);
                }
            }

            @Override
            public void onDelete(ClickableRenderer.RendererClickEvent rendererClickEvent) {

                if (("Published").equals(proposalVersion.getInternalStatus()) || ("Confirmed").equals(proposalVersion.getInternalStatus()) || ("Locked").equals(proposalVersion.getInternalStatus()) || ("DSO").equals(proposalVersion.getInternalStatus()) || ("PSO").equals(proposalVersion.getInternalStatus())) {
                    Notification.show("Cannot delete addons on Published, Confirmed and Locked versions");
                    return;
                }
                String role = ((User) VaadinSession.getCurrent().getAttribute(User.class.getName())).getRole();
                if (Objects.equals(proposalHeader.getAdminPackageFlag(), "Yes") && !("admin").equals(role)) {
                    Notification.show("Cannot delete Addons");
                    return;
                }

                if(viewOnlyValue.equals("Yes"))
                {
                    Notification.show("You are Authorized to delete Addons");
                    return;
                }

                ConfirmDialog.show(UI.getCurrent(), "", "Are you sure you want to Delete this Addon?",
                        "Yes", "No", dialog -> {
                            if (dialog.isConfirmed()) {
                                AddonProduct addon = (AddonProduct) rendererClickEvent.getItemId();
                                proposalDataProvider.removeProposalAddon(addon.getId());
                                List<AddonProduct> addons = proposalDataProvider.getVersionAddons(proposalHeader.getId(), proposalVersion.getVersion());

                                int seq = addon.getSeq();
                                addonsContainer.removeAllItems();

                                for (AddonProduct addonProduct : addons) {
                                    if (addonProduct.getSeq() > seq) {
                                        addonProduct.setSeq(addonProduct.getSeq() - 1);
                                        proposalDataProvider.updateProposalAddon(proposalHeader.getId(), addonProduct);
                                    }
                                }
                                addonsContainer.addAll(addons);
                                addonsGrid.setContainerDataSource(createGeneratedAddonsPropertyContainer());
                                updatePrice();
                            }
                        });

            }
        }));

        verticalLayout.addComponent(horizontalLayout);
        verticalLayout.setSpacing(true);

        verticalLayout.addComponent(addonsGrid);

        //shilpa add addon summary here
        Component addonSummarycomponent = buildAddonSummaryComponents ();
        verticalLayout.addComponent(addonSummarycomponent);
        verticalLayout.setComponentAlignment(addonSummarycomponent,Alignment.MIDDLE_RIGHT);

       List<AddonProduct> existingAddons = proposal.getAddons();
        int seq = 0;
        for (AddonProduct existingAddon : existingAddons) {
            existingAddon.setSeq(++seq);
        }
        addonsContainer.addAll(existingAddons);
        addonsGrid.sort(AddonProduct.SEQ, SortDirection.ASCENDING);

        return verticalLayout;

    }
    private Component buildAddonSummaryComponents() {
        HorizontalLayout addonSummaryLayout  = new HorizontalLayout();
        Label addonPricetitle = new Label("Total Addon Price : ");
        addonPricetitle.setStyleName("products-and-addons-summary-text");
        addonSummaryLayout.addComponent(addonPricetitle);
        addonSummaryLayout.setComponentAlignment(addonPricetitle, Alignment.TOP_LEFT);

        this.addonPrice = new Label();
        this.addonPrice.setConverter(getAmountConverter());
        addonPrice.setStyleName("products-and-addons-summary-text");
        addonSummaryLayout.addComponent(addonPrice);
        addonSummaryLayout.setComponentAlignment(addonPrice, Alignment.TOP_LEFT);

        return addonSummaryLayout;
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
    private boolean checkModuleSeq() {
        List<Product> product = proposal.getProducts();
        for (Product product1 : product) {
            if (product1.getManualSeq() == 0) {
                NotificationUtil.showNotification("Manual sequence cannot be zero", NotificationUtil.STYLE_BAR_WARNING_SMALL);
                return false;
            }
        }
        return true;
    }

    private void checkProductsAndAddonsAvailable1(Button.ClickEvent clickEvent) {
        LOG.info("inside checkproducts and addons avaialble1");
        if (proposal.getProducts().isEmpty() && proposal.getAddons().isEmpty()) {
            NotificationUtil.showNotification("No products found. Please add product(s) first to generate the Quote.", NotificationUtil.STYLE_BAR_WARNING_SMALL);
        }
    }

    private void checkProductsAndAddonsAvailable(Button.ClickEvent clickEvent) {
        /*LOG.info("clickEvent to confirm");

        ConfirmDialog.show(UI.getCurrent(), "", "Do You want to print Booking Form",
                "Yes", "No", dialog -> {
                    if (dialog.isConfirmed()) {
                        LOG.info("inside confirm");
                        value = "yes";
//                        createQuoteResourcePdf();
                        LOG.info("File downloader ");
                        StreamResource quotePdfresource = createQuoteResourcePdf();
//                        fileDownloaderPdf = new FileDownloader(quotePdfresource);
//                        fileDownloaderPdf.extend(dialog.getOkButton());

                    } else {
                        LOG.info("else part in confirm");
                        value = "no";
                    }
                });*/
        String replace = discountAmount.getValue().replace(",", "");
        double discountamount = Double.valueOf(replace);
        productAndAddonSelection.setDiscountPercentage(Double.valueOf(this.discountPercentage.getValue()));
        productAndAddonSelection.setDiscountAmount(discountamount);
        productAndAddonSelection.setCity(proposalHeader.getPcity());
        BookingFormPopupWindow.open(productAndAddonSelection);

        if (proposal.getProducts().isEmpty() && proposal.getAddons().isEmpty()) {
            NotificationUtil.showNotification("No products found. Please add product(s) first to generate the Quote.", NotificationUtil.STYLE_BAR_WARNING_SMALL);
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
        LOG.info("stream resoutrce " );
        StreamResource.StreamSource source = () -> {
            LOG.info("inside stream resource");
            return getInputStreamPdf();
            /*if (!proposal.getProducts().isEmpty()) {
              //  LOG.info("header value" + proposalVersion.getDiscountAmount() + "discount amount" + discountAmount.getValue());
                String replace = discountAmount.getValue().replace(",", "");
                double discountamount = Double.valueOf(replace);
                return getInputStreamPdf();

            } else {
                return null;
            }*/
        };
        return new StreamResource(source, "Quotation.pdf");
    }

    private InputStream getInputStreamPdf() {
        String replace = discountAmount.getValue().replace(",", "");
        double discountamount= Double.valueOf(replace);

        productAndAddonSelection.setDiscountPercentage(Double.valueOf(this.discountPercentage.getValue()));
        productAndAddonSelection.setDiscountAmount(discountamount);
        if(proposalVersion.getVersion().equals("1.0"))
        {
            productAndAddonSelection.setBookingFormFlag("Yes");
        }else {
            productAndAddonSelection.setBookingFormFlag("No");
        }

        productAndAddonSelection.setCity(proposalHeader.getPcity());
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

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


    private void submit(Button.ClickEvent clickEvent) {
        double projectHandlingQty=Double.valueOf(PHCQTY.getValue());
        double deepCleaningQty=Double.valueOf(DCCQTY.getValue());
        double floorProtectionQty=Double.valueOf(FPCQTY.getValue());
        double versionNo=Double.valueOf(versionNum.getValue());
        JSONObject response = null;
        if (proposalHeader.getMaxDiscountPercentage() >= Double.valueOf(discountPercentage.getValue())) {
            try {
                if (proposalVersion.getAmount() == 0) {
                    NotificationUtil.showNotification("Total amount is zero,Cannot publish", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                    return;
                }
                if(versionNo >1.0 && customAddonCheck.equalsIgnoreCase("Yes") && proposalHeader.getCustomAddonCheck().equalsIgnoreCase("yes"))
                {
                        NotificationUtil.showNotification("Custom Addon Added", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                        return;
                }

                if(quickProductCheck.equalsIgnoreCase("Yes") && versionNo>1.0)
                {
                    NotificationUtil.showNotification("quick product has been added , Please delete the quick product", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                    return;
                }

                if(proposalHeader.getDeepClearingChargesApplied().equals("true") && deepCleaningQty < 1)
                {
                    NotificationUtil.showNotification("House Keeping Quantity should be greater 0", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                    return;
                }else if(proposalHeader.getDeepClearingChargesApplied().equals("false") && deepCleaningQty < 0)
                {
                    NotificationUtil.showNotification("House Keeping Quantity should be positive number", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                    return;
                }
                else if(proposalHeader.getFloorProtectionChargesApplied().equals("true") && floorProtectionQty < 1)
                {
                    NotificationUtil.showNotification("Floor Protection Quantity should be greater 0", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                    return;
                } else if(proposalHeader.getFloorProtectionChargesApplied().equals("false") && floorProtectionQty < 0)
                {
                    NotificationUtil.showNotification("Floor Protection Quantity should be positive number", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                    return;
                }
                else if(proposalHeader.getProjectHandlingChargesApplied().equals("false") && projectHandlingQty < 0)
                {
                    NotificationUtil.showNotification("Project Handling Quantity should be positive number", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                    return;
                }

                binder.commit();

                //  LOG.info("value in submit" + totalWithoutDiscount.getValue());
                if (remarksTextArea == null || remarksTextArea.isEmpty()) {
                    NotificationUtil.showNotification("Remarks cannot be empty", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                    return;
                } else if (grandTotal.getValue().equals("0")) {
                    NotificationUtil.showNotification("Please add products", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                    return;
                }

                String disAmount = discountAmount.getValue();
                proposalVersion.setDiscountAmount(Double.parseDouble(disAmount.replace(",", "")));
                proposalVersion.setDiscountPercentage(Double.parseDouble(discountPercentage.getValue()));
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
                LocalDateTime localDate = LocalDateTime.now();
                proposalVersion.setBusinessDate(dtf.format(localDate));
                response = proposalDataProvider.publishVersion(proposalVersion.getVersion(), proposalHeader.getId(),proposalVersion.getBusinessDate().toString());
                ProposalVersion proposalVersionLatest = proposalDataProvider.getLatestVersion(this.proposalHeader.getId());
                proposalHeader.setStatus(proposalVersionLatest.getStatus());
                proposalHeader.setVersion(proposalVersionLatest.getVersion());
                proposalHeader.setAmount(proposalVersionLatest.getFinalAmount());
                proposalDataProvider.saveProposal(proposalHeader);

            } catch (FieldGroup.CommitException e) {
                NotificationUtil.showNotification("Validation Error, please fill all mandatory fields!", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            }
            proposalDataProvider.updateVersion(proposalVersion);
            publishVersionMessage(response, proposalVersion.getProposalId(), proposalVersion.getVersion());

        } else {
            NotificationUtil.showNotification("Discount should not exceed " + rateForDiscount.intValue(), NotificationUtil.STYLE_BAR_ERROR_SMALL);
            discountAmount.setValue(String.valueOf(proposalVersion.getDiscountAmount()).replace(",", ""));
            discountPercentage.setValue(String.valueOf(proposalVersion.getDiscountPercentage()));
        }
    }

    private SendToCRMOnPublish updatePriceInCRMOnPublish() {
        Double amount = 0.0;
        String quoteNumberCRM = "";
        SendToCRMOnPublish sendToCRM = new SendToCRMOnPublish();
        sendToCRM.setOpportunity_name(proposalHeader.getCrmId());
        List<ProposalHeader> proposalHeaders = proposalDataProvider.getProposalHeadersByCrmIds(proposalHeader.getCrmId());
        List<ProposalVersion> proposalVersionList = new ArrayList<>();
        List<ProposalVersion> proposalVersionList1 = new ArrayList<>();
        for (ProposalHeader p : proposalHeaders) {
            List<ProposalVersion> pv = proposalDataProvider.getAllProductDetails(p.getId());
            for (ProposalVersion version : pv) {
                if (version.getVersion().equals("2.0")) {
                    amount += version.getFinalAmount();
                    quoteNumberCRM += p.getQuoteNoNew();
                } else if (version.getVersion().startsWith("1.") && version.getStatus().equals("Published") && !version.getVersion().equals("1.0")) {
                    if (version.getVersion().equals("1.0")) {
                        amount += version.getFinalAmount();
                        quoteNumberCRM += p.getQuoteNoNew();
                    } else {
                        proposalVersionList.add(version);
                    }
                } else if (version.getVersion().startsWith("0.") && version.getStatus().equals("Published")) {
                    proposalVersionList1.add(version);
                }
                    /*else if(version.getVersion().equals("1.0"))
                    {
                        amount+=version.getFinalAmount();
                        quoteNumberCRM+=p.getQuoteNoNew();
                    }*/
            }
            if (proposalVersionList.size() != 0) {
                Date date = proposalVersionList.get(0).getDate();
                ProposalVersion proposalVersionTobeConsidered = proposalVersionList.get(0);
                for (ProposalVersion proposalVersion : proposalVersionList) {
                    if (proposalVersion.getDate().after(date) && !proposalVersion.getInternalStatus().equals("Locked")) {
                        proposalVersionTobeConsidered = proposalVersion;
                    }
                }
                amount += proposalVersionTobeConsidered.getFinalAmount();
                quoteNumberCRM += p.getQuoteNoNew();
            }
            if (proposalVersionList1.size() != 0) {
                Date date = proposalVersionList1.get(0).getDate();
                ProposalVersion proposalVersionTobeConsidered = proposalVersionList1.get(0);
                for (ProposalVersion proposalVersion : proposalVersionList1) {
                    if (proposalVersion.getUpdatedOn().after(date)) {
                        proposalVersionTobeConsidered = proposalVersion;
                    }
                }
                amount += proposalVersionTobeConsidered.getFinalAmount();
                quoteNumberCRM += p.getQuoteNoNew();
            }
        }
        sendToCRM.setEstimated_project_cost_c(amount);
        sendToCRM.setQuotation_number_c(quoteNumberCRM);
        LOG.info("CRM JSON ON PUBLISH " + sendToCRM.toString());
        return sendToCRM;
    }

    private SendToCRM updatePriceInCRMOnConfirm() {
        Double amount = 0.0;
        Double DSOAmount=0.0;
        String quoteNumberCRM = "";
        SendToCRM sendToCRM = new SendToCRM();
        sendToCRM.setOpportunity_name(proposalHeader.getCrmId());
        List<ProposalVersion> proposalVersionList = new ArrayList<>();
        List<ProposalVersion> proposalVersionListDSO=new ArrayList<>();
        List<ProposalHeader> proposalHeaders = proposalDataProvider.getProposalHeadersByCrmIds(proposalHeader.getCrmId());
        for (ProposalHeader p : proposalHeaders) {
            proposalVersionList = new ArrayList<>();
            proposalVersionListDSO=new ArrayList<>();
            List<ProposalVersion> pv = proposalDataProvider.getAllProductDetails(p.getId());
            for (ProposalVersion version : pv) {
                if (version.getVersion().equals("2.0") && version.getStatus().equals("DSO")) {
                    //proposalVersionList.add(version);
                    proposalVersionListDSO.add(version);
                } else if (version.getVersion().equals("1.0") && version.getStatus().equals("Confirmed")) {
                    proposalVersionList.add(version);
                }
            }
            if (proposalVersionList.size() != 0) {
                Date date = proposalVersionList.get(0).getUpdatedOn();
                ProposalVersion proposalVersionTobeConsidered = proposalVersionList.get(0);
                for (ProposalVersion proposalVersion : proposalVersionList) {
                    if (!proposalVersion.getInternalStatus().equals("Locked")) {

                        if (proposalVersion.getUpdatedOn().after(date) || proposalVersion.getUpdatedOn().equals(date)) {
                            //LOG.info("DSO Value" + proposalVersion.getProposalId() + " " + proposalVersion.getVersion() + " " +proposalVersion.getFinalAmount());
                            proposalVersionTobeConsidered = proposalVersion;
                        }
                    }

                }
                amount += proposalVersionTobeConsidered.getFinalAmount();
                quoteNumberCRM += p.getQuoteNoNew();
            }

            if (proposalVersionListDSO.size() != 0) {
                Date date = proposalVersionListDSO.get(0).getUpdatedOn();
                ProposalVersion proposalVersionTobeConsidered = proposalVersionListDSO.get(0);
                //LOG.info("DSO list size " +proposalVersionListDSO.size());
                for (ProposalVersion proposalVersion : proposalVersionListDSO) {
                    if (!proposalVersion.getInternalStatus().equals("Locked")) {
                        //LOG.info("1st if in dso");
                        if (proposalVersion.getUpdatedOn().after(date) || proposalVersion.getUpdatedOn().equals(date)) {
                            //LOG.info("2nd if in dso" + proposalVersion.getProposalId() + " " + proposalVersion.getVersion() + proposalVersionTobeConsidered.getFinalAmount());
                            proposalVersionTobeConsidered = proposalVersion;
                        }
                    }
                }
                DSOAmount += proposalVersionTobeConsidered.getFinalAmount();
                quoteNumberCRM += p.getQuoteNoNew();
            }
        }
        PublishOnCRM publishOnCRM = new PublishOnCRM(proposalHeader);
        SendToCRMOnPublish sendToCRMOnPublish = publishOnCRM.updatePriceInCRMOnPublish();
        sendToCRM.setEstimated_project_cost_c(sendToCRMOnPublish.getEstimated_project_cost_c());
        sendToCRM.setFinal_proposal_amount_c(DSOAmount);
        sendToCRM.setBooking_order_value_c(amount);
        sendToCRM.setQuotation_number_c(quoteNumberCRM);
        LOG.debug("Send to CRM on confirm### : " + sendToCRM.toString());

        return sendToCRM;
    }

    private void confirm(Button.ClickEvent clickEvent) {
        if (proposalHeader.getMaxDiscountPercentage() >= Double.valueOf(discountPercentage.getValue())) {
            try {
                binder.commit();
                if (remarksTextArea == null || remarksTextArea.isEmpty()) {
                    NotificationUtil.showNotification("Remarks cannot be empty", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                    return;
                } else if (grandTotal.getValue().equals("0")) {
                    NotificationUtil.showNotification("Please add products", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                    return;
                }
                String disAmount = discountAmount.getValue();
                proposalVersion.setAmount(Double.parseDouble(grandTotal.getValue()));
                proposalVersion.setDiscountAmount(Double.parseDouble(disAmount.replace(",", "")));
                proposalVersion.setDiscountPercentage(Double.parseDouble(discountPercentage.getValue()));
//                double res = totalAfterDiscount - totalAfterDiscount % 10;
                proposalVersion.setFinalAmount(Double.parseDouble(discountTotal.getValue()));
                proposalVersion.setStatus(ProposalVersion.ProposalStage.Confirmed.name());
                proposalVersion.setInternalStatus(ProposalVersion.ProposalStage.Confirmed.name());
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
                LocalDateTime localDate = LocalDateTime.now();
                proposalVersion.setBusinessDate(dtf.format(localDate));
                ProposalVersion proposalVersionLatest = proposalDataProvider.getLatestVersion(this.proposalHeader.getId());
                proposalHeader.setStatus(proposalVersionLatest.getStatus());
                proposalHeader.setVersion(proposalVersionLatest.getVersion());
                proposalHeader.setAmount(proposalVersionLatest.getFinalAmount());
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
                            proposalVersion.setBusinessDate(dtf.format(localDate));
                            proposalHeader.setStatus(proposalVersion.getStatus());
                            proposalHeader.setVersion(proposalVersion.getVersion());
                            proposalHeader.setAmount(proposalVersion.getFinalAmount());
                            proposalDataProvider.saveProposalOnConfirm(proposalHeader);
                            proposalDataProvider.copyProposalSowLineItems(proposalHeader.getId(), "1.0");
                            proposalDataProvider.lockAllPreSalesVersions(ProposalVersion.ProposalStage.Locked.name(), proposalHeader.getId());
                            success = proposalDataProvider.confirmVersion(proposalVersion.getVersion(), proposalHeader.getId(), proposalVersion.getFromVersion(), proposalVersion.getToVersion(),proposalVersion.getBusinessDate().toString());
                            proposalDataProvider.updateProposalProductOnConfirm(proposalVersion.getVersion(), proposalVersion.getProposalId(), proposalVersion.getFromVersion());
                            proposalDataProvider.updateProposalAddonOnConfirm(proposalVersion.getVersion(), proposalVersion.getProposalId(), proposalVersion.getFromVersion());
                            proposalDataProvider.updateVersion(proposalVersion);
                            proposalHeader.setVersion(proposalVersion.getVersion());
                            proposalHeader.setAmount(proposalVersion.getFinalAmount());
                            SendToCRM sendToCRM = updatePriceInCRMOnConfirm();
                            proposalDataProvider.updateCrmPrice(sendToCRM);
                            proposalDataProvider.saveProposal(proposalHeader);
                        } else if (versionNew.startsWith("1.")) {
                            proposalVersion.setFromVersion(proposalVersion.getVersion());
                            proposalVersion.setToVersion(proposalVersion.getVersion());
                            proposalVersion.setVersion("2.0");
                            proposalVersion.setStatus(ProposalVersion.ProposalStage.DSO.name());
                            proposalVersion.setInternalStatus(ProposalVersion.ProposalStage.DSO.name());
                            proposalVersion.setBusinessDate(dtf.format(localDate));
                            proposalHeader.setStatus(proposalVersion.getStatus());
                            proposalHeader.setVersion(proposalVersion.getVersion());
                            proposalHeader.setAmount(proposalVersion.getFinalAmount());
                            proposalDataProvider.copyProposalSowLineItems(proposalHeader.getId(), "2.0");
                            boolean success1 = proposalDataProvider.saveProposal(proposalHeader);
                            proposalDataProvider.lockAllPostSalesVersions(ProposalVersion.ProposalStage.Locked.name(), proposalHeader.getId());
                            success = proposalDataProvider.versionDesignSignOff(proposalVersion.getVersion(), proposalHeader.getId(), proposalVersion.getFromVersion(), proposalVersion.getToVersion(),proposalVersion.getBusinessDate().toString());
                            proposalDataProvider.updateProposalProductOnConfirm(proposalVersion.getVersion(), proposalVersion.getProposalId(), proposalVersion.getFromVersion());
                            proposalDataProvider.updateProposalAddonOnConfirm(proposalVersion.getVersion(), proposalVersion.getProposalId(), proposalVersion.getFromVersion());
                            proposalDataProvider.updateVersion(proposalVersion);
                            proposalHeader.setVersion(proposalVersion.getVersion());
                            SendToCRM sendToCRM = updatePriceInCRMOnConfirm();
                            proposalDataProvider.updateCrmPrice(sendToCRM);
                            proposalHeader.setAmount(proposalVersion.getFinalAmount());
                            proposalDataProvider.saveProposal(proposalHeader);


                        } else if (versionNew.startsWith("2.")) {
                            proposalVersion.setFromVersion(proposalVersion.getVersion());
                            proposalVersion.setToVersion(proposalVersion.getVersion());
                            proposalVersion.setVersion("3.0");
                            proposalVersion.setStatus(ProposalVersion.ProposalStage.PSO.name());
                            proposalVersion.setInternalStatus(ProposalVersion.ProposalStage.PSO.name());
                            proposalVersion.setBusinessDate(dtf.format(localDate));
                            proposalHeader.setStatus(proposalVersion.getStatus());
                            proposalHeader.setVersion(proposalVersion.getVersion());
                            proposalHeader.setAmount(proposalVersion.getFinalAmount());
                            boolean success1 = proposalDataProvider.saveProposal(proposalHeader);
                            proposalDataProvider.lockAllVersionsExceptPSO(ProposalVersion.ProposalStage.Locked.name(), proposalHeader.getId());
                            success = proposalDataProvider.versionProductionSignOff(proposalVersion.getVersion(), proposalHeader.getId(), proposalVersion.getFromVersion(), proposalVersion.getToVersion(),proposalVersion.getBusinessDate().toString());
                            proposalDataProvider.updateProposalProductOnConfirm(proposalVersion.getVersion(), proposalVersion.getProposalId(), proposalVersion.getFromVersion());
                            proposalDataProvider.updateProposalAddonOnConfirm(proposalVersion.getVersion(), proposalVersion.getProposalId(), proposalVersion.getFromVersion());
                            proposalDataProvider.updateVersion(proposalVersion);
                            proposalHeader.setVersion(proposalVersion.getVersion());
                            proposalHeader.setAmount(proposalVersion.getFinalAmount());
                            if (versionNew.equals("2.0")) {
                                SendToCRM sendToCRM = updatePriceInCRMOnConfirm();
                                proposalDataProvider.updateCrmPrice(sendToCRM);
                            }
                            proposalDataProvider.saveProposal(proposalHeader);
                        }


                        if (success) {
                            saveButton.setVisible(false);
                            try {
                                JSONObject quoteFile = proposalDataProvider.updateSowLineItems(proposalHeader.getId(), Double.parseDouble(proposalVersion.getVersion()), "yes");
                                if (quoteFile.getString("status").equalsIgnoreCase("failure")) {
                                    NotificationUtil.showNotification("Couldn't create SOW file. Please click on the button to generate the sheet again", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                                    return;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                LOG.error("Couldnt create Sow File :" + e.getMessage());
                            }
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
        } else {
            NotificationUtil.showNotification("Discount should not exceed " + rateForDiscount.intValue(), NotificationUtil.STYLE_BAR_ERROR_SMALL);
            discountAmount.setValue(String.valueOf(proposalVersion.getDiscountAmount()).replace(",", ""));
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
    /*private GeneratedPropertyContainer createGeneratedServicesContainer(){
        GeneratedPropertyContainer genContainer = new GeneratedPropertyContainer(servicesContainer);
        return genContainer;
    }*/
    private GeneratedPropertyContainer createGeneratedProductPropertyContainer() {
        GeneratedPropertyContainer genContainer = new GeneratedPropertyContainer(productContainer);
        genContainer.addGeneratedProperty("actions", getActionTextGenerator());
        genContainer.addGeneratedProperty("productCategoryText", getProductCategoryTextGenerator());
        return genContainer;
    }


    private void save(Button.ClickEvent clickEvent)
    {
        double projectHandlingQty=Double.valueOf(PHCQTY.getValue());
        double deepCleaningQty=Double.valueOf(DCCQTY.getValue());
        double floorProtectionQty=Double.valueOf(FPCQTY.getValue());
        double versionNum=Double.valueOf(proposalVersion.getVersion());
        LOG.info("PHQ " +projectHandlingQty+ " DCC " +deepCleaningQty+ " FPC " +floorProtectionQty);
        if (proposalHeader.getMaxDiscountPercentage() >= Double.valueOf(discountPercentage.getValue())) {
            remarksTextArea.setValidationVisible(false);
            try {
                remarksTextArea.validate();
            } catch (Exception e) {
                NotificationUtil.showNotification("Validation Error, please fill remarks fields and remarks should not exceed 255 characters", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                remarksTextArea.setValidationVisible(false);
                return;
            }

            if(customAddonCheck.equalsIgnoreCase("Yes") && proposalHeader.getCustomAddonCheck().equalsIgnoreCase("yes"))
            {
                LOG.info("Custom addon added ");
                Notification.show("Custom addon added");
                //NotificationUtil.showNotification("Custom Addon Added", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            }

            if(quickProductCheck.equalsIgnoreCase("Yes") && versionNum>1.0)
            {
                NotificationUtil.showNotification("quick product has been added , Please delete the quick product", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                return;
            }

            if(proposalHeader.getDeepClearingChargesApplied().equals("true") && deepCleaningQty < 1)
            {
                NotificationUtil.showNotification("House Keeping Quantity should be greater 0", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                return;
            }else if(proposalHeader.getDeepClearingChargesApplied().equals("false") && deepCleaningQty < 0)
            {
                NotificationUtil.showNotification("House Keeping Quantity should be positive number", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                return;
            }
            else if(proposalHeader.getFloorProtectionChargesApplied().equals("true") && floorProtectionQty < 1)
            {
                NotificationUtil.showNotification("Floor Protection Quantity should be greater 0", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                return;
            } else if(proposalHeader.getFloorProtectionChargesApplied().equals("false") && floorProtectionQty < 0)
            {
                NotificationUtil.showNotification("Floor Protection Quantity should be positive number", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                return;
            }
            else if(proposalHeader.getProjectHandlingChargesApplied().equals("false") && projectHandlingQty < 0)
            {
                NotificationUtil.showNotification("Project Handling Quantity should be positive number", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                return;
            }
            else
                 saveProposalVersion();
        } else {
            NotificationUtil.showNotification("Discount should not exceed " + rateForDiscount.intValue(), NotificationUtil.STYLE_BAR_ERROR_SMALL);
            discountAmount.setValue(String.valueOf(proposalVersion.getDiscountAmount()).replace(",", ""));
            discountPercentage.setValue(String.valueOf(proposalVersion.getDiscountPercentage()));
        }
    }

    private void saveProposalVersion() {
        try {
            String disAmount = discountAmount.getValue();
            proposalVersion.setAmount(Double.parseDouble(grandTotal.getValue()));
            proposalVersion.setFinalAmount(Double.parseDouble(discountTotal.getValue()));
            proposalVersion.setDiscountAmount(Double.parseDouble(disAmount.replace(",", "")));
            proposalVersion.setDiscountPercentage(Double.parseDouble(discountPercentage.getValue()));
            proposalVersion.setRemarks(remarksTextArea.getValue());
            proposalVersion.setTitle(this.ttitle.getValue());
            proposalVersion.setProjectHandlingAmount(Double.valueOf(PHCAmount.getValue()));
            proposalVersion.setDeepClearingQty(Double.valueOf(DCCQTY.getValue()));
            proposalVersion.setDeepClearingAmount(Double.valueOf(DCCAmount.getValue()));
            proposalVersion.setFloorProtectionSqft(Double.valueOf(FPCQTY.getValue()));
            proposalVersion.setFloorProtectionAmount(Double.valueOf(FPCAmount.getValue()));

            ProposalVersion proposalVersionLatest = proposalDataProvider.getLatestVersion(this.proposalHeader.getId());
            proposalHeader.setStatus(proposalVersionLatest.getStatus());
            proposalHeader.setVersion(proposalVersionLatest.getVersion());
            proposalHeader.setAmount(proposalVersionLatest.getFinalAmount());
            boolean success = proposalDataProvider.saveProposal(proposalHeader);

            if (success) {
                NotificationUtil.showNotification("Saved successfully!", NotificationUtil.STYLE_BAR_SUCCESS_SMALL);

            } else {
                NotificationUtil.showNotification("Cannot Save Proposal!!", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                return;

            }

            proposalVersion = proposalDataProvider.updateVersion(proposalVersion);
            NotificationUtil.showNotification("Saved successfully!", NotificationUtil.STYLE_BAR_SUCCESS_SMALL);
            DashboardEventBus.post(new ProposalEvent.VersionCreated(proposalVersion));
            close();
        } catch (Exception e) {
            e.printStackTrace();
            NotificationUtil.showNotification("Couldn't Save Proposal! Please contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            LOG.info("Exception :" + e.toString());
        }
    }

    private void saveVersionAmounts() {
        try {
            String disAmount = discountAmount.getValue();
            proposalVersion.setAmount(Double.parseDouble(grandTotal.getValue()));
            proposalVersion.setFinalAmount(Double.parseDouble(discountTotal.getValue()));
            proposalVersion.setDiscountAmount(Double.parseDouble(disAmount.replace(",", "")));
            proposalVersion.setDiscountPercentage(Double.parseDouble(discountPercentage.getValue()));
            proposalVersion.setRemarks(remarksTextArea.getValue());
            proposalVersion.setTitle(this.ttitle.getValue());

            proposalVersion = proposalDataProvider.updateVersion(proposalVersion);
            NotificationUtil.showNotification("Saved successfully!", NotificationUtil.STYLE_BAR_SUCCESS_SMALL);
            DashboardEventBus.post(new ProposalEvent.VersionCreated(proposalVersion));
        } catch (Exception e) {
            NotificationUtil.showNotification("Couldn't Save Proposal! Please contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            LOG.info("Exception :" + e.toString());
        }
    }

    private void saveWithoutClose(Button.ClickEvent clickEvent) {
        if (proposalHeader.getMaxDiscountPercentage() >= Double.valueOf(discountPercentage.getValue())) {
            try {

                close();
            } catch (Exception e) {
                NotificationUtil.showNotification("Couldn't Save Proposal! Please contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                LOG.info("Exception :" + e.toString());
            }
        } else {
            NotificationUtil.showNotification("Discount should not exceed " + rateForDiscount.intValue(), NotificationUtil.STYLE_BAR_ERROR_SMALL);
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
                if (Objects.equals(proposalHeader.getBeforeProductionSpecification(), "yes")) {
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
                if (versionNew.startsWith("1.")) {
                    productionSignOffButton.setVisible(false);
                    designSignOffButton.setEnabled(true);
                    confirmButton.setVisible(false);
                } else if (versionNew.startsWith("2.")) {
                    designSignOffButton.setVisible(false);
                    productionSignOffButton.setEnabled(true);
                    confirmButton.setVisible(false);
                } else if (versionNew.startsWith("3.")){
                    designSignOffButton.setVisible(false);
                    productionSignOffButton.setVisible(false);
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
                if (Objects.equals(proposalHeader.getBeforeProductionSpecification(), "yes")) {
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

                if (product.getType().equals(Product.TYPES.CUSTOMIZED.name()) || product.getType().equals(Product.TYPES.PRODUCT_LIBRARY.name())) {
                    if (StringUtils.isNotEmpty(product.getProductCategory())) {
                        return product.getProductCategory();
                    } else {
                        List<LookupItem> lookupItems = proposalDataProvider.getLookupItems(ProposalDataProvider.CATEGORY_LOOKUP);
                        // LOG.info("product.getProductCategoryCode()" +product.getProductCategoryCode());
                        if (product.getProductCategoryCode().equals("K")) {
                            product.setProductCategoryCode("Kitchen");
                        } else if (product.getProductCategoryCode().equals("W")) {
                            product.setProductCategoryCode("Wardrobe");
                        }
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
        if (Objects.equals(proposalHeader.getBeforeProductionSpecification(), "yes")) {
            addFromProductLibrary.setEnabled(false);
        }
        PHCQTY.setReadOnly(true);
        FPCQTY.setReadOnly(true);
        DCCQTY.setReadOnly(true);
        PHCAmount.setReadOnly(true);
        FPCAmount.setReadOnly(true);
        DCCAmount.setReadOnly(true);
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
            updatePrice();
           // saveVersionAmounts();
            productsGrid.sort(Product.SEQ, SortDirection.ASCENDING);
        }
    }

    @Subscribe
    public void ServiceChange(final ProposalEvent.ServicesCreatedEvent event) {
        /*List<Product> products = proposal.getProducts();
        boolean removed = products.remove(event.getProduct());
        if (removed) {*/
        List<ProposalServices> miscellaneousList=proposalDataProvider.getVersionServices(proposalHeader.getId(),proposalVersion.getVersion());
            servicesContainer.removeAllItems();
            servicesContainer.addAll(miscellaneousList);
           // servicesGrid.setContainerDataSource(createGeneratedProductPropertyContainer());
            servicesGrid.getSelectionModel().reset();
            updatePrice();
            // saveVersionAmounts();
        //}
    }

    @Subscribe
    public void productCreatedOrUpdated(final ProposalEvent.ProductCreatedOrUpdatedEvent event) {
        List<Product> products = proposalDataProvider.getVersionProducts(proposalHeader.getId(), proposalVersion.getVersion());
        productContainer.removeAllItems();
        productContainer.addAll(products);
        productsGrid.setContainerDataSource(createGeneratedProductPropertyContainer());
        productsGrid.getSelectionModel().reset();
        updatePrice();
        //saveVersionAmounts();
        productsGrid.sort(Product.SEQ, SortDirection.ASCENDING);
    }

    @Subscribe
    public void addonUpdated(final ProposalEvent.ProposalAddonUpdated event) {
        AddonProduct eventAddonProduct = event.getAddonProduct();
        // LOG.info("Product :"  + eventAddonProduct.toString());
        persistAddon(eventAddonProduct);
        List<AddonProduct> addons = proposalDataProvider.getVersionAddons(proposalHeader.getId(), proposalVersion.getVersion());
        addonsContainer.removeAllItems();
        addonsContainer.addAll(addons);
        addonsGrid.setContainerDataSource(createGeneratedAddonsPropertyContainer());
        addonsGrid.sort(AddonProduct.SEQ, SortDirection.ASCENDING);
        updatePrice();
        //saveVersionAmounts();

    }

    private void persistAddon(AddonProduct eventAddonProduct) {
        if (eventAddonProduct.getId() == 0) {
            proposalDataProvider.addProposalAddon(eventAddonProduct.getProposalId(), eventAddonProduct);
        } else {
            proposalDataProvider.updateProposalAddon(eventAddonProduct.getProposalId(), eventAddonProduct);
        }
    }

    private void handlepackage() {
        if (Objects.equals(proposalHeader.getPackageFlag(), "Yes")) {
            addKitchenOrWardrobeButton.setEnabled(false);
            addFromProductLibrary.setEnabled(false);
            addFromCatalogueButton.setEnabled(false);
            discountAmount.setReadOnly(true);
            discountPercentage.setReadOnly(true);
            PHCQTY.setReadOnly(true);
            FPCQTY.setReadOnly(true);
            DCCQTY.setReadOnly(true);

            String role = ((User) VaadinSession.getCurrent().getAttribute(User.class.getName())).getRole();
            if (Objects.equals(proposalHeader.getAdminPackageFlag(), "Yes") && !("admin").equals(role)) {
                confirmButton.setEnabled(false);
                submitButton.setEnabled(false);
                designSignOffButton.setEnabled(false);
                productionSignOffButton.setEnabled(false);
                addonAddButton.setEnabled(false);
                customAddonAddButton.setEnabled(false);
            }
        }
    }

    private void publishVersionMessage(JSONObject response, int proposalId, String version) {
        try {
            if (response.getString("status").equalsIgnoreCase("success")) {
                String disAmount = String.valueOf(discountAmount);

                NotificationUtil.showNotification("Version published successfully", NotificationUtil.STYLE_BAR_SUCCESS_SMALL);
                proposalVersion.setStatus(ProposalVersion.ProposalStage.Published.name());
                proposalVersion.setInternalStatus(ProposalVersion.ProposalStage.Published.name());
                proposalVersion.setAmount(Double.parseDouble(grandTotal.getValue()));
                proposalVersion.setFinalAmount(Double.parseDouble(discountTotal.getValue()));
                proposalVersion.setDiscountAmount(Double.parseDouble(disAmount.replace(",", "")));
                proposalVersion.setDiscountPercentage(Double.parseDouble(discountPercentage.getValue()));
               // proposalVersion.setRemarks(remarksTextArea.getValue());
                proposalVersion.setTitle(ttitle.getValue());
                proposalDataProvider.updateVersion(proposalVersion);

                DashboardEventBus.post(new ProposalEvent.VersionCreated(proposalVersion));
                /*if (!(proposalVersion.getVersion().startsWith("2.")))
                {*/
                PublishOnCRM publishOnCRM = new PublishOnCRM(proposalHeader);
                SendToCRMOnPublish sendToCRMOnPublish = publishOnCRM.updatePriceInCRMOnPublish();
                proposalDataProvider.updateCrmPriceOnPublish(sendToCRMOnPublish);
                //}

                DashboardEventBus.unregister(this);
                close();
            } else {
                LOG.info("elase part ***");
                JSONObject textValues = new JSONObject();
                textValues.put("discountAmount", discountAmount.getValue());
                textValues.put("grandTotal", Double.parseDouble(grandTotal.getValue()));
                textValues.put("discountTotal", Double.parseDouble(discountTotal.getValue()));
                textValues.put("discountPercentage", Double.parseDouble(discountPercentage.getValue()));
                /*textValues.put("remarksTextArea", remarksTextArea.getValue());*/
                textValues.put("ttitle", this.ttitle.getValue());
                textValues.put("versionNum", String.valueOf(versionNum));
                VersionPublishOrDiscardPopUpWindow.open(response, proposalId, version, this.proposal, proposalVersion, productAndAddonSelection, proposalHeader, textValues);


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setReadOnlyForUser() {
        addKitchenOrWardrobeButton.setEnabled(false);
        addFromCatalogueButton.setEnabled(false);
        addonAddButton.setEnabled(false);
        confirmButton.setEnabled(false);
        designSignOffButton.setEnabled(false);
        submitButton.setEnabled(false);
        customAddonAddButton.setEnabled(false);
        discountAmount.setReadOnly(true);
        discountPercentage.setReadOnly(true);
        remarksTextArea.setReadOnly(true);
        ttitle.setReadOnly(true);
        PHCQTY.setReadOnly(true);
        FPCQTY.setReadOnly(true);
        DCCQTY.setReadOnly(true);
        if (Objects.equals(proposalHeader.getBeforeProductionSpecification(), "yes")) {
            addFromProductLibrary.setEnabled(false);

        }
    }
    public Component buildServiceHeading()
    {

        VerticalLayout verticalLayout=new VerticalLayout();
        verticalLayout.setSpacing(true);
        verticalLayout.setMargin(new MarginInfo(false,true,false,true));

        Label heading=new Label("Title");
        heading.addStyleName("margin-label-style1");
        verticalLayout.addComponent(heading);
        verticalLayout.setComponentAlignment(heading,Alignment.MIDDLE_LEFT);


        Label projectHandlingLabel=new Label("Project Handling Charges");
        projectHandlingLabel.addStyleName("margin-label-style2");
        verticalLayout.addComponent(projectHandlingLabel);
        verticalLayout.setComponentAlignment(projectHandlingLabel,Alignment.MIDDLE_LEFT);

        Label deepClearingLabel=new Label("House Keeping Charges");
        deepClearingLabel.addStyleName("margin-label-style2");
        verticalLayout.addComponent(deepClearingLabel);
        verticalLayout.setComponentAlignment(deepClearingLabel,Alignment.MIDDLE_LEFT);

        Label floorProtectionLabel=new Label("Floor Protection Charges");
        floorProtectionLabel.addStyleName("margin-label-style2");
        verticalLayout.addComponent(floorProtectionLabel);
        verticalLayout.setComponentAlignment(floorProtectionLabel,Alignment.MIDDLE_LEFT);

        return verticalLayout;
    }
    public Component buildLayoutForUnit()
    {
        VerticalLayout verticalLayout=new VerticalLayout();
        verticalLayout.setSpacing(true);
        verticalLayout.setMargin(new MarginInfo(false,true,false,true));

        Label heading=new Label();
        heading.addStyleName("margin-label-style");
        heading.setValue("Unit");
        verticalLayout.addComponent(heading);
        verticalLayout.setComponentAlignment(heading,Alignment.MIDDLE_LEFT);

        Label PHCUnit =new Label();
        PHCUnit.addStyleName("margin-label-style2");
        PHCUnit.setValue("%");
        verticalLayout.addComponent(PHCUnit);
        verticalLayout.setComponentAlignment(PHCUnit,Alignment.MIDDLE_LEFT);

        Label DCCUnit =new Label();
        DCCUnit.addStyleName("margin-label-style2");
        DCCUnit.setValue("Rs");
        verticalLayout.addComponent(DCCUnit);
        verticalLayout.setComponentAlignment(DCCUnit,Alignment.MIDDLE_LEFT);

        Label FPCUnit = new Label();
        FPCUnit.addStyleName("margin-label-style2");
        FPCUnit.setReadOnly(true);
        FPCUnit.setValue("Rs per Sqft");
        verticalLayout.addComponent(FPCUnit);
        verticalLayout.setComponentAlignment(FPCUnit,Alignment.MIDDLE_LEFT);

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
        verticalLayout.setComponentAlignment(heading,Alignment.MIDDLE_LEFT);

        PHCRate =new Label();
        PHCRate.addStyleName("margin-label-style2");
        PHCRate.setValue(String.valueOf(projectHandlingChargesRate));
        verticalLayout.addComponent(PHCRate);
        verticalLayout.setComponentAlignment(PHCRate,Alignment.MIDDLE_LEFT);

        DCCRate =new Label();
        DCCRate.addStyleName("margin-label-style2");
        DCCRate.setValue(String.valueOf(deepCleaningChargesRate));
        verticalLayout.addComponent(DCCRate);
        verticalLayout.setComponentAlignment(DCCRate,Alignment.MIDDLE_LEFT);

        FPCRate = new Label();
        FPCRate.addStyleName("margin-label-style2");
        FPCRate.setReadOnly(true);
        FPCRate.setValue(String.valueOf(floorProtectionChargesRate));
        verticalLayout.addComponent(FPCRate);
        verticalLayout.setComponentAlignment(FPCRate,Alignment.MIDDLE_LEFT);

        return verticalLayout;
    }
    public Component buildLayout5()
    {
        VerticalLayout verticalLayout=new VerticalLayout();
        verticalLayout.setSpacing(true);
//        verticalLayout.setMargin(new MarginInfo(false,true,false,true));

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
/*
        PHCcheck.addValueChangeListener(this::projectHandlingAppliedChanged);
        DCCcheck.addValueChangeListener(this::deepClearingAppliedChanged);
        FPCcheck.addValueChangeListener(this::floorProtectionAppliedChanged);*/
        return verticalLayout;
    }

    public Component buildLayout3()
    {
        VerticalLayout verticalLayout=new VerticalLayout();
        verticalLayout.setSpacing(true);
//        verticalLayout.setMargin(new MarginInfo(false,true,false,true));


        Label heading=new Label("Quantity");
        heading.addStyleName("margin-label-style");
        verticalLayout.addComponent(heading);
        verticalLayout.setComponentAlignment(heading,Alignment.MIDDLE_LEFT);

        PHCQTY =new TextField();
        PHCQTY.addStyleName("heighttext");
        PHCQTY.addStyleName("margin-label-style2");
        PHCQTY.setValue(String.valueOf(round(proposalVersion.getProjectHandlingQty(),2)));
        verticalLayout.addComponent(PHCQTY);
        verticalLayout.setComponentAlignment(PHCQTY,Alignment.MIDDLE_LEFT);

        DCCQTY =new TextField();
        DCCQTY.addStyleName("margin-label-style2");
        DCCQTY.addStyleName("heighttext");
        DCCQTY.setValue(String.valueOf(proposalVersion.getDeepClearingQty()));
        verticalLayout.addComponent(DCCQTY);
        verticalLayout.setComponentAlignment(DCCQTY,Alignment.MIDDLE_LEFT);

        FPCQTY = new TextField();
        FPCQTY.addStyleName("margin-label-style2");
        FPCQTY.addStyleName("heighttext");
        FPCQTY.setValue(String.valueOf(proposalVersion.getFloorProtectionSqft()));
        verticalLayout.addComponent(FPCQTY);

        if(proposalHeader.getProjectHandlingChargesApplied().equals("true"))
        {
            PHCQTY.setReadOnly(true);
        }else {
            PHCQTY.setReadOnly(false);
        }
        verticalLayout.setComponentAlignment(FPCQTY,Alignment.MIDDLE_LEFT);
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
        verticalLayout.setComponentAlignment(heading,Alignment.MIDDLE_LEFT);

        PHCAmount =new Label();
        PHCAmount.addStyleName("heighttext");
        PHCAmount.addStyleName("margin-label-style2");
        //PHCAmount.setValue(String.valueOf(Double.valueOf(PHCQTY.getValue()) * projectHandlingChargesRate));
        PHCAmount.setValue(String.valueOf(round(proposalVersion.getProjectHandlingAmount(),2)));
        verticalLayout.addComponent(PHCAmount);
        verticalLayout.setComponentAlignment(PHCAmount,Alignment.MIDDLE_LEFT);

        DCCAmount =new Label();
        DCCAmount.addStyleName("margin-label-style2");
        DCCAmount.addStyleName("heighttext");
        //DCCAmount.setValue(String.valueOf(Double.valueOf(DCCQTY.getValue()) * deepCleaningChargesRate));
        DCCAmount.setValue(String.valueOf(proposalVersion.getDeepClearingAmount()));
        verticalLayout.addComponent(DCCAmount);
        verticalLayout.setComponentAlignment(DCCAmount,Alignment.MIDDLE_LEFT);

        FPCAmount = new Label();
        FPCAmount.addStyleName("margin-label-style2");
        FPCAmount.addStyleName("heighttext");
        //FPCAmount.setValue(String.valueOf(Double.valueOf(FPCQTY.getValue()) * floorProtectionChargesRate));
        FPCAmount.setValue(String.valueOf(proposalVersion.getFloorProtectionAmount()));
        verticalLayout.addComponent(FPCAmount);
        verticalLayout.setComponentAlignment(FPCAmount,Alignment.MIDDLE_LEFT);

        return verticalLayout;
    }

    private void deepCleaningchargesQuantityChanged(Property.ValueChangeEvent valueChangeEvent)
    {
        if(DCCQTY.getValue()==null || DCCQTY.getValue().length()==0)
        {
            DCCQTY.setValue(String.valueOf(proposalVersion.getDeepClearingQty()));
            DCCAmount.setValue(String.valueOf(proposalVersion.getDeepClearingAmount()));
        }
        else if(Double.valueOf(DCCQTY.getValue())< 0 )
        {
            DCCQTY.setValue(String.valueOf(proposalVersion.getDeepClearingQty()));
            DCCAmount.setValue(String.valueOf(proposalVersion.getDeepClearingAmount()));
            NotificationUtil.showNotification("House Keeping quantity should be a positive number" , NotificationUtil.STYLE_BAR_ERROR_SMALL);
        }else
        {
            DCCAmount.setValue(String.valueOf(Double.valueOf(DCCQTY.getValue()) * deepCleaningChargesRate));
            proposalVersion.setDeepClearingQty(Double.valueOf(DCCQTY.getValue()));
            status="DP";
            updatePrice();
        }

    }
    private void ProjectHandlingChargesQuantityChanged(Property.ValueChangeEvent valueChangeEvent)
    {
        if(PHCQTY.getValue()==null || PHCQTY.getValue().length()==0)
        {
            PHCQTY.setValue(String.valueOf(round(proposalVersion.getProjectHandlingQty(),2)));
            PHCAmount.setValue(String.valueOf(round(proposalVersion.getProjectHandlingAmount(),2)));
        }else if(Double.valueOf(PHCQTY.getValue())<0)
        {
            NotificationUtil.showNotification("Project Handling quantity should be a positive number" , NotificationUtil.STYLE_BAR_ERROR_SMALL);
            PHCQTY.setValue(String.valueOf(round(proposalVersion.getProjectHandlingQty(),2)));
            PHCAmount.setValue(String.valueOf(round(proposalVersion.getProjectHandlingAmount(),2)));
        }
        else
        {
            PHCAmount.setValue(String.valueOf(round((Double.valueOf(PHCQTY.getValue()) * projectHandlingChargesRate),2)));
            proposalVersion.setProjectHandlingQty(Double.valueOf(PHCQTY.getValue()));
            //status="DP";
            updatePrice();
        }

    }
    private void floorProtectionQuantityChanged(Property.ValueChangeEvent valueChangeEvent)
    {
        if(FPCQTY.getValue()==null || FPCQTY.getValue().length()==0)
        {
            FPCQTY.setValue(String.valueOf(proposalVersion.getFloorProtectionSqft()));
            FPCAmount.setValue(String.valueOf(proposalVersion.getFloorProtectionAmount()));
        }else if(Double.valueOf(FPCQTY.getValue())<0)
        {
            FPCQTY.setValue(String.valueOf(proposalVersion.getFloorProtectionSqft()));
            FPCAmount.setValue(String.valueOf(proposalVersion.getFloorProtectionAmount()));
            NotificationUtil.showNotification("Floor Protection quantity should be a positive number" , NotificationUtil.STYLE_BAR_ERROR_SMALL);
        }
        else
        {
            FPCAmount.setValue(String.valueOf(Double.valueOf(FPCQTY.getValue()) * floorProtectionChargesRate));
            proposalVersion.setFloorProtectionSqft(Double.valueOf(FPCQTY.getValue()));
            status="DP";
            updatePrice();
        }

    }
}

