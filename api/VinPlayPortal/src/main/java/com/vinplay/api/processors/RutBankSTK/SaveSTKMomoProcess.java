package com.vinplay.api.processors.RutBankSTK;

import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.utils.VinPlayUtils;

import javax.servlet.http.HttpServletRequest;

public class SaveSTKMomoProcess implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = param.get();
        String nickName = request.getParameter("nickName");
        String phoneNumber = request.getParameter("phoneNumber");
        String timelog = VinPlayUtils.getCurrentDateTime();
        RutBanhXuLy rutxuly = new RutBanhXuLy();
        InfoMomoEnity infoMomoEnity = new InfoMomoEnity(nickName, phoneNumber, timelog);
        boolean check = rutxuly.InsertRutBankMomo(infoMomoEnity);
        if (check == true) {
            return "{\"error\":0}";
        } else {
            return "{\"error\":1}";
        }

    }
}

