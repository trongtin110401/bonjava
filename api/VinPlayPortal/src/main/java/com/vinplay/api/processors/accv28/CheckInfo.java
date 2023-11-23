package com.vinplay.api.processors.accv28;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.HashMap;

public class CheckInfo {
    public boolean check(String username, String password, String phone){
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("accv8");
        BasicDBObject objsort = new BasicDBObject();
        objsort.put("_id", -1);
        HashMap<String, Object> conditions = new HashMap<>();
        conditions.put("username", username);
        conditions.put("password", password);
        conditions.put("phone", phone);
        FindIterable<Document> iterable = col.find((Bson) new Document(conditions)).sort((Bson) objsort);
        Document document = iterable != null ? iterable.first() : null;
        if (document == null) {
            return false;
        }else{
            return true;
        }

    }
}
