package com.mygubbi.game.dashboard.data;

import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;

import java.util.HashMap;

/**
 * Created by nitinpuri on 11-04-2016.
 */
public class UserDataProvider {

    private DataProviderUtil dataProviderUtil;

    public UserDataProvider(DataProviderUtil dataProviderUtil) {
        this.dataProviderUtil = dataProviderUtil;
    }

    public JSONObject authUser(String username, String password) {

        try {
            JSONObject result = dataProviderUtil.postResource("user.auth", new HashMap<String, String>() {
                {
                    put("username", username);
                    put("password", password);
                }
            });
            boolean isValid = Boolean.valueOf(result.get("valid").toString());

            if (isValid) {
                return result.getJSONObject("user");
            } else {
                throw new RuntimeException("Invalid username/password");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("error while user auth", e);
        }
    }

    public boolean changePassword (String username, String oldPassword, String newPassword) {
        try {
            JSONObject result = dataProviderUtil.postResource("user.change.pwd", new HashMap<String, String>() {
                {
                    put("username", username);
                    put("old_password", oldPassword);
                    put("new_password", newPassword);
                }
            });
            return Boolean.valueOf(result.get("success").toString());

        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("error while user auth", e);
        }

    }
}