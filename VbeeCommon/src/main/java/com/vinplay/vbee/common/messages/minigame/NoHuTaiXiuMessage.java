/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.vbee.common.messages.minigame;

import com.vinplay.vbee.common.messages.BaseMessage;

public class NoHuTaiXiuMessage
extends BaseMessage {
    private static final long serialVersionUID = 1L;
    public long phien;
    public String result;
    public long money;
    public String username;
    public String userMoneyHu;
    public long totalUser;

    public NoHuTaiXiuMessage() {
    }

    public NoHuTaiXiuMessage(long phien, String result, long money, String username, String userMoneyHu, long totalUser) {
        this.phien = phien;
        this.result = result;
        this.money = money;
        this.username = username;
        this.userMoneyHu = userMoneyHu;
        this.totalUser = totalUser;
    }
}

