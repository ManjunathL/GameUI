package com.mygubbi.game.dashboard.data.dummy;

import com.mygubbi.game.dashboard.data.DataProviderUtil;
import com.mygubbi.game.dashboard.util.FileUtil;
import us.monoid.json.JSONArray;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * Created by nitinpuri on 05-04-2016.
 */
public class FileDataProviderUtil implements DataProviderUtil {

    @Override
    public JSONObject getResource(String urlFrag, Map<String, String> params) {
        try {
            return new JSONObject(new FileUtil().readFile("jsons/" + urlFrag + ".json"));
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Error parsing file as json: " + urlFrag, e);
        }
    }

    @Override
    public JSONArray getResourceArray(String urlFrag, Map<String, String> params) {
        try {
            return new JSONArray(new FileUtil().readFile("jsons/" + urlFrag + ".json"));
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Error parsing file as json: " + urlFrag, e);
        }

    }

    @Override
    public JSONObject postResource(String urlFrag, String jsonParams) {
        return getResource(urlFrag, null);
    }

    @Override
    public JSONArray postResourceGetMultiple(String urlFrag, String jsonParams) {
        return getResourceArray(urlFrag, null);
    }

}
