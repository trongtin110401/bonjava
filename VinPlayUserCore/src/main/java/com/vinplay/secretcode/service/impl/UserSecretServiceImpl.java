package com.vinplay.secretcode.service.impl;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.bongda.entities.KeoBongDa;
import com.vinplay.bongda.utils.BongDaUtils;
import com.vinplay.dichvuthe.dao.impl.RechargeDaoImpl;
import com.vinplay.secretcode.entity.UserSecretEntity;
import com.vinplay.secretcode.service.IUserSecretService;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.bson.Document;
import org.bson.conversions.Bson;

public class UserSecretServiceImpl  implements IUserSecretService {
    private static final String USER_SECRET = "user_secret_code";

    @Override
    public UserSecretEntity findCode(String username) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            Document conditions = new Document();

            conditions.put("username", (Object) username);
            Document document = (Document) db.getCollection(USER_SECRET).find((Bson) conditions).first();
            if (document == null)
                return null;
            Gson gson = new Gson();
            UserSecretEntity model = gson.fromJson(document.toJson(), UserSecretEntity.class);
            return model;
        } catch (Exception e) {
            RechargeDaoImpl.logger.error(e);
            return null;
        }

    }

    @Override
    public boolean insertUserSecret(UserSecretEntity userSecret) {
        try {
            userSecret.setId(String.valueOf(VinPlayUtils.generateTransId()));
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection(USER_SECRET);
            Gson gson = new Gson();
            String json = gson.toJson(userSecret);
            Document doc = Document.parse(json);
            col.insertOne((Object) doc);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    @Override
    public boolean updateUserSecret(UserSecretEntity userSecret) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        Document conditions = new Document();
        conditions.put("username", (Object) userSecret.username);
        conditions.put("code", (Object) userSecret.code);
        BasicDBObject updateFields = new BasicDBObject();
        updateFields.put("code",userSecret.code);
        db.getCollection(USER_SECRET).updateOne(conditions, (Bson) new Document("$set", (Object) updateFields));
        return false;
    }
}
