package com.vinplay.api.processors;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import com.vinplay.api.processors.AutoXuLyBank.APIProcess;
import com.vinplay.usercore.dao.impl.UserDaoImpl;
import com.vinplay.usercore.service.OtherService;
import com.vinplay.usercore.service.UserExtraService;
import com.vinplay.usercore.service.UserService;
import com.vinplay.usercore.service.impl.OtherServiceImpl;
import com.vinplay.usercore.service.impl.UserExtraServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.models.cache.UserExtraInfoModel;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.response.ActivePhoneResponse;
import com.vinplay.vbee.common.response.MoneyResponse;
import com.vinplay.vbee.common.response.UserPhone;
import com.vinplay.vbee.common.statics.Consts;
import com.vinplay.vbee.common.statics.TransType;
import org.bson.Document;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Objects;

public class ActivePhoneProcessor implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {

        ActivePhoneResponse response = new ActivePhoneResponse(false, "1001");
        HttpServletRequest request = param.get();
        String phoneNumber = request.getParameter("phoneNumber");
        if (phoneNumber != null) {
            phoneNumber = phoneNumber.trim();
            if (phoneNumber.startsWith("+")) {
                phoneNumber = phoneNumber.replace("+", "").trim();
            }
            if (phoneNumber.length() < 9 || phoneNumber.length() > 11) {
                response.setSuccess(false);
                response.setErrorCode("số điện thoại không hợp lệ");
                return response.toJson();
            }
        }

        String accessToken = request.getParameter("at");
        UserExtraService userExtraService = new UserExtraServiceImpl();
        UserExtraInfoModel model = userExtraService.getModelFromToken(accessToken);
        if (model == null) {
            response.setSuccess(false);
            response.setErrorCode("accessToken không hợp lệ");
            return response.toJson();
        }
        String nickName = model.getNickname();
        OtherService otherService = new OtherServiceImpl();
        UserPhone userPhone = otherService.getUserPhoneInfoByPhoneNumber(phoneNumber);
        UserPhone user = otherService.getUserPhoneInfoByNickname(nickName);
        if (userPhone != null) {
            if (userPhone.isActive()) {
                response.setSuccess(false);
                response.setErrorCode("Tài khoản đã kích hoạt bảo mật, vui lòng liên hệ CSKH để hủy bảo mật.");
                return response.toJson();
            }
            if (userPhone.isActive() && !Objects.equals(userPhone.getNickname(), nickName)) {
                response.setSuccess(false);
                response.setErrorCode("Số điện thoại đã kích hoạt cho tài khoản khác.");
                return response.toJson();
            }
        }
        if (user == null) {
            saveUserPhone(nickName, "", phoneNumber);
        } else {
            if (!user.getPhoneNumber().equals(phoneNumber)) {
                updateUserPhone(nickName, phoneNumber);
            }
        }
        response.setActive(false);
        response.setNickname(nickName);
        response.setPhoneNumber(phoneNumber);
        response.setErrorCode("200");
        response.setSuccess(true);
        return response.toJson();
    }

    private void saveUserPhone(String nickname, String otp, String phone) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> collection = db.getCollection("user_phone");
        Document filter = new Document("nickname", nickname);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        Document updateDocument = new Document("$set", new Document("otp", otp)
                .append("timeToExpired", 300000).append("createdDate", dateFormat.format(date)).append("phone", phone));

        UpdateResult result = collection.updateOne(filter, updateDocument);
        if (result.getMatchedCount() == 0) {
            Document newDocument = new Document("nickname", nickname)
                    .append("isActive", false)
                    .append("otp", otp)
                    .append("phone", phone)
                    .append("timeToExpired", 300000)
                    .append("createdDate", dateFormat.format(date));
            collection.insertOne(newDocument);
        }
    }


    private void updateUserPhone(String nickname, String phone) {
        if (nickname == null || nickname.isEmpty() || phone == null || phone.isEmpty()) {
            throw new IllegalArgumentException("Nickname and phone must not be empty");
        }
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> collection = db.getCollection("user_phone");
        Document filter = new Document("nickname", nickname);
        Document updateDocument = new Document("$set", new Document("phone", phone).append("otp", ""));
        UpdateResult result = collection.updateOne(filter, updateDocument);
        if (result.getMatchedCount() == 0) {
            throw new IllegalArgumentException("No user found with the given nickname");
        }
    }

}

