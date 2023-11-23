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
public class AgentTransferMoneyResponse {
    public String nick_name_send;
    public String nick_name_receive;
    public long money_send;
    public long money_receive;
    public int status;
    public long fee;
    public String trans_time;
    public int top_ds;
    public int process;
    public String des_send;
    public String des_receive;
    public String trans_id;
    public int code;
    public String message;
    
    public AgentTransferMoneyResponse(int _code, String _message)
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
}
