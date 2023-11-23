/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.vinplay.dal.service.impl.CashOutByCardServiceImpl
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.response.ResultCashOutByCardResponse
 *  javax.servlet.http.HttpServletRequest
 */
package com.vinplay.api.backend.processors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinplay.dal.service.impl.CashOutByCardServiceImpl;
import com.vinplay.dichvuthe.dao.CashoutDao;
import com.vinplay.dichvuthe.dao.impl.CashoutDaoImpl;
import com.vinplay.dichvuthe.entities.CashoutBankResponse;
import com.vinplay.dichvuthe.entities.CashoutCardResponse;
import com.vinplay.dichvuthe.utils.CashoutUtil;
import com.vinplay.payment.entities.UserWithDrawCard;
import com.vinplay.payment.entities.UserWithdraw;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.ResultCashOutByBankResponse;
import com.vinplay.vbee.common.response.ResultCashOutByCardResponse;
import org.apache.log4j.Logger;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

public class CashOutByCardProcessor
        implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String) "backend");
    private static final int MAX_ITEM = 15;

    public String execute(Param<HttpServletRequest> param) {
        ResultCashOutByBankResponse response = new ResultCashOutByBankResponse(false, "1001");
        try {
            HttpServletRequest request = (HttpServletRequest) param.get();
            String nickName = request.getParameter("nn");
            String status = request.getParameter("st");
            String telco = request.getParameter("telco");
            String timeStart = request.getParameter("ts");
            String timeEnd = request.getParameter("te");
            String pageStr = request.getParameter("p");
            int page = (pageStr == null || pageStr.isEmpty()) ? 1 : Integer.parseInt(pageStr);
            String numberMax = request.getParameter("max_item");
            int maxItem = numberMax != null ? Integer.parseInt(numberMax) : MAX_ITEM;
            String transid = request.getParameter("tid");
            CashoutDao cashoutDao = new CashoutDaoImpl();
            UserWithDrawCard userWithdraw = new UserWithDrawCard(transid, nickName, telco, status);
            CashoutCardResponse res = cashoutDao.GetListCashoutCard(userWithdraw, page, maxItem, timeEnd, timeStart);
            return res.toJson();

        } catch (Exception e) {
            logger.debug(e.getMessage());
            return response.toJson();
        }


    }
}

