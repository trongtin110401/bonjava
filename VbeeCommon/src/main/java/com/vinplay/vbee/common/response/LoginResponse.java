/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.vbee.common.response;

public class LoginResponse
extends BaseResponseModel {
    private String sessionKey;
    private String accessToken;
    private int dai_ly;

    public LoginResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }

    public LoginResponse(boolean success, String errorCode, String sessionKey, String accessToken) {
        super(success, errorCode);
        this.sessionKey = sessionKey;
        this.accessToken = accessToken;
    }
    
    public LoginResponse(boolean success, String errorCode, String sessionKey, String accessToken,int dai_ly) {
        super(success, errorCode);
        this.sessionKey = sessionKey;
        this.accessToken = accessToken;
        this.dai_ly = dai_ly;
    }

    public String getSessionKey() {
        return this.sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}

