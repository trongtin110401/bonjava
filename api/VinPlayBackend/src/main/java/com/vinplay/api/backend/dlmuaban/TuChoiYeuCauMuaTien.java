package com.vinplay.api.backend.dlmuaban;

import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;

public class TuChoiYeuCauMuaTien implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        String trainid = request.getParameter("trainid");
        MuaBanTien muaban = new MuaBanTien();
        muaban.TuChoiMua(trainid);
        return "{\"errorcode\":\"200\",\"des\":\"Thanh Cong\"}";
    }
}
