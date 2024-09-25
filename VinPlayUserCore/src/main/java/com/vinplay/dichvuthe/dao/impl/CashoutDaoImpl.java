/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.mongodb.BasicDBObject
 *  com.mongodb.Block
 *  com.mongodb.client.FindIterable
 *  com.mongodb.client.MongoCollection
 *  com.mongodb.client.MongoDatabase
 *  com.mongodb.client.result.UpdateResult
 *  com.vinplay.vbee.common.messages.dvt.CashoutByBankMessage
 *  com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory
 *  com.vinplay.vbee.common.pools.ConnectionPool
 *  com.vinplay.vbee.common.utils.VinPlayUtils
 *  org.bson.Document
 *  org.bson.conversions.Bson
 */
package com.vinplay.dichvuthe.dao.impl;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import com.vinplay.dichvuthe.dao.CashoutDao;
import com.vinplay.dichvuthe.entities.*;
import com.vinplay.dichvuthe.response.CashoutTransResponse;
import com.vinplay.dichvuthe.response.CashoutUserDailyResponse;
import com.vinplay.dichvuthe.utils.CashoutUtil;
import com.vinplay.dichvuthe.utils.DvtConst;
import com.vinplay.payment.entities.UserWithDrawCard;
import com.vinplay.payment.entities.UserWithdraw;
import com.vinplay.payment.entities.UserWithdrawMomo;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.vbee.common.messages.dvt.CashoutByBankMessage;
import com.vinplay.vbee.common.messages.dvt.CashoutByCardMessage;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.pools.ConnectionPool;
import com.vinplay.vbee.common.utils.VinPlayUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;

public class CashoutDaoImpl
        implements CashoutDao {
    @Override
    public long getSystemCashout() throws SQLException {
        long money = 0L;

        String sql = "SELECT money FROM system_cashout WHERE date = ?";

        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
             PreparedStatement stm = conn.prepareStatement(sql)) {

            stm.setString(1, VinPlayUtils.getCurrentDate());

            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    money = rs.getLong("money");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Consider using a logging framework
            throw e; // Re-throw the exception to be handled by the calling code
        }

        return money;
    }


    @Override
    public boolean updateSystemCashout(long money) throws SQLException {
        boolean res = false;

        String sql = "INSERT INTO system_cashout(date, money, update_time) " +
                "VALUES(?, ?, now()) " +
                "ON DUPLICATE KEY UPDATE money = money + ?, update_time = now()";

        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
             PreparedStatement stm = conn.prepareStatement(sql)) {

            stm.setString(1, VinPlayUtils.getCurrentDate());
            stm.setLong(2, money);
            stm.setLong(3, money);

            int rowsAffected = stm.executeUpdate();
            if (rowsAffected == 1) {
                res = true;
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Consider logging the exception instead
            throw e; // Re-throw exception to allow calling code to handle it
        }

        return res;
    }


    @Override
    public void logCashoutByBank(CashoutByBankMessage message) throws Exception {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("dvt_cash_out_by_bank");
        Document doc = new Document();
        doc.append("reference_id", (Object) message.getReferenceId());
        doc.append("nick_name", (Object) message.getNickname());
        doc.append("bank", (Object) message.getBank());
        doc.append("account", (Object) message.getAccount());
        doc.append("name", (Object) message.getName());
        doc.append("amount", (Object) message.getAmount());
        doc.append("status", (Object) message.getStatus());
        doc.append("message", (Object) message.getMessage());
        doc.append("sign", (Object) message.getSign());
        doc.append("code", (Object) message.getCode());
        doc.append("description", (Object) message.getDesc());
        doc.append("time_log", (Object) message.getCreateTime());
        doc.append("update_time", (Object) message.getCreateTime());
        col.insertOne((Object) doc);
    }

    @Override
    public BankAccountInfo getBankAccountInfo(String nickname) throws SQLException {
        BankAccountInfo info = null;

        String sql = "SELECT * FROM useragent WHERE nickname=? AND parentid = -1 AND active = 1";

        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_admin");
             PreparedStatement stm = conn.prepareStatement(sql)) {

            stm.setString(1, nickname);

            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    String bank = rs.getString("namebank");
                    String name = rs.getString("nameaccount");
                    String account = rs.getString("numberaccount");
                    info = new BankAccountInfo(bank, name, account);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Consider logging the exception instead
            throw e; // Re-throw exception to allow calling code to handle it
        }

        return info;
    }


    @Override
    public CashoutUserDailyResponse getCashoutUserToday(String nickname) {
        CashoutUserDailyResponse model = new CashoutUserDailyResponse(new Date(), 0);
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("log_cash_out_user_daily");
            if (col != null) {
                HashMap<String, Object> conditions = new HashMap<String, Object>();
//                HashMap<String, String> conditions = new HashMap<String, String>();
                conditions.put("nick_name", nickname);
                conditions.put("date", VinPlayUtils.getCurrentDate());
                Document doccument = (Document) col.find((Bson) new Document(conditions)).first();
                if (doccument != null) {
                    model = new CashoutUserDailyResponse(VinPlayUtils.getDateTimeFromDate((String) doccument.getString((Object) "date")), doccument.getInteger((Object) "money"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return model;
    }

    @Override
    public List<CashoutTransResponse> getListCashoutByCardPending(String partner, String startTime, String endTime) throws Exception {
        final ArrayList<CashoutTransResponse> response = new ArrayList<CashoutTransResponse>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        FindIterable iterable = null;
        Document conditions = new Document();
        conditions.put("code", (Object) 30);
        conditions.put("partner", (Object) partner);
        if (!startTime.isEmpty() && !endTime.isEmpty()) {
            BasicDBObject obj = new BasicDBObject();
            obj.put("$gte", (Object) startTime);
            obj.put("$lte", (Object) endTime);
            conditions.put("time_log", (Object) obj);
        }
        iterable = db.getCollection("dvt_cash_out_by_card").find((Bson) conditions);
        iterable.forEach((Block) new Block<Document>() {

            public void apply(Document document) {
                CashoutTransResponse message = new CashoutTransResponse(document.getString((Object) "reference_id"), document.getString((Object) "provider"), document.getInteger((Object) "amount"), document.getInteger((Object) "quantity"), document.getString((Object) "partner"), document.getString((Object) "partner_trans_id"), document.getInteger((Object) "is_scanned"), document.getInteger((Object) "code"), document.getString((Object) "message"), document.getInteger((Object) "status"), document.getString((Object) "softpin"), document.getString((Object) "nick_name"));
                response.add(message);
            }
        });
        return response;
    }

    @Override
    public List<CashoutTransResponse> getListCashoutByCardPending() throws Exception {
        final ArrayList<CashoutTransResponse> response = new ArrayList<CashoutTransResponse>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        FindIterable iterable = null;
        Document conditions = new Document();
        conditions.put("code", (Object) 30);
        conditions.put("time_log", (Object) new BasicDBObject("$gte", (Object) VinPlayUtils.parseDateTimeToString((Date) VinPlayUtils.subtractDay((Date) new Date(), (int) GameCommon.getValueInt("TIME_RECHECK_CASHOUT_BY_CARD")))));
        iterable = db.getCollection("dvt_cash_out_by_card").find((Bson) conditions);
        iterable.forEach((Block) new Block<Document>() {

            public void apply(Document document) {
                CashoutTransResponse message = new CashoutTransResponse(document.getString((Object) "reference_id"), document.getString((Object) "provider"), document.getInteger((Object) "amount"), document.getInteger((Object) "quantity"), document.getString((Object) "partner"), document.getString((Object) "partner_trans_id"), document.getInteger((Object) "is_scanned"), document.getInteger((Object) "code"), document.getString((Object) "message"), document.getInteger((Object) "status"), document.getString((Object) "softpin"), document.getString((Object) "nick_name"));
                response.add(message);
            }
        });
        return response;
    }

    @Override
    public void updateCashOutByCard(String referenceId, String softpin, int status, String message, int code, int isScanned) throws Exception {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        BasicDBObject updateFields = new BasicDBObject();
        updateFields.append("softpin", (Object) softpin);
        updateFields.append("status", (Object) status);
        updateFields.append("message", (Object) message);
        updateFields.append("code", (Object) code);
        updateFields.append("is_scanned", (Object) isScanned);
        updateFields.append("update_time", (Object) VinPlayUtils.getCurrentDateTime());
        db.getCollection("dvt_cash_out_by_card").updateOne((Bson) new Document("reference_id", (Object) referenceId), (Bson) new Document("$set", (Object) updateFields));
    }

    @Override
    public void insertCardIntoDB(String provider, int amount, String serial, String pin, String expire) throws Exception {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("card_store");
        Document doc = new Document();
        doc.append("reference_id", (Object) VinPlayUtils.genMessageId());
        doc.append("provider", (Object) provider);
        doc.append("amount", (Object) amount);
        doc.append("serial", (Object) serial);
        doc.append("pin", (Object) pin);
        doc.append("expire_time", (Object) expire);
        doc.append("status", (Object) 0);
        doc.append("message", (Object) "Th\u00e1\u00ba\u00bb ch\u00c6\u00b0a s\u00e1\u00bb\u00ad d\u00e1\u00bb\u00a5ng");
        doc.append("create_time", (Object) VinPlayUtils.getCurrentDateTime());
        doc.append("update_time", (Object) VinPlayUtils.getCurrentDateTime());
        doc.append("partner_trans_id", (Object) "");
        col.insertOne((Object) doc);
    }

    @Override
    public boolean InsertCashoutByBankManual(UserWithdraw userWithdraw) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection(CashoutUtil.CASHOUT_BY_BANK_COLLECTION);
            Gson gson = new Gson();
            String json = gson.toJson(userWithdraw);
            // Parse to bson document and insert
            Document doc = Document.parse(json);

            col.insertOne((Object) doc);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean InsertCashoutByMomoManual(UserWithdrawMomo userWithdrawMomo) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection(CashoutUtil.CASHOUT_BY_MOMO_COLLECTION);
            Gson gson = new Gson();
            String json = gson.toJson(userWithdrawMomo);
            Document doc = Document.parse(json);
            col.insertOne((Object) doc);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean InsertCashoutByCardManual(UserWithDrawCard userWithDrawCard) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection(CashoutUtil.CASHOUT_BY_CARD_COLLECTION);
            Gson gson = new Gson();

            String json = gson.toJson(userWithDrawCard);
            // Parse to bson document and insert
            Document doc = Document.parse(json);

            col.insertOne((Object) doc);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public CashoutBankResponse GetListCashoutBank(UserWithdraw userWithdraw, int page, int maxItem, String fromTime, String endTime) {
        try {
            final ArrayList<UserWithdraw> records = new ArrayList<UserWithdraw>();
            final ArrayList<Long> num = new ArrayList<Long>();
            num.add(0, 0L);
            num.add(1, 0L);
            num.add(2, 0L);
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection(CashoutUtil.CASHOUT_BY_BANK_COLLECTION);
            page = (page - 1) < 0 ? 0 : (page - 1);
            int numStart = page * maxItem;
            int numEnd = maxItem;
            BasicDBObject objsort = new BasicDBObject();
            objsort.put("_id", -1);
            HashMap<String, Object> conditions = new HashMap<String, Object>();
            if (userWithdraw.Username != null && !userWithdraw.Username.isEmpty()) {
                String pattern = ".*" + userWithdraw.Username + ".*";
                conditions.put("Username", (Object) new BasicDBObject().append("$regex", (Object) pattern).append("$options", (Object) "i"));
            }

            if (userWithdraw.BankAccountNumber != null && !userWithdraw.BankAccountNumber.isEmpty()) {
                String pattern = ".*" + userWithdraw.BankAccountNumber + ".*";
                conditions.put("BankAccountNumber", (Object) new BasicDBObject().append("$regex", (Object) pattern).append("$options", (Object) "i"));
            }

            if (userWithdraw.BankAccountName != null && !userWithdraw.BankAccountName.isEmpty()) {
                String pattern = ".*" + userWithdraw.BankAccountName + ".*";
                conditions.put("BankAccountName", (Object) new BasicDBObject().append("$regex", (Object) pattern).append("$options", (Object) "i"));
            }


            if (userWithdraw.Id != null && !userWithdraw.Id.isEmpty()) {
                conditions.put("Id", userWithdraw.Id);
            }
            if (userWithdraw.Status != null && !userWithdraw.Status.isEmpty()) {
                conditions.put("Status", userWithdraw.Status);
            }
            if (!fromTime.isEmpty() && !endTime.isEmpty()) {
                BasicDBObject obj = new BasicDBObject();
                obj.put("$gte", (Object) fromTime);
                obj.put("$lte", (Object) endTime);
                conditions.put("CreatedAt", (Object) obj);
            }
            FindIterable iterable = col.find((Bson) new Document(conditions)).sort((Bson) objsort).skip(numStart).limit(maxItem);
            iterable.forEach((Block) new Block<Document>() {

                public void apply(Document document) {

                    Gson gson = new Gson();
                    UserWithdraw model = gson.fromJson(document.toJson(), UserWithdraw.class);

                    records.add(model);
                }
            });
            FindIterable iterable2 = col.find((Bson) new Document(conditions));
            iterable2.forEach((Block) new Block<Document>() {

                public void apply(Document document) {
                    int amount = document.getInteger((Object) "Amount");
                    String status = document.getString((Object) "Status");
                    long count = (Long) num.get(2) + 1L;

                    num.set(2, count);
                    long totalAmount = (Long) num.get(0) + amount;
                    num.set(0, totalAmount);

                    if (status.equals(CashoutUtil.STATUS_SUCCESS)) {
                        long moneySuccess = (Long) num.get(1) + amount;
                        num.set(1, moneySuccess);
                    }
                }
            });
            CashoutBankResponse res = new CashoutBankResponse(num.get(0), num.get(1), num.get(2), records);
            return res;


        } catch (Exception e) {
            return null;
        }
    }

    public CashoutBankResponse GetListCashoutBankExportFile(UserWithdraw userWithdraw, String fromTime, String endTime) {
        try {
            final ArrayList<UserWithdraw> records = new ArrayList<UserWithdraw>();
            final ArrayList<Long> num = new ArrayList<Long>();
            num.add(0, 0L);
            num.add(1, 0L);
            num.add(2, 0L);
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection(CashoutUtil.CASHOUT_BY_BANK_COLLECTION);

            BasicDBObject objsort = new BasicDBObject();
            objsort.put("_id", -1);
            HashMap<String, Object> conditions = new HashMap<String, Object>();
            if (userWithdraw.Username != null && !userWithdraw.Username.isEmpty()) {
                String pattern = ".*" + userWithdraw.Username + ".*";
                conditions.put("Username", (Object) new BasicDBObject().append("$regex", (Object) pattern).append("$options", (Object) "i"));
            }

            if (userWithdraw.BankAccountNumber != null && !userWithdraw.BankAccountNumber.isEmpty()) {
                String pattern = ".*" + userWithdraw.BankAccountNumber + ".*";
                conditions.put("BankAccountNumber", (Object) new BasicDBObject().append("$regex", (Object) pattern).append("$options", (Object) "i"));
            }

            if (userWithdraw.BankAccountName != null && !userWithdraw.BankAccountName.isEmpty()) {
                String pattern = ".*" + userWithdraw.BankAccountName + ".*";
                conditions.put("BankAccountName", (Object) new BasicDBObject().append("$regex", (Object) pattern).append("$options", (Object) "i"));
            }


            if (userWithdraw.Id != null && !userWithdraw.Id.isEmpty()) {
                conditions.put("Id", userWithdraw.Id);
            }
            if (userWithdraw.Status != null && !userWithdraw.Status.isEmpty()) {
                conditions.put("Status", userWithdraw.Status);
            }
            if (!fromTime.isEmpty() && !endTime.isEmpty()) {
                BasicDBObject obj = new BasicDBObject();
                obj.put("$gte", (Object) fromTime);
                obj.put("$lte", (Object) endTime);
                conditions.put("CreatedAt", (Object) obj);
            }
            FindIterable iterable = col.find((Bson) new Document(conditions)).sort((Bson) objsort);
            iterable.forEach((Block) new Block<Document>() {

                public void apply(Document document) {

                    Gson gson = new Gson();
                    UserWithdraw model = gson.fromJson(document.toJson(), UserWithdraw.class);

                    records.add(model);
                }
            });
            FindIterable iterable2 = col.find((Bson) new Document(conditions));
            iterable2.forEach((Block) new Block<Document>() {

                public void apply(Document document) {
                    int amount = document.getInteger((Object) "Amount");
                    String status = document.getString((Object) "Status");
                    long count = (Long) num.get(2) + 1L;

                    num.set(2, count);
                    long totalAmount = (Long) num.get(0) + amount;
                    num.set(0, totalAmount);

                    if (status.equals(CashoutUtil.STATUS_SUCCESS)) {
                        long moneySuccess = (Long) num.get(1) + amount;
                        num.set(1, moneySuccess);
                    }
                }
            });
            CashoutBankResponse res = new CashoutBankResponse(num.get(0), num.get(1), num.get(2), records);
            return res;


        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public CashoutCardResponse GetListCashoutCard(UserWithDrawCard userWithDrawCard, int page, int maxItem, String fromTime, String endTime) {
        try {
            final ArrayList<UserWithDrawCard> records = new ArrayList<UserWithDrawCard>();
            final ArrayList<Long> num = new ArrayList<Long>();
            num.add(0, 0L);
            num.add(1, 0L);
            num.add(2, 0L);
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection(CashoutUtil.CASHOUT_BY_CARD_COLLECTION);
            page = (page - 1) < 0 ? 0 : (page - 1);
            int numStart = page * maxItem;
            int numEnd = maxItem;
            BasicDBObject objsort = new BasicDBObject();
            objsort.put("_id", -1);
            HashMap<String, Object> conditions = new HashMap<String, Object>();
            if (userWithDrawCard.Username != null && !userWithDrawCard.Username.isEmpty()) {
                String pattern = ".*" + userWithDrawCard.Username + ".*";
                conditions.put("Username", (Object) new BasicDBObject().append("$regex", (Object) pattern).append("$options", (Object) "i"));
            }


            if (userWithDrawCard.telcoId != null && !userWithDrawCard.telcoId.isEmpty()) {
                String pattern = ".*" + userWithDrawCard.telcoId + ".*";
                conditions.put("telcoId", (Object) new BasicDBObject().append("$regex", (Object) pattern).append("$options", (Object) "i"));
            }


            if (userWithDrawCard.Id != null && !userWithDrawCard.Id.isEmpty()) {
                conditions.put("Id", userWithDrawCard.Id);
            }
            if (userWithDrawCard.Status != null && !userWithDrawCard.Status.isEmpty()) {
                conditions.put("Status", userWithDrawCard.Status);
            }
            if (!fromTime.isEmpty() && !endTime.isEmpty()) {
                BasicDBObject obj = new BasicDBObject();
                obj.put("$gte", (Object) fromTime);
                obj.put("$lte", (Object) endTime);
                conditions.put("CreatedAt", (Object) obj);
            }
            FindIterable iterable = col.find((Bson) new Document(conditions)).sort((Bson) objsort).skip(numStart).limit(maxItem);
            iterable.forEach((Block) new Block<Document>() {

                public void apply(Document document) {

                    Gson gson = new Gson();
                    UserWithDrawCard model = gson.fromJson(document.toJson(), UserWithDrawCard.class);

                    records.add(model);
                }
            });
            FindIterable iterable2 = col.find((Bson) new Document(conditions));
            iterable2.forEach((Block) new Block<Document>() {

                public void apply(Document document) {
                    int amount = document.getInteger((Object) "Amount");
                    String status = document.getString((Object) "Status");
                    long count = (Long) num.get(2) + 1L;

                    num.set(2, count);
                    long totalAmount = (Long) num.get(0) + amount;
                    num.set(0, totalAmount);

                    if (status.equals(CashoutUtil.STATUS_SUCCESS)) {
                        long moneySuccess = (Long) num.get(1) + amount;
                        num.set(1, moneySuccess);
                    }
                }
            });
            CashoutCardResponse res = new CashoutCardResponse(num.get(0), num.get(1), num.get(2), records);
            return res;


        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public CashoutMomoResponse GetListCashoutMomo(UserWithdrawMomo userWithdrawMomo, int page, int maxItem, String fromTime, String endTime) {
        try {
            final ArrayList<UserWithdrawMomo> records = new ArrayList<UserWithdrawMomo>();
            final ArrayList<Long> num = new ArrayList<Long>();
            num.add(0, 0L);
            num.add(1, 0L);
            num.add(2, 0L);
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection(CashoutUtil.CASHOUT_BY_MOMO_COLLECTION);
            page = (page - 1) < 0 ? 0 : (page - 1);
            int numStart = page * maxItem;
            int numEnd = maxItem;
            BasicDBObject objsort = new BasicDBObject();
            objsort.put("_id", -1);
            HashMap<String, Object> conditions = new HashMap<String, Object>();
            if (userWithdrawMomo.Nickname != null && !userWithdrawMomo.Nickname.isEmpty()) {
                String pattern = ".*" + userWithdrawMomo.Nickname + ".*";
                conditions.put("Nickname", (Object) new BasicDBObject().append("$regex", (Object) pattern).append("$options", (Object) "i"));
            }

            if (userWithdrawMomo.PhoneNumber != null && !userWithdrawMomo.PhoneNumber.isEmpty()) {
                String pattern = ".*" + userWithdrawMomo.PhoneNumber + ".*";
                conditions.put("PhoneNumber", (Object) new BasicDBObject().append("$regex", (Object) pattern).append("$options", (Object) "i"));
            }


            if (userWithdrawMomo.Id != null && !userWithdrawMomo.Id.isEmpty()) {
                conditions.put("Id", userWithdrawMomo.Id);
            }
            if (userWithdrawMomo.Status != null && !userWithdrawMomo.Status.isEmpty()) {
                conditions.put("Status", userWithdrawMomo.Status);
            }
            if (fromTime != null && !fromTime.isEmpty() && endTime != null && !endTime.isEmpty()) {
                BasicDBObject obj = new BasicDBObject();
                obj.put("$gte", (Object) fromTime);
                obj.put("$lte", (Object) endTime);
                conditions.put("CreatedAt", (Object) obj);
            }
            FindIterable iterable = col.find((Bson) new Document(conditions)).sort((Bson) objsort).skip(numStart).limit(maxItem);
            iterable.forEach((Block) new Block<Document>() {

                public void apply(Document document) {

                    Gson gson = new Gson();
                    UserWithdrawMomo model = gson.fromJson(document.toJson(), UserWithdrawMomo.class);

                    records.add(model);
                }
            });
            FindIterable iterable2 = col.find((Bson) new Document(conditions));
            iterable2.forEach((Block) new Block<Document>() {

                public void apply(Document document) {
                    int amount = document.getInteger((Object) "Amount");
                    String status = document.getString((Object) "Status");
                    long count = (Long) num.get(2) + 1L;

                    num.set(2, count);
                    long totalAmount = (Long) num.get(0) + amount;
                    num.set(0, totalAmount);

                    if (status.equals(CashoutUtil.STATUS_SUCCESS)) {
                        long moneySuccess = (Long) num.get(1) + amount;
                        num.set(1, moneySuccess);
                    }
                }
            });
            CashoutMomoResponse res = new CashoutMomoResponse(num.get(0), num.get(1), num.get(2), records);
            return res;


        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean UpdateCashoutBank(String Id, String status, String userApprove) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            Document conditions = new Document();
            conditions.put("Id", (Object) Id);
            BasicDBObject updateFields = new BasicDBObject();
            updateFields.append("Status", (Object) status);
            updateFields.append("UserProve", (Object) userApprove);
            //us
            db.getCollection(CashoutUtil.CASHOUT_BY_BANK_COLLECTION).updateOne(conditions, (Bson) new Document("$set", (Object) updateFields));
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    @Override
    public boolean UpdateCashoutMomo(String Id, String status, String userApprove) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            Document conditions = new Document();
            conditions.put("Id", (Object) Id);
            BasicDBObject updateFields = new BasicDBObject();
            updateFields.append("Status", (Object) status);
            updateFields.append("UserApprove", (Object) userApprove);
            //us
            db.getCollection(CashoutUtil.CASHOUT_BY_MOMO_COLLECTION).updateOne(conditions, (Bson) new Document("$set", (Object) updateFields));
            return true;
        } catch (Exception e) {
            return false;
        }
    }



    @Override
    public boolean UpdateCashoutCard(String Id, String status, String userApprove, String Seri, String Pin) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            Document conditions = new Document();
            conditions.put("Id", (Object) Id);
            BasicDBObject updateFields = new BasicDBObject();
            updateFields.append("Status", (Object) status);
            updateFields.append("UserProve", (Object) userApprove);
            updateFields.append("Seri", (Object) Seri);
            updateFields.append("Pin", (Object) Pin);
            //us
            db.getCollection(CashoutUtil.CASHOUT_BY_CARD_COLLECTION).updateOne(conditions, (Bson) new Document("$set", (Object) updateFields));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getCashoutBankStatus(String Id) {
        return null;
    }

    @Override
    public String getCashoutMomoStatus(String Id) {
        return null;
    }

    @Override
    public UserWithDrawCard FindCashoutCardById(String Id) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            Document conditions = new Document();
            conditions.put("Id", (Object) Id);
            Document document = (Document) db.getCollection(CashoutUtil.CASHOUT_BY_CARD_COLLECTION).find((Bson) conditions).first();
            if (document == null)
                return null;
            Gson gson = new Gson();
            UserWithDrawCard model = gson.fromJson(document.toJson(), UserWithDrawCard.class);
            return model;
        } catch (Exception e) {
            RechargeDaoImpl.logger.error(e);
            return null;
        }
    }

    @Override
    public UserWithdraw FindCashoutBankById(String Id) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            Document conditions = new Document();
            conditions.put("Id", (Object) Id);
            Document document = (Document) db.getCollection(CashoutUtil.CASHOUT_BY_BANK_COLLECTION).find((Bson) conditions).first();
            if (document == null)
                return null;
            Gson gson = new Gson();
            UserWithdraw model = gson.fromJson(document.toJson(), UserWithdraw.class);
            return model;
        } catch (Exception e) {
            RechargeDaoImpl.logger.error(e);
            return null;
        }
    }

    @Override
    public UserWithdrawMomo FindCashoutMomoById(String Id) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            Document conditions = new Document();
            conditions.put("Id", (Object) Id);
            Document document = (Document) db.getCollection(CashoutUtil.CASHOUT_BY_MOMO_COLLECTION).find((Bson) conditions).first();
            if (document == null)
                return null;
            Gson gson = new Gson();
            UserWithdrawMomo model = gson.fromJson(document.toJson(), UserWithdrawMomo.class);
            return model;
        } catch (Exception e) {
            RechargeDaoImpl.logger.error(e);
            return null;
        }
    }

    @Override
    public CashoutByCardMessage FindLogByTimeAndNickname(String time, String nickName) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            Document conditions = new Document();
            conditions.put("time_log", (Object) time);
            conditions.put("nick_name", (Object) nickName);
            Document document = (Document) db.getCollection("dvt_cash_out_by_card").find((Bson) conditions).first();
            if (document == null)
                return null;
            Gson gson = new Gson();
            CashoutByCardMessage model = gson.fromJson(document.toJson(), CashoutByCardMessage.class);
            return model;
        } catch (Exception e) {
            RechargeDaoImpl.logger.error(e);
            return null;
        }
    }

    @Override
    public int getTotalCashOutBankByNickname(String nickname) {
        int total = 0;
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection<Document> col = db.getCollection(CashoutUtil.CASHOUT_BY_BANK_COLLECTION);

            HashMap<String, Object> conditions = new HashMap<>();
            conditions.put("Username", nickname);
            conditions.put("Status", "success");
            FindIterable<Document> iterable = col.find(new Document(conditions));
            for (Document document : iterable) {
                total += document.getInteger("Amount", 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

        return total;
    }

    @Override
    public int getTotalCashOutMomoByNickname(String nickname) {
        int total = 0;
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection<Document> col = db.getCollection(CashoutUtil.CASHOUT_BY_MOMO_COLLECTION);

            HashMap<String, Object> conditions = new HashMap<>();
            conditions.put("Nickname", nickname);
            conditions.put("Status", "success");
            FindIterable<Document> iterable = col.find(new Document(conditions));
            for (Document document : iterable) {
                total += document.getInteger("Amount", 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

        return total;
    }

    @Override
    public CashoutBankResponse getListCashoutBankSuccess(String fromTime, String endTime) {
        try {
            final ArrayList<UserWithdraw> records = new ArrayList<UserWithdraw>();
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection(CashoutUtil.CASHOUT_BY_BANK_COLLECTION);
            HashMap<String, Object> conditions = new HashMap<String, Object>();
            conditions.put("Status", "success");

            if (!fromTime.isEmpty() && !endTime.isEmpty()) {
                fromTime += " 00:00:00";
                endTime += " 23:59:59";
                BasicDBObject obj = new BasicDBObject();
                obj.put("$gte", fromTime);
                obj.put("$lte", endTime);
                conditions.put("CreatedAt", obj);
            }
            FindIterable iterable = col.find((Bson) new Document(conditions));
            iterable.forEach((Block) new Block<Document>() {
                public void apply(Document document) {
                    Gson gson = new Gson();
                    UserWithdraw model = gson.fromJson(document.toJson(), UserWithdraw.class);
                    records.add(model);
                }
            });
            CashoutBankResponse res = new CashoutBankResponse(0,0,0, records);
            return res;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public CashoutMomoResponse getListCashoutMomoSuccess(String fromTime, String endTime) {
        try {
            final ArrayList<UserWithdrawMomo> records = new ArrayList<UserWithdrawMomo>();
            final ArrayList<Long> num = new ArrayList<Long>();
            num.add(0, 0L);
            num.add(1, 0L);
            num.add(2, 0L);
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection(CashoutUtil.CASHOUT_BY_MOMO_COLLECTION);
            HashMap<String, Object> conditions = new HashMap<String, Object>();
            conditions.put("Status", "success");
            if (fromTime != null && !fromTime.isEmpty() && endTime != null && !endTime.isEmpty()) {
                BasicDBObject obj = new BasicDBObject();
                fromTime += " 00:00:00";
                endTime += " 23:59:59";
                obj.put("$gte", fromTime);
                obj.put("$lte", endTime);
                conditions.put("CreatedAt", (Object) obj);
            }
            FindIterable iterable = col.find((Bson) new Document(conditions));
            iterable.forEach((Block) new Block<Document>() {
                public void apply(Document document) {
                    Gson gson = new Gson();
                    UserWithdrawMomo model = gson.fromJson(document.toJson(), UserWithdrawMomo.class);
                    records.add(model);
                }
            });
            CashoutMomoResponse res = new CashoutMomoResponse(0,0,0, records);
            return res;


        } catch (Exception e) {
            return null;
        }
    }


    public static void main(String[] args) {
        System.out.println("{\"TableStartingBlinds\":[1,10000,100000,1000000],\"TableBulletValueRate\":[1,1,10,100],\"TableRequireCardIn\":[0,0,0,0],\"NumberOfAccountPerDevice\":1000,\"NumberOfAccountPerDay\":3,\"RequestSampleRateMs\":2000,\"MaxRequestPerSecond\":15,\"LogShooting\":1,\"MinTimeBetweenShooting\":125,\"TimeBetweenVideoAdsMs0\":10000,\"TimeBetweenVideoAdsMs1\":10000,\"VideoAdsRewardCount0\":3,\"VideoAdsRewardCount1\":5,\"VideoAdsRewardType0\":100,\"VideoAdsRewardType1\":50,\"KickDuplicateUsers\":1,\"HideFishHp\":1,\"PowerUpIntervalS\":300,\"PowerUpMinPlayTimeS\":120,\"PowerUpDurationS\":15,\"BombRate\":0,\"BonusRate\":45,\"FastFireCoolDownS\":1,\"FastFireRate\":2,\"FastFireDuration\":10,\"FastFireCost\":20,\"SnipeCoolDownS\":1,\"SnipeDurationS\":10,\"SnipeCost\":20,\"FeeRate\":0.02,\"BombThreshold\":5000,\"JackpotInitial\":7300,\"JackpotRate\":0.02,\"FIRE_RATE\":4,\"TURN_TIME\":0.25,\"MIN_SPEED_2\":0.01,\"MAX_JUMP_SPEED\":150,\"MAX_JUMP_SPEED_2\":22500,\"MAX_SHADOW_SPEED_2\":25600,\"TELEPORT_SHADOW_SPEED_2\":250000,\"JUMP_ACCELERATION_OCTOPUS\":150,\"JUMP_DECCELERATION_OCTOPUS\":150,\"JUMP_ACCELERATION_CUTTLE\":150,\"JUMP_DECCELERATION_CUTTLE\":150,\"JUMP_ACCELERATION_SEA_TURTLE\":150,\"JUMP_DECCELERATION_SEA_TURTLE\":150,\"MIN_SPEED\":40,\"MAX_VARY_SPEED\":80,\"TURN_ANGLE_RAD\":0.01744444,\"JUMP_TURN_ANGLE_RAD\":0.01744444,\"MAX_BOUND_TIME\":4,\"BulletSpeed\":1400,\"BulletRadius\":22,\"MaxIdleTimeS\":120,\"a\":8,\"b\":32,\"c\":1024,\"d\":2048,\"MinScale\":1,\"MaxScale\":1.05,\"MaxFillScale\":1.2,\"NUMBER_OF_BULLETS\":100,\"NUMBER_OF_OBJECTS\":60,\"PLAYING_WAVE_DURATION\":600,\"NEW_WAVE_MAX_TIME\":120,\"SOLO_DURATION\":120,\"WAITING_WAVE_DURATION\":30,\"GUN_LENGTH\":120,\"GOLDEN_FROG_WIN_RATE\":[1,5,12,16,10],\"GOLDEN_FROG_WIN_MULTIPLE\":[5,4,3,2,0.5],\"ScreenW\":1280,\"ScreenH\":720,\"ScreenX\":-640,\"ScreenY\":-360,\"WorldW\":2400,\"WorldH\":1700,\"WorldX\":-1200,\"WorldY\":-850,\"OutWorldW\":5000,\"OutWorldH\":4000,\"OutWorldX\":-2500,\"OutWorldY\":-2000,\"SpawnW\":2000,\"SpawnH\":1600,\"SpawnX\":-1000,\"SpawnY\":-800,\"SERVER_UPDATE_LOOP_MS\":17,\"CLIENT_UPDATE_S\":0.033,\"playerPos\":[{\"x\":-320,\"y\":-320},{\"x\":320,\"y\":-320},{\"x\":320,\"y\":320},{\"x\":-320,\"y\":320}],\"TypeToPower\":{\"Basic\":1,\"Bullet1\":100,\"Bullet2\":100,\"Bullet3\":100,\"Bullet4\":100,\"Bullet5\":100,\"Bullet6\":100},\"TypeToValue\":{\"Basic\":0,\"Bullet1\":10,\"Bullet2\":20,\"Bullet3\":50,\"Bullet4\":100,\"Bullet5\":200,\"Bullet6\":500},\"FishPhysicalData\":{\"Basic\":{\"Width\":2,\"Height\":2,\"Health\":0,\"HealthRate\":1,\"HealthScale\":0.2},\"Cuttle\":{\"Width\":22,\"Height\":46,\"Health\":200,\"HealthRate\":1,\"HealthScale\":0.1},\"GoldFish\":{\"Width\":29,\"Height\":49,\"Health\":300,\"HealthRate\":1,\"HealthScale\":0.1},\"LightenFish\":{\"Width\":22,\"Height\":46,\"Health\":400,\"HealthRate\":0.9,\"HealthScale\":0.1},\"Mermaid\":{\"Width\":33,\"Height\":45,\"Health\":500,\"HealthRate\":0.95,\"HealthScale\":0.1},\"Octopus\":{\"Width\":32,\"Height\":61,\"Health\":600,\"HealthRate\":1,\"HealthScale\":0.1},\"PufferFish\":{\"Width\":27,\"Height\":41,\"Health\":700,\"HealthRate\":0.8,\"HealthScale\":0.1},\"SeaFish\":{\"Width\":30,\"Height\":58,\"Health\":800,\"HealthRate\":0.8,\"HealthScale\":0.2},\"Shark\":{\"Width\":46,\"Height\":61,\"Health\":900,\"HealthRate\":0.9,\"HealthScale\":0.1},\"Stringray\":{\"Width\":37,\"Height\":82,\"Health\":1000,\"HealthRate\":0.95,\"HealthScale\":0.1},\"Turtle\":{\"Width\":26,\"Height\":66,\"Health\":1100,\"HealthRate\":0.7,\"HealthScale\":0.1},\"CaThanTai\":{\"Width\":28,\"Height\":64,\"Health\":1200,\"HealthRate\":1,\"HealthScale\":0.2},\"FlyingFish\":{\"Width\":35,\"Height\":72,\"Health\":1300,\"HealthRate\":0.9,\"HealthScale\":0.1},\"GoldenFrog\":{\"Width\":100,\"Height\":436,\"Health\":6600,\"HealthRate\":1,\"HealthScale\":0.1},\"SeaTurtle\":{\"Width\":100,\"Height\":50,\"Health\":1400,\"HealthRate\":1,\"HealthScale\":0.1},\"MerMan\":{\"Width\":46,\"Height\":62,\"Health\":1500,\"HealthRate\":1,\"HealthScale\":0.1},\"Phoenix\":{\"Width\":61,\"Height\":63,\"Health\":1600,\"HealthRate\":1,\"HealthScale\":0.1},\"MermaidBig\":{\"Width\":100,\"Height\":88,\"Health\":1700,\"HealthRate\":0.98,\"HealthScale\":0.1},\"MermaidSmall\":{\"Width\":40,\"Height\":143,\"Health\":1800,\"HealthRate\":1,\"HealthScale\":0.1},\"BombFish\":{\"Width\":61,\"Height\":175,\"Health\":5400,\"HealthRate\":1,\"HealthScale\":0.1},\"Fish19\":{\"Width\":46,\"Height\":175,\"Health\":6000,\"HealthRate\":1,\"HealthScale\":0.1},\"Fish20\":{\"Width\":50,\"Height\":145,\"Health\":6300,\"HealthRate\":1,\"HealthScale\":0.1},\"Fish21\":{\"Width\":56,\"Height\":175,\"Health\":6600,\"HealthRate\":1,\"HealthScale\":0.1},\"Fish22\":{\"Width\":63,\"Height\":187,\"Health\":6900,\"HealthRate\":1,\"HealthScale\":0.1},\"Fish23\":{\"Width\":100,\"Height\":261,\"Health\":7200,\"HealthRate\":1,\"HealthScale\":0.1},\"Fish24\":{\"Width\":100,\"Height\":301,\"Health\":7500,\"HealthRate\":1,\"HealthScale\":0.1},\"Fish25\":{\"Width\":100,\"Height\":301,\"Health\":8500,\"HealthRate\":1,\"HealthScale\":0.1}},\"FakeJpMinTimeMS\":{\"11\":300000,\"12\":300000,\"13\":300000,\"14\":300000,\"21\":600000,\"22\":600000,\"23\":600000,\"24\":600000,\"31\":3600000,\"32\":3600000,\"33\":3600000,\"34\":3600000},\"FakeJpMaxTimeMS\":{\"11\":1800000,\"12\":1800000,\"13\":1800000,\"14\":1800000,\"21\":3600000,\"22\":3600000,\"23\":3600000,\"24\":3600000,\"31\":21600000,\"32\":21600000,\"33\":21600000,\"34\":21600000},\"MinAddFakeJp\":{\"11\":100,\"12\":100,\"13\":100,\"14\":100,\"21\":300,\"22\":300,\"23\":300,\"24\":300,\"31\":200,\"32\":200,\"33\":200,\"34\":200},\"MaxAddFakeJp\":{\"11\":500,\"12\":500,\"13\":500,\"14\":500,\"21\":1500,\"22\":1500,\"23\":1500,\"24\":1500,\"31\":1000,\"32\":1000,\"33\":1000,\"34\":1000},\"SoloFee\":0.05,\"SoloSnipe\":3,\"SoloFastFire\":3,\"SoloBomb\":3,\"SoloItemBombDamage\":300,\"TableSoloCashIn\":[1,5000,50000,500000],\"TableSoloVirtualCash\":[1,5000,50000,500000],\"UserCardInOnOff\":1,\"UserHighCash\":20000,\"UserMidCash\":10000,\"UserCardInHighCash\":20000,\"UserCardInMidCash\":10000,\"MinimumRefundUserBankRate\":[1.3,1.2,1.1],\"MinimumRefundUserCardInBankRate\":[1.2,1.1,1]}");
    }
}


