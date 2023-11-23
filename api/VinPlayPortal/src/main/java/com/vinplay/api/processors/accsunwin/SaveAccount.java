package com.vinplay.api.processors.accsunwin;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.bson.Document;
import org.bson.conversions.Bson;

public class SaveAccount {
    public void SaveNick(account acc){
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("accsunwin2");
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
