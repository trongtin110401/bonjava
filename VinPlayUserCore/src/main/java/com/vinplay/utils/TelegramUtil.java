package com.vinplay.utils;


import com.pengrad.telegrambot.TelegramBot;
import com.vinplay.usercore.utils.GameCommon;
import org.apache.log4j.Logger;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.vinplay.common.HttpCommon;
import com.vinplay.usercore.service.UserService;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

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
        Response response = null;
        try {
            response = client.newCall(request).execute();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            response.close();
        }
    }

    private static String encodeValue(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
    }

    public void sendMessageNap(String message) {
        Response response = null;
        try {
            String chatId = GameCommon.getValueStr("Telegram_chat_id");
            String bootToken = GameCommon.getValueStr("Telegram_boot_token");
            OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                    .connectTimeout(3, TimeUnit.SECONDS)
                    .readTimeout(3, TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder()
                    .url("https://api.telegram.org/bot" + bootToken + "/sendMessage?text=" + encodeValue(message) + "&chat_id=" + chatId + "&parse_mode=HTML")
                    .method("GET", null)
                    .build();
            response = client.newCall(request).execute();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (response != null){
                response.close();
            }
        }
    }

    public void sendMessageNapGiftCode(String message) {
        Response response = null;
        try {
            String chatId = GameCommon.getValueStr("Telegram_giftcode_id");
            String bootToken = GameCommon.getValueStr("Telegram_boot_token");
            OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url("https://api.telegram.org/bot" + bootToken + "/sendMessage?text=" + encodeValue(message) + "&chat_id=" + chatId + "&parse_mode=HTML")
                    .method("GET", null)
                    .build();
            response = client.newCall(request).execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (response != null){
                response.close();
            }
        }
    }

    public void sendMessageRut(String message) {
        Response response = null;
        try {
//            String chatId = GameCommon.getValueStr("Telegram_rut_chat_id");
            System.out.println("=====> Rut: " + message);
            String chatId = "-4138070971";
            String bootToken = GameCommon.getValueStr("Telegram_boot_token");
            OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                    .connectTimeout(3, TimeUnit.SECONDS)
                    .readTimeout(3, TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder()
                    .url("https://api.telegram.org/bot" + bootToken + "/sendMessage?text=" + encodeValue(message) + "&chat_id=" + chatId + "&parse_mode=HTML")
                    .method("GET", null)
                    .build();
            response = client.newCall(request).execute();
            System.out.println("=====> Rut: done");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (response != null){
                response.close();
            }
        }
    }

    public void sendMessageBetTX(String message) {
        Response response = null;
        try {
//            String chatId = GameCommon.getValueStr("Telegram_rut_chat_id");
            String chatId = "-1002087063529";
            String bootToken = GameCommon.getValueStr("Telegram_boot_token");

            OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                    .connectTimeout(3, TimeUnit.SECONDS)
                    .readTimeout(3, TimeUnit.SECONDS)
                    .build();

            Request request = new Request.Builder()
                    .url("https://api.telegram.org/bot" + bootToken + "/sendMessage?text=" + encodeValue(message) + "&chat_id=" + chatId + "&parse_mode=HTML")
                    .method("GET", null)
                    .build();
            response = client.newCall(request).execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (response != null){
                response.close();
            }
        }
    }

    public void sendMessageBetTXMD5(String message) {
        Response response = null;
        try {
            //            String chatId = GameCommon.getValueStr("Telegram_rut_chat_id");
            String chatId = "-1002101792441";
            String bootToken = GameCommon.getValueStr("Telegram_boot_token");
            OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                    .connectTimeout(3, TimeUnit.SECONDS)
                    .readTimeout(3, TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder()
                    .url("https://api.telegram.org/bot" + bootToken + "/sendMessage?text=" + encodeValue(message) + "&chat_id=" + chatId + "&parse_mode=HTML")
                    .method("GET", null)
                    .build();
            response = client.newCall(request).execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (response != null){
                response.close();
            }
        }
    }

    public void sendMessageBetTXKUBET(String message) {
        Response response = null;
        try {
            //            String chatId = GameCommon.getValueStr("Telegram_rut_chat_id");
            String chatId = "-1002101792441";
            String bootToken = GameCommon.getValueStr("Telegram_boot_token");
            OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                    .connectTimeout(3, TimeUnit.SECONDS)
                    .readTimeout(3, TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder()
                    .url("https://api.telegram.org/bot" + bootToken + "/sendMessage?text=" + encodeValue(message) + "&chat_id=" + chatId + "&parse_mode=HTML")
                    .method("GET", null)
                    .build();
            response = client.newCall(request).execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (response != null){
                response.close();
            }
        }
    }

}
