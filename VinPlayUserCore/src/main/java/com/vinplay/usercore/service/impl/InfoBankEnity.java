package com.vinplay.usercore.service.impl;

public class InfoBankEnity {
    private String nickname;
    private String bankname;
    private String banknumber;
    private String bankbran;
    private String timelog;

    public InfoBankEnity() {
    }

    public InfoBankEnity(String nickname, String bankname, String banknumber, String bankbran, String timelog) {
        this.nickname = nickname;
        this.bankname = bankname;
        this.banknumber = banknumber;
        this.bankbran = bankbran;
        this.timelog = timelog;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getBankname() {
        return bankname;
    }

    public void setBankname(String bankname) {
        this.bankname = bankname;
    }

    public String getBanknumber() {
        return banknumber;
    }

    public void setBanknumber(String banknumber) {
        this.banknumber = banknumber;
    }

    public String getBankbran() {
        return bankbran;
    }

    public void setBankbran(String bankbran) {
        this.bankbran = bankbran;
    }

    public String getTimelog() {
        return timelog;
    }

    public void setTimelog(String timelog) {
        this.timelog = timelog;
    }

    @Override
    public String toString() {
        return "InfoBankEnity{" +
                "nickname='" + nickname + '\'' +
                ", bankname='" + bankname + '\'' +
                ", banknumber='" + banknumber + '\'' +
                ", bankbran='" + bankbran + '\'' +
                ", timelog='" + timelog + '\'' +
                '}';
    }
}
