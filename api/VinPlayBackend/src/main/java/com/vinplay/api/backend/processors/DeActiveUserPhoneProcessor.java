package com.vinplay.api.backend.processors;

import com.vinplay.usercore.service.OtherService;
import com.vinplay.usercore.service.impl.OtherServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;

public class DeActiveUserPhoneProcessor
        implements BaseProcessor<HttpServletRequest, String> {

    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = param.get();
        String nickname = request.getParameter("nickname");
        OtherService otherService = new OtherServiceImpl();
        otherService.deactivateUserPhone(nickname);
        return "ok";

    }
}

