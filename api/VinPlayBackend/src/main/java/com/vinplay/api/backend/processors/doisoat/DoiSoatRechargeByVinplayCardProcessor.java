/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.dal.service.impl.RechargeByVinCardServiceImpl
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.response.recharge.DoiSoatRechargeByVinplayCardResponse
 *  javax.servlet.http.HttpServletRequest
 */
package com.vinplay.api.backend.processors.doisoat;

import com.vinplay.dal.service.impl.RechargeByVinCardServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.recharge.DoiSoatRechargeByVinplayCardResponse;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

public class DoiSoatRechargeByVinplayCardProcessor
implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        DoiSoatRechargeByVinplayCardResponse response = new DoiSoatRechargeByVinplayCardResponse(false, "1001");
        HttpServletRequest request = (HttpServletRequest)param.get();
        String timeStart = request.getParameter("ts");
        String timeEnd = request.getParameter("te");
        if (timeStart == null || timeEnd == null || timeStart.isEmpty() || timeEnd.isEmpty()) {
            return "MISSING INPUT PARAMETER";
        }
        RechargeByVinCardServiceImpl service = new RechargeByVinCardServiceImpl();
        List moneyResponse = service.moneyTotalRechargeByVinplayCard(timeStart, timeEnd, String.valueOf(0));
        response.setErrorCode("0");
        response.setSuccess(true);
        response.setMoneyReponse(moneyResponse);
        return response.toJson();
    }
}

