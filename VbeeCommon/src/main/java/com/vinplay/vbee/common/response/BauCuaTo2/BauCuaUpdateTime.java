package com.vinplay.vbee.common.response.BauCuaTo2;

import com.vinplay.vbee.common.response.BaseResponseModel;

public class BauCuaUpdateTime extends BaseResponseModel {

    long remainingTime;
    boolean isBetting;
    public BauCuaUpdateTime(boolean success, String errorCode) {
        super(success, errorCode);
    }

    @Override
    public String toString() {
        return "BauCuaUpdateTime{" +
                "remainingTime=" + remainingTime +
                ", isBetting=" + isBetting +
                '}';
    }

    public long getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(long remainingTime) {
        this.remainingTime = remainingTime;
    }

    public boolean isBetting() {
        return isBetting;
    }

    public void setBetting(boolean betting) {
        isBetting = betting;
    }
}
