package com.vinplay.api.backend.processors.removedb;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.removedb.removedbResponse;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import org.apache.log4j.Logger;
import org.bson.Document;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

public class RemovedbProcessor implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger("backend");

    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = param.get();
        removedbResponse res = new removedbResponse(false, "1001");
        try {
            String nameDbMongo = request.getParameter("db");
            String fromTime = request.getParameter("ts");
            String endTime = request.getParameter("te");
            String typeTime = request.getParameter("type");
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection(nameDbMongo);
            BasicDBObject objsort = new BasicDBObject();
            objsort.put("_id", -1);
            HashMap<String, Object> conditions = new HashMap<>();
            if (fromTime != null && !fromTime.isEmpty() && endTime != null && !endTime.isEmpty()) {
                // Todo: kiểm trả không được xóa DB của tháng gần nhất
//                if (endTime < ???) {
//                    res.setSuccess(false);
//                    res.setErrorCode("1003");
//                    return res.toJson();
//                }

                BasicDBObject obj = new BasicDBObject();
                obj.put("$gte", fromTime);
                obj.put("$lte", endTime);
                conditions.put(typeTime, obj);
                long count = col.deleteMany( new Document(conditions)).getDeletedCount();
                res.setCount(count);
                res.setSuccess(true);
                res.setErrorCode("0");
                return res.toJson();
            }
        } catch (Exception e) {
            logger.debug(e);
        }
        return res.toJson();
    }
}

