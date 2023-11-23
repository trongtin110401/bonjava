/*
 * Decompiled with CFR 0.144.
 */
package game.modules.minigame.entities;

import org.python.parser.ast.Str;

public class BotBauCua {
    private String nickname;
    private short timeBetting;
    private String betStr;
    private String avatar ;
    private long moneyCurrent;
    public BotBauCua(String nickname, short timeBetting, String betStr) {
        this.nickname = nickname;
        this.timeBetting = timeBetting;
        this.betStr = betStr;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public long getMoneyCurrent() {
        return moneyCurrent;
    }

    public void setMoneyCurrent(long moneyCurrent) {
        this.moneyCurrent = moneyCurrent;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setTimeBetting(short timeBetting) {
        this.timeBetting = timeBetting;
    }

    public short getTimeBetting() {
        return this.timeBetting;
    }

    public void setBetStr(String betStr) {
        this.betStr = betStr;
    }

    public String getBetStr() {
        return this.betStr;
    }
}

