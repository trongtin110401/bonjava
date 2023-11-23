package com.vinplay.dichvuthe.entities;

import com.vinplay.payment.entities.UserWithdraw;
import com.vinplay.vbee.common.response.BaseResponseModel;

import java.util.List;

public class CashoutBankResponse extends BaseResponseModel {
    public long totalMoney;
    public long totalMoneySuccess;
    public long totalTrans;
    public List<UserWithdraw> ListTrans;

    public CashoutBankResponse() {
        super(false, "1001");
    }

    public CashoutBankResponse(long totalMoney, long totalMoneySuccess, long totalTrans, List<UserWithdraw> listTrans) {
        super(true, "0");
        this.totalMoney = totalMoney;
        this.totalMoneySuccess = totalMoneySuccess;
        this.totalTrans = totalTrans;
        ListTrans = listTrans;
    }
}
