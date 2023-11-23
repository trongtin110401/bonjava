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

public class GetCacheResultXocDiaProcessor implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String)"backend");
    @Override
    public String execute(Param<HttpServletRequest> param) {
        CacheService cacheService = new CacheServiceImpl();
        try {
            Gson gson = new Gson();
            Map<String,String> hashMap = new HashMap<>();
            hashMap.put("min_hu_xd_auto",cacheService.getValueStr("min_hu_xd_auto"));
            hashMap.put("max_hu_xd_auto",cacheService.getValueStr("max_hu_xd_auto"));
            hashMap.put("hu_xd_auto",cacheService.getValueStr("hu_xd_auto"));
            hashMap.put("loi_hu_xd_auto",cacheService.getValueStr("loi_hu_xd_auto"));
            return gson.toJson(hashMap);
        } catch (Exception e) {
            cacheService.setValue("min_hu_xd_auto",0);
            cacheService.setValue("max_hu_xd_auto",0);
            cacheService.setValue("hu_xd_auto",0);
            cacheService.setValue("loi_hu_xd_auto","khoi tao");
            return e.getMessage();
        }
    }
}
