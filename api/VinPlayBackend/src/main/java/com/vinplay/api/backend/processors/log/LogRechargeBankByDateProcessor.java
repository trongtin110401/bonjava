/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.dichvuthe.dao.impl.RechargeDaoImpl
 *  com.vinplay.usercore.response.LogRechargeBankNapasResponse
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.log4j.Logger
 */
package com.vinplay.api.backend.processors.log;

import com.vinplay.dichvuthe.dao.impl.RechargeDaoImpl;
import com.vinplay.dichvuthe.entities.DepositBankModel;
import com.vinplay.dichvuthe.entities.DepositBankReponse;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

public class LogRechargeBankByDateProcessor
implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String)"backend");

    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        DepositBankReponse res = new DepositBankReponse(false, "1001");
        try {
            String nickname = request.getParameter("nn");
            String bank = request.getParameter("b");
            String transId = request.getParameter("tid");
            String status = request.getParameter("st");
            String startTime = request.getParameter("ts");
            String endTime = request.getParameter("te");
            RechargeDaoImpl dao = new RechargeDaoImpl();
            DepositBankModel modelSearch = new DepositBankModel(transId, nickname, status, bank);
            res = dao.GetListDepositBankExport(modelSearch, startTime, endTime);

        }
        catch (Exception e) {
            logger.debug((Object)e);
        }
        return res.toJson();
    }
}

