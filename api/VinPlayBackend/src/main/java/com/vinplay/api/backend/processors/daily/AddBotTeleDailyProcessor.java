package com.vinplay.api.backend.processors.daily;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.daily.entities.ListBotTeleDailyResponse;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.servlet.http.HttpServletRequest;


public class AddBotTeleDailyProcessor implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger("backend");

    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = param.get();
        ListBotTeleDailyResponse res = new ListBotTeleDailyResponse(false, "1001");
        try {
            String nickname = request.getParameter("nn");
            String idTele = request.getParameter("id");
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("bot_daily");
            Document doc = new Document();
            if (checkExisted(nickname, col)) {
                doc.append("teleId", idTele);
                col.updateOne((Bson) new Document("nickname", nickname), (Bson) new Document("$set", (Object) doc));
                res.setSuccess(true);
                res.setErrorCode("1");
            } else {
                doc.append("nickname", nickname);
                doc.append("teleId", idTele);
                col.insertOne(doc);
                res.setSuccess(true);
                res.setErrorCode("0");
            }
            return res.toJson();
        } catch (Exception e) {
            //todo:2- phải xử lý res khi exception
            logger.error(e.getStackTrace());
        }
        return res.toJson();
    }

    private Boolean checkExisted(String nickname, MongoCollection col) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        Document conditions = new Document();
        conditions.put("nickname", nickname);
        long totalRows = db.getCollection("bot_daily").count(conditions);
        if (totalRows > 0)
            return Boolean.TRUE;
        return Boolean.FALSE;
    }


}
