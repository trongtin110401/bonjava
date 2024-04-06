package com.vinplay.api.backend.models;

public class CallBackModel {

    private String chargeId;
    private String chargeType;
    private String chargeCode;
    private String regAmount;
    private String status;

    public CallBackModel(String chargeId, String chargeType, String chargeCode, String regAmount, String status) {
        this.chargeId = chargeId;
        this.chargeType = chargeType;
        this.chargeCode = chargeCode;
        this.regAmount = regAmount;
        this.status = status;
    }

    public CallBackModel() {
    }

    public String getChargeId() {
        return chargeId;
    }

    public void setChargeId(String chargeId) {
        this.chargeId = chargeId;
    }

    public String getChargeType() {
        return chargeType;
    }

    public void setChargeType(String chargeType) {
        this.chargeType = chargeType;
    }

    public String getChargeCode() {
        return chargeCode;
    }

    public void setChargeCode(String chargeCode) {
        this.chargeCode = chargeCode;
    }

    public String getRegAmount() {
        return regAmount;
    }

    public void setRegAmount(String regAmount) {
        this.regAmount = regAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
