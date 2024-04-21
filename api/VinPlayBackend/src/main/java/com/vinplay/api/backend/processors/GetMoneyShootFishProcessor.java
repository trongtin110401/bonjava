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
import com.vinplay.vbee.common.response.MoneyShootFishResponse;

import javax.servlet.http.HttpServletRequest;

public class GetMoneyShootFishProcessor implements BaseProcessor<HttpServletRequest, String> {

    public String execute(Param<HttpServletRequest> param) {
        MoneyShootFishResponse response;
        HttpServletRequest request = param.get();
        String timeStart = request.getParameter("timeStart");
        String timeEnd = request.getParameter("timeEnd");

        OtherService otherService = new OtherServiceImpl();
        response = otherService.getMoneyShootFish(timeStart, timeEnd);

        return response.toJson();
    }

}

