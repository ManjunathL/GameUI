package com.mygubbi.game.dashboard.data;

import com.mygubbi.game.dashboard.domain.DashboardNotification;
import com.mygubbi.game.dashboard.domain.User;

import java.util.Collection;

/**
 * Created by nitinpuri on 05-04-2016.
 */
public interface CommonDataProvider {
    /**
     * @param count
     *            Number of transactions to fetch.
     * @return A Collection of most recent transactions.
     */
    /**
     * @param userName
     * @param password
     * @return Authenticated used.
     */
    User authenticate(String userName, String password);

    /**
     * @return The number of unread notifications for the current user.
     */
    int getUnreadNotificationsCount();

    /**
     * @return Notifications for the current user.
     */
    Collection<DashboardNotification> getNotifications();

    double getTotalSum();

}
