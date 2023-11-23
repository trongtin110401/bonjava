package com.vinplay.api.backend.dlmuaban;

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

public class getFullInfoDL implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();

        ArrayList<dlmuabanEntity> listDL = new ArrayList<>();

        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("dlmuaban");
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        BasicDBObject objsort = new BasicDBObject();
        objsort.put("_id", -1);
        FindIterable iterable = col.find((Bson) new Document(conditions)).sort((Bson) objsort);
        iterable.forEach((Block)new Block<Document>(){

            public void apply(Document document) {
                Long dl_id = document.getLong((Object)"dlid");
                String fullname = document.getString((Object)"fullname");
                String username = document.getString((Object)"username");
                String nickname = document.getString((Object)"nickname");
                String phone = document.getString((Object)"sdt");
                String khuvuc = document.getString((Object)"khuvuc");
                String tele = document.getString((Object)"tele");
                String fb = document.getString((Object)"fb");
                String zalo = document.getString("zalo");
                String bank = document.getString("bank");
                String banknum = document.getString("banknum");
                String bankname = document.getString("bankname");
                String note = document.getString("note");
                ObjectId id = document.getObjectId("_id");
                dlmuabanEntity dlmb = new dlmuabanEntity(id.toString(), dl_id, fullname, username, nickname, phone, khuvuc,tele, fb, zalo,bank, banknum, bankname,note);
                listDL.add(dlmb);
            }
        });
        Gson gson = new Gson();
        return gson.toJson(listDL);
    }
}
