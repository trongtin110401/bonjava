package com.vinplay.api.backend.processors.checkgame;

import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;

public class TurnOnOffProcess implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        String stt = request.getParameter("stt");
        TurnOnOff tu = new TurnOnOff();
        tu.TurnOnOf(stt);
        return "1";
    }
}
