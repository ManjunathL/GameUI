package com.mygubbi.game.dashboard.domain;

import com.mygubbi.game.dashboard.view.FileAttachmentsHolder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by test on 30-03-2016.
 */
public final class Proposal implements FileAttachmentsHolder{

    private ProposalHeader proposalHeader = new ProposalHeader();
    private List<Product> products = new ArrayList<>();
    private List<FileAttachment> fileAttachments = new ArrayList<>();
    private List<AddonProduct> addons = new ArrayList<>();

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

    @Override
    public List<FileAttachment> getFileAttachmentList() {
        return fileAttachments;
    }

    public void setFileAttachments(List<FileAttachment> fileAttachments) {
        this.fileAttachments = fileAttachments;
    }

    public List<AddonProduct> getAddons() {
        return addons;
    }

    public void setAddons(List<AddonProduct> addons) {
        this.addons = addons;
    }
}
