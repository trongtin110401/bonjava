package com.vinplay.daily.entities;

import com.vinplay.vbee.common.response.BaseResponseModel;

import java.util.List;

public class ListBotTeleDailyResponse extends BaseResponseModel {
    public List<BotTeleDailyResponse> ListBotTeleDaily;
    public ListBotTeleDailyResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }
}
