package com.vinplay.dichvuthe.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import scala.Int;

public class DepositMobileCardModel {
    public String Id;
    public String Nickname;
    public String CreatedAt;
    public String UpdatedAt;
    public long Amount;
    public int Status;
    public String seri;
    public String pin;
    public String provider; // VT - VNP
    public String Description;
    public String UserApprove;

    public DepositMobileCardModel(String id, String nickname, String createdAt, String updatedAt, long amount, int status, String seri, String pin, String provider, String description, String userApprove) {
        Id = id;
        Nickname = nickname;
        CreatedAt = createdAt;
        UpdatedAt = updatedAt;
        Amount = amount;
        Status = status;
        this.seri = seri;
        this.pin = pin;
        this.provider = provider;
        Description = description;
        UserApprove = userApprove;
    }

    public DepositMobileCardModel(String id, String nickname, String status, String provider) {
        Id = id;
        Nickname = nickname;
        if (!status.isEmpty()) {
            Status = Integer.parseInt(status);
        }
        this.provider = provider;
    }

    public DepositMobileCardModel(String id, String nickname, long amount, String seri, String pin, String provider) {
        Id = id;
        Nickname = nickname;
        Amount = amount;
        this.seri = seri;
        this.pin = pin;
        this.provider = provider;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getNickname() {
        return Nickname;
    }

    public void setNickname(String nickname) {
        Nickname = nickname;
    }

    public String getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        CreatedAt = createdAt;
    }

    public String getUpdatedAt() {
        return UpdatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        UpdatedAt = updatedAt;
    }

    public long getAmount() {
        return Amount;
    }

    public void setAmount(long amount) {
        Amount = amount;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getSeri() {
        return seri;
    }

    public void setSeri(String seri) {
        this.seri = seri;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getUserApprove() {
        return UserApprove;
    }

    public void setUserApprove(String userApprove) {
        UserApprove = userApprove;
    }

    public String toJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString((Object) this);
        } catch (JsonProcessingException mapper) {
            return "{\"code\":500,\"message\":\"error\"}";
        }
    }
}
