package com.mygubbi.game.dashboard.view.proposals;

import com.mygubbi.game.dashboard.ServerManager;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.domain.PriceMaster;
import com.mygubbi.game.dashboard.domain.ProposalHeader;
import com.mygubbi.game.dashboard.domain.ProposalVersion;
import com.mygubbi.game.dashboard.domain.User;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.vaadin.data.Property;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vaadin.dialogs.ConfirmDialog;

import java.sql.Date;

/**
 * Created by Shruthi on 12/12/2017.
 */
public class Miscellaneous extends Window
{
    private ProposalDataProvider proposalDataProvider = ServerManager.getInstance().getProposalDataProvider();
    private static final Logger LOG = LogManager.getLogger(Miscellaneous.class);
    ProposalVersion proposalVersion;
    ProposalHeader proposalHeader;
    private Date priceDate;
    private String city;
    Label PHCRate,DCCRate,FPCRate;
    TextField PHCQTY,DCCQTY,FPCQTY;
    TextField PHCAmount,DCCAmount,FPCAmount;
    double projectHandlingChargesRate,deepCleaningChargesRate,floorProtectionChargesRate;
    Label projectHandlingLabel,deepClearingLabel,floorProtectionLabel;
    String userEmail = ((User) VaadinSession.getCurrent().getAttribute(User.class.getName())).getEmail();
    public static void open(ProposalVersion proposalVersion, ProposalHeader proposalHeader)
    {
        Miscellaneous miscellaneous=new Miscellaneous(proposalVersion,proposalHeader);
        UI.getCurrent().addWindow(miscellaneous);
        miscellaneous.focus();
    }
    Miscellaneous(ProposalVersion proposalVersion,ProposalHeader proposalHeader)
    {
        DashboardEventBus.register(this);
        this.proposalHeader=proposalHeader;
        this.proposalVersion=proposalVersion;
        LOG.info("proposal version in miscellaneous class " +proposalVersion);
        this.priceDate=proposalHeader.getPriceDate();
        this.city = proposalHeader.getPcity();
        if (this.priceDate == null)
        {
            this.priceDate = new Date(System.currentTimeMillis());
        }
        setModal(true);
        setSizeFull();

        VerticalLayout verticalLayout = new VerticalLayout();
        Responsive.makeResponsive(this);

        updateTotal();


        HorizontalLayout titleLayout=new HorizontalLayout();
        titleLayout.setMargin(new MarginInfo(false, true, true, true));
        titleLayout.setSizeFull();
        Label customerDetailsLabel = new Label("Miscellaneous Charges");
        customerDetailsLabel.addStyleName(ValoTheme.LABEL_HUGE);
        customerDetailsLabel.addStyleName(ValoTheme.LABEL_COLORED);
        customerDetailsLabel.addStyleName("products-and-addons-heading-text");
        titleLayout.addComponent(customerDetailsLabel);
        verticalLayout.addComponent(titleLayout);
        verticalLayout.setComponentAlignment(titleLayout,Alignment.TOP_CENTER);

        HorizontalLayout horizontalLayout2 = new HorizontalLayout();
        horizontalLayout2.setMargin(new MarginInfo(false, false, false, false));
        horizontalLayout2.setSizeFull();
        horizontalLayout2.addComponent(buildHeading());
        verticalLayout.addComponent(horizontalLayout2);
        verticalLayout.setComponentAlignment(horizontalLayout2,Alignment.TOP_CENTER);
        horizontalLayout2.setHeightUndefined();

        HorizontalLayout horizontalLayout3 = new HorizontalLayout();
        horizontalLayout2.setMargin(new MarginInfo(false, false, false, false));
        horizontalLayout2.setSizeFull();
        horizontalLayout2.addComponent(buildLayout2());
        verticalLayout.addComponent(horizontalLayout3);
        verticalLayout.setComponentAlignment(horizontalLayout3,Alignment.TOP_CENTER);
        horizontalLayout2.setHeightUndefined();

        HorizontalLayout horizontalLayout4 = new HorizontalLayout();
        horizontalLayout2.setMargin(new MarginInfo(false, false, false, false));
        horizontalLayout2.setSizeFull();
        horizontalLayout2.addComponent(buildLayout3());
        verticalLayout.addComponent(horizontalLayout4);
        verticalLayout.setComponentAlignment(horizontalLayout4,Alignment.TOP_CENTER);
        horizontalLayout2.setHeightUndefined();
        PHCQTY.addValueChangeListener(this::projectHandlingchargesQuantityChanged);
        DCCQTY.addValueChangeListener(this::deepCleaningchargesQuantityChanged);
        FPCQTY.addValueChangeListener(this::floorProtectionQuantityChanged);

        HorizontalLayout horizontalLayout5 = new HorizontalLayout();
        horizontalLayout2.setMargin(new MarginInfo(false, false, false, false));
        horizontalLayout2.setSizeFull();
        horizontalLayout2.addComponent(buildLayout4());
        verticalLayout.addComponent(horizontalLayout5);
        verticalLayout.setComponentAlignment(horizontalLayout5,Alignment.TOP_CENTER);
        horizontalLayout2.setHeightUndefined();

        Component componentactionbutton = buildActionButtons();
        verticalLayout.addComponent(componentactionbutton);

        setContent(verticalLayout);

    }

    private void projectHandlingchargesQuantityChanged(Property.ValueChangeEvent valueChangeEvent)
    {
        PHCAmount.setValue(String.valueOf(Double.valueOf(PHCQTY.getValue()) * projectHandlingChargesRate));
    }
    private void deepCleaningchargesQuantityChanged(Property.ValueChangeEvent valueChangeEvent)
    {
        DCCAmount.setValue(String.valueOf(Double.valueOf(DCCQTY.getValue()) * deepCleaningChargesRate));
    }
    private void floorProtectionQuantityChanged(Property.ValueChangeEvent valueChangeEvent)
    {
        FPCAmount.setValue(String.valueOf(Double.valueOf(FPCQTY.getValue()) * floorProtectionChargesRate));
    }

    public Component buildHeading()
    {
        VerticalLayout verticalLayout=new VerticalLayout();
        verticalLayout.setSpacing(true);
        verticalLayout.setSpacing(true);
        verticalLayout.setSpacing(true);
        verticalLayout.setMargin(new MarginInfo(false,true,false,true));

        Label heading=new Label("Service Title");
        heading.addStyleName("margin-label-style");
        verticalLayout.addComponent(heading);

        projectHandlingLabel=new Label("Project Handling Charges");
        projectHandlingLabel.addStyleName("margin-label-style");
        verticalLayout.addComponent(projectHandlingLabel);
        verticalLayout.setComponentAlignment(projectHandlingLabel,Alignment.MIDDLE_CENTER);

        deepClearingLabel=new Label("Deep Clearing Charges");
        deepClearingLabel.addStyleName("margin-label-style");
        verticalLayout.addComponent(deepClearingLabel);
        verticalLayout.setComponentAlignment(deepClearingLabel,Alignment.MIDDLE_CENTER);

        floorProtectionLabel=new Label("Floor Protection Charges(per Sqft)");
        floorProtectionLabel.addStyleName("margin-label-style");
        verticalLayout.addComponent(floorProtectionLabel);
        verticalLayout.setComponentAlignment(floorProtectionLabel,Alignment.MIDDLE_CENTER);

        return verticalLayout;
    }

    public Component buildLayout2()
    {
        VerticalLayout verticalLayout=new VerticalLayout();
        verticalLayout.setSpacing(true);
        verticalLayout.setMargin(new MarginInfo(false,true,false,true));

        Label heading=new Label();
        heading.addStyleName("margin-label-style2");
        heading.setValue("Rate");
        verticalLayout.addComponent(heading);

        PHCRate =new Label();
        PHCRate.addStyleName("margin-label-style2");
        PHCRate.setValue(String.valueOf(projectHandlingChargesRate*100)+"%");
        verticalLayout.addComponent(PHCRate);

        DCCRate =new Label();
        DCCRate.addStyleName("margin-label-style2");
        DCCRate.setValue(String.valueOf(deepCleaningChargesRate));
        verticalLayout.addComponent(DCCRate);

        FPCRate = new Label();
        FPCRate.addStyleName("margin-label-style2");
        FPCRate.setReadOnly(true);
        FPCRate.setValue(String.valueOf(floorProtectionChargesRate));
        verticalLayout.addComponent(FPCRate);
        verticalLayout.setComponentAlignment(FPCRate,Alignment.MIDDLE_CENTER);

        return verticalLayout;
    }

    public Component buildLayout3()
    {
        VerticalLayout verticalLayout=new VerticalLayout();
        verticalLayout.setSpacing(true);
        verticalLayout.setMargin(new MarginInfo(false,true,false,true));


        Label heading=new Label();
        heading.addStyleName("margin-label-style2");
        heading.setValue("Quantity");
        verticalLayout.addComponent(heading);

        PHCQTY =new TextField();
        PHCQTY.addStyleName("heighttext");
        PHCQTY.addStyleName("margin-label-style2");
        PHCQTY.setValue(String.valueOf(proposalVersion.getFinalAmount()));
        verticalLayout.addComponent(PHCQTY);

        DCCQTY =new TextField();
        DCCQTY.addStyleName("margin-label-style2");
        DCCQTY.addStyleName("heighttext");
        DCCQTY.setValue("1");
        verticalLayout.addComponent(DCCQTY);

        FPCQTY = new TextField();
        FPCQTY.addStyleName("margin-label-style2");
        FPCQTY.addStyleName("heighttext");
        FPCQTY.setValue("1");
        verticalLayout.addComponent(FPCQTY);

        return verticalLayout;
    }

    public Component buildLayout4()
    {
        VerticalLayout verticalLayout=new VerticalLayout();
        verticalLayout.setSpacing(true);
        verticalLayout.setMargin(new MarginInfo(false,true,false,true));

        Label heading=new Label();
        heading.addStyleName("margin-label-style2");
        heading.setValue("Amount");
        verticalLayout.addComponent(heading);

        PHCAmount =new TextField();
        PHCAmount.addStyleName("heighttext");
        PHCAmount.addStyleName("margin-label-style2");
        PHCAmount.setValue(String.valueOf(Double.valueOf(PHCQTY.getValue()) * projectHandlingChargesRate));
        verticalLayout.addComponent(PHCAmount);

        DCCAmount =new TextField();
        DCCAmount.addStyleName("margin-label-style2");
        DCCAmount.addStyleName("heighttext");
        DCCAmount.setValue(String.valueOf(Double.valueOf(DCCQTY.getValue()) * deepCleaningChargesRate));
        verticalLayout.addComponent(DCCAmount);

        FPCAmount = new TextField();
        FPCAmount.addStyleName("margin-label-style2");
        FPCAmount.addStyleName("heighttext");
        FPCAmount.setValue(String.valueOf(Double.valueOf(FPCQTY.getValue()) * floorProtectionChargesRate));
        verticalLayout.addComponent(FPCAmount);

        return verticalLayout;
    }

    public void updateTotal()
    {
        PriceMaster projectHandlingCharges=proposalDataProvider.getFactorRatePriceDetails("PHC",this.priceDate,this.city);
        projectHandlingChargesRate=projectHandlingCharges.getPrice();
        PriceMaster deepCleaningCharges=proposalDataProvider.getFactorRatePriceDetails("DCC",this.priceDate,this.city);
        deepCleaningChargesRate=deepCleaningCharges.getPrice();
        PriceMaster floorProtectionCharges=proposalDataProvider.getFactorRatePriceDetails("FPC",this.priceDate,this.city);
        floorProtectionChargesRate=floorProtectionCharges.getPrice();
    }

    private Component buildActionButtons() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setMargin(new MarginInfo(true, true, true, true));
        horizontalLayout.setSizeFull();

        HorizontalLayout right = new HorizontalLayout();

        Button saveButton = new Button("Save");
        saveButton.addStyleName(ValoTheme.BUTTON_SMALL);
        saveButton.addStyleName("margin-right-10-for-headerlevelbutton");
        saveButton.addClickListener(this::save);
        right.addComponent(saveButton);
        right.setComponentAlignment(saveButton, Alignment.MIDDLE_RIGHT);

        Button closeButton = new Button("Close");
        closeButton.addStyleName(ValoTheme.BUTTON_SMALL);
        closeButton.addStyleName("margin-right-10-for-headerlevelbutton");
        closeButton.addClickListener(this::close);
        right.addComponent(closeButton);
        right.setComponentAlignment(closeButton, Alignment.MIDDLE_RIGHT);

        horizontalLayout.addComponent(right);
        horizontalLayout.setComponentAlignment(right, Alignment.MIDDLE_CENTER);

        return horizontalLayout;
    }

    private void save(Button.ClickEvent clickEvent)
    {

        String string1="{\"services\": [{\"proposalId\": " + proposalHeader.getId() + "," + "\"fromVersion\": " + proposalVersion.getVersion() + " , " + "\"serviceTitle\":\"" +projectHandlingLabel.getValue() +"\"" + "," + "\"quantity\":" + PHCQTY.getValue() + "," + "\"amount\":" +PHCAmount.getValue()+ "," + "\"updatedBy\":\"" +userEmail+ "\""+ "},";
        String string2="{\"proposalId\": " + proposalHeader.getId() + "," + "\"fromVersion\": " + proposalVersion.getVersion() + " , " + "\"serviceTitle\":\"" +deepClearingLabel.getValue() + "\"" +"," + "\"quantity\":" +DCCQTY.getValue() + "," + "\"amount\":" +DCCAmount.getValue()+ "," + "\"updatedBy\":\"" +userEmail+"\""+"},";
        String string3="{\"proposalId\": " + proposalHeader.getId() + "," + "\"fromVersion\": " + proposalVersion.getVersion() + " , " + "\"serviceTitle\":\"" +floorProtectionLabel.getValue() +"\"" +"," + "\"quantity\":" + FPCQTY.getValue() + "," + "\"amount\":" +FPCAmount.getValue()+ "," + "\"updatedBy\":\"" +userEmail+ "\"" +"}]}";
        StringBuilder servicesJson=new StringBuilder(string1);
        servicesJson.append(string2);
        servicesJson.append(string3);
        LOG.info("final JSON " +servicesJson.toString());
        proposalDataProvider.miscellenous(servicesJson.toString());
    }

    private void close(Button.ClickEvent clickEvent)
    {
        ConfirmDialog.show(UI.getCurrent(), "", "Do you want to close this screen? All unsaved data will be lost",
                "Yes", "No", dialog -> {
                    if (!dialog.isCanceled()) {
                        DashboardEventBus.unregister(this);
                        close();

                    }
                });
    }
}
