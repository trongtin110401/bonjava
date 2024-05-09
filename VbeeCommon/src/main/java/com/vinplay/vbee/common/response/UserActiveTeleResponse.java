/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.vbee.common.response;

import java.util.ArrayList;
import java.util.List;

public class UserActiveTeleResponse extends BaseResponseModel {

    public UserActiveTeleResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }

    int totalPage;

    List<UserTele> users = new ArrayList<>();

    public List<UserTele> getUsers() {
        return users;
    }

    public void setUsers(List<UserTele> users) {
        this.users = users;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }
}

