package com.vinplay.dal.service.impl;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;

public class CardBanMoneyService {
    public void banWidrawUser(String nickname, long time) {
        HazelcastInstance client = HazelcastClientFactory.getInstance();
        IMap userMap = client.getMap("cacheBanCardDeposit");
        if (time > 0L) {
            userMap.put(nickname, (Object)(System.currentTimeMillis() + time));
        } else {
            userMap.put(nickname, (Object)time);
        }
    }


    public void banDepositUser(String nickname, long time) {
        HazelcastInstance client = HazelcastClientFactory.getInstance();
        IMap userMap = client.getMap("cacheBanCardDeposit");
        if (time > 0L) {
            userMap.put(nickname, (Object)(System.currentTimeMillis() + time));
        } else {
            userMap.put(nickname, (Object)time);
        }
    }

    public long getBanWithdrawTime(String nickname) {
        HazelcastInstance client = HazelcastClientFactory.getInstance();
        IMap userMap = client.getMap("cacheBanCardDeposit");
        if (userMap.containsKey((Object)nickname)) {
            long timeUnBan = (Long)userMap.get((Object)nickname);
            if (timeUnBan < System.currentTimeMillis()) {
                timeUnBan = 0L;
                userMap.remove((Object)nickname);
            }
            return timeUnBan;
        }
        return 0L;
    }
    public long getBanDepositTime(String nickname) {
        HazelcastInstance client = HazelcastClientFactory.getInstance();
        IMap userMap = client.getMap("cacheBanCardDeposit");
        if (userMap.containsKey((Object)nickname)) {
            long timeUnBan = (Long)userMap.get((Object)nickname);
            if (timeUnBan < System.currentTimeMillis()) {
                timeUnBan = 0L;
                userMap.remove((Object)nickname);
            }
            return timeUnBan;
        }
        return 0L;
    }
}
