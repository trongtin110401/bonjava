/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.vbee.common.response;

import java.util.ArrayList;
import java.util.List;

public class UserActivePhoneResponse extends BaseResponseModel {

    public UserActivePhoneResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }

    int totalPage;

    List<UserPhone> users = new ArrayList<>();

    public List<UserPhone> getUsers() {
        return users;
    }

    public void setUsers(List<UserPhone> users) {
        this.users = users;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }
}

