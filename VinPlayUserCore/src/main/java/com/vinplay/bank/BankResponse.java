package com.vinplay.bank;

public class BankResponse {

    int errorCode;
    String errorDescription;
    String infomationAccount;
    String comment;
    String qrcode;
    int amount;
    String type;
    String bankCode;

    public BankResponse() {
    }

    public BankResponse(int errorCode, String errorDescription, String infomationAccount, String comment, String qrcode, int amount, String type, String bankCode) {
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
        this.infomationAccount = infomationAccount;
        this.comment = comment;
        this.qrcode = qrcode;
        this.amount = amount;
        this.type = type;
        this.bankCode = bankCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public String getInfomationAccount() {
        return infomationAccount;
    }

    public void setInfomationAccount(String infomationAccount) {
        this.infomationAccount = infomationAccount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    @Override
    public String toString() {
        return "BankResponse{" +
                "errorCode=" + errorCode +
                ", errorDescription='" + errorDescription + '\'' +
                ", infomationAccount='" + infomationAccount + '\'' +
                ", comment='" + comment + '\'' +
                ", qrcode='" + qrcode + '\'' +
                ", amount=" + amount +
                ", type='" + type + '\'' +
                ", bankCode='" + bankCode + '\'' +
                '}';
    }
}
