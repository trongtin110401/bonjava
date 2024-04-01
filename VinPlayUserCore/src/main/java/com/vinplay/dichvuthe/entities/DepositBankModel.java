package com.vinplay.dichvuthe.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DepositBankModel {
    public String Id;
    public String Nickname;
    public String CreatedAt;
    public String UpdatedAt;
    public long Amount;
    public int Status;
    public String BankBrandName;
    public String BankAccountNumber;
    public String BankAccountName;
    public String Description;
    public String UserApprove;
    public String UserSender;
    public String QRCode;
    private String paymentURL;

    private int timeToExpired;

    public long getAmount() {
        return Amount;
    }

    public void setAmount(long amount) {
        Amount = amount;
    }

    public String getBankBrandName() {
        return BankBrandName;
    }

    public void setBankBrandName(String bankBrandName) {
        BankBrandName = bankBrandName;
    }

    public String getBankAccountNumber() {
        return BankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        BankAccountNumber = bankAccountNumber;
    }

    public String getBankAccountName() {
        return BankAccountName;
    }

    public void setBankAccountName(String bankAccountName) {
        BankAccountName = bankAccountName;
    }

    public String getUserApprove() {
        return UserApprove;
    }

    public void setUserApprove(String userApprove) {
        UserApprove = userApprove;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getUserSender() {
        return UserSender;
    }

    public void setUserSender(String userSender) {
        UserSender = userSender;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
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

    public String getNickname() {
        return Nickname;
    }

    public void setNickname(String nickname) {
        Nickname = nickname;
    }

    public DepositBankModel(String id, String nickname, String createdAt, String updatedAt, long amount, int status, String bankBrandName, String bankAccountNumber, String bankAccountName, String description) {
        Id = id;
        Nickname = nickname;
        CreatedAt = createdAt;
        UpdatedAt = updatedAt;
        Amount = amount;
        Status = status;
        BankBrandName = bankBrandName;
        BankAccountNumber = bankAccountNumber;
        BankAccountName = bankAccountName;
        Description = description;
    }


    public DepositBankModel(String id, String nickname, String createdAt, String updatedAt, long amount, int status, String bankBrandName, String bankAccountNumber, String bankAccountName, String description, String userApprove) {
        Id = id;
        Nickname = nickname;
        CreatedAt = createdAt;
        UpdatedAt = updatedAt;
        Amount = amount;
        Status = status;
        BankBrandName = bankBrandName;
        BankAccountNumber = bankAccountNumber;
        BankAccountName = bankAccountName;
        Description = description;
        UserApprove = userApprove;
    }

    public DepositBankModel(String nickname, long amount, String bankBrandName, String bankAccountNumber, String bankAccountName) {
        Nickname = nickname;
        Amount = amount;
        BankBrandName = bankBrandName;
        BankAccountNumber = bankAccountNumber;
        BankAccountName = bankAccountName;
        UserApprove = "";
    }

    public DepositBankModel(String nickname, int status, String bankBrandName) {
        Nickname = nickname;
        Status = status;
        BankBrandName = bankBrandName;
    }

    public DepositBankModel(String id, String nickname, String status, String bankBrandName) {
        Id = id;
        Nickname = nickname;
        if (!status.isEmpty()) {
            Status = Integer.parseInt(status);
        }
        BankBrandName = bankBrandName;
    }

    public String toJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString((Object) this);
        } catch (JsonProcessingException mapper) {
            return "{\"code\":500,\"message\":\"error\"}";
        }
    }

    public String getQRCode() {
        return QRCode;
    }

    public void setQRCode(String QRCode) {
        this.QRCode = QRCode;
    }

    public String getPaymentURL() {
        return paymentURL;
    }

    public void setPaymentURL(String paymentURL) {
        this.paymentURL = paymentURL;
    }

    public int getTimeToExpired() {
        return timeToExpired;
    }

    public void setTimeToExpired(int timeToExpired) {
        this.timeToExpired = timeToExpired;
    }
}
