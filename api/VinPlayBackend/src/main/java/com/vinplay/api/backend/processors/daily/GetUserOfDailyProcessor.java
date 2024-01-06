package com.vinplay.api.backend.processors.daily;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.daily.entities.UserDailyResponse;
import com.vinplay.daily.entities.UserFromDailyResponse;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import org.apache.log4j.Logger;
import org.bson.Document;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GetUserOfDailyProcessor implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger("backend");

    public synchronized String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = param.get();
        UserFromDailyResponse res = new UserFromDailyResponse(false, "1001");
        try {
            String codeDaily = request.getParameter("dl");
            String typeSearch = request.getParameter("type");

            if (typeSearch.equals("elk")) {
                APIelk apielk = new APIelk();
                List<UserDailyResponse> listUser;
                listUser = apielk.UserbyIDDaiLy2(codeDaily);
                Collections.reverse(listUser);
                res.ListUserOfDaily = listUser;
            } else {
                List<UserDailyResponse> listUser = new ArrayList<>();
                MongoDatabase db = MongoDBConnectionFactory.getDB();
                MongoCollection col = db.getCollection("user_map_daily");
                BasicDBObject objSort = new BasicDBObject();
                objSort.put("_id", -1);
                Document conditions = new Document();
                if (!codeDaily.isEmpty()) {
                    conditions.put("id_daily", codeDaily);
                } else {
                    return res.toJson();
                }
                FindIterable iterable = col.find(new Document(conditions)).sort(objSort).limit(1000);
                iterable.forEach((Block<Document>) document -> {
                    String nick_name = document.getString("nick_name");
                    String user_name = document.getString("user_name");
                    String time_log = document.getString("time_log");
                    if (!nick_name.equals("")) {
                        UserDailyResponse udl = new UserDailyResponse(nick_name, user_name, time_log);
                        listUser.add(udl);
                    }
                });
                Collections.reverse(listUser);
                res.ListUserOfDaily = listUser;
            }
            res.setErrorCode("0");
            res.setSuccess(true);
        } catch (Exception e) {
            logger.debug(e);
        }
        return res.toJson();
    }
}
