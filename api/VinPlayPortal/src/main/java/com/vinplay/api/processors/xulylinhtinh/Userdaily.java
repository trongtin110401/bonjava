package com.vinplay.api.processors.xulylinhtinh;

public class Userdaily {
    private int userId;
    private String user_name;
    private String nickName;
    private String id_daily;
    private String time_log;

    public Userdaily() {
    }

    public Userdaily(int userId, String user_name, String nickName, String id_daily, String time_log) {
        this.userId = userId;
        this.user_name = user_name;
        this.nickName = nickName;
        this.id_daily = id_daily;
        this.time_log = time_log;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getId_daily() {
        return id_daily;
    }

    public void setId_daily(String id_daily) {
        this.id_daily = id_daily;
    }

    public String getTime_log() {
        return time_log;
    }

    public void setTime_log(String time_log) {
        this.time_log = time_log;
    }

    @Override
    public String toString() {
        return "Userdaily{" +
                "userId=" + userId +
                ", user_name='" + user_name + '\'' +
                ", nickName='" + nickName + '\'' +
                ", id_daily='" + id_daily + '\'' +
                ", time_log='" + time_log + '\'' +
                '}';
    }
}
