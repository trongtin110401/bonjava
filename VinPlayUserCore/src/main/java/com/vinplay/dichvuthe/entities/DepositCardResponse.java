package com.vinplay.dichvuthe.entities;

import com.vinplay.vbee.common.response.BaseResponseModel;

import java.util.List;

public class DepositCardResponse extends BaseResponseModel {
    public long TotalTrans;
    public long TotalMoney;
    public long TotalSuccess;

    public List<DepositMobileCardModel> ListTrans;
    public DepositCardResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }
    public DepositCardResponse(int totalTrans, long totalMoney, List<DepositMobileCardModel> listTrans) {
        super(false, "1001");
        TotalTrans = totalTrans;
        TotalMoney = totalMoney;
        ListTrans = listTrans;
    }

    public DepositCardResponse( long totalTrans, long totalMoney, long totalSuccess, List<DepositMobileCardModel> listTrans) {
        super(false, "1001");
        TotalTrans = totalTrans;
        TotalMoney = totalMoney;
        TotalSuccess = totalSuccess;
        ListTrans = listTrans;
    }
}
