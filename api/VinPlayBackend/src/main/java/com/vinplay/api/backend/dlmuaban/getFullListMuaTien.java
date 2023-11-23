package com.vinplay.api.backend.dlmuaban;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.api.backend.processors.accsunwin.account;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

public class getFullListMuaTien implements BaseProcessor<HttpServletRequest, String> {
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
        ArrayList<yeucaumuaentity> listAcc = new ArrayList<>();

        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("DLYeuCauMua");
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
                String trainid = document.getString((Object)"trainid");
                Long dlid = document.getLong((Object)"dlid");
                String nickname = document.getString((Object)"nickname");
                Long tien = document.getLong((Object)"tien");
                String trangthai = document.getString((Object)"trangthai");
                int sttcode = document.getInteger((Object)"sttcode");
                String des = document.getString((Object)"des");
                String timelog = document.getString((Object)"timelog");
                String note = document.getString("note");
                ObjectId id = document.getObjectId("_id");
                yeucaumuaentity yeu = new yeucaumuaentity(id.toString(), trainid, dlid, nickname, tien, trangthai, sttcode, des, timelog, note);
                listAcc.add(yeu);
            }
        });

        Gson gson = new Gson();
        return gson.toJson(listAcc);
    }
}
