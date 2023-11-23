package com.vinplay.dal.service.impl;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;

public class AceMoneyService {

    public void setOpenAce(boolean isOpen) {
        HazelcastInstance client = HazelcastClientFactory.getInstance();
        IMap userMap = client.getMap("cacheBanWithdrawAce");
        if (userMap.containsKey((Object) "ISOPEN_5ACE")) {
            userMap.replace("ISOPEN_5ACE", (Object) isOpen);
        } else {
            userMap.put("ISOPEN_5ACE", (Object) isOpen);
        }
    }

    public boolean getOpenAce() {
        HazelcastInstance client = HazelcastClientFactory.getInstance();
        IMap userMap = client.getMap("cacheBanWithdrawAce");
        if (userMap.containsKey((Object) "ISOPEN_5ACE")) {
            boolean isOpen = (boolean) userMap.get("ISOPEN_5ACE");
            return isOpen;
        } else {
            userMap.put("ISOPEN_5ACE", (Object) true);
            return true;
        }
    }

    public void setLodeNew(boolean isOpen) {
        HazelcastInstance client = HazelcastClientFactory.getInstance();
        IMap userMap = client.getMap("cacheBanWithdrawAce");
        if (userMap.containsKey((Object) "ISOPEN_LODE_NEW")) {
            userMap.replace("ISOPEN_LODE_NEW", (Object) isOpen);
        } else {
            userMap.put("ISOPEN_LODE_NEW", (Object) isOpen);
        }
    }


    public boolean getLodeNew() {
        HazelcastInstance client = HazelcastClientFactory.getInstance();
        IMap userMap = client.getMap("cacheBanWithdrawAce");
        if (userMap.containsKey((Object) "ISOPEN_LODE_NEW")) {
            boolean isOpen = (boolean) userMap.get("ISOPEN_LODE_NEW");
            return isOpen;
        } else {
            userMap.put("ISOPEN_LODE_NEW", (Object) true);
            return true;
        }
    }

    public void setBongDa(boolean isOpen) {
        HazelcastInstance client = HazelcastClientFactory.getInstance();
        IMap userMap = client.getMap("cacheBanWithdrawAce");
        if (userMap.containsKey((Object) "ISOPEN_BONG_DA")) {
            userMap.replace("ISOPEN_BONG_DA", (Object) isOpen);
        } else {
            userMap.put("ISOPEN_BONG_DA", (Object) isOpen);
        }
    }


    public boolean getBongDa() {
        HazelcastInstance client = HazelcastClientFactory.getInstance();
        IMap userMap = client.getMap("cacheBanWithdrawAce");
        if (userMap.containsKey((Object) "ISOPEN_BONG_DA")) {
            boolean isOpen = (boolean) userMap.get("ISOPEN_BONG_DA");
            return isOpen;
        } else {
            userMap.put("ISOPEN_BONG_DA", (Object) true);
            return true;
        }
    }

    public void banWidrawUser(String nickname, long time) {
        HazelcastInstance client = HazelcastClientFactory.getInstance();
        IMap userMap = client.getMap("cacheBanWithdrawAce");
        if (time > 0L) {
            userMap.put(nickname, (Object)(System.currentTimeMillis() + time));
        } else {
            userMap.put(nickname, (Object)time);
        }
    }


    public void banDepositUser(String nickname, long time) {
        HazelcastInstance client = HazelcastClientFactory.getInstance();
        IMap userMap = client.getMap("cacheBanDepositAce");
        if (time > 0L) {
            userMap.put(nickname, (Object)(System.currentTimeMillis() + time));
        } else {
            userMap.put(nickname, (Object)time);
        }
    }

    public synchronized long getBanWithdrawTime(String nickname) {
        HazelcastInstance client = HazelcastClientFactory.getInstance();
        IMap userMap = client.getMap("cacheBanWithdrawAce");
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
        IMap userMap = client.getMap("cacheBanDepositAce");
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
