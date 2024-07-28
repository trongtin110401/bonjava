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

import bitzero.util.common.business.Debug;
import com.vinplay.dal.dao.impl.LogMoneyUserDaoImpl;
import com.vinplay.usercore.service.OtherService;
import com.vinplay.usercore.service.impl.GiftCodeServiceImpl;
import com.vinplay.usercore.service.impl.MailBoxServiceImpl;
import com.vinplay.usercore.service.impl.OtherServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.dto.GiftCodeDto;
import com.vinplay.vbee.common.response.*;
import com.vinplay.vbee.common.statics.Consts;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import okhttp3.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bson.Document;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class SendGiftCodeToUserWinProcessor implements BaseProcessor<HttpServletRequest, String> {

    MailBoxServiceImpl mailService = new MailBoxServiceImpl();

    public String execute(Param<HttpServletRequest> param) {
        try {
            HttpServletRequest request = param.get();
            String timeStart = request.getParameter("timeStart");
            String timeEnd = request.getParameter("timeEnd");
            String message = request.getParameter("message");
            String nickname = request.getParameter("nickname");

            int price;
            try {
                price = Integer.parseInt(request.getParameter("price"));
            } catch (Exception e) {
                price = 50000;
            }
            return process(nickname, message, timeStart, timeEnd, price);
        } catch (Exception ex) {
            System.out.println(ExceptionUtils.getStackTrace(ex));
            throw new RuntimeException(ex);
        }
    }

    public String process(String nickname, String message, String timeStart, String timeEnd, int price) throws Exception {
        try {
            // get Fish profit
            OtherService otherService = new OtherServiceImpl();
            List<MoneyShootFishResponse> userFishProfits = otherService.getTotalShootFish(timeStart, timeEnd, nickname);
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
                    .filter(log -> !Consts.NO_GAME.contains(log.getActionName()) && !"Exchange".equals(log.getActionName()))
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

            userLoseByDays.forEach(userLoseByDay -> {
                String giftCode = VinPlayUtils.genGiftCode(10);
                String content = message + " : " + genCode(price, giftCode);
                try {
                    mailService.sendMailGiftCode(userLoseByDay.getNickname(), giftCode, "Hoàn Trả Tiền Cược", content);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                UserTele userTele = otherService.getUserTeleInfoByNickname(userLoseByDay.getNickname());
                if (userTele != null && userTele.getChatID() != null) {
                    try {
                        sendMessage(userTele.getChatID(), content);
                        saveUserTeleCashBack(userTele, giftCode, price, userLoseByDay.getMoney());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
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

    public void saveUserTeleCashBack(UserTele userTele, String code, int price, long money) {
        OtherService otherService = new OtherServiceImpl();
        Document document = new Document();
        document.put("nickname", userTele.getNickname());
        document.put("chatID", userTele.getChatID());
        document.put("money", money);
        document.put("code", code);
        document.put("cashBack", price);
        document.put("status", false);
        document.put("createdDate", VinPlayUtils.getCurrentDateTime());
        String expirationDateString = calculateExpirationDate(VinPlayUtils.getCurrentDateTime());
        document.put("expirationDate", expirationDateString);
        document.put("activeDate", "");
        otherService.saveUserTeleCashBack(document);
    }

    public static String calculateExpirationDate(String createdDateString) {
        Date createdDate = parseDate(createdDateString);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(createdDate);

        calendar.add(Calendar.DAY_OF_MONTH, 3);

        Date expirationDate = calendar.getTime();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(expirationDate);
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

    public static void sendMessage(String chatId, String message) {
        try {
//            String bootToken = GameCommon.getValueStr("Telegram_boot_bon_token");
            String bot = "6831621160:AAHPfkEON1-u2e44F8WAVdu5vT9ySql8ztA";
            RequestBody requestBody = new FormBody.Builder()
                    .add("chat_id", chatId)
                    .add("text", message)
                    .build();

            Request request = new Request.Builder()
                    .url("https://api.telegram.org/bot" + bot + "/sendMessage")
                    .post(requestBody)
                    .build();

            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();
            response.close();
        } catch (Exception e) {
            e.printStackTrace();
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

