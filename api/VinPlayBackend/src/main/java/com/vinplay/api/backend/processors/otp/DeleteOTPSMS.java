package com.vinplay.api.backend.processors.otp;

import com.vinplay.api.backend.models.OTPDAO;
import com.vinplay.api.backend.models.OTPELK;
import com.vinplay.api.backend.models.UserOTP;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DeleteOTPSMS implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        try {
            HttpServletRequest request = (HttpServletRequest)param.get();
            String nickname = request.getParameter("nickname");
            OTPDAO otp_pro = new OTPDAO();
            OTPELK oelk = new OTPELK();
            String id_elk = oelk.GetIDActivebyNickname(nickname);
            otp_pro.DeleteOTP(nickname);
            otp_pro.UpdateSDTUserDelete(nickname);
            Long create_time_2 = new Date().getTime();
            Date d = new Date(create_time_2);
            SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String strDate = sdfDate.format(d);
            UserOTP uotp = new UserOTP("da xoa", "da xoa", "da xoa", "da xoa", 0, 0, 0,0,strDate);
            oelk.DeleteActiveELK(uotp, id_elk);
            return "1";
        }catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }

}
