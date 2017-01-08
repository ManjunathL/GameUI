package com.mygubbi.game.dashboard.view.proposals;


import com.google.common.eventbus.Subscribe;
import com.mygubbi.game.dashboard.ServerManager;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.domain.JsonPojo.LookupItem;
import com.mygubbi.game.dashboard.domain.*;
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
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Responsive;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.ClickableRenderer;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vaadin.gridutil.renderer.ViewEditButtonValueRenderer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.mygubbi.game.dashboard.domain.ProposalHeader.*;


/**
 * Created by test on 31-03-2016.
 */
public class CreateProposalsView extends Panel implements View {

    private static final Logger LOG = LogManager.getLogger(CreateProposalsView.class);

    private final String NEW_TITLE = "New Quotation";
    private final String NEW_DRAFT_TITLE = "Draft created for ";
    private final String NEW_VERSION = "1.0";
    private String QuoteNum=null;
    private String QuoteNumNew=null;
    private String status=null;
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
    private Field<?> quote;
    private TextField quotenew;

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
    private ComboBox designPartner;
    private Field<?> designPartnerEmail;
    private Field<?> designPartnerContact;

    private Grid productsGrid;
    private Label proposalTitleLabel;
    private final BeanFieldGroup<ProposalHeader> binder = new BeanFieldGroup<>(ProposalHeader.class);
    private Button submitButton;
    private Label draftLabel;
    private ProposalHeader proposalHeader;
    private ProposalVersion proposalVersion;
    private Proposal proposal;
    private Button saveButton;
    private Button cancelButton;
    private ProposalVersion getLatestVersionDetails;
    private BeanItemContainer productContainer;
    private Label grandTotal;
    String cityCode= "";
    String cityStatus= "";
    int month;


    private ProductAndAddonSelection productAndAddonSelection;

    private FileAttachmentComponent fileAttachmentComponent;
    private BeanItemContainer versionContainer;

    private Grid versionsGrid;

    int pid;
    String parameters;
    List<ProposalCity> proposalCityData;

    public CreateProposalsView() {
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        UI.getCurrent().getNavigator().addViewChangeListener(new ViewChangeListener() {

            @Override
            public boolean beforeViewChange(ViewChangeEvent viewChangeEvent) {
                try
                {
                    binder.commit();
                }
                catch (FieldGroup.CommitException e)
                {
                    if (proposalHeader.getQuoteNoNew() == null) {
                        proposalDataProvider.deleteProposal(proposalHeader.getId());
                    }
                    return true;
                }
                return true;
            }

            @Override
            public void afterViewChange(ViewChangeEvent viewChangeEvent) {

            }
        });
        parameters = event.getParameters();
        if (StringUtils.isNotEmpty(parameters)) {
            pid = Integer.parseInt(parameters);
            this.proposalHeader = proposalDataProvider.getProposalHeader(pid);
            this.proposal = new Proposal();
            this.proposal.setProposalHeader(this.proposalHeader);
            this.proposal.setFileAttachments(proposalDataProvider.getProposalDocuments(pid));
            proposalHeader.setEditFlag(EDIT.W.name());
            //todo: this has to be removed once server side is fixed
        } else {

            proposalHeader = proposalDataProvider.createProposal();
            proposalHeader.setTitle(NEW_TITLE);
            proposalHeader.setVersion(NEW_VERSION);
            proposalHeader.setEditFlag(EDIT.W.name());
            proposalHeader.setStatus(ProposalState.Draft.name());
            QuoteNum="";
            proposalHeader.setQuoteNo(QuoteNum);
            /*List<ProposalHeader> id=proposalDataProvider.getProposalId();
            for(ProposalHeader val: id) {
                pid=val.getId();
            }

            String date=new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            QuoteNum=date+"-"+pid ;*/
           /* proposalHeader.setQuoteNo(QuoteNum);*/
            this.proposal = new Proposal();
            this.proposal.setProposalHeader(proposalHeader);

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            proposalVersion = proposalDataProvider.createDraft(proposalHeader.getId(), NEW_DRAFT_TITLE + proposalHeader.getId(),dateFormat.format(date));

        }

       // quotationField.setValue(String.valueOf(pid));
        this.productAndAddonSelection = new ProductAndAddonSelection();
        this.productAndAddonSelection.setProposalId(this.proposalHeader.getId());

        DashboardEventBus.register(this);
        this.binder.setItemDataSource(proposalHeader);

        //proposalCityData = proposalDataProvider.getCityData(proposalHeader.getId());

        setWidth("100%");
        setHeight("100%");

        VerticalLayout vLayout = new VerticalLayout();
        vLayout.addComponent(buildHeader());

        TabSheet tabs = new TabSheet();
        tabs.addTab(buildForm(), "Header");
        tabs.addTab(buildVersionsGrids(), "Quotation Version");
//      tabs.addTab(buildProductsAndAddonsPage(), "Products and Addons");
        fileAttachmentComponent = new FileAttachmentComponent(proposal, proposalHeader.getFolderPath(),
                attachmentData -> proposalDataProvider.addProposalDoc(proposalHeader.getId(), attachmentData.getFileAttachment()),
                attachmentData -> proposalDataProvider.removeProposalDoc(attachmentData.getFileAttachment().getId()),
                !proposalHeader.getStatus().equals(ProposalState.Draft.name())
        );
        tabs.addTab(fileAttachmentComponent, "Attachments");

        vLayout.addComponent(tabs);
        setContent(vLayout);
        Responsive.makeResponsive(tabs);
        cityLockedForOldProposal();
    }

    private void cityLockedForSave()
    {
        List<ProposalCity> proposalCityList = proposalDataProvider.checkCity(proposalHeader.getId());
        for (ProposalCity proposalCity : proposalCityList)
        {
          cityStatus = proposalCity.getCityLocked();
        }
        if (("Yes").contains(cityStatus))
        {
           quotenew.setReadOnly(true);
            projectCityField.setReadOnly(true);
        }
    }

    private void cityLockedForOldProposal()
    {
        if (!(proposalHeader.getQuoteNoNew() == null))
        {
            quotenew.setReadOnly(true);
            projectCityField.setReadOnly(true);
            cancelButton.setVisible(false);
        }
    }

    private Component buildVersionsGrids() {

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        verticalLayout.setMargin(new MarginInfo(true, true, true, true));

        Label title = new Label("Version Details");
        title.setStyleName("products-and-addons-label-text");
        verticalLayout.addComponent(title);
        verticalLayout.setComponentAlignment(title,Alignment.TOP_LEFT);


        verticalLayout.setSpacing(true);

        versionContainer = new BeanItemContainer<>(ProposalVersion.class);

        List<ProposalVersion> proposalVersionList = proposalDataProvider.getProposalVersions(proposalHeader.getId());
        this.proposal.setVersions(proposalVersionList);

        LOG.debug("Proposal Version List :" + proposalVersionList.size());

        GeneratedPropertyContainer genContainer = createGeneratedVersionPropertyContainer();
        versionsGrid = new Grid(genContainer);
        versionsGrid.setSizeFull();
        versionsGrid.setColumns(ProposalVersion.VERSION, ProposalVersion.FROM_VERSION, ProposalVersion.TITLE, ProposalVersion.FINAL_AMOUNT, ProposalVersion.STATUS, ProposalVersion.DATE,
                ProposalVersion.REMARKS,"actions");


        versionContainer.addAll(proposalVersionList);
        versionsGrid.setContainerDataSource(createGeneratedVersionPropertyContainer());
        versionsGrid.getSelectionModel().reset();
        versionsGrid.sort(ProposalVersion.VERSION, SortDirection.ASCENDING);


        List<Grid.Column> columns = versionsGrid.getColumns();
        int idx = 0;
        columns.get(idx++).setHeaderCaption("Version #");
        columns.get(idx++).setHeaderCaption("From Version #");
        columns.get(idx++).setHeaderCaption("Title");
        columns.get(idx++).setHeaderCaption("Final Amount");
        columns.get(idx++).setHeaderCaption("Status");
        columns.get(idx++).setHeaderCaption("Date");
        columns.get(idx++).setHeaderCaption("Remarks");
        columns.get(idx++).setHeaderCaption("Actions").setRenderer(new ViewEditButtonValueRenderer(new ViewEditButtonValueRenderer.ViewEditButtonClickListener() {

            @Override
            public void onView(ClickableRenderer.RendererClickEvent rendererClickEvent) {

                ProposalVersion pVersion = (ProposalVersion)rendererClickEvent.getItemId();
                LOG.debug("Proposal version to be copied :" + pVersion.toString());

                if ((0.0) == pVersion.getFinalAmount())
                {
                    Notification.show("Please add products before copying");
                    return;
                }

                if (("Locked").equals(pVersion.getInternalStatus()))
                {
                    Notification.show("Cannot copy on Locked version");
                    return;
                }
                ProposalVersion copyVersion = new ProposalVersion();
                Float versionNew = Float.valueOf(pVersion.getVersion());

                if (pVersion.getVersion().startsWith("0."))
                {
                    LOG.debug("ID : " + proposalHeader.getId());
                    List<ProposalVersion> proposalVersionPreSales = proposalDataProvider.getProposalVersionPreSales(proposalHeader.getId());
                    int size = proposalVersionPreSales.size();
                    if (size == 9)
                    {
                        NotificationUtil.showNotification("Cannot exceed more than 9 versions",NotificationUtil.STYLE_BAR_ERROR_SMALL);
                        return;
                    }
                    String str= ("0."+(size+1));
                    versionNew = Float.valueOf(str);
                }
                else if (pVersion.getVersion().startsWith("1."))
                {
                    List<ProposalVersion> proposalVersionPostSales = proposalDataProvider.getProposalVersionPostSales(proposalHeader.getId());
                    int size = proposalVersionPostSales.size();
                    if (size == 9)
                    {
                        NotificationUtil.showNotification("Cannot exceed more than 9 versions", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                        return;
                    }
                    String str= ("1."+(size));
                    versionNew = Float.valueOf(str);

                }
                else if (pVersion.getVersion().startsWith("2."))
                {
                    List<ProposalVersion> proposalVersionProduction = proposalDataProvider.getProposalVersionProduction(proposalHeader.getId());
                    int size = proposalVersionProduction.size();
                    if (size == 9)
                    {
                        NotificationUtil.showNotification("Cannot exceed more than 9 versions",NotificationUtil.STYLE_BAR_ERROR_SMALL);
                        return;
                    }
                    String str= ("2."+(size));
                    versionNew = Float.valueOf(str);
                }

                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();

                copyVersion.setVersion(String.valueOf(versionNew));
                copyVersion.setFromVersion(pVersion.getVersion());
                copyVersion.setProposalId(pVersion.getProposalId());
                copyVersion.setTitle(pVersion.getTitle());
                copyVersion.setFinalAmount(pVersion.getFinalAmount());
                copyVersion.setDate(dateFormat.format(date));
                copyVersion.setStatus(ProposalVersion.ProposalStage.Draft.name());
                copyVersion.setInternalStatus(ProposalVersion.ProposalStage.Draft.name());
                copyVersion.setRemarks(pVersion.getRemarks());
                copyVersion.setToVersion(String.valueOf(versionNew));
                copyVersion.setDiscountAmount(pVersion.getDiscountAmount());
                copyVersion.setDiscountPercentage(pVersion.getDiscountPercentage());
                copyVersion.setAmount(pVersion.getAmount());


                proposalDataProvider.createProposalVersion(copyVersion);
                proposalDataProvider.createNewProduct(copyVersion);
                DashboardEventBus.post(new ProposalEvent.VersionCreated(copyVersion));
                DashboardEventBus.unregister(this);
            }

            @Override
            public void onEdit(ClickableRenderer.RendererClickEvent rendererClickEvent)
            {
                try
                {
                    binder.commit();
                }
                catch (FieldGroup.CommitException e)
                {
                    NotificationUtil.showNotification("Validation Error, please fill all mandatory fields in header tab", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                    return;
                }
                ProposalVersion proposalVersion = (ProposalVersion) rendererClickEvent.getItemId();
                ProductAndAddons.open(proposalHeader,proposal,proposalVersion.getVersion(),proposalVersion);
            }
        }));

        versionContainer.addAll(proposalVersionList);
        versionsGrid.setContainerDataSource(createGeneratedVersionPropertyContainer());
        versionsGrid.getSelectionModel().reset();
        verticalLayout.addComponent(versionsGrid);
        verticalLayout.setSpacing(true);
        return verticalLayout;
    }

    private void refreshVersionsGrid(List<ProposalVersion> updatedProposalVersions) {
        versionContainer.addAll(updatedProposalVersions);

        versionsGrid.setContainerDataSource(createGeneratedVersionPropertyContainer());
        versionsGrid.getSelectionModel().reset();
    }




    private GeneratedPropertyContainer createGeneratedVersionPropertyContainer() {
        GeneratedPropertyContainer genContainer = new GeneratedPropertyContainer(versionContainer);
        genContainer.addGeneratedProperty("actions", getEmptyActionTextGenerator());
        //genContainer.addGeneratedProperty("CNC", getEmptyActionTextGenerator());
        //genContainer.addGeneratedProperty("Copy", getEmptyActionTextGenerator());
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

      /*  draftLabel = new Label("[ " + proposalHeader.getStatus() + " ]");
        draftLabel.addStyleName(ValoTheme.LABEL_COLORED);
        draftLabel.addStyleName(ValoTheme.LABEL_H2);
        draftLabel.setWidth("1%");
        left.addComponent(draftLabel);*/

        horizontalLayout.addComponent(left);
        horizontalLayout.setComponentAlignment(left, Alignment.MIDDLE_LEFT);
        horizontalLayout.setExpandRatio(left, 3);

        HorizontalLayout right = new HorizontalLayout();

        saveButton = new Button("Save");
        saveButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        saveButton.addStyleName("margin-right-10-for-headerlevelbutton");
        saveButton.addClickListener(this::save);
        right.addComponent(saveButton);
        right.setComponentAlignment(saveButton, Alignment.MIDDLE_RIGHT);

        cancelButton = new Button("Cancel");
        cancelButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        cancelButton.addStyleName("margin-right-10-for-headerlevelbutton");
        cancelButton.addClickListener(this::cancel);
        right.addComponent(cancelButton);
        right.setComponentAlignment(cancelButton, Alignment.MIDDLE_RIGHT);

        horizontalLayout.addComponent(right);
        horizontalLayout.setExpandRatio(right, 7);
        horizontalLayout.setComponentAlignment(right, Alignment.MIDDLE_RIGHT);

        return horizontalLayout;
    }




    private String getFormattedTitle(String title) {
        if (title.length() <= 30) {
            return title;
        } else {
            return title.substring(0, 30) + "...";
        }

    }


    private void save(Button.ClickEvent clickEvent)
    {

        if (StringUtils.isEmpty(proposalHeader.getTitle()))
        {
            proposalHeader.setTitle(NEW_TITLE);
        }
        try {
            binder.commit();
        } catch (FieldGroup.CommitException e) {
            NotificationUtil.showNotification("Validation Error, please fill all mandatory fields!", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            return;
        }
        proposalVersion = new ProposalVersion();
        List<ProposalVersion> proposalVersionLatest = proposalDataProvider.getLatestVersion(proposalHeader.getId());
        for (ProposalVersion getLatestVersion : proposalVersionLatest)
        {
            proposalHeader.setStatus(getLatestVersion.getStatus());
            proposalHeader.setVersion(getLatestVersion.getVersion());
        }

       try {

           List<ProposalCity> insertCity=proposalDataProvider.getCityDataTest(proposalHeader.getId());
           LOG.info("insert city" + insertCity);
           if(!(insertCity.size() >= 1)) {
               proposalDataProvider.createCity(cityCode, month, proposalHeader.getId(), quotenew.getValue());
               LOG.info("success");
           }
       }
       catch(Exception e){
            LOG.info(e);
       }


        boolean success = proposalDataProvider.saveProposal(proposalHeader);

        if (success) {
            NotificationUtil.showNotification("Saved successfully!", NotificationUtil.STYLE_BAR_SUCCESS_SMALL);
            cityLockedForSave();
        } else {
            NotificationUtil.showNotification("Couldn't Save Proposal! Please contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
        }

    }


    private void cancel(Button.ClickEvent clickEvent)
    {
        try {
            if (StringUtils.isEmpty(proposalHeader.getTitle()) || StringUtils.isEmpty(proposalHeader.getCrmId())  || StringUtils.isEmpty(proposalHeader.getCustomerId()) || StringUtils.isEmpty(proposalHeader.getCname()) || StringUtils.isEmpty(proposalHeader.getQuoteNo()))
            {
                List<Product> listOfProducts = proposalDataProvider.getProposalProducts(proposalHeader.getId());
                if (!(listOfProducts.size() == 0))
                {
                    NotificationUtil.showNotification("Cannot cancel proposal after adding products!", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                }
                else {
                        boolean success = proposalDataProvider.deleteProposal(proposalHeader.getId());

                        if (!success) {
                            DashboardEventBus.unregister(this);
                            UI.getCurrent().getNavigator().navigateTo(DashboardViewType.PROPOSALS.name());
                        } else {
                            NotificationUtil.showNotification("Couldn't cancel Proposal! Please contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                        }
                }
            }
            else
            {
                NotificationUtil.showNotification("Couldn't Cancel Proposal! Please contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            }
        } catch (Exception e) {
            NotificationUtil.showNotification("Couldn't Cancel Proposal! Please contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
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
        horizontalLayout3.addComponent(buildContactDetailsCenter());
/*
        horizontalLayout3.addComponent(buildContactDetailsRight());
*/
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
        /*projectCityField = getCityCombo();
        binder.bind(projectCityField, P_CITY);
        projectCityField.setRequired(true);
        formLayoutRight.addComponent(projectCityField);*/

        return formLayoutRight;

    }

    private FormLayout buildContactDetailsCenter() {
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

    private FormLayout buildContactDetailsRight() {
        FormLayout formLayoutRight = new FormLayout();
        formLayoutRight.setSizeFull();
        formLayoutRight.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        Label mygubbiDetails = new Label("");
        mygubbiDetails.addStyleName(ValoTheme.LABEL_HUGE);
        mygubbiDetails.addStyleName(ValoTheme.LABEL_COLORED);
        formLayoutRight.addComponent(mygubbiDetails);

        designPartner = getDesignPartnerPersonCombo();
        binder.bind(designPartner, DESIGN_PARTNER_NAME);
        designPartner.setRequired(false);
        formLayoutRight.addComponent(designPartner);
        designPartnerEmail = binder.buildAndBind("Email", DESIGN_PARTNER_EMAIL);
        ((TextField) designPartnerEmail).setNullRepresentation("");
        designPartnerEmail.setRequired(false);
        formLayoutRight.addComponent(designPartnerEmail);
        designPartnerContact = binder.buildAndBind("Phone", DESIGN_PARTNER_PHONE);
        ((TextField) designPartnerContact).setNullRepresentation("");
        designPartnerContact.setRequired(false);
        designPartner.addValueChangeListener(this::designerChanged);
        formLayoutRight.addComponent(designPartnerContact);
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

    private void designPartnerChanged(Property.ValueChangeEvent valueChangeEvent)
    {
        if (designPartner == null)
        {
            ((TextField) designContact).setValue(null);
            ((TextField) designEmail).setValue(null);
        }
        String phone = (String) designPartner.getItem(designPartner.getValue()).getItemProperty(User.PHONE).getValue();
        ((TextField) designContact).setValue(phone);
        String email = (String) designPartner.getItem(designPartner.getValue()).getItemProperty(User.EMAIL).getValue();
        ((TextField) designEmail).setValue(email);
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

        ComboBox select = new ComboBox("Project City");
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

    private ComboBox getDesignPartnerPersonCombo() {
        List<User> list = proposalDataProvider.getDesignPartnerUsers();
        final BeanContainer<String, User> container =
                new BeanContainer<>(User.class);
        container.setBeanIdProperty(User.NAME);
        container.addAll(list);

        ComboBox select = new ComboBox("Design Partner");
        select.setWidth("300px");
        select.setNullSelectionAllowed(true);
        select.setImmediate(true);
        select.setContainerDataSource(container);
        select.setItemCaptionPropertyId(User.NAME);
        select.addValueChangeListener(this::designPartnerChanged);

     /*   if (StringUtils.isNotEmpty(proposalHeader.getDesignerName())) {
            select.setValue(proposalHeader.getDesignerName());
        } else if (container.size() > 0) {
            select.setValue(select.getItemIds().iterator().next());
            proposalHeader.setDesignerName((String) select.getValue());
            proposalHeader.setDesignerPhone(select.getItem(select.getValue()).getItemProperty(User.PHONE).getValue().toString());
            proposalHeader.setDesignerEmail(select.getItem(select.getValue()).getItemProperty(User.EMAIL).getValue().toString());
        }*/

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



        quotenew = new TextField("Quotation #");
        quotenew.setValue(proposalHeader.getQuoteNo());
        quotenew.setRequired(true);
        quotenew.setValue(proposalHeader.getQuoteNoNew());
        quotenew.setNullRepresentation("");
        formLayoutRight.addComponent(quotenew);

        quote = binder.buildAndBind("Quotation # (Old)", QUOTE_NO);
        ((TextField) quote).setNullRepresentation("");
        /*quote.setRequired(true);*/
        quote.setReadOnly(true);
        formLayoutRight.addComponent(quote);

        return formLayoutRight;
    }

    private FormLayout buildMainFormLayoutLeft() {

        FormLayout formLayoutLeft = new FormLayout();
        formLayoutLeft.setSizeFull();
        formLayoutLeft.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        proposalTitleField = binder.buildAndBind("Quotation Title", ProposalHeader.TITLE);
        proposalTitleField.setRequired(true);
        ((TextField) proposalTitleField).setNullRepresentation("");

       /* ((TextField) proposalTitleField).addTextChangeListener(textChangeEvent -> {
            String changedText = textChangeEvent.getText();
            proposalTitleLabel.setValue(getFormattedTitle(changedText) + "&nbsp;");
            proposalTitleLabel.setDescription(changedText);
        });
*/
        formLayoutLeft.addComponent(proposalTitleField);
        proposalVersionField = binder.buildAndBind("Quotation Version", VERSION);
        proposalVersionField.setReadOnly(true);
        /*formLayoutLeft.addComponent(proposalVersionField);*/
        projectCityField = getCityCombo();
        binder.bind(projectCityField, P_CITY);
        projectCityField.setRequired(true);
        formLayoutLeft.addComponent(projectCityField);
        projectCityField.addValueChangeListener(this :: cityChanged);


        return formLayoutLeft;
    }

    private void cityChanged(Property.ValueChangeEvent valueChangeEvent) {
        String city = (String) projectCityField.getValue();
        switch(city){
            case "Bangalore": cityCode= "BLR";
                break;
            case "Chennai": cityCode= "CHN";
                break;
            case "Mangalore": cityCode= "MLR";
                break;
            case "Pune": cityCode= "PUN";
                break;
        }

        LocalDate today = LocalDate.now();
        month = today.getMonthValue();
        LOG.info("month" + month);


        LOG.info("city code"+ cityCode);
        int value=0;
        List<ProposalCity> count=proposalDataProvider.getMonthCount(month,cityCode);
           value=count.size();
        LOG.info("month count"+ value);


        String valueStr;
        valueStr=Integer.toString(value);
        valueStr=String.format("%04d",value+1);
        String date=new SimpleDateFormat("yyyy-MM").format(new Date());
        QuoteNumNew= cityCode + "-" + date+"-"+ valueStr;
        proposalHeader.setQuoteNoNew(QuoteNumNew);
        LOG.info("quote no"+ QuoteNumNew);
        quotenew.setValue(QuoteNumNew);
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

/*
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
    }*/

    @Subscribe
    public void versionCreated(final ProposalEvent.VersionCreated event) {
        List<ProposalVersion> proposalVersionList = proposalDataProvider.getProposalVersions(proposalHeader.getId());
        for (ProposalVersion proposalVersionTest : proposalVersionList)
        {
            LOG.debug("proposal Version list : " + proposalVersionTest.toString());
        }
        proposalVersionList.remove(event.getProposalVersion());
        versionContainer.removeAllItems();
        proposalVersionList.add(event.getProposalVersion());
        refreshVersionsGrid(proposalVersionList);

    }

   /* @Subscribe
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
*/
    private double round(double value, int places)
    {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}

