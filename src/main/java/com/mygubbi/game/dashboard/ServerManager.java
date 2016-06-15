package com.mygubbi.game.dashboard;

import com.mygubbi.game.dashboard.config.ConfigHolder;
import com.mygubbi.game.dashboard.data.DataProviderMode;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.data.RestDataProviderMode;
import com.mygubbi.game.dashboard.data.UserDataProvider;
import com.mygubbi.game.dashboard.domain.Color;
import com.mygubbi.game.dashboard.domain.FinishTypeColor;
import com.mygubbi.game.dashboard.domain.JsonPojo.LookupItem;
import com.vaadin.server.FileResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by nitinpuri on 26-05-2016.
 */
public class ServerManager {

    private static final Logger LOG = LogManager.getLogger(ServerManager.class);

    private static final ServerManager INSTANCE = new ServerManager();
    private ProposalDataProvider proposalDataProvider;
    private UserDataProvider userDataProvider;
    private Map<String, FinishTypeColor> finishTypeColors;
    private Map<String, Color> colors;

    private Map<String, List<LookupItem>> lookupItemsByType = new HashMap<>();

    private ServerManager(){
        init();
    }

    private void init() {
        initConfigHolder();
        initDataProviders();
        cacheColorMap();
    }

    private void cacheColorMap() {
        finishTypeColors = proposalDataProvider.getFinishTypeColors().stream().collect(
                Collectors.toMap(FinishTypeColor::getFinishTypeCode, Function.identity()));

        colors = new HashMap<>();

        for (Map.Entry<String, FinishTypeColor> entry : finishTypeColors.entrySet()) {
            entry.getValue().getColors().stream().forEach(
                    color -> {
                        color.setColorImageResource(new FileResource(new File(ConfigHolder.getInstance().getImageBasePath() + color.getImagePath())));
                        colors.put(color.getCode(), color);
                    }
            );
        }
    }

    private void initDataProviders() {
        String dataProviderModeClass = ConfigHolder.getInstance().getStringValue("dataProviderMode", "");
        LOG.info("Using data provider mode - " + dataProviderModeClass);

        try {
            Class<?> clazz = Class.forName(dataProviderModeClass);
            DataProviderMode dataProviderModeFromConfig = (DataProviderMode) clazz.newInstance();

            proposalDataProvider = new ProposalDataProvider(dataProviderModeFromConfig);
            userDataProvider = new UserDataProvider(new RestDataProviderMode()); //todo: change this

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void initConfigHolder() {
        ConfigHolder.getInstance();
    }

    public static ServerManager getInstance() {
        return INSTANCE;
    }

    public ProposalDataProvider getProposalDataProvider() {
        return proposalDataProvider;
    }

    public UserDataProvider getUserDataProvider() {
        return userDataProvider;
    }

    public Map<String, FinishTypeColor> getFinishTypeColors() {
        return finishTypeColors;
    }

    public Map<String, Color> getColors() {
        return colors;
    }

    synchronized public void addLookupItems(String type, List<LookupItem> lookupItems) {
        lookupItemsByType.put(type, lookupItems);
    }

    synchronized public List<LookupItem> getLookupItems(String type) {
        return lookupItemsByType.get(type);
    }

    synchronized public void clearLookupItems() {
        lookupItemsByType.clear();
    }
}
