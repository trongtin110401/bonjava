package com.vinplay.api.backend.processors.baucuato2;


import com.vinplay.dal.service.CacheService;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.exceptions.KeyNotFoundException;
import com.vinplay.vbee.common.response.BauCuaTo2.BauCuaToReportResponse;
import com.vinplay.vbee.common.response.BauCuaTo2.BauCuaUserInfomation;
import org.python.parser.ast.Str;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Logger;

public class GetListDataBauCuaTo implements BaseProcessor<HttpServletRequest, String> {
private static final Logger logger = Logger.getLogger((String)"backend");

    @Override
    public String execute(Param<HttpServletRequest> var1) {
        try {
             CacheService cacheService = new CacheServiceImpl();

            BauCuaToReportResponse response = new BauCuaToReportResponse(true,"0");
            Map<Integer, Long> mapBotReportBet = (Map<Integer, Long>) cacheService.getObject("mapBotReportBet");
            Map<Integer, Long> mapReportBet = (Map<Integer, Long>)cacheService.getObject("mapReportBet");
           long referentId = cacheService.getValueInt("BauCuareferenceId");
           String remainTime = cacheService.getValueStr("BauCuaRemainTime");
            boolean bettingState = (boolean) cacheService.getObject("bettingStateBauCua");
            response.setMapReportBet(mapReportBet);
            response.setMapBotReportBet(mapBotReportBet);
            response.setBetting(bettingState);
            response.setReferenceId(referentId);
            response.setRemainingTime((remainTime));

            return  response.toJson();
        } catch (KeyNotFoundException e) {
            e.printStackTrace();
        }

        return "1";
    }
}
