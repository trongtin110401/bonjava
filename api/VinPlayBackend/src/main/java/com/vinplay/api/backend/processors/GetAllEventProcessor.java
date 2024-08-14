/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.vinplay.usercore.service.impl.GiftCodeServiceImpl
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.response.GiftCodeSearchResponse
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.log4j.Logger
 */
package com.vinplay.api.backend.processors;

import com.vinplay.usercore.service.OtherService;
import com.vinplay.usercore.service.impl.OtherServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.ListEventResponse;

import javax.servlet.http.HttpServletRequest;

public class GetAllEventProcessor implements BaseProcessor<HttpServletRequest, String> {

    public String execute(Param<HttpServletRequest> param) {
        OtherService service = new OtherServiceImpl();
        HttpServletRequest request = param.get();
        String timeStart = request.getParameter("timeStart");
        String timeEnd = request.getParameter("timeEnd");
        String eventName = request.getParameter("eventName");
        String rate = request.getParameter("rate");
        Boolean status = null;
        try {
            if (!request.getParameter("status").isEmpty()) {
                status = Boolean.parseBoolean(request.getParameter("status"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ListEventResponse eventResponse = service.getAllEvent(timeStart, timeEnd, eventName, rate, status);
        return eventResponse.toJson();
    }
}

