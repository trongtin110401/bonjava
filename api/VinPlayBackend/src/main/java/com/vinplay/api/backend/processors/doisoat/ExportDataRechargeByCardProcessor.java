/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.dal.service.impl.RechargeByCardServiceImpl
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.response.recharge.ExportDataRechargeByCardResponse
 *  javax.servlet.http.HttpServletRequest
 */
package com.vinplay.api.backend.processors.doisoat;

import com.vinplay.dal.service.impl.RechargeByCardServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.recharge.ExportDataRechargeByCardResponse;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

public class ExportDataRechargeByCardProcessor
implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        ExportDataRechargeByCardResponse response = new ExportDataRechargeByCardResponse(false, "1001");
        HttpServletRequest request = (HttpServletRequest)param.get();
        String timeStart = request.getParameter("ts");
        String timeEnd = request.getParameter("te");
        String provider = request.getParameter("pv");
        String amount = request.getParameter("am");
        String code = request.getParameter("co");
        RechargeByCardServiceImpl service = new RechargeByCardServiceImpl();
        List trans = service.exportDataRechargeByCard(provider, timeStart, timeEnd, amount, code);
        response.setSuccess(true);
        response.setErrorCode("0");
        response.setTransactions(trans);
        return response.toJson();
    }
}

