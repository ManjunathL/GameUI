package com.mygubbi.game.dashboard.view.proposals;


import com.mygubbi.game.dashboard.ServerManager;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.domain.ProductAndAddonSelection;
import com.mygubbi.game.dashboard.domain.ProposalHeader;
import com.mygubbi.game.dashboard.domain.Proposal_boq;
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
public class SOPopupWindow extends Window {


    JSONObject quoteFile = null;
    String id;


    public SOPopupWindow(JSONObject quoteFile) {


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
        open_sheet.setCaption("Open Link");
        open_sheet.addStyleName(ValoTheme.BUTTON_PRIMARY);

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


        try {
            BrowserWindowOpener opener = new BrowserWindowOpener(new ExternalResource(quoteFile.getString("driveWebViewLink")));
            opener.setFeatures("");
            opener.extend(open_sheet);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        horizontalLayout1.addComponent(open_sheet);
        horizontalLayout1.setSpacing(true);

        Button discard_sheet = new Button();
        discard_sheet.setCaption("Close");
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

    public static void open(JSONObject quoteFile) {
        Window w = new SOPopupWindow(quoteFile);
        UI.getCurrent().addWindow(w);
        w.focus();

    }
}
