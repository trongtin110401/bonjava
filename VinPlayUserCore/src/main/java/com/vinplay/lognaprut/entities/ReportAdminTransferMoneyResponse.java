/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.vinplay.vbee.common.response.BaseResponseModel
 */
package com.vinplay.lognaprut.entities;

import com.vinplay.vbee.common.response.BaseResponseModel;

public class ReportAdminTransferMoneyResponse
        extends BaseResponseModel {
    private long addMoney;

    private long subtractMoney;

    private long totalMoneyGiftCode;

    public long getAddMoney() {
        return addMoney;
    }

    public void setAddMoney(long addMoney) {
        this.addMoney = addMoney;
    }

    public long getSubtractMoney() {
        return subtractMoney;
    }

    public void setSubtractMoney(long subtractMoney) {
        this.subtractMoney = subtractMoney;
    }

    public long getTotalMoneyGiftCode() {
        return totalMoneyGiftCode;
    }

    public void setTotalMoneyGiftCode(long totalMoneyGiftCode) {
        this.totalMoneyGiftCode = totalMoneyGiftCode;
    }

    public ReportAdminTransferMoneyResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }


}

