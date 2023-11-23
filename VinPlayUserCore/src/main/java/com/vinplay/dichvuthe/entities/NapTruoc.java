package com.vinplay.dichvuthe.entities;

public class NapTruoc {
    private String code;
    private String xuly;
    private String timelog;
    private long time;
    private String idelk;
    private long tien;

    public NapTruoc() {
    }


    public NapTruoc(String code, String xuly, String timelog, long time, String idelk, long tien) {
        this.code = code;
        this.xuly = xuly;
        this.timelog = timelog;
        this.time = time;
        this.idelk = idelk;
        this.tien = tien;
    }

    public long getTien() {
        return tien;
    }

    public void setTien(long tien) {
        this.tien = tien;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getXuly() {
        return xuly;
    }

    public void setXuly(String xuly) {
        this.xuly = xuly;
    }

    public String getTimelog() {
        return timelog;
    }

    public void setTimelog(String timelog) {
        this.timelog = timelog;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getIdelk() {
        return idelk;
    }

    public void setIdelk(String idelk) {
        this.idelk = idelk;
    }

    @Override
    public String toString() {
        return "NapTruoc{" +
                "code='" + code + '\'' +
                ", xuly='" + xuly + '\'' +
                ", timelog='" + timelog + '\'' +
                ", time=" + time +
                ", idelk='" + idelk + '\'' +
                ", tien=" + tien +
                '}';
    }
}
