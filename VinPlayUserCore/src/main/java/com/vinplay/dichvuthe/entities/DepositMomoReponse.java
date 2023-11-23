package com.vinplay.dichvuthe.entities;

import com.vinplay.vbee.common.response.BaseResponseModel;

import java.util.List;

public class DepositMomoReponse extends BaseResponseModel {
    public long TotalTrans;
    public long TotalMoney;
    public long TotalSuccess;
    public List<DepositMomoModel> ListTrans;

    public DepositMomoReponse(boolean success, String errorCode) {
        super(success, errorCode);
    }
    public DepositMomoReponse(int totalTrans, long totalMoney, List<DepositMomoModel> listTrans) {
        super(false, "1001");
        TotalTrans = totalTrans;
        TotalMoney = totalMoney;
        ListTrans = listTrans;
    }

    public DepositMomoReponse(long totalTrans, long totalMoney, long totalSuccess, List<DepositMomoModel> listTrans) {
        super(false, "1001");
        TotalTrans = totalTrans;
        TotalMoney = totalMoney;
        TotalSuccess = totalSuccess;
        ListTrans = listTrans;
    }
}
