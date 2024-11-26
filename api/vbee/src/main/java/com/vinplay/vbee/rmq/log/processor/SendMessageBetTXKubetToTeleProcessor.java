/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.core.IMap
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.hazelcast.HazelcastClientFactory
 *  com.vinplay.vbee.common.messages.BaseMessage
 *  com.vinplay.vbee.common.messages.LogMoneyUserMessage
 *  com.vinplay.vbee.common.models.cache.ReportModel
 *  com.vinplay.vbee.common.models.cache.TransactionList
 *  com.vinplay.vbee.common.response.LogMoneyUserResponse
 *  com.vinplay.vbee.common.rmq.RMQApi
 *  com.vinplay.vbee.common.statics.Consts
 *  com.vinplay.vbee.common.utils.VinPlayUtils
 *  org.apache.log4j.Logger
 */
package com.vinplay.vbee.rmq.log.processor;

import com.vinplay.utils.TelegramAlert;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.messages.BaseMessage;
import com.vinplay.vbee.common.messages.BetTXMD5Message;

public class SendMessageBetTXKubetToTeleProcessor implements BaseProcessor<byte[], Boolean> {

    public Boolean execute(Param<byte[]> param) {
        BetTXMD5Message message = (BetTXMD5Message) BaseMessage.fromBytes(param.get());
        TelegramAlert.SendMessageBetTXKUBET(message.getNickname(), message.getBetValue(), message.getBetSide(), message.getReferenceId(), message.getUserId());
        return true;
    }


}

