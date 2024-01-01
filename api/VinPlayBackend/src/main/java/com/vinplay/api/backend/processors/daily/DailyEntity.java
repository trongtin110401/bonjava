package com.vinplay.api.backend.processors.daily;

import com.vinplay.vbee.common.response.BaseResponseModel;

public class DailyEntity extends BaseResponseModel {
    private String username;

    private String referentCode;

    private String accessToken;

    public DailyEntity(boolean success, String errorCode) {
        super(success, errorCode);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getReferentCode() {
        return referentCode;
    }

    public void setReferentCode(String referentCode) {
        this.referentCode = referentCode;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
