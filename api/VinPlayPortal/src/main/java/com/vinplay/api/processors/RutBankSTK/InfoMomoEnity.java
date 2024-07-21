package com.vinplay.api.processors.RutBankSTK;

public class InfoMomoEnity {
    private String nickname;
    private String phoneNumber;
    private String timeLog;
    private String accountName;

    public InfoMomoEnity() {
    }

    public InfoMomoEnity(String nickname, String phoneNumber, String timeLog, String accountName) {
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.timeLog = timeLog;
        this.accountName = accountName;
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

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
}
