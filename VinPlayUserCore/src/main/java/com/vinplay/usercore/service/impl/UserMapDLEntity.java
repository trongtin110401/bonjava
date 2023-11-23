package com.vinplay.usercore.service.impl;

public class UserMapDLEntity {
    private int userID;
    private String user_name;
    private String nickName;
    private String id_daily;
    private String time_log;
    private String id_elk;

    public UserMapDLEntity() {
    }

    public UserMapDLEntity(int userID, String user_name, String nickName, String id_daily, String time_log) {
        this.userID = userID;
        this.user_name = user_name;
        this.nickName = nickName;
        this.id_daily = id_daily;
        this.time_log = time_log;
    }

    public String getId_elk() {
        return id_elk;
    }

    public void setId_elk(String id_elk) {
        this.id_elk = id_elk;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
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
        return "UserMapDLEntity{" +
                "userID=" + userID +
                ", user_name='" + user_name + '\'' +
                ", nickName='" + nickName + '\'' +
                ", id_daily='" + id_daily + '\'' +
                ", time_log='" + time_log + '\'' +
                ", id_elk='" + id_elk + '\'' +
                '}';
    }
}
