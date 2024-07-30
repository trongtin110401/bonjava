package com.vinplay.vbee.common.response;

public class UserCCUResponse {

    private String nickName;
    private long totalDeposit;
    private long totalCashOut;

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
}
