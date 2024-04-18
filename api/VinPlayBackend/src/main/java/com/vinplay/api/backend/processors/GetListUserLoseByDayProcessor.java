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

        // L?y danh sách LogUserMoneyResponse
        List<LogUserMoneyResponse> list = dao.getLogMoneyUser(timeStart, timeEnd);

        // Tính t?ng ti?n m?t c?a t?ng ng??i và l?c ra nh?ng ng??i m?t ti?n
        List<UserLoseByDay> userLoseByDays = list.stream()
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

        return userCodeResponse.toJson();
    }
}

