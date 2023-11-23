package com.vinplay.api.backend.processors.otp;

public class UserOTP {
    private String nickname;
    private String username;
    private String phone;
    private String otp;
    private int active;
    private long creat_time;
    private long active_time;
    private int turn;
    private String timelog;

    public UserOTP() {
    }

    public UserOTP(String nickname, String username, String phone, String otp, int active, long creat_time, long active_time, int turn, String timelog) {
        this.nickname = nickname;
        this.username = username;
        this.phone = phone;
        this.otp = otp;
        this.active = active;
        this.creat_time = creat_time;
        this.active_time = active_time;
        this.turn = turn;
        this.timelog = timelog;
    }

    public String getTimelog() {
        return timelog;
    }

    public void setTimelog(String timelog) {
        this.timelog = timelog;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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

    public long getCreat_time() {
        return creat_time;
    }

    public void setCreat_time(long creat_time) {
        this.creat_time = creat_time;
    }

    public long getActive_time() {
        return active_time;
    }

    public void setActive_time(long active_time) {
        this.active_time = active_time;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    @Override
    public String toString() {
        return "UserOTP{" +
                "nickname='" + nickname + '\'' +
                ", username='" + username + '\'' +
                ", phone='" + phone + '\'' +
                ", otp='" + otp + '\'' +
                ", active=" + active +
                ", creat_time=" + creat_time +
                ", active_time=" + active_time +
                ", turn=" + turn +
                ", timelog='" + timelog + '\'' +
                '}';
    }
}
