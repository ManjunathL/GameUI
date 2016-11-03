package com.mygubbi.game.dashboard.data;

import com.mygubbi.game.dashboard.config.ConfigHolder;
import com.mygubbi.game.dashboard.domain.JsonPojo.Role;
import com.mygubbi.game.dashboard.domain.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import us.monoid.json.JSONArray;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by nitinpuri on 11-04-2016.
 */
public class UserDataProvider {

    private static final Logger LOG = LogManager.getLogger(UserDataProvider.class);

    private DataProviderMode dataProviderMode;
    private static final String CRM_METHOD_STUB = "__METHOD__";
    private static final List<Role> ROLES = new ArrayList<>();

    private enum CRM_METHOD {login, get_entry_list, get_entries, get_relationships}

    public UserDataProvider(DataProviderMode dataProviderMode) {
        this.dataProviderMode = dataProviderMode;
    }

    public User authUser(String username, String password) {

        boolean useCRMAuth = ConfigHolder.getInstance().useCRMAuth();

        if (useCRMAuth) {
            User user = new User();
            authUserFromCRM(username, getMD5Hash(password), user);
            setUserDetailsFromCRM(user);
            getAllRolesFromCRM(user);
            setUserRoleFromCRM(user);
            return user;
        } else {
            return authUserNative(username, password);
        }
    }

    private User authUserNative(String username, String password) {

        try {
            JSONObject result = dataProviderMode.postResource("user.auth", JsonUtil.getJson(new HashMap<String, Object>() {
                {
                    put("email", username);
                    put("password", password);
                }
            }));
            boolean isValid = result.getString("status").equals("success");

            if (isValid) {
                JSONObject userObject = new JSONObject(result.getString("user_data"));
                return new User(userObject.getString("email"), userObject.getString("role"), userObject.optString("phone", ""), userObject.getString("name"));
            } else {
                throw new RuntimeException("Invalid username/password");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("error while user auth", e);
        }
    }

    private String getMD5Hash(String password) {
        try {
            byte[] md5hash = null;
            synchronized (MessageDigest.class) {
                MessageDigest md5 = MessageDigest.getInstance("MD5");
                md5.reset();
                md5hash = md5.digest(password.getBytes("UTF-8"));
            }
            StringBuilder passSb = new StringBuilder();
            for (byte aMd5hash : md5hash) {
                passSb.append(Integer.toString((aMd5hash & 0xff) + 0x100, 16).substring(1));
            }
            return passSb.toString();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new RuntimeException("Exception while calculating md5 checksum for password.");
        }
    }

    //check if user is present in a particular role
    //http://52.66.107.178/mygubbi_crm/service/v4_1/rest.php?method=get_relationships&input_type=json&response_type=json&rest_data={"session":"0k9va38uh2gb5c8m43lstjipn7","module_name":"ACLRoles", "module_id":"855d73fd-c1bf-fb69-491e-574429895be3","link_field_name":"users","related_module_query": " user_id='5ae2b6ca-ef41-1f3f-7c5c-57ac621813a1' "}
    private void setUserRoleFromCRM(User user) {

        String baseCRMUrl = ConfigHolder.getInstance().getCRMUrl().replaceAll(CRM_METHOD_STUB, CRM_METHOD.get_relationships.name());

        for (Role role : ROLES) {
            String roleId = role.getId();
            String urlJsonParam = "{\"session\":\"" + user.getSessionId() + "\",\"module_name\":\"ACLRoles\",\"module_id\":\"" + roleId + "\",\"link_field_name\":\"users\",\"related_module_query\": \" user_id='" + user.getUserId() + "' \"}";
            try {
                JSONObject result = dataProviderMode.postResourceWithUrl(baseCRMUrl + URLEncoder.encode(urlJsonParam), JsonUtil.getJson(new HashMap<>()));
                JSONArray entryList = result.getJSONArray("entry_list");
                int len = entryList.length();

                if (len > 0) {
                    user.setRole(role.getName());
                    break;
                }

            } catch (JSONException e) {
                e.printStackTrace();
                throw new RuntimeException("error while fetching role details", e);
            }
        }
    }

    //get all roles
    //http://52.66.107.178/mygubbi_crm/service/v4_1/rest.php?method=get_entry_list&input_type=json&response_type=json&rest_data={"session":"0k9va38uh2gb5c8m43lstjipn7","module_name":"ACLRoles"}
    private void getAllRolesFromCRM(User user) {

        if (!ROLES.isEmpty()) {
            return;
        }

        synchronized (UserDataProvider.class) {
            if (ROLES.isEmpty()) {

                String baseCRMUrl = ConfigHolder.getInstance().getCRMUrl().replaceAll(CRM_METHOD_STUB, CRM_METHOD.get_entry_list.name());
                String urlJsonParam = "{\"session\": \"" + user.getSessionId() + "\", \"module_name\": \"ACLRoles\"}";

                try {
                    JSONObject result = dataProviderMode.postResourceWithUrl(baseCRMUrl + URLEncoder.encode(urlJsonParam), JsonUtil.getJson(new HashMap<>()));
                    JSONArray entryList = result.getJSONArray("entry_list");

                    int len = entryList.length();

                    for (int i = 0; i < len; i++) {
                        Role role = new Role();
                        JSONObject jsonObject = entryList.getJSONObject(i);
                        String roleId = jsonObject.getString("id");
                        String roleName = jsonObject.getJSONObject("name_value_list").getJSONObject("name").getString("value");
                        role.setId(roleId);
                        role.setName(roleName);
                        ROLES.add(role);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException("error while fetching role details", e);
                }
            }
        }
    }

    //get user details
    //http://52.66.107.178/mygubbi_crm/service/v4_1/rest.php?method=get_entries&input_type=json&response_type=json&rest_data={"session":"0k9va38uh2gb5c8m43lstjipn7","module_name":"Users","ids":["5ae2b6ca-ef41-1f3f-7c5c-57ac621813a1"]}
    private void setUserDetailsFromCRM(User user) {
        String baseCRMUrl = ConfigHolder.getInstance().getCRMUrl().replaceAll(CRM_METHOD_STUB, CRM_METHOD.get_entries.name());
        String urlJsonParam = "{\"session\": \"" + user.getSessionId() + "\", \"module_name\": \"Users\",\"ids\":[\"" + user.getUserId() + "\"]}";

        try {
            JSONObject result = dataProviderMode.postResourceWithUrl(baseCRMUrl + URLEncoder.encode(urlJsonParam), JsonUtil.getJson(new HashMap<>()));
            result.getJSONArray("entry_list").getJSONObject(0);
            JSONObject name_value_list = result.getJSONArray("entry_list").getJSONObject(0).getJSONObject("name_value_list");
            String fullName = name_value_list.getJSONObject("full_name").getString("value");
            String phone = name_value_list.getJSONObject("phone_mobile").getString("value");
            String email = name_value_list.getJSONObject("email1").getString("value");
            user.setName(fullName);
            user.setPhone(phone);
            user.setEmail(email);

        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("error while fetching user details", e);
        }

    }

    //login
    //http://52.66.107.178/mygubbi_crm/service/v4_1/rest.php?method=login&input_type=json&response_type=json&rest_data={"user_auth":{"user_name":"9999","version":".01","password":"098f6bcd4621d373cade4e832627b4f6"}, "application_name":"Custom API"}
    private void authUserFromCRM(String username, String password, User user) {

        String baseCRMUrl = ConfigHolder.getInstance().getCRMUrl().replaceAll(CRM_METHOD_STUB, CRM_METHOD.login.name());
        String urlJsonParam = "{\"user_auth\":{\"user_name\":\"" + username + "\",\"version\":\".01\",\"password\":\"" + password + "\"}, \"application_name\":\"GAME\"}";

        try {
            JSONObject result = dataProviderMode.postResourceWithUrl(baseCRMUrl + URLEncoder.encode(urlJsonParam), JsonUtil.getJson(new HashMap<>()));
            LOG.debug("Result :" + result.toString());
            boolean isValid = result.has("id");

            if (isValid) {
                user.setSessionId(result.getString("id"));
                JSONObject name_value_list = result.getJSONObject("name_value_list");
                String userId = name_value_list.getJSONObject("user_id").getString("value");
                String userName = name_value_list.getJSONObject("user_name").getString("value");
                user.setUserId(userId);
                user.setUserName(userName);
            } else {
                throw new RuntimeException(result.toString());
            }

        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("error while user auth", e);
        }
    }

    public boolean changePassword(String username, String oldPassword, String newPassword) {
        try {
            JSONObject result = dataProviderMode.postResource("user.change_pwd", JsonUtil.getJson(new HashMap<String, Object>() {
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