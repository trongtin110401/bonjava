package com.vinplay.marketing.entity;

import java.time.LocalDateTime;

public class MarketingUser {
    private long id;
    private String name;
    private String email;
    private int utmId; // Mới thêm
    private LocalDateTime createdAt;

    public int getUtmId() {
        return utmId;
    }

    public void setUtmId(int utmId) {
        this.utmId = utmId;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

