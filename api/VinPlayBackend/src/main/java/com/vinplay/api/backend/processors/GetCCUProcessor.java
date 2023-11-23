/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.dal.service.impl.ServerInfoServiceImpl
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.response.CCUResponse
 *  javax.servlet.http.HttpServletRequest
 */
package com.vinplay.api.backend.processors;

import com.vinplay.dal.service.impl.ServerInfoServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.CCUResponse;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

public class GetCCUProcessor
implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        CCUResponse response = new CCUResponse(false, "1001");
        String startDate = request.getParameter("ts");
        String endDate = request.getParameter("te");
        List trans = null;
        if (!startDate.isEmpty() && !endDate.isEmpty()) {
            ServerInfoServiceImpl service = new ServerInfoServiceImpl();
            trans = service.getLogCCU(startDate, endDate);
            response.setTransactions(trans);
            response.setErrorCode("0");
            response.setSuccess(true);
            return response.toJson();
        }
        return "MISSING PARAMETTER";
    }
}

