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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GetListUserLoseByDayProcessor implements BaseProcessor<HttpServletRequest, String> {

    public String execute(Param<HttpServletRequest> param) {
        try {
            UserLoseByDayResponse userCodeResponse = new UserLoseByDayResponse(true, "200");
            HttpServletRequest request = param.get();

            String timeStart = request.getParameter("timeStart");
            String timeEnd = request.getParameter("timeEnd");
            int pageIndex = getParameter(request, "pageIndex", 1);
            int pageSize = getParameter(request, "pageSize", 50);

            // Get Fish profit and map by nickname
            OtherService otherService = new OtherServiceImpl();
            Map<String, Long> mapUserFishProfits = otherService.getTotalShootFish(timeStart, timeEnd, null)
                    .stream()
                    .collect(Collectors.toMap(
                            MoneyShootFishResponse::getNickname,
                            MoneyShootFishResponse::getTotalProfit,
                            Long::sum)); // Merge profits in case of duplicate nicknames

            // Get user logs and aggregate their money exchanges
            LogMoneyUserDaoImpl dao = new LogMoneyUserDaoImpl();
            Map<String, Long> userMoneyMap = dao.getLogMoneyUser(timeStart, timeEnd).stream()
                    .filter(log -> !Consts.NO_GAME.contains(log.getActionName()) && !"Exchange".equals(log.getActionName()))
                    .collect(Collectors.groupingBy(
                            LogUserMoneyResponse::getNickName,
                            Collectors.summingLong(LogUserMoneyResponse::getMoneyExchange)));

            // Adjust user money with fish profits using `Map.merge()`
            mapUserFishProfits.forEach((nickname, profit) -> {
                if (profit != 0) {
                    userMoneyMap.merge(nickname, -profit, Long::sum); // Subtract profit from user money
                }
            });
            // Convert map entries to UserLoseByDay and sort by money
            List<UserLoseByDay> userLoseByDays = userMoneyMap.entrySet().stream()
                    .map(entry -> {
                        UserLoseByDay userLoseByDay = new UserLoseByDay();
                        userLoseByDay.setNickname(entry.getKey());
                        userLoseByDay.setMoney(entry.getValue());
                        return userLoseByDay;
                    })
                    .filter(user -> user.getMoney() <= -100000) // Apply filtering
                    .sorted(Comparator.comparingDouble(UserLoseByDay::getMoney)) // Sort by money
                    .collect(Collectors.toList());

            // Apply pagination
            int start = (pageIndex - 1) * pageSize;
            int end = Math.min(start + pageSize, userLoseByDays.size());
            List<UserLoseByDay> paginatedList = userLoseByDays.subList(start, end);

            // Set response
            userCodeResponse.setUsers(paginatedList);
            userCodeResponse.setTotalRecord(userLoseByDays.size());
            userCodeResponse.setPageIndex(pageIndex);
            userCodeResponse.setPageSize(pageSize);
            int totalPage = (int) Math.ceil((double) userLoseByDays.size() / pageSize);
            userCodeResponse.setTotalPage(totalPage);
            return userCodeResponse.toJson();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private int getParameter(HttpServletRequest request, String name, int defaultValue) {
        try {
            return Integer.parseInt(request.getParameter(name));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }


}

