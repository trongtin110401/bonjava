package com.vinplay.api.processors.accnhatvip;

import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;

public class CheckloginNhatVipProcess implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        CheckLoginNhatVip checkacc = new CheckLoginNhatVip();
        boolean check = checkacc.checklogin(username, password);
        return "{\"trangthai\":\""+check+"\"}";
    }
}

