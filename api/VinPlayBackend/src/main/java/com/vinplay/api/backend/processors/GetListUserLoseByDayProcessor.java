/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.vinplay.usercore.service.impl.GiftCodeServiceImpl
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.response.GiftCodeSearchResponse
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.log4j.Logger
 */
package com.vinplay.api.backend.processors;

import com.vinplay.dal.dao.impl.LogMoneyUserDaoImpl;
import com.vinplay.usercore.service.OtherService;
import com.vinplay.usercore.service.impl.GiftCodeServiceImpl;
import com.vinplay.usercore.service.impl.OtherServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.dto.GiftCodeDto;
import com.vinplay.vbee.common.response.*;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import okhttp3.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class GetListUserLoseByDayProcessor implements BaseProcessor<HttpServletRequest, String> {

    public String execute(Param<HttpServletRequest> param) {
        UserLoseByDayResponse userCodeResponse = new UserLoseByDayResponse(true, "200");
        HttpServletRequest request = param.get();

        String timeStart = request.getParameter("timeStart");
        String timeEnd = request.getParameter("timeEnd");
        LogMoneyUserDaoImpl dao = new LogMoneyUserDaoImpl();

        List<LogUserMoneyResponse> list = dao.getLogMoneyUser2(timeStart, timeEnd);

        List<UserLoseByDay> userLoseByDays = list.stream()
                .filter(log -> !"Admin".equals(log.getActionName())
                        && !"Gift Code".equals(log.getActionName())
                        && !"Gift Code".equals(log.getServiceName())
                        && !"RechargeByBank".equals(log.getActionName())
                        && !"RechargeByMomo".equals(log.getActionName())
                        && !"ChargeSMS".equals(log.getActionName())
                        && !"CashOutByBank".equals(log.getActionName())
                        && !"CashOutByMomo".equals(log.getActionName())
                        && !"RechargeByCard".equals(log.getActionName())
                        && !"RechargeBySMS".equals(log.getActionName())
                        && !"Exchange".equals(log.getActionName()))
                .collect(Collectors.groupingBy(LogUserMoneyResponse::getNickName,
                        Collectors.summingLong(LogUserMoneyResponse::getMoneyExchange)))
                .entrySet().stream()
                .filter(entry -> entry.getValue() < 0)
                .map(entry -> {
                    UserLoseByDay userLoseByDay = new UserLoseByDay();
                    userLoseByDay.setNickname(entry.getKey());
                    userLoseByDay.setMoney(entry.getValue());
                    return userLoseByDay;
                })
                .collect(Collectors.toList());

        userCodeResponse.setUsers(userLoseByDays);
        userCodeResponse.setTotalRecord(userLoseByDays.size());

        return userCodeResponse.toJson();
    }
}

