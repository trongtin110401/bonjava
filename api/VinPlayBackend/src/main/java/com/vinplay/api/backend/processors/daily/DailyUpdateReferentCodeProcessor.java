package com.vinplay.api.backend.processors.daily;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import org.apache.log4j.Logger;
import org.bson.Document;

import javax.servlet.http.HttpServletRequest;


public class DailyUpdateReferentCodeProcessor implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger("backend");

    public String execute(Param<HttpServletRequest> param) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> col = db.getCollection("daily");

        HttpServletRequest request = param.get();
        DailyEntity response = new DailyEntity(false, "1001");
        try {
            String accessToken = request.getParameter("accessToken");
            String username = request.getParameter("username");
            String referentCode = request.getParameter("referentCode");


            Document daily = checkExisted(username, accessToken, col);
            if (daily == null) {
                return response.toJson();
            }
            col.updateOne(
                    Filters.eq("user_name", username),
                    Updates.set("referent_code", referentCode)
            );

            response = new DailyEntity(true, "200");
            response.setUsername(username);
            response.setAccessToken(accessToken);
            response.setReferentCode(referentCode);
            return response.toJson();
        } catch (Exception e) {
            logger.error(e.getStackTrace());
        }
        return response.toJson();
    }

    private Document checkExisted(String username, String accessToken, MongoCollection col) {
        Document conditions = new Document();
        conditions.put("user_name", username);
        conditions.put("access_token", accessToken);
        return (Document) col.find(conditions).first();
    }


}
