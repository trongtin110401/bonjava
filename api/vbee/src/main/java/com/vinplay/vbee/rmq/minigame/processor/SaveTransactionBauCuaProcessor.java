/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.messages.BaseMessage
 *  com.vinplay.vbee.common.messages.minigame.baucua.TransactionBauCuaMsg
 */
package com.vinplay.vbee.rmq.minigame.processor;

import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.enums.Games;
import com.vinplay.vbee.common.messages.BaseMessage;
import com.vinplay.vbee.common.messages.minigame.TransactionTaiXiuMessage;
import com.vinplay.vbee.common.messages.minigame.baucua.TransactionBauCuaMsg;
import com.vinplay.vbee.dao.impl.BauCuaDaoImpl;
import com.vinplay.vbee.dto.TopVinhDanhDto;
import com.vinplay.vbee.rmq.report.processor.TopVinhDanhProcessor;

public class SaveTransactionBauCuaProcessor
        implements BaseProcessor<byte[], Boolean> {
    public Boolean execute(Param<byte[]> param) {
        byte[] body = param.get();
        TransactionBauCuaMsg message = (TransactionBauCuaMsg) BaseMessage.fromBytes((byte[]) body);
        System.out.println("add top vinh danh");
        if (message.moneyType == 1) {
            addTopVinhDanh(message);
        }
        BauCuaDaoImpl dao = new BauCuaDaoImpl();
        dao.saveTransactionBauCua(message);
        return true;
    }

    public void addTopVinhDanh(TransactionBauCuaMsg message) {
        try {
            System.out.println("add top " + message.toJson());
            TopVinhDanhDto topVinhDanhDto = new TopVinhDanhDto();
            topVinhDanhDto.setUsername(message.username);
            topVinhDanhDto.setScore(message.totalExchange);
            topVinhDanhDto.setBoardName(Games.BAU_CUA.getName());
            TopVinhDanhProcessor.addTopVinhDanh(topVinhDanhDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

