package com.mygubbi.game.dashboard.domain;

public final class User {
    private String name;
    private String role;
    private String email;
    private String phone;

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
}
