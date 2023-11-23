package com.vinplay.api.processors.autobankOK88;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

public class UpdateBankOK88 implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        String bank = request.getParameter("bank");
        String stk = request.getParameter("stk");
        String name = request.getParameter("name");
        String chinhanh = request.getParameter("chinhanh");

        chinhanh = chinhanh.toUpperCase();
        bank = bank.toUpperCase();
        name = name.toUpperCase();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("CodePayOK88");
        Document doc = new Document();
        doc.append("stk", stk);
        doc.append("name", name);
        doc.append("chinhanh",chinhanh);
        col.updateOne((Bson) new Document("bank", bank), (Bson) new Document("$set", (Object) doc));
        return "{\"trangthai\":\"ok\"}";

    }
}
