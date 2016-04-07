package com.mygubbi.game.dashboard.component;

import com.mygubbi.game.dashboard.domain.Proposal;
import com.mygubbi.game.dashboard.domain.User;
import com.mygubbi.game.dashboard.event.DashboardEvent;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.util.Date;

/**
 * Created by test on 31-03-2016.
 */
public class ProposalsWindow extends Window {

    public static final String ID = "proposalswindow";

    private final BeanFieldGroup<Proposal> fieldGroup ;

    @PropertyId("name")
    private TextField name_field;
    @PropertyId("proposal")
    private TextField proposal_field;
    @PropertyId("create_date")
    private DateField create_date_field;
    @PropertyId("status")
    private OptionGroup status_field;
    @PropertyId("last_assigned_to")
    private TextField last_assigned_to_field;
    @PropertyId("customer_name")
    private TextField customer_name_field;
    @PropertyId("amount")
    private TextField amount_field;

    public ProposalsWindow() {

        addStyleName("profile_window");
        setId(ID);
        Responsive.makeResponsive(this);

        setModal(true);
        setCloseShortcut(ShortcutAction.KeyCode.ESCAPE, null);
        setResizable(false);
        setClosable(false);
        setHeight(70.0f, Unit.PERCENTAGE);
        setWidth(50.0f, Unit.PERCENTAGE);

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

    }

    private Component buildProposalsTab() {
        HorizontalLayout root = new HorizontalLayout();
        root.setCaption("Proposals");
        root.setIcon(FontAwesome.PENCIL);
        root.setWidth(100.0f, Unit.PERCENTAGE);
        root.setSpacing(true);
        root.setMargin(true);
        root.addStyleName("profile-form");

        FormLayout details = new FormLayout();
        details.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        root.addComponent(details);
        root.setExpandRatio(details, 1);

        name_field = new TextField("Name");
        details.addComponent(name_field);

        proposal_field = new TextField("Proposal");
        details.addComponent(proposal_field);

        create_date_field = new DateField("Date");
        details.addComponent(create_date_field);

        status_field = new OptionGroup("Status");
        details.addComponent(status_field);

        last_assigned_to_field = new TextField("Last Assigned to");
        details.addComponent(last_assigned_to_field);

        customer_name_field = new TextField("Customer Name");
        details.addComponent(customer_name_field);

        amount_field = new TextField("Amount");
        details.addComponent(amount_field);

        return root;
    }

    public static void open() {
        DashboardEventBus.post(new DashboardEvent.CloseOpenWindowsEvent());
        Window w = new ProposalsWindow();
        UI.getCurrent().addWindow(w);
        w.focus();
    }


}
