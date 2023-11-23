/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.mongodb.BasicDBObject
 *  com.mongodb.Block
 *  com.mongodb.client.FindIterable
 *  com.mongodb.client.MongoCollection
 *  com.mongodb.client.MongoDatabase
 *  com.vinplay.vbee.common.models.LogCCUModel
 *  com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory
 *  org.bson.Document
 *  org.bson.conversions.Bson
 */
package com.vinplay.dal.dao.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.dal.dao.ServerInfoDAO;
import com.vinplay.vbee.common.models.LogCCUModel;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.bson.conversions.Bson;

public class ServerInfoDAOImpl
implements ServerInfoDAO {
    @Override
    public List<LogCCUModel> getLogCCU(String startTime, String endTime) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
//        HashMap<String, BasicDBObject> conditions = new HashMap<String, BasicDBObject>();
        FindIterable iterable = null;
        BasicDBObject obj = new BasicDBObject();
        obj.put("$gte", (Object)startTime);
        obj.put("$lte", (Object)endTime);
        conditions.put("time_log", obj);
        BasicDBObject objsort = new BasicDBObject();
        objsort.put("_id", (Object)1);
        iterable = db.getCollection("log_ccu").find((Bson)new Document(conditions)).sort((Bson)objsort);
        final ArrayList<LogCCUModel> results = new ArrayList<LogCCUModel>();
        iterable.forEach((Block)new Block<Document>(){

            public void apply(Document document) {
                LogCCUModel entry = new LogCCUModel();
                entry.ccu = document.getInteger((Object)"ccu", 0);
                entry.web = document.getInteger((Object)"web", 0);
                entry.ad = document.getInteger((Object)"ad", 0);
                entry.ios = document.getInteger((Object)"ios", 0);
                entry.wp = document.getInteger((Object)"wp", 0);
                entry.fb = document.getInteger((Object)"fb", 0);
                entry.dt = document.getInteger((Object)"dt", 0);
                entry.ot = document.getInteger((Object)"ot", 0);
                entry.ts = document.getString((Object)"time_log");
                results.add(entry);
            }
        });
        return results;
    }

}

