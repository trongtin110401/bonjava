package com.vinplay.common.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;

public class NotificationAdminObj implements Serializable {
    boolean napOnePay;
    boolean napBank;
    boolean napMomo;
    boolean napCardPhone;
    boolean rutBank;
    boolean rutCardPhone;
    boolean onePayOtp;
    boolean rutMomo;

    public NotificationAdminObj(boolean napOnePay, boolean napBank, boolean napMomo, boolean napCardPhone, boolean rutBank, boolean rutCardPhone, boolean onePayOtp ) {
        this.napOnePay = napOnePay;
        this.napBank = napBank;
        this.napMomo = napMomo;
        this.napCardPhone = napCardPhone;
        this.rutBank = rutBank;
        this.rutCardPhone = rutCardPhone;
        this.onePayOtp = onePayOtp;
    }

    public NotificationAdminObj() {
    }

    public boolean isNapOnePay() {
        return napOnePay;
    }

    public void setNapOnePay(boolean napOnePay) {
        this.napOnePay = napOnePay;
    }

    public boolean isNapBank() {
        return napBank;
    }

    public void setNapBank(boolean napBank) {
        this.napBank = napBank;
    }

    public boolean isNapMomo() {
        return napMomo;
    }

    public void setNapMomo(boolean napMomo) {
        this.napMomo = napMomo;
    }

    public boolean isNapCardPhone() {
        return napCardPhone;
    }

    public void setNapCardPhone(boolean napCardPhone) {
        this.napCardPhone = napCardPhone;
    }

    public boolean isRutBank() {
        return rutBank;
    }

    public void setRutBank(boolean rutBank) {
        this.rutBank = rutBank;
    }

    public boolean isRutCardPhone() {
        return rutCardPhone;
    }

    public void setRutCardPhone(boolean rutCardPhone) {
        this.rutCardPhone = rutCardPhone;
    }

    public boolean isOnePayOtp() {
        return onePayOtp;
    }

    public void setOnePayOtp(boolean onePayOtp) {
        this.onePayOtp = onePayOtp;
    }

    public boolean isRutMomo() {
        return rutMomo;
    }

    public void setRutMomo(boolean rutMomo) {
        this.rutMomo = rutMomo;
    }

    @Override
    public String toString() {
        return "NotificationAdminObj{" +
                "napOnePay=" + napOnePay +
                ", napBank=" + napBank +
                ", napMomo=" + napMomo +
                ", napCardPhone=" + napCardPhone +
                ", rutBank=" + rutBank +
                ", rutCardPhone=" + rutCardPhone +
                ", onePayOtp=" + onePayOtp +
                ", rutMomo=" + rutMomo +
                '}';
    }

    public String toJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString((Object) this);
        } catch (JsonProcessingException mapper) {
            return "{\"success\":false,\"errorCode\":\"1001\"}";
        }
    }
}
