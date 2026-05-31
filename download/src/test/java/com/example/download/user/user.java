package com.example.download.user;

public class user {
    private String userName;
    private String password;
    private String email;

    private user() {}
    public user(String userName, String password, String email) {
        this.userName = userName;
    }
    public user(String userName, String password) {}

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
