package com.mygubbi.game.dashboard.domain;

/**
 * Created by nitinpuri on 02-06-2016.
 */
public class AddonCategory {

    public static final String CATGEORY_CODE = "categoryCode";
    public static final String RATE_READ_ONLY = "rateReadOnly";
    private String categoryCode;
    private boolean rateReadOnly;

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public boolean isRateReadOnly() {
        return rateReadOnly;
    }

    public void setRateReadOnly(boolean rateReadOnly) {
        this.rateReadOnly = rateReadOnly;
    }
}
