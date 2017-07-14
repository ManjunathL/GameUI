package com.mygubbi.game.dashboard.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 26-Apr-17.
 */
public class UserProfile
{
    public static final String ID="id";
    public static final String FIREBASE_ID="fbid";
    public static final String ACTIVE="active";
    public static final String EMAIL="email";
    public static final String PROFILE="profile";
    public static final String CRM_ID="crmId";

    private String id;
    private String fbid;
    private String active;
    private String email;
    private String crmId;
    private List<Profile> profile = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getCrmId() {
        return crmId;
    }

    public void setCrmId(String crmId) {
        this.crmId = crmId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFbid() {
        return fbid;
    }

    public void setFbid(String fbid) {
        this.fbid = fbid;
    }

    public List<Profile> getProfile() {
        return profile;
    }

    public void setProfile(List<Profile> profile) {
        this.profile = profile;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "active='" + active + '\'' +
                ", id='" + id + '\'' +
                ", fbid='" + fbid + '\'' +
                ", email='" + email + '\'' +
                ", crmId='" + crmId + '\'' +
                ", profile=" + profile +
                '}';
    }
}
