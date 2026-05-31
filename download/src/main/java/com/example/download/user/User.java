package com.example.download.user;

public class User {
    private Long id;          // 建议加一个自增主键
    private String userName;
    private String password;  // 实际存储加密后的密码
    private String email;
    private boolean enabled;  // 邮箱是否已验证

    // 无参构造（必须，给框架用）
    public User() {}

    // 注册时用的构造
    public User(String userName, String password, String email, Long id, boolean enabled) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.enabled = enabled;
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName){
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password){
        this.password = password;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }

    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled){
        this.enabled = enabled;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
}