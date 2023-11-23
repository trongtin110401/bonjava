package com.vinplay.api.backend.models;

public class FullUserCode {
    private String username;
    private String nickname;
    private String phone;
    private int active;
    private String code;
    private int use;
    private String timelog;

    public FullUserCode() {
    }

    public FullUserCode(String username, String nickname, String phone, int active, String code, int use, String timelog) {
        this.username = username;
        this.nickname = nickname;
        this.phone = phone;
        this.active = active;
        this.code = code;
        this.use = use;
        this.timelog = timelog;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
        return "FullUserCode{" +
                "username='" + username + '\'' +
                ", nickname='" + nickname + '\'' +
                ", phone='" + phone + '\'' +
                ", active=" + active +
                ", code='" + code + '\'' +
                ", use=" + use +
                ", timelog='" + timelog + '\'' +
                '}';
    }
}
