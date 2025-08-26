package com.jiade.massageshopmanagement.model;

public class AdminAccount {
    private Long id;
    private String username;
    private String password; // 数据库存储加密后的密码

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}