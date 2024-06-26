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

import com.vinplay.api.processors.minigame.response.TopWinTXResponse;
import com.vinplay.dal.service.impl.TaiXiuServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.models.minigame.TopWin;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TopWinTXVinhDanhForAdminProcessor
        implements BaseProcessor<HttpServletRequest, String> {

    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = param.get();
        String beginTime = request.getParameter("beginTime");
        String endTime = request.getParameter("endTime");
        String limitNumber = request.getParameter("limitNumber");
        TopWinTXResponse response = new TopWinTXResponse(false, "1001");
        TaiXiuServiceImpl service = new TaiXiuServiceImpl();
        try {
            List<TopWin> result = service.getTopTxVinhDanhForAdmin(1, forMatTime(beginTime).toInstant(), forMatTime(endTime).toInstant(), Integer.parseInt(limitNumber));
            response.setTopTX(result);
            response.setTotalMoneyStakesMine(0L);
            response.setSuccess(true);
            response.setErrorCode("0");
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
        return response.toJson();
    }

    private Timestamp forMatTime(String time) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        Date parsedDate = dateFormat.parse(time);
        return new java.sql.Timestamp(parsedDate.getTime());
    }
}

