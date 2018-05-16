package com.mygubbi.game.dashboard.data;

import com.mygubbi.game.dashboard.config.ConfigHolder;
import jdk.nashorn.internal.runtime.logging.Loggable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import us.monoid.json.JSONArray;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;
import us.monoid.web.FormData;
import us.monoid.web.JSONResource;
import us.monoid.web.Resty;
import us.monoid.web.mime.MultipartContent;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

import static us.monoid.web.Resty.*;

/**
 * Created by nitinpuri on 05-04-2016.
 */
public class RestDataProviderMode implements DataProviderMode {
    private static final Logger LOG = LogManager.getLogger(RestDataProviderMode.class);
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
    public JSONResource postResourceWithUrlForCrm(String url, String opportunity_name, String final_proposal_amount_c, String estimated_project_cost_c, String quotation_number_c,String booking_order_value_c,String proposal_link_c,String presales_user_email,String no_of_working_days,String dso_date) {

        try {

            return new Resty().json(url, form(data("opportunity_name", opportunity_name), data("final_proposal_amount_c", final_proposal_amount_c),data("estimated_project_cost_c", estimated_project_cost_c), data("quotation_number_c", quotation_number_c),data("booking_order_value_c",booking_order_value_c),data("proposal_link_c",proposal_link_c),data("presales_user_email",presales_user_email),data("no_of_working_days",no_of_working_days),data("dso_date",dso_date)));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error querying - " + url, e);
        }
    }

    @Override
    public JSONResource postResourceWithUrlForCrmOnPublish(String url, String opportunity_name,String estimated_project_cost_c, String quotation_number_c,String proposal_link_c,String presales_user_email) {

        try {

            return new Resty().json(url, form(data("opportunity_name", opportunity_name), data("estimated_project_cost_c", estimated_project_cost_c), data("quotation_number_c", quotation_number_c),data("proposal_link_c",proposal_link_c),data("presales_user_email",presales_user_email)));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error querying - " + url, e);
        }
    }


    @Override
    public JSONResource postResourceWithFormData(String url, Map<String, String> keyValuePairs) {

       try {
            FormData[] values = new FormData[keyValuePairs.size()];
            int index = 0;
            for (String key : keyValuePairs.keySet()) {
               values[index] = data(key, keyValuePairs.get(key));
                index++;
           }
           return new Resty().json(url, form(values));
        } catch (IOException e) {
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

    private String getBaseURLforLdSqr()
    {
        LOG.info("base url " +ConfigHolder.getInstance().getStringValue("baseLdSqrUrl", ""));
        String baseLdSqrUrl = ConfigHolder.getInstance().getStringValue("baseLdSqrUrl", "") + "?accessKey=" + ConfigHolder.getInstance().getStringValue("ldSqrAccessKey", "") + "&secretKey=" + ConfigHolder.getInstance().getStringValue("ldSqrSecretKey", "");
        return baseLdSqrUrl;
    }

    private String queryParams(Map<String, String> params) {
        return params.entrySet().stream().map(entry -> (entry.getKey() + "=" + entry.getValue())).collect(Collectors.joining("&"));
    }

    @Override
    public JSONObject postResourceWithUrlForLdSuare( JSONObject jsonParams) {
        try
        {
            LOG.info("json paramater " +jsonParams);
            //return new Resty().text(getBaseURLforLdSqr(),content(jsonParams)).toString();
//
            /*JSONObject obj = new JSONObject();
            obj.put("SenderType","UserEmailAddress");
            obj.put("Sender","admin@mygubbi.com");
            obj.put("RecipientType","LeadEmailAddress");
            obj.put("RecipientEmailFields","");
            obj.put("Recipient","shruthi.r@mygubbi.com");
            obj.put("EmailType","Html");
            obj.put("EmailLibraryName","");
            obj.put("ContentHTML","<h1>Welcome John</h1>");
            obj.put("Subject","Example Subject");
            obj.put("IncludeEmailFooter","true");
            obj.put("Schedule","");
            obj.put("EmailCategory","");
            obj.put("ContentText","Welcome JOHN");*/
           JSONResource resource =  new Resty().json(getBaseURLforLdSqr(),content(jsonParams));
           return  resource.toObject(); }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException("Error querying - " + getBaseURLforLdSqr(), e);
        }

    }
}
