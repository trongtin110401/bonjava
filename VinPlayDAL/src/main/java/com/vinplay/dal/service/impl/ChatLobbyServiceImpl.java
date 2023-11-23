/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.core.IMap
 *  com.vinplay.vbee.common.hazelcast.HazelcastClientFactory
 */
package com.vinplay.dal.service.impl;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.vinplay.dal.service.ChatLobbyService;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;

public class ChatLobbyServiceImpl
implements ChatLobbyService {
    @Override
    public void banChatUser(String nickname, long time) {
        HazelcastInstance client = HazelcastClientFactory.getInstance();
        IMap userMap = client.getMap("cacheBanChat");
        if (time > 0L) {
             userMap.put(nickname, (Object)(System.currentTimeMillis() + time));
        } else {
             userMap.put(nickname, (Object)time);
        }
    }

    @Override
    public long getBanTime(String nickname) {
        HazelcastInstance client = HazelcastClientFactory.getInstance();
        IMap userMap = client.getMap("cacheBanChat");
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

