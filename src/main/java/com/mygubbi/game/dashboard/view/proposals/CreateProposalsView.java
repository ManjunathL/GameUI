package com.mygubbi.game.dashboard.view.proposals;


import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.data.dummy.FileDataProviderUtil;
import com.mygubbi.game.dashboard.domain.Proposal;
import com.mygubbi.game.dashboard.domain.User;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.mygubbi.game.dashboard.event.ProposalEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;


/**
 * Created by test on 31-03-2016.
 */
public class CreateProposalsView extends Panel implements View {


    private ProposalDataProvider proposalDataProvider = new ProposalDataProvider(new FileDataProviderUtil());

    private TextField proposalTitleField;
    private TextField crmId;
    private TextField proposalVersionField;
    private TextField quotationField;

    private TextField customerIdField;
    private TextField customerNameField;
    private TextField customerAddressLine1;
    private TextField customerAddressLine2;
    private TextField customerAddressLine3;
    private TextField customerCityField;
    private TextField customerEmailField;
    private TextField customerNumberField1;
    private TextField customerNumberField2;

    private TextField projectName;
    private TextField projectModelType;
    private TextField projectDrawingId;
    private TextField projectAddressLine1;
    private TextField projectAddressLine2;
    private TextField projectCityField;

    private TextField salesPerson;
    private TextField salesEmail;
    private TextField salesContact;
    private TextField designPerson;
    private TextField designEmail;
    private TextField designContact;

    private Grid grid;
    private Proposal proposal;

    public CreateProposalsView() {

        DashboardEventBus.register(this);

        this.proposal = proposalDataProvider.createProposal();
        DashboardEventBus.post(new ProposalEvent.ProposalUpdated());

        setSizeFull();

        VerticalLayout vLayout = new VerticalLayout();

        vLayout.addComponent(buildHeader());

        TabSheet accordion = new TabSheet();
        accordion.addTab(buildForm(), "Header");
        accordion.addTab(buildItemDetails(), "Items");

        vLayout.addComponent(accordion);

        setContent(vLayout);

        Responsive.makeResponsive(accordion);

    }

    private Component buildHeader() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSizeFull();
        horizontalLayout.setMargin(new MarginInfo(false, true, false, true));

        HorizontalLayout horizontalLayout2 = new HorizontalLayout();
        horizontalLayout.addComponent(horizontalLayout2);

        Label label = new Label("New Proposal&nbsp;", ContentMode.HTML);
        label.addStyleName(ValoTheme.LABEL_H1);
        label.setWidth("1%");
        horizontalLayout2.addComponent(label);
        horizontalLayout2.setComponentAlignment(label, Alignment.MIDDLE_LEFT);

        Label draftLabel = new Label("[ Draft ]");
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
        
        Button submitButton = new Button("Submit");
        submitButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        submitButton.addStyleName(ValoTheme.BUTTON_SMALL);
        horizontalLayout1.addComponent(submitButton);
        horizontalLayout1.setComponentAlignment(submitButton, Alignment.MIDDLE_RIGHT);

        Button saveButton = new Button("Save");
        saveButton.addStyleName(ValoTheme.BUTTON_SMALL);
        horizontalLayout1.addComponent(saveButton);
        horizontalLayout1.setComponentAlignment(saveButton, Alignment.MIDDLE_RIGHT);

        Button cancelButton = new Button("Cancel");
        cancelButton.addStyleName(ValoTheme.BUTTON_SMALL);
        horizontalLayout1.addComponent(cancelButton);
        horizontalLayout1.setComponentAlignment(cancelButton, Alignment.MIDDLE_RIGHT);

        return horizontalLayout;
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

        customerIdField = new TextField("Customer ID");
        formLayoutLeft.addComponent(customerIdField);
        customerNameField = new TextField("Customer Name");
        formLayoutLeft.addComponent(customerNameField);
        customerAddressLine1 = new TextField("Address Line 1");
        formLayoutLeft.addComponent(customerAddressLine1);
        customerAddressLine2 = new TextField("Address Line 2");
        formLayoutLeft.addComponent(customerAddressLine2);
        customerAddressLine3 = new TextField("Address Line 3");
        formLayoutLeft.addComponent(customerAddressLine3);
        customerCityField = new TextField("City");
        formLayoutLeft.addComponent(customerCityField);
        customerEmailField = new TextField("Email");
        formLayoutLeft.addComponent(customerEmailField);
        customerNumberField1 = new TextField("Phone 1");
        formLayoutLeft.addComponent(customerNumberField1);
        customerNumberField2 = new TextField("Phone 2");
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

        projectName = new TextField("Project Name");
        formLayoutRight.addComponent(projectName);
        projectModelType = new TextField("Model Type");
        formLayoutRight.addComponent(projectModelType);
        projectDrawingId = new TextField("Drawing #");
        formLayoutRight.addComponent(projectDrawingId);
        projectAddressLine1 = new TextField("Address Line 1");
        formLayoutRight.addComponent(projectAddressLine1);
        projectAddressLine2 = new TextField("Address Line 2");
        formLayoutRight.addComponent(projectAddressLine2);
        projectCityField = new TextField("City");
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

        designPerson = new TextField("Design Person");
        formLayoutRight.addComponent(designPerson);
        designEmail = new TextField("Email");
        formLayoutRight.addComponent(designEmail);
        designContact = new TextField("Phone");
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

        salesPerson = new TextField("Sales Person");
        formLayoutLeft.addComponent(salesPerson);
        salesEmail = new TextField("Email");
        formLayoutLeft.addComponent(salesEmail);
        salesContact = new TextField("Phone");
        formLayoutLeft.addComponent(salesContact);
        return formLayoutLeft;
    }

    private FormLayout buildMainFormLayoutRight() {

        FormLayout formLayoutRight = new FormLayout();
        formLayoutRight.setSizeFull();
        formLayoutRight.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        crmId = new TextField("CRM #");
        formLayoutRight.addComponent(crmId);
        quotationField = new TextField("Quotation #");
        formLayoutRight.addComponent(quotationField);

        return formLayoutRight;
    }

    private FormLayout buildMainFormLayoutLeft() {

        FormLayout formLayoutLeft = new FormLayout();
        formLayoutLeft.setSizeFull();
        formLayoutLeft.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        proposalTitleField = new TextField("Proposal Title");
        formLayoutLeft.addComponent(proposalTitleField);
        proposalVersionField = new TextField("Proposal Version");
        proposalVersionField.setValue("1.0");
        proposalVersionField.setReadOnly(true);
        formLayoutLeft.addComponent(proposalVersionField);

        return formLayoutLeft;
    }

    private User getCurrentUser() {
        return (User) VaadinSession.getCurrent().getAttribute(
                User.class.getName());
    }


    private Component buildItemDetails() {

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

