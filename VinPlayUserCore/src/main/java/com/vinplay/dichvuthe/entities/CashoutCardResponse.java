package com.vinplay.dichvuthe.entities;

import com.vinplay.payment.entities.UserWithDrawCard;
import com.vinplay.payment.entities.UserWithdraw;
import com.vinplay.vbee.common.response.BaseResponseModel;

import java.util.List;

public class CashoutCardResponse extends BaseResponseModel {
    public long totalMoney;
    public long totalMoneySuccess;
    public long totalTrans;
    public List<UserWithDrawCard> ListTrans;

    public CashoutCardResponse() {
        super(false, "1001");
    }

    public CashoutCardResponse(long totalMoney, long totalMoneySuccess, long totalTrans, List<UserWithDrawCard> listTrans) {
        super(true, "0");
        this.totalMoney = totalMoney;
        this.totalMoneySuccess = totalMoneySuccess;
        this.totalTrans = totalTrans;
        ListTrans = listTrans;
    }
}
