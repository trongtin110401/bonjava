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

import com.vinplay.dal.service.impl.AgentServiceImpl;
import com.vinplay.usercore.dao.impl.UserDaoImpl;
import com.vinplay.usercore.service.impl.MoneyInGameServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.models.cache.UserCacheModel;
import com.vinplay.vbee.common.response.AgentResponse;
import com.vinplay.vbee.common.response.ResultAgentRespone;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

public class FixFreezeMoneyProcessor
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
                UserDaoImpl userDaoImpl = new UserDaoImpl();
                List<UserCacheModel> users = userDaoImpl.GetNickNameFreeze();
                if (users != null && users.size() > 0)
                {
                    for (UserCacheModel user : users)
                    {
                        // unfreeze money
                        MoneyInGameServiceImpl migsi = new MoneyInGameServiceImpl();                                                
                        migsi.restoreFreeze(user.getNickname(), "HamCaMap", "*", "*");                        
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

