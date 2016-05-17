package com.mygubbi.game.dashboard.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nitinpuri on 01-05-2016.
 */
public class Product {

    public enum TYPE {CUSTOM, CATALOGUE};

    private int productId;
    private int seq;
    private String title;
    private String productCategory;
    private String productCategoryCode;
    private String room;
    private String roomCode;
    private String makeType;
    private String makeTypeCode;
    private String carcassMaterial;
    private String carcassMaterialCode;
    private String finishType;
    private String finishTypeCode;
    private String shutterFinish;
    private String shutterFinishCode;
    private double amount;
    private String type;

    private String quoteFilePath;

    private List<Module> modules = new ArrayList<>();
    private List<Addon> addons = new ArrayList<>();
    private List<FileAttachment> fileAttachmentList = new ArrayList<>();

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getCarcassMaterial() {
        return carcassMaterial;
    }

    public void setCarcassMaterial(String carcassMaterial) {
        this.carcassMaterial = carcassMaterial;
    }

    public String getShutterFinish() {
        return shutterFinish;
    }

    public void setShutterFinish(String shutterFinish) {
        this.shutterFinish = shutterFinish;
    }

    public String getFinishType() {
        return finishType;
    }

    public void setFinishType(String finishType) {
        this.finishType = finishType;
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

    public String getQuoteFilePath() {
        return quoteFilePath;
    }

    public void setQuoteFilePath(String uploadFileName) {
        this.quoteFilePath = uploadFileName;
    }

    public String getProductCategoryCode() {
        return productCategoryCode;
    }

    public void setProductCategoryCode(String productCategoryCode) {
        this.productCategoryCode = productCategoryCode;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
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

    public String getFinishTypeCode() {
        return finishTypeCode;
    }

    public void setFinishTypeCode(String finishTypeCode) {
        this.finishTypeCode = finishTypeCode;
    }

    public String getShutterFinishCode() {
        return shutterFinishCode;
    }

    public void setShutterFinishCode(String shutterFinishCode) {
        this.shutterFinishCode = shutterFinishCode;
    }

    public void setType(String type) {
        this.type = type;
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
