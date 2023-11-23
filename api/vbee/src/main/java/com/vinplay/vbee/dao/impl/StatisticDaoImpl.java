/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.mongodb.client.MongoCollection
 *  com.mongodb.client.MongoDatabase
 *  com.vinplay.vbee.common.messages.statistic.LoginPortalInfoMsg
 *  com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory
 *  com.vinplay.vbee.common.utils.VinPlayUtils
 *  org.bson.Document
 */
package com.vinplay.vbee.dao.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.vbee.common.messages.statistic.LoginPortalInfoMsg;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import com.vinplay.vbee.dao.StatisticDao;
import org.bson.Document;

public class StatisticDaoImpl
implements StatisticDao {
    //todo : note Mongo- Leon
    @Override
    public boolean saveLoginPortalInfo(LoginPortalInfoMsg msg) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("user_login_info");
        Document doc = new Document();
        doc.append("user_id", (Object)msg.getUserId());
        doc.append("user_name", (Object)msg.getUsername());
        doc.append("nick_name", (Object)msg.getNickname());
        doc.append("ip", (Object)msg.getIp());
        doc.append("agent", (Object)msg.getAgent());
        doc.append("type", (Object)msg.getType());
        doc.append("time_log", (Object)VinPlayUtils.getCurrentDateTime());
        col.insertOne((Object)doc);
        return true;
    }
}

