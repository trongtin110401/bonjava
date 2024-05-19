package com.vinplay.vbee.common.messages;

public class BetTXMD5Message extends BaseMessage {
    private String nickname;
    private long referenceId;
    private long betValue;
    private short betSide;
    private int userId;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(long referenceId) {
        this.referenceId = referenceId;
    }

    public long getBetValue() {
        return betValue;
    }

    public void setBetValue(long betValue) {
        this.betValue = betValue;
    }

    public short getBetSide() {
        return betSide;
    }

    public void setBetSide(short betSide) {
        this.betSide = betSide;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
