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

public class GetCacheResultTaiXiuMd5Processor implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String)"backend");
    @Override
    public String execute(Param<HttpServletRequest> param) {
        CacheService cacheService = new CacheServiceImpl();
        try {
            Gson gson = new Gson();
            Map<String,String> hashMap = new HashMap<>();
            hashMap.put("tai_xiu_be_cang",cacheService.getValueStr("tai_xiu_be_cang_md5"));
            hashMap.put("min_hu_tx_auto",cacheService.getValueStr("min_hu_tx_auto_md5"));
            hashMap.put("max_hu_tx_auto",cacheService.getValueStr("max_hu_tx_auto_md5"));
            hashMap.put("hu_tx_auto",cacheService.getValueStr("hu_tx_auto_md5"));


            hashMap.put("my_debug",cacheService.getValueStr("my_debug_md5"));

            hashMap.put("hu_100_poker",cacheService.getValueStr("hu_100_poker_md5"));
            hashMap.put("hu_1k_poker",cacheService.getValueStr("hu_1k_poker_md5"));
            hashMap.put("hu_10k_poker",cacheService.getValueStr("hu_10k_poker_md5"));
            return gson.toJson(hashMap);
        } catch (Exception e) {
            cacheService.setValue("hu_100_poker_md5",0);
            cacheService.setValue("hu_1k_poker_md5",0);
            cacheService.setValue("hu_10k_poker_md5",0);

            cacheService.setValue("my_debug_md5","");

            cacheService.setValue("min_hu_tx_auto_md5",0);
            cacheService.setValue("max_hu_tx_auto_md5",0);
            cacheService.setValue("hu_tx_auto_md5",0);
            return e.getMessage();
        }
    }
}
