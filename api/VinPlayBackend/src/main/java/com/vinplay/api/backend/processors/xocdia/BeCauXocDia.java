package com.vinplay.api.backend.processors.xocdia;

import com.vinplay.dal.service.CacheService;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import com.vinplay.utils.TelegramUtil;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.BauCuaTo2.SetBauCuaKetqua;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

public class BeCauXocDia implements BaseProcessor<HttpServletRequest, String> {
    @Override
    public String execute(Param<HttpServletRequest> var1) {
        CacheService cacheService = new CacheServiceImpl();
        HttpServletRequest request = (HttpServletRequest) var1.get();
        String status = request.getParameter("st");
        String dice1 = request.getParameter("dc1");
        String dice2 = request.getParameter("dc2");
        String dice3 = request.getParameter("dc3");
        String dice4 = request.getParameter("dc4");
        byte d1 = (byte) Integer.parseInt(dice1);
        byte d2 = (byte) Integer.parseInt(dice2);
        byte d3 = (byte) Integer.parseInt(dice3);
        byte d4 = (byte) Integer.parseInt(dice4);
        byte [] listDices = new byte[]{d1, d2, d3,d4};
        SetBauCuaKetqua setBauCuaKetqua = new SetBauCuaKetqua(status,listDices);
        cacheService.setObject("BeCauXocDia",setBauCuaKetqua);
//        try {
//            TelegramUtil.sendMessAlert( "Bon cau xoc dia la :" + dice1 + " " + dice2 + " " + dice3 + " "+dice4 +"( 0 la trang 1 la do )" ,"1954746610:AAGUFceSa35b9nm6Ps2RGb6IeS4GhbvdOmo","-581283191");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        return "0";
    }


}