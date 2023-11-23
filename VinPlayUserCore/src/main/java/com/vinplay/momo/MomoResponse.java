package com.vinplay.momo;

public class MomoResponse {
    int errorCode;
    String errorDescription;
    String infomationAccount;
    int comment;
    String qrcode;
    int amount;
    String type;

    public MomoResponse(int errorCode, String errorDescription, String infomationAccount, int comment, String qrcode, int amount, String type) {
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
        this.infomationAccount = infomationAccount;
        this.comment = comment;
        this.qrcode = qrcode;
        this.amount = amount;
        this.type = type;
    }

    public MomoResponse() {
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

    public int getComment() {
        return comment;
    }

    public void setComment(int comment) {
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
}
