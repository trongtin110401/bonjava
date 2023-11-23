/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.messages.minigame.TransactionTaiXiuMessage
 *  org.apache.log4j.Logger
 */
package com.vinplay.vbee.rmq.overunder.processor;

import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.messages.minigame.TransactionTaiXiuMessage;
import com.vinplay.vbee.dao.impl.OverUnderDaoImpl;
import org.apache.log4j.Logger;

public class SaveTransactionOverUnderProcessor
implements BaseProcessor<byte[], Boolean> {
    private static final Logger logger = Logger.getLogger((String)"vbee");

    public Boolean execute(Param<byte[]> param) {
        byte[] body = (byte[])param.get();
        try {
            TransactionTaiXiuMessage message = (TransactionTaiXiuMessage)TransactionTaiXiuMessage.fromBytes((byte[])body);
            OverUnderDaoImpl dao = new OverUnderDaoImpl();
            dao.saveTransactionTaiXiu(message);
            logger.debug((Object)("Handle message : " + message.referenceId));
        }
        catch (Exception e) {
            logger.error((Object)"Handle save transaction error ", (Throwable)e);
        }
        return false;
    }
}

