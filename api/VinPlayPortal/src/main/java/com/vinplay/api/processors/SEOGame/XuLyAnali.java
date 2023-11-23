package com.vinplay.api.processors.SEOGame;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.bongda.entities.KeoBongDa;
import com.vinplay.bongda.entities.UserBetBongDa;
import com.vinplay.bongda.entities.UserBetBongDaModel;
import com.vinplay.lognaprut.entities.HistoryTransModel;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.HashMap;

public class XuLyAnali {
    public void insertData(String utm_dl, String utm_source, String utm_medium, String utm_campaign, String create_time, String ip, String device, String note1, String note2, String note3){
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("seo_game");
            Document doc = new Document();
            doc.append("utm_dl", utm_dl);
            doc.append("utm_source", utm_source);
            doc.append("utm_medium", utm_medium);
            doc.append("utm_campaign", utm_campaign);
            doc.append("create_time", create_time);
            doc.append("ip", ip);
            doc.append("device", device);
            doc.append("Note1", note1);
            doc.append("Note2", note2);
            doc.append("Note3", note3);
            col.insertOne((Object)doc);

        } catch (Exception e) {
            System.out.println("loi ne a oi: "+e);
        }
    }

    public void insertKey(String code_dl, String key1, String key2, String key3, String key4, String key5, String key6, String key7, String key8, String key9, String key10 ){
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("seo_game_key");
            Document doc = new Document();
            doc.append("code_dl", code_dl);
            doc.append("key1", key1);
            doc.append("key2", key2);
            doc.append("key3", key3);
            doc.append("key4", key4);
            doc.append("key5", key5);
            doc.append("key6", key6);
            doc.append("key7", key7);
            doc.append("key8", key8);
            doc.append("key9", key9);
            doc.append("key10", key10);
            col.insertOne((Object)doc);

        } catch (Exception e) {
            System.out.println("loi ne a oi: "+e);
        }
    }

    public void updateKey(String code_dl,String key1, String key2, String key3, String key4, String key5, String key6, String key7, String key8, String key9, String key10) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("seo_game_key");
            Document doc = new Document();
            doc.append("key1", key1);
            doc.append("key2", key2);
            doc.append("key3", key3);
            doc.append("key4", key4);
            doc.append("key5", key5);
            doc.append("key6", key6);
            doc.append("key7", key7);
            doc.append("key8", key8);
            doc.append("key9", key9);
            doc.append("key10", key10);
            col.updateOne((Bson) new Document("code_dl", code_dl), (Bson) new Document("$set", (Object) doc));

        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    public ArrayList<KeyEnity> getListKey(int page, int maxItem){
        final ArrayList<KeyEnity> records = new ArrayList<KeyEnity>();
        try {

            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("seo_game_key");
            page = (page - 1) < 0 ? 0 : (page - 1);
            int numStart = page * maxItem;
            int numEnd = maxItem;
            BasicDBObject objsort = new BasicDBObject();
            objsort.put("_id", -1);
            FindIterable iterable = col.find().sort( objsort).skip(numStart).limit(maxItem);
            iterable.forEach((Block<Document>) document -> {
                Gson gson = new Gson();
                KeyEnity model = gson.fromJson(document.toJson(), KeyEnity.class);
                model.setCode_dl(document.getString("code_dl"));
                model.setKey1(document.getString("key1"));
                model.setKey2(document.getString("key2"));
                model.setKey3(document.getString("key3"));
                model.setKey4(document.getString("key4"));
                model.setKey5(document.getString("key5"));
                model.setKey6(document.getString("key6"));
                model.setKey7(document.getString("key7"));
                model.setKey8(document.getString("key8"));
                model.setKey9(document.getString("key9"));
                model.setKey10(document.getString("key10"));
                records.add(model);
            });
            return records;

        }catch (Exception e) {
            e.printStackTrace();
        }
        return records;
    }

    public ArrayList<AnaliEntity> GetThongKE(String fromTime, String endTime){
        final ArrayList<AnaliEntity> records = new ArrayList<AnaliEntity>();
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("seo_game");
            HashMap<String, Object> conditions = new HashMap<String, Object>();
            BasicDBObject objsort = new BasicDBObject();
            objsort.put("_id", -1);
            BasicDBObject obj = new BasicDBObject();
            obj.put("$gte", (Object) fromTime);
            obj.put("$lte", (Object) endTime);
            conditions.put("create_time", (Object) obj);
            FindIterable iterable = col.find((Bson) new Document(conditions)).sort((Bson) objsort);
            iterable.forEach((Block) new Block<Document>() {

                public void apply(Document document) {
                    Gson gson = new Gson();
                    AnaliEntity model = gson.fromJson(document.toJson(), AnaliEntity.class);
                    model.setCode_dl(document.getString("utm_dl"));
                    model.setSource(document.getString("utm_source"));
                    model.setAction(document.getString("utm_medium"));
                    model.setChiendich(document.getString("utm_campaign"));
                    model.setIp(document.getString("ip"));
                    model.setDevice(document.getString("device"));
                    records.add(model);
                }
            });
            return records;

        }catch (Exception e) {
            e.printStackTrace();
        }
        return records;
    }

}
