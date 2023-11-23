package com.vinplay.dichvuthe.entities;

import com.vinplay.vbee.common.response.BaseResponseModel;

import java.util.List;

public class DepositOnePayResponse extends BaseResponseModel {
    public long TotalTrans;
    public long TotalMoney;
    public long TotalSuccess;
    public List<DepositOnePayModel> ListTrans;

    public DepositOnePayResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }

    public DepositOnePayResponse(int totalTrans, long totalMoney, List<DepositOnePayModel> listTrans) {
        super(false, "1001");
        TotalTrans = totalTrans;
        TotalMoney = totalMoney;
        ListTrans = listTrans;
    }

    public DepositOnePayResponse(long totalTrans, long totalMoney, long totalSuccess, List<DepositOnePayModel> listTrans) {
        super(false, "1001");
        TotalTrans = totalTrans;
        TotalMoney = totalMoney;
        TotalSuccess = totalSuccess;
        ListTrans = listTrans;
    }
}
