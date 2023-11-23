/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.core.IMap
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.hazelcast.HazelcastClientFactory
 *  com.vinplay.vbee.common.models.cache.SlotFreeDaily
 *  javax.servlet.http.HttpServletRequest
 */
package com.vinplay.api.backend.processors.ad;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.models.cache.SlotFreeDaily;
import com.vinplay.vbee.common.response.BaseResponseModel;

import javax.servlet.http.HttpServletRequest;

public class CongLuotQuaySlotFreeProcessor
implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        String[] arrSoLuot;
        String listNickname = ((HttpServletRequest)param.get()).getParameter("nn");
        String gameName = ((HttpServletRequest)param.get()).getParameter("gn");
        String listSoLuot = ((HttpServletRequest)param.get()).getParameter("va");
        String[] arrNickName = listNickname.split(",");
        BaseResponseModel rp=new BaseResponseModel(false,"30");
        if (arrNickName.length == (arrSoLuot = listSoLuot.split(",")).length) {
            HazelcastInstance client = HazelcastClientFactory.getInstance();
            IMap slotMap = client.getMap("cacheSlotFree");
            for (int i = 0; i < arrNickName.length; ++i) {
                SlotFreeDaily slotModel;
                String nickname = arrNickName[i];
                String key = String.valueOf(nickname) + "-" + gameName + "-" + 100;
                int slotFree = Integer.parseInt(arrSoLuot[i]);
                if (slotMap.containsKey((Object)key)) {
                    slotModel = (SlotFreeDaily)slotMap.get((Object)key);
                    slotModel.setRotateFree(slotFree);
                    slotMap.put((Object)key, (Object)slotModel);
                    continue;
                }
                slotModel = new SlotFreeDaily(slotFree, 2000L);
                slotMap.put((Object)key, (Object)slotModel);
            }
            rp.setSuccess(true);
            rp.setErrorCode("0");
            return rp.toJson();
        }
        return "S\u1ed1 ng\u01b0\u1eddi v\u00e0 s\u1ed1 luojt kh\u00f4ng tr\u00f9ng kh\u1edbp";
    }
}

