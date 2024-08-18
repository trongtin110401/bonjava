package com.vinplay.api.processors;

import com.vinplay.usercore.service.OtherService;
import com.vinplay.usercore.service.impl.OtherServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.dto.UserBankInfoDto;
import com.vinplay.vbee.common.response.ListEventResponse;
import com.vinplay.vbee.common.response.UserBankInfoResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class GetAllEventProcessor
        implements BaseProcessor<HttpServletRequest, String> {

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

