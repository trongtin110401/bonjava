package com.vinplay.vbee.common.messages.marketing;

import com.vinplay.vbee.common.messages.BaseMessage;

public class UserAccessLogMessage extends BaseMessage {

    private long userId;
    private String device;
    private long accessTime;
    private int utmId;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public long getAccessTime() {
        return accessTime;
    }

    public void setAccessTime(long accessTime) {
        this.accessTime = accessTime;
    }

    public int getUtmId() {
        return utmId;
    }

    public void setUtmId(int utmId) {
        this.utmId = utmId;
    }
}
