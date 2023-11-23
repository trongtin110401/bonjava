package com.vinplay.api.backend.processors.otp;

import com.vinplay.api.backend.models.UserOTP;
import com.vinplay.vbee.common.response.BaseResponseModel;

import java.util.List;

public class ListOtpResponse extends BaseResponseModel {
    public List<UserOTP> UserOTP;
    public ListOtpResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }
}
