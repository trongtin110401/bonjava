package com.vinplay.api.processors.accsunwin;

import com.google.gson.Gson;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;

public class GetCaptchaLoginSun implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        XuLyAcc xu = new XuLyAcc();
        CaptchaPhepTinh captchaMoel = xu.CallCap();
        Gson gson = new Gson();
        return gson.toJson(captchaMoel);
    }
}
