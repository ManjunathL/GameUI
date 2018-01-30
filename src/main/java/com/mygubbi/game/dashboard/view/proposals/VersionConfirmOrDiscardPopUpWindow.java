package com.mygubbi.game.dashboard.view.proposals;


import com.mygubbi.game.dashboard.ServerManager;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.domain.ProductAndAddonSelection;
import com.mygubbi.game.dashboard.domain.Proposal;
import com.mygubbi.game.dashboard.domain.ProposalHeader;
import com.mygubbi.game.dashboard.domain.ProposalVersion;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.mygubbi.game.dashboard.view.NotificationUtil;
import com.vaadin.event.ShortcutAction;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;

import java.util.List;

/**
 * Created by User on 12-07-2017.
 */
public class VersionConfirmOrDiscardPopUpWindow extends Window {

    private static final Logger LOG = LogManager.getLogger(VersionConfirmOrDiscardPopUpWindow.class);


    private ProposalDataProvider proposalDataProvider = ServerManager.getInstance().getProposalDataProvider();
    private ProposalHeader proposalHeader;
    private ProposalVersion proposalVersion;
    private ProductAndAddonSelection productAndAddonSelection;
    JSONObject quoteFile = null;


    String id;
    //    private TextArea remarksOnIgnore;
    private JSONObject textValues;


    public VersionConfirmOrDiscardPopUpWindow(ProposalVersion proposalVersion, ProductAndAddonSelection productAndAddonSelection, ProposalHeader proposalHeader) {


        this.proposalVersion = proposalVersion;
        this.productAndAddonSelection = productAndAddonSelection;
        this.proposalHeader = proposalHeader;


        setModal(true);
        removeCloseShortcut(ShortcutAction.KeyCode.ESCAPE);
        setWidth("40%");
        setHeight("40%");
        setClosable(false);

        setContent(buildMainWindow());
        DashboardEventBus.register(this);

    }

    private Component buildMainWindow() {
        VerticalLayout verticalLayout1 = new VerticalLayout();
        verticalLayout1.setSizeFull();
        verticalLayout1.setMargin(new MarginInfo(true, true, false, true));

//        verticalLayout1.setSpacing(true);

        Label label_message = new Label();
        label_message.setCaption(proposalVersion.getResponseMessage());
//            label_message.setWidth("75px");
        label_message.addStyleName("captiontext");
        label_message.addStyleName("width:500px;");

        verticalLayout1.addComponent(label_message);
        verticalLayout1.setExpandRatio(label_message, 0.65f);
        verticalLayout1.setWidth("500px");

        HorizontalLayout horizontalLayout1 = new HorizontalLayout();
        horizontalLayout1.setSizeFull();
        horizontalLayout1.setMargin(new MarginInfo(false, true, true, true));


        Button open_sheet = new Button();
        open_sheet.setCaption("Open Scope of Services Sheet");
        open_sheet.addStyleName(ValoTheme.BUTTON_PRIMARY);

        open_sheet.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                NotificationUtil.showNotification("Generating the Scope of services sheet v1.0", NotificationUtil.STYLE_BAR_SUCCESS_SMALL);
                createSOWandOpenSOWPopup();
                close();
            }
        });

        horizontalLayout1.addComponent(open_sheet);
        horizontalLayout1.setExpandRatio(open_sheet, 0.5f);
        horizontalLayout1.setSpacing(true);

        Button close = new Button();
        close.setCaption("Close");
        close.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        close.addStyleName("close_button_wrap");

        horizontalLayout1.addComponent(close);
        horizontalLayout1.setExpandRatio(close, 0.5f);
        horizontalLayout1.setSpacing(true);

        close.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                close();
            }
        });


        verticalLayout1.addComponent(horizontalLayout1);
        verticalLayout1.setExpandRatio(horizontalLayout1, 0.35f);

        return verticalLayout1;
    }


    private void createSOWandOpenSOWPopup() {
        List<ProposalVersion> proposalVersions = proposalDataProvider.getProposalVersions(proposalVersion.getProposalId());
        String readOnlyFlag = "no";

        double versionToBeConsidered = Double.parseDouble(proposalVersion.getVersion());


        LOG.debug("Version to be considered : " + versionToBeConsidered);
        productAndAddonSelection.setFromVersion(String.valueOf(versionToBeConsidered));


        JSONObject quoteFile = null;
        try {
            quoteFile = proposalDataProvider.updateSowLineItems(proposalVersion.getProposalId(), versionToBeConsidered, readOnlyFlag);
            if (quoteFile.getString("status").equalsIgnoreCase("failure")) {
                NotificationUtil.showNotification("Couldn't create SOW file. Please click on the button to generate the sheet again", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            LOG.error("Couldnt create Sow File :" + e.getMessage());
        }
        SOWPopupWindow.open(proposalHeader, productAndAddonSelection, quoteFile);
    }


    public static void open(ProposalVersion proposalVersion, ProductAndAddonSelection productAndAddonSelection, ProposalHeader proposalHeader) {
        Window w = new VersionConfirmOrDiscardPopUpWindow(proposalVersion, productAndAddonSelection, proposalHeader);
        UI.getCurrent().addWindow(w);
        w.focus();

    }
}
