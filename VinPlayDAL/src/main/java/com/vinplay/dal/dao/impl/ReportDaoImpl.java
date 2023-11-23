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
import java.io.PrintStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bson.Document;
import org.bson.conversions.Bson;

public class ReportDaoImpl
implements ReportDAO {
    @Override
    public List<String> getAllBot() throws SQLException {
        ArrayList<String> res = new ArrayList<String>();
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");){
            String sql = "SELECT nick_name FROM users WHERE is_bot=1";
            PreparedStatement stm = conn.prepareStatement("SELECT nick_name FROM users WHERE is_bot=1");
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                res.add(rs.getString("nick_name"));
            }
            rs.close();
            stm.close();
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
            call.setString(param++, VinPlayUtils.getDateTimeStr((java.util.Date)VinPlayUtils.getDateTimeFromDate((String)startTime)));
            call.setString(param++, VinPlayUtils.getDateTimeStr((java.util.Date)VinPlayUtils.getDateTimeFromDate((String)endTime)));
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
        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        finally {
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
            obj.put("$gte", (Object)VinPlayUtils.getDateTimeStr((java.util.Date)VinPlayUtils.getDateTimeFromDate((String)startTime)));
            obj.put("$lte", (Object)VinPlayUtils.getDateTimeStr((java.util.Date)VinPlayUtils.getDateTimeFromDate((String)endTime)));
            conditions.put("time_log", (Object)obj);
        }
        MongoCollection col = null;
        if (!isBot) {
            col = db.getCollection("report_money_vin");
            AggregateIterable iterable = col.aggregate(Arrays.asList(new Document[]{new Document("$match", (Object)conditions), new Document("$group", (Object)new Document("_id", (Object)"$action_name").append("money_win", (Object)new Document("$sum", (Object)"$money_win")).append("money_lost", (Object)new Document("$sum", (Object)"$money_lost")).append("money_other", (Object)new Document("$sum", (Object)"$money_other")).append("fee", (Object)new Document("$sum", (Object)"$fee")))}));
            iterable.forEach((Block)new Block<Document>(){

                public void apply(Document document) {
                    ReportMoneySystemModel model = new ReportMoneySystemModel();
                    model.moneyWin = document.getLong((Object)"money_win");
                    model.moneyLost = document.getLong((Object)"money_lost");
                    model.moneyOther = document.getLong((Object)"money_other");
                    model.fee = document.getLong((Object)"fee");
                    model.revenuePlayGame = model.moneyWin + model.moneyLost;
                    model.revenue = model.revenuePlayGame + model.moneyOther;
                    String actionName = document.getString((Object)"_id");
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
            obj.put("$gte", (Object)VinPlayUtils.getDateTimeStr((java.util.Date)VinPlayUtils.getDateTimeFromDate((String)startTime)));
            obj.put("$lte", (Object)VinPlayUtils.getDateTimeStr((java.util.Date)VinPlayUtils.getDateTimeFromDate((String)endTime)));
            conditions.put("time_log", (Object)obj);
        }
        conditions.put("nick_name", (Object)nickname);
        MongoCollection col = null;
        col = !isBot ? db.getCollection("report_money_vin") : db.getCollection("report_money_vin_bot");
        AggregateIterable iterable = col.aggregate(Arrays.asList(new Document[]{new Document("$match", (Object)conditions), new Document("$group", (Object)new Document("_id", (Object)"$action_name").append("money_win", (Object)new Document("$sum", (Object)"$money_win")).append("money_lost", (Object)new Document("$sum", (Object)"$money_lost")).append("money_other", (Object)new Document("$sum", (Object)"$money_other")).append("fee", (Object)new Document("$sum", (Object)"$fee")))}));
        final HashMap<String, ReportMoneySystemModel> results = new HashMap<String, ReportMoneySystemModel>();
        iterable.forEach((Block)new Block<Document>(){

            public void apply(Document document) {
                ReportMoneySystemModel model = new ReportMoneySystemModel();
                String actionName = document.getString((Object)"_id");
                model.moneyWin = document.getLong((Object)"money_win");
                model.moneyLost = document.getLong((Object)"money_lost");
                model.moneyOther = document.getLong((Object)"money_other");
                model.fee = document.getLong((Object)"fee");
                model.revenuePlayGame = model.moneyWin + model.moneyLost;
                model.revenue = model.revenuePlayGame + model.moneyOther;
                results.put(actionName, model);
            }
        });
        return results;
    }

    @Override
    public ReportTotalMoneyModel getTotalMoney(String superAgent) throws SQLException {
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        long moneyBot = 0L;
        long moneyUser = 0L;
        long moneyAgent1 = 0L;
        long moneyAgent2 = 0L;
        long moneySuperAgent = 0L;
        try {
            String sqlBot = "SELECT (SUM(vin_total) + SUM(safe)) as sum FROM users WHERE is_bot=1";
            String sqlUser = "SELECT (SUM(vin_total) + SUM(safe)) as sum FROM users WHERE is_bot=0 AND dai_ly <> 1 AND dai_ly <> 2";
            String sqlAgent1 = "SELECT (SUM(vin_total) + SUM(safe)) as sum FROM users WHERE is_bot=0 AND dai_ly = 1";
            String sqlAgent2 = "SELECT (SUM(vin_total) + SUM(safe)) as sum FROM users WHERE is_bot=0 AND dai_ly = 2";
            String sqlSuperAgent = "SELECT (SUM(vin_total) + SUM(safe)) as sum FROM users WHERE nick_name = '" + superAgent + "'";
            PreparedStatement stmBot = conn.prepareStatement("SELECT (SUM(vin_total) + SUM(safe)) as sum FROM users WHERE is_bot=1");
            PreparedStatement stmUser = conn.prepareStatement("SELECT (SUM(vin_total) + SUM(safe)) as sum FROM users WHERE is_bot=0 AND dai_ly <> 1 AND dai_ly <> 2");
            PreparedStatement stmAgent1 = conn.prepareStatement("SELECT (SUM(vin_total) + SUM(safe)) as sum FROM users WHERE is_bot=0 AND dai_ly = 1");
            PreparedStatement stmAgent2 = conn.prepareStatement("SELECT (SUM(vin_total) + SUM(safe)) as sum FROM users WHERE is_bot=0 AND dai_ly = 2");
            PreparedStatement stmSuperAgent = conn.prepareStatement(sqlSuperAgent);
            ResultSet rsBot = stmBot.executeQuery();
            ResultSet rsUser = stmUser.executeQuery();
            ResultSet rsAgent1 = stmAgent1.executeQuery();
            ResultSet rsAgent2 = stmAgent2.executeQuery();
            ResultSet rsSuperAgent = stmSuperAgent.executeQuery();
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
            rsBot.close();
            rsUser.close();
            rsAgent1.close();
            rsAgent2.close();
            rsSuperAgent.close();
            stmBot.close();
            stmUser.close();
            stmAgent1.close();
            stmAgent2.close();
            stmSuperAgent.close();
        }
        catch (SQLException e) {
            throw e;
        }
        finally {
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
        long res = 0L;
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");){
            String sql = "SELECT vin_total FROM users WHERE nick_name=?";
            PreparedStatement stm = conn.prepareStatement("SELECT vin_total FROM users WHERE nick_name=?");
            stm.setString(1, nickname);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                res = rs.getLong("vin_total");
            }
            rs.close();
            stm.close();
        }
        return res;
    }

    @Override
    public long getSafeMoney(String nickname) throws SQLException {
        long res = 0L;
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");){
            String sql = "SELECT safe FROM users WHERE nick_name=?";
            PreparedStatement stm = conn.prepareStatement("SELECT safe FROM users WHERE nick_name=?");
            stm.setString(1, nickname);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                res = rs.getLong("safe");
            }
            rs.close();
            stm.close();
        }
        return res;
    }

    @Override
    public boolean checkBot(String nickname) throws SQLException {
        boolean res = false;
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");){
            String sql = "SELECT is_bot FROM users WHERE nick_name=?";
            PreparedStatement stm = conn.prepareStatement("SELECT is_bot FROM users WHERE nick_name=?");
            stm.setString(1, nickname);
            ResultSet rs = stm.executeQuery();
            if (rs.next() && rs.getInt("is_bot") == 1) {
                res = true;
            }
            rs.close();
            stm.close();
        }
        return res;
    }

    @Override
    public boolean saveLogTotalMoney(ReportTotalMoneyModel model) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("report_total_money");
        Document doc = new Document();
        doc.append("money_bot", (Object)model.moneyBot);
        doc.append("money_user", (Object)model.moneyUser);
        doc.append("money_agent_1", (Object)model.moneyAgent1);
        doc.append("money_agent_2", (Object)model.moneyAgent2);
        doc.append("money_super_agent", (Object)model.moneySuperAgent);
        doc.append("time_log", (Object)VinPlayUtils.getCurrentDateTime());
        col.insertOne((Object)doc);
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
            obj.put("$gte", (Object)startTime);
            obj.put("$lte", (Object)endTime);
            conditions.put("time_log", (Object)obj);
        }
        BasicDBObject sortCondtions = new BasicDBObject();
        sortCondtions.put("_id", -1);
        FindIterable iterable = db.getCollection("report_total_money").find((Bson)conditions).sort((Bson)sortCondtions).skip(skipNumber).limit(50);
        iterable.forEach((Block)new Block<Document>(){

            public void apply(Document document) {
                ReportTotalMoneyModel model = new ReportTotalMoneyModel();
                model.moneyBot = document.getLong((Object)"money_bot");
                model.moneyUser = document.getLong((Object)"money_user");
                model.moneyAgent1 = document.getLong((Object)"money_agent_1");
                model.moneyAgent2 = document.getLong((Object)"money_agent_2");
                model.moneySuperAgent = document.getLong((Object)"money_super_agent");
                model.total = model.moneyBot + model.moneyUser + model.moneyAgent1 + model.moneyAgent2 + model.moneySuperAgent;
                model.timeLog = document.getString((Object)"time_log");
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
        java.util.Date dateTime = VinPlayUtils.getDateTimeFromDate((String)date);
        if (bStart) {
            obj.put("$gte", (Object)VinPlayUtils.getDateTimeStr((java.util.Date)dateTime));
            sortCondtions.put("_id", (Object)1);
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateTime);
            cal.add(5, 1);
            obj.put("$lte", (Object)VinPlayUtils.getDateTimeStr((java.util.Date)cal.getTime()));
            sortCondtions.put("_id", -1);
        }
        conditions.put("time_log", (Object)obj);
        Document document = (Document)db.getCollection("report_total_money").find((Bson)conditions).sort((Bson)sortCondtions).first();
        ReportTotalMoneyModel model = new ReportTotalMoneyModel();
        if (document != null) {
            model.moneyBot = document.getLong((Object)"money_bot");
            model.moneyUser = document.getLong((Object)"money_user");
            model.moneyAgent1 = document.getLong((Object)"money_agent_1");
            model.moneyAgent2 = document.getLong((Object)"money_agent_2");
            model.moneySuperAgent = document.getLong((Object)"money_super_agent");
            model.total = model.moneyBot + model.moneyUser + model.moneyAgent1 + model.moneyAgent2 + model.moneySuperAgent;
            model.timeLog = document.getString((Object)"time_log");
        }
        return model;
    }

    @Override
    public boolean saveLogMoneyForReport(String nickname, String actionname, String date, ReportModel model) throws ParseException {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = null;
        col = !model.isBot ? db.getCollection("report_money_vin") : db.getCollection("report_money_vin_bot");
        BasicDBObject updateFields = new BasicDBObject();
        updateFields.append("money_win", (Object)model.moneyWin);
        updateFields.append("money_lost", (Object)model.moneyLost);
        updateFields.append("money_other", (Object)model.moneyOther);
        updateFields.append("fee", (Object)model.fee);
        updateFields.append("time_log", (Object)VinPlayUtils.getDateTimeStr((java.util.Date)VinPlayUtils.getDateTimeFromDate((String)date)));
        updateFields.append("create_time", (Object)VinPlayUtils.getDateTimeFromDate((String)date));
        BasicDBObject conditions = new BasicDBObject();
        conditions.append("nick_name", (Object)nickname);
        conditions.append("action_name", (Object)actionname);
        conditions.append("date", (Object)date);
        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
        options.upsert(true);
        col.findOneAndUpdate((Bson)conditions, (Bson)new Document("$set", (Object)updateFields), options);
        return true;
    }

    @Override
    public boolean saveTopCaoThu(String nickname, String date, long moneyWin) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("top_user_play_game_vin");
        BasicDBObject updateFields = new BasicDBObject();
        updateFields.append("money_win", (Object)moneyWin);
        BasicDBObject conditions = new BasicDBObject();
        conditions.append("nick_name", (Object)nickname);
        conditions.append("date", (Object)date);
        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
        options.upsert(true);
        col.findOneAndUpdate((Bson)conditions, (Bson)new Document("$set", (Object)updateFields), options);
        return true;
    }

    @Override
    public HashMap<String, Long> getReportTopGame(String startTime, String endTime, String actionName, boolean isBot) throws Exception {
        final HashMap<String, Long> results = new HashMap<String, Long>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        Document conditions = new Document();
        if (!startTime.isEmpty() && !endTime.isEmpty()) {
            BasicDBObject obj = new BasicDBObject();
            obj.put("$gte", (Object)VinPlayUtils.getDateTimeStr((java.util.Date)VinPlayUtils.getDateTimeFromDate((String)startTime)));
            obj.put("$lte", (Object)VinPlayUtils.getDateTimeStr((java.util.Date)VinPlayUtils.getDateTimeFromDate((String)endTime)));
            conditions.put("time_log", (Object)obj);
        }
        conditions.put("action_name", (Object)actionName);
        MongoCollection col = null;
        if (!isBot) {
            col = db.getCollection("report_money_vin");
            AggregateIterable iterable = col.aggregate(Arrays.asList(new Document[]{new Document("$match", (Object)conditions), new Document("$group", (Object)new Document("_id", (Object)"$nick_name").append("money_win", (Object)new Document("$sum", (Object)"$money_win")).append("money_lost", (Object)new Document("$sum", (Object)"$money_lost")).append("money_other", (Object)new Document("$sum", (Object)"$money_other")))}));
            iterable.forEach((Block)new Block<Document>(){

                public void apply(Document document) {
                    String nickName = document.getString((Object)"_id");
                    long money = document.getLong((Object)"money_win") + document.getLong((Object)"money_lost") + document.getLong((Object)"money_other");
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
        conditions.put("time_log", (Object)VinPlayUtils.getDateTimeStr((java.util.Date)VinPlayUtils.getDateTimeFromDate((String)date)));
        MongoCollection col = null;
        col = !isBot ? db.getCollection("report_money_vin") : db.getCollection("report_money_vin_bot");
        FindIterable iterable = col.find((Bson)conditions);
        iterable.forEach((Block)new Block<Document>(){

            public void apply(Document document) {
                ReportModel model = new ReportModel();
                String nickname = document.getString((Object)"nick_name");
                String actionname = document.getString((Object)"action_name");
                model.moneyWin = document.getLong((Object)"money_win");
                model.moneyLost = document.getLong((Object)"money_lost");
                model.moneyOther = document.getLong((Object)"money_other");
                model.fee = document.getLong((Object)"fee");
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
    public void saveReportMoneyVin(Map<String, ReportModel> input) {
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        try {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO report_money_daily(action_name, money_win, money_lost, money_other, fee, date) VALUES(?, ?, ?, ?, ?, ?)");
            Calendar cal = Calendar.getInstance();
            cal.add(5, -1);
            Date yesterday = new Date(cal.getTimeInMillis());
            for (Map.Entry<String, ReportModel> entry : input.entrySet()) {
                if (entry.getValue().isBot) continue;
                stmt.setString(1, entry.getKey());
                stmt.setLong(2, entry.getValue().moneyWin);
                stmt.setLong(3, entry.getValue().moneyLost);
                stmt.setLong(4, entry.getValue().moneyOther);
                stmt.setLong(5, entry.getValue().fee);
                stmt.setDate(6, yesterday);
                stmt.execute();
            }
            stmt.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e);
            if (conn != null) {
                try {
                    conn.close();
                }
                catch (SQLException e2) {
                    e2.printStackTrace();
                }
            }
        }
        finally {
            if (conn != null) {
                try {
                    conn.close();
                }
                catch (SQLException e2) {
                    e2.printStackTrace();
                }
            }
        }
    }

}

