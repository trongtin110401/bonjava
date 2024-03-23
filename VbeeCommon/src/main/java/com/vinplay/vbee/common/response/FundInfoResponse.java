/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.vbee.common.response;

import java.util.List;

public class FundInfoResponse
        extends BaseResponseModel {
    public FundInfoResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }

    private List<BonusFundResponse> funds;

    public List<BonusFundResponse> getFunds() {
        return funds;
    }

    public void setFunds(List<BonusFundResponse> funds) {
        this.funds = funds;
    }
}

