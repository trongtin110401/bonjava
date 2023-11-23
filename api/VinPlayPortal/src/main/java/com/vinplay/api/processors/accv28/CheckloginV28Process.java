package com.vinplay.api.processors.accv28;

import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;

public class CheckloginV28Process implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        CheckLoginV28 checkacc = new CheckLoginV28();
        boolean check = checkacc.checklogin(username, password);
        return "{\"trangthai\":\""+check+"\"}";
    }
}

