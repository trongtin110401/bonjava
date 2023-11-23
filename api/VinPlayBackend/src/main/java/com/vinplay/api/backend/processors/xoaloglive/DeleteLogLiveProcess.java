package com.vinplay.api.backend.processors.xoaloglive;

import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class DeleteLogLiveProcess implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        String nickaname = request.getParameter("nickname");
        XoaTransMongo xoamong = new XoaTransMongo();
        XoaTransELK xoaelk = new XoaTransELK();
        ArrayList<String> listid = xoamong.listTranID(nickaname);
        ArrayList<String> listidelk = xoaelk.ListIdELK(nickaname);
        for(String tran : listid){
            xoamong.xoa(tran);
        }
        for(String tranx : listidelk){
            xoaelk.Xoa(tranx);
        }
        return "{\"error\":200}";
    }
}
