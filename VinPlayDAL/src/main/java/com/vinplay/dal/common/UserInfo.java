/*
 * Decompiled with CFR 0_116.
 *
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.vinplay.dal.common;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;

public class UserInfo implements Serializable {

    private static final long serialVersionUID = 503536543992946422L;
    private String gameName;
    private volatile long lastLoginTime = 0;

    private long totalDepositBank;
    private long totalDepositMoMo;
    private long totalDepositCard;
    private long totalDepositGiftcode;
    private long totalCashoutBank;
    private long totalCashoutMoMo;
    private long totalBetValue;

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public long getTotalDepositBank() {
        return totalDepositBank;
    }

    public void setTotalDepositBank(long totalDepositBank) {
        this.totalDepositBank = totalDepositBank;
    }

    public long getTotalDepositMoMo() {
        return totalDepositMoMo;
    }

    public void setTotalDepositMoMo(long totalDepositMoMo) {
        this.totalDepositMoMo = totalDepositMoMo;
    }

    public long getTotalDepositCard() {
        return totalDepositCard;
    }

    public void setTotalDepositCard(long totalDepositCard) {
        this.totalDepositCard = totalDepositCard;
    }

    public long getTotalCashoutBank() {
        return totalCashoutBank;
    }

    public void setTotalCashoutBank(long totalCashoutBank) {
        this.totalCashoutBank = totalCashoutBank;
    }

    public long getTotalCashoutMoMo() {
        return totalCashoutMoMo;
    }

    public void setTotalCashoutMoMo(long totalCashoutMoMo) {
        this.totalCashoutMoMo = totalCashoutMoMo;
    }

    public long getTotalDepositGiftcode() {
        return totalDepositGiftcode;
    }

    public void setTotalDepositGiftcode(long totalDepositGiftcode) {
        this.totalDepositGiftcode = totalDepositGiftcode;
    }

    public long getTotalBetValue() {
        return totalBetValue;
    }

    public void setTotalBetValue(long totalBetValue) {
        this.totalBetValue = totalBetValue;
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

