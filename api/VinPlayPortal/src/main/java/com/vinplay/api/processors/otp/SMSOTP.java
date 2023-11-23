package com.vinplay.api.processors.otp;

import com.vinplay.api.entities.UserOTP;
import com.vinplay.api.otp.OTPprocess;
import com.vinplay.usercore.service.UserExtraService;
import com.vinplay.usercore.service.impl.UserExtraServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

public class SMSOTP implements BaseProcessor<HttpServletRequest, String> {
    public synchronized String execute(Param<HttpServletRequest> param) {
        try {
        HttpServletRequest request = (HttpServletRequest)param.get();
        String nicknamex = request.getParameter("nickname");
            String accessToken = request.getParameter("at");
            String nickname = this.getUserNameByAccessToken(accessToken);
        OTPprocess otp_pro = new OTPprocess();
        boolean check_active = otp_pro.checkUserActiveOTP(nickname);
        if(check_active == true){
            boolean check_mobile = otp_pro.GetMobile(nickname);
            if(check_mobile == false){
                UserOTP uotp = otp_pro.GetActive(nickname);
                otp_pro.UpdateSDTOTP(nickname, uotp.getPhone());
            }
            return "1";
        }else{
            boolean check_mobile_2 = otp_pro.GetMobile(nickname);
            if(check_mobile_2 == true){
                otp_pro.UpdateSDTOTP(nickname, null);
            }
            return "0";
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "1";
    }
    private String getUserNameByAccessToken(String accessToken){
        UserExtraService userExtraService = new UserExtraServiceImpl();
        return userExtraService.getModelFromToken(accessToken).getNickname();
    }
}
