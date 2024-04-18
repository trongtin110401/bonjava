/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.vbee.common.response;

public class UserLoseByDay {

    private String nickname;
    private long money;
    private String chatId;
    private String code;
    private long moneyCashBack;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public long getMoney() {
        return money;
    }

    public void setMoney(long money) {
        this.money = money;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getMoneyCashBack() {
        return moneyCashBack;
    }

    public void setMoneyCashBack(long moneyCashBack) {
        this.moneyCashBack = moneyCashBack;
    }
}

