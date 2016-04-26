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
            JSONObject result = dataProviderUtil.postResource("user.auth", JsonUtil.getJson(new HashMap<String, Object>() {
                {
                    put("email", username);
                    put("password", password);
                }
            }));
            boolean isValid = result.getString("status").equals("success");

            if (isValid) {
                return new JSONObject(result.getString("user_data"));
            } else {
                throw new RuntimeException("Invalid username/password");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("error while user auth", e);
        }
    }

    public boolean changePassword(String username, String oldPassword, String newPassword) {
        try {
            JSONObject result = dataProviderUtil.postResource("user.change_pwd", JsonUtil.getJson(new HashMap<String, Object>() {
                {
                    put("email", username);
                    put("old_password", oldPassword);
                    put("new_password", newPassword);
                }
            }));
            return result.getString("status").equals("success");

        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid user/password", e);
        }

    }
}