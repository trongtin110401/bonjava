package com.vinplay.api.backend.dlmuaban;

import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;

public class UpdateDLMuaBan implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        try {
            HttpServletRequest request = (HttpServletRequest)param.get();
            String id = request.getParameter("id");
            String dl_id = request.getParameter("dlid");
            Long dlid = Long.parseLong(dl_id);
            String fullname = request.getParameter("fullname");
            String username = request.getParameter("username");
            String nickname = request.getParameter("nickname");
            String phone = request.getParameter("phone");
            String khuvuc = request.getParameter("khuvuc");
            String tele = request.getParameter("tele");
            String fb = request.getParameter("fb");
            String zalo = request.getParameter("zalo");
            String bank = request.getParameter("bank");
            String banknum = request.getParameter("banknum");
            String bankname = request.getParameter("bankname");
            String note = request.getParameter("note");
            dlmuabanEntity daily = new dlmuabanEntity(id,dlid, fullname, username, nickname, phone, khuvuc, tele, fb, zalo,bank,banknum, bankname, note);
            SaveDLMuaBan savedl = new SaveDLMuaBan();
            savedl.updateDaiLy(daily);
            return "{\"errorcode\":\"200\",\"des\":\"Thanh Cong\"}";
        }catch (Exception e){
            return "{\"errorcode\":\"500\",\"des\":\"dlid la so int tu 1 den 10000"+"\"}";
        }
    }
}
