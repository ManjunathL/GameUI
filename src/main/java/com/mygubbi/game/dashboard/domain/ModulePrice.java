package com.mygubbi.game.dashboard.domain;

/**
 * Created by nitinpuri on 20-05-2016.
 */
public class ModulePrice {

    private double totalCost;
    private double woodworkCost;
    private double moduleArea;

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public double getModuleArea() {
        return moduleArea;
    }

    public void setModuleArea(double moduleArea) {
        this.moduleArea = moduleArea;
    }

    public double getWoodworkCost() {
        return woodworkCost;
    }

    public void setWoodworkCost(double woodworkCost) {
        this.woodworkCost = woodworkCost;
    }
}
