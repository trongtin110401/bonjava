/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.vbee.common.response;

import com.vinplay.vbee.common.dto.UserMomoInfoDto;

import java.util.List;

public class UserMomoInfoResponse
        extends BaseResponseModel {
    private List<UserMomoInfoDto> momo;

    public List<UserMomoInfoDto> getMomo() {
        return momo;
    }

    public void setMomo(List<UserMomoInfoDto> momo) {
        this.momo = momo;
    }

    public UserMomoInfoResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }
}

