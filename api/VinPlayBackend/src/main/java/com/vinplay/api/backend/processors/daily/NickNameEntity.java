package com.vinplay.api.backend.processors.daily;

public class NickNameEntity {
    private String nickname;
    private long moneyNap;
    private long moneyRut;

    public NickNameEntity() {
    }

    public NickNameEntity(String nickname, long moneyNap, long moneyRut) {
        this.nickname = nickname;
        this.moneyNap = moneyNap;
        this.moneyRut = moneyRut;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public long getMoneyNap() {
        return moneyNap;
    }

    public void setMoneyNap(long moneyNap) {
        this.moneyNap = moneyNap;
    }

    public long getMoneyRut() {
        return moneyRut;
    }

    public void setMoneyRut(long moneyRut) {
        this.moneyRut = moneyRut;
    }

    @Override
    public String toString() {
        return "NickNameEntity{" +
                "nickname='" + nickname + '\'' +
                ", moneyNap=" + moneyNap +
                ", moneyRut=" + moneyRut +
                '}';
    }
}
