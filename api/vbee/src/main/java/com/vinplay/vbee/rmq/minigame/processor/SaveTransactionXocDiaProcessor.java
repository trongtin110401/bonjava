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
import com.vinplay.vbee.common.enums.Games;
import com.vinplay.vbee.common.messages.minigame.TransactionTaiXiuMessage;
import com.vinplay.vbee.common.rmq.ELKrmq;
import com.vinplay.vbee.dao.impl.TaiXiuMd5DaoImpl;
import com.vinplay.vbee.dto.TopVinhDanhDto;
import com.vinplay.vbee.rmq.report.processor.TopVinhDanhProcessor;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.Date;

public class SaveTransactionXocDiaProcessor
        implements BaseProcessor<byte[], Boolean> {
    private static final Logger logger = Logger.getLogger((String) "vbee");

    public Boolean execute(Param<byte[]> param) {
        System.out.println("SaveTransactionXocDiaProcessor " + Arrays.toString(param.get()));
//        byte[] body = param.get();
//        try {
//            TransactionTaiXiuMessage message = (TransactionTaiXiuMessage) TransactionTaiXiuMessage.fromBytes((byte[]) body);
//            TaiXiuMd5DaoImpl dao = new TaiXiuMd5DaoImpl();
//
//            if (message.moneyType == 1) {
//                addTopVinhDanh(message);
//            }
//
//            dao.saveTransactionTaiXiu(message);
//            logger.debug((Object) ("Handle message : " + message.referenceId));
//        } catch (Exception e) {
//            logger.error((Object) "Handle save transaction error ", (Throwable) e);
//        }
        return false;
    }


    public void addTopVinhDanh(TransactionTaiXiuMessage message) {
        try {
            TopVinhDanhDto topVinhDanhDto = new TopVinhDanhDto();
            topVinhDanhDto.setUsername(message.username);
            topVinhDanhDto.setScore(message.prize - message.betValue);
            topVinhDanhDto.setBoardName(Games.TAI_XIU_MD5.getName());
            TopVinhDanhProcessor.addTopVinhDanh(topVinhDanhDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

