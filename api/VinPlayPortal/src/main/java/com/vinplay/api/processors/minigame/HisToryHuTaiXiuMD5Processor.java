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

import com.vinplay.api.processors.minigame.response.HistoryHuTXResponse;
import com.vinplay.dal.entities.taixiu.NohuTXDetail;
import com.vinplay.dal.service.impl.TaiXiuMd5ServiceImpl;
import com.vinplay.dal.service.impl.TaiXiuServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.List;

public class HisToryHuTaiXiuMD5Processor
        implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        HistoryHuTXResponse response = new HistoryHuTXResponse(false, "1001");
        HttpServletRequest request = (HttpServletRequest)param.get();
        int page = Integer.parseInt(request.getParameter("p"));
        if (page < 0) {
            return response.toJson();
        }
        try {
            TaiXiuMd5ServiceImpl service = new TaiXiuMd5ServiceImpl();
            List<NohuTXDetail> transaction = service.getHistoryNoHuTX(page);
            response.setNohuTXDetails(transaction);
            response.setSuccess(true);
            response.setErrorCode("0");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e);
        }

        return response.toJson();
    }
}

