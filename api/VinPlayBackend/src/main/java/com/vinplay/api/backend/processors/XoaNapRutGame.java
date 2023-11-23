package com.vinplay.api.backend.processors;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.HashMap;

public class XoaNapRutGame {
    public void xoa(String tranID){
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("NapRutGame");
        BasicDBObject objsort = new BasicDBObject();
        objsort.put("_id", -1);
        HashMap<String, Object> conditions = new HashMap<>();
        conditions.put("TranID", tranID);
        FindIterable<Document> iterable = col.find((Bson) new Document(conditions)).sort((Bson) objsort);
        Document document = iterable != null ? iterable.first() : null;
        if (document.size() > 0) {
            col.deleteOne((Bson) document);
        }
    }

    public ArrayList<String> listTranID(String nickname){
        ArrayList<String> listid = new ArrayList<>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("NapRutGame");
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        conditions.put("nick_name", nickname);
        FindIterable iterable = col.find((Bson) new Document(conditions));
        iterable.forEach((Block) new Block<Document>() {

            public void apply(Document document) {
                String tranid = document.getString((Object) "TranID");
                listid.add(tranid);
            }
        });
        return listid;
    }
}
