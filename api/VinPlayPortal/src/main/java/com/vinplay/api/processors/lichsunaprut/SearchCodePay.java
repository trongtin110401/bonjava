package com.vinplay.api.processors.lichsunaprut;

import com.google.gson.Gson;
import com.vinplay.lognaprut.HistoryTransDao;
import com.vinplay.lognaprut.impl.HistoryTransDaoImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

public class SearchCodePay implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String) "api");

    @Override
    public String execute(Param<HttpServletRequest> param) {
        try {
            HttpServletRequest request = (HttpServletRequest) param.get();
            String c = request.getParameter("code");
            HistoryTransDao historyTransDao = new HistoryTransDaoImpl();
            Gson gson = new Gson();
            return gson.toJson(historyTransDao.findCodepayLike(c));
        } catch (Exception e) {
            return e.getMessage();
        }


    }
}
