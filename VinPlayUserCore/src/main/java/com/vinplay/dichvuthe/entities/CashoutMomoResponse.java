package com.vinplay.dichvuthe.entities;

import com.vinplay.payment.entities.UserWithdraw;
import com.vinplay.payment.entities.UserWithdrawMomo;
import com.vinplay.vbee.common.response.BaseResponseModel;

import java.util.List;

public class CashoutMomoResponse  extends BaseResponseModel {
    public long totalMoney;
    public long totalMoneySuccess;
    public long totalTrans;
    public List<UserWithdrawMomo> ListTrans;

    public CashoutMomoResponse() {
        super(false, "1001");
    }

    public CashoutMomoResponse(long totalMoney, long totalMoneySuccess, long totalTrans, List<UserWithdrawMomo> listTrans) {
        super(true, "0");
        this.totalMoney = totalMoney;
        this.totalMoneySuccess = totalMoneySuccess;
        this.totalTrans = totalTrans;
        ListTrans = listTrans;
    }
}
