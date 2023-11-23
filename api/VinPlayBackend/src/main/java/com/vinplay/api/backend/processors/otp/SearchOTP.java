package com.vinplay.api.backend.processors.otp;

import com.google.gson.Gson;
import com.vinplay.api.backend.models.OTPELK;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

public class SearchOTP implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String)"backend");
    public String execute(Param<HttpServletRequest> param) {
        try {
            HttpServletRequest request = (HttpServletRequest) param.get();
            String timkiem = request.getParameter("timkiem");
            OTPprocess otPprocess = new OTPprocess();
//            logger.info("apiiii"+ timkiem);
            Gson gson = new Gson();
            UserOTP uotp1 = otPprocess.getUserOtpFind(timkiem);
//            logger.info(null == uotp1 +" vclll null roi");
            if (uotp1 != null) {
                return gson.toJson(uotp1);
            } else {
                return null;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
