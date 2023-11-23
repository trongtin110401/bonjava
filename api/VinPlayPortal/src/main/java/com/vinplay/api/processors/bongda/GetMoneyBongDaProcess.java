package com.vinplay.api.processors.bongda;

import com.vinplay.bongda.dao.KeoBongDaDao;
import com.vinplay.bongda.dao.impl.KeoBongDaImpl;
import com.vinplay.bongda.entities.KeoBongDaResponse;
import com.vinplay.common.HttpCommon;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

public class GetMoneyBongDaProcess implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String) "api");

    @Override
    public String execute(Param<HttpServletRequest> req) {
//        HttpServletRequest rq = req.get();
//        String at = rq.getParameter("at"); //dd/mm/yy
//        try {
//            OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
//                    .build();
//            Request request = new Request.Builder()
//                    .url("https://bandoluuniem.net/sport/user/getUserByToken?token="+at)
//                    .method("GET", null)
//
//                    .build();
//            Response response = client.newCall(request).execute();
//            return  response.body().string();
//        } catch (Exception e){
//
//        }
        logger.debug("fdafdafsdf sdfasdfasdfas dfasdfasd");
        return "0";

    }
}
