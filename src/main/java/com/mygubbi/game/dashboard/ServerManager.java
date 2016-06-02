package com.mygubbi.game.dashboard;

import com.mygubbi.game.dashboard.config.ConfigHolder;
import com.mygubbi.game.dashboard.data.DataProviderMode;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.data.RestDataProviderMode;
import com.mygubbi.game.dashboard.data.UserDataProvider;
import com.mygubbi.game.dashboard.domain.JsonPojo.LookupItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nitinpuri on 26-05-2016.
 */
public class ServerManager {

    private static final Logger LOG = LogManager.getLogger(ServerManager.class);

    private static final ServerManager INSTANCE = new ServerManager();
    private ProposalDataProvider proposalDataProvider;
    private UserDataProvider userDataProvider;

    private Map<String, List<LookupItem>> lookupItemsByType = new HashMap<>();

    private ServerManager(){
        init();
    }

    private void init() {
        ConfigHolder ch = ConfigHolder.getInstance();
        String dataProviderModeClass = ch.getStringValue("dataProviderMode", "");
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

    public static ServerManager getInstance() {
        return INSTANCE;
    }

    public ProposalDataProvider getProposalDataProvider() {
        return proposalDataProvider;
    }

    public UserDataProvider getUserDataProvider() {
        return userDataProvider;
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
