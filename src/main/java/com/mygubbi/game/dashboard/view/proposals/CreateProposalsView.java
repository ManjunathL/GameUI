package com.mygubbi.game.dashboard.view.proposals;


import com.mygubbi.game.dashboard.data.dummy.FileDataProviderUtil;
import com.mygubbi.game.dashboard.domain.User;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.*;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.util.Date;


/**
 * Created by test on 31-03-2016.
 */
public class CreateProposalsView extends Panel implements View {

    public static final String EDIT_ID = "proposals-edit";
    public static final String TITLE_ID = "proposals-title";

    private ProposalDataProvider proposalDataProvider = new ProposalDataProvider(new FileDataProviderUtil());

    private TextField proposal_title_field;
    private TextField crm_id;
    private TextField proposal_version_field;
    private TextField quotation_field;

    private TextField customer_id_field;
    private TextField customer_name_field;
    private TextField customer_address_line_1;
    private TextField customer_address_line_2;
    private TextField customer_address_line_3;
    private TextField customer_city_field;
    private TextField customer_email_field;
    private TextField customer_number_field_1;
    private TextField customer_number_field_2;

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

    private static final String ACTIVE = "120939";

    private Label titleLabel;
    /*private final VerticalLayout root;*/
    private Panel panel;

    private Grid grid;

    public CreateProposalsView() {

        setSizeFull();

        /*addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);*/
        DashboardEventBus.register(this);

        Accordion accordion = new Accordion();
        accordion.setCaption("Create Proposal");
        accordion.addTab(buildForm(),"Proposal Header");
        accordion.addTab(buildItemDetails(),"Item Details");
        accordion.addTab(buildForm(),"Terms and Conditions");

        setContent(accordion);

        Responsive.makeResponsive(accordion);

        // All the open sub-windows should be closed whenever the root layout
        // gets clicked.
       /* root.addLayoutClickListener(new LayoutEvents.LayoutClickListener() {
            @Override
            public void layoutClick(final LayoutEvents.LayoutClickEvent event) {
                DashboardEventBus.post(new DashboardEvent.CloseOpenWindowsEvent());
            }
        });*/
    }

    private Component buildTermsandConditions() {
        return null;
    }

    private Component buildAmount() {
        return null;
    }



    public Component buildForm()
    {
        Panel panel_form=new Panel();

        VerticalLayout verticalLayout1=new VerticalLayout();
        verticalLayout1.setSizeFull();
        verticalLayout1.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

        HorizontalLayout header = new HorizontalLayout();
        header.setSizeFull();
        header.setSpacing(true);
/*

        titleLabel = new Label("Create Proposal");
        titleLabel.setId(TITLE_ID);
        titleLabel.setSizeUndefined();
        titleLabel.addStyleName(ValoTheme.LABEL_HUGE);
        titleLabel.addStyleName(ValoTheme.LABEL_COLORED);
        header.addComponent(titleLabel);
        header.setSpacing(true);
*/

        DateField dateField = new DateField();
        dateField.setResolution(Resolution.MINUTE);
        dateField.setValue(new Date());
        dateField.setReadOnly(true);
        dateField.addStyleName(ValoTheme.DATEFIELD_ALIGN_CENTER);
        dateField.addStyleName(ValoTheme.DATEFIELD_TINY);

        Label label=new Label(dateField);
        //label.addStyleName(ValoTheme.BUTTON_LINK);
        label.addStyleName(ValoTheme.LABEL_COLORED);
        header.addComponent(label);
        header.setComponentAlignment(label, Alignment.BOTTOM_RIGHT);


        verticalLayout1.addComponent(header);

        HorizontalLayout horizontalLayout1=new HorizontalLayout();
        horizontalLayout1.setSizeFull();

        //Main Header
        FormLayout formLayout1=new FormLayout();
        formLayout1.setSizeFull();
        formLayout1.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        proposal_title_field=new TextField("Proposal Title");
        formLayout1.addComponent(proposal_title_field);
        proposal_version_field=new TextField("Proposal Version");
        formLayout1.addComponent(proposal_version_field);

        horizontalLayout1.addComponent(formLayout1);
        horizontalLayout1.setSpacing(true);

        FormLayout formLayout2=new FormLayout();
        formLayout2.setSizeFull();
        formLayout2.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        crm_id=new TextField("CRM ID");
        formLayout2.addComponent(crm_id);
        quotation_field=new TextField("Quotation");
        formLayout2.addComponent(quotation_field);

        horizontalLayout1.addComponent(formLayout2);
        verticalLayout1.addComponent(horizontalLayout1);

        //Heading
        AbsoluteLayout absoluteLayout1=new AbsoluteLayout();
        absoluteLayout1.setWidth("1000px");
        absoluteLayout1.setHeight("50px");

        Label customer_details_menu=new Label("Customer Details");
        customer_details_menu.addStyleName(ValoTheme.LABEL_HUGE);
        customer_details_menu.addStyleName(ValoTheme.LABEL_COLORED);
        absoluteLayout1.addComponent(customer_details_menu," left:20px; , top:10px;  ");
        Label project_details_menu=new Label("Project Details");
        project_details_menu.addStyleName(ValoTheme.LABEL_COLORED);
        project_details_menu.addStyleName(ValoTheme.LABEL_HUGE);
        absoluteLayout1.addComponent(project_details_menu," left:700px; , top:10px; ");

        verticalLayout1.addComponent(absoluteLayout1);
        HorizontalLayout horizontalLayout2=new HorizontalLayout();
        horizontalLayout2.setSizeFull();

        //Customer Details
        FormLayout formLayout3=new FormLayout();
        formLayout3.setSizeFull();
        formLayout3.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        customer_id_field=new TextField("Customer ID");
        formLayout3.addComponent(customer_id_field);
        customer_name_field=new TextField("Customer Name");
        formLayout3.addComponent(customer_name_field);
        customer_address_line_1=new TextField("Address Line 1");
        formLayout3.addComponent(customer_address_line_1);
        customer_address_line_2=new TextField("Address Line 2");
        formLayout3.addComponent(customer_address_line_2);
        customer_address_line_3=new TextField("Address Line 3");
        formLayout3.addComponent(customer_address_line_3);
        customer_city_field=new TextField("City");
        formLayout3.addComponent(customer_city_field);
        customer_email_field=new TextField("Email");
        formLayout3.addComponent(customer_email_field);
        customer_number_field_1=new TextField("Phone 1");
        formLayout3.addComponent(customer_number_field_1);
        customer_number_field_2=new TextField("Phone 2");
        formLayout3.addComponent(customer_number_field_2);

        horizontalLayout2.addComponent(formLayout3);
        horizontalLayout2.setSpacing(true);



        FormLayout formLayout4=new FormLayout();
        formLayout4.setSizeFull();
        formLayout4.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        project_name=new TextField("Project Name");
        formLayout4.addComponent(project_name);
        project_model_type=new TextField("Model Type");
        formLayout4.addComponent(project_model_type);
        project_drawing_id=new TextField("Drawing #");
        formLayout4.addComponent(project_drawing_id);
        project_address_line_1=new TextField("Address Line 1");
        formLayout4.addComponent(project_address_line_1);
        project_address_line_2=new TextField("Address Line 2");
        formLayout4.addComponent(project_address_line_2);
        project_city_field=new TextField("City");
        formLayout4.addComponent(project_city_field);

        horizontalLayout2.addComponent(formLayout4);
        verticalLayout1.addComponent(horizontalLayout2);


        //MyGubbi Contact Details
        Label mygubbi_details=new Label("MyGubbi Conact Details");
        mygubbi_details.addStyleName(ValoTheme.LABEL_HUGE);
        mygubbi_details.addStyleName(ValoTheme.LABEL_COLORED);
        verticalLayout1.addComponent(mygubbi_details);

        HorizontalLayout horizontalLayout3=new HorizontalLayout();
        horizontalLayout3.setSizeFull();

        FormLayout formLayout5=new FormLayout();
        formLayout5.setSizeFull();
        formLayout5.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        sales_person=new TextField("Sales Person");
        formLayout5.addComponent(sales_person);
        design_person=new TextField("Design Person");
        formLayout5.addComponent(design_person);

        horizontalLayout3.addComponent(formLayout5);
        horizontalLayout3.setSpacing(true);

        FormLayout formLayout6=new FormLayout();
        formLayout6.setSizeFull();
        formLayout6.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        sales_email=new TextField("Email");
        formLayout6.addComponent(sales_email);
        design_email=new TextField("Email");
        formLayout6.addComponent(design_email);

        horizontalLayout3.addComponent(formLayout6);
        horizontalLayout3.setSpacing(true);

        FormLayout formLayout7=new FormLayout();
        formLayout7.setSizeFull();
        formLayout7.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        sales_contact=new TextField("Phone");
        formLayout7.addComponent(sales_contact);
        design_contact=new TextField("Phone");
        formLayout7.addComponent(design_contact);

        horizontalLayout3.addComponent(formLayout7);
        horizontalLayout3.setSpacing(true);

        verticalLayout1.addComponent(horizontalLayout3);

        panel_form.setContent(verticalLayout1);

        return panel_form;
    }

    private User getCurrentUser() {
        return (User) VaadinSession.getCurrent().getAttribute(
                User.class.getName());
    }


    private Component buildItemDetails() {

        Panel panel_line_items=new Panel();

        VerticalLayout verticalLayout1=new VerticalLayout();
        verticalLayout1.setSizeFull();
        verticalLayout1.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

        HorizontalLayout horizontalLayout1=new HorizontalLayout();
        horizontalLayout1.setSizeFull();

        Button button=new Button(FontAwesome.PLUS_CIRCLE);
        button.addStyleName(ValoTheme.BUTTON_HUGE);
        button.addStyleName(ValoTheme.BUTTON_LINK);


        final User user = getCurrentUser();

        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                ItemDetailsWindow.open();


            }
        });
        horizontalLayout1.addComponent(button);
        horizontalLayout1.setComponentAlignment(button,Alignment.MIDDLE_RIGHT);

        verticalLayout1.addComponent(horizontalLayout1);
        verticalLayout1.setSpacing(true);

        grid=new Grid();
        grid.setSizeFull();

        grid.addColumn("Item #", String.class);
        grid.addColumn("Description", String.class);
        grid.addColumn("Room", String.class);
        grid.addColumn("Category", String.class);
        grid.addColumn("Material & Finish", String.class);
        grid.addColumn("Make", String.class);
        grid.addColumn("Qty", Integer.class);
        grid.addColumn("Amount(INR)", String.class);
        grid.addColumn("Actions", String.class);

        verticalLayout1.addComponent(grid);

        panel_line_items.setContent(verticalLayout1);

        return panel_line_items;
    }
/*
    public void updateGrid(String proposalId) {
        try {
            // Use the factory method of JsonContainer to instantiate the
            // data source for the table.

            JSONArray proposals = proposalDataProvider.getProposalLineItems(proposalId);

            JsonContainer dataSource = JsonContainer.Factory.newInstance(proposals.toString());
            grid.setContainerDataSource(dataSource);

            grid.setColumnReorderingAllowed(true);
            grid.setColumnOrder("s_no", "name", "room", "category", "carcass_material", "make", "quantity", "amount","total_amount");
            grid.setWidth("98%");
            grid.addStyleName(ChameleonTheme.TABLE_STRIPED);

            //grid.setHeightByRows();

*//*
            grid.setCellStyleGenerator(new Table.CellStyleGenerator() {
                @Override
                public String getStyle(Table table, Object o, Object o1) {
                    return null;
                }
            });
*//*
        } catch (IllegalArgumentException ignored) {

        }

    }*/

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

    }
}

