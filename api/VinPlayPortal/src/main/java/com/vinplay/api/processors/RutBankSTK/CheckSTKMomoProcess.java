package com.vinplay.api.processors.RutBankSTK;

import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;

public class CheckSTKMomoProcess implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = param.get();
        String nickname = request.getParameter("nickName");
        RutBanhXuLy rutxuly = new RutBanhXuLy();
        InfoMomoEnity info = rutxuly.GetRutBankMomo(nickname);
        if (info == null) {
            return "{\"error\":1,\"nameBank\": null}";
        } else {
            return "{\"error\":0,\"phoneNumber\":\"" + info.getPhoneNumber() + "\",\"nickName\":\"" + info.getNickname() + "\"}";
        }

    }
}

