/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.core.IMap
 *  com.vinplay.vbee.common.hazelcast.HazelcastClientFactory
 *  com.vinplay.vbee.common.utils.DateTimeUtils
 *  org.apache.log4j.Logger
 */
package com.vinplay.dal.service.impl;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.vinplay.dal.service.LogPortalService;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.utils.DateTimeUtils;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

public class LogPortalServiceImpl
implements LogPortalService {
    private static final long CACHE_LOG_PORTAL_TTL = 60L;
    private static Logger logger = Logger.getLogger((String)"count_request_portal_logger");
    private static String FORMAT = ",%20s,\t%s,\t%6d";

    @Override
    public void log(String command) {
        IMap map;
        HazelcastInstance instance = HazelcastClientFactory.getInstance();
        if (instance != null && (map = instance.getMap("cacheLogPortal")) != null) {
            if (!map.containsKey((Object)command)) {
                map.put((Object)command, (Object)new Long(1L), 60L, TimeUnit.MINUTES);
            } else if (map != null) {
                long count = (Long)map.get((Object)command);
                map.put((Object)command, (Object)(++count), 60L, TimeUnit.MINUTES);
            }
        }
    }

    @Override
    public void saveLog() {
        String time = DateTimeUtils.getCurrentTime();
        HazelcastInstance instance = HazelcastClientFactory.getInstance();
        IMap map = instance.getMap("cacheLogPortal");
        for (Object c : map.keySet()) {
            long count = (Long)map.get((Object)c);
            logger.debug((Object)String.format(FORMAT, time, c, count));
            map.remove((Object)c);
        }
    }
}

