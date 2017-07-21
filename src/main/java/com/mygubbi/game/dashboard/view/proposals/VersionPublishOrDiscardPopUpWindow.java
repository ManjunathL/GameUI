package com.mygubbi.game.dashboard.view.proposals;


import com.mygubbi.game.dashboard.ServerManager;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.domain.*;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.mygubbi.game.dashboard.event.ProposalEvent;
import com.mygubbi.game.dashboard.view.NotificationUtil;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vaadin.dialogs.ConfirmDialog;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by User on 12-07-2017.
 */
public class VersionPublishOrDiscardPopUpWindow extends Window {

    private static final Logger LOG = LogManager.getLogger(VersionPublishOrDiscardPopUpWindow.class);


    private ProposalDataProvider proposalDataProvider = ServerManager.getInstance().getProposalDataProvider();
    private ProposalHeader proposalHeader;
    private ProposalVersion proposalVersion;
    private ProductAndAddonSelection productAndAddonSelection;
    JSONObject quoteFile = null;

    private  Proposal proposal;

    private int proposalId;
    private String version;
    private JSONObject response;
    private String status;
    String id ;


    public VersionPublishOrDiscardPopUpWindow(JSONObject response, int proposalId,String version, Proposal proposal, ProposalVersion proposalVersion, ProductAndAddonSelection productAndAddonSelection) {

        this.proposalId = proposalId;
        this.version = version;
        this.response = response;
        this.proposal = proposal;
        this.proposalVersion = proposalVersion;
        this.productAndAddonSelection = productAndAddonSelection;


        setModal(true);
        removeCloseShortcut(ShortcutAction.KeyCode.ESCAPE);
        setWidth("45%");
        setHeight("35%");
        setClosable(false);

        setContent(buildMainWindow());
        DashboardEventBus.register(this);

    }

    private Component buildMainWindow() {
        VerticalLayout verticalLayout1 = new VerticalLayout();
        verticalLayout1.setSizeFull();
        verticalLayout1.setMargin(new MarginInfo(true,true,true,true));

        verticalLayout1.setSpacing(true);

        try {
            Label label_message = new Label();
            label_message.setCaption(response.getString("comments"));
            verticalLayout1.addComponent(label_message);
        }catch (JSONException e){
            e.printStackTrace();
        }
//        Label label_message_2 = new Label();
//        label_message_2.setCaption(" Please review them before proceeding further");
////        verticalLayout1.addComponent(label_message_2);
//        verticalLayout1.setSpacing(false);

//        Label label_message_3 = new Label();
//        try {
////            label_message_3.setCaption(" <h2> Comments : </h2>" + response.getString("comments"));
//            label_message_3.setCaption(response.getString("comments"));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        label_message_3.addStyleName("comments_label_style");
//        label_message_3.setCaptionAsHtml(true);
//        verticalLayout1.addComponent(label_message_3);

        HorizontalLayout horizontalLayout1 = new HorizontalLayout();
        horizontalLayout1.setSizeFull();
        horizontalLayout1.setMargin(new MarginInfo(true,true,true,true));


        Button open_sheet = new Button();
        open_sheet.setCaption("Open SOW Sheet");
        open_sheet.addStyleName(ValoTheme.BUTTON_PRIMARY);

        open_sheet.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                NotificationUtil.showNotification("Generating the Scope of services sheet v1.0",NotificationUtil.STYLE_BAR_SUCCESS_SMALL);
                createSOWandOpenSOWPopup();
                close();
            }
        });

        horizontalLayout1.addComponent(open_sheet);
        horizontalLayout1.setExpandRatio(open_sheet,0.3f);
        horizontalLayout1.setSpacing(true);

        Button close = new Button();
        close.setCaption("Close");
        close.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        close.addStyleName("close_button_wrap");

        horizontalLayout1.addComponent(close);
        horizontalLayout1.setExpandRatio(close,0.2f);
        horizontalLayout1.setSpacing(true);

        close.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                close();
            }
        });

        Button discard_sheet = new Button(); //
        discard_sheet.setCaption("Ignore changes and Publish");

        discard_sheet.addStyleName(ValoTheme.BUTTON_DANGER);

//        discard_sheet.addStyleName("ignore_button");

        discard_sheet.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                saveProposalVersion();
                proposalDataProvider.publishVersionOverride(version,proposalId);
                proposalVersion.setStatus(ProposalVersion.ProposalStage.Published.name());
                proposalVersion.setInternalStatus(ProposalVersion.ProposalStage.Published.name());
                DashboardEventBus.post(new ProposalEvent.VersionCreated(proposalVersion));
                NotificationUtil.showNotification("Version published successfully",NotificationUtil.STYLE_BAR_SUCCESS_SMALL);
                Collection<Window> windows = UI.getCurrent().getWindows();
                Object[] window = windows.toArray();
                Window window1 = (Window) window[0];
                UI.getCurrent().removeWindow(window1);
                close();
            }
        });

        horizontalLayout1.addComponent(discard_sheet);
        horizontalLayout1.setExpandRatio(discard_sheet,0.5f);

        horizontalLayout1.setSpacing(true);

        verticalLayout1.addComponent(horizontalLayout1);

        return verticalLayout1;
    }


    private void createSOWandOpenSOWPopup() {
        List<ProposalVersion> proposalVersions = proposal.getVersions();
        String readOnlyFlag = "no";

        double versionToBeConsidered = Double.parseDouble(proposalVersions.get(0).getVersion());
        List<String> versions = new ArrayList<String>();

        for (ProposalVersion proposalVersion : proposalVersions)
        {
            if (proposalVersion.getVersion().equals("1.0") || proposalVersion.getVersion().startsWith("0.")){
                versions.add(proposalVersion.getVersion());
            }
            if (proposalVersion.getVersion().contains("2."))
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
                if (Double.parseDouble(version) > Double.parseDouble(versions.get(0)))
                    versionToBeConsidered = Double.parseDouble(version);
            }
        }
        LOG.debug("Version to be considered : " + versionToBeConsidered);
        productAndAddonSelection.setFromVersion(String.valueOf(versionToBeConsidered));


        JSONObject quoteFile = proposalDataProvider.updateSowLineItems(proposalId,versionToBeConsidered,readOnlyFlag);
        LOG.debug("Quote file :" + quoteFile);
        SOWPopupWindow.open(proposalHeader,productAndAddonSelection,quoteFile);
    }

    private void saveProposalVersion() {
        try
        {
            String disAmount= response.getString("discountAmount");
            proposalVersion.setAmount(response.getDouble("grandTotal"));
            proposalVersion.setFinalAmount(response.getDouble("discountTotal"));
            proposalVersion.setDiscountAmount(response.getDouble(disAmount.replace(",","")));
            proposalVersion.setDiscountPercentage(response.getDouble("discountPercentage"));
            proposalVersion.setRemarks(response.getString("remarksTextArea"));
            proposalVersion.setTitle(response.getString("ttitle"));

            proposalHeader.setStatus(proposalVersion.getStatus());
            proposalHeader.setVersion(response.getString("versionNum"));

            boolean success = proposalDataProvider.saveProposal(proposalHeader);
            if (success)
            {
                NotificationUtil.showNotification("Saved successfully!", NotificationUtil.STYLE_BAR_SUCCESS_SMALL);

            }
            else
            {
                NotificationUtil.showNotification("Cannot Save Proposal!!", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                return;

            }

            proposalVersion = proposalDataProvider.updateVersion(proposalVersion);
            NotificationUtil.showNotification("Saved successfully!", NotificationUtil.STYLE_BAR_SUCCESS_SMALL);
        }
        catch (Exception e)
        {
            NotificationUtil.showNotification("Couldn't Save Proposal! Please contact GAME Admin.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            LOG.info("Exception :" + e.toString());
        }
    }

    public static void open(JSONObject response, int proposalId, String version, Proposal proposal, ProposalVersion proposalVersion, ProductAndAddonSelection productAndAddonSelection) {
        Window w = new VersionPublishOrDiscardPopUpWindow(response,proposalId,version, proposal, proposalVersion, productAndAddonSelection);
        UI.getCurrent().addWindow(w);
        w.focus();

    }
}
