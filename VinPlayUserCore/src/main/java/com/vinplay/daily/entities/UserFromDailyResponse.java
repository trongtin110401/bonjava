package com.vinplay.daily.entities;

import com.vinplay.lognaprut.entities.HistoryTransModel;
import com.vinplay.vbee.common.response.BaseResponseModel;
import java.util.List;

public class UserFromDailyResponse extends BaseResponseModel {
    public long TotalIn;
    public long TotalOut;
    public long TotalUser;
    public List<HistoryTransModel> HistoryTransIn;
    public List<HistoryTransModel> HistoryTransOut;
    public List<UserWinLostResponse> ListUser;
    public List<UserDailyResponse> ListUserOfDaily;
    public UserFromDailyResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }
}
