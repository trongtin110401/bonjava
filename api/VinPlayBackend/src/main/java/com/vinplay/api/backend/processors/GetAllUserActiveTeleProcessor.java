package com.vinplay.api.backend.processors;

import com.vinplay.usercore.service.OtherService;
import com.vinplay.usercore.service.impl.OtherServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.UserActivePhoneResponse;

import javax.servlet.http.HttpServletRequest;

public class GetAllUserActiveTeleProcessor
        implements BaseProcessor<HttpServletRequest, String> {

    public String execute(Param<HttpServletRequest> param) {
        UserActivePhoneResponse userActivePhoneResponse = new UserActivePhoneResponse(true, "0");

        HttpServletRequest request = param.get();
        String nickname = request.getParameter("nickname");
        String phone = request.getParameter("phone");
        int pageIndex = Integer.parseInt(request.getParameter("pageIndex"));
        int pageSize = Integer.parseInt(request.getParameter("pageSize"));
        OtherService otherService = new OtherServiceImpl();
        userActivePhoneResponse = otherService.getAllUserActivePhone(nickname, phone, pageIndex, pageSize);
        return userActivePhoneResponse.toJson();

    }
}

