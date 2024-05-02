/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.core.IMap
 *  com.vinplay.vbee.common.exceptions.KeyNotFoundException
 *  com.vinplay.vbee.common.hazelcast.HazelcastClientFactory
 *  com.vinplay.vbee.common.messages.BaseMessage
 *  com.vinplay.vbee.common.messages.OtpMessage
 *  com.vinplay.vbee.common.models.OtpModel
 *  com.vinplay.vbee.common.models.UserModel
 *  com.vinplay.vbee.common.models.cache.UserCacheModel
 *  com.vinplay.vbee.common.rmq.RMQApi
 *  com.vinplay.vbee.common.statics.TimeBasedOneTimePasswordUtil
 *  com.vinplay.vbee.common.utils.VinPlayUtils
 *  org.apache.log4j.Logger
 */
package com.vinplay.usercore.service.impl;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.vinplay.dichvuthe.service.impl.AlertServiceImpl;
import com.vinplay.usercore.dao.impl.OtpDaoImpl;
import com.vinplay.usercore.dao.impl.UserDaoImpl;
import com.vinplay.usercore.entities.MessageMTResponse;
import com.vinplay.usercore.logger.MoneyLogger;
import com.vinplay.usercore.service.OtherService;
import com.vinplay.usercore.service.OtpService;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.usercore.utils.PartnerConfig;
import com.vinplay.vbee.common.exceptions.KeyNotFoundException;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.messages.BaseMessage;
import com.vinplay.vbee.common.messages.LogMoneyUserMessage;
import com.vinplay.vbee.common.messages.MoneyMessageInMinigame;
import com.vinplay.vbee.common.messages.OtpMessage;
import com.vinplay.vbee.common.messages.dvt.RechargeByCardMessage;
import com.vinplay.vbee.common.models.OtpModel;
import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.models.cache.UserCacheModel;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.response.BaseResponseModel;
import com.vinplay.vbee.common.rmq.RMQApi;
import com.vinplay.vbee.common.statics.TimeBasedOneTimePasswordUtil;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import com.vinplay.vbee.common.utils.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

import org.apache.log4j.Logger;
import org.bson.Document;

public class OtpServiceImpl
        implements OtpService {
    private static final Logger logger = Logger.getLogger((String) "api");

    @Override
    public MessageMTResponse genMessageMT(OtpMessage message, String mobile) throws Exception {
        UserServiceImpl userSer = new UserServiceImpl();
        boolean success = false;
        String messageMT = "";
        String otp = "";
        if (message.getMessageMO().equals("OZZ OTP")) {
            if (userSer.checkMobile(mobile)) {
                otp = VinPlayUtils.genOtpSMS((String) mobile, (String) message.getCommandCode());
                success = true;
                messageMT = String.format(GameCommon.MESSAGE_OTP_SUCCESS, otp);
            } else {
                messageMT = GameCommon.MESSAGE_ERROR_MOBILE;
            }
        } else if (message.getMessageMO().equals("OZZ APP")) {
            if (userSer.checkMobile(mobile)) {
                otp = VinPlayUtils.genOtpSMS((String) mobile, (String) message.getCommandCode());
                success = true;
                messageMT = String.format(GameCommon.MESSAGE_APP_SUCCESS, otp);
            } else {
                messageMT = GameCommon.MESSAGE_ERROR_MOBILE;
            }
        } else if (message.getMessageMO().equals("OZZ ODP")) {
            if (userSer.checkMobileDaiLy(mobile)) {
                OtpDaoImpl otpDao = new OtpDaoImpl();
                OtpModel otpModel = otpDao.getOtpSMS(mobile, "OZZ ODP");
                if (otpModel != null && VinPlayUtils.compareDate((Date) new Date(), (Date) otpModel.getOtpTime()) == 0) {
                    otp = otpModel.getOtp();
                } else {
                    otp = VinPlayUtils.genOtpSMS((String) mobile, (String) message.getCommandCode());
                    success = true;
                }
                messageMT = String.format(GameCommon.MESSAGE_ODP_SUCCESS, otp, VinPlayUtils.getCurrentDate());
            } else {
                messageMT = GameCommon.MESSAGE_ERROR_MOBILE;
            }
        } else {
            messageMT = GameCommon.MESSAGE_ERROR_SYNTAX;
        }
        MessageMTResponse res = new MessageMTResponse(success, otp, messageMT);
        return res;
    }

    @Override
    public boolean logOTP(OtpMessage message) throws IOException, TimeoutException, InterruptedException {
        RMQApi.publishMessage((String) "queue_otp", (BaseMessage) message, (int) 201);
        return true;
    }

    @Override
    public boolean updateOtp(String mobile, String otp, String messageMO) throws SQLException {
        OtpDaoImpl dao = new OtpDaoImpl();
        return dao.updateOtpSMS(mobile, otp, messageMO);
    }

    @Override
    public String revertMobile(String mobile) {
        if (mobile.substring(0, 2).equals("84")) {
            return "0" + mobile.substring(2);
        }
        return mobile;
    }

    @Override
    public OtpModel CheckValidSMS(String nick_name) throws SQLException {
        HazelcastInstance client = HazelcastClientFactory.getInstance();
        IMap<String, UserModel> userMap = client.getMap("users");
        UserModel model = null;
        if (userMap.containsKey((Object) nick_name)) {
            model = (UserModel) userMap.get((Object) nick_name);
            UserCacheModel userCacheModel = (UserCacheModel) model;
        } else {
            UserDaoImpl dao = new UserDaoImpl();
            model = dao.getUserByNickName(nick_name);
        }
        if (model != null) {
            OtpDaoImpl dao = new OtpDaoImpl();
            try {
                OtpModel otpModel = dao.getOtpSMS(model.getMobile(), "OZZ OTP");
                if (otpModel != null) {
                    if (System.currentTimeMillis() > otpModel.getOtpTime().getTime() + 2 * 60 * 1000) {
                        otpModel.setCommandCode("OK");
                        return otpModel;
                    } else {
                        otpModel.setCommandCode("NOTOK");
                        return otpModel;
                    }
                } else {
                    return new OtpModel(model.getMobile(), "", null, "FIRST_TIME", 0);
                }
            } catch (ParseException ex) {

            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int checkOtp(String otp, String nickname, String type, String mobile) throws SQLException, UnsupportedEncodingException, NoSuchAlgorithmException, KeyNotFoundException {
        if (GameCommon.getValueStr("OTP_DEFAULT").isEmpty()) {
            int res;
            block24:
            {
                res = 77;
                if (this.checkAppOTP(nickname, otp) == 0) {
                    return 0;
                }
                try {
                    if (otp == null || type == null || otp.length() != 5 || !type.equals("0") && !type.equals("1"))
                        break block24;
                    HazelcastInstance client = HazelcastClientFactory.getInstance();
                    IMap<String, UserModel> userMap = client.getMap("users");
                    UserModel model = null;
                    if (userMap.containsKey((Object) nickname)) {
                        model = (UserModel) userMap.get((Object) nickname);
                        UserCacheModel userCacheModel = (UserCacheModel) model;
                    } else {
                        UserDaoImpl userDao = new UserDaoImpl();
                        model = userDao.getUserByNickName(nickname);
                    }
                    if (model == null) break block24;
                    mobile = mobile == null ? model.getMobile() : mobile;
                    if (mobile == null || mobile.isEmpty()) break block24;
                    if (type.equals("0")) {
                        try {
                            OtpDaoImpl dao;
                            OtpModel otpModel;
                            if (userMap.containsKey((Object) nickname)) {
                                userMap.lock(nickname);
                            }
                            if ((otpModel = (dao = new OtpDaoImpl()).getOtpSMS(mobile, "OZZ OTP")) != null && otpModel.getOtp() != null && otpModel.getOtpTime() != null && otp.equals(otpModel.getOtp())) {
                                if (!VinPlayUtils.checkOtpTimeout((Date) otpModel.getOtpTime())) {
                                    res = 0;
                                    dao.updateOtpSMS(mobile, "", "OZZ OTP");
                                } else {
                                    // 4 >> time out
                                    res = 4;
                                }
                            }
                            break block24;
                        } catch (Exception e) {
                            logger.debug((Object) e);
                            res = 74;
                            break block24;
                        } finally {
                            if (userMap.containsKey((Object) nickname)) {
                                userMap.unlock(nickname);
                            }
                        }
                    }
                    if (model.getMobile() == null || model.getMobile().isEmpty() || !model.isHasMobileSecurity() || !userMap.containsKey((Object) nickname))
                        break block24;
                    try {
                        String otpApp;
                        userMap.lock(nickname);
                        UserCacheModel user = (UserCacheModel) userMap.get((Object) nickname);
                        if ((user.getOtpApp() == null || !user.getOtpApp().equals(otp)) && (otpApp = VinPlayUtils.genOtpApp((String) nickname, (String) mobile)).equals(otp)) {
                            user.setOtpApp(otp);
                            userMap.put(nickname, user);
                            res = 0;
                        }
                    } catch (Exception e) {
                        logger.debug((Object) e);
                        res = 75;
                    } finally {
                        userMap.unlock(nickname);
                    }
                } catch (Exception e2) {
                    logger.debug((Object) e2);
                    res = 76;
                }
            }
            logger.info("Verify Otp response:" + res);
            return res;
        }
        if (otp.equals(GameCommon.getValueStr("OTP_DEFAULT"))) {
            return 0;
        }
        return 3;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int checkOtpLogin(String otp, String otpType, String nickname, String mobile, boolean appSecure) throws Exception {
        if (GameCommon.getValueStr("OTP_DEFAULT").isEmpty()) {
            int res;
            block17:
            {
                res = 3;
                if (this.checkAppOTP(nickname, otp) == 0) {
                    return 0;
                }
                try {
                    if (otp == null || otpType == null || otp.length() != 5 || !otpType.equals("0") && !otpType.equals("1"))
                        break block17;
                    if (otpType.equals("0")) {
                        OtpDaoImpl dao = new OtpDaoImpl();
                        OtpModel model = dao.getOtpSMS(mobile, "OZZ OTP");
                        if (model != null && model.getOtp() != null && model.getOtpTime() != null && otp.equals(model.getOtp())) {
                            if (!VinPlayUtils.checkOtpTimeout((Date) model.getOtpTime())) {
                                res = 0;
                                dao.updateOtpSMS(mobile, "", "OZZ OTP");
                            } else {
                                res = 4;
                            }
                        }
                        break block17;
                    }
                    HazelcastInstance client = HazelcastClientFactory.getInstance();
                    IMap<String, UserModel> userMap = client.getMap("users");
                    if (userMap.containsKey((Object) nickname)) {
                        try {
                            String otpApp;
                            userMap.lock(nickname);
                            UserCacheModel user = (UserCacheModel) userMap.get((Object) nickname);
                            if ((user.getOtpApp() == null || !user.getOtpApp().equals(otp)) && (otpApp = VinPlayUtils.genOtpApp((String) nickname, (String) mobile)).equals(otp)) {
                                user.setOtpApp(otp);
                                userMap.put(nickname, user);
                                res = 0;
                            }
                            break block17;
                        } catch (Exception e) {
                            logger.debug((Object) e);
                            break block17;
                        } finally {
                            userMap.unlock(nickname);
                        }
                    }
                    String otpApp2 = VinPlayUtils.genOtpApp((String) nickname, (String) mobile);
                    if (otpApp2.equals(otp)) {
                        res = 0;
                    }
                } catch (Exception e2) {
                    logger.debug((Object) e2);
                }
            }
            return res;
        }
        if (otp.equals(GameCommon.getValueStr("OTP_DEFAULT"))) {
            return 0;
        }
        return 3;
    }

    @Override
    public int getOdp(String nickname) throws Exception {
        int code = 1;
        HazelcastInstance client = HazelcastClientFactory.getInstance();
        IMap<String, UserModel> userMap = client.getMap("users");
        UserModel model = null;
        if (userMap.containsKey((Object) nickname)) {
            model = (UserModel) userMap.get((Object) nickname);
            UserCacheModel userCacheModel = (UserCacheModel) model;
        } else {
            UserDaoImpl dao = new UserDaoImpl();
            model = dao.getUserByNickName(nickname);
        }
        if (model != null) {
            if (model.getDaily() != 0) {
                if (model.getMobile() != null && !model.getMobile().isEmpty() && model.isHasMobileSecurity()) {
                    OtpDaoImpl otpDao = new OtpDaoImpl();
                    OtpModel otpModel = otpDao.getOtpSMS(model.getMobile(), "OZZ ODP");
                    if (otpModel != null && VinPlayUtils.compareDate((Date) new Date(), (Date) otpModel.getOtpTime()) == 0) {
                        code = 5;
                    } else {
                        String odp = VinPlayUtils.genOtpSMS((String) model.getMobile(), (String) "OZZ ODP");
                        otpDao.updateOtpSMS(model.getMobile(), odp, "OZZ ODP");
                        AlertServiceImpl service = new AlertServiceImpl();
                        String content = String.format(GameCommon.MESSAGE_ODP_SUCCESS, odp, VinPlayUtils.getCurrentDate());
                        if ("ESMS".equals(PartnerConfig.SMSPartner)) {
                            boolean rs = service.SendSMSEsms(model.getMobile(), odp);
                        } else if ("ESMS_VOICE".equals(PartnerConfig.SMSPartner)) {
                            boolean rs = service.SendVoiceOTPESMS(model.getMobile(), odp);
                        } else if ("RUTCUOC".equals(PartnerConfig.SMSPartner)) {
                            boolean rs = service.SendSMSRutCuoc(model.getMobile(), odp);
                        } else if ("SMS_BRANDNAME".equals(PartnerConfig.SMSPartner)) {
                            boolean rs = service.SendSmsBrandName(model.getMobile(), odp);
                        } else {
                            boolean rs = service.SendSMSAirpay(model.getMobile(), odp);
                        }
                        code = 0;
                    }
                } else {
                    code = 4;
                }
            } else {
                code = 3;
            }
        } else {
            code = 2;
        }
        return code;
    }

    @Override
    public int getVoiceOdp(String nickname) throws Exception {
        int code = 1;
        HazelcastInstance client = HazelcastClientFactory.getInstance();
        IMap<String, UserModel> userMap = client.getMap("users");
        UserModel model = null;
        if (userMap.containsKey((Object) nickname)) {
            model = (UserModel) userMap.get((Object) nickname);
            UserCacheModel userCacheModel = (UserCacheModel) model;
        } else {
            UserDaoImpl dao = new UserDaoImpl();
            model = dao.getUserByNickName(nickname);
        }
        if (model != null) {
            if (model.getDaily() != 0) {
                if (model.getMobile() != null && !model.getMobile().isEmpty() && model.isHasMobileSecurity()) {
                    OtpDaoImpl otpDao = new OtpDaoImpl();
                    OtpModel otpModel = otpDao.getOtpSMS(model.getMobile(), "OZZ ODP");
                    if (otpModel != null && VinPlayUtils.compareDate((Date) new Date(), (Date) otpModel.getOtpTime()) == 0) {
                        code = 5;
                    } else {
                        String odp = VinPlayUtils.genOtpSMS((String) model.getMobile(), (String) "OZZ ODP");
                        otpDao.updateOtpSMS(model.getMobile(), odp, "OZZ ODP");
                        AlertServiceImpl service = new AlertServiceImpl();
                        String content = String.format(GameCommon.MESSAGE_ODP_SUCCESS, odp, VinPlayUtils.getCurrentDate());
                        if ("ESMS".equals(PartnerConfig.SMSPartner)) {
                            boolean rs = service.SendSMSEsms(model.getMobile(), odp);
                        } else if ("ESMS_VOICE".equals(PartnerConfig.SMSPartner)) {
                            boolean rs = service.SendVoiceOTPESMS(model.getMobile(), odp);
                        } else if ("RUTCUOC".equals(PartnerConfig.SMSPartner)) {
                            boolean rs = service.SendSMSRutCuoc(model.getMobile(), odp);
                        } else if ("SMS_BRANDNAME".equals(PartnerConfig.SMSPartner)) {
                            boolean rs = service.SendSmsBrandName(model.getMobile(), odp);
                        } else {
                            boolean rs = service.SendSMSAirpay(model.getMobile(), odp);
                        }
                        code = 0;
                    }
                } else {
                    code = 4;
                }
            } else {
                code = 3;
            }
        } else {
            code = 2;
        }
        return code;
    }

    @Override
    public int getOdp(String nickname, String type) throws Exception {
        int code = 1;
        HazelcastInstance client = HazelcastClientFactory.getInstance();
        IMap<String, UserModel> userMap = client.getMap("users");
        UserModel model = null;
        if (userMap.containsKey((Object) nickname)) {
            model = (UserModel) userMap.get((Object) nickname);
            UserCacheModel userCacheModel = (UserCacheModel) model;
        } else {
            UserDaoImpl dao = new UserDaoImpl();
            model = dao.getUserByNickName(nickname);
        }
        if (model != null) {
            if (model.getDaily() != 0) {
                if (model.getMobile() != null && !model.getMobile().isEmpty() && model.isHasMobileSecurity()) {
                    OtpDaoImpl otpDao = new OtpDaoImpl();
                    OtpModel otpModel = otpDao.getOtpSMS(model.getMobile(), "OZZ ODP");
                    if (otpModel != null && VinPlayUtils.compareDate((Date) new Date(), (Date) otpModel.getOtpTime()) == 0) {
                        code = 5;
                    } else {
                        String odp = VinPlayUtils.genOtpSMS((String) model.getMobile(), (String) "OZZ ODP");
                        otpDao.updateOtpSMS(model.getMobile(), odp, "OZZ ODP");
                        AlertServiceImpl service = new AlertServiceImpl();
                        String content = String.format(GameCommon.MESSAGE_ODP_SUCCESS, odp, VinPlayUtils.getCurrentDate());
                        if ("ESMS".equals(PartnerConfig.SMSPartner)) {
                            boolean rs = service.SendSMSEsms(model.getMobile(), odp);
                        } else if ("ESMS_VOICE".equals(PartnerConfig.SMSPartner)) {
                            boolean rs = service.SendVoiceOTPESMS(model.getMobile(), odp);
                        } else if ("RUTCUOC".equals(PartnerConfig.SMSPartner)) {
                            boolean rs = service.SendSMSRutCuoc(model.getMobile(), odp);
                        } else if ("SMS_BRANDNAME".equals(PartnerConfig.SMSPartner)) {
                            boolean rs = service.SendSmsBrandName(model.getMobile(), odp);
                        } else {
                            boolean rs = service.SendSMSAirpay(model.getMobile(), odp);
                        }
                        code = 0;
                    }
                } else {
                    code = 4;
                }
            } else {
                code = 3;
            }
        } else {
            code = 2;
        }
        return code;
    }

    @Override
    public int checkOdp(String nickname, String odp) throws Exception {
        OtpDaoImpl otpDao;
        OtpModel otpModel;
        if (this.checkAppOTP(nickname, odp) == 0) {
            return 0;
        }
        int code = 1;
        if (odp == null || odp.length() != 5) {
            code = 5;
            return code;
        }
        HazelcastInstance client = HazelcastClientFactory.getInstance();
        IMap<String, UserModel> userMap = client.getMap("users");
        UserModel model = null;
        if (userMap.containsKey((Object) nickname)) {
            model = (UserModel) userMap.get((Object) nickname);
            UserCacheModel userCacheModel = (UserCacheModel) model;
        } else {
            UserDaoImpl dao = new UserDaoImpl();
            model = dao.getUserByNickName(nickname);
        }
        code = model != null ?
                (model.getDaily() != 0 ?
                        (model.getMobile() != null && !model.getMobile().isEmpty() && model.isHasMobileSecurity() ?
                                ((otpModel = (otpDao = new OtpDaoImpl()).getOtpSMS(model.getMobile(), "OZZ ODP")) != null && odp.equals(otpModel.getOtp()) ?
                                        (VinPlayUtils.compareDate((Date) new Date(), (Date) otpModel.getOtpTime()) == 0 ? 0 : 6)
                                        : 5)
                                : 4)
                        : 3)
                : 2;
        return code;
    }

    public BaseResponseModel checkOTP(String nickname, String otp) throws Exception {
        BaseResponseModel baseResponseModel = new BaseResponseModel(true, "1001");
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> collection = db.getCollection("user_tele");
        Document filter = new Document();
        filter.put("nickname", nickname);
        filter.put("otp", otp);
        MongoCursor<Document> cursor = collection.find(filter).iterator();

        try {
            if (cursor.hasNext()) {
                Document doc = cursor.next();
                long timeToExpired = doc.getInteger("timeToExpired");
                String createdDateStr = doc.getString("createdDate");
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date createdDate = dateFormat.parse(createdDateStr);
                Date currentTime = new Date();
                long expiredTimeMillis = createdDate.getTime() + timeToExpired;

                if (currentTime.getTime() > expiredTimeMillis) {
                    baseResponseModel.setSuccess(false);
                    baseResponseModel.setErrorCode("Code hết hạn");
                } else {
                    baseResponseModel.setSuccess(true);
                    OtherService otherService = new OtherServiceImpl();
                    otherService.activeUserTele(nickname);
                    baseResponseModel.setErrorCode("OK");
                }
            } else {
                baseResponseModel.setSuccess(false);
                baseResponseModel.setErrorCode("Code không hợp lệ");
            }
        } finally {
            cursor.close();
        }
        return baseResponseModel;
    }

    @Override
    public int checkOtpSmsForApp(String nickname, String otp) throws Exception {
        if (GameCommon.getValueStr("OTP_DEFAULT").isEmpty()) {
            int res = 3;
            if (this.checkAppOTP(nickname, otp) == 0) {
                return 0;
            }
            return res;
        }
        if (otp.equals(GameCommon.getValueStr("OTP_DEFAULT"))) {
            return 0;
        }
        return 3;
    }

    @Override
    public int getEsmsOTP(String nickname, String mobile, String type) throws Exception {
        int code = 1;
        HazelcastInstance client = HazelcastClientFactory.getInstance();
        IMap<String, UserModel> userMap = client.getMap("users");
        UserModel model = null;
        if (userMap.containsKey((Object) nickname)) {
            model = (UserModel) userMap.get((Object) nickname);
            UserCacheModel userCacheModel = (UserCacheModel) model;
        } else {
            UserDaoImpl dao = new UserDaoImpl();
            model = dao.getUserByNickName(nickname);
        }
        if (model != null) {
            if (model.getMobile() != null && !model.getMobile().isEmpty() && model.isHasMobileSecurity()) {
                OtpDaoImpl otpDao = new OtpDaoImpl();
                String mobile2 = this.revertMobile(model.getMobile());
                String otp = null;
                try {
                    otp = VinPlayUtils.genOtpSMS((String) model.getMobile(), (String) "");
                } catch (Exception e) {
                    logger.debug((Object) e);
                }
                otpDao.updateOtpSMS(model.getMobile(), otp, "OZZ OTP");
                AlertServiceImpl service = new AlertServiceImpl();

                String content = String.format(GameCommon.MESSAGE_OTP_SUCCESS, otp, VinPlayUtils.getCurrentDate());
                if ("ESMS".equals(PartnerConfig.SMSPartner)) {
                    boolean rs = service.SendSMSEsms(model.getMobile(), otp);
                } else if ("ESMS_VOICE".equals(PartnerConfig.SMSPartner)) {
                    boolean rs = service.SendVoiceOTPESMS(model.getMobile(), otp);
                } else if ("RUTCUOC".equals(PartnerConfig.SMSPartner)) {
                    boolean rs = service.SendSMSRutCuoc(model.getMobile(), otp);
                } else if ("SMS_BRANDNAME".equals(PartnerConfig.SMSPartner)) {
                    boolean rs = service.SendSmsBrandName(model.getMobile(), otp);
                } else {
                    boolean rs = service.SendSMSAirpay(model.getMobile(), otp);
                }
                code = 0;
            } else {
                code = 4;
            }
        } else {
            code = 2;
        }
        return code;
    }

    @Override
    public String GenerateOTP(String nickname, String mobile) throws Exception {
        int code = 1;
        HazelcastInstance client = HazelcastClientFactory.getInstance();
        IMap<String, UserModel> userMap = client.getMap("users");
        UserModel model = null;
        if (userMap.containsKey((Object) nickname)) {
            model = (UserModel) userMap.get((Object) nickname);
            UserCacheModel userCacheModel = (UserCacheModel) model;
        } else {
            UserDaoImpl dao = new UserDaoImpl();
            model = dao.getUserByNickName(nickname);
        }
        if (model != null) {
            if (model.getMobile() != null && !model.getMobile().isEmpty()) {
                OtpDaoImpl otpDao = new OtpDaoImpl();
                String mobile2 = this.revertMobile(model.getMobile());
                String otp = null;
                try {
                    otp = VinPlayUtils.genOtpSMS((String) model.getMobile(), (String) "");
                } catch (Exception e) {
                    logger.debug((Object) e);
                }
                otpDao.updateOtpSMS(model.getMobile(), otp, "OZZ OTP");
                return otp;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public int checkOtpEsms(String nickname, String odp) throws Exception {
        OtpDaoImpl otpDao;
        OtpModel otpModel;
        if (this.checkAppOTP(nickname, odp) == 0) {
            return 0;
        }
        int code = 1;
        if (odp == null || odp.length() != 5) {
            code = 5;
            return code;
        }
        HazelcastInstance client = HazelcastClientFactory.getInstance();
        IMap<String, UserModel> userMap = client.getMap("users");
        UserModel model = null;
        if (userMap.containsKey((Object) nickname)) {
            model = (UserModel) userMap.get((Object) nickname);
            UserCacheModel userCacheModel = (UserCacheModel) model;
        } else {
            UserDaoImpl dao = new UserDaoImpl();
            model = dao.getUserByNickName(nickname);
        }
        code = model != null ? (model.getDaily() != 0 ? (model.getMobile() != null && !model.getMobile().isEmpty() && model.isHasMobileSecurity() ? ((otpModel = (otpDao = new OtpDaoImpl()).getOtpSMS(model.getMobile(), "OZZ ODP")) != null && odp.equals(otpModel.getOtp()) ? (VinPlayUtils.compareDate((Date) new Date(), (Date) otpModel.getOtpTime()) == 0 ? 0 : 6) : 5) : 4) : 3) : 2;
        return code;
    }

    @Override
    public int checkAppOTP(String nickname, String otp) {
        int res = 3;
        if (otp != null && otp.length() == 6) {
            try {
                String Secret = VinPlayUtils.getUserSecretKey((String) nickname);
                int INTOTP = Integer.parseInt(otp);
                if (TimeBasedOneTimePasswordUtil.validateCurrentNumber((String) Secret, (int) INTOTP, (int) 0)) {
                    res = 0;
                }
            } catch (Exception e) {
                logger.debug((Object) e);
            }
        }
        return res;
    }


    @Override
    public int sendOtpEsms(String nickname, String mobile) throws Exception {
        int res;
        res = 3;
        HazelcastInstance client = HazelcastClientFactory.getInstance();
        IMap<String, UserModel> userMap = client.getMap("users");
        UserModel model = null;
        if (userMap.containsKey(mobile)) {
            model = (UserModel) userMap.get((Object) nickname);
            UserCacheModel userCacheModel = (UserCacheModel) model;
        } else {
            UserDaoImpl userDao = new UserDaoImpl();
            model = userDao.getUserByNickName(nickname);
        }
        if (model == null) return res;
        mobile = mobile == "" ? model.getMobile() : mobile;
        if (mobile == null || mobile.isEmpty()) return res;

        try {
            if (userMap.containsKey((Object) nickname)) {
                userMap.lock(nickname);
            }

            String otp = StringUtils.randomStringNumber(5);

            OtpDaoImpl otpDao = new OtpDaoImpl();
            OtpModel checkResponse = CheckValidSMS(nickname);
            if (checkResponse != null && checkResponse.getCommandCode().equals("OK")) {
                if (checkResponse.getCount() >= 1 && model.getVin() > 1000) {
                    // check sms count           
                    otpDao.updateOtpSMS(model.getMobile(), otp, "OZZ OTP", checkResponse.getCount() + 1);
                    AlertServiceImpl service = new AlertServiceImpl();
                    if ("ESMS".equals(PartnerConfig.SMSPartner)) {
                        boolean rs = service.SendSMSEsms(model.getMobile(), otp);
                    } else if ("ESMS_VOICE".equals(PartnerConfig.SMSPartner)) {
                        boolean rs = service.SendVoiceOTPESMS(model.getMobile(), otp);
                    } else if ("RUTCUOC".equals(PartnerConfig.SMSPartner)) {
                        boolean rs = service.SendSMSRutCuoc(model.getMobile(), otp);
                    } else if ("SMS_BRANDNAME".equals(PartnerConfig.SMSPartner)) {
                        boolean rs = service.SendSmsBrandName(model.getMobile(), otp);
                    } else {
                        boolean rs = service.SendSMSAirpay(model.getMobile(), otp);
                    }
                    res = 0;
                    if (checkResponse.getCount() >= 2) {
                        //get user
                        userMap = client.getMap("users");
                        UserCacheModel user = (UserCacheModel) userMap.get((Object) nickname);
                        user = (UserCacheModel) userMap.get((Object) nickname);
                        String description;
                        long moneyUser = user.getVin();
                        long currentMoney = user.getVinTotal();

                        long money = -1000;
                        user.setVin(moneyUser += money);
                        user.setVinTotal(currentMoney += money);
                        description = "Thanh toan phi SMS";
                        MoneyMessageInMinigame messageMoney = new MoneyMessageInMinigame(VinPlayUtils.genMessageId(), user.getId(), nickname, "RechargeByCard", moneyUser, currentMoney, money, "vin", 0L, 0, 0);
                        LogMoneyUserMessage messageLog = new LogMoneyUserMessage(user.getId(), nickname, "SMSFee", "Thanh toan phi SMS", currentMoney, money, "vin", description, 0L, false, user.isBot());
                        RMQApi.publishMessagePayment((BaseMessage) messageMoney, (int) 16);
                        RMQApi.publishMessageLogMoney((LogMoneyUserMessage) messageLog);
                        userMap.put(nickname, user);
                    }
                } else if (checkResponse.getCount() < 2) {
                    otpDao.updateOtpSMS(model.getMobile(), otp, "OZZ OTP");
                    AlertServiceImpl service = new AlertServiceImpl();
                    if ("ESMS".equals(PartnerConfig.SMSPartner)) {
                        boolean rs = service.SendSMSEsms(model.getMobile(), otp);
                    } else if ("ESMS_VOICE".equals(PartnerConfig.SMSPartner)) {
                        boolean rs = service.SendVoiceOTPESMS(model.getMobile(), otp);
                    } else if ("RUTCUOC".equals(PartnerConfig.SMSPartner)) {
                        boolean rs = service.SendSMSRutCuoc(model.getMobile(), otp);
                    } else if ("SMS_BRANDNAME".equals(PartnerConfig.SMSPartner)) {
                        boolean rs = service.SendSmsBrandName(model.getMobile(), otp);
                    } else {
                        boolean rs = service.SendSMSAirpay(model.getMobile(), otp);
                    }
                    res = 0;
                } else {
                    // cco gui sms k
                    res = 30;
                }
            } else if (checkResponse != null && checkResponse.getCommandCode().equals("FIRST_TIME")) {
                otpDao.updateOtpSMSFirst(model.getMobile(), otp, "OZZ OTP");
                AlertServiceImpl service = new AlertServiceImpl();
                if ("ESMS".equals(PartnerConfig.SMSPartner)) {
                    boolean rs = service.SendSMSEsms(model.getMobile(), otp);
                } else if ("ESMS_VOICE".equals(PartnerConfig.SMSPartner)) {
                    boolean rs = service.SendVoiceOTPESMS(model.getMobile(), otp);
                } else if ("RUTCUOC".equals(PartnerConfig.SMSPartner)) {
                    boolean rs = service.SendSMSRutCuoc(model.getMobile(), otp);
                } else if ("SMS_BRANDNAME".equals(PartnerConfig.SMSPartner)) {
                    boolean rs = service.SendSmsBrandName(model.getMobile(), otp);
                } else {
                    boolean rs = service.SendSMSAirpay(model.getMobile(), otp);
                }
                res = 0;
            } else {
                res = 30;
            }
        } catch (Exception e) {
            logger.debug((Object) e);
        } finally {
            if (userMap.containsKey((Object) nickname)) {
                userMap.unlock(nickname);
            }
        }

        return res;
    }

    @Override
    public int sendVoiceOtp(String nickname, String mobile, boolean forceCheck) throws Exception {
        int res;
        res = 3;
        HazelcastInstance client = HazelcastClientFactory.getInstance();
        IMap<String, UserModel> userMap = client.getMap("users");
        UserModel model = null;
        if (userMap.containsKey(mobile)) {
            model = (UserModel) userMap.get((Object) nickname);
            UserCacheModel userCacheModel = (UserCacheModel) model;
        } else {
            UserDaoImpl userDao = new UserDaoImpl();
            model = userDao.getUserByNickName(nickname);
        }
        if (model == null) return res;
        mobile = mobile == "" ? model.getMobile() : mobile;
        if (mobile == null || mobile.isEmpty()) return res;

        try {
            if (userMap.containsKey((Object) nickname)) {
                userMap.lock(nickname);
            }

            String otp = StringUtils.randomStringNumber(5);

            OtpDaoImpl otpDao = new OtpDaoImpl();
            OtpModel checkResponse = CheckValidSMS(nickname);
            if (!forceCheck)
                checkResponse.setCommandCode("OK");
            if (checkResponse != null && checkResponse.getCommandCode().equals("OK")) {
                if (checkResponse.getCount() >= 1 && model.getVin() > 1000) {
                    // check sms count           
                    otpDao.updateOtpSMS(mobile, otp, "OZZ OTP", checkResponse.getCount() + 1);
                    AlertServiceImpl service = new AlertServiceImpl();
                    if ("ESMS".equals(PartnerConfig.SMSPartner)) {
                        boolean rs = service.SendSMSEsms(mobile, otp);
                    } else if ("ESMS_VOICE".equals(PartnerConfig.SMSPartner)) {
                        boolean rs = service.SendVoiceOTPESMS(mobile, otp);
                    } else if ("RUTCUOC".equals(PartnerConfig.SMSPartner)) {
                        boolean rs = service.SendSMSRutCuoc(mobile, otp);
                    } else if ("SMS_BRANDNAME".equals(PartnerConfig.SMSPartner)) {
                        boolean rs = service.SendSmsBrandName(mobile, otp);
                    } else {
                        boolean rs = service.SendSMSAirpay(mobile, otp);
                    }
                    res = 0;
                    if (checkResponse.getCount() >= 2) {
                        //get user
                        userMap = client.getMap("users");
                        UserCacheModel user = (UserCacheModel) userMap.get((Object) nickname);
                        user = (UserCacheModel) userMap.get((Object) nickname);
                        String description;
                        long moneyUser = user.getVin();
                        long currentMoney = user.getVinTotal();

                        long money = -1000;
                        user.setVin(moneyUser += money);
                        user.setVinTotal(currentMoney += money);
                        description = "Thanh toan phi SMS";
                        MoneyMessageInMinigame messageMoney = new MoneyMessageInMinigame(VinPlayUtils.genMessageId(), user.getId(), nickname, "RechargeByCard", moneyUser, currentMoney, money, "vin", 0L, 0, 0);
                        LogMoneyUserMessage messageLog = new LogMoneyUserMessage(user.getId(), nickname, "SMSFee", "Thanh toan phi SMS", currentMoney, money, "vin", description, 0L, false, user.isBot());
                        RMQApi.publishMessagePayment((BaseMessage) messageMoney, (int) 16);
                        RMQApi.publishMessageLogMoney((LogMoneyUserMessage) messageLog);
                        userMap.put(nickname, user);
                    }
                } else if (checkResponse.getCount() < 2) {
                    otpDao.updateOtpSMS(mobile, otp, "OZZ OTP");
                    AlertServiceImpl service = new AlertServiceImpl();
                    if ("ESMS".equals(PartnerConfig.SMSPartner)) {
                        boolean rs = service.SendSMSEsms(mobile, otp);
                    } else if ("ESMS_VOICE".equals(PartnerConfig.SMSPartner)) {
                        boolean rs = service.SendVoiceOTPESMS(mobile, otp);
                    } else if ("RUTCUOC".equals(PartnerConfig.SMSPartner)) {
                        boolean rs = service.SendSMSRutCuoc(mobile, otp);
                    } else if ("SMS_BRANDNAME".equals(PartnerConfig.SMSPartner)) {
                        boolean rs = service.SendSmsBrandName(mobile, otp);
                    } else {
                        boolean rs = service.SendSMSAirpay(mobile, otp);
                    }
                    res = 0;
                } else {
                    // cco gui sms k
                    res = 30;
                }
            } else if (checkResponse != null && checkResponse.getCommandCode().equals("FIRST_TIME")) {
                otpDao.updateOtpSMSFirst(mobile, otp, "OZZ OTP");
                AlertServiceImpl service = new AlertServiceImpl();
                if ("ESMS".equals(PartnerConfig.SMSPartner)) {
                    boolean rs = service.SendSMSEsms(mobile, otp);
                } else if ("ESMS_VOICE".equals(PartnerConfig.SMSPartner)) {
                    boolean rs = service.SendVoiceOTPESMS(mobile, otp);
                } else if ("RUTCUOC".equals(PartnerConfig.SMSPartner)) {
                    boolean rs = service.SendSMSRutCuoc(mobile, otp);
                } else if ("SMS_BRANDNAME".equals(PartnerConfig.SMSPartner)) {
                    boolean rs = service.SendSmsBrandName(mobile, otp);
                } else {
                    boolean rs = service.SendSMSAirpay(mobile, otp);
                }
                res = 0;
            } else {
                res = 30;
            }
        } catch (Exception e) {
            logger.debug((Object) e);
        } finally {
            if (userMap.containsKey((Object) nickname)) {
                userMap.unlock(nickname);
            }
        }

        return res;
    }

    @Override
    public int sendOdpEsms(String nickname, String mobile) throws Exception {
        int res;
        res = 3;
        HazelcastInstance client = HazelcastClientFactory.getInstance();
        IMap<String, UserModel> userMap = client.getMap("users");
        UserModel model = null;
        if (userMap.containsKey(mobile)) {
            model = (UserModel) userMap.get((Object) nickname);
            UserCacheModel userCacheModel = (UserCacheModel) model;
        } else {
            UserDaoImpl userDao = new UserDaoImpl();
            model = userDao.getUserByNickName(nickname);
        }
        if (model == null) return res;
        mobile = mobile == null ? model.getMobile() : mobile;
        if (mobile == null || mobile.isEmpty()) return res;

        try {
            if (userMap.containsKey((Object) nickname)) {
                userMap.lock(nickname);
            }

            String odp = StringUtils.randomStringNumber(5);

            OtpDaoImpl otpDao = new OtpDaoImpl();

            otpDao.updateOtpSMS(model.getMobile(), odp, "OZZ ODP");
            AlertServiceImpl service = new AlertServiceImpl();
            if ("ESMS".equals(PartnerConfig.SMSPartner)) {
                boolean rs = service.SendSMSEsms(model.getMobile(), odp);
            } else if ("ESMS_VOICE".equals(PartnerConfig.SMSPartner)) {
                boolean rs = service.SendVoiceOTPESMS(model.getMobile(), odp);
            } else if ("RUTCUOC".equals(PartnerConfig.SMSPartner)) {
                boolean rs = service.SendSMSRutCuoc(model.getMobile(), odp);
            } else if ("SMS_BRANDNAME".equals(PartnerConfig.SMSPartner)) {
                boolean rs = service.SendSmsBrandName(mobile, odp);
            } else {
                boolean rs = service.SendSMSAirpay(model.getMobile(), odp);
            }
            res = 0;
        } catch (Exception e) {
            logger.debug((Object) e);
        } finally {
            if (userMap.containsKey((Object) nickname)) {
                userMap.unlock(nickname);
            }
        }

        return res;
    }

    @Override
    public String GenerateOdp(String nickname, String mobile) throws Exception {
        int res;
        res = 3;
        HazelcastInstance client = HazelcastClientFactory.getInstance();
        IMap<String, UserModel> userMap = client.getMap("users");
        UserModel model = null;
        if (userMap.containsKey(mobile)) {
            model = (UserModel) userMap.get((Object) nickname);
            UserCacheModel userCacheModel = (UserCacheModel) model;
        } else {
            UserDaoImpl userDao = new UserDaoImpl();
            model = userDao.getUserByNickName(nickname);
        }
        if (model == null) return null;
        mobile = mobile == null ? model.getMobile() : mobile;
        if (mobile == null || mobile.isEmpty()) return null;

        try {
            if (userMap.containsKey((Object) nickname)) {
                userMap.lock(nickname);
            }

            String odp = StringUtils.randomStringNumber(5);

            OtpDaoImpl otpDao = new OtpDaoImpl();

            otpDao.updateOtpSMS(model.getMobile(), odp, "OZZ ODP");
            return odp;
        } catch (Exception e) {
            logger.debug((Object) e);
        } finally {
            if (userMap.containsKey((Object) nickname)) {
                userMap.unlock(nickname);
            }
        }

        return null;
    }
}

