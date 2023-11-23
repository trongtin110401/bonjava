package com.vinplay.daily.entities;

public class BotTeleDailyResponse {
    public String NickName;
    public String IDTele;

    public BotTeleDailyResponse(String nickName , String idTele) {
        NickName = nickName;
        IDTele = idTele;
    }

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String NickName) {
        this.NickName = NickName;
    }

    public String getIDTele() {
        return IDTele;
    }

    public void setIDTele(String IDTele) {
        this.IDTele = IDTele;
    }

}
