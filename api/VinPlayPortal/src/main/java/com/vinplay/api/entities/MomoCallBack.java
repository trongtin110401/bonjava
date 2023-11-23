package com.vinplay.api.entities;

public class MomoCallBack {
    String tranID;
    String keyID;
    String phoneAccount;
    String phoneCustomer;
    String nameCustomer;
    String comment;
    String amount;
    String money;
    String signature;

    public MomoCallBack(String tranID, String keyID, String phoneAccount, String phoneCustomer, String nameCustomer, String comment, String amount, String money, String signature) {
        this.tranID = tranID;
        this.keyID = keyID;
        this.phoneAccount = phoneAccount;
        this.phoneCustomer = phoneCustomer;
        this.nameCustomer = nameCustomer;
        this.comment = comment;
        this.amount = amount;
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
}
