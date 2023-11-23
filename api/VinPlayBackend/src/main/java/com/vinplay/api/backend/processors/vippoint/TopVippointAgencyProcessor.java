/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.usercore.service.impl.OtpServiceImpl
 *  com.vinplay.usercore.service.impl.VippointServiceImpl
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  javax.servlet.http.HttpServletRequest
 */
package com.vinplay.api.backend.processors.vippoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinplay.dal.dao.VipPointEventDao;
import com.vinplay.dal.dao.impl.VipPointEventDaoImpl;
import com.vinplay.dal.entities.agent.TopVippointResponse;
import com.vinplay.usercore.service.impl.OtpServiceImpl;
import com.vinplay.usercore.service.impl.VippointServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;

public class TopVippointAgencyProcessor
implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {        
        HttpServletRequest request = (HttpServletRequest)param.get();
        int level = Integer.parseInt(request.getParameter("l"));
        String client = request.getParameter("cl");
        int page = Integer.parseInt(request.getParameter("p"));
        int pageSize = Integer.parseInt(request.getParameter("pz"));
        int skip = (page - 1) * pageSize;
        VipPointEventDao dao = new VipPointEventDaoImpl();
        try {
            if (level == 1) {
                List<TopVippointResponse> result = dao.GetTopVippointAgency(skip, pageSize, client);
                ObjectMapper mapper = new ObjectMapper();
                return mapper.writeValueAsString((Object) result);
            }
            else
            {
                int parentId = Integer.parseInt(request.getParameter("pid"));
                List<String> nicknames = dao.GetAgentByParent(parentId);
                List<TopVippointResponse> result = dao.GetTopVippointAgencyByNN(skip, pageSize, nicknames, client);
                ObjectMapper mapper = new ObjectMapper();
                return mapper.writeValueAsString((Object) result);
            }
        } catch (JsonProcessingException | SQLException ex) {
            return null;
        }
    }
}

