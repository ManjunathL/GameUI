package com.mygubbi.game.dashboard.domain;

/**
 * Created by nitinpuri on 20-05-2016.
 */
public class ModulePrice {

    private double totalCost;
    private double woodworkCost;
    private double moduleArea;
    public double shutterCost;
    public double shutterSourceCost;
    private double carcassCost;
    private double carcassSourceCost;
    private double accessoryCost;
    private double accessorySourceCost;
    private double hardwareCost;
    private double hardwareSourceCost;
    private double labourCost;
    private double labourSourceCost;
    private double handleAndKnobCost;
    private double handleAndKnobSourceCost;
    private double hingeCost;
    private double hingeSourceCost;

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

    public double getHandleAndKnobCost() {
        return handleAndKnobCost;
    }

    public void setHandleAndKnobCost(double handleAndKnobCost) {
        this.handleAndKnobCost = handleAndKnobCost;
    }

    public double getHingeCost() {
        return hingeCost;
    }

    public void setHingeCost(double hingeCost) {
        this.hingeCost = hingeCost;
    }

    public double getHandleAndKnobSourceCost() {
        return handleAndKnobSourceCost;
    }

    public void setHandleAndKnobSourceCost(double handleAndKnobSourceCost) {
        this.handleAndKnobSourceCost = handleAndKnobSourceCost;
    }

    public double getHingeSourceCost() {
        return hingeSourceCost;
    }

    public void setHingeSourceCost(double hingeSourceCost) {
        this.hingeSourceCost = hingeSourceCost;
    }

    public double getShutterSourceCost() {
        return shutterSourceCost;
    }

    public void setShutterSourceCost(double shutterSourceCost) {
        this.shutterSourceCost = shutterSourceCost;
    }

    public double getCarcassSourceCost() {
        return carcassSourceCost;
    }

    public void setCarcassSourceCost(double carcassSourceCost) {
        this.carcassSourceCost = carcassSourceCost;
    }

    public double getAccessorySourceCost() {
        return accessorySourceCost;
    }

    public void setAccessorySourceCost(double accessorySourceCost) {
        this.accessorySourceCost = accessorySourceCost;
    }

    public double getHardwareSourceCost() {
        return hardwareSourceCost;
    }

    public void setHardwareSourceCost(double hardwareSourceCost) {
        this.hardwareSourceCost = hardwareSourceCost;
    }

    public double getLabourSourceCost() {
        return labourSourceCost;
    }

    public void setLabourSourceCost(double labourSourceCost) {
        this.labourSourceCost = labourSourceCost;
    }

    @Override
    public String toString() {
        return "ModulePrice{" +
                "totalCost=" + totalCost +
                ", woodworkCost=" + woodworkCost +
                ", moduleArea=" + moduleArea +
                ", shutterCost=" + shutterCost +
                ", shutterSourceCost=" + shutterSourceCost +
                ", carcassCost=" + carcassCost +
                ", carcassSourceCost=" + carcassSourceCost +
                ", accessoryCost=" + accessoryCost +
                ", accessorySourceCost=" + accessorySourceCost +
                ", hardwareCost=" + hardwareCost +
                ", hardwareSourceCost=" + hardwareSourceCost +
                ", labourCost=" + labourCost +
                ", labourSourceCost=" + labourSourceCost +
                ", handleAndKnobCost=" + handleAndKnobCost +
                ", handleAndKnobSourceCost=" + handleAndKnobSourceCost +
                ", hingeCost=" + hingeCost +
                ", hingeSourceCost=" + hingeSourceCost +
                '}';
    }
}
