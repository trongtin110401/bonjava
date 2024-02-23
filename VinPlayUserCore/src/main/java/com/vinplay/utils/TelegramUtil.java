package com.vinplay.utils;


import com.pengrad.telegrambot.TelegramBot;
import org.apache.log4j.Logger;
import org.bson.Document;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.vinplay.common.HttpCommon;
import com.vinplay.telegram.impl.TelegramDao;
import com.vinplay.usercore.service.UserService;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TelegramUtil {

    private static final org.apache.log4j.Logger logger = Logger.getLogger((String) "tele");

    public static boolean sendMessage(String message, String chatId, String bootToken) {
        try {
            TelegramBot bot = new TelegramBot(bootToken);
            SendMessage request = new SendMessage(chatId, message)
                    .parseMode(ParseMode.HTML)
                    .disableWebPagePreview(true)
                    .disableNotification(true);
            //.replyToMessageId(1)
            //.replyMarkup(new ForceReply());
            //SendResponse sendResponse = bot.execute(request);
            //boolean ok = sendResponse.isOk();
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    private UserService userService = new UserServiceImpl();


    public void senMessToDaily(String nickname, String hanhDong, long amount, String content) {
//        TelegramDao dao = new TelegramDao();
//        AlertTeleGramDaily teleGramDaily = dao.findTransaction(nickname);
//        long currentMoney = userService.getCurrentMoneyUserCache(nickname, "vin");
//        String token = "1794953953:AAFFk-QGjN6KIGPfVi48fQKDKqYsujel2EI"; // token bot vao day
//        if (teleGramDaily != null) {
//            long moneyCurrent = currentMoney + amount;
//            String message = "<b> Hành động " + hanhDong + "</b>";
//            message += "\n Số tiền giao dịch:  <b>" + ((amount > 0) ? ("+" + String.format("%,.0f", (double) amount)) : String.format("%,.0f", (double) amount)) + "</b>";
//            message += "\n Số dư hiện tại : <b>" + String.format("%,.0f", (double) moneyCurrent) + "</b>";
//            message += "\n Nội dung : <b>" + content + "</b>";
//            try {
//                UserModel userReceive = userService.getUserByNickName(nickname);
//                if (userReceive.getDaily() == 1 || userReceive.getDaily() == 2) {
//                    sendMessAlert(message, token, teleGramDaily.teleId);
//                }
//
//            } catch (SQLException | UnsupportedEncodingException throwables) {
//                Debug.info("senMessToDaily error with exception " + throwables.getStackTrace());
//            }
//        }

    }


    public static void sendMessAlert(String mess, String token, String chatId) throws UnsupportedEncodingException {
        OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("https://api.telegram.org/bot" + token + "/sendMessage?text=" + encodeValue(mess) + "&chat_id=" + chatId + "&parse_mode=HTML")
                .method("GET", null)
                .build();
        try {
            Response response = client.newCall(request).execute();
        } catch (IOException e) {

        }
    }

    private static String encodeValue(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
    }

    public  void sendMessageNapRut(String message) {
        TelegramDao dao = new TelegramDao();
        Document bot = dao.getInfoBotNapRut();
        try {
            if (bot == null) {
                logger.error("cannot get value tele info");
                return;
            }
            String token = bot.getString("bot_tele");
            String chatId = bot.getString("chat_id");

            OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url("https://api.telegram.org/bot" + token + "/sendMessage?text=" + encodeValue(message) + "&chat_id=" + chatId + "&parse_mode=HTML")
                    .method("GET", null)
                    .build();
            Response response = client.newCall(request).execute();
        } catch (IOException e) {

        }
    }

}
