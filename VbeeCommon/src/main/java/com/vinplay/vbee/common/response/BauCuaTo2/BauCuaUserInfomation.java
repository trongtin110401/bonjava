package com.vinplay.vbee.common.response.BauCuaTo2;

import java.io.Serializable;

public class BauCuaUserInfomation implements Serializable {
    private String username;
    private long totalBet;
    private long totalCurrentMoney;

    public BauCuaUserInfomation(String username, long totalBet, long totalCurrentMoney) {
        this.username = username;
        this.totalBet = totalBet;
        this.totalCurrentMoney = totalCurrentMoney;
    }

    public BauCuaUserInfomation() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getTotalBet() {
        return totalBet;
    }

    public void setTotalBet(long totalBet) {
        this.totalBet = totalBet;
    }

    public long getTotalCurrentMoney() {
        return totalCurrentMoney;
    }

    public void setTotalCurrentMoney(long totalCurrentMoney) {
        this.totalCurrentMoney = totalCurrentMoney;
    }

    @Override
    public String toString() {
        return "BauCuaUserInfomation{" +
                "username='" + username + '\'' +
                ", totalBet=" + totalBet +
                ", totalCurrentMoney=" + totalCurrentMoney +
                '}';
    }
}
