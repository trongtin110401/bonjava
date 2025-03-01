package com.vinplay.api.backend.processors.otp;

import com.vinplay.api.backend.models.OTPDAO;
import com.vinplay.api.backend.models.OTPELK;
import com.vinplay.api.backend.models.UserOTP;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;

public class UpdatePhoneSMSOTP implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        try {
            HttpServletRequest request = (HttpServletRequest) param.get();
            String nickname = request.getParameter("nickname");
            String phone = request.getParameter("phone");
            OTPDAO otp_pro = new OTPDAO();
            OTPELK oelk = new OTPELK();
            String id_elk = oelk.GetIDActivebyNickname(nickname);
            otp_pro.UpdateSDTOTP(nickname, phone);
            UserOTP uotp = oelk.GetListActivebyNickname(nickname);
            uotp.setPhone(phone);
            oelk.DeleteActiveELK(uotp, id_elk);
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }
}
