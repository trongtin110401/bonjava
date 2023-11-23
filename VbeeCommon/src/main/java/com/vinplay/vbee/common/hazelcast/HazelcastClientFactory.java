/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.hazelcast.client.HazelcastClient
 *  com.hazelcast.client.config.ClientConfig
 *  com.hazelcast.client.config.ClientNetworkConfig
 *  com.hazelcast.config.GroupConfig
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.core.LifecycleService
 */
package com.vinplay.vbee.common.hazelcast;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.LifecycleService;
import java.util.ArrayList;
import java.util.List;

public class HazelcastClientFactory {
    public static String ADDRESS = "127.0.0.1";
    public static String GROUP_NAME = "vinplay";
    public static String GROUP_PASS = "vinplay@123";
    private static HazelcastInstance instance;
    private static ClientConfig cfg;

    public static void initDefault() {
        ArrayList<String> address = new ArrayList<String>();
        address.add(ADDRESS);
        HazelcastClientFactory.init(address, GROUP_NAME, GROUP_PASS);
    }

    public static void init(List<String> address, String groupName, String groupPassword) {
        GroupConfig groupConfig = new GroupConfig();
        ClientNetworkConfig clientNetworkConfig = new ClientNetworkConfig();
        groupConfig.setName(groupName);
        groupConfig.setPassword(groupPassword);
        cfg.setGroupConfig(groupConfig);
        for (String addr : address) {
            clientNetworkConfig.addAddress(new String[]{addr});
        }
        cfg.setNetworkConfig(clientNetworkConfig);
        instance = HazelcastClient.newHazelcastClient((ClientConfig)cfg);
    }

    public static void reconnect() {
        instance = HazelcastClient.newHazelcastClient((ClientConfig)cfg);
    }

    public static HazelcastInstance getInstance() {
        if (!instance.getLifecycleService().isRunning()) {
            HazelcastClientFactory.reconnect();
        }
        return instance;
    }

    static {
        cfg = new ClientConfig();
    }
}

