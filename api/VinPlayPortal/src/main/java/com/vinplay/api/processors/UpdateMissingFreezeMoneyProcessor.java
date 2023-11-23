/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.dal.service.impl.AgentServiceImpl
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.response.ResultAgentRespone
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.log4j.Logger
 */
package com.vinplay.api.processors;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.vinplay.dal.service.impl.AgentServiceImpl;
import com.vinplay.usercore.dao.impl.MoneyInGameDaoImpl;
import com.vinplay.usercore.dao.impl.UserDaoImpl;
import com.vinplay.usercore.service.impl.MoneyInGameServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.models.FreezeModel;
import com.vinplay.vbee.common.models.cache.UserCacheModel;
import com.vinplay.vbee.common.response.AgentResponse;
import com.vinplay.vbee.common.response.ResultAgentRespone;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

public class UpdateMissingFreezeMoneyProcessor
implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String)"api");

    public String execute(Param<HttpServletRequest> param) {
        ResultAgentRespone response = new ResultAgentRespone(false, "1001");
        AgentServiceImpl service = new AgentServiceImpl();
        HttpServletRequest request = (HttpServletRequest)param.get();
        String key = request.getParameter("k");
        try {
            if (key!= null && "gamebaiasd123".equals(key))
            {
                MoneyInGameDaoImpl dao = new MoneyInGameDaoImpl();
                List<FreezeModel> freezes = dao.getListFreezeMoneyNew();
                if (freezes != null && freezes.size() > 0)
                {
                    HazelcastInstance client = HazelcastClientFactory.getInstance();
                    IMap freezeMap = client.getMap("freeze");
                    for (FreezeModel model : freezes)
                    {
                        if (!freezeMap.containsKey((Object)model.getSessionId())) {
                            freezeMap.put((Object)model.getSessionId(), (Object)model);
                        }
                    }
                }
            }            
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.debug((Object)e);
        }
        return response.toJson();
    }
}

