package com.vinplay.dal.service.impl;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.dal.service.ReportMoneyService;
import com.vinplay.dal.entities.report.ReportMoneyModelNew;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.response.LogUserMoneyResponse;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.bson.Document;
import org.bson.conversions.Bson;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Accumulators.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ReportMoneyServiceImpl implements ReportMoneyService {
    public List<ReportMoneyModelNew> search(String nickName, String actionName, String timeStart, String timeEnd, int page, int totalRecord){
        MongoDatabase db = MongoDBConnectionFactory.getDB();

        List<Bson> filters = new ArrayList<>();
        if (nickName != null && !nickName.equals("")) {
            filters.add(eq("nick_name", nickName));
        }

        if (actionName != null && !actionName.equals("")) {
            filters.add(eq("action_name", actionName));
        }
        if (timeStart != null && !timeStart.isEmpty() && timeEnd != null && !timeEnd.isEmpty()) {
            try {
                filters.add(gte("report_date", timeStart));
                filters.add(lte("report_date", timeEnd));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        MongoCollection<Document>  collection =  db.getCollection("report_money_game");
        AggregateIterable<Document> aggregateResult =  collection.aggregate(Arrays.asList(
                match(and(filters)),
                group("$action_name"
                        ,sum("total_out", "$total_out")
                        ,sum("total_refund", "$total_refund")
                        ,sum("total_in", "$total_in")
                        ,sum("fee", "$fee")
//                        ,sum("revenue", "$revenue"),
                        ,first("action_name", "$action_name")
                )  // GROUP BY và SUM

        ));

        List<ReportMoneyModelNew> results = new ArrayList<>();
        // Duyệt qua kết quả và in ra
        for (Document doc : aggregateResult) {
            results.add(new ReportMoneyModelNew(doc));
        }

        return results;
    }
}
