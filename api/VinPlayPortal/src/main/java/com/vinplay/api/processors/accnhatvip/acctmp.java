package com.vinplay.api.processors.accnhatvip;

public class acctmp {
    private String nickname;
    private String tien;

    public acctmp() {
    }

    public acctmp(String nickname, String tien) {
        this.nickname = nickname;
        this.tien = tien;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getTien() {
        return tien;
    }

    public void setTien(String tien) {
        this.tien = tien;
    }

    @Override
    public String toString() {
        return "acctmp{" +
                "nickname='" + nickname + '\'' +
                ", tien='" + tien + '\'' +
                '}';
    }
}
