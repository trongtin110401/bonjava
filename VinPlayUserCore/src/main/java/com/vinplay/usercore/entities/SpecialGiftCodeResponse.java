package com.vinplay.usercore.entities;

import com.vinplay.vbee.common.models.SpecialGiftCode;
import com.vinplay.vbee.common.response.BaseResponseModel;

import java.util.ArrayList;
import java.util.List;

public class SpecialGiftCodeResponse
        extends BaseResponseModel {
    private SpecialGiftCode giftCode;

    public SpecialGiftCodeResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }

    public SpecialGiftCode getGiftCode() {
        return giftCode;
    }

    public void setGiftCode(SpecialGiftCode giftCode) {
        this.giftCode = giftCode;
    }
}

