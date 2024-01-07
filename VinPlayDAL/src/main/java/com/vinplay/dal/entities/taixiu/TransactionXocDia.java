/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.dal.entities.taixiu;

public class TransactionXocDia {
    public long referenceId;
    public int userId;
    public String username;
    public long totalPrize = 0L;
    public String timestamp;

    public BetResult betResult;
    public String result;
    public long totalExchange = 0L;

    public TransactionXocDia() {
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

    public long getTotalPrize() {
        return totalPrize;
    }

    public void setTotalPrize(long totalPrize) {
        this.totalPrize = totalPrize;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public BetResult getBetResult() {
        return betResult;
    }

    public void setBetResult(BetResult betResult) {
        this.betResult = betResult;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public long getTotalExchange() {
        return totalExchange;
    }

    public void setTotalExchange(long totalExchange) {
        this.totalExchange = totalExchange;
    }
}

