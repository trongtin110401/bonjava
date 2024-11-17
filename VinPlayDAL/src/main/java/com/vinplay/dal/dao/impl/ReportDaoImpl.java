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

public class ReportDaoImpl
        implements ReportDAO {
    @Override
    public List<String> getAllBot() throws SQLException {
        String sql = "SELECT nick_name FROM users WHERE is_bot=1";
        ArrayList<String> res = new ArrayList<String>();

        PreparedStatement stm = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
            stm = conn.prepareStatement(sql);
            rs = stm.executeQuery();
            while (rs.next()) {
                res.add(rs.getString("nick_name"));
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stm != null) {
                stm.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return res;
    }

    @Override
    public Map<String, ReportMoneySystemModel> getReportMoneySystemMySQL(String startTime, String endTime, boolean isBot) throws Exception {
        HashMap<String, ReportMoneySystemModel> results = new HashMap<String, ReportMoneySystemModel>();
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        CallableStatement call = null;
        try {
            call = conn.prepareCall("CALL report_money_system(?,?)");
            int param = 1;
            call.setString(param++, VinPlayUtils.getDateTimeStr((java.util.Date) VinPlayUtils.getDateTimeFromDate((String) startTime)));
            call.setString(param++, VinPlayUtils.getDateTimeStr((java.util.Date) VinPlayUtils.getDateTimeFromDate((String) endTime)));
            ResultSet rs = call.executeQuery();
            while (rs.next()) {
                ReportMoneySystemModel model = new ReportMoneySystemModel();
                model.moneyWin = rs.getLong("money_win");
                model.moneyLost = rs.getLong("money_lost");
                model.moneyOther = rs.getLong("money_other");
                model.fee = rs.getLong("fee");
                model.revenuePlayGame = model.moneyWin + model.moneyLost;
                model.revenue = model.revenuePlayGame + model.moneyOther;
                String actionName = rs.getString("action_name");
                results.put(actionName, model);
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (call != null) {
                call.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return results;
    }

    @Override
    public Map<String, ReportMoneySystemModel> getReportMoneySystem(String startTime, String endTime, boolean isBot) throws Exception {
        final HashMap<String, ReportMoneySystemModel> results = new HashMap<String, ReportMoneySystemModel>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        Document conditions = new Document();
        if (!startTime.isEmpty() && !endTime.isEmpty()) {
            BasicDBObject obj = new BasicDBObject();
            obj.put("$gte", (Object) VinPlayUtils.getDateTimeStr((java.util.Date) VinPlayUtils.getDateTimeFromDate((String) startTime)));
            obj.put("$lte", (Object) VinPlayUtils.getDateTimeStr((java.util.Date) VinPlayUtils.getDateTimeFromDate((String) endTime)));
            conditions.put("time_log", (Object) obj);
        }
        MongoCollection col = null;
        if (!isBot) {
            col = db.getCollection("report_money_vin");
            AggregateIterable iterable = col.aggregate(Arrays.asList(new Document[]{new Document("$match", (Object) conditions), new Document("$group", (Object) new Document("_id", (Object) "$action_name").append("money_win", (Object) new Document("$sum", (Object) "$money_win")).append("money_lost", (Object) new Document("$sum", (Object) "$money_lost")).append("money_other", (Object) new Document("$sum", (Object) "$money_other")).append("fee", (Object) new Document("$sum", (Object) "$fee")))}));
            iterable.forEach((Block) new Block<Document>() {

                public void apply(Document document) {
                    ReportMoneySystemModel model = new ReportMoneySystemModel();
                    model.moneyWin = document.getLong((Object) "money_win");
                    model.moneyLost = document.getLong((Object) "money_lost");
                    model.moneyOther = document.getLong((Object) "money_other");
                    model.fee = document.getLong((Object) "fee");
                    model.revenuePlayGame = model.moneyWin + model.moneyLost;
                    model.revenue = model.revenuePlayGame + model.moneyOther;
                    String actionName = document.getString((Object) "_id");
                    results.put(actionName, model);
                }
            });
            return results;
        }
        return results;
    }

    @Override
    public Map<String, ReportMoneySystemModel> getReportMoneyUser(String startTime, String endTime, String nickname, boolean isBot) throws Exception {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        Document conditions = new Document();
        if (!startTime.isEmpty() && !endTime.isEmpty()) {
            BasicDBObject obj = new BasicDBObject();
            obj.put("$gte", VinPlayUtils.getDateTimeStr(VinPlayUtils.getDateTimeFromDate(startTime)));
            obj.put("$lte", VinPlayUtils.getDateTimeStr(VinPlayUtils.getDateTimeFromDate(endTime)));
            conditions.put("time_log", obj);
        }
        conditions.put("nick_name", nickname);
        MongoCollection col;
        col = !isBot ? db.getCollection("report_money_vin") : db.getCollection("report_money_vin_bot");
        AggregateIterable iterable = col.aggregate(Arrays.asList(new Document("$match", conditions), new Document("$group", new Document("_id", "$action_name").append("money_win", new Document("$sum", "$money_win")).append("money_lost", new Document("$sum", "$money_lost")).append("money_other", new Document("$sum", "$money_other")).append("fee", new Document("$sum", "$fee")))));
        final HashMap<String, ReportMoneySystemModel> results = new HashMap<String, ReportMoneySystemModel>();
        iterable.forEach(new Block<Document>() {

            public void apply(Document document) {
                ReportMoneySystemModel model = new ReportMoneySystemModel();
                String actionName = document.getString("_id");
                model.moneyWin = document.getLong("money_win");
                model.moneyLost = document.getLong("money_lost");
                model.moneyOther = document.getLong("money_other");
                model.fee = document.getLong("fee");
                model.revenuePlayGame = model.moneyWin + model.moneyLost;
                model.revenue = model.revenuePlayGame + model.moneyOther;
                results.put(actionName, model);
            }
        });
        return results;
    }

    @Override
    public Map<String, ReportMoneySystemModel> getReportMoneyUser2(String startTime, String endTime, String nickname, boolean isBot) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        Document conditions = new Document();
        if (!startTime.isEmpty() && !endTime.isEmpty()) {
            BasicDBObject obj = new BasicDBObject();
            obj.put("$gte", startTime);
            obj.put("$lte", endTime);
            conditions.put("time_log", obj);
        }
        conditions.put("nick_name", nickname);
        MongoCollection col;
        col = !isBot ? db.getCollection("report_money_vin") : db.getCollection("report_money_vin_bot");
        AggregateIterable iterable = col.aggregate(Arrays.asList(new Document("$match", conditions), new Document("$group", new Document("_id", "$action_name").append("money_win", new Document("$sum", "$money_win")).append("money_lost", new Document("$sum", "$money_lost")).append("money_other", new Document("$sum", "$money_other")).append("fee", new Document("$sum", "$fee")))));
        final HashMap<String, ReportMoneySystemModel> results = new HashMap<String, ReportMoneySystemModel>();
        iterable.forEach(new Block<Document>() {

            public void apply(Document document) {
                ReportMoneySystemModel model = new ReportMoneySystemModel();
                String actionName = document.getString("_id");
                model.moneyWin = document.getLong("money_win");
                model.moneyLost = document.getLong("money_lost");
                model.moneyOther = document.getLong("money_other");
                model.fee = document.getLong("fee");
                model.revenuePlayGame = model.moneyWin + model.moneyLost;
                model.revenue = model.revenuePlayGame + model.moneyOther;
                results.put(actionName, model);
            }
        });
        return results;
    }

    @Override
    public ReportTotalMoneyModel getTotalMoney(String superAgent) throws SQLException {
        String sqlBot = "SELECT (SUM(vin_total) + SUM(safe)) as sum FROM users WHERE is_bot=1";
        String sqlUser = "SELECT (SUM(vin_total) + SUM(safe)) as sum FROM users WHERE is_bot=0 AND dai_ly <> 1 AND dai_ly <> 2";
        String sqlAgent1 = "SELECT (SUM(vin_total) + SUM(safe)) as sum FROM users WHERE is_bot=0 AND dai_ly = 1";
        String sqlAgent2 = "SELECT (SUM(vin_total) + SUM(safe)) as sum FROM users WHERE is_bot=0 AND dai_ly = 2";
        String sqlSuperAgent = "SELECT (SUM(vin_total) + SUM(safe)) as sum FROM users WHERE nick_name = '" + superAgent + "'";

        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        long moneyBot = 0L;
        long moneyUser = 0L;
        long moneyAgent1 = 0L;
        long moneyAgent2 = 0L;
        long moneySuperAgent = 0L;

        PreparedStatement stmBot = null;
        PreparedStatement stmUser = null;
        PreparedStatement stmAgent1 = null;
        PreparedStatement stmAgent2 = null;
        PreparedStatement stmSuperAgent = null;

        ResultSet rsBot = null;
        ResultSet rsUser = null;
        ResultSet rsAgent1 = null;
        ResultSet rsAgent2 = null;
        ResultSet rsSuperAgent = null;

        try {
            stmBot = conn.prepareStatement(sqlBot);
            stmUser = conn.prepareStatement(sqlUser);
            stmAgent1 = conn.prepareStatement(sqlAgent1);
            stmAgent2 = conn.prepareStatement(sqlAgent2);
            stmSuperAgent = conn.prepareStatement(sqlSuperAgent);


            rsBot = stmBot.executeQuery();
            rsUser = stmUser.executeQuery();
            rsAgent1 = stmAgent1.executeQuery();
            rsAgent2 = stmAgent2.executeQuery();
            rsSuperAgent = stmSuperAgent.executeQuery();
            if (rsBot.next()) {
                moneyBot = rsBot.getLong("sum");
            }
            if (rsUser.next()) {
                moneyUser = rsUser.getLong("sum");
            }
            if (rsAgent1.next()) {
                moneyAgent1 = rsAgent1.getLong("sum");
            }
            if (rsAgent2.next()) {
                moneyAgent2 = rsAgent2.getLong("sum");
            }
            if (rsSuperAgent.next()) {
                moneySuperAgent = rsSuperAgent.getLong("sum");
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            if (rsBot != null) {
                rsBot.close();
            }
            if (rsUser != null) {
                rsUser.close();
            }
            if (rsAgent1 != null) {
                rsAgent1.close();
            }
            if (rsAgent2 != null) {
                rsAgent2.close();
            }
            if (rsSuperAgent != null) {
                rsSuperAgent.close();
            }
            if (stmBot != null) {
                stmBot.close();
            }
            if (stmUser != null) {
                stmUser.close();
            }
            if (stmAgent1 != null) {
                stmAgent1.close();
            }
            if (stmAgent2 != null) {
                stmAgent2.close();
            }
            if (stmSuperAgent != null) {
                stmSuperAgent.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        long total = moneyBot + moneyUser + (moneyAgent1 -= moneySuperAgent) + moneyAgent2 + moneySuperAgent;
        ReportTotalMoneyModel model = new ReportTotalMoneyModel(moneyBot, moneyUser, moneyAgent1, moneyAgent2, moneySuperAgent, total, null);
        return model;
    }

    @Override
    public long getCurrentMoney(String nickname) throws SQLException {
        String sql = "SELECT vin_total FROM users WHERE nick_name=?";
        long res = 0L;
        PreparedStatement stm = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
            stm = conn.prepareStatement(sql);
            stm.setString(1, nickname);
            rs = stm.executeQuery();
            if (rs.next()) {
                res = rs.getLong("vin_total");
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stm != null) {
                stm.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return res;
    }

    public long getCurrentMoneyAllUsersByDaily(String nickname) throws SQLException {
        String sql = "SELECT sum(vin_total) as vin_total FROM users WHERE user_daily = ?";
        long res = 0L;
        PreparedStatement stm = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
            stm = conn.prepareStatement(sql);
            stm.setString(1, nickname);
            rs = stm.executeQuery();
            if (rs.next()) {
                res = rs.getLong("vin_total");
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stm != null) {
                stm.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return res;
    }

    @Override
    public long getSafeMoney(String nickname) throws SQLException {
        String sql = "SELECT safe FROM users WHERE nick_name=?";
        long res = 0L;
        PreparedStatement stm = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
            stm = conn.prepareStatement(sql);
            stm.setString(1, nickname);
            rs = stm.executeQuery();
            if (rs.next()) {
                res = rs.getLong("safe");
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stm != null) {
                stm.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return res;
    }

    public long getSafeMoneyAllUsersByDaily(String nickname) throws SQLException {
        String sql = "SELECT sum(safe) as safe FROM users WHERE  user_daily =?";
        long res = 0L;
        PreparedStatement stm = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
            stm = conn.prepareStatement(sql);
            stm.setString(1, nickname);
            rs = stm.executeQuery();
            if (rs.next()) {
                res = rs.getLong("safe");
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stm != null) {
                stm.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return res;
    }

    @Override
    public boolean checkBot(String nickname) throws SQLException {
        String sql = "SELECT is_bot FROM users WHERE nick_name=?";
        boolean res = false;
        PreparedStatement stm = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
            stm = conn.prepareStatement(sql);
            stm.setString(1, nickname);
            rs = stm.executeQuery();
            if (rs.next() && rs.getInt("is_bot") == 1) {
                res = true;
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stm != null) {
                stm.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return res;
    }

    @Override
    public boolean saveLogTotalMoney(ReportTotalMoneyModel model) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("report_total_money");
        Document doc = new Document();
        doc.append("money_bot", (Object) model.moneyBot);
        doc.append("money_user", (Object) model.moneyUser);
        doc.append("money_agent_1", (Object) model.moneyAgent1);
        doc.append("money_agent_2", (Object) model.moneyAgent2);
        doc.append("money_super_agent", (Object) model.moneySuperAgent);
        doc.append("time_log", (Object) VinPlayUtils.getCurrentDateTime());
        col.insertOne((Object) doc);
        return true;
    }

    @Override
    public List<ReportTotalMoneyModel> getReportTotalMoney(int pageNumber, String startTime, String endTime) {
        final ArrayList<ReportTotalMoneyModel> res = new ArrayList<ReportTotalMoneyModel>();
        int pageSize = 50;
        int skipNumber = (pageNumber - 1) * 50;
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        Document conditions = new Document();
        if (!startTime.isEmpty() && !endTime.isEmpty()) {
            BasicDBObject obj = new BasicDBObject();
            obj.put("$gte", (Object) startTime);
            obj.put("$lte", (Object) endTime);
            conditions.put("time_log", (Object) obj);
        }
        BasicDBObject sortCondtions = new BasicDBObject();
        sortCondtions.put("_id", -1);
        FindIterable iterable = db.getCollection("report_total_money").find((Bson) conditions).sort((Bson) sortCondtions).skip(skipNumber).limit(50);
        iterable.forEach((Block) new Block<Document>() {

            public void apply(Document document) {
                ReportTotalMoneyModel model = new ReportTotalMoneyModel();
                model.moneyBot = document.getLong((Object) "money_bot");
                model.moneyUser = document.getLong((Object) "money_user");
                model.moneyAgent1 = document.getLong((Object) "money_agent_1");
                model.moneyAgent2 = document.getLong((Object) "money_agent_2");
                model.moneySuperAgent = document.getLong((Object) "money_super_agent");
                model.total = model.moneyBot + model.moneyUser + model.moneyAgent1 + model.moneyAgent2 + model.moneySuperAgent;
                model.timeLog = document.getString((Object) "time_log");
                res.add(model);
            }
        });
        return res;
    }

    @Override
    public ReportTotalMoneyModel getReportTotalMoneyAtTime(String date, boolean bStart) throws ParseException {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        Document conditions = new Document();
        BasicDBObject obj = new BasicDBObject();
        BasicDBObject sortCondtions = new BasicDBObject();
        java.util.Date dateTime = VinPlayUtils.getDateTimeFromDate((String) date);
        if (bStart) {
            obj.put("$gte", (Object) VinPlayUtils.getDateTimeStr((java.util.Date) dateTime));
            sortCondtions.put("_id", (Object) 1);
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateTime);
            cal.add(5, 1);
            obj.put("$lte", (Object) VinPlayUtils.getDateTimeStr((java.util.Date) cal.getTime()));
            sortCondtions.put("_id", -1);
        }
        conditions.put("time_log", (Object) obj);
        Document document = (Document) db.getCollection("report_total_money").find((Bson) conditions).sort((Bson) sortCondtions).first();
        ReportTotalMoneyModel model = new ReportTotalMoneyModel();
        if (document != null) {
            model.moneyBot = document.getLong((Object) "money_bot");
            model.moneyUser = document.getLong((Object) "money_user");
            model.moneyAgent1 = document.getLong((Object) "money_agent_1");
            model.moneyAgent2 = document.getLong((Object) "money_agent_2");
            model.moneySuperAgent = document.getLong((Object) "money_super_agent");
            model.total = model.moneyBot + model.moneyUser + model.moneyAgent1 + model.moneyAgent2 + model.moneySuperAgent;
            model.timeLog = document.getString((Object) "time_log");
        }
        return model;
    }

    @Override
    public boolean saveLogMoneyForReport(String nickname, String actionname, String date, ReportModel model) throws ParseException {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = null;
        col = !model.isBot ? db.getCollection("report_money_vin") : db.getCollection("report_money_vin_bot");
        BasicDBObject updateFields = new BasicDBObject();
        updateFields.append("money_win", (Object) model.moneyWin);
        updateFields.append("money_lost", (Object) model.moneyLost);
        updateFields.append("money_other", (Object) model.moneyOther);
        updateFields.append("fee", (Object) model.fee);
        updateFields.append("time_log", (Object) VinPlayUtils.getDateTimeStr((java.util.Date) VinPlayUtils.getDateTimeFromDate((String) date)));
        updateFields.append("create_time", (Object) VinPlayUtils.getDateTimeFromDate((String) date));
        BasicDBObject conditions = new BasicDBObject();
        conditions.append("nick_name", (Object) nickname);
        conditions.append("action_name", (Object) actionname);
        conditions.append("date", (Object) date);
        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
        options.upsert(true);
        col.findOneAndUpdate((Bson) conditions, (Bson) new Document("$set", (Object) updateFields), options);
        return true;
    }

    @Override
    public boolean saveTopCaoThu(String nickname, String date, long moneyWin) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("top_user_play_game_vin");
        BasicDBObject updateFields = new BasicDBObject();
        updateFields.append("money_win", (Object) moneyWin);
        BasicDBObject conditions = new BasicDBObject();
        conditions.append("nick_name", (Object) nickname);
        conditions.append("date", (Object) date);
        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
        options.upsert(true);
        col.findOneAndUpdate((Bson) conditions, (Bson) new Document("$set", (Object) updateFields), options);
        return true;
    }

    @Override
    public HashMap<String, Long> getReportTopGame(String startTime, String endTime, String actionName, boolean isBot) throws Exception {
        final HashMap<String, Long> results = new HashMap<String, Long>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        Document conditions = new Document();
        if (!startTime.isEmpty() && !endTime.isEmpty()) {
            BasicDBObject obj = new BasicDBObject();
            obj.put("$gte", (Object) VinPlayUtils.getDateTimeStr((java.util.Date) VinPlayUtils.getDateTimeFromDate((String) startTime)));
            obj.put("$lte", (Object) VinPlayUtils.getDateTimeStr((java.util.Date) VinPlayUtils.getDateTimeFromDate((String) endTime)));
            conditions.put("time_log", (Object) obj);
        }
        conditions.put("action_name", (Object) actionName);
        MongoCollection col = null;
        if (!isBot) {
            col = db.getCollection("report_money_vin");
            AggregateIterable iterable = col.aggregate(Arrays.asList(new Document[]{new Document("$match", (Object) conditions), new Document("$group", (Object) new Document("_id", (Object) "$nick_name").append("money_win", (Object) new Document("$sum", (Object) "$money_win")).append("money_lost", (Object) new Document("$sum", (Object) "$money_lost")).append("money_other", (Object) new Document("$sum", (Object) "$money_other")))}));
            iterable.forEach((Block) new Block<Document>() {

                public void apply(Document document) {
                    String nickName = document.getString((Object) "_id");
                    long money = document.getLong((Object) "money_win") + document.getLong((Object) "money_lost") + document.getLong((Object) "money_other");
                    results.put(nickName, money);
                }
            });
            return results;
        }
        return results;
    }

    @Override
    public Map<String, ReportModel> getListReportModelByDay(final String date, final boolean isBot) throws Exception {
        final HashMap<String, ReportModel> results = new HashMap<String, ReportModel>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        Document conditions = new Document();
        conditions.put("time_log", VinPlayUtils.getDateTimeStr(VinPlayUtils.getDateTimeFromDate(date)));
        MongoCollection col;
        col = !isBot ? db.getCollection("report_money_vin") : db.getCollection("report_money_vin_bot");
        FindIterable iterable = col.find((Bson) conditions);
        iterable.forEach((Block) new Block<Document>() {

            public void apply(Document document) {
                ReportModel model = new ReportModel();
                String nickname = document.getString((Object) "nick_name");
                String actionname = document.getString((Object) "action_name");
                model.moneyWin = document.getLong((Object) "money_win");
                model.moneyLost = document.getLong((Object) "money_lost");
                model.moneyOther = document.getLong((Object) "money_other");
                model.fee = document.getLong((Object) "fee");
                model.isBot = isBot;
                String key = nickname + "," + actionname + "," + date;
                results.put(key, model);
            }
        });
        return results;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void saveReportMoneyVin(Map<String, ReportModel> input, String date) {
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO report_money_daily(action_name, money_win, money_lost, money_other, fee, date) VALUES(?, ?, ?, ?, ?, ?) " +
                    " ON DUPLICATE KEY UPDATE " +
                    "       money_win = ?, " +
                    "       money_lost = ?," +
                    "       money_other =  ?, " +
                    "       fee =  ?");
            for (Map.Entry<String, ReportModel> entry : input.entrySet()) {
                if (entry.getValue().isBot) continue;
                stmt.setString(1, entry.getKey());
                stmt.setLong(2, entry.getValue().moneyWin);
                stmt.setLong(3, entry.getValue().moneyLost);
                stmt.setLong(4, entry.getValue().moneyOther);
                stmt.setLong(5, entry.getValue().fee);
                stmt.setDate(6, Date.valueOf(date));
                stmt.setLong(7, entry.getValue().moneyWin);
                stmt.setLong(8, entry.getValue().moneyLost);
                stmt.setLong(9, entry.getValue().moneyOther);
                stmt.setLong(10, entry.getValue().fee);
                stmt.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e2) {
                    e2.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e2) {
                    e2.printStackTrace();
                }
            }
        }
    }

}

