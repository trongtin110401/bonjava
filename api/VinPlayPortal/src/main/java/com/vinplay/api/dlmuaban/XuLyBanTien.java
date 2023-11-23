package com.vinplay.api.dlmuaban;

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

public class XuLyBanTien {

    public void InsertBan(BanTienEnity ban){
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("BanTienChoDL");
            Document doc = new Document();
            doc.append("trainID", (Object)ban.getTrainID());
            doc.append("bank_nhan", (Object)ban.getBank_nhan());
            doc.append("stk_nhan", (Object)ban.getStk_nhan());
            doc.append("name_nhan", (Object)ban.getName_nhan());
            doc.append("tien", (Object)ban.getTien());
            doc.append("trangthai", (Object)ban.getTrangthai());
            doc.append("sttcode", (Object)ban.getSttcode());
            doc.append("nickname", (Object)ban.getNickname());
            doc.append("nickname_dl", (Object)ban.getNickname_dl());
            doc.append("dlid", (Object)ban.getDlid());
            doc.append("timelog", (Object)ban.getTimelog());
            doc.append("note", (Object)ban.getNote());
            col.insertOne((Object)doc);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BanTienEnity FindDonMuaTien(String trainsid){
        try {
            ArrayList<BanTienEnity> list_nick = new ArrayList<>();
            HashMap<String, Object> conditions = new HashMap<String, Object>();
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("BanTienChoDL");
            conditions.put("trainID", trainsid);
            FindIterable iterable = col.find((Bson) new Document(conditions));
            iterable.forEach((Block) new Block<Document>() {

                public void apply(Document document) {
                    ObjectId id = document.getObjectId("_id");
                    String trainID = document.getString("trainID");
                    String bank_nhan = document.getString("bank_nhan");
                    String stk_nhan = document.getString("stk_nhan");
                    String name_nhan = document.getString("name_nhan");
                    int tien = document.getInteger("tien");
                    String trangthai = document.getString("trangthai");
                    String sttcode = document.getString("sttcode");
                    String nickname = document.getString("nickname");
                    String nickname_dl = document.getString("nickname_dl");
                    Long dlid = document.getLong("dlid");
                    String timelog = document.getString("timelog");
                    String note = document.getString("note");

                    BanTienEnity ban = new BanTienEnity(id.toString(), trainID, bank_nhan, stk_nhan, name_nhan, tien, trangthai, sttcode, nickname, nickname_dl,dlid, timelog, note);
                    list_nick.add(ban);
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

}
