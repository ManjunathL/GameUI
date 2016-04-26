package com.mygubbi.game.dashboard.view.proposals;


import com.mygubbi.game.dashboard.data.dummy.FileDataProviderUtil;
import com.mygubbi.game.dashboard.domain.User;
import com.mygubbi.game.dashboard.event.DashboardEvent;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.mygubbi.game.dashboard.data.UserDataProvider;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class ItemDetailsWindow extends Window {

    public static final String ID = "profilepreferenceswindow";

    public UserDataProvider userDataProvider=new UserDataProvider(new FileDataProviderUtil());

    private final BeanFieldGroup<User> fieldGroup;
    /*
     * Fields for editing the User object are defined here as class members.
     * They are later bound to a FieldGroup by calling
     * fieldGroup.bindMemberFields(this). The Fields' values don't need to be
     * explicitly set, calling fieldGroup.setItemDataSource(user) synchronizes
     * the fields with the user object.
     */
    @PropertyId("itemTitle")
    private TextField itemTitle;
    @PropertyId("product")
    private ComboBox product;
    @PropertyId("carcass_material")
    private ComboBox carcassMaterial;
    @PropertyId("make")
    private ComboBox make;
    @PropertyId("type")
    private ComboBox type;
    @PropertyId("quantity")
    private TextField quantity;
    @PropertyId("shitter_material")
    private ComboBox shutter_material;
    @PropertyId("Current Password")
    private PasswordField currentPasswordField;
    @PropertyId("New Password")
    private PasswordField newPasswordField;
    @PropertyId("Confirm Password")
    private PasswordField confirmPasswordField;


    private ItemDetailsWindow() {

        addStyleName("profile-window");
        setId(ID);
        Responsive.makeResponsive(this);

        setModal(true);
        setCloseShortcut(KeyCode.ESCAPE, null);
        setResizable(true);
        setClosable(false);
        setHeight(90.0f, Unit.PERCENTAGE);

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

        detailsWrapper.addComponent(buildProfileTab());

        content.addComponent(buildFooter());

        fieldGroup = new BeanFieldGroup<User>(User.class);
        fieldGroup.bindMemberFields(this);
    }

    private Component buildProfileTab() {


        HorizontalLayout root = new HorizontalLayout();
        root.setCaption("Profile");
        root.setIcon(FontAwesome.USER);
        root.setWidth(100.0f, Unit.PERCENTAGE);
        root.setSpacing(true);
        root.setMargin(true);
        root.addStyleName("profile-form");

        FormLayout details = new FormLayout();
        details.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        root.addComponent(details);
        root.setExpandRatio(details, 1);

        itemTitle = new TextField("Item Title");
        details.addComponent(itemTitle);
        product = new ComboBox("Product");
        details.addComponent(product);
        carcassMaterial = new ComboBox("Carcass Material");
        details.addComponent(carcassMaterial);
        make = new ComboBox("Make");
        details.addComponent(make);

        return root;
    }

    private Component buildFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Unit.PERCENTAGE);

        Button ok = new Button("OK");
        ok.addStyleName(ValoTheme.BUTTON_PRIMARY);
        ok.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    fieldGroup.commit();
                    // Updated user should also be persisted to database. But
                    // not in this demo.

                    Notification success = new Notification(
                            "Profile updated successfully");
                    success.setDelayMsec(2000);
                    success.setStyleName("bar success small");
                    success.setPosition(Position.BOTTOM_CENTER);
                    success.show(Page.getCurrent());

                    DashboardEventBus.post(new DashboardEvent.ProfileUpdatedEvent());
                    close();
                } catch (CommitException e) {
                    Notification.show("Error while updating profile",
                            Type.ERROR_MESSAGE);
                }

            }
        });
        ok.focus();
        footer.addComponent(ok);
        footer.setComponentAlignment(ok, Alignment.TOP_RIGHT);
        footer.setSpacing(true);
        Button close=new Button("Cancel");
        close.addStyleName(ValoTheme.BUTTON_PRIMARY);
        close.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                close_window();
            }
        });
        close.focus();
        footer.addComponent(close);


        return footer;
    }

    public static void open() {
        DashboardEventBus.post(new DashboardEvent.CloseOpenWindowsEvent());
        Window w = new ItemDetailsWindow();
        UI.getCurrent().addWindow(w);
        w.focus();
    }
    public static void close_window() {
        DashboardEventBus.post(new DashboardEvent.CloseOpenWindowsEvent());
/*
        Window w = new CreateProposalsView();

        UI.getCurrent().removeWindow(w);
*/

    }
}