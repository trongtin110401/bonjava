package com.vinplay.api.backend.processors.daily;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.vinplay.api.backend.processors.daily.DailyRegisterProcessor.generateUniqueString;


public class DailyUpdateInformationProcessor implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger("backend");

    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = param.get();
        DailyEntity response = new DailyEntity(false, "1001");
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("daily");

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String nickName = request.getParameter("nickName");
        String bankAccount = request.getParameter("bankAccount");
        String bankNumber = request.getParameter("bankNumber");
        String accountName = request.getParameter("accountName");
        String cryptoAddressWallet = request.getParameter("cryptoAddressWallet");
        String cryptoTypeWallet = request.getParameter("cryptoTypeWallet");
        String accessToken = request.getParameter("accessToken");

        Document existingDocument = checkExisted(username, accessToken, col);
        if (existingDocument == null) {
            return response.toJson();
        }

        String documentIdToUpdate =existingDocument.getObjectId("_id").toString();
        Document filter = new Document("_id", new ObjectId(documentIdToUpdate));

        Document updatedDocument = new Document();
        if(password != null){
            updateNonNullField("password", hashMD5(password), updatedDocument);
        }
        updateNonNullField("user_name", username, updatedDocument);
        updateNonNullField("nick_name", nickName, updatedDocument);
        updateNonNullField("bank_account", bankAccount, updatedDocument);
        updateNonNullField("bank_number", bankNumber, updatedDocument);
        updateNonNullField("account_name", accountName, updatedDocument);
        updateNonNullField("crypto_address_wallet", cryptoAddressWallet, updatedDocument);
        updateNonNullField("crypto_type_wallet", cryptoTypeWallet, updatedDocument);

        Document update = new Document("$set", updatedDocument);
        col.updateOne(filter, update);
        try {
            response = new DailyEntity(true, "200");
            response.setUsername(username);

            Document newDocument = (Document) col.findOneAndUpdate(filter, update);

            response.setReferentCode(newDocument.getString("referent_code"));
            response.setAccessToken(accessToken);
            response.setNickName(newDocument.getString("nick_name"));
            response.setBankAccount(newDocument.getString("bank_account"));
            response.setBankNumber(newDocument.getString("bank_number"));
            response.setAccountName(newDocument.getString("account_name"));
            response.setCryptoAddressWallet(newDocument.getString("crypto_address_wallet"));
            response.setCryptoTypeWallet(newDocument.getString("crypto_type_wallet"));
            response.setId(newDocument.getObjectId("_id").toString());
            response.setQrCode(newDocument.getString("qr_code"));

            return response.toJson();
        } catch (Exception e) {
            logger.error(e.getStackTrace());
        }
        return response.toJson();
    }

    private Document checkExisted(String username, String accessToken, MongoCollection col) {
        Document conditions = new Document();
        conditions.put("user_name", username);
        conditions.put("access_token", accessToken);
        Document response = (Document) col.find(conditions).first();
        return response;
    }

    private static void updateNonNullField(String key, Object value, Document updateFields) {
        if (value != null) {
            updateFields.append(key, value);
        }
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
