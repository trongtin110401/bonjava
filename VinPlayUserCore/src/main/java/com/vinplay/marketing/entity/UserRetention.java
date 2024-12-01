package com.vinplay.marketing.entity;

import java.time.LocalDateTime;

public class UserRetention {
    private int id;
    private int userId;
    private LocalDateTime firstAccess;
    private boolean d1Returned;
    private boolean d7Returned;
    private boolean d14Returned;
    private boolean d30Returned;
    private int visitCount;
    private LocalDateTime lastAccess;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDateTime getFirstAccess() {
        return firstAccess;
    }

    public void setFirstAccess(LocalDateTime firstAccess) {
        this.firstAccess = firstAccess;
    }

    public boolean isD1Returned() {
        return d1Returned;
    }

    public void setD1Returned(boolean d1Returned) {
        this.d1Returned = d1Returned;
    }

    public boolean isD7Returned() {
        return d7Returned;
    }

    public void setD7Returned(boolean d7Returned) {
        this.d7Returned = d7Returned;
    }

    public boolean isD14Returned() {
        return d14Returned;
    }

    public void setD14Returned(boolean d14Returned) {
        this.d14Returned = d14Returned;
    }

    public boolean isD30Returned() {
        return d30Returned;
    }

    public void setD30Returned(boolean d30Returned) {
        this.d30Returned = d30Returned;
    }

    public int getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(int visitCount) {
        this.visitCount = visitCount;
    }

    public LocalDateTime getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(LocalDateTime lastAccess) {
        this.lastAccess = lastAccess;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
