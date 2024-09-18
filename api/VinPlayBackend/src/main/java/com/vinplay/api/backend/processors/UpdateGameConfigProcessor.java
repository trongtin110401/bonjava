/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.usercore.service.impl.GameConfigServiceImpl
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.response.BaseResponseModel
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.log4j.Logger
 */
package com.vinplay.api.backend.processors;

import com.google.gson.Gson;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.vinplay.usercore.dao.impl.GameConfigDaoImpl;
import com.vinplay.usercore.service.impl.GameConfigServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.response.BaseResponseModel;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class UpdateGameConfigProcessor
implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String)"backend");

    public String execute(Param<HttpServletRequest> param) {
        BaseResponseModel response = new BaseResponseModel(false, "1001");
        HttpServletRequest request = (HttpServletRequest)param.get();
        String value = request.getParameter("gv");
        String id = request.getParameter("gid");
        String version = request.getParameter("v");
        String platform = request.getParameter("pf");
        if (!value.isEmpty() && !id.isEmpty() & !version.isEmpty()) {
            try {
                GameConfigServiceImpl service = new GameConfigServiceImpl();
                boolean result = service.updateGameConfig(id, value, version, platform);
                if (result) {
                    GameConfigDaoImpl dao = new GameConfigDaoImpl();
                    HazelcastInstance instance = HazelcastClientFactory.getInstance();
                    String billing = dao.getGameCommon("billing");
                    IMap map = instance.getMap("cacheConfig");
                    map.put("BILLING", billing);
                    response.setErrorCode("0");
                    response.setSuccess(true);
                } else {
                    response.setErrorCode("10001");
                }
            }
            catch (Exception e) {
                logger.debug((Object)e);
            }
            return response.toJson();
        }
        return "MISSING PARAMETTER";
    }
}

