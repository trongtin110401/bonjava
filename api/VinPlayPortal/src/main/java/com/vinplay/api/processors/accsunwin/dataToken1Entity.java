package com.vinplay.api.processors.accsunwin;

public class dataToken1Entity {
    private String signature;
    private long expireIn;
    private String accessToken;
    private String message;
    private String refreshToken;
    private infoToken1Entity info;

    public dataToken1Entity() {
    }

    public dataToken1Entity(String signature, long expireIn, String accessToken, String message, String refreshToken, infoToken1Entity info) {
        this.signature = signature;
        this.expireIn = expireIn;
        this.accessToken = accessToken;
        this.message = message;
        this.refreshToken = refreshToken;
        this.info = info;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public long getExpireIn() {
        return expireIn;
    }

    public void setExpireIn(long expireIn) {
        this.expireIn = expireIn;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public infoToken1Entity getInfo() {
        return info;
    }

    public void setInfo(infoToken1Entity info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return "dataToken1Entity{" +
                "signature='" + signature + '\'' +
                ", expireIn=" + expireIn +
                ", accessToken='" + accessToken + '\'' +
                ", message='" + message + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", info=" + info +
                '}';
    }
}
