package com.vinplay.api.processors.checkgame;

import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;

public class CheckGameProcess implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        CheckOnOff checkof = new CheckOnOff();
        String check = checkof.getCheck();
        return "{\"check\": \""+check+"\"}";
    }
}
