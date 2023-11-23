package com.vinplay.api.processors.SEOGame;

import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.utils.VinPlayUtils;

import javax.servlet.http.HttpServletRequest;

public class LuuSeoProcess implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        try {
            HttpServletRequest request = (HttpServletRequest)param.get();
            String utm_dl = request.getParameter("utm_dl");
            String utm_source = request.getParameter("utm_source");
            String utm_medium= request.getParameter("utm_medium");
            String utm_campaign= request.getParameter("utm_campaign");
            String ip = request.getParameter("ip");
            String device = request.getParameter("device");
            String note1 = request.getParameter("note1");
            String note2 = request.getParameter("note2");
            String note3 = request.getParameter("note3");
            String create_time = VinPlayUtils.getCurrentDateTime();
            XuLyAnali xuly = new XuLyAnali();
            xuly.insertData(utm_dl, utm_source, utm_medium, utm_campaign, create_time, ip, device, note1, note2, note3);
            return "{\"error\":\"0\"}";

        }catch (Exception e) {
            e.printStackTrace();
        }
        return "{\"error\":\"1\"}";
    }
}
