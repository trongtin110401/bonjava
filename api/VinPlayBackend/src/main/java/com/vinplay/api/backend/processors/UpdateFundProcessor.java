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
import com.vinplay.usercore.service.OtherService;
import com.vinplay.usercore.service.impl.OtherServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.exceptions.KeyNotFoundException;
import com.vinplay.vbee.common.response.FundInfoResponse;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.bson.Document;

import javax.servlet.http.HttpServletRequest;

public class UpdateFundProcessor
        implements BaseProcessor<HttpServletRequest, String> {

    private final static String DEPOSIT = "deposit";

    private final static String WITHDRAW = "withdraw";

    public String execute(Param<HttpServletRequest> param) {
        FundInfoResponse response = new FundInfoResponse(true, "200");

        HttpServletRequest request = param.get();

        String fundName = request.getParameter("fundName");
        int amount = Integer.parseInt(request.getParameter("amount"));
        String type = request.getParameter("type");

        CacheService cacheService = new CacheServiceImpl();
        try {

            if (cacheService.getValueStr(fundName) != null) {
                long fund = cacheService.getValueInt(fundName);

                if (type.equals(DEPOSIT)) {
                    fund += amount;
                }
                if (type.equals(WITHDRAW)) {
                    fund -= amount;
                }
                cacheService.setValue(fundName, (int) fund);
            }

            if (cacheService.getValueStr("hu_tx_auto") != null) {
                response.setFundTaiXiu(cacheService.getValueInt("hu_tx_auto"));
            }
            if (cacheService.getValueStr("hu_tx_auto_md5") != null) {
                response.setFundTaiXiuMd5(cacheService.getValueInt("hu_tx_auto_md5"));
            }
            if (cacheService.getValueStr("hu_xd_auto") != null) {
                response.setFundXocDia(cacheService.getValueInt("hu_xd_auto"));
            }
            if (cacheService.getValueStr("hu_bc_auto") != null) {
                response.setFundBauCua(cacheService.getValueInt("hu_bc_auto"));
            }

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

