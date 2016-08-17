package com.mygubbi.game.dashboard.data;

import us.monoid.json.JSONArray;
import us.monoid.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Dashboard backend API.
 */
public interface DataProviderMode {

    JSONObject getResource(String urlFrag, Map<String, String> params);

    JSONArray getResourceArray(String urlFrag, Map<String, String> params);

    JSONObject postResource(String urlFrag, String json);

    JSONObject postResourceWithUrl(String urlFrag, String json);

    JSONArray postResourceGetMultiple(String urlFrag, String jsonParams);
}
