package com.vinplay.common.report;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;

public class RechargebyonepayotpAdminObj implements Serializable {
    public String Id;
    public int Status;
    public String OtpNumber;

    public RechargebyonepayotpAdminObj() {
    }

    public RechargebyonepayotpAdminObj(String id, int status, String otpNumber) {
        Id = id;
        Status = status;
        OtpNumber = otpNumber;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getOtpNumber() {
        return OtpNumber;
    }

    public void setOtpNumber(String otpNumber) {
        OtpNumber = otpNumber;
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
