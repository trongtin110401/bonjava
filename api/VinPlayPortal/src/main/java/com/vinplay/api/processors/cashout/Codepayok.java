package com.vinplay.api.processors.cashout;

public class Codepayok {
    private String nickname;
    private String codepay;
    private int use;
    private String timelog;

    public Codepayok() {
    }

    public Codepayok(String nickname, String codepay, int use, String timelog) {
        this.nickname = nickname;
        this.codepay = codepay;
        this.use = use;
        this.timelog = timelog;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getCodepay() {
        return codepay;
    }

    public void setCodepay(String codepay) {
        this.codepay = codepay;
    }

    public int getUse() {
        return use;
    }

    public void setUse(int use) {
        this.use = use;
    }

    public String getTimelog() {
        return timelog;
    }

    public void setTimelog(String timelog) {
        this.timelog = timelog;
    }

    @Override
    public String toString() {
        return "Codepayok{" +
                "nickname='" + nickname + '\'' +
                ", codepay='" + codepay + '\'' +
                ", use=" + use +
                ", timelog='" + timelog + '\'' +
                '}';
    }
}
