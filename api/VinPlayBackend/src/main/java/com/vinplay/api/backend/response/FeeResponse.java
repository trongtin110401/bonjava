/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.vinplay.dal.entities.report.ReportMoneySystemModel
 *  com.vinplay.dal.entities.report.ReportTXModel
 *  com.vinplay.vbee.common.response.BaseResponseModel
 */
package com.vinplay.api.backend.response;

import com.vinplay.vbee.common.response.BaseResponseModel;

public class FeeResponse
        extends BaseResponseModel {
    private long totalFee;

    public FeeResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }

    public long getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(long totalFee) {
        this.totalFee = totalFee;
    }
}

