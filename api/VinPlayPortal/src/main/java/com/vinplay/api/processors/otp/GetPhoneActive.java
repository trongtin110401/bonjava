package com.vinplay.api.processors.otp;

import com.vinplay.api.entities.UserOTP;
import com.vinplay.api.otp.OTPprocess;
import com.vinplay.usercore.service.UserExtraService;
import com.vinplay.usercore.service.impl.UserExtraServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.PhoneActiveResponse;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

public class GetPhoneActive implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        PhoneActiveResponse res = new PhoneActiveResponse(false, "1001");;
        try {
            HttpServletRequest request = (HttpServletRequest) param.get();
            String nicknamex = request.getParameter("nickname");
            String accessToken = request.getParameter("at");
            String nickname = this.getUserNameByAccessToken(accessToken);
            OTPprocess otp_pro = new OTPprocess();
            UserOTP uotp = otp_pro.GetActive(nickname);
            if(uotp == null){
                res.setErrorCode("404");
            } else {
                res.setErrorCode("0");
                res.setPhone(uotp.getPhone());
                res.setActive(uotp.getActive());
            }
            return res.toJson();
//            return "{\"success\":false,\"errorCode\":\"0\",\"phone\":\"012345****\",\"active\":1}";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    private String getUserNameByAccessToken(String accessToken){
        UserExtraService userExtraService = new UserExtraServiceImpl();
        return userExtraService.getModelFromToken(accessToken).getNickname();
    }
}
