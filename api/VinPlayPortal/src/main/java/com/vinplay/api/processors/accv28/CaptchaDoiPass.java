package com.vinplay.api.processors.accv28;

public class CaptchaDoiPass {
    private String captcha_base64;
    private String sessionID;

    public CaptchaDoiPass() {
    }

    public CaptchaDoiPass(String captcha_base64, String sessionID) {
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
        return "CaptchaDoiPass{" +
                "captcha_base64='" + captcha_base64 + '\'' +
                ", sessionID='" + sessionID + '\'' +
                '}';
    }
}
