package com.mygubbi.game.dashboard.domain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;

/**
 * Created by nitinpuri on 01-05-2016.
 */
public class ProposalHeader {

    public enum ProposalState {Draft, Published, Confirmed, DSO, PSO, Deleted}
    public enum EDIT {W, R}

    public static final String ID = "id";
    public static final String QUOTE_NO_NEW = "quoteNoNew";
    public static final String STATUS = "status";
    public static final String TITLE = "title";
    public static final String VERSION = "version";
    public static final String CRM_ID = "crmId";
    public static final String QUOTE_NO =  "quoteNo";
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
    public static final String DESIGN_PARTNER_NAME = "designPartnerName";
    public static final String DESIGN_PARTNER_EMAIL = "designPartnerEmail";
    public static final String DESIGN_PARTNER_PHONE = "designPartnerPhone";
    public static final String AMOUNT = "amount";
    public static final String FOLDER_PATH = "folderPath";
    public static final String CREATED_ON = "createdOn";
    public static final String CREATED_BY = "createdBy";
    public static final String UPDATED_ON = "updatedOn";
    public static final String UPDATED_BY = "updatedBy";
    public static final String PRICE_DATE = "priceDate";
    public static final String MAX_DISCOUNTPERCENTAGE="maxDiscountPercentage";
    public static final String NO_OF_DAYS_FOR_WORK_COMPLETION = "noOfDaysForWorkCompletion";
    public static final String BEFORE_PRODUCTION_SPECIFICATION="beforeProductionSpecification";
    public static final String PACKAGE_FLAG="packageFlag";
    public static final String ADMIN_PACKAGEFLAG="adminPackageFlag";
    public static final String OFFER_TYPE="offerType";
    public static final String FROM_PROPOSAL="fromProposal";
    public static final String FROM_VERSION="fromVersion";
    public static final String BOQ_STATUS = "boqStatus";
    public static final String BOQ_DRIVE_LINK = "boqDriveLink";
    public static final String PROJ_HANDLING_CHRAGES_APPLIED = "projectHandlingChargesApplied";
    public static final String DEEP_CLEANING_CHRAGES_APPLIED = "deepClearingChargesApplied";
    public static final String FLOOR_PROTECTION_CHRAGES_APPLIED = "floorProtectionChargesApplied";
    public static final String  CUSTOM_ADDON_CHECK= "customAddonCheck";
    public static final String  EXPECTED_DELIVERY_DATE= "expectedDeliveryDate";


    private int id;
    private String quoteNoNew;
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
    private String designPartnerName;
    private String designPartnerEmail;
    private String designPartnerPhone;
    private double amount;
    private String folderPath;
    private java.sql.Date createdOn;
    private String createdBy;
    private java.sql.Date updatedOn;
    private String updatedBy;
    private String editFlag = EDIT.R.name();
    private java.sql.Date priceDate;
    private double maxDiscountPercentage;
    private double noOfDaysForWorkCompletion;
    private String beforeProductionSpecification;
    private String packageFlag;
    private String adminPackageFlag;
    private String offerType;
    private String boqStatus;
    private String boqDriveLink;
    private int fromProposal;
    private String fromVersion;
    private String projectHandlingChargesApplied;
    private String deepClearingChargesApplied;
    private String floorProtectionChargesApplied;
    private String customAddonCheck;
    private String expectedDeliveryDate;

    private static final Logger LOG = LogManager.getLogger(ProposalHeader.class);


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

    public String getQuoteNoNew() {
        return quoteNoNew;
    }

    public void setQuoteNoNew(String quoteNoNew) {
        this.quoteNoNew = quoteNoNew;
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

    public java.sql.Date getCreatedOn() {

        return createdOn;
    }

    public void setCreatedOn(java.sql.Date createdOn) {
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

    public String getDesignPartnerName() {
        return designPartnerName;
    }

    public void setDesignPartnerName(String designPartnerName) {
        this.designPartnerName = designPartnerName;
    }

    public String getDesignPartnerEmail() {
        return designPartnerEmail;
    }

    public void setDesignPartnerEmail(String designPartnerEmail) {
        this.designPartnerEmail = designPartnerEmail;
    }

    public String getDesignPartnerPhone() {
        return designPartnerPhone;
    }

    public void setDesignPartnerPhone(String designPartnerPhone) {
        this.designPartnerPhone = designPartnerPhone;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public java.sql.Date getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(java.sql.Date updatedOn) {
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

    public java.sql.Date getPriceDate() {
        return priceDate;
    }

    public void setPriceDate(java.sql.Date priceDate) {
        this.priceDate = priceDate;
    }

    public double getMaxDiscountPercentage() {
        return maxDiscountPercentage;
    }

    public void setMaxDiscountPercentage(double maxDiscountPercentage) {
        this.maxDiscountPercentage = maxDiscountPercentage;
    }

    public double getNoOfDaysForWorkCompletion() {
        return noOfDaysForWorkCompletion;
    }

    public void setNoOfDaysForWorkCompletion(double noOfDaysForWorkCompletion) {
        this.noOfDaysForWorkCompletion = noOfDaysForWorkCompletion;
    }

    public String getBeforeProductionSpecification() {
        return beforeProductionSpecification;
    }

    public void setBeforeProductionSpecification(String beforeProductionSpecification) {
        this.beforeProductionSpecification = beforeProductionSpecification;
    }

    public String getPackageFlag() {
        return packageFlag;
    }

    public void setPackageFlag(String packageFlag) {
        this.packageFlag = packageFlag;
    }

    public String getAdminPackageFlag() {
        return adminPackageFlag;
    }

    public void setAdminPackageFlag(String adminPackageFlag) {
        this.adminPackageFlag = adminPackageFlag;
    }

    public String getOfferType() {
        return offerType;
    }

    public void setOfferType(String offerType) {
        this.offerType = offerType;
    }

    public String getBoqStatus() {
        return boqStatus;
    }

    public void setBoqStatus(String boqStatus) {
        this.boqStatus = boqStatus;
    }

    public String getBoqDriveLink() {
        return boqDriveLink;
    }

    public void setBoqDriveLink(String boqDriveLink) {
        this.boqDriveLink = boqDriveLink;
    }

    public int getFromProposal() {
        return fromProposal;
    }

    public void setFromProposal(int fromProposal) {
        this.fromProposal = fromProposal;
    }

    public String getFromVersion() {
        return fromVersion;
    }

    public void setFromVersion(String fromVersion) {
        this.fromVersion = fromVersion;
    }

    public String getProjectHandlingChargesApplied() {
        return projectHandlingChargesApplied;
    }

    public void setProjectHandlingChargesApplied(String projectHandlingChargesApplied) {
        this.projectHandlingChargesApplied = projectHandlingChargesApplied;
    }

    public String getDeepClearingChargesApplied() {
        return deepClearingChargesApplied;
    }

    public void setDeepClearingChargesApplied(String deepClearingChargesApplied) {
        this.deepClearingChargesApplied = deepClearingChargesApplied;
    }

    public String getFloorProtectionChargesApplied() {
        return floorProtectionChargesApplied;
    }

    public void setFloorProtectionChargesApplied(String floorProtectionChargesApplied) {
        this.floorProtectionChargesApplied = floorProtectionChargesApplied;
    }

    public String getCustomAddonCheck() {
        return customAddonCheck;
    }

    public void setCustomAddonCheck(String customAddonCheck) {
        this.customAddonCheck = customAddonCheck;
    }

    public String getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }

    public void setExpectedDeliveryDate(String expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    @Override
    public String toString() {
        return "ProposalHeader{" +
                "id=" + id +
                ", quoteNoNew='" + quoteNoNew + '\'' +
                ", status='" + status + '\'' +
                ", title='" + title + '\'' +
                ", version='" + version + '\'' +
                ", crmId='" + crmId + '\'' +
                ", quoteNo='" + quoteNo + '\'' +
                ", customerId='" + customerId + '\'' +
                ", cname='" + cname + '\'' +
                ", caddress1='" + caddress1 + '\'' +
                ", caddress2='" + caddress2 + '\'' +
                ", caddress3='" + caddress3 + '\'' +
                ", ccity='" + ccity + '\'' +
                ", cemail='" + cemail + '\'' +
                ", cphone1='" + cphone1 + '\'' +
                ", cphone2='" + cphone2 + '\'' +
                ", projectName='" + projectName + '\'' +
                ", paddress1='" + paddress1 + '\'' +
                ", paddress2='" + paddress2 + '\'' +
                ", pcity='" + pcity + '\'' +
                ", salesName='" + salesName + '\'' +
                ", salesEmail='" + salesEmail + '\'' +
                ", salesPhone='" + salesPhone + '\'' +
                ", designerName='" + designerName + '\'' +
                ", designerEmail='" + designerEmail + '\'' +
                ", designerPhone='" + designerPhone + '\'' +
                ", designPartnerName='" + designPartnerName + '\'' +
                ", designPartnerEmail='" + designPartnerEmail + '\'' +
                ", designPartnerPhone='" + designPartnerPhone + '\'' +
                ", amount=" + amount +
                ", folderPath='" + folderPath + '\'' +
                ", createdOn=" + createdOn +
                ", createdBy='" + createdBy + '\'' +
                ", updatedOn=" + updatedOn +
                ", updatedBy='" + updatedBy + '\'' +
                ", editFlag='" + editFlag + '\'' +
                ", priceDate=" + priceDate +
                ", maxDiscountPercentage=" + maxDiscountPercentage +
                ", beforeProductionSpecification='" + beforeProductionSpecification + '\'' +
                ", packageFlag='" + packageFlag + '\'' +
                ", adminPackageFlag='" + adminPackageFlag + '\'' +
                ", offerType='" + offerType + '\'' +
                ", fromProposal=" + fromProposal +
                ", fromVersion='" + fromVersion + '\'' +
                '}';
    }
}
