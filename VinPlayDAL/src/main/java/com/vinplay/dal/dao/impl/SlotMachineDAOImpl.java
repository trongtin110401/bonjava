/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.mongodb.BasicDBObject
 *  com.mongodb.Block
 *  com.mongodb.client.FindIterable
 *  com.mongodb.client.MongoCollection
 *  com.mongodb.client.MongoDatabase
 *  com.vinplay.vbee.common.enums.Games
 *  com.vinplay.vbee.common.models.minigame.pokego.LSGDPokeGo
 *  com.vinplay.vbee.common.models.minigame.pokego.TopPokeGo
 *  com.vinplay.vbee.common.models.slot.NoHuModel
 *  com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory
 *  com.vinplay.vbee.common.pools.ConnectionPool
 *  org.bson.Document
 *  org.bson.conversions.Bson
 */
package com.vinplay.dal.dao.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.dal.dao.SlotMachineDAO;
import com.vinplay.vbee.common.enums.Games;
import com.vinplay.vbee.common.models.minigame.pokego.LSGDPokeGo;
import com.vinplay.vbee.common.models.minigame.pokego.TopPokeGo;
import com.vinplay.vbee.common.models.slot.NoHuModel;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.pools.ConnectionPool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.bson.conversions.Bson;

public class SlotMachineDAOImpl
implements SlotMachineDAO {
    @Override
    public List<TopPokeGo> getTopByPage(String gameName, int pageNumber) {
        int pageSize = 10;
        if (gameName.equalsIgnoreCase(Games.KHO_BAU.getName())) {
            pageSize = 5;
        }
        int skipNumber = (pageNumber - 1) * pageSize;
        final ArrayList<TopPokeGo> results = new ArrayList<TopPokeGo>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        FindIterable iterable = null;
        Document conditions = new Document();
        conditions.put("$or", Arrays.asList(new Document[]{new Document("result", (Object)3), new Document("result", (Object)4)}));
        BasicDBObject sortCondtions = new BasicDBObject();
        sortCondtions.put("_id", -1);
        iterable = db.getCollection("log_" + gameName).find((Bson)conditions).sort((Bson)sortCondtions).skip(skipNumber).limit(pageSize);
        iterable.forEach((Block)new Block<Document>(){

            public void apply(Document document) {
                TopPokeGo entry = new TopPokeGo();
                entry.un = document.getString((Object)"user_name");
                entry.bv = document.getLong((Object)"bet_value");
                entry.pz = document.getLong((Object)"prize");
                entry.ts = document.getString((Object)"time_log");
                entry.rs = document.getInteger((Object)"result");
                results.add(entry);
            }
        });
        return results;
    }

    @Override
    public int countLSGD(String gameName, String username) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        Document conditions = new Document();
        conditions.put("user_name", (Object)username);
        long totalRows = db.getCollection("log_" + gameName).count((Bson)conditions);
        return (int)totalRows;
    }

    @Override
    public List<LSGDPokeGo> getLSGD(String gameName, String username, int pageNumber) {
        int pageSize = 10;
        if (gameName.equalsIgnoreCase(Games.KHO_BAU.getName()) ||gameName.equalsIgnoreCase(Games.SPARTAN.getName())) {
            pageSize = 5;
        }
        int skipNumber = (pageNumber - 1) * pageSize;
        final ArrayList<LSGDPokeGo> results = new ArrayList<LSGDPokeGo>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        FindIterable iterable = null;
        Document conditions = new Document();
        conditions.put("user_name", (Object)username);
        BasicDBObject sortCondtions = new BasicDBObject();
        sortCondtions.put("_id", -1);
        iterable = db.getCollection("log_" + gameName).find((Bson)conditions).sort((Bson)sortCondtions).skip(skipNumber).limit(pageSize);
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
    public long getLastRefenceId(String gameName) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        HashMap conditions = new HashMap();
        BasicDBObject objsort = new BasicDBObject();
        objsort.put("_id", -1);
        FindIterable iterable = db.getCollection("log_" + gameName).find((Bson)new Document(conditions)).sort((Bson)objsort).limit(1);
        Document document = iterable != null ? (Document)iterable.first() : null;
        long referenceId = document == null ? 0L : document.getLong((Object)"reference_id");
        return referenceId;
    }

    @Override
    public List<TopPokeGo> getTop(String gameName, int number) {
        final ArrayList<TopPokeGo> results = new ArrayList<TopPokeGo>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        FindIterable iterable = null;
        Document conditions = new Document();
        conditions.put("$or", Arrays.asList(new Document[]{new Document("result", (Object)3), new Document("result", (Object)4)}));
        BasicDBObject sortCondtions = new BasicDBObject();
        sortCondtions.put("_id", -1);
        iterable = db.getCollection("log_" + gameName).find((Bson)conditions).sort((Bson)sortCondtions).limit(number);
        iterable.forEach((Block)new Block<Document>(){

            public void apply(Document document) {
                TopPokeGo entry = new TopPokeGo();
                entry.un = document.getString((Object)"user_name");
                entry.bv = document.getLong((Object)"bet_value");
                entry.pz = document.getLong((Object)"prize");
                entry.ts = document.getString((Object)"time_log");
                entry.rs = document.getInteger((Object)"result");
                results.add(entry);
            }
        });
        return results;
    }

    @Override
    public boolean updateSlotFreeDaily(String gameName, String nickname, int room, int newValue) throws SQLException {
        boolean res = false;
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_minigame");){
            String sql = "UPDATE rotate_slot_free SET rotate_free = ? WHERE nick_name=? AND game_name = '" + gameName + "' AND room = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, newValue);
            stm.setString(2, nickname);
            stm.setInt(3, room);
            if (stm.executeUpdate() == 1) {
                res = true;
            }
            stm.close();
        }
        return res;
    }

    //Get Log nổ hũ các game
    @Override
    public List<NoHuModel> getListNoHu(String gameName, int page) {
        int numItems = 10;
        int start = (page - 1) * 10;
        final ArrayList<NoHuModel> results = new ArrayList<NoHuModel>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        FindIterable iterable = null;
        Document conditions = new Document();
        conditions.append("game_name", (Object)gameName);
        BasicDBObject sortCondtions = new BasicDBObject();
        sortCondtions.put("_id", -1);
        iterable = db.getCollection("log_no_hu_slot").find((Bson)conditions).sort((Bson)sortCondtions).skip(start).limit(10);
        iterable.forEach((Block)new Block<Document>(){

            public void apply(Document document) {
                String nick_name = document.getString((Object)"nick_name");
                NoHuModel entry = new NoHuModel();
                entry.rf = document.getLong((Object)"reference_id");
                entry.nn = nick_name.contains("[R] ") ? nick_name : "[R] " + nick_name;
                entry.bv = document.getInteger((Object)"bet_value").intValue();
                entry.pz = document.getLong((Object)"prize");
                entry.mx = document.getString((Object)"matrix");
                entry.lw = document.getString((Object)"lines_win");
                entry.pl = document.getString((Object)"prizes_on_line");
                entry.ts = document.getString((Object)"time_log");
                entry.rs = document.getInteger((Object)"result");
                results.add(entry);
            }
        });
        return results;
    }

}

