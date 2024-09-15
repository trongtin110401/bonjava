/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.mongodb.BasicDBObject
 *  com.mongodb.Block
 *  com.mongodb.client.AggregateIterable
 *  com.mongodb.client.FindIterable
 *  com.mongodb.client.MongoCollection
 *  com.mongodb.client.MongoDatabase
 *  com.mongodb.client.model.FindOneAndUpdateOptions
 *  com.vinplay.vbee.common.models.cache.ReportModel
 *  com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory
 *  com.vinplay.vbee.common.pools.ConnectionPool
 *  com.vinplay.vbee.common.utils.VinPlayUtils
 *  org.bson.Document
 *  org.bson.conversions.Bson
 */
package com.vinplay.dal.dao.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.vinplay.dal.dao.ReportDAO;
import com.vinplay.dal.entities.report.ReportMoneySystemModel;
import com.vinplay.dal.entities.report.ReportTotalMoneyModel;
import com.vinplay.vbee.common.models.TopCaoThu;
import com.vinplay.vbee.common.models.cache.ReportModel;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.pools.ConnectionPool;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.sql.Date;
import java.sql.*;
import java.text.ParseException;
import java.util.*;

public class ReportDao2Impl {

    /**
     * @param start
     * @param end
     * @param actions
     * @param sort       1 là top thắng, 2 là top thua
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public List<TopCaoThu> topPlayer(String start, String end, List<String> actions, int sort, int pageNumber, int pageSize) {

        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> collection = db.getCollection("report_money_game");

        // Danh sách action_name được truyền vào
        List<String> actionNames = actions;

        // Khoảng thời gian tìm kiếm (dưới dạng chuỗi)
        String startDate = start; // Ngày bắt đầu (định dạng YYYY-MM-DD)
        String endDate = end;   // Ngày kết thúc (định dạng YYYY-MM-DD)

        // Định nghĩa các thông số phân trang
        int skip = (pageNumber - 1) * pageSize;

        // Tạo Document cho điều kiện lọc và sắp xếp dựa trên cờ
        Document matchCondition;
        Document sortCondition;

        if (sort == 1) {
            // Nếu cờ là true, lọc với điều kiện lớn hơn 0 và sắp xếp giảm dần
            matchCondition = new Document("$match", new Document("total", new Document("$gt", 0)));
            sortCondition = new Document("$sort", new Document("total", -1)); // Sắp xếp giảm dần
        } else {
            // Nếu cờ là false, lọc với điều kiện nhỏ hơn 0 và sắp xếp tăng dần
            matchCondition = new Document("$match", new Document("total", new Document("$lt", 0)));
            sortCondition = new Document("$sort", new Document("total", 1)); // Sắp xếp tăng dần
        }


        System.out.println("Action: " + actions + ", Display Number: " + pageSize + ", Start Time: " + start + ", End Time: " + end);

        // Xây dựng pipeline để thực hiện truy vấn với phân trang và lọc
        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(
                new Document("$match", new Document("report_date", new Document("$gte", startDate).append("$lte", endDate))
                        .append("action_name", new Document("$in", actionNames))), // Lọc theo danh sách action_name và khoảng thời gian
                new Document("$group", new Document("_id", "$nick_name")
                        .append("total", new Document("$sum", new Document("$add", Arrays.asList("$total_in", "$total_out", "$total_refund"))))),
                matchCondition, // Áp dụng điều kiện lọc dựa trên cờ
                sortCondition,  // Áp dụng điều kiện sắp xếp dựa trên cờ
                new Document("$skip", skip),
                new Document("$limit", pageSize),
                new Document("$project", new Document("_id", 0)
                        .append("nick_name", "$_id")
                        .append("total", 1))
        ));

        System.out.println(result.toString());

        List<TopCaoThu> topCaoThuList = new ArrayList<>();
        for (Document doc : result) {
            String nickname = doc.getString("nick_name");
            long moneyWin = doc.getLong("total");
            TopCaoThu topCaoThu = new TopCaoThu(nickname, moneyWin);
            topCaoThuList.add(topCaoThu);
        }
        return topCaoThuList;
    }

}

