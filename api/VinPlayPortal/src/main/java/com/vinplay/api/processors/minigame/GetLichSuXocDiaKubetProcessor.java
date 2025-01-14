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
import com.vinplay.dal.entities.taixiu.TransactionXocDia;
import com.vinplay.usercore.dao.impl.XocDiaDaoImpl;
import com.vinplay.usercore.dao.impl.XocDiaKubetDaoImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class GetLichSuXocDiaKubetProcessor
        implements BaseProcessor<HttpServletRequest, String> {

    public String execute(Param<HttpServletRequest> param) {
        LichSuGiaoDichXocDiaResponse response = new LichSuGiaoDichXocDiaResponse(false, "1001");
        System.out.println("===> 1");
        HttpServletRequest request = param.get();
        String username = request.getParameter("un");
        int page = Integer.parseInt(request.getParameter("p"));
        if (page < 0) {
            System.out.println("===> 2");
            return response.toJson();
        }
        System.out.println("===> 3");
        return getLichSuXocDia(username, page).toJson();
    }

    public LichSuGiaoDichXocDiaResponse getLichSuXocDia(String username, int page) {
        System.out.println("===> 4");
        LichSuGiaoDichXocDiaResponse response = new LichSuGiaoDichXocDiaResponse(true, "0");

        XocDiaKubetDaoImpl xocDiaDao = new XocDiaKubetDaoImpl();
        List<TransactionXocDia> transactionXocDias = xocDiaDao.getLichSuXocDia(username, page);
        int totalRecord = xocDiaDao.countLichSuXocDia(username);
        int totalPages = (int) Math.ceil((double) totalRecord / 10);
        response.setSuccess(true);
        response.setErrorCode("0");
        response.setTotalPages(totalPages);
        response.setTransactions(transactionXocDias);

        System.out.println("===> 5");
        return response;
    }

}

