package com.vinplay.api.backend.processors.taixiu;

import com.vinplay.dal.service.CacheService;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import com.vinplay.miniGame.TaiXiuBotSetUpObj;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;

public class SetBotTaiXiuKubetProcessor implements BaseProcessor<HttpServletRequest, String> {
    @Override
    public String execute(Param<HttpServletRequest> param) {
        String res = "0";
        HttpServletRequest request = param.get();
        String moneyMin = request.getParameter("moneyMin");
        String moneyMax = request.getParameter("moneyMax");
        String numberUserTaiMax = request.getParameter("numberUserTaiMax");
        String numberUserXiuMax = request.getParameter("numberUserXiuMax");
        String numberUserChanMax = request.getParameter("numberUserChanMax");
        String numberUserLeMax = request.getParameter("numberUserLeMax");
        try {
            CacheService cacheService = new CacheServiceImpl();
            TaiXiuBotSetUpObj obj = new TaiXiuBotSetUpObj(true, "0");
            obj.setMoneyMin(Long.parseLong(moneyMin));
            obj.setMoneyMax(Long.parseLong(moneyMax));
            obj.setNumberUserXiuMax(Integer.parseInt(numberUserXiuMax));
            obj.setNumberUserTaiMax(Integer.parseInt(numberUserTaiMax));
            obj.setNumberUserChan(Integer.parseInt(numberUserChanMax));
            obj.setNumberUserLe(Integer.parseInt(numberUserLeMax));
            cacheService.setObject("tai_xiu_set_bot_kubet", obj);
            return "1";
        } catch (Exception e) {
            return res;
        }
    }
}
