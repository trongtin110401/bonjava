
package com.vinplay.api.backend.processors.money;

import com.vinplay.lognaprut.entities.ReportAdminTransferMoneyResponse;
import com.vinplay.lognaprut.HistoryTransDao;
import com.vinplay.lognaprut.impl.HistoryTransDaoImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;

public class AdminTransferMoneyProcessor
        implements BaseProcessor<HttpServletRequest, String> {

    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = param.get();
        String timeStart = request.getParameter("ts");
        String timeEnd = request.getParameter("te");
        HistoryTransDao historyTransDao = new HistoryTransDaoImpl();
        ReportAdminTransferMoneyResponse response = historyTransDao.getTotalAdminTransferByDay(timeStart, timeEnd);
        return response.toJson();
    }
}

