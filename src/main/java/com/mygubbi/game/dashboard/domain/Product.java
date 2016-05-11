package com.mygubbi.game.dashboard.domain;

import java.util.List;

/**
 * Created by nitinpuri on 01-05-2016.
 */
public class Product {
    private int lineItemId;
    private int seqNo;
    private String description;
    private String room;
    private String category;
    private String carcassMaterial;
    private int carcassMaterialId;
    private String shutterMaterial;
    private int shutterMaterialId;
    private String finish;
    private int finishId;
    private String materialFinish;
    private String color;
    private int qty;
    private double amount;
    private String type;

    private String uploadPath;
    private String uploadFileName;

    private List<Module> modules;
    private List<Addon> addons;
    private List<FileAttachment> fileAttachmentList;

    public int getLineItemId() {
        return lineItemId;
    }

    public void setLineItemId(int lineItemId) {
        this.lineItemId = lineItemId;
    }

    public int getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(int seqNo) {
        this.seqNo = seqNo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public String getMaterialFinish() {
        return materialFinish;
    }

    public void setMaterialFinish(String materialFinish) {
        this.materialFinish = materialFinish;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    public String getUploadFileName() {
        return uploadFileName;
    }

    public void setUploadFileName(String uploadFileName) {
        this.uploadFileName = uploadFileName;
    }

    public List<Module> getModules() {
        return modules;
    }

    public void setModules(List<Module> modules) {
        this.modules = modules;
    }

    public List<Addon> getAddons() {
        return addons;
    }

    public void setAddons(List<Addon> addons) {
        this.addons = addons;
    }

    public List<FileAttachment> getFileAttachmentList() {
        return fileAttachmentList;
    }

    public void setFileAttachmentList(List<FileAttachment> fileAttachmentList) {
        this.fileAttachmentList = fileAttachmentList;
    }
}
