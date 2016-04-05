package com.mygubbi.game.dashboard.data.dummy;

import com.mygubbi.game.dashboard.data.DataProviderUtil;
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
            return new JSONObject(readFile("jsons/" + urlFrag + ".json"));
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Error parsing file as json: " + urlFrag, e);
        }
    }

    @Override
    public JSONArray getResourceArray(String urlFrag, Map<String, String> params) {
        try {
            return new JSONArray(readFile("jsons/" + urlFrag + ".json"));
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Error parsing file as json: " + urlFrag, e);
        }

    }

    private String readFile(String path) {

        InputStream in = getClass().getClassLoader().getResourceAsStream(path);
        if (in != null) {
            try (BufferedReader r = new BufferedReader(new InputStreamReader(in))) {
                String l;
                String val = "";
                while ((l = r.readLine()) != null) {
                    val = val + l;
                }

                return val;

            } catch (IOException e) {
                throw new RuntimeException("Error reading file: " + path, e);
            }
        } else {
            throw new RuntimeException("Error reading file: " + path);
        }

    }
}
