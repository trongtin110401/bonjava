/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.vbee.common.response;

import java.util.List;

public class UserLoseByDayResponse extends BaseResponseModel {

    private List<UserLoseByDay> users;

    public UserLoseByDayResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }

    public List<UserLoseByDay> getUsers() {
        return users;
    }

    public void setUsers(List<UserLoseByDay> users) {
        this.users = users;
    }
}

