package com.mygubbi.game.dashboard.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by nitinpuri on 01-05-2016.
 */
public class Module {

    public enum ImportStatus {success, default_matched, error};

    private int seq;
    private String importedModuleCode;
    private String importedModuleDefaultCode;
    private String mgModuleCode;
    private String description;
    private double w;
    private double d;
    private double h;
    private String imagePath;
	private String makeType;
	private String makeTypeCode;
	private String carcassMaterial;
	private String carcassMaterialCode;
	private String finishType;
	private String finishTypeCode;
	private String shutterFinish;
	private String shutterFinishCode;
    private String color;
    private double amount;
    private String importStatus;
	private List<ModuleAccessory> moduleAccessories = new ArrayList<>();
    private Map<String, String> mgModuleImageMap;

	public List<ModuleAccessory> getModuleAccessories() {
		return moduleAccessories;
	}

	public void setModuleAccessories(List<ModuleAccessory> moduleAccessories) {
		this.moduleAccessories = moduleAccessories;
	}

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
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
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
}
