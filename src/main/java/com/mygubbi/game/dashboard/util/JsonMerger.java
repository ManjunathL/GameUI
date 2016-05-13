package com.mygubbi.game.dashboard.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

/**
 * Created by nitinpuri on 13-05-2016.
 */
public class JsonMerger {

    public void merge(JsonObject from, JsonObject into) {

        for (Map.Entry<String, JsonElement> entry : from.entrySet()) {
            into.add(entry.getKey(), entry.getValue());
        }
    }
}
