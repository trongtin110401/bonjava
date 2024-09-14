package com.vinplay.dal.dao.impl;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.dal.entities.report.StatMoneyInOut;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class StatMoneyInOutDaoImpl {

    public StatMoneyInOut find(String nickName) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> col = db.getCollection("stat_money_in_out");
        Document document = col.find(new Document("nick_name", nickName)).first();
        StatMoneyInOut model;
        if (document != null) {
            model = new StatMoneyInOut();
            model.nickName = document.getString("nick_name");
            model.depositMomo = document.getLong("deposit_momo");
            model.depositBank = document.getLong("deposit_bank");
            model.depositCard = document.getLong("deposit_card");
            model.withdrawMomo = document.getLong("withdraw_momo");
            model.withdrawBank = document.getLong("withdraw_bank");
        }
        return null;
    }

}
