package com.mygubbi.game.dashboard.view;

import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.ui.Notification;

/**
 * Created by nitinpuri on 26-04-2016.
 */
public class NotificationUtil {

    public static String STYLE_BAR_ERROR_SMALL = "bar error small";
    public static String STYLE_BAR_SUCCESS_SMALL = "bar success small";
    public static String STYLE_BAR_FAILURE_SMALL = "bar failure small";
    public static String STYLE_BAR_WARNING_SMALL = "bar warning small";

    public static void showNotification(String caption, String styleName) {
        Notification notification = new Notification(
                caption);
        notification.setDelayMsec(2000);
        notification.setStyleName(styleName);
        notification.setPosition(Position.BOTTOM_CENTER);
        notification.show(Page.getCurrent());
    }

}
