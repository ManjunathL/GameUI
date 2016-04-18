package com.mygubbi.game.dashboard.component;




import com.google.gwt.thirdparty.javascript.rhino.head.ast.FunctionNode;
import com.mygubbi.game.dashboard.domain.Proposal;
import com.mygubbi.game.dashboard.event.DashboardEvent;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import com.mygubbi.game.*;




/**
 * Created by test on 31-03-2016.
 */
public class CreateProposalsView implements View {

    public static final String ID = "proposalswindow";

    private BeanFieldGroup<Proposal> fieldGroup ;


    @PropertyId("proposal_title")
    private TextField proposal_title_field;
    @PropertyId("crm_id")
    private TextField crm_id;
    @PropertyId("proposal_version")
    private TextField proposal_version_field;
    @PropertyId("quotation")
    private TextField quotation_field;

    @PropertyId("customer_id")
    private TextField customer_id_field;
    @PropertyId("customer_name")
    private TextField customer_name_field;
    @PropertyId("address_line_1")
    private TextField address_line_1;
    @PropertyId("address_line_2")
    private TextField address_line_2;
    @PropertyId("address_line_3")
    private TextField address_line_3;
    @PropertyId("city")
    private TextField city_field;
    @PropertyId("email")
    private TextField email_field;
    @PropertyId("phone_1")
    private TextField number_field_1;
    @PropertyId("phone_2")
    private TextField number_field_2;

    @PropertyId("project_name")
    private TextField project_name;
    @PropertyId("model_type")
    private TextField project_model_type;
    @PropertyId("drawing_id")
    private TextField project_drawing_id;
    @PropertyId("address_line_1")
    private TextField project_address_line_1;
    @PropertyId("address_line_2")
    private TextField project_address_line_2;
    @PropertyId("city")
    private TextField project_city_field;

    @PropertyId("sales_person")
    private TextField sales_person;
    @PropertyId("sales_email")
    private TextField sales_email;
    @PropertyId("sales_contact")
    private TextField sales_contact;
    @PropertyId("design_person")
    private TextField design_person;
    @PropertyId("design_email")
    private TextField design_email;
    @PropertyId("design_contact")
    private TextField design_contact;



    @PropertyId("amount")
    private TextField amount_field;

    public CreateProposalsView() {

/*
        addStyleName("profile_window");
        setId(ID);
        Responsive.makeResponsive(this);

        setModal(true);
        setCloseShortcut(ShortcutAction.KeyCode.ESCAPE, null);
        setResizable(false);
        setClosable(false);
        setHeight(90.0f, Sizeable.Unit.PERCENTAGE);
        setWidth(45.0f, Sizeable.Unit.PERCENTAGE);

        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setMargin(new MarginInfo(true, false, false, false));
        setContent(content);

        TabSheet detailsWrapper = new TabSheet();
        detailsWrapper.setSizeFull();
        detailsWrapper.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
        detailsWrapper.addStyleName(ValoTheme.TABSHEET_ICONS_ON_TOP);
        detailsWrapper.addStyleName(ValoTheme.TABSHEET_CENTERED_TABS);
        content.addComponent(detailsWrapper);
        content.setExpandRatio(detailsWrapper, 1f);

        detailsWrapper.addComponent(buildProposalsTab());



        fieldGroup = new BeanFieldGroup<Proposal>(Proposal.class);
        fieldGroup.bindMemberFields(this);
*/

    }

    private Component buildProposalsTab() {
        VerticalLayout root = new VerticalLayout();
        root.setCaption("Proposals");
        root.setIcon(FontAwesome.PENCIL);
        root.setWidth(100.0f, Sizeable.Unit.PERCENTAGE);
        root.setSpacing(true);
        root.setMargin(true);
        root.addStyleName("profile-form");

        //Main Header
        Label main_header_label=new Label("Main Header");
        main_header_label.addStyleName(ValoTheme.LABEL_BOLD);
        root.addComponent(main_header_label);

        HorizontalLayout header_main=new HorizontalLayout();

        FormLayout creation_details1 = new FormLayout();
        creation_details1.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        FormLayout creation_details2 = new FormLayout();
        creation_details2.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);


        proposal_title_field=new TextField("Proposal title");
        creation_details1.addComponent(proposal_title_field);

        proposal_version_field=new TextField("Proposal version");
        creation_details2.addComponent(proposal_version_field);

        crm_id = new TextField("CRM #");
        creation_details1.addComponent(crm_id);

        quotation_field=new TextField("Quotation");
        creation_details2.addComponent(quotation_field);

        header_main.addComponent(creation_details1);
        header_main.setSpacing(true);
        header_main.addComponent(creation_details2);

        root.addComponent(header_main);

        AbsoluteLayout custproj=new AbsoluteLayout();
        custproj.setWidth("600px");
        custproj.setHeight("50px");

        //Customer and Project Details
        Label custproj_label1=new Label("Customer Details");
        custproj_label1.addStyleName(ValoTheme.LABEL_BOLD);

        Label custproj_label2=new Label("Project Details");
        custproj_label2.addStyleName(ValoTheme.LABEL_BOLD);

        custproj.addComponent(custproj_label1,"top:0px;left:0px;");

        custproj.addComponent(custproj_label2,"top:0px;left:350px;");

        root.addComponent(custproj);


        HorizontalLayout cust_proj_main=new HorizontalLayout();

        FormLayout customer_details = new FormLayout();
        customer_details.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
       /* root.addComponent(customer_details);
        root.setExpandRatio(customer_details, 1);*/

        customer_name_field=new TextField("Customer Name");
        customer_details.addComponent(customer_name_field);

        customer_id_field=new TextField("Customer Id");
        customer_details.addComponent(customer_id_field);

        address_line_1=new TextField("Address Line 1");
        customer_details.addComponent(address_line_1);

        address_line_2=new TextField("Address Line 2");
        customer_details.addComponent(address_line_2);

        address_line_3=new TextField("Address Line 3");
        customer_details.addComponent(address_line_3);

        city_field=new TextField("City");
        customer_details.addComponent(city_field);

        email_field=new TextField("Email");
        email_field.addValidator(new EmailValidator("Please enter a valid Email address"));
        customer_details.addComponent(email_field);

        number_field_1=new TextField("Phone 1");
        customer_details.addComponent(number_field_1);

        number_field_2=new TextField("Phone 2");
        customer_details.addComponent(number_field_2);

        FormLayout project_details = new FormLayout();
        project_details.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
       /* root.addComponent(project_details);
        root.setExpandRatio(project_details, 1);*/

        project_name=new TextField("Project Title");
        project_details.addComponent(project_name);

        project_model_type=new TextField("Model Type");
        project_details.addComponent(project_model_type);

        project_drawing_id=new TextField("Drawing #");
        project_details.addComponent(project_drawing_id);

        project_address_line_1=new TextField("Address Line 1");
        project_details.addComponent(project_address_line_1);

        project_address_line_2=new TextField("Address Line 2");
        project_details.addComponent(project_address_line_2);

        project_city_field=new TextField("City");
        project_details.addComponent(project_city_field);

        cust_proj_main.addComponent(customer_details);
        cust_proj_main.setSpacing(true);
        cust_proj_main.addComponent(project_details);
        root.addComponent(cust_proj_main);


        //Mygubbi Details
        Label mygubbi_det_label=new Label("Mygubbi Contact Details");
        mygubbi_det_label.addStyleName(ValoTheme.LABEL_BOLD);
        root.addComponent(mygubbi_det_label);

        HorizontalLayout mygubbi_det_main=new HorizontalLayout();

        FormLayout mygubbi_details1 = new FormLayout();
        mygubbi_details1.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
       /* root.addComponent(mygubbi_details);
        root.setExpandRatio(mygubbi_details, 1);*/

        FormLayout mygubbi_details2 = new FormLayout();
        mygubbi_details2.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        FormLayout mygubbi_details3 = new FormLayout();
        mygubbi_details3.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);



        sales_person=new TextField("Sales Person");
        mygubbi_details1.addComponent(sales_person);
        sales_email=new TextField("Email");
        mygubbi_details1.addComponent(sales_email);
        sales_contact=new TextField("Contact");
        mygubbi_details1.addComponent(sales_contact);

        design_person=new TextField("Design Person");
        mygubbi_details2.addComponent(design_person);
        design_email=new TextField("Email");
        mygubbi_details2.addComponent(design_email);
        design_contact=new TextField("Contact");
        mygubbi_details2.addComponent(design_contact);

        mygubbi_det_main.addComponent(mygubbi_details1);
        mygubbi_det_main.setSpacing(true);
        mygubbi_det_main.addComponent(mygubbi_details2);
        mygubbi_det_main.setSpacing(true);
        mygubbi_det_main.addComponent(mygubbi_details3);
        mygubbi_det_main.setSpacing(true);


        root.addComponent(mygubbi_det_main);

        root.addComponent(buildFooter());

        return root;
    }



    public Component buildFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Sizeable.Unit.PERCENTAGE);

        Button close=new Button("Close");
        close.addStyleName(ValoTheme.BUTTON_PRIMARY);
        close.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                close_window();
            }
        });
        close.focus();
        footer.addComponent(close);
        footer.setComponentAlignment(close, Alignment.TOP_RIGHT);

        return footer;
    }

    public static void open() {
        DashboardEventBus.post(new DashboardEvent.CloseOpenWindowsEvent());
/*
        Window w = new CreateProposalsView();
        UI.getCurrent().addWindow(w);
        w.focus();
*/
    }
    public static void close_window() {
        DashboardEventBus.post(new DashboardEvent.CloseOpenWindowsEvent());
/*
        Window w = new CreateProposalsView();
        w.getParent();
        UI.getCurrent().removeWindow(w);
*/

    }


    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

    }
}
