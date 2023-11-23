package com.vinplay.api.backend.processors.bongda;

import com.vinplay.bongda.dao.UserBetBongDaDao;
import com.vinplay.bongda.dao.impl.UserBetBongDaimpl;
import com.vinplay.bongda.entities.UserBetBongDaResponse;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;

public class GetListRequestBongDaProcess implements BaseProcessor<HttpServletRequest, String> {
    @Override
    public String execute(Param<HttpServletRequest> param) {
        UserBetBongDaDao dao = new UserBetBongDaimpl();
        HttpServletRequest request = param.get();
        String session = request.getParameter("session");
        String startTime = request.getParameter("ts");
        String endTime = request.getParameter("te");
        String pageStr = request.getParameter("p");
        int page = (pageStr == null || pageStr.isEmpty()) ? 1 : Integer.parseInt(pageStr);

        UserBetBongDaResponse response = dao.getListUserBetBongDa(session, page, 1000, startTime, endTime);
        return response.toJson();
    }
}
