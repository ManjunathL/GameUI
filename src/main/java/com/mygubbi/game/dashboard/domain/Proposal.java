package com.mygubbi.game.dashboard.domain;

import java.util.Date;
import java.util.List;

/**
 * Created by test on 30-03-2016.
 */
public final class Proposal {

    private int proposalId;
    private ProposalHeader proposalHeader;
    private List<Product> products;
    private double amount;
    private String uploadFolderPath;
    private List<FileAttachment> fileAttachments;
    private Date createDate;
    private String createdBy;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getProposalId() {
        return proposalId;
    }

    public void setProposalId(int proposalId) {
        this.proposalId = proposalId;
    }

    public ProposalHeader getProposalHeader() {
        return proposalHeader;
    }

    public void setProposalHeader(ProposalHeader proposalHeader) {
        this.proposalHeader = proposalHeader;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<FileAttachment> getFileAttachments() {
        return fileAttachments;
    }

    public void setFileAttachments(List<FileAttachment> fileAttachments) {
        this.fileAttachments = fileAttachments;
    }

    public String getUploadFolderPath() {
        return uploadFolderPath;
    }

    public void setUploadFolderPath(String uploadFolderPath) {
        this.uploadFolderPath = uploadFolderPath;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
