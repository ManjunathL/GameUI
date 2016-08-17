package com.mygubbi.game.dashboard.config;

/**
 * Created by nitinpuri on 13-05-2016.
 */

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mygubbi.game.dashboard.ServerManager;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.data.dummy.FileDataProviderMode;
import com.mygubbi.game.dashboard.domain.Color;
import com.mygubbi.game.dashboard.domain.FinishTypeColor;
import com.mygubbi.game.dashboard.util.FileUtil;
import com.mygubbi.game.dashboard.util.JsonMerger;
import com.mygubbi.game.dashboard.util.StringUtils;
import com.vaadin.server.FileResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ConfigHolder {

    private static final Logger LOG = LogManager.getLogger(ConfigHolder.class);
    private static ConfigHolder INSTANCE = new ConfigHolder();
    private JsonObject serverConfig = new JsonObject();
    private String imageBasePath;
    private String catalogueImageBasePath;

    private ConfigHolder() {

        String configFiles = System.getProperty("configFiles", "config/conf.dev.json");
        List<String> configFileList = StringUtils.fastSplit(configFiles, ',');
        loadConfig(configFileList);
        initImageBasePath();
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

    private void initImageBasePath() {
        imageBasePath = getStringValue("imageBasePath", "");
        System.out.println("imageBasePath:" + imageBasePath);
        if (!imageBasePath.endsWith("/")) {
            imageBasePath = imageBasePath + "/";
            System.out.println("imageBasePath after adding /:" + imageBasePath);
        }

        catalogueImageBasePath = getStringValue("catalogueImageBasePath", "");
        if (!catalogueImageBasePath.endsWith("/")) {
            catalogueImageBasePath = catalogueImageBasePath + "/";
        }
    }

    public String getCRMUrl() {
        return getStringValue("crmUrl", "");
    }

    public boolean useCRMAuth() {
        return getBoolean("useCrmAuth", false);
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

    public String getImageBasePath() {
        return imageBasePath;
    }

    public String getCatalogueImageBasePath() {
        return catalogueImageBasePath;
    }
}
