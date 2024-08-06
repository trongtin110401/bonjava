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

import com.vinplay.vbee.common.response.EventResponse;
import com.vinplay.usercore.service.OtherService;
import com.vinplay.usercore.service.impl.OtherServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;

public class CreateEventProcessor implements BaseProcessor<HttpServletRequest, String> {

    public String execute(Param<HttpServletRequest> param) {
        OtherService service = new OtherServiceImpl();
        EventResponse eventResponse = new EventResponse(false, "1001");
        HttpServletRequest request = param.get();
        String timeStart = request.getParameter("time   Start");
        String timeEnd = request.getParameter("timeEnd");
        String eventName = request.getParameter("eventName");
        int rate = Integer.parseInt(request.getParameter("rate"));
        if (service.checkIfHaveAnyEventActive()) {
            eventResponse.setErrorCode("Đang có một event diễn ra");
            return eventResponse.toJson();
        }
        eventResponse.setEventName(eventName);
        eventResponse.setTimeStart(timeStart);
        eventResponse.setTimeEnd(timeEnd);
        eventResponse.setRate(rate);
        eventResponse.setStatus(true);
        boolean isSuccess = service.createEvent(eventResponse);
        if (!isSuccess) {
            return eventResponse.toJson();
        }
        eventResponse.setSuccess(true);
        eventResponse.setErrorCode("200");
        return eventResponse.toJson();
    }
}

