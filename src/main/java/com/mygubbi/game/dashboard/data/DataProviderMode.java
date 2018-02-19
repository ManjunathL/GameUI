package com.mygubbi.game.dashboard.data;

import us.monoid.json.JSONArray;
import us.monoid.json.JSONObject;
import us.monoid.web.JSONResource;

import java.util.Map;

/**
 * Dashboard backend API.
 */
public interface DataProviderMode {

    JSONObject getResource(String urlFrag, Map<String, String> params);

    JSONArray getResourceArray(String urlFrag, Map<String, String> params);

    JSONObject postResource(String urlFrag, String json);

    JSONObject postResourceWithUrl(String urlFrag, String json);

    JSONResource postResourceWithUrlForCrm(String url, String opportunity_name, String final_proposal_amount_c, String estimated_project_cost_c, String quotation_number_c,String booking_order_value_cost);

    JSONResource postResourceWithUrlForCrmOnPublish(String url, String opportunity_name, String estimated_project_cost_c, String quotation_number_c);

    JSONResource postResourceWithFormData(String url, Map<String, String> keyValuePairs);

    JSONArray postResourceGetMultiple(String urlFrag, String jsonParams);

    JSONObject postResourceWithUrlForLdSuare(JSONObject json);


}
