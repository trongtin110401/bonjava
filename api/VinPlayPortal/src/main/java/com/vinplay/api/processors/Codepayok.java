package com.vinplay.api.processors;

public class Codepayok {
    private String nickname;
    private String codepay;
    private int use;
    private String timelog;
    private String bankname;
    private String transid;

    public Codepayok() {
    }

//    public Codepayok(String nickname, String codepay, int use, String timelog, String bankname) {
//        this.nickname = nickname;
//        this.codepay = codepay;
//        this.use = use;
//        this.timelog = timelog;
//        this.bankname = bankname;
//    }

    public Codepayok(String nickname, String codepay, int use, String timelog, String bankname, String transid) {
        this.nickname = nickname;
        this.codepay = codepay;
        this.use = use;
        this.timelog = timelog;
        this.bankname = bankname;
        this.transid = transid;
    }

    public String getTransid() {
        return transid;
    }

    public void setTransid(String transid) {
        this.transid = transid;
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

    public String getBankname() {
        return bankname;
    }

    public void setBankname(String bankname) {
        this.bankname = bankname;
    }

    @Override
    public String toString() {
        return "Codepayok{" +
                "nickname='" + nickname + '\'' +
                ", codepay='" + codepay + '\'' +
                ", use=" + use +
                ", timelog='" + timelog + '\'' +
                ", bankname='" + bankname + '\'' +
                ", transid='" + transid + '\'' +
                '}';
    }
}
