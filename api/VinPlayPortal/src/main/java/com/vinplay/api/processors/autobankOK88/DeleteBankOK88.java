package com.vinplay.api.processors.autobankOK88;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

public class DeleteBankOK88 implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        String bank = request.getParameter("bank");
        bank = bank.toUpperCase();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("CodePayOK88Bank");
        BasicDBObject objsort = new BasicDBObject();
        objsort.put("_id", -1);
        HashMap<String, Object> conditions = new HashMap<>();
        conditions.put("bank",(Object)bank);
        FindIterable<Document> iterable = col.find((Bson) new Document(conditions)).sort((Bson) objsort);
        Document document = iterable != null ? iterable.first() : null;
        if (document.size() > 0) {
            col.deleteOne((Bson) document);
            return "{\"trangthai\":\"ok\"}";
        }else {
            return "{\"trangthai\":\"That bai\"}";
        }
    }
}
