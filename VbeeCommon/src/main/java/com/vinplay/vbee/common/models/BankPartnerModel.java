package com.vinplay.vbee.common.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BankPartnerModel {
    public String id;
    public String qr_url;
    public String payment_url;
    public String code;
    public String phoneNum;
    public long amount;
    public String phoneName;
    public String chargeType;
    public String bank_provider;
    public int timeToExpired;

    public String toJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString((Object)this);
        }
        catch (JsonProcessingException mapper) {
            return "{\"success\":false,\"errorCode\":\"1001\"}";
        }
    }
}
