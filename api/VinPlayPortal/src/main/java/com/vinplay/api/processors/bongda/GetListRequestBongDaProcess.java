package com.vinplay.api.processors.bongda;

import com.vinplay.bongda.dao.UserBetBongDaDao;
import com.vinplay.bongda.dao.impl.UserBetBongDaimpl;
import com.vinplay.bongda.entities.UserBetBongDaResponse;
import com.vinplay.bongda.entities.UserRequestClientResponse;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;

public class GetListRequestBongDaProcess implements BaseProcessor<HttpServletRequest, String> {
    @Override
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = param.get();
        UserBetBongDaDao dao = new UserBetBongDaimpl();

        String idTrans = request.getParameter("idTrans");
        String session = request.getParameter("session");

        UserRequestClientResponse userBetBongDaResponse = dao.getListRequestBongDa(session, idTrans);

        return userBetBongDaResponse.toJson();
    }
}
