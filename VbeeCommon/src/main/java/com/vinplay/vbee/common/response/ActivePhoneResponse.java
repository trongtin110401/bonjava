package com.vinplay.vbee.common.response;


public class ActivePhoneResponse extends BaseResponseModel{
    private String nickname;
    private String phoneNumber;
    private boolean isActive;

    public ActivePhoneResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
