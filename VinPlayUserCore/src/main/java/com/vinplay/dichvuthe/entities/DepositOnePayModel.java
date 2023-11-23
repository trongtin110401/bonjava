package com.vinplay.dichvuthe.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.python.parser.ast.Str;

public class DepositOnePayModel {
    public String Id;
    public String Nickname;
    public String CreatedAt;
    public String UpdatedAt;
    public long Amount;
    public int Status;
    public String BankBrandName;
    public String BankAccountPassword;
    public String BankAccountName;
    public String OtpNumber;
    public String Description;
    public String UserApprove;
    public int sendingStatus;

    public int getSendingStatus() {
        return sendingStatus;
    }

    public void setSendingStatus(int sendingStatus) {
        this.sendingStatus = sendingStatus;
    }

    public DepositOnePayModel(String nickname, String createdAt, String updatedAt, long amount, int status, String bankBrandName, String bankAccountPassword, String bankAccountName, String otpNumber, String description, String userApprove) {
        Nickname = nickname;
        CreatedAt = createdAt;
        UpdatedAt = updatedAt;
        Amount = amount;
        Status = status;
        BankBrandName = bankBrandName;
        BankAccountPassword = bankAccountPassword;
        BankAccountName = bankAccountName;
        OtpNumber = otpNumber;
        Description = description;
        UserApprove = userApprove;
    }

    public DepositOnePayModel(String nickname, long amount, String bankBrandName, String bankAccountPassword, String bankAccountName) {
        Nickname = nickname;
        Amount = amount;
        BankBrandName = bankBrandName;
        BankAccountPassword = bankAccountPassword;
        BankAccountName = bankAccountName;
    }

    public DepositOnePayModel(String id, String nickname, String createdAt, String updatedAt, long amount, int status, String bankBrandName, String bankAccountPassword, String bankAccountName, String otpNumber, String description, String userApprove) {
        Id = id;
        Nickname = nickname;
        CreatedAt = createdAt;
        UpdatedAt = updatedAt;
        Amount = amount;
        Status = status;
        BankBrandName = bankBrandName;
        BankAccountPassword = bankAccountPassword;
        BankAccountName = bankAccountName;
        OtpNumber = otpNumber;
        Description = description;
        UserApprove = userApprove;
    }

    public DepositOnePayModel(String transId, String nickname, String status, String bank) {
        this.Id = transId;
        this.Nickname = nickname;
        if(!status.isEmpty()){
            this.Status = Integer.parseInt(status);
        }
        this.BankBrandName = bank;
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

    public String getBankBrandName() {
        return BankBrandName;
    }

    public void setBankBrandName(String bankBrandName) {
        BankBrandName = bankBrandName;
    }

    public String getBankAccountPassword() {
        return BankAccountPassword;
    }

    public void setBankAccountPassword(String bankAccountPassword) {
        BankAccountPassword = bankAccountPassword;
    }

    public String getBankAccountName() {
        return BankAccountName;
    }

    public void setBankAccountName(String bankAccountName) {
        BankAccountName = bankAccountName;
    }

    public String getOtpNumber() {
        return OtpNumber;
    }

    public void setOtpNumber(String otpNumber) {
        OtpNumber = otpNumber;
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
