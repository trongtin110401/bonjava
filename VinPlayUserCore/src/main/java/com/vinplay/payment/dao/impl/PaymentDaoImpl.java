/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.mongodb.BasicDBObject
 *  com.mongodb.Block
 *  com.mongodb.client.AggregateIterable
 *  com.mongodb.client.FindIterable
 *  com.mongodb.client.MongoCollection
 *  com.mongodb.client.MongoDatabase
 *  com.vinplay.vbee.common.messages.pay.ExchangeMessage
 *  com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory
 *  org.bson.Document
 *  org.bson.conversions.Bson
 */
package com.vinplay.payment.dao.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.payment.dao.PaymentDao;
import com.vinplay.usercore.response.LogExchangeMoneyResponse;
import com.vinplay.vbee.common.messages.pay.ExchangeMessage;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.bson.Document;
import org.bson.conversions.Bson;

public class PaymentDaoImpl
implements PaymentDao {
    @Override
    public boolean logExchangeMoney(ExchangeMessage message) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("log_exchange_money");
        Document doc = new Document();
        doc.append("nick_name", (Object)message.nickname);
        doc.append("merchant_id", (Object)message.merchantId);
        doc.append("trans_id", (Object)message.merchantTransId);
        doc.append("transaction_no", (Object)message.transNo);
        doc.append("money", (Object)message.money);
        doc.append("money_type", (Object)message.moneyType);
        doc.append("type", (Object)message.type);
        doc.append("money_exchange", (Object)message.exchangeMoney);
        doc.append("fee", (Object)message.fee);
        doc.append("code", (Object)message.code);
        doc.append("ip", (Object)message.ip);
        doc.append("trans_time", (Object)message.getCreateTime());
        doc.append("create_time", VinPlayUtils.getCurrentDateTime());
        col.insertOne((Object)doc);
        return true;
    }

    @Override
    public boolean checkMerchantTransId(String merchantId, String merchantTransId) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        Document conditions = new Document();
        conditions.put("merchant_id", (Object)merchantId);
        conditions.put("trans_id", (Object)merchantTransId);
        Document dc = (Document)db.getCollection("log_exchange_money").find((Bson)conditions).first();
        return dc != null;
    }

    @Override
    public LogExchangeMoneyResponse getLogExchangeMoney(String nickname, String merchantId, String transId, String transNo, String type, int code, String startTime, String endTime, int page) throws Exception {
        final ArrayList<ExchangeMessage> trans = new ArrayList<ExchangeMessage>();
        final List<Long> num = Arrays.asList(0L, 0L, 0L, 0L);
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        int numStart = (page - 1) * 50;
        int numEnd = 50;
        BasicDBObject objsort = new BasicDBObject();
        objsort.put("_id", -1);
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        if (!nickname.isEmpty()) {
            conditions.put("nick_name", nickname);
        }
        if (!merchantId.isEmpty()) {
            conditions.put("merchant_id", merchantId);
        }
        if (!transId.isEmpty()) {
            conditions.put("trans_id", transId);
        }
        if (!transNo.isEmpty()) {
            conditions.put("transaction_no", transNo);
        }
        if (!type.isEmpty()) {
            conditions.put("type", type);
        }
        if (code >= 0) {
            conditions.put("code", code);
        }
        if (!startTime.isEmpty() && !endTime.isEmpty()) {
            BasicDBObject obj = new BasicDBObject();
            obj.put("$gte", (Object)startTime);
            obj.put("$lte", (Object)endTime);
            conditions.put("trans_time", (Object)obj);
        }
        FindIterable iterable = db.getCollection("log_exchange_money").find((Bson)new Document(conditions)).sort((Bson)objsort).skip(numStart).limit(50);
        iterable.forEach((Block)new Block<Document>(){

            public void apply(Document document) {
                String nn = document.getString((Object)"nick_name");
                String mid = document.getString((Object)"merchant_id");
                String mtid = document.getString((Object)"trans_id");
                String tn = document.getString((Object)"transaction_no");
                long mn = document.getLong((Object)"money");
                String mt = document.getString((Object)"money_type");
                String t = document.getString((Object)"type");
                long exm = document.getLong((Object)"money_exchange");
                long f = document.getLong((Object)"fee");
                int c = document.getInteger((Object)"code");
                String ip = document.getString((Object)"ip");
                trans.add(new ExchangeMessage(nn, mid, mtid, tn, mn, mt, t, exm, f, c, ip));
            }
        });
        FindIterable iterable2 = db.getCollection("log_exchange_money").find((Bson)new Document(conditions));
        iterable2.forEach((Block)new Block<Document>(){

            public void apply(Document document) {
                long mn = document.getLong((Object)"money");
                long exm = document.getLong((Object)"money_exchange");
                long f = document.getLong((Object)"fee");
                num.set(0, (Long)num.get(0) + mn);
                num.set(1, (Long)num.get(1) + exm);
                num.set(2, (Long)num.get(2) + f);
                num.set(3, (Long)num.get(3) + 1L);
            }
        });
        return new LogExchangeMoneyResponse(num.get(0), num.get(1), num.get(2), num.get(3), trans);
    }

    @Override
    public long getTotalMoney(String merchantId, String nickname, String startTime, String endTime, String type) throws Exception {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection collection = db.getCollection("log_exchange_money");
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        if (startTime != null && !startTime.isEmpty() && endTime != null && !endTime.isEmpty()) {
            BasicDBObject obj = new BasicDBObject();
            obj.put("$gte", (Object)startTime);
            obj.put("$lte", (Object)endTime);
            conditions.put("trans_time", (Object)obj);
        }
        if (merchantId != null && !merchantId.isEmpty()) {
            conditions.put("merchant_id", merchantId);
        }
        if (nickname != null && !nickname.isEmpty()) {
            conditions.put("nick_name", nickname);
        }
        if (type != null && !type.isEmpty()) {
            conditions.put("type", type);
        }
        long money = 0L;
        Document dc = (Document)collection.aggregate(Arrays.asList(new Document[]{new Document("$match", (Object)new Document(conditions)), new Document("$group", (Object)new Document("_id", null).append("money", (Object)new Document("$sum", (Object)"$money")))})).first();
        if (dc != null) {
            money = dc.getLong((Object)"money");
        }
        return money;
    }

    @Override
    public List<ExchangeMessage> getExchangeMoney(String nickname, String merchantId, String transId, String transNo, String type, int code, String startTime, String endTime) throws Exception {
        final ArrayList<ExchangeMessage> trans = new ArrayList<ExchangeMessage>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        BasicDBObject objsort = new BasicDBObject();
        objsort.put("_id", -1);
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        if (!nickname.isEmpty()) {
            conditions.put("nick_name", nickname);
        }
        if (!merchantId.isEmpty()) {
            conditions.put("merchant_id", merchantId);
        }
        if (!transId.isEmpty()) {
            conditions.put("trans_id", transId);
        }
        if (!transNo.isEmpty()) {
            conditions.put("transaction_no", transNo);
        }
        if (!type.isEmpty()) {
            conditions.put("type", type);
        }
        if (code >= 0) {
            conditions.put("code", code);
        }
        if (!startTime.isEmpty() && !endTime.isEmpty()) {
            BasicDBObject obj = new BasicDBObject();
            obj.put("$gte", (Object)startTime);
            obj.put("$lte", (Object)endTime);
            conditions.put("trans_time", (Object)obj);
        }
        FindIterable iterable = db.getCollection("log_exchange_money").find((Bson)new Document(conditions)).sort((Bson)objsort);
        iterable.forEach((Block)new Block<Document>(){

            public void apply(Document document) {
                String nn = document.getString((Object)"nick_name");
                String mid = document.getString((Object)"merchant_id");
                String mtid = document.getString((Object)"trans_id");
                String tn = document.getString((Object)"transaction_no");
                long mn = document.getLong((Object)"money");
                String mt = document.getString((Object)"money_type");
                String t = document.getString((Object)"type");
                long exm = document.getLong((Object)"money_exchange");
                long f = document.getLong((Object)"fee");
                int c = document.getInteger((Object)"code");
                String ip = document.getString((Object)"ip");
                String tt = document.getString((Object)"trans_time");
                ExchangeMessage ms = new ExchangeMessage(nn, mid, mtid, tn, mn, mt, t, exm, f, c, ip);
                ms.setCreateTime(tt);
                trans.add(ms);
            }
        });
        return trans;
    }

}

