/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.messages.minigame.TransactionTaiXiuMessage
 *  org.apache.log4j.Logger
 */
package com.vinplay.vbee.rmq.minigame.processor;

import com.vinplay.usercore.service.impl.MailBoxServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.messages.SendMailMessage;
import org.apache.log4j.Logger;

public class SendMailQueueProcessor implements BaseProcessor<byte[], Boolean> {
    private static final Logger logger = Logger.getLogger((String) "vbee");

    public Boolean execute(Param<byte[]> param) {
        byte[] body = param.get();
        try {
            SendMailMessage message = (SendMailMessage) SendMailMessage.fromBytes(body);
            MailBoxServiceImpl service = new MailBoxServiceImpl();
            service.sendMailBoxBySystem(message.getNickName(), message.getTitle(), message.getContent(), message.getId());
        } catch (Exception e) {
            logger.error("Handle save transaction error ", (Throwable) e);
        }
        return false;

    }

}

