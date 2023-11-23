/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.vinplay.dal.entities.taixiu.ResultTaiXiu
 *  com.vinplay.dal.entities.taixiu.TransactionTaiXiuDetail
 *  com.vinplay.vbee.common.response.BaseResponseModel
 */
package com.vinplay.api.processors.minigame.response;

import com.vinplay.dal.entities.taixiu.NohuTXDetail;
import com.vinplay.vbee.common.response.BaseResponseModel;

import java.util.ArrayList;
import java.util.List;

public class HistoryHuTXResponse
        extends BaseResponseModel {
    private List<NohuTXDetail> nohuTXDetails = new ArrayList<NohuTXDetail>();

    public HistoryHuTXResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }

    public List<NohuTXDetail> getNohuTXDetails() {
        return nohuTXDetails;
    }

    public void setNohuTXDetails(List<NohuTXDetail> nohuTXDetails) {
        this.nohuTXDetails = nohuTXDetails;
    }
}

