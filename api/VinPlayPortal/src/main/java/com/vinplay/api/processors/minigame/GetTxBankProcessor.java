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

import com.vinplay.api.processors.minigame.response.LSGDBauCuaResponse;
import com.vinplay.dal.service.impl.BauCuaServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class GetTxBankProcessor
implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        byte type = Byte.parseByte(request.getParameter("type"));
        int moneyType = 1;
        try {
            String keyHazel = type == 1 ? "txBank" : "txBank2";
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

