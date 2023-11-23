package com.vinplay.daily.entities;

import com.vinplay.vbee.common.response.BaseResponseModel;

import java.util.List;

public class ListDailyResponse extends BaseResponseModel {
    public List<DailyResponse> ListDaily;
    public ListDailyResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }
}
