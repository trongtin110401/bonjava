/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.vbee.common.response;

import com.vinplay.vbee.common.models.cache.UserCacheModel;

import java.util.ArrayList;
import java.util.List;

public class UserOnlineResponse
        extends BaseResponseModel {
    private List<UserCacheModel> transactions = new ArrayList<>();

    public UserOnlineResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }

    public List<UserCacheModel> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<UserCacheModel> transactions) {
        this.transactions = transactions;
    }
}

