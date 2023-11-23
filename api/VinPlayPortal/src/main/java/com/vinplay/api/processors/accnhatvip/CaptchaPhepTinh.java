package com.vinplay.api.processors.accnhatvip;

public class CaptchaPhepTinh {
    private String captcha_base64;
    private String sessionID;

    public CaptchaPhepTinh() {
    }

    public CaptchaPhepTinh(String captcha_base64, String sessionID) {
        this.captcha_base64 = captcha_base64;
        this.sessionID = sessionID;
    }

    public String getCaptcha_base64() {
        return captcha_base64;
    }

    public void setCaptcha_base64(String captcha_base64) {
        this.captcha_base64 = captcha_base64;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    @Override
    public String toString() {
        return "CaptchaPhepTinh{" +
                "captcha_base64='" + captcha_base64 + '\'' +
                ", sessionID='" + sessionID + '\'' +
                '}';
    }
}
