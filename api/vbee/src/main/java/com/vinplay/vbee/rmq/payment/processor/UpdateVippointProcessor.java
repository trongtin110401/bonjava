/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.messages.BaseMessage
 *  com.vinplay.vbee.common.messages.VippointMessage
 *  org.apache.log4j.Logger
 */
package com.vinplay.vbee.rmq.payment.processor;

import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.messages.BaseMessage;
import com.vinplay.vbee.common.messages.VippointMessage;
import com.vinplay.vbee.dao.impl.UserDaoImpl;
import org.apache.log4j.Logger;

public class UpdateVippointProcessor
implements BaseProcessor<byte[], Boolean> {
    private static final Logger logger = Logger.getLogger((String)"vbee");

    public Boolean execute(Param<byte[]> param) {
        try {
            byte[] body = (byte[])param.get();
            VippointMessage message = (VippointMessage)BaseMessage.fromBytes((byte[])body);
            UserDaoImpl dao = new UserDaoImpl();
            dao.updateVP(message);
        }
        catch (Exception e) {
            logger.debug((Object)e);
        }
        return false;
    }
}

