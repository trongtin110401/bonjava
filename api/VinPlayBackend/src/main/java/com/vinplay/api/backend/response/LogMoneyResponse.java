/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.vbee.common.response.BaseResponseModel
 *  com.vinplay.vbee.common.response.LogUserMoneyResponse
 */
package com.vinplay.api.backend.response;

import com.vinplay.api.backend.processors.daily.UserVinEntity;
import com.vinplay.vbee.common.response.BaseResponseModel;
import com.vinplay.vbee.common.response.LogUserMoneyResponse;
import java.util.ArrayList;
import java.util.List;

public class LogMoneyResponse
extends BaseResponseModel {
    private int totalPages;
    private List<LogUserMoneyResponse> transactions = new ArrayList<LogUserMoneyResponse>();

    private List<UserVinEntity> transactionsbyelk = new ArrayList<>();

    public LogMoneyResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }

    public List<LogUserMoneyResponse> getTransactions() {
        return this.transactions;
    }
    public List<UserVinEntity> getTransactionselk() {
        return this.transactionsbyelk;
    }

    public void setTransactions(List<LogUserMoneyResponse> transactions) {
        this.transactions = transactions;
    }

    public void setTransactionsbyelk(List<UserVinEntity> transactions2) {
        this.transactionsbyelk = transactions2;
    }

    public int getTotalPages() {
        return this.totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}

