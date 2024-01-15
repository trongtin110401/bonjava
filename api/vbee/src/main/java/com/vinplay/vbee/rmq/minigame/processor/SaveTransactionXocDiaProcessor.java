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
import com.vinplay.vbee.common.messages.TransactionXocDiaMessage;
import com.vinplay.vbee.common.messages.minigame.TransactionTaiXiuMessage;
import com.vinplay.vbee.dao.impl.XocDiaDaoImpl;
import com.vinplay.vbee.dto.TopVinhDanhDto;
import com.vinplay.vbee.rmq.report.processor.TopVinhDanhProcessor;
import org.apache.log4j.Logger;

public class SaveTransactionXocDiaProcessor
        implements BaseProcessor<byte[], Boolean> {
    private static final Logger logger = Logger.getLogger((String) "vbee");

    public Boolean execute(Param<byte[]> param) {
        byte[] body = param.get();
        System.out.println("byte length " + body.length);
        try {
            TransactionXocDiaMessage message = (TransactionXocDiaMessage) TransactionXocDiaMessage.fromBytes(body);
            System.out.println("data xoc dia:   " + message.toString());
            XocDiaDaoImpl dao = new XocDiaDaoImpl();
            addTopVinhDanh(message);
            dao.saveTransactionXocDia(message);
            logger.debug("Handle message : " + message.referenceId);
        } catch (Exception e) {
            logger.error("Handle save transaction error ", (Throwable) e);
        }
        return false;

    }


    public void addTopVinhDanh(TransactionXocDiaMessage message) {
        try {
            TopVinhDanhDto topVinhDanhDto = new TopVinhDanhDto();
            topVinhDanhDto.setUsername(message.username);
            topVinhDanhDto.setScore(message.getTotalExchange());
            topVinhDanhDto.setBoardName(Games.XOC_DIA.getName());
            TopVinhDanhProcessor.addTopVinhDanh(topVinhDanhDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

