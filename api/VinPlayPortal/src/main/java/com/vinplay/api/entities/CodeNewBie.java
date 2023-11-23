package com.vinplay.api.entities;

public class CodeNewBie {
    private String username;
    private String nickname;
    private String code;
    private int use;

    public CodeNewBie() {
    }

    public CodeNewBie(String username, String nickname, String code, int use) {
        this.username = username;
        this.nickname = nickname;
        this.code = code;
        this.use = use;
    }

    public int getUse() {
        return use;
    }

    public void setUse(int use) {
        this.use = use;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "CodeNewBie{" +
                "username='" + username + '\'' +
                ", nickname='" + nickname + '\'' +
                ", code='" + code + '\'' +
                ", use=" + use +
                '}';
    }
}
