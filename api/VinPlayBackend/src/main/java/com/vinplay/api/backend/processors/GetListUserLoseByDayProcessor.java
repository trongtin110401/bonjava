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
import com.vinplay.vbee.common.statics.Consts;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import okhttp3.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class GetListUserLoseByDayProcessor implements BaseProcessor<HttpServletRequest, String> {

    public String execute(Param<HttpServletRequest> param) {
        try {
            UserLoseByDayResponse userCodeResponse = new UserLoseByDayResponse(true, "200");
            HttpServletRequest request = param.get();

            String timeStart = request.getParameter("timeStart");
            String timeEnd = request.getParameter("timeEnd");


            // get Fish profit
            OtherService otherService = new OtherServiceImpl();
            List<MoneyShootFishResponse> userFishProfits = otherService.getTotalShootFish(timeStart, timeEnd, null);
            Map<String, Long> mapUserFishProfits = new HashMap<>();
            userFishProfits.forEach(moneyShootFishResponse -> mapUserFishProfits.put(moneyShootFishResponse.getNickname(), moneyShootFishResponse.getTotalProfit()));

            LogMoneyUserDaoImpl dao = new LogMoneyUserDaoImpl();
            List<LogUserMoneyResponse> list = dao.getLogMoneyUser(timeStart, timeEnd);
            // search fund


            List<UserLoseByDay> userLoseByDays = list.stream()
//                .filter(log -> !"Admin".equals(log.getActionName())
//                        && !"Gift Code".equals(log.getActionName())
//                        && !"Gift Code".equals(log.getServiceName())
//                        && !"RechargeByBank".equals(log.getActionName())
//                        && !"RechargeByMomo".equals(log.getActionName())
//                        && !"ChargeSMS".equals(log.getActionName())
//                        && !"CashOutByBank".equals(log.getActionName())
//                        && !"RefundRechargeError".equals(log.getActionName())
//                        && !"CashOutByMomo".equals(log.getActionName())
//                        && !"RechargeByCard".equals(log.getActionName())
//                        && !"RechargeBySMS".equals(log.getActionName())
//                        && !"Exchange".equals(log.getActionName()))
                    .filter(log -> !Consts.NO_GAME.contains(log.getActionName()) && !"Exchange".equals(log.getActionName()))
                    .collect(Collectors.groupingBy(LogUserMoneyResponse::getNickName,
                            Collectors.summingLong(LogUserMoneyResponse::getMoneyExchange)))
                    .entrySet().stream()
                    .filter(entry -> entry.getValue() <= -100000)
                    .map(entry -> {
                        UserLoseByDay userLoseByDay = new UserLoseByDay();
                        userLoseByDay.setNickname(entry.getKey());
                        userLoseByDay.setMoney(entry.getValue());
                        return userLoseByDay;
                    })
                    .collect(Collectors.toList());

            for (Map.Entry<String, Long> entry : mapUserFishProfits.entrySet()) {
                String nickname = entry.getKey();
                Long profit = entry.getValue();
                Optional<UserLoseByDay> optionalUserLoseByDay = userLoseByDays.stream()
                        .filter(userLoseByDay -> userLoseByDay.getNickname().equals(nickname))
                        .findFirst();

                if (optionalUserLoseByDay.isPresent()) {
                    UserLoseByDay userLoseByDay = optionalUserLoseByDay.get();
                    userLoseByDay.setMoney(userLoseByDay.getMoney() + profit * -1);
                } else {
                    UserLoseByDay newUserLoseByDay = new UserLoseByDay();
                    newUserLoseByDay.setNickname(nickname);
                    newUserLoseByDay.setMoney(profit * -1);
                    userLoseByDays.add(newUserLoseByDay);
                }
            }


            userLoseByDays.sort(Comparator.comparingDouble(UserLoseByDay::getMoney));
            userCodeResponse.setUsers(userLoseByDays);
            userCodeResponse.setTotalRecord(userLoseByDays.size());

            return userCodeResponse.toJson();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}

