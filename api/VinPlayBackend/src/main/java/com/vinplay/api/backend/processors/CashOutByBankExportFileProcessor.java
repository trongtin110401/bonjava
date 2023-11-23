/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.vinplay.dal.service.impl.CashOutByBankServiceImpl
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.response.ResultCashOutByBankResponse
 *  javax.servlet.http.HttpServletRequest
 */
package com.vinplay.api.backend.processors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinplay.dal.common.BroadCastUserMoney;
import com.vinplay.dichvuthe.dao.CashoutDao;
import com.vinplay.dichvuthe.dao.impl.CashoutDaoImpl;
import com.vinplay.dichvuthe.entities.CashoutBankResponse;
import com.vinplay.dichvuthe.utils.CashoutUtil;
import com.vinplay.lognaprut.HistoryTransConst;
import com.vinplay.lognaprut.service.HistoryTransService;
import com.vinplay.lognaprut.service.impl.HistoryTransServiceImpl;
import com.vinplay.payment.entities.UserWithdraw;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.ResultCashOutByBankResponse;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

public class CashOutByBankExportFileProcessor
        implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String) "backend");

    public String execute(Param<HttpServletRequest> param) {
        ResultCashOutByBankResponse response = new ResultCashOutByBankResponse(false, "1001");
        try {
            HttpServletRequest request = (HttpServletRequest) param.get();
            String nickName = request.getParameter("nn");
            String bankName = request.getParameter("b");
            String bankAccountNumber = request.getParameter("bankNumber");
            String bankAccountAccountName = request.getParameter("bankAccountAccountName");
            String userAprrove = request.getParameter("uad");
            userAprrove = userAprrove == null ? "" : userAprrove;
            String status = request.getParameter("st");
            String code = request.getParameter("co");
            String timeStart = request.getParameter("ts");
            String timeEnd = request.getParameter("te");
            String pageStr = request.getParameter("p");
            int page = (pageStr == null || pageStr.isEmpty()) ? 1 : Integer.parseInt(pageStr);
            HistoryTransService historyTransService = new HistoryTransServiceImpl();
            String transid = request.getParameter("tid");
            String act = request.getParameter("action");
            act = act == null || act.isEmpty() ? "getList" : act;
            CashoutDao cashoutDao = new CashoutDaoImpl();
            if (act.equals("getList")) {

                UserWithdraw userWithdraw = new UserWithdraw(transid, nickName, bankAccountNumber, bankAccountAccountName, bankName, status);
                CashoutBankResponse res = cashoutDao.GetListCashoutBankExportFile(userWithdraw, timeEnd, timeStart);

                return res.toJson();
            }

            return "";

        } catch (Exception e) {
            logger.debug(e.getMessage());
            return response.toJson();
        }


    }
}

