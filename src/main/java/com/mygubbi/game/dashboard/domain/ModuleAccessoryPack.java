package com.mygubbi.game.dashboard.domain;

import java.util.List;

/**
 * Created by test on 15-07-2016.
 */
public class ModuleAccessoryPack {
    private String code;
    private List<String> accessories;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<String> getAccessories() {
        return accessories;
    }

    public void setAccessories(List<String> accessories) {
        this.accessories = accessories;
    }

    @Override
    public String toString() {
        return "ModuleAccessoryPack{" +
                "code='" + code + '\'' +
                ", accessories=" + accessories +
                '}';
    }
}
