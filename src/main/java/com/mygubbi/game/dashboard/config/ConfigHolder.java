package com.mygubbi.game.dashboard.config;

/**
 * Created by nitinpuri on 13-05-2016.
 */

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.data.dummy.FileDataProviderUtil;
import com.mygubbi.game.dashboard.domain.Color;
import com.mygubbi.game.dashboard.domain.FinishTypeColor;
import com.mygubbi.game.dashboard.util.FileUtil;
import com.mygubbi.game.dashboard.util.JsonMerger;
import com.mygubbi.game.dashboard.util.StringUtils;
import com.vaadin.server.FileResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Image;
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
    private Map<String, FinishTypeColor> finishTypeColors;
    private Map<String, Color> colors;
    private final String imageBasePath;
    private final String dataProviderUtilName;
    private final ProposalDataProvider proposalDataProvider;

    private ConfigHolder() {

        String configFiles = System.getProperty("configFiles", "config/dev/conf.json");
        List<String> configFileList = StringUtils.fastSplit(configFiles, ',');
        loadConfig(configFileList);

        imageBasePath = getStringValue("imageBasePath", "");
        dataProviderUtilName = getStringValue("dataProviderUtil", "");

        proposalDataProvider = new ProposalDataProvider(dataProviderUtilName);

        finishTypeColors = proposalDataProvider.getFinishTypeColors().stream().collect(
                Collectors.toMap(FinishTypeColor::getFinishTypeCode, Function.identity()));

        colors = new HashMap<>();

        for (Map.Entry<String, FinishTypeColor> entry : finishTypeColors.entrySet()) {
            entry.getValue().getColors().stream().forEach(
                    color -> {
                        color.setColorImageResource(new FileResource(new File(imageBasePath + color.getImagePath())));
                        colors.put(color.getCode(), color);
                    }
            );
        }
    }

    public ProposalDataProvider getProposalDataProvider() {
        return proposalDataProvider;
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

    public Map<String, FinishTypeColor> getFinishTypeColors() {
        return finishTypeColors;
    }

    public Map<String, Color> getColors() {
        return colors;
    }

    public String getImageBasePath() {
        return imageBasePath;
    }
}
