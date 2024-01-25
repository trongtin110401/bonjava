/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.vbee.common.response;

import com.vinplay.vbee.common.models.cache.UserCacheModel;

public class UserOnlineDetailResponse
        extends BaseResponseModel {
    private UserCacheModel user;

    public UserOnlineDetailResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }

    public UserCacheModel getUser() {
        return user;
    }

    public void setUser(UserCacheModel user) {
        this.user = user;
    }
}

