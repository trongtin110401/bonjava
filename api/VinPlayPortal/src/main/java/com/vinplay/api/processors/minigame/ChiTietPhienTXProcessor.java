/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.vinplay.dal.entities.taixiu.ResultTaiXiu
 *  com.vinplay.dal.entities.taixiu.TransactionTaiXiuDetail
 *  com.vinplay.dal.service.impl.TaiXiuServiceImpl
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  javax.servlet.http.HttpServletRequest
 */
package com.vinplay.api.processors.minigame;

import com.vinplay.api.processors.minigame.response.ChiTietPhienTXResponse;
import com.vinplay.api.processors.minigame.response.TopWinTXResponse;
import com.vinplay.dal.entities.taixiu.ResultTaiXiu;
import com.vinplay.dal.entities.taixiu.TransactionTaiXiuDetail;
import com.vinplay.dal.service.impl.OverUnderServiceImpl;
import com.vinplay.dal.service.impl.TaiXiuServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import java.io.PrintStream;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

public class ChiTietPhienTXProcessor
        implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        ChiTietPhienTXResponse response = new ChiTietPhienTXResponse(false, "1001");
        HttpServletRequest request = (HttpServletRequest) param.get();
        long referenceId = Long.parseLong(request.getParameter("rid"));
        int moneyType = Integer.parseInt(request.getParameter("mt"));
        String txType = request.getParameter("txType");

        if (txType == null || txType.equals("1")) {
            try {
                TaiXiuServiceImpl service = new TaiXiuServiceImpl();
                List transaction = service.getChiTietPhienTX(referenceId, moneyType);
                response.setTransactions(transaction);
                ResultTaiXiu resultTX = service.getKetQuaPhien(referenceId, moneyType);
                response.setResultTX(resultTX);
                response.setSuccess(true);
                response.setErrorCode("0");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println(e);
            }
        } else {
            try {
                OverUnderServiceImpl service = new OverUnderServiceImpl();
                List transaction = service.getChiTietPhienTX(referenceId, moneyType);
                response.setTransactions(transaction);
                ResultTaiXiu resultTX = service.getKetQuaPhien(referenceId, moneyType);
                response.setResultTX(resultTX);
                response.setSuccess(true);
                response.setErrorCode("0");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println(e);
            }
        }
        return response.toJson();
    }
}

