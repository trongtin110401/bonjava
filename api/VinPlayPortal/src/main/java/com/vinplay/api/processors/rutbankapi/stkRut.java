package com.vinplay.api.processors.rutbankapi;

public class stkRut {
    private String nickname;
    private String number;
    private String name;
    private String bank;
    private long tien;

    public stkRut() {
    }

    public stkRut(String nickname, String number, String name, String bank, long tien) {
        this.nickname = nickname;
        this.number = number;
        this.name = name;
        this.bank = bank;
        this.tien = tien;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public long getTien() {
        return tien;
    }

    public void setTien(long tien) {
        this.tien = tien;
    }

    @Override
    public String toString() {
        return "stkRut{" +
                "nickname='" + nickname + '\'' +
                ", number='" + number + '\'' +
                ", name='" + name + '\'' +
                ", bank='" + bank + '\'' +
                ", tien=" + tien +
                '}';
    }
}
