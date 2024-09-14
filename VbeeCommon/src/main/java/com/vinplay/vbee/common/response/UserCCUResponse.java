package com.vinplay.vbee.common.response;

public class UserCCUResponse {

    public String nickName;
    public long totalDeposit;
    public long totalCashOut;
    public long totalMoney;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public long getTotalDeposit() {
        return totalDeposit;
    }

    public void setTotalDeposit(long totalDeposit) {
        this.totalDeposit = totalDeposit;
    }

    public long getTotalCashOut() {
        return totalCashOut;
    }

    public void setTotalCashOut(long totalCashOut) {
        this.totalCashOut = totalCashOut;
    }

    public long getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(long totalMoney) {
        this.totalMoney = totalMoney;
    }
}
