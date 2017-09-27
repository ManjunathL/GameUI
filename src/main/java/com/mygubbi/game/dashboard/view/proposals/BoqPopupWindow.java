package com.mygubbi.game.dashboard.view.proposals;


import com.mygubbi.game.dashboard.ServerManager;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.domain.ProductAndAddonSelection;
import com.mygubbi.game.dashboard.domain.ProposalHeader;
import com.mygubbi.game.dashboard.domain.Proposal_boq;
import com.mygubbi.game.dashboard.domain.Proposal_sow;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.mygubbi.game.dashboard.view.NotificationUtil;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;

/**
 * Created by User on 12-07-2017.
 */
public class BoqPopupWindow extends Window {

    private static final Logger LOG = LogManager.getLogger(BoqPopupWindow.class);


    private ProposalDataProvider proposalDataProvider = ServerManager.getInstance().getProposalDataProvider();
    private ProposalHeader proposalHeader;
    private ProductAndAddonSelection productAndAddonSelection;
    JSONObject quoteFile = null;
    String id;


    public BoqPopupWindow(ProposalHeader proposalHeader, ProductAndAddonSelection productAndAddonSelection, JSONObject quoteFile) {

        this.proposalHeader = proposalHeader;
        this.productAndAddonSelection = productAndAddonSelection;
        this.quoteFile = quoteFile;

        this.quoteFile = quoteFile;
        try {
            id = quoteFile.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setModal(true);
        removeCloseShortcut(ShortcutAction.KeyCode.ESCAPE);
        setWidth("40%");
        setHeight("20%");
        setClosable(false);

        setContent(buildMainWindow());

    }

    private Component buildMainWindow() {
        VerticalLayout verticalLayout1 = new VerticalLayout();
        verticalLayout1.setSizeFull();
        verticalLayout1.setMargin(new MarginInfo(true, true, true, true));


        Label label_message = new Label();
        label_message.setCaption("Click on the link to open the file. " +
                "After all the changes are made please click on Save or Discard accordingly");
        label_message.addStyleName(ValoTheme.LABEL_BOLD);
        label_message.addStyleName(ValoTheme.LABEL_H2);
        verticalLayout1.addComponent(label_message);

        HorizontalLayout horizontalLayout1 = new HorizontalLayout();
        horizontalLayout1.setSizeFull();

        Button open_sheet = new Button();
        open_sheet.setCaption("Open Sheet");
        open_sheet.addStyleName(ValoTheme.BUTTON_PRIMARY);
        productAndAddonSelection.setFromVersion("2.0");

        open_sheet.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                try {
                    getUI().getPage().open(quoteFile.getString("driveWebViewLink"), "googleSheet");
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
                Proposal_boq proposal_boq = new Proposal_boq();
                proposal_boq.setProposalId(proposalHeader.getId());
                proposal_boq.setId(id);
                try {
                    JSONObject saved = proposalDataProvider.saveBoQFile(proposal_boq);

                    if (saved.getString("status").equalsIgnoreCase("success")) {
                        NotificationUtil.showNotification("SOW details saved Successfully", NotificationUtil.STYLE_BAR_SUCCESS_SMALL);
                        DashboardEventBus.unregister(this);
                        close();
                    } else {
                        NotificationUtil.showNotification(saved.getString("comments"), NotificationUtil.STYLE_BAR_ERROR_SMALL);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                NotificationUtil.showNotification("File Saved Successfully", NotificationUtil.STYLE_BAR_SUCCESS_SMALL);

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
        Window w = new BoqPopupWindow(proposalHeader,productAndAddonSelection,quoteFile);
        UI.getCurrent().addWindow(w);
        w.focus();

    }
}
