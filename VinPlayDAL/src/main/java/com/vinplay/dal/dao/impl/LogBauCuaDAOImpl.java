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
 *  com.vinplay.vbee.common.response.BauCuaReponseDetail
 *  com.vinplay.vbee.common.response.BauCuaResponse
 *  com.vinplay.vbee.common.response.BauCuaResultResponse
 *  org.bson.Document
 *  org.bson.conversions.Bson
 */
package com.vinplay.dal.dao.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.dal.dao.LogBauCuaDAO;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.response.BauCuaReponseDetail;
import com.vinplay.vbee.common.response.BauCuaResponse;
import com.vinplay.vbee.common.response.BauCuaResultResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.bson.conversions.Bson;

public class LogBauCuaDAOImpl
implements LogBauCuaDAO {
    @Override
    public List<BauCuaResponse> listLogBauCua(String referent_id, String nickName, String room, String timeStart, String timeEnd, String moneyType, int page) {
        final ArrayList<BauCuaResponse> results = new ArrayList<BauCuaResponse>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        FindIterable iterable = null;
        BasicDBObject obj = new BasicDBObject();
        BasicDBObject objsort = new BasicDBObject();
        int num_start = (page - 1) * 50;
        int num_end = 50;
        objsort.put("_id", -1);
        if (nickName != null && !nickName.equals("")) {
            String pattern = ".*" + nickName + ".*";
            conditions.put("user_name", (Object)new BasicDBObject().append("$regex", (Object)pattern).append("$options", (Object)"i"));
        }
        if (referent_id != null && !referent_id.equals("")) {
            conditions.put("reference_id", Long.parseLong(referent_id));
        }
        if (room != null && !room.equals("")) {
            conditions.put("room", Integer.parseInt(room));
        }
        if (timeStart != null && !timeStart.equals("") && timeEnd != null && !timeEnd.equals("")) {
            obj.put("$gte", (Object)timeStart);
            obj.put("$lte", (Object)timeEnd);
            conditions.put("time_log", (Object)obj);
        }
        if (moneyType != null && !moneyType.equals("")) {
            conditions.put("money_type", Integer.parseInt(moneyType));
        }
        iterable = db.getCollection("bau_cua_transaction").find((Bson)new Document(conditions)).sort((Bson)objsort).skip(num_start).limit(50);
        iterable.forEach((Block)new Block<Document>(){

            public void apply(Document document) {
                BauCuaResponse baucua = new BauCuaResponse();
                baucua.referent_id = document.getLong((Object)"reference_id");
                baucua.room = document.getInteger((Object)"room");
                baucua.user_name = document.getString((Object)"user_name");
                baucua.money_type = document.getInteger((Object)"money_type");
                baucua.dices = document.getString((Object)"dices");
                baucua.bet_value = document.getString((Object)"bet_value");
                baucua.bet_bau = document.getLong((Object)"bet_bau");
                baucua.bet_ca = document.getLong((Object)"bet_ca");
                baucua.bet_ga = document.getLong((Object)"bet_ga");
                baucua.bet_tom = document.getLong((Object)"bet_tom");
                baucua.bet_huou = document.getLong((Object)"bet_huou");
                baucua.bet_cua = document.getLong((Object)"bet_cua");
                baucua.prize = document.getString((Object)"prize");
                baucua.prize_bau = document.getLong((Object)"prize_bau");
                baucua.prize_cua = document.getLong((Object)"prize_cua");
                baucua.prize_ca = document.getLong((Object)"prize_ca");
                baucua.prize_tom = document.getLong((Object)"prize_tom");
                baucua.prize_huou = document.getLong((Object)"prize_huou");
                baucua.prize_ga = document.getLong((Object)"prize_ga");
                baucua.money_exchange = document.getLong((Object)"money_exchange");
                baucua.time_log = document.getString((Object)"time_log");
                results.add(baucua);
            }
        });
        return results;
    }

    @Override
    public int countLogBauCua(String referent_id, String nickName, String room, String timeStart, String timeEnd, String moneyType) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        BasicDBObject obj = new BasicDBObject();
        int record = 0;
        if (nickName != null && !nickName.equals("")) {
            String pattern = ".*" + nickName + ".*";
            conditions.put("user_name", (Object)new BasicDBObject().append("$regex", (Object)pattern).append("$options", (Object)"i"));
        }
        if (referent_id != null && !referent_id.equals("")) {
            conditions.put("reference_id", Long.parseLong(referent_id));
        }
        if (room != null && !room.equals("")) {
            conditions.put("room", Integer.parseInt(room));
        }
        if (timeStart != null && !timeStart.equals("") && timeEnd != null && !timeEnd.equals("")) {
            obj.put("$gte", (Object)(timeStart + " 00:00:00"));
            obj.put("$lte", (Object)(timeEnd + " 23:59:59"));
            conditions.put("time_log", (Object)obj);
        }
        if (moneyType != null && !moneyType.equals("")) {
            conditions.put("money_type", Integer.parseInt(moneyType));
        }
        record = (int)db.getCollection("bau_cua_transaction").count((Bson)new Document(conditions));
        return record;
    }

    @Override
    public List<BauCuaReponseDetail> getLogBauCuaDetail(String referent_id, int page) {
        int num_start = (page - 1) * 50;
        int num_end = 50;
        BasicDBObject objsort = new BasicDBObject();
        objsort.put("_id", -1);
        final ArrayList<BauCuaReponseDetail> results = new ArrayList<BauCuaReponseDetail>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
//        HashMap<String, Long> conditions = new HashMap<String, Long>();
        FindIterable iterable = null;
        if (referent_id != null && !referent_id.equals("")) {
            conditions.put("reference_id", (Object)Long.parseLong(referent_id));
        }
        iterable = db.getCollection("bau_cua_transaction_detail").find((Bson)new Document(conditions)).sort((Bson)objsort).skip(num_start).limit(50);
        iterable.forEach((Block)new Block<Document>(){

            public void apply(Document document) {
                BauCuaReponseDetail detail = new BauCuaReponseDetail();
                detail.referent_id = document.getLong((Object)"reference_id");
                detail.user_name = document.getString((Object)"user_name");
                detail.room = document.getInteger((Object)"room");
                detail.bet_value = document.getString((Object)"bet_value");
                detail.bet_bau = document.getLong((Object)"bet_bau");
                detail.bet_cua = document.getLong((Object)"bet_cua");
                detail.bet_tom = document.getLong((Object)"bet_tom");
                detail.bet_ca = document.getLong((Object)"bet_ca");
                detail.bet_ga = document.getLong((Object)"bet_ga");
                detail.bet_huou = document.getLong((Object)"bet_huou");
                detail.money_type = document.getInteger((Object)"money_type");
                detail.time_log = document.getString((Object)"time_log");
                results.add(detail);
            }
        });
        return results;
    }

    @Override
    public int countLogBauCuaDetail(String referent_id) {
        int record = 0;
        MongoDatabase db = MongoDBConnectionFactory.getDB();
//        HashMap<String, Long> conditions = new HashMap<String, Long>();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        if (referent_id != null && !referent_id.equals("")) {
            conditions.put("reference_id", (Object)Long.parseLong(referent_id));
        }
        record = (int)db.getCollection("bau_cua_transaction_detail").count((Bson)new Document(conditions));
        return record;
    }

    @Override
    public List<BauCuaResultResponse> listLogBauCauResult(String referent_id, String room, String timeStart, String timeEnd, int page) {
        final ArrayList<BauCuaResultResponse> results = new ArrayList<BauCuaResultResponse>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
//        HashMap<String, Number> conditions = new HashMap<String, Number>();
        FindIterable iterable = null;
        BasicDBObject obj = new BasicDBObject();
        BasicDBObject objsort = new BasicDBObject();
        int num_start = (page - 1) * 50;
        int num_end = 50;
        objsort.put("_id", -1);
        if (referent_id != null && !referent_id.equals("")) {
            conditions.put("reference_id", (Object)Long.parseLong(referent_id));
        }
        if (room != null && !room.equals("")) {
            conditions.put("room", Integer.parseInt(room));
        }
        if (timeStart != null && !timeStart.equals("") && timeEnd != null && !timeEnd.equals("")) {
            obj.put("$gte", (Object)timeStart);
            obj.put("$lte", (Object)timeEnd);
            conditions.put("time_log", obj);
        }
        iterable = db.getCollection("bau_cua_results").find((Bson)new Document(conditions)).sort((Bson)objsort).skip(num_start).limit(50);
        iterable.forEach((Block)new Block<Document>(){

            public void apply(Document document) {
                long money;
                BauCuaResultResponse baucua = new BauCuaResultResponse();
                baucua.reference_id = document.getLong((Object)"reference_id");
                baucua.room = document.getInteger((Object)"room");
                baucua.dice1 = document.getInteger((Object)"dice1");
                baucua.dice2 = document.getInteger((Object)"dice2");
                baucua.dice3 = document.getInteger((Object)"dice3");
                baucua.x_pot = document.getInteger((Object)"x_pot");
                baucua.bet_value = document.getString((Object)"bet_value");
                baucua.x_value = document.getInteger((Object)"x_value");
                baucua.bet_bau = document.getLong((Object)"bet_bau");
                baucua.bet_ca = document.getLong((Object)"bet_ca");
                baucua.bet_ga = document.getLong((Object)"bet_ga");
                baucua.bet_tom = document.getLong((Object)"bet_tom");
                baucua.bet_huou = document.getLong((Object)"bet_huou");
                baucua.bet_cua = document.getLong((Object)"bet_cua");
                baucua.prize_bau = document.getLong((Object)"prize_bau");
                baucua.prize_cua = document.getLong((Object)"prize_cua");
                baucua.prize_ca = document.getLong((Object)"prize_ca");
                baucua.prize_tom = document.getLong((Object)"prize_tom");
                baucua.prize_huou = document.getLong((Object)"prize_huou");
                baucua.prize_ga = document.getLong((Object)"prize_ga");
                baucua.time_log = document.getString((Object)"time_log");
                baucua.totalBet = baucua.bet_bau + baucua.bet_ca + baucua.bet_ga + baucua.bet_tom + baucua.bet_huou + baucua.bet_cua;
                baucua.totalPrize = baucua.prize_bau + baucua.prize_cua + baucua.prize_ca + baucua.prize_tom + baucua.prize_huou + baucua.prize_ga;
                baucua.total = money = baucua.totalPrize - baucua.totalBet;
                results.add(baucua);
            }
        });
        return results;
    }

    @Override
    public long countLogBauCauResult(String referent_id, String room, String timeStart, String timeEnd) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
//        HashMap<String, Number> conditions = new HashMap<String, Number>();
        BasicDBObject obj = new BasicDBObject();
        BasicDBObject objsort = new BasicDBObject();
        objsort.put("_id", -1);
        if (referent_id != null && !referent_id.equals("")) {
            conditions.put("reference_id", (Object)Long.parseLong(referent_id));
        }
        if (room != null && !room.equals("")) {
            conditions.put("room", Integer.parseInt(room));
        }
        if (timeStart != null && !timeStart.equals("") && timeEnd != null && !timeEnd.equals("")) {
            obj.put("$gte", (Object)timeStart);
            obj.put("$lte", (Object)timeEnd);
            conditions.put("time_log", obj);
        }
        long total = db.getCollection("bau_cua_results").count((Bson)new Document(conditions));
        return total;
    }

}

