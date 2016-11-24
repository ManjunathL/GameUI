package com.mygubbi.game.dashboard.data;

import com.mygubbi.game.dashboard.config.ConfigHolder;
import us.monoid.json.JSONArray;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;
import us.monoid.web.Resty;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

import static us.monoid.web.Resty.content;

/**
 * Created by nitinpuri on 05-04-2016.
 */
public class RestDataProviderMode implements DataProviderMode {

    private final Resty resty = new Resty();

    @Override
    public JSONObject getResource(String urlFrag, Map<String, String> params) {
        try {
            return resty.json(getBaseURL() + "/" + urlFrag + "?" + queryParams(params)).object();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Error querying - " + urlFrag, e);
        }
    }

    @Override
    public JSONArray getResourceArray(String urlFrag, Map<String, String> params) {
        try {
            System.out.println("----- GET ------");
            System.out.println(getBaseURL() + "/" + urlFrag + "?" + queryParams(params));
            return resty.json(getBaseURL() + "/" + urlFrag + "?" + queryParams(params)).array();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Error querying - " + urlFrag, e);
        }
    }

    @Override
    public JSONObject postResource(String urlFrag, String json) {

        try {
            return resty.json(getBaseURL() + "/" + urlFrag,
                    content(json))
                    .object();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Error querying - " + urlFrag, e);
        }
    }

    @Override
    public JSONObject postResourceWithUrl(String url, String json) {

        try {
            return resty.json(url, content(json)).object();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Error querying - " + url, e);
        }
    }

    @Override
    public JSONArray postResourceGetMultiple(String urlFrag, String jsonParams) {

        try {
            return resty.json(getBaseURL() + "/" + urlFrag,
                    content(jsonParams)).array();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Error querying - " + urlFrag, e);
        }
    }

    private String getBaseURL() {
        return ConfigHolder.getInstance().getStringValue("restUrl", "");
    }

    private String queryParams(Map<String, String> params) {
        return params.entrySet().stream().map(entry -> (entry.getKey() + "=" + entry.getValue())).collect(Collectors.joining("&"));
    }
}
