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
import com.vinplay.usercore.service.impl.UserInfoServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.exceptions.KeyNotFoundException;
import com.vinplay.vbee.common.response.FundInfoResponse;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class GetInfoFundProcessor
        implements BaseProcessor<HttpServletRequest, String> {

    public String execute(Param<HttpServletRequest> param) throws KeyNotFoundException {
        FundInfoResponse response = new FundInfoResponse(true, "200");
        CacheService cacheService = new CacheServiceImpl();
        try {
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

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response.toJson();
    }
}

