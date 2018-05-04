    package com.mygubbi.game.dashboard.view.proposals;


import com.google.common.eventbus.Subscribe;
import com.mygubbi.game.dashboard.ServerManager;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.domain.JsonPojo.AccessoryDetails;
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
import com.vaadin.server.*;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.ClickableRenderer;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vaadin.gridutil.renderer.ViewButtonValueRenderer;
import org.vaadin.gridutil.renderer.ViewEditButtonValueRenderer;
import us.monoid.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Calendar;
import java.util.stream.Collectors;

import static com.mygubbi.game.dashboard.domain.ProposalHeader.*;


/**
 * Created by test on 31-03-2016.
 */
public class CreateProposalsView extends Panel implements View {

    private static final Logger LOG = LogManager.getLogger(CreateProposalsView.class);

    String role = ((User) VaadinSession.getCurrent().getAttribute(User.class.getName())).getRole();

    private final String NEW_TITLE = "New Quotation";
    private final String NEW_DRAFT_TITLE = "Provide Option Description";
    private final String NEW_VERSION = "1.0";
    private final Integer MIN_WORK_COMPLETION_DAYS = 60;
    private String QuoteNum=null;
    private String QuoteNumNew=null;
    private String status=null;
    private ProposalDataProvider proposalDataProvider = ServerManager.getInstance().getProposalDataProvider();

    private Field<?> proposalTitleField;
    private TextField maxDiscountPercentage;
    private Field<?> noOfDaysForWorkCompletion;
    private Field<?> crmId;
    private Field<?> proposalVersionField;
    private Field<?> quotationField;

   // private Field<?> customerIdField;
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
    private ComboBox offerField;
    private ComboBox bookingFormField;

    private ComboBox salesPerson;
    private Field<?> salesEmail;
    private Field<?> salesContact;
    private ComboBox designPerson;
    private Field<?> designEmail;
    private Field<?> designContact;
    private ComboBox designPartner;
    private Field<?> designPartnerEmail;
    private Field<?> designPartnerContact;

    Button searchcrmid;
    DateField expectedDeliveryDate;

    private Label proposalTitleLabel;
    private final BeanFieldGroup<ProposalHeader> binder = new BeanFieldGroup<>(ProposalHeader.class);
    private ProposalHeader proposalHeader;
    private ProposalVersion proposalVersion;
    private Proposal proposal;
    private Button saveButton;
    private Button saveAndCloseButton;
    private Button cancelButton;
    String cityCode= "";
    String cityStatus= "";
    int month;
    java.sql.Date priceDate;
    String codeForDiscount;
    Double rateForDiscount;
    String codeForDefDaysFromWorkCompletion;
    Integer defDaysFromWorkCompletion;

    private ProductAndAddonSelection productAndAddonSelection;

    private FileAttachmentComponent fileAttachmentComponent;
    private BeanItemContainer versionContainer;
    String viewOnlyValue;
    private Grid versionsGrid;

    int pid;
    String parameters;
    CheckBox PHCcheck,DCCcheck,FPCcheck;
    Boolean dateComparision;
    String selectedMonth,selectedYear,previousMonth,previousYear,BookingMonth,BookingYear;
    public CreateProposalsView() {
    }
    public static String theMonth(int month){
        String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return monthNames[month];
    }
    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        parameters = event.getParameters();
        if (StringUtils.isNotEmpty(parameters)) {
            pid = Integer.parseInt(parameters);
            this.proposalHeader = proposalDataProvider.getProposalHeader(pid);
            this.proposal = new Proposal();
            this.proposal.setProposalHeader(this.proposalHeader);
            this.proposal.setFileAttachments(proposalDataProvider.getProposalDocuments(pid));
            this.proposalHeader.setEditFlag(EDIT.W.name());
            //todo: this has to be removed once server side is fixed
        } else {
            this.proposalHeader = proposalDataProvider.createProposal();
            this.proposalHeader.setVersion(NEW_VERSION);
            this.proposalHeader.setEditFlag(EDIT.W.name());
            this.proposalHeader.setStatus(ProposalState.Deleted.name());
            QuoteNum = "";
            this.proposalHeader.setQuoteNo(QuoteNum);
            DashboardEventBus.post(new ProposalEvent.DashboardMenuUpdated(true));

                this.proposal = new Proposal();
                this.proposal.setProposalHeader(this.proposalHeader);

                proposalVersion = proposalDataProvider.createDraft(this.proposalHeader.getId(), NEW_DRAFT_TITLE);
                if (this.proposalHeader.getPriceDate() == null)
                {
                    this.priceDate = new java.sql.Date(System.currentTimeMillis());
                }
                else {
                    this.priceDate = this.proposalHeader.getPriceDate();
                }
                proposalVersion.setBusinessDate(this.getCurrentDate());
                List<PriceMaster> priceMaster=proposalDataProvider.getDiscountAmount(String.valueOf(priceDate),String.valueOf(priceDate));
                for(PriceMaster p: priceMaster)
                {
                    PriceMaster nstdmanfcostpriceMaster=proposalDataProvider.getFactorRatePriceDetails("VDP",this.priceDate,"all");
                    proposalVersion.setDiscountPercentage(nstdmanfcostpriceMaster.getSourcePrice());
                }
                proposalDataProvider.updateDiscount(String.valueOf(proposalVersion.getDiscountPercentage()),String.valueOf(proposalVersion.getProposalId()),"0.1",proposalVersion.getBusinessDate());
        }

            // quotationField.setValue(String.valueOf(pid));
            this.productAndAddonSelection = new ProductAndAddonSelection();
            this.productAndAddonSelection.setProposalId(this.proposalHeader.getId());

            DashboardEventBus.register(this);
            this.binder.setItemDataSource(this.proposalHeader);

            //proposalCityData = proposalDataProvider.getCityData(proposalHeader.getId());

        String email = ((User) VaadinSession.getCurrent().getAttribute(User.class.getName())).getEmail();
        List<User> userList=proposalDataProvider.getUsersViewOnlyAcess(email);
        for(User user:userList)
        {
            viewOnlyValue=user.getIsViewOnly();
        }
            setWidth("100%");
            setHeight("100%");

            VerticalLayout vLayout = new VerticalLayout();
            vLayout.addComponent(buildHeader());

            TabSheet tabs = new TabSheet();
            tabs.addTab(buildForm(), "Header");
            tabs.addTab(buildVersionsGrids(), "Quotation Version");
//      tabs.addTab(buildProductsAndAddonsPage(), "Products and Addons");
            fileAttachmentComponent = new FileAttachmentComponent(proposal, this.proposalHeader.getFolderPath(),
                    attachmentData -> proposalDataProvider.addProposalDoc(this.proposalHeader.getId(), attachmentData.getFileAttachment()),
                    attachmentData -> proposalDataProvider.removeProposalDoc(attachmentData.getFileAttachment().getId()),
                    true
            );
//            tabs.addTab(fileAttachmentComponent, "Attachments");
            tabs.addTab(buildScopeOfwork(), "Scope of Services");
        String role = ((User) VaadinSession.getCurrent().getAttribute(User.class.getName())).getRole();

        boolean DSO_flag = false;

                List<ProposalVersion> proposalVersions = proposal.getVersions();
        for (ProposalVersion proposalVersion : proposalVersions )
        {
            if (proposalVersion.getVersion().equals("3.0"))
            {
                DSO_flag = true;
            }
        }

        if ((role.equals("planning") || role.equals("admin")) && DSO_flag)
        {
            tabs.addTab(buildBoq(), "BOQ");
        }

            vLayout.addComponent(tabs);
            setContent(vLayout);
            Responsive.makeResponsive(tabs);
            cityLockedForOldProposal();
            if(viewOnlyValue.equalsIgnoreCase("Yes"))
            {
                setComponentsReadonly();
            }
    }

    private void setMaxDiscountPercentange() {
        String rateCardId = "F:" + proposalHeader.getOfferCode();
        LOG.debug("String ratecard:" + rateCardId);
        List<RateCard> discountratecode = proposalDataProvider.getFactorRateCodeDetails(rateCardId);
       // LOG.info("discount percentage details" +discountratecode);
        for (RateCard discountcode : discountratecode) {
            LOG.debug("Discount code : " + discountcode.getCode());
            codeForDiscount=discountcode.getCode();
        }
        if (this.proposalHeader.getPriceDate() == null)
        {
            this.priceDate = new java.sql.Date(System.currentTimeMillis());
        }
        else {
            this.priceDate = this.proposalHeader.getPriceDate();
        }
       // LOG.info("Price date value" +priceDate);
        PriceMaster discountpriceMaster=proposalDataProvider.getFactorRatePriceDetails(codeForDiscount,this.priceDate,this.proposalHeader.getPcity());
        rateForDiscount=discountpriceMaster.getSourcePrice();
        //LOG.info("Rate for discount" +rateForDiscount);
       // maxDiscountPercentage.setValue(String.valueOf(rateForDiscount));
        proposalHeader.setMaxDiscountPercentage(rateForDiscount);
    }

    private void cityLockedForSave() {
        List<ProposalCity> proposalCityList = proposalDataProvider.checkCity(this.proposalHeader.getId());
        for (ProposalCity proposalCity : proposalCityList) {
            cityStatus = proposalCity.getCityLocked();
        }
        if (("Yes").contains(cityStatus)) {
            quotenew.setReadOnly(true);
            projectCityField.setReadOnly(true);
            customerNameField.setReadOnly(true);
            searchcrmid.setEnabled(false);
            offerField.setReadOnly(true);
        }
    }

    private void cityLockedForOldProposal()
    {
        if (!(this.proposalHeader.getQuoteNoNew() == null))
        {
            if(Objects.equals(proposalHeader.getPcity(), "") || proposalHeader.getPcity() == null)
            {
                projectCityField.setReadOnly(false);
                offerField.setReadOnly(false);
                quotenew.setReadOnly(false);
                customerNameField.setReadOnly(true);
                cancelButton.setVisible(true);
                saveAndCloseButton.setVisible(false);
            }else
            {
                projectCityField.setReadOnly(true);
                quotenew.setReadOnly(true);
                customerNameField.setReadOnly(true);
                offerField.setReadOnly(true);
                searchcrmid.setEnabled(false);
                cancelButton.setVisible(false);
            }
        }
        else
        {
            saveAndCloseButton.setVisible(false);

        }
    }

    private Component buildVersionsGrids() {

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        verticalLayout.setMargin(new MarginInfo(true, true, true, true));

        Label title = new Label("Version Details");
        title.setStyleName("products-and-addons-label-text");
        verticalLayout.addComponent(title);
        verticalLayout.setComponentAlignment(title, Alignment.TOP_LEFT);


        verticalLayout.setSpacing(true);

        versionContainer = new BeanItemContainer<>(ProposalVersion.class);

        List<ProposalVersion> proposalVersionList = proposalDataProvider.getProposalVersions(this.proposalHeader.getId());
        this.proposal.setVersions(proposalVersionList);

        GeneratedPropertyContainer genContainer = createGeneratedVersionPropertyContainer();
        versionsGrid = new Grid(genContainer);
        versionsGrid.setSizeFull();
        versionsGrid.setColumns(ProposalVersion.VERSION, ProposalVersion.FROM_VERSION, ProposalVersion.TITLE, ProposalVersion.FINAL_AMOUNT, ProposalVersion.STATUS, ProposalVersion.DATE,ProposalVersion.UPDATED_ON,
                ProposalVersion.REMARKS, "actions","CNC");

        versionContainer.removeAllItems();

        LOG.debug("version container size"+ versionContainer.size());


        versionContainer.addAll(proposalVersionList);
        versionsGrid.setContainerDataSource(createGeneratedVersionPropertyContainer());
        versionsGrid.getSelectionModel().reset();
        versionsGrid.sort(ProposalVersion.VERSION, SortDirection.DESCENDING);


        List<Grid.Column> columns = versionsGrid.getColumns();
        int idx = 0;
        columns.get(idx++).setHeaderCaption("Version #");
        columns.get(idx++).setHeaderCaption("From Version #");
        columns.get(idx++).setHeaderCaption("Title");
        columns.get(idx++).setHeaderCaption("Final Amount");
        columns.get(idx++).setHeaderCaption("Status");
        columns.get(idx++).setHeaderCaption("Date");
        columns.get(idx++).setHeaderCaption("Updated On");
        columns.get(idx++).setHeaderCaption("Remarks");
        columns.get(idx++).setHeaderCaption("Actions").setRenderer(new ViewEditButtonValueRenderer(new ViewEditButtonValueRenderer.ViewEditButtonClickListener() {

            @Override
            public void onView(ClickableRenderer.RendererClickEvent rendererClickEvent) {

                ProposalVersion pVersion = (ProposalVersion) rendererClickEvent.getItemId();
                LOG.debug("Proposal version to be copied :" + pVersion.toString());

                String role = ((User) VaadinSession.getCurrent().getAttribute(User.class.getName())).getRole();
                if(Objects.equals(proposalHeader.getAdminPackageFlag(),"Yes") && !("admin").equals(role) )
                {
                    Notification.show("Cannot copy the version");
                    return;
                }
                if(viewOnlyValue.equals("Yes"))
                {
                    Notification.show("Cannot copy the version");
                    return;
                }
                if ((0.0) == pVersion.getFinalAmount()) {
                    Notification.show("Please add products before copying");
                    return;
                }

                if(("Deleted").equals(proposalHeader.getStatus())) {
                    NotificationUtil.showNotification("Validation Error, please save the quote before proceeding", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                    return;
                }

                if (("Locked").equals(pVersion.getInternalStatus())) {
                    Notification.show("Cannot copy on Locked version");
                    return;
                }
                ProposalVersion copyVersion = new ProposalVersion();
                Float versionNew = Float.valueOf(pVersion.getVersion());

                if (pVersion.getVersion().startsWith("0.")) {
                    List<ProposalVersion> proposalVersionPreSales = proposalDataProvider.getProposalVersionPreSales(proposalHeader.getId());
                    int size = proposalVersionPreSales.size();
                    if (size == 9) {
                        NotificationUtil.showNotification("Cannot exceed more than 9 versions", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                        return;
                    }
                    String str = ("0." + (size + 1));
                    versionNew = Float.valueOf(str);
                } else if (pVersion.getVersion().startsWith("1.")) {
                    List<ProposalVersion> proposalVersionPostSales = proposalDataProvider.getProposalVersionPostSales(proposalHeader.getId());
                    int size = proposalVersionPostSales.size();
                    if (size == 9) {
                        NotificationUtil.showNotification("Cannot exceed more than 9 versions", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                        return;
                    }
                    String str = ("1." + (size));
                    versionNew = Float.valueOf(str);

                } else if (pVersion.getVersion().startsWith("2.")) {

                    //String role = ((User) VaadinSession.getCurrent().getAttribute(User.class.getName())).getRole();

                    /*if (!(("planning").equals(role) || ("admin").equals(role)))
                    {
                        NotificationUtil.showNotification("You are not authorized to create more versions", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                        return;
                    }*/

                    List<ProposalVersion> proposalVersionProduction = proposalDataProvider.getProposalVersionProduction(proposalHeader.getId());
                    int size = proposalVersionProduction.size();
                    if (size == 9) {
                        NotificationUtil.showNotification("Cannot exceed more than 9 versions", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                        return;
                    }
                    String str = ("2." + (size));
                    versionNew = Float.valueOf(str);
                }
                else if (pVersion.getVersion().startsWith("3.")) {

                    //String role = ((User) VaadinSession.getCurrent().getAttribute(User.class.getName())).getRole();

                    if (!(("planning").equals(role) || ("admin").equals(role)))
                    {
                        NotificationUtil.showNotification("You are not authorized to create more versions", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                        return;
                    }

                    List<ProposalVersion> proposalVersionProduction = proposalDataProvider.getProposalVersionafterProduction(proposalHeader.getId());
                    int size = proposalVersionProduction.size();
                    if (size == 9) {
                        NotificationUtil.showNotification("Cannot exceed more than 9 versions", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                        return;
                    }
                    String str = ("3." + (size));
                    versionNew = Float.valueOf(str);
                }

                copyVersion.setVersion(String.valueOf(versionNew));
                copyVersion.setFromVersion(pVersion.getVersion());
                copyVersion.setProposalId(pVersion.getProposalId());
                copyVersion.setTitle(pVersion.getTitle());
                copyVersion.setRemarks("");
                copyVersion.setFinalAmount(pVersion.getFinalAmount());
                copyVersion.setStatus(ProposalVersion.ProposalStage.Draft.name());
                copyVersion.setInternalStatus(ProposalVersion.ProposalStage.Draft.name());
                copyVersion.setToVersion(String.valueOf(versionNew));
                copyVersion.setDiscountAmount(pVersion.getDiscountAmount());
                copyVersion.setDiscountPercentage(pVersion.getDiscountPercentage());
                copyVersion.setAmount(pVersion.getAmount());
                copyVersion.setProposalId(pVersion.getProposalId());
                copyVersion.setProjectHandlingAmount(pVersion.getProjectHandlingAmount());
                copyVersion.setDeepClearingQty(pVersion.getDeepClearingQty());
                copyVersion.setFloorProtectionSqft(pVersion.getFloorProtectionSqft());
                copyVersion.setFloorProtectionAmount(pVersion.getFloorProtectionAmount());
                copyVersion.setDeepClearingAmount(pVersion.getDeepClearingAmount());
                copyVersion.setFloorProtectionChargesApplied(pVersion.getFloorProtectionChargesApplied());
                copyVersion.setDeepClearingChargesApplied(pVersion.getDeepClearingChargesApplied());
                copyVersion.setProjectHandlingChargesApplied(pVersion.getProjectHandlingChargesApplied());

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
                LocalDateTime localDate = LocalDateTime.now();
                copyVersion.setBusinessDate(dtf.format(localDate));

                proposalDataProvider.createProposalVersion(copyVersion);
                proposalDataProvider.createNewProduct(copyVersion);
                proposalDataProvider.createNewAddons(copyVersion);
                copyVersion.setEventAddStatus(true);
                DashboardEventBus.post(new ProposalEvent.VersionCreated(copyVersion));
                ProposalVersion proposalVersionLatest = proposalDataProvider.getLatestVersion(proposalHeader.getId());
                    proposalHeader.setStatus(proposalVersionLatest.getStatus());
                    proposalHeader.setVersion(proposalVersionLatest.getVersion());
                if ((proposalHeader.getPriceDate() == null))
                {
                    proposalHeader.setPriceDate(null);
                }
               proposalDataProvider.saveProposal(proposalHeader);
                /*cancelButton.setVisible(false);
                saveAndCloseButton.setVisible(true);

                if (success) {

                    *//*NotificationUtil.showNotification("Saved successfully!", NotificationUtil.STYLE_BAR_SUCCESS_SMALL);
                    cityLockedForSave();*//*
                } else {
                    //NotificationUtil.showNotification("Couldn't Save Proposal! Please contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                }*/
                DashboardEventBus.unregister(this);
            }

            @Override
            public void onEdit(ClickableRenderer.RendererClickEvent rendererClickEvent) {
                try {
                    if(("Deleted").equals(proposalHeader.getStatus())) {
                        NotificationUtil.showNotification("Validation Error, please save the quote before proceeding", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                        return;
                    }
                    binder.commit();
                } catch (FieldGroup.CommitException e) {
                    NotificationUtil.showNotification("Validation Error, please fill all mandatory fields in header tab", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                    return;
                }
                ProposalVersion proposalVersion = (ProposalVersion) rendererClickEvent.getItemId();
                ProductAndAddons.open(proposalHeader, proposal, proposalVersion.getVersion(), proposalVersion);
            }
        }));
        columns.get(idx++).setHeaderCaption("CNC").setRenderer(new ViewButtonValueRenderer((ViewButtonValueRenderer.RendererClickListener) rendererClickEvent -> {

            ProposalHeader proposalHeaderCreateDate = proposalDataProvider.getProposalHeader(this.proposalHeader.getId());
            java.util.Date currentDate = proposalHeaderCreateDate.getCreatedOn();
            java.util.Date date = new Date(117,2,17,0,0,00);
            if (!currentDate.after(date))
            {
                NotificationUtil.showNotification("Cannot copy a proposal created before March 17", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                return;
            }
            if(viewOnlyValue.equals("Yes"))
            {
                Notification.show("You are not autorized to copy proposal");
                return;
            }

            if(("Deleted").equals(this.proposalHeader.getStatus())) {
                NotificationUtil.showNotification("Validation Error, please save the quote before proceeding", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                return;
            }

            ProposalVersion pVersion = (ProposalVersion) rendererClickEvent.getItemId();
            ProposalHeader proposalHeaderNew;
            proposalHeaderNew = new ProposalHeader();
            proposalHeaderNew = proposalDataProvider.createProposal();
            proposalHeaderNew.setFromProposal(proposalHeader.getId());
            proposalHeaderNew.setFromVersion(pVersion.getVersion());
            try{
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                Date date1 = new Date();
                String value=dateFormat.format(date1);
                proposalHeaderNew.setExpectedDeliveryDate(value);
            }
            catch (Exception e)
            {
                LOG.info("Exception in crm search window " +e);
            }
            proposalDataProvider.saveProposal(proposalHeaderNew);
            if("Yes".equals(proposalHeader.getPackageFlag()))
            {
                proposalHeaderNew.setPackageFlag(proposalHeader.getPackageFlag());
                /*proposalHeaderNew.setPackageFlag("No");*/
                proposalHeaderNew.setMaxDiscountPercentage(proposalHeader.getMaxDiscountPercentage());
                proposalDataProvider.saveProposal(proposalHeaderNew);
            }

            ProposalVersion copyVersion = new ProposalVersion();

            copyVersion.setVersion("0.1");
            copyVersion.setFromVersion("0.0");
            copyVersion.setProposalId(proposalHeaderNew.getId());
            copyVersion.setTitle(pVersion.getTitle());
            copyVersion.setRemarks("");
            copyVersion.setFinalAmount(pVersion.getFinalAmount());
            copyVersion.setStatus(ProposalVersion.ProposalStage.Draft.name());
            copyVersion.setInternalStatus(ProposalVersion.ProposalStage.Draft.name());
            copyVersion.setToVersion(pVersion.getVersion());
            copyVersion.setDiscountAmount(pVersion.getDiscountAmount());
            copyVersion.setDiscountPercentage(pVersion.getDiscountPercentage());
            copyVersion.setAmount(pVersion.getAmount());
            copyVersion.setOldProposalId(pVersion.getProposalId());
            copyVersion.setProjectHandlingAmount(pVersion.getProjectHandlingAmount());
            copyVersion.setDeepClearingQty(pVersion.getDeepClearingQty());
            copyVersion.setFloorProtectionSqft(pVersion.getFloorProtectionSqft());
            copyVersion.setFloorProtectionAmount(pVersion.getFloorProtectionAmount());
            copyVersion.setDeepClearingAmount(pVersion.getDeepClearingAmount());
            copyVersion.setFloorProtectionChargesApplied(pVersion.getFloorProtectionChargesApplied());
            copyVersion.setDeepClearingChargesApplied(pVersion.getDeepClearingChargesApplied());
            copyVersion.setProjectHandlingChargesApplied(pVersion.getProjectHandlingChargesApplied());
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
            LocalDateTime localDate = LocalDateTime.now();
            copyVersion.setBusinessDate(dtf.format(localDate));


            proposalDataProvider.createProposalVersion(copyVersion);

            if(Objects.equals(proposalHeader.getBeforeProductionSpecification(), "yes")) {
                proposalDataProvider.createNewProductFromOldProposal(copyVersion);
            }
            else
            {
                ProposalVersion proposalVersion=proposalDataProvider.createNewProductFromOldQuotation(copyVersion);
                List<Product> products = proposalDataProvider.getVersionProducts(proposalVersion.getProposalId(),proposalVersion.getVersion());
                for (Product product : products)
                {
                    List<Module> modules = product.getModules();
                    for (Module module : modules)
                    {
                        module.setHandleType(product.getHandleType());
                        module.setHandleFinish(product.getHandleFinish());
                        module.setKnobType(product.getKnobType());
                        module.setKnobFinish(product.getKnobFinish());
                        module.setHingeType(product.getHinge());
                        module.setGlassType(product.getGlass());
                        module.setHandleThickness(product.getHandleThickness());
                        module.setHandleTypeSelection(product.getHandleTypeSelection());
                        if (!(product.getHandleTypeSelection().equals("Normal"))) {
                            module.setHandleQuantity(0);
                            module.setKnobQuantity(0);
                        } else {
                            List<AccessoryDetails> accDetailsforHandle = proposalDataProvider.getAccessoryhandleDetails(module.getMgCode(), "HL");
                            if (accDetailsforHandle.size() == 0) {
                                module.setHandleQuantity(0);
                            } else {
                                for (AccessoryDetails a : accDetailsforHandle) {
                                   // LOG.info("handle quantity " + a);
                                    module.setHandleQuantity(Integer.valueOf(a.getQty()));
                                }
                            }

                            List<AccessoryDetails> accDetailsforKnob = proposalDataProvider.getAccessoryhandleDetails(module.getMgCode(), "K");
                            if (accDetailsforKnob.size() == 0) {
                                module.setKnobQuantity(0);
                            } else {
                                for (AccessoryDetails a : accDetailsforKnob) {
                                    module.setKnobQuantity(Integer.valueOf(a.getQty()));
                                }
                            }
                            //LOG.info("mg code"  +module.getMgCode());
                            List<MGModule> handlePresent = proposalDataProvider.retrieveModuleDetails(module.getMgCode());
                            for (MGModule m : handlePresent) {
                              //  LOG.info("module mand " + m.toString());
                                if (m.getHandleMandatory().equals("Yes")) {
                                    module.setHandlePresent(m.getHandleMandatory());
                                } else {
                                    module.setHandlePresent(m.getHandleMandatory());
                                }
                                if (m.getKnobMandatory().equals("Yes")) {
                                    module.setKnobPresent(m.getKnobMandatory());
                                } else {
                                    module.setKnobPresent(m.getKnobMandatory());
                                }
                            }
                            List<ModuleHingeMap> hingeMaps1 = proposalDataProvider.getCodeLookup(module.getMgCode(), module.getHingeType());
                            //LOG.info("size of hinge" + hingeMaps1.size());
                            module.setHingePack(hingeMaps1);
                            for (MGModule m : handlePresent) {
                                if (m.getHingeMandatory().equals("Yes")) {
                                    module.setHingePresent(m.getHingeMandatory());
                                    List<ModuleHingeMap> hingeMaps = proposalDataProvider.getCodeLookup(module.getMgCode(), module.getHingeType());
                                    module.setHingePack(hingeMaps);
                                }
                            }
                        }
                    }
                    proposalDataProvider.updateProduct(product);
                }

            }

            proposalDataProvider.createNewAddonFromOldProposal(copyVersion);

            int proposalId = proposalHeaderNew.getId();


            ProposalHeader proposalHeaderUpdatePrice = proposalDataProvider.updatePriceForNewProposal(proposalHeaderNew);
            if (!(proposalHeaderUpdatePrice == null))
            {
                UI.getCurrent().getNavigator()
                        .navigateTo("New Quotation/" + proposalId);
                DashboardEventBus.unregister(this);
            }

        }));


        versionContainer.addAll(proposalVersionList);
        versionsGrid.setContainerDataSource(createGeneratedVersionPropertyContainer());
        versionsGrid.getSelectionModel().reset();
        verticalLayout.addComponent(versionsGrid);
        verticalLayout.setSpacing(true);
        return verticalLayout;
    }

    private Component buildScopeOfwork()
    {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        verticalLayout.setMargin(new MarginInfo(true, true, true, true));

        verticalLayout.setSpacing(true);

        Label label = new Label("Please click the below button in order to open the scope of services");
        verticalLayout.addComponent(label);

        Label label2 = new Label("On clicking of Open Scope of services button, you will be redirected to a new window");
        verticalLayout.addComponent(label2);

        verticalLayout.setSpacing(true);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSizeFull();
        horizontalLayout.setMargin(new MarginInfo(true, true, true, true));

        Button scope_of_work_1 = new Button();
        scope_of_work_1.setCaption("Scope of Services V1");
        scope_of_work_1.addStyleName(ValoTheme.BUTTON_PRIMARY);
        scope_of_work_1.setCaptionAsHtml(true);
        scope_of_work_1.setIcon(FontAwesome.BINOCULARS);
        productAndAddonSelection.setToVersion("0.0");



        scope_of_work_1.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                createSOWandOpenSOWPopup();

            }
        });


        horizontalLayout.addComponent(scope_of_work_1);

        Button scope_of_work_2 = new Button();
        scope_of_work_2.setCaption("Scope of Services V2");
        scope_of_work_2.addStyleName(ValoTheme.BUTTON_PRIMARY);
        scope_of_work_2.setCaptionAsHtml(true);
        scope_of_work_2.setIcon(FontAwesome.BINOCULARS);

        scope_of_work_2.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                List<ProposalVersion> proposalVersions = proposalDataProvider.getProposalVersions(proposalHeader.getId());
                String readonlyFlag = "no" ;

                double versionToBeConsidered = Double.parseDouble(proposalVersions.get(0).getVersion());
                List<String> versions = new ArrayList<String>();

                for (ProposalVersion proposalVersion : proposalVersions)
                {
                    if (proposalVersion.getVersion().equals("2.0") || proposalVersion.getVersion().startsWith("1.")) {
                        versions.add(proposalVersion.getVersion());
                    }
                    if (Double.parseDouble(proposalVersion.getVersion()) >= 2.0)
                    {
                        readonlyFlag = "yes";
                    }
                }
                if (versions.contains("2.0"))
                {
                    versionToBeConsidered = 2.0;
                }
                else
                {
                    versionToBeConsidered = Double.parseDouble(versions.get(0));
                    for (String version : versions) {
                        if (Double.parseDouble(version) > Double.parseDouble(versions.get(0))) {
                            versionToBeConsidered = Double.parseDouble(version);
                        }
                    }

                    if (versionToBeConsidered >= 2.0)
                    {
                        readonlyFlag = "yes";
                    }
                }

                LOG.debug("Version to be considered : " + versionToBeConsidered);
                List<Proposal_sow> proposal_sowsV2 = proposalDataProvider.getProposalSowLineItems(proposalHeader.getId(),"2.0");
                if (proposal_sowsV2.size() == 0)
                {
                    proposalDataProvider.copyProposalSowLineItems(proposalHeader.getId(),"1.0");

                }
                NotificationUtil.showNotification("Generating the Scope of services sheet v2.0",NotificationUtil.STYLE_BAR_SUCCESS_SMALL);
                productAndAddonSelection.setToVersion(String.valueOf(versionToBeConsidered));

                JSONObject quoteFile = proposalDataProvider.updateSowLineItems(proposalHeader.getId(),versionToBeConsidered,readonlyFlag);
                LOG.debug("Quote file :" + quoteFile);
                try{
                    if(quoteFile.getString("status").equalsIgnoreCase("success")) {
                        SOWPopupWindow.open(proposalHeader, productAndAddonSelection, quoteFile);
                    }else{
                        NotificationUtil.showNotification(quoteFile.getString("comments"), NotificationUtil.STYLE_BAR_ERROR_SMALL);
                        return;
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }

                SOWPopupWindow.open(proposalHeader,productAndAddonSelection,quoteFile);

            }
        });

        Button scope_of_work_3 = new Button();
        scope_of_work_3.setCaption("Scope of Services V3");
        scope_of_work_3.addStyleName(ValoTheme.BUTTON_PRIMARY);
        scope_of_work_3.setCaptionAsHtml(true);
        scope_of_work_3.setIcon(FontAwesome.BINOCULARS);

        scope_of_work_3.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                List<ProposalVersion> proposalVersions = proposalDataProvider.getProposalVersions(proposalHeader.getId());
                String readonlyFlag = "no" ;

                double versionToBeConsidered = Double.parseDouble(proposalVersions.get(0).getVersion());
                List<String> versions = new ArrayList<String>();

                for (ProposalVersion proposalVersion : proposalVersions)
                {
                    if (proposalVersion.getVersion().equals("3.0") || proposalVersion.getVersion().startsWith("2.")) {
                        versions.add(proposalVersion.getVersion());
                    }
                }
                if (versions.contains("3.0"))
                {
                    versionToBeConsidered = 3.0;
                }
                else
                {
                    versionToBeConsidered = Double.parseDouble(versions.get(0));
                    for (String version : versions) {
                        if (Double.parseDouble(version) > Double.parseDouble(versions.get(0))) {
                            versionToBeConsidered = Double.parseDouble(version);
                        }
                    }

                    if (versionToBeConsidered >= 3.0)
                    {
                        readonlyFlag = "yes";
                    }
                }

                LOG.debug("Version to be considered in scope of 3 button : " + versionToBeConsidered);
                List<Proposal_sow> proposal_sowsV3 = proposalDataProvider.getProposalSowLineItems(proposalHeader.getId(),"3.0");
                if (proposal_sowsV3.size() == 0)
                {
                    proposalDataProvider.copyProposalSowLineItems(proposalHeader.getId(),"2.0");

                }
                NotificationUtil.showNotification("Generating the Scope of services sheet v3.0",NotificationUtil.STYLE_BAR_SUCCESS_SMALL);
                productAndAddonSelection.setToVersion(String.valueOf(versionToBeConsidered));

                JSONObject quoteFile = proposalDataProvider.updateSowLineItems(proposalHeader.getId(),versionToBeConsidered,readonlyFlag);
                LOG.debug("Quote file :" + quoteFile);
                try{
                    if(quoteFile.getString("status").equalsIgnoreCase("success")) {
                        SOWPopupWindow.open(proposalHeader, productAndAddonSelection, quoteFile);
                    }else{
                        NotificationUtil.showNotification(quoteFile.getString("comments"), NotificationUtil.STYLE_BAR_ERROR_SMALL);
                        return;
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }

                SOWPopupWindow.open(proposalHeader,productAndAddonSelection,quoteFile);

            }
        });

        List<ProposalVersion> versions = proposal.getVersions();
        for (ProposalVersion proposalVersion : versions)
        {
            if (proposalVersion.getVersion().contains("1."))
            {
                horizontalLayout.addComponent(scope_of_work_2);
            }
            if (proposalVersion.getVersion().contains("2."))
            {
                horizontalLayout.addComponent(scope_of_work_3);
            }
        }

        verticalLayout.addComponent(horizontalLayout);

        return verticalLayout;
    }

    private void createSOWandOpenSOWPopup() {
        List<ProposalVersion> proposalVersions = proposalDataProvider.getProposalVersions(proposalHeader.getId());
        String readOnlyFlag = "no";

        double versionToBeConsidered = Double.parseDouble(proposalVersions.get(0).getVersion());
        List<String> versions = new ArrayList<String>();

        for (ProposalVersion proposalVersion : proposalVersions)
        {
            if (proposalVersion.getVersion().equals("1.0") || proposalVersion.getVersion().startsWith("0.")){
                versions.add(proposalVersion.getVersion());
            }
            if (Double.parseDouble(proposalVersion.getVersion()) >= 1.0)
            {
                readOnlyFlag = "yes";
            }
        }
        if (versions.contains("1.0"))
        {
            versionToBeConsidered = 1.0;
        }
        else
        {
            for (String version : versions) {
                if (Double.parseDouble(version) > Double.parseDouble(versions.get(0))) {
                    versionToBeConsidered = Double.parseDouble(version);

                }
            }
        }
        LOG.debug("Version to be considered : " + versionToBeConsidered);
        productAndAddonSelection.setToVersion(String.valueOf(versionToBeConsidered));

        NotificationUtil.showNotification("Generating the Scope of services sheet v1.0",NotificationUtil.STYLE_BAR_SUCCESS_SMALL);

        JSONObject quoteFile = proposalDataProvider.updateSowLineItems(proposalHeader.getId(),versionToBeConsidered,readOnlyFlag);
        LOG.debug("Quote file :" + quoteFile);
        try{
            if(quoteFile.getString("status").equalsIgnoreCase("success")) {
                SOWPopupWindow.open(proposalHeader, productAndAddonSelection, quoteFile);
            }else{
                NotificationUtil.showNotification(quoteFile.getString("comments"), NotificationUtil.STYLE_BAR_ERROR_SMALL);
                return;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private Component buildBoq() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        verticalLayout.setMargin(new MarginInfo(true, true, true, true));

        verticalLayout.setSpacing(true);

        Label label = new Label("Please click the below buttons in order to open the scope of services");
        verticalLayout.addComponent(label);

        verticalLayout.setSpacing(true);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSizeFull();
        horizontalLayout.setMargin(new MarginInfo(true, true, true, true));

        Button boq = new Button();
        boq.setCaption("BOQ");
        boq.addStyleName(ValoTheme.BUTTON_PRIMARY);
        boq.setCaptionAsHtml(true);
        horizontalLayout.addComponent(boq);


        boq.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                List<ProposalVersion> proposalVersions = proposal.getVersions();
                String readOnlyFlag = "no";

                ProposalVersion proposalVersionToBeConsidered = null;
                for (ProposalVersion proposalVersion : proposalVersions) {
                    if (proposalVersion.getVersion().equals("3.0"))
                    {
                        proposalVersionToBeConsidered = proposalVersion;
                    }
                }

                if (proposalVersionToBeConsidered == null)
                {
                    NotificationUtil.showNotification("3.0 version not found for the proposal", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                    return;
                }

                productAndAddonSelection.setToVersion("3.0");

                NotificationUtil.showNotification("Generating the BOQ sheet", NotificationUtil.STYLE_BAR_SUCCESS_SMALL);

                JSONObject quoteFile = null;


                quoteFile = proposalDataProvider.updateBoqLineItems(proposalHeader.getId(), Double.parseDouble(proposalVersionToBeConsidered.getVersion()), "no");


                LOG.debug("Quote file :" + quoteFile);
                try {
                    if (quoteFile.getString("status").equalsIgnoreCase("success")) {
                        BoqPopupWindow.open(proposalHeader, productAndAddonSelection, quoteFile);
                    } else {
                        NotificationUtil.showNotification("Error in opening BOQ sheet, Please contact GAME admin", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        verticalLayout.addComponent(horizontalLayout);

        VerticalLayout verticalLayoutSO = new VerticalLayout();
        verticalLayoutSO.setSizeFull();

        verticalLayoutSO.addComponent(new Label("Please click on the Generate SO Extracts button only after you have saved the BOQ Master sheet"));

//        verticalLayoutSO.addComponent(new Label("Note: Once the Generate SO Extracts button is clicked, changes to BOQ cannot be made and the master sheet would get locked"));

        Button generateSo = new Button();
        generateSo.setCaption("Generate SO Extracts");
        generateSo.addStyleName(ValoTheme.BUTTON_PRIMARY);

        Proposal_boq proposal_boq = new Proposal_boq();
        proposal_boq.setProposalId(proposalHeader.getId());
        verticalLayoutSO.addComponent(generateSo);

        verticalLayout.addComponent(verticalLayoutSO);

        generateSo.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                try {
                    JSONObject jsonObject = proposalDataProvider.generateSoExtracts(proposal_boq);


                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        SOPopupWindow.open(jsonObject);
                    } else {
                        NotificationUtil.showNotification("Error in generating SO extracts, Please contact GAME admin", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    }


            }
        });
       /* if (proposalHeader.getBoqDriveLink() == null || !(proposalHeader.getBoqDriveLink().equals(""))) {
            verticalLayout.removeComponent(verticalLayoutSO);
            verticalLayout.addComponent(boqLinkLayout(proposalHeader.getBoqDriveLink()));
        }
*/



        return verticalLayout;
    }
/*

    private VerticalLayout boqLinkLayout(String webViewLink) {

        VerticalLayout boqLinkLayout = new VerticalLayout();
        boqLinkLayout.setSizeFull();

        boqLinkLayout.addComponent(new Label("Please click the below button to navigate to the folder where the SO's have been created"));

        Button openBoqFile = new Button();
        openBoqFile.setCaption("Generate SO Extracts");
        openBoqFile.addStyleName(ValoTheme.BUTTON_PRIMARY);

        boqLinkLayout.addComponent(openBoqFile);

        openBoqFile.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                String email = ((User) VaadinSession.getCurrent().getAttribute(User.class.getName())).getEmail();

                Proposal_boq proposal_boq = new Proposal_boq();
                proposal_boq.setProposalId(proposalHeader.getId());
                proposal_boq.setId("0");
                proposal_boq.setUserId(email);
                JSONObject soExtract = proposalDataProvider.generateSoExtracts(proposal_boq);
            }
        });


        return boqLinkLayout;

    }
*/


    private void refreshVersionsGrid(List<ProposalVersion> updatedProposalVersions) {
        versionContainer.addAll(updatedProposalVersions);

        versionsGrid.setContainerDataSource(createGeneratedVersionPropertyContainer());
        versionsGrid.getSelectionModel().reset();
    }
    private GeneratedPropertyContainer createGeneratedVersionPropertyContainer() {
        GeneratedPropertyContainer genContainer = new GeneratedPropertyContainer(versionContainer);
        genContainer.addGeneratedProperty("actions", getEmptyActionTextGenerator());
        genContainer.addGeneratedProperty("CNC", getEmptyActionTextGenerator());
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
        noOfDaysForWorkCompletion.setReadOnly(readOnly);
        crmId.setReadOnly(readOnly);
        quotationField.setReadOnly(readOnly);
        //customerIdField.setReadOnly(readOnly);
        customerNameField.setReadOnly(readOnly);
        customerAddressLine1.setReadOnly(readOnly);
        customerAddressLine2.setReadOnly(readOnly);
        customerAddressLine3.setReadOnly(readOnly);
        customerEmailField.setReadOnly(readOnly);
        customerNumberField1.setReadOnly(readOnly);
        projectName.setReadOnly(readOnly);
        projectAddressLine1.setReadOnly(readOnly);
        projectCityField.setReadOnly(readOnly);
        offerField.setReadOnly(readOnly);
        salesPerson.setReadOnly(readOnly);
        designPerson.setReadOnly(readOnly);
    }

    private Component buildHeader() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSizeFull();

        HorizontalLayout left = new HorizontalLayout();
        horizontalLayout.setMargin(new MarginInfo(false, false, false, true));
        String title = this.proposalHeader.getTitle();
        if (title == null) title = "";
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

        saveAndCloseButton = new Button("Save & Close");
        saveAndCloseButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        saveAndCloseButton.addStyleName("margin-right-10-for-headerlevelbutton");
        saveAndCloseButton.addClickListener(this::saveAndClose);
        right.addComponent(saveAndCloseButton);
        right.setComponentAlignment(saveAndCloseButton, Alignment.MIDDLE_RIGHT);

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

    private void save(Button.ClickEvent clickEvent) {

        try {
            binder.commit();
            if(Objects.equals(proposalHeader.getOfferType(),"") || Objects.equals(proposalHeader.getPcity(),"") )
            {
                NotificationUtil.showNotification("Validation Error, please fill all mandatory fields!", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                return;
            }

            String dateStr = expectedDeliveryDate.getValue().toString();
            DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
            DateFormat newDateFormat = new SimpleDateFormat("YYYY-MM-dd");
            Date date = (Date)formatter.parse(dateStr);
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(date);
            if(date.before(proposalHeader.getPriceDate()) || Objects.equals(proposalHeader.getExpectedDeliveryDate(),"") )
            {
                NotificationUtil.showNotification("Invalid Expected delivery date", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                return;
            }
            if(Objects.equals(proposalHeader.getQuoteNoNew(),""))
            {
                LOG.info("proposal header city " +proposalHeader.getPcity());
                proposalDataProvider.getQuotationNumber(this.proposalHeader);
                List<ProposalCity> proposalCities=proposalDataProvider.getCityDataTest(this.proposalHeader.getId());
                for(ProposalCity proposalCity:proposalCities)
                {
                    LOG.info("quote number after save method " +proposalCity.getQuoteNo());
                    proposalHeader.setQuoteNoNew(proposalCity.getQuoteNo());
                    quotenew.setValue(proposalHeader.getQuoteNoNew());
                }
            }
        } catch (Exception e) {
            NotificationUtil.showNotification("Validation Error, please fill all mandatory fields!", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            return;
        }


        if(Integer.parseInt(noOfDaysForWorkCompletion.getValue().toString()) < MIN_WORK_COMPLETION_DAYS){
            NotificationUtil.showNotification("# of days for Works completion should be minimum of "+MIN_WORK_COMPLETION_DAYS+" days.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            return;
        }

        List<Product> products = proposalDataProvider.getProposalProducts(proposalHeader.getId());
        if ((Objects.equals(proposalHeader.getQuoteNoNew(), "") || proposalHeader.getQuoteNoNew() == null || proposalHeader.getQuoteNoNew().isEmpty()) && !((products.size() == 0)))
        {
            LOG.debug("Inside If save");
            LOG.debug("This proposal header 1" + this.proposalHeader);
            proposalDataProvider.updatePriceForNewProposal(proposalHeader);

            if (StringUtils.isEmpty(this.proposalHeader.getTitle())) {
                this.proposalHeader.setTitle(NEW_TITLE);
            }
            try {
                binder.commit();
            } catch (FieldGroup.CommitException e) {
                NotificationUtil.showNotification("Validation Error, please fill all mandatory fields!", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                return;
            }
            ProposalVersion proposalVersionLatest = proposalDataProvider.getLatestVersion(this.proposalHeader.getId());
            this.proposalHeader.setStatus(proposalVersionLatest.getStatus());
            this.proposalHeader.setVersion(proposalVersionLatest.getVersion());
            this.proposalHeader.setMaxDiscountPercentage(Double.valueOf(maxDiscountPercentage.getValue()));

            /*checkQuoteNoNew();
            this.proposalHeader.setQuoteNoNew(QuoteNumNew);*/
            boolean success = proposalDataProvider.saveProposal(this.proposalHeader);


            /*try {

                List<ProposalCity> insertCity = proposalDataProvider.getCityDataTest(this.proposalHeader.getId());
                if (!(insertCity.size() >= 1)) {
                    ProposalCity proposalCity = proposalDataProvider.createCity(cityCode, month, this.proposalHeader.getId(), quotenew.getValue());
                    if (proposalCity == null)
                    {
                        this.getQuoteNum(true);
                        ProposalCity proposalCity1 = proposalDataProvider.createCity(cityCode, month, this.proposalHeader.getId(), quotenew.getValue());
                        if (proposalCity1 == null)
                        {
                            this.getQuoteNum(true);
                            proposalDataProvider.createCity(cityCode, month, this.proposalHeader.getId(), quotenew.getValue());
                        }
                    }
                }
            } catch (Exception e) {
                LOG.info(e);
            }*/

            setMaxDiscountPercentange();
            this.proposalHeader.setMaxDiscountPercentage(Double.valueOf(maxDiscountPercentage.getValue()));
            proposalDataProvider.updatePriceForNewProposal(this.proposalHeader);
            proposalDataProvider.saveProposal(this.proposalHeader);

            if(proposalHeader.getMaxDiscountPercentage()==0 ) {
                maxDiscountPercentage.setReadOnly(false);
                maxDiscountPercentage.setValue(String.valueOf(rateForDiscount));
                this.proposalHeader.setMaxDiscountPercentage(Double.valueOf(maxDiscountPercentage.getValue()));
                proposalDataProvider.saveProposal(this.proposalHeader);
                if(!(("admin").equals(role))) {
                    maxDiscountPercentage.setReadOnly(true);
                }
            }

            cancelButton.setVisible(false);
            saveAndCloseButton.setVisible(true);


            if (success) {

                NotificationUtil.showNotification("Saved successfully!", NotificationUtil.STYLE_BAR_SUCCESS_SMALL);
                cityLockedForSave();
            } else {
                NotificationUtil.showNotification("Couldn't Save Proposal! Please contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            }
        }
        else {

            LOG.debug("Inside else save");
            LOG.debug("This proposal header 1" + this.proposalHeader);
            List<ProposalVersion> proposalVersions = proposalDataProvider.getversion(proposalHeader.getId(),"1.0");
            String Bookingdate=null;
            LOG.info("booking order month " +bookingFormField.getValue().toString() + proposalHeader.getBookingOrderMonth().equals(null));
            if(!(StringUtils.isEmpty(this.proposalHeader.getBookingOrderMonth())))
            {
                for(ProposalVersion p : proposalVersions)
                {
                    if(p.getVersion().equalsIgnoreCase("1.0"))
                    {
                        Bookingdate=p.getBusinessDate().substring(0,10);
                    }
                    else
                    {
                        Bookingdate=null;
                    }
                }

                LocalDate now = LocalDate.now(); // 2015-11-24
                DateTimeFormatter currentFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate confirmDate = LocalDate.parse(Bookingdate, currentFormatter);
                confirmDate.getMonth(); // java.time.Month = OCTOBER
                confirmDate.getMonth().getValue(); // 10
                int confirmdateyear=confirmDate.getYear() % 100; // 2015
                //String str = "2015-01-15";

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate dateTime = LocalDate.parse(Bookingdate, formatter);
                LOG.info("get month " +dateTime.getMonth()+ "get year " +dateTime.getYear()+ "get month value " +dateTime.getMonthValue());
                LocalDate earlier = dateTime.minusMonths(1); // 2015-10-24
                earlier.getMonth(); // java.time.Month = OCTOBER
                earlier.getMonth().getValue(); // 10
                int year=earlier.getYear() % 100; // 2015

                selectedMonth=proposalHeader.getBookingOrderMonth().substring(0,3);
                selectedYear=proposalHeader.getBookingOrderMonth().substring(4,6);
                previousMonth=this.theMonth(earlier.getMonth().getValue()-1);
                previousYear=String.valueOf(year);
                BookingMonth=this.theMonth(confirmDate.getMonth().getValue()-1);
                BookingYear=String.valueOf(confirmdateyear);

                if((selectedMonth.equalsIgnoreCase(BookingMonth) && selectedYear.equalsIgnoreCase(BookingYear)))
                {
                    //NotificationUtil.showNotification("selected month s equal to booking month", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                }else if((selectedMonth.equalsIgnoreCase(previousMonth) && selectedYear.equalsIgnoreCase(previousYear)))
                {
                    //NotificationUtil.showNotification("selected month is equal to previous month", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                }
                else
                {
                    NotificationUtil.showNotification("Incorrect month value 2nd case", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                    return;
                }
            }

            if (StringUtils.isEmpty(this.proposalHeader.getTitle())) {
                this.proposalHeader.setTitle(NEW_TITLE);
            }
            try {
                binder.commit();
            } catch (FieldGroup.CommitException e) {
                NotificationUtil.showNotification("Validation Error, please fill all mandatory fields!", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                return;
            }
            ProposalVersion proposalVersionLatest = proposalDataProvider.getLatestVersion(this.proposalHeader.getId());
            this.proposalHeader.setStatus(proposalVersionLatest.getStatus());
            this.proposalHeader.setVersion(proposalVersionLatest.getVersion());
            this.proposalHeader.setAmount(proposalVersionLatest.getFinalAmount());
            this.proposalHeader.setMaxDiscountPercentage(Double.valueOf(maxDiscountPercentage.getValue()));

           /* checkQuoteNoNew();
            this.proposalHeader.setQuoteNoNew(QuoteNumNew);*/

            /*try {

                List<ProposalCity> insertCity = proposalDataProvider.getCityDataTest(this.proposalHeader.getId());
                if (!(insertCity.size() >= 1)) {
                    ProposalCity proposalCity = proposalDataProvider.createCity(cityCode, month, this.proposalHeader.getId(), quotenew.getValue());
                    LOG.debug("1st Proposal city :" + proposalCity.toString());
                    if (proposalCity.getQuoteNo() == null)
                    {
                        LOG.debug("Inside 1st condition");
                        this.getQuoteNum(true);
                        ProposalCity proposalCity1 = proposalDataProvider.createCity(cityCode, month, this.proposalHeader.getId(), quotenew.getValue());
                        if (proposalCity1.getQuoteNo() == null)
                        {
                            this.getQuoteNum(true);
                            proposalDataProvider.createCity(cityCode, month, this.proposalHeader.getId(), quotenew.getValue());
                        }
                    }
                }
            } catch (Exception e) {
                LOG.info(e);
            }*/

            setMaxDiscountPercentange();
            this.proposalHeader.setMaxDiscountPercentage(Double.valueOf(maxDiscountPercentage.getValue()));
            boolean success = proposalDataProvider.saveProposal(this.proposalHeader);

            /*LOG.debug("Inside else save");
            LOG.debug("This proposal header 2" + this.proposalHeader);*/


            if(proposalHeader.getMaxDiscountPercentage()==0 ) {
                maxDiscountPercentage.setReadOnly(false);
                maxDiscountPercentage.setValue(String.valueOf(rateForDiscount));
                this.proposalHeader.setMaxDiscountPercentage(Double.valueOf(maxDiscountPercentage.getValue()));
                proposalDataProvider.saveProposal(this.proposalHeader);
                if(!(("admin").equals(role))) {
                    maxDiscountPercentage.setReadOnly(true);
                }
            }

            cancelButton.setVisible(false);
            saveAndCloseButton.setVisible(true);

            if (success) {

                NotificationUtil.showNotification("Saved successfully!", NotificationUtil.STYLE_BAR_SUCCESS_SMALL);
                cityLockedForSave();
            } else {
                NotificationUtil.showNotification("Couldn't Save Proposal! Please contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            }

        }

    }


    private void checkQuoteNoNew() {
        if (!(this.proposalHeader.getQuoteNoNew() == null || this.proposalHeader.getQuoteNoNew().isEmpty()))
        {
            QuoteNumNew = this.proposalHeader.getQuoteNoNew();
        }

        if (this.proposalHeader.getQuoteNoNew() == null || this.proposalHeader.getQuoteNoNew().isEmpty())
        {
            quotenew.setValue(QuoteNumNew);
        }
    }

    private void saveAndClose(Button.ClickEvent clickEvent) {
       /* boolean duplicateCrm = checkForDuplicateCRM();

        if (duplicateCrm) {
            NotificationUtil.showNotification("Quotation with same crmId already exists", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            return;
        }*/

        try {
            binder.commit();
            String dateStr = expectedDeliveryDate.getValue().toString();
            DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
            DateFormat newDateFormat = new SimpleDateFormat("YYYY-MM-dd");
            Date date = (Date)formatter.parse(dateStr);

            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(date);
            if(date.before(proposalHeader.getPriceDate()) || Objects.equals(proposalHeader.getExpectedDeliveryDate(),"") )
            {
                NotificationUtil.showNotification("Invalid Expected delivery date", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                return;
            }
            if(Objects.equals(proposalHeader.getQuoteNoNew(),""))
            {
                proposalDataProvider.getQuotationNumber(this.proposalHeader);
                List<ProposalCity> proposalCities=proposalDataProvider.getCityDataTest(this.proposalHeader.getId());
                for(ProposalCity proposalCity:proposalCities)
                {
                    proposalHeader.setQuoteNoNew(proposalCity.getQuoteNo());
                    quotenew.setValue(proposalHeader.getQuoteNoNew());
                }
            }
        } catch (Exception e) {
            NotificationUtil.showNotification("Validation Error, please fill all mandatory fields!", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            return;
        }

        if (StringUtils.isEmpty(this.proposalHeader.getTitle())) {
            this.proposalHeader.setTitle(NEW_TITLE);
        }
        List<ProposalVersion> proposalVersions = proposalDataProvider.getversion(proposalHeader.getId(),"1.0");
        String Bookingdate=null;
        LOG.info("booking order month " +bookingFormField.getValue().toString() + proposalHeader.getBookingOrderMonth().equals(null));
        if(!(StringUtils.isEmpty(this.proposalHeader.getBookingOrderMonth())))
        {
            for(ProposalVersion p : proposalVersions)
            {
                if(p.getVersion().equalsIgnoreCase("1.0"))
                {
                    Bookingdate=p.getBusinessDate().substring(0,10);
                }
                else
                {
                    Bookingdate=null;
                }
            }

            LocalDate now = LocalDate.now(); // 2015-11-24
            DateTimeFormatter currentFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate confirmDate = LocalDate.parse(Bookingdate, currentFormatter);
            confirmDate.getMonth(); // java.time.Month = OCTOBER
            confirmDate.getMonth().getValue(); // 10
            int confirmdateyear=confirmDate.getYear() % 100; // 2015
            //String str = "2015-01-15";

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate dateTime = LocalDate.parse(Bookingdate, formatter);
            LOG.info("get month " +dateTime.getMonth()+ "get year " +dateTime.getYear()+ "get month value " +dateTime.getMonthValue());
            LocalDate earlier = dateTime.minusMonths(1); // 2015-10-24
            earlier.getMonth(); // java.time.Month = OCTOBER
            earlier.getMonth().getValue(); // 10
            int year=earlier.getYear() % 100; // 2015

            selectedMonth=proposalHeader.getBookingOrderMonth().substring(0,3);
            selectedYear=proposalHeader.getBookingOrderMonth().substring(4,6);
            previousMonth=this.theMonth(earlier.getMonth().getValue()-1);
            previousYear=String.valueOf(year);
            BookingMonth=this.theMonth(confirmDate.getMonth().getValue()-1);
            BookingYear=String.valueOf(confirmdateyear);

            if((selectedMonth.equalsIgnoreCase(BookingMonth) && selectedYear.equalsIgnoreCase(BookingYear)))
            {
                //NotificationUtil.showNotification("selected month s equal to booking month", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            }else if((selectedMonth.equalsIgnoreCase(previousMonth) && selectedYear.equalsIgnoreCase(previousYear)))
            {
                //NotificationUtil.showNotification("selected month is equal to previous month", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            }
            else
            {
                NotificationUtil.showNotification("Incorrect month value 2nd case", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                return;
            }
        }

        try {
            binder.commit();
        } catch (FieldGroup.CommitException e) {
            NotificationUtil.showNotification("Validation Error, please fill all mandatory fields!", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            return;
        }
        ProposalVersion proposalVersionLatest = proposalDataProvider.getLatestVersion(this.proposalHeader.getId());

        this.proposalHeader.setStatus(proposalVersionLatest.getStatus());
        this.proposalHeader.setVersion(proposalVersionLatest.getVersion());


        /*try {

            List<ProposalCity> insertCity = proposalDataProvider.getCityDataTest(this.proposalHeader.getId());
            if (!(insertCity.size() >= 1)) {
                ProposalCity proposalCity = proposalDataProvider.createCity(cityCode, month, this.proposalHeader.getId(), quotenew.getValue());
                if (proposalCity == null)
                {
                    this.cityChanged(null);
                    ProposalCity proposalCity1 = proposalDataProvider.createCity(cityCode, month, this.proposalHeader.getId(), quotenew.getValue());
                    if (proposalCity1 == null)
                    {
                        this.cityChanged(null);
                        proposalDataProvider.createCity(cityCode, month, this.proposalHeader.getId(), quotenew.getValue());
                    }
                }
            }
        } catch (Exception e) {
            LOG.info(e);
        }*/
        boolean success = proposalDataProvider.saveProposal(this.proposalHeader);
        cancelButton.setVisible(false);
        DashboardEventBus.post(new ProposalEvent.DashboardMenuUpdated(false));

        if (success) {
            NotificationUtil.showNotification("Saved successfully!", NotificationUtil.STYLE_BAR_SUCCESS_SMALL);
            cityLockedForSave();
        } else {
            NotificationUtil.showNotification("Couldn't Save Proposal! Please contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
        }
        DashboardEventBus.unregister(this);
        UI.getCurrent().getNavigator().navigateTo(DashboardViewType.PROPOSALS.name());
    }

    /*private boolean checkForDuplicateCRM() {

        String crmIdValue = (String) crmId.getValue();
        ProposalHeader getCrm = proposalDataProvider.getProposalHeader(proposalHeader.getId());


        List<ProposalHeader> crmIdFromOldProposals = proposalDataProvider.getProposalHeaders();
        LOG.debug(crmIdFromOldProposals.size());
        boolean duplicateCrm = false;
        for (ProposalHeader crmOld : crmIdFromOldProposals) {
            String crmOldTest = crmOld.getCrmId();
            if (null == crmOldTest || ("").equals(crmOldTest)) continue;
            if (getCrm.getCrmId().isEmpty() || getCrm.getCrmId() == null) {

                if (crmOldTest.equals(crmIdValue)) {
                    duplicateCrm = true;
                    break;
                }
            }
        }
        return duplicateCrm;
    }*/


    private void cancel(Button.ClickEvent clickEvent) {
        try {
            if (StringUtils.isEmpty(this.proposalHeader.getTitle()) || StringUtils.isEmpty(this.proposalHeader.getCrmId()) || StringUtils.isEmpty(this.proposalHeader.getCname()) || StringUtils.isEmpty(proposalHeader.getQuoteNo()) || StringUtils.isEmpty(proposalHeader.getQuoteNoNew())) {

                    boolean success = proposalDataProvider.deleteProposal(this.proposalHeader.getId());

                    if (!success) {
                        DashboardEventBus.post(new ProposalEvent.DashboardMenuUpdated(false));
                        UI.getCurrent().getNavigator().navigateTo(DashboardViewType.PROPOSALS.name());
                        DashboardEventBus.unregister(this);
                    } else {
                        NotificationUtil.showNotification("Couldn't cancel Proposal! Please contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                    }

            } else {
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

        HorizontalLayout hlayout=new HorizontalLayout();
        searchcrmid =new Button("Search Customer");
        hlayout.addStyleName("crmstyle");
        searchcrmid.addClickListener(this::searchCRMData);
        hlayout.addComponent(searchcrmid);
        verticalLayout.addComponent(hlayout);

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

        /*customerIdField = binder.buildAndBind("Customer ID", CUSTOMER_ID);
        customerIdField.setRequired(true);
        ((TextField) customerIdField).setNullRepresentation("");
        formLayoutLeft.addComponent(customerIdField);*/
        customerNameField = binder.buildAndBind("Customer Name", C_NAME);
        customerNameField.setRequired(true);
        customerNameField.setReadOnly(true);
        ((TextField) customerNameField).setNullRepresentation("");
        formLayoutLeft.addComponent(customerNameField);

        customerAddressLine1 = binder.buildAndBind("Address", C_ADDRESS1);
        ((TextField) customerAddressLine1).setNullRepresentation("");
        customerAddressLine1.setReadOnly(true);
        formLayoutLeft.addComponent(customerAddressLine1);

        /*customerAddressLine2 = binder.buildAndBind("Address Line 2", C_ADDRESS2);
        ((TextField) customerAddressLine2).setNullRepresentation("");
        formLayoutLeft.addComponent(customerAddressLine2);
        customerAddressLine3 = binder.buildAndBind("Address Line 3", C_ADDRESS3);
        ((TextField) customerAddressLine3).setNullRepresentation("");
        formLayoutLeft.addComponent(customerAddressLine3);
        customerCityField = binder.buildAndBind("City", C_CITY);
        ((TextField) customerCityField).setNullRepresentation("");
        formLayoutLeft.addComponent(customerCityField);*/

        customerEmailField = binder.buildAndBind("Email", C_EMAIL);
        ((TextField) customerEmailField).setNullRepresentation("");
        customerEmailField.setReadOnly(true);
        //formLayoutLeft.addComponent(customerEmailField);
        customerNumberField1 = binder.buildAndBind("Phone", C_PHONE1);
        customerNumberField1.setReadOnly(true);
        ((TextField) customerNumberField1).setNullRepresentation("");
        //formLayoutLeft.addComponent(customerNumberField1);
        /*customerNumberField2 = binder.buildAndBind("Phone 2", C_PHONE2);
        ((TextField) customerNumberField2).setNullRepresentation("");
        formLayoutLeft.addComponent(customerNumberField2);*/

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
        projectName.setReadOnly(true);
        formLayoutRight.addComponent(projectName);
        projectAddressLine1 = binder.buildAndBind("Address", P_ADDRESS1);
        ((TextField) projectAddressLine1).setNullRepresentation("");
        //projectAddressLine1.setReadOnly(true);
        formLayoutRight.addComponent(projectAddressLine1);
       /* projectAddressLine2 = binder.buildAndBind("Address Line 2", P_ADDRESS2);
        ((TextField) projectAddressLine2).setNullRepresentation("");
        formLayoutRight.addComponent(projectAddressLine2);*/
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
        designPartner.addValueChangeListener(this::designPartnerChanged);
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

    private void offerFieldValueChanged(Property.ValueChangeEvent valueChangeEvent)
    {
        /*proposalHeader.setOfferType(valueChangeEvent.getProperty().getValue().toString());
        proposalHeader.setOfferCode(valueChangeEvent.getProperty().getClass().getName());*/
        /*proposalHeader.setOfferCode(valueChangeEvent.getProperty().ge);*/


        String offerType = (String) offerField.getItem(offerField.getValue()).getItemProperty(OfferMaster.OFFER_NAME).getValue();
        proposalHeader.setOfferType(offerType);
        String offerCode = (String) offerField.getItem(offerField.getValue()).getItemProperty(OfferMaster.OFFER_CODE).getValue();
        proposalHeader.setOfferCode(offerCode);
        LOG.debug("MSC OFFER : " + offerCode + " : " + offerType);

    }

    private void salesPersonChanged(Property.ValueChangeEvent valueChangeEvent) {
        String phone = (String) salesPerson.getItem(salesPerson.getValue()).getItemProperty(User.PHONE).getValue();
        ((TextField) salesContact).setValue(phone);
        String email = (String) salesPerson.getItem(salesPerson.getValue()).getItemProperty(User.EMAIL).getValue();
        ((TextField) salesEmail).setValue(email);
    }

    private void designPartnerChanged(Property.ValueChangeEvent valueChangeEvent) {
        String phone = (String) designPartner.getItem(designPartner.getValue()).getItemProperty(User.PHONE).getValue();
        ((TextField) designPartnerContact).setValue(phone);
        String email = (String) designPartner.getItem(designPartner.getValue()).getItemProperty(User.EMAIL).getValue();
        ((TextField) designPartnerEmail).setValue(email);
    }

    private ComboBox getSalesPersonCombo() {
        List<User> list = proposalDataProvider.getSalesAndDesigner();
        final BeanContainer<String, User> container =
                new BeanContainer<>(User.class);
        container.setBeanIdProperty(User.NAME);
        container.addAll(list);

        ComboBox select = new ComboBox("Sales");
        select.setWidth("300px");
        select.setNullSelectionAllowed(false);
        select.setContainerDataSource(container);
        select.setItemCaptionPropertyId(User.NAME);

        if (StringUtils.isNotEmpty(this.proposalHeader.getSalesName())) {
            select.setValue(this.proposalHeader.getSalesName());
        } else if (container.size() == 1) {
            select.setValue(select.getItemIds().iterator().next());
            // proposalHeader.setSalesName((String) select.getValue());
            // proposalHeader.setSalesPhone(select.getItem(select.getValue()).getItemProperty(User.PHONE).getValue().toString());
            // proposalHeader.setSalesEmail(select.getItem(select.getValue()).getItemProperty(User.EMAIL).getValue().toString());
        }
        return select;
    }

    private ComboBox getBookingMonthCombo()
    {
        List<LookupItem> list=proposalDataProvider.getLookupItems(ProposalDataProvider.DATEAND_YEAR);
        final BeanContainer<String, LookupItem> container = new BeanContainer<>(LookupItem.class);
        container.setBeanIdProperty(LookupItem.TITLE);
        container.addAll(list);

        ComboBox select = new ComboBox("Booking Month");
        select.setWidth("300px");
        select.setNullSelectionAllowed(false);
        select.setContainerDataSource(container);
        select.setItemCaptionPropertyId(LookupItem.TITLE);

        if (StringUtils.isNotEmpty(this.proposalHeader.getBookingOrderMonth())) {
            select.setValue(this.proposalHeader.getBookingOrderMonth());
        } else if (container.size() == 1) {
            select.setValue(select.getItemIds().iterator().next());
        }

        return select;
    }
    private ComboBox getCityCombo() {
        List<LookupItem> list = proposalDataProvider.getLookupItems(ProposalDataProvider.CITY_LOOKUP);
        final BeanContainer<String, LookupItem> container =
                new BeanContainer<>(LookupItem.class);
        container.setBeanIdProperty(LookupItem.TITLE);
        container.addAll(list);

        ComboBox select = new ComboBox("Region");
        select.setWidth("300px");
        select.setNullSelectionAllowed(false);
        select.setContainerDataSource(container);
        select.setItemCaptionPropertyId(LookupItem.TITLE);

        if (StringUtils.isNotEmpty(this.proposalHeader.getPcity())) {
            select.setValue(this.proposalHeader.getPcity());
        } else if (container.size() == 1) {
            select.setValue(select.getItemIds().iterator().next());
        }

        return select;
    }

    private ComboBox getOfferCombo()
    {
        /*List<LookupItem> list = proposalDataProvider.getLookupItems(ProposalDataProvider.OFFERTYPE_LOOKUP);
        LOG.info("size of offer combo list " +list.size());
        final BeanContainer<String, LookupItem> container =
                new BeanContainer<>(LookupItem.class);
        container.setBeanIdProperty(LookupItem.TITLE);
        container.addAll(list);*/
        if (this.proposalHeader.getPriceDate() == null)
        {
            this.priceDate = new java.sql.Date(System.currentTimeMillis());
        }
        else {
            this.priceDate = this.proposalHeader.getPriceDate();
        }
        List<OfferMaster> offerMasters = proposalDataProvider.getOfferCombo(String.valueOf(priceDate), String.valueOf(priceDate));
        final BeanContainer<String, OfferMaster> container = new BeanContainer<>(OfferMaster.class);
        container.setBeanIdProperty(OfferMaster.OFFER_NAME);
        container.addAll(offerMasters);

        ComboBox select = new ComboBox("OfferMaster Type");
        select.setWidth("300px");
        select.setNullSelectionAllowed(false);
        select.setContainerDataSource(container);
        select.setItemCaptionPropertyId(OfferMaster.OFFER_NAME);
        select.setId(OfferMaster.OFFER_CODE);

        if (StringUtils.isNotEmpty(this.proposalHeader.getOfferType())) {
            select.setValue(this.proposalHeader.getOfferType());
        } else if (container.size() == 1) {
            select.setValue(select.getItemIds().iterator().next());
        }

        return select;
    }
    private ComboBox getDesignPersonCombo() {
        //List<User> list = proposalDataProvider.getDesignerUsers();
        List<User> list = proposalDataProvider.getSalesAndDesigner();
        final BeanContainer<String, User> container =
                new BeanContainer<>(User.class);
        container.setBeanIdProperty(User.NAME);
        container.addAll(list);

        ComboBox select = new ComboBox("Designer");
        select.setWidth("300px");
        select.setNullSelectionAllowed(false);
        select.setContainerDataSource(container);
        select.setItemCaptionPropertyId(User.NAME);

        if (StringUtils.isNotEmpty(this.proposalHeader.getDesignerName())) {
            select.setValue(this.proposalHeader.getDesignerName());
        } else if (container.size() == 1) {
            select.setValue(select.getItemIds().iterator().next());
            // proposalHeader.setDesignerName((String) select.getValue());
            //proposalHeader.setDesignerPhone(select.getItem(select.getValue()).getItemProperty(User.PHONE).getValue().toString());
            // proposalHeader.setDesignerEmail(select.getItem(select.getValue()).getItemProperty(User.EMAIL).getValue().toString());
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

        if (StringUtils.isNotEmpty(this.proposalHeader.getDesignPartnerName())) {
            select.setValue(this.proposalHeader.getDesignPartnerName());
        } else if (container.size() == 1) {
            select.setValue(select.getItemIds().iterator().next());
           /* proposalHeader.setDesignerName((String) select.getValue());
            proposalHeader.setDesignerPhone(select.getItem(select.getValue()).getItemProperty(User.PHONE).getValue().toString());
            proposalHeader.setDesignerEmail(select.getItem(select.getValue()).getItemProperty(User.EMAIL).getValue().toString());*/
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
        crmId.setReadOnly(true);
        formLayoutRight.addComponent(crmId);

        quotenew = new TextField("Quotation #");
        quotenew.setValue(this.proposalHeader.getQuoteNoNew());
        quotenew.setRequired(true);
       /* quotenew.setValue(this.proposalHeader.getQuoteNoNew());*/
        quotenew.setNullRepresentation("");
        formLayoutRight.addComponent(quotenew);

        /*quote = binder.buildAndBind("Quotation # (Old)", QUOTE_NO);
        ((TextField) quote).setNullRepresentation("");
        *//*quote.setRequired(true);*//*
        quote.setReadOnly(true);
        formLayoutRight.addComponent(quote);*/

        offerField = getOfferCombo();

        binder.bind(offerField, OFFER_TYPE);
        offerField.setRequired(true);
        formLayoutRight.addComponent(offerField);
        offerField.addValueChangeListener(this::offerFieldValueChanged);

        noOfDaysForWorkCompletion = binder.buildAndBind("MyGubbi Works Completion Days", ProposalHeader.NO_OF_DAYS_FOR_WORK_COMPLETION);
        noOfDaysForWorkCompletion.setRequired(true);
        ((TextField) noOfDaysForWorkCompletion).setNullRepresentation("");
        formLayoutRight.addComponent(noOfDaysForWorkCompletion);

        HorizontalLayout horizontalLayoutForCheckMiscellaneous=new HorizontalLayout();
        PHCcheck=new CheckBox("PHC");
        binder.bind(PHCcheck,PROJ_HANDLING_CHRAGES_APPLIED);
        if(proposalHeader.getProjectHandlingChargesApplied()==null)
        {
            PHCcheck.setValue(true);
            PHCcheck.setImmediate(true);
        }

        horizontalLayoutForCheckMiscellaneous.addComponent(PHCcheck);

        DCCcheck=new CheckBox("HKC");
        binder.bind(DCCcheck,DEEP_CLEANING_CHRAGES_APPLIED);
        if(proposalHeader.getDeepClearingChargesApplied()==null)
        {
            DCCcheck.setValue(true);
            DCCcheck.setImmediate(true);
        }

        horizontalLayoutForCheckMiscellaneous.addComponent(DCCcheck);

        FPCcheck=new CheckBox("FPC");
        binder.bind(FPCcheck,FLOOR_PROTECTION_CHRAGES_APPLIED);
        if(proposalHeader.getFloorProtectionChargesApplied()==null)
        {
            FPCcheck.setValue(true);
            FPCcheck.setImmediate(true);
        }
        horizontalLayoutForCheckMiscellaneous.addComponent(FPCcheck);
        formLayoutRight.addComponent(horizontalLayoutForCheckMiscellaneous);
        if(!(("admin").equals(role)) )
        {
            horizontalLayoutForCheckMiscellaneous.setVisible(false);
        }


        return formLayoutRight;
    }

    private void crmIdChanged(Property.ValueChangeEvent valueChangeEvent) {
        String crmIdValue = (String) crmId.getValue();
        List<ProposalHeader> crmIdFromOldProposals = proposalDataProvider.getProposalHeaders();
        for (ProposalHeader crmOld : crmIdFromOldProposals) {
            if ((crmOld.getCrmId()).contains(crmIdValue))
                NotificationUtil.showNotification("Quotation with same crmId already exists", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            return;
        }

    }

    private FormLayout buildMainFormLayoutLeft() {

        FormLayout formLayoutLeft = new FormLayout();
        formLayoutLeft.setSizeFull();
        formLayoutLeft.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        proposalTitleField = binder.buildAndBind("Quotation Title", ProposalHeader.TITLE);
        proposalTitleField.setRequired(true);
        ((TextField) proposalTitleField).setNullRepresentation("");

        formLayoutLeft.addComponent(proposalTitleField);
        proposalVersionField = binder.buildAndBind("Quotation Version", VERSION);
        proposalVersionField.setReadOnly(true);
        /*formLayoutLeft.addComponent(proposalVersionField);*/
        projectCityField = getCityCombo();
        binder.bind(projectCityField, P_CITY);
        projectCityField.setRequired(true);
        formLayoutLeft.addComponent(projectCityField);
        projectCityField.addValueChangeListener(this::cityChanged);

        maxDiscountPercentage = new TextField("Max Discount Percentage");
        maxDiscountPercentage.setValue(String.valueOf(proposalHeader.getMaxDiscountPercentage()));
        formLayoutLeft.addComponent(maxDiscountPercentage);

       /* noOfDaysForWorkCompletion = binder.buildAndBind("# of days for Works Completion", ProposalHeader.NO_OF_DAYS_FOR_WORK_COMPLETION);
        noOfDaysForWorkCompletion.setRequired(true);
        ((TextField) noOfDaysForWorkCompletion).setNullRepresentation("");
        formLayoutLeft.addComponent(noOfDaysForWorkCompletion);*/

        expectedDeliveryDate=new DateField("Client Expected Works Completion Date");
        expectedDeliveryDate.setWidth("50%");
        if(proposalHeader.getExpectedDeliveryDate()!=null)
        {
            try
            {
                Date date1=new SimpleDateFormat("yyyy-MM-dd").parse(proposalHeader.getExpectedDeliveryDate());
                expectedDeliveryDate.setValue(date1);
            }
            catch (Exception e)
            {
                LOG.info("Exception " +e);
            }
        }

        expectedDeliveryDate.setDateFormat("yyyy-MM-dd");
        expectedDeliveryDate.addValueChangeListener(valueChangeEvent -> {
            try{
                String dateStr = expectedDeliveryDate.getValue().toString();
                DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
                DateFormat newDateFormat = new SimpleDateFormat("YYYY-MM-dd");
                Date date = (Date)formatter.parse(dateStr);
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.setTime(date);
                String formatedDate = cal.get(java.util.Calendar.YEAR) + "-" + (cal.get(java.util.Calendar.MONTH) + 1) + "-" + cal.get(java.util.Calendar.DATE);
                String d1 = newDateFormat.format(newDateFormat.parse(formatedDate));
                /*proposalHeader.setExpectedDeliveryDate(formatedDate);*/
                dateComparision=date.before(proposalHeader.getPriceDate());
                if(!date.before(proposalHeader.getPriceDate()) || Objects.equals(proposalHeader.getExpectedDeliveryDate(),"") )
                {
                    proposalHeader.setExpectedDeliveryDate(formatedDate);
                }
            }
            catch (Exception e)
            {
                LOG.info("exception in date parsing " +e);
            }
        });
        formLayoutLeft.addComponent(expectedDeliveryDate);

        if(!(("admin").equals(role)) )
        {
            maxDiscountPercentage.setVisible(false);
        }

        bookingFormField = getBookingMonthCombo();
        binder.bind(bookingFormField, BOOKINGORDER_MONTH);
       // bookingFormField.setRequired(true);
        formLayoutLeft.addComponent(bookingFormField);
        bookingFormField.addValueChangeListener(this::bookingFormFieldValueChanged);

        OptionGroup single = new OptionGroup("Package");
        single.addItems("Yes", "No");
        binder.bind(single,PACKAGE_FLAG);
        if (proposalHeader.getPackageFlag()== null)
        {
            single.select("No");
            single.setImmediate(true);
        }
        single.addStyleName("horizontal");
        single.addValueChangeListener(this::packageselectionchanged);
        formLayoutLeft.addComponent(single);

        if(!(("admin").equals(role)) )
        {
            single.setVisible(false);
        }

        return formLayoutLeft;
    }

    private void cityChanged(Property.ValueChangeEvent valueChangeEvent) {
        LOG.info("City changed ");
        proposalHeader.setPcity(valueChangeEvent.getProperty().getValue().toString());
//        getQuoteNum(false);
        //proposalHeader.setQuoteNoNew(QuoteNumNew);
        //         quotenew.setValue(QuoteNumNew);
    }

    private void getQuoteNum(boolean override) {
        String city = (String) projectCityField.getValue();
        switch (city) {
            case "Bangalore":
                cityCode = "BLR";
                break;
            case "Chennai":
                cityCode = "CHN";
                break;
            case "Mangalore":
                cityCode = "MLR";
                break;
            case "Pune":
                cityCode = "PUN";
                break;
        }

        LocalDate today = LocalDate.now();
        month = today.getMonthValue();

        int value = 0;
        List<ProposalCity> count = proposalDataProvider.getMonthCount(month, cityCode);
        LOG.info("Count size " +count.size());
        value = count.size();

        if (override==true)
        {
            value = count.size()+1;
        }
        String valueStr = null;
        valueStr = String.format("%04d", value + 2);
        LOG.debug("Count size after override method : " + value);
        LOG.debug("Vlaue STR : " + valueStr);
        String date = new SimpleDateFormat("yyyy-MM").format(new Date());
        String s = cityCode + "-" + date + "-" + valueStr;
        for(ProposalCity proposalCity : count)
        {
            if (Objects.equals(proposalCity.getQuoteNo(), s)){
                valueStr = String.format("%04d", value + 2);
                LOG.debug("Vlaue STR inside: " + valueStr);
                s = cityCode + "-" + date + "-" + valueStr;
            }
        }
        LOG.debug("S : " + s);
        QuoteNumNew = s;
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
        List<ProposalVersion> proposalVersionList = proposalDataProvider.getProposalVersions(this.proposalHeader.getId());
        proposalVersionList.remove(event.getProposalVersion());
        versionContainer.removeAllItems();
        if (event.getProposalVersion().isEventAddStatus())
        {
            proposalVersionList.add(event.getProposalVersion());
        }
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
    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


    public final class ValoMenuItemButton extends Button {

        private static final String STYLE_SELECTED = "selected";

        private final DashboardViewType.ViewType view;

        public ValoMenuItemButton(final DashboardViewType.ViewType view) {
            this.view = view;
            setPrimaryStyleName("valo-menu-item");
            setIcon(view.getIcon());
            setCaption(view.getViewName().substring(0, 1).toUpperCase()
                    + view.getViewName().substring(1));

            DashboardEventBus.register(this);
            addClickListener(new ClickListener() {
                @Override
                public void buttonClick(final ClickEvent event) {
                    UI.getCurrent().getNavigator()
                            .navigateTo(view.getViewName());
                }
            });

        }


    }

    private void searchCRMData(Button.ClickEvent clickEvent)
    {
        callWindow();
    }
    private void callWindow()
    {
        CRMsearchWindow.open(proposalHeader);
    }

    private void bookingFormFieldValueChanged(Property.ValueChangeEvent valueChangeEvent)
    {
        List<ProposalVersion> proposalVersions = proposalDataProvider.getversion(proposalHeader.getId(),"1.0");
        LOG.info("proposal version list size " +proposalVersions.size());
        String Bookingdate=null;
        for(ProposalVersion p : proposalVersions)
        {
            if(p.getVersion().equalsIgnoreCase("1.0"))
            {
                Bookingdate=p.getBusinessDate().substring(0,10);
                /*String str = date.substring(0,10);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate dateTime = LocalDate.parse(str, formatter);
                LOG.info("get month " +dateTime.getMonth()+ "get year " +dateTime.getYear()+ "get month value " +dateTime.getMonthValue());
*/
            }
            else
            {
                Bookingdate=null;
            }
        }

        try {

            LocalDate now = LocalDate.now(); // 2015-11-24
            DateTimeFormatter currentFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate confirmDate = LocalDate.parse(Bookingdate, currentFormatter);
            confirmDate.getMonth(); // java.time.Month = OCTOBER
            confirmDate.getMonth().getValue(); // 10
            int confirmdateyear=confirmDate.getYear() % 100; // 2015
            //String str = "2015-01-15";

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate dateTime = LocalDate.parse(Bookingdate, formatter);
            LOG.info("get month " +dateTime.getMonth()+ "get year " +dateTime.getYear()+ "get month value " +dateTime.getMonthValue());
            LocalDate earlier = dateTime.minusMonths(1); // 2015-10-24
            earlier.getMonth(); // java.time.Month = OCTOBER
            earlier.getMonth().getValue(); // 10
            int year=earlier.getYear() % 100; // 2015

            selectedMonth=valueChangeEvent.getProperty().getValue().toString().substring(0,3);
            selectedYear=valueChangeEvent.getProperty().toString().substring(4,6);
            previousMonth=this.theMonth(earlier.getMonth().getValue()-1);
            previousYear=String.valueOf(year);
            BookingMonth=this.theMonth(confirmDate.getMonth().getValue()-1);
            BookingYear=String.valueOf(confirmdateyear);

            LOG.info("Selecte month " +selectedMonth+ " selected year " +selectedYear);
            LOG.info("previous month " +previousMonth+ " previous year " +previousYear);
            LOG.info("Booking month " +BookingMonth+ " BookingYear " +BookingYear);

            if((selectedMonth.equalsIgnoreCase(BookingMonth) && selectedYear.equalsIgnoreCase(BookingYear)))
            {
                //NotificationUtil.showNotification("selected month s equal to booking month", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            }else if((selectedMonth.equalsIgnoreCase(previousMonth) && selectedYear.equalsIgnoreCase(previousYear)))
            {
                //NotificationUtil.showNotification("selected month is equal to previous month", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            }
            else
            {
                NotificationUtil.showNotification("Incorrect month value 2nd case", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            }

        }
        catch (Exception e)
        {
            LOG.info("Exception in booking form field value change" +e);
        }

    }

    private void packageselectionchanged(Property.ValueChangeEvent valueChangeEvent)
    {
        proposalHeader.setPackageFlag(valueChangeEvent.getProperty().getValue().toString());
        /*proposalHeader.setAdminPackageFlag(valueChangeEvent.getProperty().getValue().toString());*/
    }

   /* private boolean validateAddonsAgainstSOW(int proposalId, String version)
    {
        List<AddonProduct> addonProducts = proposalDataProvider.getVersionAddons(proposalId,version);

        List<Proposal_sow> proposal_sows = proposalDataProvider.getProposalSowLineItems(proposalId,version);

        List<Sow_addon_map> addonsBasedOnSpaces  = proposalDataProvider.getAddonCodeBasedSpaces(L1SCode);



    }*/
   private String getCurrentDate(){
       DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
       LocalDateTime localDate = LocalDateTime.now();
       return dtf.format(localDate);
   }
    private void setComponentsReadonly()
    {
        proposalTitleField.setReadOnly(true);
        noOfDaysForWorkCompletion.setReadOnly(true);
        crmId.setReadOnly(true);
        customerNameField.setReadOnly(true);
        customerAddressLine1.setReadOnly(true);
        customerEmailField.setReadOnly(true);
        projectName.setReadOnly(true);
        projectAddressLine1.setReadOnly(true);
        projectCityField.setReadOnly(true);
        offerField.setReadOnly(true);
        salesPerson.setReadOnly(true);
        designPerson.setReadOnly(true);
        offerField.setReadOnly(true);
        maxDiscountPercentage.setReadOnly(true);
        salesEmail.setReadOnly(true);
        salesContact.setReadOnly(true);
        designEmail.setReadOnly(true);
        designContact.setReadOnly(true);
        quotenew.setReadOnly(true);
        designPartner.setReadOnly(true);
        designContact.setReadOnly(true);
        designEmail.setReadOnly(true);
        searchcrmid.setEnabled(false);
    }
}

