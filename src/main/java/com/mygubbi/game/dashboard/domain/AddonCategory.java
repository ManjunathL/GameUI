package com.mygubbi.game.dashboard.domain;

/**
 * Created by nitinpuri on 02-06-2016.
 */
public class AddonCategory {

    public static final String CATGEORY_CODE = "categoryCode";
    public static final String RATE_READ_ONLY = "rateReadonly";
    private String categoryCode;
    private boolean rateReadonly;

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public boolean isRateReadonly() {
        return rateReadonly;
    }

    public void setRateReadonly(boolean rateReadonly) {
        this.rateReadonly = rateReadonly;
    }
}
