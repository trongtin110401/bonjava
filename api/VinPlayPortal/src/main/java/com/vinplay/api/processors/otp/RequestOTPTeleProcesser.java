package com.vinplay.api.processors.otp;

import com.vinplay.api.processors.response.RequestOTPTeleResponse;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.UserTele;

import javax.servlet.http.HttpServletRequest;

public class RequestOTPTeleProcesser implements BaseProcessor<HttpServletRequest, String> {

    public synchronized String execute(Param<HttpServletRequest> param) {
        try {
            RequestOTPTeleResponse response = new RequestOTPTeleResponse(false, "1001");
            HttpServletRequest request = param.get();
            OTPTeleService otpTeleService = new OTPTeleService();

            String nickname = request.getParameter("nickname");
            UserTele u = otpTeleService.getInfoByNickname(nickname);
            if (u == null || !u.isActive() || u.getChatID() == null || u.getChatID().isEmpty()) {
                response.setErrorCode("Tài khoản chưa xác thực Tele");
                return response.toJson();
            }
            String otp = otpTeleService.generateOTP();
            otpTeleService.sendOTP(u.getChatID(), otp);
            otpTeleService.saveOTP(u.getChatID(), otp);
            response.setErrorCode("200");
            response.setSuccess(true);
            return response.toJson();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
