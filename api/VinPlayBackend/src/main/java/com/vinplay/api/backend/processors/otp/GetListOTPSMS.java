package com.vinplay.api.backend.processors.otp;

import com.vinplay.api.backend.models.OTPDAO;
import com.vinplay.api.backend.models.OTPELK;
import com.vinplay.api.backend.models.UserOTP;
import com.vinplay.cashout.BankCBResponse;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class GetListOTPSMS implements BaseProcessor<HttpServletRequest, String> {

    public String execute(Param<HttpServletRequest> param) {
        ListOtpResponse res = new ListOtpResponse(false, "1001");
        try {
            OTPDAO otp_pro = new OTPDAO();
            OTPELK oelk = new OTPELK();
//            ArrayList<UserOTP> list_otp = otp_pro.GetListOTP();
            ArrayList<UserOTP> list_otp = oelk.GetListActive();
            res.UserOTP = list_otp;
            res.setErrorCode("0");
            res.setSuccess(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return res.toJson();
    }
}
