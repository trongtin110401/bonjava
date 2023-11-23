package com.vinplay.utils;

import java.io.Serializable;

public class SlotNohuObject  implements Serializable{
    public String username;
    public byte type; // 1 là nổ hũ
    public long totalPrizes;
    public String gameName;

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public long getTotalPrizes() {
        return totalPrizes;
    }

    public void setTotalPrizes(long totalPrizes) {
        this.totalPrizes = totalPrizes;
    }

    public SlotNohuObject() {
    }

    public SlotNohuObject(String username, byte type, long totalPrizes, String gameName) {
        this.username = username;
        this.type = type;
        this.totalPrizes = totalPrizes;
        this.gameName = gameName;
    }

    public SlotNohuObject(String username, byte type, long totalPrizes) {
        this.username = username;
        this.type = type;
        this.totalPrizes = totalPrizes;
    }

}
