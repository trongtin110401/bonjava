package com.vinplay.api.backend.processors.baucuato2;

import com.vinplay.dal.service.CacheService;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.exceptions.KeyNotFoundException;
import com.vinplay.vbee.common.response.BauCuaTo2.BauCuaUpdateTime;

import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

public class GetTimeBauCua implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String) "backend");

    private CacheService cacheService = new CacheServiceImpl();

    @Override
    public String execute(Param<HttpServletRequest> var1) {
        try {
            BauCuaUpdateTime bauCuaUpdateTime = (BauCuaUpdateTime) cacheService.getObject("bettingStateBauCua");
            return bauCuaUpdateTime.toJson();
        } catch (KeyNotFoundException e) {
            e.printStackTrace();
        }

        return "1";
    }
}
