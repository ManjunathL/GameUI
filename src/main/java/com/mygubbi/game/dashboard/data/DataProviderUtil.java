package com.mygubbi.game.dashboard.data;

import java.util.Collection;
import java.util.Map;

import com.mygubbi.game.dashboard.domain.DashboardNotification;
import com.mygubbi.game.dashboard.domain.User;
import us.monoid.json.JSONArray;
import us.monoid.json.JSONObject;

/**
 * Dashboard backend API.
 */
public interface DataProviderUtil {

    JSONObject getResource(String urlFrag, Map<String, String> params);
    JSONArray getResourceArray(String urlFrag, Map<String, String> params);

}
