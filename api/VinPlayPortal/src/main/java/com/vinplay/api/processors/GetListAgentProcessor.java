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
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.AgentResponse;
import com.vinplay.vbee.common.response.ResultAgentRespone;
import java.util.Collections;
import java.util.List;
import java.util.Collections;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

public class GetListAgentProcessor
implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String)"api");

    public String execute(Param<HttpServletRequest> param) {
        ResultAgentRespone response = new ResultAgentRespone(false, "1001");
        AgentServiceImpl service = new AgentServiceImpl();
        HttpServletRequest request = (HttpServletRequest)param.get();
        String cp = request.getParameter("cp");
        String type = !Objects.equals(request.getParameter("type"), null) ? request.getParameter("type") : "vn";
        logger.debug((Object)("GetListAgentProcessor:"+cp));
        try {
            if (cp != null)
            {
                if ("".equals(cp))
                    cp = "XX";
                else if (cp.equals("MAN"))
                    cp = "M";
                else if(cp.equals("R")){
                    cp = "XX";
                }
                else if(cp.equals("V")){
                    cp = "V";
                }
                else
                    cp = "XX";
                logger.debug((Object)("GetListAgentProcessor:"+cp));
                List<AgentResponse> trans = service.listAgentByClient(cp, type);
                if (trans != null && trans.size() > 0)
                    Collections.shuffle(trans); 
                response.setTransactions(trans);
                response.setSuccess(true);
                response.setErrorCode("0");
            }
            else
            {                
                cp = "XX";
                List<AgentResponse> trans = service.listAgentByClient(cp, type);
                if (trans != null && trans.size() > 0)
                    Collections.shuffle(trans); 
                response.setTransactions(trans);
                response.setSuccess(true);
                response.setErrorCode("0");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.debug((Object)e);
        }
        return response.toJson();
    }
}

