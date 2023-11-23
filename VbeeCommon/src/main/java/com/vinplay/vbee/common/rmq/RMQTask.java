/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.rabbitmq.client.Channel
 */
package com.vinplay.vbee.common.rmq;

import com.rabbitmq.client.Channel;
import com.vinplay.vbee.common.rmq.RMQPool;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public abstract class RMQTask {
    protected String queueName;

    public RMQTask(String queueName) {
        this.queueName = queueName;
    }

    public abstract void run(Channel var1) throws IOException;

    public void start() throws IOException, TimeoutException, InterruptedException {
        RMQPool pool = null;
        Channel channel = null;
        pool = RMQPool.getInstance();
        channel = pool.getChannel(this.queueName);
        this.run(channel);
        if (pool != null && channel != null) {
            pool.releaseChannel(channel);
        }
    }
}

