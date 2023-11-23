package com.vinplay.dichvuthe.entities;

import com.vinplay.vbee.common.response.BaseResponseModel;

import java.util.List;

public class DepositBankReponse extends BaseResponseModel {
    public long TotalTrans;
    public long TotalMoney;
    public long TotalSuccess;
    public List<DepositBankModel> ListTrans;

    public DepositBankReponse(boolean success, String errorCode) {
        super(success, errorCode);
    }

    public DepositBankReponse(int totalTrans, long totalMoney, List<DepositBankModel> listTrans) {
        super(false, "1001");
        TotalTrans = totalTrans;
        TotalMoney = totalMoney;
        ListTrans = listTrans;
    }

    public DepositBankReponse(long totalTrans, long totalMoney, long totalSuccess, List<DepositBankModel> listTrans) {
        super(false, "1001");
        TotalTrans = totalTrans;
        TotalMoney = totalMoney;
        TotalSuccess = totalSuccess;
        ListTrans = listTrans;
    }
}
