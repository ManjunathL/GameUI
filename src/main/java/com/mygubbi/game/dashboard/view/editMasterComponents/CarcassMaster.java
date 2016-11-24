package com.mygubbi.game.dashboard.view.editMasterComponents;

import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Responsive;
import com.vaadin.ui.*;
import com.vaadin.ui.Component;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import java.awt.*;

import java.awt.Label;
import java.awt.Panel;


/**
 * Created by test on 27-06-2016.
 */
public class CarcassMaster extends Window implements View {

    public static final String TITLE_ID = "AccessoryHardwareMaster-title";

    VerticalLayout root;
    private com.vaadin.ui.Label titleLabel;

    public CarcassMaster(){
        addStyleName(ValoTheme.PANEL_BORDERLESS);
        setSizeFull();
        DashboardEventBus.register(this);

        root = new VerticalLayout();
        root.setSizeFull();
        root.setMargin(true);

        Responsive.makeResponsive(root);

        Component header = buildHeader();
        root.addComponent(header);
        root.setExpandRatio(header, 0.2f);
    }



    private Component buildHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setSpacing(true);

        titleLabel = new com.vaadin.ui.Label("Carcass Master");
        titleLabel.setId(TITLE_ID);
        // titleLabel.setSizeUndefined();
        titleLabel.addStyleName(ValoTheme.LABEL_H1);
        titleLabel.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(titleLabel);
        return header;
    }

    public static void open() {
        com.vaadin.ui.Window w = new AccessoryHardwareMaster();
        UI.getCurrent().addWindow(w);
        w.focus();
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

    }
}
