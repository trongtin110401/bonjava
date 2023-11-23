package com.vinplay.api.backend.processors.matkhaucap2;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.response.BaseResponseModel;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.servlet.http.HttpServletRequest;

public class ResetMatKhauCap2Processor implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger("backend");
    private static final String USER_SECRET = "user_secret_code";
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = param.get();
        BaseResponseModel res = new BaseResponseModel(false, "1001");
        try {
            String username = request.getParameter("nickname");
            String code = request.getParameter("code");
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection(USER_SECRET);
            Document doc = new Document();
            if (checkExisted(username)) {
                doc.append("code", code);
                col.updateOne((Bson) new Document("username", username), (Bson) new Document("$set", (Object) doc));
                res.setSuccess(true);
                res.setErrorCode("1");
            } else {
                res.setSuccess(false);
                res.setErrorCode("2");
            }
            return res.toJson();
        } catch (Exception e) {
            logger.error(e.getStackTrace());
        }
        return res.toJson();
    }

    private Boolean checkExisted(String username) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        Document conditions = new Document();
        conditions.put("username", username);
        long totalRows = db.getCollection(USER_SECRET).count(conditions);
        if (totalRows > 0)
            return Boolean.TRUE;
        return Boolean.FALSE;
    }


}
