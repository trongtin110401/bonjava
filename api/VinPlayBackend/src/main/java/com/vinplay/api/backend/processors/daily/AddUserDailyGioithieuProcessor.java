package com.vinplay.api.backend.processors.daily;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.daily.entities.AddUserToListDailyResponse;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;


public class AddUserDailyGioithieuProcessor implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger("backend");
    private static final String TOW = "-2";
    private static final String ONE = "-1";

    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = param.get();
        AddUserToListDailyResponse res = new AddUserToListDailyResponse(false, "1001");
        block10:
        {
            try {
                String codeDaily = request.getParameter("dl");
                String nickname = request.getParameter("nn");
                MongoDatabase db = MongoDBConnectionFactory.getDB();
                MongoCollection col = db.getCollection("idDaily_map_nickName");
                Document doc = new Document();
                String code = TOW;
                if (nickname != null) {
                    try {
                        UserServiceImpl service = new UserServiceImpl();
                        UserModel model = service.getUserByNickName(nickname);
                        code = model != null ? String.valueOf(model.getDaily()) : ONE;
                    } catch (Exception e) {
                        //todo: 1- phải xử lý res khi exception
                        logger.error(e.getStackTrace());
                    }
                }
                //
                if (!code.equals(TOW) && !code.equals(ONE)) {
                    if (checkExisted(codeDaily, col)) {
                        res.setSuccess(false);
                        res.setErrorCode("1002");
                        break block10;
                    }
                    doc.append("nickName", nickname);
                    doc.append("id_daily", codeDaily);
                    doc.append("time_log", VinPlayUtils.getCurrentDateTime());
                    col.insertOne(doc);
                    res.setSuccess(true);
                    res.setErrorCode("0");

                }
                return res.toJson();
            } catch (Exception e) {
                //todo:2- phải xử lý res khi exception
                logger.error(e.getStackTrace());
            }
        }
        return res.toJson();
    }

    private Boolean checkExisted(String codeDaily, MongoCollection col) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        Document conditions = new Document();
        conditions.put("id_daily", codeDaily);
        long totalRows = db.getCollection("idDaily_map_nickName").count(conditions);
        if (totalRows > 0)
            return Boolean.TRUE;
        return Boolean.FALSE;
    }


}
