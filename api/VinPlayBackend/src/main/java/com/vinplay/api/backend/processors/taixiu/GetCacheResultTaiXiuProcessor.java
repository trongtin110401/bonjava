package com.vinplay.api.backend.processors.taixiu;

import com.google.gson.Gson;
import com.vinplay.dal.service.CacheService;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class GetCacheResultTaiXiuProcessor implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String)"backend");
    @Override
    public String execute(Param<HttpServletRequest> param) {
        CacheService cacheService = new CacheServiceImpl();
        try {
            Gson gson = new Gson();
            Map<String,String> hashMap = new HashMap<>();
            hashMap.put("tai_xiu_be_cang",cacheService.getValueStr("tai_xiu_be_cang"));
            hashMap.put("min_fund_tx_auto",cacheService.getValueStr("min_fund_tx_auto"));
            hashMap.put("max_fund_tx_auto",cacheService.getValueStr("max_fund_tx_auto"));
            hashMap.put("fund_tx_auto",cacheService.getValueStr("fund_tx_auto"));


            hashMap.put("my_debug",cacheService.getValueStr("my_debug"));

            hashMap.put("hu_100_poker",cacheService.getValueStr("hu_100_poker"));
            hashMap.put("hu_1k_poker",cacheService.getValueStr("hu_1k_poker"));
            hashMap.put("hu_10k_poker",cacheService.getValueStr("hu_10k_poker"));
            return gson.toJson(hashMap);
        } catch (Exception e) {
            cacheService.setValue("hu_100_poker",0);
            cacheService.setValue("hu_1k_poker",0);
            cacheService.setValue("hu_10k_poker",0);

            cacheService.setValue("my_debug","");

            cacheService.setValue("min_fund_tx_auto",0);
            cacheService.setValue("max_fund_tx_auto",0);
            cacheService.setValue("fund_tx_auto",0);
            return e.getMessage();
        }
    }
}
