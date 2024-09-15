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
import com.vinplay.dal.service.impl.ReportMoneyServiceImpl;
import com.vinplay.usercore.service.OtherService;
import com.vinplay.usercore.service.impl.GiftCodeServiceImpl;
import com.vinplay.usercore.service.impl.MailBoxServiceImpl;
import com.vinplay.usercore.service.impl.OtherServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.dto.GiftCodeDto;
import com.vinplay.vbee.common.messages.UserBackCodeMessage;
import com.vinplay.vbee.common.response.*;
import com.vinplay.vbee.common.rmq.RMQApi;
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
            // get Fish profit
            OtherService otherService = new OtherServiceImpl();
//            List<MoneyShootFishResponse> userFishProfits = otherService.getTotalShootFish(timeStart, timeEnd, null);
//            Map<String, Long> mapUserFishProfits = new HashMap<>();
//            userFishProfits.forEach(moneyShootFishResponse -> mapUserFishProfits.put(moneyShootFishResponse.getNickname(), moneyShootFishResponse.getTotalProfit()));

            LogMoneyUserDaoImpl dao = new LogMoneyUserDaoImpl();
            List<LogUserMoneyResponse> list = dao.getLogMoneyUser(timeStart, timeEnd);

            // map nickname => money
            Map<String, Long> topWins = new ReportMoneyServiceImpl().getGameLoser(timeStart, 1, 1000000);
            // search fund
            List<UserLoseByDay> userLoseByDays = topWins.entrySet().stream().map(entry -> {
                UserLoseByDay userLoseByDay = new UserLoseByDay();
                userLoseByDay.setNickname(entry.getKey());
                userLoseByDay.setMoney(entry.getValue());
                return userLoseByDay;
            }).collect(Collectors.toList());


//            List<UserLoseByDay> userLoseByDays = list.stream()
//                    .filter(log -> !Consts.NO_GAME.contains(log.getActionName()) && !"Exchange".equals(log.getActionName()))
//                    .collect(Collectors.groupingBy(LogUserMoneyResponse::getNickName,
//                            Collectors.summingLong(LogUserMoneyResponse::getMoneyExchange)))
//                    .entrySet().stream()
//                    .map(entry -> {
//                        UserLoseByDay userLoseByDay = new UserLoseByDay();
//                        userLoseByDay.setNickname(entry.getKey());
//                        userLoseByDay.setMoney(entry.getValue());
//                        return userLoseByDay;
//                    })
//                    .collect(Collectors.toList());

//            userLoseByDays.forEach(userLoseByDay -> {
//                if (mapUserFishProfits.containsKey(userLoseByDay.getNickname())) {
//                    userLoseByDay.setMoney(userLoseByDay.getMoney() + (mapUserFishProfits.get(userLoseByDay.getNickname()) * -1));
//                }
//            });

            // update status
            OtherService service = new OtherServiceImpl();
            service.updateStatusSendBackCodeByDay("LOSE", "", timeStart, timeEnd);

            userLoseByDays = userLoseByDays.stream()
                    .filter(user -> user.getMoney() <= -100000)
                    .collect(Collectors.toList());

            userLoseByDays.forEach(userLoseByDay -> {
                int giftCodeValue = (int) (userLoseByDay.getMoney() * percent / 100 * -1);
                if (giftCodeValue < 0) {
                    giftCodeValue = giftCodeValue * -1;
                }
                try {
                    // push to rabbitmq
                    UserBackCodeMessage userBackCodeMessage = new UserBackCodeMessage();
                    userBackCodeMessage.backType = 0;
                    userBackCodeMessage.nickname = userLoseByDay.getNickname();
                    userBackCodeMessage.giftValue = giftCodeValue;
                    userBackCodeMessage.money = userBackCodeMessage.getMoney();
                    RMQApi.publishMessage("queue_backcode", userBackCodeMessage, 1502);


//                    String giftCode = VinPlayUtils.genGiftCode(10);
//                    String content = message + " : " + genCode(giftCodeValue, giftCode);
//
//                    mailService.sendMailGiftCode(userLoseByDay.getNickname(), giftCode, "Hoàn Trả Tiền Cược", content);
//                    UserTele userTele = otherService.getUserTeleInfoByNickname(userLoseByDay.getNickname());
//                    if (userTele != null) {
//                        sendMessage(userTele.getChatID(), content);
//                    }
//                    saveUserTeleCashBack(userLoseByDay.getNickname(), giftCode, giftCodeValue, userLoseByDay.getMoney());

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

    public void saveUserTeleCashBack(String nickname, String code, int price, long money) {
        OtherService otherService = new OtherServiceImpl();
        Document document = new Document();
        document.put("nickname", nickname);
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
        Response response = null;
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
            response = client.newCall(request).execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            response.close();
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

