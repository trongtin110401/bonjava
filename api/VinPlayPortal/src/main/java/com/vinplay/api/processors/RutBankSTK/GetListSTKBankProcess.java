package com.vinplay.api.processors.RutBankSTK;

import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;

public class GetListSTKBankProcess implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        String nickname = request.getParameter("nickname");
        String page = request.getParameter("page");
        int maxItem = 15;
        RutBanhXuLy rutxuly = new RutBanhXuLy();
        boolean check_xoa = rutxuly.deleteSTK(nickname);
        if(check_xoa == true){
            return "{\"error\":1,\"Des\":\"Xoa Thanh Cong\"}";
        }else{
            return "{\"error\":0,\"Des\":\"Khong Tim Thay Nickname\"}";
        }

    }
}
