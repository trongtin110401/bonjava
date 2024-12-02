package com.vinplay.marketing.entity;

import java.time.LocalDateTime;

public class ServiceLog {
    private int id;
    private int userServiceId;
    private String action;
    private LocalDateTime actionTime;
    private String details;
    private LocalDateTime createdAt;

    private long actionValue;

    public long getActionValue() {
        return actionValue;
    }

    public void setActionValue(long actionValue) {
        this.actionValue = actionValue;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserServiceId() {
        return userServiceId;
    }

    public void setUserServiceId(int userServiceId) {
        this.userServiceId = userServiceId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public LocalDateTime getActionTime() {
        return actionTime;
    }

    public void setActionTime(LocalDateTime actionTime) {
        this.actionTime = actionTime;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

