/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.vbee.common.response;

import com.vinplay.vbee.common.dto.UserBankInfoDto;

import java.util.List;

public class UserBankInfoResponse
        extends BaseResponseModel {
    private List<UserBankInfoDto> banks;

    public List<UserBankInfoDto> getBanks() {
        return banks;
    }

    public void setBanks(List<UserBankInfoDto> banks) {
        this.banks = banks;
    }

    public UserBankInfoResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }
}

