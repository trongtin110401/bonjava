/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.usercore.service.impl;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.UpdateOptions;
import com.vinplay.usercore.service.OtherService;
import com.vinplay.vbee.common.models.TopCaoThu;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.pools.ConnectionPool;
import com.vinplay.vbee.common.response.*;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.python.parser.ast.Str;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
                linkSocialResponse.setMd5(document.getString("md5"));
                linkSocialResponse.setGroupTele(document.getString("groupTele"));
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
        if (response.getGroupTele() != null && !response.getGroupTele().trim().isEmpty()) {
            document.put("groupTele", response.getGroupTele());
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
        if (response.getMd5() != null && !response.getMd5().trim().isEmpty()) {
            document.put("md5", response.getMd5());
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

        MongoCursor<Document> cursor = col.find(query).skip(skip).limit(pageSize).sort(Sorts.descending("time_log")).iterator();

        long totalCount = col.count(query);

        List<Document> transactions = new ArrayList<>();
        long totalWithdraw = 0;
        long totalDeposit = 0;
        while (cursor.hasNext()) {
            Document document = cursor.next();
            Document fund = new Document();
            fund.put("fundName", document.getString("fund_name"));
            fund.put("amount", document.getLong("amount"));
            fund.put("type", document.getString("type"));
            fund.put("gameName", document.getString("game_name"));
            if (document.getString("type").equals("deposit")) {
                totalDeposit += document.getLong("amount");
            } else {
                totalWithdraw += document.getLong("amount");
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
        Document filter = new Document("nickname", nickname).append("isActive", true);
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
    public void deactivateUserPhone(String nickname) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> collection = db.getCollection("user_phone");
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
    public void updateCodeCallBack(String code) {

        MongoDatabase db = MongoDBConnectionFactory.getDB();

        MongoCollection<Document> collection = db.getCollection("user_tele_cash_back");
        Document updateQuery = new Document("$set", new Document("status", true).append("activeDate", VinPlayUtils.getCurrentDateTime()));
        collection.updateOne(new Document("code", code), updateQuery);
    }

    @Override
    public UserLoseByDayResponse getListUserTeleCashBack(int pageIndex, int pageSize, String timeStart, String timeEnd, String nickname, String code, String type) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> collection = db.getCollection("user_tele_cash_back");
        UserLoseByDayResponse userLoseByDayResponse = new UserLoseByDayResponse(true, "200");
        Document filter = new Document();
        if ((timeStart != null && !timeStart.isEmpty()) && (timeEnd != null && !timeEnd.isEmpty())) {
            filter.append("createdDate", new Document("$gte", timeStart + " 00:00:00").append("$lte", timeEnd + " 23:59:59"));
        }
        if (StringUtils.isNotEmpty(type)) {
            if (type.equals("WIN")) {
                filter.append("money", new Document("$gt", 0));
            } else {
                filter.append("money", new Document("$lt", 0));
            }
        }
        if (nickname != null && !nickname.isEmpty()) {
            filter.append("nickname", nickname);
        }
        if (code != null && !code.isEmpty()) {
            filter.append("code", code);
        }

        FindIterable<Document> result = collection.find(filter).skip(pageIndex * pageSize).limit(pageSize).sort(Sorts.descending("createdDate"));

        List<UserLoseByDay> users = new ArrayList<>();
        for (Document document : result) {
            UserLoseByDay userLoseByDay = new UserLoseByDay();
            userLoseByDay.setNickname(document.getString("nickname"));
            userLoseByDay.setMoney(document.getLong("money"));
            userLoseByDay.setChatId(document.getString("chatID"));
            userLoseByDay.setCode(document.getString("code"));
            userLoseByDay.setMoneyCashBack(document.getInteger("cashBack"));
            userLoseByDay.setCreatedDate(document.getString("createdDate"));
            userLoseByDay.setExpirationDate(document.getString("expirationDate"));
            if (document.getBoolean("status") == null) {
                userLoseByDay.setStatus(false);
            } else {
                userLoseByDay.setStatus(document.getBoolean("status"));
            }
            userLoseByDay.setActiveDate(document.getString("activeDate"));
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
            stm.setTimestamp(1, Timestamp.valueOf(LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
            stm.setTimestamp(2, Timestamp.valueOf(LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
//            stm.setTimestamp(2, Date.valueOf(endTime));
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
            if (nickname != null && !nickname.isEmpty()) {
                sql = "SELECT * FROM cgame.bc_trans_log c " + "JOIN users u ON c.UserId = u.user_id " + "WHERE c.time >= ? AND c.time <= ? AND u.nickname = ?";
            } else {
                sql = "SELECT * FROM cgame.bc_trans_log WHERE time >= ? AND time <= ?";
            }
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setTimestamp(1, Timestamp.valueOf(startTime + " 00:00:00"));
            stm.setTimestamp(2, Timestamp.valueOf(endTime + " 23:59:59"));
            if (nickname != null && !nickname.isEmpty()) {
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
        totalProfit = totalProfit * -1;
        return totalProfit;
    }

    @Override
    public List<TopCaoThu> getBanCa(String startTime, String endTime) {
        List<TopCaoThu> result = new ArrayList<>();
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_banca");) {
            String sql = "SELECT * FROM cgame.bc_trans_log c " + "JOIN users u ON c.UserId = u.user_id " + "WHERE c.time >= ? AND c.time <= ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setTimestamp(1, Timestamp.valueOf(startTime + " 00:00:00"));
            stm.setTimestamp(2, Timestamp.valueOf(endTime + " 23:59:59"));
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                if (rs.getString("Type").equals("1")) {
                    String nickname = rs.getString("nickname");
                    int cashGain = rs.getInt("CashGain");

                    // Check if the nickname already exists in the result list
                    Optional<TopCaoThu> existingTopCaoThu = result.stream()
                            .filter(topCaoThu -> topCaoThu.getNickname().equals(nickname))
                            .findFirst();

                    if (existingTopCaoThu.isPresent()) {
                        // If found, add the new cash gain to the existing amount
                        existingTopCaoThu.get().setMoneyWin(existingTopCaoThu.get().getMoneyWin() + cashGain);
                    } else {
                        // If not found, create a new TopCaoThu and add it to the result list
                        TopCaoThu topCaoThu = new TopCaoThu();
                        topCaoThu.setMoneyWin(cashGain);
                        topCaoThu.setNickname(nickname);
                        result.add(topCaoThu);
                    }
                }
            }
            rs.close();
            stm.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public long getMoneyShootFishByNickname(String startTime, String endTime, String nickname) {
        MoneyShootFishResponse response = new MoneyShootFishResponse(false, "1001");
        long totalProfit = 0;
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_banca");) {
            String sql;
            if (nickname != null && !nickname.isEmpty()) {
                sql = "SELECT * FROM cgame.bc_trans_log c " + "JOIN users u ON c.UserId = u.user_id " + "WHERE c.time >= ? AND c.time <= ? AND u.nickname = ?";
            } else {
                sql = "SELECT * FROM cgame.bc_trans_log WHERE time >= ? AND time <= ?";
            }
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setTimestamp(1, Timestamp.valueOf(startTime + " 00:00:00"));
            stm.setTimestamp(2, Timestamp.valueOf(endTime + " 23:59:59"));
            if (nickname != null && !nickname.isEmpty()) {
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
    public boolean checkIfHaveAnyEventActive() {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> collection = db.getCollection("event");
        Document document = new Document();
        document.put("status", true);
        return collection.count(document) > 0;
    }

    @Override
    public EventResponse getCurrentEvent() {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> collection = db.getCollection("event");
        String currentDate = VinPlayUtils.getCurrentDate();
        Document filter = new Document("status", true)
                .append("end_time", new Document("$gte", currentDate));
        MongoCursor<Document> cursor = collection.find(filter).iterator();
        EventResponse eventResponse = new EventResponse(true, "1001");

        if (cursor.hasNext()) {
            Document doc = cursor.next();
            eventResponse.setRate(doc.getInteger("rate"));
            eventResponse.setEventName(doc.getString("event_name"));
            eventResponse.setTimeEnd(doc.getString("end_time"));
            eventResponse.setStatus(doc.getBoolean("status"));
            eventResponse.setTimeStart(doc.getString("start_time"));
            eventResponse.setId(String.valueOf(doc.getLong("id")));
            eventResponse.setSuccess(true);
            eventResponse.setErrorCode("200");
            return eventResponse;
        }
        else {
            return null;
        }
    }

    @Override
    public boolean checkUserNapTienEvent(String eventId, String nickname) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> collection = db.getCollection("user_event");
        Document filter = new Document("event_id", eventId).append("nickname", nickname);
        long count = collection.count(filter);
        return count > 0;
    }

    @Override
    public void saveUserNapTienEvent(UserEvent userEvent) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> collection = db.getCollection("user_event");
        Document document = new Document();
        document.put("id", userEvent.getId());
        document.put("event_id", userEvent.getEventId());
        document.put("create_date", userEvent.getCreatedDate());
        document.put("actual_amount", userEvent.getActualAmount());
        document.put("event_amount", userEvent.getEventAmount());
        document.put("event_name", userEvent.getEventName());
        document.put("nickname", userEvent.getNickname());
        collection.insertOne(document);

    }

    @Override
    public ListEventResponse getAllEvent(String timeStart, String timeEnd, String eventName, String rate, Boolean status) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> collection = db.getCollection("event");
        Document filter = new Document();

        if (timeStart != null && !timeStart.isEmpty()) {
            filter.append("start_time", new Document("$gte", timeStart));
        }

        if (timeEnd != null && !timeEnd.isEmpty()) {
            filter.append("end_time", new Document("$lte", timeEnd));
        }

        if (eventName != null && !eventName.isEmpty()) {
            filter.append("event_name", eventName);
        }

        if (rate != null && !rate.isEmpty()) {
            filter.append("rate", Integer.parseInt(rate));
        }
        if (status != null) {
            filter.append("status", status);
        }

        MongoCursor<Document> cursor = collection.find(filter).iterator();
        List<Event> events = new ArrayList<>();
        while (cursor.hasNext()) {
            Document doc = cursor.next();
            Event eventResponse = new Event();
            eventResponse.setRate(doc.getInteger("rate"));
            eventResponse.setEventName(doc.getString("event_name"));
            eventResponse.setTimeEnd(doc.getString("end_time"));
            eventResponse.setStatus(doc.getBoolean("status"));
            eventResponse.setTimeStart(doc.getString("start_time"));
            eventResponse.setId(String.valueOf(doc.getLong("id")));
            events.add(eventResponse);
        }
        ListEventResponse eventResponse = new ListEventResponse(true, "200");
        eventResponse.setEvents(events);
        return eventResponse;
    }

    @Override
    public void updateEvent(long id, String timeStart, String timeEnd, String eventName, int rate, boolean status) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> collection = db.getCollection("event");
        Document filter = new Document("id", id);
        Document updateFields = new Document();
        updateFields.append("start_time", timeStart);
        updateFields.append("end_time", timeEnd);
        updateFields.append("event_name", eventName);
        updateFields.append("rate", rate);
        updateFields.append("status", status);
        Document updateOperation = new Document("$set", updateFields);
        collection.updateOne(filter, updateOperation);
    }

    @Override
    public List<MoneyShootFishResponse> getTotalShootFish(String startTime, String endTime, String nickname) throws Exception {

        List<MoneyShootFishResponse> responses = new ArrayList<>();
        long totalProfit = 0;
        PreparedStatement stm = null;
        ResultSet rs = null;

        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_banca");) {
            String sql = "SELECT u.nickname, sum(c.CashGain) as CashGain " + "FROM cgame.bc_trans_log c JOIN cgame.users u ON c.UserId = u.user_id " + "WHERE c.time >= ?  AND c.time <= ? AND c.type = 1 ";

            boolean findByNickName = false;
            if (StringUtils.isNotEmpty(nickname)) {
                sql += " AND u.nickname = ? ";
                findByNickName = true;
            }
            sql += "GROUP BY u.nickname";

            stm = conn.prepareStatement(sql);
            stm.setTimestamp(1, Timestamp.valueOf(LocalDateTime.parse(startTime + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
            stm.setTimestamp(2, Timestamp.valueOf(LocalDateTime.parse(endTime + " 23:59:59", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
            if (findByNickName) {
                stm.setString(3, nickname);
            }
            rs = stm.executeQuery();

            MoneyShootFishResponse response = null;
            while (rs.next()) {
                response = new MoneyShootFishResponse(true, "0", rs.getString("nickname"), rs.getLong("CashGain") * -1);
                responses.add(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stm != null) {
                stm.close();
            }
        }
        return responses;
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
            return document.getString("phone");
        } else {
            return "";
        }
    }

    @Override
    public String getPhoneActiveByNickname(String nickname) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> collection = db.getCollection("user_phone");
        Document filter = new Document("nickname", nickname).append("isActive", true);
        FindIterable<Document> result = collection.find(filter);
        Iterator<Document> iterator = result.iterator();
        if (iterator.hasNext()) {
            Document document = iterator.next();
            return document.getString("phone");
        } else {
            return "";
        }
    }

    @Override
    public void updateStatusSendBackCodeByDay(String type, String nickname, String startTime, String endTime) {
        if (checkIsSendBackCodeByDay(type, nickname, startTime, endTime)) {
            return;
        }
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> collection = db.getCollection("status_back_code");
        Document document = new Document();
        document.put("type", type);
        document.put("start_time", startTime);
        document.put("end_time", endTime);
        document.put("nickname", nickname);
        collection.insertOne(document);
    }

    @Override
    public boolean checkIsSendBackCodeByDay(String type, String nickname, String startTime, String endTime) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> collection = db.getCollection("status_back_code");
        Document filter = new Document();
        filter.put("type", type);
        filter.put("nickname", nickname);
        filter.put("start_time", new Document("$gte", startTime));
        filter.put("end_time", new Document("$lte", endTime));
        return collection.find(filter).first() != null;
    }

    @Override
    public boolean createEvent(EventResponse eventResponse) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> collection = db.getCollection("event");
        Document document = new Document();
        document.put("id", System.currentTimeMillis());
        document.put("start_time", eventResponse.getTimeStart());
        document.put("end_time", eventResponse.getTimeEnd());
        document.put("event_name", eventResponse.getEventName());
        document.put("rate", eventResponse.getRate());
        document.put("status", true);
        try {
            collection.insertOne(document);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public UserPhone getUserPhoneInfoByPhoneNumber(String phoneNumber) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> collection = db.getCollection("user_phone");
        Document filter = new Document("phone", phoneNumber);
        MongoCursor<Document> cursor = collection.find(filter).iterator();

        try {
            if (cursor.hasNext()) {
                Document doc = cursor.next();
                UserPhone user = new UserPhone();
                String id = doc.getObjectId("_id").toString();
                boolean isActive = doc.getBoolean("isActive");
                String otp = doc.getString("otp");
                long timeToExpired = doc.getInteger("timeToExpired");
                String createdDate = doc.getString("createdDate");
                user.setId(id);
                user.setNickname(doc.getString("nickname"));
                user.setPhoneNumber(phoneNumber);
                user.setActive(isActive);
                user.setOtp(otp);
                user.setTimeToExpired(timeToExpired);
                user.setCreatedDate(createdDate);
                return user;
            } else {
                return null;
            }
        } finally {
            cursor.close();
        }
    }

    @Override
    public UserActivePhoneResponse getAllUserActivePhone(String nickname, String phone, int pageIndex, int pageSize) {
        UserActivePhoneResponse userActivePhoneResponse = new UserActivePhoneResponse(true, "0");
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> collection = db.getCollection("user_phone");

        Document query = new Document();

        if (nickname != null && !nickname.isEmpty()) {
            query.append("nickname", nickname);
        }
        if (phone != null && !phone.isEmpty()) {
            query.append("phone", phone);
        }
        Document sort = new Document("createdDate", -1);
        List<UserPhone> result = new ArrayList<>();
        MongoCursor<Document> cursor = collection.find(query).sort(sort).skip(pageIndex * pageSize).limit(pageSize).iterator();
        while (cursor.hasNext()) {
            Document doc = cursor.next();
            UserPhone userPhone = new UserPhone();
            userPhone.setActive(doc.getBoolean("isActive"));
            userPhone.setNickname(doc.getString("nickname"));
            userPhone.setPhoneNumber(doc.getString("phone"));
            userPhone.setCreatedDate(doc.getString("createdDate"));
            userPhone.setId(doc.getObjectId("_id").toString());
            result.add(userPhone);
        }
        userActivePhoneResponse.setUsers(result);
        userActivePhoneResponse.setTotalPage(Integer.parseInt(String.valueOf(collection.count(query))));
        return userActivePhoneResponse;
    }

    @Override
    public UserActiveTeleResponse getAllUserActiveTele(String nickname, int pageIndex, int pageSize) {
        UserActiveTeleResponse userActivePhoneResponse = new UserActiveTeleResponse(true, "0");
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> collection = db.getCollection("user_tele");

        Document query = new Document();

        if (nickname != null && !nickname.isEmpty()) {
            query.append("nickname", nickname);
        }
        Document sort = new Document("createdDate", -1);
        List<UserTele> result = new ArrayList<>();
        MongoCursor<Document> cursor = collection.find(query).sort(sort).skip(pageIndex * pageSize).limit(pageSize).iterator();
        while (cursor.hasNext()) {
            Document doc = cursor.next();
            UserTele userPhone = new UserTele();
            userPhone.setActive(doc.getBoolean("isActive"));
            userPhone.setNickname(doc.getString("nickname"));
            userPhone.setPhoneNumber(doc.getString("phoneNumber"));
            userPhone.setCreatedDate(doc.getString("createdDate"));
            userPhone.setId(doc.getObjectId("_id").toString());
            result.add(userPhone);
        }
        userActivePhoneResponse.setUsers(result);
        userActivePhoneResponse.setTotalPage(Integer.parseInt(String.valueOf(collection.count(query))));
        return userActivePhoneResponse;
    }
}

