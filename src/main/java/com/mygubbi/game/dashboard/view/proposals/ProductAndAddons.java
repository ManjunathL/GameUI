package com.mygubbi.game.dashboard.view.proposals;

import com.mygubbi.game.dashboard.ServerManager;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.domain.*;
import com.mygubbi.game.dashboard.domain.JsonPojo.LookupItem;
import com.mygubbi.game.dashboard.event.DashboardEvent;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.mygubbi.game.dashboard.event.ProposalEvent;
import com.mygubbi.game.dashboard.view.DashboardViewType;
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
import com.vaadin.event.FieldEvents;
import com.vaadin.event.SelectionEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
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
 * Created by user on 12-Dec-16.
 */
public class ProductAndAddons extends Window
{
    private static final Logger LOG = LogManager.getLogger(ProductAndAddons.class);

    private ProposalDataProvider proposalDataProvider = ServerManager.getInstance().getProposalDataProvider();

    private ProductAndAddonSelection productAndAddonSelection;
    private Button publishButton;
    private Button addKitchenOrWardrobeButton;
    private Button addFromCatalogueButton;
    private FileAttachmentComponent fileAttachmentComponent;
    private TextField discountPercentage;
    private TextField discountAmount;
    private Label discountTotal;
    private Button addonAddButton;
    private BeanItemContainer<AddonProduct> addonsContainer;
    private Grid addonsGrid;
    private MenuBar.MenuItem deleteMenuItem;
    private MenuBar.MenuItem cancelMenuItem;
    private MenuBar.MenuItem reviseMenuItem;

    private Grid productsGrid;
    private Label proposalTitleLabel;
    private final BeanFieldGroup<ProposalHeader> binder = new BeanFieldGroup<>(ProposalHeader.class);
    private Button submitButton;
    private Label draftLabel;
    private ProposalHeader proposalHeader;
    private ModulePrice modulePrice;
    private ProposalVersion proposalVersion;
    private Proposal proposal;
    private Button saveButton;
    private BeanItemContainer productContainer;
    private Label grandTotal;

    private String status=null;
    int pid;
    String Pstatus;
    String parameters;
    private final String NEW_TITLE = "New Proposal";
    private static Set<ProductAndAddons> previousInstances = new HashSet<>();

    public static   void open(ProposalHeader proposalHeader,Proposal proposal, ProductAndAddonSelection productAndAddonSelection)
    {

        DashboardEventBus.post(new DashboardEvent.CloseOpenWindowsEvent());
        ProductAndAddons w = new ProductAndAddons(proposalHeader,proposal,productAndAddonSelection);
        try
        {
            previousInstances.forEach(DashboardEventBus::unregister);
        }
        catch (Exception e) {
            //ignore
        }
        previousInstances.clear();
        previousInstances.add(w);
        UI.getCurrent().addWindow(w);
        w.focus();

    }

    ProductAndAddons(ProposalHeader proposalHeader,Proposal proposal,ProductAndAddonSelection productAndAddonSelection)
    {
        this.proposalHeader=proposalHeader;
        this.proposal=proposal;
        this.productAndAddonSelection=productAndAddonSelection;

        DashboardEventBus.register(this);
        setModal(true);
        setSizeUndefined();
        //setSizeFull();
        setResizable(false);
        setClosable(false);

        /*VerticalLayout vLayout = new VerticalLayout();
        vLayout.setSizeFull();
        vLayout.addStyleName("v-vertical-customized-product-details");
        vLayout.setMargin(new MarginInfo(true, true, true, true));
        //vLayout.addComponent(buildProductsAndAddonsPage());
        setContent(vLayout);
         Responsive.makeResponsive(this);

        HorizontalLayout horizontalLayout0 = new HorizontalLayout();
        horizontalLayout0.setSizeFull();
        //horizontalLayout0.addComponent(buildProductsAndAddonsPage());

        Component componentactionbutton=buildActionButtons();
        vLayout.addComponent(componentactionbutton);
        vLayout.setExpandRatio(componentactionbutton,0.2f);

        Component amountsLayout = getAmountLayout();
        this.discountPercentage.addFocusListener(this::onFocusToDiscountPercentage);
        this.discountAmount.addFocusListener(this::onFocusToDiscountAmount);
        this.discountPercentage.addValueChangeListener(this::onDiscountPercentageValueChange);
        this.discountAmount.addValueChangeListener(this::onDiscountAmountValueChange);
        vLayout.addComponent(amountsLayout);
        vLayout.setExpandRatio(amountsLayout,0.3f);

        Component componentProductDetails=buildProductDetails();
        vLayout.addComponent(componentProductDetails);
        vLayout.setExpandRatio(componentProductDetails,0.3f);

        Component componentAddonDetails = buildAddons();
        vLayout.addComponent(componentAddonDetails);
        vLayout.setExpandRatio(componentAddonDetails,0.3f);

        vLayout.addComponent(horizontalLayout0);
        horizontalLayout0.setHeightUndefined();
        vLayout.setExpandRatio(horizontalLayout0, 0.35f);*/

        LOG.info("Product Layout");
        VerticalLayout verticalLayout = new VerticalLayout();
        //verticalLayout.setSizeFull();
        verticalLayout.setSizeUndefined();

        verticalLayout.setSpacing(true);
        setContent(verticalLayout);

        Component componentactionbutton=buildActionButtons();
        verticalLayout.addComponent(componentactionbutton);
        //verticalLayout.setExpandRatio(componentactionbutton,0.2f);

        Component amountsLayout = getAmountLayout();
        this.discountPercentage.addFocusListener(this::onFocusToDiscountPercentage);
        this.discountAmount.addFocusListener(this::onFocusToDiscountAmount);
        this.discountPercentage.addValueChangeListener(this::onDiscountPercentageValueChange);
        this.discountAmount.addValueChangeListener(this::onDiscountAmountValueChange);
        verticalLayout.addComponent(amountsLayout);
        //verticalLayout.setExpandRatio(amountsLayout,0.3f);


        Component componentProductDetails=buildProductDetails();
        verticalLayout.addComponent(componentProductDetails);
        ///verticalLayout.setExpandRatio(componentProductDetails,0.3f);

        Component componentAddonDetails = buildAddons();
        verticalLayout.addComponent(componentAddonDetails);

        updateTotal();
        //verticalLayout.setExpandRatio(componentAddonDetails,0.3f);

        //return verticalLayout;
    }

    private Component buildProductsAndAddonsPage()
    {
        LOG.info("Product Layout");
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        verticalLayout.setSpacing(true);

        Component componentactionbutton=buildActionButtons();
        verticalLayout.addComponent(componentactionbutton);
        verticalLayout.setExpandRatio(componentactionbutton,0.2f);

        Component amountsLayout = getAmountLayout();

        this.discountPercentage.addFocusListener(this::onFocusToDiscountPercentage);
        this.discountAmount.addFocusListener(this::onFocusToDiscountAmount);

        this.discountPercentage.addValueChangeListener(this::onDiscountPercentageValueChange);
        this.discountAmount.addValueChangeListener(this::onDiscountAmountValueChange);

        verticalLayout.addComponent(amountsLayout);
        verticalLayout.setExpandRatio(amountsLayout,0.3f);


        Component componentProductDetails=buildProductDetails();
        verticalLayout.addComponent(componentProductDetails);
        verticalLayout.setExpandRatio(componentProductDetails,0.3f);

        Component componentAddonDetails = buildAddons();
        verticalLayout.addComponent(componentAddonDetails);
        verticalLayout.setExpandRatio(componentAddonDetails,0.3f);

        return verticalLayout;
    }
    private Component buildActionButtons()
    {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSizeFull();
        //setSizeUndefined();

       //HorizontalLayout left = new HorizontalLayout();
        horizontalLayout.setMargin(new MarginInfo(true,true,true,true));
        HorizontalLayout right = new HorizontalLayout();

        Button soExtractButton = new Button("SO Extract&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
        soExtractButton.setCaptionAsHtml(true);
        soExtractButton.setIcon(FontAwesome.DOWNLOAD);
        soExtractButton.setStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
        soExtractButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        soExtractButton.addStyleName(ValoTheme.BUTTON_SMALL);
        soExtractButton.addStyleName("margin-right-10-for-headerlevelbutton");
        soExtractButton.setWidth("120px");
        soExtractButton.addClickListener(this::doSalesOrderDownloadValidation);

        FileDownloader soExtractDownloader = this.getSOExtractFileDownloader();
        soExtractDownloader.extend(soExtractButton);
        right.addComponent(soExtractButton);
        right.setComponentAlignment(soExtractButton, Alignment.MIDDLE_RIGHT);

        Button quotePdf = new Button("Quote Pdf&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
        quotePdf.setCaptionAsHtml(true);
        quotePdf.setIcon(FontAwesome.DOWNLOAD);
        quotePdf.setStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
        quotePdf.addStyleName(ValoTheme.BUTTON_PRIMARY);
        quotePdf.addStyleName(ValoTheme.BUTTON_SMALL);
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
        downloadButton.addStyleName("margin-right-10-for-headerlevelbutton");
        downloadButton.setWidth("85px");
        downloadButton.addClickListener(this::checkProductsAndAddonsAvailable);

        StreamResource myResource = createQuoteResource();
        FileDownloader fileDownloader = new FileDownloader(myResource);
        fileDownloader.extend(downloadButton);
        right.addComponent(downloadButton);
        right.setComponentAlignment(downloadButton, Alignment.MIDDLE_RIGHT);

        Button jobcardButton = new Button("Job Card&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
        jobcardButton.setCaptionAsHtml(true);
        jobcardButton.setIcon(FontAwesome.DOWNLOAD);
        jobcardButton.setStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
        jobcardButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        jobcardButton.addStyleName(ValoTheme.BUTTON_SMALL);
        jobcardButton.addStyleName("margin-right-10-for-headerlevelbutton");
        jobcardButton.setWidth("100px");
        jobcardButton.addClickListener(this::checkSingleProductSelection);

        StreamResource jobcardResource = createJobcardResource();
        FileDownloader jobcardDownloader = new FileDownloader(jobcardResource);
        jobcardDownloader.extend(jobcardButton);
        right.addComponent(jobcardButton);
        right.setComponentAlignment(jobcardButton, Alignment.MIDDLE_RIGHT);

        submitButton = new Button("Submit");
        submitButton.setVisible(ProposalHeader.ProposalState.draft.name().equals(proposalHeader.getStatus()));
        submitButton.addStyleName(ValoTheme.BUTTON_SMALL);
        submitButton.addStyleName("margin-right-10-for-headerlevelbutton");
        submitButton.addClickListener(this::submit);
        right.addComponent(submitButton);
        right.setComponentAlignment(submitButton, Alignment.MIDDLE_RIGHT);

        publishButton = new Button("Publish");
        publishButton.setVisible(ProposalHeader.ProposalState.active.name().equals(proposalHeader.getStatus()));
        publishButton.addStyleName(ValoTheme.BUTTON_SMALL);
        publishButton.addStyleName("margin-right-10-for-headerlevelbutton");
        publishButton.addClickListener(this::publish);
        right.addComponent(publishButton);
        right.setComponentAlignment(publishButton, Alignment.MIDDLE_RIGHT);

        saveButton = new Button("Save");
        saveButton.addStyleName(ValoTheme.BUTTON_SMALL);
        saveButton.addStyleName("margin-right-10-for-headerlevelbutton");
        saveButton.addClickListener(this::save);
        right.addComponent(saveButton);
        right.setComponentAlignment(saveButton, Alignment.MIDDLE_RIGHT);

        MenuBar menu = new MenuBar();
        menu.setStyleName(ValoTheme.MENUBAR_SMALL);
        menu.addStyleName("margin-right-10-for-headerlevelbutton");

        MenuBar.MenuItem moreMenuItem = menu.addItem("more", null);
        reviseMenuItem = moreMenuItem.addItem("Revise", this::revise);
        reviseMenuItem.setVisible(ProposalHeader.ProposalState.active.name().equals(proposalHeader.getStatus()));

        cancelMenuItem = moreMenuItem.addItem("Cancel", this::cancel);
        String role = ((User) VaadinSession.getCurrent().getAttribute(User.class.getName())).getRole();

        if (role.equals("admin"))
        {
            deleteMenuItem = moreMenuItem.addItem("Delete", this::deleteProposal);
        }
        moreMenuItem.addItem("Close", this::close);

        right.addComponent(menu);
        right.setComponentAlignment(menu, Alignment.MIDDLE_RIGHT);

        horizontalLayout.addComponent(right);
       //horizontalLayout.setExpandRatio(right, 7);
        horizontalLayout.setComponentAlignment(right, Alignment.MIDDLE_RIGHT);

        return horizontalLayout;

    }

    private Component getAmountLayout() {
        HorizontalLayout amountsLayout = new HorizontalLayout();
        amountsLayout.setMargin(new MarginInfo(true,true,true,true));

        Label totalWithoutDiscount = new Label("<b>Total Without Discount: </b>", ContentMode.HTML);
        amountsLayout.addComponent(totalWithoutDiscount);
        amountsLayout.setSpacing(true);
        totalWithoutDiscount.addStyleName("amount-text-label");
        totalWithoutDiscount.addStyleName("v-label-amount-text-label");
        totalWithoutDiscount.addStyleName("margin-top-18");

        this.grandTotal=new Label("</b>",ContentMode.HTML);
        this.grandTotal.addStyleName("amount-text");
        this.grandTotal.addStyleName("margin-top-18");
        this.grandTotal.addStyleName("v-label-amount-text-label");
        this.grandTotal.setCaptionAsHtml(true);
        this.grandTotal.setConverter(getAmountConverter());
        this.grandTotal.setReadOnly(true);
        amountsLayout.addComponent(grandTotal);
        amountsLayout.setSpacing(true);

        Label Discount = new Label("<b>Discount % :</b>",ContentMode.HTML);
        amountsLayout.addComponent(Discount);
        amountsLayout.setSpacing(true);
        Discount.addStyleName("inputlabel");
        Discount.addStyleName("margin-top-18");

        this.discountPercentage = new TextField();
        this.discountPercentage.setConverter(new StringToDoubleConverter());
        this.discountPercentage.addStyleName("inputTextbox");
        this.discountPercentage.addStyleName("margin-top-18");
        this.discountPercentage.addStyleName("v-label-amount-text-label");
        this.discountPercentage.setCaptionAsHtml(true);
        this.discountPercentage.setValue("0");
        this.discountPercentage.setNullRepresentation("0");
        amountsLayout.addComponent(discountPercentage);

        Label DiscountAmount = new Label("<b>Discount Amount :</b>",ContentMode.HTML);
        amountsLayout.addComponent(DiscountAmount);
        amountsLayout.setSpacing(true);
        DiscountAmount.addStyleName("inputlabel");
        DiscountAmount.addStyleName("margin-top-18");

        this.discountAmount=new TextField();
        this.discountAmount.setConverter(new StringToDoubleConverter());
        this.discountAmount.addStyleName("inputTextbox");
        this.discountAmount.addStyleName("margin-top-18");
        this.discountAmount.addStyleName("v-label-amount-text-label");
        this.discountAmount.setCaptionAsHtml(true);
        this.discountAmount.setValue("0");
        this.discountAmount.setNullRepresentation("0");
        amountsLayout.addComponent(discountAmount);

        Label totalAfterDiscount = new Label("<b>Total After Discount :</b>",ContentMode.HTML);
        amountsLayout.addComponent(totalAfterDiscount);
        amountsLayout.setSpacing(true);
        totalAfterDiscount.addStyleName("amount-text-label");
        totalAfterDiscount.addStyleName("margin-top-18");

        this.discountTotal=new Label("</b>",ContentMode.HTML);
        this.discountTotal.addStyleName("amount-text");
        this.discountTotal.addStyleName("margin-top-18");
        this.discountTotal.addStyleName("v-label-amount-text-label");
        this.discountTotal.setCaptionAsHtml(true);
        this.discountTotal.setConverter(getAmountConverter());
        this.discountTotal.setReadOnly(true);
        amountsLayout.addComponent(discountTotal);
        amountsLayout.setSpacing(true);

        return amountsLayout;
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
        LOG.info("update total");
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
        List<Product> products = proposal.getProducts();
        LOG.info("Product :" + products.toString());
        for (Product product : products) {
            totalWoAccessories += product.getCostWoAccessories();
        }

        Double totalAmount = addonsTotal + productsTotal;
        Double costOfAccessories = productsTotal - totalWoAccessories;

        Double discountPercent=0.0,discountAmount=0.0;

        if("DP".equals(status))
        {
            discountPercent = (Double) this.discountPercentage.getConvertedValue();
            if(discountPercent<=30) {
                if (discountPercent == null) {
                    discountPercent = 0.0;
                }
                discountAmount = totalWoAccessories * discountPercent / 100.0;
                this.discountAmount.setValue(String.valueOf(round(discountAmount, 2)));
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
        LOG.info(totalAfterDiscount+costOfAccessories+addonsTotal);

        Double grandTotal = totalAfterDiscount + costOfAccessories + addonsTotal;
        LOG.info("totaL" +grandTotal);
        Double rem=grandTotal%10;

        if(rem<5)
        {
            grandTotal=grandTotal-rem;
        }
        else
        {
            grandTotal=grandTotal+(10-rem);
        }

        this.discountTotal.setReadOnly(false);
        this.discountTotal.setValue(grandTotal.intValue() + "");
        this.discountTotal.setReadOnly(true);

        this.grandTotal.setReadOnly(false);
        this.grandTotal.setValue(totalAmount.intValue() + "");
        this.grandTotal.setReadOnly(true);

        productAndAddonSelection.setDiscountPercentage(discountPercent);
        productAndAddonSelection.setDiscountAmount(discountAmount);
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
                    newProduct.setSeq(this.proposal.getProducts().size() + 1);
                    newProduct.setProposalId(this.proposalHeader.getId());
                    CustomizedProductDetailsWindow.open(ProductAndAddons.this.proposal, newProduct);
                }
        );

        addFromCatalogueButton.addClickListener(
                clickEvent -> {
                    CatalogueProduct newProduct = new CatalogueProduct();
                    newProduct.setType(CatalogueProduct.TYPES.CATALOGUE.name());
                    newProduct.setSeq(this.proposal.getProducts().size() + 1);
                    newProduct.setProposalId(this.proposalHeader.getId());
                    CatalogItemDetailsWindow.open(ProductAndAddons.this.proposal, newProduct);
                }
        );

        productContainer = new BeanItemContainer<>(Product.class);
        GeneratedPropertyContainer genContainer = createGeneratedProductPropertyContainer();

        productsGrid = new Grid(genContainer);
        productsGrid.setSelectionMode(Grid.SelectionMode.MULTI);
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
        columns.get(idx++).setHeaderCaption("Actions").setRenderer(new EditDeleteButtonValueRenderer(new EditDeleteButtonValueRenderer.EditDeleteButtonClickListener() {
            @Override
            public void onEdit(ClickableRenderer.RendererClickEvent rendererClickEvent) {

                Product product = (Product) rendererClickEvent.getItemId();

                if (product.getType().equals(Product.TYPES.CUSTOMIZED.name())) {
                    if (product.getModules().isEmpty()) {
                        Product productDetails = proposalDataProvider.getProposalProductDetails(product.getId());
                        product.setModules(productDetails.getModules());
                        product.setAddons(productDetails.getAddons());
                    }

                    if (product.getFileAttachmentList().isEmpty()) {
                        List<FileAttachment> productAttachments = proposalDataProvider.getProposalProductDocuments(product.getId());
                        product.setFileAttachmentList(productAttachments);
                    }
                    CustomizedProductDetailsWindow.open(proposal, product);
                } else {
                    CatalogueProduct catalogueProduct = new CatalogueProduct();
                    catalogueProduct.populateFromProduct(product);
                    CatalogItemDetailsWindow.open(proposal, catalogueProduct);
                }

            }

            @Override
            public void onDelete(ClickableRenderer.RendererClickEvent rendererClickEvent) {

                if (isProposalReadonly()) {
                    NotificationUtil.showNotification("This operation is allowed only in 'draft' state.", NotificationUtil.STYLE_BAR_WARNING_SMALL);
                } else {

                    ConfirmDialog.show(UI.getCurrent(), "", "Are you sure you want to Delete this Product?",
                            "Yes", "No", dialog -> {
                                if (dialog.isConfirmed()) {
                                    Product product = (Product) rendererClickEvent.getItemId();

                                    proposal.getProducts().remove(product);

                                    int seq = product.getSeq();
                                    productContainer.removeAllItems();

                                    for (Product product1 : proposal.getProducts()) {
                                        if (product1.getSeq() > seq) {
                                            product1.setSeq(product1.getSeq() - 1);
                                            proposalDataProvider.updateProduct(product1);
                                        }
                                    }
                                    productContainer.addAll(proposal.getProducts());
                                    productsGrid.setContainerDataSource(createGeneratedProductPropertyContainer());
                                    productsGrid.getSelectionModel().reset();
                                    updateTotal();
                                    proposalDataProvider.deleteProduct(product.getId());
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

        verticalLayout.addComponent(label);
        return verticalLayout;
    }

    private Component buildAddons() {

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        verticalLayout.setMargin(new MarginInfo(true,true,true,true));

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSizeFull();
        //horizontalLayout.setMargin(new MarginInfo(true, true, true, true));

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
        addonsGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        addonsGrid.addSelectionListener(this::updateTotal);
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
                    NotificationUtil.showNotification("This operation is allowed only in 'draft' state.", NotificationUtil.STYLE_BAR_WARNING_SMALL);
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
        //verticalLayout.setExpandRatio(addonsGrid, 1);

        Label label = new Label("Select Addons and click Download Quote/Job Card button to generate output for only the selected Addons.");
        label.setStyleName("font-italics");

        verticalLayout.addComponent(label);

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
            NotificationUtil.showNotification("No Addons found.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
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
    private void submit(Button.ClickEvent clickEvent) {
        try {
            binder.commit();
            proposalHeader.setStatus(ProposalHeader.ProposalState.active.name());
            boolean success = proposalDataProvider.saveProposal(proposalHeader);
            if (success) {
                boolean mapped = true;
                for (Product product : proposal.getProducts()) {
                    Product populatedProduct = proposalDataProvider.getProposalProductDetails(product.getId());
                    mapped = populatedProduct.getType().equals(Product.TYPES.CATALOGUE.name()) || (!populatedProduct.getModules().isEmpty());
                    if (!mapped) {
                        break;
                    }
                }

                if (!mapped) {
                    NotificationUtil.showNotification("Couldn't Submit. Please ensure all Products have mapped Modules.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                } else {

                    success = proposalDataProvider.submitProposal(proposalHeader.getId());
                    if (success) {
                        reviseMenuItem.setVisible(true);
                        publishButton.setVisible(true);
                        submitButton.setVisible(false);
                        deleteMenuItem.setVisible(false);
                        saveButton.setVisible(false);
                        draftLabel.setValue("[ " + ProposalHeader.ProposalState.active.name() + " ]");
                        NotificationUtil.showNotification("Submitted successfully!", NotificationUtil.STYLE_BAR_SUCCESS_SMALL);
                        handleState();
                    } else {
                        NotificationUtil.showNotification("Couldn't Activate Proposal! Please contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                    }
                }
            } else {
                NotificationUtil.showNotification("Couldn't Save Proposal! Please contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            }
        } catch (FieldGroup.CommitException e) {
            NotificationUtil.showNotification("Validation Error, please fill all mandatory fields!", NotificationUtil.STYLE_BAR_ERROR_SMALL);
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
    private void publish(Button.ClickEvent clickEvent) {
        ConfirmDialog.show(UI.getCurrent(), "", "Do you want to publish this Proposal?",
                "Yes", "No", dialog -> {
                    if (!dialog.isCanceled()) {
                        try {
                            binder.commit();
                            proposalHeader.setStatus(ProposalHeader.ProposalState.published.name());
                            boolean success = proposalDataProvider.saveProposal(proposalHeader);
                            if (success) {
                                success = proposalDataProvider.publishProposal(proposalHeader.getId());
                                if (success) {
                                    reviseMenuItem.setVisible(false);
                                    submitButton.setVisible(false);
                                    publishButton.setVisible(false);
                                    deleteMenuItem.setVisible(false);
                                    saveButton.setVisible(false);
                                    draftLabel.setValue("[ " + ProposalHeader.ProposalState.published.name() + " ]");
                                    handleState();
                                } else {
                                    NotificationUtil.showNotification("Couldn't publish Proposal! Please contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                                }
                            } else {
                                NotificationUtil.showNotification("Couldn't Save Proposal! Please contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                            }
                        } catch (FieldGroup.CommitException e) {
                            NotificationUtil.showNotification("Validation Error, please fill all mandatory fields!", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                        }
                    }
                });
    }

    private void revise(MenuBar.MenuItem selectedItem) {
        try {
            binder.commit();
            proposalHeader.setStatus(ProposalHeader.ProposalState.draft.name());
            boolean success = proposalDataProvider.saveProposal(proposalHeader);
            if (success) {
                success = proposalDataProvider.reviseProposal(proposalHeader.getId());
                if (success) {
                    reviseMenuItem.setVisible(false);
                    publishButton.setVisible(false);
                    submitButton.setVisible(true);
                    saveButton.setVisible(true);
                    deleteMenuItem.setVisible(true);
                    draftLabel.setValue("[ " + ProposalHeader.ProposalState.draft.name() + " ]");
                    handleState();
                } else {
                    NotificationUtil.showNotification("Couldn't revise Proposal! Please contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                }
            } else {
                NotificationUtil.showNotification("Couldn't Save Proposal! Please contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            }
        } catch (FieldGroup.CommitException e) {
            NotificationUtil.showNotification("Validation Error, please fill all mandatory fields!", NotificationUtil.STYLE_BAR_ERROR_SMALL);
        }
    }

    private void save(Button.ClickEvent clickEvent)
    {
        LOG.info("save button clicked");
        /*if (StringUtils.isEmpty(parameters))
        {
            ProposalHeader proposalHeader1 = proposalDataProvider.getProposalHeader(pid);
            LOG.info("parameter value" +parameters);
            proposalHeader.setId(pid);
        }

        if (StringUtils.isEmpty(proposalHeader.getTitle()))
        {
            LOG.info("title" +proposalHeader.getTitle());
            proposalHeader.setTitle(NEW_TITLE);
        }*/

        LOG.info("Cname" +proposalHeader.getCname());
        try
        {
            binder.commit();
        }
        catch (FieldGroup.CommitException e)
        {
            NotificationUtil.showNotification("Validation Error, please fill all mandatory fields!", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            return;
        }

        boolean success = proposalDataProvider.saveProposal(proposalHeader);
        LOG.info("Success Value" +success);
        if (success) {
            NotificationUtil.showNotification("Saved successfully!", NotificationUtil.STYLE_BAR_SUCCESS_SMALL);
        } else {
            NotificationUtil.showNotification("Couldn't Save Proposal! Please contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
        }

    }

    private boolean isProposalReadonly() {
        return !proposal.getProposalHeader().getStatus().equals(ProposalHeader.ProposalState.draft.name());
    }

    private void cancel(MenuBar.MenuItem selectedItem) {
        ConfirmDialog.show(UI.getCurrent(), "", "Do you want to cancel this Proposal?",
                "Yes", "No", dialog -> {
                    if (!dialog.isCanceled()) {
                        try {
                            binder.commit();
                            proposalHeader.setStatus(ProposalHeader.ProposalState.cancelled.name());
                            boolean success = proposalDataProvider.saveProposal(proposalHeader);
                            if (success) {
                                success = proposalDataProvider.cancelProposal(proposalHeader.getId());
                                if (success) {
                                    reviseMenuItem.setVisible(false);
                                    submitButton.setVisible(false);
                                    publishButton.setVisible(false);
                                    deleteMenuItem.setVisible(false);
                                    saveButton.setVisible(false);
                                    cancelMenuItem.setVisible(false);
                                    draftLabel.setValue("[ " + ProposalHeader.ProposalState.cancelled.name() + " ]");
                                    handleState();
                                } else {
                                    NotificationUtil.showNotification("Couldn't cancel Proposal! Please contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                                }
                            } else {
                                NotificationUtil.showNotification("Couldn't Save Proposal! Please contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                            }
                        } catch (FieldGroup.CommitException e) {
                            NotificationUtil.showNotification("Validation Error, please fill all mandatory fields!", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                        }
                    }
                });
    }

    private void deleteProposal(MenuBar.MenuItem selectedItem) {

        ConfirmDialog.show(UI.getCurrent(), "", "Do you want to delete this Proposal?",
                "Yes", "No", dialog -> {
                    if (!dialog.isCanceled()) {
                        proposalDataProvider.deleteProposal(proposalHeader.getId());
                        DashboardEventBus.post(new ProposalEvent.ProposalUpdated());
                    }
                    DashboardEventBus.unregister(this);
                    UI.getCurrent().getNavigator()
                            .navigateTo(DashboardViewType.PROPOSALS.name());
                });

    }

    private void close(MenuBar.MenuItem selectedItem) {

        ConfirmDialog.show(UI.getCurrent(), "", "Do you want to close this Proposal? Unsaved data will be lost.",
                "Yes", "No", dialog -> {
                    if (!dialog.isCanceled()) {
                        DashboardEventBus.unregister(this);
                        UI.getCurrent().getNavigator()
                                .navigateTo(DashboardViewType.PROPOSALS.name());
                    }
                });
    }

    private void handleState() {
        if (proposalHeader.isReadonly()) {
            setComponentsReadonly();
        } else {
            ProposalHeader.ProposalState proposalState = ProposalHeader.ProposalState.valueOf(proposalHeader.getStatus());
            switch (proposalState) {
                case draft:
                    addKitchenOrWardrobeButton.setEnabled(true);
                    addFromCatalogueButton.setEnabled(true);
                    fileAttachmentComponent.getFileUploadCtrl().setEnabled(true);
                    setHeaderFieldsReadOnly(false);
                    fileAttachmentComponent.setReadOnly(false);
                    addonAddButton.setEnabled(true);
                    break;
                case active:
                case cancelled:
                case published:
                    setComponentsReadonly();
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
        fileAttachmentComponent.getFileUploadCtrl().setEnabled(false);
        setHeaderFieldsReadOnly(true);
        fileAttachmentComponent.setReadOnly(true);
        addonAddButton.setEnabled(false);
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

    /*
    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

    }*/
}
