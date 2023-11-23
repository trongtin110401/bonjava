/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.dichvuthe.service.impl;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.vinplay.dichvuthe.dao.ExchangeDao;
import com.vinplay.dichvuthe.dao.impl.ExchangeDaoImpl;
import com.vinplay.dichvuthe.entities.CardResponse;
import com.vinplay.dichvuthe.response.RechargeResponse;
import com.vinplay.dichvuthe.service.ExchangeService;
import com.vinplay.usercore.logger.MoneyLogger;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.messages.dvt.RechargeByCardMessage;
import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.models.cache.UserCacheModel;

public class ExchangeServiceImpl
implements ExchangeService {
    private ExchangeDao exDao = new ExchangeDaoImpl();

    @Override
    public long getExchangeMoney(String merchantId, String nickname, String startTime, String endTime) {
        return this.exDao.getExchangeMoney(merchantId, nickname, startTime, endTime);
    }

    @Override
    public CardResponse BuyCardFromKhoThe(String nickName, String telco, int amount, int quantity) {
        CardResponse response = new CardResponse(-1, "failed");
        HazelcastInstance client;
        IMap<String, UserModel> userMap;
        String description;
        if ((client = HazelcastClientFactory.getInstance()) == null) {
            return response;
        }
        userMap = client.getMap("users");
        RechargeByCardMessage message = null;
        if (userMap.containsKey((Object)nickName)) {
            try {
                UserCacheModel user = (UserCacheModel) userMap.get((Object) nickName);
                response.setCurrentMoney(user.getVinTotal());
                // - tien user
            }
            catch ( Exception ex)
            {

            }
        }
        return response;
    }
}

