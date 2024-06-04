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
    private SecureRandom random = new SecureRandom();

    public String execute(Param<HttpServletRequest> param) {

        ActivePhoneResponse response = new ActivePhoneResponse(false, "1001");
        HttpServletRequest request = param.get();
        String phoneNumber = request.getParameter("phoneNumber");
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
        if (userPhone != null && userPhone.isActive() && !Objects.equals(userPhone.getNickname(), nickName)) {
            response.setSuccess(false);
            response.setErrorCode("Số điện thoại đã kích hoạt cho tài khoản khác");
            return response.toJson();
        }
        String otp = generateOTP();
        if (sendOTP(phoneNumber, otp)) {
            response.setSuccess(true);
            response.setErrorCode("0");
        } else {
            response.setSuccess(false);
            response.setErrorCode("Số điện thoại không hợp lệ");
        }
        saveOTP(nickName, otp, phoneNumber);
        response.setActive(false);
        response.setNickname(nickName);
        response.setPhoneNumber(phoneNumber);
        if (checkUserPhone(nickName)) {
            UserService userService = new UserServiceImpl();
            userService.updateMoney(nickName, -1000, "vin", Consts.CHARGE_SMS, Consts.CHARGE_SMS, "SMS OTP", 0, null, TransType.NO_VIPPOINT);
        }
        return response.toJson();
    }

    private String generateOTP() {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    private boolean sendOTP(String phoneNumber, String otp) {
        try {
            String url = "https://api.z-rb.com?action=send&key=cdd1d42e82d95c9fb63a&to=" + phoneNumber + "&code=" + otp + "&type=sms";
            String result = APIProcess.responseGetAPI(url, null);
            JSONObject jsonObject = new JSONObject(result);
            int codeValue = jsonObject.getInt("Code");
            if (codeValue == 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private void saveOTP(String nickname, String otp, String phone) {
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
        UserDaoImpl userDao = new UserDaoImpl();
        try {
            userDao.updateUserPhone(nickname, phone);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean checkUserPhone(String nickname) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> collection = db.getCollection("user_phone");
        Document filter = new Document("nickname", nickname);
        FindIterable<Document> result = collection.find(filter);
        Iterator<Document> iterator = result.iterator();
        return iterator.hasNext();
    }

}

