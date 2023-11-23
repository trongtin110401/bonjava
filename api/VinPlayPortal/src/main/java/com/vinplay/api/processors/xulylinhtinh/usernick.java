package com.vinplay.api.processors.xulylinhtinh;

public class usernick {
    private String username;
    private String nickname;
    private String daily;

    public usernick() {
    }

    public usernick(String username, String nickname) {
        this.username = username;
        this.nickname = nickname;
    }

    public usernick(String username, String nickname, String daily) {
        this.username = username;
        this.nickname = nickname;
        this.daily = daily;
    }

    public String getDaily() {
        return daily;
    }

    public void setDaily(String daily) {
        this.daily = daily;
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

    @Override
    public String toString() {
        return "usernick{" +
                "username='" + username + '\'' +
                ", nickname='" + nickname + '\'' +
                ", daily='" + daily + '\'' +
                '}';
    }
}
