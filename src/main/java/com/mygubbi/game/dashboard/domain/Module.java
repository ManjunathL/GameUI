package com.mygubbi.game.dashboard.domain;

/**
 * Created by nitinpuri on 01-05-2016.
 */
public class Module {

    public enum ImportStatus {MATCHED, DEFAULT_MATCHED, NO_MATCH};

    private int seqNo;
    private String importedModuleCode;
    private String mgModuleCode;
    private String description;
    private double w;
    private double d;
    private double h;
    private String imagePath;
    private String carcassMaterial;
    private int carcassMaterialId;
    private String shutterMaterial;
    private int shutterMaterialId;
    private String finish;
    private int finishId;
    private String make;
    private int makeId;
    private int qty;
    private double amount;
    private ImportStatus importStatus;

    public int getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(int seqNo) {
        this.seqNo = seqNo;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getW() {
        return w;
    }

    public void setW(double w) {
        this.w = w;
    }

    public double getD() {
        return d;
    }

    public void setD(double d) {
        this.d = d;
    }

    public double getH() {
        return h;
    }

    public void setH(double h) {
        this.h = h;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getCarcassMaterial() {
        return carcassMaterial;
    }

    public void setCarcassMaterial(String carcassMaterial) {
        this.carcassMaterial = carcassMaterial;
    }

    public int getCarcassMaterialId() {
        return carcassMaterialId;
    }

    public void setCarcassMaterialId(int carcassMaterialId) {
        this.carcassMaterialId = carcassMaterialId;
    }

    public String getShutterMaterial() {
        return shutterMaterial;
    }

    public void setShutterMaterial(String shutterMaterial) {
        this.shutterMaterial = shutterMaterial;
    }

    public int getShutterMaterialId() {
        return shutterMaterialId;
    }

    public void setShutterMaterialId(int shutterMaterialId) {
        this.shutterMaterialId = shutterMaterialId;
    }

    public String getFinish() {
        return finish;
    }

    public void setFinish(String finish) {
        this.finish = finish;
    }

    public int getFinishId() {
        return finishId;
    }

    public void setFinishId(int finishId) {
        this.finishId = finishId;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public int getMakeId() {
        return makeId;
    }

    public void setMakeId(int makeId) {
        this.makeId = makeId;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public ImportStatus getImportStatus() {
        return importStatus;
    }

    public void setImportStatus(ImportStatus importStatus) {
        this.importStatus = importStatus;
    }
}
