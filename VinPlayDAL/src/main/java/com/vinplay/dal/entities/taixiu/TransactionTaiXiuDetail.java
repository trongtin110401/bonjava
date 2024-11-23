/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.dal.entities.taixiu;

import java.sql.Date;

public class TransactionTaiXiuDetail {
    public long referenceId;
    public String transactionCode;
    public int transactionId;
    public int userId;
    public String username;
    public long betValue = 0L;
    public int betSide;
    public long prize = 0L;
    public long refund = 0L;
    public int inputTime;
    public int moneyType;
    public long totalExchange = 0L;
    public Date timestamp;
    public long kubetSessionId;


    public TransactionTaiXiuDetail(long referenceId, int userId, String username, long betValue, int betSide, int inputTime, int moneyType) {
        this.referenceId = referenceId;
        this.userId = userId;
        this.username = username;
        this.betValue = betValue;
        this.betSide = betSide;
        this.prize = 0L;
        this.totalExchange = 0L;
        this.refund = 0L;
        this.inputTime = inputTime;
        this.moneyType = moneyType;
    }

    public TransactionTaiXiuDetail(long referenceId, int userId, String username, long betValue, int betSide, int inputTime, int moneyType, long kubetSessionId) {
        this.referenceId = referenceId;
        this.userId = userId;
        this.username = username;
        this.betValue = betValue;
        this.betSide = betSide;
        this.prize = 0L;
        this.totalExchange = 0L;
        this.refund = 0L;
        this.inputTime = inputTime;
        this.moneyType = moneyType;
        this.kubetSessionId = kubetSessionId;
    }

    public TransactionTaiXiuDetail() {
    }

    public void genTransactionCode() {
        this.transactionCode = this.username + "_" + this.inputTime + System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "TransactionTaiXiuDetail{" +
                "referenceId=" + referenceId +
                ", transactionCode='" + transactionCode + '\'' +
                ", transactionId=" + transactionId +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", betValue=" + betValue +
                ", betSide=" + betSide +
                ", prize=" + prize +
                ", refund=" + refund +
                ", inputTime=" + inputTime +
                ", moneyType=" + moneyType +
                ", totalExchange=" + totalExchange +
                ", timestamp=" + timestamp +
                '}';
    }
}

