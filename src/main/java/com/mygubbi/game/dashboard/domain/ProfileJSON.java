package com.mygubbi.game.dashboard.domain;

/**
 * Created by user on 23-May-17.
 */
public class ProfileJSON
{
    public static final String ADDRESS="address";
    public static final String PROJECT_NAME="projectName";
    public static final String PROPERTY_ADDRESS_CITY="propertyAddressCity";

    public String address;
    public String projectName;
    public String propertyAddressCity;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getPropertyAddressCity() {
        return propertyAddressCity;
    }

    public void setPropertyAddressCity(String propertyAddressCity) {
        this.propertyAddressCity = propertyAddressCity;
    }

    @Override
    public String toString() {
        return "ProfileJSON{" +
                "address='" + address + '\'' +
                ", projectName='" + projectName + '\'' +
                ", propertyAddressCity='" + propertyAddressCity + '\'' +
                '}';
    }
}
