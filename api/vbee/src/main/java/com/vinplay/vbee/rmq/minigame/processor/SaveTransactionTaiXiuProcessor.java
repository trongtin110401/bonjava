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
import com.vinplay.vbee.common.messages.minigame.TransactionTaiXiuMessage;
import com.vinplay.vbee.common.rmq.ELKrmq;
import com.vinplay.vbee.dao.impl.TaiXiuDaoImpl;
import org.apache.log4j.Logger;

import java.util.Date;

public class SaveTransactionTaiXiuProcessor
implements BaseProcessor<byte[], Boolean> {
    private static final Logger logger = Logger.getLogger((String)"vbee");

    public Boolean execute(Param<byte[]> param) {
        byte[] body = (byte[])param.get();
        try {
            TransactionTaiXiuMessage message = (TransactionTaiXiuMessage)TransactionTaiXiuMessage.fromBytes((byte[])body);
            TaiXiuDaoImpl dao = new TaiXiuDaoImpl();
            /*int totalRecord = dao.getTotalTrans();
            if(totalRecord > 100 ){
                dao.deleteTopTrans();
            }*/
            dao.saveTransactionTaiXiu(message);
            saveToElk(message);
            logger.debug((Object)("Handle message : " + message.referenceId));
        }
        catch (Exception e) {
            logger.error((Object)"Handle save transaction error ", (Throwable)e);
        }
        return false;
    }

    public void saveToElk(TransactionTaiXiuMessage message) {
        long total_exchange = 0;
        if(message.prize > 0) {
            if(message.moneyType == 1) {
                total_exchange = Math.round(message.prize * 98 / 198);
            }else {
                total_exchange = Math.round(message.prize * 95 / 195);
            }
        }else{
            total_exchange = -(message.betValue - message.refund);
        }
        ELKrmq elKrmq = new ELKrmq();
        elKrmq.InsertLogTranSactionTaiXiu(0L,message.referenceId,message.userId,message.username,message.betValue,message.betSide,message.prize,message.refund,total_exchange,message.moneyType,new Date().getTime());
    }
}

