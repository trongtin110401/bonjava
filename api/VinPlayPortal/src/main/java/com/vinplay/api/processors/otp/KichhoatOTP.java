package com.vinplay.api.processors.otp;

import com.vinplay.api.entities.UserOTP;
import com.vinplay.api.otp.GenOTP;
import com.vinplay.api.otp.OTPELK;
import com.vinplay.api.otp.OTPprocess;
import com.vinplay.api.otp.SendOTP;
import com.vinplay.usercore.service.UserExtraService;
import com.vinplay.usercore.service.impl.UserExtraServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

public class KichhoatOTP implements BaseProcessor<HttpServletRequest, String> {
    public synchronized String execute(Param<HttpServletRequest> param) {
        try {
            HttpServletRequest request = (HttpServletRequest)param.get();
            String nicknamex = request.getParameter("nickname");
            String otp = request.getParameter("otp");
            String accessToken = request.getParameter("at");
            String nickname = this.getUserNameByAccessToken(accessToken);
            OTPprocess otp_pro = new OTPprocess();
            OTPELK oelk = new OTPELK();
            UserOTP uotp = otp_pro.GetActive(nickname);
            if(uotp == null){
                return "0";
            }else{
                Long active_time = new Date().getTime();
                int turn = uotp.getTurn()+1;
                if(turn > 3){
                    uotp.setActive(2);
                    uotp.setActive_time(active_time);
                    uotp.setTurn(turn);
                    otp_pro.UpdateActive(uotp);
                    String id_elk = oelk.GetIDActivebyNickname(uotp.getNickname());
                    oelk.UpdateActiveELK(uotp, id_elk);
                    return "3";
                }else{
                    boolean check_active = otp_pro.ActiveOTP(uotp.getCreat_time(),active_time,turn);
                    boolean check_otp_ok = otp_pro.CheckOTPok(nickname, otp);
                    if(check_active == true && check_otp_ok == true){
                        uotp.setActive(1);
                        uotp.setActive_time(active_time);
                        uotp.setTurn(turn);
                        otp_pro.UpdateActive(uotp);
                        otp_pro.UpdateSDTOTP(uotp.getNickname(), uotp.getPhone());
                        String id_elk = oelk.GetIDActivebyNickname(uotp.getNickname());
                        oelk.UpdateActiveELK(uotp, id_elk);
                        return "1";
                    }else if(check_active == false){
                        uotp.setActive(0);
                        uotp.setActive_time(active_time);
                        uotp.setTurn(turn);
                        return "2";
                    }else{
                        uotp.setActive(0);
                        uotp.setActive_time(active_time);
                        uotp.setTurn(turn);
                        return "0";
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }
    private String getUserNameByAccessToken(String accessToken){
        UserExtraService userExtraService = new UserExtraServiceImpl();
        return userExtraService.getModelFromToken(accessToken).getNickname();
    }
}
