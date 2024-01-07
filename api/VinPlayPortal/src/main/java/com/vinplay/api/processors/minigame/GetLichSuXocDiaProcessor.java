/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.vinplay.dal.service.impl.TaiXiuServiceImpl
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.models.minigame.TopWin
 *  javax.servlet.http.HttpServletRequest
 */
package com.vinplay.api.processors.minigame;

import com.vinplay.api.processors.minigame.response.LichSuGiaoDichXocDiaResponse;
import com.vinplay.dal.entities.taixiu.BetResult;
import com.vinplay.dal.entities.taixiu.TransactionXocDia;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.utils.DateTimeUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class GetLichSuXocDiaProcessor
        implements BaseProcessor<HttpServletRequest, String> {
    private static final Object BY_DAY = "byDay";

    public String execute(Param<HttpServletRequest> param) {
        LichSuGiaoDichXocDiaResponse response = new LichSuGiaoDichXocDiaResponse(false, "1001");
        HttpServletRequest request = param.get();
        String username = request.getParameter("un");
        int page = Integer.parseInt(request.getParameter("p"));
        if (page < 0) {
            return response.toJson();
        }
        response = getLichSuXocDia();
        return response.toJson();
    }

    public LichSuGiaoDichXocDiaResponse getLichSuXocDia() {
        LichSuGiaoDichXocDiaResponse response = new LichSuGiaoDichXocDiaResponse(true, "0");
        List<TransactionXocDia> transactionXocDias = new ArrayList<>();

        TransactionXocDia transactionXocDia1 = new TransactionXocDia();
        transactionXocDia1.setReferenceId(1);
        transactionXocDia1.setUserId(1);
        transactionXocDia1.setUsername("user");
        transactionXocDia1.setTotalPrize(5000);
        transactionXocDia1.setTimestamp(DateTimeUtils.getCurrentTime("yyyy-MM-dd HH:mm:ss"));
        transactionXocDia1.setTotalExchange(100000);

        BetResult betResult1 = new BetResult();
        betResult1.setZeroWhite(500);
        betResult1.setOneWhite(3000);
        betResult1.setThreeWhite(2000);
        betResult1.setFourWhite(6000);
        betResult1.setEven(1000);
        betResult1.setOdd(5000);
        transactionXocDia1.setResult("threeWhite");
        transactionXocDia1.setBetResult(betResult1);


        TransactionXocDia transactionXocDia2 = new TransactionXocDia();
        transactionXocDia2.setReferenceId(1);
        transactionXocDia2.setUserId(1);
        transactionXocDia2.setUsername("user");
        transactionXocDia2.setTotalPrize(20000);
        transactionXocDia2.setTimestamp(DateTimeUtils.getCurrentTime("yyyy-MM-dd HH:mm:ss"));
        transactionXocDia2.setTotalExchange(100000);

        BetResult betResult2 = new BetResult();
        betResult2.setZeroWhite(6000);
        betResult2.setOneWhite(30000);
        betResult2.setThreeWhite(62000);
        betResult2.setFourWhite(64000);
        betResult2.setEven(16000);
        betResult2.setOdd(95000);
        transactionXocDia1.setResult("oneWhite");
        transactionXocDia2.setBetResult(betResult2);

        transactionXocDias.add(transactionXocDia1);
        transactionXocDias.add(transactionXocDia2);
        response.setTransactions(transactionXocDias);
        response.setTotalPages(2);
        return response;
    }

}

