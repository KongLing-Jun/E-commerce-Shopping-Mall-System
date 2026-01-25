package com.thinking.backendmall.model;

public class User {
    private Long id;
    private String username;
    private String passwordHash;
    private String phone;
    private boolean active;

    public User() {
    }

    public User(Long id, String username, String passwordHash, String phone, boolean active) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.phone = phone;
        this.active = active;
    }

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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
