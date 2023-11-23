package com.vinplay.api.backend.dlmuaban;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

public class SaveDLMuaBan {
    private static final Logger logger = Logger.getLogger((String)"backend");
    public void SaveDL(dlmuabanEntity DL){
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("dlmuaban");
            Document doc = new Document();
            doc.append("dlid", (Object)DL.getDl_id());
            doc.append("fullname", (Object)DL.getFullname());
            doc.append("username", (Object)DL.getUsername());
            doc.append("nickname", (Object)DL.getNickname());
            doc.append("sdt", (Object)DL.getPhone());
            doc.append("khuvuc", (Object)DL.getKhuvuc());
            doc.append("tele", (Object)DL.getTelegram());
            doc.append("fb", (Object)DL.getFacebook());
            doc.append("zalo", (Object)DL.getZalo());
            doc.append("bank", (Object)DL.getBank());
            doc.append("banknum", (Object)DL.getBanknumber());
            doc.append("bankname", (Object)DL.getBankname());
            doc.append("note", (Object)DL.getNote());
            col.insertOne((Object)doc);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateDaiLy(dlmuabanEntity DL) {
        try {
            ObjectId id = new ObjectId( DL.getId());
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("dlmuaban");
            Document doc = new Document();
            doc.append("dlid", (Object)DL.getDl_id());
            doc.append("fullname", (Object)DL.getFullname());
            doc.append("username", (Object)DL.getUsername());
            doc.append("nickname", (Object)DL.getNickname());
            doc.append("sdt", (Object)DL.getPhone());
            doc.append("khuvuc", (Object)DL.getKhuvuc());
            doc.append("tele", (Object)DL.getTelegram());
            doc.append("fb", (Object)DL.getFacebook());
            doc.append("zalo", (Object)DL.getZalo());
            doc.append("bank", (Object)DL.getBank());
            doc.append("banknum", (Object)DL.getBanknumber());
            doc.append("bankname", (Object)DL.getBankname());
            doc.append("note", (Object)DL.getNote());
            col.updateOne((Bson) new Document("_id", id), (Bson) new Document("$set", (Object) doc));
        }catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(),  e);
        }

    }

    public void SaveBankDL(bankdlEntity DL){
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("bankdailymuaban");
            Document doc = new Document();
            doc.append("dlid", (Object)DL.getDlid());
            doc.append("bank", (Object)DL.getBank());
            doc.append("banknum", (Object)DL.getBanknum());
            doc.append("bankname", (Object)DL.getBankname());
            doc.append("chinhanh", (Object)DL.getChinhanh());
            col.insertOne((Object)doc);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void UpdateBankDL(bankdlEntity DL){
        try {
            ObjectId id = new ObjectId( DL.getId());
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("bankdailymuaban");
            Document doc = new Document();
            doc.append("dlid", (Object)DL.getDlid());
            doc.append("bank", (Object)DL.getBank());
            doc.append("banknum", (Object)DL.getBanknum());
            doc.append("bankname", (Object)DL.getBankname());
            doc.append("chinhanh", (Object)DL.getChinhanh());
            col.updateOne((Bson) new Document("_id", id), (Bson) new Document("$set", (Object) doc));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
