package com.vinplay.api.dlmuaban;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

public class getFullBankDLmuaban implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();

        ArrayList<bankdlEntity> listDL = new ArrayList<>();

        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("bankdailymuaban");
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        BasicDBObject objsort = new BasicDBObject();
        objsort.put("_id", -1);
        FindIterable iterable = col.find((Bson) new Document(conditions)).sort((Bson) objsort);
        iterable.forEach((Block)new Block<Document>(){

            public void apply(Document document) {
                Long dl_id = document.getLong((Object)"dlid");
                String bank = document.getString("bank");
                String banknum = document.getString("banknum");
                String bankname = document.getString("bankname");
                String chinhanh = document.getString("chinhanh");
                ObjectId id = document.getObjectId("_id");
                bankdlEntity dlmb = new bankdlEntity(id.toString(), dl_id, bank, bankname, banknum, chinhanh);
                listDL.add(dlmb);
            }
        });
        Gson gson = new Gson();
        return gson.toJson(listDL);
    }
}
