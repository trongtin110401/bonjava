package com.vinplay.api.backend.dlmuaban;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.HashMap;

public class MuaBanTien {

    public void Mua(yeucaumuaentity yeucau){
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("DLYeuCauMua");
            Document doc = new Document();
            doc.append("trainid", (Object)yeucau.getTrain_id());
            doc.append("dlid", (Object)yeucau.getDlid());
            doc.append("nickname", (Object)yeucau.getNickname());
            doc.append("tien", (Object)yeucau.getTien());
            doc.append("trangthai", (Object)yeucau.getTrangthai());
            doc.append("sttcode", (Object)yeucau.getSttcode());
            doc.append("des", (Object)yeucau.getDes());
            doc.append("timelog", (Object)yeucau.getTimelog());
            doc.append("note", (Object)yeucau.getNote());
            col.insertOne((Object)doc);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Ban(yeucaumuaentity yeucau){
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("DLYeuCauBan");
            Document doc = new Document();
            doc.append("trainid", (Object)yeucau.getTrain_id());
            doc.append("dlid", (Object)yeucau.getDlid());
            doc.append("nickname", (Object)yeucau.getNickname());
            doc.append("tien", (Object)yeucau.getTien());
            doc.append("trangthai", (Object)yeucau.getTrangthai());
            doc.append("sttcode", (Object)yeucau.getSttcode());
            doc.append("des", (Object)yeucau.getDes());
            doc.append("timelog", (Object)yeucau.getTimelog());
            doc.append("note", (Object)yeucau.getNote());
            col.insertOne((Object)doc);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void DuyetMua(String trainid){
        try {
            String trangthai = "Thành Công";
            int sttcode = 100;
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("DLYeuCauMua");
            Document doc = new Document();
            doc.append("trangthai", (Object)trangthai);
            doc.append("sttcode", (Object)sttcode);
            col.updateOne((Bson) new Document("trainid", trainid), (Bson) new Document("$set", (Object) doc));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void TuChoiMua(String trainid){
        try {
            String trangthai = "Thất Bại";
            int sttcode = 2;
            String des = "Từ Chối Đơn";
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("DLYeuCauMua");
            Document doc = new Document();
            doc.append("trangthai", (Object)trangthai);
            doc.append("sttcode", (Object)sttcode);
            doc.append("des", des);
            col.updateOne((Bson) new Document("trainid", trainid), (Bson) new Document("$set", (Object) doc));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    public yeucaumuaentity FindDonMuaTien(String trainsid){
        try {
            ArrayList<yeucaumuaentity> list_nick = new ArrayList<>();
            HashMap<String, Object> conditions = new HashMap<String, Object>();
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("DLYeuCauMua");
            conditions.put("trainid", trainsid);
            FindIterable iterable = col.find((Bson) new Document(conditions));
            iterable.forEach((Block) new Block<Document>() {

                public void apply(Document document) {
                    ObjectId id = document.getObjectId("_id");
                    String train_id = document.getString("trainid");
                    Long dlid = document.getLong("dlid");
                    String nickname = document.getString("nickname");
                    Long tien = document.getLong("tien");
                    String trangthai = document.getString("trangthai");
                    int sttcode = document.getInteger("sttcode");
                    String des = document.getString("des");
                    String timelog = document.getString("timelog");
                    String note = document.getString("note");
                    yeucaumuaentity yeu = new yeucaumuaentity(id.toString(), train_id, dlid,nickname, tien, trangthai, sttcode,des, timelog, note);
                    list_nick.add(yeu);
                }
            });
            if(list_nick.size() == 0){
                return null;
            }else{
                return list_nick.get(0);
            }

        }catch (Exception e){
            return null;
        }
    }

    public void TuChoiBan(String trainid){
        try {
            String trangthai = "Thất Bại";
            int sttcode = 2;
            String des = "Từ Chối Đơn";
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("DLYeuCauBan");
            Document doc = new Document();
            doc.append("trangthai", (Object)trangthai);
            doc.append("sttcode", (Object)sttcode);
            doc.append("des", des);
            col.updateOne((Bson) new Document("trainid", trainid), (Bson) new Document("$set", (Object) doc));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void DuyetBan(String trainid){
        try {
            String trangthai = "Thành Công";
            int sttcode = 100;
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("DLYeuCauBan");
            Document doc = new Document();
            doc.append("trangthai", (Object)trangthai);
            doc.append("sttcode", (Object)sttcode);
            col.updateOne((Bson) new Document("trainid", trainid), (Bson) new Document("$set", (Object) doc));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}
