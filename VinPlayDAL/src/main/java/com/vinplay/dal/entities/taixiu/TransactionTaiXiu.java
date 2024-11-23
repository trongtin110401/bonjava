/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.dal.entities.taixiu;

public class TransactionTaiXiu {
    public long referenceId;
    public int userId;
    public String username;
    public long betValue = 0L;
    public int betSide;
    public long totalPrize = 0L;
    public long totalRefund = 0L;
    public int moneyType;
    public String timestamp;
    public String resultPhien;
    public long totalExchange = 0L;
    public long kubetSessionId;

    public TransactionTaiXiu() {
    }

    public long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(long referenceId) {
        this.referenceId = referenceId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getBetValue() {
        return betValue;
    }

    public void setBetValue(long betValue) {
        this.betValue = betValue;
    }

    public int getBetSide() {
        return betSide;
    }

    public void setBetSide(int betSide) {
        this.betSide = betSide;
    }

    public long getTotalPrize() {
        return totalPrize;
    }

    public void setTotalPrize(long totalPrize) {
        this.totalPrize = totalPrize;
    }

    public long getTotalRefund() {
        return totalRefund;
    }

    public void setTotalRefund(long totalRefund) {
        this.totalRefund = totalRefund;
    }

    public int getMoneyType() {
        return moneyType;
    }

    public void setMoneyType(int moneyType) {
        this.moneyType = moneyType;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getResultPhien() {
        return resultPhien;
    }

    public void setResultPhien(String resultPhien) {
        this.resultPhien = resultPhien;
    }

    public long getTotalExchange() {
        return totalExchange;
    }

    public void setTotalExchange(long totalExchange) {
        this.totalExchange = totalExchange;
    }

    public long getKubetSessionId() {
        return kubetSessionId;
    }

    public void setKubetSessionId(long kubetSessionId) {
        this.kubetSessionId = kubetSessionId;
    }
}

