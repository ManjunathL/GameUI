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
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.gridutil.renderer.EditDeleteButtonValueRenderer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.mygubbi.game.dashboard.domain.Product.TYPE;
import static com.mygubbi.game.dashboard.domain.ProposalHeader.*;


/**
 * Created by test on 31-03-2016.
 */
public class CreateProposalsView extends Panel implements View {


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
    private Field<?> projectCityField;

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
    private Proposal proposal;
    private Button saveButton;
    private BeanItemContainer productContainer;
    private Button deleteButton;
    private TextField grandTotal;

    private ProductSelections productSelections;
    private Button reviseButton;
    private Button publishButton;
    private Button cancelButton;
    private Button addKitchenOrWardrobeButton;
    private Button addFromCatalogueButton;
    private FileAttachmentComponent fileAttachmentComponent;

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
            proposalHeader.setVersion(NEW_VERSION);
        } else {
            proposalHeader = proposalDataProvider.createProposal();
            proposalHeader.setTitle(NEW_TITLE);
            proposalHeader.setVersion(NEW_VERSION);
            proposalHeader.setStatus(ProposalState.draft.name());
            this.proposal = new Proposal();
            this.proposal.setProposalHeader(proposalHeader);
        }

        this.productSelections = new ProductSelections();
        this.productSelections.setProposalId(this.proposalHeader.getId());

        DashboardEventBus.register(this);
        this.binder.setItemDataSource(proposalHeader);

        setSizeFull();

        VerticalLayout vLayout = new VerticalLayout();
        vLayout.addComponent(buildHeader());

        TabSheet tabs = new TabSheet();
        tabs.addTab(buildForm(), "Header");
        tabs.addTab(buildProductDetails(), "Products");
        fileAttachmentComponent = new FileAttachmentComponent(proposal, proposalHeader.getFolderPath(),
                attachmentData -> proposalDataProvider.addProposalDoc(proposalHeader.getId(), attachmentData.getFileAttachment()),
                attachmentData -> proposalDataProvider.removeProposalDoc(attachmentData.getFileAttachment().getId()),
                !proposalHeader.getStatus().equals(ProposalState.draft.name())
        );
        tabs.addTab(fileAttachmentComponent, "Attachments");

        vLayout.addComponent(tabs);
        setContent(vLayout);
        Responsive.makeResponsive(tabs);
        updateTotal(null);
        handleState();
    }

    private void handleState() {

        ProposalState proposalState = ProposalState.valueOf(proposalHeader.getStatus());
        switch (proposalState) {
            case draft:
                addKitchenOrWardrobeButton.setEnabled(true);
                addFromCatalogueButton.setEnabled(true);
                fileAttachmentComponent.getFileUploadCtrl().setEnabled(true);
                setHeaderFieldsReadOnly(false);
                fileAttachmentComponent.setReadOnly(false);
                break;
            case active:
            case cancelled:
            case published:
                addKitchenOrWardrobeButton.setEnabled(false);
                addFromCatalogueButton.setEnabled(false);
                fileAttachmentComponent.getFileUploadCtrl().setEnabled(false);
                setHeaderFieldsReadOnly(true);
                fileAttachmentComponent.setReadOnly(true);
                break;
            default:
                throw new RuntimeException("Unknown State");
        }
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
        horizontalLayout.setMargin(new MarginInfo(false, true, false, true));

        HorizontalLayout horizontalLayout2 = new HorizontalLayout();
        horizontalLayout.addComponent(horizontalLayout2);

        String title = proposalHeader.getTitle();
        proposalTitleLabel = new Label(getFormattedTitle(title) + "&nbsp;", ContentMode.HTML);
        proposalTitleLabel.addStyleName(ValoTheme.LABEL_H2);
        proposalTitleLabel.setWidth("1%");
        proposalTitleLabel.setDescription(title);
        horizontalLayout2.addComponent(proposalTitleLabel);
        horizontalLayout2.setComponentAlignment(proposalTitleLabel, Alignment.MIDDLE_LEFT);

        draftLabel = new Label("[ " + proposalHeader.getStatus() + " ]");
        draftLabel.addStyleName(ValoTheme.LABEL_COLORED);
        draftLabel.addStyleName(ValoTheme.LABEL_H2);
        horizontalLayout2.addComponent(draftLabel);
        horizontalLayout2.setComponentAlignment(draftLabel, Alignment.MIDDLE_LEFT);

        VerticalLayout verticalLayout = new VerticalLayout();
        horizontalLayout.addComponent(verticalLayout);
        horizontalLayout.setComponentAlignment(verticalLayout, Alignment.MIDDLE_CENTER);

        HorizontalLayout horizontalLayout1 = new HorizontalLayout();
        horizontalLayout1.setSizeFull();
        horizontalLayout1.setSpacing(true);
        verticalLayout.addComponent(horizontalLayout1);

        Label spacingLabel = new Label("&nbsp;", ContentMode.HTML);
        horizontalLayout1.addComponent(spacingLabel);
        horizontalLayout1.setExpandRatio(spacingLabel, 1.0f);

        Button downloadButton = new Button("Quote&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
        downloadButton.setCaptionAsHtml(true);
        downloadButton.setIcon(FontAwesome.DOWNLOAD);
        downloadButton.setStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
        downloadButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        downloadButton.addStyleName(ValoTheme.BUTTON_SMALL);

        StreamResource myResource = createQuoteResource();
        FileDownloader fileDownloader = new FileDownloader(myResource);
        fileDownloader.extend(downloadButton);
        horizontalLayout1.addComponent(downloadButton);
        horizontalLayout1.setComponentAlignment(downloadButton, Alignment.MIDDLE_RIGHT);

        Button jobcardButton = new Button("Job Card&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
        jobcardButton.setCaptionAsHtml(true);
        jobcardButton.setIcon(FontAwesome.DOWNLOAD);
        jobcardButton.setStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
        jobcardButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        jobcardButton.addStyleName(ValoTheme.BUTTON_SMALL);

        StreamResource jobcardResource = createJobcardResource();
        FileDownloader jobcardDownloader = new FileDownloader(jobcardResource);
        jobcardDownloader.extend(jobcardButton);
        horizontalLayout1.addComponent(jobcardButton);
        horizontalLayout1.setComponentAlignment(jobcardButton, Alignment.MIDDLE_RIGHT);

        submitButton = new Button("Submit");
        submitButton.setVisible(ProposalState.draft.name().equals(proposalHeader.getStatus()));
        submitButton.addStyleName(ValoTheme.BUTTON_SMALL);
        submitButton.addClickListener(this::submit);
        horizontalLayout1.addComponent(submitButton);
        horizontalLayout1.setComponentAlignment(submitButton, Alignment.MIDDLE_RIGHT);

        publishButton = new Button("Publish");
        publishButton.setVisible(ProposalState.active.name().equals(proposalHeader.getStatus()));
        publishButton.addStyleName(ValoTheme.BUTTON_SMALL);
        publishButton.addClickListener(this::publish);
        horizontalLayout1.addComponent(publishButton);
        horizontalLayout1.setComponentAlignment(publishButton, Alignment.MIDDLE_RIGHT);

        reviseButton = new Button("Revise");
        reviseButton.setVisible(ProposalState.active.name().equals(proposalHeader.getStatus()));
        reviseButton.addStyleName(ValoTheme.BUTTON_SMALL);
        reviseButton.addClickListener(this::revise);
        horizontalLayout1.addComponent(reviseButton);
        horizontalLayout1.setComponentAlignment(reviseButton, Alignment.MIDDLE_RIGHT);

        cancelButton = new Button("Cancel");
        cancelButton.addStyleName(ValoTheme.BUTTON_SMALL);
        cancelButton.addClickListener(this::cancel);
        horizontalLayout1.addComponent(cancelButton);
        horizontalLayout1.setComponentAlignment(cancelButton, Alignment.MIDDLE_RIGHT);

        deleteButton = new Button("Delete");
        deleteButton.addStyleName(ValoTheme.BUTTON_SMALL);
        deleteButton.addClickListener(this::deleteProposal);
        horizontalLayout1.addComponent(deleteButton);
        horizontalLayout1.setComponentAlignment(deleteButton, Alignment.MIDDLE_RIGHT);

        saveButton = new Button("Save");
        saveButton.addStyleName(ValoTheme.BUTTON_SMALL);
        saveButton.addClickListener(this::save);
        horizontalLayout1.addComponent(saveButton);
        horizontalLayout1.setComponentAlignment(saveButton, Alignment.MIDDLE_RIGHT);

        Button closeButton = new Button("Close");
        closeButton.addStyleName(ValoTheme.BUTTON_SMALL);
        closeButton.addClickListener(this::close);
        horizontalLayout1.addComponent(closeButton);
        horizontalLayout1.setComponentAlignment(closeButton, Alignment.MIDDLE_RIGHT);

        return horizontalLayout;
    }

    private void cancel(Button.ClickEvent clickEvent) {
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
                                    reviseButton.setVisible(false);
                                    submitButton.setVisible(false);
                                    publishButton.setVisible(false);
                                    deleteButton.setVisible(false);
                                    saveButton.setVisible(false);
                                    cancelButton.setVisible(false);
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
                                    reviseButton.setVisible(false);
                                    submitButton.setVisible(false);
                                    publishButton.setVisible(false);
                                    deleteButton.setVisible(false);
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

    private void revise(Button.ClickEvent clickEvent) {
        try {
            binder.commit();
            proposalHeader.setStatus(ProposalState.draft.name());
            boolean success = proposalDataProvider.saveProposal(proposalHeader);
            if (success) {
                success = proposalDataProvider.reviseProposal(proposalHeader.getId());
                if (success) {
                    reviseButton.setVisible(false);
                    publishButton.setVisible(false);
                    submitButton.setVisible(true);
                    saveButton.setVisible(true);
                    deleteButton.setVisible(true);
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

    private void deleteProposal(Button.ClickEvent clickEvent) {

        ConfirmDialog.show(UI.getCurrent(), "", "Do you want to delete this Proposal?",
                "Yes", "No", dialog -> {
                    if (!dialog.isCanceled()) {
                        proposalDataProvider.deleteProposal(proposalHeader.getId());
                        DashboardEventBus.post(new ProposalEvent.ProposalUpdated());
                    }
                    UI.getCurrent().getNavigator()
                            .navigateTo(DashboardViewType.PROPOSALS.name());
                });

    }

    private String getFormattedTitle(String title) {
        if (title.length() <= 40) {
            return title;
        } else {
            return title.substring(0, 40) + "...";
        }

    }

    private StreamResource createQuoteResource() {
        StreamResource.StreamSource source = () -> {
            String quoteFile = proposalDataProvider.getProposalQuoteFile(this.productSelections);
            InputStream input = null;
            try {
                input = new ByteArrayInputStream(FileUtils.readFileToByteArray(new File(quoteFile)));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return input;
        };
        return new StreamResource(source, "Quotation.xlsx");
    }

    private StreamResource createJobcardResource() {
        StreamResource.StreamSource source = () -> {

            if (this.productSelections.getProductIds().size() == 1) {
                String jobcardFile = proposalDataProvider.getJobCardFile(this.productSelections);
                InputStream input = null;
                try {
                    input = new ByteArrayInputStream(FileUtils.readFileToByteArray(new File(jobcardFile)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return input;
            } else {
                NotificationUtil.showNotification("Please select a single product to download its Job Card.", NotificationUtil.STYLE_BAR_WARNING_SMALL);
                return null;
            }
        };
        return new StreamResource(source, "JobCard.xlsx");
    }

    private void close(Button.ClickEvent clickEvent) {

        ConfirmDialog.show(UI.getCurrent(), "", "Do you want to close this Proposal? Unsaved data will be lost.",
                "Yes", "No", dialog -> {
                    if (!dialog.isCanceled()) {
                        UI.getCurrent().getNavigator()
                                .navigateTo(DashboardViewType.PROPOSALS.name());
                    }
                });
    }

    private void save(Button.ClickEvent clickEvent) {
        if (StringUtils.isEmpty(proposalHeader.getTitle())) {
            proposalHeader.setTitle("New Proposal");
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
                    mapped = populatedProduct.allModulesMapped() && !populatedProduct.getModules().isEmpty();
                    if (!mapped) {
                        break;
                    }
                }

                if (!mapped) {
                    NotificationUtil.showNotification("Couldn't Submit. Please ensure all Products have mapped Modules.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                } else {

                    success = proposalDataProvider.submitProposal(proposalHeader.getId());
                    if (success) {
                        reviseButton.setVisible(true);
                        publishButton.setVisible(true);
                        submitButton.setVisible(false);
                        deleteButton.setVisible(false);
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
        projectCityField = binder.buildAndBind("City", P_CITY);
        ((TextField) projectCityField).setNullRepresentation("");
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
        //designContact.setReadOnly(false);
        ((TextField) designContact).setValue(phone);
        //designContact.setReadOnly(true);
        String email = (String) designPerson.getItem(designPerson.getValue()).getItemProperty(User.EMAIL).getValue();
        //designEmail.setReadOnly(false);
        ((TextField) designEmail).setValue(email);
        //designEmail.setReadOnly(true);
    }

    private void salesPersonChanged(Property.ValueChangeEvent valueChangeEvent) {
        String phone = (String) salesPerson.getItem(salesPerson.getValue()).getItemProperty(User.PHONE).getValue();
        //salesContact.setReadOnly(false);
        ((TextField) salesContact).setValue(phone);
        //salesContact.setReadOnly(true);
        String email = (String) salesPerson.getItem(salesPerson.getValue()).getItemProperty(User.EMAIL).getValue();
        //salesEmail.setReadOnly(false);
        ((TextField) salesEmail).setValue(email);
        //salesEmail.setReadOnly(true);
    }

    private ComboBox getSalesPersonCombo() {
        List<User> list = proposalDataProvider.getSalesUsers();
        final BeanContainer<String, User> container =
                new BeanContainer<>(User.class);
        container.setBeanIdProperty(User.NAME);
        container.addAll(list);

        ComboBox select = new ComboBox("Sales");
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

    private ComboBox getDesignPersonCombo() {
        List<User> list = proposalDataProvider.getDesignerUsers();
        final BeanContainer<String, User> container =
                new BeanContainer<>(User.class);
        container.setBeanIdProperty(User.NAME);
        container.addAll(list);

        ComboBox select = new ComboBox("Designer");
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
        });

        formLayoutLeft.addComponent(proposalTitleField);
        proposalVersionField = binder.buildAndBind("Proposal Version", VERSION);
        proposalVersionField.setReadOnly(true);
        formLayoutLeft.addComponent(proposalVersionField);

        return formLayoutLeft;
    }

    private Component buildProductDetails() {

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        verticalLayout.setSpacing(true);
        verticalLayout.setMargin(new MarginInfo(true, true, true, true));
        verticalLayout.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSizeFull();
        horizontalLayout.setSpacing(true);
        horizontalLayout.setDefaultComponentAlignment(Alignment.TOP_RIGHT);

        Label spacingLabel = new Label("&nbsp;", ContentMode.HTML);
        horizontalLayout.addComponent(spacingLabel);
        horizontalLayout.setExpandRatio(spacingLabel, 1.0f);

        addKitchenOrWardrobeButton = new Button("Kitchen/Wardrobe");
        addKitchenOrWardrobeButton.setIcon(FontAwesome.PLUS_CIRCLE);
        addKitchenOrWardrobeButton.addStyleName(ValoTheme.BUTTON_SMALL);
        horizontalLayout.addComponent(addKitchenOrWardrobeButton);

        addFromCatalogueButton = new Button("From Catalogue");
        addFromCatalogueButton.setIcon(FontAwesome.PLUS_CIRCLE);
        addFromCatalogueButton.addStyleName(ValoTheme.BUTTON_SMALL);
        horizontalLayout.addComponent(addFromCatalogueButton);

        verticalLayout.addComponent(horizontalLayout);

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


                if (!proposalHeader.getStatus().equals(ProposalState.draft.name())) {
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
                                    updateTotal(null);
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

        verticalLayout.addComponent(productsGrid);

        Label label = new Label("Select Products and click Download Quote/Job Card button to generate output for only the selected Products.");
        label.setStyleName("font-italics");
        verticalLayout.addComponent(label);

        FormLayout formLayout = new FormLayout();
        formLayout.setStyleName("grand-total-flayout");
        this.grandTotal = new TextField("<h2>Grand Total:</h2>");
        this.grandTotal.setStyleName("amount-text");
        this.grandTotal.addStyleName("margin-top-18");
        this.grandTotal.setCaptionAsHtml(true);
        this.grandTotal.setReadOnly(true);
        formLayout.addComponent(this.grandTotal);
        formLayout.setSizeUndefined();

        verticalLayout.addComponent(formLayout);

        return verticalLayout;
    }

    private void updateTotal(SelectionEvent selectionEvent) {
        Collection<?> objects = productsGrid.getSelectedRows();
        boolean anythingSelected = true;
        this.productSelections.getProductIds().clear();

        if (objects.size() == 0) {
            anythingSelected = false;
            objects = this.productsGrid.getContainerDataSource().getItemIds();
        }

        double grandTotal = 0;

        for (Object object : objects) {
            Double amount = (Double) this.productsGrid.getContainerDataSource().getItem(object).getItemProperty(Product.AMOUNT).getValue();
            grandTotal += amount;
            Integer id = (Integer) this.productsGrid.getContainerDataSource().getItem(object).getItemProperty(Product.ID).getValue();

            if (anythingSelected) {
                this.productSelections.getProductIds().add(id);
            }
        }

        this.grandTotal.setReadOnly(false);
        this.grandTotal.setValue(grandTotal + "");
        this.grandTotal.setReadOnly(true);
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
        updateTotal(null);
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
            updateTotal(null);
            productsGrid.sort(Product.SEQ, SortDirection.ASCENDING);
        }
        //updateTotalAmount();
    }
}

