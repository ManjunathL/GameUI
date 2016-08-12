package com.mygubbi.game.dashboard.data.dummy;

import com.mygubbi.game.dashboard.data.DataProviderMode;
import com.mygubbi.game.dashboard.util.FileUtil;
import us.monoid.json.JSONArray;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;

import java.util.Map;

/**
 * Created by nitinpuri on 05-04-2016.
 */
public class FileDataProviderMode implements DataProviderMode {

    @Override
    public JSONObject getResource(String urlFrag, Map<String, String> params) {
        try {
            return new JSONObject(new FileUtil().readFile("jsons/" + formPath(urlFrag, params) + ".json"));
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Error parsing file as json: " + formPath(urlFrag, params), e);
        }
    }

    @Override
    public JSONArray getResourceArray(String urlFrag, Map<String, String> params) {
        try {
            return new JSONArray(new FileUtil().readFile("jsons/" + formPath(urlFrag, params) + ".json"));
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Error parsing file as json: " + formPath(urlFrag, params), e);
        }

    }

    @Override
    public JSONObject postResource(String urlFrag, String json) {
        return getResource(formPath(urlFrag, null), null);
    }

    @Override
    public JSONObject postResourceWithUrl(String urlFrag, String json) {
        return getResource(formPath(urlFrag, null), null);
    }

    @Override
    public JSONArray postResourceGetMultiple(String urlFrag, String jsonParams) {
        return getResourceArray(formPath(urlFrag, null), null);
    }

    private String formPath(String urlFrag, Map<String, String> params) {
        if (urlFrag.equals("codelookup")) {
            urlFrag = params.get("lookupType");
        }
        return urlFrag.replaceAll("/", "_");
    }

}
