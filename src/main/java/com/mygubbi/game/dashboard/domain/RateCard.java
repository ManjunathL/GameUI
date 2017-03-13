package com.mygubbi.game.dashboard.domain;

/**
 * Created by user on 09-Mar-17.
 */
public class RateCard
{
    public static final String RATECARD_ID = "rateCardId";
    public static final String TYPE = "type";
    public static final String CODE = "code";
    public static final String THICKNESS = "thickness";

    private String rateCardId;
    private String type;
    private String code;
    private String thickness;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRateCardId() {
        return rateCardId;
    }

    public void setRateCardId(String rateCardId) {
        this.rateCardId = rateCardId;
    }

    public String getThickness() {
        return thickness;
    }

    public void setThickness(String thickness) {
        this.thickness = thickness;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "RateCard{" +
                "code='" + code + '\'' +
                ", rateCardId='" + rateCardId + '\'' +
                ", type='" + type + '\'' +
                ", thickness='" + thickness + '\'' +
                '}';
    }
}
