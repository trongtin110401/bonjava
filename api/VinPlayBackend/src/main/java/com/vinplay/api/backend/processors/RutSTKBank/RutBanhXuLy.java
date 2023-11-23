package com.vinplay.api.backend.processors.RutSTKBank;

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

public class RutBanhXuLy {
    public synchronized boolean InsertRutBankSTK(InfoBankEnity info){
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("RutBankSTK");
        Document doc = new Document();
        doc.append("nickname", (Object)info.getNickname());
        doc.append("bankname", (Object)info.getBankname());
        doc.append("banknumber", (Object)info.getBanknumber());
        doc.append("bankbran", (Object)info.getBankbran());
        doc.append("timelog", (Object)info.getTimelog());
        col.insertOne((Object)doc);
        return true;
    }

    public InfoBankEnity GetRutBankSTK(String nickname){
        ArrayList<InfoBankEnity> list_Info = new ArrayList<>();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("RutBankSTK");
        conditions.put("nickname", nickname);
        FindIterable iterable = col.find((Bson) new Document(conditions));
        iterable.forEach((Block) new Block<Document>() {

            public void apply(Document document) {
                String bankname = document.getString((Object) "bankname");
                String banknumber = document.getString((Object) "banknumber");
                String bankbran = document.getString((Object) "bankbran");
                String timelog = document.getString((Object) "timelog");
                InfoBankEnity info = new InfoBankEnity(nickname,bankname,banknumber, bankbran, timelog);
                list_Info.add(info);
            }
        });
        if(list_Info.size() != 0){
            return list_Info.get(0);
        }else{
            return null;
        }
    }

    public boolean deleteSTK(String nickname){
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("RutBankSTK");
        BasicDBObject objsort = new BasicDBObject();
        objsort.put("_id", -1);
        HashMap<String, Object> conditions = new HashMap<>();
        conditions.put("nickname",(Object)nickname);
        FindIterable<Document> iterable = col.find((Bson) new Document(conditions)).sort((Bson) objsort);
        Document document = iterable != null ? iterable.first() : null;
        if(document == null){
            return false;
        }else{
            if (document.size() > 0) {
                col.deleteOne((Bson) document);
                return true;
            }else {
                return false;
            }
        }

    }
}
