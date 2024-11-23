/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.vbee.common.messages.minigame;

import com.vinplay.vbee.common.messages.BaseMessage;

public class TransactionTaiXiuDetailMessage
extends BaseMessage {
    private static final long serialVersionUID = 1L;
    public long referenceId;
    public String transactionCode;
    public int userId;
    public String username;
    public long betValue;
    public int betSide;
    public long prize;
    public long refund;
    public int inputTime;
    public int moneyType;
    public long kubetSessionId;

    public TransactionTaiXiuDetailMessage() {
    }

    public TransactionTaiXiuDetailMessage(long referenceId, String transactionCode, int userId, String username, long betValue, int betSide, long prize, long refund, int inputTime, int moneyType, long kubetSessionId) {
        this.referenceId = referenceId;
        this.transactionCode = transactionCode;
        this.userId = userId;
        this.username = username;
        this.betValue = betValue;
        this.betSide = betSide;
        this.prize = prize;
        this.refund = refund;
        this.inputTime = inputTime;
        this.moneyType = moneyType;
        this.kubetSessionId = kubetSessionId;
    }
}

