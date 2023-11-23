package com.vinplay.api.processors.RutBankSTK;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;

public class CheckSTKBankProcess implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        String nickname = request.getParameter("nickname");
        RutBanhXuLy rutxuly = new RutBanhXuLy();
        InfoBankEnity info = rutxuly.GetRutBankSTK(nickname);
        if(info == null){
            return "{\"error\":1,\"nameBank\": null}";
        }else{
            return "{\"error\":0,\"nameBank\":\""+info.getBankname()+"\",\"bankname\":\""+info.getBankbran()+"\"}";
        }

    }
}

