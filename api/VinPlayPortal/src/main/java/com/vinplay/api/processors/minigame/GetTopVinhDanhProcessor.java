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

import com.vinplay.api.processors.minigame.response.TopVinhDanhResponse;
import com.vinplay.api.processors.minigame.response.TopWinTXResponse;
import com.vinplay.dal.dao.TaiXiuDAO;
import com.vinplay.dal.dao.impl.TaiXiuDAOImpl;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class GetTopVinhDanhProcessor
        implements BaseProcessor<HttpServletRequest, String> {
    private static final Object BY_DAY = "byDay";

    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = param.get();
//        int moneyType = Integer.parseInt(request.getParameter("mt"));
//        String txType = request.getParameter("txType");
//        String userCurrent = request.getParameter("userCurrent");
//        String typeVinhDanh = request.getParameter("typeVinhDanh");
        TopVinhDanhResponse response = new TopVinhDanhResponse(true, "0");
        response.setListVinhDanh(getListTopWin(""));
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

    public List<TopWin> getListTopWin(String gameName) {
        List<TopWin> response = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            TopWin topWin = new TopWin();
            topWin.setUsername("user_" + i);
            topWin.setMoney(10000000);
            response.add(topWin);
        }

        return response;

    }
}

