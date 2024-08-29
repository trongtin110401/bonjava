package com.vinplay.api.processors.otp;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.vinplay.common.HttpCommon;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.response.UserTele;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.bson.Document;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OTPTeleService {
    private SecureRandom random = new SecureRandom();

    public void sendOTP(String chatId, String otp) {
        String message = "Mã OTP của bạn là : " + otp + " và có hiệu lực trong vòng 5 phút";
        try {
            String bootToken = "6831621160:AAHPfkEON1-u2e44F8WAVdu5vT9ySql8ztA";
            OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url("https://api.telegram.org/bot" + bootToken + "/sendMessage?text=" + encodeValue(message) + "&chat_id=" + chatId + "&parse_mode=HTML")
                    .method("GET", null)
                    .build();
            Response response = client.newCall(request).execute();
            response.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String encodeValue(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
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
