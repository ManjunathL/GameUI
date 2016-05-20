package com.mygubbi.game.dashboard.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by nitinpuri on 01-05-2016.
 */
public class Module {

    public static final String DEFAULT = "default";

    public void setGenerated() {
        if (importStatus.equals(ImportStatus.default_matched.name())) {
            importedModuleText = importedModuleCode + " / " + importedModuleDefaultCode;
        } else {
            importedModuleText = importedModuleCode;
        }
    }

    public enum ImportStatus {success, default_matched, error}

    private int seq;
    private String importedModuleCode;
    private String importedModuleDefaultCode;
    private String importedModuleText;
    private String mgModuleCode;
    private String makeType;
    private String makeTypeCode;
    private String makeTypeText;
    private String carcassMaterial;
    private String carcassMaterialCode;
    private String carcassMaterialText;
    private String finishType;
    private String finishTypeCode;
    private String finishTypeText;
    private String shutterFinish;
    private String shutterFinishCode;
    private String shutterFinishText;
    private String colorCode;
    private String colorName;
    private String colorImagePath;
    private double amount;
    private String importStatus;
    private List<MGModule> mgModules = new ArrayList<>();
    private Map<String, String> mgModuleImageMap;
    private ModulePrice modulePrice;

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getImportedModuleCode() {
        return importedModuleCode;
    }

    public void setImportedModuleCode(String importedModuleCode) {
        this.importedModuleCode = importedModuleCode;
    }

    public String getMgModuleCode() {
        return mgModuleCode;
    }

    public void setMgModuleCode(String mgModuleCode) {
        this.mgModuleCode = mgModuleCode;
    }

    public String getCarcassMaterial() {
        return carcassMaterial;
    }

    public void setCarcassMaterial(String carcassMaterial) {
        this.carcassMaterial = carcassMaterial;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getImportStatus() {
        return importStatus;
    }

    public void setImportStatus(String importStatus) {
        this.importStatus = importStatus;
    }

    public Map<String, String> getMgModuleImageMap() {
        return mgModuleImageMap;
    }

    public void setMgModuleImageMap(Map<String, String> mgModuleImageMap) {
        this.mgModuleImageMap = mgModuleImageMap;
    }

    public String getImportedModuleDefaultCode() {
        return importedModuleDefaultCode;
    }

    public void setImportedModuleDefaultCode(String importedModuleDefaultCode) {
        this.importedModuleDefaultCode = importedModuleDefaultCode;
    }

    public String getMakeType() {
        return makeType;
    }

    public void setMakeType(String makeType) {
        this.makeType = makeType;
    }

    public String getMakeTypeCode() {
        return makeTypeCode;
    }

    public void setMakeTypeCode(String makeTypeCode) {
        this.makeTypeCode = makeTypeCode;
    }

    public String getCarcassMaterialCode() {
        return carcassMaterialCode;
    }

    public void setCarcassMaterialCode(String carcassMaterialCode) {
        this.carcassMaterialCode = carcassMaterialCode;
    }

    public String getFinishType() {
        return finishType;
    }

    public void setFinishType(String finishType) {
        this.finishType = finishType;
    }

    public String getFinishTypeCode() {
        return finishTypeCode;
    }

    public void setFinishTypeCode(String finishTypeCode) {
        this.finishTypeCode = finishTypeCode;
    }

    public String getShutterFinish() {
        return shutterFinish;
    }

    public void setShutterFinish(String shutterFinish) {
        this.shutterFinish = shutterFinish;
    }

    public String getShutterFinishCode() {
        return shutterFinishCode;
    }

    public void setShutterFinishCode(String shutterFinishCode) {
        this.shutterFinishCode = shutterFinishCode;
    }

    public String getImportedModuleText() {
        return importedModuleText;
    }

    public void setImportedModuleText(String importedModuleText) {
        this.importedModuleText = importedModuleText;
    }

    public String getMakeTypeText() {
        return makeTypeText;
    }

    public void setMakeTypeText(String makeTypeText) {
        this.makeTypeText = makeTypeText;
    }

    public String getCarcassMaterialText() {
        return carcassMaterialText;
    }

    public void setCarcassMaterialText(String carcassMaterialText) {
        this.carcassMaterialText = carcassMaterialText;
    }

    public String getFinishTypeText() {
        return finishTypeText;
    }

    public void setFinishTypeText(String finishTypeText) {
        this.finishTypeText = finishTypeText;
    }

    public String getShutterFinishText() {
        return shutterFinishText;
    }

    public void setShutterFinishText(String shutterFinishText) {
        this.shutterFinishText = shutterFinishText;
    }

    public List<MGModule> getMgModules() {
        return mgModules;
    }

    public void setMgModules(List<MGModule> mgModules) {
        this.mgModules = mgModules;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public String getColorImagePath() {
        return colorImagePath;
    }

    public void setColorImagePath(String colorImagePath) {
        this.colorImagePath = colorImagePath;
    }

    public ModulePrice getModulePrice() {
        return modulePrice;
    }

    public void setModulePrice(ModulePrice modulePrice) {
        this.modulePrice = modulePrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Module module = (Module) o;

        return seq == module.seq;

    }

    @Override
    public int hashCode() {
        return seq;
    }
}
