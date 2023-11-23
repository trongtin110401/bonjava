/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.core.IMap
 *  com.hazelcast.transaction.TransactionContext
 *  com.hazelcast.transaction.TransactionOptions
 *  com.hazelcast.transaction.TransactionOptions$TransactionType
 *  com.vinplay.vbee.common.hazelcast.HazelcastClientFactory
 *  com.vinplay.vbee.common.messages.BaseMessage
 *  com.vinplay.vbee.common.messages.LogMoneyUserMessage
 *  com.vinplay.vbee.common.messages.NoHuMessage
 *  com.vinplay.vbee.common.messages.PotMessage
 *  com.vinplay.vbee.common.messages.gamebai.LogNoHuGameBaiMessage
 *  com.vinplay.vbee.common.models.FreezeModel
 *  com.vinplay.vbee.common.models.PotModel
 *  com.vinplay.vbee.common.models.cache.UserCacheModel
 *  com.vinplay.vbee.common.response.PotResponse
 *  com.vinplay.vbee.common.rmq.RMQApi
 *  com.vinplay.vbee.common.utils.VinPlayUtils
 *  org.apache.log4j.Logger
 */
package com.vinplay.dal.service.impl;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.transaction.TransactionContext;
import com.hazelcast.transaction.TransactionOptions;
import com.vinplay.dal.service.PotService;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.messages.BaseMessage;
import com.vinplay.vbee.common.messages.LogMoneyUserMessage;
import com.vinplay.vbee.common.messages.NoHuMessage;
import com.vinplay.vbee.common.messages.PotMessage;
import com.vinplay.vbee.common.messages.gamebai.LogNoHuGameBaiMessage;
import com.vinplay.vbee.common.models.FreezeModel;
import com.vinplay.vbee.common.models.PotModel;
import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.models.cache.UserCacheModel;
import com.vinplay.vbee.common.response.PotResponse;
import com.vinplay.vbee.common.rmq.RMQApi;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import java.util.Calendar;
import org.apache.log4j.Logger;

public class PotServiceImpl
implements PotService {
    private static final Logger logger = Logger.getLogger((String)"user_core");

    @Override
    public PotModel getPot(String gameName) {
        IMap potMap = HazelcastClientFactory.getInstance().getMap("huGameBai");
        if (potMap.containsKey((Object)gameName)) {
            return (PotModel)potMap.get((Object)gameName);
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PotResponse addMoneyPot(String potName, long money, boolean isInitial) {
        logger.debug((Object)("Request addMoneyPot: " + potName));
        PotResponse res = new PotResponse(false, "1001");
        HazelcastInstance client = HazelcastClientFactory.getInstance();
        if (client == null) {
            logger.debug((Object)"hazelcast not connected");
            return res;
        }
        IMap potMap = client.getMap("huGameBai");
        if (potMap.containsKey((Object)potName)) {
            try {
                potMap.lock((Object)potName);
                potMap.lock((Object)"Vinplay");
                PotModel pot = (PotModel)potMap.get((Object)potName);
                long valuePot = pot.getValue();
                long maxPot = -1L;
                IMap map = client.getMap("cacheConfig");
                if (map != null && map.containsKey((Object)"HU_GAME_BAI_MAX")) {
                    maxPot = Long.parseLong((String)map.get((Object)"HU_GAME_BAI_MAX"));
                }
                if (maxPot == -1L || valuePot + money <= maxPot) {
                    Calendar today = Calendar.getInstance();
                    int dateInYear = today.get(6);
                    if (!isInitial || dateInYear != pot.getLastDay()) {
                        PotModel potSystem = (PotModel)potMap.get((Object)"Vinplay");
                        TransactionContext context = client.newTransactionContext(new TransactionOptions().setTransactionType(TransactionOptions.TransactionType.ONE_PHASE));
                        context.beginTransaction();
                        try {
                            long valuePotSystem = potSystem.getValue();
                            res.setValue(valuePot);
                            res.setValue(valuePotSystem);
                            valuePotSystem -= money;
                            pot.setValue(valuePot += money);
                            if (isInitial) {
                                pot.setLastDay(dateInYear);
                            }
                            potSystem.setValue(valuePotSystem);
                            PotMessage message = new PotMessage(potName, money, valuePot, valuePotSystem);
                            RMQApi.publishMessage((String)"queue_hu_gamebai", (BaseMessage)message, (int)401);
                            potMap.put((Object)potName, (Object)pot);
                            potMap.put("Vinplay", (Object)potSystem);
                            context.commitTransaction();
                            res.setSuccess(true);
                            res.setErrorCode("0");
                            res.setValue(valuePot);
                        }
                        catch (Exception e) {
                            logger.debug((Object)("addMoneyPot error : " + e));
                            context.rollbackTransaction();
                        }
                    }
                } else {
                    res.setErrorCode("1050");
                }
            }
            catch (Exception e2) {
                logger.debug((Object)("addMoneyPot error : " + e2));
            }
            finally {
                potMap.unlock((Object)potName);
                potMap.unlock((Object)"Vinplay");
            }
        }
        logger.debug((Object)("Response addMoneyPot: " + res.toJson()));
        return res;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    //todo : method nohu
    @Override
    public PotResponse noHu(String sessionId, String nickname, String gameName, String roomId, long maxFreeze, String matchId, String potName, double ratioPot, int betValue, String desc) {
        logger.debug((Object)("Request noHu: sessionId: " + sessionId + ", nickname: " + nickname + ", gameName: " + gameName + ", roomId: " + roomId + ", maxFreeze: " + maxFreeze + ", matchId: " + matchId + ", potName: " + potName + ", ratioPot: " + ratioPot + ", betValue: " + betValue + ", desc: " + desc));
        PotResponse res = new PotResponse(false, "1001");
        HazelcastInstance client = HazelcastClientFactory.getInstance();
        if (client == null) {
            logger.debug((Object)"hazelcast not connected");
            return res;
        }
        IMap potMap = client.getMap("huGameBai");
        IMap<String, UserModel> userMap = client.getMap("users");
        IMap freezeMap = client.getMap("freeze");
        if (potMap.containsKey((Object)potName) && userMap.containsKey((Object)nickname) && freezeMap.containsKey((Object)sessionId)) {
            try {
                potMap.lock((Object)potName);
                 userMap.lock(nickname);
                freezeMap.lock((Object)sessionId);
                PotModel pot = (PotModel)potMap.get((Object)potName);
                UserCacheModel user = (UserCacheModel)userMap.get((Object)nickname);
                FreezeModel freeze = (FreezeModel)freezeMap.get((Object)sessionId);
                TransactionContext context = client.newTransactionContext(new TransactionOptions().setTransactionType(TransactionOptions.TransactionType.ONE_PHASE));
                context.beginTransaction();
                try {
                    long valuePotBefore;
                    long moneyUser = user.getVin();
                    long currentMoney = user.getVinTotal();
                    long freezeMoney = freeze.getMoney();
                    long valuePot = valuePotBefore = pot.getValue();
                    long money = Math.round((double)valuePot * ratioPot);
                    res.setCurrentMoneyUser(currentMoney);
                    res.setFreezeMoneyUser(freezeMoney);
                    res.setValue(valuePot);
                    res.setMoneyExchange(money);
                    long addMoneyUser = 0L;
                    long addMoneyFreeze = 0L;
                    if (freezeMoney >= maxFreeze) {
                        addMoneyUser = money;
                    } else if (freezeMoney + money > maxFreeze) {
                        addMoneyUser = freezeMoney + money - maxFreeze;
                        addMoneyFreeze = maxFreeze - freezeMoney;
                    } else {
                        addMoneyFreeze = money;
                    }
                    user.setVin(moneyUser += addMoneyUser);
                    user.setVinTotal(currentMoney += money);
                    freeze.setMoney(freezeMoney += addMoneyFreeze);
                    pot.setValue(valuePot -= money);
                    long fMoney = addMoneyFreeze == 0L ? -1L : freezeMoney;
                    NoHuMessage message = new NoHuMessage(VinPlayUtils.genMessageId(), user.getId(), nickname, gameName, moneyUser, currentMoney, money, "vin", fMoney, sessionId, 0L, 0, 0, potName, valuePot);
                    LogMoneyUserMessage messageLog = new LogMoneyUserMessage(user.getId(), nickname, gameName, VinPlayUtils.getServiceName((String)gameName), currentMoney, money, "vin", "N\u1ed5 h\u0169. Ph\u00f2ng: " + roomId + ", B\u00e0n: " + matchId, 0L, false, user.isBot());
                    LogNoHuGameBaiMessage nohuMessage = new LogNoHuGameBaiMessage(nickname, betValue, valuePotBefore, money, gameName, desc, "");
                    RMQApi.publishMessagePayment((BaseMessage)message, (int)19);
                    RMQApi.publishMessageLogMoney((LogMoneyUserMessage)messageLog);
                    RMQApi.publishMessage((String)"queue_hu_gamebai", (BaseMessage)nohuMessage, (int)402);
                    freezeMap.put((Object)sessionId, (Object)freeze);
                    userMap.put(nickname, user);
                    potMap.put((Object)potName, (Object)pot);
                    context.commitTransaction();
                    res.setSuccess(true);
                    res.setErrorCode("0");
                    res.setCurrentMoneyUser(currentMoney);
                    res.setFreezeMoneyUser(freezeMoney);
                    res.setValue(valuePot);
                    res.setMoneyExchange(money);
                }
                catch (Exception e) {
                    logger.debug((Object)("noHu error : " + e));
                    context.rollbackTransaction();
                }
            }
            catch (Exception e2) {
                logger.debug((Object)("noHu error : " + e2));
            }
            finally {
                potMap.unlock((Object)potName);
                 userMap.unlock(nickname);
                freezeMap.unlock((Object)sessionId);
            }
        }
        logger.debug((Object)("Response noHu: " + res.toJson()));
        return res;
    }
}

