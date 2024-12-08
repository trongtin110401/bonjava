package com.vinplay.marketing.entity;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class MarketingUser {
    private long id;
    private String name;
    private String email;
    private int utmId; // Mới thêm
    private Timestamp createdAt;
    private int agencyId;

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

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public int getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(int agencyId) {
        this.agencyId = agencyId;
    }
}

