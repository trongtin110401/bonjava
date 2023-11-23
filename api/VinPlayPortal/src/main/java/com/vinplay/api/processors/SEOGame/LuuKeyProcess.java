package com.vinplay.api.processors.SEOGame;

import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.utils.VinPlayUtils;

import javax.servlet.http.HttpServletRequest;

public class LuuKeyProcess implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        try {
            HttpServletRequest request = (HttpServletRequest)param.get();
            String type = request.getParameter("type");
            String code_dl = request.getParameter("code_dl");
            String key1 = request.getParameter("key1");
            String key2 = request.getParameter("key2");
            String key3 = request.getParameter("key3");
            String key4 = request.getParameter("key4");
            String key5 = request.getParameter("key5");
            String key6 = request.getParameter("key6");
            String key7 = request.getParameter("key7");
            String key8 = request.getParameter("key8");
            String key9 = request.getParameter("key9");
            String key10 = request.getParameter("key10");
            XuLyAnali xuly = new XuLyAnali();
            if(type.equalsIgnoreCase("1")){
                xuly.insertKey(code_dl, key1, key2, key3, key4, key5, key6, key7, key8, key9, key10);
            }else{
                xuly.updateKey(code_dl, key1, key2, key3, key4, key5, key6, key7, key8, key9, key10);
            }

            return "{\"error\":\"0\"}";

        }catch (Exception e) {
            e.printStackTrace();
        }
        return "{\"error\":\"1\"}";
    }
}

