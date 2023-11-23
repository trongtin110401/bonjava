package com.vinplay.momo;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinplay.vbee.common.utils.VinPlayUtils;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;

public class RequestWithDrawCardAuto {

    String telco;
    int value;
    int amount = 1;
    String username = "daodaosunvin";
    String timestamp = new Timestamp(System.currentTimeMillis()).getTime() + "";
    String checkSum;

    public RequestWithDrawCardAuto(String telco, int value) {
        this.telco = telco;
        this.value = value;

    }

    public String getTelco() {
        return telco;
    }

    public void setTelco(String telco) {
        this.telco = telco;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }

    public String getCheckSum() {
        try {
            return VinPlayUtils.getMD5Hash(telco + value + amount + username + timestamp + "#G&VeFpdaodaosunvin");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String toJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString((Object) this);
        } catch (JsonProcessingException mapper) {
            return "{\"success\":false,\"errorCode\":\"1001\"}";
        }
    }
}
