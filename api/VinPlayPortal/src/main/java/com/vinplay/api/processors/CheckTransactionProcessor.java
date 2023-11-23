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
import org.json.JSONObject;

public class CheckTransactionProcessor
implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String)"api");

    public String execute(Param<HttpServletRequest> param) {       
        HttpServletRequest request = (HttpServletRequest)param.get();
        String tid = request.getParameter("tid");
        AgentDAO dao = new AgentDAOImpl();
        AgentTransferMoneyResponse resp = new AgentTransferMoneyResponse(404,"not found");
        LogAgentTranferMoneyResponse response = dao.searchAgentTranferMoney(tid);
        if (response != null && response.nick_name_send != null && response.nick_name_send != "")
        {
            resp.code = 0;
            resp.des_receive = response.des_receive;
            resp.des_send = response.des_send;
            resp.fee = response.fee;
            resp.message = "success";
            resp.money_receive = response.money_receive;
            resp.money_send = response.money_send;
            resp.nick_name_receive = response.nick_name_receive;
            resp.nick_name_send = response.nick_name_send;
            resp.process = response.process;
            resp.status = response.status;
            resp.top_ds = response.top_ds;
            resp.trans_id = tid;
            resp.trans_time = response.trans_time;
        }
        return resp.toJson();
    }
}