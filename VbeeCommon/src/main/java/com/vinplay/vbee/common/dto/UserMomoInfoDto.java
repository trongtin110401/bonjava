/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.vbee.common.dto;

public class UserMomoInfoDto {
    private String id;
    private String nickName;
    private String phoneName;
    private String phoneNumber;
    private String createdDate;

    public UserMomoInfoDto(String nickName, String phoneName, String phoneNumber, String createdDate) {
        this.nickName = nickName;
        this.phoneName = phoneName;
        this.phoneNumber = phoneNumber;
        this.createdDate = createdDate;
    }

    public UserMomoInfoDto() {
    }

    public String getPhoneName() {
        return phoneName;
    }

    public void setPhoneName(String phoneName) {
        this.phoneName = phoneName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

