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

import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.messages.minigame.NoHuTaiXiuMessage;
import com.vinplay.vbee.common.messages.minigame.TransactionTaiXiuMessage;
import com.vinplay.vbee.dao.impl.TaiXiuDaoImpl;
import org.apache.log4j.Logger;

public class SaveHistoryHuTaiXiuProcessor
implements BaseProcessor<byte[], Boolean> {
    private static final Logger logger = Logger.getLogger((String)"vbee");

    public Boolean execute(Param<byte[]> param) {
        byte[] body = (byte[])param.get();
        try {
            NoHuTaiXiuMessage message = (NoHuTaiXiuMessage)NoHuTaiXiuMessage.fromBytes((byte[])body);
            TaiXiuDaoImpl dao = new TaiXiuDaoImpl();
            /*int totalRecord = dao.getTotalTrans();
            if(totalRecord > 100 ){
                dao.deleteTopTrans();
            }*/
            dao.saveHistoryNoHuTaiXiu(message);
            logger.debug((Object)("Handle message : " + message.phien));
        }
        catch (Exception e) {
            logger.error((Object)"Handle save transaction error ", (Throwable)e);
        }
        return false;
    }
}

