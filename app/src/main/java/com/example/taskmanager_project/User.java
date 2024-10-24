package com.example.taskmanager_project;

public class User {
    private String userId;
    private String email;
    private String userName;
    private String role;
    private String photoUrl;

    public User() {

    }

    public User(String userId, String email, String userName, String role, String photoUrl) {
        this.userId = userId;
        this.email = email;
        this.userName = userName;
        this.role = role;
        this.photoUrl = photoUrl;
    }

    public User(String updatedName, String updatedRole) {
        this.userName = updatedName;
        this.role = updatedRole;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @Override
    public String toString(){
        return userId + ": " + userName + " - " + email + ".";
    }
}
