/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.dal.service.impl.RechargeBySmsPlus9029ServiceImpl
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.response.recharge.DoiSoatRechargeBySmsPlus9029Response
 *  javax.servlet.http.HttpServletRequest
 */
package com.vinplay.api.backend.processors.doisoat;

import com.vinplay.dal.service.impl.RechargeBySmsPlus9029ServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.recharge.DoiSoatRechargeBySmsPlus9029Response;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

public class DoiSoatSmsPlus9029Processor
implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        DoiSoatRechargeBySmsPlus9029Response response = new DoiSoatRechargeBySmsPlus9029Response(false, "1001");
        HttpServletRequest request = (HttpServletRequest)param.get();
        String timeStart = request.getParameter("ts");
        String timeEnd = request.getParameter("te");
        if (timeStart == null || timeEnd == null || timeStart.isEmpty() || timeEnd.isEmpty()) {
            return "MISSING INPUT PARAMETER";
        }
        RechargeBySmsPlus9029ServiceImpl service = new RechargeBySmsPlus9029ServiceImpl();
        List moneyResponse = service.moneyTotalRechargeBySmsPlus9029(timeStart, timeEnd, String.valueOf(0));
        response.setErrorCode("0");
        response.setSuccess(true);
        response.setMoneyReponse(moneyResponse);
        return response.toJson();
    }
}

