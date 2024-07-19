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
import com.vinplay.vbee.common.response.UserLoseByDayResponse;

import javax.servlet.http.HttpServletRequest;

public class GetHistoryListUserLoseByDayProcessor implements BaseProcessor<HttpServletRequest, String> {

    public String execute(Param<HttpServletRequest> param) {
        UserLoseByDayResponse userCodeResponse = new UserLoseByDayResponse(true, "200");
        HttpServletRequest request = param.get();
        int pageIndex = Integer.parseInt(request.getParameter("pageIndex"));
        int pageSize = Integer.parseInt(request.getParameter("pageSize"));
        String timeStart = request.getParameter("timeStart");
        String timeEnd = request.getParameter("timeEnd");
        String nickname = request.getParameter("nickname");
        String code = request.getParameter("code");
        String type = request.getParameter("type");

        if (pageIndex < 0 || pageSize <= 0) {
            return userCodeResponse.toJson();
        }

        OtherService otherService = new OtherServiceImpl();
        userCodeResponse = otherService.getListUserTeleCashBack(pageIndex, pageSize, timeStart, timeEnd, nickname, code, type);
        return userCodeResponse.toJson();
    }
}

