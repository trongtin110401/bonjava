/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.vinplay.gamebai.entities.BossXocDiaModel
 *  com.vinplay.vbee.common.response.BaseResponseModel
 */
package com.vinplay.vbee.common.response;

public class MoneyShootFishResponse
        extends BaseResponseModel {
    public MoneyShootFishResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }

    public MoneyShootFishResponse(boolean success, String errorCode, String nickname, long totalProfit) {
        super(success, errorCode);
        this.nickname = nickname;
        this.totalProfit = totalProfit;
    }

    private String nickname;
    private long totalCashIn;
    private long totalCashOut;
    private long totalProfit;

    public long getTotalCashIn() {
        return totalCashIn;
    }

    public void setTotalCashIn(long totalCashIn) {
        this.totalCashIn = totalCashIn;
    }

    public long getTotalCashOut() {
        return totalCashOut;
    }

    public void setTotalCashOut(long totalCashOut) {
        this.totalCashOut = totalCashOut;
    }

    public long getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(long totalProfit) {
        this.totalProfit = totalProfit;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}

