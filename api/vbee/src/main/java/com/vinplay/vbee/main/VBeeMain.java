/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.vbee.common.hazelcast.HazelcastLoader
 *  com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory
 *  org.apache.log4j.Logger
 *  org.apache.log4j.PropertyConfigurator
 */
package com.vinplay.vbee.main;

import com.vinplay.vbee.common.config.VBeePath;
import com.vinplay.vbee.common.hazelcast.HazelcastLoader;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.dao.impl.LogMoneyUserDaoImpl;
import com.vinplay.vbee.dao.impl.LuckyDaoImpl;
import com.vinplay.vbee.rmq.RMQ;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
// TODO : LẮNG NGHE TRONG QUEUE VÀ INSERT VÀO DATA BASE
public class VBeeMain {
    private static final String LOG_PROPERTIES_FILE = "config/log4j.properties";
    private static final Logger logger = Logger.getLogger((String)"vbee");
    public static long luckyReferenceId;
    public static long moneyVinReferenceId;
    public static long moneyXuReferenceId;
    private static String basePath;

    private static void initializeLogger() {
        Properties logProperties = new Properties();
        try {
            File file = new File(basePath.concat(LOG_PROPERTIES_FILE));
            logProperties.load(new FileInputStream(file));
            PropertyConfigurator.configure((Properties)logProperties);
            logger.info((Object)"Logging initialized.");
        }
        catch (IOException e) {
            throw new RuntimeException("Unable to load logging property config/log4j.properties");
        }
    }

    public static void main(String[] args) {
        basePath = VBeePath.initBasePath(VBeeMain.class);
        VBeeMain.initializeLogger();
        logger.info((Object)"VBEE INIT");
        try {
            HazelcastLoader.start();
            MongoDBConnectionFactory.init();
            LuckyDaoImpl lkDao = new LuckyDaoImpl();
            luckyReferenceId = lkDao.getLastReferenceId();
            LogMoneyUserDaoImpl mnDao = new LogMoneyUserDaoImpl();
            moneyVinReferenceId = mnDao.getLastReferenceId("vin");
            moneyXuReferenceId = mnDao.getLastReferenceId("xu");
            logger.info((Object)("luckyReferenceId: " + luckyReferenceId));
            logger.info((Object)("moneyVinReferenceId: " + moneyVinReferenceId));
            logger.info((Object)("moneyXuReferenceId: " + moneyXuReferenceId));
            RMQ.start();
        }
        catch (IOException e) {
            e.printStackTrace();
            logger.debug((Object)e);
        }
    }
}

