package com.vinplay.api.backend.processors.daily;


import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.daily.entities.BotTeleDailyResponse;
import com.vinplay.daily.entities.ListBotTeleDailyResponse;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GetListBotDailyProcessor implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger("backend");

    public String execute(Param<HttpServletRequest> param) {
        ListBotTeleDailyResponse res = new ListBotTeleDailyResponse(false, "1001");
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("bot_daily");
            final List<BotTeleDailyResponse> records = new ArrayList<>();
            BasicDBObject objsort = new BasicDBObject();
            objsort.put("_id", -1);
            HashMap<String, Object> conditions = new HashMap<>();
            FindIterable iterable = col.find((Bson) new Document(conditions)).sort((Bson) objsort);

            iterable.forEach((Block<Document>) document -> {
                BotTeleDailyResponse botTeleDaily = new BotTeleDailyResponse(document.getString("nickname"), document.getString("teleId"));
                records.add(botTeleDaily);
            });
            res.ListBotTeleDaily = records;
            res.setErrorCode("0");
            res.setSuccess(true);
        }
        catch (Exception e) {
            logger.debug(e);
        }
        return res.toJson();
    }
}
