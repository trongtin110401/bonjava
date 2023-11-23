package com.vinplay.api.backend.processors.AutoBankOK88;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import org.bson.Document;

import javax.servlet.http.HttpServletRequest;

public class InsertBankOK88 implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        String bank = request.getParameter("bank");
        String stk = request.getParameter("stk");
        String name = request.getParameter("name");
        String chinhanh = request.getParameter("chinhanh");

        bank = bank.toUpperCase();
        stk = stk.toUpperCase();
        name = name.toUpperCase();
        chinhanh = chinhanh.toUpperCase();

        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("CodePayOK88Bank");
            Document doc = new Document();
            doc.append("bank", (Object)bank);
            doc.append("stk", (Object)stk);
            doc.append("name", (Object)name);
            doc.append("chinhanh", (Object)chinhanh);
            col.insertOne((Object)doc);
            return "{\"trangthai\":\"ok\"}";
        }catch (Exception e) {
            e.printStackTrace();
            return "{\"trangthai\":\"That bai\"}";
        }
    }
}
