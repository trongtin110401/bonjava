/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.vinplay.vbee.common.models.FreezeModel
 *  com.vinplay.vbee.common.pools.ConnectionPool
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.vinplay.usercore.dao.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.dal.entities.taixiu.BetResult;
import com.vinplay.dal.entities.taixiu.TransactionXocDia;
import com.vinplay.gamebai.entities.BossXocDiaModel;
import com.vinplay.gamebai.entities.XocDiaBoss;
import com.vinplay.usercore.dao.XocDiaDao;
import com.vinplay.usercore.service.impl.MoneyInGameServiceImpl;
import com.vinplay.vbee.common.models.FreezeModel;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.pools.ConnectionPool;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

public class XocDiaDaoImpl
        implements XocDiaDao {
    @Override
    public boolean saveRoomBoss(XocDiaBoss boss) throws SQLException {
        boolean res = false;
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_gamebai");
        CallableStatement call = null;
        try {
            call = conn.prepareCall("CALL save_xd_boss(?,?,?,?,?)");
            int param = 1;
            call.setString(param++, boss.getSessionId());
            call.setString(param++, boss.getNickname());
            call.setInt(param++, boss.getRoomId());
            call.setString(param++, boss.getRoomSetting());
            call.setLong(param++, boss.getFundInitial());
            call.executeUpdate();
            res = true;
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
        return res;
    }

    @Override
    public boolean updateRoomBoss(String sessionId, String nickname, long fundInitial, int fee, long revenue, int type) throws SQLException {
        boolean res = false;
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_gamebai");
        CallableStatement call = null;
        try {
            call = conn.prepareCall("CALL update_xd_boss(?,?,?,?,?,?)");
            int param = 1;
            call.setString(param++, sessionId);
            call.setString(param++, nickname);
            call.setLong(param++, fundInitial);
            call.setInt(param++, fee);
            call.setLong(param++, revenue);
            call.setInt(param++, type);
            call.executeUpdate();
            res = true;
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
        return res;
    }

    @Override
    public XocDiaBoss getRoomBoss(String nickname, int roomId) throws SQLException {
        String sql = "SELECT * FROM xoc_dia_boss WHERE `nick_name`=? AND room_id = ? AND `status` = 1";
        XocDiaBoss boss = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = ConnectionPool.getInstance().getConnection("mysqlpool_gamebai");
            stm = conn.prepareStatement(sql);
            stm.setString(1, nickname);
            rs = stm.executeQuery();
            if (rs.next()) {
                String sessionId = rs.getString("session_id");
                String roomSetting = rs.getString("room_setting");
                long fundInitial = rs.getLong("fund_initial");
                int fee = rs.getInt("fee");
                long revenue = rs.getLong("revenue");
                String createTime = rs.getString("create_time");
                boss = new XocDiaBoss(sessionId, nickname, roomId, roomSetting, fundInitial, 1, fee, revenue, createTime);
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
        return boss;
    }

    @Override
    public Map<Integer, XocDiaBoss> getListRoomBossActive() throws SQLException {
        String sql = "SELECT * FROM xoc_dia_boss WHERE `status` = 1";
        HashMap<Integer, XocDiaBoss> bossList = new HashMap<Integer, XocDiaBoss>();
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_gamebai"); PreparedStatement stm = conn.prepareStatement(sql); ResultSet rs = stm.executeQuery()) {
            while (rs.next()) {
                String sessionId = rs.getString("session_id");
                String nickname = rs.getString("nick_name");
                int roomId = rs.getInt("room_id");
                String roomSetting = rs.getString("room_setting");
                long fundInitial = rs.getLong("fund_initial");
                int fee = rs.getInt("fee");
                long revenue = rs.getLong("revenue");
                String createTime = rs.getString("create_time");
                XocDiaBoss boss = new XocDiaBoss(sessionId, nickname, roomId, roomSetting, fundInitial, 1, fee, revenue, createTime);
                bossList.put(roomId, boss);
            }
        }
        return bossList;
    }

    @Override
    public List<String> getListBossActive() throws SQLException {
        String sql = "SELECT * FROM xoc_dia_boss WHERE `status` = 1";
        ArrayList<String> bossList = new ArrayList<String>();
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_gamebai"); PreparedStatement stm = conn.prepareStatement(sql); ResultSet rs = stm.executeQuery()) {
            while (rs.next()) {
                String nickname = rs.getString("nick_name");
                bossList.add(nickname);
            }
        }
        return bossList;
    }

    @Override
    public List<String> getListSessionActive() throws SQLException {
        String sql = "SELECT * FROM xoc_dia_boss WHERE `status` = 1";
        ArrayList<String> sessionList = new ArrayList<String>();
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpool_gamebai"); PreparedStatement stm = conn.prepareStatement(sql); ResultSet rs = stm.executeQuery()) {
            while (rs.next()) {
                String sessionId = rs.getString("session_id");
                sessionList.add(sessionId);
            }
        }
        return sessionList;
    }

    @Override
    public List<BossXocDiaModel> getListRoomBoss(String nickname, int roomId, int status, int moneyBet) throws SQLException, JSONException {
        String sql = "SELECT * FROM xoc_dia_boss WHERE 1 = 1";
        if (nickname != null && !nickname.isEmpty()) {
            sql = sql + " AND nick_name = ?";
        }
        if (roomId >= 0) {
            sql = sql + " AND room_id = ?";
        }
        if (status >= 0) {
            sql = sql + " AND status = ?";
        }
        sql = sql + " ORDER BY create_time DESC";

        ArrayList<BossXocDiaModel> bossList = new ArrayList<BossXocDiaModel>();
        PreparedStatement stm = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = ConnectionPool.getInstance().getConnection("mysqlpool_gamebai");
            stm = conn.prepareStatement(sql);
            int param = 1;
            if (nickname != null && !nickname.isEmpty()) {
                stm.setString(param++, nickname);
            }
            if (roomId >= 0) {
                stm.setInt(param++, roomId);
            }
            if (status >= 0) {
                stm.setInt(param++, status);
            }
            rs = stm.executeQuery();
            MoneyInGameServiceImpl mnSer = new MoneyInGameServiceImpl();
            while (rs.next()) {
                JSONObject roomSetting = new JSONObject(rs.getString("room_setting"));
                int mnB = roomSetting.getInt("moneyBet");
                if (moneyBet >= 0 && mnB != moneyBet) continue;
                String sessionId = rs.getString("session_id");
                String nn = rs.getString("nick_name");
                int rId = rs.getInt("room_id");
                String password = roomSetting.getString("password");
                String roomName = roomSetting.getString("roomName");
                long fundInitial = rs.getLong("fund_initial");
                long fund = 0L;
                FreezeModel model = mnSer.getFreeze(sessionId);
                if (model != null) {
                    fund = model.getMoney();
                }
                int st = rs.getInt("status");
                int fee = rs.getInt("fee");
                long revenue = rs.getLong("revenue");
                String createTime = rs.getString("create_time");
                BossXocDiaModel boss = new BossXocDiaModel(sessionId, nn, rId, mnB, password, roomName, fundInitial, fund, st, fee, revenue, createTime);
                bossList.add(boss);
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
        return bossList;
    }

    @Override
    public List<TransactionXocDia> getLichSuXocDia(String username, int page) {
        int skipNumber = (page - 1) * 10;
        List<TransactionXocDia> results = new ArrayList<>();
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        FindIterable iterable;
        Document conditions = new Document();
        conditions.put("user_name", username);
        BasicDBObject sortCondtions = new BasicDBObject();
        sortCondtions.put("_id", -1);
        iterable = db.getCollection("xoc_dia_transaction").find(conditions).sort(sortCondtions).skip(skipNumber).limit(10);
        iterable.forEach((Block) new Block<Document>() {

            public void apply(Document document) {
                TransactionXocDia transactionXocDia = new TransactionXocDia();
                BetResult betResult = new BetResult();
                transactionXocDia.setUsername(document.getString("user_name"));
                transactionXocDia.setTotalPrize(document.getLong("total_prize"));
                transactionXocDia.setTotalExchange(document.getLong("money_exchange"));
                transactionXocDia.setTimestamp(document.getString("create_time"));
                betResult.setZeroWhite(document.getLong("zero_white"));
                betResult.setFourWhite(document.getLong("four_white"));
                betResult.setThreeWhite(document.getLong("three_white"));
                betResult.setOneWhite(document.getLong("one_white"));
                betResult.setEven(document.getLong("even"));
                betResult.setOdd(document.getLong("odd"));
                transactionXocDia.setReferenceId(document.getLong("reference_id"));
                transactionXocDia.setResult(document.getString("result"));

                transactionXocDia.setBetResult(betResult);
                results.add(transactionXocDia);
            }
        });
        return results;
    }

    @Override
    public int countLichSuXocDia(String username) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> collection = db.getCollection("xoc_dia_transaction");
        Document conditions = new Document();
        conditions.put("user_name", username);
        return (int) collection.count(conditions);
    }
}

