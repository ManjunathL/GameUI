package com.mygubbi.game.dashboard.view.proposals;


import com.mygubbi.game.dashboard.event.DashboardEvent;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.LayoutEvents;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.server.Sizeable;
import com.vaadin.server.WebBrowser;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ChameleonTheme;
import com.vaadin.ui.themes.Runo;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.teemu.jsoncontainer.JsonContainer;
import us.monoid.json.JSONArray;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;

import java.util.Date;


/**
 * Created by test on 31-03-2016.
 */
public class CreateProposalsView extends Panel implements View {

    public static final String EDIT_ID = "proposals-edit";
    public static final String TITLE_ID = "proposals-title";


    private TextField proposal_title_field;
    private TextField crm_id;
    private TextField proposal_version_field;
    private TextField quotation_field;

    private TextField customer_id_field;
    private TextField customer_name_field;
    private TextField address_line_1;
    private TextField address_line_2;
    private TextField address_line_3;
    private TextField city_field;
    private TextField email_field;
    private TextField number_field_1;
    private TextField number_field_2;

    private TextField project_name;
    private TextField project_model_type;
    private TextField project_drawing_id;
    private TextField project_address_line_1;
    private TextField project_address_line_2;
    private TextField project_city_field;

    private TextField sales_person;
    private TextField sales_email;
    private TextField sales_contact;
    private TextField design_person;
    private TextField design_email;
    private TextField design_contact;

    private Label titleLabel;
    private final VerticalLayout root;
    private Panel panel;

    public CreateProposalsView() {

        addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
        DashboardEventBus.register(this);


        root = new VerticalLayout();
        /*root.setSizeFull();*/
        root.setMargin(true);
        root.setCaption("All Proposals");

        setContent(root);

        Responsive.makeResponsive(root);

        root.addComponent(buildHeader());
        root.setSpacing(true);

        // All the open sub-windows should be closed whenever the root layout
        // gets clicked.
        root.addLayoutClickListener(new LayoutEvents.LayoutClickListener() {
            @Override
            public void layoutClick(final LayoutEvents.LayoutClickEvent event) {
                DashboardEventBus.post(new DashboardEvent.CloseOpenWindowsEvent());
            }
        });

    }


    public Component buildHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setSpacing(true);

        titleLabel = new Label("Create Proposal");
        titleLabel.setId(TITLE_ID);
        titleLabel.setSizeUndefined();
        titleLabel.addStyleName(ValoTheme.LABEL_H1);
        titleLabel.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(titleLabel);
        header.setSpacing(true);

        DateField dateField = new DateField();
        dateField.setResolution(Resolution.MINUTE);
        dateField.setValue(new Date());
        dateField.setReadOnly(true);
        dateField.addStyleName(ValoTheme.DATEFIELD_ALIGN_CENTER);
        dateField.addStyleName(ValoTheme.DATEFIELD_TINY);

        header.addComponent(dateField);


        return header;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

    }


}
