/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.mongodb.client.MongoCollection
 *  com.mongodb.client.MongoDatabase
 *  com.vinplay.vbee.common.messages.minigame.LogRutLocMessge
 *  com.vinplay.vbee.common.messages.minigame.LogTanLocMessage
 *  com.vinplay.vbee.common.messages.minigame.ResultTaiXiuMessage
 *  com.vinplay.vbee.common.messages.minigame.ThanhDuMessage
 *  com.vinplay.vbee.common.messages.minigame.TransactionTaiXiuDetailMessage
 *  com.vinplay.vbee.common.messages.minigame.TransactionTaiXiuMessage
 *  com.vinplay.vbee.common.messages.minigame.UpdateFundMessage
 *  com.vinplay.vbee.common.messages.minigame.UpdateLuotRutLocMessage
 *  com.vinplay.vbee.common.messages.minigame.UpdatePotMessage
 *  com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory
 *  com.vinplay.vbee.common.pools.ConnectionPool
 *  org.bson.Document
 */
package com.vinplay.vbee.dao.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.vbee.common.messages.TransactionXocDiaMessage;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import com.vinplay.vbee.dao.XocDiaDao;
import org.bson.Document;

public class XocDiaDaoImpl implements XocDiaDao {

    @Override
    public void saveTransactionXocDia(TransactionXocDiaMessage msg) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("xoc_dia_transaction");
        Document doc = new Document();
        doc.append("reference_id", msg.referenceId);
        doc.append("user_name", msg.username);
        doc.append("total_prize", msg.getBetResult().getTotalBetValue());
        doc.append("zero_white", msg.betResult.getZeroWhite());
        doc.append("four_white", msg.betResult.getFourWhite());
        doc.append("three_white", msg.betResult.getThreeWhite());
        doc.append("one_white", msg.betResult.getOneWhite());
        doc.append("even", msg.betResult.getEven());
        doc.append("odd", msg.betResult.getOdd());
        doc.append("result", msg.getResult());
        doc.append("money_exchange", msg.totalExchange);
        doc.append("create_time", VinPlayUtils.getCurrentDateTime());
        col.insertOne(doc);
    }
}

