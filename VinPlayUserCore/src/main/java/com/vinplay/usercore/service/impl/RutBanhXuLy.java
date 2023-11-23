package com.vinplay.usercore.service.impl;

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
}
