/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.vinplay.usercore.service.impl.OtpServiceImpl
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.log4j.Logger
 */
package com.vinplay.api.processors.otp;

import com.vinplay.usercore.service.impl.OtpServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;

import com.vinplay.vbee.common.response.BaseResponseModel;
import org.apache.log4j.Logger;

public class CheckOtpProcessor implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger("api");

    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = param.get();
        String nickname = request.getParameter("nickname");
        String otp = request.getParameter("otp");
        if (nickname != null && otp != null && !otp.isEmpty()) {
            OtpServiceImpl service = new OtpServiceImpl();
            try {
                BaseResponseModel model = service.checkOTP(nickname, otp);
                return model.toJson();
            } catch (Exception e) {
                logger.debug(e);
            }
        }
        return "1";
    }
}

