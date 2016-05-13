package com.mygubbi.game.dashboard.config;

/**
 * Created by nitinpuri on 13-05-2016.
 */

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mygubbi.game.dashboard.util.FileUtil;
import com.mygubbi.game.dashboard.util.JsonMerger;
import com.mygubbi.game.dashboard.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class ConfigHolder {

    private static final Logger LOG = LogManager.getLogger(ConfigHolder.class);
    private static ConfigHolder INSTANCE = new ConfigHolder();
    private JsonObject serverConfig = new JsonObject();

    private ConfigHolder() {

        String configFiles = System.getProperty("configFiles", "config/dev/conf.json");
        List<String> configFileList = StringUtils.fastSplit(configFiles, ',');
        loadConfig(configFileList);
    }

    public static ConfigHolder getInstance() {
        return INSTANCE;
    }

    private void loadConfig(List<String> configFiles) {

        LOG.info("Loading config files - " + configFiles.toString());

        JsonParser parser = new JsonParser();
        JsonMerger merger = new JsonMerger();

        for (String configFile : configFiles) {
            String fileContent = new FileUtil().readFile(configFile);
            JsonObject configObject = parser.parse(fileContent).getAsJsonObject();
            merger.merge(configObject, this.serverConfig);
        }

        LOG.info("Final merged config : " + this.serverConfig.toString());
    }

    public String getStringValue(String key, String defaultValue) {
        if (!this.serverConfig.has(key)) return defaultValue;
        return this.serverConfig.getAsJsonPrimitive(key).getAsString();
    }

    public int getInteger(String key, int defaultValue) {
        if (!this.serverConfig.has(key)) return defaultValue;
        return this.serverConfig.getAsJsonPrimitive(key).getAsInt();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        if (!this.serverConfig.has(key)) return defaultValue;
        return this.serverConfig.getAsJsonPrimitive(key).getAsBoolean();
    }


}
