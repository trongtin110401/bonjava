/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.hazelcast.core.IMap
 *  com.vinplay.vbee.common.messages.gamebai.LogNoHuGameBaiMessage
 *  com.vinplay.vbee.common.models.cache.TransactionList
 *  com.vinplay.vbee.common.response.LogMoneyUserResponse
 *  com.vinplay.vbee.common.response.LogUserMoneyResponse
 */
package com.vinplay.dal.service;

import com.hazelcast.core.IMap;
import com.vinplay.vbee.common.messages.gamebai.LogNoHuGameBaiMessage;
import com.vinplay.vbee.common.models.cache.TransactionList;
import com.vinplay.vbee.common.response.LogMoneyUserResponse;
import com.vinplay.vbee.common.response.LogUserMoneyResponse;
import java.util.List;

public interface LogMoneyUserService {
    public List<LogUserMoneyResponse> searchLogMoneyUser(String var1, String var2, String var3, String var4, String var5, String var6, String var7, int var8, int var9, int var10);
    public List<LogUserMoneyResponse> searchLogMoneyUser(String nickName, String serviceName, String actionName, String timeStart,
                                                         String timeEnd, int page,  int totalRecord);

    public int countsearchLogMoneyUser(String var1, String var2, String var3, String var4, String var5, String var6, int var7);

    public List<LogMoneyUserResponse> getHistoryTransactionLogMoney(String var1, int var2, int var3);

    public int countHistoryTransactionLogMoney(String var1, int var2);

    public List<LogNoHuGameBaiMessage> getNoHuGameBaiHistory(int var1, String var2);

    public int countNoHuGameBaiHistory();

    public List<LogUserMoneyResponse> searchLogMoneyTranferUser(String var1, String var2, String var3, String var4, int var5);

    public boolean UpdateProcessLogChuyenTienDaiLy(String var1, String var2, String var3, String var4);

    public List<LogMoneyUserResponse> pushHistoryTransactionDBToCache(IMap<String, TransactionList> var1, String var2, int var3);
}

