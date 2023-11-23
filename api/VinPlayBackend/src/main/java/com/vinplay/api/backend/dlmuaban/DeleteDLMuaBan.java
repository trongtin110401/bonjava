package com.vinplay.api.backend.dlmuaban;

import com.mongodb.BasicDBObject;
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
import java.util.HashMap;

public class DeleteDLMuaBan implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        String id = request.getParameter("id");
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("dlmuaban");
        BasicDBObject objsort = new BasicDBObject();
        objsort.put("_id", -1);
        HashMap<String, Object> conditions = new HashMap<>();

        conditions.put("_id",new ObjectId(id));
        FindIterable<Document> iterable = col.find((Bson) new Document(conditions)).sort((Bson) objsort);
        Document document = iterable != null ? iterable.first() : null;
        if (document.size() > 0) {
            col.deleteOne((Bson) document);
            return "{\"errorcode\":\"200\",\"des\":\"Thanh Cong\"}";
        }else {
            return "{\"errorcode\":\"500\",\"des\":\"That Bai\"}";
        }
    }
}