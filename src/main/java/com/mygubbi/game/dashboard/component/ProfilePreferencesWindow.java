package com.mygubbi.game.dashboard.component;

import com.mygubbi.game.dashboard.data.RestDataProviderUtil;
import com.mygubbi.game.dashboard.data.UserDataProvider;
import com.mygubbi.game.dashboard.domain.User;
import com.mygubbi.game.dashboard.event.DashboardEvent;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.mygubbi.game.dashboard.view.NotificationUtil;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class ProfilePreferencesWindow extends Window {

    public static final String ID = "profilepreferenceswindow";

    public UserDataProvider userDataProvider = new UserDataProvider(new RestDataProviderUtil());

    private final BeanFieldGroup<User> fieldGroup;
    /*
     * Fields for editing the User object are defined here as class members.
     * They are later bound to a FieldGroup by calling
     * fieldGroup.bindMemberFields(this). The Fields' values don't need to be
     * explicitly set, calling fieldGroup.setItemDataSource(user) synchronizes
     * the fields with the user object.
     */
    @PropertyId("name")
    private TextField nameField;
    @PropertyId("email")
    private TextField emailField;
    @PropertyId("phone")
    private TextField phoneField;
    @PropertyId("Current Password")
    private PasswordField currentPasswordField;
    @PropertyId("New Password")
    private PasswordField newPasswordField;
    @PropertyId("Confirm Password")
    private PasswordField confirmPasswordField;




    private ProfilePreferencesWindow(final User user,
            final boolean preferencesTabOpen) {

        addStyleName("profile-window");
        setId(ID);
        Responsive.makeResponsive(this);

        setModal(true);
        setCloseShortcut(KeyCode.ESCAPE, null);
        setResizable(false);
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
        detailsWrapper.addComponent(buildSettingsTab());

        if (preferencesTabOpen) {
            detailsWrapper.setSelectedTab(1);
        }

        content.addComponent(buildFooter());

        fieldGroup = new BeanFieldGroup<User>(User.class);
        fieldGroup.bindMemberFields(this);
        fieldGroup.setItemDataSource(user);
    }

    private Component buildSettingsTab() {

        VerticalLayout root = new VerticalLayout();
        root.setCaption("Settings");
        root.setIcon(FontAwesome.COGS);
        root.setSpacing(true);
        root.setMargin(true);
        root.setSizeFull();

        FormLayout details = new FormLayout();
        details.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        root.addComponent(details);
        root.setExpandRatio(details, 1);

        Label message = new Label("Change Password");
        details.addComponent(message);

        currentPasswordField = new PasswordField("Currnet Password");
        details.addComponent(currentPasswordField);
        newPasswordField = new PasswordField("New Password");
        details.addComponent(newPasswordField);
        confirmPasswordField = new PasswordField("Confirm Password");
        details.addComponent(confirmPasswordField);
        return root;

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

        nameField = new TextField("Name");
        details.addComponent(nameField);

        Label section = new Label("Contact Info");
        section.addStyleName(ValoTheme.LABEL_H4);
        section.addStyleName(ValoTheme.LABEL_COLORED);
        details.addComponent(section);

        emailField = new TextField("Email");
        emailField.setWidth("100%");
       // emailField.setRequired(true);
       // emailField.setNullRepresentation("");
        emailField.setDescription("enter your email");
        emailField.setReadOnly(true);
        details.addComponent(emailField);


        phoneField = new TextField("Phone");
        phoneField.setWidth("100%");
        phoneField.setNullRepresentation("");
        details.addComponent(phoneField);


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

                    User user = (User) VaadinSession.getCurrent().getAttribute(User.class.getName());
                    String newPassword = newPasswordField.getValue();
                    String confirmPassword = confirmPasswordField.getValue();
                    String oldPassword = currentPasswordField.getValue();

                    if (!oldPassword.isEmpty() && !newPassword.isEmpty()) {
                        if (newPassword.equals(confirmPassword)) {
                            boolean success = userDataProvider.changePassword(user.getEmail(), oldPassword, newPassword);
                            if (success) {
                                NotificationUtil.showNotification("Password changed successfully", NotificationUtil.STYLE_BAR_SUCCESS_SMALL);
                                DashboardEventBus.post(new DashboardEvent.ProfileUpdatedEvent());
                                close();
                            } else {
                                NotificationUtil.showNotification("Incorrect password, please check!", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                            }
                        } else
                            NotificationUtil.showNotification("New and Confirm Passwords do not match!", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                    }

                } catch (CommitException e) {
                    Notification.show("Error while updating password",
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

    public static void open(final User user, final boolean preferencesTabActive) {
        DashboardEventBus.post(new DashboardEvent.CloseOpenWindowsEvent());
        Window w = new ProfilePreferencesWindow(user, preferencesTabActive);
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
