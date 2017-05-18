package com.mygubbi.game.dashboard.domain;

/**
 * Created by shruthi on 26-Apr-17.
 */
public class Profile implements Cloneable
{

    public static final String CRM_ID="crmId";
    public static final String CUSTOMER_PHONE="mobile";
    public static final String DISPLAY_NAME="displayName";
    public static final String PROFILE_IMAGE="profileImage";
    public static final String CUSTOMER_EMAIL="email";
    public static final String OPPORTUNITY_ID="opportunityId";
    public static final String USER_ID="userId";
    public static final String FIRST_NAME="first_name";
    public static final String PROJECT_CITY="city";
    public static final String LAST_NAME="last_name";
    public static final String DESIGNER_EMAIL="designerUserId";
    public static final String DESIGNER_NAME="designerName";
    public static final String DESIGNER_MOBILE="designerMobile";
    public static final String SALES_EMAIL="salesExecUserId";
    public static final String SALES_NAME="salesExecName";
    public static final String SALES_MOBILE="salesExecMobile";

    private String crmId;
    private String mobile;
    private String displayName;
    private String profileImage;
    private String opportunityId;
    private String email;
    private String first_name;
    private String city;
    private String last_name;
    private String designerUserId;
    private String designerName;
    private String designerMobile;
    private String salesExecUserId;
    private String salesExecName;
    private String salesExecMobile;



    public String getCrmId() {
        return crmId;
    }

    public void setCrmId(String crmId) {
        this.crmId = crmId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDesignerMobile() {
        return designerMobile;
    }

    public void setDesignerMobile(String designerMobile) {
        this.designerMobile = designerMobile;
    }

    public String getDesignerName() {
        return designerName;
    }

    public void setDesignerName(String designerName) {
        this.designerName = designerName;
    }

    public String getDesignerUserId() {
        return designerUserId;
    }

    public void setDesignerUserId(String designerUserId) {
        this.designerUserId = designerUserId;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getSalesExecMobile() {
        return salesExecMobile;
    }

    public void setSalesExecMobile(String salesExecMobile) {
        this.salesExecMobile = salesExecMobile;
    }

    public String getSalesExecName() {
        return salesExecName;
    }

    public void setSalesExecName(String salesExecName) {
        this.salesExecName = salesExecName;
    }

    public String getSalesExecUserId() {
        return salesExecUserId;
    }

    public void setSalesExecUserId(String salesExecUserId) {
        this.salesExecUserId = salesExecUserId;
    }

    public String getOpportunityId() {
        return opportunityId;
    }

    public void setOpportunityId(String opportunityId) {
        this.opportunityId = opportunityId;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "city='" + city + '\'' +
                ", crmId='" + crmId + '\'' +
                ", mobile='" + mobile + '\'' +
                ", displayName='" + displayName + '\'' +
                ", profileImage='" + profileImage + '\'' +
                ", opportunityId='" + opportunityId + '\'' +
                ", email='" + email + '\'' +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", designerUserId='" + designerUserId + '\'' +
                ", designerName='" + designerName + '\'' +
                ", designerMobile='" + designerMobile + '\'' +
                ", salesExecUserId='" + salesExecUserId + '\'' +
                ", salesExecName='" + salesExecName + '\'' +
                ", salesExecMobile='" + salesExecMobile + '\'' +
                '}';
    }

    @Override
    public Object clone()  {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
            //Will never happen since we are implementing Cloneable
        }
    }
}
