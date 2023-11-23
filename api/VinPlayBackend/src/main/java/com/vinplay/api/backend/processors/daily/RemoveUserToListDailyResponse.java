package com.vinplay.api.backend.processors.daily;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.daily.entities.AddUserToListDailyResponse;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

public class RemoveUserToListDailyResponse implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger("backend");

    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = param.get();
        AddUserToListDailyResponse res = new AddUserToListDailyResponse(false, "1001");
        try {
            String codeDaily = request.getParameter("dl");
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("idDaily_map_nickName");
            BasicDBObject objsort = new BasicDBObject();
            objsort.put("_id", -1);
            HashMap<String, Object> conditions = new HashMap<>();
            conditions.put("id_daily", codeDaily);
            FindIterable<Document> iterable = col.find((Bson) new Document(conditions)).sort((Bson) objsort);
            Document document = iterable != null ? iterable.first() : null;
            if (document.size() > 0) {
                col.deleteOne((Bson) document);
                res.setSuccess(true);
                res.setErrorCode("0");
            }
            return res.toJson();
        } catch (Exception e) {
            logger.debug(e);
        }

        return res.toJson();
    }
}

