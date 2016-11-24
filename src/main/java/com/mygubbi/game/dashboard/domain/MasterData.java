package com.mygubbi.game.dashboard.domain;

/**
 * Created by test on 30-06-2016.
 */
public class MasterData {

    public static final String TABLE = "table";
    public static final String DESCRIPTION = "description";

    private String table;
    private String description;

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
