package com.vinplay.lognaprut.impl;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.dichvuthe.dao.impl.RechargeDaoImpl;
import com.vinplay.lognaprut.HistoryTransDao;
import com.vinplay.lognaprut.entities.HistoryTransModel;
import com.vinplay.lognaprut.entities.HistoryTransResponse;
import com.vinplay.lognaprut.entities.ReportAdminTransferMoneyResponse;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HistoryTransDaoImpl implements HistoryTransDao {


    @Override
    public void insertTransaction(HistoryTransModel historyTransModel) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("History_User_transaction");
        Document doc = new Document();
        long idelk = VinPlayUtils.generateTransId();
        String timeAt = VinPlayUtils.getCurrentDateTime();
        doc.append("Id", idelk);
        doc.append("giaodich", historyTransModel.getGiaodich());
        doc.append("congGiaoDich", historyTransModel.congGiaoDich);
        doc.append("hinhthuc", historyTransModel.hinhthuc);
        doc.append("sotien", historyTransModel.sotien);
        doc.append("trangthai", historyTransModel.trangthai);
        doc.append("ghiChu", historyTransModel.ghiChu);
        doc.append("nickName", historyTransModel.nickName);
        doc.append("hinhthucTrans", historyTransModel.hinhthucTrans);
        doc.append("transId", historyTransModel.transId);
        doc.append("createAt", timeAt);
        col.insertOne(doc);
    }

    @Override
    public HistoryTransModel findTransaction(String transId, String nickName, String hinhthucTrans) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            Document conditions = new Document();
            conditions.put("transId", (Object) transId);
            conditions.put("nickName", nickName);
            conditions.put("hinhthucTrans", hinhthucTrans);
            Document document = (Document) db.getCollection("History_User_transaction").find((Bson) conditions).first();
            if (document == null)
                return null;
            Gson gson = new Gson();
            HistoryTransModel model = gson.fromJson(document.toJson(), HistoryTransModel.class);
            model.setId(String.valueOf(document.getLong("Id")));
            return model;
        } catch (Exception e) {
            RechargeDaoImpl.logger.error(e);
            return null;
        }

    }

    @Override
    public HistoryTransModel findTransactionByTransId(String transId) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            Document conditions = new Document();
            conditions.put("transId", (Object) transId);
            Document document = (Document) db.getCollection("History_User_transaction").find((Bson) conditions).first();
            if (document == null)
                return null;
            Gson gson = new Gson();
            HistoryTransModel model = gson.fromJson(document.toJson(), HistoryTransModel.class);
            model.setId(String.valueOf(document.getLong("Id")));
            return model;
        } catch (Exception e) {
            RechargeDaoImpl.logger.error(e);
            return null;
        }
    }

    @Override
    public List<HistoryTransModel> findCodepayLike(String codepay) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            Document conditions = new Document();
            List<HistoryTransModel> results = new ArrayList<>();
            String pattern = ".*" + codepay;
            conditions.put("giaodich", (Object) new BasicDBObject().append("$regex", (Object) pattern).append("$options", (Object) "i"));
            FindIterable iterable = null;
            iterable = db.getCollection("History_User_transaction").find((Bson) new Document(conditions)).limit(50);
            iterable.forEach((Block) new Block<Document>() {

                public void apply(Document document) {
                    String giaodich = document.getString("giaodich");
                    String sotien = document.getString("sotien");
                    String trangthai = document.getString("trangthai");
                    String nickName = document.getString("nickName");
                    String hinhthucTrans = document.getString("hinhthucTrans");
                    String createAt = document.getString("createAt");
                    String transId = document.getString("transId");
                    String ghiChu = document.getString("ghiChu");
                    HistoryTransModel historyTransModel = new HistoryTransModel(giaodich, "", "", sotien, trangthai, ghiChu, nickName, hinhthucTrans, transId, "", createAt);
                    results.add(historyTransModel);
                }
            });
            return results;
        } catch (Exception e) {
            RechargeDaoImpl.logger.error(e);
            return null;
        }
    }

    @Override
    public boolean updateTransaction(HistoryTransModel historyTransModel) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            Document conditions = new Document();
            conditions.put("transId", (Object) historyTransModel.transId);
            conditions.put("nickName", (Object) historyTransModel.nickName);
            conditions.put("hinhthucTrans", (Object) historyTransModel.hinhthucTrans);
            BasicDBObject updateFields = new BasicDBObject();
            updateFields.append("trangthai", (Object) historyTransModel.trangthai);
            updateFields.append("ghiChu", (Object) historyTransModel.ghiChu);
            db.getCollection("History_User_transaction").updateOne(conditions, (Bson) new Document("$set", (Object) updateFields));
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    @Override
    public HistoryTransResponse getListTransByName(String nickName, int page, int maxItem) {
        try {
            HistoryTransResponse response = new HistoryTransResponse(true, "0");
            final ArrayList<HistoryTransModel> records = new ArrayList<HistoryTransModel>();
            final ArrayList<Long> num = new ArrayList<Long>();
            num.add(0, 0L);
            num.add(1, 0L);
            num.add(2, 0L);
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("History_User_transaction");
            page = (page - 1) < 0 ? 0 : (page - 1);
            int numStart = page * maxItem;
            int numEnd = maxItem;
            BasicDBObject objsort = new BasicDBObject();
            objsort.put("createAt", -1);
            HashMap<String, Object> conditions = new HashMap<String, Object>();
            if (nickName != null && !nickName.isEmpty()) {
                conditions.put("nickName", nickName.trim());
            }


            FindIterable iterable = col.find(new Document(conditions)).sort(objsort).skip(numStart).limit(maxItem);
            iterable.forEach((Block<Document>) document -> {
                Gson gson = new Gson();
                HistoryTransModel model = gson.fromJson(document.toJson(), HistoryTransModel.class);
                model.setId(document.getLong("Id").toString());
                model.setCreateAt(document.getString("createAt"));
                records.add(model);
            });


            FindIterable iterable2 = col.find(new Document(conditions));
            iterable2.forEach((Block) document -> {
                long count = (Long) num.get(0) + 1L;
                num.set(0, count);

            });

            int totalPages = (int) Math.ceil((double) Math.toIntExact(num.get(0)) / 5);
            response.setListTrans(records);
            response.setTotalpage(totalPages);
            return response;


        } catch (Exception e) {
            return null;
        }

    }

    @Override
    public HistoryTransResponse getListTransNapByName(String nickName, int page, int maxItem) {
        try {
            HistoryTransResponse response = new HistoryTransResponse(true, "0");
            final ArrayList<HistoryTransModel> records = new ArrayList<HistoryTransModel>();
            final ArrayList<Long> num = new ArrayList<Long>();
            num.add(0, 0L);
            num.add(1, 0L);
            num.add(2, 0L);
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("History_User_transaction");
            page = (page - 1) < 0 ? 0 : (page - 1);
            int numStart = page * maxItem;
            int numEnd = maxItem;
            BasicDBObject objsort = new BasicDBObject();
            objsort.put("createAt", -1);
            HashMap<String, Object> conditions = new HashMap<String, Object>();
            if (nickName != null && !nickName.isEmpty()) {
                conditions.put("nickName", nickName.trim());
                conditions.put("hinhthuc", "recharge");

                BasicDBObject obj = new BasicDBObject();
                obj.put("$gt", "0");
                conditions.put("sotien", obj );
            }


            FindIterable iterable = col.find(new Document(conditions)).sort(objsort).skip(numStart).limit(maxItem);
            iterable.forEach((Block<Document>) document -> {
                Gson gson = new Gson();
                HistoryTransModel model = gson.fromJson(document.toJson(), HistoryTransModel.class);
                model.setId(document.getLong("Id").toString());
                model.setCreateAt(document.getString("createAt"));
                records.add(model);
            });


            FindIterable iterable2 = col.find(new Document(conditions));
            iterable2.forEach((Block) document -> {
                long count = (Long) num.get(0) + 1L;
                num.set(0, count);
            });
            response.setListTrans(records);
            int totalPages = (int) Math.ceil((double) Math.toIntExact(num.get(0)) / 5);
            response.setTotalpage(totalPages);
            return response;


        } catch (Exception e) {
            return null;
        }

    }

    @Override
    public HistoryTransResponse getListTransRutTheByName(String nickName, int page, int maxItem) {
        try {
            HistoryTransResponse response = new HistoryTransResponse(true, "0");
            final ArrayList<HistoryTransModel> records = new ArrayList<HistoryTransModel>();
            final ArrayList<Long> num = new ArrayList<Long>();
            num.add(0, 0L);
            num.add(1, 0L);
            num.add(2, 0L);
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("History_User_transaction");
            page = (page - 1) < 0 ? 0 : (page - 1);
            int numStart = page * maxItem;
            int numEnd = maxItem;
            BasicDBObject objsort = new BasicDBObject();
            objsort.put("createAt", -1);
            HashMap<String, Object> conditions = new HashMap<String, Object>();
            if (nickName != null && !nickName.isEmpty()) {
                conditions.put("nickName", nickName.trim());
                conditions.put("hinhthucTrans", "RUT_CARD");
            }


            FindIterable iterable = col.find(new Document(conditions)).sort(objsort).skip(numStart).limit(maxItem);
            iterable.forEach((Block<Document>) document -> {
                Gson gson = new Gson();
                HistoryTransModel model = gson.fromJson(document.toJson(), HistoryTransModel.class);
                model.setId(document.getLong("Id").toString());
                model.setCreateAt(document.getString("createAt"));
                records.add(model);
            });


            FindIterable iterable2 = col.find(new Document(conditions));
            iterable2.forEach((Block) document -> {
                long count = (Long) num.get(0) + 1L;
                num.set(0, count);

            });

            response.setListTrans(records);
            int totalPages = (int) Math.ceil((double) Math.toIntExact(num.get(0)) / 5);
            response.setTotalpage(totalPages);
            return response;


        } catch (Exception e) {
            return null;
        }

    }

    @Override
    public HistoryTransResponse getListTransRutBankByName(String nickName, int page, int maxItem) {
        try {
            HistoryTransResponse response = new HistoryTransResponse(true, "0");
            final ArrayList<HistoryTransModel> records = new ArrayList<HistoryTransModel>();
            final ArrayList<Long> num = new ArrayList<Long>();
            num.add(0, 0L);
            num.add(1, 0L);
            num.add(2, 0L);
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("History_User_transaction");
            page = (page - 1) < 0 ? 0 : (page - 1);
            int numStart = page * maxItem;
            int numEnd = maxItem;
            BasicDBObject objsort = new BasicDBObject();
            objsort.put("_id", -1);
            HashMap<String, Object> conditions = new HashMap<String, Object>();
            if (nickName != null && !nickName.isEmpty()) {
                conditions.put("nickName", nickName.trim());
                conditions.put("hinhthucTrans", "RUT_BANK");
            }


            FindIterable iterable = col.find(new Document(conditions)).sort(objsort).skip(numStart).limit(maxItem);
            iterable.forEach((Block<Document>) document -> {
                Gson gson = new Gson();
                HistoryTransModel model = gson.fromJson(document.toJson(), HistoryTransModel.class);
                model.setId(document.getLong("Id").toString());
                model.setCreateAt(document.getString("createAt"));
                records.add(model);
            });


            FindIterable iterable2 = col.find(new Document(conditions));
            iterable2.forEach((Block) document -> {
                long count = (Long) num.get(0) + 1L;
                num.set(0, count);

            });

            response.setListTrans(records);
            int totalPages = (int) Math.ceil((double) Math.toIntExact(num.get(0)) / 5);
            response.setTotalpage(totalPages);
            return response;


        } catch (Exception e) {
            return null;
        }

    }


    /**
     * Minhanh add getListTransByDay
     */

    @Override
    public HistoryTransResponse getListTransByDay(String nickName, String fromTime, String endTime) {
        try {
            HistoryTransResponse response = new HistoryTransResponse(true, "0");
            final ArrayList<HistoryTransModel> records = new ArrayList<HistoryTransModel>();
            final ArrayList<Long> num = new ArrayList<Long>();
            num.add(0, 0L);
            num.add(1, 0L);
            num.add(2, 0L);
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("History_User_transaction");

            BasicDBObject objsort = new BasicDBObject();
            objsort.put("_id", -1);
            HashMap<String, Object> conditions = new HashMap<String, Object>();

            if (!fromTime.isEmpty() && !endTime.isEmpty()) {
                BasicDBObject obj = new BasicDBObject();
                obj.put("$gte", fromTime);
                obj.put("$lte", endTime);
                conditions.put("createAt", obj);
            }

            if (nickName != null && !nickName.isEmpty()) {
                conditions.put("nickName", nickName);
            }

            FindIterable iterable = col.find(new Document(conditions)).sort(objsort);
            iterable.forEach((Block) new Block<Document>() {
                public void apply(Document document) {
                    Gson gson = new Gson();
                    HistoryTransModel model = gson.fromJson(document.toJson(), HistoryTransModel.class);
                    model.setId(document.getLong("Id").toString());
                    model.setCreateAt(document.getString("createAt"));
                    records.add(model);
                }
            });

            response.setListTrans(records);
            return response;

        } catch (Exception e) {
            return null;
        }

    }

    @Override
    public ReportAdminTransferMoneyResponse getTotalAdminTransferByDay(String startTime, String endTime) {
        ReportAdminTransferMoneyResponse response = new ReportAdminTransferMoneyResponse(true, "0");
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("History_User_transaction");
        HashMap<String, Object> conditions = new HashMap<>();

        if (!startTime.isEmpty() && !endTime.isEmpty()) {
            BasicDBObject obj = new BasicDBObject();
            obj.put("$gte", startTime);
            obj.put("$lte", endTime);
            conditions.put("createAt", obj);
        }
        conditions.put("congGiaoDich", "Admin");

        FindIterable iterable = col.find(new Document(conditions));
        iterable.forEach((Block) new Block<Document>() {
            public void apply(Document document) {
                long money = Long.parseLong(document.getString("sotien"));
                if (money > 0) {
                    response.setAddMoney(response.getAddMoney() + money);
                } else {
                    response.setSubtractMoney(response.getSubtractMoney() + money);
                }
            }
        });

        MongoCollection collection = db.getCollection("user_gift_code");


        HashMap<String, Object> conditionsGiftCode = new HashMap<>();

        if (!startTime.isEmpty() && !endTime.isEmpty()) {
            BasicDBObject obj = new BasicDBObject();
            obj.put("$gte", startTime);
            obj.put("$lte", endTime);
            conditionsGiftCode.put("created_time", obj);
        }

        FindIterable findIterable = collection.find(new Document(conditionsGiftCode));

        findIterable.forEach((Block) new Block<Document>() {
            public void apply(Document document) {
                long money = document.getInteger("price");
                response.setTotalMoneyGiftCode(response.getTotalMoneyGiftCode() + money);
            }
        });

        return response;
    }

}
