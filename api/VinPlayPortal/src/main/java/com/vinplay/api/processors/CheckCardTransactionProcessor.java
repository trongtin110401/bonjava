/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.dal.service.impl.LogMoneyUserServiceImpl
 *  com.vinplay.usercore.service.impl.UserServiceImpl
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.response.LogMoneyUserResponse
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.log4j.Logger
 */
package com.vinplay.api.processors;

import com.vinplay.api.entities.AgentTransferMoneyResponse;
import com.vinplay.api.processors.response.LogMoneyResponse;
import com.vinplay.dal.dao.AgentDAO;
import com.vinplay.dal.dao.impl.AgentDAOImpl;
import com.vinplay.dal.service.impl.LogMoneyUserServiceImpl;
import com.vinplay.dichvuthe.dao.impl.RechargeDaoImpl;
import com.vinplay.usercore.dao.GiftCodeDAO;
import com.vinplay.usercore.dao.impl.GiftCodeDAOImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.LogAgentTranferMoneyResponse;
import com.vinplay.vbee.common.response.LogMoneyUserResponse;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.json.JSONObject;

public class CheckCardTransactionProcessor
implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String)"api");

    public String execute(Param<HttpServletRequest> param) {       
        HttpServletRequest request = (HttpServletRequest)param.get();
        String tid = request.getParameter("tid");                
        RechargeDaoImpl dao = new RechargeDaoImpl();
        Document trans = dao.getRechargeByGachthe(tid);
        if (trans != null)
        {
            if ("0".equals(trans.getInteger("code")))
            {
                return "{\"code\":0,\"message\":\"success\"}";
            }
            else
            {
                return "{\"code\":"+ trans.getInteger("code") +",\"message\":\"failed\"}";
            }
        }
        else
        {
            return "{\"code\":404,\"message\":\"error\"}";
        }
    }
}