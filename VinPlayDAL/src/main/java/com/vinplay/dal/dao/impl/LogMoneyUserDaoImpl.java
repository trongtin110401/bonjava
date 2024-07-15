/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.core.IMap
 *  com.mongodb.BasicDBObject
 *  com.mongodb.Block
 *  com.mongodb.client.AggregateIterable
 *  com.mongodb.client.FindIterable
 *  com.mongodb.client.MongoCollection
 *  com.mongodb.client.MongoDatabase
 *  com.mongodb.client.result.UpdateResult
 *  com.vinplay.vbee.common.hazelcast.HazelcastClientFactory
 *  com.vinplay.vbee.common.messages.gamebai.LogNoHuGameBaiMessage
 *  com.vinplay.vbee.common.models.UserModel
 *  com.vinplay.vbee.common.models.cache.UserCacheModel
 *  com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory
 *  com.vinplay.vbee.common.pools.ConnectionPool
 *  com.vinplay.vbee.common.response.LogMoneyUserResponse
 *  com.vinplay.vbee.common.response.LogUserMoneyResponse
 *  com.vinplay.vbee.common.statics.Consts
 *  com.vinplay.vbee.common.utils.DateTimeUtils
 *  com.vinplay.vbee.common.utils.UserUtil
 *  org.bson.Document
 *  org.bson.conversions.Bson
 */
package com.vinplay.dal.dao.impl;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.DBCollection;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import com.mongodb.client.result.UpdateResult;
import com.vinplay.dal.dao.LogMoneyUserDao;
import com.vinplay.dal.dao.impl.UserDaoImpl;
import com.vinplay.dal.entities.gamebai.TopGameBaiModel;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.messages.gamebai.LogNoHuGameBaiMessage;
import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.models.cache.UserCacheModel;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.pools.ConnectionPool;
import com.vinplay.vbee.common.response.LogMoneyUserResponse;
import com.vinplay.vbee.common.response.LogUserMoneyResponse;
import com.vinplay.vbee.common.statics.Consts;
import com.vinplay.vbee.common.utils.DateTimeUtils;
import com.vinplay.vbee.common.utils.UserUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.bson.Document;
import org.bson.conversions.Bson;

public class LogMoneyUserDaoImpl
        implements LogMoneyUserDao {
    @Override
    public List<LogUserMoneyResponse> searchLogMoneyUser(String nickName, String userName, String moneyType, String serviceName, String actionName, String timeStart, String timeEnd, int page, int like, int totalRecord) {
        final ArrayList<LogUserMoneyResponse> results = new ArrayList<LogUserMoneyResponse>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        FindIterable iterable = null;
        BasicDBObject obj = new BasicDBObject();
        int numStart = (page - 1) * totalRecord;
        if (nickName != null && !nickName.equals("")) {
            conditions.put("nick_name", nickName);
        }
        if (userName != null && !userName.equals("")) {
            conditions.put("user_name", userName);
        }
        if (actionName != null && !actionName.equals("")) {
            conditions.put("action_name", actionName);
        }
        if (serviceName != null && !serviceName.equals("")) {
            conditions.put("service_name", serviceName);
        }
        if (timeStart != null && !timeStart.equals("") && timeEnd != null && !timeEnd.equals("")) {
            obj.put("$gte", (Object) timeStart);
            obj.put("$lte", (Object) timeEnd);
            conditions.put("trans_time", obj);
        }
        if (moneyType.equals("vin")) {
            BasicDBObject objsort = new BasicDBObject();
            objsort.put("trans_id", -1);
            iterable = db.getCollection("log_money_user_vin").find((Bson) new Document(conditions)).sort(objsort).skip(numStart).limit(totalRecord);

        } else if (moneyType.equals("xu")) {
            BasicDBObject objsort = new BasicDBObject();
            objsort.put("trans_time", -1);
            iterable = db.getCollection("log_money_user_xu").find((Bson) new Document(conditions)).sort(objsort).skip(numStart).limit(totalRecord);
        }
        iterable.forEach((Block) new Block<Document>() {

            public void apply(Document document) {
                LogUserMoneyResponse tranlogmoney = new LogUserMoneyResponse();
                tranlogmoney.transId = document.getLong("trans_id");
                tranlogmoney.nickName = document.getString((Object) "nick_name");
                tranlogmoney.serviceName = document.getString((Object) "service_name");
                tranlogmoney.currentMoney = document.getLong((Object) "current_money");
                tranlogmoney.moneyExchange = document.getLong((Object) "money_exchange");
                tranlogmoney.description = document.getString((Object) "description");
                tranlogmoney.transactionTime = document.getString((Object) "trans_time");
                tranlogmoney.actionName = document.getString((Object) "action_name");
                tranlogmoney.fee = document.getLong((Object) "fee");
                results.add(tranlogmoney);
            }
        });
        return results;
    }

    @Override
    public List<LogUserMoneyResponse> searchLogMoneyUser(String nickName, String serviceName, String actionName, String timeStart, String timeEnd, int page, int totalRecord) {
        final ArrayList<LogUserMoneyResponse> results = new ArrayList<LogUserMoneyResponse>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        FindIterable iterable = null;
        BasicDBObject obj = new BasicDBObject();
        int numStart = (page - 1) * totalRecord;
        if (nickName != null && !nickName.equals("")) {
            conditions.put("nick_name", nickName);
        }

        if (actionName != null && !actionName.equals("")) {
            conditions.put("action_name", actionName);
        }
        if (serviceName != null && !serviceName.equals("")) {
            conditions.put("service_name", serviceName);
        }
        if (timeStart != null && !timeStart.equals("") && timeEnd != null && !timeEnd.equals("")) {
            try {
                obj.put("$gte", VinPlayUtils.getDateTimeStr(VinPlayUtils.getDateTimeFromDate(timeStart + " 00:00:00")));
                obj.put("$lte", VinPlayUtils.getDateTimeStr(VinPlayUtils.getDateTimeFromDate(timeEnd + " 23:59:59")));
            } catch (Exception e) {
                e.printStackTrace();
            }

            conditions.put("trans_time", obj);
        }
        if (numStart > -1 && totalRecord > -1) {
            iterable = db.getCollection("log_money_user_vin").find((Bson) new Document(conditions)).skip(numStart).limit(totalRecord);
        } else {
            iterable = db.getCollection("log_money_user_vin").find((Bson) new Document(conditions));
        }

        iterable.forEach((Block) new Block<Document>() {

            public void apply(Document document) {
                LogUserMoneyResponse tranlogmoney = new LogUserMoneyResponse();
                tranlogmoney.nickName = document.getString((Object) "nick_name");
                tranlogmoney.serviceName = document.getString((Object) "service_name");
                tranlogmoney.currentMoney = document.getLong((Object) "current_money");
                tranlogmoney.moneyExchange = document.getLong((Object) "money_exchange");
                tranlogmoney.description = document.getString((Object) "description");
                tranlogmoney.transactionTime = document.getString((Object) "trans_time");
                tranlogmoney.actionName = document.getString((Object) "action_name");
                tranlogmoney.fee = document.getLong((Object) "fee");
                results.add(tranlogmoney);
            }
        });
        return results;
    }

    public List<LogUserMoneyResponse> searchLogMoneyUser2(String nickName, String serviceName, String actionName, String timeStart, String timeEnd, int page, int totalRecord) {
        final ArrayList<LogUserMoneyResponse> results = new ArrayList<LogUserMoneyResponse>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        FindIterable iterable = null;
        BasicDBObject obj = new BasicDBObject();
        int numStart = (page - 1) * totalRecord;
        if (nickName != null && !nickName.equals("")) {
            conditions.put("nick_name", nickName);
        }

        if (actionName != null && !actionName.equals("")) {
            conditions.put("action_name", actionName);
        }
        if (serviceName != null && !serviceName.equals("")) {
            conditions.put("service_name", serviceName);
        }
        if (timeStart != null && !timeStart.equals("") && timeEnd != null && !timeEnd.equals("")) {
            try {
                obj.put("$gte", timeStart + " 00:00:00");
                obj.put("$lte", timeEnd + " 23:59:59");
            } catch (Exception e) {
                e.printStackTrace();
            }

            conditions.put("trans_time", obj);
        }
        if (numStart > -1 && totalRecord > -1) {
            iterable = db.getCollection("log_money_user_vin").find((Bson) new Document(conditions)).skip(numStart).limit(totalRecord);
        } else {
            iterable = db.getCollection("log_money_user_vin").find((Bson) new Document(conditions));
        }

        iterable.forEach((Block) new Block<Document>() {

            public void apply(Document document) {
                LogUserMoneyResponse tranlogmoney = new LogUserMoneyResponse();
                tranlogmoney.nickName = document.getString((Object) "nick_name");
                tranlogmoney.serviceName = document.getString((Object) "service_name");
                tranlogmoney.currentMoney = document.getLong((Object) "current_money");
                tranlogmoney.moneyExchange = document.getLong((Object) "money_exchange");
                tranlogmoney.description = document.getString((Object) "description");
                tranlogmoney.transactionTime = document.getString((Object) "trans_time");
                tranlogmoney.actionName = document.getString((Object) "action_name");
                tranlogmoney.fee = document.getLong((Object) "fee");
                results.add(tranlogmoney);
            }
        });
        return results;
    }


    public List<LogUserMoneyResponse> getLogMoneyUser(String timeStart, String timeEnd) {
        final ArrayList<LogUserMoneyResponse> results = new ArrayList<LogUserMoneyResponse>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        HashMap<String, Object> conditions = new HashMap<>();
        FindIterable iterable;
        BasicDBObject obj = new BasicDBObject();
        conditions.put("is_bot", false);
        if (timeStart != null && !timeStart.equals("") && timeEnd != null && !timeEnd.equals("")) {
            try {
                obj.put("$gte", VinPlayUtils.getDateTimeStr(VinPlayUtils.getDateTimeFromDate(timeStart + " 00:00:00")));
                obj.put("$lte", VinPlayUtils.getDateTimeStr(VinPlayUtils.getDateTimeFromDate(timeEnd + " 23:59:59")));
            } catch (Exception e) {
                e.printStackTrace();
            }

            conditions.put("trans_time", obj);
        }
        iterable = db.getCollection("log_money_user_vin").find(new Document(conditions));

        iterable.forEach((Block) new Block<Document>() {

            public void apply(Document document) {
                LogUserMoneyResponse tranlogmoney = new LogUserMoneyResponse();
                tranlogmoney.nickName = document.getString((Object) "nick_name");
                tranlogmoney.serviceName = document.getString((Object) "service_name");
                tranlogmoney.currentMoney = document.getLong((Object) "current_money");
                tranlogmoney.moneyExchange = document.getLong((Object) "money_exchange");
                tranlogmoney.description = document.getString((Object) "description");
                tranlogmoney.transactionTime = document.getString((Object) "trans_time");
                tranlogmoney.actionName = document.getString((Object) "action_name");
                tranlogmoney.fee = document.getLong((Object) "fee");
                results.add(tranlogmoney);
            }
        });
        return results;
    }


    public List<LogUserMoneyResponse> getLogMoneyUserByNickname(String timeStart, String timeEnd, String nickname) {
        final ArrayList<LogUserMoneyResponse> results = new ArrayList<LogUserMoneyResponse>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        HashMap<String, Object> conditions = new HashMap<>();
        FindIterable iterable;
        BasicDBObject obj = new BasicDBObject();
        conditions.put("is_bot", false);
        if (timeStart != null && !timeStart.equals("") && timeEnd != null && !timeEnd.equals("")) {
            try {
                obj.put("$gte", VinPlayUtils.getDateTimeStr(VinPlayUtils.getDateTimeFromDate(timeStart + " 00:00:00")));
                obj.put("$lte", VinPlayUtils.getDateTimeStr(VinPlayUtils.getDateTimeFromDate(timeEnd + " 23:59:59")));
            } catch (Exception e) {
                e.printStackTrace();
            }


            conditions.put("trans_time", obj);
        }
        if (!nickname.equals("")) {
            conditions.put("nick_name", nickname);
        }
        iterable = db.getCollection("log_money_user_vin").find(new Document(conditions));

        iterable.forEach((Block) new Block<Document>() {

            public void apply(Document document) {
                LogUserMoneyResponse tranlogmoney = new LogUserMoneyResponse();
                tranlogmoney.nickName = document.getString((Object) "nick_name");
                tranlogmoney.serviceName = document.getString((Object) "service_name");
                tranlogmoney.currentMoney = document.getLong((Object) "current_money");
                tranlogmoney.moneyExchange = document.getLong((Object) "money_exchange");
                tranlogmoney.description = document.getString((Object) "description");
                tranlogmoney.transactionTime = document.getString((Object) "trans_time");
                tranlogmoney.actionName = document.getString((Object) "action_name");
                tranlogmoney.fee = document.getLong((Object) "fee");
                results.add(tranlogmoney);
            }
        });
        return results;
    }

    @Override
    public LogUserMoneyResponse searchLastLogMoneyUser(String nick_name, String type, ArrayList<String> agents) {
        final ArrayList<LogUserMoneyResponse> results = new ArrayList<LogUserMoneyResponse>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        FindIterable iterable = null;
        BasicDBObject obj = new BasicDBObject();
        if (nick_name != null && !nick_name.equals("")) {
            conditions.put("nick_name", nick_name);
        }
        conditions.put("action_name", "TransferMoney");

        BasicDBObject objsort = new BasicDBObject();
        objsort.put("create_time", -1);

        // money
        BasicDBObject objMoney = new BasicDBObject();
        BasicDBObject objAgent = new BasicDBObject();
        if ("TRANSFER_USER".equals(type)) {
            // chuyen tien cho user
            objMoney.put("$lt", 0);
            conditions.put("money_exchange", objMoney);
        } else if ("TRANSFER_AGENT".equals(type)) {
            // chuyen tien cho dai ly
            objMoney.put("$lt", 0);
            conditions.put("money_exchange", objMoney);
        } else if ("RECEIVE_AGENT".equals(type)) {
            // nhan tien tu dai ly
            objMoney.put("$gt", 0);
            conditions.put("money_exchange", objMoney);
        } else if ("RECEIVE_USER".equals(type)) {
            // nha tien tu user
            objMoney.put("$gt", 0);
            conditions.put("money_exchange", objMoney);
        } else {

        }
        iterable = db.getCollection("log_money_user_xu").find((Bson) new Document(conditions)).sort((Bson) objsort).skip(0).limit(100);
        iterable.forEach((Block) new Block<Document>() {

            public void apply(Document document) {
                LogUserMoneyResponse tranlogmoney = new LogUserMoneyResponse();
                tranlogmoney.nickName = document.getString((Object) "nick_name");
                tranlogmoney.serviceName = document.getString((Object) "service_name");
                tranlogmoney.currentMoney = document.getLong((Object) "current_money");
                tranlogmoney.moneyExchange = document.getLong((Object) "money_exchange");
                tranlogmoney.description = document.getString((Object) "description");
                tranlogmoney.transactionTime = document.getString((Object) "trans_time");
                tranlogmoney.actionName = document.getString((Object) "action_name");
                tranlogmoney.fee = document.getLong((Object) "fee");
                tranlogmoney.createdTime = document.getDate("create_time").getTime();
                results.add(tranlogmoney);
            }
        });
        if (results != null && results.size() > 0) {
            LogUserMoneyResponse resp = null;
            for (LogUserMoneyResponse response : results) {
                if ("TRANSFER_USER".equals(type) || "RECEIVE_USER".equals(type)) {
                    // khong phai agent  
                    boolean match = false;
                    for (String s : agents) {
                        if (response.description.contains(s)) {
                            match = true;
                        }
                    }
                    if (!match) {
                        // tai khoan user
                        resp = response;
                        break;
                    }
                } else {
                    // khong phai agent  
                    boolean match = false;
                    for (String s : agents) {
                        if (response.description.contains(s)) {
                            match = true;
                        }
                    }
                    if (match) {
                        // tai khoan user
                        resp = response;
                        break;
                    }
                }
            }
            return resp;
        }
        return null;
    }

    @Override
    public List<LogUserMoneyResponse> searchAllLogMoneyUser(String nick_name, String type, boolean seven_days) {
        final ArrayList<LogUserMoneyResponse> results = new ArrayList<LogUserMoneyResponse>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        FindIterable iterable = null;
        if (nick_name != null && !nick_name.equals("")) {
            conditions.put("nick_name", nick_name);
        }
        if (seven_days) {
            Date currentDate = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(currentDate);
            c.add(Calendar.DATE, -7);
            Date currentDatePlusSeven = c.getTime();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String strDate = dateFormat.format(currentDatePlusSeven);
            BasicDBObject obj = new BasicDBObject();
            obj.put("$gte", (Object) strDate);
            conditions.put("trans_time", obj);
        }

        BasicDBObject objsort = new BasicDBObject();
        objsort.put("create_time", -1);
        if ("TRANSFER".equals(type)) {
            conditions.put("action_name", "TransferMoney");
            iterable = db.getCollection("log_money_user_tieu_vin").find((Bson) new Document(conditions)).sort((Bson) objsort);
        } else if ("GIFTCODE".equals(type)) {
            //conditions.put("action_name", "GiftCode");
            //BasicDBObject objGiftCode = new BasicDBObject();   
            //objGiftCode.put("$regex", "GiftCode");
            //conditions.put("action_name", objGiftCode); 
            String pattern = ".*" + "GiftCode" + ".*";
            conditions.put("action_name", (Object) new BasicDBObject().append("$regex", (Object) pattern).append("$options", (Object) "i"));
            iterable = db.getCollection("log_money_user_nap_vin").find((Bson) new Document(conditions)).sort((Bson) objsort);
        } else if ("CARD".equals(type)) {
            conditions.put("action_name", "RechargeByCard");
            iterable = db.getCollection("log_money_user_nap_vin").find((Bson) new Document(conditions)).sort((Bson) objsort);
        } else if ("BANK".equals(type)) {
            conditions.put("action_name", "RechargeByBank");
            iterable = db.getCollection("log_money_user_nap_vin").find((Bson) new Document(conditions)).sort((Bson) objsort);
        } else if ("MOMO".equals(type)) {
            conditions.put("action_name", "RechargeByMomo");
            iterable = db.getCollection("log_money_user_nap_vin").find((Bson) new Document(conditions)).sort((Bson) objsort);
        } else {
            conditions.put("action_name", "TransferMoney");
            iterable = db.getCollection("log_money_user_nap_vin").find((Bson) new Document(conditions)).sort((Bson) objsort);
        }
        iterable.forEach((Block) new Block<Document>() {

            public void apply(Document document) {
                LogUserMoneyResponse tranlogmoney = new LogUserMoneyResponse();
                tranlogmoney.nickName = document.getString((Object) "nick_name");
                tranlogmoney.serviceName = document.getString((Object) "service_name");
                tranlogmoney.currentMoney = document.getLong((Object) "current_money");
                tranlogmoney.moneyExchange = document.getLong((Object) "money_exchange");
                tranlogmoney.description = document.getString((Object) "description");
                tranlogmoney.transactionTime = document.getString((Object) "trans_time");
                tranlogmoney.actionName = document.getString((Object) "action_name");
                tranlogmoney.fee = document.getLong((Object) "fee");
                if (document.getDate("create_time") != null)
                    tranlogmoney.createdTime = document.getDate("create_time").getTime() + 7 * 60 * 60 * 1000;
                results.add(tranlogmoney);
            }
        });
        return results;
    }

    @Override
    public long getTotalBetWin(String nick_name, String type, String action_name) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
//        FindIterable iterable = null;        
//        conditions.put("is_bot", false);
//        if (nick_name != null && !nick_name.equals("")) {
//            conditions.put("nick_name", nick_name);
//        }  
//        conditions.put("play_game", true);
//        
//        
//        iterable = db.getCollection("log_money_user_vin").find();
        List<String> slots = new ArrayList<>();
        slots.add("KhoBau");
        slots.add("NuDiepVien");
        slots.add("SieuAnhHung");
        slots.add("VuongQuocVin");
        List<BasicDBObject> pipeline = new ArrayList<>();
        if ("BET".equals(type)) {
            if (action_name != null && !"".equals(action_name)) {
                if ("SLOT".equals(action_name)) {
                    BasicDBObject match = new BasicDBObject(
                            "$match", (new BasicDBObject("is_bot", false))
                            .append("nick_name", nick_name)
                            .append("action_name", new BasicDBObject("$in", slots))
                            .append("play_game", true)
                            .append("money_exchange", new BasicDBObject("$lt", 0)));
                    pipeline.add(match);
                } else {
                    BasicDBObject match = new BasicDBObject(
                            "$match", (new BasicDBObject("is_bot", false))
                            .append("nick_name", nick_name)
                            .append("action_name", action_name)
                            .append("play_game", true)
                            .append("money_exchange", new BasicDBObject("$lt", 0)));
                    pipeline.add(match);
                }
            } else {
                BasicDBObject match = new BasicDBObject(
                        "$match", (new BasicDBObject("is_bot", false))
                        .append("nick_name", nick_name)
                        .append("play_game", true)
                        .append("money_exchange", new BasicDBObject("$lt", 0)));
                pipeline.add(match);
            }
        } else {
            if (action_name != null && !"".equals(action_name)) {
                if ("SLOT".equals(action_name)) {
                    BasicDBObject match = new BasicDBObject(
                            "$match", (new BasicDBObject("is_bot", false))
                            .append("nick_name", nick_name)
                            .append("action_name", new BasicDBObject("$in", slots))
                            .append("play_game", true)
                            .append("money_exchange", new BasicDBObject("$gt", 0)));
                    pipeline.add(match);
                } else {
                    BasicDBObject match = new BasicDBObject(
                            "$match", (new BasicDBObject("is_bot", false))
                            .append("nick_name", nick_name)
                            .append("action_name", action_name)
                            .append("play_game", true)
                            .append("money_exchange", new BasicDBObject("$gt", 0)));
                    pipeline.add(match);
                }
            } else {
                BasicDBObject match = new BasicDBObject(
                        "$match", (new BasicDBObject("is_bot", false))
                        .append("nick_name", nick_name)
                        .append("play_game", true)
                        .append("money_exchange", new BasicDBObject("$gt", 0)));
                pipeline.add(match);
            }
        }

        BasicDBObject group = new BasicDBObject(
                "$group", new BasicDBObject("_id", null).append(
                "total", new BasicDBObject("$sum", "$money_exchange")
        )
        );
        pipeline.add(group);
        AggregateIterable<Document> output = db.getCollection("log_money_user_vin").aggregate(pipeline);
        List<Document> results = new ArrayList<Document>();
        output.forEach((Block) new Block<Document>() {

            public void apply(Document document) {
                results.add(document);
            }
        });
        if (results != null && results.size() > 0) {
            return results.get(0).getLong("total");
        }
        return 0;
    }

    @Override
    public int countsearchLogMoneyUser(String nickName, String moneyType, String serviceName, String actionName, String timeStart, String timeEnd, int like) {
        int countRecord = 0;
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        BasicDBObject obj = new BasicDBObject();
        BasicDBObject objsort = new BasicDBObject();
        objsort.put("_id", -1);
        if (nickName != null && !nickName.equals("")) {
            if (like == 0) {
                conditions.put("nick_name", nickName);
            } else {
                conditions.put("nick_name", nickName);
            }
        }
        if (actionName != null && !actionName.equals("")) {
            conditions.put("action_name", actionName);
        }
        if (serviceName != null && !serviceName.equals("")) {
            conditions.put("service_name", serviceName);
        }
        if (timeStart != null && !timeStart.equals("") && timeEnd != null && !timeEnd.equals("")) {
            obj.put("$gte", (Object) timeStart);
            obj.put("$lte", (Object) timeEnd);
            conditions.put("trans_time", obj);
        }
        if (moneyType.equals("vin")) {
            countRecord = (int) db.getCollection("log_money_user_vin").count((Bson) new Document(conditions));
        } else if (moneyType.equals("xu")) {
            countRecord = (int) db.getCollection("log_money_user_xu").count((Bson) new Document(conditions));
        }
        return countRecord;
    }

    @Override
    public List<LogMoneyUserResponse> getHistoryTransactionLogMoney(String nickName, int moneyType, int page) {
        int numStart = (page - 1) * 5;
        int numEnd = 5;
        List<LogMoneyUserResponse> result = this.getTransactionList(nickName, moneyType, numStart, numEnd);
        return result;
    }

    private Map<String, Object> buildConditionNapVin(String nickName) {
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        conditions.put("nick_name", nickName);
        return conditions;
    }

    private Map<String, Object> buildConditionTieuVin(String nickName) {
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        conditions.put("nick_name", nickName);
        return conditions;
    }

    private Map<String, Object> buildConditionTransaction(String nickName, int moneyType) {
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        conditions.put("nick_name", nickName);
        ArrayList<BasicDBObject> lstServerName = new ArrayList<BasicDBObject>();
        switch (moneyType) {
            case 4: {
                for (String action : Consts.NAP_XU) {
                    BasicDBObject query = new BasicDBObject("action_name", (Object) action);
                    lstServerName.add(query);
                }
                conditions.put("$or", lstServerName);
                conditions.put("money_exchange", (Object) new BasicDBObject("$gt", (Object) 0));
            }
        }
        return conditions;
    }

    @Override
    public int countHistoryTransactionLogMoney(String nickName, int queryType) {
        int countRecord = 0;
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        Map<String, Object> conditions = this.buildConditionTransaction(nickName, queryType);
        Map<String, Object> conditionNapVin = this.buildConditionNapVin(nickName);
        Map<String, Object> conditionTieuVin = this.buildConditionTieuVin(nickName);
        switch (queryType) {
            case 1: {
                countRecord = (int) db.getCollection("log_money_user_vin").count((Bson) new Document(conditions));
                break;
            }
            case 3: {
                countRecord = (int) db.getCollection("log_money_user_nap_vin").count((Bson) new Document(conditionNapVin));
                break;
            }
            case 5: {
                countRecord = (int) db.getCollection("log_money_user_tieu_vin").count((Bson) new Document(conditionTieuVin));
                break;
            }
            default: {
                countRecord = (int) db.getCollection("log_money_user_xu").count((Bson) new Document(conditions));
            }
        }
        return countRecord;
    }

    @Override
    public List<LogMoneyUserResponse> getTransactionList(String nickName, int queryType, int start, int end) {
        final ArrayList<LogMoneyUserResponse> results = new ArrayList<LogMoneyUserResponse>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        Map<String, Object> conditions = this.buildConditionTransaction(nickName, queryType);
        Map<String, Object> conditionNapVin = this.buildConditionNapVin(nickName);
        Map<String, Object> conditionTieuVin = this.buildConditionTieuVin(nickName);
        FindIterable iterable = null;
        BasicDBObject objsort = new BasicDBObject();
        objsort.put("_id", -1);
        switch (queryType) {
            case 1: {
                iterable = db.getCollection("log_money_user_vin").find((Bson) new Document(conditions)).sort((Bson) objsort).skip(start).limit(end);
                break;
            }
            case 3: {
                iterable = db.getCollection("log_money_user_nap_vin").find((Bson) new Document(conditionNapVin)).sort((Bson) objsort).skip(start).limit(end);
                break;
            }
            case 5: {
                iterable = db.getCollection("log_money_user_tieu_vin").find((Bson) new Document(conditionTieuVin)).sort((Bson) objsort).skip(start).limit(end);
                break;
            }
            default: {
                iterable = db.getCollection("log_money_user_xu").find((Bson) new Document(conditions)).sort((Bson) objsort).skip(start).limit(end);
            }
        }
        iterable.forEach((Block) new Block<Document>() {

            public void apply(Document document) {
                LogMoneyUserResponse tranlogmoney = new LogMoneyUserResponse();
                tranlogmoney.transId = document.getLong((Object) "trans_id");
                tranlogmoney.serviceName = document.getString((Object) "service_name");
                tranlogmoney.currentMoney = document.getLong((Object) "current_money");
                tranlogmoney.moneyExchange = document.getLong((Object) "money_exchange");
                tranlogmoney.description = document.getString((Object) "description");
                tranlogmoney.transactionTime = document.getString((Object) "trans_time");
                tranlogmoney.actionName = document.getString((Object) "action_name");
                results.add(tranlogmoney);
            }
        });
        return results;
    }

    @Override
    public Map<String, TopGameBaiModel> getTopGameBai(String gameName) {
        TopGameBaiModel model;
        HashMap<String, TopGameBaiModel> result = new HashMap<String, TopGameBaiModel>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection collection = db.getCollection("log_money_user_xu");
        Document conditionWin = new Document();
        conditionWin.put("action_name", (Object) gameName);
        conditionWin.put("money_exchange", (Object) new BasicDBObject("$gt", (Object) 0));
        Document conditionWinWeek = new Document();
        conditionWinWeek.put("action_name", (Object) gameName);
        conditionWinWeek.put("money_exchange", (Object) new BasicDBObject("$gt", (Object) 0));
        String startTime = "2016-10-17 00;00;00";
        BasicDBObject obj = new BasicDBObject();
        obj.put("$gte", (Object) "2016-10-17 00;00;00");
        conditionWinWeek.put("trans_time", (Object) obj);
        AggregateIterable iterableWin = collection.aggregate(Arrays.asList(new Document[]{new Document("$match", (Object) conditionWin), new Document("$group", (Object) new Document("_id", (Object) "$nick_name").append("money", (Object) new Document("$sum", (Object) "$money_exchange")).append("count", (Object) new Document("$sum", (Object) 1)))}));
        final ArrayList<TopGameBaiModel> resultWin = new ArrayList();
        iterableWin.forEach((Block) new Block<Document>() {

            public void apply(Document document) {
                TopGameBaiModel top = new TopGameBaiModel();
                top.setNickName(document.getString((Object) "_id"));
                top.setWinCount(document.getInteger((Object) "count"));
                top.setMoneyWin(document.getLong((Object) "money"));
                resultWin.add(top);
            }
        });
        AggregateIterable iterableWinWeek = collection.aggregate(Arrays.asList(new Document[]{new Document("$match", (Object) conditionWinWeek), new Document("$group", (Object) new Document("_id", (Object) "$nick_name").append("money", (Object) new Document("$sum", (Object) "$money_exchange")).append("count", (Object) new Document("$sum", (Object) 1)))}));
        final ArrayList<TopGameBaiModel> resultWinWeek = new ArrayList();
        iterableWinWeek.forEach((Block) new Block<Document>() {

            public void apply(Document document) {
                TopGameBaiModel top = new TopGameBaiModel();
                top.setNickName(document.getString((Object) "_id"));
                top.setWinCountThisWeek(document.getInteger((Object) "count"));
                top.setMoneyWinThisWeek(document.getLong((Object) "money"));
                resultWinWeek.add(top);
            }
        });
        Document conditionLost = new Document();
        conditionLost.put("action_name", (Object) gameName);
        conditionLost.put("money_exchange", (Object) new BasicDBObject("$lt", (Object) 0));
        Document conditionLostWeek = new Document();
        conditionLostWeek.put("action_name", (Object) gameName);
        conditionLostWeek.put("money_exchange", (Object) new BasicDBObject("$lt", (Object) 0));
        conditionLostWeek.put("trans_time", (Object) obj);
        AggregateIterable iterableLost = collection.aggregate(Arrays.asList(new Document[]{new Document("$match", (Object) conditionLost), new Document("$group", (Object) new Document("_id", (Object) "$nick_name").append("money", (Object) new Document("$sum", (Object) "$money_exchange")).append("count", (Object) new Document("$sum", (Object) 1)))}));
        final ArrayList<TopGameBaiModel> resultLost = new ArrayList();
        iterableLost.forEach((Block) new Block<Document>() {

            public void apply(Document document) {
                TopGameBaiModel top = new TopGameBaiModel();
                top.setNickName(document.getString((Object) "_id"));
                top.setLostCount(document.getInteger((Object) "count"));
                top.setMoneyLost(document.getLong((Object) "money"));
                resultLost.add(top);
            }
        });
        AggregateIterable iterablLostWeek = collection.aggregate(Arrays.asList(new Document[]{new Document("$match", (Object) conditionLostWeek), new Document("$group", (Object) new Document("_id", (Object) "$nick_name").append("money", (Object) new Document("$sum", (Object) "$money_exchange")).append("count", (Object) new Document("$sum", (Object) 1)))}));
        final ArrayList<TopGameBaiModel> resultLostWeek = new ArrayList();
        iterablLostWeek.forEach((Block) new Block<Document>() {

            public void apply(Document document) {
                TopGameBaiModel top = new TopGameBaiModel();
                top.setNickName(document.getString((Object) "_id"));
                top.setLostCountThisWeek(document.getInteger((Object) "count"));
                top.setMoneyLostThisWeek(document.getLong((Object) "money"));
                resultLostWeek.add(top);
            }
        });
        for (TopGameBaiModel top : resultWin) {
            top.setWinCountThisMonth(top.getWinCount());
            top.setWinCountThisYear(top.getWinCount());
            top.setMoneyWinThisMonth(top.getMoneyWin());
            top.setMoneyWinThisYear(top.getMoneyWin());
            result.put(top.getNickName(), top);
        }
        for (TopGameBaiModel top : resultWinWeek) {
            if (!result.containsKey(top.getNickName())) continue;
            model = (TopGameBaiModel) result.get(top.getNickName());
            model.setWinCountThisWeek(top.getWinCountThisWeek());
            model.setMoneyWinThisWeek(top.getMoneyWinThisWeek());
            result.put(top.getNickName(), model);
        }
        for (TopGameBaiModel top : resultLost) {
            if (result.containsKey(top.getNickName())) {
                model = (TopGameBaiModel) result.get(top.getNickName());
                model.setLostCount(top.getLostCount());
                model.setLostCountThisMonth(top.getLostCount());
                model.setLostCountThisYear(top.getLostCount());
                model.setMoneyLost(top.getMoneyLost());
                model.setMoneyLostThisMonth(top.getMoneyLost());
                model.setMoneyLostThisYear(top.getMoneyLost());
                result.put(top.getNickName(), model);
                continue;
            }
            top.setLostCountThisMonth(top.getLostCount());
            top.setLostCountThisYear(top.getLostCount());
            top.setMoneyLostThisMonth(top.getMoneyLost());
            top.setMoneyLostThisYear(top.getMoneyLost());
            result.put(top.getNickName(), top);
        }
        for (TopGameBaiModel top : resultLostWeek) {
            if (!result.containsKey(top.getNickName())) continue;
            model = (TopGameBaiModel) result.get(top.getNickName());
            model.setLostCountThisWeek(top.getLostCountThisWeek());
            model.setMoneyLostThisWeek(top.getMoneyLostThisWeek());
            result.put(top.getNickName(), model);
        }
        return result;
    }

    @Override
    public List<LogNoHuGameBaiMessage> getNoHuGameBaiHistory(int pageNumber, String gameName) {
        int pageSize = 10;
        int skipNumber = (pageNumber - 1) * 10;
        final ArrayList<LogNoHuGameBaiMessage> results = new ArrayList<LogNoHuGameBaiMessage>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        FindIterable iterable = null;
        Document conditions = new Document();
        if (gameName != null && !gameName.isEmpty()) {
            conditions.put("game_name", (Object) gameName);
        } else {
            conditions.put("game_name", (Object) new Document("$ne", (Object) "PokerTour"));
        }
        BasicDBObject sortCondtions = new BasicDBObject();
        sortCondtions.put("_id", -1);
        iterable = db.getCollection("log_no_hu_game_bai").find((Bson) conditions).sort((Bson) sortCondtions).skip(skipNumber).limit(10);
        iterable.forEach((Block) new Block<Document>() {

            public void apply(Document document) {
                LogNoHuGameBaiMessage entry = new LogNoHuGameBaiMessage(document.getString((Object) "nick_name"), document.getInteger((Object) "room").intValue(), document.getLong((Object) "pot_value").longValue(), document.getLong((Object) "money_win").longValue(), document.getString((Object) "game_name"), document.getString((Object) "description"), document.getString((Object) "tour_id"));
                entry.setCreateTime(document.getString((Object) "trans_time"));
                results.add(entry);
            }
        });
        return results;
    }

    @Override
    public int countNoHuGameBaiHistory() {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        Document conditions = new Document();
        long totalRows = db.getCollection("log_no_hu_game_bai").count((Bson) conditions);
        return (int) totalRows;
    }

    @Override
    public UserModel getUserByNickName(String nickname) throws SQLException {
        UserModel user = null;
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");) {
            String sql = "SELECT * FROM users WHERE nick_name=?";
            PreparedStatement stm = conn.prepareStatement("SELECT * FROM users WHERE nick_name=?");
            stm.setString(1, nickname);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                user = UserUtil.parseResultSetToUserModel((ResultSet) rs);
            }
            rs.close();
            stm.close();
        }
        return user;
    }

    @Override
    public List<LogUserMoneyResponse> searchLogMoneyTranferUser(String nickName, String timeStart, String timeEnd, String type, int page) {
        final ArrayList<LogUserMoneyResponse> results = new ArrayList<LogUserMoneyResponse>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        FindIterable iterable = null;
        BasicDBObject obj = new BasicDBObject();
        BasicDBObject objsort = new BasicDBObject();
        objsort.put("_id", -1);
        int numStart = (page - 1) * 100;
        int numEnd = 100;
//        conditions.put("action_name", "TransferMoney");
        if (!nickName.isEmpty()) {
            conditions.put("nick_name", nickName);
        }
        if (!timeStart.isEmpty() && !timeEnd.isEmpty()) {
            obj.put("$gte", (Object) timeStart);
            obj.put("$lte", (Object) timeEnd);
            conditions.put("trans_time", obj);
        }
        if (type.equals("1")) {
            iterable = db.getCollection("log_money_user_tieu_vin").find((Bson) new Document(conditions)).sort((Bson) objsort).skip(numStart).limit(100);
        } else if (type.equals("2")) {
            iterable = db.getCollection("log_money_user_nap_vin").find((Bson) new Document(conditions)).sort((Bson) objsort).skip(numStart).limit(100);
        }
        iterable.forEach((Block) new Block<Document>() {

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void apply(Document document) {
                LogUserMoneyResponse tranlogmoney = new LogUserMoneyResponse();
                tranlogmoney.nickName = document.getString((Object) "nick_name");
                HazelcastInstance client = HazelcastClientFactory.getInstance();
                IMap<String, UserModel> userMap = client.getMap("users");
                if (userMap.containsKey((Object) tranlogmoney.nickName)) {
                    try {
                        userMap.lock(tranlogmoney.nickName);
                        UserCacheModel user = (UserCacheModel) userMap.get((Object) tranlogmoney.nickName);
                        tranlogmoney.userName = user.getUsername();
                    } catch (Exception user) {
                    } finally {
                        userMap.unlock(tranlogmoney.nickName);
                    }
                } else {
                    UserDaoImpl dao = new UserDaoImpl();
                    try {
                        UserModel user2 = dao.getUserByNickName(tranlogmoney.nickName);
                        tranlogmoney.userName = user2.getUsername();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                tranlogmoney.serviceName = document.getString((Object) "service_name");
                tranlogmoney.currentMoney = document.getLong((Object) "current_money");
                tranlogmoney.moneyExchange = document.getLong((Object) "money_exchange");
                tranlogmoney.description = document.getString((Object) "description");
                tranlogmoney.transactionTime = document.getString((Object) "trans_time");
                tranlogmoney.actionName = document.getString((Object) "action_name");
                tranlogmoney.fee = document.getLong((Object) "fee");
                results.add(tranlogmoney);
            }
        });
        return results;
    }

    @Override
    public boolean UpdateProcessLogChuyenTienDaiLy(String nickNameSend, String nickNameReceive, String timeLog, String Status) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("log_chuyen_tien_dai_ly");
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        conditions.put("trans_time", timeLog);
        conditions.put("nick_name_send", nickNameSend);
        conditions.put("nick_name_receive", nickNameReceive);
        col.updateOne((Bson) new Document(conditions), (Bson) new Document("$set", (Object) new Document("process", (Object) Integer.parseInt(Status))));
        return true;
    }

    @Override
    public boolean UpdateProcessLogChuyenTienDaiLyMySQL(String nickNameSend, String nickNameReceive, String timeLog, String Status) throws SQLException {
        String sql = " UPDATE vinplay.log_tranfer_agent  SET process = ?,      update_time = ?  WHERE trans_time = ?        AND nick_name_send = ?        AND nick_name_receive = ? ";
        PreparedStatement stmt = null;
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");) {
            stmt = conn.prepareStatement(" UPDATE vinplay.log_tranfer_agent  SET process = ?,      update_time = ?  WHERE trans_time = ?        AND nick_name_send = ?        AND nick_name_receive = ? ");
            stmt.setInt(1, Integer.parseInt(Status));
            stmt.setString(2, DateTimeUtils.getCurrentTime((String) "yyyy-MM-dd HH:mm:ss"));
            stmt.setString(3, timeLog);
            stmt.setString(4, nickNameSend);
            stmt.setString(5, nickNameReceive);
            stmt.executeUpdate();
            stmt.close();
        }
        return true;
    }

    public List<LogUserMoneyResponse> getMoneyCashInAndCashOutByNickname(String nickName, List<String> actionNames) {
        final ArrayList<LogUserMoneyResponse> results = new ArrayList<>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> collection = db.getCollection("log_money_user_vin");

        // Create conditions
        BasicDBObject query = new BasicDBObject();
        if (nickName != null && !nickName.equals("")) {
            query.put("nick_name", nickName);
        }
        if (actionNames != null && !actionNames.isEmpty()) {
            query.put("action_name", new BasicDBObject("$in", actionNames));
        }

        // Execute the query
        FindIterable<Document> iterable = collection.find(query);

        // Process the results
        iterable.forEach((Block<Document>) document -> {
            LogUserMoneyResponse tranlogmoney = new LogUserMoneyResponse();
            tranlogmoney.transId = document.getLong("trans_id");
            tranlogmoney.nickName = document.getString("nick_name");
            tranlogmoney.serviceName = document.getString("service_name");
            tranlogmoney.currentMoney = document.getLong("current_money");
            tranlogmoney.moneyExchange = document.getLong("money_exchange");
            tranlogmoney.description = document.getString("description");
            tranlogmoney.transactionTime = document.getString("trans_time");
            tranlogmoney.actionName = document.getString("action_name");
            tranlogmoney.fee = document.getLong("fee");
            results.add(tranlogmoney);
        });

        return results;
    }
}

