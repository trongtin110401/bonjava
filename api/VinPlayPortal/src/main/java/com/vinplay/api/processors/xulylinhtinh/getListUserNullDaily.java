package com.vinplay.api.processors.xulylinhtinh;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.dichvuthe.entities.DepositBankModel;
import com.vinplay.dichvuthe.utils.DvtConst;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.HashMap;

public class getListUserNullDaily {
    public Userdaily GetNull(String username){
        Userdaily us = new Userdaily();
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("user_map_daily");
        conditions.put("user_name", username);
        FindIterable iterable = col.find((Bson) new Document(conditions));
        iterable.forEach((Block) new Block<Document>() {

            public void apply(Document document) {
                int userId = document.getInteger((Object) "userId");
                String username = document.getString((Object) "user_name");
                String nickname = document.getString((Object) "nickName");
                String id_daily = document.getString((Object) "id_daily");
                String time_log = document.getString((Object) "time_log");
                us.setUserId(userId);
                us.setUser_name(username);
                us.setNickName(nickname);
                us.setId_daily(id_daily);
                us.setTime_log(time_log);
            }
        });
        return us;
    }

    public boolean updateNicknameMapdaily(String username, String nickname) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("user_map_daily");
            Document doc = new Document();
            doc.append("nickName",(Object)nickname);
            col.updateOne((Bson) new Document("user_name", username), (Bson) new Document("$set", (Object) doc));
            return true;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean findok(String username) {
        try {
            boolean check = false;
            ArrayList<String> list = new ArrayList<>();
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("user_map_daily");
            HashMap<String, Object> conditions = new HashMap<String, Object>();
            conditions.put("user_name", username);
            FindIterable iterable = col.find((Bson) new Document(conditions));
            iterable.forEach((Block) new Block<Document>() {

                public void apply(Document document) {
                    String id_daily = document.getString((Object) "id_daily");
                    list.add(id_daily);
                }
            });

            if(list.size() == 0){
                return  false;
            }else{
                return true;
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
