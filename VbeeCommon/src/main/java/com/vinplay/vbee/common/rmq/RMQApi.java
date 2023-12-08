/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.vbee.common.rmq;

import com.vinplay.vbee.common.config.VBeePath;
import com.vinplay.vbee.common.messages.*;
import com.vinplay.vbee.common.rmq.RMQConnectionFactory;
import com.vinplay.vbee.common.rmq.RMQPublishTask;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

public class RMQApi {
    public static void publishMessage(String queueName, BaseMessage message, int command) throws IOException, TimeoutException, InterruptedException {
        RMQPublishTask task = new RMQPublishTask(message, queueName, command);
        task.start();
    }

    public static void publishMessagePayment(BaseMessage message, int command) throws IOException, TimeoutException, InterruptedException {
        ELKrmq elk = new ELKrmq();
        String queueName = "queue_payment";
//        switch (command) {
//            case 16: {
//                elk.InsertLog30((MoneyMessageInMinigame)message);
//                queueName = "queue_payment_minigame";
//                command = 30;
//                break;
//            }
//            case 10: {
//                elk.InsertLog40((MoneyMessageInGame)message);
//                queueName = "queue_payment_gamebai";
//                command = 40;
//                break;
//            }
//            case 12: {
//                elk.InsertLog41((FreezeMoneyMessage)message);
//                queueName = "queue_payment_gamebai";
//                command = 41;
//                break;
//            }
//            case 13: {
//                elk.InsertLog42((FreezeMoneyMessage)message);
//                queueName = "queue_payment_gamebai";
//                command = 42;
//            }
//        }
        RMQPublishTask task = new RMQPublishTask(message, queueName, command);
        task.start();
    }

    public static void publishMessageLogMoney(LogMoneyUserMessage message) throws IOException, TimeoutException, InterruptedException {
//        ELKrmq elk = new ELKrmq();
//        elk.InsertLog601(message);
        RMQPublishTask task = new RMQPublishTask(message, "queue_log_money", 601);
        task.start();
        RMQPublishTask taskExtra = new RMQPublishTask(message, "queue_log_money_extra", 1001);
        taskExtra.start();
    }

    public static void start(String configFile) throws IOException {
        if (configFile == null || configFile.isEmpty()) {
            configFile = "config/rmq.properties";
        }
        Properties prop = new Properties();
        FileInputStream input = new FileInputStream(VBeePath.basePath.concat(configFile));
        prop.load(input);
        RMQConnectionFactory.USERNAME = prop.getProperty("rmq_username");
        RMQConnectionFactory.PASSWORD = prop.getProperty("rmq_password");
        RMQConnectionFactory.SERVER_ADR = prop.getProperty("rmq_server");
        RMQConnectionFactory.SERVER_PORT = Integer.parseInt(prop.getProperty("rmq_port"));
        RMQConnectionFactory.RECONNECT_DELAY = Integer.parseInt(prop.getProperty("rmq_reconnect_delay"));
        RMQConnectionFactory.CONNECTION_TIMEOUT = Integer.parseInt(prop.getProperty("rmq_connection_timeout"));
        RMQConnectionFactory.HANDSHAKE_TIMEOUT = Integer.parseInt(prop.getProperty("rmq_handshake_timeout"));        
    }
}

