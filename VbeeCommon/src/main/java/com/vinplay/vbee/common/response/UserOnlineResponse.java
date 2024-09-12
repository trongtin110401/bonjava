/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.vbee.common.response;

import java.util.List;

public class UserOnlineResponse
        extends BaseResponseModel {

    private List<UserCCUResponse> users;

    private int totalRecord;
    private int totalPage;
    private int pageIndex;
    private int pageSize;

    public UserOnlineResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }

    public List<UserCCUResponse> getUsers() {
        return users;
    }

    public void setUsers(List<UserCCUResponse> users) {
        this.users = users;
    }

    public int getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(int totalRecord) {
        this.totalRecord = totalRecord;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
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
}

