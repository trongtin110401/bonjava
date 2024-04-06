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
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");) {
            String sql = "SELECT money FROM system_cashout WHERE date=?";
            PreparedStatement stm = conn.prepareStatement("SELECT money FROM system_cashout WHERE date=?");
            stm.setString(1, VinPlayUtils.getCurrentDate());
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                money = rs.getLong("money");
            }
            rs.close();
            stm.close();
        }
        return money;
    }

    @Override
    public boolean updateSystemCashout(long money) throws SQLException {
        boolean res = false;
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");) {
            String sql = "INSERT INTO system_cashout(date, money, update_time) VALUES(?, ?, now()) ON DUPLICATE KEY UPDATE money=money+?, update_time=now()";
            PreparedStatement stm = conn.prepareStatement("INSERT INTO system_cashout(date, money, update_time) VALUES(?, ?, now()) ON DUPLICATE KEY UPDATE money=money+?, update_time=now()");
            stm.setString(1, VinPlayUtils.getCurrentDate());
            stm.setLong(2, money);
            stm.setLong(3, money);
            if (stm.executeUpdate() == 1) {
                res = true;
            }
            stm.close();
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
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_admin");) {
            String sql = "SELECT * FROM useragent WHERE nickname=? AND parentid = -1 AND active = 1";
            PreparedStatement stm = conn.prepareStatement("SELECT * FROM useragent WHERE nickname=? AND parentid = -1 AND active = 1");
            stm.setString(1, nickname);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                String bank = rs.getString("namebank");
                String name = rs.getString("nameaccount");
                String account = rs.getString("numberaccount");
                info = new BankAccountInfo(bank, name, account);
            }
            rs.close();
            stm.close();
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
    public boolean UpdateCashoutCard(String Id, String status, String userApprove, String Seri , String Pin) {
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
}

