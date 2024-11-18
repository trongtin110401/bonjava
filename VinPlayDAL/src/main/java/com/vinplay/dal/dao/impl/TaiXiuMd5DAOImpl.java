/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.core.IMap
 *  com.mongodb.BasicDBObject
 *  com.mongodb.Block
 *  com.mongodb.client.AggregateIterable
 *  com.mongodb.client.FindIterable
 *  com.mongodb.client.MongoCollection
 *  com.mongodb.client.MongoDatabase
 *  com.vinplay.vbee.common.hazelcast.HazelcastClientFactory
 *  com.vinplay.vbee.common.models.cache.ReportModel
 *  com.vinplay.vbee.common.models.cache.ThanhDuTXModel
 *  com.vinplay.vbee.common.models.minigame.TopWin
 *  com.vinplay.vbee.common.models.minigame.taixiu.XepHangRLTLModel
 *  com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory
 *  com.vinplay.vbee.common.pools.ConnectionPool
 *  com.vinplay.vbee.common.utils.CommonUtils
 *  com.vinplay.vbee.common.utils.VinPlayUtils
 *  org.bson.Document
 *  org.bson.conversions.Bson
 */
package com.vinplay.dal.dao.impl;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.vinplay.dal.dao.TaiXiuDAO;
import com.vinplay.dal.entities.report.ReportMoneySystemModel;
import com.vinplay.dal.entities.taixiu.*;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.models.cache.ReportModel;
import com.vinplay.vbee.common.models.cache.ThanhDuTXModel;
import com.vinplay.vbee.common.models.minigame.TopWin;
import com.vinplay.vbee.common.models.minigame.taixiu.XepHangRLTLModel;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.pools.ConnectionPool;
import com.vinplay.vbee.common.utils.CommonUtils;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.sql.*;
import java.util.*;

public class TaiXiuMd5DAOImpl
        implements TaiXiuDAO {

    private static final Logger logger = Logger.getLogger((String) "api");

    @Override
    public List<ResultTaiXiu> getLichSuPhien(int number, int moneyType) throws SQLException {
        List<ResultTaiXiu> results = new ArrayList<>();
        String sql = "SELECT * FROM result_tai_xiu_md5 WHERE money_type = ? ORDER BY `timestamp` DESC LIMIT ?";

        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_minigame");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, moneyType);
            stmt.setInt(2, number);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ResultTaiXiu entry = new ResultTaiXiu();
                    entry.referenceId = rs.getLong("reference_id");
                    entry.result = rs.getInt("result");
                    entry.dice1 = rs.getInt("dice1");
                    entry.dice2 = rs.getInt("dice2");
                    entry.dice3 = rs.getInt("dice3");
                    entry.totalTai = rs.getLong("total_tai");
                    entry.totalXiu = rs.getLong("total_xiu");
                    entry.numBetTai = rs.getInt("num_bet_tai");
                    entry.numBetXiu = rs.getInt("num_bet_xiu");
                    entry.totalPrize = rs.getLong("total_prize");
                    entry.totalRefundTai = rs.getLong("total_refund_tai");
                    entry.totalRefundXiu = rs.getLong("total_refund_xiu");
                    entry.totalRevenue = rs.getLong("total_revenue");
                    entry.moneyType = rs.getInt("money_type");
                    Timestamp timestamp = rs.getTimestamp("timestamp");
                    entry.timestamp = CommonUtils.convertTimestampToString((java.util.Date) timestamp);
                    results.add(entry);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Consider using a logging framework
            throw e; // Re-throw the exception to be handled by the calling code
        }

        return results;
    }


    @Override
    public List<TransactionTaiXiu> getLichSuGiaoDich(String nickname, int number, int moneyType) throws SQLException {
        ArrayList<TransactionTaiXiu> results = new ArrayList<TransactionTaiXiu>();
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_minigame");
        CallableStatement call = null;
        ResultSet rs = null;
        try {
            call = conn.prepareCall("CALL tx_get_lich_su_giao_dich_md5(?,?,?)");
            int param = 1;
            call.setString(param++, nickname);
            call.setInt(param++, number);
            call.setByte(param++, (byte) moneyType);
            rs = call.executeQuery();
            while (rs.next()) {
                TransactionTaiXiu entry = new TransactionTaiXiu();
                entry.referenceId = rs.getLong("reference_id");
                entry.userId = rs.getInt("user_id");
                entry.username = rs.getString("user_name");
                entry.betValue = rs.getLong("bet_value");
                entry.betSide = rs.getInt("bet_side");
                entry.totalPrize = rs.getLong("total_prize");
                entry.totalRefund = rs.getLong("total_refund");
                Timestamp date = rs.getTimestamp("timestamp");
                entry.timestamp = CommonUtils.convertTimestampToString((java.util.Date) date);
                byte dice1 = rs.getByte("dice1");
                byte dice2 = rs.getByte("dice2");
                byte dice3 = rs.getByte("dice3");
                entry.totalExchange = rs.getLong("total_exchange");
                int total = dice1 + dice2 + dice3;
                entry.resultPhien = String.valueOf(total);
                results.add(entry);
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            if (rs != null) {
                rs.close();
            }
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
    public List<TopWin> getTopTaiXiu(int moneyType) throws SQLException {
        ArrayList<TopWin> result = new ArrayList<TopWin>();
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_minigame");
        CallableStatement call = null;
        ResultSet rs = null;
        try {
            call = conn.prepareCall("CALL tx_get_top_win_md5(?)");
            int param = 1;
            call.setByte(param++, (byte) moneyType);
            rs = call.executeQuery();
            while (rs.next()) {
                TopWin entry = new TopWin();
                entry.setUsername(rs.getString("user_name"));
                entry.setMoney(rs.getLong("money"));
                result.add(entry);
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (call != null) {
                call.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return result;
    }

    @Override
    public List<TopWin> getTopTaiXiuVinhDanh(int moneyType, Timestamp beginTime, Timestamp endTime, int number) throws SQLException {
        ArrayList<TopWin> result = new ArrayList<>();
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_minigame");
        CallableStatement call = null;
        ResultSet rs = null;
        try {
            call = conn.prepareCall("CALL tx_get_top_vinh_danh(?, ?, ?, ?)");
            int param = 1;
            call.setByte(param++, (byte) moneyType);
            call.setTimestamp(param++, beginTime);
            call.setTimestamp(param++, endTime);
            call.setInt(param++, number);
            rs = call.executeQuery();
            while (rs.next()) {
                TopWin entry = new TopWin();
                entry.setUsername(rs.getString("user_name"));
                entry.setMoney(rs.getLong("money"));
                result.add(entry);
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (call != null) {
                call.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return result;
    }


    @Override
    public Long getMoneyStakesTXByMonth(String nickname, Timestamp beginTime, Timestamp endTime) throws SQLException {
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_minigame");
        CallableStatement call = null;
        ResultSet rs = null;
        long money = 0;
        try {
            call = conn.prepareCall("CALL tx_get_money_user_stakes_by_moth(?, ?, ?)");
            int param = 1;
            call.setString(param++, nickname);
            call.setTimestamp(param++, beginTime);
            call.setTimestamp(param++, endTime);
            rs = call.executeQuery();
            while (rs.next()) {
                money = rs.getLong("totalMoney");
            }
        } catch (SQLException e) {
            logger.info("getMoneyStakesTX error " + e.getMessage());
            throw e;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (call != null) {
                call.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return money;
    }

    @Override
    public int countLichSuGiaoDichTX(String nickname, int moneyType) throws SQLException {
        int totalRecords = -1;
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_minigame");
        CallableStatement call = null;
        try {
            call = conn.prepareCall("CALL tx_count_lich_su_giao_dich_md5(?, ?, ?)");
            int param = 1;
            call.setString(param++, nickname);
            call.setByte(param++, (byte) moneyType);
            call.registerOutParameter(param++, 4);
            call.execute();
            totalRecords = call.getInt(3);
        } catch (SQLException e) {
            throw e;
        } finally {
            if (call != null) {
                call.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return totalRecords;
    }

    @Override
    public List<TransactionTaiXiuDetail> getChiTietPhien(long referenceId, int moneyType) throws SQLException {
        ArrayList<TransactionTaiXiuDetail> results = new ArrayList<TransactionTaiXiuDetail>();
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_minigame");
        CallableStatement call = null;
        ResultSet rs = null;
        try {
            call = conn.prepareCall("CALL tx_get_chi_tiet_phien_md5(?,?)");
            int param = 1;
            call.setLong(param++, referenceId);
            call.setByte(param++, (byte) moneyType);
            rs = call.executeQuery();
            while (rs.next()) {
                TransactionTaiXiuDetail entry = new TransactionTaiXiuDetail();
                entry.referenceId = rs.getLong("reference_id");
                entry.userId = rs.getInt("user_id");
                entry.username = rs.getString("user_name");
                entry.betValue = rs.getLong("bet_value");
                entry.betSide = rs.getInt("bet_side");
                entry.prize = rs.getLong("prize");
                entry.refund = rs.getLong("refund");
                entry.inputTime = rs.getInt("input_time");
                entry.moneyType = rs.getByte("money_type");
                entry.timestamp = rs.getDate("timestamp");
                results.add(entry);
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            if (rs != null) {
                rs.close();
            }
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
    public List<NohuTXDetail> getHistoryNoHuTX(int page) throws SQLException {
        ArrayList<NohuTXDetail> results = new ArrayList<>();
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_minigame");
        CallableStatement call = null;
        ResultSet rs = null;
        try {
            call = conn.prepareCall("CALL tx_get_history_no_hu_md5(?)");
            int param = 1;
            call.setInt(param++, page);
            rs = call.executeQuery();
            while (rs.next()) {
                NohuTXDetail entry = new NohuTXDetail();
                entry.phien = rs.getLong("reference_id");
                entry.result = rs.getString("result");
                entry.money = rs.getLong("money");
                entry.userMoneyHu = rs.getString("user_money_hu");
                entry.totalUser = rs.getLong("total_user");
                entry.username = rs.getString("username");
                entry.time = CommonUtils.convertTimestampToString(rs.getTimestamp("time"));
                results.add(entry);
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            if (rs != null) {
                rs.close();
            }
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
    public ResultTaiXiu getKetQuaPhien(long referenceId, int moneyType) throws SQLException {
        ResultTaiXiuMd5 entry = null;
        String sql = "SELECT * FROM result_tai_xiu_md5 WHERE reference_id = ? AND money_type = ?";

        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_minigame");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, referenceId);
            stmt.setInt(2, moneyType);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    entry = new ResultTaiXiuMd5();
                    entry.referenceId = rs.getLong("reference_id");
                    entry.result = rs.getInt("result");
                    entry.dice1 = rs.getInt("dice1");
                    entry.dice2 = rs.getInt("dice2");
                    entry.dice3 = rs.getInt("dice3");
                    entry.totalTai = rs.getLong("total_tai");
                    entry.totalXiu = rs.getLong("total_xiu");
                    entry.numBetTai = rs.getInt("num_bet_tai");
                    entry.numBetXiu = rs.getInt("num_bet_xiu");
                    entry.totalPrize = rs.getLong("total_prize");
                    entry.totalRefundTai = rs.getLong("total_refund_tai");
                    entry.totalRefundXiu = rs.getLong("total_refund_xiu");
                    entry.totalRevenue = rs.getLong("total_revenue");
                    entry.moneyType = rs.getInt("money_type");
                    Timestamp timestamp = rs.getTimestamp("timestamp");
                    entry.timestamp = CommonUtils.convertTimestampToString((java.util.Date) timestamp);
                    entry.setPlantTextResult(rs.getString("plainText"));
                    entry.setMd5TextResult(rs.getString("md5"));
                }
            } catch (SQLException e) {
                e.printStackTrace(); // Consider using a logging framework
                throw e; // Re-throw the exception to be handled by the calling code
            }
        }

        return entry;
    }


    @Override
    public List<ThanhDuTXModel> getTopThanhDuDaily(String startTime, String endTime, short type) throws SQLException {
        ArrayList<ThanhDuTXModel> results = new ArrayList<ThanhDuTXModel>();
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_minigame");
        CallableStatement call = null;
        ResultSet rs = null;
        try {
            call = conn.prepareCall("CALL tx_get_top_thanh_du(?, ?, ?)");
            int param = 1;
            call.setString(param++, startTime);
            call.setString(param++, endTime);
            call.setByte(param++, (byte) type);
            rs = call.executeQuery();
            while (rs.next()) {
                String username = rs.getString("user_name");
                ThanhDuTXModel entry = new ThanhDuTXModel(username);
                entry.number = rs.getInt("number");
                entry.totalValue = rs.getLong("total_betting");
                entry.currentReferenceId = rs.getLong("last_reference");
                entry.parseReferences(rs.getString("references"));
                results.add(entry);
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            if (rs != null) {
                rs.close();
            }
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
    public Long getMoneyStakesTX(String nickname) throws SQLException {
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_minigame");
        CallableStatement call = null;
        ResultSet rs = null;
        long money = 0;
        try {
            call = conn.prepareCall("CALL tx_get_money_stakes_an_user(?)");
            int param = 1;
            call.setString(param++, nickname);
            rs = call.executeQuery();
            while (rs.next()) {
                money = rs.getLong("totalMoney");
            }
        } catch (SQLException e) {
            logger.info("getMoneyStakesTX error " + e.getMessage());
            throw e;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (call != null) {
                call.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return money;
    }


    @Override
    public int getMaxThanhDu(String username, short type) throws SQLException {
        String sql = "SELECT `number` FROM thanh_du WHERE user_name='" + username + "' AND `type`=" + type + " AND DATE(`last_update`)=CURDATE()";
        int max = 0;
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_minigame"); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery();) {
            if (rs.next()) {
                max = rs.getInt("number");
            }
        }
        return max;
    }

    @Override
    public int getSoLanRutLoc(String username) throws SQLException {
        String sql = "SELECT so_lan_rut FROM user_rut_loc_md5 WHERE user_name='" + username + "'";
        int soLanRut = 0;
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_minigame"); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery();) {
            if (rs.next()) {
                soLanRut = rs.getInt("so_lan_rut");
            }
        }
        return soLanRut;
    }

    public List<VinhDanhRLTLModel> getVinhDanhTLRL(String collectionName) {
        final ArrayList<VinhDanhRLTLModel> results = new ArrayList<VinhDanhRLTLModel>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        FindIterable iterable = null;
        BasicDBObject sortCondtions = new BasicDBObject();
        sortCondtions.put("_id", -1);
        iterable = db.getCollection(collectionName).find().sort((Bson) sortCondtions).limit(10);
        iterable.forEach((Block) new Block<Document>() {

            public void apply(Document document) {
                VinhDanhRLTLModel entry = new VinhDanhRLTLModel();
                entry.username = document.getString((Object) "user_name");
                entry.money = document.getLong((Object) "money");
                entry.time = document.getString((Object) "time_log");
                results.add(entry);
            }
        });
        return results;
    }

    private List<XepHangRLTLModel> getBangXepHangTLRL(String collectionName) {
        final ArrayList<XepHangRLTLModel> results = new ArrayList<XepHangRLTLModel>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        AggregateIterable iterable = db.getCollection(collectionName).aggregate(Arrays.asList(new Document[]{new Document("$group", (Object) new Document("_id", (Object) "$user_name").append("total", (Object) new Document("$sum", (Object) "$money"))), new Document("$sort", (Object) new Document("total", -1)), new Document("$limit", (Object) 10)}));
        iterable.forEach((Block) new Block<Document>() {

            public void apply(Document document) {
                XepHangRLTLModel model = new XepHangRLTLModel();
                model.username = document.getString((Object) "_id");
                model.money = document.getLong((Object) "total");
                results.add(model);
            }
        });
        return results;
    }

    private long getTienTLRL(String username, String collectionName) {
        long tongTien = 0L;
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        HashMap<String, String> condition = new HashMap<String, String>();
        condition.put("user_name", username);
        AggregateIterable iterable = db.getCollection(collectionName).aggregate(Arrays.asList(new Document[]{new Document("$match", condition), new Document("$group", (Object) new Document("_id", (Object) "null").append("total", (Object) new Document("$sum", (Object) "$money")))}));
        Document document = (Document) iterable.first();
        if (document != null) {
            tongTien = document.getLong((Object) "total");
        }
        return tongTien;
    }

    @Override
    public List<XepHangRLTLModel> getXepHangTanLoc() {
        return this.getBangXepHangTLRL("tan_loc_md5");
    }

    @Override
    public List<VinhDanhRLTLModel> getVinhDanhTanLoc() {
        return this.getVinhDanhTLRL("tan_loc_md5");
    }

    @Override
    public long getTongTienTanLoc(String username) {
        return this.getTienTLRL(username, "tan_loc_md5");
    }

    @Override
    public List<XepHangRLTLModel> getXepHangRutLoc() {
        return this.getBangXepHangTLRL("rut_loc_md5");
    }

    @Override
    public List<VinhDanhRLTLModel> getVinhDanhRutLoc() {
        return this.getVinhDanhTLRL("rut_loc_md5");
    }

    @Override
    public long getTongTienRutLoc(String username) {
        return this.getTienTLRL(username, "rut_loc_md5");
    }

    @Override
    public ReportMoneySystemModel getReportTXToDay() {
        ReportMoneySystemModel res = new ReportMoneySystemModel();
        String today = VinPlayUtils.getCurrentDate();
        HazelcastInstance client = HazelcastClientFactory.getInstance();
        IMap<String, ReportModel> reportMap = client.getMap("cacheReports");
        for (Map.Entry<String, ReportModel> entry : reportMap.entrySet()) {
            if (!((String) entry.getKey()).contains(today) || !((String) entry.getKey()).contains("TaiXiuMd5"))
                continue;
            ReportModel model = (ReportModel) entry.getValue();
            if (model.isBot) continue;
            ReportMoneySystemModel reportMoneySystemModel = res;
            reportMoneySystemModel.moneyWin += model.moneyWin;
            ReportMoneySystemModel reportMoneySystemModel2 = res;
            reportMoneySystemModel2.moneyLost += model.moneyLost;
            ReportMoneySystemModel reportMoneySystemModel3 = res;
            reportMoneySystemModel3.moneyOther += model.moneyOther;
            ReportMoneySystemModel reportMoneySystemModel4 = res;
            reportMoneySystemModel4.fee += model.fee;
            ReportMoneySystemModel reportMoneySystemModel5 = res;
            reportMoneySystemModel5.revenuePlayGame += model.moneyWin + model.moneyLost;
            ReportMoneySystemModel reportMoneySystemModel6 = res;
            reportMoneySystemModel6.revenue += model.moneyWin + model.moneyLost + model.moneyOther;
        }
        return res;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ReportMoneySystemModel getReportTX(String startDate, String endDate) {
        String sql = "SELECT SUM(money_win) as total_win, SUM(money_lost) as total_lost, SUM(money_other) as total_other, SUM(fee) as total_fee FROM vinplay.report_money_daily WHERE `date` >= '" + startDate + "?' and `date` <= '" + endDate + "' and action_name = 'TaiXiuMd5'";
        ReportMoneySystemModel res = new ReportMoneySystemModel();

        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_minigame");
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery();) {
            while (rs.next()) {
                res.moneyWin = rs.getLong("total_win");
                res.moneyLost = rs.getLong("total_lost");
                res.moneyOther = rs.getLong("total_other");
                res.fee = rs.getLong("total_fee");
            }
            res.revenuePlayGame = res.moneyWin + res.moneyLost;
            res.revenue = res.moneyWin + res.moneyLost + res.moneyOther;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

}

