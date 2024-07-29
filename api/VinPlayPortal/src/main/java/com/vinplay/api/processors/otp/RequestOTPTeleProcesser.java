package com.vinplay.api.processors.otp;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.vinplay.api.processors.response.RequestOTPTeleResponse;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.response.UserTele;
import org.bson.Document;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.servlet.http.HttpServletRequest;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RequestOTPTeleProcesser implements BaseProcessor<HttpServletRequest, String> {
    private SecureRandom random = new SecureRandom();

    public synchronized String execute(Param<HttpServletRequest> param) {
        try {
            RequestOTPTeleResponse response = new RequestOTPTeleResponse(false, "1001");
            HttpServletRequest request = param.get();
            String nickname = request.getParameter("nickname");
            UserTele u = getInfoByNickname(nickname);
            if (u == null || !u.isActive() || u.getChatID() == null || u.getChatID().isEmpty()) {
                response.setErrorCode("Tài khoản chưa xác thực Tele");
                return response.toJson();
            }
            String otp = generateOTP();
            sendOTP(u.getChatID(), otp);
            saveOTP(u.getChatID(), otp);
            response.setErrorCode("200");
            response.setSuccess(true);
            return response.toJson();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    private void saveOTP(String chatId, String otp) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> collection = db.getCollection("user_tele");
        Document filter = new Document("chatID", chatId);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        Document updateDocument = new Document("$set", new Document("otp", otp).append("timeToExpired", 300000).append("createdDate", dateFormat.format(date)));
        collection.updateOne(filter, updateDocument);
    }

    private void sendOTP(String chatId, String otp) {
//        SendMessage message = new SendMessage();
//        message.setChatId(chatId);
//        message.setText("Mã OTP của bạn là : " + otp + " và có hiệu lực trong vòng 5 phút.");
//        try {
//            execute(message);
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
    }

    private String generateOTP() {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    private UserTele getInfoByNickname(String nickname) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> collection = db.getCollection("user_tele");
        Document filter = new Document("nickname", nickname);
        MongoCursor<Document> cursor = collection.find(filter).iterator();

        try {
            if (cursor.hasNext()) {
                Document doc = cursor.next();
                UserTele user = extractUserInfo(doc);
                return user;
            } else {
                return null;
            }
        } finally {
            cursor.close();
        }

    }

    private UserTele extractUserInfo(Document doc) {
        String id = doc.getObjectId("_id").toString();
        String nickname = doc.getString("nickname");
        String phoneNumber = doc.getString("phoneNumber");
        boolean isActive = doc.getBoolean("isActive");
        String otp = doc.getString("otp");
        long timeToExpired = doc.getInteger("timeToExpired");
        String createdDate = doc.getString("createdDate");
        String chatID = doc.getString("chatID");
        UserTele user = new UserTele();
        user.setId(id);
        user.setNickname(nickname);
        user.setPhoneNumber(phoneNumber);
        user.setActive(isActive);
        user.setOtp(otp);
        user.setTimeToExpired(timeToExpired);
        user.setCreatedDate(createdDate);
        user.setChatID(chatID);

        return user;
    }
}
