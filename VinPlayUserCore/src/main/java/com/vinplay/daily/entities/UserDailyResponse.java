package com.vinplay.daily.entities;

public class UserDailyResponse {
    public String nickName;
    public String user_name;
    public String time_log;

    public UserDailyResponse(String nickName, String user_name, String time_log) {
        this.nickName = nickName;
        this.user_name = user_name;
        this.time_log = time_log;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getTime_log() {
        return time_log;
    }

    public void setTime_log(String time_log) {
        this.time_log = time_log;
    }

    @Override
    public String toString() {
        return "UserDailyResponse{" +
                "nickName='" + nickName + '\'' +
                ", user_name='" + user_name + '\'' +
                ", time_log='" + time_log + '\'' +
                '}';
    }




}
