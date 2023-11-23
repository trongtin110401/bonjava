/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.hazelcast.core.IMap
 *  com.vinplay.usercore.utils.UserMakertingUtil
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.hazelcast.HazelcastUtils
 *  com.vinplay.vbee.common.messages.BaseMessage
 *  com.vinplay.vbee.common.messages.MoneyMessageInMinigame
 *  com.vinplay.vbee.common.models.cache.UserActiveModel
 *  com.vinplay.vbee.common.utils.VinPlayUtils
 *  org.apache.log4j.Logger
 */
package com.vinplay.vbee.rmq.payment.processor;

import com.hazelcast.core.IMap;
import com.vinplay.usercore.utils.UserMakertingUtil;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.hazelcast.HazelcastUtils;
import com.vinplay.vbee.common.messages.BaseMessage;
import com.vinplay.vbee.common.messages.MoneyMessageInMinigame;
import com.vinplay.vbee.common.models.cache.UserActiveModel;
import com.vinplay.vbee.common.statics.Consts;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import com.vinplay.vbee.dao.impl.UserDaoImpl;
import java.util.Date;
import org.apache.log4j.Logger;

public class UpdateMoneyProcessor
implements BaseProcessor<byte[], Boolean> {
    private static final Logger logger = Logger.getLogger((String)"vbee");
    private static final long INTERVAL_TIME_UPDATE_MONEY = 10L;

    public Boolean execute(Param<byte[]> param) {
        byte[] body = (byte[])param.get();
        MoneyMessageInMinigame message = (MoneyMessageInMinigame)BaseMessage.fromBytes((byte[])body);
        try {
            boolean updateTimeOut;
            block20 : {
                String nickname = message.getNickname();
                IMap userMap = HazelcastUtils.getActiveMap((String)nickname);
                updateTimeOut = true;
                if (userMap.containsKey((Object)nickname)) {
                    try {
                        userMap.lock((Object)nickname);
                        UserActiveModel model = (UserActiveModel)userMap.get((Object)nickname);
                        if (Long.parseLong(message.getId()) < model.getLastMessageId() || model.isBot()) {
                            return true;
                        }
                        long currentTime = System.currentTimeMillis();
                        long validTimeMs = currentTime - 600000L;
                        if (model.getLastActive() > validTimeMs) {
                            updateTimeOut = false;
                        } else {
                            if (message.getMoneyType().equals("vin")) {
                                boolean bl = updateTimeOut = !model.isBot() || VinPlayUtils.updateMoneyTimeout((long)model.getLastActiveVin(), (int)30);
                                if (updateTimeOut) {
                                    model.setLastActiveVin(new Date().getTime());
                                }
                            } else {
                                updateTimeOut = model.isBot() ? VinPlayUtils.updateMoneyTimeout((long)model.getLastActiveXu(), (int)30) : VinPlayUtils.updateMoneyTimeout((long)model.getLastActiveXu(), (int)10);
                                if (updateTimeOut) {
                                    model.setLastActiveXu(new Date().getTime());
                                }
                            }
                            model.setLastActive(new Date().getTime());
                            model.setLastMessageId(Long.parseLong(message.getId()));
                        }
                        model.setUpdateMySQL(updateTimeOut);
                        userMap.put((Object)nickname, (Object)model);
                    }
                    catch (Exception e) {
                        logger.error((Object)e);
                        break block20;
                    }
                    try {
                        userMap.unlock((Object)nickname);
                    }
                    catch (Exception e) {
                        // empty catch block
                        logger.error( e.getStackTrace());
                    }
                }
            }
            int type = 0;
            if (message.getActionName().equals("Bot")) {
                type = 3;
            } else if (message.getActionName().equals("RechargeByCard") || message.getActionName().equals("RechargeByVinCard") || message.getActionName().equals("RechargeByMegaCard") || message.getActionName().equals("RechargeByBank") || message.getActionName().equals("RechargeByIAP") || message.getActionName().equals("RechargeBySMS") || message.getActionName().equals("TransferMoney")
                    || message.getActionName().equals(Consts.RECHARGE_BY_MOMO) || message.getActionName().equals(Consts.RECHARGE_BY_BANK)
                    && message.getMoneyVP() == -1) {
                type = 1;
                UserMakertingUtil.userNapVin((String)message.getNickname(), (long)message.getMoneyExchange());
            } else if (message.getMoneyVP() > 0 || message.getVp() != 0) {
                type = 2;
            }
            if (!updateTimeOut) {
                logger.info((Object)("update no timeout nickname: " + message.getNickname() + " money: " + message.getMoneyExchange() + " current: " + message.getAfterMoney() + " moneyType: " + message.getMoneyType()));
                return true;
            }
            UserDaoImpl userDao = new UserDaoImpl();
            userDao.updateMoney(message, type);
            return true;
        }
        catch (Exception e2) {
            e2.printStackTrace();
            logger.error("UpdateMoneyProcessor error with" + e2.getStackTrace());
            return false;
        }
    }
}

