/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.mongodb.BasicDBObject
 *  com.mongodb.Block
 *  com.mongodb.client.FindIterable
 *  com.mongodb.client.MongoCollection
 *  com.mongodb.client.MongoDatabase
 *  com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory
 *  com.vinplay.vbee.common.response.MiniPokerResponse
 *  org.bson.Document
 *  org.bson.conversions.Bson
 */
package com.vinplay.dal.dao.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.dal.dao.LogMiniPokerDAO;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.response.MiniPokerResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.bson.conversions.Bson;

public class LogMiniPokerDAOImpl
implements LogMiniPokerDAO {
    @Override
    public List<MiniPokerResponse> listMiniPoker(String nickName, String bet_value, String timeStart, String timeEnd, String moneyType, int page) {
        final ArrayList<MiniPokerResponse> results = new ArrayList<MiniPokerResponse>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        FindIterable iterable = null;
        BasicDBObject obj = new BasicDBObject();
        int num_start = (page - 1) * 50;
        int num_end = 50;
        if (nickName != null && !nickName.equals("")) {
            String pattern = ".*" + nickName + ".*";
            conditions.put("user_name", (Object)new BasicDBObject().append("$regex", (Object)pattern).append("$options", (Object)"i"));
        }
        if (bet_value != null && !bet_value.equals("")) {
            conditions.put("bet_value", Long.parseLong(bet_value));
        }
        if (timeStart != null && !timeStart.equals("") && timeEnd != null && !timeEnd.equals("")) {
            obj.put("$gte", (Object)timeStart);
            obj.put("$lte", (Object)timeEnd);
            conditions.put("time_log", (Object)obj);
        }
        if (moneyType != null && !moneyType.equals("")) {
            conditions.put("money_type", Integer.parseInt(moneyType));
        }
        iterable = db.getCollection("log_mini_poker").find((Bson)new Document(conditions)).skip(num_start).limit(50);
        iterable.forEach((Block)new Block<Document>(){

            public void apply(Document document) {
                MiniPokerResponse minipoker = new MiniPokerResponse();
                minipoker.user_name = document.getString((Object)"user_name");
                minipoker.bet_value = document.getLong((Object)"bet_value");
                minipoker.result = document.getInteger((Object)"result");
                minipoker.prize = document.getLong((Object)"prize");
                minipoker.cards = document.getString((Object)"cards");
                minipoker.current_pot = document.getLong((Object)"current_pot");
                minipoker.current_fund = document.getLong((Object)"current_fund");
                minipoker.money_type = document.getInteger((Object)"money_type");
                minipoker.time_log = document.getString((Object)"time_log");
                results.add(minipoker);
            }
        });
        return results;
    }

    @Override
    public int countMiniPoker(String nickName, String bet_value, String timeStart, String timeEnd, String moneyType) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        BasicDBObject obj = new BasicDBObject();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        if (nickName != null && !nickName.equals("")) {
            String pattern = ".*" + nickName + ".*";
            conditions.put("user_name", (Object)new BasicDBObject().append("$regex", (Object)pattern).append("$options", (Object)"i"));
        }
        if (bet_value != null && !bet_value.equals("")) {
            conditions.put("bet_value", Long.parseLong(bet_value));
        }
        if (timeStart != null && !timeStart.equals("") && timeEnd != null && !timeEnd.equals("")) {
            obj.put("$gte", (Object)(timeStart + " 00:00:00"));
            obj.put("$lte", (Object)(timeEnd + " 23:59:59"));
            conditions.put("time_log", (Object)obj);
        }
        if (moneyType != null && !moneyType.equals("")) {
            conditions.put("money_type", Integer.parseInt(moneyType));
        }
        int record = (int)db.getCollection("log_mini_poker").count((Bson)new Document(conditions));
        return record;
    }

}

