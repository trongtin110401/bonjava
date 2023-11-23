/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.vbee.common.response.GetUserInfoResponse
 *  com.vinplay.vbee.common.response.UserInfoResponse
 */
package com.vinplay.usercore.dao;

import com.vinplay.usercore.entities.ExportUser;
import com.vinplay.vbee.common.response.GetUserInfoResponse;
import com.vinplay.vbee.common.response.UserInfoResponse;
import java.sql.SQLException;
import java.util.List;

public interface UserInfoDao {
    public List<UserInfoResponse> searchUserInfo(String var1, String var2, String var3, String var4, String var5, int var6);

    public int countsearchUserInfo(String var1, String var2, String var3, String var4, String var5);

    public GetUserInfoResponse listGetNickName(String var1) throws SQLException;
    
    public List<ExportUser> GetExportUser(String startDate, String endDate) throws SQLException;
}

