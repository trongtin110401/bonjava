package com.vinplay.bongda.dao.impl;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.bongda.dao.KeoBongDaDao;
import com.vinplay.bongda.entities.KeoBongDa;
import com.vinplay.bongda.entities.KeoBongDaResponse;
import com.vinplay.bongda.utils.BongDaUtils;
import com.vinplay.dichvuthe.dao.impl.RechargeDaoImpl;
import com.vinplay.dichvuthe.entities.CashoutMomoResponse;
import com.vinplay.dichvuthe.utils.CashoutUtil;
import com.vinplay.payment.entities.UserWithdrawMomo;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.HashMap;

public class KeoBongDaImpl implements KeoBongDaDao {
    @Override
    public KeoBongDa findKeoBongDaBySessionAndId(String session, String id) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            Document conditions = new Document();
            conditions.put("Id", (Object) id);
            conditions.put("session", (Object) session);
            Document document = (Document) db.getCollection(BongDaUtils.BONG_DA).find((Bson) conditions).first();
            if (document == null)
                return null;
            Gson gson = new Gson();
            KeoBongDa model = gson.fromJson(document.toJson(), KeoBongDa.class);
            return model;
        } catch (Exception e) {
            RechargeDaoImpl.logger.error(e);
            return null;
        }
    }

    @Override
    public KeoBongDa findKeoBongDaBySessionAndId(String id) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            Document conditions = new Document();
            conditions.put("Id", (Object) id);

            Document document = (Document) db.getCollection(BongDaUtils.BONG_DA).find((Bson) conditions).first();
            if (document == null)
                return null;
            Gson gson = new Gson();
            KeoBongDa model = gson.fromJson(document.toJson(), KeoBongDa.class);
            return model;
        } catch (Exception e) {
            RechargeDaoImpl.logger.error(e);
            return null;
        }
    }

    @Override
    public boolean insertKeoBongDa(KeoBongDa keoBongDa) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection(BongDaUtils.BONG_DA);
            Gson gson = new Gson();
            String json = gson.toJson(keoBongDa);
            Document doc = Document.parse(json);
            col.insertOne((Object) doc);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean updateKeoBongDa(KeoBongDa keoBongDa) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            Document conditions = new Document();
            conditions.put("Id", (Object) keoBongDa.Id);
            BasicDBObject updateFields = new BasicDBObject();
            updateFields.append("banThangDoiA", (Object) keoBongDa.banThangDoiA);
            updateFields.append("banThangDoiB", (Object) keoBongDa.banThangDoiB);
            updateFields.append("banThangHiep1DoiA", (Object) keoBongDa.banThangHiep1DoiA);
            updateFields.append("banThangHiep2DoiA", (Object) keoBongDa.banThangHiep2DoiA);
            updateFields.append("banThangHiep1DoiB", (Object) keoBongDa.banThangHiep1DoiB);
            updateFields.append("banThangHiep2DoiB", (Object) keoBongDa.banThangHiep2DoiB);
          ///  updateFields.append("banThangDoiB", (Object) keoBongDa.banThangDoiB);
            updateFields.append("tiLeDoiAChapCaTran", (Object) keoBongDa.tiLeDoiAChapCaTran);
            updateFields.append("tiLeDoiBChapCaTran", (Object) keoBongDa.tiLeDoiBChapCaTran);
            updateFields.append("tileDoiAChapTaiXiu", (Object) keoBongDa.tileDoiAChapTaiXiu);
            updateFields.append("tileDoiBChapTaiXiu", (Object) keoBongDa.tileDoiBChapTaiXiu);
            updateFields.append("tileDoiAChapHiep1", (Object) keoBongDa.tileDoiAChapHiep1);
            updateFields.append("tileDoiBChapHiep1", (Object) keoBongDa.tileDoiBChapHiep1);
            updateFields.append("tileDoiAChapHiep2", (Object) keoBongDa.tileDoiAChapHiep2);
            updateFields.append("tileDoiBChapHiep2", (Object) keoBongDa.tileDoiBChapHiep2);

            updateFields.append("tileAnDoiACaTran", (Object) keoBongDa.tileAnDoiACaTran);
            updateFields.append("tileAnDoiBCaTran", (Object) keoBongDa.tileAnDoiBCaTran);
            updateFields.append("tileAnDoiATaiXiu", (Object) keoBongDa.tileAnDoiATaiXiu);
            updateFields.append("tileAnDoiBTaiXiu", (Object) keoBongDa.tileAnDoiBTaiXiu);
            updateFields.append("tileAnDoiAHiep1", (Object) keoBongDa.tileAnDoiAHiep1);
            updateFields.append("tileAnDoiBHiep1", (Object) keoBongDa.tileAnDoiBHiep1);
            updateFields.append("tileAnDoiAHiep2", (Object) keoBongDa.tileAnDoiAHiep2);
            updateFields.append("tileAnDoiBHiep2", (Object) keoBongDa.tileAnDoiBHiep2);
            updateFields.append("thoiGianDa", (Object) keoBongDa.thoiGianDa);
            updateFields.append("status", (Object) keoBongDa.getStatus());
            updateFields.append("url", (Object) keoBongDa.url);
            updateFields.append("UpdatedAt", (Object) VinPlayUtils.getCurrentDateTime());
            updateFields.append("doiA", (Object) keoBongDa.doiA);
            updateFields.append("doiB", (Object) keoBongDa.doiB);
            //us
            db.getCollection(BongDaUtils.BONG_DA).updateOne(conditions, (Bson) new Document("$set", (Object) updateFields));
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    @Override
    public KeoBongDaResponse getListKeoBongDa(String session, int page, int maxItem, String fromTime, String endTime) {
        try {
            final ArrayList<KeoBongDa> records = new ArrayList<KeoBongDa>();
            final ArrayList<Long> num = new ArrayList<Long>();
            num.add(0, 0L);
            num.add(1, 0L);
            num.add(2, 0L);
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection(BongDaUtils.BONG_DA);
            page = (page - 1) < 0 ? 0 : (page - 1);
            int numStart = page * maxItem;
            int numEnd = maxItem;
            BasicDBObject objsort = new BasicDBObject();
            objsort.put("_id", -1);
            HashMap<String, Object> conditions = new HashMap<String, Object>();

            if (session != null) {
                if ("".equals(session)) {

                } else {
                    conditions.put("session", session);
                }
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
                    KeoBongDa model = gson.fromJson(document.toJson(), KeoBongDa.class);

                    records.add(model);
                }
            });

            KeoBongDaResponse res = new KeoBongDaResponse(true, "success");
            res.setListTrans(records);
            return res;


        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public KeoBongDaResponse getListKeoBongDaClient(String session) {
        try {
            final ArrayList<KeoBongDa> records = new ArrayList<KeoBongDa>();

            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection(BongDaUtils.BONG_DA);

            BasicDBObject objsort = new BasicDBObject();
            objsort.put("_id", -1);
            HashMap<String, Object> conditions = new HashMap<String, Object>();

            conditions.put("session", session);

            FindIterable iterable = col.find((Bson) new Document(conditions)).sort((Bson) objsort);
            iterable.forEach((Block) new Block<Document>() {

                public void apply(Document document) {

                    Gson gson = new Gson();
                    KeoBongDa model = gson.fromJson(document.toJson(), KeoBongDa.class);

                    records.add(model);
                }
            });

            KeoBongDaResponse res = new KeoBongDaResponse(true, "success");
            res.setListTrans(records);
            return res;


        } catch (Exception e) {
            return null;
        }
    }


    @Override
    public boolean deletekeoBongDa(String id) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection(BongDaUtils.BONG_DA);
            Document conditions = new Document();
            conditions.put("Id", (Object) id);
            BasicDBObject updateFields = new BasicDBObject();
            Document document = (Document) db.getCollection(BongDaUtils.BONG_DA).find((Bson) conditions).first();
            if (document == null)
                return false;
            col.deleteOne((Bson) document);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
