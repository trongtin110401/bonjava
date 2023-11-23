/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.usercore.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TransferMoneyResponse {
    private byte code;
    private long moneyUse;
    private long currentMoney;
    private String nicknameReceive;
    private long currentMoneyReceive;
    private long moneyReceive;

    public TransferMoneyResponse(byte code, long moneyUse, long currentMoney) {
        this.code = code;
        this.moneyUse = moneyUse;
        this.currentMoney = currentMoney;
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

    public long getMoneyReceive() {
        return this.moneyReceive;
    }

    public void setMoneyReceive(long moneyReceive) {
        this.moneyReceive = moneyReceive;
    }

    public long getCurrentMoneyReceive() {
        return this.currentMoneyReceive;
    }

    public void setCurrentMoneyReceive(long currentMoneyReceive) {
        this.currentMoneyReceive = currentMoneyReceive;
    }

    public String getNicknameReceive() {
        return this.nicknameReceive;
    }

    public void setNicknameReceive(String nicknameReceive) {
        this.nicknameReceive = nicknameReceive;
    }

    public byte getCode() {
        return this.code;
    }

    public void setCode(byte code) {
        this.code = code;
    }

    public long getMoneyUse() {
        return this.moneyUse;
    }

    public void setMoneyUse(long moneyUse) {
        this.moneyUse = moneyUse;
    }

    public long getCurrentMoney() {
        return this.currentMoney;
    }

    public void setCurrentMoney(long currentMoney) {
        this.currentMoney = currentMoney;
    }
}

