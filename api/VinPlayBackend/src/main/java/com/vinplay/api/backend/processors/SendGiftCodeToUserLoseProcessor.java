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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class SendGiftCodeToUserLoseProcessor implements BaseProcessor<HttpServletRequest, String> {

    MailBoxServiceImpl mailService = new MailBoxServiceImpl();

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

            long percent;
            try {
                percent = Long.parseLong(request.getParameter("percent"));
            } catch (Exception e) {
                percent = 3;
            }

//            // get Fish profit
//            OtherService otherService = new OtherServiceImpl();
//            List<MoneyShootFishResponse> userFishProfits = otherService.getTotalShootFish(timeStart, timeEnd);
//            Map<String, Long> mapUserFishProfits = new HashMap<>();
//            userFishProfits.forEach(moneyShootFishResponse -> mapUserFishProfits.put(moneyShootFishResponse.getNickname(), moneyShootFishResponse.getTotalProfit()));
//
//
//            LogMoneyUserDaoImpl dao = new LogMoneyUserDaoImpl();
//            Map<String, Long> users = new HashMap<>();
//            List<LogUserMoneyResponse> list = dao.getLogMoneyUser(timeStart, timeEnd);
//            for (LogUserMoneyResponse response : list) {
//                if ("Gift Code".equalsIgnoreCase(response.serviceName)) {
//                    continue;
//                }
//                users.merge(response.nickName, response.moneyExchange, Long::sum);
//            }
//
//            for (Map.Entry<String, Long> entry : users.entrySet()) {
//                if (entry.getValue() < 0) {
//                    UserTele userTele = otherService.getUserTeleInfoByNickname(entry.getKey());
//                    if (userTele != null && userTele.getChatID() != null) {
//                        int price = (int) (entry.getValue() * percent / 100 * -1);
//                        if (price <= -100000) {
//                            price = price * -1;
//                        }
//                        String giftCode = VinPlayUtils.genGiftCode(10);
//                        String content = message + " : " + genCode(price, giftCode);
//                        mailService.sendMailGiftCode(userTele.getNickname(), giftCode, "Hoan Tra Tien Cuoc", content);
//                        sendMessage(userTele.getChatID(), content);
//                        saveUserTeleCashBack(userTele, giftCode, price, entry.getValue());
//                    }
//                }
//            }
//
//            UserCodeReponse userCodeResponse = new UserCodeReponse(true, "200");
//            Map<String, Long> filteredUsers = users.entrySet().stream()
//                    .filter(entry -> entry.getValue() <= -100000)
//                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
//            userCodeResponse.setUsers(filteredUsers);
//            return userCodeResponse.toJson();

            return process(message, timeStart, timeEnd, percent);
        } catch (Exception ex) {
            System.out.println(ExceptionUtils.getStackTrace(ex));
            throw new RuntimeException(ex);
        }
    }

    public String process(String message, String timeStart, String timeEnd, long percent) throws Exception {
        try {
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

            userLoseByDays.forEach(userLoseByDay -> {
                if (mapUserFishProfits.containsKey(userLoseByDay.getNickname())) {
                    userLoseByDay.setMoney(userLoseByDay.getMoney() + (mapUserFishProfits.get(userLoseByDay.getNickname()) * -1));
                }
            });

            userLoseByDays.forEach(userLoseByDay -> {
                int price = (int) (userLoseByDay.getMoney() * percent / 100 * -1);
                if (price < 0) {
                    price = price * -1;
                }
                String giftCode = VinPlayUtils.genGiftCode(10);
                String content = message + " : " + genCode(price, giftCode);


                try {
                    mailService.sendMailGiftCode(userLoseByDay.getNickname(), giftCode, "Hoàn Trả Tiền Cược", content);
                    UserTele userTele = otherService.getUserTeleInfoByNickname(userLoseByDay.getNickname());
                    if (userTele != null) {
                        sendMessage(userTele.getChatID(), content);
                        saveUserTeleCashBack(userTele, giftCode, price, userLoseByDay.getMoney());
                    }
                    OtherService service = new OtherServiceImpl();
                    service.updateStatusSendBackCodeByDay("LOSE", "", timeStart, timeEnd);
                } catch (SQLException e) {
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

    public void saveUserTeleCashBack(UserTele userTele, String code, int price, long money) {
        OtherService otherService = new OtherServiceImpl();
        Document document = new Document();
        document.put("nickname", userTele.getNickname());
        document.put("money", money);
        document.put("code", code);
        document.put("cashBack", price);
        String createdDate = VinPlayUtils.getCurrentDateTime();
        document.put("createdDate", VinPlayUtils.getCurrentDateTime());
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(createdDate);
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, 3);
            Date newDate = calendar.getTime();
            String expirationDate = format.format(newDate);
            document.put("expirationDate", expirationDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        otherService.saveUserTeleCashBack(document);
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

