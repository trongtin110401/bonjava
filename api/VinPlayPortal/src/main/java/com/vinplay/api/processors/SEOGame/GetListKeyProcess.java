package com.vinplay.api.processors.SEOGame;

import com.google.gson.Gson;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.utils.VinPlayUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class GetListKeyProcess implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        try {
            HttpServletRequest request = (HttpServletRequest)param.get();
            String pagex = request.getParameter("page");
            String maxItemx = request.getParameter("maxItem");
            int page = Integer.parseInt(pagex);
            int maxIteam = Integer.parseInt(maxItemx);
            XuLyAnali xuly = new XuLyAnali();
            ArrayList<KeyEnity> list_key = xuly.getListKey(page,maxIteam);
            Gson gson = new Gson();
            return gson.toJson(list_key);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return "{\"error\":\"1\"}";
    }
}
