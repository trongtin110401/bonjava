package com.vinplay.daily.entities;

public class DailyResponse {
    public String NickName;
    public String CodeDaily;

    public DailyResponse(String nickName , String codeDaily) {
        NickName = nickName;
        CodeDaily = codeDaily;
    }

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String NickName) {
        this.NickName = NickName;
    }

    public String getCodeDaily() {
        return CodeDaily;
    }

    public void setCodeDaily(long TotalInUser) {
        this.CodeDaily = CodeDaily;
    }

}
