/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vinplay.api.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author HA
 */
public class GetUserInfoResponse {
    private int code;
    private String message;
    private String nickname;
    private String mobile;
    private String identification;
    private String email;
    private long vip_point;
    private long current_balance;    
    
    public GetUserInfoResponse(int _code, String _message)
    {
        this.code = _code;
        this.message = _message;
    }
    
    public String toJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString((Object)this);
        }
        catch (JsonProcessingException mapper) {
            return "{\"code\":500,\"message\":\"error\"}";
        }
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getVip_point() {
        return vip_point;
    }

    public void setVip_point(long vip_point) {
        this.vip_point = vip_point;
    }

    public long getCurrent_balance() {
        return current_balance;
    }

    public void setCurrent_balance(long current_balance) {
        this.current_balance = current_balance;
    }
}


