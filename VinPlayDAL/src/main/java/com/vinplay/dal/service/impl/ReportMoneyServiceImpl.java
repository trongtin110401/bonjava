package com.vinplay.dal.service.impl;

import com.google.gson.Gson;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.dal.service.ReportMoneyService;
import com.vinplay.dal.entities.report.ReportMoneyModelNew;
import com.vinplay.vbee.common.enums.Games;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.statics.Consts;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Accumulators.*;

import java.util.*;
import java.util.stream.Collectors;

public class ReportMoneyServiceImpl implements ReportMoneyService {
    public List<ReportMoneyModelNew> search(String nickName, String actionName, String timeStart, String timeEnd, int page, int totalRecord) {
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
        MongoCollection<Document> collection = db.getCollection("report_money_game");
        AggregateIterable<Document> aggregateResult = collection.aggregate(Arrays.asList(
                match(and(filters)),
                group("$action_name"
                        , sum("total_out", "$total_out")
                        , sum("total_refund", "$total_refund")
                        , sum("total_in", "$total_in")
                        , sum("fee", "$fee")
                        , first("action_name", "$action_name")
                )  // GROUP BY và SUM

        ));

        List<ReportMoneyModelNew> results = new ArrayList<>();
        // Duyệt qua kết quả và in ra
        for (Document doc : aggregateResult) {
            results.add(new ReportMoneyModelNew(doc));
        }

        return results;
    }


    public Map<String, Long> getGameLoser(String reportDate, String nickName, int pageNumber, int pageSize) {

        Map<String, Long> results = new HashMap<>();

        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> collection = db.getCollection("report_money_game");

        // Tạo đối tượng $match ban đầu
        List<String> actions = Consts.GAMES.stream().filter(s -> !s.equals(Games.HAM_CA_MAP.getName())).collect(Collectors.toList());
        Document matchDocument = new Document("report_date", reportDate)
                .append("action_name", new Document("$in", actions));

        // Kiểm tra điều kiện nick_name và chỉ thêm nếu cần thiết
        if (StringUtils.isNotEmpty(nickName)) {
            matchDocument.append("nick_name", nickName);
        }


        // Cài đặt các tham số phân trang
        int skipRecords = (pageNumber - 1) * pageSize;

        // Tạo pipeline cho aggregation
        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(
                new Document("$match", matchDocument),
                new Document("$project", new Document("nick_name", 1)
                        .append("total", new Document("$add", Arrays.asList("$total_in", "$total_out", "$total_refund")))
                ),
                new Document("$match", new Document("total", new Document("$lt", 0))),
                new Document("$group", new Document("_id", "$nick_name")
                        .append("total", new Document("$sum", "$total"))
                ),
                new Document("$sort", new Document("total", 1)), // Sắp xếp tăng dần theo total
                new Document("$skip", skipRecords), // Bỏ qua số lượng bản ghi
                new Document("$limit", pageSize) // Lấy số lượng bản ghi tương ứng với kích thước trang
        ));

        // In kết quả
        for (Document doc : result) {
            String nickname = doc.getString("_id");
            long amount = doc.getLong("total");
            results.put(nickname, amount);
        }


        return results;
    }


    public Map<String, Long> getGameWinner(String reportDate, String nickName, int pageNumber, int pageSize) {


        Map<String, Long> results = new HashMap<>();

        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> collection = db.getCollection("report_money_game");

        // Cài đặt các tham số phân trang
        int skipRecords = (pageNumber - 1) * pageSize;

        // Tạo đối tượng $match ban đầu
        List<String> actions = Consts.GAMES.stream().filter(s -> !s.equals(Games.HAM_CA_MAP.getName())).collect(Collectors.toList());
        Document matchDocument = new Document("report_date", reportDate)
                .append("action_name", new Document("$in", actions));

        // Kiểm tra điều kiện nick_name và chỉ thêm nếu cần thiết
        if (StringUtils.isNotEmpty(nickName)) {
            matchDocument.append("nick_name", nickName);
        }

        // Tạo pipeline cho aggregation
        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(
                new Document("$match", matchDocument),
                new Document("$project", new Document("nick_name", 1)
                        .append("total", new Document("$add", Arrays.asList("$total_in", "$total_out", "$total_refund")))
                ),
                new Document("$match", new Document("total", new Document("$gt", 0))),
                new Document("$group", new Document("_id", "$nick_name")
                        .append("total", new Document("$sum", "$total"))
                ),
                new Document("$sort", new Document("total", -1)), // Sắp xếp tăng dần theo total
                new Document("$skip", skipRecords), // Bỏ qua số lượng bản ghi
                new Document("$limit", pageSize) // Lấy số lượng bản ghi tương ứng với kích thước trang
        ));

        // In kết quả
        for (Document doc : result) {
            String nickname = doc.getString("_id");
            long amount = doc.getLong("total");
            results.put(nickname, amount);
        }


        return results;
    }

}
