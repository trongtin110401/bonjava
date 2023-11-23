package com.vinplay.api.backend.processors;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.daily.entities.UserDailyResponse;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class GetTongUserByDaiLyProcess implements BaseProcessor<HttpServletRequest, String> {
    public synchronized String execute(Param<HttpServletRequest> param) {
        try {
            HttpServletRequest request = (HttpServletRequest)param.get();
            String codedaily = request.getParameter("codedaily");
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("user_map_daily");
            BasicDBObject objSort = new BasicDBObject();
            objSort.put("_id", -1);
            Document conditions = new Document();
            List<UserDailyResponse> listUser = new ArrayList<>();
            if(codedaily != null){
                conditions.put("id_daily", codedaily);
            }else{
                return "0";
            }
//            FindIterable iterable = col.find( new Document(conditions)).sort( objSort);
            long count = col.count((Bson) new Document(conditions));
            return count+"";
//            iterable.forEach((Block<Document>) document -> {
//                String nick_name = document.getString("nickName");
//                String user_name = document.getString("user_name");
//                String time_log = document.getString("time_log");
//                if (!nick_name.equals("")) {
//                    UserDailyResponse udl = new UserDailyResponse(nick_name, user_name, time_log);
//                    listUser.add(udl);
//                }
//            });
//            return listUser.size()+"";
        }
        catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
