package com.vinplay.api.backend.processors.daily;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import org.apache.log4j.Logger;
import org.bson.Document;

import javax.servlet.http.HttpServletRequest;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.vinplay.api.backend.processors.daily.DailyRegisterProcessor.generateUniqueString;


public class DailyLoginProcessor implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger("backend");

    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = param.get();
        DailyEntity response = new DailyEntity(false, "1001");
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("daily");
        try {
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            Document daily = checkExisted(username, hashMD5(password), col);
            if (daily == null) {
                return response.toJson();
            }
            String accessToken = generateUniqueString();
            daily.append("access_token", accessToken);
            col.updateOne(
                    Filters.eq("user_name", username),
                    Updates.set("access_token", accessToken)
            );
            response = new DailyEntity(true, "200");
            response.setUsername(username);
            response.setReferentCode(daily.getString("referent_code"));
            response.setAccessToken(accessToken);
            response.setNickName(daily.getString("nick_name"));
            response.setBankAccount(daily.getString("bank_account"));
            response.setBankNumber(daily.getString("bank_number"));
            response.setAccountName(daily.getString("account_name"));
            response.setCryptoAddressWallet(daily.getString("crypto_address_wallet"));
            response.setCryptoTypeWallet(daily.getString("crypto_type_wallet"));
            response.setId(daily.getObjectId("_id").toString());

            return response.toJson();
        } catch (Exception e) {
            logger.error(e.getStackTrace());
        }
        return response.toJson();
    }

    private Document checkExisted(String username, String password, MongoCollection col) {
        Document conditions = new Document();
        conditions.put("user_name", username);
        conditions.put("password", password);
        Document response = (Document) col.find(conditions).first();
        return response;
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

}
