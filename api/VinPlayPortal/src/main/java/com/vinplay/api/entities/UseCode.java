package com.vinplay.api.entities;

public class UseCode {
    private String nickname;
    private String code;
    private int use;
    private String timelog;
    private String username;
    private String phone;
    private int active;

    public UseCode() {
    }

//    public UseCode(String nickname, String code, int use, String timelog) {
//        this.nickname = nickname;
//        this.code = code;
//        this.use = use;
//        this.timelog = timelog;
//    }

    public UseCode(String nickname, String code, int use, String timelog, String username, String phone, int active) {
        this.nickname = nickname;
        this.code = code;
        this.use = use;
        this.timelog = timelog;
        this.username = username;
        this.phone = phone;
        this.active = active;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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
        return "UseCode{" +
                "nickname='" + nickname + '\'' +
                ", code='" + code + '\'' +
                ", use=" + use +
                ", timelog='" + timelog + '\'' +
                ", username='" + username + '\'' +
                ", phone='" + phone + '\'' +
                ", active=" + active +
                '}';
    }
}
