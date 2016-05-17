package com.mygubbi.game.dashboard.view.proposals;


import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.data.dummy.FileDataProviderUtil;
import com.mygubbi.game.dashboard.domain.Proposal;
import com.mygubbi.game.dashboard.domain.ProposalHeader;
import com.mygubbi.game.dashboard.domain.User;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.mygubbi.game.dashboard.event.ProposalEvent;
import com.mygubbi.game.dashboard.view.DashboardViewType;
import com.mygubbi.game.dashboard.view.NotificationUtil;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.commons.lang.StringUtils;
import org.vaadin.dialogs.ConfirmDialog;


/**
 * Created by test on 31-03-2016.
 */
public class CreateProposalsView extends Panel implements View {


    private ProposalDataProvider proposalDataProvider = new ProposalDataProvider(new FileDataProviderUtil());

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

    private Field<?> salesPerson;
    private Field<?> salesEmail;
    private Field<?> salesContact;
    private Field<?> designPerson;
    private Field<?> designEmail;
    private Field<?> designContact;

    private Grid grid;
    private Proposal proposal;
    private Label proposalTitleLabel;
    private final BeanFieldGroup<ProposalHeader> binder = new BeanFieldGroup<>(ProposalHeader.class);
    ;
    private Button submitButton;
    private Label draftLabel;

    public CreateProposalsView() {

        DashboardEventBus.register(this);

        this.proposal = proposalDataProvider.createProposal();
        ProposalHeader proposalHeader = this.proposal.getProposalHeader();
        initHeader(proposalHeader);
        this.binder.setItemDataSource(proposalHeader);

        DashboardEventBus.post(new ProposalEvent.ProposalUpdated());
        setSizeFull();

        VerticalLayout vLayout = new VerticalLayout();
        vLayout.addComponent(buildHeader());

        TabSheet accordion = new TabSheet();
        accordion.addTab(buildForm(), "Header");
        accordion.addTab(buildProductDetails(), "Products");

        vLayout.addComponent(accordion);
        setContent(vLayout);
        Responsive.makeResponsive(accordion);

    }

    private BeanItem<ProposalHeader> getProposalHeaderBeanItem(ProposalHeader proposalHeader) {
        BeanItem<ProposalHeader> proposalHeaderBeanItem = new BeanItem<>(proposalHeader);
        return proposalHeaderBeanItem;
    }

    private void initHeader(ProposalHeader proposalHeader) {
        proposalHeader.setProposalTitle("New Proposal");
        proposalHeader.setProposalVersion("--auto--");
    }

    private Component buildHeader() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSizeFull();
        horizontalLayout.setMargin(new MarginInfo(false, true, false, true));

        HorizontalLayout horizontalLayout2 = new HorizontalLayout();
        horizontalLayout.addComponent(horizontalLayout2);

        proposalTitleLabel = new Label("New Proposal&nbsp;", ContentMode.HTML);
        proposalTitleLabel.addStyleName(ValoTheme.LABEL_H1);
        proposalTitleLabel.setWidth("1%");
        horizontalLayout2.addComponent(proposalTitleLabel);
        horizontalLayout2.setComponentAlignment(proposalTitleLabel, Alignment.MIDDLE_LEFT);

        draftLabel = new Label("[ Draft ]");
        draftLabel.addStyleName(ValoTheme.LABEL_COLORED);
        draftLabel.addStyleName(ValoTheme.LABEL_H1);
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


        Button downloadButton = new Button("Download");
        downloadButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        downloadButton.addStyleName(ValoTheme.BUTTON_SMALL);
        horizontalLayout1.addComponent(downloadButton);
        horizontalLayout1.setComponentAlignment(downloadButton, Alignment.MIDDLE_RIGHT);

        submitButton = new Button("Submit");
        submitButton.setVisible(!"active".equals(proposal.getProposalHeader().getStatus()));
        submitButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        submitButton.addStyleName(ValoTheme.BUTTON_SMALL);
        submitButton.addClickListener(this::submit);
        horizontalLayout1.addComponent(submitButton);
        horizontalLayout1.setComponentAlignment(submitButton, Alignment.MIDDLE_RIGHT);

        Button saveButton = new Button("Save");
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

    private void close(Button.ClickEvent clickEvent) {

        ConfirmDialog.show(UI.getCurrent(), "", "Please Confirm:\n\n - On 'Save & Close' data will be saved before closing\n - On 'Close' unsaved data will be lost",
                "Save & Close", "Cancel", "Close", dialog -> {
                    if (!dialog.isCanceled()) {
                        if (dialog.isConfirmed()) {
                            boolean success = proposalDataProvider.saveProposal(proposal.getProposalHeader());
                            if (success) {
                                NotificationUtil.showNotification("Saved successfully!", NotificationUtil.STYLE_BAR_SUCCESS_SMALL);
                            } else {
                                NotificationUtil.showNotification("Couldn't Save Proposal! Please contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                            }
                        }
                        UI.getCurrent().getNavigator()
                                .navigateTo(DashboardViewType.PROPOSALS.name());
                    }
                });
    }

    private void save(Button.ClickEvent clickEvent) {
        ProposalHeader header = proposal.getProposalHeader();
        if (StringUtils.isEmpty(header.getProposalTitle())) {
            header.setProposalTitle("New Proposal");
        }
        boolean success = proposalDataProvider.saveProposal(header);

        if (success) {
            NotificationUtil.showNotification("Saved successfully!", NotificationUtil.STYLE_BAR_SUCCESS_SMALL);
        } else {
            NotificationUtil.showNotification("Couldn't Save Proposal! Please contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
        }

    }

    private void submit(Button.ClickEvent clickEvent) {
        try {
            binder.commit();
            ProposalHeader header = proposal.getProposalHeader();
            header.setStatus("active");
            boolean success = proposalDataProvider.saveProposal(header);
            if (success) {
                success = proposalDataProvider.submitProposal(header.getProposalId());
                if (success) {
                    submitButton.setVisible(false);
                    draftLabel.setValue("[ Active ]");
                    NotificationUtil.showNotification("Submitted successfully!", NotificationUtil.STYLE_BAR_SUCCESS_SMALL);
                } else {
                    NotificationUtil.showNotification("Couldn't Activate Proposal! Please contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
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

        customerIdField = binder.buildAndBind("Customer ID", "customerId");
        customerIdField.setRequired(true);
        ((TextField) customerIdField).setNullRepresentation("");
        formLayoutLeft.addComponent(customerIdField);
        customerNameField = binder.buildAndBind("Customer Name", "customerName");
        customerNameField.setRequired(true);
        ((TextField) customerNameField).setNullRepresentation("");
        formLayoutLeft.addComponent(customerNameField);
        customerAddressLine1 = binder.buildAndBind("Address Line 1", "customerAddressLine1");
        ((TextField) customerAddressLine1).setNullRepresentation("");
        formLayoutLeft.addComponent(customerAddressLine1);
        customerAddressLine2 = binder.buildAndBind("Address Line 2", "customerAddressLine2");
        ((TextField) customerAddressLine2).setNullRepresentation("");
        formLayoutLeft.addComponent(customerAddressLine2);
        customerAddressLine3 = binder.buildAndBind("Address Line 3", "customerAddressLine3");
        ((TextField) customerAddressLine3).setNullRepresentation("");
        formLayoutLeft.addComponent(customerAddressLine3);
        customerCityField = binder.buildAndBind("City", "customerCity");
        ((TextField) customerCityField).setNullRepresentation("");
        formLayoutLeft.addComponent(customerCityField);
        customerEmailField = binder.buildAndBind("Email", "customerEmail");
        ((TextField) customerEmailField).setNullRepresentation("");
        formLayoutLeft.addComponent(customerEmailField);
        customerNumberField1 = binder.buildAndBind("Phone 1", "customerPhone1");
        ((TextField) customerNumberField1).setNullRepresentation("");
        formLayoutLeft.addComponent(customerNumberField1);
        customerNumberField2 = binder.buildAndBind("Phone 2", "customerPhone2");
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

        projectName = binder.buildAndBind("Project Name", "projectName");
        ((TextField) projectName).setNullRepresentation("");
        formLayoutRight.addComponent(projectName);
        projectAddressLine1 = binder.buildAndBind("Address Line 1", "projectAddressLine1");
        ((TextField) projectAddressLine1).setNullRepresentation("");
        formLayoutRight.addComponent(projectAddressLine1);
        projectAddressLine2 = binder.buildAndBind("Address Line 2", "projectAddressLine2");
        ((TextField) projectAddressLine2).setNullRepresentation("");
        formLayoutRight.addComponent(projectAddressLine2);
        projectCityField = binder.buildAndBind("City", "projectCity");
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

        designPerson = binder.buildAndBind("Design Person", "designContactName");
        designPerson.setRequired(true);
        ((TextField) designPerson).setNullRepresentation("");
        formLayoutRight.addComponent(designPerson);
        designEmail = binder.buildAndBind("Email", "designContactEmail");
        ((TextField) designEmail).setNullRepresentation("");
        formLayoutRight.addComponent(designEmail);
        designContact = binder.buildAndBind("Phone", "designContactPhone");
        ((TextField) designContact).setNullRepresentation("");
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

        salesPerson = binder.buildAndBind("Sales Person", "salesContactName");
        ((TextField) salesPerson).setNullRepresentation("");
        salesPerson.setRequired(true);
        formLayoutLeft.addComponent(salesPerson);
        salesEmail = binder.buildAndBind("Email", "salesContactEmail");
        ((TextField) salesEmail).setNullRepresentation("");
        formLayoutLeft.addComponent(salesEmail);
        salesContact = binder.buildAndBind("Phone", "salesContactPhone");
        ((TextField) salesContact).setNullRepresentation("");
        formLayoutLeft.addComponent(salesContact);
        return formLayoutLeft;
    }

    private FormLayout buildMainFormLayoutRight() {

        FormLayout formLayoutRight = new FormLayout();
        formLayoutRight.setSizeFull();
        formLayoutRight.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        crmId = binder.buildAndBind("CRM #", "crmId");
        crmId.setRequired(true);
        ((TextField) crmId).setNullRepresentation("");
        formLayoutRight.addComponent(crmId);

        quotationField = binder.buildAndBind("Quotation #", "quotationNo");
        ((TextField) quotationField).setNullRepresentation("");
        quotationField.setRequired(true);

        formLayoutRight.addComponent(quotationField);

        return formLayoutRight;
    }

    private FormLayout buildMainFormLayoutLeft() {

        FormLayout formLayoutLeft = new FormLayout();
        formLayoutLeft.setSizeFull();
        formLayoutLeft.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        proposalTitleField = binder.buildAndBind("Proposal Title", "proposalTitle");
        proposalTitleField.setRequired(true);
        ((TextField) proposalTitleField).setNullRepresentation("");

        ((TextField) proposalTitleField).addTextChangeListener(textChangeEvent -> {
            String changedText = textChangeEvent.getText();
            proposalTitleLabel.setValue(changedText + "&nbsp;");
        });

        formLayoutLeft.addComponent(proposalTitleField);
        proposalVersionField = binder.buildAndBind("Proposal Version", "proposalVersion");
        proposalVersionField.setReadOnly(true);
        formLayoutLeft.addComponent(proposalVersionField);

        return formLayoutLeft;
    }

    private User getCurrentUser() {
        return (User) VaadinSession.getCurrent().getAttribute(
                User.class.getName());
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

        Button button = new Button("Add Customized");
        button.addStyleName(ValoTheme.BUTTON_SMALL);
        horizontalLayout.addComponent(button);

        Button standardItemBtn = new Button("Add from Catalogue");
        standardItemBtn.addStyleName(ValoTheme.BUTTON_SMALL);
        horizontalLayout.addComponent(standardItemBtn);

        verticalLayout.addComponent(horizontalLayout);

        button.addClickListener(clickEvent ->
                CustomizedItemDetailsWindow.open(CreateProposalsView.this.proposal)
        );

        standardItemBtn.addClickListener(clickEvent ->
                CatalogItemDetailsWindow.open(CreateProposalsView.this.proposal)
        );

        grid = new Grid();
        grid.setSizeFull();

        grid.addColumn("Item #", String.class);
        grid.addColumn("Description", String.class);
        grid.addColumn("Room", String.class);
        grid.addColumn("Category", String.class);
        grid.addColumn("Material & Finish", String.class);
        grid.addColumn("Qty", Integer.class);
        grid.addColumn("Amount", String.class);
        grid.addColumn("Actions", String.class);

        verticalLayout.addComponent(grid);

        return verticalLayout;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

    }
}

