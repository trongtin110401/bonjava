package com.vinplay.api.backend.processors;

import com.vinplay.usercore.service.impl.OtherServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;

public class GetLinkSocialProcessor
        implements BaseProcessor<HttpServletRequest, String> {

    public String execute(Param<HttpServletRequest> param) {
        OtherServiceImpl otherService = new OtherServiceImpl();
        return otherService.getLinkSocial().toJson();
    }
}

