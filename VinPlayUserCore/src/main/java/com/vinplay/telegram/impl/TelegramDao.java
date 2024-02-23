package com.vinplay.telegram.impl;

import com.google.gson.Gson;
import com.mongodb.client.MongoDatabase;
import com.vinplay.dichvuthe.dao.impl.RechargeDaoImpl;
import com.vinplay.telegram.AlertTeleGramDaily;
import com.vinplay.telegram.TeleGramDao;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import org.bson.Document;
import org.bson.conversions.Bson;

public class TelegramDao implements TeleGramDao {
    @Override
    public AlertTeleGramDaily findTransaction(String nickName) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            Document conditions = new Document();

            conditions.put("nickname", nickName);

            Document document = (Document) db.getCollection("bot_daily").find((Bson) conditions).first();
            if (document == null)
                return null;
            Gson gson = new Gson();
            AlertTeleGramDaily model = gson.fromJson(document.toJson(), AlertTeleGramDaily.class);

            return model;
        } catch (Exception e) {
            RechargeDaoImpl.logger.error(e);
            return null;
        }

    }

    public Document getInfoBotNapRut() {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        Document document = db.getCollection("link_social").find().first();
        if (document == null)
            return null;

        return document;

    }
}
