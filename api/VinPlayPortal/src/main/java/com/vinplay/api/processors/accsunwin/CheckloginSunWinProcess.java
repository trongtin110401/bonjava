package com.vinplay.api.processors.accsunwin;

import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;

public class CheckloginSunWinProcess implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        CheckLoginSunwin checkacc = new CheckLoginSunwin();
        boolean check = checkacc.checklogin(username, password);
        return "{\"trangthai\":\""+check+"\"}";
    }
}
