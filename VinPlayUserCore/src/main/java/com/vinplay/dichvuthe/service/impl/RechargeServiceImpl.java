/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.core.IMap
 *  com.vinplay.vbee.common.enums.I2BType
 *  com.vinplay.vbee.common.enums.PhoneCardType
 *  com.vinplay.vbee.common.enums.ProviderType
 *  com.vinplay.vbee.common.hazelcast.HazelcastClientFactory
 *  com.vinplay.vbee.common.messages.BaseMessage
 *  com.vinplay.vbee.common.messages.LogMoneyUserMessage
 *  com.vinplay.vbee.common.messages.MoneyMessageInMinigame
 *  com.vinplay.vbee.common.messages.dvt.RechargeByBankMessage
 *  com.vinplay.vbee.common.messages.dvt.RechargeByCardMessage
 *  com.vinplay.vbee.common.models.UserModel
 *  com.vinplay.vbee.common.models.cache.ApiOtpModel
 *  com.vinplay.vbee.common.models.cache.UserCacheModel
 *  com.vinplay.vbee.common.rmq.RMQApi
 *  com.vinplay.vbee.common.utils.UserValidaton
 *  com.vinplay.vbee.common.utils.VinPlayUtils
 *  org.apache.log4j.Logger
 *  org.json.JSONObject
 */
package com.vinplay.dichvuthe.service.impl;

//import bitzero.util.common.business.Debug;

import com.google.gson.Gson;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.bank.BankGenTrainID;
import com.vinplay.common.notification.NotificationAdminObj;
import com.vinplay.common.notification.SendToWS;
import com.vinplay.dal.common.BroadCastUserMoney;
import com.vinplay.dichvuthe.client.DvtAlert;
import com.vinplay.dichvuthe.client.HttpURLClient;
import com.vinplay.dichvuthe.client.VinplayClient;
import com.vinplay.dichvuthe.dao.RechargeDao;
import com.vinplay.dichvuthe.dao.impl.RechargeDaoImpl;
import com.vinplay.dichvuthe.entities.*;
import com.vinplay.dichvuthe.response.*;
import com.vinplay.dichvuthe.service.RechargeService;
import com.vinplay.dichvuthe.utils.DvtConst;
import com.vinplay.dichvuthe.utils.DvtUtils;
import com.vinplay.dichvuthe.utils.NapasUtils;
import com.vinplay.dichvuthe.utils.NganLuongUtils;
import com.vinplay.epay.megacard.ChargeReponse;
import com.vinplay.epay.megacard.EpayMegaCardAlert;
import com.vinplay.epay.megacard.EpayMegaCardCharging;
import com.vinplay.gachthe.*;
import com.vinplay.iap.lib.Purchase;
import com.vinplay.iap.lib.Security;
import com.vinplay.lognaprut.HistoryTransConst;
import com.vinplay.lognaprut.HistoryTransDao;
import com.vinplay.lognaprut.entities.HistoryTransModel;
import com.vinplay.lognaprut.impl.HistoryTransDaoImpl;
import com.vinplay.lognaprut.service.HistoryTransService;
import com.vinplay.lognaprut.service.impl.HistoryTransServiceImpl;
import com.vinplay.lucky79.Lucky79Alert;
import com.vinplay.lucky79.Lucky79Client;
import com.vinplay.lucky79.Lucky79Exception;
import com.vinplay.lucky79.TheCaoResponse;
import com.vinplay.maxpay.*;
import com.vinplay.usercore.dao.impl.UserDaoImpl;
import com.vinplay.usercore.entities.IAPModel;
import com.vinplay.usercore.logger.MoneyLogger;
import com.vinplay.usercore.service.impl.MailBoxServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.usercore.utils.PartnerConfig;
import com.vinplay.utils.TelegramAlert;
import com.vinplay.vbee.common.enums.I2BType;
import com.vinplay.vbee.common.enums.PhoneCardType;
import com.vinplay.vbee.common.enums.ProviderType;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.messages.BaseMessage;
import com.vinplay.vbee.common.messages.LogMoneyUserMessage;
import com.vinplay.vbee.common.messages.MoneyMessageInMinigame;
import com.vinplay.vbee.common.messages.dvt.RechargeByBankMessage;
import com.vinplay.vbee.common.messages.dvt.RechargeByCardMessage;
import com.vinplay.vbee.common.models.BankPartnerModel;
import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.models.cache.ApiOtpModel;
import com.vinplay.vbee.common.models.cache.UserCacheModel;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.response.UserInfoModel;
import com.vinplay.vbee.common.rmq.HttpCommon;
import com.vinplay.vbee.common.rmq.RMQApi;
import com.vinplay.vbee.common.statics.Consts;
import com.vinplay.vbee.common.utils.UserValidaton;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import com.vinplay.vtc.VTCAlert;
import com.vinplay.vtc.VTCRechargeClient;
import com.vinplay.vtc.VTCRechargeResponse;
import okhttp3.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class RechargeServiceImpl
        implements RechargeService {
    public static final Logger logger = Logger.getLogger((String) "recharge");
    public BankGenTrainID banktrain;

    @Override
    public RechargeResponse rechargeByCard(String nickname, ProviderType provider, String serial, String pin, String amount, String platform) throws Exception {
        String name;
        logger.debug((Object) ("Start rechargeByCard ham cha:  nickname: " + nickname + ", provider: " + provider.getName() + ", serial: " + serial + ", pin: " + pin + ", platform:" + platform));
        RechargeResponse response = null;
        block6:
        switch (name = provider.getName()) {
            case "Viettel": {
                String valueStr2;
                String valueStr;
                String providername = GameCommon.getValueStr("RECHARGE_VTT_PRIMARY");
                switch (valueStr = GameCommon.getValueStr("RECHARGE_VTT_PRIMARY")) {
                    case "maxpay": {
                        response = this.rechargeByCardMaxpay(nickname, provider, serial, pin, platform);
                        break;
                    }
                    case "lucky79": {
                        response = this.rechargeByCardLucky79(nickname, provider, serial, pin, amount, platform);
                    }
                }
                if (response != null) break;
                switch (valueStr2 = GameCommon.getValueStr("RECHARGE_VTT_BACKUP")) {
                    case "maxpay": {
                        response = this.rechargeByCardMaxpay(nickname, provider, serial, pin, platform);
                        break block6;
                    }
                    case "lucky79": {
                        response = this.rechargeByCardLucky79(nickname, provider, serial, pin, amount, platform);
                    }
                }
                break;
            }
            case "Mobifone": {
                String valueStr4;
                String valueStr3;
                switch (valueStr3 = GameCommon.getValueStr("RECHARGE_VMS_PRIMARY")) {
                    case "maxpay": {
                        response = this.rechargeByCardMaxpay(nickname, provider, serial, pin, platform);
                        break;
                    }
                    case "lucky79": {
                        response = this.rechargeByCardLucky79(nickname, provider, serial, pin, amount, platform);
                    }
                }
                if (response != null) break;
                switch (valueStr4 = GameCommon.getValueStr("RECHARGE_VMS_BACKUP")) {
                    case "maxpay": {
                        response = this.rechargeByCardMaxpay(nickname, provider, serial, pin, platform);
                        break block6;
                    }
                    case "lucky79": {
                        response = this.rechargeByCardLucky79(nickname, provider, serial, pin, amount, platform);
                    }
                }
                break;
            }
            case "Vinaphone": {
                String valueStr5;
                String valueStr6;
                switch (valueStr5 = GameCommon.getValueStr("RECHARGE_VNP_PRIMARY")) {
                    case "maxpay": {
                        response = this.rechargeByCardMaxpay(nickname, provider, serial, pin, platform);
                        break;
                    }
                    case "lucky79": {
                        response = this.rechargeByCardLucky79(nickname, provider, serial, pin, amount, platform);
                    }
                }
                if (response != null) break;
                switch (valueStr6 = GameCommon.getValueStr("RECHARGE_VNP_BACKUP")) {
                    case "maxpay": {
                        response = this.rechargeByCardMaxpay(nickname, provider, serial, pin, platform);
                        break block6;
                    }
                    case "lucky79": {
                        response = this.rechargeByCardLucky79(nickname, provider, serial, pin, amount, platform);
                    }
                }
                break;
            }
            case "Gate": {
                String valueStr7;
                String valueStr8;
                switch (valueStr7 = GameCommon.getValueStr("RECHARGE_GATE_PRIMARY")) {
                    case "maxpay": {
                        response = this.rechargeByCardMaxpay(nickname, provider, serial, pin, platform);
                    }
                }
                if (response != null) break;
                switch (valueStr8 = GameCommon.getValueStr("RECHARGE_GATE_BACKUP")) {
                    case "maxpay": {
                        response = this.rechargeByCardMaxpay(nickname, provider, serial, pin, platform);
                    }
                }
                break;
            }
        }
        if (response == null) {
            response = new RechargeResponse(-1, 0L, 0, 0L);
            RechargeByCardMessage message = new RechargeByCardMessage(nickname, "", provider.getName(), serial, pin, 0, -1, "M\u1ea5t k\u1ebft n\u1ed1i t\u1ea5t c\u1ea3 c\u00e1c partner", -1, 0, (String) null, (String) null, "", platform, (String) null);
            RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 301);
        }
        logger.debug((Object) ("Finish rechargeByCard ham cha:  nickname: " + nickname + ", provider: " + provider.getName() + ", serial: " + serial + ", pin: " + pin + ", platform:" + platform));
        return response;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    private RechargeResponse rechargeByCardMaxpay(String nickname, ProviderType provider, String serial, String pin, String platform) throws Exception {
        RechargeServiceImpl.logger.debug((Object) ("Start rechargeByCard Maxpay:  nickname: " + nickname + ", provider: " + provider.getName() + ", serial: " + serial + ", pin: " + pin));
        int code = 1;
        HazelcastInstance client;
        IMap<String, UserModel> userMap;
        String description;
        RechargeResponse res = new RechargeResponse(code, 0L, 0, 0L);
        if (GameCommon.getValueInt("IS_RECHARGE_CARD") == 1 || provider == null) {
            RechargeServiceImpl.logger.debug((Object) "rechargeByCard: param fail");
            return res;
        }
        if (pin != null) {
            pin = pin.trim();
        }
        if (serial != null) {
            serial = serial.trim();
        }
        if ((client = HazelcastClientFactory.getInstance()) == null) {
            MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the", "1030", "can not connect hazelcast");
            return res;
        }
        userMap = client.getMap("users");
        RechargeByCardMessage message = null;
        if (userMap.containsKey((Object) nickname)) {
            try {
                UserCacheModel user = (UserCacheModel) userMap.get((Object) nickname);
                res.setCurrentMoney(user.getVinTotal());
                boolean cardFail = false;
                long time = DvtUtils.checkRechargeFail(user.getRechargeFail(), user.getRechargeFailTime());
                lbl129:
                // 8 sources:
                if (time <= 0L) {
                    if (UserValidaton.validateSerialPin((String) serial)) {
                        if (UserValidaton.validateSerialPin((String) pin)) {
                            String id = VinPlayUtils.genTransactionId((int) user.getId());
                            ChargeObj obj = null;
                            ChargeMaxpayResponse response = null;
                            try {
                                MaxpayClient maxpay = new MaxpayClient(GameCommon.getValueStr("MAXPAY_MERCHANT_ID"), GameCommon.getValueStr("MAXPAY_SECRET_KEY"));
                                response = maxpay.doCharge(provider.getValue(), pin, serial, id);
                            } catch (MaxpayException me) {
                                RechargeServiceImpl.logger.debug((Object) me);
                                MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the", "1034", "Loi inpput parameter: " + me.getMessage());
                                DvtUtils.errorDvt(client, "RechargeByCard");
                                if (++MaxpayAlert.maxpayDisconnect != GameCommon.getValueInt("COUNT_FAIL")) return null;
                                if (VinPlayUtils.isAlertTimeout((Date) MaxpayAlert.alertMaxpayDisconnectTime, (int) 1) == false)
                                    return null;
                                this.alert(GameCommon.getValueStr("DISCONNECT_GROUP_NUMBER"), "SOS! Canh bao he thong Maxpay dang bi mat ket noi!", true);
                                MaxpayAlert.alertMaxpayDisconnectTime = new Date();
                                return null;
                            } catch (Exception e) {
                                RechargeServiceImpl.logger.debug((Object) e);
                                MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the", "1034", "Loi ket noi maxpay: " + e.getMessage());
                                DvtUtils.errorDvt(client, "RechargeByCard");
                                if (++MaxpayAlert.maxpayDisconnect != GameCommon.getValueInt("COUNT_FAIL")) return null;
                                if (VinPlayUtils.isAlertTimeout((Date) MaxpayAlert.alertMaxpayDisconnectTime, (int) 1) == false)
                                    return null;
                                this.alert(GameCommon.getValueStr("DISCONNECT_GROUP_NUMBER"), "SOS! Canh bao he thong Maxpay dang bi mat ket noi!", true);
                                MaxpayAlert.alertMaxpayDisconnectTime = new Date();
                                return null;
                            }
                            if (response != null) {
                                obj = new ChargeObj(response.getTxn_id(), provider.getValue(), serial, pin, (int) response.getCard_amount(), Integer.parseInt(response.getCode()), this.mapVinplayMessage(response.getCode()), "maxpay");
                            }
                            long money = 0L;
                            if (obj != null) {
                                money = Math.round((double) obj.getAmount() * GameCommon.getValueDouble("RATIO_RECHARGE_CARD"));
                                code = this.mapVinplayCode(String.valueOf(obj.getStatus()));
                                if (code == 0 && (obj.getAmount() == 0 || obj.getAmount() > PhoneCardType._5M.getValue() || obj.getAmount() % PhoneCardType._10K.getValue() != 0)) {
                                    code = 30;
                                }
                                message = new RechargeByCardMessage(nickname, id, provider.getName(), serial, pin, obj.getAmount(), obj.getStatus(), obj.getMessage(), code, (int) money, (String) null, (String) null, "maxpay", platform, (String) null);
                            } else {
                                message = new RechargeByCardMessage(nickname, id, provider.getName(), serial, pin, 0, -1, "L\u00e1\u00bb\u2014i k\u00e1\u00ba\u00bft n\u00e1\u00bb\u2018i maxpay", code, 0, (String) null, (String) null, "maxpay", platform, (String) null);
                                ++MaxpayAlert.maxpayDisconnect;
                            }
                            try {
                                userMap = client.getMap("users");
                                userMap.lock(nickname);
                                user = (UserCacheModel) userMap.get((Object) nickname);
                                long moneyUser = user.getVin();
                                long currentMoney = user.getVinTotal();
                                long rechargeMoney = user.getRechargeMoney();
                                if (code == 0) {
                                    user.setVin(moneyUser += money);
                                    user.setVinTotal(currentMoney += money);
                                    user.setRechargeMoney(rechargeMoney += money);
                                    user.setRechargeFail(0);
                                    description = "K\u1ebft qu\u1ea3: Th\u00e0nh c\u00f4ng, M\u00e3 GD: " + id + ", Th\u1ebb: " + provider.getName() + ", M\u1ec7nh gi\u00e1: " + obj.getAmount() + ", Serial: " + serial + ", Pin: " + pin + "";
                                    MoneyMessageInMinigame messageMoney = new MoneyMessageInMinigame(VinPlayUtils.genMessageId(), user.getId(), nickname, "RechargeByCard", moneyUser, currentMoney, money, "vin", 0L, 0, 0);
                                    LogMoneyUserMessage messageLog = new LogMoneyUserMessage(user.getId(), nickname, "RechargeByCard", "N\u1ea1p Win qua th\u1ebb", currentMoney, money, "vin", description, 0L, false, user.isBot());
                                    RMQApi.publishMessagePayment((BaseMessage) messageMoney, (int) 16);
                                    RMQApi.publishMessageLogMoney((LogMoneyUserMessage) messageLog);
                                    RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 301);
                                    userMap.put(nickname, user);
                                    res.setCurrentMoney(currentMoney);
                                    MaxpayAlert.maxpayDisconnect = 0;
                                    if (provider.getName().equals(ProviderType.VIETTEL.getName())) {
                                        MaxpayAlert.viettelPending = 0;
                                    }
                                    if (provider.getName().equals(ProviderType.MOBIFONE.getName())) {
                                        MaxpayAlert.mobiPending = 0;
                                    }
                                    if (provider.getName().equals(ProviderType.VINAPHONE.getName())) {
                                        MaxpayAlert.vinaPending = 0;
                                    }
                                    if (!provider.getName().equals(ProviderType.GATE.getName())) break lbl129;
                                    MaxpayAlert.gatePending = 0;
                                }
                                if (code == 30) {
                                    description = "K\u1ebft qu\u1ea3: \u0110ang x\u1eed l\u00fd, M\u00e3 GD: " + id + ", Th\u1ebb: " + provider.getName() + ", M\u1ec7nh gi\u00e1: " + obj.getAmount() + ", Serial: " + serial + ", Pin: " + pin;
                                    LogMoneyUserMessage messageLog2 = new LogMoneyUserMessage(user.getId(), nickname, "RechargeByCard", "N\u1ea1p Win qua th\u1ebb", currentMoney, 0L, "vin", description, 0L, false, user.isBot());
                                    RMQApi.publishMessageLogMoney((LogMoneyUserMessage) messageLog2);
                                    RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 301);
                                    MaxpayAlert.maxpayDisconnect = 0;
                                    if (provider.getName().equals(ProviderType.VIETTEL.getName())) {
                                        ++MaxpayAlert.viettelPending;
                                    }
                                    if (provider.getName().equals(ProviderType.MOBIFONE.getName())) {
                                        ++MaxpayAlert.mobiPending;
                                    }
                                    if (provider.getName().equals(ProviderType.VINAPHONE.getName())) {
                                        ++MaxpayAlert.vinaPending;
                                    }
                                    if (!provider.getName().equals(ProviderType.GATE.getName())) break lbl129;
                                    ++MaxpayAlert.gatePending;
                                }
                                if (code != 1) {
                                    cardFail = true;
                                }
                                RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 301);
                            } catch (Exception e2) {
                                code = 1;
                                RechargeServiceImpl.logger.debug((Object) e2);
                                MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the", "1001", e2.getMessage());
                                return null;
//                                var21_32 = null;
//                                return var21_32;
                            } finally {
                                userMap.unlock(nickname);
                            }
                        } else {
                            code = 35;
                            cardFail = true;
                        }
                    } else {
                        code = 36;
                        cardFail = true;
                    }
                } else {
                    code = 8;
                    res.setTime(time);
                }

                if (cardFail) {
                    try {
                        userMap = client.getMap("users");
                        userMap.lock(nickname);
                        user = (UserCacheModel) userMap.get((Object) nickname);
                        user.setRechargeFail(user.getRechargeFail() + 1);
                        user.setRechargeFailTime(new Date());
                        userMap.put(nickname, user);
                    } catch (Exception e3) {
                        code = 1;
                        RechargeServiceImpl.logger.debug((Object) e3);
                        MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the", "1001", e3.getMessage());
                        return null;
//                        obj = null;
//                        return obj;
                    } finally {
                        userMap.unlock(nickname);
                    }
                }
                res.setFail(user.getRechargeFail());
            } catch (Exception e4) {
                code = 1;
                RechargeServiceImpl.logger.debug((Object) e4);
                MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the", "1001", e4.getMessage());
                return null;
            }
        }
        res.setCode(code);
        RechargeServiceImpl.logger.debug((Object) ("Finish rechargeByCard Maxpay, Response : " + code));
        if (MaxpayAlert.viettelPending == GameCommon.getValueInt("COUNT_FAIL") && VinPlayUtils.isAlertTimeout((Date) MaxpayAlert.alertViettelPendingTime, (int) 1)) {
            this.alert(GameCommon.getValueStr("PENDING_GROUP_NUMBER"), "[CANH BAO] He thong Maxpay tra ve pending qua 5 lan lien tiep Viettel!", true);
            MaxpayAlert.alertViettelPendingTime = new Date();
        }
        if (MaxpayAlert.mobiPending == GameCommon.getValueInt("COUNT_FAIL") && VinPlayUtils.isAlertTimeout((Date) MaxpayAlert.alertMobiPendingTime, (int) 1)) {
            this.alert(GameCommon.getValueStr("PENDING_GROUP_NUMBER"), "[CANH BAO] He thong Maxpay tra ve pending qua 5 lan lien tiep Mobifone!", true);
            MaxpayAlert.alertMobiPendingTime = new Date();
        }
        if (MaxpayAlert.vinaPending == GameCommon.getValueInt("COUNT_FAIL") && VinPlayUtils.isAlertTimeout((Date) MaxpayAlert.alertVinaPendingTime, (int) 1)) {
            this.alert(GameCommon.getValueStr("PENDING_GROUP_NUMBER"), "[CANH BAO] He thong Maxpay tra ve pending qua 5 lan lien tiep Vinaphone!", true);
            MaxpayAlert.alertVinaPendingTime = new Date();
        }
        if (MaxpayAlert.gatePending != GameCommon.getValueInt("COUNT_FAIL")) return res;
        if (VinPlayUtils.isAlertTimeout((Date) MaxpayAlert.alertGatePendingTime, (int) 1) == false) return res;
        this.alert(GameCommon.getValueStr("PENDING_GROUP_NUMBER"), "[CANH BAO] He thong Maxpay tra ve pending qua 5 lan lien tiep FPT Gate!", true);
        MaxpayAlert.alertGatePendingTime = new Date();
        return res;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    public RechargeResponse rechargeByCardLucky79(String nickname, ProviderType provider, String serial, String pin, String amount, String platform) throws Exception {
        RechargeServiceImpl.logger.debug((Object) ("Start rechargeByCard ozze:  nickname: " + nickname + ", provider: " + provider.getName() + ", serial: " + serial + ", pin: " + pin));
        int code = 1;
        String description;
        HazelcastInstance client;
        RechargeResponse res = new RechargeResponse(code, 0L, 0, 0L);
        if (GameCommon.getValueInt("IS_RECHARGE_CARD") == 1 || provider == null) {
            RechargeServiceImpl.logger.debug((Object) "rechargeByCard: param fail");
            return res;
        }
        if (pin != null) {
            pin = pin.trim();
        }
        if (serial != null) {
            serial = serial.trim();
        }
        if ((client = HazelcastClientFactory.getInstance()) == null) {
            MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the", "1030", "can not connect hazelcast");
            return res;
        }
        IMap<String, UserModel> userMap = client.getMap("users");
        RechargeByCardMessage message = null;
        if (userMap.containsKey((Object) nickname)) {
            try {
                UserCacheModel user = (UserCacheModel) userMap.get((Object) nickname);
                res.setCurrentMoney(user.getVinTotal());
                boolean cardFail = false;
                long time = DvtUtils.checkRechargeFailLucky79(user.getRechargeFail(), user.getRechargeFailTime());
                lbl126:
                // 8 sources:
                if (time <= 0L) {
                    if (UserValidaton.validateSerialPin((String) serial)) {
                        if (UserValidaton.validateSerialPin((String) pin)) {
                            String id = VinPlayUtils.genTransactionId((int) user.getId());
                            TheCaoResponse response = null;
                            try {
                                Lucky79Client maxpay = new Lucky79Client(GameCommon.getValueStr("LUCKY79_MERCHANT_ID"), GameCommon.getValueStr("LUCKY79_SECRET_KEY"));
                                response = maxpay.doCharge(provider.getName(), pin, serial, id, amount);
                            } catch (Lucky79Exception me) {
                                RechargeServiceImpl.logger.debug((Object) me);
                                MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap Win qua the", "1034", "Loi inpput parameter: " + me.getMessage());
                                DvtUtils.errorDvt(client, "RechargeByCard");
                                if (++Lucky79Alert.maxpayDisconnect != GameCommon.getValueInt("COUNT_FAIL"))
                                    return null;
                                if (VinPlayUtils.isAlertTimeout((Date) Lucky79Alert.alertMaxpayDisconnectTime, (int) 1) == false)
                                    return null;
                                this.alert(GameCommon.getValueStr("DISCONNECT_GROUP_NUMBER"), "SOS! Canh bao he thong nap the ozze dang bi mat ket noi!", true);
                                Lucky79Alert.alertMaxpayDisconnectTime = new Date();
                                return null;
                            } catch (Exception e) {
                                RechargeServiceImpl.logger.debug((Object) e);
                                MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the", "1034", "Loi ket noi maxpay: " + e.getMessage());
                                DvtUtils.errorDvt(client, "RechargeByCard");
                                if (++Lucky79Alert.maxpayDisconnect != GameCommon.getValueInt("COUNT_FAIL"))
                                    return null;
                                if (VinPlayUtils.isAlertTimeout((Date) Lucky79Alert.alertMaxpayDisconnectTime, (int) 1) == false)
                                    return null;
                                this.alert(GameCommon.getValueStr("DISCONNECT_GROUP_NUMBER"), "SOS! Canh bao he thong nap the ozze dang bi mat ket noi!", true);
                                Lucky79Alert.alertMaxpayDisconnectTime = new Date();
                                return null;
                            }
                            long money = 0L;
                            if (response != null) {
                                money = Math.round(response.getAmount() * GameCommon.getValueDouble("RATIO_RECHARGE_CARD"));
                                code = this.mapPayVietToVinplayCode(response.getStatus());
                                if (code == 0 && (response.getAmount() == 0.0 || response.getAmount() > (double) PhoneCardType._5M.getValue() || Double.valueOf(amount) % (double) PhoneCardType._10K.getValue() != 0.0)) {
                                    code = 30;
                                }
                                message = new RechargeByCardMessage(nickname, id, provider.getName(), serial, pin, (int) response.getDeclared_value(), this.mapLucky79ToVinplayCode(response.getStatus()), response.getMessage(), code, (int) money, response.getDate(), (String) null, "lucky79", platform, (String) null);
                                message.setTranId(response.getTrans_id());
                                message.setRequestId(response.getRequest_id());
                            } else {
                                message = new RechargeByCardMessage(nickname, id, provider.getName(), serial, pin, 0, -1, "0", code, 0, (String) null, (String) null, "lucky79", platform, (String) null);
                                ++Lucky79Alert.maxpayDisconnect;
                            }
                            try {
                                userMap = client.getMap("users");
                                userMap.lock(nickname);
                                user = (UserCacheModel) userMap.get((Object) nickname);
                                long moneyUser = user.getVin();
                                long currentMoney = user.getVinTotal();
                                long rechargeMoney = user.getRechargeMoney();
                                if (code == 0) {
                                    user.setVin(moneyUser += money);
                                    user.setVinTotal(currentMoney += money);
                                    user.setRechargeMoney(rechargeMoney += money);
                                    user.setRechargeFail(0);
                                    description = "K\u1ebft qu\u1ea3: Th\u00e0nh c\u00f4ng, M\u00e3 GD: " + id + ", Th\u1ebb: " + provider.getName() + ", M\u1ec7nh gi\u00e1: " + response.getAmount() + ", Serial: " + serial + ", Pin: " + pin;
                                    MoneyMessageInMinigame messageMoney = new MoneyMessageInMinigame(VinPlayUtils.genMessageId(), user.getId(), nickname, "RechargeByCard", moneyUser, currentMoney, money, "vin", 0L, 0, 0);
                                    LogMoneyUserMessage messageLog = new LogMoneyUserMessage(user.getId(), nickname, "RechargeByCard", "N\u1ea1p Win qua th\u1ebb", currentMoney, money, "vin", description, 0L, false, user.isBot());
                                    RMQApi.publishMessagePayment((BaseMessage) messageMoney, (int) 16);
                                    RMQApi.publishMessageLogMoney((LogMoneyUserMessage) messageLog);
                                    RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 301);
                                    userMap.put(nickname, user);
                                    res.setCurrentMoney(currentMoney);
                                    Lucky79Alert.maxpayDisconnect = 0;
                                    if (provider.getName().equals(ProviderType.VIETTEL.getName())) {
                                        Lucky79Alert.viettelPending = 0;
                                    }
                                    if (provider.getName().equals(ProviderType.MOBIFONE.getName())) {
                                        Lucky79Alert.mobiPending = 0;
                                    }
                                    if (provider.getName().equals(ProviderType.VINAPHONE.getName())) {
                                        Lucky79Alert.vinaPending = 0;
                                    }
                                    if (!provider.getName().equals(ProviderType.GATE.getName())) break lbl126;
                                    Lucky79Alert.gatePending = 0;
                                }
                                if (code == 30) {
                                    description = "K\u1ebft qu\u1ea3: \u0110ang x\u1eed l\u00fd, M\u00e3 GD: " + id + ", Th\u1ebb: " + provider.getName() + ", M\u1ec7nh gi\u00e1: " + response.getAmount() + ", Serial: " + serial + ", Pin: " + pin;
                                    LogMoneyUserMessage messageLog2 = new LogMoneyUserMessage(user.getId(), nickname, "RechargeByCard", "N\u1ea1p Win qua th\u1ebb", currentMoney, 0L, "vin", description, 0L, false, user.isBot());
                                    RMQApi.publishMessageLogMoney((LogMoneyUserMessage) messageLog2);
                                    RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 301);
                                    Lucky79Alert.maxpayDisconnect = 0;
                                    if (provider.getName().equals(ProviderType.VIETTEL.getName())) {
                                        ++Lucky79Alert.viettelPending;
                                    }
                                    if (provider.getName().equals(ProviderType.MOBIFONE.getName())) {
                                        ++Lucky79Alert.mobiPending;
                                    }
                                    if (provider.getName().equals(ProviderType.VINAPHONE.getName())) {
                                        ++Lucky79Alert.vinaPending;
                                    }
                                    if (!provider.getName().equals(ProviderType.GATE.getName())) break lbl126;
                                    ++Lucky79Alert.gatePending;
                                }
                                if (code != 1) {
                                    cardFail = true;
                                }
                                RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 301);
                            } catch (Exception e2) {
                                code = 1;
                                RechargeServiceImpl.logger.debug((Object) e2);
                                MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap Win qua the", "1001", e2.getMessage());
                                return null;
//                                var20_31 = null;
//                                return var20_31;
                            } finally {
                                userMap.unlock(nickname);
                            }
                        } else {
                            code = 35;
                            cardFail = true;
                        }
                    } else {
                        code = 36;
                        cardFail = true;
                    }
                } else {
                    code = 8;
                    res.setTime(time);
                }

                if (cardFail) {
                    try {
                        userMap = client.getMap("users");
                        userMap.lock(nickname);
                        user = (UserCacheModel) userMap.get((Object) nickname);
                        user.setRechargeFail(user.getRechargeFail() + 1);
                        user.setRechargeFailTime(new Date());
                        userMap.put(nickname, user);
                    } catch (Exception e3) {
                        code = 1;
                        RechargeServiceImpl.logger.debug((Object) e3);
                        MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the", "1001", e3.getMessage());
                        return null;
//                        response = null;
//                        return response;
                    } finally {
                        userMap.unlock(nickname);
                    }
                }
                res.setFail(user.getRechargeFail());
            } catch (Exception e4) {
                code = 1;
                RechargeServiceImpl.logger.debug((Object) e4);
                MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the", "1001", e4.getMessage());
                return null;
            }
        }
        res.setCode(code);
        RechargeServiceImpl.logger.debug((Object) ("Finish rechargeByCard Lucky79, Response : " + code));
        if (Lucky79Alert.viettelPending == GameCommon.getValueInt("COUNT_FAIL") && VinPlayUtils.isAlertTimeout((Date) Lucky79Alert.alertViettelPendingTime, (int) 1)) {
            this.alert(GameCommon.getValueStr("PENDING_GROUP_NUMBER"), "[CANH BAO] He thong nap the ozze tra ve pending qua 5 lan lien tiep Viettel!", true);
            Lucky79Alert.alertViettelPendingTime = new Date();
        }
        if (Lucky79Alert.mobiPending == GameCommon.getValueInt("COUNT_FAIL") && VinPlayUtils.isAlertTimeout((Date) Lucky79Alert.alertMobiPendingTime, (int) 1)) {
            this.alert(GameCommon.getValueStr("PENDING_GROUP_NUMBER"), "[CANH BAO] He thong nap the ozze tra ve pending qua 5 lan lien tiep Mobifone!", true);
            Lucky79Alert.alertMobiPendingTime = new Date();
        }
        if (Lucky79Alert.vinaPending == GameCommon.getValueInt("COUNT_FAIL") && VinPlayUtils.isAlertTimeout((Date) Lucky79Alert.alertVinaPendingTime, (int) 1)) {
            this.alert(GameCommon.getValueStr("PENDING_GROUP_NUMBER"), "[CANH BAO] He thong nap the ozze tra ve pending qua 5 lan lien tiep Vinaphone!", true);
            Lucky79Alert.alertVinaPendingTime = new Date();
        }
        if (Lucky79Alert.gatePending != GameCommon.getValueInt("COUNT_FAIL")) return res;
        if (VinPlayUtils.isAlertTimeout((Date) Lucky79Alert.alertGatePendingTime, (int) 1) == false) return res;
        this.alert(GameCommon.getValueStr("PENDING_GROUP_NUMBER"), "[CANH BAO] He thong nap the ozze tra ve pending qua 5 lan lien tiep FPT Gate!", true);
        Lucky79Alert.alertGatePendingTime = new Date();
        return res;
    }

    @Override
    public synchronized RechargeResponse rechargeByBankManual(String nickname, long amount, String bankAccountNumber, String senderUser) {
        synchronized (this) {
            try {

                int code = 1;
                RechargeResponse res = new RechargeResponse(code, 0L, 0, 0L);
                //HazelcastInstance client;
                //IMap<String, UserModel> userMap;
                if (nickname.isEmpty() || amount <= 0 || bankAccountNumber.isEmpty()) {
                    return res;
                }

                //get pending transaction
                RechargeDao rechargeDao = new RechargeDaoImpl();
//               if (rechargeDao.isPendingTransDepositBank(nickname)) {
//                   res.setCode(DvtConst.RECHARGE_STATUS_PENDING_TRANS);
//                   return res;
//               }
                //validate bank infor
//               JSONObject bankInfo = DvtUtils.CheckDepositBankManual(bankAccountNumber);
//               if (bankInfo == null) {
//                   res.setCode(DvtConst.RECHARGE_STATUS_DATA_ERROR);
//                   return res;
//               }
                String[] dataall = senderUser.split("\\|");
                String userSend = dataall[0].trim();
                String bankname = dataall[1].trim();
                String bankAccname = dataall[2].trim();
                String TranID = dataall[3].trim();
                String commentTrans = dataall[4].trim();


                // insert to db
                DepositBankModel model = new DepositBankModel(nickname, amount, bankname, bankAccountNumber, bankAccname);
                model.setUserSender(userSend);
                model.setId(TranID);
                model.setDescription(commentTrans);

                DepositBankModel mo = rechargeDao.FindDepositBankById(TranID);

                if (rechargeDao.isPendingTransDepositBank(nickname) & model.getUserSender().equalsIgnoreCase("momo") == false) {
                    res.setCode(DvtConst.RECHARGE_STATUS_PENDING_TRANS);
                    return res;
                }

                if (mo != null) {
                    if (model.getUserSender().trim().equalsIgnoreCase("momo")) {
                        res.setCode(DvtConst.RECHARGE_STATUS_SUCCESS_MOMO);
                        return res;
                    } else {
                        while (mo != null) {
                            TranID = String.valueOf(VinPlayUtils.generateTransId());
                            model.setId(TranID);
                            model.setDescription(commentTrans);
                            mo = rechargeDao.FindDepositBankById(TranID);
                        }
                        if (!rechargeDao.InsertDepositBankManual(model)) {
                            return res;
                        }
                        HistoryTransDao historyTransDao = new HistoryTransDaoImpl();
                        if (model.getUserSender().equalsIgnoreCase("codepay")) {
                            historyTransDao.insertTransaction(new HistoryTransModel(bankname + "|" + commentTrans, "CodePay", "Nạp tiền", "", "Đang xử lý", "Đang chờ xử lý", nickname, HistoryTransConst.BANK, model.Id));
                        } else if (model.getUserSender().equalsIgnoreCase("momo")) {
                            historyTransDao.insertTransaction(new HistoryTransModel("Nạp NH", "Momo", "Nạp tiền", "", "Đang xử lý", "Đang chờ xử lý", nickname, HistoryTransConst.BANK, model.Id));
                        } else {
                            historyTransDao.insertTransaction(new HistoryTransModel(bankname + "|" + commentTrans, "Ngân Hàng", "Nạp tiền", String.valueOf(amount), "Đang xử lý", "Đang chờ xử lý", nickname, HistoryTransConst.BANK, model.Id));
                        }
                        //            new TelegramUtil().senMessToDaily(nickname, "Tạo phiếu nạp Ngân Hàng", 0);
                        NotificationAdminObj obj = new NotificationAdminObj();
                        try {
                            model.setStatus(1);
                            model.setDescription("NEW " + commentTrans);
                            model.setCreatedAt(VinPlayUtils.getCurrentDateTime());
                            model.setUpdatedAt(VinPlayUtils.getCurrentDateTime());
                            if (model.getUserSender().equalsIgnoreCase("momo")) {
                                SendToWS.sendBEExcRechargebyMomosunvin(model);
                            } else {
                                SendToWS.sendBEExcRechargebybank(model);
                            }
                            obj.setNapBank(true);
                            SendToWS.sendBEExcNotification(obj);

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        if (model.getUserSender().equalsIgnoreCase("codepay")) {
                            res.setCode(DvtConst.RECHARGE_STATUS_SUCCESS_CODEPAY);
                        } else if (model.getUserSender().equalsIgnoreCase("momo")) {
                            res.setCode(DvtConst.RECHARGE_STATUS_SUCCESS_MOMO);
                        } else {
                            res.setCode(DvtConst.RECHARGE_STATUS_SUCCESS);
                        }
                        return res;

                    }
                } else {
                    if (!rechargeDao.InsertDepositBankManual(model)) {
                        return res;
                    }
                    HistoryTransDao historyTransDao = new HistoryTransDaoImpl();
                    if (model.getUserSender().equalsIgnoreCase("codepay")) {
                        historyTransDao.insertTransaction(new HistoryTransModel(bankname + "|" + commentTrans, "CodePay", "Nạp tiền", "", "Đang xử lý", "Đang chờ xử lý", nickname, HistoryTransConst.BANK, model.Id));
                    } else if (model.getUserSender().equalsIgnoreCase("momo")) {
                        historyTransDao.insertTransaction(new HistoryTransModel("Nạp NH", "Momo", "Nạp tiền", "", "Đang xử lý", "Đang chờ xử lý", nickname, HistoryTransConst.BANK, model.Id));
                    } else {
                        historyTransDao.insertTransaction(new HistoryTransModel(bankname + "|" + commentTrans, "Ngân Hàng", "Nạp tiền", String.valueOf(amount), "Đang xử lý", "Đang chờ xử lý", nickname, HistoryTransConst.BANK, model.Id));
                    }
                    //            new TelegramUtil().senMessToDaily(nickname, "Tạo phiếu nạp Ngân Hàng", 0);
                    NotificationAdminObj obj = new NotificationAdminObj();
                    try {
                        model.setStatus(1);
                        model.setDescription("NEW " + commentTrans);
                        model.setCreatedAt(VinPlayUtils.getCurrentDateTime());
                        model.setUpdatedAt(VinPlayUtils.getCurrentDateTime());
                        if (model.getUserSender().equalsIgnoreCase("momo")) {
                            SendToWS.sendBEExcRechargebyMomosunvin(model);
                        } else {
                            SendToWS.sendBEExcRechargebybank(model);
//                           if(model.getUserSender().equalsIgnoreCase("codepay")){
//                               CheckBankingTruocELK checktruoc = new CheckBankingTruocELK();
//                               CallbackNapCodePayFuck callb = new CallbackNapCodePayFuck();
//                               ArrayList<NapTruoc> listdon = checktruoc.GetDon(commentTrans.toUpperCase());
//                               if(listdon.size() != 0){
//                                   for(NapTruoc napx : listdon){
//                                       Long time2 = new Date().getTime();
//                                       Long time3 = time2 - napx.getTime();
//                                       if(time3 <= 300000 && time3 >= 0){
//                                           callb.callback(commentTrans.toUpperCase(), napx.getTien()+"",nickname);
//                                       }
//                                   }
//                               }
//                           }
                        }
                        obj.setNapBank(true);
                        SendToWS.sendBEExcNotification(obj);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    if (model.getUserSender().equalsIgnoreCase("codepay")) {
                        res.setCode(DvtConst.RECHARGE_STATUS_SUCCESS_CODEPAY);
                    } else if (model.getUserSender().equalsIgnoreCase("momo")) {
                        res.setCode(DvtConst.RECHARGE_STATUS_SUCCESS_MOMO);
                    } else {
                        res.setCode(DvtConst.RECHARGE_STATUS_SUCCESS);
                    }
                    return res;
                }


            } catch (Exception e) {
                RechargeServiceImpl.logger.error(e);
                System.out.println("error len don admin: " + e.getMessage());
                return null;
            }
        }
    }

    public synchronized RechargeResponse rechargeByBankManual2(String nickname, long amount, String bankAccountNumber, String senderUser) {
        synchronized (this) {
            try {
                int code = 1;
                RechargeResponse res = new RechargeResponse(code, 0L, 0, 0L);
                if (nickname.isEmpty() || amount <= 0 || bankAccountNumber.isEmpty()) {
                    return res;
                }
                String[] dataall = senderUser.split("\\|");
                String userSend = dataall[0].trim();
                String bankname = dataall[1].trim();
                String bankAccname = dataall[2].trim();
                String TranID = dataall[3].trim();
                String commentTrans = dataall[4].trim();

                // insert to db
                DepositBankModel model = new DepositBankModel(nickname, amount, bankname, bankAccountNumber, bankAccname);
                model.setUserSender(userSend);
                model.setId(TranID);
                model.setDescription(commentTrans);
                String timeAt = VinPlayUtils.getCurrentDateTime();


                if (userSend.equalsIgnoreCase("codepay") == true) {
                    insertCodepayDon(TranID, nickname, timeAt, timeAt, amount, 1, bankname, bankAccountNumber, bankAccname, commentTrans, "", userSend);
                } else if (userSend.equalsIgnoreCase("momo") == true) {
                    insertMomoDon(TranID, nickname, timeAt, timeAt, amount, 1, bankname, bankAccountNumber, bankAccname, commentTrans, "", userSend);
                } else {
                    insertNHDon(TranID, nickname, timeAt, timeAt, amount, 1, bankname, bankAccountNumber, bankAccname, commentTrans, "", userSend);
                }

                NotificationAdminObj obj = new NotificationAdminObj();
                try {
                    model.setStatus(1);
                    model.setDescription("NEW " + commentTrans);
                    model.setCreatedAt(VinPlayUtils.getCurrentDateTime());
                    model.setUpdatedAt(VinPlayUtils.getCurrentDateTime());
                    if (model.getUserSender().equalsIgnoreCase("momo")) {
                        SendToWS.sendBEExcRechargebyMomosunvin(model);
                    } else {
                        SendToWS.sendBEExcRechargebybank(model);
                    }
                    obj.setNapBank(true);
                    SendToWS.sendBEExcNotification(obj);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                if (model.getUserSender().equalsIgnoreCase("codepay")) {
                    res.setCode(DvtConst.RECHARGE_STATUS_SUCCESS_CODEPAY);
                } else if (model.getUserSender().equalsIgnoreCase("momo")) {
                    res.setCode(DvtConst.RECHARGE_STATUS_SUCCESS_MOMO);
                } else {
                    res.setCode(DvtConst.RECHARGE_STATUS_SUCCESS);
                }
                return res;

            } catch (Exception e) {
                RechargeServiceImpl.logger.error(e);
                System.out.println("error len don admin: " + e.getMessage());
                return null;
            }
        }
    }


    public synchronized RechargeResponse rechargeByAutoMomo(String nickname, BankPartnerModel requestTaoCode, String transactionId ) {
        synchronized (this) {
            try {
                int amount = 1;
                int code = 1;
                RechargeResponse res = new RechargeResponse(code, 0L, 0, 0L);
                // insert to db
                DepositBankModel model = new DepositBankModel(nickname, amount, requestTaoCode.bank_provider, requestTaoCode.phoneName, requestTaoCode.phoneName);
                model.setUserSender(nickname);
                model.setId(String.valueOf(requestTaoCode.id));
                model.setDescription(requestTaoCode.code);
                String timeAt = VinPlayUtils.getCurrentDateTime();


                insertMomoTransaction(String.valueOf(requestTaoCode.id), nickname, timeAt, timeAt, amount, 1,
                        requestTaoCode.bank_provider, requestTaoCode.phoneNum, requestTaoCode.phoneName,
                        requestTaoCode.code, "", requestTaoCode.bank_provider,
                        requestTaoCode.qr_url, requestTaoCode.payment_url, requestTaoCode.timeToExpired, transactionId);

                NotificationAdminObj obj = new NotificationAdminObj();
                try {
                    model.setStatus(1);
                    model.setDescription("NEW " + requestTaoCode.code);
                    model.setCreatedAt(VinPlayUtils.getCurrentDateTime());
                    model.setUpdatedAt(VinPlayUtils.getCurrentDateTime());
                    SendToWS.sendBEExcRechargebyMomosunvin(model);
                    obj.setNapBank(true);
                    SendToWS.sendBEExcNotification(obj);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                res.setCode(DvtConst.RECHARGE_STATUS_SUCCESS_MOMO);
                return res;

            } catch (Exception e) {
                RechargeServiceImpl.logger.error(e);
                System.out.println("error len don admin: " + e.getMessage());
                return null;
            }
        }
    }

    public void insertCodepayDon(String Id, String Nickname, String CreatedAt, String UpdatedAt, long Amount,
                                 int Status, String BankBrandName, String BankAccountNumber, String BankAccountName,
                                 String Description, String UserApprove, String UserSender) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("deposit_codepay_manual");
            Document doc = new Document();
            doc.append("Id", Id);
            doc.append("Nickname", Nickname);
            doc.append("CreatedAt", CreatedAt);
            doc.append("UpdatedAt", UpdatedAt);
            doc.append("Amount", Amount);
            doc.append("Status", Status);
            doc.append("BankBrandName", BankBrandName);
            doc.append("BankAccountNumber", BankAccountNumber);
            doc.append("BankAccountName", BankAccountName);
            doc.append("Description", Description);
            doc.append("UserApprove", UserApprove);
            doc.append("UserSender", UserSender);
            doc.append("Note1", "");
            doc.append("Note2", "");
            doc.append("Note3", "");
            col.insertOne((Object) doc);

        } catch (Exception e) {
            System.out.println("loi ne a oi: " + e);
        }
    }

    public void insertMomoDon(String Id, String Nickname, String CreatedAt, String UpdatedAt, long Amount,
                              int Status, String BankBrandName, String BankAccountNumber, String BankAccountName,
                              String Description, String UserApprove, String UserSender) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("deposit_momo2_manual");
            Document doc = new Document();
            doc.append("Id", Id);
            doc.append("Nickname", Nickname);
            doc.append("CreatedAt", CreatedAt);
            doc.append("UpdatedAt", UpdatedAt);
            doc.append("Amount", Amount);
            doc.append("Status", Status);
            doc.append("BankBrandName", BankBrandName);
            doc.append("BankAccountNumber", BankAccountNumber);
            doc.append("BankAccountName", BankAccountName);
            doc.append("Description", Description);
            doc.append("UserApprove", UserApprove);
            doc.append("UserSender", UserSender);
            doc.append("Note1", "");
            doc.append("Note2", "");
            doc.append("Note3", "");
            col.insertOne((Object) doc);

        } catch (Exception e) {
            System.out.println("loi ne a oi: " + e);
        }
    }


    public void insertMomoTransaction(String Id, String Nickname, String CreatedAt, String UpdatedAt, long Amount,
                              int Status, String BankBrandName, String BankAccountNumber, String BankAccountName,
                              String Description, String UserApprove, String UserSender,
                                      String qrCode, String paymentUrl,int timeToExpired,String transactionId) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("deposit_momo2_manual");
            Document doc = new Document();
            doc.append("Id", Id);
            doc.append("Nickname", Nickname);
            doc.append("CreatedAt", CreatedAt);
            doc.append("UpdatedAt", UpdatedAt);
            doc.append("Amount", Amount);
            doc.append("Status", Status);
            doc.append("BankBrandName", BankBrandName);
            doc.append("BankAccountNumber", BankAccountNumber);
            doc.append("BankAccountName", BankAccountName);
            doc.append("Description", Description);
            doc.append("UserApprove", UserApprove);
            doc.append("UserSender", UserSender);
            doc.append("QRCode", qrCode);
            doc.append("PaymentURL", paymentUrl);
            doc.append("TimeToExpired", timeToExpired);
            doc.append("TransactionId",transactionId);
            col.insertOne(doc);

        } catch (Exception e) {
            System.out.println("loi ne a oi: " + e);
        }
    }

    public DepositBankModel finMoMoDeposit(String nickname) {
        try {
            HashMap<String, Object> conditions = new HashMap<String, Object>();
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("deposit_momo2_manual");
            conditions.put("Nickname", nickname);
            conditions.put("Status", 1);
            Document document = (Document) col.find((Bson) new Document(conditions)).first();
            if (document != null) {
                String Id = document.getString((Object) "Id");
                String Nickname = document.getString((Object) "Nickname");
                String CreatedAt = document.getString((Object) "CreatedAt");
                String UpdatedAt = document.getString((Object) "UpdatedAt");
                long Amount = document.getLong((Object) "Amount");
                int Status = document.getInteger((Object) "Status");
                String BankBrandName = document.getString((Object) "BankBrandName");
                String BankAccountNumber = document.getString((Object) "BankAccountNumber");
                String BankAccountName = document.getString((Object) "BankAccountName");
                String Description = document.getString((Object) "Description");
                DepositBankModel desp = new DepositBankModel(Id, Nickname, CreatedAt, UpdatedAt, Amount, Status, BankBrandName, BankAccountNumber, BankAccountName, Description);
                desp.setQRCode(document.getString((Object) "QRCode"));
                desp.setPaymentURL(document.getString((Object) "PaymentURL"));
                desp.setTimeToExpired(document.getInteger((Object) "TimeToExpired"));
                return desp;
            }

        } catch (Exception e) {
            DepositBankModel desp = new DepositBankModel("Id", "Nickname", "CreatedAt", "UpdatedAt", 0, 0, "BankBrandName", "BankAccountNumber", "BankAccountName", e.getMessage());
            return desp;
        }
        return null;
    }
    public DepositBankModel findBankDepositByTransactionId(String transactionId) {
        try {
            HashMap<String, Object> conditions = new HashMap<String, Object>();
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection(DvtConst.DEPOSIT_BANK_COLLECTION);
            conditions.put("Id", Integer.valueOf(transactionId));
            conditions.put("Status", 1);
//            BasicDBObject sortCondtions = new BasicDBObject();
//            sortCondtions.put("CreatedAt", -1);
            Document document = (Document) col.find((Bson) new Document(conditions)).first();
//            Document document = (Document) col.find((Bson) new Document(conditions)).sort((Bson) sortCondtions).first();
            if (document != null) {
                String Id = document.getString((Object) "Id");
                String Nickname = document.getString((Object) "Nickname");
                String CreatedAt = document.getString((Object) "CreatedAt");
                String UpdatedAt = document.getString((Object) "UpdatedAt");
                long Amount = document.getLong((Object) "Amount");
                int Status = document.getInteger((Object) "Status");
                String BankBrandName = document.getString((Object) "BankBrandName");
                String BankAccountNumber = document.getString((Object) "BankAccountNumber");
                String BankAccountName = document.getString((Object) "BankAccountName");
                String Description = document.getString((Object) "Description");
                String UserApprove = document.getString((Object) "UserApprove");
                String UserSender = document.getString((Object) "UserSender");
                DepositBankModel desp = new DepositBankModel(Id, Nickname, CreatedAt, UpdatedAt, Amount, Status, BankBrandName, BankAccountNumber, BankAccountName, Description);
                return desp;
            }

        } catch (Exception e) {
            DepositBankModel desp = new DepositBankModel("Id", "Nickname", "CreatedAt", "UpdatedAt", 0, 0, "BankBrandName", "BankAccountNumber", "BankAccountName", e.getMessage());
            return desp;
        }
        return null;
    }
    public DepositBankModel finMoMoDepositByTransactionId(String transactionId) {
        try {
            HashMap<String, Object> conditions = new HashMap<String, Object>();
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("deposit_momo2_manual");
            conditions.put("Id", transactionId);
            conditions.put("Status", 1);
//            BasicDBObject sortCondtions = new BasicDBObject();
//            sortCondtions.put("CreatedAt", -1);
            Document document = (Document) col.find((Bson) new Document(conditions)).first();
//            Document document = (Document) col.find((Bson) new Document(conditions)).sort((Bson) sortCondtions).first();
            if (document != null) {
                String Id = document.getString((Object) "Id");
                String Nickname = document.getString((Object) "Nickname");
                String CreatedAt = document.getString((Object) "CreatedAt");
                String UpdatedAt = document.getString((Object) "UpdatedAt");
                long Amount = document.getLong((Object) "Amount");
                int Status = document.getInteger((Object) "Status");
                String BankBrandName = document.getString((Object) "BankBrandName");
                String BankAccountNumber = document.getString((Object) "BankAccountNumber");
                String BankAccountName = document.getString((Object) "BankAccountName");
                String Description = document.getString((Object) "Description");
                String UserApprove = document.getString((Object) "UserApprove");
                String UserSender = document.getString((Object) "UserSender");
                DepositBankModel desp = new DepositBankModel(Id, Nickname, CreatedAt, UpdatedAt, Amount, Status, BankBrandName, BankAccountNumber, BankAccountName, Description);
                return desp;
            }

        } catch (Exception e) {
            DepositBankModel desp = new DepositBankModel("Id", "Nickname", "CreatedAt", "UpdatedAt", 0, 0, "BankBrandName", "BankAccountNumber", "BankAccountName", e.getMessage());
            return desp;
        }
        return null;
    }

    public DepositBankModel finMoMoDepositByID(String id) {
        try {
            ArrayList<DepositBankModel> list_nick = new ArrayList<>();
            HashMap<String, Object> conditions = new HashMap<String, Object>();
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            logger.debug("id no day "+ id+"vv");
            MongoCollection col = db.getCollection("deposit_momo2_manual");
            conditions.put("Id", id);
            Document document = (Document) col.find((Bson) new Document(conditions)).first();
            if (document != null) {
                String Id = document.getString((Object) "Id");
                String Nickname = document.getString((Object) "Nickname");
                String CreatedAt = document.getString((Object) "CreatedAt");
                String UpdatedAt = document.getString((Object) "UpdatedAt");
                long Amount = document.getLong((Object) "Amount");
                int Status = document.getInteger((Object) "Status");
                String BankBrandName = document.getString((Object) "BankBrandName");
                String BankAccountNumber = document.getString((Object) "BankAccountNumber");
                String BankAccountName = document.getString((Object) "BankAccountName");
                String Description = document.getString((Object) "Description");
                String UserApprove = document.getString((Object) "UserApprove");
                String UserSender = document.getString((Object) "UserSender");
                DepositBankModel desp = new DepositBankModel(Id, Nickname, CreatedAt, UpdatedAt, Amount, Status, BankBrandName, BankAccountNumber, BankAccountName, Description);
                list_nick.add(desp);
            }
            if (list_nick.size() == 0) {
                return null;
            } else {
                return list_nick.get(0);
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }
    public void cancelMomoById(String TrainID) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("deposit_momo2_manual");
            Document doc = new Document();
            doc.append("Status", 2);
            doc.append("Amount", 0l);
            doc.append("UserApprove", "Nap Bank Auto");
            col.updateOne((Bson) new Document("Id", TrainID), (Bson) new Document("$set", (Object) doc));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void insertNHDon(String Id, String Nickname, String CreatedAt, String UpdatedAt, long Amount,
                            int Status, String BankBrandName, String BankAccountNumber, String BankAccountName,
                            String Description, String UserApprove, String UserSender) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("deposit_nh_manual");
            Document doc = new Document();
            doc.append("Id", Id);
            doc.append("Nickname", Nickname);
            doc.append("CreatedAt", CreatedAt);
            doc.append("UpdatedAt", UpdatedAt);
            doc.append("Amount", Amount);
            doc.append("Status", Status);
            doc.append("BankBrandName", BankBrandName);
            doc.append("BankAccountNumber", BankAccountNumber);
            doc.append("BankAccountName", BankAccountName);
            doc.append("Description", Description);
            doc.append("UserApprove", UserApprove);
            doc.append("UserSender", UserSender);
            doc.append("Note1", "");
            doc.append("Note2", "");
            doc.append("Note3", "");
            col.insertOne((Object) doc);

        } catch (Exception e) {
            System.out.println("loi ne a oi: " + e);
        }
    }

    public void InsertCodepayDonELK(String Id, String Nickname, String CreatedAt, String UpdatedAt, long Amount,
                                    int Status, String BankBrandName, String BankAccountNumber, String BankAccountName,
                                    String Description, String UserApprove, String UserSender) {
        try {
            boolean check = false;
            String sig = "\"successful\":1";
            int timeretry = 10;
            int retry = 3;
            do {
                retry--;
                if (retry < 0) {
                    return;
                }
                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\"Id\":\"" + Id + "\",\"Nickname\":\"" + Nickname + "\",\"CreatedAt\":\"" + CreatedAt + "\",\"UpdatedAt\":\"" + UpdatedAt + "\",\"Amount\":" + Amount + ",\"Status\":1,\"BankBrandName\":\"" + BankBrandName + "\",\"BankAccountNumber\":\"" + BankAccountNumber + "\",\"BankAccountName\":\"" + BankAccountName + "\",\"Description\":\"" + Description + "\",\"UserApprove\":\"" + UserApprove + "\",\"UserSender\":\"" + UserSender + "\",\"Note1\":\"\",\"Note2\":\"\",\"Note3\":\"\"}");
                Request request = new Request.Builder()
                        .url(System.getenv("ELASTICSEARCH_URL") + "/deposit_codepay_manual/_doc")
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
                        .build();
                Response response = client.newCall(request).execute();
                String data = response.body().string();
                if (data.contains(sig) == true) {
                    check = true;
                }
                timeretry--;

            } while (check == false && timeretry > 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void InsertMomoDonELK(String Id, String Nickname, String CreatedAt, String UpdatedAt, long Amount,
                                 int Status, String BankBrandName, String BankAccountNumber, String BankAccountName,
                                 String Description, String UserApprove, String UserSender) {
        try {
            boolean check = false;
            String sig = "\"successful\":1";
            int timeretry = 10;
            int retry = 3;
            do {
                retry--;
                if (retry < 0) {
                    return;
                }
                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\"Id\":\"" + Id + "\",\"Nickname\":\"" + Nickname + "\",\"CreatedAt\":\"" + CreatedAt + "\",\"UpdatedAt\":\"" + UpdatedAt + "\",\"Amount\":" + Amount + ",\"Status\":1,\"BankBrandName\":\"" + BankBrandName + "\",\"BankAccountNumber\":\"" + BankAccountNumber + "\",\"BankAccountName\":\"" + BankAccountName + "\",\"Description\":\"" + Description + "\",\"UserApprove\":\"" + UserApprove + "\",\"UserSender\":\"" + UserSender + "\",\"Note1\":\"\",\"Note2\":\"\",\"Note3\":\"\"}");
                Request request = new Request.Builder()
                        .url(System.getenv("ELASTICSEARCH_URL") + "/deposit_momo2_manual/_doc")
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
                        .build();
                Response response = client.newCall(request).execute();
                String data = response.body().string();
                if (data.contains(sig) == true) {
                    check = true;
                }
                timeretry--;

            } while (check == false && timeretry > 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void InsertNHDonELK(String Id, String Nickname, String CreatedAt, String UpdatedAt, long Amount,
                               int Status, String BankBrandName, String BankAccountNumber, String BankAccountName,
                               String Description, String UserApprove, String UserSender) {
        try {
            boolean check = false;
            String sig = "\"successful\":1";
            int timeretry = 10;
            do {
                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\"Id\":\"" + Id + "\",\"Nickname\":\"" + Nickname + "\",\"CreatedAt\":\"" + CreatedAt + "\",\"UpdatedAt\":\"" + UpdatedAt + "\",\"Amount\":" + Amount + ",\"Status\":1,\"BankBrandName\":\"" + BankBrandName + "\",\"BankAccountNumber\":\"" + BankAccountNumber + "\",\"BankAccountName\":\"" + BankAccountName + "\",\"Description\":\"" + Description + "\",\"UserApprove\":\"" + UserApprove + "\",\"UserSender\":\"" + UserSender + "\",\"Note1\":\"\",\"Note2\":\"\",\"Note3\":\"\"}");
                Request request = new Request.Builder()
                        .url(System.getenv("ELASTICSEARCH_URL") + "/deposit_nh_manual/_doc")
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
                        .build();
                Response response = client.newCall(request).execute();
                String data = response.body().string();
                if (data.contains(sig) == true) {
                    check = true;
                }
                timeretry--;

            } while (check == false && timeretry > 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //todo : rechargeOnepay
    @Override
    public RechargeResponse rechargeByOnePayBankManual(String nickname, long amount, String bankAccountNumber, String bankpassword, String bankName) {
        synchronized (this) {
            try {
                int code = 1;
                RechargeResponse res = new RechargeResponse(code, 0L, 0, 0L);
                //HazelcastInstance client;
                //IMap<String, UserModel> userMap;
                if (nickname.isEmpty() || amount <= 0 || bankAccountNumber.isEmpty()) {
                    return res;
                }

                //get pending transaction
                RechargeDao rechargeDao = new RechargeDaoImpl();
                if (rechargeDao.isPendingTransDepositOnePayBank(nickname) || rechargeDao.isPendingGetAnOPTDepositOnePayBank(nickname)
                        || rechargeDao.isPendingWaitOPTDepositOnePayBank(nickname)
                        || rechargeDao.isPendingSentResOTPDepositOnePayBank(nickname)) {
                    res.setCode(DvtConst.RECHARGE_STATUS_PENDING_TRANS);
                    return res;
                }

                // insert to db
                DepositOnePayModel model = new DepositOnePayModel(nickname, amount, bankName, bankpassword, bankAccountNumber);
                String transactionId = rechargeDao.InsertDepositOnePayBankManual(model);
                res.setTid(transactionId);
                HistoryTransDao historyTransDao = new HistoryTransDaoImpl();
                historyTransDao.insertTransaction(new HistoryTransModel("nạp ONEPAY", "Smart Link", "Nạp tiền", String.valueOf(amount), "Đang xử lý", "Đang chờ xử lý", nickname, HistoryTransConst.ONE_PAY, transactionId));
//            new TelegramUtil().senMessToDaily(nickname, "Tạo phiếu nạp ONEPAY", 0);
                NotificationAdminObj obj = new NotificationAdminObj();
                try {
                    model.setStatus(1);
                    model.setId(transactionId);
                    model.setCreatedAt(VinPlayUtils.getCurrentDateTime());
                    model.setUpdatedAt(VinPlayUtils.getCurrentDateTime());
                    SendToWS.sendBEExcRechargebyonepay(model);
                    obj.setNapOnePay(true);
                    SendToWS.sendBEExcNotification(obj);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }


                if (!transactionId.equals("")) {
                    return res;
                }
//            TelegramAlert.SendMessageDepositBank(model);
                res.setCode(DvtConst.RECHARGE_STATUS_SUCCESS);
                return res;

            } catch (Exception e) {
                RechargeServiceImpl.logger.error(e);
                return null;
            }
        }
    }

    // todo : check status
    public boolean isPendingOTPTransDepositOnePayBank(String nickname, String transId) {
        RechargeDao rechargeDao = new RechargeDaoImpl();
        return rechargeDao.isPendingOTPTransDepositOnePayBank(nickname, transId);
    }

    // todo : update status
    public boolean UpdateDepositStatusOnepay(String transId, int status, int sendingSts) {
        RechargeDao rechargeDao = new RechargeDaoImpl();
        return rechargeDao.UpdateDepositStatusOnepay(transId, status, sendingSts);
    }

    @Override
    public boolean UpdateDepositOnepayOTP(String transId, String otp, int status) {
        RechargeDao rechargeDao = new RechargeDaoImpl();
        return rechargeDao.UpdateDepositOnepayOTP(transId, otp, status);
    }

    @Override
    public int isDoneTranstionOnePay(String name, String transId) {
        RechargeDao rechargeDao = new RechargeDaoImpl();
        return rechargeDao.isDoneTranstionOnePay(name, transId);
    }

    @Override
    public String getIdTranstionOnePay(String name, int sts) {
        RechargeDao rechargeDao = new RechargeDaoImpl();
        return rechargeDao.getTransDoneTranstionOnePay(name, sts);
    }

    @Override
    public ArrayList<DepositOnePayModel> GetListDepositOnePayBank(int sendingStatus) {
        RechargeDao rechargeDao = new RechargeDaoImpl();
        return rechargeDao.GetListDepositOnePayBank(sendingStatus);
    }

    @Override
    public DepositOnePayModel FindDepositOnePayById(String transId) {
        RechargeDao rechargeDao = new RechargeDaoImpl();
        return rechargeDao.FindDepositOnePayById(transId);
    }

    @Override
    public RechargeResponse rechargeByMomoManual(String nickname, long amount, String sendFromNumber) {
        try {
            int code = 1;
            RechargeResponse res = new RechargeResponse(code, 0L, 0, 0L);
            //HazelcastInstance client;
            //IMap<String, UserModel> userMap;
            if (nickname.isEmpty() || amount <= 0 || sendFromNumber.isEmpty()) {
                res.setCode(DvtConst.RECHARGE_STATUS_DATA_ERROR);
                return res;
            }

            //get pending transaction
            RechargeDao rechargeDao = new RechargeDaoImpl();
            if (rechargeDao.isPendingTransDepositMomo(nickname)) {
                res.setCode(DvtConst.RECHARGE_STATUS_PENDING_TRANS);
                return res;
            }
            //validate bank infor
            String momoInfo = DvtUtils.getMomoNumber();
            if (momoInfo == null) {
                res.setCode(DvtConst.RECHARGE_STATUS_DATA_ERROR);
                return res;
            }

            // insert to db
            DepositMomoModel model = new DepositMomoModel(nickname, amount, momoInfo, "", sendFromNumber);
            if (!rechargeDao.InsertDepositMomoManual(model)) {
                return res;
            }
            TelegramAlert.SendMessageDepositMomo(model);
            res.setCode(DvtConst.RECHARGE_STATUS_SUCCESS);
            return res;
        } catch (Exception e) {
            return null;
        }
    }

    public RechargeResponse rechargeByGachThe_old(String nickname, ProviderType provider, String serial, String pin, String sAmount, String platform, int UserId) throws Exception {
        //RechargeServiceImpl.logger.debug((Object)("Start rechargeByCard Gachthe:  nickname: " + nickname + ", provider: " + provider.getName() + ", serial: " + serial + ", pin: " + pin));
        int code = 1;
        HazelcastInstance client;
        IMap<String, UserModel> userMap;
        String description;
        RechargeResponse res = new RechargeResponse(code, 0L, 0, 0L);
        if (GameCommon.getValueInt("IS_RECHARGE_CARD") == 1 || provider == null) {
            RechargeServiceImpl.logger.debug((Object) "rechargeByCard: param fail");
            return res;
        }
        if (pin != null) {
            pin = pin.trim();
        }
        if (serial != null) {
            serial = serial.trim();
        }
        if ((client = HazelcastClientFactory.getInstance()) == null) {
            MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the", "1030", "can not connect hazelcast");
            return res;
        }
        long amount = Long.parseLong(sAmount);
        userMap = client.getMap("users");
        RechargeByCardMessage message = null;
        if (userMap.containsKey((Object) nickname)) {
            try {
                UserCacheModel user = (UserCacheModel) userMap.get((Object) nickname);
                res.setCurrentMoney(user.getVinTotal());
                boolean cardFail = false;
                if (UserValidaton.validateSerialPin((String) serial)) {
                    if (UserValidaton.validateSerialPin((String) pin)) {
                        // check exists card pending
                        RechargeDaoImpl dao = new RechargeDaoImpl();
                        Document doc = dao.getRechargeByGachthe(nickname, serial, pin);
                        if (doc != null) {
                            // pending card
                            code = 30;
                        } else {
                            String id = VinPlayUtils.genTransactionId((int) user.getId());
                            res.setTid(id);
                            GachTheClient gachTheClient = new GachTheClient();
                            org.json.simple.JSONObject result = gachTheClient.doCharge(provider.getValue().toUpperCase(), pin, serial, id, amount);
                            if ("0".equals(result.get("Code").toString())) {
                                code = 1;
                            } else if ("1".equals(result.get("Code").toString())) {
                                try {
                                    code = 30;
                                    // thẻ được chấp nhận
                                    userMap = client.getMap("users");
                                    user = (UserCacheModel) userMap.get((Object) nickname);
                                    long moneyUser = user.getVin();
                                    long currentMoney = user.getVinTotal();
                                    long rechargeMoney = user.getRechargeMoney();

                                    if (code == 30) {
                                        UserServiceImpl userService = new UserServiceImpl();

                                        long money = Math.round((double) amount * GameCommon.getValueDouble("RATIO_RECHARGE_CARD"));
                                        //String nickname, String serial, String pin, int amount, String requestId, String requestTime, int code, String des, int money
                                        dao.saveLogRechargeByGachThe(nickname, serial, pin, amount, id, System.currentTimeMillis() + "", 30, "pending_card", currentMoney, provider.getValue(), platform, currentMoney, money, UserId, user.getUsername(), "gachthe",
                                                userService.getUserByNickName(nickname).getClient());
                                    }
                                } catch (Exception e2) {
                                    code = 1;
                                    RechargeServiceImpl.logger.debug((Object) e2);
                                    MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the", "1001", e2.getMessage());
                                } finally {

                                }
                            } else {
                                code = 1;
                            }
                        }
                    } else {
                        code = 35;
                        cardFail = true;
                    }
                } else {
                    code = 36;
                    cardFail = true;
                }
                if (cardFail) {
                    try {
                        userMap = client.getMap("users");
                        userMap.lock(nickname);
                        user = (UserCacheModel) userMap.get((Object) nickname);
                        user.setRechargeFail(user.getRechargeFail() + 1);
                        user.setRechargeFailTime(new Date());
                        userMap.put(nickname, user);
                    } catch (Exception e3) {
                        code = 1;
                        RechargeServiceImpl.logger.debug((Object) e3);
                        MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the", "1001", e3.getMessage());
//                        obj = null;
//                        return obj;
                    } finally {
                        userMap.unlock(nickname);
                    }
                }
                res.setFail(user.getRechargeFail());
            } catch (Exception e4) {
                code = 1;
                RechargeServiceImpl.logger.debug((Object) e4);
                MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the", "1001", e4.getMessage());
            }
        }
        res.setCode(code);
        return res;
    }

    // todo : đấu cổng

    public RechargeResponse rechargeByGachThe(String nickname, ProviderType provider, String serial, String pin, String sAmount, String platform, int UserId) throws Exception {


        int code = 1;
        HazelcastInstance client;
        IMap<String, UserModel> userMap;
        String description;
        RechargeResponse res = new RechargeResponse(code, 0L, 0, 0L);
        if (GameCommon.getValueInt("IS_RECHARGE_CARD") == 1 || provider == null) {
            RechargeServiceImpl.logger.debug((Object) "rechargeByCard: param fail");
            return res;
        }
        if (pin != null) {
            pin = pin.trim();
        }
        if (serial != null) {
            serial = serial.trim();
        }
        if ((client = HazelcastClientFactory.getInstance()) == null) {
            MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the", "1030", "can not connect hazelcast");
            return res;
        }
        long amount = Long.parseLong(sAmount);
        userMap = client.getMap("users");
        RechargeByCardMessage message = null;
        if (userMap.containsKey((Object) nickname)) {
            try {
//                cacheService.setValue("nap_card_phone", "true");


                UserCacheModel user = (UserCacheModel) userMap.get((Object) nickname);
                res.setCurrentMoney(user.getVinTotal());
                boolean cardFail = false;
                if (UserValidaton.validateSerialPin((String) serial)) {
                    if (UserValidaton.validateSerialPin((String) pin)) {
                        // check exists card pending
                        RechargeDaoImpl dao = new RechargeDaoImpl();
                        String id = "gacon_time" + String.valueOf(VinPlayUtils.generateTransId());
                        GachTheClient gachTheClient = new GachTheClient();
                        //fix fix donal trump

                        org.json.simple.JSONObject result = gachTheClient.doCharge(provider.getValue(), pin, serial, id, amount);

                        String loaithe = "";
                        if (provider.getValue().equalsIgnoreCase("VT")) {
                            loaithe = "Viettel";
                        } else if (provider.getValue().equalsIgnoreCase("Vina")) {
                            loaithe = "Vinaphone";
                        } else if (provider.getValue().equalsIgnoreCase("Mobi")) {
                            loaithe = "MobiFone";
                        } else {
                            loaithe = "";
                        }


                        RechargeServiceImpl.logger.debug((Object) result.toString());
                        if ("0".equals(result.get("errorCode").toString())) { // 0 là thành công
                            try {
                                code = 30;
                                res.setTid(id);
                                //todo: insert record vào db
                                DepositMobileCardModel depositMobileCardModel = new DepositMobileCardModel(id, nickname, amount, serial, pin, provider.getValue());
                                dao.InsertDepositMobileCardManual(depositMobileCardModel);
                                HistoryTransDao historyTransDao = new HistoryTransDaoImpl();
                                historyTransDao.insertTransaction(new HistoryTransModel(loaithe, "Thẻ Điện thoại", "Nạp tiền", String.valueOf(amount), "Đang xử lý", "Đang chờ xử lý", nickname, HistoryTransConst.Card, id));
                                // insert vào history

//                                new TelegramUtil().senMessToDaily(nickname, "Tạo phiếu nạp Thẻ Điện thoại", 0);
                                NotificationAdminObj obj = new NotificationAdminObj();
                                try {
                                    obj.setNapCardPhone(true);
                                    SendToWS.sendBEExcNotification(obj);
                                    SendToWS.sendBEExcRechargebyautocard(depositMobileCardModel);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            } catch (Exception e2) {
                                code = 1;
                                RechargeServiceImpl.logger.debug((Object) e2);
                                MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the", "1001", e2.getMessage());
                            } finally {

                            }
                        } else {
                            code = 1;
                            MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the", "1001", result.get("errorCode").toString());
                        }

                    } else {
                        code = 35;
                        cardFail = true;
                    }
                } else {
                    code = 36;
                    cardFail = true;
                }
                if (cardFail) {
                    try {
                        userMap = client.getMap("users");
                        userMap.lock(nickname);
                        user = (UserCacheModel) userMap.get((Object) nickname);
                        user.setRechargeFail(user.getRechargeFail() + 1);
                        user.setRechargeFailTime(new Date());
                        userMap.put(nickname, user);
                    } catch (Exception e3) {
                        code = 1;
                        RechargeServiceImpl.logger.debug((Object) e3);
                        MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the", "1001", e3.getMessage());
                    } finally {
                        userMap.unlock(nickname);
                    }
                }
                res.setFail(user.getRechargeFail());
            } catch (Exception e4) {
                code = 1;
                RechargeServiceImpl.logger.debug((Object) e4);
                MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the", "1001", e4.getMessage());
            }
        }
        res.setCode(code);
        return res;
    }

    public RechargeResponse rechargeByNapTienGa(String nickname, ProviderType provider, String serial,
                                                String pin, String sAmount, String platform, int UserId
    ) throws Exception {
        RechargeServiceImpl.logger.debug((Object) ("Start rechargeByCard Gachthe:  nickname: " + nickname + ", provider: " + provider.getName() + ", serial: " + serial + ", pin: " + pin));
        int code = 1;
        HazelcastInstance client;
        IMap<String, UserModel> userMap;
        String description;
        RechargeResponse res = new RechargeResponse(code, 0L, 0, 0L);
        if (GameCommon.getValueInt("IS_RECHARGE_CARD") == 1 || provider == null) {
            RechargeServiceImpl.logger.debug((Object) "rechargeByCard: param fail");
            return res;
        }
        if (pin != null) {
            pin = pin.trim();
        }
        if (serial != null) {
            serial = serial.trim();
        }
        if ((client = HazelcastClientFactory.getInstance()) == null) {
            MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the", "1030", "can not connect hazelcast");
            return res;
        }
        long amount = Long.parseLong(sAmount);
        userMap = client.getMap("users");
        RechargeByCardMessage message = null;
        if (userMap.containsKey((Object) nickname)) {
            try {
                UserCacheModel user = (UserCacheModel) userMap.get((Object) nickname);
                res.setCurrentMoney(user.getVinTotal());
                boolean cardFail = false;
                if (UserValidaton.validateSerialPin((String) serial)) {
                    if (UserValidaton.validateSerialPin((String) pin)) {
                        // check exists card pending
                        RechargeDaoImpl dao = new RechargeDaoImpl();
                        Document doc = dao.getRechargeByGachthe(nickname, serial, pin);
                        if (doc != null) {
                            // pending card
                            code = 30;
                        } else {
                            String id = VinPlayUtils.genTransactionId((int) user.getId());
                            res.setTid(id);
                            NapTienGaClient napTienGaClient = new NapTienGaClient();
                            org.json.simple.JSONObject result = napTienGaClient.doCharge(provider.getValue().toUpperCase(), pin, serial, id, amount,
                                    user.getClient());
                            if ("1".equals(result.get("stt").toString())) {
                                try {
                                    code = 30;
                                    // thẻ được chấp nhận
                                    userMap = client.getMap("users");
                                    user = (UserCacheModel) userMap.get((Object) nickname);
                                    long moneyUser = user.getVin();
                                    long currentMoney = user.getVinTotal();
                                    long rechargeMoney = user.getRechargeMoney();
                                    RechargeServiceImpl.logger.debug((Object) ("rechargeByNapTienGa" +
                                            "  nickname: " + nickname + ", Client: " + user.getClient()));
                                    if (code == 30) {
                                        UserServiceImpl userService = new UserServiceImpl();

                                        long money = Math.round((double) amount * GameCommon.getValueDouble("RATIO_RECHARGE_CARD"));
                                        //String nickname, String serial, String pin, int amount, String requestId, String requestTime, int code, String des, int money                                      
                                        dao.saveLogRechargeByGachThe(nickname, serial, pin, amount, id,
                                                System.currentTimeMillis() + "", 30, "pending_card",
                                                currentMoney, provider.getValue(), platform, currentMoney, money, UserId, user.getUsername(),
                                                "naptienga", userService.getUserByNickName(nickname).getClient());
                                    }
                                } catch (Exception e2) {
                                    code = 1;
                                    RechargeServiceImpl.logger.debug((Object) e2);
                                    MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the", "1001", e2.getMessage());
                                } finally {

                                }
                            } else {
                                code = 31;
                            }
                        }
                    } else {
                        code = 35;
                        cardFail = true;
                    }
                } else {
                    code = 36;
                    cardFail = true;
                }
                if (cardFail) {
                    try {
                        userMap = client.getMap("users");
                        userMap.lock(nickname);
                        user = (UserCacheModel) userMap.get((Object) nickname);
                        user.setRechargeFail(user.getRechargeFail() + 1);
                        user.setRechargeFailTime(new Date());
                        userMap.put(nickname, user);
                    } catch (Exception e3) {
                        code = 41;
                        RechargeServiceImpl.logger.debug((Object) e3);
                        MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the", "1001", e3.getMessage());
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        e3.printStackTrace(pw);
                        String sStackTrace = sw.toString(); // stack trace as a string
                        System.err.println(sStackTrace);
                        //return e3.getMessage() + "\n" + sStackTrace; 
                    } finally {
                        userMap.unlock(nickname);
                    }
                }
                res.setFail(user.getRechargeFail());
            } catch (Exception e4) {
                RechargeServiceImpl.logger.debug((Object) e4);
                MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the", "1001", e4.getMessage());
                code = 21;
                RechargeServiceImpl.logger.debug((Object) e4);
                System.err.println(e4.getMessage());
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e4.printStackTrace(pw);
                String sStackTrace = sw.toString(); // stack trace as a string
                System.err.println(sStackTrace);
                //return e4.getMessage() + "\n" + sStackTrace; 
            }
        }
        res.setCode(code);
        return res;
    }

    public RechargeResponse rechargeByMuaCard24h(String nickname, ProviderType provider, String serial, String pin, String sAmount, String platform, int UserId) throws Exception {
        RechargeServiceImpl.logger.debug((Object) ("Start rechargeByCard Muacard24h:  nickname: " + nickname + ", provider: " + provider.getName() + ", serial: " + serial + ", pin: " + pin));
        int code = 1;
        HazelcastInstance client;
        IMap<String, UserModel> userMap;
        String description;
        RechargeResponse res = new RechargeResponse(code, 0L, 0, 0L);
        if (GameCommon.getValueInt("IS_RECHARGE_CARD") == 1 || provider == null) {
            RechargeServiceImpl.logger.debug((Object) "rechargeByCard: param fail");
            return res;
        }
        if (pin != null) {
            pin = pin.trim();
        }
        if (serial != null) {
            serial = serial.trim();
        }
        if ((client = HazelcastClientFactory.getInstance()) == null) {
            MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the", "1030", "can not connect hazelcast");
            return res;
        }
        long amount = Long.parseLong(sAmount);
        userMap = client.getMap("users");
        RechargeByCardMessage message = null;
        if (userMap.containsKey((Object) nickname)) {
            try {
                UserCacheModel user = (UserCacheModel) userMap.get((Object) nickname);
                res.setCurrentMoney(user.getVinTotal());
                boolean cardFail = false;
                if (UserValidaton.validateSerialPin((String) serial)) {
                    if (UserValidaton.validateSerialPin((String) pin)) {
                        // check exists card pending
                        RechargeDaoImpl dao = new RechargeDaoImpl();
                        Document doc = dao.getRechargeByGachthe(nickname, serial, pin);
                        if (doc != null) {
                            // pending card
                            code = 30;
                        } else {
                            String id = VinPlayUtils.genTransactionId((int) user.getId());
                            res.setTid(id);
                            MuaTheClient muaTheClient = new MuaTheClient();
                            org.json.simple.JSONObject result = muaTheClient.doCharge(provider.getValue().toUpperCase(), pin, serial, id, amount);
                            RechargeServiceImpl.logger.debug((Object) result);
                            if ("99".equals(result.get("status").toString())) {
                                try {
                                    code = 30;
                                    // thẻ được chấp nhận
                                    userMap = client.getMap("users");
                                    user = (UserCacheModel) userMap.get((Object) nickname);
                                    long moneyUser = user.getVin();
                                    long currentMoney = user.getVinTotal();
                                    long rechargeMoney = user.getRechargeMoney();
                                    if (code == 30) {
                                        UserServiceImpl userService = new UserServiceImpl();

                                        long money = Math.round((double) amount * GameCommon.getValueDouble("RATIO_RECHARGE_CARD"));
                                        //String nickname, String serial, String pin, int amount, String requestId, String requestTime, int code, String des, int money                                      
                                        dao.saveLogRechargeByGachThe(nickname, serial, pin, amount, id,
                                                System.currentTimeMillis() + "", 30, "pending_card",
                                                currentMoney, provider.getValue(), platform, currentMoney, money, UserId,
                                                user.getUsername(), "muacard24h", userService.getUserByNickName(nickname).getClient());
                                    }
                                } catch (Exception e2) {
                                    code = 1;
                                    RechargeServiceImpl.logger.debug((Object) e2);
                                    MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the", "1001", e2.getMessage());
                                } finally {

                                }
                            } else {
                                code = 31;
                            }
                        }
                    } else {
                        code = 35;
                        cardFail = true;
                    }
                } else {
                    code = 36;
                    cardFail = true;
                }
                if (cardFail) {
                    try {
                        userMap = client.getMap("users");
                        userMap.lock(nickname);
                        user = (UserCacheModel) userMap.get((Object) nickname);
                        user.setRechargeFail(user.getRechargeFail() + 1);
                        user.setRechargeFailTime(new Date());
                        userMap.put(nickname, user);
                    } catch (Exception e3) {
                        code = 41;
                        RechargeServiceImpl.logger.debug((Object) e3);
                        MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the", "1001", e3.getMessage());
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        e3.printStackTrace(pw);
                        String sStackTrace = sw.toString(); // stack trace as a string
                        System.err.println(sStackTrace);
                        //return e3.getMessage() + "\n" + sStackTrace; 
                    } finally {
                        userMap.unlock(nickname);
                    }
                }
                res.setFail(user.getRechargeFail());
            } catch (Exception e4) {
                RechargeServiceImpl.logger.debug((Object) e4);
                MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the", "1001", e4.getMessage());
                code = 21;
                RechargeServiceImpl.logger.debug((Object) e4);
                System.err.println(e4.getMessage());
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e4.printStackTrace(pw);
                String sStackTrace = sw.toString(); // stack trace as a string
                System.err.println(sStackTrace);
                //return e4.getMessage() + "\n" + sStackTrace; 
            }
        }
        res.setCode(code);
        return res;
    }

    public RechargeResponse rechargeByMuaCard(String nickname, ProviderType provider, String serial, String pin, String sAmount, String platform, int UserId) throws Exception {
        RechargeServiceImpl.logger.debug((Object) ("Start rechargeByCard MuaCard:  nickname: " + nickname + ", provider: " + provider.getName() + ", serial: " + serial + ", pin: " + pin));
        int code = 1;
        HazelcastInstance client;
        IMap<String, UserModel> userMap;
        String description;
        RechargeResponse res = new RechargeResponse(code, 0L, 0, 0L);
        if (GameCommon.getValueInt("IS_RECHARGE_CARD") == 1 || provider == null) {
            RechargeServiceImpl.logger.debug((Object) "rechargeByCard: param fail");
            code = 2;
            return res;
        }
        if (pin != null) {
            pin = pin.trim();
        }
        if (serial != null) {
            serial = serial.trim();
        }
        if ((client = HazelcastClientFactory.getInstance()) == null) {
            MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the", "1030", "can not connect hazelcast");
            code = 3;
            return res;
        }
        long amount = Long.parseLong(sAmount);
        userMap = client.getMap("users");
        RechargeByCardMessage message = null;
        if (userMap.containsKey((Object) nickname)) {
            try {
                UserCacheModel user = (UserCacheModel) userMap.get((Object) nickname);
                res.setCurrentMoney(user.getVinTotal());
                boolean cardFail = false;
                if (UserValidaton.validateSerialPin((String) serial)) {
                    if (UserValidaton.validateSerialPin((String) pin)) {
                        // check exists card pending
                        RechargeDaoImpl dao = new RechargeDaoImpl();
                        Document doc = dao.getRechargeByGachthe(nickname, serial, pin);
                        if (doc != null) {
                            // pending card
                            code = 30;
                        } else {
                            String id = VinPlayUtils.genTransactionId((int) user.getId());
                            res.setTid(id);
                            MuaCardClient muaCardClient = new MuaCardClient();
                            org.json.simple.JSONObject result = muaCardClient.doCharge(provider.getValue().toUpperCase(), pin, serial, id, amount);
                            RechargeServiceImpl.logger.debug((Object) ("Start rechargeByCard MuaCard:" + result.get("code").toString()));
                            if ("200".equals(result.get("code").toString())) {
                                try {
                                    code = 30;
                                    // thẻ được chấp nhận
                                    userMap = client.getMap("users");
                                    user = (UserCacheModel) userMap.get((Object) nickname);
                                    long moneyUser = user.getVin();
                                    long currentMoney = user.getVinTotal();
                                    long rechargeMoney = user.getRechargeMoney();
                                    if (code == 30) {
                                        UserServiceImpl userService = new UserServiceImpl();

                                        long money = Math.round((double) amount * GameCommon.getValueDouble("RATIO_RECHARGE_CARD"));
                                        //String nickname, String serial, String pin, int amount, String requestId, String requestTime, int code, String des, int money                                        
                                        dao.saveLogRechargeByGachThe(nickname, serial, pin, amount, id,
                                                System.currentTimeMillis() + "", 30,
                                                "pending_card", currentMoney, provider.getValue(),
                                                platform, currentMoney, money, UserId, user.getUsername(),
                                                "muacard", userService.getUserByNickName(nickname).getClient());
                                    }
                                } catch (Exception e2) {
                                    code = 31;
                                    RechargeServiceImpl.logger.debug((Object) e2);
                                    MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the", "1001", e2.getMessage());
                                } finally {

                                }
                            } else {
                                code = (int) result.get("code");
                            }
                        }
                    } else {
                        code = 35;
                        cardFail = true;
                    }
                } else {
                    code = 36;
                    cardFail = true;
                }
                if (cardFail) {
                    try {
                        userMap = client.getMap("users");
                        userMap.lock(nickname);
                        user = (UserCacheModel) userMap.get((Object) nickname);
                        user.setRechargeFail(user.getRechargeFail() + 1);
                        user.setRechargeFailTime(new Date());
                        userMap.put(nickname, user);
                    } catch (Exception e3) {
                        code = 11;
                        RechargeServiceImpl.logger.debug((Object) e3);
                        MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the", "1001", e3.getMessage());
                    } finally {
                        userMap.unlock(nickname);
                    }
                }
                code = 22;
                res.setFail(user.getRechargeFail());
            } catch (Exception e4) {
                code = 21;
                RechargeServiceImpl.logger.debug((Object) e4);
                System.err.println(e4.getMessage());
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e4.printStackTrace(pw);
                String sStackTrace = sw.toString(); // stack trace as a string
                System.err.println(sStackTrace);
                //return e4.getMessage() + "\n" + sStackTrace;                 
            }
        }
        res.setCode(code);
        return res;
    }

    public RechargeResponse rechargeByECard(String nickname, ProviderType provider, String serial, String pin, String sAmount, String platform, int UserId) throws Exception {
        RechargeServiceImpl.logger.debug((Object) ("Start rechargeByCard MuaCard:  nickname: " + nickname + ", provider: " + provider.getName() + ", serial: " + serial + ", pin: " + pin));
        int code = 1;
        HazelcastInstance client;
        IMap<String, UserModel> userMap;
        String description;
        RechargeResponse res = new RechargeResponse(code, 0L, 0, 0L);
        if (GameCommon.getValueInt("IS_RECHARGE_CARD") == 1 || provider == null) {
            RechargeServiceImpl.logger.debug((Object) "rechargeByCard: param fail");
            code = 2;
            return res;
        }
        if (pin != null) {
            pin = pin.trim();
        }
        if (serial != null) {
            serial = serial.trim();
        }
        if ((client = HazelcastClientFactory.getInstance()) == null) {
            MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the", "1030", "can not connect hazelcast");
            code = 3;
            return res;
        }
        long amount = Long.parseLong(sAmount);
        userMap = client.getMap("users");
        RechargeByCardMessage message = null;
        if (userMap.containsKey((Object) nickname)) {
            try {
                UserCacheModel user = (UserCacheModel) userMap.get((Object) nickname);
                res.setCurrentMoney(user.getVinTotal());
                boolean cardFail = false;
                if (UserValidaton.validateSerialPin((String) serial)) {
                    if (UserValidaton.validateSerialPin((String) pin)) {
                        // check exists card pending
                        RechargeDaoImpl dao = new RechargeDaoImpl();
                        Document doc = dao.getRechargeByGachthe(nickname, serial, pin);
                        if (doc != null) {
                            // pending card
                            code = 30;
                        } else {
                            Random rnd = new Random();
                            Date date = new Date();
                            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                            long randomNumber = 10000 + rnd.nextInt(89999);
                            String id = PartnerConfig.ECardPartnerId + "_" + format.format(date) + "_" + randomNumber;//VinPlayUtils.genTransactionId((int) user.getId());
                            res.setTid(id);
                            ECardClient eCardClient = new ECardClient();
                            org.json.simple.JSONObject result = eCardClient.doCharge(provider.getValue().toUpperCase(), pin, serial, id, amount);
                            if (result != null)
                                logger.debug(result.toJSONString());
                            if ("00".equals(result.get("status").toString())) {
                                try {
                                    code = 30;
                                    // thẻ được chấp nhận
                                    userMap = client.getMap("users");
                                    user = (UserCacheModel) userMap.get((Object) nickname);
                                    long moneyUser = user.getVin();
                                    long currentMoney = user.getVinTotal();
                                    long rechargeMoney = user.getRechargeMoney();
                                    if (code == 30) {
                                        UserServiceImpl userService = new UserServiceImpl();

                                        long money = Math.round((double) amount * GameCommon.getValueDouble("RATIO_RECHARGE_CARD"));
                                        //String nickname, String serial, String pin, int amount, String requestId, String requestTime, int code, String des, int money                                        
                                        dao.saveLogRechargeByGachThe(nickname, serial, pin, amount, id,
                                                System.currentTimeMillis() + "", 30,
                                                "pending_card", currentMoney, provider.getValue(),
                                                platform, currentMoney, money, UserId, user.getUsername(),
                                                "ecard", userService.getUserByNickName(nickname).getClient());
                                    }
                                } catch (Exception e2) {
                                    code = 31;
                                    RechargeServiceImpl.logger.debug((Object) e2);
                                    MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the", "1001", e2.getMessage());
                                } finally {

                                }
                            } else {
                                //code = Integer.parseInt(result.get("Status"));
                                code = 31;
                                cardFail = true;
                            }
                        }
                    } else {
                        code = 35;
                        cardFail = true;
                    }
                } else {
                    code = 36;
                    cardFail = true;
                }
                if (cardFail) {
                    try {
                        userMap = client.getMap("users");
                        userMap.lock(nickname);
                        user = (UserCacheModel) userMap.get((Object) nickname);
                        user.setRechargeFail(user.getRechargeFail() + 1);
                        user.setRechargeFailTime(new Date());
                        userMap.put(nickname, user);
                    } catch (Exception e3) {
                        code = 11;
                        RechargeServiceImpl.logger.debug((Object) e3);
                        MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the", "1001", e3.getMessage());
                    } finally {
                        userMap.unlock(nickname);
                    }
                }
                code = 22;
                res.setFail(user.getRechargeFail());
            } catch (Exception e4) {
                code = 21;
                RechargeServiceImpl.logger.debug((Object) e4);
                System.err.println(e4.getMessage());
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e4.printStackTrace(pw);
                String sStackTrace = sw.toString(); // stack trace as a string
                System.err.println(sStackTrace);
                //return e4.getMessage() + "\n" + sStackTrace;                 
            }
        }
        res.setCode(code);
        return res;
    }

    @Override
    public RechargeResponse rechargeByZoan(String nickname, ProviderType provider, String serial, String pin, String sAmount, String platform, int UserId) throws Exception {
        RechargeServiceImpl.logger.debug((Object) ("Start rechargeByCard Zoan:  nickname: " + nickname + ", provider: " + provider.getName() + ", serial: " + serial + ", pin: " + pin));
        int code = 1;
        HazelcastInstance client;
        IMap<String, UserModel> userMap;
        String description;
        RechargeResponse res = new RechargeResponse(code, 0L, 0, 0L);
        if (GameCommon.getValueInt("IS_RECHARGE_CARD") == 1 || provider == null) {
            RechargeServiceImpl.logger.debug((Object) "rechargeByCard: param fail");
            code = 2;
            return res;
        }
        if (pin != null) {
            pin = pin.trim();
        }
        if (serial != null) {
            serial = serial.trim();
        }
        if ((client = HazelcastClientFactory.getInstance()) == null) {
            MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the", "1030", "can not connect hazelcast");
            code = 3;
            return res;
        }
        long amount = Long.parseLong(sAmount);
        userMap = client.getMap("users");
        RechargeByCardMessage message = null;
        if (userMap.containsKey((Object) nickname)) {
            try {
                UserCacheModel user = (UserCacheModel) userMap.get((Object) nickname);
                res.setCurrentMoney(user.getVinTotal());
                boolean cardFail = false;
                if (UserValidaton.validateSerialPin((String) serial)) {
                    if (UserValidaton.validateSerialPin((String) pin)) {
                        // check exists card pending
                        RechargeDaoImpl dao = new RechargeDaoImpl();
                        Document doc = dao.getRechargeByGachthe(nickname, serial, pin);
                        if (doc != null) {
                            // pending card
                            code = 30;
                        } else {
                            String id = VinPlayUtils.genTransactionId((int) user.getId());
                            res.setTid(id);
                            ZoanCardClient zoanCardClient = new ZoanCardClient();
                            org.json.JSONObject result = zoanCardClient.doCharge(provider.getValue().toUpperCase(), pin, serial, id, amount);
                            if (result != null)
                                logger.debug(result.toString());
                            if ("200".equals(result.get("code").toString())) {
                                try {
                                    code = 30;
                                    // thẻ được chấp nhận
                                    userMap = client.getMap("users");
                                    user = (UserCacheModel) userMap.get((Object) nickname);
                                    long moneyUser = user.getVin();
                                    long currentMoney = user.getVinTotal();
                                    long rechargeMoney = user.getRechargeMoney();
                                    if (code == 30) {
                                        UserServiceImpl userService = new UserServiceImpl();

                                        long money = Math.round((double) amount * GameCommon.getValueDouble("RATIO_RECHARGE_CARD"));
                                        //String nickname, String serial, String pin, int amount, String requestId, String requestTime, int code, String des, int money
                                        dao.saveLogRechargeByGachThe(nickname, serial, pin, amount, id,
                                                System.currentTimeMillis() + "", 30,
                                                "pending_card", currentMoney, provider.getValue(),
                                                platform, currentMoney, money, UserId, user.getUsername(),
                                                "zoan_card", userService.getUserByNickName(nickname).getClient());
                                    }
                                    res.setCode(code);
                                    return res;
                                } catch (Exception e2) {
                                    code = 31;
                                    RechargeServiceImpl.logger.debug((Object) e2);
                                    MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the", "1001", e2.getMessage());
                                } finally {

                                }
                            } else {
                                //code = Integer.parseInt(result.get("Status"));
                                code = 31;
                                cardFail = true;
                            }
                        }
                    } else {
                        code = 35;
                        cardFail = true;
                    }
                } else {
                    code = 36;
                    cardFail = true;
                }
                if (cardFail) {
                    try {
                        userMap = client.getMap("users");
                        userMap.lock(nickname);
                        user = (UserCacheModel) userMap.get((Object) nickname);
                        user.setRechargeFail(user.getRechargeFail() + 1);
                        user.setRechargeFailTime(new Date());
                        userMap.put(nickname, user);
                    } catch (Exception e3) {
                        code = 11;
                        RechargeServiceImpl.logger.debug((Object) e3);
                        MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the", "1001", e3.getMessage());
                    } finally {
                        userMap.unlock(nickname);
                    }
                }
                code = 22;
                res.setFail(user.getRechargeFail());
            } catch (Exception e4) {
                code = 21;
                RechargeServiceImpl.logger.debug((Object) e4);
                System.err.println(e4.getMessage());
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e4.printStackTrace(pw);
                String sStackTrace = sw.toString(); // stack trace as a string
                System.err.println(sStackTrace);
                //return e4.getMessage() + "\n" + sStackTrace;
            }
        }
        res.setCode(code);
        return res;
    }

    // xử lý gach thẻ callback
    public String receiveResultFromGachThe_old_15_01_2020(Map<String, String[]> request) {
        block22:
        {
            try {
                StringBuilder log = new StringBuilder("");
                HashMap<String, String> fields = new HashMap<String, String>();
                for (Map.Entry<String, String[]> entry : request.entrySet()) {
                    String fieldName = entry.getKey();
                    String fieldValue = entry.getValue()[0];
                    log.append(fieldName).append(":").append(fieldValue).append(", ");
                    if (fieldValue == null || fieldValue.length() <= 0) {
                        continue;
                    }
                    fields.put(fieldName, fieldValue);
                }
                logger.debug((Object) ("Gachthe Response: " + log.toString()));
                RechargeByCardMessage message = null;
                HazelcastInstance client;
                if ((client = HazelcastClientFactory.getInstance()) == null) {
                }
                // kiểm tra dữ liệu trả về rỗng
                if (fields.size() <= 0) {
                    logger.debug((Object) ("Gachthe Response: Lỗi Dữ liệu trả về fields.size =0 "));
                    return "invalid trans";
                }
                // tìm theo id
                RechargeDaoImpl dao = new RechargeDaoImpl();
                Document trans = dao.getRechargeByGachthe(fields.get("TrxID"));
                if (trans != null && trans.getInteger("code") == 30) {
                    String nickname = trans.getString("nick_name");
                    if (fields.get("Code") != null && "1".equals(fields.get("Code"))) {
                        IMap<String, UserModel> userMap = client.getMap("users");
                        //get user
                        userMap = client.getMap("users");
                        userMap.lock(nickname);
                        UserCacheModel user = (UserCacheModel) userMap.get((Object) nickname);
                        user = (UserCacheModel) userMap.get((Object) nickname);
                        try {
                            double cardAmount = Double.parseDouble(fields.get("CardValue"));
                            message = new RechargeByCardMessage(nickname, trans.getString("request_id"), trans.getString("provider"),
                                    trans.getString("serial"), trans.getString("pin"), (int) cardAmount, 1, "Thanh cong", 1, (int) cardAmount,
                                    (String) null, (String) null, "gachthe", trans.getString("platform"), (String) null);
                            String description;
                            long moneyUser = user.getVin();
                            long currentMoney = user.getVinTotal();
                            long rechargeMoney = user.getRechargeMoney();
                            // Thẻ ok

                            long money = Math.round(cardAmount * GameCommon.getValueDouble("RATIO_RECHARGE_CARD"));
                            user.setVin(moneyUser += money);
                            user.setVinTotal(currentMoney += money);
                            user.setRechargeMoney(rechargeMoney += money);
                            user.setRechargeFail(0);
                            description = "K\u1ebft qu\u1ea3: Th\u00e0nh c\u00f4ng, M\u00e3 GD: " + trans.getString("request_id") + ", Th\u1ebb: " + trans.getString("provider") + ", M\u1ec7nh gi\u00e1: " + cardAmount + ", Serial: " + trans.getString("serial") + ", Pin: " + trans.getString("pin") + "";
                            MoneyMessageInMinigame messageMoney = new MoneyMessageInMinigame(VinPlayUtils.genMessageId(), user.getId(), nickname, "RechargeByCard", moneyUser, currentMoney, money, "vin", 0L, 0, 0);
                            LogMoneyUserMessage messageLog = new LogMoneyUserMessage(user.getId(), nickname, "RechargeByCard", "N\u1ea1p Win qua th\u1ebb", currentMoney, money, "vin", description, 0L, false, user.isBot());
                            RMQApi.publishMessagePayment((BaseMessage) messageMoney, (int) 16);
                            RMQApi.publishMessageLogMoney((LogMoneyUserMessage) messageLog);
                            RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 301);
                            userMap.put(nickname, user);
                            // update trans success
                            dao.UpdateGachtheTransctions(fields.get("TrxID"), 0, "success");
                            //RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 301);
                        } catch (Exception ex) {
                            logger.debug((Object) ex);
                            MoneyLogger.log("", "RechargeByGachThe", 0L, 0L, "vin", "Nap vin qua the cao", "1001", ex.getMessage());
                            return ex.getMessage();
                        } finally {
                            userMap.unlock(nickname);
                        }
                    } else if (fields.get("Code") != null && "99".equals(fields.get("Code"))) {
                        // bảo trì
                        message = new RechargeByCardMessage(nickname, trans.getString("request_id"), trans.getString("provider"),
                                trans.getString("serial"), trans.getString("pin"), 0, 99, "Cong thanh toan bao tri", 99, 0,
                                (String) null, (String) null, "gachthe", trans.getString("platform"), (String) null);
                        RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 301);
                        dao.UpdateGachtheTransctions(fields.get("TrxID"), 99, "system maintained");
                    } else {
                        // failed
                        message = new RechargeByCardMessage(nickname, trans.getString("request_id"), trans.getString("provider"),
                                trans.getString("serial"), trans.getString("pin"), 0, -1, "The loi", -1, 0,
                                (String) null, (String) null, "gachthe", trans.getString("platform"), (String) null);
                        RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 301);
                        dao.UpdateGachtheTransctions(fields.get("TrxID"), Integer.parseInt(fields.get("Code")), "failed");
                    }
                    return "valid trans";
                } else {
                    return "invalid trans";
                }
            } catch (Exception e4) {
                logger.debug((Object) e4);
                MoneyLogger.log("", "RechargeByGachThe", 0L, 0L, "vin", "Nap vin qua ngan hang", "1001", e4.getMessage());
                return e4.getMessage();
            }
        }
    }

    public String rechargeByMomo(Map<String, String[]> request) {
        try {
            StringBuilder parameter = new StringBuilder();
            HashMap<String, String> fields = new HashMap<>();
            for (Map.Entry<String, String[]> entry : request.entrySet()) {
                String fieldName = entry.getKey();
                String fieldValue = entry.getValue()[0];
                parameter.append(fieldName).append(":").append(fieldValue).append(", ");
                if (fieldValue == null || fieldValue.length() <= 0) {
                    continue;
                }
                fields.put(fieldName, fieldValue);
            }
            Gson gson = new Gson();
            String requestId = fields.get("requestId");
            String status = fields.get("status");
            String money = fields.get("chargeAmount");
            String chargeId = fields.get("chargeId");
            String chargeCode = fields.get("chargeCode");
            String signature = fields.get("signature");
//            String msg = fields.get("result");
            String chargeType = fields.get("chargeType");
            logger.debug("bank_id: " + requestId + " status: " + status + " money: " + money + " signature: " + signature + " result: fff");

            //udpate
//            String nickName = request.getParameter("nn");
            String approvedName = "AUTOBANK";
            String orderId = requestId;
            int realStatus = 0;
            if (status.equalsIgnoreCase("waiting")) {
                realStatus = 0;
            } else if (status.equalsIgnoreCase("success")) {
                realStatus = 2;
            } else if (status.equalsIgnoreCase("timeout")) {
                realStatus = 3;
            }
            long amountReal = 0;
            try {
                amountReal = Long.parseLong(money);
            }catch (Exception e) {
                return "loi amount";
            }
            if (org.apache.commons.lang3.StringUtils.isBlank(orderId)) {
                return "orderId is null or empty";
            }
            String hash = DigestUtils.md5Hex(chargeId + chargeType + chargeCode + money + status + requestId + "dg238iyfuifhdsg54gg5");
//            if (!hash.toLowerCase().equals(signature.toLowerCase())) {
//                return "hash error";
//            }
            HistoryTransService historyTransService = new HistoryTransServiceImpl();
            RechargeDao dao = new RechargeDaoImpl();
            // find transaction in db
            DepositBankModel trans = this.finMoMoDepositByID(requestId.trim());
            if (trans == null) {
                return "Không tồn tại transaction Id";
            }
            // update trạng thái thành công
            if (trans.getStatus() != 100 && realStatus == 2) { // đúng mới cộng tiền
                HistoryTransDao historyTransDao = new HistoryTransDaoImpl();
                historyTransDao.insertTransaction(new HistoryTransModel(HistoryTransConst.MOMO + "|" + trans.getDescription(), "CodePay", "Nạp tiền", amountReal+"", "Thành công", "Nạp tiền Thành công ", trans.Nickname, HistoryTransConst.MOMO, trans.Id));
//                historyTransService.update(trans.Id, trans.Nickname, HistoryTransConst.MOMO, "Thành công", " giao dịch thành công");
                boolean resultUpdateTrans = dao.UpdateDepositMomoManualStatus2(amountReal, requestId, DvtConst.STATUS_APPROVE, "", "Momo Auto");
                if (!resultUpdateTrans) {
                    return "Cập nhật thất bại";
                }

                // cộng tiền
                UserServiceImpl service = new UserServiceImpl();
                try {
                    double fee = GameCommon.getValueDouble("RATIO_RECHARGE_MOMO");
                    double amount = fee * amountReal;
                    long totalFee = Math.round(amountReal - amount);
                    totalFee = totalFee > 0 ? totalFee : 0;
                    service.updateMoneyFromAdmin(trans.Nickname, (long) amount, "vin",
                            Consts.RECHARGE_BY_MOMO, "Nạp Momo",
                            "nạp Momo tự động", totalFee);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                this.cancelMomoById(requestId);
                historyTransService.update(trans.Id, trans.Nickname, HistoryTransConst.MOMO, "Từ chối", "Quá thời gian");
            }
            BroadCastUserMoney.pushBroadCast(trans.Nickname);
            return "ok";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String receiveResultFromGachThe(Map<String, String[]> request) {
        block22:
        {
            try {
                StringBuilder log = new StringBuilder("");
                HashMap<String, String> fields = new HashMap<String, String>();
                for (Map.Entry<String, String[]> entry : request.entrySet()) {
                    String fieldName = entry.getKey();
                    String fieldValue = entry.getValue()[0];
                    log.append(fieldName).append(":").append(fieldValue).append(", ");
                    if (fieldValue == null || fieldValue.length() <= 0) {
                        continue;
                    }
                    fields.put(fieldName, fieldValue);
                }
                logger.debug((Object) ("Gachthe Response: " + log.toString()));
                RechargeByCardMessage message = null;
                HazelcastInstance client;
                if ((client = HazelcastClientFactory.getInstance()) == null) {
                }
                // kiểm tra dữ liệu trả về rỗng
                if (fields.size() <= 0) {
                    logger.debug((Object) ("Gachthe Response: Lỗi Dữ liệu trả về fields.size =0 "));
                    return "invalid trans";
                }
                // tìm theo id
                RechargeDaoImpl dao = new RechargeDaoImpl();
                Document trans = dao.getRechargeByGachthe(fields.get("trans_id"));
                if (trans != null && trans.getInteger("code") == 30) {
                    String nickname = trans.getString("nick_name");
                    if (fields.get("trans_status") != null && "Success".equals(fields.get("trans_status"))) {
                        IMap<String, UserModel> userMap = client.getMap("users");
                        //get user
                        userMap = client.getMap("users");
                        userMap.lock(nickname);
                        UserCacheModel user = (UserCacheModel) userMap.get((Object) nickname);
                        user = (UserCacheModel) userMap.get((Object) nickname);
                        try {
                            double cardAmount = Double.parseDouble(fields.get("card_real_value"));
                            message = new RechargeByCardMessage(nickname, trans.getString("request_id"), trans.getString("provider"),
                                    trans.getString("serial"), trans.getString("pin"), (int) cardAmount, 1, "Thanh cong", 1, (int) cardAmount,
                                    (String) null, (String) null, "gachthe", trans.getString("platform"), (String) null);
                            String description;
                            long moneyUser = user.getVin();
                            long currentMoney = user.getVinTotal();
                            long rechargeMoney = user.getRechargeMoney();
                            // Thẻ ok

                            long money = Math.round(cardAmount * GameCommon.getValueDouble("RATIO_RECHARGE_CARD"));
                            user.setVin(moneyUser += money);
                            user.setVinTotal(currentMoney += money);
                            user.setRechargeMoney(rechargeMoney += money);
                            user.setRechargeFail(0);
                            description = "K\u1ebft qu\u1ea3: Th\u00e0nh c\u00f4ng, M\u00e3 GD: " + trans.getString("request_id") + ", Th\u1ebb: " + trans.getString("provider") + ", M\u1ec7nh gi\u00e1: " + cardAmount + ", Serial: " + trans.getString("serial") + ", Pin: " + trans.getString("pin") + "";
                            MoneyMessageInMinigame messageMoney = new MoneyMessageInMinigame(VinPlayUtils.genMessageId(), user.getId(), nickname, "RechargeByCard", moneyUser, currentMoney, money, "vin", 0L, 0, 0);
                            LogMoneyUserMessage messageLog = new LogMoneyUserMessage(user.getId(), nickname, "RechargeByCard", "N\u1ea1p Win qua th\u1ebb", currentMoney, money, "vin", description, 0L, false, user.isBot());
                            RMQApi.publishMessagePayment((BaseMessage) messageMoney, (int) 16);
                            RMQApi.publishMessageLogMoney((LogMoneyUserMessage) messageLog);
                            RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 301);
                            userMap.put(nickname, user);
                            // update trans success
                            dao.UpdateGachtheTransctions(fields.get("trans_id"), 0, "success");
                            //RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 301);
                        } catch (Exception ex) {
                            logger.debug((Object) ex);
                            MoneyLogger.log("", "RechargeByGachThe", 0L, 0L, "vin", "Nap vin qua the cao", "1001", ex.getMessage());
                            return ex.getMessage();
                        } finally {
                            userMap.unlock(nickname);
                        }
                    }
//                    else if (fields.get("Code") != null && "99".equals(fields.get("Code")))
//                    {
//                        // bảo trì
//                        message = new RechargeByCardMessage(nickname, trans.getString("request_id"), trans.getString("provider"),
//                                trans.getString("serial"), trans.getString("pin"), 0, 99, "Cong thanh toan bao tri", 99, 0,
//                                (String)null, (String)null, "gachthe", trans.getString("platform"), (String)null);
//                        RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 301);
//                        dao.UpdateGachtheTransctions(fields.get("TrxID"), 99, "system maintained");
//                    }
                    else {
                        // failed
                        message = new RechargeByCardMessage(nickname, trans.getString("request_id"), trans.getString("provider"),
                                trans.getString("serial"), trans.getString("pin"), 0, -1, "The loi", -1, 0,
                                (String) null, (String) null, "gachthe", trans.getString("platform"), (String) null);
                        RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 301);
                        dao.UpdateGachtheTransctions(fields.get("TrxID"), Integer.parseInt(fields.get("Code")), "failed");
                    }
                    return "valid trans";
                } else {
                    return "invalid trans";
                }
            } catch (Exception e4) {
                logger.debug((Object) e4);
                MoneyLogger.log("", "RechargeByGachThe", 0L, 0L, "vin", "Nap vin qua ngan hang", "1001", e4.getMessage());
                return e4.getMessage();
            }
        }
    }

    // xử lý nap tien ga thẻ callback
    public String receiveResultFromNapTienGa(Map<String, String[]> request) {
        block22:
        {
            try {
                StringBuilder log = new StringBuilder("");
                HashMap<String, String> fields = new HashMap<String, String>();
                for (Map.Entry<String, String[]> entry : request.entrySet()) {
                    String fieldName = entry.getKey();
                    String fieldValue = entry.getValue()[0];
                    log.append(fieldName).append(":").append(fieldValue).append(", ");
                    if (fieldValue == null || fieldValue.length() <= 0) {
                        continue;
                    }
                    fields.put(fieldName, fieldValue);
                }
                logger.debug((Object) ("receiveResultFromNapTienGa Response: " + log.toString()));
                RechargeByCardMessage message = null;
                HazelcastInstance client;
                if ((client = HazelcastClientFactory.getInstance()) == null) {
                }
                // tìm theo id
                RechargeDaoImpl dao = new RechargeDaoImpl();
                Document trans = dao.getRechargeByGachthe(fields.get("requestId"));
                if (trans != null && trans.getInteger("code") == 30) {
                    String nickname = trans.getString("nick_name");
                    logger.debug((Object) ("receiveResultFromNapTienGa: " + nickname));
//                    Debug.trace((Object)("receiveResultFromNapTienGa: "+nickname));
                    if (fields.get("status") != null && "success".equals(fields.get("status"))) {
                        UserModel umd = new UserServiceImpl().getUserByNickName(nickname);
                        String napTienGaSecretKey = PartnerConfig.NapTienGaSecretKey;
                        String clientName = umd.getClient();
                        if (clientName != null && clientName.equals("V"))
                            napTienGaSecretKey = PartnerConfig.NapTienGaSecretKeyVip52;
                        // check signature
                        String sign = fields.get("requestId") + fields.get("status") + fields.get("menhGiaThe") + napTienGaSecretKey;
                        sign = DvtUtils.getMd5(sign);
                        logger.debug((Object) ("receiveResultFromNapTienGa clientName:" + clientName + " napTienGaSecretKey:" + napTienGaSecretKey));
                        logger.debug((Object) ("receiveResultFromNapTienGa sign:" + sign + " fields.get(\"signature\"):" + fields.get("signature")));
//                        Debug.trace((Object)("receiveResultFromNapTienGa clientName:"+clientName + " napTienGaSecretKey:"+napTienGaSecretKey));
//                        Debug.trace((Object)("receiveResultFromNapTienGa sign:"+sign + " fields.get(\"signature\"):"+fields.get("signature")));

                        if (!sign.equals(fields.get("signature"))) {
                            // failed
                            message = new RechargeByCardMessage(nickname, trans.getString("request_id"), trans.getString("provider"),
                                    trans.getString("serial"), trans.getString("pin"), 0, -1, "sai chữ ký", -1, 0,
                                    (String) null, (String) null, "naptienga", trans.getString("platform"), (String) null);
                            RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 301);
                            dao.UpdateGachtheTransctions(fields.get("requestId"), 70, "signature invalid:" + sign + ";sign2:" + fields.get("signature"));
                            return "signature invalid";
                        }
                        IMap<String, UserModel> userMap = client.getMap("users");
                        //get user
                        userMap = client.getMap("users");
                        userMap.lock(nickname);
                        UserCacheModel user = (UserCacheModel) userMap.get((Object) nickname);
                        user = (UserCacheModel) userMap.get((Object) nickname);
                        try {
                            double cardAmount = Double.parseDouble(fields.get("menhGiaThuc"));
                            message = new RechargeByCardMessage(nickname, trans.getString("request_id"), trans.getString("provider"),
                                    trans.getString("serial"), trans.getString("pin"), (int) cardAmount, 1, "Thanh cong", 1, (int) cardAmount,
                                    (String) null, (String) null, "naptienga", trans.getString("platform"), (String) null);
                            String description;
                            long moneyUser = user.getVin();
                            long currentMoney = user.getVinTotal();
                            long rechargeMoney = user.getRechargeMoney();
                            // Thẻ ok

                            long money = Math.round(cardAmount * GameCommon.getValueDouble("RATIO_RECHARGE_CARD"));
                            user.setVin(moneyUser += money);
                            user.setVinTotal(currentMoney += money);
                            user.setRechargeMoney(rechargeMoney += money);
                            user.setRechargeFail(0);
                            description = "K\u1ebft qu\u1ea3: Th\u00e0nh c\u00f4ng, M\u00e3 GD: " + trans.getString("request_id") + ", Th\u1ebb: " + trans.getString("provider") + ", M\u1ec7nh gi\u00e1: " + cardAmount + ", Serial: " + trans.getString("serial") + ", Pin: " + trans.getString("pin") + "";
                            MoneyMessageInMinigame messageMoney = new MoneyMessageInMinigame(VinPlayUtils.genMessageId(), user.getId(), nickname, "RechargeByCard", moneyUser, currentMoney, money, "vin", 0L, 0, 0);
                            LogMoneyUserMessage messageLog = new LogMoneyUserMessage(user.getId(), nickname, "RechargeByCard", "N\u1ea1p Win qua th\u1ebb", currentMoney, money, "vin", description, 0L, false, user.isBot());
                            RMQApi.publishMessagePayment((BaseMessage) messageMoney, (int) 16);
                            RMQApi.publishMessageLogMoney((LogMoneyUserMessage) messageLog);
                            RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 301);
                            userMap.put(nickname, user);
                            // update trans success
                            dao.UpdateGachtheTransctions(fields.get("requestId"), 0, "success");
                            RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 301);
                        } catch (Exception ex) {
                            logger.debug((Object) ex);
                            MoneyLogger.log("", "RechargeByGachThe", 0L, 0L, "vin", "Nap vin qua ngan hang", "1001", ex.getMessage());
                            return ex.getMessage();
                        } finally {
                            userMap.unlock(nickname);
                        }
                    } else {
                        // failed
                        message = new RechargeByCardMessage(nickname, trans.getString("request_id"), trans.getString("provider"),
                                trans.getString("serial"), trans.getString("pin"), 0, -1, "The loi", -1, 0,
                                (String) null, (String) null, "naptienga", trans.getString("platform"), (String) null);
                        RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 301);
                        dao.UpdateGachtheTransctions(fields.get("requestId"), 1, "failed wtf");
                    }
                    return "valid trans";
                } else {
                    return "invalid trans";
                }
            } catch (Exception e4) {
                logger.debug((Object) e4);
                MoneyLogger.log("", "RechargeByNapTienGa", 0L, 0L, "vin", "Nap vin qua ngan hang", "1001", e4.getMessage());
                return e4.getMessage();
            }
        }
    }

    // xử lý nap tien ga thẻ callback
    public String receiveResultFromMuaCard(Map<String, String[]> request) {
        block22:
        {
            try {
                StringBuilder log = new StringBuilder("");
                HashMap<String, String> fields = new HashMap<String, String>();
                for (Map.Entry<String, String[]> entry : request.entrySet()) {
                    String fieldName = entry.getKey();
                    String fieldValue = entry.getValue()[0];
                    log.append(fieldName).append(":").append(fieldValue).append(", ");
                    if (fieldValue == null || fieldValue.length() <= 0) {
                        continue;
                    }
                    fields.put(fieldName, fieldValue);
                }
                logger.debug((Object) ("receiveResultFromMuaCard Response: " + log.toString()));
                RechargeByCardMessage message = null;
                HazelcastInstance client;
                if ((client = HazelcastClientFactory.getInstance()) == null) {
                }
                // tìm theo id
                RechargeDaoImpl dao = new RechargeDaoImpl();
                Document trans = dao.getRechargeByGachthe(fields.get("trans_id"));
                if (trans != null && trans.getInteger("code") == 30) {
                    String nickname = trans.getString("nick_name");
                    if (fields.get("status") != null && "3".equals(fields.get("status").toString())) {
                        // check signature
                        String sign = PartnerConfig.MuaCardSecretKey + fields.get("trans_id").toString();
                        sign = DvtUtils.getMd5(sign);
                        if (!sign.equals(fields.get("signature").toString())) {
                            // failed
                            message = new RechargeByCardMessage(nickname, trans.getString("request_id"), trans.getString("provider"),
                                    trans.getString("serial"), trans.getString("pin"), 0, -1, "Sai chu ky", -1, 0,
                                    (String) null, (String) null, "naptienga", trans.getString("platform"), (String) null);
                            RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 301);
                            dao.UpdateGachtheTransctions(fields.get("trans_id"), 70, "signature invalid");
                            return "signature invalid";
                        }
                        IMap<String, UserModel> userMap = client.getMap("users");
                        //get user
                        userMap = client.getMap("users");
                        userMap.lock(nickname);
                        UserCacheModel user = (UserCacheModel) userMap.get((Object) nickname);
                        user = (UserCacheModel) userMap.get((Object) nickname);
                        try {
                            double cardAmount = Double.parseDouble(fields.get("amount"));
                            message = new RechargeByCardMessage(nickname, trans.getString("request_id"), trans.getString("provider"),
                                    trans.getString("serial"), trans.getString("pin"), (int) cardAmount, 1, "Thanh cong", 1, (int) cardAmount,
                                    (String) null, (String) null, "MuaCard", trans.getString("platform"), (String) null);
                            String description;
                            long moneyUser = user.getVin();
                            long currentMoney = user.getVinTotal();
                            long rechargeMoney = user.getRechargeMoney();
                            // Thẻ ok

                            long money = Math.round(cardAmount * GameCommon.getValueDouble("RATIO_RECHARGE_CARD"));
                            user.setVin(moneyUser += money);
                            user.setVinTotal(currentMoney += money);
                            user.setRechargeMoney(rechargeMoney += money);
                            user.setRechargeFail(0);
                            description = "K\u1ebft qu\u1ea3: Th\u00e0nh c\u00f4ng, M\u00e3 GD: " + trans.getString("request_id") + ", Th\u1ebb: " + trans.getString("provider") + ", M\u1ec7nh gi\u00e1: " + cardAmount + ", Serial: " + trans.getString("serial") + ", Pin: " + trans.getString("pin") + "";
                            MoneyMessageInMinigame messageMoney = new MoneyMessageInMinigame(VinPlayUtils.genMessageId(), user.getId(), nickname, "RechargeByCard", moneyUser, currentMoney, money, "vin", 0L, 0, 0);
                            LogMoneyUserMessage messageLog = new LogMoneyUserMessage(user.getId(), nickname, "RechargeByCard", "N\u1ea1p Win qua th\u1ebb", currentMoney, money, "vin", description, 0L, false, user.isBot());
                            RMQApi.publishMessagePayment((BaseMessage) messageMoney, (int) 16);
                            RMQApi.publishMessageLogMoney((LogMoneyUserMessage) messageLog);
                            RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 301);
                            userMap.put(nickname, user);
                            // update trans success
                            dao.UpdateGachtheTransctions(fields.get("trans_id"), 0, "success");
                            RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 301);
                        } catch (Exception ex) {
                            logger.debug((Object) ex);
                            MoneyLogger.log("", "RechargeByMuaCard", 0L, 0L, "vin", "Nap vin qua ngan hang", "1001", ex.getMessage());
                            return ex.getMessage();
                        } finally {
                            userMap.unlock(nickname);
                        }
                    } else {
                        // failed
                        message = new RechargeByCardMessage(nickname, trans.getString("request_id"), trans.getString("provider"),
                                trans.getString("serial"), trans.getString("pin"), 0, -1, "The loi", -1, 0,
                                (String) null, (String) null, "naptienga", trans.getString("platform"), (String) null);
                        RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 301);
                        dao.UpdateGachtheTransctions(fields.get("trans_id"), 1, "failed");
                    }
                    return "valid trans";
                } else {
                    return "invalid trans";
                }
            } catch (Exception e4) {
                logger.debug((Object) e4);
                MoneyLogger.log("", "RechargeByMuaCard", 0L, 0L, "vin", "Nap vin qua ngan hang", "1001", e4.getMessage());
                return e4.getMessage();
            }
        }
    }

    // xu ly mua card 24h callback
    public String receiveResultFromMuaCard24h(Map<String, String[]> request) {
        block22:
        {
            try {
                StringBuilder log = new StringBuilder("");
                HashMap<String, String> fields = new HashMap<String, String>();
                for (Map.Entry<String, String[]> entry : request.entrySet()) {
                    String fieldName = entry.getKey();
                    String fieldValue = entry.getValue()[0];
                    log.append(fieldName).append(":").append(fieldValue).append(", ");
                    if (fieldValue == null || fieldValue.length() <= 0) {
                        continue;
                    }
                    fields.put(fieldName, fieldValue);
                }
                logger.debug((Object) ("Muacard24h Response: " + log.toString()));
                RechargeByCardMessage message = null;
                HazelcastInstance client;
                if ((client = HazelcastClientFactory.getInstance()) == null) {
                }
                // tìm theo id
                RechargeDaoImpl dao = new RechargeDaoImpl();
                Document trans = dao.getRechargeByGachthe(fields.get("request_id"));
                if (trans != null && trans.getInteger("code") == 30) {
                    String nickname = trans.getString("nick_name");
                    if (fields.get("status") != null && "1".equals(fields.get("status"))) {
                        // check signature
                        String sign = PartnerConfig.MuaCard24hPartnerKey + trans.getString("pin") + trans.getString("serial");
                        sign = DvtUtils.getMd5(sign);
                        if (!sign.equals(fields.get("callback_sign"))) {
                            // failed
                            message = new RechargeByCardMessage(nickname, trans.getString("request_id"), trans.getString("provider"),
                                    trans.getString("serial"), trans.getString("pin"), 0, -1, "sai chữ ký", -1, 0,
                                    (String) null, (String) null, "muacard24h", trans.getString("platform"), (String) null);
                            RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 301);
                            dao.UpdateGachtheTransctions(fields.get("request_id"), 70, "signature invalid");
                            return "signature invalid";
                        }
                        IMap<String, UserModel> userMap = client.getMap("users");
                        //get user
                        userMap = client.getMap("users");
                        userMap.lock(nickname);
                        UserCacheModel user = (UserCacheModel) userMap.get((Object) nickname);
                        user = (UserCacheModel) userMap.get((Object) nickname);
                        try {
                            double cardAmount = Double.parseDouble(fields.get("value"));
                            message = new RechargeByCardMessage(nickname, trans.getString("request_id"), trans.getString("provider"),
                                    trans.getString("serial"), trans.getString("pin"), (int) cardAmount, 1, "Thanh cong", 1, (int) cardAmount,
                                    (String) null, (String) null, "muacard24h", trans.getString("platform"), (String) null);
                            String description;
                            long moneyUser = user.getVin();
                            long currentMoney = user.getVinTotal();
                            long rechargeMoney = user.getRechargeMoney();
                            // Thẻ ok

                            long money = Math.round(cardAmount * GameCommon.getValueDouble("RATIO_RECHARGE_CARD"));
                            user.setVin(moneyUser += money);
                            user.setVinTotal(currentMoney += money);
                            user.setRechargeMoney(rechargeMoney += money);
                            user.setRechargeFail(0);
                            description = "K\u1ebft qu\u1ea3: Th\u00e0nh c\u00f4ng, M\u00e3 GD: " + trans.getString("request_id") + ", Th\u1ebb: " + trans.getString("provider") + ", M\u1ec7nh gi\u00e1: " + cardAmount + ", Serial: " + trans.getString("serial") + ", Pin: " + trans.getString("pin") + "";
                            MoneyMessageInMinigame messageMoney = new MoneyMessageInMinigame(VinPlayUtils.genMessageId(), user.getId(), nickname, "RechargeByCard", moneyUser, currentMoney, money, "vin", 0L, 0, 0);
                            LogMoneyUserMessage messageLog = new LogMoneyUserMessage(user.getId(), nickname, "RechargeByCard", "N\u1ea1p Win qua th\u1ebb", currentMoney, money, "vin", description, 0L, false, user.isBot());
                            RMQApi.publishMessagePayment((BaseMessage) messageMoney, (int) 16);
                            RMQApi.publishMessageLogMoney((LogMoneyUserMessage) messageLog);
                            RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 301);
                            userMap.put(nickname, user);
                            // update trans success
                            dao.UpdateGachtheTransctions(fields.get("request_id"), 0, "success-" + fields.get("trans_id"));
                            RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 301);
                        } catch (Exception ex) {
                            logger.debug((Object) ex);
                            MoneyLogger.log("", "RechargeByMuaCard24h", 0L, 0L, "vin", "Nap vin qua ngan hang", "1001", ex.getMessage());
                            return ex.getMessage();
                        } finally {
                            userMap.unlock(nickname);
                        }
                    } else {
                        // failed
                        message = new RechargeByCardMessage(nickname, trans.getString("request_id"), trans.getString("provider"),
                                trans.getString("serial"), trans.getString("pin"), 0, -1, "The loi", -1, 0,
                                (String) null, (String) null, "muacard24h", trans.getString("platform"), (String) null);
                        RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 301);
                        dao.UpdateGachtheTransctions(fields.get("request_id"), 1, "failed");
                    }
                    return "valid trans";
                } else {
                    return "invalid trans";
                }
            } catch (Exception e4) {
                logger.debug((Object) e4);
                MoneyLogger.log("", "RechargeByMuacard24h", 0L, 0L, "vin", "Nap vin qua ngan hang", "1001", e4.getMessage());
                return e4.getMessage();
            }
        }
    }

    // xử lý ecard callback
    public String receiveResultFromEcard(Map<String, String[]> request) {
        block22:
        {
            try {
                StringBuilder log = new StringBuilder("");
                HashMap<String, String> fields = new HashMap<String, String>();
                for (Map.Entry<String, String[]> entry : request.entrySet()) {
                    String fieldName = entry.getKey();
                    String fieldValue = entry.getValue()[0];
                    log.append(fieldName).append(":").append(fieldValue).append(", ");
                    if (fieldValue == null || fieldValue.length() <= 0) {
                        continue;
                    }
                    fields.put(fieldName, fieldValue);
                }
                logger.debug((Object) ("Ecard Response: " + log.toString()));
                RechargeByCardMessage message = null;
                HazelcastInstance client;
                if ((client = HazelcastClientFactory.getInstance()) == null) {
                }
                // tìm theo id
                RechargeDaoImpl dao = new RechargeDaoImpl();
                Document trans = dao.getRechargeByGachthe(fields.get("RefCode"));
                if (trans != null && trans.getInteger("code") == 30) {
                    String nickname = trans.getString("nick_name");
                    if (fields.get("Status") != null && "00".equals(fields.get("Status"))) {
                        IMap<String, UserModel> userMap = client.getMap("users");
                        //get user
                        userMap = client.getMap("users");
                        userMap.lock(nickname);
                        UserCacheModel user = (UserCacheModel) userMap.get((Object) nickname);
                        user = (UserCacheModel) userMap.get((Object) nickname);
                        try {
                            double cardAmount = Double.parseDouble(fields.get("Amount"));
                            message = new RechargeByCardMessage(nickname, trans.getString("request_id"), trans.getString("provider"),
                                    trans.getString("serial"), trans.getString("pin"), (int) cardAmount, 1, "Thanh cong", 1, (int) cardAmount,
                                    (String) null, (String) null, "ecard", trans.getString("platform"), (String) null);
                            String description;
                            long moneyUser = user.getVin();
                            long currentMoney = user.getVinTotal();
                            long rechargeMoney = user.getRechargeMoney();
                            // Thẻ ok
                            long money = Math.round(cardAmount * GameCommon.getValueDouble("RATIO_RECHARGE_CARD"));
                            user.setVin(moneyUser += money);
                            user.setVinTotal(currentMoney += money);
                            user.setRechargeMoney(rechargeMoney += money);
                            user.setRechargeFail(0);
                            description = "K\u1ebft qu\u1ea3: Th\u00e0nh c\u00f4ng, M\u00e3 GD: " + trans.getString("request_id") + ", Th\u1ebb: " + trans.getString("provider") + ", M\u1ec7nh gi\u00e1: " + cardAmount + ", Serial: " + trans.getString("serial") + ", Pin: " + trans.getString("pin") + "";
                            MoneyMessageInMinigame messageMoney = new MoneyMessageInMinigame(VinPlayUtils.genMessageId(), user.getId(), nickname, "RechargeByCard", moneyUser, currentMoney, money, "vin", 0L, 0, 0);
                            LogMoneyUserMessage messageLog = new LogMoneyUserMessage(user.getId(), nickname, "RechargeByCard", "N\u1ea1p Win qua th\u1ebb", currentMoney, money, "vin", description, 0L, false, user.isBot());
                            RMQApi.publishMessagePayment((BaseMessage) messageMoney, (int) 16);
                            RMQApi.publishMessageLogMoney((LogMoneyUserMessage) messageLog);
                            RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 301);
                            userMap.put(nickname, user);
                            // update trans success
                            dao.UpdateGachtheTransctions(fields.get("RefCode"), 0, "success");
                            RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 301);
                        } catch (Exception ex) {
                            logger.debug((Object) ex);
                            MoneyLogger.log("", "RechargeByGachThe", 0L, 0L, "vin", "Nap vin qua ngan hang", "1001", ex.getMessage());
                            return ex.getMessage();
                        } finally {
                            userMap.unlock(nickname);
                        }
                    } else {
                        // failed
                        message = new RechargeByCardMessage(nickname, trans.getString("request_id"), trans.getString("provider"),
                                trans.getString("serial"), trans.getString("pin"), 0, -1, "The loi", -1, 0,
                                (String) null, (String) null, "ecard", trans.getString("platform"), (String) null);
                        RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 301);
                        dao.UpdateGachtheTransctions(fields.get("RefCode"), 1, "failed");
                    }
                    return "valid trans";
                } else {
                    return "invalid trans";
                }
            } catch (Exception e4) {
                logger.debug((Object) e4);
                MoneyLogger.log("", "RechargeByECard", 0L, 0L, "vin", "Nap vin qua ngan hang", "1001", e4.getMessage());
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e4.printStackTrace(pw);
                String sStackTrace = sw.toString(); // stack trace as a string
                logger.debug((Object) sStackTrace);
                return e4.getMessage();
            }
        }
    }

    // zoan momo
    public ZoanMomoResponse receiveResultFromZoanMomo(String callback) {
        ZoanMomoResponse response = new ZoanMomoResponse(200, "success");
        try {
            logger.debug((Object) ("Zoan Response: " + callback));
            RechargeByCardMessage message = null;
            HazelcastInstance client;
            if ((client = HazelcastClientFactory.getInstance()) == null) {
            }
            Gson gson = new Gson();
            ZoanMomoReq result = gson.fromJson(callback, ZoanMomoReq.class);
            if (result != null) {
                RechargeDaoImpl dao = new RechargeDaoImpl();
                // find nick name by mobile
                UserServiceImpl userService = new UserServiceImpl();
                List<UserInfoModel> users = userService.checkPhoneByUser(result.getFrom());
                if (users != null && users.size() == 1) {
                    String sign = result.getRequest_id() + "|" + PartnerConfig.MomoZoanPartnerKey + "|" + result.getTrans_time();
                    sign = DvtUtils.getMd5(sign);
                    if (!sign.equals(result.getSignature())) {
                        // failed
                        message = new RechargeByCardMessage(users.get(0).nickName, result.getRequest_id(), "MOMO",
                                result.getFrom(), result.getFrom(), 0, -1, "sai chữ ký", -1, 0,
                                (String) null, (String) null, "ZoanMomo", "MOMO", (String) null);
                        RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 301);
                        dao.saveZoanMomoTran(result, 70, users.get(0).nickName, 0, users.get(0).userName, "web", "");
                        return response;
                    }
                }
                // check signature
                IMap<String, UserModel> userMap = client.getMap("users");
                //get user
                userMap = client.getMap("users");
                String nickname = users.get(0).nickName;
                userMap.lock(nickname);
                UserCacheModel user = (UserCacheModel) userMap.get((Object) nickname);
                user = (UserCacheModel) userMap.get((Object) nickname);
                try {
                    double cardAmount = Double.parseDouble(result.getAmount());
                    message = new RechargeByCardMessage(nickname, result.getRequest_id(), "MOMO",
                            result.getFrom(), result.getFrom(), (int) cardAmount, 1, "Thanh cong", 1, (int) cardAmount,
                            (String) null, (String) null, "ZoanMomo", "web", (String) null);
                    String description;
                    long moneyUser = user.getVin();
                    long currentMoney = user.getVinTotal();
                    long rechargeMoney = user.getRechargeMoney();
                    // Thẻ ok
                    long money = Math.round(cardAmount * GameCommon.getValueDouble("RATIO_RECHARGE_CARD"));
                    user.setVin(moneyUser += money);
                    user.setVinTotal(currentMoney += money);
                    user.setRechargeMoney(rechargeMoney += money);
                    user.setRechargeFail(0);
                    description = "K\u1ebft qu\u1ea3: Th\u00e0nh c\u00f4ng, M\u00e3 GD: " + result.getRequest_id() + ", Th\u1ebb: " + "MOMO" + ", M\u1ec7nh gi\u00e1: " + cardAmount + ", Serial: " + result.getFrom() + ", Pin: " + result.getFrom() + "";
                    MoneyMessageInMinigame messageMoney = new MoneyMessageInMinigame(VinPlayUtils.genMessageId(), user.getId(), nickname, "RechargeByCard", moneyUser, currentMoney, money, "vin", 0L, 0, 0);
                    LogMoneyUserMessage messageLog = new LogMoneyUserMessage(user.getId(), nickname, "RechargeByCard", "N\u1ea1p Win qua th\u1ebb", currentMoney, money, "vin", description, 0L, false, user.isBot());
                    RMQApi.publishMessagePayment((BaseMessage) messageMoney, (int) 16);
                    RMQApi.publishMessageLogMoney((LogMoneyUserMessage) messageLog);
                    RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 301);
                    userMap.put(nickname, user);
                    // update trans success
                    dao.saveZoanMomoTran(result, 0, users.get(0).nickName, 0, users.get(0).userName, "web", "");
                    RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 301);
                } catch (Exception ex) {
                    logger.debug((Object) ex);
                    MoneyLogger.log("", "RechargeByMuaCard24h", 0L, 0L, "vin", "Nap vin qua ngan hang", "1001", ex.getMessage());
                    return response;
                } finally {
                    userMap.unlock(nickname);
                }
            }
        } catch (Exception e4) {

        }
        return response;
    }

    // xử lý nap tien ga thẻ callback
    public String receiveResultFromZoanCard(Map<String, String[]> request) {
        block22:
        {
            try {
                StringBuilder log = new StringBuilder("");
                HashMap<String, String> fields = new HashMap<String, String>();
                for (Map.Entry<String, String[]> entry : request.entrySet()) {
                    String fieldName = entry.getKey();
                    String fieldValue = entry.getValue()[0];
                    log.append(fieldName).append(":").append(fieldValue).append(", ");
                    if (fieldValue == null || fieldValue.length() <= 0) {
                        continue;
                    }
                    fields.put(fieldName, fieldValue);
                }
                logger.debug((Object) ("ZoanCard Response: " + log.toString()));
                RechargeByCardMessage message = null;
                HazelcastInstance client;
                if ((client = HazelcastClientFactory.getInstance()) == null) {
                }
                // tìm theo id
                RechargeDaoImpl dao = new RechargeDaoImpl();
                Document trans = dao.getRechargeByGachthe(fields.get("orderId"));
                if (trans != null && trans.getInteger("code") == 30) {
                    String nickname = trans.getString("nick_name");
                    if (fields.get("status") != null && "1".equals(fields.get("status"))) {
                        IMap<String, UserModel> userMap = client.getMap("users");
                        //get user
                        userMap = client.getMap("users");
                        userMap.lock(nickname);
                        UserCacheModel user = (UserCacheModel) userMap.get((Object) nickname);
                        user = (UserCacheModel) userMap.get((Object) nickname);
                        try {
                            double cardAmount = Double.parseDouble(fields.get("amount"));
                            message = new RechargeByCardMessage(nickname, trans.getString("request_id"), trans.getString("provider"),
                                    trans.getString("serial"), trans.getString("pin"), (int) cardAmount, 1, "Thanh cong", 1, (int) cardAmount,
                                    (String) null, (String) null, "zoan_card", trans.getString("platform"), (String) null);
                            String description;
                            long moneyUser = user.getVin();
                            long currentMoney = user.getVinTotal();
                            long rechargeMoney = user.getRechargeMoney();
                            // Thẻ ok

                            long money = Math.round(cardAmount * GameCommon.getValueDouble("RATIO_RECHARGE_CARD"));
                            user.setVin(moneyUser += money);
                            user.setVinTotal(currentMoney += money);
                            user.setRechargeMoney(rechargeMoney += money);
                            user.setRechargeFail(0);
                            description = "K\u1ebft qu\u1ea3: Th\u00e0nh c\u00f4ng, M\u00e3 GD: " + trans.getString("request_id") + ", Th\u1ebb: " + trans.getString("provider") + ", M\u1ec7nh gi\u00e1: " + cardAmount + ", Serial: " + trans.getString("serial") + ", Pin: " + trans.getString("pin") + "";
                            MoneyMessageInMinigame messageMoney = new MoneyMessageInMinigame(VinPlayUtils.genMessageId(), user.getId(), nickname, "RechargeByCard", moneyUser, currentMoney, money, "vin", 0L, 0, 0);
                            LogMoneyUserMessage messageLog = new LogMoneyUserMessage(user.getId(), nickname, "RechargeByCard", "N\u1ea1p Win qua th\u1ebb", currentMoney, money, "vin", description, 0L, false, user.isBot());
                            RMQApi.publishMessagePayment((BaseMessage) messageMoney, (int) 16);
                            RMQApi.publishMessageLogMoney((LogMoneyUserMessage) messageLog);
                            RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 301);
                            userMap.put(nickname, user);
                            // update trans success
                            dao.UpdateGachtheTransctions(fields.get("requestId"), 0, "success");
                            RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 301);
                        } catch (Exception ex) {
                            logger.debug((Object) ex);
                            MoneyLogger.log("", "RechargeByGachThe", 0L, 0L, "vin", "Nap vin qua ngan hang", "1001", ex.getMessage());
                            return ex.getMessage();
                        } finally {
                            userMap.unlock(nickname);
                        }
                    } else {
                        // failed
                        message = new RechargeByCardMessage(nickname, trans.getString("request_id"), trans.getString("provider"),
                                trans.getString("serial"), trans.getString("pin"), 0, -1, "The loi", -1, 0,
                                (String) null, (String) null, "zoan_card", trans.getString("platform"), (String) null);
                        RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 301);
                        dao.UpdateGachtheTransctions(fields.get("orderId"), 1, "failed");
                    }
                    return "valid trans";
                } else {
                    return "invalid trans";
                }
            } catch (Exception e4) {
                logger.debug((Object) e4);
                MoneyLogger.log("", "RechargeByNapTienGa", 0L, 0L, "vin", "Nap vin qua ngan hang", "1001", e4.getMessage());
                return e4.getMessage();
            }
        }
    }

    private String mapVinplayLucky79Message(String lucky79Code) {
        String trim;
        switch (trim = lucky79Code.trim()) {
            case "waiting": {
                return "Th\u1ebb \u0111ang ch\u1edd \u0111\u1ec3 n\u1ea1p";
            }
            case "processing": {
                return "Th\u1ebb \u0111ang \u0111\u01b0\u1ee3c n\u1ea1p tr\u00ean thi\u1ebft b\u1ecb";
            }
            case "success": {
                return "N\u1ea1p th\u1ebb th\u00e0nh c\u00f4ng";
            }
            case "card_fail": {
                return "N\u1ea1p Kh\u00f4ng th\u1ebb th\u00e0nh c\u00f4ng";
            }
        }
        return "Tr\u1ea1ng th\u00e1i kh\u00f4ng x\u00e1c \u0111\u1ecbnh";
    }

    private int mapLucky79ToVinplayCode(String lucky79Code) {
        String trim;
        switch (trim = lucky79Code.trim()) {
            case "success": {
                return 0;
            }
            case "card_fail": {
                return 35;
            }
            case "processing": {
                return 30;
            }
            case "waiting": {
                return 30;
            }
        }
        return 30;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private RechargeResponse rechargeVcoinCard(String nickname, String serial, String pin, String platform) throws Exception {
        HazelcastInstance client;
        logger.debug((Object) ("Start rechargeVcoinCard:  nickname: " + nickname + ", serial: " + serial + ", pin: " + pin));
        int code = 1;
        RechargeResponse res = new RechargeResponse(code, 0L, 0, 0L);
        if (GameCommon.getValueInt("IS_RECHARGE_CARD") == 1) {
            logger.debug((Object) "rechargeByCard: param fail");
            return res;
        }
        if (pin != null) {
            pin = pin.trim().toUpperCase();
        }
        if (serial != null) {
            serial = serial.trim().toUpperCase();
        }
        if ((client = HazelcastClientFactory.getInstance()) == null) {
            MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the", "1030", "can not connect hazelcast");
            return res;
        }
        IMap<String, UserModel> userMap = client.getMap("users");
        RechargeByCardMessage message = null;
        if (userMap.containsKey((Object) nickname)) {
            try {
                ChargeObj obj;
                UserCacheModel user = (UserCacheModel) userMap.get((Object) nickname);
                res.setCurrentMoney(user.getVinTotal());
                boolean cardFail = false;
                long time = DvtUtils.checkRechargeFail(user.getRechargeFail(), user.getRechargeFailTime());
                if (time <= 0L) {
                    if (UserValidaton.validateSerialPin((String) serial)) {
                        if (UserValidaton.validateSerialPin((String) pin)) {
                            String transId = String.valueOf(VinPlayUtils.generateTransId());
                            obj = null;
                            VTCRechargeResponse response = null;
                            try {
                                response = VTCRechargeClient.rechargeVcoinCard(transId, serial, pin, nickname);
                            } catch (Exception e) {
                                logger.debug((Object) e);
                                MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the", "1034", "Loi ket noi VTC: " + e.getMessage());
                                DvtUtils.errorDvt(client, "RechargeByCard");
                                if (++VTCAlert.disconnectRecharge != GameCommon.getValueInt("COUNT_FAIL")) return null;
                                if (!VinPlayUtils.isAlertTimeout((Date) VTCAlert.alertDisconnectRechargeTime, (int) 1))
                                    return null;
                                this.alert(GameCommon.getValueStr("DISCONNECT_GROUP_NUMBER"), "SOS! Canh bao he thong gach the vcoin VTC dang bi mat ket noi!", true);
                                VTCAlert.alertDisconnectRechargeTime = new Date();
                                return null;
                            }
                            if (response != null) {
                                code = response.getCode();
                                obj = response.getStatus() == -1 ? new ChargeObj(transId, ProviderType.VCOIN.getValue(), serial, pin, response.getAmount(), Integer.parseInt(String.valueOf(response.getResponseCode())), response.getDescription(), "vtc") : new ChargeObj(transId, ProviderType.VCOIN.getValue(), serial, pin, response.getAmount(), response.getStatus(), response.getDescription(), "vtc");
                            }
                            long money = 0L;
                            if (obj != null) {
                                money = Math.round((double) obj.getAmount() * GameCommon.getValueDouble("RATIO_RECHARGE_CARD"));
                                if (code == 0 && (obj.getAmount() == 0 || obj.getAmount() > PhoneCardType._5M.getValue() || obj.getAmount() % PhoneCardType._10K.getValue() != 0)) {
                                    code = 30;
                                }
                                message = new RechargeByCardMessage(nickname, transId, ProviderType.VCOIN.getName(), serial, pin, obj.getAmount(), obj.getStatus(), obj.getMessage(), code, (int) money, (String) null, (String) null, "vtc", platform, (String) null);
                            } else {
                                message = new RechargeByCardMessage(nickname, transId, ProviderType.VCOIN.getName(), serial, pin, 0, -1, "L\u00e1\u00bb\u2014i k\u00e1\u00ba\u00bft n\u00e1\u00bb\u2018i VTC recharge", code, 0, (String) null, (String) null, "vtc", platform, (String) null);
                                ++VTCAlert.disconnectRecharge;
                            }
                            try {
                                userMap = client.getMap("users");
                                userMap.lock(nickname);
                                user = (UserCacheModel) userMap.get((Object) nickname);
                                long moneyUser = user.getVin();
                                long currentMoney = user.getVinTotal();
                                long rechargeMoney = user.getRechargeMoney();
                                if (code == 0) {
                                    user.setVin(moneyUser += money);
                                    user.setVinTotal(currentMoney += money);
                                    user.setRechargeMoney(rechargeMoney += money);
                                    user.setRechargeFail(0);
                                    String description = "K\u00e1\u00ba\u00bft qu\u00e1\u00ba\u00a3: Th\u00c3\u00a0nh c\u00c3\u00b4ng, M\u00c3\u00a3 GD: " + transId + ", Th\u00e1\u00ba\u00bb: " + ProviderType.VCOIN.getName() + ", M\u00e1\u00bb\u2021nh gi\u00c3\u00a1: " + obj.getAmount() + ", Serial: " + serial + ", Pin: " + pin;
                                    MoneyMessageInMinigame messageMoney = new MoneyMessageInMinigame(VinPlayUtils.genMessageId(), user.getId(), nickname, "RechargeByCard", moneyUser, currentMoney, money, "vin", 0L, 0, 0);
                                    LogMoneyUserMessage messageLog = new LogMoneyUserMessage(user.getId(), nickname, "RechargeByCard", "N\u00e1\u00ba\u00a1p Vin b\u00e1\u00ba\u00b1ng th\u00e1\u00ba\u00bb", currentMoney, money, "vin", description, 0L, false, user.isBot());
                                    RMQApi.publishMessagePayment((BaseMessage) messageMoney, (int) 16);
                                    RMQApi.publishMessageLogMoney((LogMoneyUserMessage) messageLog);
                                    RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 301);
                                    userMap.put(nickname, user);
                                    res.setCurrentMoney(currentMoney);
                                    VTCAlert.disconnectRecharge = 0;
                                    VTCAlert.pendingRecharge = 0;
                                }
                                if (code == 30) {
                                    String description = "K\u00e1\u00ba\u00bft qu\u00e1\u00ba\u00a3: \u00c4\ufffdang x\u00e1\u00bb\u00ad l\u00c3\u00bd, M\u00c3\u00a3 GD: " + transId + ", Th\u00e1\u00ba\u00bb: " + ProviderType.VCOIN.getName() + ", M\u00e1\u00bb\u2021nh gi\u00c3\u00a1: " + obj.getAmount() + ", Serial: " + serial + ", Pin: " + pin;
                                    LogMoneyUserMessage messageLog2 = new LogMoneyUserMessage(user.getId(), nickname, "RechargeByCard", "N\u00e1\u00ba\u00a1p Vin b\u00e1\u00ba\u00b1ng th\u00e1\u00ba\u00bb", currentMoney, 0L, "vin", description, 0L, false, user.isBot());
                                    RMQApi.publishMessageLogMoney((LogMoneyUserMessage) messageLog2);
                                    RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 301);
                                    VTCAlert.disconnectRecharge = 0;
                                    ++VTCAlert.pendingRecharge;
                                }
                                if (code != 1) {
                                    cardFail = true;
                                }
                                RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 301);
                            } catch (Exception e2) {
                                code = 1;
                                logger.debug((Object) e2);
                                MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the Vcoin", "1001", e2.getMessage());
                                RechargeResponse rechargeResponse = null;
                                return rechargeResponse;
                            } finally {
                                userMap.unlock(nickname);
                            }
                        } else {
                            code = 35;
                            cardFail = true;
                        }
                    } else {
                        code = 36;
                        cardFail = true;
                    }
                } else {
                    code = 8;
                    res.setTime(time);
                }
                if (cardFail) {
                    try {
                        userMap = client.getMap("users");
                        userMap.lock(nickname);
                        user = (UserCacheModel) userMap.get((Object) nickname);
                        user.setRechargeFail(user.getRechargeFail() + 1);
                        user.setRechargeFailTime(new Date());
                        userMap.put(nickname, user);
                    } catch (Exception e3) {
                        code = 1;
                        logger.debug((Object) e3);
                        MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the Vcoin", "1001", e3.getMessage());
                        return null;
//                        obj = null;
//                        return obj;
                    } finally {
                        userMap.unlock(nickname);
                    }
                }
                res.setFail(user.getRechargeFail());
            } catch (Exception e4) {
                code = 1;
                logger.debug((Object) e4);
                MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the Vcoin", "1001", e4.getMessage());
                return null;
            }
        }
        res.setCode(code);
        logger.debug((Object) ("Finish rechargeVcoinCard, Response : " + code));
        if (VTCAlert.pendingRecharge != GameCommon.getValueInt("COUNT_FAIL")) return res;
        if (!VinPlayUtils.isAlertTimeout((Date) VTCAlert.alertPendingRechargeTime, (int) 1)) return res;
        this.alert(GameCommon.getValueStr("DISCONNECT_GROUP_NUMBER"), "[CANH BAO] He thong VTC tra ve pending qua 5 lan lien tiep Vcoin!", true);
        VTCAlert.alertPendingRechargeTime = new Date();
        return res;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    private RechargeResponse rechargeByCardDvt(String nickname, ProviderType provider, String serial, String pin, String platform) throws Exception {
        RechargeServiceImpl.logger.debug((Object) ("Start rechargeByCard Dvt:  nickname: " + nickname + ", provider: " + provider.getName() + ", serial: " + serial + ", pin: " + pin));
        int code = 1;
        HazelcastInstance client;
        String description;
        RechargeResponse res = new RechargeResponse(code, 0L, 0, 0L);
        if (GameCommon.getValueInt("IS_RECHARGE_CARD") == 1 || provider == null) {
            RechargeServiceImpl.logger.debug((Object) "rechargeByCard: param fail");
            return res;
        }
        if (pin != null) {
            pin = pin.trim();
        }
        if (serial != null) {
            serial = serial.trim();
        }
        if ((client = HazelcastClientFactory.getInstance()) == null) {
            MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the", "1030", "can not connect hazelcast");
            return res;
        }
        IMap<String, UserModel> userMap = client.getMap("users");
        RechargeByCardMessage message = null;
        if (userMap.containsKey((Object) nickname)) {
            try {
                UserCacheModel user = (UserCacheModel) userMap.get((Object) nickname);
                res.setCurrentMoney(user.getVinTotal());
                boolean cardFail = false;
                long time = DvtUtils.checkRechargeFail(user.getRechargeFail(), user.getRechargeFailTime());
                lbl120:
                // 8 sources:
                if (time <= 0L) {
                    if (UserValidaton.validateSerialPin((String) serial)) {
                        if (UserValidaton.validateSerialPin((String) pin)) {
                            String id = String.valueOf(VinPlayUtils.generateTransId());
                            ChargeObj obj = null;
                            try {
                                obj = VinplayClient.rechargeByCard(id, provider.getValue(), serial, pin);
                            } catch (Exception e) {
                                RechargeServiceImpl.logger.debug((Object) e);
                                MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the", "1034", "Loi ket noi dvt: " + e.getMessage());
                                DvtUtils.errorDvt(client, "RechargeByCard");
                                if (++DvtAlert.disconnectRecharge != GameCommon.getValueInt("COUNT_FAIL")) return null;
                                if (VinPlayUtils.isAlertTimeout((Date) DvtAlert.alertDisconnectRechargeTime, (int) 1) == false)
                                    return null;
                                this.alert(GameCommon.getValueStr("DISCONNECT_GROUP_NUMBER"), "SOS! Canh bao he thong dichvuthe dang bi mat ket noi!", true);
                                DvtAlert.alertDisconnectRechargeTime = new Date();
                                return null;
                            }
                            long money = 0L;
                            if (obj != null) {
                                money = Math.round((double) obj.getAmount() * GameCommon.getValueDouble("RATIO_RECHARGE_CARD"));
                                code = this.getErrorCode(obj.getStatus());
                                if (code == 0 && (obj.getAmount() == 0 || obj.getAmount() > PhoneCardType._5M.getValue() || obj.getAmount() % PhoneCardType._10K.getValue() != 0)) {
                                    code = 30;
                                }
                                message = new RechargeByCardMessage(nickname, id, provider.getName(), serial, pin, obj.getAmount(), obj.getStatus(), obj.getMessage(), code, (int) money, (String) null, (String) null, obj.getChannel(), platform, (String) null);
                            } else {
                                message = new RechargeByCardMessage(nickname, id, provider.getName(), serial, pin, 0, -1, "L\u00e1\u00bb\u2014i k\u00e1\u00ba\u00bft n\u00e1\u00bb\u2018i D\u00e1\u00bb\u2039ch v\u00e1\u00bb\u00a5 th\u00e1\u00ba\u00bb", code, 0, (String) null, (String) null, "dvt", platform, (String) null);
                                ++DvtAlert.disconnectRecharge;
                            }
                            try {
                                userMap = client.getMap("users");
                                userMap.lock(nickname);
                                user = (UserCacheModel) userMap.get((Object) nickname);
                                long moneyUser = user.getVin();
                                long currentMoney = user.getVinTotal();
                                long rechargeMoney = user.getRechargeMoney();
                                MoneyMessageInMinigame messageMoney;
                                if (code == 0) {
                                    user.setVin(moneyUser += money);
                                    user.setVinTotal(currentMoney += money);
                                    user.setRechargeMoney(rechargeMoney += money);
                                    user.setRechargeFail(0);
                                    description = "K\u00e1\u00ba\u00bft qu\u00e1\u00ba\u00a3: Th\u00c3\u00a0nh c\u00c3\u00b4ng, M\u00c3\u00a3 GD: " + id + ", Th\u00e1\u00ba\u00bb: " + provider.getName() + ", M\u00e1\u00bb\u2021nh gi\u00c3\u00a1: " + obj.getAmount() + ", Serial: " + serial + ", Pin: " + pin;
                                    messageMoney = new MoneyMessageInMinigame(VinPlayUtils.genMessageId(), user.getId(), nickname, "RechargeByCard", moneyUser, currentMoney, money, "vin", 0L, 0, 0);
                                    LogMoneyUserMessage messageLog = new LogMoneyUserMessage(user.getId(), nickname, "RechargeByCard", "N\u00e1\u00ba\u00a1p Vin b\u00e1\u00ba\u00b1ng th\u00e1\u00ba\u00bb", currentMoney, money, "vin", description, 0L, false, user.isBot());
                                    RMQApi.publishMessagePayment((BaseMessage) messageMoney, (int) 16);
                                    RMQApi.publishMessageLogMoney((LogMoneyUserMessage) messageLog);
                                    RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 301);
                                    userMap.put(nickname, user);
                                    res.setCurrentMoney(currentMoney);
                                    DvtAlert.disconnectRecharge = 0;
                                    if (provider.getName().equals(ProviderType.VIETTEL.getName())) {
                                        DvtAlert.viettelPending = 0;
                                    }
                                    if (provider.getName().equals(ProviderType.MOBIFONE.getName())) {
                                        DvtAlert.mobiPending = 0;
                                    }
                                    if (provider.getName().equals(ProviderType.VINAPHONE.getName())) {
                                        DvtAlert.vinaPending = 0;
                                    }
                                    if (provider.getName().equals(ProviderType.GATE.getName())) {
                                        DvtAlert.gatePending = 0;
                                    }
                                    if (!provider.getName().equals(ProviderType.VCOIN.getName())) break lbl120;
                                    DvtAlert.vcoinPending = 0;
                                }
                                if (code == 30) {
                                    description = "K\u00e1\u00ba\u00bft qu\u00e1\u00ba\u00a3: \u00c4\ufffdang x\u00e1\u00bb\u00ad l\u00c3\u00bd, M\u00c3\u00a3 GD: " + id + ", Th\u00e1\u00ba\u00bb: " + provider.getName() + ", M\u00e1\u00bb\u2021nh gi\u00c3\u00a1: " + obj.getAmount() + ", Serial: " + serial + ", Pin: " + pin;
                                    LogMoneyUserMessage messageLog2 = new LogMoneyUserMessage(user.getId(), nickname, "RechargeByCard", "N\u00e1\u00ba\u00a1p Vin b\u00e1\u00ba\u00b1ng th\u00e1\u00ba\u00bb", currentMoney, 0L, "vin", description, 0L, false, user.isBot());
                                    RMQApi.publishMessageLogMoney((LogMoneyUserMessage) messageLog2);
                                    RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 301);
                                    DvtAlert.disconnectRecharge = 0;
                                    if (provider.getName().equals(ProviderType.VIETTEL.getName())) {
                                        ++DvtAlert.viettelPending;
                                    }
                                    if (provider.getName().equals(ProviderType.MOBIFONE.getName())) {
                                        ++DvtAlert.mobiPending;
                                    }
                                    if (provider.getName().equals(ProviderType.VINAPHONE.getName())) {
                                        ++DvtAlert.vinaPending;
                                    }
                                    if (provider.getName().equals(ProviderType.GATE.getName())) {
                                        ++DvtAlert.gatePending;
                                    }
                                    if (!provider.getName().equals(ProviderType.VCOIN.getName())) break lbl120;
                                    ++DvtAlert.vcoinPending;
                                }
                                if (code != 1) {
                                    cardFail = true;
                                }
                                RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 301);
                            } catch (Exception e2) {
                                code = 1;
                                RechargeServiceImpl.logger.debug((Object) e2);
                                MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the", "1001", e2.getMessage());
                                return null;
//                                var20_29 = null;
//                                return var20_29;
                            } finally {
                                userMap.unlock(nickname);
                            }
                        } else {
                            code = 35;
                            cardFail = true;
                        }
                    } else {
                        code = 36;
                        cardFail = true;
                    }
                } else {
                    code = 8;
                    res.setTime(time);
                }

                if (cardFail) {
                    try {
                        userMap = client.getMap("users");
                        userMap.lock(nickname);
                        user = (UserCacheModel) userMap.get((Object) nickname);
                        user.setRechargeFail(user.getRechargeFail() + 1);
                        user.setRechargeFailTime(new Date());
                        userMap.put(nickname, user);
                    } catch (Exception e3) {
                        code = 1;
                        RechargeServiceImpl.logger.debug((Object) e3);
                        MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the", "1001", e3.getMessage());
                        return null;
//                        obj = null;
//                        return obj;
                    } finally {
                        userMap.unlock(nickname);
                    }
                }
                res.setFail(user.getRechargeFail());
            } catch (Exception e4) {
                code = 1;
                RechargeServiceImpl.logger.debug((Object) e4);
                MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the", "1001", e4.getMessage());
                return null;
            }
        }
        res.setCode(code);
        RechargeServiceImpl.logger.debug((Object) ("Finish rechargeByCard Dvt, Response : " + code));
        if (DvtAlert.viettelPending == GameCommon.getValueInt("COUNT_FAIL") && VinPlayUtils.isAlertTimeout((Date) DvtAlert.alertViettelPendingTime, (int) 1)) {
            this.alert(GameCommon.getValueStr("PENDING_GROUP_NUMBER"), "[CANH BAO] He thong DVT tra ve pending qua 5 lan lien tiep Viettel!", true);
            DvtAlert.alertViettelPendingTime = new Date();
        }
        if (DvtAlert.mobiPending == GameCommon.getValueInt("COUNT_FAIL") && VinPlayUtils.isAlertTimeout((Date) DvtAlert.alertMobiPendingTime, (int) 1)) {
            this.alert(GameCommon.getValueStr("PENDING_GROUP_NUMBER"), "[CANH BAO] He thong DVT tra ve pending qua 5 lan lien tiep Mobifone!", true);
            DvtAlert.alertMobiPendingTime = new Date();
        }
        if (DvtAlert.vinaPending == GameCommon.getValueInt("COUNT_FAIL") && VinPlayUtils.isAlertTimeout((Date) DvtAlert.alertVinaPendingTime, (int) 1)) {
            this.alert(GameCommon.getValueStr("PENDING_GROUP_NUMBER"), "[CANH BAO] He thong DVT tra ve pending qua 5 lan lien tiep Vinaphone!", true);
            DvtAlert.alertVinaPendingTime = new Date();
        }
        if (DvtAlert.gatePending == GameCommon.getValueInt("COUNT_FAIL") && VinPlayUtils.isAlertTimeout((Date) DvtAlert.alertGatePendingTime, (int) 1)) {
            this.alert(GameCommon.getValueStr("PENDING_GROUP_NUMBER"), "[CANH BAO] He thong DVT tra ve pending qua 5 lan lien tiep FPT Gate!", true);
            DvtAlert.alertGatePendingTime = new Date();
        }
        if (DvtAlert.vcoinPending != GameCommon.getValueInt("COUNT_FAIL")) return res;
        if (VinPlayUtils.isAlertTimeout((Date) DvtAlert.alertVcoinPendingTime, (int) 1) == false) return res;
        this.alert(GameCommon.getValueStr("PENDING_GROUP_NUMBER"), "[CANH BAO] He thong DVT tra ve pending qua 5 lan lien tiep FPT Gate!", true);
        DvtAlert.alertVcoinPendingTime = new Date();
        return res;
    }

    private String mapVinplayMessage(String maxpayCode) {
        switch (maxpayCode) {
            case "200": {
                return "G\u1ecdi api th\u00e0nh c\u00f4ng";
            }
            case "400": {
                return "D\u1eef li\u1ec7u g\u1eedi l\u00ean kh\u00f4ng ch\u00ednh x\u00e1c";
            }
            case "404": {
                return "Kh\u00f4ng t\u00ecm th\u1ea5y giao d\u1ecbch th\u1ebb";
            }
            case "1": {
                return "N\u1ea1p th\u1ebb th\u00e0nh c\u00f4ng";
            }
            case "2": {
                return "Th\u1ebb sai ho\u1eb7c \u0111\u00e3 s\u1eed d\u1ee5ng";
            }
            case "3": {
                return "Th\u1ebb b\u1ecb kh\u00f3a";
            }
            case "4": {
                return "S\u1ed1 l\u1ea7n n\u1ea1p th\u1ebb sai li\u00ean ti\u1ebfp v\u01b0\u1ee3t quy \u0111\u1ecbnh";
            }
            case "5": {
                return "Th\u00f4ng tin merchant sai";
            }
            case "6": {
                return "Ch\u01b0a truy\u1ec1n transaction id";
            }
            case "7": {
                return "Th\u1ebb sai \u0111\u1ecbnh d\u1ea1ng";
            }
            case "8": {
                return "Kh\u00f4ng t\u00ecm th\u1ea5y nh\u00e0 cung c\u1ea5p th\u1ebb";
            }
            case "9": {
                return "Th\u00f4ng tin Session sai";
            }
            case "10": {
                return "Session timeout";
            }
            case "11": {
                return "L\u1ed7i h\u1ec7 th\u1ed1ng nh\u00e0 cung c\u1ea5p";
            }
            case "12": {
                return "Merchant b\u1ecb kh\u00f3a";
            }
            case "13": {
                return "Ip kh\u00f4ng h\u1ee3p l\u1ec7";
            }
            case "14": {
                return "Transaction id b\u1ecb tr\u00f9ng";
            }
            case "15": {
                return "Th\u1ebb kh\u00f4ng h\u1ee3p l\u1ec7";
            }
            case "16": {
                return "Lo\u1ea1i th\u1ebb b\u1ecb kh\u00f3a";
            }
            case "17": {
                return "Th\u1ebb \u0111ang x\u1eed l\u00fd";
            }
            case "96": {
                return "N\u1ea1p th\u1ebb th\u1ea5t b\u1ea1i";
            }
            case "98": {
                return "Ch\u1edd x\u1eed l\u00fd";
            }
            case "99": {
                return "Tr\u1ea1ng th\u00e1i kh\u00f4ng x\u00e1c \u0111\u1ecbnh";
            }
        }
        return "Tr\u1ea1ng th\u00e1i kh\u00f4ng x\u00e1c \u0111\u1ecbnh";
    }

    private int mapVinplayCode(String maxpayCode) {
        switch (maxpayCode) {
            case "1": {
                return 0;
            }
            case "2": {
                return 31;
            }
            case "3": {
                return 32;
            }
            case "16": {
                return 32;
            }
            case "7": {
                return 35;
            }
            case "4": {
                return 1;
            }
            case "400": {
                return 1;
            }
            case "404": {
                return 1;
            }
            case "8": {
                return 1;
            }
            case "15": {
                return 1;
            }
            case "96": {
                return 1;
            }
            case "9": {
                return 1;
            }
            case "10": {
                return 1;
            }
            case "11": {
                return 1;
            }
            case "12": {
                return 1;
            }
            case "14": {
                return 1;
            }
            case "5": {
                return 1;
            }
            case "6": {
                return 1;
            }
            case "13": {
                return 1;
            }
            case "17": {
                return 30;
            }
            case "98": {
                return 30;
            }
            case "99": {
                return 30;
            }
        }
        return 30;
    }

    @Override
    public Map<String, Long> updatePendingCardStatus(String startTime, String endTime, String actor) throws Exception {
        HashMap<String, Long> mapRes = new HashMap<String, Long>();
        long totalRecord = 0L;
        long successRecord = 0L;
        if (GameCommon.getValueInt("IS_RECHARGE_CARD") == 1) {
            return mapRes;
        }
        RechargeDaoImpl dao = new RechargeDaoImpl();
        List<RechargeByCardMessage> listPending = dao.getListCardPending(startTime, endTime);
        totalRecord = listPending.size();
        for (RechargeByCardMessage message : listPending) {
            message.setError((String) null);
            if (message.getPartner().equals("maxpay") || message.getPartner().equals("dvt")) {
                if ((message = this.reCheckRechargeByCardMaxpay(message, actor)).getError() != null) continue;
                ++successRecord;
                continue;
            }
            if (!message.getPartner().equals("epay") || !message.getProvider().equals("MegaCard") || (message = this.reCheckRechargeByMegaCard(message, actor)).getError() != null)
                continue;
            ++successRecord;
        }
        mapRes.put("totalRecord", totalRecord);
        mapRes.put("successRecord", successRecord);
        return mapRes;
    }

    @Override
    public RechargeByCardMessage updatePendingCardStatus(String referenceId, String actor) throws Exception {
        RechargeDaoImpl dao = new RechargeDaoImpl();
        RechargeByCardMessage pendingCard = dao.getPendingCardByReferenceId(referenceId);
        if (pendingCard != null && pendingCard.getCode() == 30) {
            pendingCard.setError((String) null);
            if (pendingCard.getPartner().equals("maxpay") || pendingCard.getPartner().equals("dvt")) {
                pendingCard = this.reCheckRechargeByCardMaxpay(pendingCard, actor);
            } else if (pendingCard.getPartner().equals("epay") && pendingCard.getProvider().equals("MegaCard")) {
                pendingCard = this.reCheckRechargeByMegaCard(pendingCard, actor);
            }
        }
        return pendingCard;
    }

    @Override
    public Map<String, Long> reCheckRechargeByCard() throws Exception {
        HashMap<String, Long> mapRes = new HashMap<String, Long>();
        if (GameCommon.getValueInt("IS_RECHARGE_CARD") == 1) {
            return mapRes;
        }
        RechargeDaoImpl dao = new RechargeDaoImpl();
        List<RechargeByCardMessage> listPending = dao.getListCardPending();
        for (RechargeByCardMessage message : listPending) {
            message.setError((String) null);
            if (message.getPartner().equals("dvt") || message.getPartner().equals("maxpay")) {
                this.reCheckRechargeByCardMaxpay(message, "system");
                continue;
            }
            if (!message.getPartner().equals("epay") || !message.getProvider().equals("MegaCard")) continue;
            this.reCheckRechargeByMegaCard(message, "system");
        }
        return mapRes;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private RechargeByCardMessage reCheckRechargeByCardMaxpay(RechargeByCardMessage message, String actor) {
        block19:
        {
            try {
                String response_code;
                ReCheckMaxpayResponse response = null;
                RechargeDaoImpl dao = new RechargeDaoImpl();
                HazelcastInstance client = HazelcastClientFactory.getInstance();
                IMap<String, UserModel> userMap = client.getMap("users");
                MaxpayClient maxpay = new MaxpayClient(GameCommon.getValueStr("MAXPAY_MERCHANT_ID"), GameCommon.getValueStr("MAXPAY_SECRET_KEY"));
                response = maxpay.doReCheck(message.getReferenceId());
                if (response == null) break block19;
                if ("200".equals(response.getResponse_code())) {
                    int code = this.mapVinplayCode(response.getCode());
                    if (code == 30) break block19;
                    long money = 0L;
                    if (code == 0) {
                        if (userMap.containsKey((Object) message.getNickname())) {
                            money = Math.round(response.getCard_amount() * GameCommon.getValueDouble("RATIO_RECHARGE_CARD"));
                            try {
                                userMap.lock(message.getNickname());
                                UserCacheModel user = (UserCacheModel) userMap.get((Object) message.getNickname());
                                long moneyUser = user.getVin();
                                long currentMoney = user.getVinTotal();
                                long rechargeMoney = user.getRechargeMoney();
                                user.setVin(moneyUser += money);
                                user.setVinTotal(currentMoney += money);
                                user.setRechargeMoney(rechargeMoney += money);
                                String description = "M\u00c3\u00a3 GD: " + message.getReferenceId() + ", Th\u00e1\u00ba\u00bb: " + message.getProvider() + ", M\u00e1\u00bb\u2021nh gi\u00c3\u00a1: " + response.getCard_amount() + ", Serial: " + message.getSerial() + ", Pin: " + message.getPin();
                                MoneyMessageInMinigame messageMoney = new MoneyMessageInMinigame(VinPlayUtils.genMessageId(), user.getId(), message.getNickname(), "RechargeByCard", moneyUser, currentMoney, money, "vin", 0L, 0, 0);
                                LogMoneyUserMessage messageLog = new LogMoneyUserMessage(user.getId(), message.getNickname(), "RechargeByCard", "N\u00e1\u00ba\u00a1p vin b\u00e1\u00ba\u00b3ng th\u00e1\u00ba\u00bb", currentMoney, money, "vin", description, 0L, false, user.isBot());
                                RMQApi.publishMessagePayment((BaseMessage) messageMoney, (int) 16);
                                RMQApi.publishMessageLogMoney((LogMoneyUserMessage) messageLog);
                                userMap.put(message.getNickname(), user);
                            } catch (Exception e) {
                                logger.debug((Object) e);
                                code = 1;
                                message.setError("1037");
                            } finally {
                                userMap.unlock(message.getNickname());
                            }
                            if (code == 0) {
                                ArrayList<String> nicknames = new ArrayList<String>();
                                nicknames.add(message.getNickname());
                                MailBoxServiceImpl mailSer = new MailBoxServiceImpl();
                                String content = "N\u00e1\u00ba\u00a1p Vin th\u00c3\u00a0nh c\u00c3\u00b4ng. B\u00e1\u00ba\u00a1n \u00c4\u2018\u00c3\u00a3 nh\u00e1\u00ba\u00adn \u00c4\u2018\u00c6\u00b0\u00e1\u00bb\u00a3c " + money + " Vin. M\u00c3\u00a3 GD: " + message.getReferenceId() + ", Th\u00e1\u00ba\u00bb: " + message.getProvider() + ", M\u00e1\u00bb\u2021nh gi\u00c3\u00a1: " + response.getCard_amount() + ", Serial: " + message.getSerial() + ", Pin: " + message.getPin();
                                mailSer.sendMailBoxFromByNickName(nicknames, "Th\u00c3\u00b4ng b\u00c3\u00a1o n\u00e1\u00ba\u00a1p Vin th\u00c3\u00a0nh c\u00c3\u00b4ng", content);
                                dao.insertLogUpdateCardPending(message.getReferenceId(), message.getNickname(), message.getProvider(), message.getSerial(), message.getPin(), String.valueOf(response.getCard_amount()), String.valueOf(response.getCode()), this.mapVinplayMessage(response.getCode()), String.valueOf(code), message.getTimeLog(), String.valueOf(money), actor);
                                dao.updateCard(message.getReferenceId(), (int) response.getCard_amount(), Integer.parseInt(response.getCode()), this.mapVinplayMessage(response.getCode()), code);
                                message.setAmount((int) response.getCard_amount());
                                message.setStatus(Integer.parseInt(response.getCode()));
                                message.setMessage(this.mapVinplayMessage(response.getCode()));
                                message.setCode(code);
                            }
                            break block19;
                        }
                        message.setError("1038");
                        break block19;
                    }
                    ArrayList<String> nicknames = new ArrayList<String>();
                    nicknames.add(message.getNickname());
                    MailBoxServiceImpl mailSer = new MailBoxServiceImpl();
                    String content = "N\u00e1\u00ba\u00a1p Vin th\u00e1\u00ba\u00a5t b\u00e1\u00ba\u00a1i. M\u00c3\u00a3 GD: " + message.getReferenceId() + ", Th\u00e1\u00ba\u00bb: " + message.getProvider() + ", M\u00e1\u00bb\u2021nh gi\u00c3\u00a1: " + response.getCard_amount() + ", Serial: " + message.getSerial() + ", Pin: " + message.getPin() + ". L\u00c3\u00bd do: " + this.mapVinplayMessage(response.getCode());
                    mailSer.sendMailBoxFromByNickName(nicknames, "Th\u00c3\u00b4ng b\u00c3\u00a1o n\u00e1\u00ba\u00a1p Vin th\u00e1\u00ba\u00a5t b\u00e1\u00ba\u00a1i", content);
                    dao.updateCard(message.getReferenceId(), (int) response.getCard_amount(), Integer.parseInt(response.getCode()), this.mapVinplayMessage(response.getCode()), code);
                    dao.insertLogUpdateCardPending(message.getReferenceId(), message.getNickname(), message.getProvider(), message.getSerial(), message.getPin(), String.valueOf(response.getCard_amount()), String.valueOf(response.getCode()), this.mapVinplayMessage(response.getCode()), String.valueOf(code), message.getTimeLog(), String.valueOf(money), actor);
                    message.setAmount((int) response.getCard_amount());
                    message.setStatus(Integer.parseInt(response.getCode()));
                    message.setMessage(this.mapVinplayMessage(response.getCode()));
                    message.setCode(code);
                    break block19;
                }
                switch (response_code = response.getResponse_code()) {
                    case "400": {
                        message.setError("1039");
                        break;
                    }
                    case "404": {
                        message.setError("1040");
                        break;
                    }
                    default: {
                        message.setError("1041");
                    }
                }
                dao.insertLogUpdateCardPending(message.getReferenceId(), message.getNickname(), message.getProvider(), message.getSerial(), message.getPin(), "0", String.valueOf(response.getResponse_code()), this.mapVinplayMessage(response.getResponse_code()), String.valueOf(this.mapVinplayCode(response.getResponse_code())), message.getTimeLog(), "0", actor);
                dao.updateCard(message.getReferenceId(), 0, Integer.parseInt(response.getResponse_code()), this.mapVinplayMessage(response.getResponse_code()), this.mapVinplayCode(response.getResponse_code()));
            } catch (Exception e2) {
                e2.printStackTrace();
                logger.debug((Object) e2);
            }
        }
        return message;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private RechargeByCardMessage reCheckRechargeByMegaCard(RechargeByCardMessage message, String actor) {
        block10:
        {
            try {
                int code;
                ChargeReponse response = null;
                RechargeDaoImpl dao = new RechargeDaoImpl();
                HazelcastInstance client = HazelcastClientFactory.getInstance();
                IMap<String, UserModel> userMap = client.getMap("users");
                String userNameMega = "";
                userNameMega = message.getUserNameMega() == null || message.getUserNameMega().isEmpty() ? GameCommon.getValueStr("MEGA_USER") : message.getUserNameMega();
                EpayMegaCardCharging.updateConfigMega(userNameMega);
                response = EpayMegaCardCharging.getTransactionStatus(EpayMegaCardCharging.login(), message.getReferenceId());
                if (response == null || (code = this.mapErrorCodeEpayMegaCard(response.getStatus())) == 30)
                    break block10;
                long money = 0L;
                if (code == 0) {
                    if (userMap.containsKey((Object) message.getNickname())) {
                        money = Math.round((double) Integer.parseInt(response.getAmount()) * GameCommon.getValueDouble("RATIO_NAP_MEGA_CARD"));
                        try {
                            userMap.lock(message.getNickname());
                            UserCacheModel user = (UserCacheModel) userMap.get((Object) message.getNickname());
                            long moneyUser = user.getVin();
                            long currentMoney = user.getVinTotal();
                            long rechargeMoney = user.getRechargeMoney();
                            user.setVin(moneyUser += money);
                            user.setVinTotal(currentMoney += money);
                            user.setRechargeMoney(rechargeMoney += money);
                            String description = "M\u00c3\u00a3 GD: " + message.getReferenceId() + ", Th\u00e1\u00ba\u00bb: " + message.getProvider() + ", M\u00e1\u00bb\u2021nh gi\u00c3\u00a1: " + response.getAmount() + ", Serial: " + message.getSerial() + ", Pin: " + message.getPin();
                            MoneyMessageInMinigame messageMoney = new MoneyMessageInMinigame(VinPlayUtils.genMessageId(), user.getId(), message.getNickname(), "RechargeByMegaCard", moneyUser, currentMoney, money, "vin", 0L, 0, 0);
                            LogMoneyUserMessage messageLog = new LogMoneyUserMessage(user.getId(), message.getNickname(), "RechargeByMegaCard", "N\u00e1\u00ba\u00a1p vin b\u00e1\u00ba\u00b3ng th\u00e1\u00ba\u00bb", currentMoney, money, "vin", description, 0L, false, user.isBot());
                            RMQApi.publishMessagePayment((BaseMessage) messageMoney, (int) 16);
                            RMQApi.publishMessageLogMoney((LogMoneyUserMessage) messageLog);
                            userMap.put(message.getNickname(), user);
                        } catch (Exception e) {
                            logger.debug((Object) e);
                            code = 1;
                            message.setError("1037");
                        } finally {
                            userMap.unlock(message.getNickname());
                        }
                        if (code == 0) {
                            ArrayList<String> nicknames = new ArrayList<String>();
                            nicknames.add(message.getNickname());
                            MailBoxServiceImpl mailSer = new MailBoxServiceImpl();
                            String content = "N\u00e1\u00ba\u00a1p Vin th\u00c3\u00a0nh c\u00c3\u00b4ng. B\u00e1\u00ba\u00a1n \u00c4\u2018\u00c3\u00a3 nh\u00e1\u00ba\u00adn \u00c4\u2018\u00c6\u00b0\u00e1\u00bb\u00a3c " + money + " Vin. M\u00c3\u00a3 GD: " + message.getReferenceId() + ", Th\u00e1\u00ba\u00bb: " + message.getProvider() + ", M\u00e1\u00bb\u2021nh gi\u00c3\u00a1: " + response.getAmount() + ", Serial: " + message.getSerial() + ", Pin: " + message.getPin();
                            mailSer.sendMailBoxFromByNickName(nicknames, "Th\u00c3\u00b4ng b\u00c3\u00a1o n\u00e1\u00ba\u00a1p Vin th\u00c3\u00a0nh c\u00c3\u00b4ng", content);
                            dao.insertLogUpdateCardPending(message.getReferenceId(), message.getNickname(), message.getProvider(), message.getSerial(), message.getPin(), response.getAmount(), response.getStatus(), response.getMessage(), String.valueOf(code), message.getTimeLog(), String.valueOf(money), actor);
                            dao.updateCard(message.getReferenceId(), Integer.parseInt(response.getAmount()), Integer.parseInt(response.getStatus()), response.getMessage(), code);
                            message.setAmount(Integer.parseInt(response.getAmount()));
                            message.setStatus(Integer.parseInt(response.getStatus()));
                            message.setMessage(response.getMessage());
                            message.setCode(code);
                        }
                        break block10;
                    }
                    message.setError("1038");
                    break block10;
                }
                ArrayList<String> nicknames = new ArrayList<String>();
                nicknames.add(message.getNickname());
                MailBoxServiceImpl mailSer = new MailBoxServiceImpl();
                String content = "N\u00e1\u00ba\u00a1p Vin th\u00e1\u00ba\u00a5t b\u00e1\u00ba\u00a1i. M\u00c3\u00a3 GD: " + message.getReferenceId() + ", Th\u00e1\u00ba\u00bb: " + message.getProvider() + ", Serial: " + message.getSerial() + ", Pin: " + message.getPin() + ". L\u00c3\u00bd do: " + response.getMessage();
                mailSer.sendMailBoxFromByNickName(nicknames, "Th\u00c3\u00b4ng b\u00c3\u00a1o n\u00e1\u00ba\u00a1p Vin th\u00e1\u00ba\u00a5t b\u00e1\u00ba\u00a1i", content);
                dao.updateCard(message.getReferenceId(), Integer.parseInt(response.getAmount()), Integer.parseInt(response.getStatus()), response.getMessage(), code);
                dao.insertLogUpdateCardPending(message.getReferenceId(), message.getNickname(), message.getProvider(), message.getSerial(), message.getPin(), response.getAmount(), response.getStatus(), response.getMessage(), String.valueOf(code), message.getTimeLog(), String.valueOf(money), actor);
                message.setAmount(Integer.parseInt(response.getAmount()));
                message.setStatus(Integer.parseInt(response.getStatus()));
                message.setMessage(response.getMessage());
                message.setCode(code);
            } catch (Exception e2) {
                e2.printStackTrace();
                logger.debug((Object) e2);
            }
        }
        return message;
    }

    @Override
    public I2BResponse rechargeByBank(String nickname, long money, byte bank, String ip, String platform) throws Exception {
        int code = 1;
        I2BResponse res = new I2BResponse("", code);
        if (GameCommon.getValueInt("IS_RECHARGE_BANK") == 1) {
            return res;
        }
        if (money >= 0L) {
            if (GameCommon.getValueInt("NL_OPEN") != 0) {
                res = NganLuongUtils.setExpressCheckout(nickname, money, bank, ip);
                return res;
            }
            I2BType bankType = I2BType.getBankById((int) bank);
            if (bankType != null) {
                String version = GameCommon.getValueStr("NAPAS_VERSION");
                String command = "pay";
                String accessCode = GameCommon.getValueStr("NAPAS_ACCESS_CODE");
                UserDaoImpl userDao = new UserDaoImpl();
                int userId = userDao.getIdByNickname(nickname);
                String transId = "VP_" + userId + System.currentTimeMillis();
                String merchant = GameCommon.getValueStr("NAPAS_MERCHANT");
                String orderInfo = "N\u00e1\u00ba\u00a1p vin qua ng\u00c3\u00a2n h\u00c3\u00a0ng";
                long amount = money * 100L;
                String urlReturn = GameCommon.getValueStr("NAPAS_URL_RESULT");
                String urlBack = GameCommon.getValueStr("NAPAS_URL_CANCEL");
                String locale = "vn";
                String currencyCode = "VND";
                String paymentGateway = "ATM";
                String cardType = bankType.getName();
                HashMap<String, String> fields = new HashMap<String, String>();
                fields.put("vpc_Version", version);
                fields.put("vpc_Command", "pay");
                fields.put("vpc_AccessCode", accessCode);
                fields.put("vpc_MerchTxnRef", transId);
                fields.put("vpc_Merchant", merchant);
                fields.put("vpc_OrderInfo", "N\u00e1\u00ba\u00a1p vin qua ng\u00c3\u00a2n h\u00c3\u00a0ng");
                fields.put("vpc_Amount", String.valueOf(amount));
                fields.put("vpc_ReturnURL", urlReturn);
                fields.put("vpc_BackURL ", urlBack);
                fields.put("vpc_Locale", "vn");
                fields.put("vpc_CurrencyCode", "VND");
                fields.put("vpc_TicketNo", ip);
                fields.put("vpc_PaymentGateway", "ATM");
                fields.put("vpc_CardType", bankType.getValue());
                RechargeByBankMessage message = new RechargeByBankMessage(nickname, money, cardType, transId, String.valueOf(amount), "N\u00e1\u00ba\u00a1p vin qua ng\u00c3\u00a2n h\u00c3\u00a0ng", ip, platform);
                RechargeDaoImpl dao = new RechargeDaoImpl();
                if (dao.logRechargeByBank(message)) {
                    String url = NapasUtils.getRedirectUrl(fields);
                    logger.debug((Object) ("NAPAS Request: " + url));
                    code = 0;
                    res.setUrl(url);
                }
            } else {
                code = 3;
            }
        } else {
            code = 2;
        }
        res.setCode(code);
        return res;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void receiveResultFromBank(Map<String, String[]> request) {
        block22:
        {
            try {
                if (GameCommon.getValueInt("NL_OPEN") == 0) {
                    StringBuilder log = new StringBuilder("");
                    HashMap<String, String> fields = new HashMap<String, String>();
                    for (Map.Entry<String, String[]> entry : request.entrySet()) {
                        String fieldName = entry.getKey();
                        String fieldValue = entry.getValue()[0];
                        log.append(fieldName).append(":").append(fieldValue).append(", ");
                        if (fieldValue == null || fieldValue.length() <= 0) continue;
                        fields.put(fieldName, fieldValue);
                    }
                    logger.debug((Object) ("NAPAS Response: " + log.toString()));
                    String txnResponseCode = (String) fields.get("vpc_ResponseCode");
                    String merchTxnRef = (String) fields.get("vpc_MerchTxnRef");
                    String amount = (String) fields.get("vpc_Amount");
                    String version = (String) fields.get("vpc_Version");
                    String command = (String) fields.get("vpc_Command");
                    String merchantID = (String) fields.get("vpc_Merchant");
                    String orderInfo = (String) fields.get("vpc_OrderInfo");
                    String currency = (String) fields.get("vpc_CurrencyCode");
                    String locale = (String) fields.get("vpc_Locale");
                    String cardType = (String) fields.get("vpc_CardType");
                    String transactionNo = (String) fields.get("vpc_TransactionNo");
                    String message = (String) fields.get("vpc_Message");
                    if (transactionNo == null) {
                        transactionNo = "";
                    }
                    if (message == null) {
                        message = "";
                    }
                    long money = 0L;
                    String vpcTxnSecureHash = (String) fields.remove("vpc_SecureHash");
                    String secureHash = NapasUtils.hashAllFields(fields);
                    RechargeDaoImpl dao = new RechargeDaoImpl();
                    RechargeByBankMessage mes = null;
                    try {
                        if (merchTxnRef != null) {
                            mes = dao.getRechargeByBank(merchTxnRef);
                        }
                    } catch (Exception e) {
                        logger.debug((Object) e);
                    }
                    String description = "";
                    if (mes == null) {
                        description = "Kh\u00c3\u00b4ng t\u00c3\u00acm th\u00e1\u00ba\u00a5y giao d\u00e1\u00bb\u2039ch";
                    }
                    if (!vpcTxnSecureHash.equalsIgnoreCase(secureHash)) {
                        description = "checksum kh\u00c3\u00b4ng ch\u00c3\u00adnh x\u00c3\u00a1c";
                    }
                    if (txnResponseCode == null) {
                        description = "responseCode is null";
                    }
                    if (description.isEmpty()) {
                        block21:
                        {
                            description = NapasUtils.getResponseDescription(txnResponseCode);
                            String nickname = mes.getNickname();
                            try {
                                if (!txnResponseCode.equals("0")) break block21;
                                money = Long.parseLong(mes.getAmount()) / 100L;
                                money = Math.round((double) money * GameCommon.getValueDouble("RATIO_RECHARGE_BANK"));
                                HazelcastInstance client = HazelcastClientFactory.getInstance();
                                if (client == null) {
                                    MoneyLogger.log(nickname, "RechargeByBank", money, 0L, "vin", "Nap vin qua ngan hang", "1030", "can not connect hazelcast");
                                    return;
                                }
                                IMap<String, UserModel> userMap = client.getMap("users");
                                if (!userMap.containsKey((Object) nickname)) break block21;
                                try {
                                    userMap.lock(nickname);
                                    UserCacheModel user = (UserCacheModel) userMap.get((Object) nickname);
                                    long moneyUser = user.getVin();
                                    long currentMoney = user.getVinTotal();
                                    long rechargeMoney = user.getRechargeMoney();
                                    user.setVin(moneyUser += money);
                                    user.setVinTotal(currentMoney += money);
                                    user.setRechargeMoney(rechargeMoney += money);
                                    String desc = "M\u00c3\u00a3 GD: " + merchTxnRef + ". Ng\u00c3\u00a2n h\u00c3\u00a0ng: " + mes.getBank();
                                    MoneyMessageInMinigame messageMoney = new MoneyMessageInMinigame(VinPlayUtils.genMessageId(), user.getId(), nickname, "RechargeByBank", moneyUser, currentMoney, money, "vin", 0L, 0, 0);
                                    LogMoneyUserMessage messageLog = new LogMoneyUserMessage(user.getId(), nickname, "RechargeByBank", "N\u00e1\u00ba\u00a1p Vin qua ng\u00c3\u00a2n h\u00c3\u00a0ng", currentMoney, money, "vin", desc, 0L, false, user.isBot());
                                    RMQApi.publishMessagePayment((BaseMessage) messageMoney, (int) 16);
                                    RMQApi.publishMessageLogMoney((LogMoneyUserMessage) messageLog);
                                    userMap.put(nickname, user);
                                } catch (Exception e2) {
                                    logger.debug((Object) e2);
                                    MoneyLogger.log(nickname, "RechargeByBank", money, 0L, "vin", "Nap vin qua ngan hang", "1031", "rmq error: " + e2.getMessage());
                                } finally {
                                    userMap.unlock(nickname);
                                }
                            } catch (Exception e3) {
                                logger.debug((Object) e3);
                                MoneyLogger.log(nickname, "RechargeByBank", money, 0L, "vin", "Nap vin qua ngan hang", "1001", e3.getMessage());
                            }
                        }
                        dao.updateRechargeByBank(merchTxnRef, txnResponseCode, description, transactionNo, message, amount);
                        break block22;
                    }
                    dao.insertLogRechargeByBankError(txnResponseCode, version, command, merchTxnRef, merchantID, orderInfo, currency, amount, locale, cardType, transactionNo, message, secureHash, description);
                    break block22;
                }
                NganLuongUtils.receiveResultFromBank(request);
            } catch (Exception e4) {
                logger.debug((Object) e4);
                MoneyLogger.log("", "RechargeByBank", 0L, 0L, "vin", "Nap vin qua ngan hang", "1001", e4.getMessage());
            }
        }
    }

    private int mapErrorCodeEpayMegaCard(String status) {
        switch (status) {
            case "1": {
                return 0;
            }
            case "99": {
                return 30;
            }
            case "51": {
                return 36;
            }
            case "-10": {
                return 35;
            }
            case "-2": {
                return 32;
            }
            case "-3": {
                return 34;
            }
            case "50": {
                return 31;
            }
            case "59": {
                return 33;
            }
            case "53":
            case "-24":
            case "-11":
            case "0":
            case "3":
            case "4":
            case "5":
            case "7":
            case "8":
            case "9":
            case "10":
            case "11":
            case "12":
            case "13":
            case "52":
            case "55":
            case "62":
            case "57":
            case "58":
            case "60":
            case "61":
            case "56":
            case "16":
            case "63":
            case "64":
            case "65":
            case "66":
            case "67":
            case "68": {
                return 40;
            }
        }
        return 30;
    }

    private int getErrorCode(int status) {
        switch (status) {
            case 0: {
                return 0;
            }
            case 10: {
                return 1;
            }
            case 12: {
                return 31;
            }
            case 13: {
                return 35;
            }
            case 14: {
                return 32;
            }
            case 15: {
                return 33;
            }
            case 16: {
                return 34;
            }
            case 17:
            case 18: {
                return 36;
            }
            case 99: {
                return 30;
            }
        }
        return 30;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public byte checkRechargeIAP(String nickname, int iapPackage) {
        int res;
        block8:
        {
            res = 1;
            try {
                IMap userMap;
                HazelcastInstance client;
                if (GameCommon.getValueInt("IS_RECHARGE_IAP") == 1) {
                    return (byte) res;
                }
                IAPModel iapPK = GameCommon.getIAPPackageById(iapPackage);
                if (iapPK == null || !(userMap = (client = HazelcastClientFactory.getInstance()).getMap("users")).containsKey((Object) nickname))
                    break block8;
                try {
                    userMap.lock(nickname);
                    RechargeDaoImpl dao = new RechargeDaoImpl();
                    Calendar cal = Calendar.getInstance();
                    long iapUserInDay = dao.getTotalRechargeIapInday(nickname, cal);
                    cal = Calendar.getInstance();
                    long iapSystemInDay = dao.getTotalRechargeIapInday("", cal);
                    res = iapUserInDay + (long) iapPK.getValue() <= (long) GameCommon.getValueInt("IAP_MAX") && iapSystemInDay + (long) iapPK.getValue() <= (long) GameCommon.getValueInt("SYSTEM_IAP_MAX") ? 0 : 2;
                } catch (Exception e) {
                    logger.debug((Object) e);
                } finally {
                    userMap.unlock(nickname);
                }
            } catch (Exception e2) {
                logger.debug((Object) e2);
            }
        }
        return (byte) res;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public RechargeIAPResponse rechargeIAP(String nickname, String signedData, String signature) {
        RechargeIAPResponse res;
        int code;
        block18:
        {
            code = 1;
            long currentMoney = 0L;
            res = new RechargeIAPResponse(code, currentMoney, 0);
            try {
                if (signature == null || signedData == null || GameCommon.getValueInt("IS_RECHARGE_IAP") == 1) {
                    return res;
                }
                HazelcastInstance client = HazelcastClientFactory.getInstance();
                IMap<String, UserModel> userMap = client.getMap("users");
                if (!userMap.containsKey((Object) nickname)) break block18;
                try {
                    String[] split;
                    userMap.lock(nickname);
                    String[] arr = split = GameCommon.getValueStr("IAP_KEY").split(",,,");
                    for (String base64PublicKey : split) {
                        if (Security.verifyPurchase(base64PublicKey, signedData, signature)) {
                            String itemType = "";
                            RechargeDaoImpl dao = new RechargeDaoImpl();
                            try {
                                Purchase pc = new Purchase("", signedData, signature);
                                if (pc == null) continue;
                                IAPModel iapPK = GameCommon.getIAPPackageByName(pc.getSku());
                                if (iapPK != null) {
                                    res.setProductId(iapPK.getId());
                                    if (!dao.checkOrderId(pc.getOrderId())) {
                                        UserCacheModel user = (UserCacheModel) userMap.get((Object) nickname);
                                        Calendar cal = Calendar.getInstance();
                                        long iapUserInDay = dao.getTotalRechargeIapInday(nickname, cal);
                                        cal = Calendar.getInstance();
                                        long iapSystemInDay = dao.getTotalRechargeIapInday("", cal);
                                        if (iapUserInDay + (long) iapPK.getValue() <= (long) GameCommon.getValueInt("IAP_MAX") && iapSystemInDay + (long) iapPK.getValue() <= (long) GameCommon.getValueInt("SYSTEM_IAP_MAX")) {
                                            int money = iapPK.getValue();
                                            long moneyUser = user.getVin();
                                            currentMoney = user.getVinTotal();
                                            res.setCurrentMoney(currentMoney);
                                            long rechargeMoney = user.getRechargeMoney();
                                            int iapInDay = 0;
                                            if (user.getIapTime() != null && VinPlayUtils.compareDate((Date) new Date(), (Date) user.getIapTime()) == 0) {
                                                iapInDay = user.getIapInDay();
                                            }
                                            user.setVin(moneyUser += (long) money);
                                            user.setVinTotal(currentMoney += (long) money);
                                            user.setRechargeMoney(rechargeMoney += (long) money);
                                            user.setIapInDay(iapInDay += money);
                                            user.setIapTime(new Date());
                                            String description = "K\u00e1\u00ba\u00bft qu\u00e1\u00ba\u00a3: Th\u00c3\u00a0nh c\u00c3\u00b4ng, G\u00c3\u00b3i: " + money + " vin.";
                                            MoneyMessageInMinigame messageMoney = new MoneyMessageInMinigame(VinPlayUtils.genMessageId(), user.getId(), nickname, "RechargeByIAP", moneyUser, currentMoney, (long) money, "vin", 0L, 0, 0);
                                            LogMoneyUserMessage messageLog = new LogMoneyUserMessage(user.getId(), nickname, "RechargeByIAP", "N\u00e1\u00ba\u00a1p Vin qua Google", currentMoney, (long) money, "vin", description, 0L, false, user.isBot());
                                            RMQApi.publishMessagePayment((BaseMessage) messageMoney, (int) 16);
                                            RMQApi.publishMessageLogMoney((LogMoneyUserMessage) messageLog);
                                            dao.saveLogIAP(pc, nickname, money, 0, "Th\u00c3\u00a0nh c\u00c3\u00b4ng");
                                            userMap.put(nickname, user);
                                            res.setCurrentMoney(currentMoney);
                                            code = 0;
                                        }
                                    } else {
                                        code = 2;
                                        logger.debug((Object) ("Order id \u00c4\u2018\u00c3\u00a3 t\u00e1\u00bb\u201cn t\u00e1\u00ba\u00a1i: " + pc.getOrderId()));
                                    }
                                } else {
                                    logger.debug((Object) ("Product ID kh\u00c3\u00b4ng h\u00e1\u00bb\u00a3p l\u00e1\u00bb\u2021: " + pc.getSku()));
                                }
                                break;
                            } catch (Exception e3) {
                                logger.debug((Object) ("Parse signedData fail: " + signedData));
                                continue;
                            }
                        }
                        logger.debug((Object) ("Purchase verification failed: " + signature));
                    }
                } catch (Exception e) {
                    logger.debug((Object) e);
                } finally {
                    userMap.unlock(nickname);
                }
            } catch (Exception e2) {
                logger.debug((Object) e2);
            }
        }
        res.setCode(code);
        return res;
    }

    @Override
    public String smsPlusCheckMO(Map<String, String[]> request) {
        SMSPlusResponse res = new SMSPlusResponse(0, "", "text");
        String sms = GameCommon.SMSPLUS_ERROR_SYSTEM;
        int code = -1;
        String des = "L\u00e1\u00bb\u2014i h\u00e1\u00bb\u2021 th\u00e1\u00bb\u2018ng";
        try {
            if (GameCommon.getValueInt("SMS_PLUS_OPEN") == 0) {
                StringBuilder log = new StringBuilder("");
                HashMap<String, String> fields = new HashMap<String, String>();
                for (Map.Entry<String, String[]> entry : request.entrySet()) {
                    String fieldName = entry.getKey();
                    String fieldValue = entry.getValue()[0];
                    log.append(fieldName).append(":").append(fieldValue).append(", ");
                    if (fieldValue == null || fieldValue.length() <= 0) continue;
                    fields.put(fieldName, fieldValue);
                }
                logger.debug((Object) ("SMS Plus check MO request: " + log.toString()));
                String accessKey = (String) fields.get("access_key");
                int amount = Integer.parseInt((String) fields.get("amount"));
                String commandCode = (String) fields.get("command_code");
                String moMessage = (String) fields.get("mo_message");
                String msisdn = (String) fields.get("msisdn");
                String telco = (String) fields.get("telco");
                String signature = (String) fields.get("signature");
                if (accessKey != null && commandCode != null && moMessage != null && msisdn != null && telco != null && signature != null) {
                    String mobile = DvtUtils.revertMobile84To(msisdn);
                    if (Arrays.asList(DvtUtils.SMS_AMOUNT).contains(amount)) {
                        if (amount >= GameCommon.getValueInt("SMS_PLUS_AMOUNT_MIN")) {
                            if (accessKey.equals(GameCommon.getValueStr("SMS_PLUS_ACCESS_KEY"))) {
                                if ((commandCode = commandCode.toUpperCase()).equals(GameCommon.getValueStr("SMS_PLUS_COMMAND_CODE"))) {
                                    String provider = DvtUtils.getProviderSMS(telco);
                                    if (provider.equals("Viettel")) {
                                        String[] arr = moMessage.trim().split(" ");
                                        if (arr.length == 3) {
                                            String gameCode = arr[0].toUpperCase();
                                            String nap = arr[1].toUpperCase();
                                            String nickname = arr[2];
                                            if (gameCode.equals(GameCommon.getValueStr("SMS_PLUS_GAME_CODE"))) {
                                                if (nap.startsWith("NAP")) {
                                                    fields.remove("signature");
                                                    String key = GameCommon.getValueStr("SMS_PLUS_SECRET_KEY");
                                                    String checksum = VinPlayUtils.hmacDigest((String) DvtUtils.getSMSPlusHMAC(fields), (String) key, (String) "HmacSHA256");
                                                    if (signature.equals(checksum)) {
                                                        HazelcastInstance client = HazelcastClientFactory.getInstance();
                                                        IMap<String, UserModel> userMap = client.getMap("users");
                                                        int checkNN = 1;
                                                        if (userMap.containsKey((Object) nickname)) {
                                                            checkNN = 0;
                                                        } else {
                                                            UserDaoImpl dao = new UserDaoImpl();
                                                            UserModel model = dao.getUserByNickName(nickname);
                                                            if (model != null) {
                                                                nickname = model.getNickname();
                                                                if (userMap.containsKey((Object) nickname)) {
                                                                    checkNN = 0;
                                                                }
                                                            } else {
                                                                checkNN = 2;
                                                            }
                                                        }
                                                        if (checkNN == 0) {
                                                            sms = "";
                                                            res.setStatus(1);
                                                            code = 0;
                                                            des = "Th\u00c3\u00a0nh c\u00c3\u00b4ng";
                                                        } else if (checkNN == 2) {
                                                            sms = String.format(GameCommon.SMSPLUS_ERROR_NICKNAME, nickname);
                                                            logger.debug((Object) ("nickname khong ton tai: " + moMessage));
                                                            des = "nickname kh\u00c3\u00b4ng t\u00e1\u00bb\u201cn t\u00e1\u00ba\u00a1i";
                                                        } else {
                                                            sms = GameCommon.SMSPLUS_ERROR_LOGIN;
                                                            logger.debug((Object) ("nickname khong co tren cache: " + moMessage));
                                                            des = "nickname kh\u00c3\u00b4ng c\u00c3\u00b3 tr\u00c3\u00aan cache";
                                                        }
                                                    } else {
                                                        logger.debug((Object) ("checksum khong hop le: " + signature + " - " + checksum));
                                                        des = "checksum kh\u00c3\u00b4ng h\u00e1\u00bb\u00a3p l\u00e1\u00bb\u2021";
                                                    }
                                                } else {
                                                    sms = GameCommon.SMSPLUS_ERROR_SYNTAX;
                                                    logger.debug((Object) ("maNap khong hop le: " + moMessage));
                                                    des = "maNap kh\u00c3\u00b4ng h\u00e1\u00bb\u00a3p l\u00e1\u00bb\u2021";
                                                }
                                            } else {
                                                sms = GameCommon.SMSPLUS_ERROR_SYNTAX;
                                                logger.debug((Object) ("gameCode khong hop le: " + moMessage));
                                                des = "gameCode kh\u00c3\u00b4ng h\u00e1\u00bb\u00a3p l\u00e1\u00bb\u2021";
                                            }
                                        } else {
                                            sms = GameCommon.SMSPLUS_ERROR_SYNTAX;
                                            logger.debug((Object) ("moMessage khong hop le: " + moMessage));
                                            des = "moMessage kh\u00c3\u00b4ng h\u00e1\u00bb\u00a3p l\u00e1\u00bb\u2021";
                                        }
                                    } else {
                                        logger.debug((Object) ("telco khong hop le: " + telco));
                                        des = "telco kh\u00c3\u00b4ng h\u00e1\u00bb\u00a3p l\u00e1\u00bb\u2021";
                                    }
                                } else {
                                    logger.debug((Object) ("commandCode khong hop le: " + commandCode));
                                    des = "commandCode kh\u00c3\u00b4ng h\u00e1\u00bb\u00a3p l\u00e1\u00bb\u2021";
                                }
                            } else {
                                logger.debug((Object) ("accessKey khong hop le: " + accessKey));
                                des = "accessKey kh\u00c3\u00b4ng h\u00e1\u00bb\u00a3p l\u00e1\u00bb\u2021";
                            }
                        } else {
                            sms = GameCommon.SMSPLUS_ERROR_AMOUNT;
                            logger.debug((Object) ("amount khong hop le: " + amount));
                            des = "amount kh\u00c3\u00b4ng h\u00e1\u00bb\u00a3p l\u00e1\u00bb\u2021";
                        }
                    } else {
                        sms = GameCommon.SMSPLUS_ERROR_AMOUNT;
                        logger.debug((Object) ("amount khong hop le: " + amount));
                        des = "amount kh\u00c3\u00b4ng h\u00e1\u00bb\u00a3p l\u00e1\u00bb\u2021";
                    }
                    RechargeDaoImpl rcdao = new RechargeDaoImpl();
                    rcdao.saveLogRechargeBySMSPlusCheckMO(mobile, moMessage, amount, "9029", code, des);
                } else {
                    logger.debug((Object) "tham so khong hop le");
                }
            } else {
                logger.debug((Object) "khoa sms plus");
            }
        } catch (Exception e) {
            logger.debug((Object) e);
        }
        res.setSms(sms);
        return res.toJson();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    @Override
    public synchronized String smsPlusRequest(Map<String, String[]> request) {
        SMSPlusResponse res = new SMSPlusResponse(0, "", "text");
        int code = -1;
        String des = "L\u00e1\u00bb\u2014i h\u00e1\u00bb\u2021 th\u00e1\u00bb\u2018ng";
        String sms = GameCommon.SMSPLUS_ERROR_SYSTEM;

        try {
            if (GameCommon.getValueInt("SMS_PLUS_OPEN") == 0) {
                StringBuilder log = new StringBuilder("");
                HashMap<String, String> fields = new HashMap<String, String>();
                for (Map.Entry<String, String[]> entry : request.entrySet()) {
                    String fieldName = entry.getKey();
                    String fieldValue = entry.getValue()[0];
                    log.append(fieldName).append(":").append(fieldValue).append(", ");
                    if (fieldValue == null || fieldValue.length() <= 0) continue;
                    fields.put(fieldName, fieldValue);
                }
                RechargeServiceImpl.logger.debug((Object) ("SMS Plus request: " + log.toString()));
                String accessKey = (String) fields.get("access_key");
                int amount = Integer.parseInt((String) fields.get("amount"));
                String commandCode = (String) fields.get("command_code");
                String errorCode = (String) fields.get("error_code");
                String errorMessage = (String) fields.get("error_message");
                String moMessage = (String) fields.get("mo_message");
                String msisdn = (String) fields.get("msisdn");
                String requestId = (String) fields.get("request_id");
                String requestTime = (String) fields.get("request_time");
                String signature = (String) fields.get("signature");
                if (accessKey != null && commandCode != null && errorCode != null && errorMessage != null && moMessage != null && msisdn != null && requestId != null && requestTime != null && signature != null) {
                    String[] arr = moMessage.trim().split(" ");
                    if (arr.length == 3) {
                        String gameCode = arr[0].toUpperCase();
                        String nap = arr[1].toUpperCase();
                        String nickname = arr[2];
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                        String timeRequest = VinPlayUtils.getDateTimeStr((Date) format.parse(requestTime));
                        long money = 0L;
                        String mobile = DvtUtils.revertMobile84To(msisdn);
                        RechargeDaoImpl rcdao = new RechargeDaoImpl();
                        lbl167:
                        // 13 sources:
                        if (Arrays.asList(DvtUtils.SMS_AMOUNT).contains(amount)) {
                            if (amount >= GameCommon.getValueInt("SMS_PLUS_AMOUNT_MIN")) {
                                if (accessKey.equals(GameCommon.getValueStr("SMS_PLUS_ACCESS_KEY"))) {
                                    if ((commandCode = commandCode.toUpperCase()).equals(GameCommon.getValueStr("SMS_PLUS_COMMAND_CODE"))) {
                                        if (gameCode.equals(GameCommon.getValueStr("SMS_PLUS_GAME_CODE"))) {
                                            if (nap.startsWith("NAP")) {
                                                fields.remove("signature");
                                                String key = GameCommon.getValueStr("SMS_PLUS_SECRET_KEY");
                                                String checksum = VinPlayUtils.hmacDigest((String) DvtUtils.getSMSPlusHMAC(fields), (String) key, (String) "HmacSHA256");
                                                if (signature.equals(checksum)) {
                                                    HazelcastInstance client = HazelcastClientFactory.getInstance();
                                                    IMap<String, UserModel> userMap = client.getMap("users");
                                                    int checkNN = 1;
                                                    if (userMap.containsKey((Object) nickname)) {
                                                        checkNN = 0;
                                                    } else {
                                                        UserDaoImpl dao = new UserDaoImpl();
                                                        UserModel model = dao.getUserByNickName(nickname);
                                                        if (model != null) {
                                                            nickname = model.getNickname();
                                                            if (userMap.containsKey((Object) nickname)) {
                                                                checkNN = 0;
                                                            }
                                                        } else {
                                                            checkNN = 2;
                                                        }
                                                    }
                                                    if (checkNN == 0) {
                                                        try {
                                                            userMap.lock(nickname);
                                                            UserCacheModel user = (UserCacheModel) userMap.get((Object) nickname);
                                                            if (!rcdao.checkRequestIdSMSPlus(requestId)) {
                                                                String var35_37 = errorCode;
                                                                int var36_39 = -1;
                                                                switch (var35_37.hashCode()) {
                                                                    case -1585413230: {
                                                                        if (!var35_37.equals("WCG-0000")) break;
                                                                        var36_39 = 0;
                                                                        break;
                                                                    }
                                                                    case -1585413229: {
                                                                        if (!var35_37.equals("WCG-0001")) break;
                                                                        var36_39 = 1;
                                                                        break;
                                                                    }
                                                                    case -1585413228: {
                                                                        if (!var35_37.equals("WCG-0002")) break;
                                                                        var36_39 = 2;
                                                                        break;
                                                                    }
                                                                    case -1585413225: {
                                                                        if (!var35_37.equals("WCG-0005")) break;
                                                                        var36_39 = 3;
                                                                    }
                                                                }
                                                                switch (var36_39) {
                                                                    case 0: {
                                                                        code = 0;
                                                                        des = "Th\u00c3\u00a0nh c\u00c3\u00b4ng";
                                                                        break;
                                                                    }
                                                                    case 1: {
                                                                        code = 1;
                                                                        des = "Thu\u00c3\u00aa bao kh\u00c3\u00b4ng h\u00e1\u00bb\u00a3p l\u00e1\u00bb\u2021";
                                                                        break;
                                                                    }
                                                                    case 2: {
                                                                        code = 2;
                                                                        des = "D\u00e1\u00bb\u00b1 li\u00e1\u00bb\u2021u CP g\u00e1\u00bb\u00adi l\u00c3\u00aan sai";
                                                                        break;
                                                                    }
                                                                    case 3: {
                                                                        code = 5;
                                                                        des = "T\u00c3\u00a0i kho\u00e1\u00ba\u00a3n kh\u00c3\u00b4ng \u00c4\u2018\u00e1\u00bb\u00a7 ti\u00e1\u00bb\ufffdn";
                                                                        break;
                                                                    }
                                                                    default:
                                                                        code = -1;
                                                                        des = "L\u00e1\u00bb\u2014i kh\u00c3\u00b4ng x\u00c3\u00a1c \u00c4\u2018\u00e1\u00bb\u2039nh";
                                                                        break;
                                                                }
//                                                                code = -1;
//                                                                des = "L\u00e1\u00bb\u2014i kh\u00c3\u00b4ng x\u00c3\u00a1c \u00c4\u2018\u00e1\u00bb\u2039nh";
                                                                lbl107:
                                                                // 5 sources:
                                                                if (code != 0) break lbl167;
                                                                long moneyUser = user.getVin();
                                                                long currentMoney = user.getVinTotal();
                                                                long rechargeMoney = user.getRechargeMoney();
                                                                money = Math.round(GameCommon.getValueDouble("RATIO_RECHARGE_SMS") * (double) amount);
                                                                user.setVin(moneyUser += money);
                                                                user.setVinTotal(currentMoney += money);
                                                                user.setRechargeMoney(rechargeMoney += money);
                                                                String description = "SDT: " + mobile + " .N\u00e1\u00bb\u2122i dung tin nh\u00e1\u00ba\u00afn: " + moMessage + ". \u00c4\ufffd\u00e1\u00ba\u00a7u s\u00e1\u00bb\u2018: 9029";
                                                                MoneyMessageInMinigame messageMoney = new MoneyMessageInMinigame(VinPlayUtils.genMessageId(), user.getId(), nickname, "RechargeBySMS", moneyUser, currentMoney, money, "vin", 0L, 0, 0);
                                                                LogMoneyUserMessage messageLog = new LogMoneyUserMessage(user.getId(), nickname, "RechargeBySMS", "N\u00e1\u00ba\u00a1p Vin qua SMS", currentMoney, money, "vin", description, 0L, false, user.isBot());
                                                                RMQApi.publishMessagePayment((BaseMessage) messageMoney, (int) 16);
                                                                RMQApi.publishMessageLogMoney((LogMoneyUserMessage) messageLog);
                                                                userMap.put(nickname, user);
                                                                sms = String.format(GameCommon.SMSPLUS_SUCCESS, new Object[]{nickname, money});
                                                                res.setStatus(1);
                                                            }
                                                            des = "requestId \u00c4\u2018\u00c3\u00a3 x\u00e1\u00bb\u00ad l\u00c3\u00bd";
                                                            RechargeServiceImpl.logger.debug((Object) ("requestId da xu ly: " + requestId));
                                                        } catch (Exception e) {
                                                            RechargeServiceImpl.logger.debug((Object) e);
                                                        } finally {
                                                            userMap.unlock(nickname);
                                                        }
                                                    } else if (checkNN == 2) {
                                                        sms = String.format(GameCommon.SMSPLUS_ERROR_NICKNAME, new Object[]{nickname});
                                                        des = "nickname kh\u00c3\u00b4ng t\u00e1\u00bb\u201cn t\u00e1\u00ba\u00a1i";
                                                        RechargeServiceImpl.logger.debug((Object) ("nickname khong ton tai: " + moMessage));
                                                    } else {
                                                        des = "nickname kh\u00c3\u00b4ng c\u00c3\u00b3 tr\u00c3\u00aan cache";
                                                        sms = GameCommon.SMSPLUS_ERROR_LOGIN;
                                                        RechargeServiceImpl.logger.debug((Object) ("nickname khong co tren cache: " + moMessage));
                                                    }
                                                } else {
                                                    des = "checksum kh\u00c3\u00b4ng h\u00e1\u00bb\u00a3p l\u00e1\u00bb\u2021";
                                                    RechargeServiceImpl.logger.debug((Object) ("checksum khong hop le: " + signature + " - " + checksum));
                                                }
                                            } else {
                                                des = "m\u00c3\u00a3 n\u00e1\u00ba\u00a1p kh\u00c3\u00b4ng h\u00e1\u00bb\u00a3p l\u00e1\u00bb\u2021";
                                                sms = GameCommon.SMSPLUS_ERROR_SYNTAX;
                                                RechargeServiceImpl.logger.debug((Object) ("maNap khong hop le: " + moMessage));
                                            }
                                        } else {
                                            des = "gameCode kh\u00c3\u00b4ng h\u00e1\u00bb\u00a3p l\u00e1\u00bb\u2021";
                                            sms = GameCommon.SMSPLUS_ERROR_SYNTAX;
                                            RechargeServiceImpl.logger.debug((Object) ("gameCode khong hop le: " + moMessage));
                                        }
                                    } else {
                                        des = "commandCode kh\u00c3\u00b4ng h\u00e1\u00bb\u00a3p l\u00e1\u00bb\u2021";
                                        RechargeServiceImpl.logger.debug((Object) ("commandCode khong hop le: " + commandCode));
                                    }
                                } else {
                                    des = "accessKey kh\u00c3\u00b4ng h\u00e1\u00bb\u00a3p l\u00e1\u00bb\u2021";
                                    RechargeServiceImpl.logger.debug((Object) ("accessKey khong hop le: " + accessKey));
                                }
                            } else {
                                sms = GameCommon.SMSPLUS_ERROR_AMOUNT;
                                des = "amount kh\u00c3\u00b4ng h\u00e1\u00bb\u00a3p l\u00e1\u00bb\u2021";
                                RechargeServiceImpl.logger.debug((Object) ("amount khong hop le: " + amount));
                            }
                        } else {
                            sms = GameCommon.SMSPLUS_ERROR_AMOUNT;
                            des = "amount kh\u00c3\u00b4ng h\u00e1\u00bb\u00a3p l\u00e1\u00bb\u2021";
                            RechargeServiceImpl.logger.debug((Object) ("amount khong hop le: " + amount));
                        }

                        rcdao.saveLogRechargeBySMSPlus(nickname, mobile, moMessage, amount, "9029", errorCode, errorMessage, requestId, timeRequest, code, des, (int) money);
                    } else {
                        sms = GameCommon.SMSPLUS_ERROR_SYNTAX;
                        RechargeServiceImpl.logger.debug((Object) ("moMessage khong hop le: " + moMessage));
                    }
                } else {
                    RechargeServiceImpl.logger.debug((Object) "tham so khong hop le");
                }
            } else {
                RechargeServiceImpl.logger.debug((Object) "khoa sms plus");
            }
        } catch (Exception e2) {
            RechargeServiceImpl.logger.debug((Object) e2);
        }
        res.setSms(sms);
        return res.toJson();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    @Override
    public synchronized String sms8xRequest(Map<String, String[]> request) {
        SMSPlusResponse res = new SMSPlusResponse(0, "", "text");
        int code = -1;
        String des = "L\u00e1\u00bb\u2014i h\u00e1\u00bb\u2021 th\u00e1\u00bb\u2018ng";
        String sms = GameCommon.SMSPLUS_ERROR_SYSTEM;
        try {
            if (GameCommon.getValueInt("SMS_OPEN") == 0) {
                StringBuilder log = new StringBuilder("");
                HashMap<String, String> fields = new HashMap<String, String>();
                for (Map.Entry<String, String[]> entry : request.entrySet()) {
                    String fieldName = entry.getKey();
                    String fieldValue = entry.getValue()[0];
                    log.append(fieldName).append(":").append(fieldValue).append(", ");
                    if (fieldValue == null || fieldValue.length() <= 0) continue;
                    fields.put(fieldName, fieldValue);
                }
                RechargeServiceImpl.logger.debug((Object) ("SMS 8x request: " + log.toString()));
                String accessKey = (String) fields.get("access_key");
                String command = (String) fields.get("command");
                String moMessage = (String) fields.get("mo_message");
                String msisdn = (String) fields.get("msisdn");
                String requestId = (String) fields.get("request_id");
                String requestTime = (String) fields.get("request_time");
                String shortCode = (String) fields.get("short_code");
                String signature = (String) fields.get("signature");
                if (accessKey != null && command != null && moMessage != null && msisdn != null && requestId != null && requestTime != null && signature != null) {
                    int amount = DvtUtils.getAmountSMSFromShortCode(shortCode);
                    String[] arr = moMessage.trim().split(" ");
                    if (arr.length == 2) {
                        RechargeDaoImpl rcdao = new RechargeDaoImpl();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                        String timeRequest = VinPlayUtils.getDateTimeStr((Date) format.parse(requestTime));
                        String mobile = DvtUtils.revertMobile84To(msisdn);
                        long money = 0L;
                        String gameCode = arr[0].toUpperCase();
                        String nickname = arr[1];
                        lbl124:
                        // 12 sources:
                        if (Arrays.asList(DvtUtils.SMS_AMOUNT).contains(amount)) {
                            if (amount >= GameCommon.getValueInt("SMS_PLUS_AMOUNT_MIN")) {
                                if (accessKey.equals(GameCommon.getValueStr("SMS_PLUS_ACCESS_KEY"))) {
                                    if ((command = command.toUpperCase()).equals(GameCommon.getValueStr("SMS_COMMAND"))) {
                                        if (gameCode.equals(GameCommon.getValueStr("SMS_COMMAND"))) {
                                            fields.remove("signature");
                                            String key = GameCommon.getValueStr("SMS_PLUS_SECRET_KEY");
                                            String checksum = VinPlayUtils.hmacDigest((String) DvtUtils.getSMSPlusHMAC(fields), (String) key, (String) "HmacSHA256");
                                            if (signature.equals(checksum)) {
                                                HazelcastInstance client = HazelcastClientFactory.getInstance();
                                                IMap<String, UserModel> userMap = client.getMap("users");
                                                int checkNN = 1;
                                                if (userMap.containsKey((Object) nickname)) {
                                                    checkNN = 0;
                                                } else {
                                                    UserDaoImpl dao = new UserDaoImpl();
                                                    UserModel model = dao.getUserByNickName(nickname);
                                                    if (model != null) {
                                                        nickname = model.getNickname();
                                                        if (userMap.containsKey((Object) nickname)) {
                                                            checkNN = 0;
                                                        }
                                                    } else {
                                                        checkNN = 2;
                                                    }
                                                }
                                                if (checkNN == 0) {
                                                    try {
                                                        userMap.lock(nickname);
                                                        UserCacheModel user = (UserCacheModel) userMap.get((Object) nickname);
                                                        if (!rcdao.checkRequestIdSMS(requestId)) {
                                                            code = 0;
                                                            des = "Th\u00c3\u00a0nh c\u00c3\u00b4ng";
                                                            if (code != 0) break lbl124;
                                                            long moneyUser = user.getVin();
                                                            long currentMoney = user.getVinTotal();
                                                            long rechargeMoney = user.getRechargeMoney();
                                                            money = Math.round(GameCommon.getValueDouble("RATIO_RECHARGE_SMS") * (double) amount);
                                                            user.setVin(moneyUser += money);
                                                            user.setVinTotal(currentMoney += money);
                                                            user.setRechargeMoney(rechargeMoney += money);
                                                            String description = "SDT: " + mobile + " .N\u00e1\u00bb\u2122i dung tin nh\u00e1\u00ba\u00afn: " + moMessage + ". \u00c4\ufffd\u00e1\u00ba\u00a7u s\u00e1\u00bb\u2018: " + shortCode;
                                                            MoneyMessageInMinigame messageMoney = new MoneyMessageInMinigame(VinPlayUtils.genMessageId(), user.getId(), nickname, "RechargeBySMS", moneyUser, currentMoney, money, "vin", 0L, 0, 0);
                                                            LogMoneyUserMessage messageLog = new LogMoneyUserMessage(user.getId(), nickname, "RechargeBySMS", "N\u00e1\u00ba\u00a1p Vin qua SMS", currentMoney, money, "vin", description, 0L, false, user.isBot());
                                                            RMQApi.publishMessagePayment((BaseMessage) messageMoney, (int) 16);
                                                            RMQApi.publishMessageLogMoney((LogMoneyUserMessage) messageLog);
                                                            userMap.put(nickname, user);
                                                            sms = String.format(GameCommon.SMSPLUS_SUCCESS, new Object[]{nickname, money});
                                                            res.setStatus(1);
                                                        }
                                                        des = "requestId \u00c4\u2018\u00c3\u00a3 x\u00e1\u00bb\u00ad l\u00c3\u00bd";
                                                    } catch (Exception e) {
                                                        RechargeServiceImpl.logger.debug((Object) e);
                                                    } finally {
                                                        userMap.unlock(nickname);
                                                    }
                                                } else if (checkNN == 2) {
                                                    sms = String.format(GameCommon.SMSPLUS_ERROR_NICKNAME, new Object[]{nickname});
                                                    RechargeServiceImpl.logger.debug((Object) ("nickname khong ton tai: " + moMessage));
                                                    des = "nickname kh\u00c3\u00b4ng t\u00e1\u00bb\u201cn t\u00e1\u00ba\u00a1i";
                                                } else {
                                                    sms = GameCommon.SMSPLUS_ERROR_LOGIN;
                                                    RechargeServiceImpl.logger.debug((Object) ("nickname khong co tren cache: " + moMessage));
                                                    des = "nickname kh\u00c3\u00b4ng c\u00c3\u00b3 tr\u00c3\u00aan cache";
                                                }
                                            } else {
                                                des = "checksum kh\u00c3\u00b4ng h\u00e1\u00bb\u00a3p l\u00e1\u00bb\u2021";
                                                RechargeServiceImpl.logger.debug((Object) ("checksum khong hop le: " + signature + " - " + checksum));
                                            }
                                        } else {
                                            des = "gameCode kh\u00c3\u00b4ng h\u00e1\u00bb\u00a3p l\u00e1\u00bb\u2021";
                                            sms = GameCommon.SMSPLUS_ERROR_SYNTAX;
                                            RechargeServiceImpl.logger.debug((Object) ("gameCode khong hop le: " + moMessage));
                                        }
                                    } else {
                                        des = "command kh\u00c3\u00b4ng h\u00e1\u00bb\u00a3p l\u00e1\u00bb\u2021";
                                        RechargeServiceImpl.logger.debug((Object) ("command khong hop le: " + command));
                                    }
                                } else {
                                    des = "accessKey kh\u00c3\u00b4ng h\u00e1\u00bb\u00a3p l\u00e1\u00bb\u2021";
                                    RechargeServiceImpl.logger.debug((Object) ("accessKey khong hop le: " + accessKey));
                                }
                            } else {
                                sms = GameCommon.SMSPLUS_ERROR_AMOUNT;
                                des = "amount kh\u00c3\u00b4ng h\u00e1\u00bb\u00a3p l\u00e1\u00bb\u2021";
                                RechargeServiceImpl.logger.debug((Object) ("amount khong hop le: " + amount));
                            }
                        } else {
                            sms = GameCommon.SMSPLUS_ERROR_AMOUNT;
                            des = "shortCode kh\u00c3\u00b4ng h\u00e1\u00bb\u00a3p l\u00e1\u00bb\u2021";
                            RechargeServiceImpl.logger.debug((Object) ("shortCode khong hop le: " + shortCode));
                        }

                        rcdao.saveLogRechargeBySMS(nickname, mobile, moMessage, amount, shortCode, requestId, timeRequest, code, des, (int) money);
                    } else {
                        sms = GameCommon.SMSPLUS_ERROR_SYNTAX;
                        RechargeServiceImpl.logger.debug((Object) ("moMessage khong hop le: " + moMessage));
                    }
                } else {
                    RechargeServiceImpl.logger.debug((Object) "tham so khong hop le");
                }
            } else {
                RechargeServiceImpl.logger.debug((Object) "khoa sms");
            }
        } catch (Exception e2) {
            RechargeServiceImpl.logger.debug((Object) e2);
        }
        res.setSms(sms);
        return res.toJson();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized RechargeApiOTPResponse sendRequestChargingOTP(String nickname, String mobile, int amount) {
        int code = 1;
        String url = "";
        String requestId = "";
        String transId = "";
        int fail = 0;
        long time = 0L;
        HazelcastInstance client = HazelcastClientFactory.getInstance();
        IMap<String, UserModel> userMap = client.getMap("users");
        if (userMap.containsKey((Object) nickname)) {
            try {
                if (GameCommon.getValueInt("API_OTP_OPEN") == 0) {
                    userMap.lock(nickname);
                    UserCacheModel user = (UserCacheModel) userMap.get((Object) nickname);
                    fail = user.getApiOTPFail();
                    if (UserValidaton.validateMobileVN((String) mobile)) {
                        if (Arrays.asList(DvtUtils.SMS_AMOUNT).contains(amount) && amount >= GameCommon.getValueInt("SMS_PLUS_AMOUNT_MIN")) {
                            time = DvtUtils.checkApiOtpFail(fail, user.getApiOTPFailTime());
                            if (time <= 0L) {
                                time = DvtUtils.checkApiOtpTimeDelay(user.getApiOTPTime());
                                if (time <= 0L) {
                                    requestId = new StringBuilder(user.getId()).append(System.currentTimeMillis()).toString();
                                    HashMap<String, String> params = new HashMap<String, String>();
                                    params.put("access_key", GameCommon.getValueStr("SMS_PLUS_ACCESS_KEY"));
                                    params.put("amount", String.valueOf(amount));
                                    params.put("content", nickname);
                                    params.put("msisdn", DvtUtils.revertMobile0To84(mobile));
                                    params.put("requestId", requestId);
                                    String signature = VinPlayUtils.hmacDigest((String) DvtUtils.getSMSPlusHMAC(params), (String) GameCommon.getValueStr("SMS_PLUS_SECRET_KEY"), (String) "HmacSHA256");
                                    params.put("signature", signature);
                                    String response = HttpURLClient.sendPOST(GameCommon.getValueStr("API_OTP_URL_REQUEST"), params);
                                    JSONObject obj = new JSONObject(response);
                                    String errorMessage = obj.getString("errorMessage");
                                    String rpRequestId = obj.getString("requestId");
                                    String rpTransId = obj.getString("transId");
                                    String errorCode = obj.getString("errorCode");
                                    String redirectUrl = obj.getString("redirectUrl");
                                    if (errorMessage != null && rpRequestId != null && rpTransId != null && errorCode != null && requestId.equals(rpRequestId)) {
                                        transId = rpTransId;
                                        code = (byte) DvtUtils.getCodeApiOTP(errorCode, false);
                                        if (code == 0) {
                                            if (redirectUrl != null && !redirectUrl.isEmpty()) {
                                                url = redirectUrl;
                                            }
                                            IMap apiOtpMap = client.getMap("cacheApiOtp");
                                            ApiOtpModel apiOtp = new ApiOtpModel(rpRequestId, transId, mobile, nickname, amount);
                                            apiOtpMap.put((Object) requestId, (Object) apiOtp, (long) GameCommon.getValueInt("API_OTP_TIMEOUT"), TimeUnit.MINUTES);
                                        }
                                        RechargeDaoImpl rcdao = new RechargeDaoImpl();
                                        rcdao.saveLogRequestApiOTP(nickname, mobile, amount, errorCode, errorMessage, requestId, transId, code, redirectUrl);
                                    }
                                } else {
                                    code = 8;
                                }
                            } else {
                                code = 7;
                            }
                        }
                    } else {
                        code = 2;
                    }
                } else {
                    logger.debug((Object) "khoa nap qua api otp");
                }
            } catch (Exception e) {
                logger.debug((Object) e);
            } finally {
                userMap.unlock(nickname);
            }
        }
        RechargeApiOTPResponse res = new RechargeApiOTPResponse((byte) code, requestId, transId, url, 0L, fail, time);
        return res;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized RechargeApiOTPResponse sendConfirmChargingOTP(String nickname, String requestId, String otp) {
        byte code = 1;
        long currentMoney = 0L;
        HazelcastInstance client = HazelcastClientFactory.getInstance();
        IMap<String, UserModel> userMap = client.getMap("users");
        IMap apiOtpMap = client.getMap("cacheApiOtp");
        if (userMap.containsKey((Object) nickname)) {
            try {
                if (GameCommon.getValueInt("API_OTP_OPEN") == 0) {
                    userMap.lock(nickname);
                    UserCacheModel user = (UserCacheModel) userMap.get((Object) nickname);
                    if (requestId != null && UserValidaton.validate((String) otp, (String) GameCommon.getValueStr("API_OTP_FORMAT")) && apiOtpMap.containsKey((Object) requestId)) {
                        ApiOtpModel apiOtp = (ApiOtpModel) apiOtpMap.get((Object) requestId);
                        apiOtpMap.remove((Object) requestId);
                        String transId = apiOtp.getTransId();
                        int amount = apiOtp.getAmount();
                        String mobile = apiOtp.getMobile();
                        long time = DvtUtils.checkApiOtpFail(user.getApiOTPFail(), user.getApiOTPFailTime());
                        if (time <= 0L && (time = DvtUtils.checkApiOtpTimeDelay(user.getApiOTPTime())) <= 0L) {
                            user.setApiOTPTime(new Date());
                            int money = 0;
                            HashMap<String, String> params = new HashMap<String, String>();
                            params.put("access_key", GameCommon.getValueStr("SMS_PLUS_ACCESS_KEY"));
                            params.put("otp", String.valueOf(otp));
                            params.put("requestId", requestId);
                            params.put("transId", transId);
                            String signature = VinPlayUtils.hmacDigest((String) DvtUtils.getSMSPlusHMAC(params), (String) GameCommon.getValueStr("SMS_PLUS_SECRET_KEY"), (String) "HmacSHA256");
                            params.put("signature", signature);
                            String response = HttpURLClient.sendPOST(GameCommon.getValueStr("API_OTP_URL_CONFIRM"), params);
                            JSONObject obj = new JSONObject(response);
                            String errorMessage = obj.getString("errorMessage");
                            String rpRequestId = obj.getString("requestId");
                            String rpTransId = obj.getString("transId");
                            String errorCode = obj.getString("errorCode");
                            String des = "L\u00e1\u00bb\u2014i h\u00e1\u00bb\u2021 th\u00e1\u00bb\u2018ng";
                            if (errorMessage != null && rpRequestId != null && rpTransId != null && errorCode != null && requestId.equals(rpRequestId) && transId.equals(rpTransId)) {
                                code = (byte) DvtUtils.getCodeApiOTP(errorCode, true);
                                des = DvtUtils.getDesbyCodeApiOTP(code);
                                if (code == 0) {
                                    user.setApiOTPFail(0);
                                    long moneyUser = user.getVin();
                                    currentMoney = user.getVinTotal();
                                    long rechargeMoney = user.getRechargeMoney();
                                    money = (int) Math.round(GameCommon.getValueDouble("RATIO_RECHARGE_SMS") * (double) amount);
                                    user.setVin(moneyUser += (long) money);
                                    user.setVinTotal(currentMoney += (long) money);
                                    user.setRechargeMoney(rechargeMoney += (long) money);
                                    String description = "SDT: " + mobile + " .S\u00e1\u00bb\u2018 ti\u00e1\u00bb\ufffdn: " + amount;
                                    MoneyMessageInMinigame messageMoney = new MoneyMessageInMinigame(VinPlayUtils.genMessageId(), user.getId(), nickname, "RechargeBySMS", moneyUser, currentMoney, (long) money, "vin", 0L, 0, 0);
                                    LogMoneyUserMessage messageLog = new LogMoneyUserMessage(user.getId(), nickname, "RechargeBySMS", "N\u00e1\u00ba\u00a1p Vin qua SMS", currentMoney, (long) money, "vin", description, 0L, false, user.isBot());
                                    RMQApi.publishMessagePayment((BaseMessage) messageMoney, (int) 16);
                                    RMQApi.publishMessageLogMoney((LogMoneyUserMessage) messageLog);
                                } else if (code != 1) {
                                    user.setApiOTPFail(user.getApiOTPFail() + 1);
                                    user.setApiOTPFailTime(new Date());
                                }
                                userMap.put(nickname, user);
                            }
                            RechargeDaoImpl dao = new RechargeDaoImpl();
                            errorCode = errorCode == null ? "" : errorCode;
                            errorMessage = errorMessage == null ? "" : errorMessage;
                            dao.saveLogConfirmApiOTP(nickname, mobile, amount, otp, errorCode, errorMessage, requestId, transId, code, des, money);
                        }
                    }
                } else {
                    logger.debug((Object) "khoa nap qua api otp");
                }
            } catch (Exception e) {
                logger.debug((Object) e);
            } finally {
                userMap.unlock(nickname);
            }
        }
        RechargeApiOTPResponse res = new RechargeApiOTPResponse(code, requestId, "", "", currentMoney, 0, 0L);
        return res;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    @Override
    public synchronized String receiveConfirmChargingOTP(Map<String, String[]> request) {
        SMSPlusResponse res = new SMSPlusResponse(0, "", "text");
        int code = -1;
        String des = "L\u00e1\u00bb\u2014i h\u00e1\u00bb\u2021 th\u00e1\u00bb\u2018ng";
        String sms = GameCommon.SMSPLUS_ERROR_SYSTEM;
        try {
            if (GameCommon.getValueInt("API_OTP_OPEN") == 0) {
                StringBuilder log = new StringBuilder("");
                HashMap<String, String> fields = new HashMap<String, String>();
                for (Map.Entry<String, String[]> entry : request.entrySet()) {
                    String fieldName = entry.getKey();
                    String fieldValue = entry.getValue()[0];
                    log.append(fieldName).append(":").append(fieldValue).append(", ");
                    if (fieldValue == null || fieldValue.length() <= 0) continue;
                    fields.put(fieldName, fieldValue);
                }
                RechargeServiceImpl.logger.debug((Object) ("Api OTP confirm: " + log.toString()));
                String accessKey = (String) fields.get("access_key");
                int amount = Integer.parseInt((String) fields.get("amount"));
                String errorCode = (String) fields.get("error_code");
                String errorMessage = (String) fields.get("error_message");
                String msisdn = (String) fields.get("msisdn");
                String requestId = (String) fields.get("request_id");
                String requestTime = (String) fields.get("request_time");
                String transId = (String) fields.get("trans_id");
                String signature = (String) fields.get("signature");
                if (accessKey != null && errorCode != null && errorMessage != null && msisdn != null && requestId != null && requestTime != null && transId != null && signature != null) {
                    long money = 0L;
                    String mobile = DvtUtils.revertMobile84To(msisdn);
                    String nickname = "";
                    HazelcastInstance client = HazelcastClientFactory.getInstance();
                    IMap<String, ApiOtpModel> apiOtpMap = client.getMap("cacheApiOtp");

                    lbl103:
                    // 4 sources:
                    if (apiOtpMap.containsKey((Object) requestId)) {
                        try {
                            apiOtpMap.lock(requestId);
                            ApiOtpModel apiOtp = (ApiOtpModel) apiOtpMap.get((Object) requestId);
                            if (!mobile.equals(apiOtp.getMobile()) || !transId.equals(apiOtp.getTransId()) || !requestId.equals(apiOtp.getRequestId()) || amount != apiOtp.getAmount())
                                break lbl103;
                            nickname = apiOtp.getNickname();
                            if ((Arrays.asList(DvtUtils.SMS_AMOUNT).contains(amount)) && (amount >= GameCommon.getValueInt("SMS_PLUS_AMOUNT_MIN"))) {
                                if (accessKey.equals(GameCommon.getValueStr("SMS_PLUS_ACCESS_KEY"))) {
                                    fields.remove("signature");
                                    String key = GameCommon.getValueStr("SMS_PLUS_SECRET_KEY");
                                    String checksum = VinPlayUtils.hmacDigest((String) DvtUtils.getSMSPlusHMAC(fields), (String) key, (String) "HmacSHA256");
                                    if (signature.equals(checksum)) {
                                        IMap<String, UserModel> userMap = client.getMap("users");
                                        if (userMap.containsKey((Object) nickname)) {
                                            try {
                                                userMap.lock(nickname);
                                                UserCacheModel user = (UserCacheModel) userMap.get((Object) nickname);
                                                code = DvtUtils.getCodeApiOTP(errorCode, true);
                                                des = DvtUtils.getDesbyCodeApiOTP(code);
                                                if (code == 0) {
                                                    user.setApiOTPFail(0);
                                                    long moneyUser = user.getVin();
                                                    long currentMoney = user.getVinTotal();
                                                    long rechargeMoney = user.getRechargeMoney();
                                                    money = Math.round(GameCommon.getValueDouble("RATIO_RECHARGE_SMS") * (double) amount);
                                                    user.setVin(moneyUser += money);
                                                    user.setVinTotal(currentMoney += money);
                                                    user.setRechargeMoney(rechargeMoney += money);
                                                    String description = "SDT: " + mobile + " .S\u00e1\u00bb\u2018 ti\u00e1\u00bb\ufffdn: " + amount;
                                                    MoneyMessageInMinigame messageMoney = new MoneyMessageInMinigame(VinPlayUtils.genMessageId(), user.getId(), nickname, "RechargeBySMS", moneyUser, currentMoney, money, "vin", 0L, 0, 0);
                                                    LogMoneyUserMessage messageLog = new LogMoneyUserMessage(user.getId(), nickname, "RechargeBySMS", "N\u00e1\u00ba\u00a1p Vin qua SMS", currentMoney, money, "vin", description, 0L, false, user.isBot());
                                                    RMQApi.publishMessagePayment((BaseMessage) messageMoney, (int) 16);
                                                    RMQApi.publishMessageLogMoney((LogMoneyUserMessage) messageLog);
                                                    sms = String.format(GameCommon.SMSPLUS_SUCCESS, new Object[]{nickname, money});
                                                    res.setStatus(1);
                                                } else if (code != 1) {
                                                    user.setApiOTPFail(user.getApiOTPFail() + 1);
                                                    user.setApiOTPFailTime(new Date());
                                                }
                                                userMap.put(nickname, user);
                                            } catch (Exception e) {
                                                RechargeServiceImpl.logger.debug((Object) e);
                                            } finally {
                                                userMap.unlock(nickname);
                                            }
                                        }
                                    } else {
                                        des = "checksum kh\u00c3\u00b4ng h\u00e1\u00bb\u00a3p l\u00e1\u00bb\u2021";
                                        RechargeServiceImpl.logger.debug((Object) ("checksum khong hop le: " + signature + " - " + checksum));
                                    }
                                } else {
                                    des = "accessKey kh\u00c3\u00b4ng h\u00e1\u00bb\u00a3p l\u00e1\u00bb\u2021";
                                    RechargeServiceImpl.logger.debug((Object) ("accessKey khong hop le: " + accessKey));
                                }
                            } else {
                                des = "amount kh\u00c3\u00b4ng h\u00e1\u00bb\u00a3p l\u00e1\u00bb\u2021";
                                RechargeServiceImpl.logger.debug((Object) ("amount khong hop le: " + amount));
                            }
                            apiOtpMap.remove((Object) requestId);
                        } catch (Exception e2) {
                            RechargeServiceImpl.logger.debug((Object) e2);
                        } finally {
                            apiOtpMap.unlock(requestId);
                        }
                    } else {
                        des = "requestId kh\u00c3\u00b4ng t\u00e1\u00bb\u201cn t\u00e1\u00ba\u00a1i";
                        RechargeServiceImpl.logger.debug((Object) ("requestId khong ton tai: " + requestId));
                    }

                    RechargeDaoImpl dao = new RechargeDaoImpl();
                    dao.saveLogConfirmApiOTP(nickname, mobile, amount, "", errorCode, errorMessage, requestId, transId, code, des, (int) money);
                } else {
                    RechargeServiceImpl.logger.debug((Object) "tham so khong hop le");
                }
            } else {
                RechargeServiceImpl.logger.debug((Object) "khoa sms plus");
            }
        } catch (Exception e3) {
            RechargeServiceImpl.logger.debug((Object) e3);
        }
        res.setSms(sms);
        return res.toJson();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public RechargeResponse rechargeByVinCard(String nickname, String serial, String pin, String platform) throws Exception {
        HazelcastInstance client;
        logger.debug((Object) ("Request rechargeByCard:  nickname: " + nickname + ", serial: " + serial + ", pin: " + pin));
        int code = 1;
        RechargeResponse res = new RechargeResponse(code, 0L, 0, 0L);
        if (GameCommon.getValueInt("IS_RECHARGE_VIN_CARD") == 1) {
            return res;
        }
        if (pin != null) {
            pin = pin.trim();
        }
        if (serial != null) {
            serial = serial.trim();
        }
        if ((client = HazelcastClientFactory.getInstance()) == null) {
            MoneyLogger.log(nickname, "RechargeByVinCard", 0L, 0L, "vin", "Nap vin qua vin card", "1030", "can not connect hazelcast");
            return res;
        }
        IMap<String, UserModel> userMap = client.getMap("users");
        IMap configMap = client.getMap("cacheConfig");
        RechargeByCardMessage message = null;
        if (userMap.containsKey((Object) nickname)) {
            try {
                userMap.lock(nickname);
                configMap.lock((Object) "VIN_CARD_SYSTEM_DAILY");
                UserCacheModel user = (UserCacheModel) userMap.get((Object) nickname);
                res.setCurrentMoney(user.getVinTotal());
                boolean cardFail = false;
                long time = DvtUtils.checkRechargeFail(user.getRechargeVCFail(), user.getRechargeVCFailTime());
                if (time <= 0L) {
                    if (UserValidaton.validateSerialVinCard((String) serial)) {
                        if (UserValidaton.validatePinVinCard((String) pin)) {
                            long rechargeVCToday;
                            boolean rechargedToday = user.getRechargeVCTime() != null && VinPlayUtils.compareDate((Date) new Date(), (Date) user.getRechargeVCTime()) == 0;
                            long l = rechargeVCToday = rechargedToday ? user.getRechargeVCInDay() : 0L;
                            if (rechargeVCToday < GameCommon.getValueLong("VIN_CARD_USER_LIMIT")) {
                                long rechargeVCSystem = GameCommon.getValueLong("VIN_CARD_SYSTEM_DAILY");
                                if (rechargeVCSystem < GameCommon.getValueLong("VIN_CARD_SYSTEM_LIMIT")) {
                                    String id = String.valueOf(System.currentTimeMillis());
                                    String partner = GameCommon.getValueStr("VIN_CARD_PARTNER");
                                    ChargeVCObj obj = null;
                                    try {
                                        obj = VinplayClient.rechargeByVinCard(id, partner, serial, pin, nickname);
                                        if (obj.getSerial() != null) {
                                            serial = obj.getSerial();
                                        }
                                    } catch (Exception e) {
                                        logger.debug((Object) e);
                                        MoneyLogger.log(nickname, "RechargeByVinCard", 0L, 0L, "vin", "Nap vin qua vin card", "1034", "Loi ket noi dvt: " + e.getMessage());
                                    }
                                    long money = 0L;
                                    if (obj != null) {
                                        money = Math.round((double) obj.getAmount() * GameCommon.getValueDouble("RATIO_RECHARGE_VIN_CARD"));
                                        code = this.getErrorCode(obj.getStatus());
                                        if (code == 0 && (obj.getAmount() == 0 || obj.getAmount() > PhoneCardType._5M.getValue() || obj.getAmount() % PhoneCardType._10K.getValue() != 0)) {
                                            code = 30;
                                        }
                                        message = new RechargeByCardMessage(nickname, id, partner, serial, pin, obj.getAmount(), obj.getStatus(), obj.getMessage(), code, (int) money, (String) null, (String) null, (String) null, platform, (String) null);
                                    } else {
                                        message = new RechargeByCardMessage(nickname, id, partner, serial, pin, 0, -1, "L\u00e1\u00bb\u2014i k\u00e1\u00ba\u00bft n\u00e1\u00bb\u2018i D\u00e1\u00bb\u2039ch v\u00e1\u00bb\u00a5 th\u00e1\u00ba\u00bb", code, 0, (String) null, (String) null, (String) null, platform, (String) null);
                                    }
                                    long moneyUser = user.getVin();
                                    long currentMoney = user.getVinTotal();
                                    long rechargeMoney = user.getRechargeMoney();
                                    if (code == 0) {
                                        configMap.put("VIN_CARD_SYSTEM_DAILY", (Object) String.valueOf(rechargeVCSystem += (long) obj.getAmount()));
                                        user.setVin(moneyUser += money);
                                        user.setVinTotal(currentMoney += money);
                                        user.setRechargeMoney(rechargeMoney += money);
                                        user.setRechargeVCFail(0);
                                        user.setRechargeVCInDay(rechargeVCToday += (long) obj.getAmount());
                                        user.setRechargeVCTime(new Date());
                                        String description = "K\u00e1\u00ba\u00bft qu\u00e1\u00ba\u00a3: Th\u00c3\u00a0nh c\u00c3\u00b4ng, M\u00c3\u00a3 GD: " + id + ", M\u00e1\u00bb\u2021nh gi\u00c3\u00a1: " + obj.getAmount() + ", Serial: " + serial + ", Pin: " + pin;
                                        MoneyMessageInMinigame messageMoney = new MoneyMessageInMinigame(VinPlayUtils.genMessageId(), user.getId(), nickname, "RechargeByVinCard", moneyUser, currentMoney, money, "vin", 0L, 0, 0);
                                        LogMoneyUserMessage messageLog = new LogMoneyUserMessage(user.getId(), nickname, "RechargeByVinCard", "N\u00e1\u00ba\u00a1p qua VinPlay Card", currentMoney, money, "vin", description, 0L, false, user.isBot());
                                        RMQApi.publishMessagePayment((BaseMessage) messageMoney, (int) 16);
                                        RMQApi.publishMessageLogMoney((LogMoneyUserMessage) messageLog);
                                        RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 307);
                                        userMap.put(nickname, user);
                                        res.setCurrentMoney(currentMoney);
                                    } else if (code == 30) {
                                        String description = "K\u00e1\u00ba\u00bft qu\u00e1\u00ba\u00a3: \u00c4\ufffdang x\u00e1\u00bb\u00ad l\u00c3\u00bd, M\u00c3\u00a3 GD: " + id + ", M\u00e1\u00bb\u2021nh gi\u00c3\u00a1: " + obj.getAmount() + ", Serial: " + serial + ", Pin: " + pin;
                                        LogMoneyUserMessage messageLog2 = new LogMoneyUserMessage(user.getId(), nickname, "RechargeByVinCard", "N\u00e1\u00ba\u00a1p qua VinPlay Card", currentMoney, 0L, "vin", description, 0L, false, user.isBot());
                                        RMQApi.publishMessageLogMoney((LogMoneyUserMessage) messageLog2);
                                        RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 307);
                                    } else {
                                        if (code != 1) {
                                            cardFail = true;
                                        }
                                        RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 307);
                                    }
                                } else {
                                    code = 38;
                                }
                            } else {
                                code = 37;
                            }
                        } else {
                            code = 35;
                            cardFail = true;
                        }
                    } else {
                        code = 36;
                        cardFail = true;
                    }
                } else {
                    code = 8;
                    res.setTime(time);
                }
                if (cardFail) {
                    user.setRechargeVCFail(user.getRechargeVCFail() + 1);
                    user.setRechargeVCFailTime(new Date());
                    userMap.put(nickname, user);
                }
                res.setFail(user.getRechargeVCFail());
            } catch (Exception e2) {
                code = 1;
                logger.debug((Object) e2);
                MoneyLogger.log(nickname, "RechargeByVinCard", 0L, 0L, "vin", "Nap vin qua vin card", "1001", e2.getMessage());
            } finally {
                userMap.unlock(nickname);
                configMap.unlock((Object) "VIN_CARD_SYSTEM_DAILY");
            }
        }
        res.setCode(code);
        logger.debug((Object) ("Response rechargeByVinCard: " + code));
        return res;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public RechargeResponse rechargeByMegaCard(String nickname, String serial, String pin, String platform) throws Exception {
        HazelcastInstance client;
        logger.debug((Object) ("Request rechargeByMegaCard:  nickname: " + nickname + ", serial: " + serial + ", pin: " + pin + ", platform: " + platform));
        int code = 1;
        RechargeResponse res = new RechargeResponse(code, 0L, 0, 0L);
        if (GameCommon.getValueInt("IS_NAP_MEGA_CARD") == 1) {
            return res;
        }
        if (pin != null) {
            pin = pin.trim();
        }
        if (serial != null) {
            serial = serial.trim().toUpperCase();
        }
        if ((client = HazelcastClientFactory.getInstance()) == null) {
            MoneyLogger.log(nickname, "RechargeByMegaCard", 0L, 0L, "vin", "Nap vin qua MegaCard", "1030", "can not connect hazelcast");
            return res;
        }
        IMap<String, UserModel> userMap = client.getMap("users");
        RechargeByCardMessage message = null;
        if (userMap.containsKey((Object) nickname)) {
            try {
                userMap.lock(nickname);
                UserCacheModel user = (UserCacheModel) userMap.get((Object) nickname);
                res.setCurrentMoney(user.getVinTotal());
                boolean cardFail = false;
                long time = DvtUtils.checkRechargeFail(user.getRechargeMegaCardFail(), user.getRechargeMegaCardFailTime());
                if (time <= 0L) {
                    if (UserValidaton.validateSerialMegaCard((String) serial)) {
                        if (UserValidaton.validatePinMegaCard((String) pin)) {
                            ChargeReponse chargeObj = null;
                            String transID = VinPlayUtils.genTransactionId((int) user.getId());
                            try {
                                EpayMegaCardCharging.init();
                                chargeObj = EpayMegaCardCharging.chargingMegaCard(EpayMegaCardCharging.login(), transID, serial, pin);
                            } catch (Exception e) {
                                logger.debug((Object) e);
                                MoneyLogger.log(nickname, "RechargeByMegaCard", 0L, 0L, "vin", "Nap vin qua MegaCard", "1034", "Loi ket noi Epay MegaCard: " + e.getMessage());
                                ++EpayMegaCardAlert.megaDisconnect;
                            }
                            long money = 0L;
                            String userNameMega = "";
                            if (GameCommon.getValueInt("MEGA_IS_VAT") == 0) {
                                userNameMega = GameCommon.getValueStr("MEGA_USER");
                            } else if (GameCommon.getValueInt("MEGA_IS_VAT") == 1) {
                                userNameMega = GameCommon.getValueStr("MEGA_USER_VAT");
                            }
                            if (chargeObj != null) {
                                code = this.mapErrorCodeEpayMegaCard(chargeObj.getStatus());
                                if (code == 0) {
                                    money = Math.round((double) Integer.parseInt(chargeObj.getAmount()) * GameCommon.getValueDouble("RATIO_NAP_MEGA_CARD"));
                                }
                                message = new RechargeByCardMessage(nickname, transID, "MegaCard", serial, pin, Integer.parseInt(chargeObj.getAmount()), Integer.parseInt(chargeObj.getStatus()), chargeObj.getMessage(), code, (int) money, (String) null, (String) null, "epay", platform, userNameMega);
                                EpayMegaCardAlert.megaDisconnect = 0;
                            } else {
                                message = new RechargeByCardMessage(nickname, transID, "MegaCard", serial, pin, 0, -1, "L\u00e1\u00bb\u2014i k\u00e1\u00ba\u00bft n\u00e1\u00bb\u2018i Epay MegaCard", code, 0, (String) null, (String) null, "epay", platform, userNameMega);
                                ++EpayMegaCardAlert.megaDisconnect;
                            }
                            long moneyUser = user.getVin();
                            long currentMoney = user.getVinTotal();
                            long rechargeMoney = user.getRechargeMoney();
                            if (code == 0) {
                                user.setVin(moneyUser += money);
                                user.setVinTotal(currentMoney += money);
                                user.setRechargeMoney(rechargeMoney += money);
                                user.setRechargeMegaCardFail(0);
                                String description = "K\u00e1\u00ba\u00bft qu\u00e1\u00ba\u00a3: Th\u00c3\u00a0nh c\u00c3\u00b4ng, M\u00c3\u00a3 GD: " + transID + ", M\u00e1\u00bb\u2021nh gi\u00c3\u00a1: " + chargeObj.getAmount() + ", Serial: " + serial + ", Pin: " + pin;
                                MoneyMessageInMinigame messageMoney = new MoneyMessageInMinigame(VinPlayUtils.genMessageId(), user.getId(), nickname, "RechargeByMegaCard", moneyUser, currentMoney, money, "vin", 0L, 0, 0);
                                LogMoneyUserMessage messageLog = new LogMoneyUserMessage(user.getId(), nickname, "RechargeByMegaCard", "N\u00e1\u00ba\u00a1p qua Mega Card", currentMoney, money, "vin", description, 0L, false, user.isBot());
                                RMQApi.publishMessagePayment((BaseMessage) messageMoney, (int) 16);
                                RMQApi.publishMessageLogMoney((LogMoneyUserMessage) messageLog);
                                RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 301);
                                userMap.put(nickname, user);
                                res.setCurrentMoney(currentMoney);
                                EpayMegaCardAlert.megaDisconnect = 0;
                                EpayMegaCardAlert.megaPending = 0;
                            } else if (code == 30) {
                                String description = "K\u00e1\u00ba\u00bft qu\u00e1\u00ba\u00a3: \u00c4\ufffdang x\u00e1\u00bb\u00ad l\u00c3\u00bd, M\u00c3\u00a3 GD: " + transID + ", M\u00e1\u00bb\u2021nh gi\u00c3\u00a1: " + chargeObj.getAmount() + ", Serial: " + serial + ", Pin: " + pin;
                                LogMoneyUserMessage messageLog2 = new LogMoneyUserMessage(user.getId(), nickname, "RechargeByMegaCard", "N\u00e1\u00ba\u00a1p qua Mega Card", currentMoney, 0L, "vin", description, 0L, false, user.isBot());
                                RMQApi.publishMessageLogMoney((LogMoneyUserMessage) messageLog2);
                                RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 301);
                                EpayMegaCardAlert.megaDisconnect = 0;
                                ++EpayMegaCardAlert.megaPending;
                            } else {
                                if (code != 1) {
                                    cardFail = true;
                                }
                                RMQApi.publishMessage((String) "queue_dvt", (BaseMessage) message, (int) 301);
                            }
                        } else {
                            code = 35;
                            cardFail = true;
                        }
                    } else {
                        code = 36;
                        cardFail = true;
                    }
                } else {
                    code = 8;
                    res.setTime(time);
                }
                if (cardFail) {
                    user.setRechargeMegaCardFail(user.getRechargeMegaCardFail() + 1);
                    user.setRechargeMegaCardFailTime(new Date());
                    userMap.put(nickname, user);
                }
                res.setFail(user.getRechargeMegaCardFail());
            } catch (Exception e2) {
                code = 1;
                logger.debug((Object) e2);
                MoneyLogger.log(nickname, "RechargeByMegaCard", 0L, 0L, "vin", "Nap vin qua MegaCard", "1001", e2.getMessage());
            } finally {
                userMap.unlock(nickname);
            }
        }
        res.setCode(code);
        logger.debug((Object) ("Response rechargeByMegaCard: " + code));
        if (EpayMegaCardAlert.megaPending == GameCommon.getValueInt("COUNT_FAIL") && VinPlayUtils.isAlertTimeout((Date) EpayMegaCardAlert.alertMegaPendingTime, (int) 1)) {
            this.alert(GameCommon.getValueStr("MEGA_CARD_GROUP_NUMBER"), "[CANH BAO] He thong MegaCard tra ve pending qua 5 lan lien tiep!", false);
            EpayMegaCardAlert.alertMegaPendingTime = new Date();
        }
        if (EpayMegaCardAlert.megaDisconnect == GameCommon.getValueInt("COUNT_FAIL") && VinPlayUtils.isAlertTimeout((Date) EpayMegaCardAlert.alertMegaDisconnectTime, (int) 1)) {
            this.alert(GameCommon.getValueStr("MEGA_CARD_GROUP_NUMBER"), "SOS! Canh bao he thong MegaCard dang bi mat ket noi!", true);
            EpayMegaCardAlert.alertMegaDisconnectTime = new Date();
        }
        return res;
    }

    @Override
    public RechargeResponse rechargeByVcoin(String nickname, String serial, String pin, String platform) throws Exception {
        logger.debug((Object) ("Start rechargeByVcoin ham cha:  nickname: " + nickname + ", serial: " + serial + ", pin: " + pin));
        RechargeResponse response = null;
        return response;
    }

    private void alert(String number, String content, boolean isCall) {
        AlertServiceImpl alertService = new AlertServiceImpl();
        if (number.contains(",")) {
            String[] arr = number.split(",");
            ArrayList<String> mList = new ArrayList<String>();
            for (String m : arr) {
                m = m.trim();
                mList.add(m);
            }
            alertService.alert2List(mList, content, isCall);
        } else {
            alertService.alert2One(number, content, isCall);
        }
    }

    private int mapPayVietToVinplayCode(String lucky79Code) {
        String trim;
        switch (trim = lucky79Code.trim()) {
            case "99": {
                return 30;
            }

        }
        return 35;
    }
}

