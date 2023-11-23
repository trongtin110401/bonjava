package com.vinplay.removedb;

import com.vinplay.vbee.common.response.BaseResponseModel;

public class removedbResponse extends BaseResponseModel {
    long count;
    public removedbResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }
    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
