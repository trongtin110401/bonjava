/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.vbee.common.response;

import com.vinplay.vbee.common.dto.UserUsedGiftCodeAndDepositDto;

import java.util.List;

public class UserUsedCodeAndDepositResponse extends BaseResponseModel{

    List<UserUsedGiftCodeAndDepositDto> results;
    public UserUsedCodeAndDepositResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }

    public List<UserUsedGiftCodeAndDepositDto> getResults() {
        return results;
    }

    public void setResults(List<UserUsedGiftCodeAndDepositDto> results) {
        this.results = results;
    }
}

