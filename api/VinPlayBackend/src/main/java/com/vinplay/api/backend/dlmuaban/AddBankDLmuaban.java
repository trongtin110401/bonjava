package com.vinplay.api.backend.dlmuaban;

import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;

public class AddBankDLmuaban implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {

        try {
            HttpServletRequest request = (HttpServletRequest)param.get();
            String dl_id = request.getParameter("dlid");
            Long dlid = Long.parseLong(dl_id);
            String bank = request.getParameter("bank");
            String banknum = request.getParameter("banknum");
            String bankname = request.getParameter("bankname");
            String chinhanh = request.getParameter("chinhanh");
            bankdlEntity daily = new bankdlEntity(dlid, bank, bankname, banknum, chinhanh);
            SaveDLMuaBan savedl = new SaveDLMuaBan();
            savedl.SaveBankDL(daily);
            return "{\"errorcode\":\"200\",\"des\":\"Thanh Cong\"}";
        }catch (Exception e){
            return "{\"errorcode\":\"500\",\"des\":\"Exception "+e+"\"}";
        }

    }
}
