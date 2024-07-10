package com.vinplay.vbee.common.models.cache;

import java.io.Serializable;

public class KickUserSignal implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int DUPLICATE_LOGIN = 1;
    public static final int BLOCK_USER = 2;

    private String nickname;
    private int kickType;

    public KickUserSignal() {
    }

    public KickUserSignal(String nickname, int kickType) {
        this.nickname = nickname;
        this.kickType = kickType;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getKickType() {
        return kickType;
    }

    public void setKickType(int kickType) {
        this.kickType = kickType;
    }
}
