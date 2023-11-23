package com.vinplay.api.backend.processors.otp;

import com.google.gson.Gson;
import com.vinplay.api.backend.models.OTPELK;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class GetListOTPByDate implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        try {
            HttpServletRequest request = (HttpServletRequest)param.get();
            String startDate = request.getParameter("startDate");
            String endDate = request.getParameter("endDate");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long startTime = simpleDateFormat.parse(startDate).getTime();
            long endTime = simpleDateFormat.parse(endDate).getTime();
            OTPprocess otPprocess = new OTPprocess();
            List<UserOTP> listActive = otPprocess.GetListOTPByTime(startTime,endTime);
            Gson gson = new Gson();
            return gson.toJson(listActive);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
