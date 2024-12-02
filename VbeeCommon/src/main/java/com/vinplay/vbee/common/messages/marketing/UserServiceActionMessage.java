package com.vinplay.vbee.common.messages.marketing;

import com.vinplay.vbee.common.messages.BaseMessage;

public class UserServiceActionMessage extends BaseMessage {

    private String nickname;
    private String action;
    private long actionValue;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public long getActionValue() {
        return actionValue;
    }

    public void setActionValue(long actionValue) {
        this.actionValue = actionValue;
    }
}
