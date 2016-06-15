package com.mygubbi.game.dashboard.domain;

/**
 * Created by nitinpuri on 08-06-2016.
 */
public class CatalogueMaterialFinish {

    private double basePrice;
    private String material;
    private String finish;

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getFinish() {
        return finish;
    }

    public void setFinish(String finish) {
        this.finish = finish;
    }
}
