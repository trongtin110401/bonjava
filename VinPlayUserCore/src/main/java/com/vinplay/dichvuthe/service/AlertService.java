/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.dichvuthe.service;

import java.util.List;

public interface AlertService {
    public boolean sendSMS2List(List<String> var1, String var2, boolean var3);
    
    public boolean SendVoiceOTPESMS(String phone, String otp);

    public boolean sendSMS2One(String var1, String var2, boolean var3);

    public boolean sendSMS2User(String var1, String var2);

    public boolean sendEmail(String var1, String var2, List<String> var3);

    public boolean alert2List(List<String> var1, String var2, boolean var3);

    public boolean alert2One(String var1, String var2, boolean var3);

    public boolean SendSMSEsms(String var1, String var2);
    
    public boolean SendSMSRutCuoc(String phone, String messge);

    public boolean SendSMSAirpay(String phone, String messge);
    public boolean SendSmsBrandName(String phone, String message);
}

