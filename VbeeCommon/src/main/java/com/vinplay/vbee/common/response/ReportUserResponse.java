/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.vbee.common.response;

import com.vinplay.vbee.common.models.UserAdminInfo;

import java.util.ArrayList;
import java.util.List;

public class ReportUserResponse
        extends BaseResponseModel {
    private long total;
    private long userPay;
    private long userSecurity;

    private long userPayAndSecurity;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getUserPayAndSecurity() {
        return userPayAndSecurity;
    }

    public void setUserPayAndSecurity(long userPayAndSecurity) {
        this.userPayAndSecurity = userPayAndSecurity;
    }

    public long getUserPay() {
        return userPay;
    }

    public void setUserPay(long userPay) {
        this.userPay = userPay;
    }

    public long getUserSecurity() {
        return userSecurity;
    }

    public void setUserSecurity(long userSecurity) {
        this.userSecurity = userSecurity;
    }

    public ReportUserResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }
}

