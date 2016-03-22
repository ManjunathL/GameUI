package com.mygubbi.game.dashboard.data;

import java.util.Collection;

import com.mygubbi.game.dashboard.domain.DashboardNotification;
import com.mygubbi.game.dashboard.domain.User;

/**
 * QuickTickets Dashboard backend API.
 */
public interface DataProvider {
    /**
     * @param count
     *            Number of transactions to fetch.
     * @return A Collection of most recent transactions.
     */

    /**
     * @param id
     *            Movie identifier.
     * @return A Collection of daily revenues for the movie.
     */


    /**
     * @return Total revenues for each listed movie.
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

    /**
     * @return The total summed up revenue of sold movie tickets
     */
    double getTotalSum();

    /**
     * @return A Collection of movies.
     */


    /**
     * @param movieId
     *            Movie's identifier
     * @return A Movie instance for the given id.
     */


    /**
     * @param startDate
     * @param endDate
     * @return A Collection of Transactions between the given start and end
     *         dates.
     */

}
