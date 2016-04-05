package com.mygubbi.game.dashboard.view.proposals;




import com.google.common.eventbus.Subscribe;
import com.mygubbi.game.dashboard.event.DashboardEvent;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.mygubbi.game.dashboard.DashboardUI;

import com.mygubbi.game.dashboard.view.proposals.ProposalsEdit.DashboardEditListener;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;

import com.vaadin.navigator.View;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;

import com.vaadin.ui.*;

import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public final class ProposalsView extends TabSheet implements View,
        DashboardEditListener {

    public static final String EDIT_ID = "proposals-edit";
    public static final String TITLE_ID = "proposals-title";

    private Label titleLabel;
    private final VerticalLayout root;

    public ProposalsView() {
        addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
        setSizeFull();
        DashboardEventBus.register(this);

        root = new VerticalLayout();
        root.setSizeFull();
        root.setMargin(true);
        root.setCaption("All Proposals");
        root.addStyleName("proposals-view");
        addTab(root);
        Responsive.makeResponsive(root);

        root.addComponent(buildHeader());
/*
        Button test = new Button("Open Proposal");
        final String title = "Kitchen for Sanjay Gupta, Durga Solitaire";
        final ProposalDetailsView proposalDetailsView = new ProposalDetailsView("123", title);

        test.addClickListener(event -> {
            addTab(proposalDetailsView).setClosable(true);
            setSelectedTab(getComponentCount() - 1);
            getTab(proposalDetailsView).setCaption(title);
        });
        root.addComponent(test);
*/

        // All the open sub-windows should be closed whenever the root layout
        // gets clicked.
        root.addLayoutClickListener(new LayoutClickListener() {
            @Override
            public void layoutClick(final LayoutClickEvent event) {
                DashboardEventBus.post(new DashboardEvent.CloseOpenWindowsEvent());
            }
        });
    }


    private Component buildHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("viewheader");
        header.setSpacing(true);

        titleLabel = new Label("Proposals");
        titleLabel.setId(TITLE_ID);
       // titleLabel.setSizeUndefined();
        titleLabel.addStyleName(ValoTheme.LABEL_H1);
        titleLabel.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(titleLabel);


        return header;
    }


    @Override
    public void dashboardNameEdited(final String name) {
        titleLabel.setValue(name);
    }

    @Override
    public void enter(ViewChangeEvent viewChangeEvent) {

    }


    public static final class NotificationsButton extends Button {
        private static final String STYLE_UNREAD = "unread";
        public static final String ID = "dashboard-notifications";

        public NotificationsButton() {
            setIcon(FontAwesome.BELL);
            setId(ID);
            addStyleName("notifications");
            addStyleName(ValoTheme.BUTTON_ICON_ONLY);
            DashboardEventBus.register(this);
        }

        @Subscribe
        public void updateNotificationsCount(
                final DashboardEvent.NotificationsCountUpdatedEvent event) {
            setUnreadCount(DashboardUI.getDataProvider()
                    .getUnreadNotificationsCount());
        }

        public void setUnreadCount(final int count) {
            setCaption(String.valueOf(count));

            String description = "Notifications";
            if (count > 0) {
                addStyleName(STYLE_UNREAD);
                description += " (" + count + " unread)";
            } else {
                removeStyleName(STYLE_UNREAD);
            }
            setDescription(description);
        }
    }

}
