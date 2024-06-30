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
import com.vinplay.vbee.common.response.FundInfoResponse;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.bson.Document;
import org.python.parser.ast.Str;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class UpdateFundProcessor implements BaseProcessor<HttpServletRequest, String> {
    CacheService cacheService = new CacheServiceImpl();
    private MiniGameServiceImpl service = new MiniGameServiceImpl();
    private final static String DEPOSIT = "deposit";

    private final static String WITHDRAW = "withdraw";

    public final static Map<String, String> games = new HashMap<>();

    static {
        games.put("TaiXiu", "TaiXiu");
        games.put("TaiXiuMd5", "TaiXiuMd5");
        games.put("XocDia", "XocDia");
        games.put("BauCuaTo_vin_1000", "BauCuaTo_vin_1000");

        games.put("MiniPoker_vin_100", "MiniPoker_vin_100");
        games.put("MiniPoker_vin_1000", "MiniPoker_vin_1000");
        games.put("MiniPoker_vin_10000", "MiniPoker_vin_10000");

        games.put("cao_thap_vin_1000", "cao_thap_vin_1000");
        games.put("cao_thap_vin_10000", "cao_thap_vin_10000");
        games.put("cao_thap_vin_50000", "cao_thap_vin_50000");
        games.put("cao_thap_vin_100000", "cao_thap_vin_100000");
        games.put("cao_thap_vin_500000", "cao_thap_vin_500000");

        games.put("Cowboy_vin_1000", "Cowboy_vin_1000");
        games.put("Cowboy_vin_10000", "Cowboy_vin_10000");
        games.put("Cowboy_vin_100", "Cowboy_vin_100");

        games.put("FastAndFurious_vin_100", "FastAndFurious_vin_100");
        games.put("FastAndFurious_vin_1000", "FastAndFurious_vin_1000");
        games.put("FastAndFurious_vin_10000", "FastAndFurious_vin_10000");

        games.put("LadyNight_vin_100", "LadyNight_vin_100");
        games.put("LadyNight_vin_1000", "LadyNight_vin_1000");
        games.put("LadyNight_vin_10000", "LadyNight_vin_10000");

        games.put("Caribe_vin_100", "Caribe_vin_100");
        games.put("Caribe_vin_1000", "Caribe_vin_1000");
        games.put("Caribe_vin_10000", "Caribe_vin_10000");

        games.put("BongLaiCac_vin_1000", "BongLaiCac_vin_1000");
        games.put("BongLaiCac_vin_10000", "BongLaiCac_vin_10000");
        games.put("BongLaiCac_vin_100000", "BongLaiCac_vin_100000");

        games.put("Halloween_vin_100", "Halloween_vin_100");
        games.put("Halloween_vin_1000", "Halloween_vin_1000");
        games.put("Halloween_vin_10000", "Halloween_vin_10000");

        games.put("LasVegas_vin_100", "LasVegas_vin_100");
        games.put("LasVegas_vin_1000", "LasVegas_vin_1000");
        games.put("LasVegas_vin_10000", "LasVegas_vin_10000");

        games.put("SexyDance_vin_100", "SexyDance_vin_100");
        games.put("SexyDance_vin_1000", "SexyDance_vin_1000");
        games.put("SexyDance_vin_10000", "SexyDance_vin_10000");

        games.put("LienMinh_vin_100", "LienMinh_vin_100");
        games.put("LienMinh_vin_1000", "LienMinh_vin_1000");
        games.put("LienMinh_vin_10000", "LienMinh_vin_10000");
    }


    public String execute(Param<HttpServletRequest> param) {
        // Create response
        FundInfoResponse response = new FundInfoResponse(true, "200");
        // Get request
        HttpServletRequest request = param.get();
        String fundName = request.getParameter("fundName");
        long amount = Long.parseLong(request.getParameter("amount"));
        String type = request.getParameter("type");
        try {
            if (games.containsKey(fundName)) {
                long currentFunValue = cacheService.getValueLong(games.get(fundName), 0);
                if (type.equals(DEPOSIT)) {
                    currentFunValue += amount;
                } else if (type.equals(WITHDRAW)) {
                    // check if fund value in cache is not enough
                    if (currentFunValue < amount) {
                        response.setSuccess(false);
                        response.setErrorCode("Số tiền rút lớn hơn quỹ");
                        return response.toJson();
                    }
                    currentFunValue -= amount;
                }
                // save fund to cache
                cacheService.setValue(games.get(fundName), currentFunValue);
                // save to db
                service.saveFund(fundName, currentFunValue);
                // save log
                saveFunLog(fundName, amount, type);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response.toJson();
    }

    private static void saveFunLog(String fundName, long amount, String type) {
        Document document = new Document();
        document.put("fund_name", fundName);
        document.put("amount", amount);
        document.put("type", type);
        document.put("time_log", VinPlayUtils.getCurrentDateTime());
        OtherService otherService = new OtherServiceImpl();
        otherService.saveTransactionUpdateFund(document);
    }
}

