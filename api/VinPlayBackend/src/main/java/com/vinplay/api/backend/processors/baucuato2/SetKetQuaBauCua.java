package com.vinplay.api.backend.processors.baucuato2;

import com.vinplay.dal.service.CacheService;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.BauCuaTo2.SetBauCuaKetqua;

import javax.servlet.http.HttpServletRequest;

public class SetKetQuaBauCua implements BaseProcessor<HttpServletRequest, String> {
    @Override
    public String execute(Param<HttpServletRequest> var1) {
        CacheService cacheService = new CacheServiceImpl();
        HttpServletRequest request = (HttpServletRequest) var1.get();
        String status = request.getParameter("st");
        String dice1 = request.getParameter("dc1");
        String dice2 = request.getParameter("dc2");
        String dice3 = request.getParameter("dc3");
        byte d1 = (byte) Integer.parseInt(dice1);
        byte d2 = (byte) Integer.parseInt(dice2);
        byte d3 = (byte) Integer.parseInt(dice3);
        byte [] listDices = new byte[]{d1, d2, d3};
        SetBauCuaKetqua setBauCuaKetqua = new SetBauCuaKetqua(status,listDices);
        cacheService.setObject("setBauCuaKetqua",setBauCuaKetqua);
        return "0";
    }
}
