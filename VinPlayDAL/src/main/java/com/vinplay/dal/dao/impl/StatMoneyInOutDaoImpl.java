package com.vinplay.dal.dao.impl;

import com.hazelcast.core.IMap;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.vinplay.dal.common.UserInfo;
import com.vinplay.dal.entities.report.StatMoneyInOut;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatMoneyInOutDaoImpl {

    private static StatMoneyInOutDaoImpl instance;

    private StatMoneyInOutDaoImpl() {
    }

    public static StatMoneyInOutDaoImpl getInstance() {
        if (instance == null) {
            synchronized (StatMoneyInOutDaoImpl.class) {
                if (instance == null) {
                    instance = new StatMoneyInOutDaoImpl();
                }
            }
        }
        return instance;
    }

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
            model.depositGiftcode =document.getLong("deposit_giftcode");
        }
        return null;
    }

    public void upsertStatisticMoneyInOut(String nickName, long depositMomo, long depositBank, long withdrawBank, long withdrawMomo, long depositCard, long depositGiftCode) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> col = db.getCollection("stat_money_in_out");
        Map<String, Object> map = new HashMap<>();
        map.put("nick_name", nickName);

        // Define the query filter
        Document query = new Document(map);

        // Define the update operation
        Document updateOperations = new Document();
        updateOperations.append("deposit_momo", depositMomo);
        updateOperations.append("deposit_bank", depositBank);
        updateOperations.append("withdraw_momo", withdrawMomo);
        updateOperations.append("withdraw_bank", withdrawBank);
        updateOperations.append("deposit_card", depositCard);
        updateOperations.append("deposit_giftcode", depositGiftCode);
        Document update = new Document("$inc", updateOperations);

        // Define the options (upsert: true)
        UpdateOptions options = new UpdateOptions().upsert(true);

        // Perform the update operation with upsert
        col.updateOne(query, update, options);

        // update to cache
        IMap iMap = HazelcastClientFactory.getInstance().getMap("USER_ONLINE");
        try {
            UserInfo userInfo = (UserInfo) iMap.get(nickName);
            if (userInfo != null) {
                userInfo.setTotalCashoutBank(userInfo.getTotalCashoutBank() + withdrawBank);
                userInfo.setTotalCashoutMoMo(userInfo.getTotalCashoutMoMo() + withdrawMomo);
                userInfo.setTotalDepositBank(userInfo.getTotalDepositBank() + depositBank);
                userInfo.setTotalDepositCard(userInfo.getTotalDepositCard() + depositCard);
                userInfo.setTotalCashoutMoMo(userInfo.getTotalDepositMoMo() + depositMomo);
                iMap.set(nickName, userInfo);
            }
        } catch (Exception ex) {
        }
    }

}
