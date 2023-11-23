/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.dal.entities.taixiu;


import java.util.Date;

public class NohuTXDetail {
    public long phien;
    public String time;
    public String result;
    public long money;
    public String username;
    public String userMoneyHu;
    public long totalUser;

    public NohuTXDetail(long phien, String time, String result, long money, String username, String userMoneyHu, long totalUser) {
        this.phien = phien;
        this.time = time;
        this.result = result;
        this.money = money;
        this.username = username;
        this.userMoneyHu = userMoneyHu;
        this.totalUser = totalUser;
    }

    public NohuTXDetail() {
    }

    @Override
    public String toString() {
        return "NohuTXDetail{" +
                "phien=" + phien +
                ", time=" + time +
                ", result='" + result + '\'' +
                ", money=" + money +
                ", userName='" + username + '\'' +
                ", userMoneyHu=" + userMoneyHu +
                ", totalUser=" + totalUser +
                '}';
    }
}

