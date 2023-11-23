/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.core.IMap
 *  com.vinplay.vbee.common.hazelcast.HazelcastClientFactory
 *  com.vinplay.vbee.common.messages.BaseMessage
 *  com.vinplay.vbee.common.messages.LogMoneyUserMessage
 *  com.vinplay.vbee.common.messages.MoneyMessageInMinigame
 *  com.vinplay.vbee.common.models.UserModel
 *  com.vinplay.vbee.common.models.cache.UserCacheModel
 *  com.vinplay.vbee.common.models.vippoint.UserVPEventModel
 *  com.vinplay.vbee.common.response.MoneyResponse
 *  com.vinplay.vbee.common.rmq.RMQApi
 *  com.vinplay.vbee.common.utils.VinPlayUtils
 *  org.apache.log4j.Logger
 */
package com.vinplay.dal.service.impl;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.vinplay.dal.dao.impl.UserDaoImpl;
import com.vinplay.dal.service.BotService;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.messages.BaseMessage;
import com.vinplay.vbee.common.messages.LogMoneyUserMessage;
import com.vinplay.vbee.common.messages.MoneyMessageInMinigame;
import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.models.cache.UserCacheModel;
import com.vinplay.vbee.common.models.vippoint.UserVPEventModel;
import com.vinplay.vbee.common.response.MoneyResponse;
import com.vinplay.vbee.common.rmq.RMQApi;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Date;
import org.apache.log4j.Logger;

public class BotServiceImpl
implements BotService {
    private static final Logger logger = Logger.getLogger((String)"user_core");

    @Override
    public UserModel login(String nickname) throws SQLException, UnsupportedEncodingException, NoSuchAlgorithmException {
        IMap userMap = HazelcastClientFactory.getInstance().getMap("users");
        UserModel userModel = null;
        if (!userMap.containsKey((Object)nickname)) {
            UserDaoImpl dao = new UserDaoImpl();
            userModel = dao.getUserByNickName(nickname);
            if (userModel != null) {
                if(!userModel.isBot()){
                    logger.debug("user "+nickname +" not is bot");
                }
                UserVPEventModel vpModel = dao.getUserVPByNickName(userModel.getNickname());
                UserCacheModel userCache = new UserCacheModel(userModel.getId(), userModel.getUsername(), userModel.getNickname(), userModel.getPassword(), userModel.getEmail(), userModel.getFacebookId(), userModel.getGoogleId(), userModel.getMobile(), userModel.getBirthday(), userModel.isGender(), userModel.getAddress(), userModel.getVin(), userModel.getXu(), userModel.getVinTotal(), userModel.getXuTotal(), userModel.getSafe(), userModel.getRechargeMoney(), userModel.getVippoint(), userModel.getDaily(), userModel.getStatus(), userModel.getAvatar(), userModel.getIdentification(), userModel.getVippointSave(), userModel.getCreateTime(), userModel.getMoneyVP(), userModel.getSecurityTime(), userModel.getLoginOtp(), userModel.isBot(), 0, new Date(), vpModel.getVpEvent(), vpModel.getVpReal(), vpModel.getVpAdd(), vpModel.getVpSub(), vpModel.getNumAdd(), vpModel.getNumSub(), vpModel.getPlace(), vpModel.getPlaceMax());
                String accessToken = VinPlayUtils.genAccessToken((int)userModel.getId());
                userCache.setAccessToken(accessToken);
                userCache.setLastMessageId(0L);
                userCache.setLastActive(new Date());
                userCache.setOnline(0);
                 userMap.put(userCache.getNickname(), (Object)userCache);
            }
        } else {
            userModel = (UserModel)userMap.get((Object)nickname);
        }
        return userModel;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public MoneyResponse addMoney(String nickname, long money, String moneyType, String description) {
        MoneyResponse res = new MoneyResponse(false, "1001");
        if (nickname == null || money == 0L) {
            return res;
        }
        HazelcastInstance client = HazelcastClientFactory.getInstance();
        IMap<String, UserModel> userMap = client.getMap("users");
        if (userMap.containsKey((Object)nickname)) {
            try {
                 userMap.lock(nickname);
                UserCacheModel user = (UserCacheModel)userMap.get((Object)nickname);
                if (user.isBot()) {
                    long moneyUser = user.getMoney(moneyType);
                    long currentMoney = user.getCurrentMoney(moneyType);
                    if (currentMoney + money > 0L) {
                        user.setMoney(moneyType, moneyUser += money);
                        user.setCurrentMoney(moneyType, currentMoney += money);
                        MoneyMessageInMinigame message = new MoneyMessageInMinigame(VinPlayUtils.genMessageId(), user.getId(), nickname, "Bot", moneyUser, currentMoney, money, moneyType, 0L, 0, 0);
                        String des = "Chuy\u1ec3n kho\u1ea3n";
                        LogMoneyUserMessage messageLog = new LogMoneyUserMessage(user.getId(), nickname, "Bot", "Chuy\u1ec3n kho\u1ea3n", currentMoney, money, moneyType, description, 0L, false, user.isBot());
                        RMQApi.publishMessagePayment((BaseMessage)message, (int)16);
                        RMQApi.publishMessageLogMoney((LogMoneyUserMessage)messageLog);
                        userMap.put(nickname, user);
                        res.setSuccess(true);
                        res.setErrorCode("0");
                        res.setCurrentMoney(currentMoney);
                        res.setMoneyUse(moneyUser);
                    } else {
                        res.setErrorCode("1002");
                    }
                }
            }
            catch (Exception e) {
                logger.debug((Object)e);
            }
            finally {
                 userMap.unlock(nickname);
            }
        }
        return res;
    }
}

