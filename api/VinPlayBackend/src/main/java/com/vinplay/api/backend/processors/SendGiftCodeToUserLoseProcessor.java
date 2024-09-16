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
import com.vinplay.dal.dao.impl.ReportDao2Impl;
import com.vinplay.dal.service.impl.ReportMoneyServiceImpl;
import com.vinplay.usercore.service.OtherService;
import com.vinplay.usercore.service.impl.GiftCodeServiceImpl;
import com.vinplay.usercore.service.impl.MailBoxServiceImpl;
import com.vinplay.usercore.service.impl.OtherServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.dto.GiftCodeDto;
import com.vinplay.vbee.common.enums.Games;
import com.vinplay.vbee.common.messages.UserBackCodeMessage;
import com.vinplay.vbee.common.models.TopCaoThu;
import com.vinplay.vbee.common.response.*;
import com.vinplay.vbee.common.rmq.RMQApi;
import com.vinplay.vbee.common.statics.Consts;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import okhttp3.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bson.Document;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class SendGiftCodeToUserLoseProcessor implements BaseProcessor<HttpServletRequest, String> {

    public String execute(Param<HttpServletRequest> param) {
        try {
            HttpServletRequest request = param.get();
            String timeStart = request.getParameter("timeStart");
            String timeEnd = request.getParameter("timeEnd");
            String message = request.getParameter("message");
            OtherService service = new OtherServiceImpl();
            UserCodeReponse userCodeResponse = new UserCodeReponse(true, "200");
            if (service.checkIsSendBackCodeByDay("LOSE", "", timeStart, timeEnd)) {
                userCodeResponse.setErrorCode("Gift code đã gửi");
                userCodeResponse.setSuccess(false);
                return userCodeResponse.toJson();
            }
            long percent = 3;
            try {
                percent = Long.parseLong(request.getParameter("percent"));
            } catch (Exception e) {
                percent = 3;
            }
            return process(message, timeStart, timeEnd, percent);
        } catch (Exception ex) {
            System.out.println(ExceptionUtils.getStackTrace(ex));
            throw new RuntimeException(ex);
        }
    }

    public String process(String message, String timeStart, String timeEnd, long percent) throws Exception {
        try {
            // map nickname => money
            // Get user logs and aggregate their money exchanges
            List<String> actions = Consts.GAMES.stream().filter(s -> !s.equals(Games.HAM_CA_MAP.getName())).collect(Collectors.toList());
            ReportDao2Impl reportDao2 = new ReportDao2Impl();
            List<TopCaoThu> topCaoThuList = reportDao2.topPlayer(null, timeStart, timeStart, actions, 2, 1, 10000000);
            Map<String, Long> topWins = topCaoThuList.stream().collect(Collectors.toMap(TopCaoThu::getNickname, TopCaoThu::getMoneyWin));

            // search fund
            List<UserLoseByDay> userLoseByDays = topWins.entrySet().stream().map(entry -> {
                UserLoseByDay userLoseByDay = new UserLoseByDay();
                userLoseByDay.setNickname(entry.getKey());
                userLoseByDay.setMoney(entry.getValue());
                return userLoseByDay;
            }).collect(Collectors.toList());

            // update status
            OtherService service = new OtherServiceImpl();
            service.updateStatusSendBackCodeByDay("LOSE", "", timeStart, timeEnd);

            userLoseByDays = userLoseByDays.stream()
                    .filter(user -> user.getMoney() <= -100000)
                    .collect(Collectors.toList());

            userLoseByDays.forEach(userLoseByDay -> {
                int giftCodeValue = roundToNearestThousand((int) (userLoseByDay.getMoney() * percent / 100 * -1));
                if (giftCodeValue < 0) {
                    giftCodeValue = giftCodeValue * -1;
                }
                try {
                    // push to rabbitmq
                    UserBackCodeMessage userBackCodeMessage = new UserBackCodeMessage();
                    userBackCodeMessage.backType = 0;
                    userBackCodeMessage.nickname = userLoseByDay.getNickname();
                    userBackCodeMessage.giftValue = giftCodeValue;
                    userBackCodeMessage.money = userLoseByDay.getMoney();
                    userBackCodeMessage.message = message;
                    RMQApi.publishMessage("queue_backcode", userBackCodeMessage, 1502);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            UserCodeReponse userCodeResponse = new UserCodeReponse(true, "200");
            Map<String, Long> filteredUsers = userLoseByDays.stream()
                    .collect(Collectors.toMap(userLoseByDay -> userLoseByDay.getNickname(), userLoseByDay -> userLoseByDay.getMoney()));
            userCodeResponse.setUsers(filteredUsers);
            return userCodeResponse.toJson();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static int roundToNearestThousand(int amount) {
        return (amount / 1000) * 1000;
    }
}

