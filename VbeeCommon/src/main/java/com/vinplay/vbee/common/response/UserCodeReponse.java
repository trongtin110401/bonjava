/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.vbee.common.response;

import java.util.Map;

public class UserCodeReponse extends BaseResponseModel {
    Map<String, Long> users;

    public UserCodeReponse(boolean success, String errorCode) {
        super(success, errorCode);
    }

    public Map<String, Long> getUsers() {
        return users;
    }

    public void setUsers(Map<String, Long> users) {
        this.users = users;
    }
}

