package com.vinplay.payment.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinplay.usercore.utils.GameCommon;

public class UserWithDrawCard {
    public String Id;
    public String Username;
    public int Amount;
    public int AmountReal;
    public String telcoId;
    public String CreatedAt;
    public String UpdatedAt;
    public String Status;
    public String Pin;
    public String Seri;
    public int Version;
    public String UserProve;
    public int quantity;

    public UserWithDrawCard(String transid, String nickName, String seri, String pin, String telcoId, String status) {
        this.Id = transid;
        this.Username = nickName;
        this.Seri = seri;
        this.Pin = pin;
        this.Status = status;
        this.telcoId = telcoId;
    }

    public UserWithDrawCard(String transid, String nickName, String telco, String status) {
        this.Id = transid;
        this.Username = nickName;
        this.Status = status;
        this.telcoId = telco;
    }

    public String getPin() {
        return Pin;
    }

    public void setPin(String pin) {
        Pin = pin;
    }

    public String getSeri() {
        return Seri;
    }

    public void setSeri(String seri) {
        Seri = seri;
    }

    public UserWithDrawCard(String nickname, String telcoId, int amount, int quantity) {
        this.Username = nickname;
        this.telcoId = telcoId;
        this.Amount = amount;
        this.quantity = quantity;
        try{
            double feeWithdraw = GameCommon.getValueDouble("RATIO_CASHOUT_CARD");
            this.AmountReal = (int)(feeWithdraw * amount);
        }catch (Exception e){
            this.AmountReal = 0;
        }
    }

    public int getAmount() {
        return Amount;
    }

    public void setAmount(int amount) {
        Amount = amount;
    }

    public String getTelcoId() {
        return telcoId;
    }

    public void setTelcoId(String telcoId) {
        this.telcoId = telcoId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }


    public int getAmountReal() {
        return AmountReal;
    }

    public void setAmountReal(int amountReal) {
        AmountReal = amountReal;
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

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public int getVersion() {
        return Version;
    }

    public void setVersion(int version) {
        Version = version;
    }

    public String getUserProve() {
        return UserProve;
    }

    public void setUserProve(String userProve) {
        UserProve = userProve;
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
