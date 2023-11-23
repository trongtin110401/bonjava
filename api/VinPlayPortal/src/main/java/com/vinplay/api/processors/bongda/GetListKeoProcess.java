package com.vinplay.api.processors.bongda;

import com.vinplay.bongda.dao.KeoBongDaDao;
import com.vinplay.bongda.dao.impl.KeoBongDaImpl;
import com.vinplay.bongda.dao.impl.UserBetBongDaimpl;
import com.vinplay.bongda.entities.KeoBongDaResponse;
import com.vinplay.bongda.entities.UserBetBongDaResponse;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

public class GetListKeoProcess implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String) "api");

    @Override
    public String execute(Param<HttpServletRequest> req) {
        HttpServletRequest request = req.get();
        String session = request.getParameter("session"); //dd/mm/yy
        KeoBongDaDao dao = new KeoBongDaImpl();
        KeoBongDaResponse userBetBongDaResponse = dao.getListKeoBongDaClient(session);
        if (userBetBongDaResponse == null) {
            return "1";
        }

        return userBetBongDaResponse.toJson();
    }
}
