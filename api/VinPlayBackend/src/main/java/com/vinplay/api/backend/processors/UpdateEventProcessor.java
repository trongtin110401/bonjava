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
import com.vinplay.vbee.common.response.EventResponse;
import com.vinplay.vbee.common.response.ListEventResponse;

import javax.servlet.http.HttpServletRequest;

public class UpdateEventProcessor implements BaseProcessor<HttpServletRequest, String> {

    public String execute(Param<HttpServletRequest> param) {
        OtherService service = new OtherServiceImpl();
        HttpServletRequest request = param.get();
        ListEventResponse eventResponse = new ListEventResponse(false, "1001");
        EventResponse response = service.getCurrentEvent();
        String timeStart = request.getParameter("timeStart");
        String timeEnd = request.getParameter("timeEnd");
        String eventName = request.getParameter("eventName");
        int rate = Integer.parseInt(request.getParameter("rate"));
        long id = Long.parseLong(request.getParameter("id"));
        if (response != null) {
            if (!response.getId().equals(String.valueOf(id))) {
                eventResponse.setErrorCode("Đang có một sự kiện diễn ra");
                return eventResponse.toJson();
            }
        }
        boolean status = Boolean.parseBoolean(request.getParameter("status"));
        service.updateEvent(id, timeStart, timeEnd, eventName, rate, status);
        eventResponse = service.getAllEvent(null, null, null, null, status);
        return eventResponse.toJson();
    }
}

