/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.vbee.common.models;

import java.util.Date;

public class OtpModel {
    private String mobile;
    private String otp;
    private Date otpTime;
    private String commandCode;
    private int count;

    public OtpModel() {
    }

    public OtpModel(String mobile, String otp, Date otpTime, String commandCode) {
        this.mobile = mobile;
        this.otp = otp;
        this.otpTime = otpTime;
        this.commandCode = commandCode;
    }
    
    public OtpModel(String mobile, String otp, Date otpTime, String commandCode,int count) {
        this.mobile = mobile;
        this.otp = otp;
        this.otpTime = otpTime;
        this.commandCode = commandCode;
        this.count = count;
    }

    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getOtp() {
        return this.otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public Date getOtpTime() {
        return this.otpTime;
    }

    public void setOtpTime(Date otpTime) {
        this.otpTime = otpTime;
    }

    public String getCommandCode() {
        return this.commandCode;
    }

    public void setCommandCode(String commandCode) {
        this.commandCode = commandCode;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}

