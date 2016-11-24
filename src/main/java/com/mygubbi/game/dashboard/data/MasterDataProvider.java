package com.mygubbi.game.dashboard.data;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mygubbi.game.dashboard.data.dummy.FileDataProviderMode;
import com.mygubbi.game.dashboard.domain.FinishTypeColor;
import com.mygubbi.game.dashboard.domain.MasterData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import us.monoid.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by test on 30-06-2016.
 */
public class MasterDataProvider {

    private static final Logger LOG = LogManager.getLogger(MasterDataProvider.class);
    private final FileDataProviderMode dataProviderMode;
    private final ObjectMapper mapper;


    public static final String TABLE = "table";
    public static final String DESCRIPTION = "description";

    public MasterDataProvider(FileDataProviderMode dataProviderMode) {
        this.dataProviderMode = dataProviderMode;
        this.mapper = new ObjectMapper();
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public List<MasterData> getMasterData() {
        JSONArray array = dataProviderMode.getResourceArray("master/data", new HashMap<>());
        LOG.debug("master data - \n" + array.toString());
        try {
            MasterData[] items = this.mapper.readValue(array.toString(), MasterData[].class);
            return new ArrayList<>(Arrays.asList(items));
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

}
