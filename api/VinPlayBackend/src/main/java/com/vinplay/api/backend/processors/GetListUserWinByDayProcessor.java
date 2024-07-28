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
import com.vinplay.usercore.service.impl.OtherServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.LogUserMoneyResponse;
import com.vinplay.vbee.common.response.MoneyShootFishResponse;
import com.vinplay.vbee.common.response.UserLoseByDay;
import com.vinplay.vbee.common.response.UserLoseByDayResponse;
import com.vinplay.vbee.common.statics.Consts;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GetListUserWinByDayProcessor implements BaseProcessor<HttpServletRequest, String> {

    public String execute(Param<HttpServletRequest> param) {

        try {
            UserLoseByDayResponse userCodeResponse = new UserLoseByDayResponse(true, "200");
            HttpServletRequest request = param.get();
            String timeStart = request.getParameter("timeStart");
            String timeEnd = request.getParameter("timeEnd");

            // get Fish profit
            OtherService otherService = new OtherServiceImpl();
            List<MoneyShootFishResponse> userFishProfits = otherService.getTotalShootFish(timeStart, timeEnd);
            Map<String, Long> mapUserFishProfits = new HashMap<>();
            userFishProfits.forEach(moneyShootFishResponse -> mapUserFishProfits.put(moneyShootFishResponse.getNickname(), moneyShootFishResponse.getTotalProfit()));

            LogMoneyUserDaoImpl dao = new LogMoneyUserDaoImpl();
            List<LogUserMoneyResponse> list = dao.getLogMoneyUser(timeStart, timeEnd);

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
                    .filter(log -> !Consts.NO_GAME.contains(log.getActionName()))
                    .collect(Collectors.groupingBy(LogUserMoneyResponse::getNickName,
                            Collectors.summingLong(LogUserMoneyResponse::getMoneyExchange)))
                    .entrySet().stream()
                    .filter(entry -> entry.getValue() >= 100000)
                    .map(entry -> {
                        UserLoseByDay userLoseByDay = new UserLoseByDay();
                        userLoseByDay.setNickname(entry.getKey());
                        userLoseByDay.setMoney(entry.getValue());
                        return userLoseByDay;
                    })
                    .collect(Collectors.toList());

            userLoseByDays.forEach(userLoseByDay -> {
                if (mapUserFishProfits.containsKey(userLoseByDay.getNickname())) {
                    userLoseByDay.setMoney(userLoseByDay.getMoney() + (mapUserFishProfits.get(userLoseByDay.getNickname()) * -1));
                }
            });

            userCodeResponse.setUsers(userLoseByDays);
            userCodeResponse.setTotalRecord(userLoseByDays.size());

            return userCodeResponse.toJson();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}

