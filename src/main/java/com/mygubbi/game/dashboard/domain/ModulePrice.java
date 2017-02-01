package com.mygubbi.game.dashboard.domain;

/**
 * Created by nitinpuri on 20-05-2016.
 */
public class ModulePrice {

    private double totalCost;
    private double woodworkCost;
    private double moduleArea;
    public double shutterCost;
    private double carcassCost;
    private double accessoryCost;
    private double hardwareCost;
    private double labourCost;

    public double getShutterCost() {
        return shutterCost;
    }

    public void setShutterCost(double shutterCost) {
        this.shutterCost = shutterCost;
    }

    public double getCarcassCost() {
        return carcassCost;
    }

    public void setCarcassCost(double carcassCost) {
        this.carcassCost = carcassCost;
    }

    public double getAccessoryCost() {
        return accessoryCost;
    }

    public void setAccessoryCost(double accessoryCost) {
        this.accessoryCost = accessoryCost;
    }

    public double getHardwareCost() {
        return hardwareCost;
    }

    public void setHardwareCost(double hardwareCost) {
        this.hardwareCost = hardwareCost;
    }

    public double getLabourCost() {
        return labourCost;
    }

    public void setLabourCost(double labourCost) {
        this.labourCost = labourCost;
    }

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

    @Override
    public String toString() {
        return "PriceModule{" +
                "totalCost='" + totalCost + '\'' +
                ", woodworkCost='" + woodworkCost + '\'' +
                ", hardwareCost='" + hardwareCost + '\'' +
                ", module area=" +moduleArea +
                ", shuttercost=" + shutterCost +
                ", carcasscost=" + carcassCost +
                ", accessory cost='" + accessoryCost + '\'' +
                ", labour cost='" + labourCost + '\'' +
                '}';
    }
}
