/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.rabbitmq.client.AMQP
 *  com.rabbitmq.client.AMQP$Queue
 *  com.rabbitmq.client.AMQP$Queue$DeclareOk
 *  com.rabbitmq.client.Channel
 *  com.rabbitmq.client.Connection
 */
package com.vinplay.vbee.common.rmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.vinplay.vbee.common.rmq.RMQConnectionFactory;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class RMQPool {
    private Connection connection = RMQConnectionFactory.newConnection();
    private static RMQPool instance;

    private RMQPool() throws IOException, TimeoutException {
    }

    public static RMQPool getInstance() throws IOException, TimeoutException {
        if (instance == null) {
            instance = new RMQPool();
        }
        return instance;
    }

    public Channel getChannel(String queueName) throws IOException {
        Channel channel = this.connection.createChannel();
        channel.queueDeclare(queueName, true, false, false, null);
        return channel;
    }

    public void releaseChannel(Channel channel) throws InterruptedException, IOException, TimeoutException {
        channel.close();
    }
}

