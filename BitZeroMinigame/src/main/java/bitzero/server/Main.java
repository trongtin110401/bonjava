/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server;

import bitzero.server.BitZeroServer;
import com.hazelcast.core.HazelcastInstance;
import com.vinplay.dal.service.CacheService;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import com.vinplay.vbee.common.config.VBeePath;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        boolean clusterMode = false;
        boolean useConsole = false;
        if (args.length > 0) {
            clusterMode = args[0].equalsIgnoreCase("cluster");
            useConsole = args.length > 1 && args[1].equalsIgnoreCase("console");
        }
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
        BitZeroServer bzServer = BitZeroServer.getInstance();
        bzServer.setClustered(clusterMode);
        if (useConsole) {
            bzServer.startDebugConsole();
        }
        bzServer.start();
    }

    public static void init() throws IOException {
        Properties prop = new Properties();
        FileInputStream input = new FileInputStream(VBeePath.basePath.concat("config/cluster.properties"));
        prop.load(input);
        CacheService cacheService = new CacheServiceImpl();
        cacheService.setValue("url_leader_board", prop.getProperty("url_leader_board"));
    }
}

