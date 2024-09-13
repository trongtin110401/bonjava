/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.vbee.common.response;

import java.util.List;

public class UserLoseByDayResponse extends BaseResponseModel {

    private List<UserLoseByDay> users;

    private int totalRecord;
    private int pageIndex;
    private int pageSize;
    private int totalPage;

    public UserLoseByDayResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }

    public List<UserLoseByDay> getUsers() {
        return users;
    }

    public void setUsers(List<UserLoseByDay> users) {
        this.users = users;
    }

    public int getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(int totalRecord) {
        this.totalRecord = totalRecord;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }
}

