package com.mygubbi.game.dashboard.view.proposals;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Created by nitinpuri on 05-04-2016.
 */
public class ProposalDetailsView extends VerticalLayout {
    private String proposalId;
    private String title;

    public ProposalDetailsView(String proposalId, String title) {

        this.proposalId = proposalId;
        this.title = title;
        setSizeFull();
        addStyleName(ValoTheme.PANEL_SCROLL_INDICATOR);
        addComponent(buildHeader());
    }

    private Component buildHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("viewheader");
        header.setSpacing(true);
        Label titleLabel = new Label(this.title);
        titleLabel.addStyleName(ValoTheme.LABEL_H1);
        titleLabel.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(titleLabel);
        return header;
    }

}
