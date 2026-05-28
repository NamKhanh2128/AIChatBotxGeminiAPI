package com.training.studyfx.model;

public class User {
    private int id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String status;
    private String profileImagePath;
    public String bietdanh;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.fullName = username;
        this.status = "Available";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String v) {
        this.username = v;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String v) {
        this.password = v;
    }

    public String getFullName() {
        return fullName != null ? fullName : username;
    }

    public void setFullName(String v) {
        this.fullName = v;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String v) {
        this.email = v;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String v) {
        this.status = v;
    }

    public String getProfileImagePath() {
        return profileImagePath;
    }

    public void setProfileImagePath(String v) {
        this.profileImagePath = v;
    }
}