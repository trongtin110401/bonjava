/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.vbee.common.models;

public class UserClientInfo {
    private String nickname;
    private String avatar;
    private long vinTotal;
    private long xuTotal;
    private int vippoint;
    private int vippointSave;
    private String createTime;
    private String ipAddress;
    private boolean certificate;
    private int luckyRotate;
    private int daiLy;
    private int mobileSecure;
    private String birthday;
    private int appSecure;
    private int id;

    public UserClientInfo(String nickname, String avatar, long vinTotal, long xuTotal, int vippoint, int vippointSave, String createTime, String ipAddress, boolean certificate, int luckyRotate, int daiLy, int mobileSecure, String birthday, int appSecure) {
        this.nickname = nickname;
        this.avatar = avatar;
        this.vinTotal = vinTotal;
        this.xuTotal = xuTotal;
        this.vippoint = vippoint;
        this.vippointSave = vippointSave;
        this.createTime = createTime;
        this.ipAddress = ipAddress;
        this.certificate = certificate;
        this.luckyRotate = luckyRotate;
        this.daiLy = daiLy;
        this.mobileSecure = mobileSecure;
        this.birthday = birthday;
        this.appSecure = appSecure;
    }

    public String getBirthday() {
        return this.birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getMobileSecure() {
        return this.mobileSecure;
    }

    public void setMobileSecure(int mobileSecure) {
        this.mobileSecure = mobileSecure;
    }

    public int getDaiLy() {
        return this.daiLy;
    }

    public void setDaiLy(int daiLy) {
        this.daiLy = daiLy;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return this.avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public long getVinTotal() {
        return this.vinTotal;
    }

    public void setVinTotal(long vinTotal) {
        this.vinTotal = vinTotal;
    }

    public long getXuTotal() {
        return this.xuTotal;
    }

    public void setXuTotal(long xuTotal) {
        this.xuTotal = xuTotal;
    }

    public int getVippoint() {
        return this.vippoint;
    }

    public void setVippoint(int vippoint) {
        this.vippoint = vippoint;
    }

    public int getVippointSave() {
        return this.vippointSave;
    }

    public void setVippointSave(int vippointSave) {
        this.vippointSave = vippointSave;
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public boolean isCertificate() {
        return this.certificate;
    }

    public void setCertificate(boolean certificate) {
        this.certificate = certificate;
    }

    public int getLuckyRotate() {
        return this.luckyRotate;
    }

    public void setLuckyRotate(int luckyRotate) {
        this.luckyRotate = luckyRotate;
    }
    public void setAppSecure(int appSecure) {
        this.appSecure = appSecure;
    }
    public int getAppSecure(){
        return this.appSecure;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

