package com.vinplay.api.processors.RutBankSTK;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.utils.VinPlayUtils;

import javax.servlet.http.HttpServletRequest;

public class SaveSTKBankProcess implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        String nickname = request.getParameter("nickname");
        String bankname = request.getParameter("bankname");
        String banknumber = request.getParameter("banknumber");
        String bankbran = request.getParameter("bankbran");
        String timelog = VinPlayUtils.getCurrentDateTime();
        bankname = bankname.replaceAll("_"," ");
        RutBanhXuLy rutxuly = new RutBanhXuLy();
        InfoBankEnity info = new InfoBankEnity(nickname, bankname, banknumber, bankbran, timelog);
        boolean check = rutxuly.InsertRutBankSTK(info);
        if(check == true){
            return "{\"error\":0}";
        }else{
            return "{\"error\":1}";
        }

    }
}

