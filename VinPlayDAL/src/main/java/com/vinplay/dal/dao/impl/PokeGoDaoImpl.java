/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.mongodb.BasicDBObject
 *  com.mongodb.Block
 *  com.mongodb.client.FindIterable
 *  com.mongodb.client.MongoCollection
 *  com.mongodb.client.MongoDatabase
 *  com.vinplay.vbee.common.models.minigame.pokego.LSGDPokeGo
 *  com.vinplay.vbee.common.models.minigame.pokego.TopPokeGo
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
import com.mongodb.client.model.Sorts;
import com.vinplay.dal.dao.PokeGoDAO;
import com.vinplay.vbee.common.models.minigame.pokego.LSGDPokeGo;
import com.vinplay.vbee.common.models.minigame.pokego.TopPokeGo;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.bson.conversions.Bson;

public class PokeGoDaoImpl
implements PokeGoDAO {
    @Override
    public List<TopPokeGo> getTopPokeGo(int moneyType, int pageNumber) {
        int pageSize = 10;
        int skipNumber = (pageNumber - 1) * 10;
        final ArrayList<TopPokeGo> results = new ArrayList<TopPokeGo>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        FindIterable iterable = null;
        Document conditions = new Document();
        conditions.put("$or", Arrays.asList(new Document[]{new Document("result", (Object)3), new Document("result", (Object)4)}));
        conditions.put("money_type", (Object)moneyType);
        BasicDBObject sortCondtions = new BasicDBObject();
        sortCondtions.put("_id", -1);
        iterable = db.getCollection("log_candy").find((Bson)conditions).sort((Bson)sortCondtions).skip(skipNumber).limit(10);
        iterable.forEach((Block)new Block<Document>(){

            public void apply(Document document) {
                TopPokeGo entry = new TopPokeGo();
                entry.un = document.getString((Object)"user_name");
                entry.bv = document.getLong((Object)"bet_value");
                entry.pz = document.getLong((Object)"prize");
                entry.ts = document.getString((Object)"time_log");
                results.add(entry);
            }
        });
        return results;
    }

    @Override
    public int countLSGD(String username, int moneyType) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        Document conditions = new Document();
        conditions.put("user_name", (Object)username);
        conditions.put("money_type", (Object)moneyType);
        long totalRows = db.getCollection("log_candy").count((Bson)conditions);
        return (int)totalRows;
    }

    @Override
    public List<LSGDPokeGo> getLSGD(String username, int moneyType, int pageNumber) {
        int pageSize = 10;
        int skipNumber = (pageNumber - 1) * 10;
        final ArrayList<LSGDPokeGo> results = new ArrayList<LSGDPokeGo>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        FindIterable iterable = null;
        Document conditions = new Document();
        conditions.put("user_name", (Object)username);
        conditions.put("money_type", (Object)moneyType);
        iterable = db.getCollection("log_candy").find((Bson)conditions)
                .sort(Sorts.descending("time_log")).skip(skipNumber).limit(10);
        iterable.forEach((Block)new Block<Document>(){

            public void apply(Document document) {
                LSGDPokeGo entry = new LSGDPokeGo();
                entry.rf = document.getLong((Object)"reference_id");
                entry.un = document.getString((Object)"user_name");
                entry.bv = document.getLong((Object)"bet_value");
                entry.pz = document.getLong((Object)"prize");
                entry.lb = document.getString((Object)"lines_betting");
                entry.lw = document.getString((Object)"lines_win");
                entry.ps = document.getString((Object)"prizes_on_line");
                entry.ts = document.getString((Object)"time_log");
                results.add(entry);
            }
        });
        return results;
    }

    @Override
    public long getLastRefenceId() {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        HashMap conditions = new HashMap();
        BasicDBObject objsort = new BasicDBObject();
        objsort.put("_id", -1);
        FindIterable iterable = db.getCollection("log_candy").find((Bson)new Document(conditions)).sort((Bson)objsort).limit(1);
        Document document = iterable != null ? (Document)iterable.first() : null;
        long referenceId = document == null ? 0L : document.getLong((Object)"reference_id");
        return referenceId;
    }

    @Override
    public List<TopPokeGo> getTop(int moneyType, int number) {
        final ArrayList<TopPokeGo> results = new ArrayList<TopPokeGo>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        FindIterable iterable = null;
        Document conditions = new Document();
        conditions.put("$or", Arrays.asList(new Document[]{new Document("result", (Object)3), new Document("result", (Object)4)}));
        conditions.put("money_type", (Object)moneyType);
        BasicDBObject sortCondtions = new BasicDBObject();
        sortCondtions.put("_id", -1);
        iterable = db.getCollection("log_candy").find((Bson)conditions).sort((Bson)sortCondtions).limit(number);
        iterable.forEach((Block)new Block<Document>(){

            public void apply(Document document) {
                TopPokeGo entry = new TopPokeGo();
                entry.un = document.getString((Object)"user_name");
                entry.bv = document.getLong((Object)"bet_value");
                entry.pz = document.getLong((Object)"prize");
                entry.ts = document.getString((Object)"time_log");
                results.add(entry);
            }
        });
        return results;
    }

}

