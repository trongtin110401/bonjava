package com.vinplay.api.backend.processors.taixiu;

import com.vinplay.dal.service.CacheService;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import com.vinplay.miniGame.TaiXiuBotSetUpObj;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;

public class GetInfSetBotTaiXiuKubetProcessor implements BaseProcessor<HttpServletRequest, String> {
    @Override
    public String execute(Param<HttpServletRequest> param) {
        String res = "0";
        try {
            CacheService cacheService = new CacheServiceImpl();
            TaiXiuBotSetUpObj response = (TaiXiuBotSetUpObj) cacheService.getObject("tai_xiu_set_bot_kubet");
            return response.toJson();
        } catch (Exception e) {
            return res;
        }
    }
}
