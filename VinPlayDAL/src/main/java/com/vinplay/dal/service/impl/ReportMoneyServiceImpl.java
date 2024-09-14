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
    public List<ReportMoneyModelNew> search(String nickName, String actionName, String timeStart,
                                            String timeEnd, int page, int totalRecord){
        MongoDatabase db = MongoDBConnectionFactory.getDB();

        List<Bson> filters = new ArrayList<>();
        if (nickName != null && !nickName.equals("")) {
            filters.add(eq("nick_name", nickName));
        }

        if (actionName != null && !actionName.equals("")) {
            filters.add(eq("action_name", actionName));
        }
        if (timeStart != null && !timeStart.equals("") && timeEnd != null && !timeEnd.equals("")) {
            try {
                filters.add(gte("report_date", VinPlayUtils.getDateTimeStr(VinPlayUtils.getDateTimeFromDate(timeStart + " 00:00:00"))));
                filters.add(lte("report_date", VinPlayUtils.getDateTimeStr(VinPlayUtils.getDateTimeFromDate(timeStart + " 23:59:59"))));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        MongoCollection<Document>  collection =  db.getCollection("report_money_game");
        AggregateIterable<Document> aggregateResult =  collection.aggregate(Arrays.asList(
                match(and(filters)),
                group("$action_name"
                        ,sum("money_win", "$money_win")
                        ,sum("money_lost", "$money_lost")
                        ,sum("money_other", "$money_other")
                        ,sum("money_exchange", "$money_exchange")
                        ,sum("fee", "$fee")
                        ,sum("revenue", "$revenue")
                )  // GROUP BY và SUM

        ));

        List<ReportMoneyModelNew> results = new ArrayList<>();
        // Duyệt qua kết quả và in ra
        for (Document doc : aggregateResult) {
            System.out.println(doc.toJson());
            results.add(new ReportMoneyModelNew(doc));
        }

        return results;
    }
}
