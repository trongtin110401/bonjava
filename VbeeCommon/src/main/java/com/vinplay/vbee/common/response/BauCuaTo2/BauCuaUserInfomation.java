package com.vinplay.vbee.common.response.BauCuaTo2;

import java.io.Serializable;
import java.util.HashMap;

public class BauCuaUserInfomation implements Serializable {
    private String username;
    private long totalBet;
    private long totalCurrentMoney;

    private HashMap<Integer, Long> betDetail = new HashMap<>();

    public BauCuaUserInfomation() {
        initDetailedBettingValue();
    }

    private void initDetailedBettingValue() {
        betDetail.put(0, 0L);
        betDetail.put(1, 0L);
        betDetail.put(2, 0L);
        betDetail.put(3, 0L);
        betDetail.put(4, 0L);
        betDetail.put(5, 0L);
    }

    public BauCuaUserInfomation(String username, long totalBet, long totalCurrentMoney) {
        this.username = username;
        this.totalBet = totalBet;
        this.totalCurrentMoney = totalCurrentMoney;

        initDetailedBettingValue();
    }

    public void increaseBettingValueByPot(int potId, long value) {
        Long betValue = betDetail.get(potId);
        if (betValue == null) {
            betDetail.put(potId, value);
        } else {
            betValue += value;
            betDetail.put(potId, betValue);
        }
    }

    public void resetDetailedBettingValue() {
        initDetailedBettingValue();
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

    public HashMap<Integer, Long> getBetDetail() {
        return betDetail;
    }

    public void setBetDetail(HashMap<Integer, Long> betDetail) {
        this.betDetail = betDetail;
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
