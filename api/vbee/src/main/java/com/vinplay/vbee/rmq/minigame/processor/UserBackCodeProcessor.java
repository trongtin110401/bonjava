/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.messages.minigame.TransactionTaiXiuMessage
 *  org.apache.log4j.Logger
 */
package com.vinplay.vbee.rmq.minigame.processor;

import com.vinplay.usercore.service.OtherService;
import com.vinplay.usercore.service.impl.GiftCodeServiceImpl;
import com.vinplay.usercore.service.impl.MailBoxServiceImpl;
import com.vinplay.usercore.service.impl.OtherServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.dto.GiftCodeDto;
import com.vinplay.vbee.common.messages.UserBackCodeMessage;
import com.vinplay.vbee.common.response.UserTele;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import okhttp3.*;
import org.apache.log4j.Logger;
import org.bson.Document;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class UserBackCodeProcessor implements BaseProcessor<byte[], Boolean> {
    private static final Logger logger = Logger.getLogger((String) "vbee");

    MailBoxServiceImpl mailService = new MailBoxServiceImpl();


    public Boolean execute(Param<byte[]> param) {
        byte[] body = param.get();
        try {
            UserBackCodeMessage message = (UserBackCodeMessage) UserBackCodeMessage.fromBytes(body);

            String giftCode = VinPlayUtils.genGiftCode(10);
            String content = message.message + " : " + genCode(Long.valueOf(message.getGiftValue()).intValue(), giftCode);

            // send mail
            mailService.sendMailGiftCode(message.getNickname(), giftCode, "Hoàn Trả Tiền Cược", content);
            saveUserTeleCashBack(message.getNickname(), giftCode, Long.valueOf(message.getGiftValue()).intValue(), message.getMoney());

            try {
                OtherService otherService = new OtherServiceImpl();
                UserTele userTele = otherService.getUserTeleInfoByNickname(message.getNickname());
                if (userTele != null) {
                    sendMessage(userTele.getChatID(), content);
                }
            } catch (Exception ignored) {
            }
        } catch (Exception e) {
            logger.error("Handle save transaction error ", (Throwable) e);
        }
        return false;

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
}

