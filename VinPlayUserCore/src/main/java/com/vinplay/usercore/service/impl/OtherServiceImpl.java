/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.usercore.service.impl;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.vinplay.usercore.service.OtherService;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.response.LinkSocialResponse;
import com.vinplay.vbee.common.response.TransactionExpenseResponse;
import com.vinplay.vbee.common.response.TransactionFundResponse;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OtherServiceImpl implements OtherService {
    @Override
    public LinkSocialResponse getLinkSocial() {
        LinkSocialResponse linkSocialResponse = new LinkSocialResponse(true, "0");

        HashMap<String, Object> conditions = new HashMap<>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("link_social");
        conditions.put("id", 1);
        FindIterable iterable = col.find(new Document(conditions));
        iterable.forEach((Block) new Block<Document>() {
            public void apply(Document document) {
                linkSocialResponse.setId(1);
                linkSocialResponse.setBotTele(document.getString("bot_tele"));
                linkSocialResponse.setFanPage(document.getString("fan_page"));
                linkSocialResponse.setGroupFacebook(document.getString("group_facebook"));
                linkSocialResponse.setLiveChat(document.getString("live_chat"));
                linkSocialResponse.setTeleCSKH(document.getString("tele_cskh"));
                linkSocialResponse.setBotTele(document.getString("bot_tele"));
                linkSocialResponse.setLinkDownload(document.getString("link_download"));
                linkSocialResponse.setHome(document.getString("home"));
                linkSocialResponse.setChatId(document.getString("chat_id"));
            }
        });
        return linkSocialResponse;
    }

    @Override
    public void updateLinkSocial(LinkSocialResponse response) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("link_social");
        Document filter = new Document("id", 1);
        Document document = new Document();

        if (response.getBotTele() != null && !response.getBotTele().trim().isEmpty()) {
            document.put("bot_tele", response.getBotTele());
        }
        if (response.getFanPage() != null && !response.getFanPage().trim().isEmpty()) {
            document.put("fan_page", response.getFanPage());
        }
        if (response.getGroupFacebook() != null && !response.getGroupFacebook().trim().isEmpty()) {
            document.put("group_facebook", response.getGroupFacebook());
        }
        if (response.getLiveChat() != null && !response.getLiveChat().trim().isEmpty()) {
            document.put("live_chat", response.getLiveChat());
        }
        if (response.getTeleCSKH() != null && !response.getTeleCSKH().trim().isEmpty()) {
            document.put("tele_cskh", response.getTeleCSKH());
        }
        if (response.getLinkDownload() != null && !response.getLinkDownload().trim().isEmpty()) {
            document.put("link_download", response.getLinkDownload());
        }
        if (response.getHome() != null && !response.getHome().trim().isEmpty()) {
            document.put("home", response.getHome());
        }
        if (response.getChatId() != null && !response.getChatId().trim().isEmpty()) {
            document.put("chat_id", response.getChatId());
        }

        Document update = new Document("$set", document);
        col.updateOne(filter, update, new UpdateOptions().upsert(true));
    }


    @Override
    public void saveTransactionUpdateFund(Document document) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("fund_transaction");
        col.insertOne(document);
    }

    @Override
    public void saveExpenseTransaction(Document document) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("expense_transaction");
        col.insertOne(document);
    }

    @Override
    public TransactionFundResponse getTransactionFund(int pageIndex, int pageSize, String type, String startTime, String endTime, String fundName) {
        TransactionFundResponse response = new TransactionFundResponse(true, "0");

        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("fund_transaction");

        Document query = new Document();
        int skip = (pageIndex - 1) * pageSize;
        if ((startTime != null && !startTime.isEmpty()) && (endTime != null && !endTime.isEmpty())) {
            query.append("time_log", new Document("$gte", startTime).append("$lte", endTime));
        }
        if (type != null && !type.isEmpty()) {
            query.append("type", type);
        }
        if (fundName != null && !fundName.isEmpty()) {
            query.append("fund_name", fundName);
        }

        MongoCursor<Document> cursor = col.find(query).skip(skip).limit(pageSize).iterator();

        long totalCount = col.count(query);

        List<Document> transactions = new ArrayList<>();
        double totalWithdraw = 0;
        double totalDeposit = 0;
        while (cursor.hasNext()) {
            Document document = cursor.next();
            Document fund = new Document();
            fund.put("fundName", document.getString("fund_name"));
            fund.put("amount", document.getInteger("amount"));
            fund.put("type", document.getString("type"));
            if (document.getString("type").equals("deposit")) {
                totalDeposit += document.getInteger("amount");
            } else {
                totalWithdraw += document.getInteger("amount");
            }

            fund.put("createdTime", document.getString("time_log"));
            transactions.add(fund);
        }
        response.setTransactions(transactions);
        response.setTotal((int) totalCount);
        response.setPageIndex(pageIndex);
        response.setPageSize(pageSize);
        response.setTotalDeposit(totalDeposit);
        response.setTotalWithdraw(totalWithdraw);
        response.setProfit(totalWithdraw - totalDeposit);

        return response;
    }

    @Override
    public TransactionExpenseResponse getTransactionExpense(int pageIndex, int pageSize, String type, String startTime, String endTime, String expense) {
        TransactionExpenseResponse response = new TransactionExpenseResponse(true, "0");

        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("expense_transaction");

        Document query = new Document();
        int skip = (pageIndex - 1) * pageSize;
        if ((startTime != null && !startTime.isEmpty()) && (endTime != null && !endTime.isEmpty())) {
            query.append("time_log", new Document("$gte", startTime).append("$lte", endTime));
        }
        if (type != null && !type.isEmpty()) {
            query.append("type", type);
        }
        if (expense != null && !expense.isEmpty()) {
            query.append("expense", expense);
        }
        MongoCursor<Document> cursor = col.find(query).skip(skip).limit(pageSize).iterator();

        long totalCount = col.count(query);

        List<Document> transactions = new ArrayList<>();
        double totalAmount = 0;
        while (cursor.hasNext()) {
            Document document = cursor.next();
            Document fund = new Document();
            fund.put("expense", document.getString("expense"));
            fund.put("amount", document.getString("amount"));
            totalAmount += Integer.parseInt(document.getString("amount"));
            fund.put("type", document.getString("type"));
            fund.put("createdTime", document.getString("time_log"));
            transactions.add(fund);
        }
        response.setTransactions(transactions);
        response.setTotal((int) totalCount);
        response.setPageIndex(pageIndex);
        response.setPageSize(pageSize);
        response.setTotalExpense(totalAmount);
        return response;
    }
}

