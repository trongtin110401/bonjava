/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.mongodb.client.FindIterable
 *  com.mongodb.client.MongoCollection
 *  com.mongodb.client.MongoDatabase
 *  com.vinplay.vbee.common.models.FreezeModel
 *  com.vinplay.vbee.common.models.cache.UserCacheModel
 *  com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory
 *  com.vinplay.vbee.common.pools.ConnectionPool
 *  com.vinplay.vbee.common.utils.UserUtil
 *  org.bson.Document
 *  org.bson.conversions.Bson
 */
package com.vinplay.usercore.dao.impl;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.usercore.dao.MoneyInGameDao;
import com.vinplay.usercore.entities.LogTransferAgentModel;
import com.vinplay.vbee.common.models.FreezeModel;
import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.models.cache.UserCacheModel;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.pools.ConnectionPool;
import com.vinplay.vbee.common.utils.UserUtil;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.bson.conversions.Bson;

public class MoneyInGameDaoImpl
implements MoneyInGameDao {
    @Override
    public FreezeModel getFreezeMoneyAgentTranferBySessionId(String sessionId) throws SQLException {
        FreezeModel response = new FreezeModel();
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");){
            String sql = " SELECT user_id, nick_name, game_name, room_id, money, money_type, create_time, status  FROM vinplay.freeze_money  WHERE session_id = ? ";
            PreparedStatement stm = conn.prepareStatement(" SELECT user_id, nick_name, game_name, room_id, money, money_type, create_time, status  FROM vinplay.freeze_money  WHERE session_id = ? ");
            stm.setString(1, sessionId);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                response.setSessionId(sessionId);
                response.setNickname(rs.getString("nick_name"));
                response.setGameName(rs.getString("game_name"));
                response.setRoomId("room_id");
                response.setMoney(rs.getLong("money"));
                response.setMoneyType(rs.getString("money_type"));
                response.setUserId(rs.getInt("user_id"));
                response.setCreateTime((java.util.Date)rs.getDate("create_time"));
                response.setStatus(rs.getInt("status"));
            }
            rs.close();
            stm.close();
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            Document conditions = new Document();
            conditions.put("session_id_freeze_money", (Object)sessionId);
            Document dc = (Document)db.getCollection("log_chuyen_tien_dai_ly").find((Bson)conditions).first();
            if (dc != null) {
                response.setTransNo(dc.getString((Object)"transaction_no"));
            }
        }
        return response;
    }
    
    @Override
    public LogTransferAgentModel getMoneyAgentTranferBySessionId(String sessionId) throws SQLException {
        LogTransferAgentModel response = new LogTransferAgentModel();
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");){
            String sql = " SELECT * FROM vinplay.log_tranfer_agent WHERE session_id_freeze_money = ? ";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, sessionId);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                response.setAgent_level1(rs.getString("agent_level1"));
                response.setId(rs.getInt("id"));
                response.setMoney_receive(rs.getLong("money_receive"));
                response.setMoney_send(rs.getLong("money_send"));
                response.setNick_name_receive(rs.getString("nick_name_receive"));
                response.setNick_name_send(rs.getString("nick_name_send"));
                response.setTransaction_no(rs.getString("transaction_no"));
            }
            rs.close();
            stm.close();            
        }
        return response;
    }

    @Override
    public String getNickNameFreezeMoneyAgentTranferBySessionId(String sessionId) throws SQLException {
        String response = "";
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");){
            String sql = " SELECT nick_name  FROM vinplay.freeze_money  WHERE session_id = ? ";
            PreparedStatement stm = conn.prepareStatement(" SELECT nick_name  FROM vinplay.freeze_money  WHERE session_id = ? ");
            stm.setString(1, sessionId);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                response = rs.getString("nick_name");
            }
            rs.close();
            stm.close();
        }
        return response;
    }

    @Override
    public List<FreezeModel> getListFreezeMoneyAgentTranfer(String gameName, String nickName, String moneyType, String startTime, String endTime, int page, String status) throws SQLException {
        ArrayList<FreezeModel> response = new ArrayList<FreezeModel>();
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");){
            String sql = " SELECT *  FROM vinplay.freeze_money  WHERE 1 = 1 ";
            String condition = "";
            int numStart = (page - 1) * 50;
            int numEnd = numStart + 50;
            if (gameName != null && !gameName.isEmpty()) {
                condition = condition + " AND game_name = '" + gameName + "' ";
            }
            if (nickName != null && !nickName.isEmpty()) {
                condition = condition + " AND nick_name = '" + nickName + "' ";
            }
            if (moneyType != null && !moneyType.isEmpty()) {
                condition = condition + " AND money_type = '" + moneyType + "' ";
            }
            if (startTime != null && !startTime.isEmpty()) {
                condition = condition + " AND create_time >= '" + startTime + "' ";
            }
            if (endTime != null && !endTime.isEmpty()) {
                condition = condition + " AND create_time <= '" + endTime + "' ";
            }
            if (status != null && !status.isEmpty()) {
                condition = condition + " AND status = " + status;
            }
            sql = sql + condition + " ORDER BY create_time DESC ";
            sql = sql + " LIMIT " + numStart + ", " + numEnd;
            PreparedStatement stm = conn.prepareStatement(sql);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                FreezeModel freezeModel = new FreezeModel();
                freezeModel.setSessionId(rs.getString("session_id"));
                freezeModel.setNickname(rs.getString("nick_name"));
                freezeModel.setGameName(rs.getString("game_name"));
                freezeModel.setRoomId(rs.getString("room_id"));
                freezeModel.setMoney(rs.getLong("money"));
                freezeModel.setMoneyType(rs.getString("money_type"));
                freezeModel.setUserId(rs.getInt("user_id"));
                String strCreateTime = rs.getString("create_time");
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                try {
                    java.util.Date createTime = format.parse(strCreateTime);
                    freezeModel.setCreateTime(createTime);
                }
                catch (ParseException e) {
                    e.printStackTrace();
                }
                freezeModel.setStatus(rs.getInt("status"));
                response.add(freezeModel);
            }
            rs.close();
            stm.close();
        }
        return response;
    }
    
    @Override
    public List<FreezeModel> getListFreezeMoneyNew() throws SQLException {
        ArrayList<FreezeModel> response = new ArrayList<FreezeModel>();
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");){
            String sql = "select * from vinplay.freeze_money where `status` = 1 and nick_name in (SELECT nick_name COLLATE utf8_unicode_ci FROM vinplay.users where vin != vin_total and is_bot = 0 and dai_ly = 0)";            
            PreparedStatement stm = conn.prepareStatement(sql);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                FreezeModel freezeModel = new FreezeModel();
                freezeModel.setSessionId(rs.getString("session_id"));
                freezeModel.setNickname(rs.getString("nick_name"));
                freezeModel.setGameName(rs.getString("game_name"));
                freezeModel.setRoomId(rs.getString("room_id"));
                freezeModel.setMoney(rs.getLong("money"));
                freezeModel.setMoneyType(rs.getString("money_type"));
                freezeModel.setUserId(rs.getInt("user_id"));
                String strCreateTime = rs.getString("create_time");
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                try {
                    java.util.Date createTime = format.parse(strCreateTime);
                    freezeModel.setCreateTime(createTime);
                }
                catch (ParseException e) {
                    e.printStackTrace();
                }
                freezeModel.setStatus(rs.getInt("status"));
                response.add(freezeModel);
            }
            rs.close();
            stm.close();
        }
        return response;
    }

    @Override
    public UserCacheModel getUserByNickName(String nickName) throws SQLException {
        UserCacheModel response = new UserCacheModel();
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");){
            String sql = "SELECT id, vin, vin_total, safe FROM vinplay.users WHERE nick_name = ?";
            PreparedStatement stm = conn.prepareStatement("SELECT id, vin, vin_total, safe FROM vinplay.users WHERE nick_name = ?");
            stm.setString(1, nickName);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                response.setId(rs.getInt("id"));
                response.setVin(rs.getLong("vin"));
                response.setVinTotal(rs.getLong("vin_total"));
                response.setSafe(rs.getLong("safe"));
            }
            rs.close();
            stm.close();
        }
        return response;
    }

    @Override
    public boolean updateSafeMoney(long safeMoney, long userId) throws SQLException {
        boolean res = false;
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");){
            String sql = "UPDATE vinplay.users SET safe = ? WHERE id = ?";
            PreparedStatement stm = conn.prepareStatement("UPDATE vinplay.users SET safe = ? WHERE id = ?");
            stm.setLong(1, safeMoney);
            stm.setLong(2, userId);
            if (stm.executeUpdate() == 1) {
                res = true;
            }
            stm.close();
        }
        return res;
    }

    @Override
    public List<FreezeModel> getAllFreeze() throws SQLException {
        ArrayList<FreezeModel> res = new ArrayList<FreezeModel>();
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");){
            String sql = "SELECT * FROM freeze_money WHERE status = 1";
            PreparedStatement stm = conn.prepareStatement("SELECT * FROM freeze_money WHERE status = 1");
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                FreezeModel model = UserUtil.parseResultSetToFreezeModel((ResultSet)rs);
                res.add(model);
            }
            rs.close();
            stm.close();
        }
        return res;
    }

    @Override
    public FreezeModel getFreeze(String sessionId) throws SQLException {
        FreezeModel model = null;
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");){
            String sql = "SELECT * FROM freeze_money WHERE session_id=? AND status = 1";
            PreparedStatement stm = conn.prepareStatement("SELECT * FROM freeze_money WHERE session_id=? AND status = 1");
            stm.setString(1, sessionId);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                model = UserUtil.parseResultSetToFreezeModel((ResultSet)rs);
            }
            rs.close();
            stm.close();
        }
        return model;
    }

    @Override
    public boolean updateVippoint(String nickname, int vp, int moneyVP) throws SQLException {
        boolean res = false;
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");){
            String sql = "UPDATE users SET vip_point = vip_point + ?, vip_point_save = vip_point_save + ?, money_vp = ? WHERE nick_name = ?";
            PreparedStatement stm = conn.prepareStatement("UPDATE users SET vip_point = vip_point + ?, vip_point_save = vip_point_save + ?, money_vp = ? WHERE nick_name = ?");
            stm.setInt(1, vp);
            stm.setInt(2, vp);
            stm.setInt(3, moneyVP);
            stm.setString(4, nickname);
            if (stm.executeUpdate() == 1) {
                res = true;
            }
            stm.close();
        }
        return res;
    }

    @Override
    public boolean updateVippointAgent(String nickname, int vp, int vpSave, int moneyVP) throws SQLException {
        boolean res = false;
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");){
            String sql = "UPDATE users SET vip_point = vip_point + ?, vip_point_save = vip_point_save + ?, money_vp = ? WHERE nick_name = ?";
            PreparedStatement stm = conn.prepareStatement("UPDATE users SET vip_point = vip_point + ?, vip_point_save = vip_point_save + ?, money_vp = ? WHERE nick_name = ?");
            stm.setInt(1, vp);
            stm.setInt(2, vpSave);
            stm.setInt(3, moneyVP);
            stm.setString(4, nickname);
            if (stm.executeUpdate() == 1) {
                res = true;
            }
            stm.close();
        }
        return res;
    }

    @Override
    public boolean restoreAllGame(List<String> sessionBlockList) throws SQLException {
        boolean res = false;
        StringBuilder ssBIn = new StringBuilder("");
        StringBuilder ssBNotIn = new StringBuilder("");
        if (sessionBlockList.size() > 0) {
            ssBNotIn.append("AND session_id NOT IN (");
            ssBIn.append("OR session_id IN (");
            for (int i = 0; i < sessionBlockList.size(); ++i) {
                ssBNotIn.append("?,");
                ssBIn.append("?,");
            }
            ssBNotIn.deleteCharAt(ssBNotIn.length() - 1).append(")");
            ssBIn.deleteCharAt(ssBIn.length() - 1).append(")");
        }
        try (Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");){
            conn.setAutoCommit(false);
            String sql1 = " UPDATE vinplay.freeze_money SET money = 0, status = 0 WHERE game_name <> 'FreezeMoneyTranferAgent' AND status = 1 " + ssBNotIn.toString();
            PreparedStatement stm1 = conn.prepareStatement(sql1);
            int index = 1;
            for (String ss : sessionBlockList) {
                stm1.setString(index++, ss);
            }
            stm1.executeUpdate();
            String sql2 = " UPDATE vinplay.users SET vin = vin_total , xu = xu_total WHERE 1 = 1 ";
            PreparedStatement stm2 = conn.prepareStatement(" UPDATE vinplay.users SET vin = vin_total , xu = xu_total WHERE 1 = 1 ");
            stm2.executeUpdate();
            String sqlSelectTotalMoneyLock = " SELECT user_id, SUM(money) AS totalMoneyLock  FROM vinplay.freeze_money WHERE (game_name = 'FreezeMoneyTranferAgent' " + ssBIn.toString() + " ) AND status = 1 GROUP BY user_id ";
            PreparedStatement stm3 = conn.prepareStatement(sqlSelectTotalMoneyLock);
            int index2 = 1;
            for (String ss2 : sessionBlockList) {
                stm3.setString(index2++, ss2);
            }
            ResultSet rs3 = stm3.executeQuery();
            while (rs3.next()) {
                PreparedStatement stm5;
                long userId = rs3.getLong("user_id");
                long totalMoneyLock = rs3.getLong("totalMoneyLock");
                String sqlGetMoney = " SELECT vin_total FROM vinplay.users WHERE id = ? ";
                PreparedStatement stm4 = conn.prepareStatement(" SELECT vin_total FROM vinplay.users WHERE id = ? ");
                stm4.setLong(1, userId);
                ResultSet rs4 = stm4.executeQuery();
                long moneyUse = 0L;
                while (rs4.next()) {
                    moneyUse = rs4.getLong("vin_total");
                }
                rs4.close();
                String sqlUpdateMoneyLock = "";
                if (moneyUse < totalMoneyLock) {
                    sqlUpdateMoneyLock = " UPDATE vinplay.users SET vin = 0 WHERE id = ? ";
                    stm5 = conn.prepareStatement(sqlUpdateMoneyLock);
                    stm5.setLong(1, userId);
                    stm5.executeUpdate();
                    stm5.close();
                } else {
                    sqlUpdateMoneyLock = " UPDATE vinplay.users SET vin = (vin_total - ?) WHERE id = ? ";
                    stm5 = conn.prepareStatement(sqlUpdateMoneyLock);
                    stm5.setLong(1, totalMoneyLock);
                    stm5.setLong(2, userId);
                    stm5.executeUpdate();
                    stm5.close();
                }
                stm4.close();
            }
            conn.commit();
            rs3.close();
            stm1.close();
            stm2.close();
            stm3.close();
            res = true;
        }
        return res;
    }
}

