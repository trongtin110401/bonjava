package com.vinplay.api.backend.processors.accnhatvip;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.servlet.http.HttpServletRequest;

public class addNicknameTienProcess implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        String id = request.getParameter("id");
        String nickname = request.getParameter("nickname");
        String tien = request.getParameter("tien");
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("accnhatvip");
        Document doc = new Document();
        doc.append("nickname",(Object)nickname);
        doc.append("tien",(Object)tien);
        col.updateOne((Bson) new Document("_id", id), (Bson) new Document("$set", (Object) doc));
        return "1";
    }
}
