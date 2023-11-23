/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.rabbitmq.client.AMQP
 *  com.rabbitmq.client.AMQP$BasicProperties
 *  com.rabbitmq.client.AMQP$Queue
 *  com.rabbitmq.client.AMQP$Queue$DeclareOk
 *  com.rabbitmq.client.Channel
 *  com.rabbitmq.client.Connection
 *  com.rabbitmq.client.Consumer
 *  com.rabbitmq.client.DefaultConsumer
 *  com.rabbitmq.client.Envelope
 *  com.vinplay.vbee.common.cp.BaseController
 *  com.vinplay.vbee.common.cp.NoCommandRegistered
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.rmq.RMQConnectionFactory
 *  org.apache.log4j.Logger
 */
package com.vinplay.vbee.rmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.vinplay.vbee.common.cp.BaseController;
import com.vinplay.vbee.common.cp.NoCommandRegistered;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.rmq.RMQConnectionFactory;
import com.vinplay.vbee.logger.HandleMessageLogger;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;

public class RMQConsumer {
    private static final int PREFETCH_COUNT = 20;
    private static final Logger logger = Logger.getLogger((String)"vbee");
    private BaseController<byte[], Boolean> controller;
    private String queueName;
    private int numConsumer = 1;

    public RMQConsumer(String queueName, int numConsumer) {
        this.queueName = queueName;
        if (numConsumer > 1) {
            this.numConsumer = numConsumer;
        }
    }

    public void start(Map<Integer, String> commandMap) {
        try {
            this.controller = new BaseController();
            for (Map.Entry<Integer, String> entry : commandMap.entrySet()) {
                logger.debug((Object)("  " + entry.getKey() + " - " + entry.getValue()));
            }
            try {
                this.controller.initCommands(commandMap);
            }
            catch (Exception e) {
                e.printStackTrace();
                logger.error((Object)e.getMessage());
            }
            Connection connection = RMQConnectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(this.queueName, true, false, false, (Map)null);
            this.run(channel);
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.error((Object)e.getMessage());
        }
    }

    private void run(final Channel channel) throws IOException {
        channel.basicQos(20);
        DefaultConsumer consumer = new DefaultConsumer(channel){

            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                long startHandleTime = System.currentTimeMillis();
                int command = Integer.valueOf(properties.getMessageId());
                try {
                    logger.debug((Object)(String.valueOf(RMQConsumer.this.queueName) + " HANDLE MESSAGE: " + command));
                    Param p = new Param();
                    p.set((Object)body);
                    RMQConsumer.this.controller.processCommand(Integer.valueOf(command), p);
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
                catch (NoCommandRegistered e2) {
                    logger.error((Object)("COMMAND ZZZ: " + command + " NOT FOUND IN QUEUE: " + RMQConsumer.this.queueName));
                }
                catch (Exception e) {
                    e.printStackTrace();
                    logger.error((Object)(String.valueOf(RMQConsumer.this.queueName) + " HANDLE MESSAGE: " + command + " ERROR: "), (Throwable)e);
                }
                long endHandleTime = System.currentTimeMillis();
                long handleTime = endHandleTime - startHandleTime;
                HandleMessageLogger.log(RMQConsumer.this.queueName, command, handleTime, new Date());
            }
        };
        for (int i = 0; i < this.numConsumer; ++i) {
            channel.basicConsume(this.queueName, false, (Consumer)consumer);
        }
    }

}

