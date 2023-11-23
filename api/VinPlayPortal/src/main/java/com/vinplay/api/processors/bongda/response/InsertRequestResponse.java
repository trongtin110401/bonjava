package com.vinplay.api.processors.bongda.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class InsertRequestResponse {
   public String code;
    public long money;

    public InsertRequestResponse(String code, long money) {
        this.code = code;
        this.money = money;
    }

    public InsertRequestResponse() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getMoney() {
        return money;
    }

    public void setMoney(long money) {
        this.money = money;
    }
    public String toJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString((Object) this);
        } catch (JsonProcessingException mapper) {
            return "{\"errorCode\":500,\"errorDescription\":\"error\"}";
        }
    }
}
