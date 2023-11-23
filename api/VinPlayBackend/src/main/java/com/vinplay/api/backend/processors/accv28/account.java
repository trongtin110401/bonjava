package com.vinplay.api.backend.processors.accv28;

public class account {
    private String username;
    private String password;
    private String codedaily;
    private String phone;
    private boolean dangky;
    private String timelog;
    private String nickname;
    private String tien;
    private String id;

    public account() {
    }

    public account(String username, String password, String codedaily, String phone, boolean dangky) {
        this.username = username;
        this.password = password;
        this.codedaily = codedaily;
        this.phone = phone;
        this.dangky = dangky;
    }

    public account(String username, String password, String codedaily, String phone, boolean dangky, String timelog) {
        this.username = username;
        this.password = password;
        this.codedaily = codedaily;
        this.phone = phone;
        this.dangky = dangky;
        this.timelog = timelog;
    }

    public account(String username, String password, String codedaily, String phone, boolean dangky, String timelog, String nickname, String tien) {
        this.username = username;
        this.password = password;
        this.codedaily = codedaily;
        this.phone = phone;
        this.dangky = dangky;
        this.timelog = timelog;
        this.nickname = nickname;
        this.tien = tien;
    }

    public account(String username, String password, String codedaily, String phone, boolean dangky, String timelog, String nickname, String tien, String id) {
        this.username = username;
        this.password = password;
        this.codedaily = codedaily;
        this.phone = phone;
        this.dangky = dangky;
        this.timelog = timelog;
        this.nickname = nickname;
        this.tien = tien;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getTien() {
        return tien;
    }

    public void setTien(String tien) {
        this.tien = tien;
    }

    public String getTimelog() {
        return timelog;
    }

    public void setTimelog(String timelog) {
        this.timelog = timelog;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCodedaily() {
        return codedaily;
    }

    public void setCodedaily(String codedaily) {
        this.codedaily = codedaily;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isDangky() {
        return dangky;
    }

    public void setDangky(boolean dangky) {
        this.dangky = dangky;
    }

    @Override
    public String toString() {
        return "account{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", codedaily='" + codedaily + '\'' +
                ", phone='" + phone + '\'' +
                ", dangky=" + dangky +
                ", timelog='" + timelog + '\'' +
                ", nickname='" + nickname + '\'' +
                ", tien='" + tien + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
