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

    public TransactionTaiXiu() {
    }

    public TransactionTaiXiu(long referenceId, int userId, String username, long betValue, int betSide, long totalPrize, long totalRefund, int moneyType, String timestamp, String resultPhien, long totalExchange) {
        this.referenceId = referenceId;
        this.userId = userId;
        this.username = username;
        this.betValue = betValue;
        this.betSide = betSide;
        this.totalPrize = totalPrize;
        this.totalRefund = totalRefund;
        this.moneyType = moneyType;
        this.timestamp = timestamp;
        this.resultPhien = resultPhien;
        this.totalExchange = totalExchange;
    }
}

