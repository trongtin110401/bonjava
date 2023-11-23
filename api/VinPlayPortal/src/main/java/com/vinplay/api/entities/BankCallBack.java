package com.vinplay.api.entities;

public class BankCallBack {
    String tranID;
    String keyID;
    String phoneAccount;
    String phoneCustomer;
    String nameCustomer;
    String comment;
    String amount;
    String type;
    String money;
    String signature;

    public BankCallBack() {
    }

    public BankCallBack(String tranID, String keyID, String phoneAccount, String phoneCustomer, String nameCustomer, String comment, String amount, String type, String money, String signature) {
        this.tranID = tranID;
        this.keyID = keyID;
        this.phoneAccount = phoneAccount;
        this.phoneCustomer = phoneCustomer;
        this.nameCustomer = nameCustomer;
        this.comment = comment;
        this.amount = amount;
        this.type = type;
        this.money = money;
        this.signature = signature;
    }

    public String getTranID() {
        return tranID;
    }

    public void setTranID(String tranID) {
        this.tranID = tranID;
    }

    public String getKeyID() {
        return keyID;
    }

    public void setKeyID(String keyID) {
        this.keyID = keyID;
    }

    public String getPhoneAccount() {
        return phoneAccount;
    }

    public void setPhoneAccount(String phoneAccount) {
        this.phoneAccount = phoneAccount;
    }

    public String getPhoneCustomer() {
        return phoneCustomer;
    }

    public void setPhoneCustomer(String phoneCustomer) {
        this.phoneCustomer = phoneCustomer;
    }

    public String getNameCustomer() {
        return nameCustomer;
    }

    public void setNameCustomer(String nameCustomer) {
        this.nameCustomer = nameCustomer;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public String toString() {
        return "BankCallBack{" +
                "tranID='" + tranID + '\'' +
                ", keyID='" + keyID + '\'' +
                ", phoneAccount='" + phoneAccount + '\'' +
                ", phoneCustomer='" + phoneCustomer + '\'' +
                ", nameCustomer='" + nameCustomer + '\'' +
                ", comment='" + comment + '\'' +
                ", amount='" + amount + '\'' +
                ", type='" + type + '\'' +
                ", money='" + money + '\'' +
                ", signature='" + signature + '\'' +
                '}';
    }
}
