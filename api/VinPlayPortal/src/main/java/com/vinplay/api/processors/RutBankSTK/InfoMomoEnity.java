package com.vinplay.api.processors.RutBankSTK;

public class InfoMomoEnity {
    private String nickname;
    private String phoneNumber;
    private String timeLog;

    public InfoMomoEnity() {
    }

    public InfoMomoEnity(String nickname, String phoneNumber, String timeLog) {
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.timeLog = timeLog;
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

    public String getTimeLog() {
        return timeLog;
    }

    public void setTimeLog(String timeLog) {
        this.timeLog = timeLog;
    }
}
