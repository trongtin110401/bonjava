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

import com.vinplay.api.backend.processors.report.ReportTopGameProcessor2;
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
import com.vinplay.vbee.common.response.UserCodeReponse;
import com.vinplay.vbee.common.response.UserLoseByDay;
import com.vinplay.vbee.common.rmq.RMQApi;
import com.vinplay.vbee.common.statics.Consts;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SendGiftCodeToUserWinProcessor implements BaseProcessor<HttpServletRequest, String> {

    public String execute(Param<HttpServletRequest> param) {
        try {
            HttpServletRequest request = param.get();
            String timeStart = request.getParameter("timeStart");
            String timeEnd = request.getParameter("timeEnd");
            String message = request.getParameter("message");
            String nickname = request.getParameter("nickname");

            OtherService service = new OtherServiceImpl();
            UserCodeReponse userCodeResponse = new UserCodeReponse(true, "200");
            if (service.checkIsSendBackCodeByDay("WIN", nickname, timeStart, timeEnd)) {
                userCodeResponse.setErrorCode("Gift code đã gửi");
                userCodeResponse.setSuccess(false);
                return userCodeResponse.toJson();
            }

            int price;
            try {
                price = Integer.parseInt(request.getParameter("price"));
            } catch (Exception e) {
                price = 50000;
            }
            price = roundToNearestThousand(price);
            return process(nickname, message, timeStart, timeEnd, price);
        } catch (Exception ex) {
            System.out.println(ExceptionUtils.getStackTrace(ex));
            throw new RuntimeException(ex);
        }
    }

    public static int roundToNearestThousand(int amount) {
        return (amount / 1000) * 1000;
    }

    public String process(String nickname, String message, String timeStart, String timeEnd, int price) throws Exception {
        try {
            // map nickname => money
            // Get user logs and aggregate their money exchanges
            List<String> actions = Consts.GAMES.stream().filter(s -> !s.equals(Games.HAM_CA_MAP.getName())).collect(Collectors.toList());
            ReportDao2Impl reportDao2 = new ReportDao2Impl();
            List<TopCaoThu> topCaoThuList = reportDao2.topPlayer(nickname, timeStart, timeStart, actions, 1, 1, 10000000);

            Map<String, Long> topWins = topCaoThuList.stream().collect(Collectors.toMap(TopCaoThu::getNickname, TopCaoThu::getMoneyWin));
            List<UserLoseByDay> userLoseByDays = topWins.entrySet().stream().map(entry -> {
                UserLoseByDay userLoseByDay = new UserLoseByDay();
                userLoseByDay.setNickname(entry.getKey());
                userLoseByDay.setMoney(entry.getValue());
                return userLoseByDay;
            }).collect(Collectors.toList());

            OtherService service = new OtherServiceImpl();
            service.updateStatusSendBackCodeByDay("WIN", "", timeStart, timeEnd);

            userLoseByDays.forEach(userLoseByDay -> {
                try {
                    String giftCode = VinPlayUtils.genGiftCode(10);
                    String content = message + " : " + genCode(price, giftCode);

                    // push to rabbitmq
                    UserBackCodeMessage userBackCodeMessage = new UserBackCodeMessage();
                    userBackCodeMessage.backType = 1;
                    userBackCodeMessage.nickname = userLoseByDay.getNickname();
                    userBackCodeMessage.giftValue = price;
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

    // Ph??ng th?c ?? chuy?n ??i chu?i th�nh Date
    public static Date parseDate(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String genCode(int price, String giftCode) {
        LocalDate currentDate = LocalDate.now();
        LocalDate newDate = currentDate.plusDays(10);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String expirationTime = newDate.format(formatter);
        String createdDate = currentDate.format(formatter);
        GiftCodeServiceImpl service = new GiftCodeServiceImpl();
        GiftCodeDto giftCodeDto = new GiftCodeDto();
        giftCodeDto.setType(createdDate);
        giftCodeDto.setPrice(price);
        giftCodeDto.setQuantity(1);
        giftCodeDto.setLength(10);
        giftCodeDto.setCreatedDate(createdDate);
        giftCodeDto.setExpirationTime(expirationTime);
        giftCodeDto.setCode(giftCode);
        giftCodeDto.setActive(true);
        giftCodeDto.setExpirationDate(3);
        service.saveGiftCode(giftCodeDto);
        return giftCode;
    }

}

