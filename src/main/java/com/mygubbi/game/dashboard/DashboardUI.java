package com.mygubbi.game.dashboard;

import com.google.common.eventbus.Subscribe;
import com.mygubbi.game.dashboard.data.UserDataProvider;
import com.mygubbi.game.dashboard.domain.User;
import com.mygubbi.game.dashboard.event.DashboardEvent;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.mygubbi.game.dashboard.view.LoginView;
import com.mygubbi.game.dashboard.view.MainView;
import com.mygubbi.game.dashboard.view.NotificationUtil;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.*;
import com.vaadin.server.Page.BrowserWindowResizeEvent;
import com.vaadin.server.Page.BrowserWindowResizeListener;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;

import java.util.Locale;

@Theme("dashboard")
@Widgetset("com.mygubbi.game.dashboard.DashboardWidgetSet")
@Title("MyGubbi GAME")
@SuppressWarnings("serial")
public final class DashboardUI extends UI {
    /*
     * This field stores an access to the dummy backend layer. In real
     * applications you most likely gain access to your beans trough lookup or
     * injection; and not in the UI but somewhere closer to where they're
     * actually accessed.
     */
    private final UserDataProvider dataProvider = ServerManager.getInstance().getUserDataProvider();
    private final DashboardEventBus dashboardEventbus = new DashboardEventBus();
    private static final Logger LOG = LogManager.getLogger(DashboardUI.class);

    @Override
    protected void init(final VaadinRequest request) {
        setLocale(Locale.US);
        


        DashboardEventBus.register(this);
        Responsive.makeResponsive(this);

        setErrorHandler((ErrorHandler) event -> {
            LOG.error("Error", event.getThrowable());
            //NotificationUtil.showNotification("Internal Error");
        });

        addStyleName(ValoTheme.UI_WITH_MENU);

        updateContent();

        // Some views need to be aware of browser resize events so a
        // BrowserResizeEvent gets fired to the event bus on every occasion.
        Page.getCurrent().addBrowserWindowResizeListener(
                new BrowserWindowResizeListener() {
                    @Override
                    public void browserWindowResized(
                            final BrowserWindowResizeEvent event) {
                        DashboardEventBus.post(new DashboardEvent.BrowserResizeEvent());
                    }
                });
    }

    /**
     * Updates the correct content for this UI based on the current user status.
     * If the user is logged in with appropriate privileges, main view is shown.
     * Otherwise login view is shown.
     */
    private void updateContent() {
        User user = (User) VaadinSession.getCurrent().getAttribute(
                User.class.getName());
        if (user != null) {
            // Authenticated user
            setContent(new MainView());
            removeStyleName("loginview");
            getNavigator().navigateTo(getNavigator().getState());
        } else {
            setContent(new LoginView());
            addStyleName("loginview");
        }
    }

    @Subscribe
    public void userLoginRequested(final DashboardEvent.UserLoginRequestedEvent event) {
        try {
            JSONObject userObject = getDataProvider().authUser(event.getUserName(), event.getPassword());
            User user = getUser(userObject);
            VaadinSession.getCurrent().setAttribute(User.class.getName(), user);
            updateContent();
        } catch (Exception e) {
            e.printStackTrace();
            NotificationUtil.showNotification("Invalid User or Password!", NotificationUtil.STYLE_BAR_ERROR_SMALL);
        }
    }

    private User getUser(JSONObject userObject) throws JSONException {
        return new User(userObject.getString("email"), userObject.getString("role"), userObject.optString("phone", ""), userObject.getString("name"));
    }

    @Subscribe
    public void userLoggedOut(final DashboardEvent.UserLoggedOutEvent event) {
        // When the user logs out, current VaadinSession gets closed and the
        // page gets reloaded on the login screen. Do notice the this doesn't
        // invalidate the current HttpSession.
        VaadinSession.getCurrent().close();
        Page.getCurrent().reload();
    }

    @Subscribe
    public void closeOpenWindows(final DashboardEvent.CloseOpenWindowsEvent event) {
        for (Window window : getWindows()) {
            window.close();
        }
    }

    /**
     * @return An instance for accessing the (dummy) services layer.
     */
    public static UserDataProvider getDataProvider() {
        return ((DashboardUI) getCurrent()).dataProvider;
    }

    public static DashboardEventBus getDashboardEventbus() {
        return ((DashboardUI) getCurrent()).dashboardEventbus;
    }
}
