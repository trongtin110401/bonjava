/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.vbee.common.messages.statistic;

import com.vinplay.vbee.common.messages.BaseMessage;

public class LoginPortalInfoMsg
extends BaseMessage {
    private static final long serialVersionUID = 1L;
    private int userId;
    private String username;
    private String nickname;
    private String ip;
    private String agent;
    private int type;
    private String platform;

    public LoginPortalInfoMsg(int userId, String username, String nickname, String ip, String agent, int type, String platform) {
        this.userId = userId;
        this.username = username;
        this.nickname = nickname;
        this.ip = ip;
        this.agent = agent;
        this.type = type;
        this.platform = platform;
    }

    public int getUserId() {
        return this.userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getAgent() {
        return this.agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static long getSerialversionuid() {
        return 1L;
    }

    public String getPlatform() {
        return this.platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}

