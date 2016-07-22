package com.mygubbi.game.dashboard.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by test on 15-07-2016.
 */
public class AccessoryPack {

    public static final String ACCESSORY_PACK_CODE = "code";
    public static final String ACCESSORY_PACK_TITLE  = "title";

    private String code;
    private String title;
    private List<Accessory> accessories=new ArrayList<>();

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Accessory> getAccessories() {
        return accessories;
    }

    public void setAccessories(List<Accessory> accessories) {
        this.accessories = accessories;
    }
}
