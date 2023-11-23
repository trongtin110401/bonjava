package com.vinplay.api.backend.processors.accv28;

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

public class ShowAccV28Process implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        String pagex = request.getParameter("page");
        String maxItemx = request.getParameter("maxitem");
        String timeStart = request.getParameter("timestart");
        String timeEnd = request.getParameter("timeend");
        int page = Integer.parseInt(pagex);
        int maxItem = Integer.parseInt(maxItemx);
        int numStart = page * maxItem;
        int numEnd = maxItem;
        ArrayList<account> listAcc = new ArrayList<>();

        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("accv8");
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        BasicDBObject objsort = new BasicDBObject();
        objsort.put("_id", -1);
        BasicDBObject obj = new BasicDBObject();
        obj.put("$gte", (Object) timeStart);
        obj.put("$lte", (Object) timeEnd);
        conditions.put("timelog", (Object) obj);
        FindIterable iterable = col.find((Bson) new Document(conditions)).sort((Bson) objsort).skip(numStart).limit(maxItem);
        iterable.forEach((Block)new Block<Document>(){

            public void apply(Document document) {
                String username = document.getString((Object)"username");
                String pwd = document.getString((Object)"password");
                String codedl = document.getString((Object)"MaDaily");
                String phone = document.getString((Object)"phone");
                boolean dangky = document.getBoolean((Object)"dangky");
                String timelog = document.getString((Object)"timelog");
                String nickname = document.getString((Object)"nickname");
                String tien = document.getString((Object)"tien");
//                String id = document.getString("_id");
                ObjectId id = document.getObjectId("_id");
                account acc = new account(username, pwd, codedl, phone, dangky, timelog, nickname, tien,id.toString());
                listAcc.add(acc);

            }
        });

        Gson gson = new Gson();
        return gson.toJson(listAcc);
    }
}

