/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.vinplay.usercore.service.impl.UserForAdminServiceImpl
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.response.ResultUserReponse
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.log4j.Logger
 */
package com.vinplay.api.backend.processors;

import com.vinplay.dal.service.impl.MiniGameServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.FundInfoResponse;

import javax.servlet.http.HttpServletRequest;

public class GetInfoFundProcessor
        implements BaseProcessor<HttpServletRequest, String> {

    private MiniGameServiceImpl service = new MiniGameServiceImpl();

    public String execute(Param<HttpServletRequest> param) {
        FundInfoResponse response = new FundInfoResponse(true, "200");

        try {
            response.setFunds(service.getFunds());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response.toJson();
    }
}

