/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.vinplay.usercore.service.impl.UserForAdminServiceImpl
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.response.ResultUserReponse
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.log4j.Logger
 */
package com.vinplay.api.backend.processors;

import com.vinplay.dal.service.CacheService;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import com.vinplay.dal.service.impl.MiniGameServiceImpl;
import com.vinplay.usercore.service.OtherService;
import com.vinplay.usercore.service.impl.OtherServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.exceptions.KeyNotFoundException;
import com.vinplay.vbee.common.response.FundInfoResponse;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.bson.Document;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class UpdateFundProcessor implements BaseProcessor<HttpServletRequest, String> {
    CacheService cacheService = new CacheServiceImpl();
//    private MiniGameServiceImpl service = new MiniGameServiceImpl();
    private final static String DEPOSIT = "deposit";

    private final static String WITHDRAW = "withdraw";


    public String execute(Param<HttpServletRequest> param) {
        Map<String, String> games = new HashMap<>();
        games.put("TaiXiu", "update_fund_tx_auto");
        games.put("TaiXiuMd5", "update_fund_tx_md5_auto");
        games.put("XocDia", "update_fund_xd_auto");
        games.put("BauCuaTo_vin_1000", "update_fund_bau_cua_to");


        FundInfoResponse response = new FundInfoResponse(true, "200");

        HttpServletRequest request = param.get();

        String fundName = request.getParameter("fundName");
        long amount = Long.parseLong(request.getParameter("amount"));
        String type = request.getParameter("type");
        try {
//            long fund = service.getFund(fundName);
//            if (type.equals(DEPOSIT)) {
//                fund += amount;
//            }
//            if (type.equals(WITHDRAW)) {
//                fund -= amount;
//            }

            if (games.containsKey(fundName)) {
                if (type.equals(DEPOSIT)) {
                    cacheService.setValue(games.get(fundName), amount);
                }
                if (type.equals(WITHDRAW)) {
                    cacheService.setValue(games.get(fundName), amount * -1);
                }
            }

//            service.saveFund(fundName, fund);

            Document document = new Document();
            document.put("fund_name", fundName);
            document.put("amount", amount);
            document.put("type", type);
            document.put("time_log", VinPlayUtils.getCurrentDateTime());
            OtherService otherService = new OtherServiceImpl();
            otherService.saveTransactionUpdateFund(document);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return response.toJson();
    }
}

