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
import com.vinplay.dal.service.impl.TaiXiuMd5ServiceImpl;
import com.vinplay.dal.service.impl.TaiXiuServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.models.minigame.TopWin;
import com.vinplay.vbee.common.rmq.ELKrmq;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class TopWinTXMD5VinhDanhProcessor implements BaseProcessor<HttpServletRequest, String> {
    private static final Object BY_DAY = "byDay";
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request =  param.get();
        int moneyType = Integer.parseInt(request.getParameter("mt"));
        String txType = request.getParameter("txType");
        String userCurrent = request.getParameter("userCurrent");
        String typeVinhDanh = request.getParameter("typeVinhDanh");
        TopWinTXResponse response = new TopWinTXResponse(false, "1001");
        if ((txType == null || txType.equals("1")) && typeVinhDanh != null) {
            TaiXiuMd5ServiceImpl service = new TaiXiuMd5ServiceImpl();
            try {
                ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");
                LocalDate today = LocalDate.now(zoneId);
                List<TopWin> result = Objects.equals(typeVinhDanh, BY_DAY) ? service.getTopWinVinhDanhByDay(moneyType, userCurrent) : service.getTopWinVinhDanhByMonth(moneyType, userCurrent);
                response.setTopTX(result);
                //update new elk
                ELKrmq elKrmq = new ELKrmq();
                Long totalStakesTx = elKrmq.getTotalStakesTx(userCurrent, atStartOfDay(new Date()).getTime(),atEndOfDay(new Date()).getTime());
                Long totalStakesTxMonth = elKrmq.getTotalStakesTx(userCurrent, atFirstOfDayOfMonth(new Date()).getTime(),atLastOfDayOfMonth(new Date()).getTime());
                response.setTotalMoneyStakesMine(Objects.equals(typeVinhDanh, BY_DAY) ? totalStakesTx : totalStakesTxMonth);

                response.setSuccess(true);
                response.setErrorCode("0");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return response.toJson();
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

    public static Date atFirstOfDayOfMonth(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date).with(TemporalAdjusters.firstDayOfMonth());
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        return localDateTimeToDate(startOfDay);
    }

    public static Date atLastOfDayOfMonth(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date).with(TemporalAdjusters.lastDayOfMonth());
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MAX);
        return localDateTimeToDate(startOfDay);
    }
}

