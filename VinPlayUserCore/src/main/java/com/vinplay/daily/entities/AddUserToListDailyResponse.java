package com.vinplay.daily.entities;
import com.vinplay.vbee.common.response.BaseResponseModel;

public class AddUserToListDailyResponse extends BaseResponseModel {
    public AddUserToListDailyResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }
}