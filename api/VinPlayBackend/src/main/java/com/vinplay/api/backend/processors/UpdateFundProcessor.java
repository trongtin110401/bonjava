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
import com.vinplay.vbee.common.enums.Games;
import com.vinplay.vbee.common.response.FundInfoResponse;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.bson.Document;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class UpdateFundProcessor implements BaseProcessor<HttpServletRequest, String> {
    CacheService cacheService = new CacheServiceImpl();
    private MiniGameServiceImpl service = new MiniGameServiceImpl();
    private final static String DEPOSIT = "deposit";

    private final static String WITHDRAW = "withdraw";

    public final static Map<String, String> funds = new HashMap<>();
    public static final Map<String, String> fund2GameName = new HashMap<>();

    static {
        funds.put("TaiXiu", "TaiXiu");
        funds.put("TaiXiuMd5", "TaiXiuMd5");
        funds.put("XocDia", "XocDia");
        funds.put("BauCuaTo_vin_1000", "BauCuaTo_vin_1000");

        funds.put("MiniPoker_vin_100", "MiniPoker_vin_100");
        funds.put("MiniPoker_vin_1000", "MiniPoker_vin_1000");
        funds.put("MiniPoker_vin_10000", "MiniPoker_vin_10000");

        funds.put("cao_thap_vin_1000", "cao_thap_vin_1000");
        funds.put("cao_thap_vin_10000", "cao_thap_vin_10000");
        funds.put("cao_thap_vin_50000", "cao_thap_vin_50000");
        funds.put("cao_thap_vin_100000", "cao_thap_vin_100000");
        funds.put("cao_thap_vin_500000", "cao_thap_vin_500000");

        funds.put("Cowboy_vin_1000", "Cowboy_vin_1000");
        funds.put("Cowboy_vin_10000", "Cowboy_vin_10000");
        funds.put("Cowboy_vin_100", "Cowboy_vin_100");

        funds.put("FastAndFurious_vin_100", "FastAndFurious_vin_100");
        funds.put("FastAndFurious_vin_1000", "FastAndFurious_vin_1000");
        funds.put("FastAndFurious_vin_10000", "FastAndFurious_vin_10000");

        funds.put("LadyNight_vin_100", "LadyNight_vin_100");
        funds.put("LadyNight_vin_1000", "LadyNight_vin_1000");
        funds.put("LadyNight_vin_10000", "LadyNight_vin_10000");

        funds.put("Caribe_vin_100", "Caribe_vin_100");
        funds.put("Caribe_vin_1000", "Caribe_vin_1000");
        funds.put("Caribe_vin_10000", "Caribe_vin_10000");

        funds.put("BongLaiCac_vin_100", "BongLaiCac_vin_100");
        funds.put("BongLaiCac_vin_1000", "BongLaiCac_vin_1000");
        funds.put("BongLaiCac_vin_10000", "BongLaiCac_vin_10000");

        funds.put("Halloween_vin_100", "Halloween_vin_100");
        funds.put("Halloween_vin_1000", "Halloween_vin_1000");
        funds.put("Halloween_vin_10000", "Halloween_vin_10000");

        funds.put("LasVegas_vin_100", "LasVegas_vin_100");
        funds.put("LasVegas_vin_1000", "LasVegas_vin_1000");
        funds.put("LasVegas_vin_10000", "LasVegas_vin_10000");

        funds.put("SexyDance_vin_100", "SexyDance_vin_100");
        funds.put("SexyDance_vin_1000", "SexyDance_vin_1000");
        funds.put("SexyDance_vin_10000", "SexyDance_vin_10000");

        funds.put("LienMinh_vin_100", "LienMinh_vin_100");
        funds.put("LienMinh_vin_1000", "LienMinh_vin_1000");
        funds.put("LienMinh_vin_10000", "LienMinh_vin_10000");

        funds.put("CANDY_vin_100", "CANDY_vin_100");
        funds.put("CANDY_vin_1000", "CANDY_vin_1000");
        funds.put("CANDY_vin_10000", "CANDY_vin_10000");


        fund2GameName.put("TaiXiu", Games.TAI_XIU.getName());
        fund2GameName.put("TaiXiuMd5", Games.TAI_XIU_MD5.getName());
        fund2GameName.put("XocDia", Games.XOC_DIA.getName());
        fund2GameName.put("BauCuaTo_vin_1000", "BauCuaTo");

        fund2GameName.put("MiniPoker_vin_100", Games.MINI_POKER.getName());
        fund2GameName.put("MiniPoker_vin_1000", Games.MINI_POKER.getName());
        fund2GameName.put("MiniPoker_vin_10000", Games.MINI_POKER.getName());

        fund2GameName.put("CANDY_vin_100", Games.CANDY.getName());
        fund2GameName.put("CANDY_vin_1000", Games.CANDY.getName());
        fund2GameName.put("CANDY_vin_10000", Games.CANDY.getName());

        fund2GameName.put("cao_thap_vin_1000", Games.CAO_THAP.getName());
        fund2GameName.put("cao_thap_vin_10000", Games.CAO_THAP.getName());
        fund2GameName.put("cao_thap_vin_50000", Games.CAO_THAP.getName());
        fund2GameName.put("cao_thap_vin_100000", Games.CAO_THAP.getName());
        fund2GameName.put("cao_thap_vin_500000", Games.MINI_POKER.getName());

        fund2GameName.put("Cowboy_vin_1000", Games.COWBOY.getName());
        fund2GameName.put("Cowboy_vin_10000", Games.COWBOY.getName());
        fund2GameName.put("Cowboy_vin_100", Games.COWBOY.getName());

        fund2GameName.put("FastAndFurious_vin_100", Games.FAST_AND_FURIOUS.getName());
        fund2GameName.put("FastAndFurious_vin_1000", Games.FAST_AND_FURIOUS.getName());
        fund2GameName.put("FastAndFurious_vin_10000", Games.FAST_AND_FURIOUS.getName());

        fund2GameName.put("LadyNight_vin_100", Games.LADY_NIGHT.getName());
        fund2GameName.put("LadyNight_vin_1000", Games.LADY_NIGHT.getName());
        fund2GameName.put("LadyNight_vin_10000", Games.LADY_NIGHT.getName());

        fund2GameName.put("Caribe_vin_100", Games.CARIBE.getName());
        fund2GameName.put("Caribe_vin_1000", Games.CARIBE.getName());
        fund2GameName.put("Caribe_vin_10000", Games.CARIBE.getName());

        fund2GameName.put("BongLaiCac_vin_1000", Games.BONG_LAI_CAC.getName());
        fund2GameName.put("BongLaiCac_vin_10000", Games.BONG_LAI_CAC.getName());
        fund2GameName.put("BongLaiCac_vin_100000", Games.BONG_LAI_CAC.getName());

        fund2GameName.put("Halloween_vin_100", Games.HALLOWEEN.getName());
        fund2GameName.put("Halloween_vin_1000", Games.HALLOWEEN.getName());
        fund2GameName.put("Halloween_vin_10000", Games.HALLOWEEN.getName());

        fund2GameName.put("LasVegas_vin_100", Games.LAS_VEGAS.getName());
        fund2GameName.put("LasVegas_vin_1000", Games.LAS_VEGAS.getName());
        fund2GameName.put("LasVegas_vin_10000", Games.LAS_VEGAS.getName());

        fund2GameName.put("SexyDance_vin_100", Games.SEXY_DANCE.getName());
        fund2GameName.put("SexyDance_vin_1000", Games.SEXY_DANCE.getName());
        fund2GameName.put("SexyDance_vin_10000", Games.SEXY_DANCE.getName());

        fund2GameName.put("LienMinh_vin_100", Games.LIEN_MINH.getName());
        fund2GameName.put("LienMinh_vin_1000", Games.LIEN_MINH.getName());
        fund2GameName.put("LienMinh_vin_10000", Games.LIEN_MINH.getName());
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
            if (funds.containsKey(fundName)) {
                long currentFunValue = cacheService.getValueLong(funds.get(fundName), 0);
                if (type.equals(DEPOSIT)) {
                    currentFunValue += amount;
                } else if (type.equals(WITHDRAW)) {
                    // check if fund value in cache is not enough
                    System.out.println("Nạp/Rút Quỹ: " + currentFunValue + " | " + amount);
                    if (currentFunValue < amount) {
                        response.setSuccess(false);
                        response.setErrorCode("Số tiền rút lớn hơn quỹ");
                        return response.toJson();
                    }
                    currentFunValue -= amount;
                }
                // save fund to cache
                cacheService.setValue(funds.get(fundName), currentFunValue);
                // save to db
                service.saveFund(fundName, currentFunValue);
                // save log
                saveFunLog(fund2GameName.get(fundName), fundName, amount, type);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response.toJson();
    }

    private static void saveFunLog(String gameName, String fundName, long amount, String type) {
        Document document = new Document();
        document.put("game_name", gameName);
        document.put("fund_name", fundName);
        document.put("amount", amount);
        document.put("type", type);
        document.put("time_log", VinPlayUtils.getCurrentDateTime());
        OtherService otherService = new OtherServiceImpl();
        otherService.saveTransactionUpdateFund(document);
    }
}

