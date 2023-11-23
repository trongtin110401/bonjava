/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.vbee.common.response;

public class PhoneActiveResponse
extends BaseResponseModel {
    private String phone;
    private int active;

    public PhoneActiveResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

}

