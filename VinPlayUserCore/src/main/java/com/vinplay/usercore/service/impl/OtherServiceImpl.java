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
import com.vinplay.vbee.common.pools.ConnectionPool;
import com.vinplay.vbee.common.response.*;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

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
        long totalAmount = 0;
        while (cursor.hasNext()) {
            Document document = cursor.next();
            Document fund = new Document();
            fund.put("_id", document.getObjectId("_id").toString());
            fund.put("expense", document.getString("expense"));
            fund.put("amount", document.getString("amount"));
            totalAmount += Long.parseLong(document.getString("amount"));
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

    @Override
    public UserTele getUserTeleInfoByNickname(String nickname) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> collection = db.getCollection("user_tele");
        Document filter = new Document("nickname", nickname);
        MongoCursor<Document> cursor = collection.find(filter).iterator();

        try {
            if (cursor.hasNext()) {
                Document doc = cursor.next();
                UserTele user = extractUserInfo(doc);
                return user;
            } else {
                return null;
            }
        } finally {
            cursor.close();
        }
    }

    @Override
    public boolean checkActiveByNickname(String nickname) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> collection = db.getCollection("user_tele");
        Document filter = new Document("nickname", nickname)
                .append("isActive", true);
        MongoCursor<Document> cursor = collection.find(filter).iterator();

        try {
            return cursor.hasNext();
        } finally {
            cursor.close();
        }
    }

    @Override
    public void activeUserTele(String nickname) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> collection = db.getCollection("user_tele");
        Document filter = new Document("nickname", nickname);
        Document update = new Document("$set", new Document("isActive", true));
        collection.updateOne(filter, update);
    }

    @Override
    public void activeUserPhone(String nickname) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> collection = db.getCollection("user_phone");
        Document filter = new Document("nickname", nickname);
        Document update = new Document("$set", new Document("isActive", true));
        collection.updateOne(filter, update);
    }

    @Override
    public void deactivateUserTele(String nickname) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> collection = db.getCollection("user_tele");
        Document filter = new Document("nickname", nickname);
        Document update = new Document("$set", new Document("isActive", false));
        collection.updateOne(filter, update);
    }


    @Override
    public void saveUserTeleCashBack(Document document) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> collection = db.getCollection("user_tele_cash_back");
        collection.insertOne(document);
    }

    @Override
    public UserLoseByDayResponse getListUserTeleCashBack(int pageIndex, int pageSize, String timeStart, String timeEnd, String nickname, String code) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> collection = db.getCollection("user_tele_cash_back");
        UserLoseByDayResponse userLoseByDayResponse = new UserLoseByDayResponse(true, "200");
        Document filter = new Document();
        if (timeStart != null && !timeStart.isEmpty()) {
            filter.append("createdDate", new Document("$gte", timeStart));
        }
        if (timeEnd != null && !timeEnd.isEmpty()) {
            filter.append("createdDate", new Document("$lte", timeEnd));
        }
        if (nickname != null && !nickname.isEmpty()) {
            filter.append("nickname", nickname);
        }
        if (code != null && !code.isEmpty()) {
            filter.append("code", code);
        }

        FindIterable<Document> result = collection.find(filter).skip(pageIndex * pageSize).limit(pageSize);

        List<UserLoseByDay> users = new ArrayList<>();
        for (Document document : result) {
            UserLoseByDay userLoseByDay = new UserLoseByDay();
            userLoseByDay.setNickname(document.getString("nickname"));
            userLoseByDay.setMoney(document.getLong("money"));
            userLoseByDay.setChatId(document.getString("chatID"));
            userLoseByDay.setCode(document.getString("code"));
            userLoseByDay.setMoneyCashBack(document.getLong("cashBack"));
            users.add(userLoseByDay);
        }
        userLoseByDayResponse.setUsers(users);
        userLoseByDayResponse.setTotalRecord((int) collection.count());
        return userLoseByDayResponse;
    }

    @Override
    public MoneyShootFishResponse getMoneyShootFish(String startTime, String endTime) {
        MoneyShootFishResponse response = new MoneyShootFishResponse(false, "1001");
        long totalCashIn = 0;
        long totalCashOut = 0;
        long totalProfit = 0;
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_banca");) {
            String sql = "SELECT * FROM cgame.bc_trans_log where time >= ? and time <= ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setDate(1, Date.valueOf(startTime));
            stm.setDate(2, Date.valueOf(endTime));
            ResultSet rs = stm.executeQuery();

            totalCashIn = 0;
            totalCashOut = 0;
            totalProfit = 0;

            while (rs.next()) {
                if (rs.getString("Type").equals("50")) {
                    if (rs.getString("Extra").equals("xxeng cashout")) {
                        totalCashOut += (rs.getInt("CashGain") * -1);
                    } else {
                        totalCashIn += (rs.getInt("CashGain"));
                    }
                }
                if (rs.getString("Type").equals("1")) {
                    totalProfit += (rs.getInt("CashGain"));
                }
            }
            rs.close();
            stm.close();
        } catch (Exception e) {
            response.setSuccess(false);
            response.setErrorCode(e.getMessage());
            e.printStackTrace();
        }
        totalProfit = totalProfit * -1;
        response.setTotalCashIn(totalCashIn);
        response.setTotalCashOut(totalCashOut);
        response.setTotalProfit(totalProfit);
        response.setSuccess(true);
        response.setErrorCode("Ok");
        return response;
    }

    @Override
    public long getTotalShootFishByNickname(String startTime, String endTime, String nickname) {
        MoneyShootFishResponse response = new MoneyShootFishResponse(false, "1001");
        long totalProfit = 0;
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_banca");) {
            String sql;
            if (nickname != null) {
                sql = "SELECT * FROM cgame.bc_trans_log c " +
                        "JOIN users u ON c.UserId = u.user_id " +
                        "WHERE c.time >= ? AND c.time <= ? AND u.nickname = ?";
            } else {
                sql = "SELECT * FROM cgame.bc_trans_log WHERE time >= ? AND time <= ?";
            }
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setDate(1, Date.valueOf(startTime));
            stm.setDate(2, Date.valueOf(endTime));
            if (nickname != null) {
                stm.setString(3, nickname);
            }
            ResultSet rs = stm.executeQuery();

            totalProfit = 0;

            while (rs.next()) {
                if (rs.getString("Type").equals("1")) {
                    totalProfit += rs.getInt("CashGain");
                }
            }
            rs.close();
            stm.close();
        } catch (Exception e) {
            response.setSuccess(false);
            response.setErrorCode(e.getMessage());
            e.printStackTrace();
        }
        return totalProfit;
    }

    @Override
    public void deleteExpenseById(String id) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> col = db.getCollection("expense_transaction");
        ObjectId objectId = new ObjectId(id);
        col.deleteOne(eq("_id", objectId));
    }


    private UserTele extractUserInfo(Document doc) {
        String id = doc.getObjectId("_id").toString();
        String nickname = doc.getString("nickname");
        String phoneNumber = doc.getString("phoneNumber");
        boolean isActive = doc.getBoolean("isActive");
        String otp = doc.getString("otp");
        long timeToExpired = doc.getInteger("timeToExpired");
        String createdDate = doc.getString("createdDate");
        String chatID = doc.getString("chatID");
        UserTele user = new UserTele();
        user.setId(id);
        user.setNickname(nickname);
        user.setPhoneNumber(phoneNumber);
        user.setActive(isActive);
        user.setOtp(otp);
        user.setTimeToExpired(timeToExpired);
        user.setCreatedDate(createdDate);
        user.setChatID(chatID);
        return user;
    }

    @Override
    public boolean activePhoneNumber(String nickname, String phoneNumber) {
        return false;
    }

    @Override
    public String getPhoneByNickname(String nickname) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> collection = db.getCollection("user_phone");
        Document filter = new Document("nickname", nickname);
        FindIterable<Document> result = collection.find(filter);
        Iterator<Document> iterator = result.iterator();
        if (iterator.hasNext()) {
            Document document = iterator.next();
            return document.getString("phoneNumber");
        } else {
            return "";
        }
    }
}

