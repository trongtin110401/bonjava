package com.vinplay.api.processors.otp;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.response.UserTele;
import org.bson.Document;

import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OTPTeleService {
    private SecureRandom random = new SecureRandom();

    public void sendOTP(String chatId, String otp) {
//        SendMessage message = new SendMessage();
//        message.setChatId(chatId);
//        message.setText("Mã OTP c?a b?n là : " + otp + " và có hi?u l?c trong vòng 5 phút.");
//        try {
//            execute(message);
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
    }

    public void saveOTP(String chatId, String otp) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> collection = db.getCollection("user_tele");
        Document filter = new Document("chatID", chatId);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        Document updateDocument = new Document("$set", new Document("otp", otp).append("timeToExpired", 300000).append("createdDate", dateFormat.format(date)));
        collection.updateOne(filter, updateDocument);
    }

    public String generateOTP() {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    public UserTele getInfoByNickname(String nickname) {
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
