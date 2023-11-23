package com.vinplay.api.backend.processors.otp;

import com.vinplay.api.backend.models.OTPDAO;
import com.vinplay.api.backend.models.OTPELK;
import com.vinplay.api.backend.models.UserOTP;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

public class KichhoatTayOTP implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        try {
            HttpServletRequest request = (HttpServletRequest)param.get();
            String nickname = request.getParameter("nickname");
            OTPDAO otp_pro = new OTPDAO();
            OTPELK oelk = new OTPELK();
            String id_elk = oelk.GetIDActivebyNickname(nickname);
            UserOTP uo = otp_pro.GetActive(nickname);
            uo.setActive(1);
            otp_pro.UpdateActive(uo);
            otp_pro.UpdateSDTOTP2(uo.getNickname(), uo.getPhone());
            oelk.UpdateActiveELK(uo, id_elk);

            return "1";
        }catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }

}
