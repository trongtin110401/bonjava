/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.vbee.common.dto;

public class UserBankInfoDto {
    private String id;
    private String nickName;
    private String bankAccount;

    private String bankName;

    private String accountName;

    private String createdDate;

    public UserBankInfoDto(String nickName, String bankAccount, String bankName, String accountName, String createdDate) {
        this.nickName = nickName;
        this.bankAccount = bankAccount;
        this.bankName = bankName;
        this.accountName = accountName;
        this.createdDate=createdDate;
    }

    public UserBankInfoDto() {
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

