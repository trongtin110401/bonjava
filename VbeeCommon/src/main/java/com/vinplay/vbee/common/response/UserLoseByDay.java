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
    private boolean active;
    private String createdDate;
    private String expirationDate;
    private String activeDate;

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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getActiveDate() {
        return activeDate;
    }

    public void setActiveDate(String activeDate) {
        this.activeDate = activeDate;
    }
}

