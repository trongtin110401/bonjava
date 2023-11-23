package com.vinplay.api.processors.accnhatvip;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import org.bson.Document;

public class SaveAccount {
    public void SaveNick(account acc){
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("accnhatvip");
            Document doc = new Document();
            doc.append("username", (Object)acc.getUsername());
            doc.append("password", (Object)acc.getPassword());
            doc.append("MaDaily", (Object)acc.getCodedaily());
            doc.append("phone", (Object)acc.getPhone());
            doc.append("dangky", (Object)acc.isDangky());
            doc.append("timelog", (Object)acc.getTimelog());
            doc.append("nickname", (Object)acc.getNickname());
            doc.append("tien", (Object)acc.getTien());
            col.insertOne((Object)doc);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
