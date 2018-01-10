package com.mygubbi.game.dashboard.domain;

public final class User {

    public static final String NAME = "name";
    public static final String ROLE = "role";
    public static final String EMAIL = "email";
    public static final String PHONE = "phone";
    public static final String ADMIN_ROLE = "admin";

    private String name;
    private String role;
    private String email;
    private String phone;
    private String sessionId;
    private String userId;
    private String userName;
    private String isViewOnly;

    public User()
    {

    }

    public User(String email, String role, String phone, String name) {
        this.email = email;
        this.role = role;
        this.phone = phone;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(final String role) {
        this.role = role;
        setIsViewOnly();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getIsViewOnly() {
        return isViewOnly;
    }

    public void setIsViewOnly() {
        String yes = "Yes";
        String no = "No";
        if(this.getRole().equalsIgnoreCase("purchasemanager") || this.getRole().equalsIgnoreCase("operations")
                || this.getRole().equalsIgnoreCase("crm") || this.getRole().equalsIgnoreCase("operations"))
            this.isViewOnly = yes;
        else
            this.isViewOnly = no;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", role='" + role + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", isViewOnly='" + isViewOnly + '\'' +
                '}';
    }
}
