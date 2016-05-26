package com.mygubbi.game.dashboard.view.proposals;


import com.google.common.eventbus.Subscribe;
import com.mygubbi.game.dashboard.ServerManager;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.domain.Product;
import com.mygubbi.game.dashboard.domain.Proposal;
import com.mygubbi.game.dashboard.domain.ProposalHeader;
import com.mygubbi.game.dashboard.domain.User;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.mygubbi.game.dashboard.event.ProposalEvent;
import com.mygubbi.game.dashboard.view.DashboardViewType;
import com.mygubbi.game.dashboard.view.NotificationUtil;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.commons.lang.StringUtils;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.gridutil.renderer.EditButtonValueRenderer;

import java.util.List;

import static com.mygubbi.game.dashboard.domain.Product.TYPE;
import static com.mygubbi.game.dashboard.domain.ProposalHeader.*;


/**
 * Created by test on 31-03-2016.
 */
public class CreateProposalsView extends Panel implements View {


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

    private Field<?> salesPerson;
    private Field<?> salesEmail;
    private Field<?> salesContact;
    private Field<?> designPerson;
    private Field<?> designEmail;
    private Field<?> designContact;

    private Grid productsGrid;
    private Label proposalTitleLabel;
    private final BeanFieldGroup<ProposalHeader> binder = new BeanFieldGroup<>(ProposalHeader.class);
    private Button submitButton;
    private Label draftLabel;
    private final ProposalHeader proposalHeader;
    private final Proposal proposal;
    private Button saveButton;
    private BeanItemContainer productContainer;

    public CreateProposalsView() {

        DashboardEventBus.register(this);
        proposalHeader = proposalDataProvider.createProposal();
        proposal = new Proposal();
        proposal.setProposalHeader(proposalHeader);
        initHeader(proposalHeader);
        this.binder.setItemDataSource(proposalHeader);

        DashboardEventBus.post(new ProposalEvent.ProposalUpdated());
        setSizeFull();

        VerticalLayout vLayout = new VerticalLayout();
        vLayout.addComponent(buildHeader());

        TabSheet tabs = new TabSheet();
        tabs.addTab(buildForm(), "Header");
        tabs.addTab(buildProductDetails(), "Products");
        tabs.addTab(buildAttachmentsTab(), "Attachments");

/*
        tabs.addSelectedTabChangeListener(selectedTabChangeEvent -> {
            TabSheet tabsheet = selectedTabChangeEvent.getTabSheet();
            Layout tab = (Layout) tabsheet.getSelectedTab();
            String caption = tabsheet.getTab(tab).getCaption();
            if (caption.equals("Header")) {
                saveButton.setVisible(true);
            } else {
                saveButton.setVisible(false);
            }
        });
*/

        vLayout.addComponent(tabs);
        setContent(vLayout);
        Responsive.makeResponsive(tabs);

    }

    private Component buildAttachmentsTab() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        return verticalLayout;
    }

    private BeanItem<ProposalHeader> getProposalHeaderBeanItem(ProposalHeader proposalHeader) {
        BeanItem<ProposalHeader> proposalHeaderBeanItem = new BeanItem<>(proposalHeader);
        return proposalHeaderBeanItem;
    }

    private void initHeader(ProposalHeader proposalHeader) {
        proposalHeader.setTitle("New Proposal");
        proposalHeader.setVersion("1.0");
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
        submitButton.setVisible(!"active".equals(proposalHeader.getStatus()));
        submitButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        submitButton.addStyleName(ValoTheme.BUTTON_SMALL);
        submitButton.addClickListener(this::submit);
        horizontalLayout1.addComponent(submitButton);
        horizontalLayout1.setComponentAlignment(submitButton, Alignment.MIDDLE_RIGHT);

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

    private void close(Button.ClickEvent clickEvent) {

        ConfirmDialog.show(UI.getCurrent(), "", "Please Confirm:\n\n - On 'Save & Close' data will be saved before closing\n - On 'Close' unsaved data will be lost",
                "Save & Close", "Cancel", "Close", dialog -> {
                    if (!dialog.isCanceled()) {
                        if (dialog.isConfirmed()) {
                            boolean success = proposalDataProvider.saveProposal(proposalHeader);
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
            proposalHeader.setStatus("active");
            boolean success = proposalDataProvider.saveProposal(proposalHeader);
            if (success) {
                success = proposalDataProvider.submitProposal(proposalHeader.getId());
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

        designPerson = binder.buildAndBind("Design Person", DESIGNER_NAME);
        designPerson.setRequired(true);
        ((TextField) designPerson).setNullRepresentation("");
        formLayoutRight.addComponent(designPerson);
        designEmail = binder.buildAndBind("Email", DESIGNER_EMAIL);
        ((TextField) designEmail).setNullRepresentation("");
        formLayoutRight.addComponent(designEmail);
        designContact = binder.buildAndBind("Phone", DESIGNER_PHONE);
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

        salesPerson = binder.buildAndBind("Sales Person", SALES_NAME);
        ((TextField) salesPerson).setNullRepresentation("");
        salesPerson.setRequired(true);
        formLayoutLeft.addComponent(salesPerson);
        salesEmail = binder.buildAndBind("Email", SALES_EMAIL);
        ((TextField) salesEmail).setNullRepresentation("");
        formLayoutLeft.addComponent(salesEmail);
        salesContact = binder.buildAndBind("Phone", SALES_PHONE);
        ((TextField) salesContact).setNullRepresentation("");
        formLayoutLeft.addComponent(salesContact);
        return formLayoutLeft;
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
            proposalTitleLabel.setValue(changedText + "&nbsp;");
        });

        formLayoutLeft.addComponent(proposalTitleField);
        proposalVersionField = binder.buildAndBind("Proposal Version", VERSION);
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
                CustomizedProductDetailsWindow.open(CreateProposalsView.this.proposalHeader)
        );

        standardItemBtn.addClickListener(clickEvent ->
                CatalogItemDetailsWindow.open(CreateProposalsView.this.proposalHeader)
        );

        productContainer = new BeanItemContainer<>(Product.class);
        GeneratedPropertyContainer genContainer = new GeneratedPropertyContainer(productContainer);
        genContainer.addGeneratedProperty("actions", getActionTextGenerator());

        productsGrid = new Grid(genContainer);
        productsGrid.setSizeFull();
        productsGrid.setColumnReorderingAllowed(true);
        productsGrid.setColumns(Product.SEQ, Product.ROOM, Product.TITLE, Product.PRODUCT_CATEGORY, Product.QTY, Product.AMOUNT, TYPE, "actions");

        List<Grid.Column> columns = productsGrid.getColumns();
        int idx = 0;

        columns.get(idx++).setHeaderCaption("Seq");
        columns.get(idx++).setHeaderCaption("Room");
        columns.get(idx++).setHeaderCaption("Title");
        columns.get(idx++).setHeaderCaption("Category");
        columns.get(idx++).setHeaderCaption("Qty");
        columns.get(idx++).setHeaderCaption("Amount");
        columns.get(idx++).setHeaderCaption("Type");
        columns.get(idx++).setHeaderCaption("Actions").setRenderer(new EditButtonValueRenderer(rendererClickEvent -> {
        }));

        verticalLayout.addComponent(productsGrid);

        return verticalLayout;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

    }

    private PropertyValueGenerator<String> getActionTextGenerator() {
        return new PropertyValueGenerator<String>() {

            @Override
            public String getValue(Item item, Object o, Object o1) {
                return "Edit";
            }

            @Override
            public Class<String> getType() {
                return String.class;
            }
        };
    }


    @Subscribe
    public void productCreated(final ProposalEvent.ProductCreatedEvent event) {
        List<Product> products = proposal.getProducts();
        boolean removed = products.remove(event.getProduct());
        products.add(event.getProduct());
        productContainer.removeAllItems();
        productContainer.addAll(products);
        productsGrid.sort(Product.SEQ, SortDirection.ASCENDING);
        //updateTotalAmount();
    }
}

