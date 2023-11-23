/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.dal.service.impl.RechargeBySms8x98ServiceImpl
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.response.recharge.DoiSoatRechargeBySms8x98Response
 *  javax.servlet.http.HttpServletRequest
 */
package com.vinplay.api.backend.processors.doisoat;

import com.vinplay.dal.service.impl.RechargeBySms8x98ServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.recharge.DoiSoatRechargeBySms8x98Response;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

public class DoiSoatSms8x98Processor
implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        DoiSoatRechargeBySms8x98Response response = new DoiSoatRechargeBySms8x98Response(false, "1001");
        HttpServletRequest request = (HttpServletRequest)param.get();
        String timeStart = request.getParameter("ts");
        String timeEnd = request.getParameter("te");
        if (timeStart == null || timeEnd == null || timeStart.isEmpty() || timeEnd.isEmpty()) {
            return "MISSING INPUT PARAMETER";
        }
        RechargeBySms8x98ServiceImpl service = new RechargeBySms8x98ServiceImpl();
        List moneyResponse = service.moneyTotalRechargeBySms8x98(timeStart, timeEnd, String.valueOf(0));
        response.setErrorCode("0");
        response.setSuccess(true);
        response.setMoneyReponse(moneyResponse);
        return response.toJson();
    }
}

