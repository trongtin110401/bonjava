package com.vinplay.bongda.dao.impl;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.bongda.dao.KeoBongDaDao;
import com.vinplay.bongda.dao.UserBetBongDaDao;
import com.vinplay.bongda.entities.*;
import com.vinplay.bongda.utils.BongDaUtils;
import com.vinplay.dichvuthe.dao.impl.RechargeDaoImpl;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.HashMap;

public class UserBetBongDaimpl implements UserBetBongDaDao {

    @Override
    public UserBetBongDa findUserBetBongDaBySessionAndId(String session, String id) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            Document conditions = new Document();
            conditions.put("ID", (Object) id);
            conditions.put("session", (Object) session);
            Document document = (Document) db.getCollection(BongDaUtils.BONG_DA_REQUEST).find((Bson) conditions).first();
            if (document == null)
                return null;
            Gson gson = new Gson();
            UserBetBongDa model = gson.fromJson(document.toJson(), UserBetBongDa.class);
            return model;
        } catch (Exception e) {
            RechargeDaoImpl.logger.error(e);
            return null;
        }
    }


    @Override
    public boolean insertUserBetBongDa(UserBetBongDa keoBongDa) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection(BongDaUtils.BONG_DA_REQUEST);
            Gson gson = new Gson();
            String json = gson.toJson(keoBongDa);
            // Parse to bson document and insert
            Document doc = Document.parse(json);

            col.insertOne((Object) doc);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deleteUserBetBongDa(String id) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection(BongDaUtils.BONG_DA_REQUEST);
            Document conditions = new Document();
            conditions.put("ID", (Object) id);
            BasicDBObject updateFields = new BasicDBObject();
            Document document = (Document) db.getCollection(BongDaUtils.BONG_DA_REQUEST).find((Bson) conditions).first();
            if (document == null)
                return false;
            col.deleteOne((Bson) document);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean updateUserBetBongDa(String id, int result, int moneyWin) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            Document conditions = new Document();
            conditions.put("ID", (Object) id);
            BasicDBObject updateFields = new BasicDBObject();
            updateFields.append("result", (Object) result);
            updateFields.append("moneyWin", (Object) moneyWin);
            updateFields.append("UpdatedAt", (Object) VinPlayUtils.getCurrentDateTime());
            //us
            db.getCollection(BongDaUtils.BONG_DA_REQUEST).updateOne(conditions, (Bson) new Document("$set", (Object) updateFields));
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    @Override
    public UserBetBongDaResponse getListUserBetBongDa(String session, int page, int maxItem, String fromTime, String endTime) {
        try {
            final ArrayList<UserBetBongDaModel> records = new ArrayList<UserBetBongDaModel>();
            final KeoBongDaDao dao = new KeoBongDaImpl();
            final ArrayList<Long> num = new ArrayList<Long>();
            num.add(0, 0L);
            num.add(1, 0L);
            num.add(2, 0L);
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection(BongDaUtils.BONG_DA_REQUEST);
            page = (page - 1) < 0 ? 0 : (page - 1);
            int numStart = page * maxItem;
            int numEnd = maxItem;
            BasicDBObject objsort = new BasicDBObject();
            objsort.put("_id", -1);
            HashMap<String, Object> conditions = new HashMap<String, Object>();
            if (null != session.trim() && null != session && !session.isEmpty()) {

                conditions.put("session", session);
            }
            if (fromTime != null && !fromTime.isEmpty() && endTime != null && !endTime.isEmpty()) {
                BasicDBObject obj = new BasicDBObject();
                obj.put("$gte", (Object) fromTime);
                obj.put("$lte", (Object) endTime);
                conditions.put("CreatedAt", (Object) obj);
            }
            FindIterable iterable = col.find((Bson) new Document(conditions)).sort((Bson) objsort).skip(numStart).limit(maxItem);
            iterable.forEach((Block) new Block<Document>() {

                public void apply(Document document) {

                    Gson gson = new Gson();
                    UserBetBongDa model = gson.fromJson(document.toJson(), UserBetBongDa.class);

                    KeoBongDa keo = dao.findKeoBongDaBySessionAndId(session, model.getIdTran());
                    records.add(new UserBetBongDaModel(model, keo));
                }
            });

            UserBetBongDaResponse res = new UserBetBongDaResponse(true, "success", records);
            return res;

        } catch (Exception e) {
            return null;
        }
    }


    @Override
    public UserRequestClientResponse getListRequestBongDa(String session, String id) {
        try {
            final ArrayList<UserBetBongDa> records = new ArrayList<UserBetBongDa>();
            final KeoBongDaDao dao = new KeoBongDaImpl();
            final ArrayList<Long> num = new ArrayList<Long>();
            num.add(0, 0L);
            num.add(1, 0L);
            num.add(2, 0L);
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection(BongDaUtils.BONG_DA_REQUEST);

            BasicDBObject objsort = new BasicDBObject();
            objsort.put("_id", -1);
            HashMap<String, Object> conditions = new HashMap<String, Object>();
            conditions.put("idTran", (Object) id);
            conditions.put("session", session);

            FindIterable iterable = col.find((Bson) new Document(conditions)).sort((Bson) objsort);
            iterable.forEach((Block) new Block<Document>() {

                public void apply(Document document) {

                    Gson gson = new Gson();
                    UserBetBongDa model = gson.fromJson(document.toJson(), UserBetBongDa.class);


                    records.add(model);
                }
            });

            UserRequestClientResponse res = new UserRequestClientResponse(true, "success");
            res.setListTrans(records);
            return res;

        } catch (Exception e) {
            return null;
        }
    }
}
