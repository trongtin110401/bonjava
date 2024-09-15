/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.vbee.common.messages;

public class UserBackCodeMessage extends BaseMessage {

    private static final long serialVersionUID = 1L;

    public String nickname;
    public long amount;

    // 1: win, 0: lose
    public int backType;


    public UserBackCodeMessage() {
    }



    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public int getBackType() {
        return backType;
    }

    public void setBackType(int backType) {
        this.backType = backType;
    }
}

