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

import com.vinplay.dal.service.CacheService;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.BaseResponseModel;

import javax.servlet.http.HttpServletRequest;

public class UpdateSlotFeeProcessor
        implements BaseProcessor<HttpServletRequest, String> {

    private final String PERCENT_FEE = "_PERCENT_FEE";

    public String execute(Param<HttpServletRequest> param) {
        BaseResponseModel response = new BaseResponseModel(true, "200");
        HttpServletRequest request = param.get();

        int fee = Integer.parseInt(request.getParameter("fee"));
        String gameName = request.getParameter("gameName");

        if (fee < 0) {
            return response.toJson();
        }
        CacheService cacheService = new CacheServiceImpl();
        cacheService.setValue(gameName + PERCENT_FEE, fee);

        return response.toJson();

    }
}

