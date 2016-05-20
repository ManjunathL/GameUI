package com.mygubbi.game.dashboard.domain;

/**
 * Created by nitinpuri on 20-05-2016.
 */
public class ModulePrice {

    private double carcassCost;
    private double shutterCost;
    private double accessoryCost;
    private double hardwareCost;
    private double labourCost;
    private double totalCost;

    public double getCarcassCost() {
        return carcassCost;
    }

    public void setCarcassCost(double carcassCost) {
        this.carcassCost = carcassCost;
    }

    public double getShutterCost() {
        return shutterCost;
    }

    public void setShutterCost(double shutterCost) {
        this.shutterCost = shutterCost;
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
}
