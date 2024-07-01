/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.mongodb.BasicDBObject
 *  com.mongodb.Block
 *  com.mongodb.client.FindIterable
 *  com.mongodb.client.MongoCollection
 *  com.mongodb.client.MongoDatabase
 *  com.mongodb.client.result.UpdateResult
 *  com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory
 *  com.vinplay.vbee.common.response.GiftCodeGameResponse
 *  org.bson.Document
 *  org.bson.conversions.Bson
 */
package com.vinplay.usercore.dao.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import com.vinplay.usercore.dao.GiftCodeGameDao;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.response.GiftCodeGameResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.bson.conversions.Bson;

public class GiftCodeGameDaoImpl
implements GiftCodeGameDao {
    @Override
    public boolean exportGiftCodeStore(GiftCodeGameResponse msg) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeLog = df.format(new Date());
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        conditions.put("giftcode", msg.giftCode);
        conditions.put("surfing", msg.surfing);
        conditions.put("release", msg.release);
        conditions.put("source", msg.source);
        conditions.put("quantity", msg.quantity);
        conditions.put("count_use", 0);
        conditions.put("time_log", msg.timeLog);
        long count = db.getCollection("gift_code_game_store").count((Bson)new Document(conditions));
        MongoCollection col = db.getCollection("gift_code_game_store");
        if (count > 0L) {
            col.updateOne((Bson)new Document("giftcode", (Object)msg.giftCode), (Bson)new Document("$set", (Object)new Document("surfing", (Object)msg.surfing).append("release", (Object)msg.release).append("source", (Object)msg.source).append("update_time", (Object)timeLog)));
        } else {
            Document doc = new Document();
            doc.append("giftcode", (Object)msg.giftCode);
            doc.append("surfing", (Object)msg.surfing);
            doc.append("quantity", (Object)msg.quantity);
            doc.append("source", (Object)msg.source);
            doc.append("count_use", (Object)0);
            doc.append("money_type", (Object)1);
            doc.append("create_time", (Object)timeLog);
            doc.append("release", (Object)msg.release);
            col.insertOne((Object)doc);
        }
        return true;
    }

    @Override
    public boolean exportGiftCode(final GiftCodeGameResponse msg) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final String timeLog = df.format(new Date());
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        final MongoCollection col = db.getCollection("gift_code_game");
        final MongoCollection colstore = db.getCollection("gift_code_game_store");
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        conditions.put("count_use", 0);
        conditions.put("surfing", msg.surfing);
        long count = db.getCollection("gift_code_game_store").count((Bson)new Document(conditions));
        if (count >= (long)msg.quantity) {
            FindIterable iterable = db.getCollection("gift_code_game_store").find((Bson)new Document(conditions)).limit(msg.quantity);
            iterable.forEach((Block)new Block<Document>(){

                public void apply(Document document) {
                    Document doc = new Document();
                    int len = 12 - (msg.release + msg.surfing + msg.source).length();
                    int lengiftcode = document.getString((Object)"giftcode").length();
                    String giftcode = "";
                    giftcode = lengiftcode > len ? document.getString((Object)"giftcode").substring(0, len) : document.getString((Object)"giftcode");
                    doc.append("giftcode", (Object)document.getString((Object)"giftcode"));
                    doc.append("surfing", (Object)msg.surfing);
                    doc.append("quantity", (Object)msg.quantity);
                    doc.append("source", (Object)msg.source);
                    doc.append("count_use", (Object)0);
                    doc.append("create_time", (Object)timeLog);
                    doc.append("release", (Object)msg.release);
                    doc.append("money_type", (Object)1);
                    doc.append("nick_name", (Object)"");
                    doc.append("user_name", (Object)"");
                    doc.append("mobile", (Object)"");
                    doc.append("ip", (Object)"");
                    doc.append("block", (Object)0);
                    doc.append("giftcodefull", (Object)(msg.release + msg.surfing + msg.source + giftcode));
                    doc.append("update_time", (Object)"");
                    doc.append("expire_time", (Object)"");
                    col.insertOne((Object)doc);
                    colstore.updateOne((Bson)new Document("giftcode", (Object)document.getString((Object)"giftcode")), (Bson)new Document("$set", (Object)new Document("count_use", (Object)1).append("update_time", (Object)timeLog)));
                }
            });
            return true;
        }
        return false;
    }

    @Override
    public List<GiftCodeGameResponse> searchAllGiftCode(String nickName, String giftcode, String surfing, String source, String timeStart, String timeEnd, String userName, String block, String giftuse, int page, int totalRecord) {
        int num_start = (page - 1) * totalRecord;
        final ArrayList<GiftCodeGameResponse> results = new ArrayList<GiftCodeGameResponse>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        BasicDBObject obj = new BasicDBObject();
        BasicDBObject objsort = new BasicDBObject();
        conditions.put("money_type", 1);
        objsort.put("_id", -1);
        if (surfing != null && !surfing.equals("")) {
            conditions.put("surfing", surfing);
        }
        if (timeStart != null && !timeStart.isEmpty() && timeEnd != null && !timeEnd.isEmpty()) {
            obj.put("$gte", (Object)timeStart);
            obj.put("$lte", (Object)timeEnd);
            conditions.put("update_time", (Object)obj);
        }
        if (source != null && !source.isEmpty()) {
            conditions.put("source", source);
        }
        if (userName != null && !userName.isEmpty()) {
            conditions.put("user_name", userName);
        }
        if (nickName != null && !nickName.isEmpty()) {
            conditions.put("nick_name", nickName);
        }
        if (giftcode != null && !giftcode.isEmpty()) {
            conditions.put("giftcodefull", giftcode);
        }
        if (block != null && !block.isEmpty()) {
            conditions.put("block", Integer.parseInt(block));
        }
        if (giftuse != null && !giftuse.isEmpty()) {
            conditions.put("count_use", Integer.parseInt(giftuse));
        }
        FindIterable iterable = db.getCollection("gift_code_game").find((Bson)new Document(conditions)).skip(num_start).limit(totalRecord).sort((Bson)objsort);
        iterable.forEach((Block)new Block<Document>(){

            public void apply(Document document) {
                GiftCodeGameResponse giftcode = new GiftCodeGameResponse();
                giftcode.surfing = document.getString((Object)"surfing");
                giftcode.source = document.getString((Object)"source");
                giftcode.quantity = document.getInteger((Object)"quantity");
                giftcode.giftCodeFull = document.getString((Object)"giftcodefull");
                giftcode.release = document.getString((Object)"release");
                giftcode.timeLog = document.getString((Object)"create_time");
                giftcode.updateTime = document.getString((Object)"update_time");
                giftcode.moneyType = document.getInteger((Object)"money_type");
                giftcode.useGiftCode = document.getInteger((Object)"count_use");
                giftcode.userName = document.getString((Object)"user_name");
                giftcode.nickName = document.getString((Object)"nick_name");
                giftcode.block = document.getInteger((Object)"block");
                results.add(giftcode);
            }
        });
        return results;
    }

    @Override
    public long countSearchAllGiftCode(String nickName, String giftcode, String surfing, String source, String timeStart, String timeEnd, String userName, String block, String giftuse) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        BasicDBObject obj = new BasicDBObject();
        conditions.put("money_type", 1);
        if (surfing != null && !surfing.equals("")) {
            conditions.put("surfing", surfing);
        }
        if (timeStart != null && !timeStart.isEmpty() && timeEnd != null && !timeEnd.isEmpty()) {
            obj.put("$gte", (Object)timeStart);
            obj.put("$lte", (Object)timeEnd);
            conditions.put("update_time", (Object)obj);
        }
        if (source != null && !source.isEmpty()) {
            conditions.put("source", source);
        }
        if (nickName != null && !nickName.isEmpty()) {
            conditions.put("nick_name", nickName);
        }
        if (userName != null && !userName.isEmpty()) {
            conditions.put("user_name", userName);
        }
        if (giftcode != null && !giftcode.isEmpty()) {
            conditions.put("giftcodefull", giftcode);
        }
        if (block != null && !block.isEmpty()) {
            conditions.put("block", Integer.parseInt(block));
        }
        if (giftuse != null && !giftuse.isEmpty()) {
            conditions.put("count_use", Integer.parseInt(giftuse));
        }
        long record = db.getCollection("gift_code_game").count((Bson)new Document(conditions));
        return record;
    }

    @Override
    public List<GiftCodeGameResponse> searchAllGiftCodeAdmin(String surfing, String source, String timeStart, String timeEnd, String giftuse, int page, int totalRecord) {
        int num_start = (page - 1) * totalRecord;
        final ArrayList<GiftCodeGameResponse> results = new ArrayList<GiftCodeGameResponse>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        BasicDBObject obj = new BasicDBObject();
        BasicDBObject objsort = new BasicDBObject();
        objsort.put("_id", -1);
        conditions.put("money_type", 1);
        if (surfing != null && !surfing.equals("")) {
            conditions.put("surfing", surfing);
        }
        if (timeStart != null && !timeStart.isEmpty() && timeEnd != null && !timeEnd.isEmpty()) {
            obj.put("$gte", (Object)timeStart);
            obj.put("$lte", (Object)timeEnd);
            conditions.put("update_time", (Object)obj);
        }
        if (source != null && !source.isEmpty()) {
            conditions.put("source", source);
        }
        if (giftuse != null && !giftuse.isEmpty()) {
            conditions.put("count_use", Integer.parseInt(giftuse));
        }
        FindIterable iterable = db.getCollection("gift_code_game_store").find((Bson)new Document(conditions)).skip(num_start).limit(totalRecord).sort((Bson)objsort);
        iterable.forEach((Block)new Block<Document>(){

            public void apply(Document document) {
                GiftCodeGameResponse giftcode = new GiftCodeGameResponse();
                giftcode.surfing = document.getString((Object)"surfing");
                giftcode.giftCode = document.getString((Object)"release") + document.getString((Object)"surfing") + document.getString((Object)"source") + document.getString((Object)"giftcode");
                giftcode.source = document.getString((Object)"source");
                giftcode.quantity = document.getInteger((Object)"quantity");
                giftcode.release = document.getString((Object)"release");
                giftcode.timeLog = document.getString((Object)"create_time");
                giftcode.updateTime = document.getString((Object)"update_time");
                giftcode.moneyType = document.getInteger((Object)"money_type");
                giftcode.useGiftCode = document.getInteger((Object)"count_use");
                results.add(giftcode);
            }
        });
        return results;
    }

    @Override
    public long countSearchAllGiftCodeAdmin(String surfing, String source, String timeStart, String timeEnd, String giftuse) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        BasicDBObject obj = new BasicDBObject();
        conditions.put("money_type", 1);
        if (surfing != null && !surfing.equals("")) {
            conditions.put("surfing", surfing);
        }
        if (timeStart != null && !timeStart.isEmpty() && timeEnd != null && !timeEnd.isEmpty()) {
            obj.put("$gte", (Object)timeStart);
            obj.put("$lte", (Object)timeEnd);
            conditions.put("update_time", (Object)obj);
        }
        if (source != null && !source.isEmpty()) {
            conditions.put("source", source);
        }
        if (giftuse != null && !giftuse.isEmpty()) {
            conditions.put("count_use", Integer.parseInt(giftuse));
        }
        long record = db.getCollection("gift_code_game_store").count((Bson)new Document(conditions));
        return record;
    }

    @Override
    public boolean blockGiftCode(String giftCode, String block) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection colstore = db.getCollection("gift_code_game");
        colstore.updateOne((Bson)new Document("giftcode", (Object)giftCode), (Bson)new Document("$set", (Object)new Document("block", (Object)Integer.parseInt(block))));
        return true;
    }

}

