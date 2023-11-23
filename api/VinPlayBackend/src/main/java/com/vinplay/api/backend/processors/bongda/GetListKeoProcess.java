package com.vinplay.api.backend.processors.bongda;

import com.vinplay.bongda.dao.KeoBongDaDao;
import com.vinplay.bongda.dao.impl.KeoBongDaImpl;
import com.vinplay.bongda.entities.KeoBongDaResponse;
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
        String start = request.getParameter("start"); //dd/mm/yy
        String end = request.getParameter("end"); //dd/mm/yy
        String page = request.getParameter("p"); //dd/mm/yy
        int mpage = (page == null || page.isEmpty()) ? 1 : Integer.parseInt(page);
        KeoBongDaDao dao = new KeoBongDaImpl();
        KeoBongDaResponse userBetBongDaResponse = dao.getListKeoBongDa(session, mpage, 50, start, end);
        if (userBetBongDaResponse == null) {
            return "1";
        }

        return userBetBongDaResponse.toJson();
    }
}
