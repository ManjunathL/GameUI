package com.mygubbi.game.dashboard.view.proposals;


import com.mygubbi.game.dashboard.ServerManager;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.domain.*;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.mygubbi.game.dashboard.view.NotificationUtil;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;

/**
 * Created by User on 12-07-2017.
 */
public class SOWPopupWindow extends Window {

    private ProposalDataProvider proposalDataProvider = ServerManager.getInstance().getProposalDataProvider();
    private ProposalHeader proposalHeader;
    private ProductAndAddonSelection productAndAddonSelection;
    JSONObject quoteFile = null;
    String id ;


    public SOWPopupWindow(ProposalHeader proposalHeader, ProductAndAddonSelection productAndAddonSelection, JSONObject quoteFile) {

        this.proposalHeader = proposalHeader;
        this.productAndAddonSelection = productAndAddonSelection;
        this.quoteFile = quoteFile;
        try {
            id = quoteFile.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setModal(true);
        removeCloseShortcut(ShortcutAction.KeyCode.ESCAPE);
        setWidth("35%");
        setHeight("20%");
        setClosable(false);

        setContent(buildMainWindow());
        DashboardEventBus.register(this);

    }

    private Component buildMainWindow() {
        VerticalLayout verticalLayout1 = new VerticalLayout();
        verticalLayout1.setSizeFull();
        verticalLayout1.setMargin(new MarginInfo(true,true,true,true));

        verticalLayout1.setSpacing(true);

        Label label_message = new Label();
        label_message.setCaption("Click on the Open Sheet button to edit the Scope Of Services.");
        label_message.addStyleName(ValoTheme.LABEL_BOLD);
        label_message.addStyleName(ValoTheme.LABEL_H2);
        verticalLayout1.addComponent(label_message);

        Label label_message_2 = new Label();
        label_message_2.setCaption("Please click on save or discard after making the changes");
        label_message_2.addStyleName(ValoTheme.LABEL_BOLD);
        label_message_2.addStyleName(ValoTheme.LABEL_H2);
        verticalLayout1.addComponent(label_message_2);

        HorizontalLayout horizontalLayout1 = new HorizontalLayout();
        horizontalLayout1.setSizeFull();

        Button open_sheet = new Button();
        open_sheet.setCaption("Open Sheet");
        open_sheet.addStyleName(ValoTheme.BUTTON_PRIMARY);

        open_sheet.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                try {
                    BrowserWindowOpener opener = new BrowserWindowOpener(new ExternalResource(quoteFile.getString("driveWebViewLink")));
                    opener.setFeatures("");
                    opener.extend(open_sheet);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        horizontalLayout1.addComponent(open_sheet);
        horizontalLayout1.setSpacing(true);

        Button save_sheet = new Button();
        save_sheet.setCaption("Save");
        save_sheet.addStyleName(ValoTheme.BUTTON_PRIMARY);

        horizontalLayout1.addComponent(save_sheet);
        horizontalLayout1.setSpacing(true);

        save_sheet.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                Proposal_sow proposal_sow = new Proposal_sow();
                proposal_sow.setProposalId(productAndAddonSelection.getProposalId());
                proposal_sow.setId(id);
                try {
                    proposal_sow.setVersion(quoteFile.getString("version"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONObject saved = proposalDataProvider.saveSOWFile(proposal_sow);
                if (!(saved == null))
                {
                    NotificationUtil.showNotification("Saved Successfully",NotificationUtil.STYLE_BAR_SUCCESS_SMALL);
                    DashboardEventBus.unregister(this);
                    close();
                }
                else {
                    NotificationUtil.showNotification("Problem occured while saving the file",NotificationUtil.STYLE_BAR_SUCCESS_SMALL);
                }
            }
        });

        Button discard_sheet = new Button();
        discard_sheet.setCaption("Discard");
        discard_sheet.addStyleName(ValoTheme.BUTTON_DANGER);

        discard_sheet.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                DashboardEventBus.unregister(this);
                close();

            }
        });

        horizontalLayout1.addComponent(discard_sheet);
        horizontalLayout1.setSpacing(true);

        verticalLayout1.addComponent(horizontalLayout1);

        return verticalLayout1;
    }

    public static void open(ProposalHeader proposalHeader,ProductAndAddonSelection productAndAddonSelection, JSONObject quoteFile) {
        Window w = new SOWPopupWindow(proposalHeader,productAndAddonSelection,quoteFile);
        UI.getCurrent().addWindow(w);
        w.focus();

    }
}
