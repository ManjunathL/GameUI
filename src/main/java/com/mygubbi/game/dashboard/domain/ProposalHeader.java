package com.mygubbi.game.dashboard.domain;

import java.util.Date;

/**
 * Created by nitinpuri on 01-05-2016.
 */
public class ProposalHeader {

    public enum ProposalState {draft, active, published, cancelled}
    public enum EDIT {W, R}

    public static final String ID = "id";
    public static final String STATUS = "status";
    public static final String TITLE = "title";
    public static final String VERSION = "version";
    public static final String CRM_ID = "crmId";
    public static final String QUOTE_NO = "quoteNo";
    public static final String CUSTOMER_ID = "customerId";
    public static final String C_NAME = "cname";
    public static final String C_ADDRESS1 = "caddress1";
    public static final String C_ADDRESS2 = "caddress2";
    public static final String C_ADDRESS3 = "caddress3";
    public static final String C_CITY = "ccity";
    public static final String C_EMAIL = "cemail";
    public static final String C_PHONE1 = "cphone1";
    public static final String C_PHONE2 = "cphone2";
    public static final String PROJECT_NAME = "projectName";
    public static final String P_ADDRESS1 = "paddress1";
    public static final String P_ADDRESS2 = "paddress2";
    public static final String P_CITY = "pcity";
    public static final String SALES_NAME = "salesName";
    public static final String SALES_EMAIL = "salesEmail";
    public static final String SALES_PHONE = "salesPhone";
    public static final String DESIGNER_NAME = "designerName";
    public static final String DESIGNER_EMAIL = "designerEmail";
    public static final String DESIGNER_PHONE = "designerPhone";
    public static final String AMOUNT = "amount";
    public static final String FOLDER_PATH = "folderPath";
    public static final String CREATED_ON = "createdOn";
    public static final String CREATED_BY = "createdBy";
    public static final String UPDATED_ON = "updatedOn";
    public static final String UPDATED_BY = "updatedBy";

    private int id;
    private String status;
    private String title;
    private String version;
    private String crmId;
    private String quoteNo;
    private String customerId;
    private String cname;
    private String caddress1;
    private String caddress2;
    private String caddress3;
    private String ccity;
    private String cemail;
    private String cphone1;
    private String cphone2;
    private String projectName;
    private String paddress1;
    private String paddress2;
    private String pcity;
    private String salesName;
    private String salesEmail;
    private String salesPhone;
    private String designerName;
    private String designerEmail;
    private String designerPhone;
    private double amount;
    private String folderPath;
    private Date createdOn;
    private String createdBy;
    private Date updatedOn;
    private String updatedBy;
    private String editFlag;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCrmId() {
        return crmId;
    }

    public void setCrmId(String crmId) {
        this.crmId = crmId;
    }

    public String getQuoteNo() {
        return quoteNo;
    }

    public void setQuoteNo(String quoteNo) {
        this.quoteNo = quoteNo;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getCaddress1() {
        return caddress1;
    }

    public void setCaddress1(String caddress1) {
        this.caddress1 = caddress1;
    }

    public String getCaddress2() {
        return caddress2;
    }

    public void setCaddress2(String caddress2) {
        this.caddress2 = caddress2;
    }

    public String getCaddress3() {
        return caddress3;
    }

    public void setCaddress3(String caddress3) {
        this.caddress3 = caddress3;
    }

    public String getCcity() {
        return ccity;
    }

    public void setCcity(String ccity) {
        this.ccity = ccity;
    }

    public String getCemail() {
        return cemail;
    }

    public void setCemail(String cemail) {
        this.cemail = cemail;
    }

    public String getCphone1() {
        return cphone1;
    }

    public void setCphone1(String cphone1) {
        this.cphone1 = cphone1;
    }

    public String getCphone2() {
        return cphone2;
    }

    public void setCphone2(String cphone2) {
        this.cphone2 = cphone2;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getPaddress1() {
        return paddress1;
    }

    public void setPaddress1(String paddress1) {
        this.paddress1 = paddress1;
    }

    public String getPaddress2() {
        return paddress2;
    }

    public void setPaddress2(String paddress2) {
        this.paddress2 = paddress2;
    }

    public String getPcity() {
        return pcity;
    }

    public void setPcity(String pcity) {
        this.pcity = pcity;
    }

    public String getSalesName() {
        return salesName;
    }

    public void setSalesName(String salesName) {
        this.salesName = salesName;
    }

    public String getSalesEmail() {
        return salesEmail;
    }

    public void setSalesEmail(String salesEmail) {
        this.salesEmail = salesEmail;
    }

    public String getSalesPhone() {
        return salesPhone;
    }

    public void setSalesPhone(String salesPhone) {
        this.salesPhone = salesPhone;
    }

    public String getDesignerName() {
        return designerName;
    }

    public void setDesignerName(String designerName) {
        this.designerName = designerName;
    }

    public String getDesignerEmail() {
        return designerEmail;
    }

    public void setDesignerEmail(String designerEmail) {
        this.designerEmail = designerEmail;
    }

    public String getDesignerPhone() {
        return designerPhone;
    }

    public void setDesignerPhone(String designerPhone) {
        this.designerPhone = designerPhone;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getEditFlag() {
        return editFlag;
    }

    public void setEditFlag(String editFlag) {
        this.editFlag = editFlag;
    }

    public boolean isReadonly() {
        return getEditFlag().equals(EDIT.R.name());
    }

}
