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
import com.vinplay.vbee.common.response.LogUserMoneyResponse;
import com.vinplay.vbee.common.response.UserCodeReponse;
import com.vinplay.vbee.common.response.UserTele;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import okhttp3.*;
import org.bson.Document;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SendGiftCodeToUserLoseProcessor implements BaseProcessor<HttpServletRequest, String> {

    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = param.get();
        String timeStart = request.getParameter("timeStart");
        String timeEnd = request.getParameter("timeEnd");
        String message = request.getParameter("message");

//        System.out.println("SendGiftCodeToUserLoseProcessor: " + timeStart + " " + timeEnd );

        long percent;
        try {
            percent = Long.parseLong(request.getParameter("percent"));
        } catch (Exception e) {
            percent = 3;
        }
        LogMoneyUserDaoImpl dao = new LogMoneyUserDaoImpl();
        Map<String, Long> users = new HashMap<>();
        List<LogUserMoneyResponse> list = dao.getLogMoneyUser(timeStart, timeEnd);


        for (LogUserMoneyResponse response : list) {
            if ("Gift Code".equalsIgnoreCase(response.serviceName)) {
                continue;
            }
            users.merge(response.nickName, response.moneyExchange, Long::sum);
        }

        OtherService otherService = new OtherServiceImpl();
        for (Map.Entry<String, Long> entry : users.entrySet()) {
            if (entry.getValue() < 0) {
                UserTele userTele = otherService.getUserTeleInfoByNickname(entry.getKey());
                if (userTele != null && userTele.getChatID() != null) {
                    int price = (int) (entry.getValue() * percent / 100 * -1);
                    if (price < 0) {
                        price = price * -1;
                    }
                    String giftCode = VinPlayUtils.genGiftCode(10);
                    String content = message + " : " + genCode(price, giftCode);
                    sendMessage(userTele.getChatID(), content);
                    saveUserTeleCashBack(userTele, giftCode, price, entry.getValue());
                }
            }
        }

        UserCodeReponse userCodeResponse = new UserCodeReponse(true, "200");
        Map<String, Long> filteredUsers = users.entrySet().stream()
                .filter(entry -> entry.getValue() < 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        userCodeResponse.setUsers(filteredUsers);
        return userCodeResponse.toJson();
    }

    public void saveUserTeleCashBack(UserTele userTele, String code, int price, long money) {
        OtherService otherService = new OtherServiceImpl();
        Document document = new Document();
        document.put("nickname", userTele.getNickname());
        document.put("money", money);
        document.put("code", code);
        document.put("cashBack", price);
        document.put("createdDate", VinPlayUtils.getCurrentDateTime());
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
            System.out.println("Response Code: " + response.code());
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

