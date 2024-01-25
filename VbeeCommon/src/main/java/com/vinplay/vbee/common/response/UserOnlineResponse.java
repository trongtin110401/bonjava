/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.vbee.common.response;

import java.util.List;

public class UserOnlineResponse
        extends BaseResponseModel {
    private List<String> users;

    public UserOnlineResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }
}

