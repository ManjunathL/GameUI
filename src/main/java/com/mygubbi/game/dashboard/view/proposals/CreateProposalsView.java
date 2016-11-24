package com.mygubbi.game.dashboard.view.proposals;


import com.google.common.eventbus.Subscribe;
import com.mygubbi.game.dashboard.ServerManager;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.domain.*;
import com.mygubbi.game.dashboard.domain.JsonPojo.LookupItem;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.mygubbi.game.dashboard.event.ProposalEvent;
import com.mygubbi.game.dashboard.view.DashboardViewType;
import com.mygubbi.game.dashboard.view.FileAttachmentComponent;
import com.mygubbi.game.dashboard.view.NotificationUtil;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.*;
import com.vaadin.data.util.converter.StringToDoubleConverter;
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
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.mygubbi.game.dashboard.domain.Product.TYPE;
import static com.mygubbi.game.dashboard.domain.ProposalHeader.*;


/**
 * Created by test on 31-03-2016.
 */
public class CreateProposalsView extends Panel implements View {

    private static final Logger LOG = LogManager.getLogger(CreateProposalsView.class);

    private final String NEW_TITLE = "New Proposal";
    private final String NEW_VERSION = "1.0";
    private ProposalDataProvider proposalDataProvider = ServerManager.getInstance().getProposalDataProvider();

    private Field<?> proposalTitleField;
    private Field<?> crmId;
    private Field<?> proposalVersionField;
    private Field<?> quotationField;

    private Field<?> customerIdField;
    private Field<?> customerNameField;
    private Field<?> customerAddressLine1;
    private Field<?> customerAddressLine2;
    private Field<?> customerAddressLine3;
    private Field<?> customerCityField;
    private Field<?> customerEmailField;
    private Field<?> customerNumberField1;
    private Field<?> customerNumberField2;

    private Field<?> projectName;
    private Field<?> projectAddressLine1;
    private Field<?> projectAddressLine2;
    private ComboBox projectCityField;

    private ComboBox salesPerson;
    private Field<?> salesEmail;
    private Field<?> salesContact;
    private ComboBox designPerson;
    private Field<?> designEmail;
    private Field<?> designContact;

    private Grid productsGrid;
    private Label proposalTitleLabel;
    private final BeanFieldGroup<ProposalHeader> binder = new BeanFieldGroup<>(ProposalHeader.class);
    private Button submitButton;
    private Label draftLabel;
    private ProposalHeader proposalHeader;
    private ModulePrice modulePrice;
    private Proposal proposal;
    private Button saveButton;
    private BeanItemContainer productContainer;
    private Label grandTotal;

    private ProductAndAddonSelection productAndAddonSelection;
    private Button publishButton;
    private Button addKitchenOrWardrobeButton;
    private Button addFromCatalogueButton;
    private FileAttachmentComponent fileAttachmentComponent;
    private TextField discountPercentage;
    private Label discountTotal;
    private Button addonAddButton;
    private BeanItemContainer<AddonProduct> addonsContainer;
    private Grid addonsGrid;
    private MenuBar.MenuItem deleteMenuItem;
    private MenuBar.MenuItem cancelMenuItem;
    private MenuBar.MenuItem reviseMenuItem;

    public CreateProposalsView() {
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        String parameters = event.getParameters();
        if (StringUtils.isNotEmpty(parameters)) {
            int proposalId = Integer.parseInt(parameters);
            this.proposalHeader = proposalDataProvider.getProposalHeader(proposalId);
            this.proposal = new Proposal();
            this.proposal.setProposalHeader(this.proposalHeader);
            this.proposal.setProducts(proposalDataProvider.getProposalProducts(proposalId));
            this.proposal.setFileAttachments(proposalDataProvider.getProposalDocuments(proposalId));
            this.proposal.setAddons(proposalDataProvider.getProposalAddons(proposalId));
            proposalHeader.setVersion(NEW_VERSION);
            proposalHeader.setEditFlag(EDIT.W.name()); //todo: this has to be removed once server side is fixed
        } else {
            proposalHeader = proposalDataProvider.createProposal();
            proposalHeader.setTitle(NEW_TITLE);
            proposalHeader.setVersion(NEW_VERSION);
            proposalHeader.setEditFlag(EDIT.W.name());
            proposalHeader.setStatus(ProposalState.draft.name());
            this.proposal = new Proposal();
            this.proposal.setProposalHeader(proposalHeader);
        }

        this.productAndAddonSelection = new ProductAndAddonSelection();
        this.productAndAddonSelection.setProposalId(this.proposalHeader.getId());

        DashboardEventBus.register(this);
        this.binder.setItemDataSource(proposalHeader);

        setWidth("100%");
        setHeight("100%");

        VerticalLayout vLayout = new VerticalLayout();
        vLayout.addComponent(buildHeader());

        TabSheet tabs = new TabSheet();
        tabs.addTab(buildForm(), "Header");
        tabs.addTab(buildProductsAndAddonsPage(), "Products and Addons");
        fileAttachmentComponent = new FileAttachmentComponent(proposal, proposalHeader.getFolderPath(),
                attachmentData -> proposalDataProvider.addProposalDoc(proposalHeader.getId(), attachmentData.getFileAttachment()),
                attachmentData -> proposalDataProvider.removeProposalDoc(attachmentData.getFileAttachment().getId()),
                !proposalHeader.getStatus().equals(ProposalState.draft.name())
        );
        tabs.addTab(fileAttachmentComponent, "Attachments");

        vLayout.addComponent(tabs);
        setContent(vLayout);
        Responsive.makeResponsive(tabs);
        updateTotal();
        handleState();
    }

    private Component buildAddons() {

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        verticalLayout.setMargin(new MarginInfo(false, true, true, true));

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
        verticalLayout.setExpandRatio(addonsGrid, 1);

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

    private boolean isProposalReadonly() {
        return !proposal.getProposalHeader().getStatus().equals(ProposalHeader.ProposalState.draft.name());
    }

    private GeneratedPropertyContainer createGeneratedAddonsPropertyContainer() {
        GeneratedPropertyContainer genContainer = new GeneratedPropertyContainer(addonsContainer);
        genContainer.addGeneratedProperty("actions", getEmptyActionTextGenerator());
        return genContainer;
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

    private void handleState() {
        if (proposalHeader.isReadonly()) {
            setComponentsReadonly();
        } else {
            ProposalState proposalState = ProposalState.valueOf(proposalHeader.getStatus());
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

    private void setComponentsReadonly() {
        addKitchenOrWardrobeButton.setEnabled(false);
        addFromCatalogueButton.setEnabled(false);
        fileAttachmentComponent.getFileUploadCtrl().setEnabled(false);
        setHeaderFieldsReadOnly(true);
        fileAttachmentComponent.setReadOnly(true);
        addonAddButton.setEnabled(false);
    }

    private void setHeaderFieldsReadOnly(boolean readOnly) {
        proposalTitleField.setReadOnly(readOnly);
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
        designPerson.setReadOnly(readOnly);
    }

    private Component buildHeader() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSizeFull();

        HorizontalLayout left = new HorizontalLayout();
        horizontalLayout.setMargin(new MarginInfo(false,false,false,true));
        String title = proposalHeader.getTitle();
        proposalTitleLabel = new Label(getFormattedTitle(title) + "&nbsp;", ContentMode.HTML);
        proposalTitleLabel.addStyleName(ValoTheme.LABEL_H2);
        proposalTitleLabel.setWidth("1%");
        proposalTitleLabel.setDescription(title);
        left.addComponent(proposalTitleLabel);

        draftLabel = new Label("[ " + proposalHeader.getStatus() + " ]");
        draftLabel.addStyleName(ValoTheme.LABEL_COLORED);
        draftLabel.addStyleName(ValoTheme.LABEL_H2);
        draftLabel.setWidth("1%");
        left.addComponent(draftLabel);

        horizontalLayout.addComponent(left);
        horizontalLayout.setComponentAlignment(left, Alignment.MIDDLE_LEFT);
        horizontalLayout.setExpandRatio(left, 3);

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
        submitButton.setVisible(ProposalState.draft.name().equals(proposalHeader.getStatus()));
        submitButton.addStyleName(ValoTheme.BUTTON_SMALL);
        submitButton.addStyleName("margin-right-10-for-headerlevelbutton");
        submitButton.addClickListener(this::submit);
        right.addComponent(submitButton);
        right.setComponentAlignment(submitButton, Alignment.MIDDLE_RIGHT);

        publishButton = new Button("Publish");
        publishButton.setVisible(ProposalState.active.name().equals(proposalHeader.getStatus()));
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
        reviseMenuItem.setVisible(ProposalState.active.name().equals(proposalHeader.getStatus()));

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
        horizontalLayout.setExpandRatio(right, 7);
        horizontalLayout.setComponentAlignment(right, Alignment.MIDDLE_RIGHT);

        return horizontalLayout;
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

    private void checkSingleProductSelection(Button.ClickEvent clickEvent) {
        if (this.productAndAddonSelection.getProductIds().size() != 1) {
            NotificationUtil.showNotification("Please select a single product to download its Job Card.", NotificationUtil.STYLE_BAR_WARNING_SMALL);
        }
    }

    private void doSalesOrderDownloadValidation(Button.ClickEvent clickEvent) {
        if (this.productAndAddonSelection.getProductIds().size() != 1) {
            NotificationUtil.showNotification("Please select a single product to download SO extract.", NotificationUtil.STYLE_BAR_WARNING_SMALL);
        }
    }

    private void checkProductsAndAddonsAvailable(Button.ClickEvent clickEvent) {
        if (proposal.getProducts().isEmpty() || proposal.getAddons().isEmpty()) {
            NotificationUtil.showNotification("No products found. Please add product(s) first to generate the Quote.", NotificationUtil.STYLE_BAR_WARNING_SMALL);
        }
    }

    private void cancel(MenuBar.MenuItem selectedItem) {
        ConfirmDialog.show(UI.getCurrent(), "", "Do you want to cancel this Proposal?",
                "Yes", "No", dialog -> {
                    if (!dialog.isCanceled()) {
                        try {
                            binder.commit();
                            proposalHeader.setStatus(ProposalState.cancelled.name());
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
                                    draftLabel.setValue("[ " + ProposalState.cancelled.name() + " ]");
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

    private void publish(Button.ClickEvent clickEvent) {
        ConfirmDialog.show(UI.getCurrent(), "", "Do you want to publish this Proposal?",
                "Yes", "No", dialog -> {
                    if (!dialog.isCanceled()) {
                        try {
                            binder.commit();
                            proposalHeader.setStatus(ProposalState.published.name());
                            boolean success = proposalDataProvider.saveProposal(proposalHeader);
                            if (success) {
                                success = proposalDataProvider.publishProposal(proposalHeader.getId());
                                if (success) {
                                    reviseMenuItem.setVisible(false);
                                    submitButton.setVisible(false);
                                    publishButton.setVisible(false);
                                    deleteMenuItem.setVisible(false);
                                    saveButton.setVisible(false);
                                    draftLabel.setValue("[ " + ProposalState.published.name() + " ]");
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
            proposalHeader.setStatus(ProposalState.draft.name());
            boolean success = proposalDataProvider.saveProposal(proposalHeader);
            if (success) {
                success = proposalDataProvider.reviseProposal(proposalHeader.getId());
                if (success) {
                    reviseMenuItem.setVisible(false);
                    publishButton.setVisible(false);
                    submitButton.setVisible(true);
                    saveButton.setVisible(true);
                    deleteMenuItem.setVisible(true);
                    draftLabel.setValue("[ " + ProposalState.draft.name() + " ]");
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

    private String getFormattedTitle(String title) {
        if (title.length() <= 30) {
            return title;
        } else {
            return title.substring(0, 30) + "...";
        }

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

    private void save(Button.ClickEvent clickEvent) {
        if (StringUtils.isEmpty(proposalHeader.getTitle())) {
            proposalHeader.setTitle(NEW_TITLE);
        }
        try {
            binder.commit();
        } catch (FieldGroup.CommitException e) {
            NotificationUtil.showNotification("Validation Error, please fill all mandatory fields!", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            return;
        }

        boolean success = proposalDataProvider.saveProposal(proposalHeader);

        if (success) {
            NotificationUtil.showNotification("Saved successfully!", NotificationUtil.STYLE_BAR_SUCCESS_SMALL);
        } else {
            NotificationUtil.showNotification("Couldn't Save Proposal! Please contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
        }

    }

    private void submit(Button.ClickEvent clickEvent) {
        try {
            binder.commit();
            proposalHeader.setStatus(ProposalState.active.name());
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
                        draftLabel.setValue("[ " + ProposalState.active.name() + " ]");
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


    private Component buildForm() {

        VerticalLayout verticalLayout = new VerticalLayout();

        HorizontalLayout horizontalLayout0 = new HorizontalLayout();
        horizontalLayout0.setSizeFull();
        verticalLayout.addComponent(horizontalLayout0);

        HorizontalLayout horizontalLayout1 = new HorizontalLayout();
        horizontalLayout1.setSizeFull();
        horizontalLayout1.addComponent(buildMainFormLayoutLeft());
        horizontalLayout1.addComponent(buildMainFormLayoutRight());
        verticalLayout.addComponent(horizontalLayout1);

        HorizontalLayout horizontalLayout2 = new HorizontalLayout();
        horizontalLayout2.setSizeFull();
        horizontalLayout2.addComponent(buildDetailsLeft());
        horizontalLayout2.addComponent(buildDetailsRight());
        verticalLayout.addComponent(horizontalLayout2);

        HorizontalLayout horizontalLayout3 = new HorizontalLayout();
        horizontalLayout3.setSizeFull();
        horizontalLayout3.addComponent(buildContactDetailsLeft());
        horizontalLayout3.addComponent(buildContactDetailsRight());
        verticalLayout.addComponent(horizontalLayout3);

        return verticalLayout;
    }

    private Component buildDetailsLeft() {

        FormLayout formLayoutLeft = new FormLayout();
        formLayoutLeft.setSizeFull();
        formLayoutLeft.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        Label customerDetailsLabel = new Label("Customer Details");
        customerDetailsLabel.addStyleName(ValoTheme.LABEL_HUGE);
        customerDetailsLabel.addStyleName(ValoTheme.LABEL_COLORED);
        formLayoutLeft.addComponent(customerDetailsLabel);
        formLayoutLeft.setComponentAlignment(customerDetailsLabel, Alignment.MIDDLE_LEFT);

        customerIdField = binder.buildAndBind("Customer ID", CUSTOMER_ID);
        customerIdField.setRequired(true);
        ((TextField) customerIdField).setNullRepresentation("");
        formLayoutLeft.addComponent(customerIdField);
        customerNameField = binder.buildAndBind("Customer Name", C_NAME);
        customerNameField.setRequired(true);
        ((TextField) customerNameField).setNullRepresentation("");
        formLayoutLeft.addComponent(customerNameField);
        customerAddressLine1 = binder.buildAndBind("Address Line 1", C_ADDRESS1);
        ((TextField) customerAddressLine1).setNullRepresentation("");
        formLayoutLeft.addComponent(customerAddressLine1);
        customerAddressLine2 = binder.buildAndBind("Address Line 2", C_ADDRESS2);
        ((TextField) customerAddressLine2).setNullRepresentation("");
        formLayoutLeft.addComponent(customerAddressLine2);
        customerAddressLine3 = binder.buildAndBind("Address Line 3", C_ADDRESS3);
        ((TextField) customerAddressLine3).setNullRepresentation("");
        formLayoutLeft.addComponent(customerAddressLine3);
        customerCityField = binder.buildAndBind("City", C_CITY);
        ((TextField) customerCityField).setNullRepresentation("");
        formLayoutLeft.addComponent(customerCityField);
        customerEmailField = binder.buildAndBind("Email", C_EMAIL);
        ((TextField) customerEmailField).setNullRepresentation("");
        formLayoutLeft.addComponent(customerEmailField);
        customerNumberField1 = binder.buildAndBind("Phone 1", C_PHONE1);
        ((TextField) customerNumberField1).setNullRepresentation("");
        formLayoutLeft.addComponent(customerNumberField1);
        customerNumberField2 = binder.buildAndBind("Phone 2", C_PHONE2);
        ((TextField) customerNumberField2).setNullRepresentation("");
        formLayoutLeft.addComponent(customerNumberField2);

        return formLayoutLeft;
    }

    private Component buildDetailsRight() {
        FormLayout formLayoutRight = new FormLayout();
        formLayoutRight.setSizeFull();
        formLayoutRight.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        Label projectDetailsLabel = new Label("Project Details");
        projectDetailsLabel.addStyleName(ValoTheme.LABEL_COLORED);
        projectDetailsLabel.addStyleName(ValoTheme.LABEL_HUGE);
        formLayoutRight.addComponent(projectDetailsLabel);
        formLayoutRight.setComponentAlignment(projectDetailsLabel, Alignment.MIDDLE_LEFT);

        projectName = binder.buildAndBind("Project Name", PROJECT_NAME);
        ((TextField) projectName).setNullRepresentation("");
        formLayoutRight.addComponent(projectName);
        projectAddressLine1 = binder.buildAndBind("Address Line 1", P_ADDRESS1);
        ((TextField) projectAddressLine1).setNullRepresentation("");
        formLayoutRight.addComponent(projectAddressLine1);
        projectAddressLine2 = binder.buildAndBind("Address Line 2", P_ADDRESS2);
        ((TextField) projectAddressLine2).setNullRepresentation("");
        formLayoutRight.addComponent(projectAddressLine2);
        projectCityField = getCityCombo();
        binder.bind(projectCityField, P_CITY);
        projectCityField.setRequired(true);
        formLayoutRight.addComponent(projectCityField);

        return formLayoutRight;

    }

    private FormLayout buildContactDetailsRight() {
        FormLayout formLayoutRight = new FormLayout();
        formLayoutRight.setSizeFull();
        formLayoutRight.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        Label mygubbiDetails = new Label("");
        mygubbiDetails.addStyleName(ValoTheme.LABEL_HUGE);
        mygubbiDetails.addStyleName(ValoTheme.LABEL_COLORED);
        formLayoutRight.addComponent(mygubbiDetails);

        designPerson = getDesignPersonCombo();
        binder.bind(designPerson, DESIGNER_NAME);
        designPerson.setRequired(true);
        formLayoutRight.addComponent(designPerson);
        designEmail = binder.buildAndBind("Email", DESIGNER_EMAIL);
        ((TextField) designEmail).setNullRepresentation("");
        designEmail.setRequired(true);
        formLayoutRight.addComponent(designEmail);
        designContact = binder.buildAndBind("Phone", DESIGNER_PHONE);
        ((TextField) designContact).setNullRepresentation("");
        designContact.setRequired(true);
        designPerson.addValueChangeListener(this::designerChanged);
        formLayoutRight.addComponent(designContact);
        return formLayoutRight;
    }

    private FormLayout buildContactDetailsLeft() {
        FormLayout formLayoutLeft = new FormLayout();
        formLayoutLeft.setSizeFull();
        formLayoutLeft.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        Label mygubbiDetails = new Label("MyGubbi Contact");
        mygubbiDetails.addStyleName(ValoTheme.LABEL_HUGE);
        mygubbiDetails.addStyleName(ValoTheme.LABEL_COLORED);
        formLayoutLeft.addComponent(mygubbiDetails);

        salesPerson = getSalesPersonCombo();
        binder.bind(salesPerson, SALES_NAME);
        salesPerson.setRequired(true);
        formLayoutLeft.addComponent(salesPerson);
        salesEmail = binder.buildAndBind("Email", SALES_EMAIL);
        ((TextField) salesEmail).setNullRepresentation("");
        salesEmail.setRequired(true);
        formLayoutLeft.addComponent(salesEmail);
        salesContact = binder.buildAndBind("Phone", SALES_PHONE);
        ((TextField) salesContact).setNullRepresentation("");
        salesContact.setRequired(true);
        formLayoutLeft.addComponent(salesContact);
        salesPerson.addValueChangeListener(this::salesPersonChanged);
        return formLayoutLeft;
    }

    private void designerChanged(Property.ValueChangeEvent valueChangeEvent) {
        String phone = (String) designPerson.getItem(designPerson.getValue()).getItemProperty(User.PHONE).getValue();
        ((TextField) designContact).setValue(phone);
        String email = (String) designPerson.getItem(designPerson.getValue()).getItemProperty(User.EMAIL).getValue();
        ((TextField) designEmail).setValue(email);
    }

    private void salesPersonChanged(Property.ValueChangeEvent valueChangeEvent) {
        String phone = (String) salesPerson.getItem(salesPerson.getValue()).getItemProperty(User.PHONE).getValue();
        ((TextField) salesContact).setValue(phone);
        String email = (String) salesPerson.getItem(salesPerson.getValue()).getItemProperty(User.EMAIL).getValue();
        ((TextField) salesEmail).setValue(email);
    }

    private ComboBox getSalesPersonCombo() {
        List<User> list = proposalDataProvider.getSalesUsers();
        final BeanContainer<String, User> container =
                new BeanContainer<>(User.class);
        container.setBeanIdProperty(User.NAME);
        container.addAll(list);

        ComboBox select = new ComboBox("Sales");
        select.setWidth("300px");
        select.setNullSelectionAllowed(false);
        select.setContainerDataSource(container);
        select.setItemCaptionPropertyId(User.NAME);

        if (StringUtils.isNotEmpty(proposalHeader.getSalesName())) {
            select.setValue(proposalHeader.getSalesName());
        } else if (container.size() > 0) {
            select.setValue(select.getItemIds().iterator().next());
            proposalHeader.setSalesName((String) select.getValue());
            proposalHeader.setSalesPhone(select.getItem(select.getValue()).getItemProperty(User.PHONE).getValue().toString());
            proposalHeader.setSalesEmail(select.getItem(select.getValue()).getItemProperty(User.EMAIL).getValue().toString());
        }

        return select;
    }

    private ComboBox getCityCombo() {
        List<LookupItem> list = proposalDataProvider.getLookupItems(ProposalDataProvider.CITY_LOOKUP);
        final BeanContainer<String, LookupItem> container =
                new BeanContainer<>(LookupItem.class);
        container.setBeanIdProperty(LookupItem.TITLE);
        container.addAll(list);

        ComboBox select = new ComboBox("City");
        select.setWidth("300px");
        select.setNullSelectionAllowed(false);
        select.setContainerDataSource(container);
        select.setItemCaptionPropertyId(LookupItem.TITLE);

        if (StringUtils.isNotEmpty(proposalHeader.getPcity())) {
            select.setValue(proposalHeader.getPcity());
        } else if (container.size() == 1) {
            select.setValue(select.getItemIds().iterator().next());
        }

        return select;
    }

    private ComboBox getDesignPersonCombo() {
        List<User> list = proposalDataProvider.getDesignerUsers();
        final BeanContainer<String, User> container =
                new BeanContainer<>(User.class);
        container.setBeanIdProperty(User.NAME);
        container.addAll(list);

        ComboBox select = new ComboBox("Designer");
        select.setWidth("300px");
        select.setNullSelectionAllowed(false);
        select.setContainerDataSource(container);
        select.setItemCaptionPropertyId(User.NAME);

        if (StringUtils.isNotEmpty(proposalHeader.getDesignerName())) {
            select.setValue(proposalHeader.getDesignerName());
        } else if (container.size() > 0) {
            select.setValue(select.getItemIds().iterator().next());
            proposalHeader.setDesignerName((String) select.getValue());
            proposalHeader.setDesignerPhone(select.getItem(select.getValue()).getItemProperty(User.PHONE).getValue().toString());
            proposalHeader.setDesignerEmail(select.getItem(select.getValue()).getItemProperty(User.EMAIL).getValue().toString());
        }

        return select;
    }

    private FormLayout buildMainFormLayoutRight() {

        FormLayout formLayoutRight = new FormLayout();
        formLayoutRight.setSizeFull();
        formLayoutRight.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        crmId = binder.buildAndBind("CRM #", CRM_ID);
        crmId.setRequired(true);
        ((TextField) crmId).setNullRepresentation("");
        formLayoutRight.addComponent(crmId);

        quotationField = binder.buildAndBind("Quotation #", QUOTE_NO);
        ((TextField) quotationField).setNullRepresentation("");
        quotationField.setRequired(true);

        formLayoutRight.addComponent(quotationField);

        return formLayoutRight;
    }

    private FormLayout buildMainFormLayoutLeft() {

        FormLayout formLayoutLeft = new FormLayout();
        formLayoutLeft.setSizeFull();
        formLayoutLeft.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        proposalTitleField = binder.buildAndBind("Proposal Title", ProposalHeader.TITLE);
        proposalTitleField.setRequired(true);
        ((TextField) proposalTitleField).setNullRepresentation("");

        ((TextField) proposalTitleField).addTextChangeListener(textChangeEvent -> {
            String changedText = textChangeEvent.getText();
            proposalTitleLabel.setValue(getFormattedTitle(changedText) + "&nbsp;");
            proposalTitleLabel.setDescription(changedText);
        });

        formLayoutLeft.addComponent(proposalTitleField);
        proposalVersionField = binder.buildAndBind("Proposal Version", VERSION);
        proposalVersionField.setReadOnly(true);
        formLayoutLeft.addComponent(proposalVersionField);

        return formLayoutLeft;
    }

    private Component buildProductsAndAddonsPage(){
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();

        HorizontalLayout amountsLayout = getAmountLayout();

        this.discountPercentage.addValueChangeListener(this::updateTotal);

        verticalLayout.addComponent(amountsLayout);

        Component componentProductDetails=buildProductDetails();
        verticalLayout.addComponent(componentProductDetails);

        Component componentAddonDetails = buildAddons();
        verticalLayout.addComponent(componentAddonDetails);

        return verticalLayout;
    }

    private HorizontalLayout getAmountLayout() {
        HorizontalLayout amountsLayout = new HorizontalLayout();

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

        Label Discount = new Label("<b>Discount :</b>",ContentMode.HTML);
        amountsLayout.addComponent(Discount);
        amountsLayout.setSpacing(true);
        Discount.addStyleName("amount-text-label");
        Discount.addStyleName("margin-top-18");

        this.discountPercentage = new TextField();
        this.discountPercentage.setConverter(new StringToDoubleConverter());
        this.discountPercentage.addStyleName("amount-text");
        this.discountPercentage.addStyleName("margin-top-18");
        this.discountPercentage.addStyleName("v-label-amount-text-label");
        this.discountPercentage.setCaptionAsHtml(true);
        this.discountPercentage.setValue("0");
        this.discountPercentage.setNullRepresentation("0");
        amountsLayout.addComponent(discountPercentage);

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
                    CustomizedProductDetailsWindow.open(CreateProposalsView.this.proposal, newProduct);
                }
        );

        addFromCatalogueButton.addClickListener(
                clickEvent -> {
                    CatalogueProduct newProduct = new CatalogueProduct();
                    newProduct.setType(CatalogueProduct.TYPES.CATALOGUE.name());
                    newProduct.setSeq(this.proposal.getProducts().size() + 1);
                    newProduct.setProposalId(this.proposalHeader.getId());
                    CatalogItemDetailsWindow.open(CreateProposalsView.this.proposal, newProduct);
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

    private void updateTotal(SelectionEvent selectionEvent) {
        updateTotal();
    }

    private void updateTotal(Property.ValueChangeEvent valueChangeEvent) {
        updateTotal();
    }

    private void updateTotal() {
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

        Double discountPercent = (Double) this.discountPercentage.getConvertedValue();

        if (discountPercent== null)
        {
            discountPercent = 0.0;
        }
        LOG.info("Discount percent : " + discountPercent);
        double discountAmount = totalWoAccessories * discountPercent / 100.0;
        Double totalAfterDiscount = this.round((totalWoAccessories - discountAmount), 0);
        Double grandTotal = this.round((totalAfterDiscount + costOfAccessories + addonsTotal), 0);

        this.discountTotal.setReadOnly(false);
        this.discountTotal.setValue(grandTotal + "");
        this.discountTotal.setReadOnly(true);

        this.grandTotal.setReadOnly(false);
        this.grandTotal.setValue(totalAmount + "");
        this.grandTotal.setReadOnly(true);

        productAndAddonSelection.setDiscountPercentage(discountPercent);
        productAndAddonSelection.setDiscountAmount(discountAmount);
    }

    private GeneratedPropertyContainer createGeneratedProductPropertyContainer() {
        GeneratedPropertyContainer genContainer = new GeneratedPropertyContainer(productContainer);
        genContainer.addGeneratedProperty("actions", getActionTextGenerator());
        genContainer.addGeneratedProperty("productCategoryText", getProductCategoryTextGenerator());
        return genContainer;
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

    private double round(double value, int places)
    {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}

