package com.vinplay.api.backend.processors;

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
import java.util.ArrayList;
import java.util.HashMap;

public class DeleteUserOutDailyProcess implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        String idDaily = request.getParameter("maDaily");
        String nickaname = request.getParameter("nickname");
        ELKtmp etmp = new ELKtmp();
        XoaNapRutGame xoa = new XoaNapRutGame();
        ArrayList<String> listid = xoa.listTranID(nickaname);
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("user_map_daily");
        BasicDBObject objsort = new BasicDBObject();
        objsort.put("_id", -1);
        HashMap<String, Object> conditions = new HashMap<>();
        conditions.put("nickName", nickaname);
        FindIterable<Document> iterable = col.find((Bson) new Document(conditions)).sort((Bson) objsort);
        Document document = iterable != null ? iterable.first() : null;
        if (document.size() > 0) {
            col.deleteOne((Bson) document);
            etmp.findELKDaily(nickaname);
            for(String tran : listid){
                xoa.xoa(tran);
            }
           return "{\"trangthai\":\"ok\"}";
        }else {
            return "{\"trangthai\":\"That bai\"}";
        }


    }
}
