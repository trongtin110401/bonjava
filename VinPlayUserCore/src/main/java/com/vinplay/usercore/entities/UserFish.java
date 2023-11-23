package com.vinplay.usercore.entities;

public class UserFish {
    public int Id;
    public String Username;
    public String Nickname;
    public String Password;
    public int Active;
    public int Type;
    public long Cash;
    public long CashSafe;
    public long CashSilver;
    public long VipPoint;
    public String PhoneNumber;

    public UserFish(int id, String username, String nickname, int active, long cash) {

        Id = id;
        Username = username;
        Nickname = nickname;
        Active = active;
        Cash = cash;
    }
    public UserFish() {


    }

    public void setId(int id) {
        Id = id;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public void setNickname(String nickname) {
        Nickname = nickname;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public void setActive(int active) {
        Active = active;
    }

    public void setType(int type) {
        Type = type;
    }

    public void setCash(long cash) {
        Cash = cash;
    }

    public void setCashSafe(long cashSafe) {
        CashSafe = cashSafe;
    }

    public void setCashSilver(long cashSilver) {
        CashSilver = cashSilver;
    }

    public void setVipPoint(long vipPoint) {
        VipPoint = vipPoint;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public int getId() {
        return Id;
    }

    public String getUsername() {
        return Username;
    }

    public String getNickname() {
        return Nickname;
    }

    public String getPassword() {
        return Password;
    }

    public int getActive() {
        return Active;
    }

    public int getType() {
        return Type;
    }

    public long getCash() {
        return Cash;
    }

    public long getCashSafe() {
        return CashSafe;
    }

    public long getCashSilver() {
        return CashSilver;
    }

    public long getVipPoint() {
        return VipPoint;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }
}
