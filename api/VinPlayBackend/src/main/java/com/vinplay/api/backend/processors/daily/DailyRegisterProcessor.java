package com.vinplay.api.backend.processors.daily;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.servlet.http.HttpServletRequest;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class DailyRegisterProcessor implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger("backend");


    private static final String DOMAIN = "http://45.76.178.154:3000/?code_daily=";


    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = param.get();
        DailyEntity response = new DailyEntity(false, "1001");
        try {
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String nickName = request.getParameter("nickName");
            String bankAccount = request.getParameter("bankAccount");
            String bankNumber = request.getParameter("bankNumber");
            String accountName = request.getParameter("accountName");
            String cryptoAddressWallet = request.getParameter("cryptoAddressWallet");
            String cryptoTypeWallet = request.getParameter("cryptoTypeWallet");
            if (checkExisted(username, nickName)) {
                return response.toJson();
            }
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("daily");
            String time_log = VinPlayUtils.getCurrentDateTime();
            Document doc = new Document();
            doc.append("user_name", username);
            doc.append("password", hashMD5(password));
            doc.append("nick_name", nickName);
            doc.append("bank_account", bankAccount);
            doc.append("bank_number", bankNumber);
            doc.append("account_name", accountName);
            String referentCode = generateString(5);
            String qrCode = "https://api.qrserver.com/v1/create-qr-code/?data=" + DOMAIN + referentCode + "&amp;size=100x100";
            doc.append("referent_code", referentCode);
            doc.append("time_log", time_log);
            String accessToken = generateUniqueString();
            doc.append("access_token", accessToken);
            doc.append("crypto_address_wallet", cryptoAddressWallet);
            doc.append("crypto_type_wallet", cryptoTypeWallet);
            doc.append("qr_code", qrCode);
            col.insertOne(doc);
            ObjectId generatedId = doc.getObjectId("_id");
            response = new DailyEntity(true, "200");

            response.setUsername(username);
            response.setReferentCode(referentCode);
            response.setAccessToken(accessToken);
            response.setNickName(nickName);
            response.setBankAccount(bankAccount);
            response.setBankNumber(bankNumber);
            response.setAccountName(accountName);
            response.setCryptoAddressWallet(cryptoAddressWallet);
            response.setCryptoTypeWallet(cryptoTypeWallet);
            response.setQrCode(qrCode);
            response.setId(generatedId.toString());


            return response.toJson();
        } catch (Exception e) {
            logger.error(e.getStackTrace());
        }
        return response.toJson();
    }

    private Boolean checkExisted(String username, String nickName) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        Document conditions = new Document();
        conditions.put("user_name", username);
        conditions.put("nick_name", nickName);
        long totalRows = db.getCollection("daily").count(conditions);
        if (totalRows > 0)
            return Boolean.TRUE;
        return Boolean.FALSE;
    }

    private Boolean checkReferentCodeExisted(String referentCode) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        Document conditions = new Document();
        conditions.put("referent_code", referentCode);
        long totalRows = db.getCollection("daily").count(conditions);
        if (totalRows > 0)
            return Boolean.TRUE;
        return Boolean.FALSE;
    }

    private String generateString(int length) {
        SecureRandom random = new SecureRandom();
        final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder randomString = new StringBuilder(length);
        while (checkReferentCodeExisted(randomString.toString())) {
            for (int i = 0; i < length; i++) {
                int randomIndex = random.nextInt(CHARACTERS.length());
                char randomChar = CHARACTERS.charAt(randomIndex);
                randomString.append(randomChar);
            }
        }
        return randomString.toString();

    }

    public static String hashMD5(String input) {
        try {
            // Create an MD5 message digest
            MessageDigest md = MessageDigest.getInstance("MD5");

            // Update the message digest with the input bytes
            md.update(input.getBytes());

            // Get the hash value as an array of bytes
            byte[] digest = md.digest();

            // Convert the byte array to a hexadecimal string
            StringBuilder result = new StringBuilder();
            for (byte b : digest) {
                result.append(String.format("%02x", b));
            }

            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            // Handle the exception, e.g., log it or throw a custom exception
            e.printStackTrace();
            return null;
        }
    }

    public static String generateUniqueString() {
        long timestamp = System.currentTimeMillis();

        // Generate random bytes
        byte[] randomBytes = new byte[8];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(randomBytes);

        // Combine timestamp and random bytes
        ByteBuffer buffer = ByteBuffer.allocate(20);
        buffer.putLong(timestamp);
        buffer.put(randomBytes);

        // Encode the combined bytes to Base64
        String uniqueString = Base64.getUrlEncoder().withoutPadding().encodeToString(buffer.array());

        return uniqueString;
    }

}
