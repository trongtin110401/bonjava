package com.vinplay.api.backend.processors.RutSTKBank;

import com.google.gson.Gson;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;

public class GetListSTKBankProcess implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        String nickname = request.getParameter("nickname");
        RutBanhXuLy rutxuly = new RutBanhXuLy();
//        boolean check_xoa = rutxuly.deleteSTK(nickname);
        InfoBankEnity info = rutxuly.GetRutBankSTK(nickname);
        Gson gson = new Gson();
        if(info == null){
            return null;
        }else{
            return gson.toJson(info);
        }

    }
}
