/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.dal.service.impl.BauCuaServiceImpl
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.models.minigame.baucua.TransactionBauCua
 *  javax.servlet.http.HttpServletRequest
 */
package com.vinplay.api.processors.minigame;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;

import javax.servlet.http.HttpServletRequest;

public class GetTxMd5BankProcessor
implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        byte type = Byte.parseByte(request.getParameter("type"));
        int moneyType = 1;
        try {
            String keyHazel = type == 1 ? "txBank_md5" : "txBank2_md5";
            HazelcastInstance client = HazelcastClientFactory.getInstance();
            IMap bankMap = client.getMap(keyHazel);
            String key = keyHazel + ":" + moneyType;
            long bank = 0L;
            if (bankMap.containsKey(key)) {
                bank = (long)bankMap.get(key);
            }

            return "" + bank;
        }
        catch (Exception ex) {
            return ex.getMessage();
        }
    }
}

