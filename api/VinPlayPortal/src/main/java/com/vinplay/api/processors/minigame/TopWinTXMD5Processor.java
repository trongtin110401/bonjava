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
import com.vinplay.dal.dao.TaiXiuDAO;
import com.vinplay.dal.dao.impl.TaiXiuDAOImpl;
import com.vinplay.dal.service.impl.OverUnderServiceImpl;
import com.vinplay.dal.service.impl.TaiXiuMd5ServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.models.minigame.TopWin;
import com.vinplay.vbee.common.rmq.ELKrmq;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class TopWinTXMD5Processor implements BaseProcessor<HttpServletRequest, String> {
    public static long requestCache = new Date().getTime();
    public static String listBankData = "";

    public synchronized String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = param.get();
        int moneyType = Integer.parseInt(request.getParameter("mt"));
        String txType = request.getParameter("txType");
        String userCurrent = request.getParameter("userCurrent");
        TopWinTXResponse response = new TopWinTXResponse(false, "1001");
        long currentTime = new Date().getTime();
        long timeDiff = currentTime - requestCache;
        if (timeDiff <= 30000 && !listBankData.isEmpty()) {
            return listBankData;
        } else {
            if (txType == null || txType.equals("1")) {
                TaiXiuMd5ServiceImpl service = new TaiXiuMd5ServiceImpl();
                try {
                    List<TopWin> result = service.getTopWin(moneyType, userCurrent);
                    response.setTopTX(result);
                    //old mysql
//                response.setTotalMoneyStakesMine(dao.getMoneyStakesTX(userCurrent));
                    //new elk
                    ELKrmq elKrmq = new ELKrmq();
                    Long totalStakesTx = elKrmq.getTotalStakesTx(userCurrent, atStartOfDay(new Date()).getTime(), atEndOfDay(new Date()).getTime());

                    response.setTotalMoneyStakesMine(totalStakesTx);
                    //end

                    response.setSuccess(true);
                    response.setErrorCode("0");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                OverUnderServiceImpl service = new OverUnderServiceImpl();
                try {
                    List result = service.getTopWin(moneyType);
                    response.setTopTX(result);
                    response.setSuccess(true);
                    response.setErrorCode("0");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            String data = response.toJson();

            requestCache = new Date().getTime();
            listBankData = data;
            return data;
        }
    }

    public static Date atStartOfDay(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        return localDateTimeToDate(startOfDay);
    }

    public static Date atEndOfDay(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
        return localDateTimeToDate(endOfDay);
    }

    private static LocalDateTime dateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    private static Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}

